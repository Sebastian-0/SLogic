/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import graphicalcircuit.config.ComponentConfig;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import util.Grid;
import circuit.Component;

public interface CircuitType {
  public static final int PIN_LENGHT = Grid.GRID_WIDTH;
  
  public enum Category {
    GATE, REGISTER, MODULE, IO, OTHER;
  }
  
  
  public boolean intersects(Rectangle rect, Rotation rotation, GraphicalComponent component);
  public boolean contains(int x, int y, Rotation rotation, GraphicalComponent component);
  
  public void render(Graphics2D g, int x, int y, GraphicalComponent component);
  public void render(Graphics2D g, int x, int y, GraphicalComponent component, ComponentConfig config);

  public ComponentConfig generateDefaultConfig(Component component);
  public SimulationInputListener createSimulationInputListener(GraphicalComponent component);
  public CircuitType makeCopy();
  public Component makeBackendComponent(int id);
  
  public boolean canBePartOfModule();
  
  public Category getCategory(); 
  public PinLayout getLayout();
  public int getWidth(GraphicalComponent component);
  public int getHeight(GraphicalComponent component);
  
  @Override
  public String toString();
  public String getIdentifierString();
}
