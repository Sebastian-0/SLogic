/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import circuit.Circuit;

public class GraphicalCircuit extends Observable implements Serializable
{
  private static final long serialVersionUID = -3417574915855214940L;
  
  private List<GraphicalComponent> components;
  private List<GraphicalWire> wires;
  
  
  public GraphicalCircuit()
  {
    components = new ArrayList<GraphicalComponent>();
    wires = new ArrayList<GraphicalWire>();
  }
  
  
  public void wasDeserialized(Circuit backendCircuit, Collection<CircuitType> types)
  {
    for (GraphicalComponent component : components)
    {
      component.wasDeserialized(backendCircuit, types);
    }
    for (GraphicalWire wire : wires)
    {
      wire.wasDeserialized(backendCircuit, types);
    }
  }
  
  public void generateNewIds(Circuit backendCircuit)
  {
    generateNewIds(backendCircuit, backendCircuit.nextId());
  }
  public void generateNewIds(Circuit backendCircuit, int firstId)
  {
    backendCircuit.generateNewIds(firstId);

    for (GraphicalComponent component : components)
      component.updateId();
    for (GraphicalWire wire : wires)
      wire.updateId();
  }


  public synchronized void addAllEntitiesFrom(GraphicalCircuit otherCircuit)
  {
    components.addAll(otherCircuit.components);
    wires.addAll(otherCircuit.wires);
    informObservers();
  }
  
  public synchronized void addComponent(GraphicalComponent component)
  {
    components.add(component);
    informObservers();
  }
  
  public synchronized void removeComponent(GraphicalComponent component)
  {
    if (components.remove(component))
    {
      informObservers();
    }
  }
  
  public synchronized void addWire(GraphicalWire wire)
  {
    wires.add(wire);
    informObservers();
  }
  
  public synchronized void removeWire(GraphicalWire wire)
  {
    if (wires.remove(wire))
      informObservers();
  }

  private void informObservers()
  {
    setChanged();
    notifyObservers();
  }
  
  public synchronized void removeComponentsOutside(Rectangle bounds, Circuit circuit)
  {
    new CircuitTrimmer().trimOutside(bounds, circuit, this, components, wires);
    notifyObservers();
  }
  
  public synchronized void removeComponentsInside(Rectangle bounds, Circuit circuit)
  {
    new CircuitTrimmer().trimInside(bounds, circuit, this, components, wires);
    notifyObservers();
  }
  
  public synchronized void moveComponents(int dx, int dy)
  {
    for (GraphicalComponent component : components)
      component.move(dx, dy);
    for (GraphicalWire wire : wires)
      wire.move(dx, dy);
  }
  
  
  public synchronized GraphicalComponent getComponent(int id)
  {
    for (GraphicalComponent component : components)
    {
      if (component.getComponent().getId() == id)
        return component;
    }
    
    return null;
  }
  
  public synchronized GraphicalWire getWire(int id)
  {
    for (GraphicalWire wire : wires)
    {
      if (wire.getWire().getId() == id)
        return wire;
    }
    
    return null;
  }
  
  public synchronized GraphicalWire getWireAt(int x, int y)
  {
    for (GraphicalWire wire : wires)
    {
      if (wire.contains(x, y))
        return wire;
    }
    
    return null;
  }
  
  public synchronized GraphicalComponent getComponentAt(int x, int y)
  {
    for (GraphicalComponent component : components)
    {
      if (component.contains(x, y))
        return component;
    }
    
    return null;
  }
  
  public synchronized GraphicalComponent getComponentWithPinAt(int x, int y)
  {
    for (GraphicalComponent component : components)
    {
      if (component.getPinAt(x, y) != null)
        return component;
    }
    
    return null;
  }
  
  public List<GraphicalComponent> getComponents() {
    return components; 
  }
  
  public List<GraphicalComponent> getComponents(CircuitType... types)
  {
    List<GraphicalComponent> matches = new ArrayList<GraphicalComponent>();
    for (GraphicalComponent component : components) {
      for (CircuitType type : types) {
        if (component.getType().equals(type))
          matches.add(component);
      }
    }
    return matches;
  }
  
  public boolean isSufficientlyConnected()
  {
    boolean isFullyConnected = true;
    for (GraphicalComponent c : components)
    {
      if (!c.isSufficientlyConnected())
      {
        isFullyConnected = false;
        break;
      }
    }
    for (GraphicalWire w : wires)
    {
      if (!w.isFullyConnected())
      {
        isFullyConnected = false;
        break;
      }
    }
    return isFullyConnected;
  }
  
  public List<SimulationInputListener> createSimulationListeners() 
  {
    List<SimulationInputListener> listeners = new ArrayList<SimulationInputListener>();
    for (GraphicalComponent component : components) {
      SimulationInputListener listener = component.createSimulationListener();
      if (listener != null) {
        listeners.add(listener);
      }
    }
    return listeners;
  }
  
  
  public synchronized void render(Graphics2D g2d)
  {
    for (GraphicalComponent c : components)
    {
      c.render(g2d);
    }
    
    for (GraphicalWire w : wires)
    {
      w.render(g2d);
    }
  }
  
  
  
  public GraphicalCircuit makeCopy(Circuit backend)
  {
    // TODO GraphicalCircuit; Introducera ett copy-objekt som kan ha metoder för makeCopy som returnerar en kopia av en komponent med en viss id
    Map<Integer, Object> circuitParts = new HashMap<Integer, Object>();
    copyComponents(backend, circuitParts);
    copyWires(backend, circuitParts);
    
    return makeNewCircuit(circuitParts);
  }

  private void copyComponents(Circuit backend, Map<Integer, Object> circuitParts)
  {
    for (GraphicalComponent component : components)
    {
      if (circuitParts.get(component.getComponent().getId()) == null)
      {
        component.makeCopy(backend, circuitParts);
      }
    }
  }

  private void copyWires(Circuit backend, Map<Integer, Object> circuitParts)
  {
    for (GraphicalWire wire : wires)
    {
      if (circuitParts.get(wire.getWire().getId()) == null)
      {
        wire.makeCopy(backend, circuitParts);
      }
    }
  }

  private GraphicalCircuit makeNewCircuit(Map<Integer, Object> circuitParts)
  {
    GraphicalCircuit newCircuit = new GraphicalCircuit();    
    for (Object entity : circuitParts.values())
    {
      if (entity instanceof GraphicalComponent)
        newCircuit.components.add((GraphicalComponent)entity);
      else
        newCircuit.wires.add((GraphicalWire)entity);
    }
    sortAfterId(newCircuit);
    return newCircuit;
  }
  
  private void sortAfterId(GraphicalCircuit newCircuit)
  {
    Collections.sort(newCircuit.components, new Comparator<GraphicalComponent>() {
      @Override
      public int compare(GraphicalComponent o1, GraphicalComponent o2)
      {
        return o1.getComponent().getId() - o2.getComponent().getId();
      }
    });
  }
}
