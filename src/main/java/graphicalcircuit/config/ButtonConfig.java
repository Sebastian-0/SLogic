/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.config;

public class ButtonConfig implements ComponentConfig
{
  private static final long serialVersionUID = -8052450206032378385L;
  
  public boolean isToggleButton;
  
  public ButtonConfig()
  {
    isToggleButton = true;
  }
  
  @Override
  public String encodeIntoString() {
    return Boolean.toString(isToggleButton);
  }
  
  @Override
  public void decodeFromString(String data) {
    isToggleButton = Boolean.parseBoolean(data);
  }

  @Override
  public ComponentConfig makeCopy()
  {
    ButtonConfig copy = new ButtonConfig();
    copy.isToggleButton = isToggleButton;
    return copy;
  }
}
