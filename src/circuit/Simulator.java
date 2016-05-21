/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import configuration.Config;
import sutilities.Pool;
import sutilities.Poolable;

public class Simulator {

  private static int maxUpdatesPerComponentPerSimulationStep = -1;

  private Pool<SimulationComponent> componentPool;
  private Pool<SimulationWire> wirePool;

  private Queue<SimulationComponent> componentQueue;
  private Queue<SimulationWire> wireQueue;

  private Set<Component> componentSet;
  private Set<Wire> wireSet;


  public Simulator() {
    componentPool = new Pool<SimulationComponent>(SimulationComponent.class);
    wirePool = new Pool<SimulationWire>(SimulationWire.class);

    componentQueue = new PriorityQueue<SimulationComponent>();
    wireQueue = new PriorityQueue<SimulationWire>();

    componentSet = new HashSet<Component>();
    wireSet = new HashSet<Wire>();
  }


  public void simulate(List<Component> components) {
    componentSet.clear();
    wireSet.clear();

    for (Component c : components)
    {
      if (c.hasExternalChange())
      {
        componentSet.add(c);
        c.clearExternalChangeFlag();
      }
    }
    addComponentsToQueue(componentSet, 0);

    doSimulate();
  }

  private void doSimulate()
  {
    Map<Component, Integer> componentUpdateCount = new HashMap<Component, Integer>();

    while (!componentQueue.isEmpty() || !wireQueue.isEmpty())
    {
      long componentDelay = Integer.MAX_VALUE;
      long wireDelay = Integer.MAX_VALUE;
      if (!wireQueue.isEmpty())
        wireDelay = wireQueue.peek().delay;
      if (!componentQueue.isEmpty())
        componentDelay = componentQueue.peek().delay;

      if (componentDelay < wireDelay) {
        SimulationComponent component = componentQueue.poll();
        simulateComponent(component, componentUpdateCount);
        componentPool.store(component);
      }
      else {
        SimulationWire wire = wireQueue.poll();
        simulateWire(wire);
        wirePool.store(wire);
      }
    }
  }

  private void simulateComponent(SimulationComponent component, Map<Component, Integer> componentUpdateCount)
  {
    increaseUpdateCounter(componentUpdateCount, component.component);
    component.component.update(wireSet);
    addWiresToQueue(wireSet, component.delay);
  }

  private void increaseUpdateCounter(
      Map<Component, Integer> componentUpdateCount, Component c)
  {
    Integer count = componentUpdateCount.get(c);
    if (count == null)
      count = Integer.valueOf(0);
    count = Integer.valueOf(count.intValue() + 1);
    if (count > getUpdateLimit())
      throw new IllegalStateException(Integer.toString(c.getId()));
    componentUpdateCount.put(c, count);
  }

  private int getUpdateLimit()
  {
    if (maxUpdatesPerComponentPerSimulationStep == -1)
      maxUpdatesPerComponentPerSimulationStep = Integer.parseInt(Config.get(Config.MAXIMUM_UPDATES_PER_COMPONENT_PER_SIMULATION_STEP));
    return maxUpdatesPerComponentPerSimulationStep;
  }

  private void simulateWire(SimulationWire wire)
  {
    wire.wire.update(componentSet);
    addComponentsToQueue(componentSet, wire.delay);
  }

  private void addComponentsToQueue(Set<Component> components, long previousDelay) {
    for (Component component : components) {
      SimulationComponent scomponent = componentPool.acquire();
      scomponent.component = component;
      scomponent.delay = previousDelay + 1000 + (long)(Math.random() * 1000);
      componentQueue.add(scomponent);
    }
    components.clear();
  }

  private void addWiresToQueue(Set<Wire> wires, long previousDelay) {
    for (Wire wire : wires) {
      SimulationWire swire = wirePool.acquire();
      swire.wire = wire;
      swire.delay = previousDelay + 1000 + (long)(Math.random() * 1000);
      wireQueue.add(swire);
    }
    wires.clear();
  }


  public static class SimulationComponent implements Poolable, Comparable<SimulationComponent> {
    public Component component;
    public long delay;

    @Override
    public int compareTo(SimulationComponent o) {
      return (int) (delay - o.delay);
    }
  }


  public static class SimulationWire implements Poolable, Comparable<SimulationWire> {
    public Wire wire;
    public long delay;

    @Override
    public int compareTo(SimulationWire o) {
      return (int) (delay - o.delay);
    }
  }
}
