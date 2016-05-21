/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend.tools;

import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.GraphicalWire;
import graphicalcircuit.GraphicalWire.Node;
import gui.backend.ProgramBackend;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import util.Grid;
import snet.internal.Message;
import network.hooks.RemoveComponentHook;
import network.hooks.RemoveWireSegmentHook;
import configuration.Table;
import database.Database;

public class DeletionTool extends AbstractSelectionTool
{
  private List<Message> messagesToSend;
  
  public DeletionTool()
  {
    messagesToSend = new ArrayList<Message>();
  }
  
  @Override
  protected void performActionOnSelection(ProgramBackend backend, Point start, Point end)
  {
    List<GraphicalComponent> removedComponents = new ArrayList<GraphicalComponent>();
    List<Node> removedWireNodes = new ArrayList<Node>();
    
    // TODO DeletionTool; Flytta till nätverks-hooken istället? Då behöver man endast skicka 1 meddelande, inte hundratals...
    for (int x = (int)start.getX(); x <= end.getX(); x += Grid.GRID_WIDTH)
    {
      for (int y = (int)start.getY(); y <= end.getY(); y += Grid.GRID_WIDTH)
      {
        if (!removeWireSegment(removedWireNodes, backend, new Point(x, y)))
          removeCircuit(removedComponents, backend, new Point(x, y));
      }
    }
    
    for (Message msg : messagesToSend)
      backend.getSession().getClient().getNetwork().send(null, msg);
    
    messagesToSend.clear();
  }
  
  private boolean removeWireSegment(List<Node> removedWireNodes, ProgramBackend backend, Point alignedMouse)
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    GraphicalWire wire = database.getGraphicalCircuit().getWireAt(alignedMouse.x, alignedMouse.y);
    if (wire != null)
    {
      Node node = wire.getNodeAt(alignedMouse.x, alignedMouse.y);
      if (!removedWireNodes.contains(node))
      {
        removedWireNodes.add(node);
        Message msg = new RemoveWireSegmentHook(null).createMessage(alignedMouse.x, alignedMouse.y);
        messagesToSend.add(msg);
        return true;
      }
    }
    return false;
  }

  private void removeCircuit(List<GraphicalComponent> removedComponents, ProgramBackend backend, Point alignedMouse)
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    GraphicalComponent component = database.getGraphicalCircuit().getComponentAt(alignedMouse.x, alignedMouse.y);
    if (component != null)
    {
      if (!removedComponents.contains(component))
      {
        removedComponents.add(component);
        Message msg = new RemoveComponentHook(null).createMessage(component);
        messagesToSend.add(msg);
      }
    }
  }
  
  @Override
  protected Color getSelectionColor()
  {
    return Color.RED;
  }
  
  @Override
  public void reset() {
    messagesToSend.clear();
  }

  @Override
  public String getName()
  {
    return Table.get("tool_delete");
  }
}
