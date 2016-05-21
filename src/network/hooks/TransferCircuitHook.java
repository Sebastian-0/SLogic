/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import gui.backend.ChatMessage;
import gui.backend.ProgramBackend;
import gui.backend.ProgramWindow.MessageType;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import network.Session;
import network.Workspace;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import sutilities.Debugger;
import configuration.Table;

public class TransferCircuitHook extends AbstractFileTransferHook
{

  public TransferCircuitHook(ProgramBackend backend)
  {
    super('\u1013', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    boolean hasCircuitBeenSaved = (integer("" + message.extract().charAt(0)) == 1);
    String fileData = message.extract().substring(1);
    
    File tempFile = writeDataStringToTempFile(fileData);
    
    try
    {
      if (tempFile != null)
      {
        Workspace workspace = backend.getSession().getClient().getWorkspace();
        workspace.loadCircuitFrom(tempFile);
        if (!hasCircuitBeenSaved)
          workspace.currentSaveLocation = null;
        
        backend.refreshRenderingSurface(true);
      }
    } catch (IOException e)
    {
      Debugger.error("TransferCircuitHook: client()", "Failed to load the database!", e);
      failedToTransfer();
    } catch (ClassNotFoundException e) {
      Debugger.error("TransferCircuitHook: client()", "Failed to load the database!", e);
      failedToTransfer();
    } catch (IllegalStateException e) {
      Debugger.error("TransferCircuitHook: client()", "Failed to load the database!", e);
      failedToLoad();
    }
  }

  private void failedToTransfer()
  {
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_LEFT_SERVER.text), Session.CHAT_LEFT_SERVER.color));
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
    backend.getSession().startLocalServer();
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        backend.getWindow().openMessageDialog(
            Table.get("transfer_circuit_failed_transfer_message"),
            Table.get("transfer_circuit_failed_transfer_title"),
            MessageType.Error);
      }
    });

    backend.refreshInterface();
    backend.refreshRenderingSurface();
    backend.refreshChat();
  }
  
  private void failedToLoad()
  {
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_LEFT_SERVER.text), Session.CHAT_LEFT_SERVER.color));
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
    backend.getSession().startLocalServer();
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        backend.getWindow().openMessageDialog(
            Table.get("transfer_circuit_missing_circuit_types_message"),
            Table.get("transfer_circuit_missing_circuit_types_title"),
            MessageType.Error);
      }
    });
    
    backend.refreshInterface();
    backend.refreshRenderingSurface();
    backend.refreshChat();
  }

  public Message createMessage(File fileToLoad, boolean hasCircuitBeenSaved)
  {
    String fileContents = getFileAsString(fileToLoad);
    
    int extraNumber = 0;
    if (hasCircuitBeenSaved) extraNumber = 1;
    return createMessage(extraNumber + fileContents);
  }
}
