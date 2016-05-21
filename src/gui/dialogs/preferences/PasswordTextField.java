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

import javax.swing.JTextField;

import configuration.Config;
import configuration.Table;

public class PasswordTextField extends JTextField implements PreferenceComponent
{
  private PasswordProtectionCheckBox passwordProtection;
  
  public PasswordTextField(ProgramUI program, PasswordProtectionCheckBox passwordProtection)
  {
    super (Config.get(Config.PASSWORD, ""));
    
    this.passwordProtection = passwordProtection;
    passwordProtection.addActionListener(checkboxListener);
    
    setEnabled(passwordProtection.isSelected() && !program.getBackend().getSession().isClient());
  }
  

  @Override
  public void collectErrors(List<String> targetList)
  {
    if (passwordProtection.isSelected() && getText().isEmpty())
      targetList.add(Table.get("preferences_password_must_have_characters") + "!");
  }

  @Override
  public boolean saveSettings()
  {
    Config.put(Config.PASSWORD, getText());
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
