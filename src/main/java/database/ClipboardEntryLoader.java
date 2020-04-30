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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

public class ClipboardEntryLoader
{ 
  private File target;
  
  public ClipboardEntryLoader(File targetPath)
  {
    target = targetPath;
  }
  
  
  public ClipboardEntry load(Collection<CircuitType> circuitTypes) throws IOException, ClassNotFoundException
  {
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(target));
    ClipboardEntry clipboardEntry = (ClipboardEntry)in.readObject();
    clipboardEntry.wasDeserialized(circuitTypes);
    in.close();
    
    return clipboardEntry;
  }
}
