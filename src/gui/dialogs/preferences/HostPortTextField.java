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

import javax.swing.JTextField;

import configuration.Config;
import configuration.Table;

public class HostPortTextField extends JTextField implements
    PreferenceComponent
{
  public HostPortTextField()
  {
    setText(Config.get(Config.HOST_PORT));
  }

  @Override
  public void collectErrors(List<String> targetList)
  {
    try
    {
      int port = Integer.parseInt(getText());
      if (port < 1025 || port > 65535) throw new NumberFormatException();
    }
    catch (NumberFormatException e)
    {
      targetList.add(Table.get("preferences_invalid_host_port") + "!");
    }
  }

  @Override
  public boolean saveSettings()
  {
    String currentHostPort = Config.get(Config.HOST_PORT);
    if (!currentHostPort.equals(getText()))
    {
      Config.put(Config.HOST_PORT, getText());
      return true;
    }
    
    return false;
  }
}
