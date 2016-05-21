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
import configuration.Table;

public class DrawUnusedPinMarkerMenuItem extends AbstractCheckBoxMenuItem
{
  public DrawUnusedPinMarkerMenuItem(ProgramUI window)
  {
    super (Table.get("menubar_draw_unused_pin_marker"), window);
    setSelected(Config.get(Config.MARK_UNUSED_PINS).equals("true"));
  }

  @Override
  protected void doAction(ProgramUI program)
  {
    Config.put(Config.MARK_UNUSED_PINS, Boolean.toString(isSelected()));
    program.getBackend().refreshRenderingSurface();
  }
}
