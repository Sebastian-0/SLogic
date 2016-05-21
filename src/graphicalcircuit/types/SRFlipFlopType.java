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
import graphicalcircuit.PinLayout.Pin;
import graphicalcircuit.config.ComponentConfig;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import circuit.Component;
import circuit.SRFlipFlop;
import configuration.Table;

public class SRFlipFlopType extends AbstractCircuitType
{
  private static final int WIDTH = 40;
  private static final int HEIGHT = 60;
  
  private PinLayout layout;
  
  
  public SRFlipFlopType()
  {
    int WIDTH = SRFlipFlopType.WIDTH + PIN_LENGHT*2;
    
    layout = new PinLayout(
        new Point[] { 
            new Point(-WIDTH/2, -HEIGHT/4),
            new Point(-WIDTH/2,  HEIGHT/4) },
        new Point[] { 
            new Point(WIDTH/2, -HEIGHT/4), 
            new Point(WIDTH/2,  HEIGHT/4) });
  }
  
  @Override
  public CircuitType makeCopy() {
    return new SRFlipFlopType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new SRFlipFlop(id);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    g.setColor(Color.WHITE);
    g.fillRect(x - WIDTH/2, y - HEIGHT/2, WIDTH, HEIGHT);
    g.setColor(Color.BLACK);
    g.drawRect(x - WIDTH/2, y - HEIGHT/2, WIDTH, HEIGHT);

    g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
    FontMetrics m = g.getFontMetrics();
    
    Pin[] inputs = layout.getInputPins();
    g.drawString(
        "S", 
        x + AbstractCircuitType.PIN_LENGHT + 2 + inputs[0].getPosition().x, 
        y + inputs[0].getPosition().y + m.getAscent()/2);
    g.drawString(
        "R", 
        x + AbstractCircuitType.PIN_LENGHT + 2 + inputs[1].getPosition().x, 
        y + inputs[1].getPosition().y + m.getAscent()/2);

    Pin[] outputs = layout.getOutputPins();
    g.drawOval(
        x - AbstractCircuitType.PIN_LENGHT - 7 + outputs[1].getPosition().x, 
        y + outputs[1].getPosition().y - 2, 
        5,
        5);
  }

  @Override
  public Category getCategory()
  {
    return Category.REGISTER;
  }

  @Override
  public PinLayout getLayout()
  {
    return layout;
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
    return "SR " + Table.get("circuit_flip_flop");
  }
}
