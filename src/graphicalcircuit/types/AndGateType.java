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
import circuit.gates.AndGate;

public class AndGateType extends GateType
{
  public AndGateType(int numSlots)
  {
    super(numSlots);
  }
  
  @Override
  protected CircuitType makeCopy(int numSlots) {
    return new AndGateType(numSlots);
  }
  
  @Override
  public Component makeBackendComponent(int id, int numSlots) {
    return new AndGate(id, numSlots);
  }

  @Override
  protected String getName()
  {
    return "And";
  }
}
