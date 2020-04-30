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

import network.Session;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import configuration.Table;

public class ClientDroppedHook extends ExtendedHook
{
  public ClientDroppedHook(ProgramBackend backend)
  {
    super ('\u1044', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    String data[] = message.extract().split("\\" + SEPARATOR);
    final MessageType type = MessageType.getTypeFromNumber(integer(data[0]));
    final String title = data[1];
    final String text = data[2];

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        backend.getWindow().openMessageDialog(Table.get(text), Table.get(title), type);

        backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
        backend.getSession().startLocalServer();
        backend.refreshInterface();
        backend.refreshRenderingSurface();
        backend.refreshChat();
      }
    });
  }
  
  
  public Message createMessage(String title, String message, MessageType messageType)
  {
    return createMessage(messageType.value + SEPARATOR + title + SEPARATOR + message);
  }
}
