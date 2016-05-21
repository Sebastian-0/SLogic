/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IntegratedModule extends Component
{
  private static final long serialVersionUID = -6795269631729570393L;
  
  private IntegratedModuleCircuit circuit;
  private FakeComponent fakeComponent;

  
  public IntegratedModule(int id, IntegratedModuleCircuit circuit)
  {
    super(id, circuit.inWires.size(), circuit.outWires.size());
    this.circuit = circuit;
    
    fakeComponent = new FakeComponent(this, circuit.outWires.size());
    for (int i = 0; i < circuit.outWires.size(); i++)
    {
      Wire out = circuit.outWires.get(i);
      out.addTarget(fakeComponent, i);
    }
  }
  
  @Override
  public void update(Set<Wire> wiresToUpdate)
  {
    for (int i = 0; i < in.length; i++)
    {
      if (circuit.inWires.get(i).setState(in[i]))
        wiresToUpdate.add(circuit.inWires.get(i));
    }
  }

  @Override
  protected State[] generateOutStates(State[] inputs) { return null; }

  @Override
  protected Component makeCopy(int id, int numInputs)
  {
    IntegratedModule newModule = new IntegratedModule(
        id,
        circuit.makeCopy());
    
    newModule.fakeComponent = (FakeComponent)newModule.circuit.components.remove(-10);
    newModule.fakeComponent.connectedModule = newModule;

    return newModule;
  }
  
  
  private static class FakeComponent extends Component
  {
    private static final long serialVersionUID = -6654383311812509218L;
    private static final int ID = -10;
    
    private List<Integer> pinsToUpdate;
    
    private IntegratedModule connectedModule;
    
    
    public FakeComponent(IntegratedModule m, int numberOfInSignals)
    {
      super(ID, numberOfInSignals, 0);
      
      this.connectedModule = m;
      
      pinsToUpdate = new ArrayList<Integer>();
    }

    @Override
    protected Component makeCopy(int id, int numInputs)
    {
      return new FakeComponent(connectedModule, getAmountOfInputs());
    }
    
    
    @Override
    public boolean setState(State newState, int pinIndex)
    {
      if (super.setState(newState, pinIndex))
      {
        pinsToUpdate.add(pinIndex);
        return true;
      }
      return false;
    }
    
    @Override
    public void update(Set<Wire> wiresToUpdate)
    {
      for (Integer i : pinsToUpdate)
      {
        if (connectedModule.out[i] != null)
        {
          if (connectedModule.out[i].setState(FakeComponent.this.in[i]))
            wiresToUpdate.add(connectedModule.out[i]);
        }
      }
    }

    @Override
    protected State[] generateOutStates(State[] inputs) { return null; }
  }
}
