/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.types;

import graphicalcircuit.CircuitType;
import circuit.Component;
import circuit.gates.XorGate;

public class XorGateType extends GateType
{
  public XorGateType(int numSlots)
  {
    super(numSlots);
  }
  
  @Override
  protected CircuitType makeCopy(int numSlots) {
    return new XorGateType(numSlots);
  }
  
  @Override
  public Component makeBackendComponent(int id, int numSlots) {
    return new XorGate(id, numSlots);
  }

  @Override
  protected String getName()
  {
    return "Xor";
  }
}
