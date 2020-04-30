/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntegratedModuleCircuit implements Serializable
{
  private static final long serialVersionUID = -8755570701486720509L;

  public Map<Integer, CircuitEntity> components;
  
  public List<Wire> inWires;
  public List<Wire> outWires;
  
  public IntegratedModuleCircuit()
  {
    this (new ArrayList<Wire>(), new ArrayList<Wire>(), new HashMap<Integer, CircuitEntity>());
  }
  
  /**
   * Creates an integrated module circuit, containing the specified lists
   *  of wires. The input wires must correspond to the module input pins, i.e.
   *  the first wire corresponds to input pin 1, the second to pin 2, etc...
   *  The same goes for the output pins. The input wires should be connected to
   *  the integrated circuit as inputs and the output wires should be connected
   *  to the outputs of the module.
   * @param inWires The input wires, corresponding to the input pins of the module
   * @param outWires The output wires, corresponding to the output pins of the module
   * @param components A map containing all the components in the integrated module circuit
   */
  public IntegratedModuleCircuit(List<Wire> inWires, List<Wire> outWires, Map<Integer, CircuitEntity> components)
  {
    this.inWires = new ArrayList<Wire>(inWires);
    this.outWires = new ArrayList<Wire>(outWires);
    
    this.components = new HashMap<Integer, CircuitEntity>(components);
  }
  
  
  public IntegratedModuleCircuit makeCopy()
  {
    IntegratedModuleCircuit newModule = new IntegratedModuleCircuit();
    
    for (Wire wire : inWires)
    {
      Wire copy = createWireCopyIfMissing(newModule.components, wire);
      newModule.inWires.add(copy);
    }
    
    for (Wire wire : outWires)
    {
      Wire copy = createWireCopyIfMissing(newModule.components, wire);
      newModule.outWires.add(copy);
    }
    
    return newModule;
  }

  private Wire createWireCopyIfMissing(Map<Integer, CircuitEntity> copiedComponents, Wire wire)
  {
    Wire copy = (Wire)copiedComponents.get(wire.getId());
    if (copy == null)
    {
      copy = (Wire)wire.makeCopy(copiedComponents);
    }
    return copy;
  }
}
