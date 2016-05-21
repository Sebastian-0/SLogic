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
import graphicalcircuit.Module;
import graphicalcircuit.PinLayout;
import graphicalcircuit.config.ComponentConfig;

import java.awt.Graphics2D;

import circuit.Component;

public class ModuleType extends AbstractCircuitType
{
  private Module module;
  
  
  public ModuleType(Module module)
  {
    this.module = module;
  }
  
  @Override
  public CircuitType makeCopy() {
    return new ModuleType(module);
  }
  
  @Override
  public Component makeBackendComponent(int id) {
    return module.constructComponent(id);
  }

  @Override
  protected void renderType(Graphics2D g, int x, int y, Component component, ComponentConfig config)
  {
    module.render(g, x, y);
  }

  @Override
  public Category getCategory()
  {
    return Category.MODULE;
  }

  @Override
  public PinLayout getLayout()
  {
    return module.getLayout();
  }
  
  @Override
  public int getWidth(GraphicalComponent component)
  {
    return module.getWidth();
  }
  
  @Override
  public int getHeight(GraphicalComponent component)
  {
    return module.getHeight();
  }
  
  public String getDescription()
  {
    return module.getDescription();
  }

  @Override
  public String toString()
  {
    return module.getName();
  }
  
  @Override
  public String getIdentifierString()
  {
    // TODO ModuleType; Om moduler får ids, lägg till idn här!
    return super.getIdentifierString() + module.getName();
  }
}
