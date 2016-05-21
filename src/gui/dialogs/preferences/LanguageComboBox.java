/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.preferences;

import java.util.List;

import javax.swing.JComboBox;

import configuration.Config;
import configuration.Table;

public class LanguageComboBox extends JComboBox<String> implements PreferenceComponent
{
  public LanguageComboBox()
  {
    for (Table language : Table.getLanguages())
    {
      addItem(language.getName());
    }
    
    setSelectedItem(Config.get(Config.LANGUAGE));
  }
  

  @Override
  public void collectErrors(List<String> targetList)
  {
  }

  @Override
  public boolean saveSettings()
  {
    String oldLanguage = Config.get(Config.LANGUAGE);
    if (!oldLanguage.equals(getSelectedItem().toString()))
    {
      Config.put(Config.LANGUAGE, getSelectedItem().toString());
      return true;
    }
    
    return false;
  }

}
