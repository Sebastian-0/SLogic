/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.config;

public class TextLabelConfig extends NameConfig {
  private static final long serialVersionUID = 4570969882344361046L;
  
  public int width;
  public int height;

  public TextLabelConfig(String initialText) {
    super(initialText);
  }
  
  @Override
  public ComponentConfig makeCopy() {
    TextLabelConfig config = new TextLabelConfig(name);
    config.width = width;
    config.height = height;
    return config;
  }
}
