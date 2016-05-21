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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import configuration.Table;

public class DescriptionPanel extends JPanel
{
  private ModuleDefinition definition;

  private JTextArea descriptionArea;
  
  public DescriptionPanel(ModuleDefinition definition)
  {
    this.definition = definition;
    
    setBorder(new TitledBorder(Table.get("modules_description")));
    
    descriptionArea = new JTextArea(definition.description, 8, 10);
    descriptionArea.getDocument().addDocumentListener(listener);

    JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(descriptionArea);
    
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
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
      definition.description = descriptionArea.getText().trim();
      if (definition.description.equals(""))
        definition.description = null;
    }
  };
}
