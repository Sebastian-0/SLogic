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



public class OrGate extends Gate
{
  private static final long serialVersionUID = -4436540882953179585L;


  public OrGate(int id, int numPorts)
  {
    super (id, numPorts);
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    boolean isTrue = false;
    boolean hasValidInput = false;
    for (State state : inputs)
    {
      if (state != State.HIGH)
        hasValidInput = true;
      isTrue = isTrue || (state == State.ON);
    }
    if (!hasValidInput)
      return createOutState(State.HIGH);
    
    if (isTrue)
      return createOutState(State.ON);
    return createOutState(State.OFF);
  }
  
  
  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    return new OrGate(id, numInputs);
  }
}
