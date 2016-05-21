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
import graphicalcircuit.config.ClockConfig;

import java.util.Timer;
import java.util.TimerTask;

import circuit.CircuitEntity.State;

public class SimulationClock
{
  private SimulationClockListener listener;
  
  private GraphicalComponent clockComponent;
  
  private State state;
  
  public SimulationClock(GraphicalComponent clockComponent, SimulationClockListener listener)
  {
    this.listener = listener;
    this.clockComponent = clockComponent;
    state = State.OFF;
  }
  
  public int toggleState()
  {
    if (state == State.OFF)
    {
      state = State.ON;
      return ((ClockConfig)clockComponent.getConfig()).onTimeMillis;
    }
    else
    {
      state = State.OFF;
      return ((ClockConfig)clockComponent.getConfig()).offTimeMillis;
    }
  }
  
  public State getState()
  {
    return state;
  }
  
  
  public void createTimerTask(final Timer timer)
  {
    Runnable runnable = new Runnable() {
      
      @Override
      public void run()
      {
        TimerTask newTask = new TimerTaskImplementation(this);
        timer.schedule(newTask, toggleState());
        listener.stateChanged(clockComponent.getComponent().getId(), getState());
      }
    };
    
    TimerTask task = new TimerTaskImplementation(runnable);
    timer.schedule(task, ((ClockConfig)clockComponent.getConfig()).initialOffTime);
  }
  
  
  private class TimerTaskImplementation extends TimerTask
  {
    private Runnable runnable;
    
    public TimerTaskImplementation(Runnable r)
    {
      runnable = r;
    }
    
    @Override
    public void run()
    {
      runnable.run();
    }
  }
}
