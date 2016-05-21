/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config.led;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

public class ColorChooserRadioButton extends JRadioButton {
  
  private LEDColorDialog dialog;
  private Color color;
  
  public ColorChooserRadioButton(LEDColorDialog dialog, String text) {
    super (text);
    this.dialog = dialog;
    
    addActionListener(listener);
  }
  
  public void setColor(Color color)
  {
    this.color = color;
  }
  
  public Color getColor()
  {
    return color;
  }

  private ActionListener listener = new ActionListener() {
    
    @Override
    public void actionPerformed(ActionEvent e) {
      dialog.changeCurrentButton(ColorChooserRadioButton.this);  
    }
  };
}
