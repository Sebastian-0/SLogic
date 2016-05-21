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

import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import util.SimpleGridBagLayout;

public class SettingsPanel extends JPanel
{
  private InitialDelaySlider initialDelay;
  private OffTimeSlider offTime;
  private OnTimeSlider onTime;

  public SettingsPanel()
  {
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    initialDelay = new InitialDelaySlider();
    offTime = new OffTimeSlider();
    onTime = new OnTimeSlider();
    
    layout.addToGrid(initialDelay, 0, 0, 1, 1, GridBagConstraints.BOTH, 1, 1);
    layout.addToGrid(offTime, 0, 1, 1, 1, GridBagConstraints.BOTH, 1, 1);
    layout.addToGrid(onTime, 0, 2, 1, 1, GridBagConstraints.BOTH, 1, 1);
  }
  
  public void setClockConfig(ClockConfig config) {
    initialDelay.setClockConfig(config);
    offTime.setClockConfig(config);
    onTime.setClockConfig(config);
  }
}
