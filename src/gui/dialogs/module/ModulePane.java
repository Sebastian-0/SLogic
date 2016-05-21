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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class ModulePane extends JPanel
{
  private GraphicalModule module;
  
  
  public ModulePane(ModuleDefinition definition)
  {
    this.module = new GraphicalModule(definition);
    
    setBackground(Color.WHITE);
    setBorder(new LineBorder(Color.DARK_GRAY));
    setPreferredSize(new Dimension(200, 200));
    
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseListener);
  }
  
  
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    g.translate(getWidth()/2, getHeight()/2);
    module.render(g);
    g.translate(-getWidth()/2, -getHeight()/2);
  }
  
  
  
  private MouseAdapter mouseListener = new MouseAdapter() {
    
    @Override
    public void mousePressed(MouseEvent e)
    {
      e.translatePoint(-getWidth()/2, -getHeight()/2);
      module.mousePressed(e.getX(), e.getY());
      e.translatePoint(getWidth()/2, getHeight()/2);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      module.mouseReleased();
      repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
      mouseMotion(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
      mouseMotion(e);
    }
    
    private void mouseMotion(MouseEvent e)
    {
      e.translatePoint(-getWidth()/2, -getHeight()/2);
      updateCursor(e.getX(), e.getY());
      module.mouseMoved(e.getX(), e.getY());
      e.translatePoint(getWidth()/2, getHeight()/2);
      repaint();
    }

    private void updateCursor(int x, int y)
    {
      boolean hasHorizontalCollision = module.hasHorizontalCollision(x, y);
      boolean hasVerticalCollision = module.hasVerticalCollision(x, y);
      if (hasHorizontalCollision && hasVerticalCollision)
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      else if (hasHorizontalCollision)
        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
      else if (hasVerticalCollision)
        setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
      else
        setCursor(Cursor.getDefaultCursor());
    }
  };
}
