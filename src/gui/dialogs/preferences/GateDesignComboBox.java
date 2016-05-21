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
import configuration.GateDesignType;
import configuration.Table;

public class GateDesignComboBox extends JComboBox<String> implements PreferenceComponent
{
  public GateDesignComboBox()
  {
    GateDesignType currentGateDesignType = GateDesignType.getTypeByName(Config.get(Config.GATE_DESIGN_TYPE));
    for (GateDesignType type : GateDesignType.values())
    {
      addItem(Table.get("gate_design_" + type.getName()));
    }
    setSelectedItem(Table.get("gate_design_" + currentGateDesignType.getName()));
  }
  

  @Override
  public void collectErrors(List<String> targetList)
  {
  }

  @Override
  public boolean saveSettings()
  {
    Config.put(Config.GATE_DESIGN_TYPE, GateDesignType.values()[getSelectedIndex()].getName());
    return false;
  }
}
