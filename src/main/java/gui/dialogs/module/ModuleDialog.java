/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.module;

import graphicalcircuit.Module;
import graphicalcircuit.ModuleDefinition;
import graphicalcircuit.ModuleDefinition.Connection;
import gui.backend.ProgramUI;
import gui.dialogs.CancelButton;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import snet.internal.Message;
import network.hooks.TransferModuleHook;
import sutilities.Debugger;
import util.Grid;
import util.SimpleGridBagLayout;
import configuration.Table;
import database.ModuleSaver;

public class ModuleDialog extends JDialog
{
  private ProgramUI program;
  
  private ModuleMaker moduleMaker;
  private ModuleDefinition moduleDefinition;
  
  private CreateModuleButton createModuleButton;
  private CancelButton cancelButton;
  
  public ModuleDialog(ProgramUI program, ModuleMaker moduleMaker)
  {
    super((Frame) program, Table.get("modules_title"));
    
    this.program = program;
    this.moduleMaker = moduleMaker;
    
    moduleDefinition = moduleMaker.createDefaultDefinition(Grid.GRID_WIDTH);
    
    createModuleButton = new CreateModuleButton(this);
    cancelButton = new CancelButton(this);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    layout.addToGrid(new ConnectionPanel(this, fetchInputs(moduleDefinition.inputs), Table.get("modules_inputs")), 0, 2, 1, 1, GridBagConstraints.VERTICAL, 0, 1);
    layout.addToGrid(new ConnectionPanel(this, fetchClocks(moduleDefinition.inputs), Table.get("modules_clocks")), 1, 2, 1, 1, GridBagConstraints.VERTICAL, 0, 1);
    layout.addToGrid(new ConnectionPanel(this, moduleDefinition.outputs, Table.get("modules_outputs")), 2, 2, 1, 1, GridBagConstraints.VERTICAL, 0, 1);
    
    layout.addToGrid(new NamePanel(this, moduleDefinition), 0, 0, 3, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    layout.addToGrid(new DescriptionPanel(moduleDefinition), 0, 1, 3, 1, GridBagConstraints.BOTH, 0, 1);
    
    layout.addToGrid(new ModulePane(moduleDefinition), 3, 0, 1, 3, GridBagConstraints.BOTH, 1, 1);
    
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(createModuleButton); buttonPanel.add(cancelButton);
    layout.addToGrid(buttonPanel, 0, 3, 4, 1, GridBagConstraints.NONE, 0, 0);
    
    pack();
    setMinimumSize(getSize());
    
    setLocationRelativeTo((Component) program);
  }
  
  private List<Connection> fetchInputs(List<Connection> connections)
  {
    List<Connection> result = new ArrayList<Connection>(connections);
    for (int i = 0; i < result.size(); i++)
    {
      if (result.get(i).type != Connection.INPUT)
        result.remove(i--);
    }
    return result;
  }
  
  private List<Connection> fetchClocks(List<Connection> connections)
  {
    List<Connection> result = new ArrayList<Connection>(connections);
    for (int i = 0; i < result.size(); i++)
    {
      if (result.get(i).type != Connection.CLOCK)
        result.remove(i--);
    }
    return result;
  }
  
  
  public void makeModule()
  {
    try
    {
      Module module = moduleMaker.createModule(moduleDefinition);
      
      File fileToTransfer = Files.createTempFile("CLS_M_Trans", ".tmp").toFile();
      new ModuleSaver(fileToTransfer).save(module);
      
      Message message = new TransferModuleHook(null).createMessage(fileToTransfer);
      program.getBackend().getSession().getClient().getNetwork().send(null, message);
      
      fileToTransfer.delete();
    } catch (IOException exception)
    {
      JOptionPane.showMessageDialog(
          (Component) program,
          Table.get("transfer_module_failed_to_save_locally") + "!",
          Table.get("popup_title_error"),
          JOptionPane.ERROR_MESSAGE);
      Debugger.error("ModuleDialog: makeModule()", "Failed to save the module locally before transfer!", exception);
    }
  }
}
