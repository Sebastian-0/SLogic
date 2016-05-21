/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.module;

import graphicalcircuit.ModuleDefinition;
import graphicalcircuit.ModuleDefinition.Connection;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GraphicalPinList
{
  private List<GraphicalPin> pins;
  
  
  public GraphicalPinList(List<Connection> definitionPins, boolean isInverted)
  {
    pins = new ArrayList<GraphicalPin>();
    
    for (Connection connection : definitionPins)
      pins.add(new GraphicalPin(connection, isInverted));
  }
  
  
  public void render(Graphics g, ModuleDefinition def, int x, int y)
  {
    float spacePerPin = def.height/(pins.size() + 0f);
    
    float yOffset = spacePerPin/2;
    for (GraphicalPin pin : pins)
    {
      pin.render(g, x, y + (int)yOffset);
      
      yOffset += spacePerPin;
    }
  }
}
