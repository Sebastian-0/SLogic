/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import graphicalcircuit.CircuitType;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.Rotation;
import gui.backend.ProgramBackend;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import circuit.Component;
import database.Database;

public class CreateComponentHook extends ExtendedHook
{
  public CreateComponentHook(ProgramBackend backend)
  {
    super('\u1000', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      parseMessage(
          backend.getSession().getServer().getWorkspace().database,
          message);
      
      server.sendToAll(message);
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    parseMessage(
        backend.getSession().getClient().getWorkspace().database,
        message);
    
    backend.refreshRenderingSurface();
  }

  private void parseMessage(Database database, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    int xPos = integer(data[0]);
    int yPos = integer(data[1]);
    Rotation rotation = rotation(data[2]);
    String identifier = data[3];
    
    CircuitType type = null;
    for (CircuitType t : database.getCircuitTypes()) {
      if (t.getIdentifierString().equals(identifier)) {
        type = t;
        break;
      }
    }
    
    if (type != null) {
      Component component = type.makeBackendComponent(
          database.getCircuit().nextId());
      GraphicalComponent gcomponent = new GraphicalComponent(
          type.makeCopy(), 
          component, 
          xPos, 
          yPos, 
          rotation);
      
      database.getGraphicalCircuit().addComponent(gcomponent);
      database.getCircuit().addComponent(component);
    }
  }

  
  public Message createMessage(int xPos, int yPos, Rotation rotation, CircuitType type)
  {
    return createMessage(xPos + SEPARATOR + yPos + SEPARATOR + rotation.id + SEPARATOR + type.getIdentifierString());
  }
}
