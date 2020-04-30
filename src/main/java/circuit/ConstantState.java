/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;


public class ConstantState extends Component
{
  private static final long serialVersionUID = 6868769756989236187L;
  
  private State initialState;
  private State[] state;

  
  public ConstantState(int id, State initialState)
  {
    super (id, 0, 1);
    init(initialState);
  }

  private void init(State initialState)
  {
    this.initialState = initialState;
    state = new State[1];
    state[0] = initialState;
  }
  
  
  @Override
  public void reset()
  {
    super.reset();
    state[0] = initialState;
    setChangedExternally();
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    return state;
  }
  
  @Override
  public boolean setState(State state, int pin)
  {
    if (this.state[0] != state)
    {
      this.state[0] = state;
      setChangedExternally();
      return true;
    }
    return false;
  }

  public State getState()
  {
    return state[0];
  }
  
  
  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    ConstantState copy = new ConstantState(id, initialState);
    copy.state = new State[1];
    copy.state[0] = state[0];
    return copy;
  }
}
