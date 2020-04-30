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



public class NotGate extends Gate
{
  private static final long serialVersionUID = -6175865213376059601L;


  public NotGate(int id)
  {
    super (id, 1);
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    boolean isTrue = true;
    boolean hasValidInput = false;
    for (State state : inputs)
    {
      if (state != State.HIGH)
        hasValidInput = true;
      isTrue = isTrue && (state == State.ON);
    }
    if (!hasValidInput)
      return createOutState(State.HIGH);
    
    if (isTrue)
      return createOutState(State.OFF);
    return createOutState(State.ON);
  }
  
  
  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    return new NotGate(id);
  }
}
