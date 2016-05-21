/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;




public class TFlipFlop extends Component
{
  private static final long serialVersionUID = -2259076496232500012L;

  private State currentClockState;
  
  private State toggleState;
  private State currentOut;
  
  private State[] outState;

  
  public TFlipFlop(int id)
  {
    super(id, 2, 2); // Coded as T, CLK : Q, !Q
    init();
  }
  
  private void init()
  {
    currentClockState = State.HIGH;
    toggleState = State.HIGH;
    currentOut = State.HIGH;
    
    outState = new State[2];
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    checkHighState(inputs);
    
    toggleState = inputs[0];
    
    State clock = inputs[1];
    if (clock == State.ON && clock != currentClockState)
    {
      if (toggleState == State.ON)
        currentOut = State.getOpposite(currentOut);
    }
    
    currentClockState = clock;

    outState[0] = currentOut;
    outState[1] = State.getOpposite(currentOut);
    return outState;
  }

  private void checkHighState(State[] inputs) {
    boolean hasValidState = hasValidState(inputs);
    if (currentOut == State.HIGH && hasValidState)
    {
      currentOut = State.OFF;
    }
    else if (currentOut != State.HIGH && !hasValidState)
    {
      currentOut = State.HIGH;
    }
  }

  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    TFlipFlop copy = new TFlipFlop(id);
    copy.currentClockState = currentClockState;
    copy.currentOut = currentOut;
    copy.toggleState = toggleState;
    
    return copy;
  }
}
