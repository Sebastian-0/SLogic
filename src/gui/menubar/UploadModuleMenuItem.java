/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import gui.backend.ProgramBackend;
import gui.backend.ProgramUI;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import network.User.Privileges;
import snet.internal.Message;
import network.hooks.ModuleUploadRequestHook;
import configuration.Config;
import configuration.Table;

public class UploadModuleMenuItem extends DialogMenuItem implements Observer
{
  public UploadModuleMenuItem(ProgramUI window)
  {
    super(
        Table.get("menubar_upload_module"),
        window,
        new File(System.getProperty("user.dir") + "/" + Config.get(Config.MODULE_FOLDER_PATH)),
        new FileNameExtensionFilter(
            Table.get("file_description_module") + " (*." + Config.MODULE_EXTENSION + ")",
            Config.MODULE_EXTENSION));
    
    window.getBackend().addInterfaceObserver(this);

    setEnabled(window.getBackend().getSession().isClient());
  }
  
  @Override
  public void update(Observable o, Object arg)
  {
    ProgramBackend backend = (ProgramBackend)arg;
    setEnabled(backend.getSession().isClient() && backend.getSession().getClient().getPrivileges() == Privileges.ReadWrite);
  }
  
  @Override
  protected boolean shouldOpenDialog(ProgramUI window)
  {
    return true;
  }
  
  @Override
  protected boolean shouldAddExtensionIfMissing()
  {
    return false;
  }
  
  @Override
  protected int openDialog(ProgramUI window, JFileChooser chooser)
  {
    return chooser.showDialog((Component) window, Table.get("file_chooser_title_upload_module"));
  }

  @Override
  protected void performAction(ProgramUI window, File file) throws FileNotFoundException
  {
    if (!file.exists())
    {
      file = new File(file.getAbsolutePath() + "." + Config.MODULE_EXTENSION);
      if (!file.exists())
        throw new FileNotFoundException(file.getName().substring(0, file.getName().length() - Config.MODULE_EXTENSION.length() - 1));
    }
    
    Message msg = new ModuleUploadRequestHook(null).createMessage(file);
    window.getBackend().getSession().getClient().getNetwork().send(null, msg);
    
    JOptionPane.showMessageDialog(
        (Component) window,
        Table.get("modules_upload_request_message"),
        Table.get("modules_upload_request_title"),
        JOptionPane.INFORMATION_MESSAGE);
  }
}
