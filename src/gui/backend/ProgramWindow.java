/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend;

import graphicalcircuit.GraphicalComponent;

public interface ProgramWindow {
  
  public enum MessageType {
    Information(0), Warning(1), Error(2), Question(3);
    
    public final int value;
    
    private MessageType(int value) {
      this.value = value;
    }
    
    public static MessageType getTypeFromNumber(int n)
    {
      for (MessageType type : values())
        if (type.value == n)
          return type;
      return null;
    }
  }
  public enum MessageOptions { YesNo, YesNoCancel }
  public enum DialogAnswer { Yes, No, Cancel }
  
  public void openMessageDialog(String message, String title, MessageType type);
  public DialogAnswer openOptionDialog(String message, String title, MessageOptions optionType, MessageType type);
  
  public void openConfigDialogFor(GraphicalComponent component);
  public void closeAllDialogs();
}
