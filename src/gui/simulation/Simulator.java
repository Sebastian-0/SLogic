/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.simulation;

import graphicalcircuit.GraphicalCircuit;
import gui.simulation.backend.SimulationBackend;
import gui.simulation.backend.SimulationListener;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import circuit.Circuit;
import configuration.Table;

public class Simulator extends JDialog
{
  private SimulationBackend backend;
  private SimulationPane simulationPane;
  
  
  public Simulator(JFrame parent, Circuit circuit, GraphicalCircuit graphicalCircuit)
  {
    super (parent);
    setTitle(Table.get("window_simulation_title"));
    
    backend = new SimulationBackend(simulationListener, circuit, graphicalCircuit);
    simulationPane = new SimulationPane(backend, graphicalCircuit.createSimulationListeners());
    
    add(simulationPane);
    
    pack();
    
    setLocationRelativeTo(parent);
    
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
    
    backend.initSimulation();
  }
  
  @Override
  public void dispose() {
    super.dispose();
    backend.dispose();
  }
  
  
  private SimulationListener simulationListener = new SimulationListener() {
    
    @Override
    public void simulationContainsInfiniteLoop()
    {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run()
        {
          JOptionPane.showMessageDialog(
              Simulator.this, 
              Table.get("simulation_failed_infinite_loop_message"),
              Table.get("simulation_failed_infinite_loop_title"),
              JOptionPane.ERROR_MESSAGE);
        }
      });
    }
  };
}
