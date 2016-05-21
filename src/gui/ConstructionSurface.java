/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import gui.backend.Tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import network.User;
import network.User.Privileges;
import util.Grid;
import configuration.Config;
import configuration.EditorGridMode;
import configuration.Table;

public class ConstructionSurface extends JPanel implements Observer
{
  private static final float MINIMUM_SCALE_SIZE = 1/2f;
  private static final float MAXIMUM_SCALE_SIZE = 2f;
  
  
  private LogicSim window;

  private MouseSynchronizer mouseSynchronizer;
  
  private boolean isMovingViewPort;
  private boolean isReadOnlyMode;
  
  private float viewportX;
  private float viewportY;
  
  private int mouseX;
  private int mouseY;
  
  private Font labelFont;
  
  private float scale;
  
  
  public ConstructionSurface(LogicSim window)
  {
    this.window = window;
    window.getBackend().addRenderingObserver(this);
    
    mouseSynchronizer = new MouseSynchronizer(window.getBackend());
    
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(400, 400));
    
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseListener);
    addMouseWheelListener(mouseListener);
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
    
    labelFont = getFont().deriveFont(Font.BOLD, 12f);
    
    scale = 1;
  }
  
  
  public void resetViewport()
  {
    viewportX = 0;
    viewportY = 0;
  }
  
  
  public void zoomIn()
  {
    if (scale < MAXIMUM_SCALE_SIZE)
    {
      float oldScale = scale;
      if (scale < 1)
        scale += 1/4f;
      else
        scale += 1/2f;
      updateViewportAfterZooming(oldScale, scale);
      repaint();
    }
  }
  
  public void zoomOut()
  {
    if (scale > MINIMUM_SCALE_SIZE)
    {
      float oldScale = scale;
      if (scale <= 1)
        scale -= 1/4f;
      else
        scale -= 1/2f;
      updateViewportAfterZooming(oldScale, scale);
      repaint();
    }
  }
  
  private void updateViewportAfterZooming(float oldScale, float newScale)
  {
    int halfWidth = getWidth()/2;
    int halfHeight = getHeight()/2;
    
    float offsetX = halfWidth/oldScale - halfWidth/newScale;
    float offsetY = halfHeight/oldScale - halfHeight/newScale;

    viewportX += offsetX;
    viewportY += offsetY;
  }
  
  
  public void dispose()
  {
    mouseSynchronizer.dispose();
  }
  
  
  @Override
  public void paintComponent(Graphics graphics)
  {
    super.paintComponent(graphics);
    
    Graphics2D g2d = (Graphics2D)graphics;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    g2d.scale(scale, scale);
    
    renderGrids(graphics);

    g2d.translate(-(int)viewportX, -(int)viewportY);
    renderCircuit(g2d);
    if (!isReadOnlyMode)
      renderTool(g2d);
    renderUserCursors(g2d);
    g2d.translate((int)viewportX, (int)viewportY);

    g2d.scale(1/scale, 1/scale);

    if (isReadOnlyMode)
      renderReadOnlyLabel(g2d);
    else
      renderToolLabel(g2d);
    renderZoomLabel(g2d);
  }

  private void renderGrids(Graphics graphics)
  {
    EditorGridMode gridMode = EditorGridMode.getTypeByName(Config.get(Config.GRID_RENDERING_MODE));
    
    if (gridMode == EditorGridMode.Grid)
    {
      graphics.setColor(new Color(230, 230, 230));
      renderGrid(graphics, Grid.GRID_WIDTH * 4);
      graphics.setColor(new Color(210, 210, 210));
      renderGrid(graphics, Grid.GRID_WIDTH * 16);
    }
    else if (gridMode == EditorGridMode.Dots)
    {
      graphics.setColor(new Color(160, 160, 160));
      renderDots(graphics, Grid.GRID_WIDTH);
    }
  }

  private void renderGrid(Graphics graphics, int spaceBetweenLines)
  {
    int amountOfVerticalLines = scaleValueToInt(getWidth() / spaceBetweenLines) + 2;
    int amountOfHorizontalLines = scaleValueToInt(getHeight() / spaceBetweenLines) + 2;
    for (int x = 0; x < amountOfVerticalLines; x++)
    {
      graphics.drawLine(
          x * spaceBetweenLines - (int)viewportX % spaceBetweenLines,
          0,
          x * spaceBetweenLines - (int)viewportX % spaceBetweenLines,
          scaleValueToInt(getHeight()));
    }
    for (int y = 0; y < amountOfHorizontalLines; y++)
    {
      graphics.drawLine(
          0,
          y * spaceBetweenLines - (int)viewportY % spaceBetweenLines,
          scaleValueToInt(getWidth()),
          y * spaceBetweenLines - (int)viewportY % spaceBetweenLines);
    }
  }

  private void renderDots(Graphics graphics, int spaceBetweenLines)
  {
    int amountOfVerticalLines = scaleValueToInt(getWidth() / spaceBetweenLines) + 2;
    int amountOfHorizontalLines = scaleValueToInt(getHeight() / spaceBetweenLines) + 2;
    for (int x = 0; x < amountOfVerticalLines; x++)
    {
      for (int y = 0; y < amountOfHorizontalLines; y++)
      {
        graphics.fillRect(
            x * spaceBetweenLines - (int)viewportX % spaceBetweenLines,
            y * spaceBetweenLines - (int)viewportY % spaceBetweenLines,
            1,
            1);
      }
    }
  }

  private void renderCircuit(Graphics2D g2d)
  {
    window.getBackend().getSession().getClient().getWorkspace().database.getGraphicalCircuit().render(g2d);
  }

  private void renderTool(Graphics2D g2d)
  {
    getCurrentTool().paintMarker(
        window.getBackend(),
        g2d,
        scaleValueToInt(mouseX) + (int)viewportX,
        scaleValueToInt(mouseY) + (int)viewportY);
  }

  private void renderUserCursors(Graphics2D g2d)
  {
    g2d.setColor(Color.BLUE);
    for (User user : window.getBackend().getSession().getUsers())
    {
      if (!user.isTheUserOfThisClient)
      {
        g2d.fillRect(
            user.currentCursorPosition.x - 2,
            user.currentCursorPosition.y - 2,
            5,
            5);
      }
    }
  }

  private void renderToolLabel(Graphics2D g2d)
  {
    g2d.setColor(Color.BLACK);
    renderTopRightLabel(g2d, getCurrentTool().getName());
  }

  private void renderReadOnlyLabel(Graphics2D g2d)
  {
    g2d.setColor(new Color(40, 130, 0));
    renderTopRightLabel(g2d, Table.get("surface_read_only_mode"));
  }

  private void renderTopRightLabel(Graphics2D g2d, String text)
  {
    g2d.setFont(labelFont);
    FontMetrics metrics = g2d.getFontMetrics();
    g2d.drawString(text, getWidth() - metrics.stringWidth(text), metrics.getAscent());
  }

  private void renderZoomLabel(Graphics2D g2d)
  {
    if (scale != 1)
    {
      g2d.setColor(Color.BLACK);
      g2d.setFont(labelFont);
      FontMetrics metrics = g2d.getFontMetrics();
      String text = Table.get("surface_zoom") + ": " + getZoomPercent() + "%";
      g2d.drawString(text, getWidth() - metrics.stringWidth(text), getHeight() - metrics.getHeight() + metrics.getAscent());
    }
  }
  
  private String getZoomPercent()
  {
    return "" + (int)Math.round(scale*100);
  }

  private Tool getCurrentTool()
  {
    return window.getBackend().getSession().getClient().getWorkspace().tool;
  }
  
  
  @Override
  public void update(Observable o, Object arg)
  {
    if (arg instanceof Boolean)
    {
      boolean hasLoadedNewCircuit = ((Boolean) arg).booleanValue();
      if (hasLoadedNewCircuit)
        resetViewport();
    }
    
    isReadOnlyMode = window.getBackend().getSession().getClient().getPrivileges() != Privileges.ReadWrite;
    
    if (isReadOnlyMode)
      mouseSynchronizer.disable();
    else
      mouseSynchronizer.enable();
    
    repaint();
  }
  
  private int scaleValueToInt(int value)
  {
    return (int)scaleValue(value);
  }
  
  private float scaleValue(int value)
  {
    return value/scale;
  }
  
  
  private MouseAdapter mouseListener = new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e)
    {
      scaleMouseEvent(e);
      
      boolean usedInput = getCurrentTool().mousePressed(window.getBackend(), e, (int)viewportX, (int)viewportY);
      if (usedInput)
      {
        repaint();
      }
      else if (e.getButton() == MouseEvent.BUTTON2)
      {
        isMovingViewPort = true;
      }
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      scaleMouseEvent(e);
      
      if (isMovingViewPort)
      {
        isMovingViewPort = false;
        viewportX = (int)viewportX; // Remove any half-pixels from the viewport 
        viewportY = (int)viewportY;
      }
      else
        getCurrentTool().mouseReleased(window.getBackend(), e, (int)viewportX, (int)viewportY);
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
      mouseMoved(e, false);
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
      mouseMoved(e, true);
    }
    
    private void mouseMoved(MouseEvent e, boolean wasDragged)
    {
      int oldX = mouseX;
      int oldY = mouseY;
          
      mouseX = e.getX();
      mouseY = e.getY();
      
      if (isMovingViewPort)
      {
        viewportX += scaleValue(oldX - mouseX);
        viewportY += scaleValue(oldY - mouseY);
      }
      else
      {
        scaleMouseEvent(e);
        
        getCurrentTool().mouseMoved(window.getBackend(), e, wasDragged, (int)viewportX, (int)viewportY);
      }
      repaint();
      
      
      mouseSynchronizer.setMousePosition(
          scaleValueToInt(mouseX) + (int)viewportX,
          scaleValueToInt(mouseY) + (int)viewportY);
    }

    private void scaleMouseEvent(MouseEvent e) {
      int xd = (int)scaleValue(e.getX()) - e.getX();
      int yd = (int)scaleValue(e.getY()) - e.getY();
      e.translatePoint(xd, yd);
    }
    
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      if (e.isControlDown())
      {
        if (e.getWheelRotation() < 0)
          zoomIn();
        else
          zoomOut();
      }
    }
  };
  
  private KeyEventDispatcher keyDispatcher = new KeyEventDispatcher()
  {
    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
      boolean usedInput = false;
      if (e.getID() == KeyEvent.KEY_PRESSED)
      {
        usedInput = getCurrentTool().keyPressed(window.getBackend(), e);
      }
      if (e.getID() == KeyEvent.KEY_RELEASED)
      {
        usedInput = getCurrentTool().keyReleased(window.getBackend(), e);
      }
      
      if (usedInput)
      {
        repaint();
      }
      
      return usedInput;
    }
  };
}
