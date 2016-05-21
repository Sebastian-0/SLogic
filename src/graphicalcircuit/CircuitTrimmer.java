/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import java.awt.Rectangle;
import java.util.List;

import circuit.Circuit;

public class CircuitTrimmer
{
  public void trimOutside(Rectangle bounds, Circuit circuit, GraphicalCircuit gcircuit,
      List<GraphicalComponent> components, List<GraphicalWire> wires)
  {
    removeComponents(bounds, circuit, gcircuit, components, false);
    splitWiresAlongBounds(bounds, circuit, gcircuit, wires);
    removeWires(bounds, circuit, gcircuit, wires, false);
    optimizeWires(circuit, gcircuit, wires);
  }

  public void trimInside(Rectangle bounds, Circuit circuit, GraphicalCircuit gcircuit,
      List<GraphicalComponent> components, List<GraphicalWire> wires)
  {
    removeComponents(bounds, circuit, gcircuit, components, true);
    splitWiresAlongBounds(bounds, circuit, gcircuit, wires);
    removeWires(bounds, circuit, gcircuit, wires, true);
    optimizeWires(circuit, gcircuit, wires);
  }

  private void removeComponents(Rectangle bounds, Circuit circuit,
      GraphicalCircuit gcircuit, List<GraphicalComponent> components,
      boolean removeInsideBounds)
  {
    for (int i = 0; i < components.size(); i++)
    {
      GraphicalComponent component = components.get(i);
      if (component.intersects(bounds) == removeInsideBounds)
      {
        gcircuit.removeComponent(component);
        component.wasRemoved();
        circuit.removeComponent(component.getComponent());
        i--;
      }
    }
  }

  private void removeWires(Rectangle bounds, Circuit circuit,
      GraphicalCircuit gcircuit, List<GraphicalWire> wires,
      boolean removeInsideBounds)
  {
    for (int i = 0; i < wires.size(); i++)
    {
      GraphicalWire wire = wires.get(i);
      if (wire.intersects(bounds) == removeInsideBounds)
      {
        removeWire(circuit, gcircuit, wire);
        i--;
      }
    }
  }

  private void splitWiresAlongBounds(Rectangle bounds, Circuit circuit,
      GraphicalCircuit gcircuit, List<GraphicalWire> wires)
  {
    bounds.x -= 1;
    bounds.y -= 1;
    bounds.width += 2;
    bounds.height += 2;
    
    for (int i = 0; i < wires.size(); i++)
    {
      GraphicalWire wire = wires.get(i);
      boolean wasRemoved = wire.splitWireAround(circuit, gcircuit, bounds, false);
      if (wasRemoved)
      {
        removeWire(circuit, gcircuit, wire);
        i--;
      }
    }
    
    bounds.x += 1;
    bounds.y += 1;
    bounds.width -= 2;
    bounds.height -= 2;
  }

  private void optimizeWires(Circuit circuit, GraphicalCircuit gcircuit,
      List<GraphicalWire> wires)
  {
    for (int i = 0; i < wires.size(); i++)
    {
      GraphicalWire wire = wires.get(i);
      wire.optimize();
      if (wire.isSingleNode())
      {
        removeWire(circuit, gcircuit, wire);
        i--;
      }
    }
  }

  private void removeWire(Circuit circuit, GraphicalCircuit gcircuit,
      GraphicalWire wire)
  {
    gcircuit.removeWire(wire);
    wire.wasRemoved();
    circuit.removeWire(wire.getWire());
  }
}
