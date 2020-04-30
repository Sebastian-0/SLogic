/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.config;


public class ClockConfig implements ComponentConfig
{
  private static final long serialVersionUID = -3017047890543656137L;
  
  public int initialOffTime;
  public int onTimeMillis;
  public int offTimeMillis;
  
  public ClockConfig()
  {
    initialOffTime = 0;
    onTimeMillis = 1000;
    offTimeMillis = 1000;
  }
  
  @Override
  public String encodeIntoString() {
    return Integer.toString(initialOffTime) + '-' + Integer.toString(onTimeMillis) + '-' + Integer.toString(offTimeMillis);
  }
  
  @Override
  public void decodeFromString(String data) {
    String[] numbers = data.split("-");
    initialOffTime = Integer.parseInt(numbers[0]);
    onTimeMillis = Integer.parseInt(numbers[1]);
    offTimeMillis = Integer.parseInt(numbers[2]);
  }

  @Override
  public ComponentConfig makeCopy()
  {
    ClockConfig copy = new ClockConfig();
    copy.initialOffTime = initialOffTime;
    copy.onTimeMillis = onTimeMillis;
    copy.offTimeMillis = offTimeMillis;
    return copy;
  }
}
