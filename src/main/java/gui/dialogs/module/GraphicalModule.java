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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public class GraphicalModule
{
  private static final int boundWidth = 6;
  
  private ModuleDefinition definition;
  
  private GraphicalPinList inputPins;
  private GraphicalPinList outputPins;
  
  private int mouseX;
  private int mouseY;
  
  private boolean isMovingTop;
  private boolean isMovingBottom;
  private boolean isMovingLeft;
  private boolean isMovingRight;
  
  
  public GraphicalModule(ModuleDefinition definition)
  {
    this.definition = definition;
    
    inputPins  = new GraphicalPinList(definition.inputs, true);
    outputPins = new GraphicalPinList(definition.outputs, false);
  }
  
  
  public boolean hasHorizontalCollision(int x, int y)
  {
    return collidesWithLeftSide(x, y) || collidesWithRightSide(x, y);
  }

  public boolean hasVerticalCollision(int x, int y)
  {
    return collidesWithTop(x, y) || collidesWithBottom(x, y);
  }
  
  
  public void mousePressed(int x, int y)
  {
    isMovingLeft = collidesWithLeftSide(x, y);
    isMovingRight = collidesWithRightSide(x, y);
    isMovingTop = collidesWithTop(x, y);
    isMovingBottom = collidesWithBottom(x, y);
  }

  private boolean collidesWithLeftSide(int x, int y)
  {
    Rectangle2D leftBounds = new Rectangle2D.Double(
        -definition.width/2 - boundWidth/2,
        -definition.height/2 - boundWidth/2,
        boundWidth,
        definition.height + boundWidth);
    
    return leftBounds.contains(x, y);
  }

  private boolean collidesWithRightSide(int x, int y)
  {
    Rectangle2D rightBounds = new Rectangle2D.Double(
         definition.width/2 - boundWidth/2,
        -definition.height/2 - boundWidth/2,
        boundWidth,
        definition.height + boundWidth);
    
    return rightBounds.contains(x, y);
  }

  private boolean collidesWithTop(int x, int y)
  {
    Rectangle2D topBounds = new Rectangle2D.Double(
        -definition.width/2 - boundWidth/2,
        -definition.height/2 - boundWidth/2,
        definition.width + boundWidth,
        boundWidth);
    
    return topBounds.contains(x, y);
  }

  private boolean collidesWithBottom(int x, int y)
  {
    Rectangle2D bottomBounds = new Rectangle2D.Double(
        -definition.width/2 - boundWidth/2,
         definition.height/2 - boundWidth/2,
        definition.width + boundWidth,
        boundWidth);
    
    return bottomBounds.contains(x, y);
  }
  
  
  public void mouseReleased()
  {
    isMovingBottom = false;
    isMovingTop = false;
    isMovingLeft = false;
    isMovingRight = false;
    
    definition.alignSizeToGrid();
  }
  
  
  public void mouseMoved(int newX, int newY)
  {
    int dx = newX - mouseX;
    int dy = newY - mouseY;
    
    mouseX = newX;
    mouseY = newY;
    
    if (isMovingLeft || isMovingRight)
      moveHorizontally(dx);
    if (isMovingTop || isMovingBottom)
      moveVertically(dy);
  }

  private void moveHorizontally(int dx)
  {
    if (isMovingLeft) dx *= -1;
    
    definition.width += dx*2;
    if (definition.width < definition.minWidth)
    {
      definition.width = definition.minWidth;
    }
  }

  private void moveVertically(int dy)
  {
    if (isMovingTop) dy *= -1;
    
    definition.height += dy*2;
    if (definition.height < definition.minHeight)
    {
      definition.height = definition.minHeight;
    }
  }


  public void render(Graphics g)
  {
    int width = definition.width;
    int height = definition.height;
    definition.alignSizeToGrid();
    
    g.setColor(Color.BLACK);
    g.drawRect(
        -definition.width/2,
        -definition.height/2,
        definition.width,
        definition.height);
    
    inputPins.render(g, definition, -definition.width/2, -definition.height/2);
    outputPins.render(g, definition, definition.width/2, -definition.height/2);
    
    FontMetrics metrics = g.getFontMetrics();
    g.drawString(
        definition.name,
        -metrics.stringWidth(definition.name)/2,
        -metrics.getHeight()/2 + metrics.getAscent());
    
    definition.width = width;
    definition.height = height;
  }
}
