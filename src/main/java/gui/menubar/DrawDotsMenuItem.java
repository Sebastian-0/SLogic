/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import gui.backend.ProgramUI;
import configuration.Config;
import configuration.EditorGridMode;
import configuration.Table;

public class DrawDotsMenuItem extends AbstractRadioButtonMenuItem
{
  public DrawDotsMenuItem(ProgramUI window)
  {
    super (Table.get("menubar_draw_dots"), window);
    setSelected(EditorGridMode.getTypeByName(Config.get(Config.GRID_RENDERING_MODE)) == EditorGridMode.Dots);
  }

  @Override
  protected void doAction(ProgramUI program)
  {
    Config.put(Config.GRID_RENDERING_MODE, EditorGridMode.Dots.getName());
    program.getBackend().refreshRenderingSurface();
  }
}
