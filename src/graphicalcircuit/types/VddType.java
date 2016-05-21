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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import util.TextureStorage;
import circuit.Component;
import circuit.Vdd;
import configuration.Table;

public class VddType extends AbstractCircuitType
{
  private static final int SIDE = 30;
  
  private PinLayout layout;
  
  
  public VddType()
  {
    layout = new PinLayout(new Point[0], new Point[] { new Point(SIDE/2 + PIN_LENGHT, 0) });
  }
  
  @Override
  public CircuitType makeCopy() {
    return new VddType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new Vdd(id);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    BufferedImage texture = TextureStorage.instance().getTexture("vdd");
    g.drawImage(texture, x - texture.getWidth()/2, y - texture.getHeight()/2, null);
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
    return Table.get("circuit_vdd");
  }
}
