/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.module;

import graphicalcircuit.CircuitType;
import graphicalcircuit.ModuleDefinition.Connection;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class GraphicalPin
{
  public Connection connection;
  
  private boolean isInvertedHorizontally;
  
  public GraphicalPin(Connection connection, boolean isInverted)
  {
    this.connection = connection;
    
    isInvertedHorizontally = isInverted;
  }
  
  
  public void render(Graphics g, int x, int y)
  {
    final int pinLenght = CircuitType.PIN_LENGHT;
    
    renderLine(g, x, y, pinLenght);
    if (connection.type == Connection.CLOCK)
    {
      renderName(g, x + 5, y);
      renderTriangle(g, x, y);
    }
    else
    {
      renderName(g, x, y);
    }
  }

  private void renderLine(Graphics g, int x, int y, final int pinLenght)
  {
    g.setColor(Color.BLACK);
    if (isInvertedHorizontally)
      g.drawLine(x, y, x - pinLenght, y);
    else
      g.drawLine(x, y, x + pinLenght, y);
  }

  private void renderName(Graphics g, int x, int y)
  {
    Font oldFont = g.getFont();
    g.setFont(oldFont.deriveFont(9f));
    FontMetrics metrics = g.getFontMetrics();
    
    if (isInvertedHorizontally)
      g.drawString(
          connection.name,
          x + 2,
          y - metrics.getHeight()/2 + metrics.getAscent());
    else
      g.drawString(
          connection.name,
          x - metrics.stringWidth(connection.name) - 2,
          y - metrics.getHeight()/3 + metrics.getAscent());
    
    g.setFont(oldFont);
  }

  private void renderTriangle(Graphics g, int x, int y)
  {
    int clockPinY = y;
    g.drawLine(x, clockPinY - 5, x+5, clockPinY);
    g.drawLine(x, clockPinY + 5, x+5, clockPinY);
  }
}
