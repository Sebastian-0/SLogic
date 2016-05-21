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

import java.awt.Color;

import network.User;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;

public class SendUserChatMessageHook extends ExtendedHook
{
  public SendUserChatMessageHook(ProgramBackend backend)
  {
    super('\u1018', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    server.sendToAll(createMessage(((ServerConnection)message.receiver).getId(), message.extract()));
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR, -1);
    
    User user = backend.getSession().getUser(data[0]);
    if (user != null)
    {
      String text = user.name + ": " + data[1];
      backend.putChatMessage(new ChatMessage(text, Color.BLACK));
      backend.refreshChat();
    }
  }
  
  
  public Message createMessage(String id, String text)
  {
    return createMessage(id + SEPARATOR + text);
  }
}
