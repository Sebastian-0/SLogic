/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend;

import java.awt.Color;

public class ChatMessage
{
  public final String text;
  public final Color color;
  
  public ChatMessage(String text, Color color)
  {
    this.color = color;
    this.text = text;
  }
}
