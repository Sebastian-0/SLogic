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
import graphicalcircuit.GraphicalWire;
import gui.backend.ProgramBackend;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import snet.Client;
import snet.Server;
import snet.internal.Message;
import circuit.Circuit;
import circuit.CircuitEntity;
import circuit.Wire;
import database.Database;

public class CreateWireHook extends ExtendedHook
{
  public CreateWireHook(ProgramBackend backend)
  {
    super ('\u1003', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    GraphicalCircuit gcircuit = backend.getSession().getServer().getWorkspace().database.getGraphicalCircuit();
    Circuit circuit = backend.getSession().getServer().getWorkspace().database.getCircuit();
    
    String[] data = message.extract().split("\\" + SEPARATOR);
    int id1 = integer(data[0]);
    int id2 = integer(data[1]);
    List<Point> nodes = buildNodes(data, 2);
    if (shouldReverseNodes(gcircuit, id2, nodes))
    {
      Collections.reverse(nodes);
      int tmp = id1;
      id1 = id2;
      id2 = tmp;
    }
    
    Wire wire = new Wire(circuit.nextId());
    GraphicalWire gwire = connectComponentsToWire(gcircuit, id1, id2, nodes, wire);
    
    circuit.addWire(wire);
    gcircuit.addWire(gwire);
    
    gwire.optimize();
    
    server.sendToAll(message);

    boolean startIsWire = gcircuit.getWire(id1) != null;
    boolean endIsWire   = gcircuit.getWire(id2) != null;
    if (startIsWire)
    {
      MergeWireHook hook = new MergeWireHook(backend);
      Message newMessage = hook.createMessage(gwire, gcircuit.getWire(id1), nodes.get(0));
      newMessage.receiver = message.receiver;
      hook.server(server, newMessage);
    }
    if (endIsWire)
    {
      MergeWireHook hook = new MergeWireHook(backend);
      Message newMessage = hook.createMessage(gwire, gcircuit.getWire(id2), nodes.get(nodes.size()-1));
      newMessage.receiver = message.receiver;
      hook.server(server, newMessage);
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    int id1 = integer(data[0]);
    int id2 = integer(data[1]);
    List<Point> nodes = buildNodes(data, 2);

    Database database = backend.getSession().getClient().getWorkspace().database;
    Wire wire = new Wire(database.getCircuit().nextId());
    GraphicalWire gwire = connectComponentsToWire(database.getGraphicalCircuit(), id1, id2, nodes, wire);
    
    database.getCircuit().addWire(wire);
    database.getGraphicalCircuit().addWire(gwire);
    
    gwire.optimize();

    backend.refreshRenderingSurface();
  }

  private List<Point> buildNodes(String[] data, int startIndex)
  {
    List<Point> nodes = new ArrayList<Point>();
    for (int i = startIndex; i < data.length; i += 2)
    {
      int x = integer(data[i]);
      int y = integer(data[i+1]);
      nodes.add(new Point(x, y));
    }
    return nodes;
  }
  
  private boolean shouldReverseNodes(GraphicalCircuit gc, int id2, List<Point> nodes)
  {
    Point lastPoint = nodes.get(nodes.size()-1);
    GraphicalComponent comp = gc.getComponent(id2);
    if (comp != null)
    {
      return !comp.getPinAt(lastPoint.x, lastPoint.y).isInput();
    }
    return false;
  }

  private GraphicalWire connectComponentsToWire(GraphicalCircuit targetCircuit,
      int id1, int id2, List<Point> nodes, Wire wire)
  {
    GraphicalComponent c1 = targetCircuit.getComponent(id1);
    GraphicalComponent c2 = targetCircuit.getComponent(id2);
    
    GraphicalWire gwire = new GraphicalWire(nodes, wire, c1, c2);
    
    return gwire;
  }
  
  
  public Message createMessage(CircuitEntity c1, CircuitEntity c2, List<Point> nodes)
  {
    int id1 = -1;
    int id2 = -1;
    if (c1 != null) id1 = c1.getId();
    if (c2 != null) id2 = c2.getId();
    
    StringBuilder builder = new StringBuilder();
    builder.append(id1).append(SEPARATOR);
    builder.append(id2).append(SEPARATOR);
    for (Point p : nodes)
    {
      builder.append(p.x).append(SEPARATOR);
      builder.append(p.y).append(SEPARATOR);
    }
    builder.delete(builder.length() - SEPARATOR.length(), builder.length());
    
    return createMessage(builder.toString());
  }
}
