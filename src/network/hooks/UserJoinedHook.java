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
import network.User;
import snet.Client;
import snet.Server;
import snet.internal.Message;

public class UserJoinedHook extends ExtendedHook
{

  public UserJoinedHook(ProgramBackend backend)
  {
    super('\u1016', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    String data[] = message.extract().split("\\" + SEPARATOR);
    backend.getSession().userJoined(data[0]);
    boolean isSentToTheNewUser = Boolean.parseBoolean(data[1]);
    if (isSentToTheNewUser)
    {
      User user = backend.getSession().getUser(data[0]);
      user.name = ""; // Do this to avoid 'user joined' message for the joining user
      user.isTheUserOfThisClient = true;

      // If the server doesn't have any modules, we must still refresh the component tree
      backend.refreshComponentTree(); // TODO UserJoinedHook; Flytta detta till Session.connect() och Session.startLocalServer()?
    }
    backend.refreshChat();
  }
  
  
  public Message createMessage(String id, boolean isSentToTheNewUser)
  {
    return createMessage(id + SEPARATOR + isSentToTheNewUser);
  }
}
