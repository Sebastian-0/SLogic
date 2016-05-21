/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import graphicalcircuit.GraphicalCircuit;
import gui.backend.ProgramBackend;
import gui.backend.ProgramUI;
import gui.dialogs.module.ModuleDialog;
import gui.dialogs.module.ModuleMaker;

import java.awt.Component;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import network.User.Privileges;
import circuit.Circuit;
import configuration.Table;
import database.Database;

public class CreateModuleMenuItem extends AbstractMenuItem implements Observer
{
  public CreateModuleMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_create_module"), window);
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
    Database database = program.getBackend().getSession().getClient().getWorkspace().database;
    Circuit circuit = database.getCircuit().makeCopy();
    GraphicalCircuit gcircuit = database.getGraphicalCircuit().makeCopy(circuit);
    
    ModuleMaker moduler = new ModuleMaker(circuit, gcircuit);
    if (moduler.canMakeModule())
      new ModuleDialog(program, moduler).setVisible(true);
    else
      JOptionPane.showMessageDialog(
          (Component) program,
          Table.get("modules_failed_to_create_message"),
          Table.get("modules_failed_to_create_title"),
          JOptionPane.ERROR_MESSAGE);
  }
}
