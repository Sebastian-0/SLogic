/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.simulation.backend;

import circuit.CircuitEntity.State;

public interface SimulationClockListener
{
  public void stateChanged(int clockComponentId, State newState);
}
