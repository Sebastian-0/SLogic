/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;


public class SRFlipFlop extends Component
{
  private static final long serialVersionUID = -2481657236949012L;

  private State[] outState;

  
  public SRFlipFlop(int id)
  {
    super(id, 2, 2); // Coded as S, R : Q, !Q
    init();
  }
  
  private void init()
  {
    outState = new State[2];
    outState[0] = State.HIGH;
    outState[1] = State.HIGH;
  }
  

  @Override
  protected State[] generateOutStates(State[] inputs)
  {
    State s = inputs[0];
    State r = inputs[1];

    boolean areAllInputsHighState = s == State.HIGH && r == State.HIGH;
    if (areAllInputsHighState) {
      outState[0] = State.HIGH;
      outState[1] = State.HIGH;
    }
    else
    {
      generateNextOutSignal(s, r);
      
      if (outState[0] == State.HIGH || outState[1] == State.HIGH) {
        outState[0] = State.ON;
        outState[1] = State.OFF;
      }
    }
    
    return outState;
  }

  private void generateNextOutSignal(State s, State r)
  {
    if (s == State.OFF && r == State.ON) {
      outState[0] = State.OFF;
      outState[1] = State.ON;
    }
    else if (s == State.ON && r == State.OFF) {
      outState[0] = State.ON;
      outState[1] = State.OFF;
    }
    else if (s == State.ON && r == State.ON) {
      outState[0] = State.OFF;
      outState[1] = State.OFF;
    }
  }
  

  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    SRFlipFlop copy = new SRFlipFlop(id);
    return copy;
  }
}
