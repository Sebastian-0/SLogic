/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import graphicalcircuit.Rotation;
import gui.backend.ProgramBackend;
import network.User;
import network.User.Privileges;
import snet.NetworkHook;
import snet.internal.ServerConnection;

public abstract class ExtendedHook extends NetworkHook<Object>
{
  protected ProgramBackend backend;
  
  public ExtendedHook(char commandCode, ProgramBackend backend)
  {
    super (commandCode);
    
    this.backend = backend;
  }
  
  
  protected int integer(String data)
  {
    return Integer.parseInt(data);
  }
  
  protected Rotation rotation(String data)
  {
    return Rotation.getRotationFromId(Short.parseShort(data));
  }
  
  protected boolean canUserEdit(ServerConnection serverConnection)
  {
    String userId = serverConnection.getId();
    User user = backend.getSession().getServer().getUser(userId);
    return user != null && user.privileges == Privileges.ReadWrite;
  }
}
