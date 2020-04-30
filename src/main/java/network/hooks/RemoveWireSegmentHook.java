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

public class RemoveWireSegmentHook extends ExtendedHook
{
  public RemoveWireSegmentHook(ProgramBackend backend)
  {
    super('\u1006', backend);
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

  private boolean doRemove(Database database, Message message) {
    String data[] = message.extract().split("\\" + SEPARATOR);
    int segmentX = integer(data[0]);
    int segmentY = integer(data[1]);
    
    GraphicalWire wire = database.getGraphicalCircuit().getWireAt(segmentX, segmentY);
    if (wire != null)
    {
      wire.removeWireSegmentAt(
          database.getCircuit(), 
          database.getGraphicalCircuit(), 
          segmentX,
          segmentY);
      
      wire.optimize();
      return true;
    }
    return false;
  }

  public Message createMessage(int segmentX, int segmentY)
  {
    return createMessage(segmentX + SEPARATOR + segmentY);
  }
}
