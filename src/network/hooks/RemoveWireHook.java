/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import graphicalcircuit.GraphicalWire;
import gui.backend.ProgramBackend;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import database.Database;

public class RemoveWireHook extends ExtendedHook
{
  public RemoveWireHook(ProgramBackend backend)
  {
    super('\u1031', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      Database database = backend.getSession().getServer().getWorkspace().database;
      if (doRemove(database, message)) 
      {
        server.sendToAll(message);
      }
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    if (doRemove(database, message)) 
    {
      backend.refreshRenderingSurface();
    }
  }

  private boolean doRemove(Database database, Message message) 
  {
    int wireId = integer(message.extract());
    GraphicalWire wire = database.getGraphicalCircuit().getWire(wireId);
    if (wire != null)
    {
      database.getGraphicalCircuit().removeWire(wire);
      wire.wasRemoved();
      database.getCircuit().removeWire(wire.getWire());
      return true;
    }
    return false;
  }

  public Message createMessage(GraphicalWire wire)
  {
    return createMessage(Integer.toString(wire.getWire().getId()));
  }
}
