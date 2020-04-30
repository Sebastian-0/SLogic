/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend.tools;

import gui.backend.ProgramBackend;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public abstract class AbstractSelectionTool extends AbstractTool
{
  private Point selectionStart;
  private Point selectionEnd;
  
  private boolean hasSelection;
  
  
  public AbstractSelectionTool()
  {
    selectionStart = new Point();
    selectionEnd = new Point();
  }
  
  
  @Override
  public boolean mousePressed(ProgramBackend backend, MouseEvent event,
      int viewportX, int viewportY)
  {
    Point alignedMouse = new Point(event.getX() + viewportX, event.getY() + viewportY);
    alignToGrid(alignedMouse);
    
    if (isLeftMouseButton(event))
    {
      selectionStart.setLocation(alignedMouse);
      selectionEnd.setLocation(selectionStart);
      hasSelection = true;
      return true;
    }
    
    return false;
  }
  
  @Override
  public void mouseMoved(ProgramBackend backend, MouseEvent event,
      boolean isDragged, int viewportX, int viewportY)
  {
    Point alignedMouse = new Point(event.getX() + viewportX, event.getY() + viewportY);
    alignToGrid(alignedMouse);
    
    if (hasSelection)
      selectionEnd.setLocation(alignedMouse);
  }
  
  @Override
  public void mouseReleased(ProgramBackend backend, MouseEvent event,
      int viewportX, int viewportY)
  {
    Point alignedMouse = new Point(event.getX() + viewportX, event.getY() + viewportY);
    alignToGrid(alignedMouse);
    
    if (hasSelection && isLeftMouseButton(event))
    {
      sortSelectionPoints();
      performActionOnSelection(backend, selectionStart, selectionEnd);
      hasSelection = false;
      
      backend.refreshRenderingSurface(); // Remove the selection box
    }
  }
  
  private void sortSelectionPoints()
  {
    int xMin = Math.min(selectionStart.x, selectionEnd.x);
    int xMax = Math.max(selectionStart.x, selectionEnd.x);
    int yMin = Math.min(selectionStart.y, selectionEnd.y);
    int yMax = Math.max(selectionStart.y, selectionEnd.y);
    
    selectionStart.setLocation(xMin, yMin);
    selectionEnd.setLocation(xMax, yMax);
  }
  
  protected abstract void performActionOnSelection(ProgramBackend backend, Point start, Point end);
  

  @Override
  public void paintMarker(ProgramBackend backend, Graphics2D g, int xPos,
      int yPos)
  {
    int x = Math.min(selectionStart.x, selectionEnd.x);
    int y = Math.min(selectionStart.y, selectionEnd.y);
    int width = Math.abs(selectionStart.x - selectionEnd.x);
    int height = Math.abs(selectionStart.y - selectionEnd.y);
    
    if (hasSelection)
    {
      g.setColor(getSelectionColor());
      paintBackground(g, x, y, width, height);
      g.setColor(getSelectionColor().darker());
      paintBorder(g, x, y, width, height);
    }
  }

  private void paintBackground(Graphics2D g, int x, int y, int width, int height)
  {
    Composite oldComposite = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    g.fillRect(x, y, width, height);
    g.setComposite(oldComposite);
  }

  private void paintBorder(Graphics2D g, int x, int y, int width, int height)
  {
    g.drawRect(x, y, width, height);
  }
  
  protected abstract Color getSelectionColor();
  
  
  @Override
  public void reset() {
    hasSelection = false;
  }
}
