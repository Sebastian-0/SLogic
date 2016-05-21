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

public class NumberOfBackupsTextField extends JTextField implements PreferenceComponent
{
  public NumberOfBackupsTextField(ProgramUI program)
  {
    super (Config.get(Config.BACKUP_SAVE_AMOUNT));
    
    setEnabled(!program.getBackend().getSession().isClient());
  }
  

  @Override
  public void collectErrors(List<String> targetList)
  {
    boolean hasValidNumber = true;
    try
    {
      int number = Integer.parseInt(getText());
      if (number < 0 || number > 10)
        hasValidNumber = false;
    }
    catch (NumberFormatException e)
    {
      hasValidNumber = false;
    }
    
    if (!hasValidNumber)
      targetList.add(Table.get("preferences_invalid_number_of_backups") + "!");
  }

  @Override
  public boolean saveSettings()
  {
    Config.put(Config.BACKUP_SAVE_AMOUNT, getText());
    return false;
  }
}
