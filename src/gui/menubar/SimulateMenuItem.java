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

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import configuration.Table;

public class SimulateMenuItem extends AbstractMenuItem
{
  public SimulateMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_simulate"), window);
    
    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
  }

  @Override
  protected void doAction(ProgramUI program)
  {
    program.startSimulation();
  }
}
