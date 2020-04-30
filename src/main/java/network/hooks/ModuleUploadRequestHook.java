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
import gui.backend.ProgramWindow.DialogAnswer;
import gui.backend.ProgramWindow.MessageOptions;
import gui.backend.ProgramWindow.MessageType;

import java.awt.EventQueue;
import java.io.File;
import java.text.MessageFormat;

import network.User;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import configuration.Table;

public class ModuleUploadRequestHook extends ExtendedHook
{
  public ModuleUploadRequestHook(ProgramBackend backend)
  {
    super('\u1037', backend);
  }
  

  @Override
  public void server(final Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      final String senderId = ((ServerConnection)message.receiver).getId();
      
      final String messageContents = message.extract();
      final String data[] = messageContents.split("\\" + SEPARATOR);
      
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() // TODO ModuleUploadHook; Hosten ska få frågan skickad till sig på något sätt
        {
          User user = backend.getSession().getUser(senderId);
          DialogAnswer result = backend.getWindow().openOptionDialog(
              MessageFormat.format(Table.get("modules_upload_response_message"), user.name, data[0]),
              Table.get("modules_upload_response_title"),
              MessageOptions.YesNo,
              MessageType.Question);
          if (result == DialogAnswer.Yes)
          {
            // Sending the message back means the request has been accepted
            server.send(senderId, createMessage(messageContents));
          }
          else
          {
            Message msg = new OptionPaneMessageHook(null).createMessage(
                "modules_upload_request_denied_title",
                "modules_upload_request_denied_message",
                MessageType.Error);
            server.send(senderId, msg);
          }
        }
      });
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    String data[] = message.extract().split("\\" + SEPARATOR);
    
    File moduleFile = new File(data[1]);
    Message msg = new TransferModuleHook(null).createMessage(moduleFile);
    client.send(null, msg);
  }
  
  public Message createMessage(File fileToUpload)
  {
    return createMessage(fileToUpload.getName() + SEPARATOR + fileToUpload.getAbsolutePath());
  }
}
