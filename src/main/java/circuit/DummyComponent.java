/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package circuit;

public class DummyComponent extends Component {
  private static final long serialVersionUID = 2614939709445713856L;
  
  private static State[] state = new State[0];

  public DummyComponent(int id) {
    super(id, 0, 0);
  }

  @Override
  protected State[] generateOutStates(State[] inputs) {
    return state;
  }

  @Override
  protected Component makeCopy(int id, int numInputs) {
    return new DummyComponent(id);
  }
}
