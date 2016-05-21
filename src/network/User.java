/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network;

import java.awt.Point;

public class User
{
  public enum Privileges { Read, ReadWrite, Unspecified }
  
  
  public final String id;
  public String name;
  
  public Privileges privileges;
  
  public boolean isTheUserOfThisClient;
  
  public Point currentCursorPosition;
  
  
  public User(String id, String name)
  {
    this (id, name, Privileges.Unspecified);
  }
  public User(String id, String name, Privileges privileges)
  {
    this.id = id;
    this.name = name;
    
    this.privileges = privileges;
    
    currentCursorPosition = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
}
