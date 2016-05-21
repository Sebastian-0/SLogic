/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package database;

import graphicalcircuit.CircuitType;
import graphicalcircuit.GraphicalCircuit;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Collection;

import circuit.Circuit;

public class ClipboardEntry implements Serializable
{
  private static final long serialVersionUID = -9177276308324333128L;
  
  private String id;

  private Point location;
  
  private GraphicalCircuit gcircuit;
  private Circuit          circuit;
  
  private ClipboardEntry(Point location, Circuit circuit, GraphicalCircuit gcircuit)
  {
    this.location = location;
    this.gcircuit = gcircuit;
    this.circuit = circuit;
  }
  
  public void wasDeserialized(Collection<CircuitType> circuitTypes)
  {
    gcircuit.wasDeserialized(circuit, circuitTypes);
  }
  
  
  public static ClipboardEntry createClipboardEntry(Circuit circuit, GraphicalCircuit gcircuit,
      Point selectionStart, Point selectionEnd)
  {
    Circuit cc = circuit.makeCopy();
    GraphicalCircuit gc = gcircuit.makeCopy(cc);
    
    gc.generateNewIds(cc);
    
    gc.removeComponentsOutside(
        new Rectangle(
            selectionStart.x,
            selectionStart.y,
            selectionEnd.x - selectionStart.x,
            selectionEnd.y - selectionStart.y),
        cc);
    
    return new ClipboardEntry(selectionEnd, cc, gc);
  }
  
  
  public void pasteCopy(Circuit circuit, GraphicalCircuit gcircuit, Point destination)
  {
    Circuit cc = this.circuit.makeCopy();
    GraphicalCircuit gc = this.gcircuit.makeCopy(cc);
    
    gc.moveComponents(destination.x - location.x, destination.y - location.y);
    
    int lowestId = circuit.nextId();
    gc.generateNewIds(cc, lowestId);

    circuit.addAllEntitiesFrom(cc);
    gcircuit.addAllEntitiesFrom(gc);
  }
  
  
  protected void setId(String id)
  {
    this.id = id;
  }
  
  public String getId()
  {
    return id;
  }
}
