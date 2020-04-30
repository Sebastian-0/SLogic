/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;




public class SevenSegmentDisplay extends Component
{
  private static final long serialVersionUID = 1533501242623624201L;

  private static State[] emptyState = new State[0];
  
  private State[] state;
  
  
  public SevenSegmentDisplay(int id)
  {
    super (id, 8, 0);
    init();
  }

  private void init()
  {
    state = new State[8];
    for (int i = 0; i < state.length; i++)
    {
      state[i] = State.HIGH;
    }
  }
  
  
  @Override
  public void reset()
  {
    super.reset();
    for (int i = 0; i < state.length; i++)
    {
      state[i] = State.HIGH;
    }
  }
  
  
  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    for (int i = 0; i < state.length; i++)
    {
      state[i] = inputs[i];
    }
    return emptyState;
  }

  public State getState(int index)
  {
    return state[index];
  }
  
  
  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    SevenSegmentDisplay led = new SevenSegmentDisplay(id);
    System.arraycopy(state, 0, led.state, 0, state.length);
    return led;
  }
}
