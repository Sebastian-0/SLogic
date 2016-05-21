/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import graphicalcircuit.GraphicalCircuit;
import graphicalcircuit.GraphicalComponent;
import gui.backend.ProgramBackend;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import circuit.Circuit;
import database.Database;

public class RemoveComponentHook extends ExtendedHook
{
  public RemoveComponentHook(ProgramBackend backend)
  {
    super('\u1002', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      Database database = backend.getSession().getServer().getWorkspace().database;
      if (doRemove(
          database.getCircuit(),
          database.getGraphicalCircuit(),
          message))
      {
        server.sendToAll(message);
      }
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    if (doRemove(
        database.getCircuit(),
        database.getGraphicalCircuit(),
        message))
    {
      backend.refreshRenderingSurface();
    }
  }

  private boolean doRemove(Circuit circuit, GraphicalCircuit gcircuit, Message message)
  {
    int id = Integer.parseInt(message.extract());
    GraphicalComponent removedComponent = gcircuit.getComponent(id);
    if (removedComponent != null)
    {
      gcircuit.removeComponent(removedComponent);
      removedComponent.wasRemoved();
      circuit.removeComponent(removedComponent.getComponent());
      return true;
    }
    return false;
  }
  

  public Message createMessage(GraphicalComponent componentToRemove)
  {
    return createMessage(Integer.toString(componentToRemove.getComponent().getId()));
  }
}
