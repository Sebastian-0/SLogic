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
import network.hooks.LoadCircuitRequestHook;
import configuration.Config;
import configuration.Table;

public class OpenMenuItem extends DialogMenuItem implements Observer
{
  public OpenMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_open"), window);
    window.getBackend().addInterfaceObserver(this);
    
    setEnabled(!window.getBackend().getSession().isClient());
    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
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
    return false;
  }

  @Override
  protected int openDialog(ProgramUI window, JFileChooser chooser)
  {
    return chooser.showDialog((Component) window, Table.get("file_chooser_title_open"));
  }

  @Override
  protected void performAction(ProgramUI window, File file) throws FileNotFoundException
  {
    if (!file.exists())
    {
      file = new File(file.getAbsolutePath() + "." + Config.CIRCUIT_EXTENSION);
      if (!file.exists())
        throw new FileNotFoundException(file.getName().substring(0, file.getName().length() - Config.CIRCUIT_EXTENSION.length() - 1));
    }
    
    Message message = new LoadCircuitRequestHook(null).createMessage(file);
    window.getBackend().getSession().getClient().getNetwork().send(null, message);
  }
}
