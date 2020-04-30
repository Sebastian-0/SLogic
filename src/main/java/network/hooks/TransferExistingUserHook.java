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

public class TransferExistingUserHook extends ExtendedHook
{
  public TransferExistingUserHook(ProgramBackend backend)
  {
    super('\u1020', backend);
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
    backend.getSession().getUser(data[0]).name = data[1];
    backend.refreshChat();
  }

  
  public Message createMessage(String idOfUserToTransfer, User user)
  {
    return createMessage(idOfUserToTransfer + SEPARATOR + user.name);
  }
}
