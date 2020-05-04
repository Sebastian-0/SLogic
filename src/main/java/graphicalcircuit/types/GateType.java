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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import util.Grid;
import util.TextureStorage;
import circuit.Component;
import configuration.Config;
import configuration.GateDesignType;

public abstract class GateType extends AbstractCircuitType
{
  private static final int GATE_SIDE = 50;
  
  private static Font textFont;
  
  private int numSlots;
  private PinLayout layout;
  
  
  static
  {
    textFont = new JLabel().getFont().deriveFont(Font.PLAIN, 12f);
  }
  
  
  public GateType(int numSlots)
  {
    this.numSlots = numSlots;

    Point[] inPos = calculateInPinPositions(numSlots);
    Point[] outPos = calculateOutPinPosition();
    
    layout = new PinLayout(inPos, outPos);
  }

  private Point[] calculateInPinPositions(int numSlots)
  {
    int gateWidth = getWidth(null) + PIN_LENGHT*2;
    int gateHeight = getHeight(null);
    
    Point[] inPos = new Point[numSlots];
    int gridStepsPerSlot = gateHeight / numSlots / Grid.GRID_WIDTH;
    int nextSlot = 0;
    int offset = gridStepsPerSlot;
    boolean hasOddNumberOfSlots = (numSlots % 2) != 0;
    if (hasOddNumberOfSlots)
    {
      inPos[0] = new Point(-gateWidth/2, 0);
      nextSlot = 1;
    }
    else
    {
      if (offset > 1)
        offset /= 2;
    }
    
    for (; nextSlot+1 < numSlots; nextSlot += 2)
    {
      inPos[nextSlot] = new Point(-gateWidth/2, -offset * Grid.GRID_WIDTH);
      inPos[nextSlot+1] = new Point(-gateWidth/2, offset * Grid.GRID_WIDTH);
      
      offset += gridStepsPerSlot;
    }
    
    return inPos;
  }

  private Point[] calculateOutPinPosition()
  {
    int gateWidth = getWidth(null) + PIN_LENGHT*2;
    return new Point[] { new Point(gateWidth/2, 0) };
  }
  
  @Override
  public CircuitType makeCopy() {
    return makeCopy(numSlots);
  }
  
  protected abstract CircuitType makeCopy(int numSlots);
  
  @Override
  public Component makeBackendComponent(int id) {
    return makeBackendComponent(id, numSlots);
  }
  
  protected abstract Component makeBackendComponent(int id, int numSlots);
  
  
  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    g.setColor(Color.WHITE);
    g.fillRect(x - GATE_SIDE/2, y - GATE_SIDE/2, GATE_SIDE, GATE_SIDE);
    
    GateDesignType design = getGateDesign();
    if (design == GateDesignType.Text)
      renderTextDesign(g, x, y);
    if (design == GateDesignType.American)
      renderAmericanDesign(g, x, y);
    if (design == GateDesignType.IEC)
      renderIECDesign(g, x, y);

    g.setColor(Color.BLACK);
    g.drawRect(x - GATE_SIDE/2, y - GATE_SIDE/2, GATE_SIDE, GATE_SIDE);
  }

  private void renderTextDesign(Graphics2D g, int x, int y)
  {
    g.setColor(Color.BLACK);
    g.setFont(textFont);
    FontMetrics metrics = g.getFontMetrics();
    g.drawString(
        getName(),
        x - metrics.stringWidth(getName())/2,
        y - metrics.getHeight()/2 + metrics.getAscent());
  }

  private void renderAmericanDesign(Graphics2D g, int x, int y)
  {
    BufferedImage texture = TextureStorage.instance().getTexture("us_" + getName().toLowerCase());
    g.drawImage(texture, x - texture.getWidth()/2, y - texture.getHeight()/2, null);
  }

  private void renderIECDesign(Graphics2D g, int x, int y)
  {
    BufferedImage texture = TextureStorage.instance().getTexture("iec_" + getName().toLowerCase());
    g.drawImage(texture, x - texture.getWidth()/2, y - texture.getHeight()/2, null);
  }
  
  
  private GateDesignType getGateDesign()
  {
    String gateTypeName = Config.get(Config.GATE_DESIGN_TYPE);
    return GateDesignType.getTypeByName(gateTypeName);
  }
  
  
  @Override
  public String toString()
  {
    return getName() + numSlots;
  }

  protected abstract String getName();

  
  @Override
  public Category getCategory()
  {
    return Category.GATE;
  }
  
  @Override
  public PinLayout getLayout()
  {
    return layout;
  }
  
  @Override
  public int getWidth(GraphicalComponent component)
  {
    return GATE_SIDE;
  }
  
  @Override
  public int getHeight(GraphicalComponent component)
  {
    return GATE_SIDE;
  }
}
