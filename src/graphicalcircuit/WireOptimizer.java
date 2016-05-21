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
import util.Grid;

public class WireOptimizer
{
  private static final byte NO_ACTION = 0;
  private static final byte REMOVED_NODE = 1;
  private static final byte REMOVED_DUPLICATE_NODE = 2;
  
  /**
   * Optimizes the wire with the specified root node, the new root node is returned.
   * @param rootNode The root node of the wire to optimize
   * @return The new root node
   */
  public Node optimize(Node rootNode)
  {
    if (rootNode.children.size() > 1)
    {
      rootNode = findNewRoot(rootNode);
      new WireRootConverter().convertToRoot(rootNode);
    }
    
    roundPosition(rootNode);
    
    doOptimize(rootNode);
    
    return rootNode;
  }
  
  private Node findNewRoot(Node currentNode)
  {
    if (currentNode.children.isEmpty())
      return currentNode;
    for (Node child : currentNode.children)
    {
      Node newRoot = findNewRoot(child);
      if (newRoot != null)
        return newRoot;
    }
    
    return null;
  }

  private boolean doOptimize(Node rootNode)
  {
    for (int i = 0; i < rootNode.children.size(); i++)
    {
      Node child = rootNode.children.get(i);
          
      roundPosition(child);
      
      byte actionTaken = removeIfUnnecessary(rootNode, child);
      if (actionTaken == REMOVED_NODE)
      {
        i--;
      }
      else if (actionTaken == REMOVED_DUPLICATE_NODE)
      {
        return true;
      }
      else
      {
        if (doOptimize(child)) // Go back one step, reiteration required
          i--;
      }
    }
    
    return false;
  }
  
  private void roundPosition(Node node)
  {
    node.position.x = Math.round(node.position.x / (float)Grid.GRID_WIDTH) * Grid.GRID_WIDTH;
    node.position.y = Math.round(node.position.y / (float)Grid.GRID_WIDTH) * Grid.GRID_WIDTH;
  }
  
  private byte removeIfUnnecessary(Node parent, Node nodeToCheck)
  {
    boolean hasConnection = nodeToCheck.connection != null;
    if (!hasConnection)
    {
      boolean isSimpleLink = nodeToCheck.children.size() == 1;
      if (isSimpleLink)
      {
        Node child = nodeToCheck.children.get(0);
        if (areAligned(parent, nodeToCheck, child))
        {
          removeNode(parent, nodeToCheck, child);
          return REMOVED_NODE;
        }
      }
      
      if (nodeToCheck.position.equals(parent.position))
      {
        removeNode(parent, nodeToCheck, null);
        return REMOVED_DUPLICATE_NODE;
      }
    }
    
    return NO_ACTION;
  }

  private void removeNode(Node parent, Node nodeToRemove, Node child)
  {
    parent.children.remove(nodeToRemove);
    if (child != null)
    {
      parent.children.add(child);
      child.parent = parent;
      nodeToRemove.children.remove(child);
    }
  }
  
  private boolean areAligned(Node a, Node b, Node c)
  {
    if (a.position.x == b.position.x && b.position.x == c.position.x)
      return true;
    if (a.position.y == b.position.y && b.position.y == c.position.y)
      return true;
    return false;
  }
}
