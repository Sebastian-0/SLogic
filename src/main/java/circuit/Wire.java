/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Wire extends CircuitEntity
{
  private static final long serialVersionUID = 1686052121118807945L;
  
  private State state;
  private List<TargetComponent> targets;
  
  
  public Wire(int id)
  {
    super(id);
    init();
  }
  
  private void init()
  {
    state = State.HIGH;
    targets = new ArrayList<TargetComponent>();
  }
  
  
  public void reset()
  {
    state = State.HIGH;
  }
  
  
  public void update(Set<Component> componentsToUpdate)
  {
    for (TargetComponent target : targets)
    {
      if (target.target.setState(state, target.pin))
        componentsToUpdate.add(target.target);
    }
  }
  
  
  public boolean setState(State newState)
  {
    if (state != newState)
    {
      state = newState;
      return true;
    }
    return false;
  }
  
  public void addTarget(Component target, int pinIndex)
  {
    TargetComponent component = new TargetComponent(target, pinIndex);
    if (!targets.contains(component))
      targets.add(component);
  }
  
  public void removeTarget(Component target, int pinIndex)
  {
    targets.remove(new TargetComponent(target, pinIndex));
  }

  public void removeAllTargets()
  {
    targets.clear();
  }
  
  
  public void mergeWith(Wire otherWire)
  {
    for (TargetComponent target : otherWire.targets)
    {
      targets.add(target);
    }
  }
  
  
  public State getState()
  {
    return state;
  }
  
  
  @Override
  protected CircuitEntity makeCopy(Map<Integer,CircuitEntity> copiedComponents)
  {
    Wire newWire = new Wire(getId());
    newWire.state = state;
    
    copiedComponents.put(newWire.getId(), newWire);
    
    for (TargetComponent target : targets)
    {
      Component newTarget = (Component)copiedComponents.get(target.target.getId());
      if (newTarget == null)
      {
        newTarget = (Component)target.target.makeCopy(copiedComponents);
      }
      
      newWire.addTarget(newTarget, target.pin);
    }
    
    return newWire;
  }
  
  
  
  private static class TargetComponent implements Serializable
  {
    private static final long serialVersionUID = -2513867266276393664L;
    
    public Component target;
    public int pin;
    
    public TargetComponent(Component target, int pin)
    {
      this.target = target;
      this.pin = pin;
    }
    
    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof TargetComponent)
      {
        TargetComponent other = ((TargetComponent) obj);
        if (pin == other.pin && target.equals(other.target))
          return true;
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(pin, target);
    }
  }
}
