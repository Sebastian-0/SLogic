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

import network.hooks.CreateClipboardEntryHook;
import network.hooks.PasteClipboardEntryHook;
import network.hooks.RemoveAllInSelectionHook;
import snet.internal.Message;
import configuration.Table;

public class MoveTool extends AbstractComponentMovementTool
{
  @Override
  protected void selectionMade(ProgramBackend backend, Point start, Point end)
  {
  }
  
  @Override
  protected boolean selectionPlaced(ProgramBackend backend, Point selectionStart,
      Point selectionEnd, Point destination)
  {
    if (!selectionEnd.equals(destination))
    {
      Message msg = new CreateClipboardEntryHook(null).createMessage(selectionStart, selectionEnd);
      backend.getSession().getClient().getNetwork().send(null, msg);
  
      msg = new RemoveAllInSelectionHook(null).createMessage(selectionStart, selectionEnd);
      backend.getSession().getClient().getNetwork().send(null, msg);
      
      msg = new PasteClipboardEntryHook(null).createMessage(destination);
      backend.getSession().getClient().getNetwork().send(null, msg);
    }
    
    return false;
  }

  @Override
  protected Color getSelectionColor()
  {
    return new Color(50, 170, 0); // Bright green
  }

  @Override
  public String getName()
  {
    return Table.get("tool_move");
  }
}
