/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface Tool {
  public boolean mousePressed(ProgramBackend backend, MouseEvent event, int viewportX, int viewportY);
  public void mouseReleased(ProgramBackend backend, MouseEvent event, int viewportX, int viewportY);
  public void mouseMoved(ProgramBackend backend, MouseEvent event, boolean isDragged, int viewportX, int viewportY);
  public boolean keyPressed(ProgramBackend backend, KeyEvent event);
  public boolean keyReleased(ProgramBackend backend, KeyEvent event);
  
  public void paintMarker(ProgramBackend backend, Graphics2D g, int xPos, int yPos);
 
  public void reset();
  
  public String getName();
}
