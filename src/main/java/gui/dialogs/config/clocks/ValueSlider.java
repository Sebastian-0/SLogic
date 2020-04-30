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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.SimpleGridBagLayout;

public abstract class ValueSlider extends JPanel
{
  private ClockConfig config;
  
  private JSlider slider;
  private JTextField textField;
  
  public ValueSlider(int min, int max, int initial, String borderTitle)
  {
    slider = new JSlider(min, max, initial);
    textField = new JTextField(Integer.toString(initial), 5);
    
    slider.addChangeListener(changeListener);
    textField.addActionListener(actionListener);

    slider.setMajorTickSpacing(max - min);
    slider.setPaintLabels(true);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    layout.addToGrid(slider, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    layout.addToGrid(textField, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0.3, 0);

    setBorder(new TitledBorder(borderTitle));
  }
  
  
  public int getValue()
  {
    return slider.getValue();
  }
  
  public void setClockConfig(ClockConfig config)
  {
    this.config = config;
    int newValue = getValueFromNewClock(config);
    slider.setValue(newValue);
    textField.setText(Integer.toString(slider.getValue()));
  }
  
  
  protected abstract int getValueFromNewClock(ClockConfig config);
  protected abstract void valueChangedByUser(ClockConfig config, int newValue);
  
  
  
  private ChangeListener changeListener = new ChangeListener() {
    
    @Override
    public void stateChanged(ChangeEvent e)
    {
      textField.setText(Integer.toString(slider.getValue()));
      if (!slider.getValueIsAdjusting())
        valueChangedByUser(config, slider.getValue());
    }
  };
  
  
  
  private ActionListener actionListener = new ActionListener() {
    
    @Override
    public void actionPerformed(ActionEvent event)
    {
      try
      {
        int newValue = Integer.parseInt(textField.getText());
        slider.setValue(newValue);
        valueChangedByUser(config, slider.getValue());
      }
      catch (IllegalArgumentException e) { }
    }
  };
}
