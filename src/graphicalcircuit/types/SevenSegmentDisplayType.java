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
import graphicalcircuit.config.ComponentConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import util.Grid;
import circuit.CircuitEntity.State;
import circuit.Component;
import circuit.SevenSegmentDisplay;
import configuration.Table;

public class SevenSegmentDisplayType extends AbstractCircuitType
{
  private static final int WIDTH  = 40;
  private static final int HEIGHT = 60;
  
  private PinLayout layout;
  
  
  public SevenSegmentDisplayType()
  {
    final int gridSize = Grid.GRID_WIDTH;
    int width = WIDTH + PIN_LENGHT*2;
    layout = new PinLayout(
        new Point[] {
            new Point(-width/2, -gridSize*4), 
            new Point(-width/2, -gridSize*3),
            new Point(-width/2, -gridSize*2), 
            new Point(-width/2, -gridSize),
            new Point(-width/2,  gridSize), 
            new Point(-width/2,  gridSize*2),  
            new Point(-width/2,  gridSize*3), 
            new Point(-width/2,  gridSize*4) },
        new Point[0]);
  }
  
  @Override
  public CircuitType makeCopy() {
    return new SevenSegmentDisplayType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new SevenSegmentDisplay(id);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    g.setColor(Color.BLACK);
    g.fillRect(x - WIDTH/2, y - HEIGHT/2, WIDTH, HEIGHT);
    
    if (isValidComponent(component))
    {
      final int padding = 8;
      
      int width  = WIDTH - padding * 2;
      int height = HEIGHT - padding * 2;
      
      int lw = 4;
      
      SevenSegmentDisplay display = (SevenSegmentDisplay)component;
      drawSegment(g, display.getState(0), x-width/2+lw, y-height/2   , width-lw*2, lw);
      drawSegment(g, display.getState(1), x+width/2-lw, y-height/2+lw, lw        , height/2-lw*3/2);
      drawSegment(g, display.getState(2), x+width/2-lw, y+lw/2       , lw        , height/2-lw*3/2);
      drawSegment(g, display.getState(3), x-width/2+lw, y+height/2-lw, width-lw*2, lw);
      drawSegment(g, display.getState(4), x-width/2   , y+lw/2       , lw        , height/2-lw*3/2);
      drawSegment(g, display.getState(5), x-width/2   , y-height/2+lw, lw        , height/2-lw*3/2);
      drawSegment(g, display.getState(6), x-width/2+lw, y-lw/2       , width-lw*2, lw);
      drawSegment(g, display.getState(7), x+width/2+2 , y+height/2-lw, lw        , lw);
    }
  }
  
  private void drawSegment(Graphics2D g, State state, int x, int y, int width, int height)
  {
    if (state == State.ON)
      g.setColor(Color.RED);
    else
      g.setColor(Color.DARK_GRAY);
    g.fillRect(x, y, width, height);
  }
  
  private boolean isValidComponent(Component c)
  {
    if (c instanceof SevenSegmentDisplay)
      return true;
    return false;
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
    return WIDTH;
  }
  
  @Override
  public int getHeight(GraphicalComponent component)
  {
    return HEIGHT;
  }

  @Override
  public String toString()
  {
    return Table.get("circuit_7_segment");
  }
}
