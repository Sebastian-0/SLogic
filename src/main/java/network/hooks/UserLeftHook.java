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
import configuration.Table;

public class UserLeftHook extends ExtendedHook
{

  public UserLeftHook(ProgramBackend backend)
  {
    super('\u1015', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    String userId = message.extract();
    User user = backend.getSession().getUser(userId);
    if (user != null)
    {
      backend.putChatMessage(new ChatMessage(user.name + " " + Table.get(Session.CHAT_USER_LEFT.text), Session.CHAT_USER_LEFT.color));
    }
    backend.getSession().userLeft(message.extract());
    
    backend.refreshRenderingSurface();
    backend.refreshChat();
  }
}
