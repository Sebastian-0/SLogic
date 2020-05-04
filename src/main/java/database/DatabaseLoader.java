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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

import circuit.Circuit;

public class DatabaseLoader
{ 
  protected File target;
  
  public DatabaseLoader(File targetPath)
  {
    target = targetPath;
  }
  
  
  public Database load(Collection<CircuitType> circuitTypes) throws IOException, ClassNotFoundException
  {
    try (FileInputStream fis = new FileInputStream(target); ObjectInputStream in = new ObjectInputStream(fis)) {
      Circuit circuit = (Circuit)in.readObject();
      GraphicalCircuit graphicalCircuit = (GraphicalCircuit)in.readObject();
      graphicalCircuit.wasDeserialized(circuit, circuitTypes);
      return new Database(circuit, graphicalCircuit);
    }
  }
}
