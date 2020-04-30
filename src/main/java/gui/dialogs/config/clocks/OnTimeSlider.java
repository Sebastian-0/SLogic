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

public class OnTimeSlider extends ValueSlider
{
  public OnTimeSlider()
  {
    super (10, 10000, 1000, Table.get("config_dialog_clocks_on_time"));
  }
  
  @Override
  protected int getValueFromNewClock(ClockConfig config)
  {
    return config.onTimeMillis;
  }

  @Override
  protected void valueChangedByUser(ClockConfig config, int newValue)
  {
    config.onTimeMillis = newValue;
  }
}
