/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import graphicalcircuit.PinLayout.Pin;
import graphicalcircuit.config.ComponentConfig;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Map;

import circuit.Circuit;
import circuit.Component;

public class GraphicalComponent extends GraphicalCircuitEntity
{
  private static final long serialVersionUID = 3908301983886298492L;

  private String typeId;
  private transient CircuitType type;
  
  private ComponentConfig config;
  
  private int componentId;
  private transient Component component;
  
  private GraphicalWire[] inPins;
  private GraphicalWire[] outPins;
  
  private int x;
  private int y;
  
  private Rotation rotation;
  
  
  public GraphicalComponent(CircuitType type, Component component, int x, int y, Rotation rotation)
  {
    typeId = type.getIdentifierString();
    this.type = type;
    this.component = component;
    updateId();
    
    this.config = type.generateDefaultConfig(component);
    
    inPins = new GraphicalWire[component.getAmountOfInputs()];
    outPins = new GraphicalWire[component.getAmountOfOutputs()];
    
    this.x = x;
    this.y = y;
    
    this.rotation = rotation;
  }
  
  @Override
  protected void wasDeserialized(Circuit circuit, Collection<CircuitType> types)
  {
    if (typeId.startsWith("control.components.types")) {
      typeId = "graphicalcircuit.types" + typeId.substring(typeId.lastIndexOf('.'));
    }
    
    component = circuit.getComponent(componentId);
    for (CircuitType type : types)
    {
      if (type.getIdentifierString().equals(typeId))
      {
        this.type = type;
      }
    }

    if (type == null)
      throw new IllegalStateException("Missing the type for this component: " + typeId);
  }
  
  @Override
  protected void updateId()
  {
    componentId = component.getId();
  }
  
  
  @Override
  protected void move(int dx, int dy)
  {
    x += dx;
    y += dy;
  }
  
  @Override
  public boolean contains(int x, int y)
  {
    return type.contains(x - this.x, y - this.y, rotation, this);
  }
  
  @Override
  public boolean intersects(Rectangle rect)
  {
    rect.translate(-this.x, -this.y);
    boolean intersects = type.intersects(rect, rotation, this);
    rect.translate(this.x, this.y);
    return intersects;
  }
  
  
  public void setInput(GraphicalWire in, int pinIndex)
  {
    inPins[pinIndex] = in;
  }
  
  public void setOutput(GraphicalWire out, int pinIndex)
  {
    outPins[pinIndex] = out;
    if (out != null)
      component.setOutputWire(out.getWire(), pinIndex);
    else
      component.setOutputWire(null, pinIndex);
  }
  
  
  public void render(Graphics2D g)
  {
    g.rotate(rotation.rotationInRadians, x, y);
    type.render(g, x, y, this);
    g.rotate(-rotation.rotationInRadians, x, y);
  }
  
  
  public CircuitType getType()
  {
    return type;
  }
  
  public ComponentConfig getConfig()
  {
    return config;
  }
  
  public Component getComponent()
  {
    return component;
  }
  

  public GraphicalWire getInput(int pin)
  {
    return inPins[pin];
  }
  
  public GraphicalWire getOutput(int pin)
  {
    return outPins[pin];
  }
  
  
  public Pin getPinAt(int x, int y)
  {
    return type.getLayout().getPinAt(rotation, x - this.x, y - this.y);
  }
  
  
  public SimulationInputListener createSimulationListener() 
  {
    return type.createSimulationInputListener(this);
  }


  public boolean canBePartOfModule()
  {
    return type.canBePartOfModule();
  }
  
  public boolean isSufficientlyConnected()
  {
    for (GraphicalWire in : inPins)
    {
      if (in == null)
        return false;
    }
    for (GraphicalWire out : outPins)
    {
      if (out != null)
        return true;
    }
    
    return outPins.length == 0;
  }
  
  
  @Override
  public void wasRemoved()
  {
    for (int i = 0; i < inPins.length; i++)
    {
      GraphicalWire input = inPins[i];
      if (input != null)
      {
        input.removeOutput(this, i);
      }
    }
    
    for (int i = 0; i < outPins.length; i++)
    {
      GraphicalWire output = outPins[i];
      if (output != null)
      {
        output.removeInput();
      }
    }
  }
  
  
  protected GraphicalComponent makeCopy(Circuit backend, Map<Integer, Object> copiedComponents)
  {
    GraphicalComponent copy = new GraphicalComponent(type, component, x, y, rotation);
    copy.component = backend.getComponent(component.getId());
    copiedComponents.put(component.getId(), copy);
    
    if (config != null)
      copy.config = config.makeCopy();
    
    copyPinConnections(backend, copiedComponents, inPins, copy.inPins);
    copyPinConnections(backend, copiedComponents, outPins, copy.outPins);
    
    return copy;
  }

  private void copyPinConnections(Circuit backend, Map<Integer, Object> copiedComponents,
      GraphicalWire[] source, GraphicalWire[] target)
  {
    for (int i = 0; i < source.length; i++)
    {
      if (source[i] != null)
      {
        GraphicalWire newWire = (GraphicalWire)copiedComponents.get(source[i].getWire().getId());
        if (newWire == null)
        {
          newWire = (GraphicalWire)source[i].makeCopy(backend, copiedComponents);
        }
        
        target[i] = newWire;
      }
    }
  }
  
  @Override
  public String toString() {
    return "GC: " + type.toString() + " (id " + component.getId() + ")";
  }
}
