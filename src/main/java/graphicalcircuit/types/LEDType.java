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
import graphicalcircuit.config.LEDConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import circuit.CircuitEntity.State;
import circuit.Component;
import circuit.LED;
import configuration.Table;

public class LEDType extends AbstractCircuitType
{
  private static final int SIDE = 30;
  
  private PinLayout layout;
  
  
  public LEDType()
  {
    layout = new PinLayout(new Point[] { new Point(-SIDE/2 - PIN_LENGHT, 0) }, new Point[0]);
  }
  
  @Override
  public ComponentConfig generateDefaultConfig(Component component)
  {
    return new LEDConfig();
  }
  
  @Override
  public CircuitType makeCopy() {
    return new LEDType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new LED(id);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    LEDConfig ledConfig = (LEDConfig)config;
    
    g.setColor(Color.BLACK);
    g.fillRect(x - SIDE/2, y - SIDE/2, SIDE, SIDE);
    
    if (isValidComponent(component) && ((LED)component).getState() == State.ON)
      g.setColor(ledConfig.onColor);
    else
      g.setColor(ledConfig.offColor);
    g.fillRect(x - SIDE/4, y - SIDE/4, SIDE/2, SIDE/2);
  }
  
  private boolean isValidComponent(Component c)
  {
    if (c instanceof LED)
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
    return Table.get("circuit_led");
  }
}
