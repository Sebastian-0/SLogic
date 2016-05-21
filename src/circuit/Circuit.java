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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class Circuit extends Observable implements Serializable
{
  private static final long serialVersionUID = -2158389747966374698L;
  
  private int nextId;
  
  private List<Component> components;
  private List<Wire> wires;
  
  private transient Simulator simulator;
  
  
  public Circuit()
  {
    components = new ArrayList<Component>();
    wires = new ArrayList<Wire>();
  }
  

  public int nextId()
  {
    return nextId++;
  }
  
  public void generateNewIds()
  {
    generateNewIds(nextId());
  }
  public void generateNewIds(int firstId)
  {
    nextId = firstId;
    
    for (Component component : components)
      component.setId(nextId());
    for (Wire wire : wires)
      wire.setId(nextId());
  }
  
  
  public void addAllEntitiesFrom(Circuit otherCircuit)
  {
    components.addAll(otherCircuit.components);
    wires.addAll(otherCircuit.wires);
    nextId = Math.max(nextId, otherCircuit.nextId);
    informObservers();
  }
  
  public void addComponent(Component component)
  {
    components.add(component);
    nextId = Math.max(nextId, component.getId() + 1);
    informObservers();
  }
  
  public void removeComponent(Component component)
  {
    components.remove(component);
    informObservers();
  }
  
  public void addWire(Wire wire)
  {
    wires.add(wire);
    nextId = Math.max(nextId, wire.getId() + 1);
    informObservers();
  }
  
  public void removeWire(Wire wire)
  {
    wires.remove(wire);
    informObservers();
  }

  private void informObservers()
  {
    setChanged();
    notifyObservers();
  }
  

  public Component getComponent(int id)
  {
    for (Component component : components)
    {
      if (component.getId() == id)
        return component;
    }
    
    return null;
  }
  
  public Wire getWire(int id)
  {
    for (Wire wire : wires)
    {
      if (wire.getId() == id)
        return wire;
    }
    
    return null;
  }
  
  
  public void resetForSimulation()
  {
    for (Wire w : wires)
      w.reset();
    
    for (Component c : components)
    {
      c.clearExternalChangeFlag();
      c.reset();
    }
  }
  
  
  public void simulate()
  {
    if (simulator == null) {
      simulator = new Simulator();
    }
    
    simulator.simulate(components);
  }
  
  
  public Circuit makeCopy()
  {
    Map<Integer, CircuitEntity> circuitParts = new HashMap<Integer, CircuitEntity>();
    copyComponents(circuitParts);
    copyWires(circuitParts);

    return makeNewCircuit(circuitParts);
  }

  private void copyComponents(Map<Integer, CircuitEntity> circuitParts)
  {
    for (Component component : components)
    {
      if (circuitParts.get(component.getId()) == null)
      {
        component.makeCopy(circuitParts);
      }
    }
  }

  private void copyWires(Map<Integer, CircuitEntity> circuitParts)
  {
    for (Wire wire : wires)
    {
      if (circuitParts.get(wire.getId()) == null)
      {
        wire.makeCopy(circuitParts);
      }
    }
  }

  private Circuit makeNewCircuit(Map<Integer, CircuitEntity> circuitParts)
  {
    Circuit newCircuit = new Circuit();
    newCircuit.nextId = nextId;
    for (CircuitEntity entity : circuitParts.values())
    {
      if (entity instanceof Component)
        newCircuit.components.add((Component)entity);
      else
        newCircuit.wires.add((Wire)entity);
    }
    sortAfterId(newCircuit);
    return newCircuit;
  }
  
  private void sortAfterId(Circuit newCircuit)
  {
    Collections.sort(newCircuit.components, new Comparator<Component>() {
      @Override
      public int compare(Component o1, Component o2)
      {
        return o1.getId() - o2.getId();
      }
    });
  }
}
