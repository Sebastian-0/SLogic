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
import graphicalcircuit.PinLayout;
import graphicalcircuit.config.ClockConfig;
import graphicalcircuit.config.ComponentConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import circuit.CircuitEntity.State;
import circuit.Component;
import circuit.ConstantState;
import configuration.Table;

public class ClockType extends AbstractCircuitType
{
  private static final int SIDE = 30;
  
  private PinLayout layout;
  
  
  public ClockType()
  {
    layout = new PinLayout(new Point[0], new Point[] { new Point(SIDE/2 + PIN_LENGHT, 0) });
  }
  
  @Override
  public ComponentConfig generateDefaultConfig(Component component)
  {
    return new ClockConfig();
  }
  
  @Override
  public CircuitType makeCopy() {
    return new ClockType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new ConstantState(id, State.OFF);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    g.setColor(Color.WHITE);
    g.fillRect(x - SIDE/2, y - SIDE/2, SIDE, SIDE);
    g.setColor(Color.BLACK);
    g.drawRect(x - SIDE/2, y - SIDE/2, SIDE, SIDE);
    
    drawSymbol(g, x, y);
  }

  private void drawSymbol(Graphics2D g, int x, int y)
  {
    int lineLenght = 10;
    g.drawLine(x - lineLenght/2, y - lineLenght/2, x + lineLenght/2, y - lineLenght/2);
    g.drawLine(x - lineLenght/2, y - lineLenght/2, x - lineLenght/2, y + lineLenght/2);
    g.drawLine(x + lineLenght/2, y - lineLenght/2, x + lineLenght/2, y + lineLenght/2);
    g.drawLine(x - lineLenght/2, y + lineLenght/2, x - (lineLenght*2)/2, y + lineLenght/2);
    g.drawLine(x + lineLenght/2, y + lineLenght/2, x + (lineLenght*2)/2, y + lineLenght/2);
  }
  

  @Override
  public Category getCategory()
  {
    return Category.IO;
  }

  @Override
  public PinLayout getLayout()
  {
    return layout;
  }
  
  @Override
  public boolean canBePartOfModule() {
    return false;
  }
  
  @Override
  public int getWidth(GraphicalComponent component)
  {
    return SIDE;
  }
  
  @Override
  public int getHeight(GraphicalComponent component)
  {
    return SIDE;
  }

  @Override
  public String toString()
  {
    return Table.get("circuit_clock");
  }
}
