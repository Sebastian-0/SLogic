/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit.types;

import graphicalcircuit.CircuitType;
import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.PinLayout;
import graphicalcircuit.SimulationInputListener;
import graphicalcircuit.config.ButtonConfig;
import graphicalcircuit.config.ComponentConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import circuit.CircuitEntity.State;
import circuit.Component;
import circuit.ConstantState;
import configuration.Table;

public class ButtonType extends AbstractCircuitType
{
  private static final int SIDE = 30;
  
  private PinLayout layout;
  
  
  public ButtonType()
  {
    layout = new PinLayout(new Point[0], new Point[] { new Point(SIDE/2 + PIN_LENGHT, 0) });
  }
  
  @Override
  public ComponentConfig generateDefaultConfig(Component component)
  {
    return new ButtonConfig();
  }
  
  @Override
  public CircuitType makeCopy() {
    return new ButtonType();
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return new ConstantState(id, State.OFF);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    if (isValidComponent(component) && ((ConstantState)component).getState() == State.ON)
      g.setColor(Color.RED.darker().darker());
    else
      g.setColor(Color.RED);
    g.fillRect(x - SIDE/2, y - SIDE/2, SIDE, SIDE);
  }
  
  private boolean isValidComponent(Component c)
  {
    if (c instanceof ConstantState)
      return true;
    return false;
  }

  @Override
  public SimulationInputListener createSimulationInputListener(
      GraphicalComponent component) {
    return new InputListener(component);
  }
  
  @Override
  public Category getCategory()
  {
    return Category.IO;
  }

  @Override
  public PinLayout getLayout()
  {
    return layout;
  }
  
  @Override
  public boolean canBePartOfModule() {
    return false;
  }
  
  @Override
  public int getWidth(GraphicalComponent component)
  {
    return SIDE;
  }
  
  @Override
  public int getHeight(GraphicalComponent component)
  {
    return SIDE;
  }

  @Override
  public String toString()
  {
    return Table.get("circuit_button");
  }
  
  
  private class InputListener extends SimulationInputListener {
    
    public InputListener(GraphicalComponent component) {
      super(component);
    }
    
    
    @Override
    public boolean mousePressed(int x, int y) {
      if (component.contains(x, y)) {
        if (isToggleButton()) {
          toggleButtonState();
        }
        else {
          ConstantState state = (ConstantState)component.getComponent();
          state.setState(State.ON, 0);
        }
        return true;
      }
      return false;
    }

    private void toggleButtonState() {
      ConstantState state = (ConstantState)component.getComponent();
      if (state.getState() == State.ON)
        state.setState(State.OFF, 0);
      else
        state.setState(State.ON, 0);
    }
    
    
    @Override
    public boolean mouseReleased(int x, int y) {
      if (!isToggleButton()) {
        ConstantState state = (ConstantState)component.getComponent();
        state.setState(State.OFF, 0);
        return true;
      }
      return false;
    }

    private boolean isToggleButton() {
      return ((ButtonConfig)component.getConfig()).isToggleButton;
    }
  }
}
