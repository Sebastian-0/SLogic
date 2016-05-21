/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend;

import java.util.Observable;

public class GuiUpdater extends Observable
{
  @Override
  public synchronized void setChanged()
  {
    super.setChanged();
  }
}
