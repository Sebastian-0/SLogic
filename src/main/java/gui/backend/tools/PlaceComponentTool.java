/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend.tools;

import graphicalcircuit.CircuitType;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.Rotation;
import graphicalcircuit.config.ComponentConfig;
import gui.backend.ProgramBackend;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import snet.internal.Message;
import network.hooks.CreateComponentHook;
import network.hooks.RemoveComponentHook;
import circuit.Gnd;
import configuration.Table;
import database.Database;

public class PlaceComponentTool extends AbstractTool
{
  private Rotation rotation;
  
  public PlaceComponentTool()
  {
    rotation = Rotation.EAST;
  }

  @Override
  public boolean mousePressed(ProgramBackend backend, MouseEvent event,
      int viewportX, int viewportY)
  {
    Point alignedMouse = new Point(event.getX() + viewportX, event.getY() + viewportY);
    alignToGrid(alignedMouse);
    
    if (isLeftMouseButton(event))
    {
      Database database = backend.getSession().getClient().getWorkspace().database;
      GraphicalComponent component = database.getGraphicalCircuit().getComponentAt(alignedMouse.x, alignedMouse.y);
      if (component == null)
        return createCircuit(backend, alignedMouse);
      else
      {
        backend.getWindow().openConfigDialogFor(component);
      }
    }
    else if (isRightMouseButton(event))
    {
      return removeCircuit(backend, alignedMouse);
    }
    
    return false;
  }

  private boolean createCircuit(ProgramBackend backend, Point alignedMouse)
  {
    CircuitType selectedCircuitType = backend.getSession().getClient().getWorkspace().selectedCircuitType;
    if (selectedCircuitType != null)
    {
      Message msg = new CreateComponentHook(backend).createMessage(alignedMouse.x, alignedMouse.y, rotation, selectedCircuitType);
//      Message msg = selectedCircuitType.creationMessage(alignedMouse.x, alignedMouse.y, rotation);
      backend.getSession().getClient().getNetwork().send(null, msg);
      return true;
    }
    return false;
  }

  private boolean removeCircuit(ProgramBackend backend, Point alignedMouse)
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    GraphicalComponent component = database.getGraphicalCircuit().getComponentAt(alignedMouse.x, alignedMouse.y);
    if (component != null)
    {
      Message msg = new RemoveComponentHook(null).createMessage(component);
      backend.getSession().getClient().getNetwork().send(null, msg);
      return true;
    }
    return false;
  }
  
  @Override
  public boolean keyPressed(ProgramBackend backend, KeyEvent event)
  {
    if (event.isControlDown())
    {
      if (event.getKeyCode() == KeyEvent.VK_R)
      {
        rotation = Rotation.getRotationFromId((short)(((rotation.id - 1) < 0) ? 3 : (rotation.id - 1)));
        return true;
      }
      else if (event.getKeyCode() == KeyEvent.VK_T)
      {
        rotation = Rotation.getRotationFromId((short)((rotation.id + 1) % 4));
        return true;
      }
    }
    
    return false;
  }
  
  
  @Override
  public void paintMarker(ProgramBackend backend, Graphics2D g, int xPos, int yPos)
  {
    Point pos = new Point(xPos, yPos);
    alignToGrid(pos);
    
    CircuitType type = backend.getSession().getClient().getWorkspace().selectedCircuitType;
    if (type != null)
    {
      ComponentConfig config = type.generateDefaultConfig(new Gnd(0));
      g.rotate(rotation.rotationInRadians, pos.x, pos.y);
      type.render(g, pos.x, pos.y, null, config);
      g.rotate(-rotation.rotationInRadians, pos.x, pos.y);
    }
  }
  
  @Override
  public void reset() {
    rotation = Rotation.EAST;
  }
  
  @Override
  public String getName()
  {
    return Table.get("tool_place_components");
  }
}
