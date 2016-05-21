/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package graphicalcircuit;

import graphicalcircuit.GraphicalWire.Node;

public class WireRootConverter
{
  public void convertToRoot(Node newRootNode)
  {
    Node previousNode = newRootNode;
    Node currentNodeToProcess = newRootNode.parent;
    while (currentNodeToProcess != null)
    {
      currentNodeToProcess.children.remove(previousNode);
      previousNode.children.add(currentNodeToProcess);
      Node currentParent = currentNodeToProcess.parent;
      currentNodeToProcess.parent = previousNode;
      previousNode = currentNodeToProcess;
      currentNodeToProcess = currentParent;
    }
    
    newRootNode.parent = null;
  }
}
