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
import snet.internal.ServerConnection;

public class SetUserMousePositionHook extends ExtendedHook
{

  public SetUserMousePositionHook(ProgramBackend backend)
  {
    super('\u1026', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    String senderId = ((ServerConnection)message.receiver).getId();
    message = createMessage(senderId + SEPARATOR + message.extract());
    server.forward(message);
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    
    User user = backend.getSession().getUser(data[0]);
    if (user != null)
    {
      user.currentCursorPosition.setLocation(integer(data[1]), integer(data[2]));
      backend.refreshRenderingSurface();
    }
  }
  
  
  public Message createMessage(int x, int y)
  {
    return createMessage(x + SEPARATOR + y);
  }
}

