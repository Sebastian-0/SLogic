/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.module;

import graphicalcircuit.ModuleDefinition;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import configuration.Table;

public class NamePanel extends JPanel
{
  private ModuleDialog dialog;
  
  private ModuleDefinition definition;

  private JTextField nameField;
  
  public NamePanel(ModuleDialog dialog, ModuleDefinition definition)
  {
    this.dialog = dialog;
    this.definition = definition;
    
    setBorder(new TitledBorder(Table.get("modules_name")));
    
    nameField = new JTextField(10);
    nameField.setDocument(filterDocument);
    nameField.setText(definition.name);
    nameField.getDocument().addDocumentListener(listener);
    
    setLayout(new BorderLayout());
    add(nameField, BorderLayout.CENTER);
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
      definition.name = nameField.getText();
      dialog.repaint();
    }
  };
  
  
  private PlainDocument filterDocument = new PlainDocument() {
    
    @Override
    public void insertString(int offs, String str, AttributeSet a)
        throws BadLocationException
    {
      String text = str;
      String result = "";
      for (int i = 0; i < text.length(); i++)
      {
        char current = text.charAt(i);
        if (Character.isLetterOrDigit(current) || current == '-')
          result += current;
      }
      
      if (!result.isEmpty())
        super.insertString(offs, result, a);
    }
  };
}
