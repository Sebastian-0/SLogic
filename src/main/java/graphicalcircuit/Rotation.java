/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

public enum Rotation {
  
  NORTH(Math.PI/2, (short)1), SOUTH(Math.PI*3/2, (short)3), WEST(Math.PI, (short)2), EAST(0, (short)0);
  
  public final double rotationInRadians;
  public final short id;
  
  private Rotation(double rotationInRadians, short id)
  {
    this.rotationInRadians = rotationInRadians;
    this.id = id;
  }
  
  public static Rotation getRotationFromId(short id)
  {
    for (Rotation value : values())
    {
      if (value.id == id)
        return value;
    }
    
    return null;
  }
}
