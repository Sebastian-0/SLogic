/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.preferences;

import gui.backend.ProgramUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

import configuration.Config;
import configuration.PasswordProtectionLevel;
import configuration.Table;

public class ProtectionLevelComboBox extends JComboBox<String> implements PreferenceComponent
{
  private PasswordProtectionCheckBox passwordProtection;
  
  public ProtectionLevelComboBox(ProgramUI program, PasswordProtectionCheckBox passwordProtection)
  {
    this.passwordProtection = passwordProtection;
    passwordProtection.addActionListener(checkboxListener);
    
    PasswordProtectionLevel currentProtectionLevel = PasswordProtectionLevel.getTypeByName(Config.get(Config.PROTECTION_LEVEL));
    for (PasswordProtectionLevel type : PasswordProtectionLevel.values())
    {
      addItem(Table.get("protection_level_" + type.getName()));
    }
    setSelectedItem(Table.get("protection_level_" + currentProtectionLevel.getName()));
    setEnabled(passwordProtection.isSelected() && !program.getBackend().getSession().isClient());
  }
  

  @Override
  public void collectErrors(List<String> targetList)
  {
  }

  @Override
  public boolean saveSettings()
  {
    Config.put(Config.PROTECTION_LEVEL, PasswordProtectionLevel.values()[getSelectedIndex()].getName());
    return false;
  }
  
  
  private ActionListener checkboxListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      setEnabled(passwordProtection.isSelected());
    }
  };
}
