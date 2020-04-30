/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package configuration;

public enum GateDesignType {
  
  Text("text"), American("us"), IEC("iec");
  
  private String name;
  
  private GateDesignType(String name)
  {
    this.name = name;
  }
  
  
  /**
   * Returns the gate design type associated with the specified name, if the name
   *  is invalid {@link #Text} is returned.
   * @param name The name of the gate design type
   * @return The gate design type associated with the specified name, or
   *  {@link #Text} if name is invalid 
   */
  public static GateDesignType getTypeByName(String name)
  {
    for (GateDesignType type : values())
    {
      if (type.name.equalsIgnoreCase(name))
        return type;
    }
    
    return Text;
  }
  
  
  public String getName()
  {
    return name;
  }
}
