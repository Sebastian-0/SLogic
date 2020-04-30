/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;


public abstract class SimulationInputListener {
  
  protected GraphicalComponent component;
  
  public SimulationInputListener(GraphicalComponent component) {
    this.component = component;
  }

  public boolean mousePressed(int x, int y) { return false; }
  public boolean mouseReleased(int x, int y) { return false; }
  public boolean mouseMoved(int x, int y) { return false; }
}
