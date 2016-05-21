/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package database;

import graphicalcircuit.CircuitType;
import graphicalcircuit.GraphicalCircuit;
import graphicalcircuit.Module;
import graphicalcircuit.types.AndGateType;
import graphicalcircuit.types.ButtonType;
import graphicalcircuit.types.ClockType;
import graphicalcircuit.types.DFlipFlopType;
import graphicalcircuit.types.GndType;
import graphicalcircuit.types.JKFlipFlopType;
import graphicalcircuit.types.LEDType;
import graphicalcircuit.types.ModuleClockInputType;
import graphicalcircuit.types.ModuleInputType;
import graphicalcircuit.types.ModuleOutputType;
import graphicalcircuit.types.ModuleType;
import graphicalcircuit.types.NandGateType;
import graphicalcircuit.types.NorGateType;
import graphicalcircuit.types.NotGateType;
import graphicalcircuit.types.OrGateType;
import graphicalcircuit.types.SRFlipFlopType;
import graphicalcircuit.types.SevenSegmentDisplayType;
import graphicalcircuit.types.TFlipFlopType;
import graphicalcircuit.types.TextLabelType;
import graphicalcircuit.types.VddType;
import graphicalcircuit.types.XnorGateType;
import graphicalcircuit.types.XorGateType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import circuit.Circuit;

public class Database
{
  private Circuit circuit;
  private GraphicalCircuit graphicalCircuit;
  
  private Map<String, ModuleType> moduleTypes;
  private List<CircuitType> circuitTypes;
  
  
  public Database()
  {
    this (new Circuit(), new GraphicalCircuit());
  }
  public Database(Circuit circuit, GraphicalCircuit graphicalCircuit)
  {
    this.circuit = circuit;
    this.graphicalCircuit = graphicalCircuit;
    
    moduleTypes = new TreeMap<String, ModuleType>();
    
    circuitTypes = new ArrayList<CircuitType>();
    
    circuitTypes.add(new ClockType());
    circuitTypes.add(new ButtonType());
    circuitTypes.add(new LEDType());
    circuitTypes.add(new SevenSegmentDisplayType());
    circuitTypes.add(new GndType());
    circuitTypes.add(new VddType());
    circuitTypes.add(new ModuleInputType());
    circuitTypes.add(new ModuleOutputType());
    circuitTypes.add(new ModuleClockInputType());

    circuitTypes.add(new SRFlipFlopType());
    circuitTypes.add(new DFlipFlopType());
    circuitTypes.add(new JKFlipFlopType());
    circuitTypes.add(new TFlipFlopType());
    
    circuitTypes.add(new AndGateType(2));
    circuitTypes.add(new AndGateType(3));
    circuitTypes.add(new AndGateType(4));
    circuitTypes.add(new AndGateType(5));
    circuitTypes.add(new NandGateType(2));
    circuitTypes.add(new NandGateType(3));
    circuitTypes.add(new NandGateType(4));
    circuitTypes.add(new NandGateType(5));
    circuitTypes.add(new OrGateType(2));
    circuitTypes.add(new OrGateType(3));
    circuitTypes.add(new OrGateType(4));
    circuitTypes.add(new OrGateType(5));
    circuitTypes.add(new NorGateType(2));
    circuitTypes.add(new NorGateType(3));
    circuitTypes.add(new NorGateType(4));
    circuitTypes.add(new NorGateType(5));
    circuitTypes.add(new XorGateType(2));
    circuitTypes.add(new XorGateType(3));
    circuitTypes.add(new XorGateType(4));
    circuitTypes.add(new XorGateType(5));
    circuitTypes.add(new XnorGateType(2));
    circuitTypes.add(new XnorGateType(3));
    circuitTypes.add(new XnorGateType(4));
    circuitTypes.add(new XnorGateType(5));
    circuitTypes.add(new NotGateType());
    
    circuitTypes.add(new TextLabelType());
  }

  public Circuit getCircuit()
  {
    return circuit;
  }

  public GraphicalCircuit getGraphicalCircuit()
  {
    return graphicalCircuit;
  }
  
  public ModuleType getModuleType(String name)
  {
    return moduleTypes.get(name);
  }
  
  public Collection<CircuitType> getCircuitTypes()
  {
    ArrayList<CircuitType> types = new ArrayList<CircuitType>(circuitTypes);
    types.addAll(moduleTypes.values());
    return types;
  }
  
  
  public void addModuleType(Module module)
  {
    moduleTypes.put(module.getName(), new ModuleType(module));
  }
  
  public void clear()
  {
    circuit = new Circuit();
    graphicalCircuit = new GraphicalCircuit();
  }
  
  public void clearModuleTypes()
  {
    moduleTypes.clear();
  }
  
  public void saveToFile(DatabaseSaver saver) throws IOException
  {
    saver.save(this);
  }
  
  public void loadFromFile(DatabaseLoader loader) throws ClassNotFoundException, IOException
  {
    Database data = loader.load(getCircuitTypes());
    this.circuit = data.getCircuit();
    this.graphicalCircuit = data.getGraphicalCircuit();
  }
}
