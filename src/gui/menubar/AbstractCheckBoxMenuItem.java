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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

public abstract class AbstractCheckBoxMenuItem extends JCheckBoxMenuItem
{
  private ProgramUI window;
  
  public AbstractCheckBoxMenuItem(String text, ProgramUI window)
  {
    super (text);
    
    this.window = window;
    
    addActionListener(actionListener);
  }
  
  protected abstract void doAction(ProgramUI program);
  
  
  private ActionListener actionListener = new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      doAction(window);
    }
  };
}
