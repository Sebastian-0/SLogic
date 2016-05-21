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
import network.Session;
import network.User;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import configuration.Table;

public class SetUsernameHook extends ExtendedHook
{

  public SetUsernameHook(ProgramBackend backend)
  {
    super('\u1014', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    String senderId = ((ServerConnection)message.receiver).getId();
    User user = backend.getSession().getServer().getUser(senderId);
    if (user != null)
    {
      user.name = message.extract();
      server.sendToAll(createMessage(senderId, message.extract()));
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    String data[] = message.extract().split("\\" + SEPARATOR);
    User user = backend.getSession().getUser(data[0]);
    if (user != null)
    {
      if (user.name.equals(data[0]))
      {
        backend.putChatMessage(new ChatMessage(data[1] + " " + Table.get(Session.CHAT_USER_JOINED.text), Session.CHAT_USER_JOINED.color));
      }
      user.name = data[1];
      
      backend.refreshChat();
    }
  }
  
  public Message createMessage(String id, String username)
  {
    return createMessage(id + SEPARATOR + username);
  }
}
