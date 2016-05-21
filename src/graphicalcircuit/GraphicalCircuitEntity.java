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
import java.io.Serializable;
import java.util.Collection;

import circuit.Circuit;

public abstract class GraphicalCircuitEntity implements Serializable
{
  private static final long serialVersionUID = -1571169680358465560L;
  
  
  protected abstract void wasDeserialized(Circuit circuit, Collection<CircuitType> types);
  
  protected abstract void updateId();
  
  protected abstract void move(int dx, int dy);
  
  public abstract boolean contains(int x, int y);
  public abstract boolean intersects(Rectangle bounds);
  
  public abstract void wasRemoved();
}
