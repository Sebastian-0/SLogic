/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import graphicalcircuit.GraphicalCircuit;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.types.ButtonType;
import graphicalcircuit.types.ClockType;
import graphicalcircuit.types.LEDType;
import graphicalcircuit.types.ModuleClockInputType;
import graphicalcircuit.types.ModuleInputType;
import graphicalcircuit.types.ModuleOutputType;
import graphicalcircuit.types.TextLabelType;
import gui.backend.ChatMessage;
import gui.backend.ProgramBackend;
import gui.backend.ProgramUI;
import gui.backend.ProgramWindow;
import gui.backend.tools.ToolFactory;
import gui.dialogs.config.ConfigDialog;
import gui.dialogs.config.NameDialog;
import gui.dialogs.config.button.ButtonDialog;
import gui.dialogs.config.clocks.ClockDialog;
import gui.dialogs.config.led.LEDColorDialog;
import gui.menubar.MenuBar;
import gui.simulation.Simulator;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import network.Session;
import snet.internal.Message;
import sutilities.Debugger;
import util.TextureStorage;
import circuit.Circuit;
import configuration.Config;
import configuration.Table;
import database.Database;

public class LogicSim extends JFrame implements ProgramUI
{
  private static final long serialVersionUID = -5866798050773414601L;
  
  private ProgramBackend backend;
  
  private ConstructionPanel constructionPanel;


  public LogicSim()
  {
    initBackend();
    initGui();

    backend.getSession().startLocalServer();
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
    backend.refreshChat();
  }

  private void initBackend()
  {
    initLanguages();
    
    backend = new ProgramBackend(new ProgramWindowInterface());
    backend.getSession().getClient().getWorkspace().tool = ToolFactory.getTool(ToolFactory.Type.PLACE_CIRCUIT);
  }

