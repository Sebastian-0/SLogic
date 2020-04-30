/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.module;

import graphicalcircuit.ModuleDefinition.Connection;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ConnectionEditor extends JTextField
{
  private ModuleDialog dialog;
  
  private Connection connection;
  
  public ConnectionEditor(ModuleDialog dialog, Connection connection)
  {
    super (5);
    
    this.dialog = dialog;
    
    this.connection = connection;
    
    setText(connection.name);
    
    getDocument().addDocumentListener(listener);
  }
  
  
  private DocumentListener listener = new DocumentListener() {
    
    @Override
    public void removeUpdate(DocumentEvent e)
    {
      changed();
    }
    
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      changed();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e)
    {
      changed();
    }
    
    private void changed()
    {
      connection.name = getText();
      dialog.repaint();
    }
  };
}
