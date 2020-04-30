/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

public class Gnd extends Component
{
  private static final long serialVersionUID = 2125353210465102941L;
  
  private static State[] outState;
  
  static
  {
    outState = new State[1];
    outState[0] = State.OFF;
  }

  
  public Gnd(int id)
  {
    super(id, 0, 1);
  }
  
  
  @Override
  public void reset()
  {
    super.reset();
    setChangedExternally();
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    return outState;
  }

  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    return new Gnd(id);
  }
}
