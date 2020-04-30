/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.config;

import java.awt.Color;

public class LEDConfig implements ComponentConfig
{
  private static final long serialVersionUID = -4199098140278334120L;
  
  public Color offColor;
  public Color onColor;
  
  public LEDConfig()
  {
    offColor = Color.RED;
    onColor = Color.GREEN;
  }
  
  @Override
  public String encodeIntoString() {
    return Integer.toString(offColor.getRGB()) + '&' + Integer.toString(onColor.getRGB());
  }
  
  @Override
  public void decodeFromString(String data) {
    String[] colors = data.split("&");
    offColor = new Color(Integer.parseInt(colors[0]));
    onColor = new Color(Integer.parseInt(colors[1]));
  }

  @Override
  public ComponentConfig makeCopy()
  {
    LEDConfig copy = new LEDConfig();
    copy.offColor = offColor;
    copy.onColor = onColor;
    return copy;
  }
}
