/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config;

import graphicalcircuit.config.ComponentConfig;
import graphicalcircuit.config.NameConfig;

import java.awt.Component;

import javax.swing.JOptionPane;

import snet.internal.Message;
import network.hooks.TransferComponentConfigHook;
import configuration.Table;

public class NameDialog implements ConfigDialog {

  private Component parent;
  private String message_key;
  
  public NameDialog(Component parent, String message_key) {
    this.parent = parent;
    this.message_key = message_key;
  }

  @Override
  public Message open(int componentId, ComponentConfig config) {
    NameConfig nameConfig = (NameConfig)config;
    String newName = JOptionPane.showInputDialog(parent, Table.get(message_key), nameConfig.name);
    if (newName != null && !newName.isEmpty()) {
      nameConfig.name = newName;
      
      return new TransferComponentConfigHook(null).createMessage(componentId, nameConfig);
    }
    
    return null;
  }
}
