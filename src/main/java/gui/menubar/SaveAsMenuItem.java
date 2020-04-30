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

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import network.User.Privileges;
import snet.internal.Message;
import network.hooks.SaveCircuitHook;
import configuration.Table;

public class SaveAsMenuItem extends DialogMenuItem implements Observer
{
  public SaveAsMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_save_as"), window);
    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
    
    window.getBackend().addInterfaceObserver(this);

    setEnabled(!window.getBackend().getSession().isClient());
  }
  
  @Override
  public void update(Observable o, Object arg)
  {
    ProgramBackend backend = (ProgramBackend)arg;
    setEnabled(!backend.getSession().isClient() && backend.getSession().getClient().getPrivileges() == Privileges.ReadWrite);
  }

  @Override
  protected boolean shouldOpenDialog(ProgramUI window)
  {
    return true;
  }
  
  @Override
  protected boolean shouldAddExtensionIfMissing()
  {
    return true;
  }
  
  @Override
  protected int openDialog(ProgramUI window, JFileChooser chooser)
  {
    return chooser.showDialog((Component) window, Table.get("file_chooser_title_save_as"));
  }

  @Override
  protected void performAction(ProgramUI window, File file) throws FileNotFoundException
  {
    Message message = new SaveCircuitHook(null).createMessage(file);
    window.getBackend().getSession().getClient().getNetwork().send(null, message);
  }
}
