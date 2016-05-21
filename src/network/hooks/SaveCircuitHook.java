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
import java.io.IOException;

import network.Session;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import sutilities.Debugger;

public class SaveCircuitHook extends ExtendedHook
{
  public SaveCircuitHook(ProgramBackend backend)
  {
    super('\u1009', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      String senderId = ((ServerConnection)message.receiver).getId();
      try
      {
        String targetFilePath = message.extract();
        if (senderId.equals(backend.getSession().getServer().getHostId()) && targetFilePath.length() > 0)
        {
          if (!backend.getSession().getServer().getWorkspace().hasSaveLocation())
          {
            Message msg = new FlagCurrentCircuitAsSavedHook(null).createMessage("");
            server.sendToAll(msg);
          }
          File targetFile = new File(targetFilePath);
          backend.getSession().getServer().getWorkspace().saveCircuitTo(targetFile);
          
          sendSavedMessage(server);
        }
        else
        {
          if (backend.getSession().getServer().getWorkspace().hasSaveLocation())
          {
            backend.getSession().getServer().getWorkspace().saveCircuit();
            
            sendSavedMessage(server);
          }
          else
          {
            Message error = new OptionPaneMessageHook(null).createMessage(
                "popup_title_error",
                "save_error_host_must_choose_save_file",
                MessageType.Error);
            server.send(senderId, error);
  
            Message request = new OptionPaneMessageHook(null).createMessage(
                "save_user_wants_to_save_title",
                "save_user_wants_to_save_message",
                MessageType.Information);
            server.send(backend.getSession().getServer().getHostId(), request);
          }
        }
      }
      catch (IOException e)
      {
        Message error = new OptionPaneMessageHook(null).createMessage(
            "popup_title_error",
            "save_failed" + OptionPaneMessageHook.TABLE_KEY_END_MARKER + ": " + e.getMessage(),
            MessageType.Error);
        server.send(backend.getSession().getServer().getHostId(), error);
        
        Debugger.error("SaveCircuitHook", "Failed to save: " + e.getMessage(), e);
      }
    }
  }

  private void sendSavedMessage(Server server)
  {
    Message msg = new SendInformationChatMessageHook(null).createMessage(
        Session.CHAT_SAVED_CIRCUIT.text,
        Session.CHAT_SAVED_CIRCUIT.color);
    server.sendToAll(msg);
  }

  @Override
  public void client(Client client, Message message)
  {
  }
  
  
  public Message createMessage(File targetFile)
  {
    if (targetFile == null)
      return createMessage("");
    return createMessage(targetFile.getAbsolutePath());
  }
}
