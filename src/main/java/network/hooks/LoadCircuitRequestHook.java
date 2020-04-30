/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import gui.backend.ProgramBackend;
import gui.backend.ProgramWindow.MessageType;

import java.io.File;

import network.Session;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import sutilities.Debugger;

public class LoadCircuitRequestHook extends ExtendedHook
{
  public LoadCircuitRequestHook(ProgramBackend backend)
  {
    super('\u1012', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    String senderId = ((ServerConnection)message.receiver).getId();
    
    try
    {
      String targetFilePath = message.extract();
      if (senderId.equals(backend.getSession().getServer().getHostId()))
      {
        File targetFile = new File(targetFilePath);
        backend.getSession().getServer().getWorkspace().loadCircuitFrom(targetFile);
        Message loadCircuitMessage = new TransferCircuitHook(null).createMessage(targetFile, true);
        server.sendToAll(loadCircuitMessage);

        Message msg = new SendInformationChatMessageHook(null).createMessage(
            Session.CHAT_LOADED_CIRCUIT.text,
            Session.CHAT_LOADED_CIRCUIT.color);
        server.sendToAll(msg);
      }
      else
      {
        Message error = new OptionPaneMessageHook(null).createMessage(
            "popup_title_error",
            "load_only_host_may_load",
            MessageType.Error);
        server.send(senderId, error);
      }
    }
    catch (Throwable e)
    {
      reportException(server, e);
    }
  }

  private void reportException(Server server, Throwable e)
  {
    Message error = new OptionPaneMessageHook(null).createMessage(
        "popup_title_error",
        "load_failed" + OptionPaneMessageHook.TABLE_KEY_END_MARKER + ": " + e.getMessage(),
        MessageType.Error);
    server.send(backend.getSession().getServer().getHostId(), error);

    Debugger.error("LoadCircuitRequestHook", "Failed to load: " + e.getMessage(), e);
  }

  @Override
  public void client(Client client, Message message)
  {
  }
  
  
  public Message createMessage(File targetFile)
  {
    return createMessage(targetFile.getAbsolutePath());
  }
}
