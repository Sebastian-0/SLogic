/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit.gates;

import circuit.Component;



public class XnorGate extends Gate
{
  private static final long serialVersionUID = -4436540882953179585L;

  
  public XnorGate(int id, int numPorts)
  {
    super (id, numPorts);
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    int onCount = 0;
    boolean hasValidInput = false;
    for (State state : inputs)
    {
      if (state != State.HIGH)
        hasValidInput = true;
      if (state == State.ON)
        onCount += 1;
    }
    if (!hasValidInput)
      return createOutState(State.HIGH);
    
    if ((onCount % 2) != 0)
      return createOutState(State.OFF);
    return createOutState(State.ON);
  }
  
  
  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    return new XnorGate(id, numInputs);
  }
}
