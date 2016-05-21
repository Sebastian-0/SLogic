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

public abstract class Gate extends Component
{
  private static final long serialVersionUID = -6303095872263829861L;
  
  private State[] outState;

  
  public Gate(int id, int numPorts)
  {
    super (id, numPorts, 1);
    init(numPorts);
  }

  private void init(int numPorts)
  {
    if (numPorts < 1)
      throw new IllegalArgumentException("Must have at least one ports!");
    outState = new State[1];
  }
  
  protected State[] createOutState(State state)
  {
    outState[0] = state;
    return outState;
  }
}
