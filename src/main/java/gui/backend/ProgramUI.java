/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend;


public interface ProgramUI {
  public void shutdown();
  public void startSimulation();
  public void repaint();

  public void zoomIn();
  public void zoomOut();
  
  public ProgramBackend getBackend();
}
