/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network;

import graphicalcircuit.CircuitType;
import gui.backend.Tool;

import java.io.File;
import java.io.IOException;

import configuration.Config;
import database.CircuitClipboard;
import database.Database;
import database.DatabaseLoader;
import database.DatabaseSaver;

public class Workspace {
  public Tool tool;
  public CircuitType selectedCircuitType;
  
  public CircuitClipboard clipboard;

  public Database database;
  public File currentSaveLocation;
  
  
  public Workspace() {
    database = new Database();
    clipboard = new CircuitClipboard();
  }

  
  public void createEmptyCircuit()
  {
    database.clear();
    currentSaveLocation = null;
    if (tool != null)
      tool.reset();
  }

  public void loadCircuitFrom(File target) throws IOException, ClassNotFoundException
  {
    currentSaveLocation = target;
    database.loadFromFile(new DatabaseLoader(target));
    if (tool != null)
      tool.reset();
  }

  
  public void saveCircuit() throws IOException
  {
    saveCircuitTo(currentSaveLocation);
  }
  public void saveCircuitTo(File target) throws IOException
  {
    makeBakups(target);
    
    currentSaveLocation = target;
    database.saveToFile(new DatabaseSaver(target));
  }

  private void makeBakups(File target)
  {
    int numberOfBackups = Integer.parseInt(Config.get(Config.BACKUP_SAVE_AMOUNT));
    for (int i = numberOfBackups - 2; i >= 0; i--)
    {
      File fileToRename = new File(target.getAbsolutePath() + ".bkp." + i);
      File fileToRenameTo = new File(target.getAbsolutePath() + ".bkp." + (i+1));
      replace(fileToRename, fileToRenameTo);
    }
    
    File fileToRename = target;
    File fileToRenameTo = new File(target.getAbsolutePath() + ".bkp.0");
    replace(fileToRename, fileToRenameTo);
  }

  private void replace(File newFile, File fileToReplace)
  {
    if (newFile.exists())
    {
      if (fileToReplace.exists())
        fileToReplace.delete();
      newFile.renameTo(fileToReplace);
    }
  }

  
  public boolean hasSaveLocation()
  {
    return currentSaveLocation != null;
  }
}
