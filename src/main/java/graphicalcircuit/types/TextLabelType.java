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
import graphicalcircuit.config.TextLabelConfig;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import circuit.Component;
import circuit.DummyComponent;
import configuration.Table;

public class TextLabelType extends AbstractCircuitType
{
  private static final Color BACKGROUND_COLOR = new Color(216, 237, 255);
  private static final int MARGIN = 5;

  private PinLayout layout;
  
  
  public TextLabelType()
  {
    layout = new PinLayout(new Point[0], new Point[0]);
  }
  
  @Override
  public ComponentConfig generateDefaultConfig(Component component)
  {
    TextLabelConfig config = new TextLabelConfig(Table.get("circuit_text_label_default_text"));
    config.width = 70;
    config.height = 20;
    return config;
  }
  
  @Override
  public CircuitType makeCopy() {
    return new TextLabelType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new DummyComponent(id); // TODO TextLabel; Finns det ett sätt att undvika att skapa skräp-komponenter? Eller ska de filtreras bort när moduler skapas?
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    TextLabelConfig labelConfig = (TextLabelConfig)config;
    
    g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
    adaptSize(g, labelConfig);
    
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(x - labelConfig.width/2, y - labelConfig.height/2, labelConfig.width, labelConfig.height);
    g.setColor(Color.BLACK);
    g.drawRect(x - labelConfig.width/2, y - labelConfig.height/2, labelConfig.width, labelConfig.height);

    FontMetrics metrics = g.getFontMetrics();
    g.drawString(
        labelConfig.name, 
        x - labelConfig.width/2 + MARGIN * 2, 
        y - metrics.getHeight() / 2 + metrics.getAscent());
  }
  
  private void adaptSize(Graphics2D g, TextLabelConfig config) {
    String text = config.name;
    FontMetrics metrics = g.getFontMetrics();
    
    config.width = metrics.stringWidth(text) + MARGIN * 4;
    config.height = metrics.getHeight() + MARGIN * 2;
  }

  @Override
  public Category getCategory()
  {
    return Category.OTHER;
  }

  @Override
  public PinLayout getLayout()
  {
    return layout;
  }
  
  @Override
  public int getWidth(GraphicalComponent component)
  {
    return ((TextLabelConfig)component.getConfig()).width;
  }
  
  @Override
  public int getHeight(GraphicalComponent component)
  {
    return ((TextLabelConfig)component.getConfig()).height;
  }

  @Override
  public String toString()
  {
    return Table.get("circuit_text_label");
  }
}
