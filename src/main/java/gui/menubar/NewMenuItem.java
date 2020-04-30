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

import java.util.Observable;
import java.util.Observer;

import network.User.Privileges;
import snet.internal.Message;
import network.hooks.CreateNewCircuitHook;
import configuration.Table;

public class NewMenuItem extends AbstractMenuItem implements Observer
{
  public NewMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_new"), window);
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
  protected void doAction(ProgramUI program)
  {
    Message message = new CreateNewCircuitHook(null).createMessage("");
    program.getBackend().getSession().getClient().getNetwork().send(null, message);
  }
}
