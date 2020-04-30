/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;



public class LED extends Component
{
  private static final long serialVersionUID = -3870359505449961131L;

  private static State[] emptyState = new State[0];
  
  private State state;

  
  public LED(int id)
  {
    super (id, 1, 0);
    init();
  }

  private void init()
  {
    state = State.HIGH;
  }
  
  
  @Override
  public void reset()
  {
    super.reset();
    state = State.HIGH;
  }
  
  
  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    state = inputs[0];
    return emptyState;
  }

  public State getState()
  {
    return state;
  }
  
  
  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    LED led = new LED(id);
    led.state = state;
    return led;
  }
}
