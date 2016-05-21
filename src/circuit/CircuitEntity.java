/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

import java.io.Serializable;
import java.util.Map;


public abstract class CircuitEntity implements Serializable
{
  private static final long serialVersionUID = -1229633095065919136L;


  public enum State {
    ON, OFF, HIGH;
    
    public static State getOpposite(State state) {
      switch (state) {
      case ON:
        return OFF;
      case OFF:
        return ON;
      default:
        return HIGH;
      }
    }
  }
  
  private int id;

  
  public CircuitEntity(int id)
  {
    this.id = id;
  }
  
  
  public int getId()
  {
    return id;
  }
  
  protected void setId(int id)
  {
    this.id = id;
  }
  
  
  protected abstract CircuitEntity makeCopy(Map<Integer, CircuitEntity> copiedComponents);
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CircuitEntity)
    {
      if (((CircuitEntity) obj).getId() == id &&
          obj.getClass().getName().equals(getClass().getName()))
        return true;
    }
    return false;
  }
}
