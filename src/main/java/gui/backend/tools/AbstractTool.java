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
import gui.backend.Tool;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import util.Grid;

public abstract class AbstractTool implements Tool
{
  @Override
  public boolean mousePressed(ProgramBackend backend, MouseEvent event, int viewportX, int viewportY) { return false; }
  @Override
  public void mouseReleased(ProgramBackend backend, MouseEvent event, int viewportX, int viewportY) {}
  @Override
  public void mouseMoved(ProgramBackend backend, MouseEvent event, boolean isDragged, int viewportX, int viewportY) {}
  @Override
  public boolean keyPressed(ProgramBackend backend, KeyEvent event) { return false; }
  @Override
  public boolean keyReleased(ProgramBackend backend, KeyEvent event) { return false; }
  
  
  protected Point alignToGrid(Point source)
  {
    return Grid.alignToGrid(source);
  }
  
  protected boolean isLeftMouseButton(MouseEvent event)
  {
    return event.getButton() == MouseEvent.BUTTON1;
  }
  
  protected boolean isMiddleMouseButton(MouseEvent event)
  {
    return event.getButton() == MouseEvent.BUTTON2;
  }
  
  protected boolean isRightMouseButton(MouseEvent event)
  {
    return event.getButton() == MouseEvent.BUTTON3;
  }
}

