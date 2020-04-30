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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import util.SimpleGridBagLayout;

public class ConnectionPanel extends JPanel
{
  public ConnectionPanel(ModuleDialog dialog, List<Connection> connections, String title)
  {
    setBorder(new TitledBorder(title));
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    layout.setInsets(0, 0, 0, 0);
    
    int y = 0;
    for (Connection connection : connections)
    {
      layout.addToGrid(new ConnectionEditor(dialog, connection), 0, y++, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0, GridBagConstraints.NORTH);
    }
    JPanel fillPanel = new JPanel();
    fillPanel.setPreferredSize(new Dimension(80, 10));
    layout.addToGrid(fillPanel, 0, y, 1, 1, GridBagConstraints.VERTICAL, 0, 1, GridBagConstraints.CENTER);
  }
}
