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

import java.util.List;

import javax.swing.JTextField;

import configuration.Config;
import configuration.Table;

public class UsernameTextField extends JTextField implements
    PreferenceComponent
{
  private ProgramUI program;
  
  public UsernameTextField(ProgramUI program)
  {
    this.program = program;
    
    setText(Config.get(Config.USER_NAME));
  }

  @Override
  public void collectErrors(List<String> targetList)
  {
    if (getText().trim().isEmpty())
      targetList.add(Table.get("preferences_username_must_have_characters") + "!");
  }

  @Override
  public boolean saveSettings()
  {
    String currentUsername = Config.get(Config.USER_NAME);
    String newUsername = getText().trim();
    if (!currentUsername.equals(newUsername))
    {
      Config.put(Config.USER_NAME, newUsername);
      
      program.getBackend().getSession().getClient().setUsername(newUsername);
    }
    
    return false;
  }
}
