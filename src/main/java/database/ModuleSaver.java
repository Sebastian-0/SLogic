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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ModuleSaver
{
  private File target;
  
  public ModuleSaver(File targetPath)
  {
    target = targetPath;
  }
  
  public void save(Module module) throws IOException
  {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target));
    try {
      out.writeObject(module);
    }
    finally {
      out.close();
    }
  }
}

