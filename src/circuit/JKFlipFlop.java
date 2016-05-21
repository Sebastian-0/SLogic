/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;



public class JKFlipFlop extends Component
{
  private static final long serialVersionUID = -225992831232509012L;

  private State currentClockState;
  
  private State nextOut;
  private State currentOut;
  
  private State[] outState;

  
  public JKFlipFlop(int id)
  {
    super(id, 3, 2); // Coded as J, CLK, K : Q, !Q
    init();
  }
  
  private void init()
  {
    currentClockState = State.HIGH;
    nextOut = State.HIGH;
    currentOut = State.HIGH;
    
    outState = new State[2];
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    State j = inputs[0];
    State clock = inputs[1];
    State k = inputs[2];

    boolean hasValidState = hasValidState(inputs);
    if (!hasValidState)
      currentOut = State.HIGH;
    else
    {
      generateNextOutSignal(j, k);
      updateClock(clock);
      
      if (currentOut == State.HIGH)
        currentOut = State.OFF;
    }

    outState[0] = currentOut;
    outState[1] = State.getOpposite(currentOut);
    return outState;
  }

  private void generateNextOutSignal(State j, State k)
  {
    if (j == State.OFF && k == State.OFF)
      nextOut = currentOut;
    else if (j == State.OFF && k == State.ON)
      nextOut = State.OFF;
    else if (j == State.ON && k == State.OFF)
      nextOut = State.ON;
    else if (j == State.ON && k == State.ON)
    {
      if (currentOut == State.ON)
        nextOut = State.OFF;
      else
        nextOut = State.ON;
    }
  }

  private void updateClock(State clock)
  {
    if (clock == State.ON && clock != currentClockState)
    {
      currentOut = nextOut;
    }
    
    currentClockState = clock;
  }
  

  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    JKFlipFlop copy = new JKFlipFlop(id);
    copy.currentClockState = currentClockState;
    copy.currentOut = currentOut;
    copy.nextOut = nextOut;
    
    return copy;
  }
}
