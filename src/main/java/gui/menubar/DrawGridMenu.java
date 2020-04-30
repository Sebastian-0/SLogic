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

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import configuration.Table;

public class DrawGridMenu extends JMenu
{
  public DrawGridMenu(ProgramUI window)
  {
    super (Table.get("menubar_draw_grid_menu"));
    
    ButtonGroup group = new ButtonGroup();
    
    DrawGridMenuItem drawGrid = new DrawGridMenuItem(window);
    DrawDotsMenuItem drawDots= new DrawDotsMenuItem(window);
    DrawNoGridMenuItem drawNone = new DrawNoGridMenuItem(window);
    
    group.add(drawGrid);
    group.add(drawDots);
    group.add(drawNone);
    
    add(drawGrid);
    add(drawDots);
    add(drawNone);
  }
}
