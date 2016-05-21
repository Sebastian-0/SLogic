/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package util;

import java.awt.Point;

public class Grid {
  public static final int GRID_WIDTH = 5;

  public static Point alignToGrid(Point source)
  {
    final int gridWidth = GRID_WIDTH;
    
    source.x = ((source.x + gridWidth / 2) / gridWidth) * gridWidth;
    source.y = ((source.y + gridWidth / 2) / gridWidth) * gridWidth;
    
    if (source.x < 0)
      source.x -= gridWidth;
    if (source.y < 0)
      source.y -= gridWidth;
    
    return source;
  }

}
