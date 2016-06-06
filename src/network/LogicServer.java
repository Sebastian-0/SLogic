/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network;

import graphicalcircuit.Module;
import gui.backend.ProgramWindow.MessageType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import network.User.Privileges;
import network.hooks.OptionPaneMessageHook;
import network.hooks.SendInformationChatMessageHook;
import network.hooks.ServerClosingHook;
import network.hooks.TransferCircuitHook;
import network.hooks.TransferExistingClipboardEntryHook;
import network.hooks.TransferExistingUserHook;
import network.hooks.TransferModuleHook;
import network.hooks.UserJoinedHook;
import network.hooks.UserLeftHook;
import snet.Server;
import snet.ServerLifecycleListener;
import snet.internal.ConnectionManagerInterface.DisconnectReason;
import snet.internal.Message;
import sutilities.Debugger;
import configuration.Config;
import configuration.Table;
import database.ClipboardEntry;
import database.ClipboardEntrySaver;
import database.DatabaseSaver;
import database.ModuleLoader;
import database.ModuleSaver;

public class LogicServer
{
  private Server server;
  private MessagePoller messagePoller;
  
  private Workspace workspace;
  
  private int secureHostIdentifier;
  
  private String hostId;
  private Map<String, User> connectedUsers;
  
  private ServerStartListener serverStartListener;
  
  
  public LogicServer(ServerStartListener serverStartListener)
  {
    server = new Server(serverLifecycleListener);
    server.setWaitForPoll(true);
    
    workspace = new Workspace();
    
    connectedUsers = new HashMap<String, User>();
    
    this.serverStartListener = serverStartListener;
  }
  
  
  /**
   * Starts the server on the specified port and returns the secure host
   *  identifier used for this session.
   * @param port The port to host on
   * @return The secure host identifier used for this session
   */
  public int start(int port)
  {
    workspace.database.clear();
    connectedUsers.clear();
    
    loadModuleTypes(new File(Config.get(Config.MODULE_FOLDER_PATH)));
    
    server.start(port);
    messagePoller = new MessagePoller(server);
    messagePoller.start();
    
    generateSecureHostIdentifier();
    return secureHostIdentifier;
  }
  
  private void generateSecureHostIdentifier()
  {
    secureHostIdentifier = (int)(Math.random() * Integer.MAX_VALUE);
  }
  
  public void stop()
  {
    if (server.isConnected())
    {
      for (User user : connectedUsers.values())
      {
        if (user.id != hostId)
          server.send(user.id, new ServerClosingHook(null).createMessage(Table.get("server_closing_message")));
      }
      server.disconnect();
      server.setWaitForPoll(false); // To stop messages from ending up waiting in the queue, risk dispatching them asynchronously
      messagePoller.stop();
      
      // Poll any remaining messages
      try {
        server.pollMessages(false);
      } catch (InterruptedException e) { }
      
      messagePoller = null;
      hostId = null;
    }
  }
  
  
  public void setHostId(String id)
  {
    hostId = id;
  }
  
  public User getUser(String id)
  {
    return connectedUsers.get(id);
  }

  
  // TODO LogicServer; Flytta följande till Workspace?
  public void saveModuleType(File fileToSaveTo, Module module) throws IOException
  {
    new ModuleSaver(fileToSaveTo).save(module);
  }
  
  public void loadModuleTypes(File moduleTypeFolder)
  {
    if (moduleTypeFolder.isDirectory())
    {
      workspace.database.clearModuleTypes();
      
      for (File moduleFile : moduleTypeFolder.listFiles())
      {
        try
        {
          Module module = new ModuleLoader(moduleFile).load();
          
          workspace.database.addModuleType(module);
        } catch (ClassNotFoundException e)
        {
          // TODO LogicServer; Meddelanden när den inte kan ladda en modulfil?
          Debugger.error("LogisServer: loadModuleTypes()", "Failed to load module: " + moduleFile.getPath(), e);
        } catch (IOException e)
        {
          // TODO LogicServer; Meddelanden när den inte kan ladda en modulfil?
          Debugger.error("LogisServer: loadModuleTypes()", "Failed to load module: " + moduleFile.getPath(), e);
        }
      }
    }
  }
  
  public void addModuleType(Module module)
  {
    workspace.database.addModuleType(module);
  }
  
  
  public String getHostId()
  {
    return hostId;
  }
  
  public Server getNetwork()
  {
    return server;
  }
  