  private void initLanguages() {
    try
    {
      Table.loadLanguages(new File(Config.get(Config.LANGUAGE_FOLDER_PATH)), Config.STRING_TABLE_EXTENSION);
      
      Table table = Table.getLanguage(Config.get(Config.LANGUAGE));
      if (table == null)
      {
        for (Table stringTable : Table.getLanguages())
        {
          String tableLocale = stringTable.getTableString("java_locale_language") + "_" + stringTable.getTableString("java_locale_country");
          
          if (tableLocale.equalsIgnoreCase(Locale.getDefault().toString().substring(0, 5)))
          {
            table = stringTable;
            break;
          }
        }
      }
      
      if (table == null)
        table = Table.getLanguage("English");
      
      if (table == null && !Table.getLanguages().isEmpty())
        table = Table.getLanguages().get(0);
      
      if (table != null)
      {
        Config.put(Config.LANGUAGE, table.getName());
        Table.setLanguage(table);
      }
      else
        JOptionPane.showMessageDialog(
            null,
            "Failed to load language files, no languages are installed!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    catch (IllegalArgumentException e)
    {
      JOptionPane.showMessageDialog(
          null,
          "Failed to load language files, their folder doesn't exist!",
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void initGui()
  {
    setTitle(Table.get("window_title"));
    loadProgramIcon();
    
    setJMenuBar(new MenuBar(this));
    
    constructionPanel = new ConstructionPanel(this);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new CircuitTypePanel(this), constructionPanel);
    split.setOneTouchExpandable(true);
    add(split);
    
    pack(); // Pack two times to set minimum size before resizing to the preferred size
    setMinimumSize(getSize());
    setPreferredSize(getPreviousSize());
    setExtendedState(getPreviousExtendedState());
    pack();
    
    addWindowListener(windowListener);
    addWindowStateListener(windowListener);
    addComponentListener(componentListener);
    
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setVisible(true);
  }

  private Dimension getPreviousSize()
  {
    return new Dimension(
        Integer.parseInt(Config.get(Config.LAST_WINDOW_WIDTH, "10")),
        Integer.parseInt(Config.get(Config.LAST_WINDOW_HEIGHT, "10")));
  }
  
  private int getPreviousExtendedState()
  {
    return Integer.parseInt(Config.get(Config.LAST_EXTENDED_STATE, "" + NORMAL));
  }

  private void loadProgramIcon()
  {
    Image image = TextureStorage.instance().getTexture("circuit_icon");
    Image image16 = TextureStorage.instance().getTexture("circuit_icon16");
    
    List<Image> images = new ArrayList<Image>();
    images.add(image);
    images.add(image16);
    setIconImages(images);
  }
  
  
  @Override
  public void shutdown()
  {
    constructionPanel.dispose();
    backend.getSession().dispose();
    dispose();
  }
  
  
  @Override
  public void startSimulation()
  {
    Database database = backend.getSession().getClient().getWorkspace().database;
    Circuit newCircuit = database.getCircuit().makeCopy();
    GraphicalCircuit newGraphicalCircuit = database.getGraphicalCircuit().makeCopy(newCircuit);
    new Simulator(this, newCircuit, newGraphicalCircuit);
  }
  
  @Override
  public void zoomIn() {
    constructionPanel.getConstructionSurface().zoomIn();
  }
  
  @Override
  public void zoomOut() {
    constructionPanel.getConstructionSurface().zoomOut();
  }
  

  @Override
  public ProgramBackend getBackend()
  {
    return backend;
  }
  
  
  private WindowAdapter windowListener = new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e)
    {
      shutdown();
    }
    
    @Override
    public void windowStateChanged(WindowEvent e)
    {
      int previousMaximizedState = (e.getOldState() & MAXIMIZED_BOTH);
      int currentMaximizedState = (e.getNewState() & MAXIMIZED_BOTH);
      if (previousMaximizedState != currentMaximizedState)
      {
        Config.put(Config.LAST_EXTENDED_STATE, "" + currentMaximizedState);
      }
    }
  };
  
  private ComponentAdapter componentListener = new ComponentAdapter() {
    @Override
    public void componentResized(ComponentEvent e)
    {
      if ((getExtendedState() & MAXIMIZED_HORIZ) == 0)
        Config.put(Config.LAST_WINDOW_WIDTH, "" + getWidth());
      if ((getExtendedState() & MAXIMIZED_VERT) == 0)
        Config.put(Config.LAST_WINDOW_HEIGHT, "" + getHeight());
    }
  };
  
  private class ProgramWindowInterface implements ProgramWindow {
    
    private HashMap<String, ConfigDialog> componentConfigs;
    
    public ProgramWindowInterface()
    {
      componentConfigs = new HashMap<String, ConfigDialog>();
      componentConfigs.put(ModuleClockInputType.class.getName(), new NameDialog(LogicSim.this, "config_dialog_name_message"));
      componentConfigs.put(ModuleInputType.class.getName(), new NameDialog(LogicSim.this, "config_dialog_name_message"));
      componentConfigs.put(ModuleOutputType.class.getName(), new NameDialog(LogicSim.this, "config_dialog_name_message"));
      componentConfigs.put(LEDType.class.getName(), new LEDColorDialog(LogicSim.this));
      componentConfigs.put(ClockType.class.getName(), new ClockDialog(LogicSim.this));
      componentConfigs.put(ButtonType.class.getName(), new ButtonDialog(LogicSim.this));
      componentConfigs.put(TextLabelType.class.getName(), new NameDialog(LogicSim.this, "config_dialog_text_label_message"));
    }
    
    
    @Override
    public void openMessageDialog(String message, String title, MessageType type) {
      JOptionPane.showMessageDialog(
          LogicSim.this,
          message,
          title,
          typeToInt(type));
    }
    
    @Override
    public DialogAnswer openOptionDialog(String message, String title,
        MessageOptions optionType, MessageType messageType) {
      int result = JOptionPane.showConfirmDialog(
          LogicSim.this,
          message,
          title, 
          optionsToInt(optionType), 
          typeToInt(messageType));
      return intToAnswer(result);
    }
    
    private int typeToInt(MessageType type)
    {
      if (type == MessageType.Information)
        return JOptionPane.INFORMATION_MESSAGE;
      if (type == MessageType.Warning)
        return JOptionPane.WARNING_MESSAGE;
      if (type == MessageType.Error)
        return JOptionPane.ERROR_MESSAGE;
      if (type == MessageType.Question)
        return JOptionPane.QUESTION_MESSAGE;
      return 0;
    }
    
    private int optionsToInt(MessageOptions type)
    {
      if (type == MessageOptions.YesNo)
        return JOptionPane.YES_NO_OPTION;
      if (type == MessageOptions.YesNoCancel)
        return JOptionPane.YES_NO_CANCEL_OPTION;
      return 0;
    }
    
    private DialogAnswer intToAnswer(int nbr)
    {
      if (nbr == JOptionPane.YES_OPTION)
        return DialogAnswer.Yes;
      if (nbr == JOptionPane.NO_OPTION)
        return DialogAnswer.No;
      if (nbr == JOptionPane.CANCEL_OPTION)
        return DialogAnswer.Cancel;
      return DialogAnswer.No;
    }
    
    @Override
    public void openConfigDialogFor(GraphicalComponent component) {
      String componentTypeName = component.getType().getClass().getName();
      if (componentConfigs.containsKey(componentTypeName))
      {
        ConfigDialog dialog = componentConfigs.get(componentTypeName);
        Message networkMessage = dialog.open(component.getComponent().getId(), component.getConfig().makeCopy());
        if (networkMessage != null)
          backend.getSession().getClient().getNetwork().send(null, networkMessage);
      }
    }
    
    @Override
    public void closeAllDialogs() {
      for (Window w : getOwnedWindows())
        w.dispose();
    }
  };
   
  
  
  public static void main(String[] args)
  {
    Debugger.setIsInDebugMode(true);
    try {
      new LogicSim();
    } catch (Throwable e) {
      Debugger.fatal(LogicSim.class.getSimpleName() + ": main()", "Failed to start program!", e);
      JOptionPane.showMessageDialog(null, "Failed to start program, see log for details: " + e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
  }
}
