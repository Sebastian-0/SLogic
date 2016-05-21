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
import graphicalcircuit.GraphicalWire;
import gui.backend.ProgramBackend;

import java.awt.Point;

import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import circuit.Circuit;
import database.Database;

public class MergeWireHook extends ExtendedHook
{
  public MergeWireHook(ProgramBackend backend)
  {
    super('\u1008', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      Database database = backend.getSession().getServer().getWorkspace().database;
      doMerge(
          database.getGraphicalCircuit(),
          database.getCircuit(), message);
      
      server.sendToAll(message);
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    doMerge(database.getGraphicalCircuit(), database.getCircuit(), message);

    backend.refreshRenderingSurface();
  }

  private void doMerge(GraphicalCircuit gcircuit, Circuit circuit, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    int mergeTargetId = integer(data[0]);
    int wireToMergeId = integer(data[1]);
    Point mergePosition = new Point(integer(data[2]), integer(data[3]));
    
    GraphicalWire mergeTarget = gcircuit.getWire(mergeTargetId);
    GraphicalWire wireToMerge = gcircuit.getWire(wireToMergeId);
    
    mergeTarget.mergeWith(wireToMerge, mergePosition);
    
    gcircuit.removeWire(wireToMerge);
    circuit.removeWire(wireToMerge.getWire());

    mergeTarget.optimize();
  }

  
  public Message createMessage(GraphicalWire mergeTarget, GraphicalWire wireToMerge, Point mergePosition)
  {
    return createMessage(
        mergeTarget.getWire().getId() + SEPARATOR +
        wireToMerge.getWire().getId() + SEPARATOR +
        mergePosition.x + SEPARATOR +
        mergePosition.y);
  }
}
