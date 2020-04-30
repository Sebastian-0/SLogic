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

public class ZoomInMenuItem extends AbstractMenuItem
{
  public ZoomInMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_zoom_in"), window);

    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK));
  }

  @Override
  protected void doAction(ProgramUI program)
  {
    program.zoomIn();
  }
}