  public Workspace getWorkspace() {
    return workspace;
  }
  
  public boolean isAcceptableAsHost(int secureHostIdentifierOfClient)
  {
    return secureHostIdentifier == secureHostIdentifierOfClient;
  }
  

  
  /**
   * This method connects the user with the specified id to the server, this
   *  method is invoked when the user connects if there is no password protection.
   *  If password protection is enabled, this method is invoked when the user
   *  has authenticated himself.
   *  @param idOfNewUser The server connection id of the new user
   *  @param privileges The privileges of the new user
   */
  public void userConnected(String idOfNewUser, Privileges privileges)
  {
    connectedUsers.put(idOfNewUser, new User(idOfNewUser, idOfNewUser, privileges));
    
    if (server.isConnected())
    {
      sendUserJoinedMessages(idOfNewUser);
      transferExistingUsersToNewUser(idOfNewUser);
      transferModulesToNewUser(idOfNewUser);
      transferCurrentCircuitToNewUser(idOfNewUser);
      transferExistingClipboardEntries(idOfNewUser);
    }
  }


  private void transferExistingClipboardEntries(String idOfNewUser)
  {
    try
    {
      File fileToTransfer = Files.createTempFile("CLS_CE_Trans", ".tmp").toFile();
      ClipboardEntrySaver clipboardEntrySaver = new ClipboardEntrySaver(fileToTransfer);
      
      for (ClipboardEntry entry : workspace.clipboard.getEntries())
      {
        clipboardEntrySaver.save(entry);
        
        Message msg = new TransferExistingClipboardEntryHook(null).createMessage(fileToTransfer);
        server.send(idOfNewUser, msg);
      }
      
      fileToTransfer.delete();
    } catch (IOException e)
    {
      Debugger.error("LogicServer: transferExistingClipboardEntries()", "Failed to save clipboard entry!", e);
    }
  }

  private void sendUserJoinedMessages(String id)
  {
    for (User user : connectedUsers.values())
    {
      server.send(user.id, new UserJoinedHook(null).createMessage(id, (user.id == id)));
    }
    server.send(id, new SendInformationChatMessageHook(null).createMessage(Session.CHAT_CONNECTED.text, Session.CHAT_CONNECTED.color));
  }

  private void transferExistingUsersToNewUser(String id)
  {
    TransferExistingUserHook hook = new TransferExistingUserHook(null);
    for (User user : connectedUsers.values())
    {
      if (user.id != id)
      {
        Message msg = hook.createMessage(user.id, user);
        server.send(id, msg);
      }
    }
  }
  
  private void transferModulesToNewUser(String id) // TODO LogicServer; Skicka modulerna som är inlästa! Om filerna skickas som det ser ut nu, och någon ny fil lagts till, så hamnar klienten och servern ur sync
  {
    File moduleFolder = new File(Config.get(Config.MODULE_FOLDER_PATH));
    if (moduleFolder.isDirectory())
    {
      for (File module : moduleFolder.listFiles())
      {
        Message loadMessage = new TransferModuleHook(null).createMessage(module);
        server.send(id, loadMessage);
      }
    }
  }

  private void transferCurrentCircuitToNewUser(String id)
  {
    try
    {
      File temp = Files.createTempFile("CLSTrans", ".tmp").toFile();
      workspace.database.saveToFile(new DatabaseSaver(temp));
      Message loadMessage = new TransferCircuitHook(null).createMessage(temp, workspace.hasSaveLocation());
      server.send(id, loadMessage);
      temp.delete();
    } catch (IOException e)
    {
      server.send(id, new OptionPaneMessageHook(null).createMessage(
          "transfer_circuit_failed_transfer_title",
          "transfer_circuit_failed_transfer_message",
          MessageType.Error));
      server.drop(id);
    }
  }
  
  
  
  private ServerLifecycleListener serverLifecycleListener = new ServerLifecycleListener()
  {
    @Override
    public void failedToStart()
    {
      messagePoller.stop();
      serverStartListener.startFailed();
    }
    
    @Override
    public void serverStarted(int port)
    {
      serverStartListener.serverStarted(port, secureHostIdentifier);
    }
    
    @Override
    public void connected(String idOfNewUser)
    {
    }
    
    @Override
    public void disconnected(String id, DisconnectReason reason)
    {
      server.sendToAll(new UserLeftHook(null).createMessage(id));
      
      connectedUsers.remove(id);
    }
  };
}
