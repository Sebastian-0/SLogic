/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Grid;

public class ModuleDefinition implements Serializable
{
  private static final long serialVersionUID = 8405942449342896493L;

  public String name;
  public String description;
  
  public int width;
  public int height;
  
  public int minWidth;
  public int minHeight;
  
  public List<Connection> inputs;
  public List<Connection> outputs;
  
  public ModuleDefinition()
  {
    inputs = new ArrayList<Connection>();
    outputs = new ArrayList<Connection>();
  }
  
  
  public void calculateMinimimSize(int gridSize)
  {
    minWidth = 30;
    minHeight = 10;
    
    int amountOfVerticalPins = Math.max(inputs.size(), outputs.size());
    
    minHeight = Math.max(minHeight, (amountOfVerticalPins + 1) * gridSize * 2);
    
    width = Math.max(minWidth, width);
    height = Math.max(minHeight, height);
  }
  
  
  public void alignSizeToGrid()
  {
    Point aligned = Grid.alignToGrid(new Point(width/2, height/2));
    width = aligned.x*2;
    height = aligned.y*2;
  }
  
  
  public static class Connection implements Serializable
  {
    private static final long serialVersionUID = 4247960270858050893L;
    
    public static final byte CLOCK = 0;
    public static final byte INPUT = 1;
    public static final byte OUTPUT = 2;
    
    public String name;
    public byte type;
    
    public Connection(byte type, String name)
    {
      this.type = type;
      this.name = name;
    }
  }
}
