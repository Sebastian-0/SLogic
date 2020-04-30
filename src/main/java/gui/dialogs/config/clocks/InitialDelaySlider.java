/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config.clocks;

import graphicalcircuit.config.ClockConfig;
import configuration.Table;


public class InitialDelaySlider extends ValueSlider
{
  public InitialDelaySlider()
  {
    super (0, 10000, 1000, Table.get("config_dialog_clocks_initial_off_time"));
  }
  
  @Override
  protected int getValueFromNewClock(ClockConfig config)
  {
    return config.initialOffTime;
  }

  @Override
  protected void valueChangedByUser(ClockConfig config, int newValue)
  {
    config.initialOffTime = newValue;
  }
}
