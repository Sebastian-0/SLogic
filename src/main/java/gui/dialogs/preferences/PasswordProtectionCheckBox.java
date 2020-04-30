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

import javax.swing.JCheckBox;

import configuration.Config;
import configuration.Table;

public class PasswordProtectionCheckBox extends JCheckBox implements
    PreferenceComponent
{
  public PasswordProtectionCheckBox(ProgramUI program)
  {
    super (Table.get("preferences_use_password"));
    setOpaque(false);
    setSelected(hasPasswordProtection());
    
    setEnabled(!program.getBackend().getSession().isClient());
  }

  private boolean hasPasswordProtection()
  {
    return Config.get(Config.USE_PASSWORD_PROTECTION).equals("true");
  }

  @Override
  public void collectErrors(List<String> targetList)
  {
  }

  @Override
  public boolean saveSettings()
  {
    Config.put(Config.USE_PASSWORD_PROTECTION, Boolean.toString(isSelected()));
    return false;
  }
}
