/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.simulation.backend;

import graphicalcircuit.GraphicalCircuit;
import graphicalcircuit.types.ClockType;

import java.util.Observable;

import circuit.Circuit;
import circuit.CircuitEntity.State;
import circuit.ConstantState;

public class SimulationBackend extends Observable
{
  private SimulationClockTimer timer;
  
  private Circuit circuit;
  private GraphicalCircuit graphicalCircuit;
  
  private SimulationListener listener;
  private boolean hasSimulationFailed;
  
  public SimulationBackend(SimulationListener listener, Circuit circuit, GraphicalCircuit graphicalCircuit)
  {
    this.listener = listener;
    
    this.circuit = circuit;
    this.graphicalCircuit = graphicalCircuit;
  }
  
  
  public void initSimulation()
  {
    circuit.resetForSimulation();
    simulate();
    
    timer = new SimulationClockTimer(graphicalCircuit.getComponents(new ClockType()), clockListener);
  }
  
  
  public void simulate()
  {
    if (!hasSimulationFailed)
    {
      try
      {
        circuit.simulate();
      } catch (IllegalStateException e)
      {
        hasSimulationFailed = true;
        listener.simulationContainsInfiniteLoop();
      }
    }
  }
  
  
  public void dispose()
  {
    timer.terminate();
  }
  

  public Circuit getCircuit()
  {
    return circuit;
  }

  public GraphicalCircuit getGraphicalCircuit()
  {
    return graphicalCircuit;
  }
  
  
  private SimulationClockListener clockListener = new SimulationClockListener() {
    
    @Override
    public void stateChanged(int clockComponentId, State newState)
    {
      ConstantState constant = (ConstantState)circuit.getComponent(clockComponentId);
      constant.setState(newState, 0);
      simulate();
      setChanged();
      notifyObservers();
    }
  };
}
