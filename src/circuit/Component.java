/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

import java.util.Map;
import java.util.Set;

public abstract class Component extends CircuitEntity
{
  private static final long serialVersionUID = 6196004806541841519L;

  private boolean hasChanged;
  
  protected Wire[] out;
  protected State[] in;
  
  
  public Component(int id, int numberOfInSignals, int numberOfOutSignals)
  {
    super(id);
    init(numberOfInSignals, numberOfOutSignals);
  }
  
  private void init(int numberOfInSignals, int numberOfOutSignals)
  {
    out = new Wire[numberOfOutSignals];
    in = new State[numberOfInSignals];
    for (int i = 0; i < in.length; i++)
    {
      in[i] = State.HIGH;
    }
  }
  
  
  public void reset()
  {
    for (int i = 0; i < in.length; i++)
    {
      in[i] = State.HIGH;
    }
  }
  
  
  public void update(Set<Wire> wiresToUpdate)
  {
    State[] outState = generateOutStates(in);
    for (int i = 0; i < out.length; i++)
    {
      if ((out[i] != null) && out[i].setState(outState[i]))
        wiresToUpdate.add(out[i]);
    }
  }

  protected abstract State[] generateOutStates(State[] inputs);
  
  protected boolean hasValidState(State[] states) {
    for (State state : states) {
      if (state != null && state != State.HIGH)
        return true;
    }
    
    return false;
  }
  
  
  public void setOutputWire(Wire wire, int pinIndex)
  {
    out[pinIndex] = wire;
  }
  
  public boolean setState(State newState, int pinIndex)
  {
    if (in[pinIndex] != newState)
    {
      in[pinIndex] = newState;
      return true;
    }
    return false;
  }
  
  public void setChangedExternally()
  {
    hasChanged = true;
  }
  
  public void clearExternalChangeFlag()
  {
    hasChanged = false;
  }
  
  public boolean hasExternalChange()
  {
    return hasChanged;
  }
  
  public int getAmountOfInputs()
  {
    return in.length;
  }
  
  public int getAmountOfOutputs()
  {
    return out.length;
  }
  
  
  @Override
  protected CircuitEntity makeCopy(Map<Integer, CircuitEntity> copiedComponents)
  {
    Component copy = makeCopy(getId(), in.length);
    copiedComponents.put(getId(), copy);
    
    for (int i = 0; i < out.length; i++)
    {
      if (out[i] != null)
      {
        Wire newOut = (Wire)copiedComponents.get(out[i].getId());
        if (newOut == null)
        {
          newOut = (Wire)out[i].makeCopy(copiedComponents);
        }
        
        copy.out[i] = newOut;
      }
    }
    
    for (int i = 0; i < in.length; i++)
    {
      copy.in[i] = in[i];
    }
    
    return copy;
  }
  
  protected abstract Component makeCopy(int id, int numInputs);
}
