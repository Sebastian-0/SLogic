/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend.tools;

import graphicalcircuit.GraphicalCircuitEntity;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.GraphicalWire;
import gui.backend.ProgramBackend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import snet.internal.Message;
import network.hooks.CreateWireHook;
import circuit.CircuitEntity;
import configuration.Table;

public class PlaceWireTool extends AbstractTool
{
  private GraphicalCircuitEntity componentAtStart;
  private List<Point> nodes;
  
  
  public PlaceWireTool()
  {
    nodes = new ArrayList<Point>();
  }

  @Override
  public boolean mousePressed(ProgramBackend backend, MouseEvent event,
      int viewportX, int viewportY)
  {
    Point alignedMouse = new Point(event.getX() + viewportX, event.getY() + viewportY);
    alignToGrid(alignedMouse);
    
    if (isLeftMouseButton(event))
    {
      boolean isPlacingWire = (componentAtStart != null);
      if (!isPlacingWire)
      {
        createWireStart(backend, alignedMouse);
      }
      else
      {
        createNextNode(backend, alignedMouse);
      }
      return true;
    }
    else if (isRightMouseButton(event))
    {
      if (nodes.size() > 1)
      {
        tryCreateWireEnd(backend, componentAtStart, null);
        componentAtStart = null;
        return true;
      }
    }
    
    return false;
  }

  private void createWireStart(ProgramBackend backend, Point alignedMouse)
  {
    GraphicalCircuitEntity target = findTarget(backend, alignedMouse);
    if (target != null)
    {
      componentAtStart = target;
      nodes.add(alignedMouse);
    }
  }

  private void createNextNode(ProgramBackend backend, Point alignedMouse)
  {
    Point correctionNode = createCorrectionNode(alignedMouse);
    if (correctionNode != null)
    {
      nodes.add(correctionNode);
    }
    else
    {
      nodes.add(alignedMouse);
      
      GraphicalCircuitEntity targetComponent = findTarget(backend, alignedMouse);
      if (targetComponent != null)
      {
        if (tryCreateWireEnd(backend, componentAtStart, targetComponent))
          componentAtStart = null;
        else
          nodes.remove(alignedMouse);
      }
    }
  }
  
  private GraphicalCircuitEntity findTarget(ProgramBackend backend, Point position)
  {
    GraphicalWire wire = backend.getSession().getClient().getWorkspace().database.getGraphicalCircuit().getWireAt(position.x, position.y);
    if (wire != null)
      return wire;
    return backend.getSession().getClient().getWorkspace().database.getGraphicalCircuit().getComponentWithPinAt(position.x, position.y);
  }

  private boolean tryCreateWireEnd(ProgramBackend backend, GraphicalCircuitEntity componentAtStart,
      GraphicalCircuitEntity componentAtEnd)
  {
    if (createBackendWire(backend, componentAtStart, componentAtEnd))
    {
      nodes.clear();
      return true;
    }
    else
    {
      return false;
    }
  }
  
  private boolean createBackendWire(ProgramBackend backend, GraphicalCircuitEntity c1, GraphicalCircuitEntity c2)
  {
    boolean hasSameStartAndEnd = nodes.get(0).equals(nodes.get(nodes.size() - 1));
    if (!hasSameStartAndEnd && canConnectWire(c1, c2))
    {
      Message msg = new CreateWireHook(null).createMessage(getComponentOf(c1), getComponentOf(c2), nodes);
      backend.getSession().getClient().getNetwork().send(null, msg);
      return true;
    }
    
    return false;
  }
  
  private boolean canConnectWire(GraphicalCircuitEntity entity1, GraphicalCircuitEntity entity2)
  {
    if (!(entity1 instanceof GraphicalWire) || entity1 != entity2)
    {
      Point startPosition = nodes.get(0);
      Point endPosition = nodes.get(nodes.size()-1);
      
      if (!givesWireInput(entity1, startPosition) || !givesWireInput(entity2, endPosition))
        return true;
    }
    
    return false;
  }
  
  private boolean givesWireInput(GraphicalCircuitEntity entity, Point position)
  {
    if (entity instanceof GraphicalComponent)
      return !((GraphicalComponent) entity).getPinAt(position.x, position.y).isInput();
    if (entity instanceof GraphicalWire)
      return ((GraphicalWire) entity).hasInput();
    return false;
  }
  
  private CircuitEntity getComponentOf(GraphicalCircuitEntity entity)
  {
    if (entity instanceof GraphicalComponent)
    {
      return ((GraphicalComponent) entity).getComponent();
    }
    if (entity instanceof GraphicalWire)
    {
      return ((GraphicalWire) entity).getWire();
    }
    
    return null;
  }
  
  @Override
  public boolean keyPressed(ProgramBackend backend, KeyEvent event)
  {
    if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
    {
      removeLastNode();
      return true;
    }
    
    return false;
  }

  private void removeLastNode()
  {
    if (nodes.size() > 0)
    {
      nodes.remove(nodes.size()-1);
    }
    if (nodes.isEmpty())
      componentAtStart = null;
  }

  @Override
  public void paintMarker(ProgramBackend backend, Graphics2D g, int xPos, int yPos)
  {
    Point mousePosition = new Point(xPos, yPos);
    alignToGrid(mousePosition);

    if (!nodes.isEmpty())
    {
      g.setColor(Color.BLUE);
      paintPlacedWires(g);

      Stroke oldStroke = g.getStroke();
      g.setStroke(new BasicStroke(1, 0, 0, 1f, new float[]{ 4, 8 }, 0));
      
      paintWireSelection(g, mousePosition);
      
      g.setStroke(oldStroke);
    }
    
    paintCursor(g, mousePosition);
  }

  private void paintPlacedWires(Graphics2D g)
  {
    for (int i = 1; i < nodes.size(); i++)
    {
      Point previousNode = nodes.get(i-1);
      Point currentNode  = nodes.get(i);
      g.drawLine(previousNode.x, previousNode.y, currentNode.x, currentNode.y);
    }
  }

  private void paintWireSelection(Graphics2D g, Point mousePosition)
  {
    Point lastPlacedNode = nodes.get(nodes.size()-1);
    Point correctionNode = createCorrectionNode(mousePosition);
    if (correctionNode != null)
    {
      g.drawLine(lastPlacedNode.x, lastPlacedNode.y, correctionNode.x, correctionNode.y);
      g.drawLine(correctionNode.x, correctionNode.y, mousePosition.x, mousePosition.y);
    }
    else
    {
      g.drawLine(lastPlacedNode.x, lastPlacedNode.y, mousePosition.x, mousePosition.y);
    }
  }

  private void paintCursor(Graphics2D g, Point mousePosition)
  {
    g.setColor(Color.BLUE);
    g.drawRect(mousePosition.x - 1, mousePosition.y - 1, 2, 2);
  }
  

  private Point createCorrectionNode(Point alignedMouse)
  {
    Point result = null;
    int dx = nodes.get(nodes.size()-1).x - alignedMouse.x;
    int dy = nodes.get(nodes.size()-1).y - alignedMouse.y;
    if (dx != 0 && dy != 0)
    {
      if (Math.abs(dx) > Math.abs(dy))
      {
        result = new Point(alignedMouse.x, nodes.get(nodes.size()-1).y);
      }
      else
      {
        result = new Point(nodes.get(nodes.size()-1).x, alignedMouse.y);
      }
    }
    return result;
  }
  
  
  @Override
  public void reset() {
    componentAtStart = null;
    nodes.clear();
  }
  
  
  @Override
  public String getName()
  {
    return Table.get("tool_place_wires");
  }
}
