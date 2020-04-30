/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package database;

import graphicalcircuit.Module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ModuleLoader
{ 
  protected File target;
  
  public ModuleLoader(File targetPath)
  {
    target = targetPath;
  }
  
  
  public Module load() throws IOException, ClassNotFoundException
  {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(target))) {
      return (Module) in.readObject();
    }
  }
}
