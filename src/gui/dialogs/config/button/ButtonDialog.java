/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config.button;

import graphicalcircuit.config.ButtonConfig;
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

public class ButtonDialog implements ConfigDialog, Closable {

  private JFrame parent;

  private JDialog dialog;

  private IsToggleButtonCheckbox isToggleButton;
  
  private boolean saveSettings;

  
  public ButtonDialog(JFrame parent) {
    this.parent = parent;

    initializeComponents();
  }

  private void initializeComponents() {
    isToggleButton = new IsToggleButtonCheckbox();
    
    dialog = new JDialog(parent, Table.get("config_dialog_button_title"));
    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(dialog);
    layout.setInsets(5, 50, 5, 50);
    layout.addToGrid(isToggleButton, 0, 0, 1, 1, GridBagConstraints.NONE, 0, 0);
    layout.setInsets(5, 5, 5, 5);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(new OkButton(this)); buttonPanel.add(new CancelButton(dialog));
    layout.addToGrid(buttonPanel, 0, 1, 1, 1, GridBagConstraints.NONE, 0, 0);
    
    
    dialog.pack();
  }

  @Override
  public Message open(int componentId, ComponentConfig config) {
    ButtonConfig buttonConfig = (ButtonConfig)config;
    
    saveSettings = false;

    isToggleButton.setSelected(buttonConfig.isToggleButton);
    
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (saveSettings)
    {
      buttonConfig.isToggleButton = isToggleButton.isSelected();
      return new TransferComponentConfigHook(null).createMessage(componentId, buttonConfig);
    }
    
    return null;
  }
  
  @Override
  public void saveAndClose() {
    saveSettings = true;
    dialog.dispose();
  }
}
