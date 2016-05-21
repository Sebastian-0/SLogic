/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import gui.backend.ProgramUI;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import configuration.Config;
import configuration.Table;

public abstract class DialogMenuItem extends AbstractMenuItem
{
  private JFileChooser fileChooser;
  private String extension;
  
  protected DialogMenuItem(String name, ProgramUI window)
  {
    this (
        name,
        window,
        new File(System.getProperty("user.dir") + "/" + Config.get(Config.CIRCUIT_FOLDER_PATH)),
        new FileNameExtensionFilter(
            Table.get("file_description_circuit") + " (*." + Config.CIRCUIT_EXTENSION + ")",
            Config.CIRCUIT_EXTENSION));
  }
  
  protected DialogMenuItem(String name, ProgramUI window, File defaultFolder, FileNameExtensionFilter chooserFilter)
  {
    super (name, window);
    
    extension = chooserFilter.getExtensions()[0];
    
    defaultFolder.mkdirs();
    fileChooser = new JFileChooser(defaultFolder);
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(chooserFilter);
    fileChooser.setAcceptAllFileFilterUsed(true); // Put the 'all files'-filter at the bottom of the list
  }
  

  protected abstract boolean shouldOpenDialog(ProgramUI window);
  protected abstract int openDialog(ProgramUI window, JFileChooser chooser);
  protected abstract void performAction(ProgramUI window, File file) throws FileNotFoundException;
  protected abstract boolean shouldAddExtensionIfMissing();
  
  @Override
  protected void doAction(ProgramUI program)
  {
    File target = program.getBackend().getSession().getClient().getWorkspace().currentSaveLocation;
    int result = JFileChooser.APPROVE_OPTION;
    if (shouldOpenDialog(program))
    {
      result = openDialog(program, fileChooser);
      if (result == JFileChooser.APPROVE_OPTION)
      {
        target = fileChooser.getSelectedFile();
        if (shouldAddExtensionIfMissing() && !target.getName().toLowerCase().endsWith("." + extension))
          target = new File(target.getAbsolutePath() + "." + extension);
      }
    }
    
    if (result == JFileChooser.APPROVE_OPTION)
    {
      try
      {
        performAction(program, target);
      }
      catch (FileNotFoundException e)
      {
        JOptionPane.showMessageDialog(
            (Component) program,
            Table.get("error_message_could_not_find_file") + ": " + e.getMessage(),
            Table.get("popup_title_error"),
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
