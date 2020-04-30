/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend.tools;

import gui.backend.ProgramBackend;

import java.awt.Color;
import java.awt.Point;

import snet.internal.Message;
import network.hooks.CreateClipboardEntryHook;
import network.hooks.PasteClipboardEntryHook;
import configuration.Table;

public class CopyTool extends AbstractComponentMovementTool
{
  @Override
  protected void selectionMade(ProgramBackend backend, Point start, Point end)
  {
    Message msg = new CreateClipboardEntryHook(null).createMessage(start, end);
    backend.getSession().getClient().getNetwork().send(null, msg);
  }
  
  @Override
  protected boolean selectionPlaced(ProgramBackend backend,
      Point selectionStart, Point selectionEnd, Point destination)
  {
    Message msg = new PasteClipboardEntryHook(null).createMessage(destination);
    backend.getSession().getClient().getNetwork().send(null, msg);
    
    return true;
  }

  @Override
  protected Color getSelectionColor()
  {
    return new Color(80, 150, 255); // Bright blue
  }

  @Override
  public String getName()
  {
    return Table.get("tool_copy");
  }
}
