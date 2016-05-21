/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import graphicalcircuit.ModuleDefinition.Connection;
import graphicalcircuit.PinLayout.Pin;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import util.Grid;
import circuit.Component;
import circuit.IntegratedModule;
import circuit.IntegratedModuleCircuit;

public class Module implements Serializable
{
  private static final long serialVersionUID = -1617193867460119837L;
  
  private IntegratedModuleCircuit circuit;
  private ModuleDefinition definition;
  
  private transient PinLayout pinLayout;
  
  
  public Module(ModuleDefinition definition, IntegratedModuleCircuit circuit)
  {
    this.circuit = circuit;
    this.definition = definition;
    
    constructLayout();
  }
  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    constructLayout();
  }
  
  private void constructLayout()
  {
    Point[] inputs = calculatePinPositions(true, definition.inputs.size());
    Point[] outputs = calculatePinPositions(false, definition.outputs.size());
    pinLayout = new PinLayout(inputs, outputs);
  }

  private Point[] calculatePinPositions(boolean isLeftSide, int numPins) // TODO Module; Copy-paste på GateTypes metod!!!
  {
    int gateWidth = definition.width + CircuitType.PIN_LENGHT*2;
    int gateHeight = definition.height;
    
    int xPosition = gateWidth/2;
    if (isLeftSide)
      xPosition *= -1;
    
    Point[] pinPositions = new Point[numPins];
    int gridStepsPerSlot = gateHeight / numPins / Grid.GRID_WIDTH;
    int nextSlot = 0;
    int offset = gridStepsPerSlot;
    boolean hasOddNumberOfSlots = (numPins % 2) != 0;
    if (hasOddNumberOfSlots)
    {
      pinPositions[0] = new Point(xPosition, 0);
      nextSlot = 1;
    }
    else
    {
      if (offset > 1)
        offset /= 2;
    }
    
    for (; nextSlot < numPins; nextSlot += 2)
    {
      pinPositions[nextSlot] = new Point(xPosition, -offset * Grid.GRID_WIDTH);
      pinPositions[nextSlot+1] = new Point(xPosition, offset * Grid.GRID_WIDTH);
      
      offset += gridStepsPerSlot;
    }
    
    sortPinsInCorrectOrder(pinPositions);
    
    return pinPositions;
  }

  private void sortPinsInCorrectOrder(Point[] pinPositions)
  {
    Arrays.sort(pinPositions, new Comparator<Point>() {
      @Override
      public int compare(Point o1, Point o2)
      {
        return o1.y - o2.y;
      }
    });
  }
  
  
  public void render(Graphics2D g, int x, int y)
  {
    int drawWidth = definition.width;
    int drawHeight = definition.height;
    int drawX = x - drawWidth/2;
    int drawY = y - drawHeight/2;
    
    g.setColor(Color.WHITE);
    g.fillRect(drawX, drawY, drawWidth, drawHeight);
    g.setColor(Color.BLACK);
    g.drawRect(drawX, drawY, drawWidth, drawHeight);
    
    drawName(g, x, y);
    drawPinNames(g, x, y);
  }

  private void drawName(Graphics2D g, int x, int y)
  {
    g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
    FontMetrics metrics = g.getFontMetrics();
    g.drawString(
        definition.name,
        x-metrics.stringWidth(definition.name)/2,
        y-metrics.getHeight()/2 + metrics.getAscent());
  }
  
  private void drawPinNames(Graphics2D g, int x, int y)
  {
    g.setFont(g.getFont().deriveFont(Font.PLAIN, 9f));
    FontMetrics metrics = g.getFontMetrics();
    drawInputPinNames(g, x, y, metrics);
    drawOutputPinNames(g, x, y, metrics);
  }

  private void drawInputPinNames(Graphics2D g, int x, int y, FontMetrics metrics)
  {
    for (int i = 0; i < pinLayout.getInputPins().length; i++)
    {
      Pin pin = pinLayout.getInputPins()[i];
      Point position = pin.getPosition();
      
      position.translate(x, y);
      if (definition.inputs.get(i).type == Connection.CLOCK)
      {
        renderTriangle(g, position.x + CircuitType.PIN_LENGHT, position.y);
        position.x += 5;
      }
      
      int drawX = position.x + CircuitType.PIN_LENGHT + 3;
      int drawY = position.y - metrics.getHeight()/2 + metrics.getAscent();
      g.drawString(
          definition.inputs.get(i).name,
          drawX,
          drawY);
    }
  }

  private void renderTriangle(Graphics g, int x, int y)
  {
    g.drawLine(x, y - 5, x+5, y);
    g.drawLine(x, y + 5, x+5, y);
  }

  private void drawOutputPinNames(Graphics2D g, int x, int y,
      FontMetrics metrics)
  {
    for (int i = 0; i < pinLayout.getOutputPins().length; i++)
    {
      Pin pin = pinLayout.getOutputPins()[i];
      Point position = pin.getPosition();
      position.translate(x, y);
      int drawX = position.x - CircuitType.PIN_LENGHT - metrics.stringWidth(definition.outputs.get(i).name) - 3;
      int drawY = position.y - metrics.getHeight()/2 + metrics.getAscent();
      g.drawString(
          definition.outputs.get(i).name,
          drawX,
          drawY);
    }
  }

  
  public Component constructComponent(int id)
  {
    IntegratedModule module = new IntegratedModule(id, circuit.makeCopy());
    return module;
  }

  
  public String getName()
  {
    return definition.name;
  }
  
  public String getDescription()
  {
    return definition.description;
  }
  
  public int getWidth()
  {
    return definition.width;
  }
  
  public int getHeight()
  {
    return definition.height;
  }
  
  public PinLayout getLayout()
  {
    return pinLayout;
  }
}
