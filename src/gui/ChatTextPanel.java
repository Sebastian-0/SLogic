/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import configuration.Config;

public class ChatTextPanel extends JPanel
{
  private Font font;
  private FontMetrics fontMetrics;
  
  private List<ChatEntry> entries;
  
  
  public ChatTextPanel(Font font, int height)
  {
    setPreferredSize(new Dimension(10, height));
    setBackground(Color.WHITE);
    setBorder(new LineBorder(Color.GRAY));
    
    this.font = font;
    createFontMetrics(font);
    
    entries = new ArrayList<ChatEntry>();
    
    addComponentListener(componentListener);
  }

  private void createFontMetrics(Font font)
  {
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    fontMetrics = g.getFontMetrics(font);
    g.dispose();
  }
  
  
  @Override
  protected synchronized void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    
    tryEnableAntialiasing(g);
    for (ChatEntry entry : entries)
      entry.render(g);
  }

  private void tryEnableAntialiasing(Graphics g)
  {
    if (g instanceof Graphics2D)
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
  
  
  public synchronized void addMessage(String text, Color color)
  {
    ChatEntry entry = new ChatEntry(color);
    entry.setText(text);
    
    entry.y = getHeight() - entry.height;
    for (ChatEntry existingEntry : entries)
    {
      existingEntry.y -= entry.height;
    }
    
    entries.add(entry);
  }
  

  private ComponentListener componentListener = new ComponentAdapter() {
    @Override
    public void componentResized(ComponentEvent e)
    {
      wasResized();
    }
  };
  
  private synchronized void wasResized()
  {
    List<ChatEntry> oldEntries = new ArrayList<ChatEntry>(entries);
    entries.clear();
    for (ChatEntry entry : oldEntries)
    {
      addMessage(entry.originalText, entry.color);
    }
    repaint();
    
    Config.put(Config.LAST_CHAT_PANEL_HEIGHT, Integer.toString(getHeight()));
  }
  
  
  private class ChatEntry
  {
    private static final int PADDING = 2;
    
    private int y;
    private int height;
    
    private String originalText;
    private String[] lines;
    
    private Color color;
    
    public ChatEntry(Color color)
    {
      this.color = color;
    }
    
    public void setText(String text)
    {
      this.originalText = text;
      this.lines = lineBreak(text);
      height = lines.length * fontMetrics.getHeight();
    }
    
    private String[] lineBreak(String text)
    {
      List<String> result = new ArrayList<String>();
      
      int index = 0;
      while (text.length() > 0 && index <= text.length())
      {
        String sub = text.substring(0, index);
        if (fontMetrics.stringWidth(sub) > getWidth() - PADDING*2)
        {
          index = Math.max(1, index - 1); // Must have at least one character to avoid infinite loop
          result.add(sub.substring(0, index));
          text = text.substring(index);
          index = 0;
        }
        else
        {
          index += 1;
        }
      }
      if (text.length() > 0)
        result.add(text);
        
      return result.toArray(new String[0]);
    }
    
    
    public void render(Graphics g)
    {
      g.setColor(color);
      g.setFont(font);
      for (int i = 0; i < lines.length; i++)
      {
        String line = lines[i];
        g.drawString(line, PADDING, y + (i+1) * fontMetrics.getHeight() - fontMetrics.getDescent());
      }
    }
  }
}
