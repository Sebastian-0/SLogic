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
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class AbstractComponentMovementTool extends AbstractSelectionTool
{
  private Point start; 
  private Point end; 
  
  private BufferedImage copyImage;
  private boolean isMovingComponents;
  
  
  @Override
  public boolean mousePressed(ProgramBackend backend, MouseEvent event,
      int viewportX, int viewportY)
  {
    if (!isMovingComponents)
      return super.mousePressed(backend, event, viewportX, viewportY);
    
    if (isRightMouseButton(event))
    {
      isMovingComponents = false;
      return true;
    }
    else if (isLeftMouseButton(event))
    {
      Point destination = new Point(event.getX() + viewportX, event.getY() + viewportY);
      alignToGrid(destination);

      isMovingComponents = selectionPlaced(backend, start, end, destination);
      
      return true;
    }
    
    return false;
  }
  
  protected abstract boolean selectionPlaced(ProgramBackend backend, Point selectionStart, Point selectionEnd, Point destination);
  

  @Override
  protected void performActionOnSelection(ProgramBackend backend, Point start,
      Point end)
  {
    // TODO AbstractComponentMovementTool; Lägg till en metod i GraphicalCircuit som returnerar alla entities inom ett område,
    //  används sedan det här för att avgöra om det finns några komponenter inom markeringen. Om inte så händer inget.
    int selectionWidth = end.x - start.x;
    int selectionHeight = end.y - start.y;
    if (selectionWidth > 0 && selectionHeight > 0)
    {
      generateCopyImage(backend, start, selectionWidth, selectionHeight);
      isMovingComponents = true;
      
      this.start = start;
      this.end = end;
      
      selectionMade(backend, start, end);
    }
  }

  private void generateCopyImage(ProgramBackend backend, Point start, int selectionWidth,
      int selectionHeight)
  {
    copyImage = new BufferedImage(selectionWidth, selectionHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = copyImage.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics.translate(-start.x, -start.y);
    backend.getSession().getClient().getWorkspace().database.getGraphicalCircuit().render(graphics);
    graphics.translate(start.x, start.y);
  }
  
  protected abstract void selectionMade(ProgramBackend backend, Point start, Point end);
  
  
  @Override
  public void paintMarker(ProgramBackend backend, Graphics2D g, int xPos,
      int yPos)
  {
    if (!isMovingComponents)
      super.paintMarker(backend, g, xPos, yPos);
    else
    {
      Point alignedMouse = new Point(xPos, yPos);
      alignToGrid(alignedMouse);
      
      paintCopyImage(g, alignedMouse);
      
      g.setColor(getSelectionColor().darker());
      paintCopyOutline(g, alignedMouse);
    }
  }

  private void paintCopyImage(Graphics2D g, Point alignedMouse)
  {
    Composite oldComposite = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    g.drawImage(copyImage, alignedMouse.x - copyImage.getWidth(), alignedMouse.y - copyImage.getHeight(), null);
    g.setComposite(oldComposite);
  }

  private void paintCopyOutline(Graphics2D g, Point alignedMouse)
  {
    Stroke oldStroke = g.getStroke();
    g.setStroke(new BasicStroke(1.5f, 0, 0, 1, new float[] { 8, 6 }, 0));
    g.drawRect(alignedMouse.x - copyImage.getWidth(), alignedMouse.y - copyImage.getHeight(), copyImage.getWidth(), copyImage.getHeight());
    g.setStroke(oldStroke);
  }
  
  
  @Override
  public void reset() {
    isMovingComponents = false;
  }
}
