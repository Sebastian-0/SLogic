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

public interface PreferenceComponent
{
  public void collectErrors(List<String> targetList);
  
  /**
   * Saves the settings of this tab, returning true if one of the settings demands
   *  the program to be restarted to apply correctly.
   * @return True if the program must be restarted to apply some of the changes
   */
  public boolean saveSettings();
}
