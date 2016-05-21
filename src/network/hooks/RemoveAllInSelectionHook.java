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
import gui.backend.ProgramBackend;

import java.awt.Point;
import java.awt.Rectangle;

import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import circuit.Circuit;
import database.Database;

public class RemoveAllInSelectionHook extends ExtendedHook
{
  public RemoveAllInSelectionHook(ProgramBackend backend)
  {
    super('\u1046', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    ServerConnection receiver = (ServerConnection)message.receiver;
    if (canUserEdit(receiver))
    {
      String[] data = message.extract().split("\\" + SEPARATOR);
      Point start = new Point(integer(data[0]), integer(data[1]));
      Point end = new Point(integer(data[2]), integer(data[3]));
      
      Database database = backend.getSession().getServer().getWorkspace().database;
      doRemove(
          database.getGraphicalCircuit(),
          database.getCircuit(),
          start,
          end);
      
      server.sendToAll(message);
    }
  }

  private void doRemove(GraphicalCircuit gcircuit, Circuit circuit, Point start, Point end)
  {
    gcircuit.removeComponentsInside(
        new Rectangle(
          start.x,
          start.y,
          end.x - start.x,
          end.y - start.y),
        circuit);
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    Point start = new Point(integer(data[0]), integer(data[1]));
    Point end = new Point(integer(data[2]), integer(data[3]));
    
    Database database = backend.getSession().getClient().getWorkspace().database;
    doRemove(
        database.getGraphicalCircuit(),
        database.getCircuit(),
        start,
        end);
    
    backend.refreshRenderingSurface();
  }
  
  /**
   * Creates a message describing a clipboard creation.
   * @param selectionStart The start of the selection (lowest x/y-coordinates)
   * @param selectionEnd The end of the selection (highest x/y-coordinates)
   * @return A message that can be sent over the network
   */
  public Message createMessage(Point selectionStart, Point selectionEnd)
  {
    return createMessage(
        selectionStart.x + SEPARATOR + selectionStart.y + SEPARATOR +
        selectionEnd.x + SEPARATOR + selectionEnd.y);
  }
}
