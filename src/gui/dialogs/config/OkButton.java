/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import configuration.Table;

public class OkButton extends JButton
{
  private Closable closable;
  
  public OkButton(Closable dialog)
  {
    super(Table.get("button_ok"));
    this.closable = dialog;
    addActionListener(actionListener);
  }
  
  
  private ActionListener actionListener = new ActionListener() {
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
      closable.saveAndClose();
    }
  };
  
  
  public interface Closable {
    public void saveAndClose();
  }
}
