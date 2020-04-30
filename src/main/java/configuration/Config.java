/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import sutilities.Debugger;

public class Config
{
  private static final File CONFIG_FILE = new File("config.ini");

  public static final String CIRCUIT_EXTENSION = "cir";
  public static final String MODULE_EXTENSION = "mod";
  public static final String STRING_TABLE_EXTENSION = "lang";
  

  public static final String USER_NAME = "userName";
  public static final String HOST_PORT = "portUsedWhenHosting";

  public static final String CLIENT_IP = "joinIp";
  public static final String CLIENT_PORT = "joinPort";
  
  public static final String USE_PASSWORD_PROTECTION = "usePasswordProtection";
  public static final String PROTECTION_LEVEL = "protectionLevel";
  public static final String PASSWORD = "password";
  
  public static final String LANGUAGE = "language";
  
  public static final String BACKUP_SAVE_AMOUNT = "amountOfBackupSaves";

  public static final String LAST_WINDOW_WIDTH = "lastWindowWidth";
  public static final String LAST_WINDOW_HEIGHT = "lastWindowHeight";
  public static final String LAST_EXTENDED_STATE = "lastExtendedState";
  
  public static final String LAST_CHAT_PANEL_HEIGHT = "lastChatPanelHeight";
  
  public static final String GRID_RENDERING_MODE = "gridRenderingMode";
  public static final String MARK_UNUSED_WIRE_ENDS = "markUnusedWireEnds";
  public static final String MARK_UNUSED_PINS = "markUnusedPins";
  
  public static final String MODULE_FOLDER_PATH = "moduleFolderPath";
  public static final String CIRCUIT_FOLDER_PATH = "circuitFolderPath";
  public static final String LANGUAGE_FOLDER_PATH = "languageFolderPath";

  public static final String GATE_DESIGN_TYPE = "gateDesignType";
  
  public static final String MAXIMUM_UPDATES_PER_COMPONENT_PER_SIMULATION_STEP = "maximumUpdatesPerComponentPerSimulationStep";
  
  public static final String USE_DEBUG_MODE = "useDebugMode";

  
  private static Properties properties;
  
  static
  {
    properties = new Properties();
    loadConfig();
  }
  
  public static String get(String configKey)
  {
    return properties.getProperty(configKey, "");
  }
  
  public static String get(String configKey, String defaultValue)
  {
    return properties.getProperty(configKey, defaultValue);
  }
  
  public static void put(String configKey, String value)
  {
    properties.put(configKey, value);
    saveConfig();
  }


  private static void loadConfig()
  {
    generateDefaultValues();
    
    if (CONFIG_FILE.exists())
    {
      try
      {
        FileInputStream in = new FileInputStream(CONFIG_FILE);
        properties.load(in);
        in.close();
      } catch (IOException e)
      {
        Debugger.error("Config <static>", "Failed to load config", e);
      }
    }
    else
    {
      // Save default settings
      saveConfig();
    }
  }
  
  private static void generateDefaultValues()
  {
    properties.put(USER_NAME, "A User");
    properties.put(HOST_PORT, "27015");
    properties.put(CLIENT_IP, "localhost");
    properties.put(CLIENT_PORT, "27015");
    properties.put(MODULE_FOLDER_PATH, "modules");
    properties.put(CIRCUIT_FOLDER_PATH, "circuits");
    properties.put(LANGUAGE_FOLDER_PATH, "languages");
    properties.put(GATE_DESIGN_TYPE, GateDesignType.American.getName());
    properties.put(USE_PASSWORD_PROTECTION, "false");
    properties.put(PROTECTION_LEVEL, PasswordProtectionLevel.NoAccess.getName());
    properties.put(BACKUP_SAVE_AMOUNT, "3");
    properties.put(GRID_RENDERING_MODE, EditorGridMode.Grid.getName());
    properties.put(MARK_UNUSED_WIRE_ENDS, "true");
    properties.put(MARK_UNUSED_PINS, "false");
    properties.put(MAXIMUM_UPDATES_PER_COMPONENT_PER_SIMULATION_STEP, "100");
    properties.put(USE_DEBUG_MODE, "false");
  }

  private static void saveConfig()
  {
    try
    {
      FileOutputStream out = new FileOutputStream(CONFIG_FILE);
      properties.store(out, "Logic Sim ini-file. Do not change manually\n");
      out.close();
    } catch (IOException e)
    {
      Debugger.error("Config <static>", "Failed to save config", e);
    }
  }
}
