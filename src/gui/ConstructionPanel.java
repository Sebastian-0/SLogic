/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class ConstructionPanel extends JPanel
{
  private ConstructionSurface constructionSurface;
  
  public ConstructionPanel(LogicSim program)
  {
    constructionSurface = new ConstructionSurface(program);
    
    setLayout(new BorderLayout());
    
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, constructionSurface, new ChatPanel(program));
    splitPane.setResizeWeight(1);
    add(splitPane, BorderLayout.CENTER);
  }
  
  public void dispose()
  {
    constructionSurface.dispose();
  }
  
  public ConstructionSurface getConstructionSurface()
  {
    return constructionSurface;
  }
}
