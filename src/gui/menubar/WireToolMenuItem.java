/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import gui.backend.ProgramBackend;
import gui.backend.ProgramUI;
import gui.backend.tools.ToolFactory;

import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.KeyStroke;

import network.User.Privileges;
import configuration.Table;

public class WireToolMenuItem extends AbstractMenuItem implements Observer
{
  public WireToolMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_wire_tool"), window);
    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    
    window.getBackend().addInterfaceObserver(this);
  }
  
  @Override
  public void update(Observable o, Object arg)
  {
    ProgramBackend backend = (ProgramBackend)arg;
    setEnabled(backend.getSession().getClient().getPrivileges() == Privileges.ReadWrite);
  }

  @Override
  protected void doAction(ProgramUI program)
  {
    program.getBackend().getSession().getClient().getWorkspace().tool = ToolFactory.getTool(ToolFactory.Type.PLACE_WIRE);
    program.repaint();
  }
}
