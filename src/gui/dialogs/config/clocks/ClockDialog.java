/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config.clocks;

import graphicalcircuit.config.ClockConfig;
import graphicalcircuit.config.ComponentConfig;
import gui.dialogs.CancelButton;
import gui.dialogs.config.ConfigDialog;
import gui.dialogs.config.OkButton;
import gui.dialogs.config.OkButton.Closable;

import java.awt.Dialog.ModalityType;
import java.awt.GridBagConstraints;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.SimpleGridBagLayout;
import snet.internal.Message;
import network.hooks.TransferComponentConfigHook;
import configuration.Table;

public class ClockDialog implements ConfigDialog, Closable
{
  private JDialog dialog;
  private SettingsPanel settingsPanel;
  
  private boolean saveSettings;
  

  public ClockDialog(JFrame program)
  {
    dialog = new JDialog(program, Table.get("config_dialog_clocks_title"));
    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(dialog);
    
    settingsPanel = new SettingsPanel();
    
    layout.addToGrid(settingsPanel, 0, 0, 1, 1, GridBagConstraints.BOTH, 1, 1);
    layout.setInsets(5, 5, 5, 5);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(new OkButton(this)); buttonPanel.add(new CancelButton(dialog));
    layout.addToGrid(buttonPanel, 0, 1, 1, 1, GridBagConstraints.NONE, 0, 0);
    
    dialog.pack();
    
    dialog.setMinimumSize(dialog.getSize());
    dialog.setLocationRelativeTo(program);
  }
  
  @Override
  public Message open(int componentId, ComponentConfig config) {
    saveSettings = false;
    
    settingsPanel.setClockConfig((ClockConfig)config);
    dialog.setVisible(true);
    
    if (saveSettings)
      return new TransferComponentConfigHook(null).createMessage(componentId, config);
    return null;
  }
  
  @Override
  public void saveAndClose() {
    saveSettings = true;
    dialog.dispose();
  }
}
