/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.simulation.backend;

import graphicalcircuit.GraphicalComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class SimulationClockTimer
{
  private Timer timer;

  public SimulationClockTimer(List<GraphicalComponent> clockComponents, SimulationClockListener listener)
  {
    timer = new Timer();
    
    for (GraphicalComponent clockComponent : clockComponents)
    {
      SimulationClock clock = new SimulationClock(clockComponent, listener);
      clock.createTimerTask(timer);
    }
  }
  
  public void terminate()
  {
    timer.cancel();
  }
}
