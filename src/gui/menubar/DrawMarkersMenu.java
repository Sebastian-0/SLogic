/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import gui.backend.ProgramUI;

import javax.swing.JMenu;

import configuration.Table;

public class DrawMarkersMenu extends JMenu
{
  public DrawMarkersMenu(ProgramUI window)
  {
    super (Table.get("menubar_draw_markers_menu"));
    
    DrawUnconnectedWireMarkerMenuItem drawGrid = new DrawUnconnectedWireMarkerMenuItem(window);
    DrawUnusedPinMarkerMenuItem drawDots= new DrawUnusedPinMarkerMenuItem(window);
    
    add(drawGrid);
    add(drawDots);
  }
}
