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
import circuit.gates.NandGate;

public class NandGateType extends GateType
{
  public NandGateType(int numSlots)
  {
    super(numSlots);
  }
  
  @Override
  protected CircuitType makeCopy(int numSlots) {
    return new NandGateType(numSlots);
  }
  
  @Override
  public Component makeBackendComponent(int id, int numSlots) {
    return new NandGate(id, numSlots);
  }

  @Override
  protected String getName()
  {
    return "Nand";
  }
}
