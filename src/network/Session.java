/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network;

import gui.backend.ChatMessage;
import gui.backend.ProgramBackend;
import gui.backend.ProgramWindow.MessageType;

import java.awt.Color;
import java.awt.EventQueue;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snet.NetworkHook;
import snet.internal.ConnectionManagerInterface.DisconnectReason;
import sutilities.Debugger;
import util.ReflectionUtils;
import configuration.Config;
import configuration.Table;

public class Session
{
  public static final ChatMessage CHAT_USER_JOINED = new ChatMessage("chat_user_joined", Color.BLUE);
  public static final ChatMessage CHAT_USER_LEFT   = new ChatMessage("chat_user_left", Color.RED);
  public static final ChatMessage CHAT_SERVER_CLOSED = new ChatMessage("chat_server_closed", Color.LIGHT_GRAY);
  public static final ChatMessage CHAT_JOINING_SERVER = new ChatMessage("chat_connecting", Color.GRAY);
  public static final ChatMessage CHAT_CONNECTED = new ChatMessage("chat_connected", Color.GRAY);
  public static final ChatMessage CHAT_LEFT_SERVER = new ChatMessage("chat_left_server", Color.LIGHT_GRAY);
  public static final ChatMessage CHAT_HOSTED_SERVER = new ChatMessage("chat_hosted_server", new Color(230, 90, 0));
  public static final ChatMessage CHAT_LOADED_CIRCUIT = new ChatMessage("chat_loaded_circuit", Color.GREEN.darker());
  public static final ChatMessage CHAT_SAVED_CIRCUIT = new ChatMessage("chat_saved_circuit", Color.GREEN.darker());
  public static final ChatMessage CHAT_NEW_CIRCUIT = new ChatMessage("chat_new_circuit", Color.GREEN.darker());

  private ProgramBackend backend; // TODO Session; Extrahera en chatt-klass ifrån PB så att jag slipper spara den här klassen?
  
  private LogicServer server;
  private LogicClient client;
  
  private volatile boolean isClient;
  private Map<String, User> connectedUsers; // TODO Session; Flytta till client?
  
  
  public Session(ProgramBackend backend)
  {
    this.backend = backend;
    
    connectedUsers = new HashMap<String, User>();
    
    server = new LogicServer(serverStartListener);
    client = new LogicClient(clientDisconnectListener);
    
    createNetworkHooks(backend);
  }

  private void createNetworkHooks(ProgramBackend backend)
  {
    List<NetworkHook<?>> networkHooks = ReflectionUtils.runMethodForAllClassesInPackage("network/hooks", NetworkHook.class, new Class [] {ProgramBackend.class}, backend);
    Debugger.debug(Session.class.getSimpleName() + ": createNetworkHooks()", "Registering network hooks (" + networkHooks.size() + "):", false);
    for (NetworkHook<?> hook : networkHooks) {
      client.getNetwork().registerHook(hook);
      server.getNetwork().registerHook(hook);
      Debugger.debug(Session.class.getSimpleName() + ": createNetworkHooks()", " - " + hook.getClass().getSimpleName(), false);
    }
  }
  
  
  public void startLocalServer()
  {
    client.disconnect();
    server.stop();

    connectedUsers.clear();

    int port = Integer.parseInt(Config.get(Config.HOST_PORT));
    server.start(port);
    
    isClient = false;
  }
  
  public void connectTo(String ip, int port, String username)
  {
    client.disconnect();
    server.stop();

    connectedUsers.clear();
    
    client.setUsername(username);
    client.setSecureHostIdentifier(-1);
    client.connectTo(ip, port);
    
    isClient = true;
  }
  
  public void dispose()
  {
    client.disconnect();
    server.stop();
  }
  
  
  public void userJoined(String id)
  {
    connectedUsers.put(id, new User(id, id));
  }
  
  public void userLeft(String id)
  {
    connectedUsers.remove(id);
  }
  
  public User getUser(String id)
  {
    return connectedUsers.get(id);
  }
  
  public Collection<User> getUsers()
  {
    return connectedUsers.values();
  }
  
  
  
  public boolean isClient()
  {
    return isClient;
  }

  public LogicServer getServer()
  {
    return server;
  }

  public LogicClient getClient()
  {
    return client;
  }

  
  private ClientListener clientDisconnectListener = new ClientListener() {
    
    @Override
    public void connectionFailed()
    {
      if (isClient)
      {
        backend.getWindow().openMessageDialog(
            Table.get("host_unreachable_message"),
            Table.get("host_unreachable_title"),
            MessageType.Warning);
        
        backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
        startLocalServer();

        backend.refreshInterface();
        backend.refreshRenderingSurface();
        backend.refreshChat();
      }
      else
      {
        backend.getWindow().openMessageDialog(
            Table.get("local_connection_failed_message"),
            Table.get("local_connection_failed_title"),
            MessageType.Error);
        System.exit(-1); // TODO Session; Shut down the program properly, use the backend to shut down the program
      }
    }
    
    @Override
    public void connected()
    {
    }
    
    @Override
    public void disconnected(DisconnectReason reason)
    {
      if (reason == DisconnectReason.Timeout)
      {
        EventQueue.invokeLater(new Runnable() {
          @Override
          public void run()
          {
            backend.getWindow().openMessageDialog(
                Table.get("warning_disconnected_timeout_message"),
                Table.get("popup_title_disconnected"),
                MessageType.Warning);

            backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_LEFT_SERVER.text), Session.CHAT_LEFT_SERVER.color));
            backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
            startLocalServer();

            backend.refreshInterface();
            backend.refreshRenderingSurface();
            backend.refreshChat();
          }
        });
      }
    }
  };
  
  
  private ServerStartListener serverStartListener = new ServerStartListener() {
    
    private boolean startFailed;
    
    @Override
    public void startFailed()
    {
      if (!startFailed) {
        server.start(0);
        startFailed = true;
      } else {
        Debugger.fatal("Session: startFailed()", "Failed to start server with arbitrary port");
        // TODO Session; Informera användaren om att server ej kunde skapas
      }
    }
    
    @Override
    public void serverStarted(final int port, int secureHostIdentifier)
    {
      client.setUsername(Config.get(Config.USER_NAME));
      client.setSecureHostIdentifier(secureHostIdentifier);
      client.connectTo("localhost", port);
      
      if (startFailed)
      {
        EventQueue.invokeLater(new Runnable() {
          
          @Override
          public void run()
          {
            backend.getWindow().openMessageDialog(
                MessageFormat.format(Table.get("port_in_use_message"), port),
                Table.get("port_in_use_title"),
                MessageType.Warning);
          }
        });
      }
      startFailed = false;
    }
  };
}
