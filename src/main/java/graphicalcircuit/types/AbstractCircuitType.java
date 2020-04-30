/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.types;

import graphicalcircuit.CircuitType;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.Rotation;
import graphicalcircuit.SimulationInputListener;
import graphicalcircuit.PinLayout.Pin;
import graphicalcircuit.config.ComponentConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import circuit.Component;
import configuration.Config;

public abstract class AbstractCircuitType implements CircuitType
{
  @Override
  public boolean intersects(Rectangle rect, Rotation rotation, GraphicalComponent component)
  {
    Rectangle bounds = makeBounds(rotation, component);
    return bounds.intersects(rect);
  }
  
  @Override
  public boolean contains(int x, int y, Rotation rotation, GraphicalComponent component)
  {
    Rectangle bounds = makeBounds(rotation, component);
    return bounds.contains(x, y);
  }
  
  private Rectangle makeBounds(Rotation rotation, GraphicalComponent component)
  {
    if (rotation == Rotation.EAST || rotation == Rotation.WEST)
    {
      return new Rectangle(-getWidth(component)/2, -getHeight(component)/2, getWidth(component), getHeight(component));
    }
    return new Rectangle(-getHeight(component)/2, -getWidth(component)/2, getHeight(component), getWidth(component));
  }

  
  @Override
  public void render(Graphics2D g, int x, int y, GraphicalComponent component)
  {
    render(g, x, y, component, getCircuitConfig(component));
  }
  @Override
  public void render(Graphics2D g, int x, int y, GraphicalComponent component, ComponentConfig config)
  {
    renderType(g, x, y, getCircuitComponent(component), config);
    renderPins(g, x, y, component);
  }

  private Component getCircuitComponent(GraphicalComponent component)
  {
    if (component != null)
      return component.getComponent();
    return null;
  }

  private ComponentConfig getCircuitConfig(GraphicalComponent component)
  {
    if (component != null)
      return component.getConfig();
    return null;
  }

  private void renderPins(Graphics2D g, int x, int y, GraphicalComponent component)
  {
    g.setColor(Color.BLACK);
    for (Pin pin : getLayout().getInputPins())
    {
      renderPin(g, x, y, pin, component == null || component.getInput(pin.getIndex()) != null, component);
    }
    for (Pin pin : getLayout().getOutputPins())
    {
      renderPin(g, x, y, pin, component == null || component.getOutput(pin.getIndex()) != null, component);
    }
  }
  
  private void renderPin(Graphics2D g, int x, int y, Pin pin, boolean isPinConnected, GraphicalComponent component)
  {
    final int pinLenght = AbstractCircuitType.PIN_LENGHT;
    
    float widthToHeightRatio = getWidth(component) / getHeight(component);
    
    Point startPosition = pin.getPosition();
    Point endPosition = new Point();
    startPosition.translate(x, y);
    if (pin.isAtComponentBottom(widthToHeightRatio))
      endPosition.setLocation(startPosition.x, startPosition.y-pinLenght);
    else if (pin.isAtComponentTop(widthToHeightRatio))
      endPosition.setLocation(startPosition.x, startPosition.y+pinLenght);
    else if (pin.isAtComponentLeftSide(widthToHeightRatio))
      endPosition.setLocation(startPosition.x+pinLenght, startPosition.y);
    else if (pin.isAtComponentRightSide(widthToHeightRatio))
      endPosition.setLocation(startPosition.x-pinLenght, startPosition.y);
    
    g.drawLine(startPosition.x, startPosition.y, endPosition.x, endPosition.y);
    
    if (!isPinConnected && shouldDrawMarkerAtUnconnectedPins())
    {
      g.setColor(Color.RED.darker());
      g.drawLine(startPosition.x - 2, startPosition.y - 2, startPosition.x + 2, startPosition.y + 2);
      g.drawLine(startPosition.x + 2, startPosition.y - 2, startPosition.x - 2, startPosition.y + 2);
      g.setColor(Color.BLACK);
    }
  }
  
  private boolean shouldDrawMarkerAtUnconnectedPins()
  {
    return Config.get(Config.MARK_UNUSED_PINS).equals("true");
  }
  
  protected abstract void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config);
  

  @Override
  public ComponentConfig generateDefaultConfig(Component component)
  {
    return null;
  }
  
  @Override
  public SimulationInputListener createSimulationInputListener(GraphicalComponent component) {
    return null;
  }
  
  @Override
  public boolean canBePartOfModule()
  {
    return true;
  }
  
  @Override
  public String getIdentifierString()
  {
    return getClass().getName() + getLayout().toString();
  }
  
  @Override
  public boolean equals(Object obj) {
    return getClass().getName().equals(obj.getClass().getName());
  }

  @Override
  public int hashCode()
  {
    return getClass().getName().hashCode();
  }
}
