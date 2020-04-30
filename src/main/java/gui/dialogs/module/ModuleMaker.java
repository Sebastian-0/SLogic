/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.module;

import graphicalcircuit.GraphicalCircuit;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.Module;
import graphicalcircuit.ModuleDefinition;
import graphicalcircuit.ModuleDefinition.Connection;
import graphicalcircuit.config.NameConfig;
import graphicalcircuit.types.ModuleClockInputType;
import graphicalcircuit.types.ModuleInputType;
import graphicalcircuit.types.ModuleOutputType;

import java.util.ArrayList;
import java.util.List;

import circuit.Circuit;
import circuit.IntegratedModuleCircuit;
import circuit.Wire;

public class ModuleMaker
{
  private Circuit circuit;
  private GraphicalCircuit gcircuit;
  
  public ModuleMaker(Circuit circuit, GraphicalCircuit gcircuit)
  {
    this.circuit = circuit;
    this.gcircuit = gcircuit;
  }
  
  
  public boolean canMakeModule()
  {
    Circuit ccircuit = circuit.makeCopy();
    GraphicalCircuit cgcircuit = gcircuit.makeCopy(ccircuit);
    
    removeUnsupportedComponents(cgcircuit, ccircuit);
    
    if (cgcircuit.isSufficientlyConnected())
    {
      boolean hasInput = !getAllModuleInputs(cgcircuit).isEmpty();
      boolean hasOutput = !getModuleOutputs(cgcircuit).isEmpty();
      
      return hasInput && hasOutput;
    }
    
    return false;
  }

  private void removeUnsupportedComponents(GraphicalCircuit cgcircuit, Circuit ccircuit)
  {
    List<GraphicalComponent> componentsToRemove = new ArrayList<GraphicalComponent>();
    for (GraphicalComponent c : cgcircuit.getComponents())
    {
      if (!c.canBePartOfModule())
        componentsToRemove.add(c);
    }
    
    for (GraphicalComponent c : componentsToRemove)
    {
      cgcircuit.removeComponent(c);
      c.wasRemoved();
      ccircuit.removeComponent(c.getComponent());
    }
  }

  
  
  
  public ModuleDefinition createDefaultDefinition(int gridSize)
  {
    ModuleDefinition table = new ModuleDefinition();
    
    table.name = "Circuit";

    for (GraphicalComponent c : getModuleInputs(gcircuit))
      table.inputs.add(new Connection(Connection.INPUT, ((NameConfig)c.getConfig()).name));
    for (GraphicalComponent c : getModuleClockInputs(gcircuit))
      table.inputs.add(new Connection(Connection.CLOCK, ((NameConfig)c.getConfig()).name));
    for (GraphicalComponent c : getModuleOutputs(gcircuit))
      table.outputs.add(new Connection(Connection.OUTPUT, ((NameConfig)c.getConfig()).name));
    
    table.calculateMinimimSize(gridSize);
    table.width *= 3;
    
    return table;
  }

  
  
  public Module createModule(ModuleDefinition definition)
  {
    IntegratedModuleCircuit imc = new IntegratedModuleCircuit();

    for (GraphicalComponent c : getAllModuleInputs(gcircuit))
    {
      imc.inWires.add(c.getOutput(0).getWire());
    }
    for (GraphicalComponent c : getModuleOutputs(gcircuit))
    {
      Wire outWire = c.getInput(0).getWire();
      outWire.removeTarget(c.getComponent(), 0);
      imc.outWires.add(outWire);
    }
    
    Module m = new Module(definition, imc);
    return m;
  }


  private List<GraphicalComponent> getAllModuleInputs(GraphicalCircuit gcircuit) {
    List<GraphicalComponent> moduleInputs = new ArrayList<GraphicalComponent>();
    moduleInputs.addAll(getModuleInputs(gcircuit));
    moduleInputs.addAll(getModuleClockInputs(gcircuit));
    return moduleInputs;
  }

  private List<GraphicalComponent> getModuleInputs(GraphicalCircuit gcircuit) {
    return gcircuit.getComponents(new ModuleInputType());
  }

  private List<GraphicalComponent> getModuleClockInputs(GraphicalCircuit gcircuit) {
    return gcircuit.getComponents(new ModuleClockInputType());
  }

  private List<GraphicalComponent> getModuleOutputs(GraphicalCircuit gcircuit) {
    return gcircuit.getComponents(new ModuleOutputType());
  }
}
