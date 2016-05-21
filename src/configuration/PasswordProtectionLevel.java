/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package configuration;

public enum PasswordProtectionLevel {
  
  NoAccess("no_access"), AllowRead("allow_read");
  
  private String name;
  
  private PasswordProtectionLevel(String name)
  {
    this.name = name;
  }
  
  
  /**
   * Returns the password protection level associated with the specified name,
   *  if the name is invalid {@link #NoAccess} is returned.
   * @param name The name of the gate design type
   * @return The the password protection level associated with the specified name, or
   *  {@link #NoAccess} if name is invalid 
   */
  public static PasswordProtectionLevel getTypeByName(String name)
  {
    for (PasswordProtectionLevel type : values())
    {
      if (type.name.equalsIgnoreCase(name))
        return type;
    }
    
    return NoAccess;
  }
  
  
  public String getName()
  {
    return name;
  }
}
