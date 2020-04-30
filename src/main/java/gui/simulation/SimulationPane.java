/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.simulation;

import graphicalcircuit.SimulationInputListener;
import gui.simulation.backend.SimulationBackend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

public class SimulationPane extends JPanel implements Observer
{
  private SimulationBackend backend;
  
  private List<SimulationInputListener> inputListeners;

  private boolean isMovingViewPort;
  
  private int viewportX;
  private int viewportY;
  
  private int mouseX;
  private int mouseY;
  
  public SimulationPane(SimulationBackend backend, List<SimulationInputListener> inputsListeners)
  {
    this.backend = backend;
    this.inputListeners = inputsListeners;
    backend.addObserver(this);
    
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(800, 600));
    
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseListener);
  }
  
  
  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    g2d.translate(-viewportX, -viewportY);
    backend.getGraphicalCircuit().render(g2d);
    g2d.translate(viewportX, viewportY);
  }
  
  
  @Override
  public void update(Observable o, Object arg)
  {
    repaint();
  }
  
  
  private MouseAdapter mouseListener = new MouseAdapter() {
    
    @Override
    public void mousePressed(MouseEvent e)
    {
      boolean usedInput = false;
      for (SimulationInputListener listener : inputListeners) 
      {
        usedInput |= listener.mousePressed(e.getX() + viewportX, e.getY() + viewportY);
      }
      
      if (usedInput)
      {
        updateSimulation();
      }
      else
      {
        isMovingViewPort = true;
      }
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      boolean usedInput = false;
      for (SimulationInputListener listener : inputListeners) 
      {
        usedInput |= listener.mouseReleased(e.getX() + viewportX, e.getY() + viewportY);
      }
      if (usedInput)
        updateSimulation();
      
      isMovingViewPort = false;
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
      mouseRepositioned(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
      mouseRepositioned(e);
    }

    private void mouseRepositioned(MouseEvent e)
    {
      if (isMovingViewPort)
      {
        viewportX += mouseX - e.getX();
        viewportY += mouseY - e.getY();
        repaint();
      }
      else
      {
        boolean usedInput = false;
        for (SimulationInputListener listener : inputListeners) 
        {
          usedInput |= listener.mouseMoved(e.getX() + viewportX, e.getY() + viewportY);
        }
        if (usedInput)
          updateSimulation();
      }
      
      mouseX = e.getX();
      mouseY = e.getY();
    }


    private void updateSimulation() {
      backend.simulate();
      repaint();
    }
  };
}
