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
import network.User.Privileges;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import sutilities.Debugger;

public class ClientStatusHook extends ExtendedHook
{
  public ClientStatusHook(ProgramBackend backend)
  {
    super ('\u1043', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    
    Privileges privileges = privileges(data[0]);
    
    backend.getSession().getClient().setPrivileges(privileges);
    backend.refreshInterface();
    backend.refreshRenderingSurface();
  }

  private Privileges privileges(String data)
  {
    try
    {
      Privileges privileges = Privileges.valueOf(data);
      return privileges;
    }
    catch (IllegalArgumentException e)
    {
      Debugger.warning("ClientStatusHook: client()", "Unknown privilege group \"" + data + "\", using read only access instead.");
      return Privileges.Read;
    }
  }

  public Message createMessage(Privileges privileges, boolean isSetAsHost)
  {
    return createMessage(privileges.name() + SEPARATOR + isSetAsHost);
  }
}
