/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import gui.backend.ProgramBackend;

import java.util.Timer;
import java.util.TimerTask;

import snet.internal.Message;
import snet.Network;
import network.hooks.SetUserMousePositionHook;

public class MouseSynchronizer
{
  private static final int TIME_BETWEEN_UPDATES = 300;

  
  private Timer timer;
  
  private ProgramBackend backend;
  
  private volatile boolean enabled;
  private volatile boolean hasChanged;
  private volatile int mouseX;
  private volatile int mouseY;
  
  
  public MouseSynchronizer(ProgramBackend backend)
  {
    this.backend = backend;

    timer = new Timer();
    timer.schedule(task, TIME_BETWEEN_UPDATES, TIME_BETWEEN_UPDATES);
  }
  
  
  public void setMousePosition(int x, int y)
  {
    if (x != mouseX || y != mouseY)
    {
      hasChanged = true;
      mouseX = x;
      mouseY = y;
    }
  }
  
  
  public void enable()
  {
    enabled = true;
  }
  
  public void disable()
  {
    enabled = false;
  }
  
  
  public void dispose()
  {
    timer.cancel();
  }
  
  
  private TimerTask task = new TimerTask() {
    
    @Override
    public void run()
    {
      if (enabled && hasChanged)
      {
        Message msg = new SetUserMousePositionHook(null).createMessage(mouseX, mouseY);
        Network clientNetwork = backend.getSession().getClient().getNetwork();
        if (clientNetwork != null)
        {
          clientNetwork.send(null, msg);
          hasChanged = false;
        }
      }
    }
  }; 
}
