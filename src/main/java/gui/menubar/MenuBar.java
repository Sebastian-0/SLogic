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

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import configuration.Table;

public class MenuBar extends JMenuBar
{
  public MenuBar(ProgramUI window)
  {
    JMenu fileMenu = new JMenu(Table.get("menubar_file"));
    fileMenu.add(new NewMenuItem(window));
    fileMenu.add(new OpenMenuItem(window));
    fileMenu.addSeparator();
    fileMenu.add(new SaveMenuItem(window));
    fileMenu.add(new SaveAsMenuItem(window));
    fileMenu.addSeparator();
    fileMenu.add(new ExitMenuItem(window));
    add(fileMenu);
    
    JMenu toolMenu = new JMenu(Table.get("menubar_tool"));
    toolMenu.add(new CircuitToolMenuItem(window));
    toolMenu.add(new WireToolMenuItem(window));
    toolMenu.add(new MoveToolMenuItem(window));
    toolMenu.add(new CopyToolMenuItem(window));
    toolMenu.add(new DeletionToolMenuItem(window));
    add(toolMenu);
    
    JMenu viewMenu = new JMenu(Table.get("menubar_view"));
    viewMenu.add(new ZoomInMenuItem(window));
    viewMenu.add(new ZoomOutMenuItem(window));
    add(viewMenu);
    
    JMenu networkMenu = new JMenu(Table.get("menubar_network"));
    networkMenu.add(new DisconnectMenuItem(window));
    networkMenu.addSeparator();
    networkMenu.add(new ConnectMenuItem(window));
    add(networkMenu);
    
    JMenu moduleMenu = new JMenu(Table.get("menubar_modules"));
    moduleMenu.add(new CreateModuleMenuItem(window));
    moduleMenu.add(new UploadModuleMenuItem(window));
    add(moduleMenu);

    JMenu simulateMenu = new JMenu(Table.get("menubar_simulation"));
    simulateMenu.add(new SimulateMenuItem(window));
    add(simulateMenu);

    JMenu settingsMenu = new JMenu(Table.get("menubar_settings"));
    settingsMenu.add(new DrawGridMenu(window));
    settingsMenu.add(new DrawMarkersMenu(window));
    settingsMenu.add(new PreferencesMenuItem(window));
    add(settingsMenu);
  }
}
