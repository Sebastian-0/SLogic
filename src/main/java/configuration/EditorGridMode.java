/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package configuration;

public enum EditorGridMode {
  
  Grid("grid"), Dots("dots"), None("none");
  
  private String name;
  
  private EditorGridMode(String name)
  {
    this.name = name;
  }
  
  
  /**
   * Returns the grid mode associated with the specified name, if the name
   *  is invalid {@link #Grid} is returned.
   * @param name The name of the gate design type
   * @return The grid mode associated with the specified name, or
   *  {@link #Grid} if name is invalid 
   */
  public static EditorGridMode getTypeByName(String name)
  {
    for (EditorGridMode type : values())
    {
      if (type.name.equalsIgnoreCase(name))
        return type;
    }
    
    return Grid;
  }
  
  
  public String getName()
  {
    return name;
  }
}
