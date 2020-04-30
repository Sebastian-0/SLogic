/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import java.awt.Point;
import java.io.Serializable;

import util.Grid;

public class PinLayout implements Serializable
{
  private static final long serialVersionUID = 8134458656501417008L;
  
  private Pin[] inPins;
  private Pin[] outPins;
  
  public PinLayout(Point[] inputPinPositions, Point[] outputPinPositions)
  {
    inPins  = createPins(inputPinPositions, true);
    outPins = createPins(outputPinPositions, false);
  }

  private Pin[] createPins(Point[] pinPositions, boolean isInput)
  {
    Pin[] pins = new Pin[pinPositions.length];
    for (int i = 0; i < pinPositions.length; i++)
    {
      pins[i] = new Pin(i, pinPositions[i].x, pinPositions[i].y, isInput);
    }
    return pins;
  }
  
  
  public Pin[] getInputPins()
  {
    return inPins;
  }
  
  public Pin[] getOutputPins()
  {
    return outPins;
  }
  
  public Pin getPinAt(Rotation rotationOfCircuit, int x, int y)
  {
    Pin pin = getPinAt(rotationOfCircuit, x, y, inPins);
    if (pin == null)
      pin = getPinAt(rotationOfCircuit, x, y, outPins);
    
    return pin;
  }

  private Pin getPinAt(Rotation rotationOfCircuit, int x, int y, Pin[] pinsToCheck)
  {
    for (Pin pin : pinsToCheck)
    {
      Point pos = pin.getPosition(rotationOfCircuit);
      int dx = Math.abs(pos.x - x);
      int dy = Math.abs(pos.y - y);
      if (dx < Grid.GRID_WIDTH && dy < Grid.GRID_WIDTH)
        return pin;
    }
    
    return null;
  }
  
  
  @Override
  public String toString()
  {
    return "in" + inPins.length + "-out" + outPins.length;
  }
  
  
  
  public static class Pin
  {
    private Point position;
    private boolean isInput;
    private int index;
    
    public Pin(int index, int x, int y, boolean isInput)
    {
      this.index = index;
      this.position = new Point(x, y);
      this.isInput = isInput;
    }
    
    public Point getPosition()
    {
      return getPosition(Rotation.EAST);
    }
    
    public Point getPosition(Rotation rotation)
    {
      double cos = Math.cos(rotation.rotationInRadians);
      double sin = Math.sin(rotation.rotationInRadians);
      
      Point result = new Point();
      result.x = (int)(position.x * cos - position.y * sin);
      result.y = (int)(position.x * sin + position.y * cos);
      
      return result;
    }
    
    public boolean isInput()
    {
      return isInput;
    }
    
    public int getIndex()
    {
      return index;
    }

    
    public boolean isAtComponentTop(float widthToHeightRatio)
    {
      if (position.y*widthToHeightRatio < 0 && position.y*widthToHeightRatio < -Math.abs(position.x))
        return true;
      return false;
    }
    
    public boolean isAtComponentBottom(float widthToHeightRatio)
    {
      if (position.y*widthToHeightRatio > 0 && position.y*widthToHeightRatio > Math.abs(position.x))
        return true;
      return false;
    }
    
    public boolean isAtComponentLeftSide(float widthToHeightRatio)
    {
      if (position.x < 0 && position.x < -Math.abs(position.y*widthToHeightRatio))
        return true;
      return false;
    }
    
    public boolean isAtComponentRightSide(float widthToHeightRatio)
    {
      if (position.x > 0 && position.x > Math.abs(position.y*widthToHeightRatio))
        return true;
      return false;
    }
  }
}
