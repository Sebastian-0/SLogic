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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import circuit.Circuit;
import circuit.CircuitEntity.State;
import circuit.Wire;
import configuration.Config;

public class GraphicalWire extends GraphicalCircuitEntity
{
  private static final long serialVersionUID = -706871661211956650L;

  private int wireId;
  private transient Wire wire;
  
  private Node rootNode;
  
  
  public GraphicalWire(List<Point> nodes, Wire wire, GraphicalComponent input, GraphicalComponent output)
  {
    this.wire = wire;
    updateId();
    
    rootNode = new Node();
    Node currentNode = rootNode;
    Node lastNode = null;
    for (Point p : nodes)
    {
      currentNode.position.setLocation(p);
      currentNode.parent = lastNode;
      if (lastNode != null)
        lastNode.children.add(currentNode);
      
      lastNode = currentNode;
      currentNode = new Node();
    }
    
    if (input != null)
      connectComponent(rootNode, input);
    if (output != null)
      connectComponent(lastNode, output);
  }

  private void connectComponent(Node targetNode, GraphicalComponent componentAtNode)
  {
    Pin pin = componentAtNode.getPinAt(targetNode.position.x, targetNode.position.y);
    if (pin.isInput())
    {
      targetNode.connection = new Connection(componentAtNode, pin.getIndex());
      targetNode.isInputConnection = false;
      wire.addTarget(componentAtNode.getComponent(), pin.getIndex());
      componentAtNode.setInput(this, pin.getIndex());
    }
    else
    {
      targetNode.connection = new Connection(componentAtNode, pin.getIndex());
      targetNode.isInputConnection = true;
      componentAtNode.setOutput(this, pin.getIndex());
    }
  }
  
  
  @Override
  protected void wasDeserialized(Circuit circuit, Collection<CircuitType> types)
  {
    wire = circuit.getWire(wireId);
  }
  
  @Override
  protected void updateId()
  {
    wireId = wire.getId();
  }
  
  
  @Override
  protected void move(int dx, int dy) {
    move(rootNode, dx, dy);
  }
  
  private void move(Node currentNode, int dx, int dy)
  {
    currentNode.position.translate(dx, dy);
    
    for (Node child : currentNode.children)
    {
      move(child, dx, dy);
    }
  }
  
  @Override
  public boolean contains(int x, int y)
  {
    return getNodeAt(x, y) != null;
  }
  
  @Override
  public boolean intersects(Rectangle bounds)
  {
    return intersects(rootNode, rootNode, bounds);
  }
  
  private boolean intersects(Node lastNode, Node currentNode, Rectangle bounds)
  {
    if (bounds.intersectsLine(
        lastNode.position.x,
        lastNode.position.y,
        currentNode.position.x,
        currentNode.position.y))
      return true;
    
    for (Node child : currentNode.children)
    {
      boolean result = intersects(currentNode, child, bounds);
      if (result)
        return true;
    }
    
    return false;
  }
  
  
  public Node getNodeAt(int x, int y)
  {
    return findNodeAt(rootNode, rootNode, x, y);
  }
  
  private Node findNodeAt(Node lastNode, Node currentNode, int x, int y)
  {
    if (collides(lastNode.position, currentNode.position, x, y))
      return currentNode;
    
    for (Node child : currentNode.children)
    {
      Node result = findNodeAt(currentNode, child, x, y);
      if (result != null)
        return result;
    }
    
    return null;
  }
  
  private boolean collides(Point p1, Point p2, int x, int y)
  {
    int dx = x - p1.x;
    int dy = y - p1.y;
    if (dx == 0)
    {
      if (y >= p1.y && y <= p2.y ||
          y <= p1.y && y >= p2.y)
        return true;
    }
    if (dy == 0)
    {
      if (x >= p1.x && x <= p2.x ||
          x <= p1.x && x >= p2.x)
        return true;
    }
    return false;
  }
  
  
  public void render(Graphics2D g)
  {
    if (wire.getState() == State.OFF)
      g.setColor(Color.BLACK);
    else if (wire.getState() == State.ON)
      g.setColor(Color.GREEN.darker());
    else
      g.setColor(Color.MAGENTA);
    
    renderNode(g, null, rootNode, 0);
  }
  
  private void renderNode(Graphics2D g, Node previousNode, Node currentNode, int nodeDistanceFromRoot)
  {
    for (Node node : currentNode.children)
    {
      renderNode(g, currentNode, node, nodeDistanceFromRoot+1);
    }
    
    if (previousNode != null)
    {
      g.drawLine(previousNode.position.x, previousNode.position.y, currentNode.position.x, currentNode.position.y);
    }
    
    int amountOfConnections = currentNode.children.size();
    if (currentNode.parent != null) amountOfConnections += 1;
    if (amountOfConnections > 2)
    {
      g.drawRect(currentNode.position.x - 1, currentNode.position.y - 1, 2, 2);
    }
    else if (amountOfConnections == 1 && currentNode.connection == null)
    {
      if (shouldDrawMarkerAtCutWireEnds())
      {
        drawMarker(g, currentNode);
      }
    }
    
    if (shouldDrawDebug())
    {
      drawDebug(g, currentNode, nodeDistanceFromRoot);
    }
  }

  private boolean shouldDrawMarkerAtCutWireEnds()
  {
    return Config.get(Config.MARK_UNUSED_WIRE_ENDS).equals("true");
  }

  private void drawMarker(Graphics2D g, Node currentNode)
  {
    Color color = g.getColor();
    g.setColor(Color.RED.darker());
    g.drawLine(currentNode.position.x - 2, currentNode.position.y - 2, currentNode.position.x + 2, currentNode.position.y + 2);
    g.drawLine(currentNode.position.x + 2, currentNode.position.y - 2, currentNode.position.x - 2, currentNode.position.y + 2);
    g.setColor(color);
  }
  
  private boolean shouldDrawDebug()
  {
    return Config.get(Config.USE_DEBUG_MODE).equals("true");
  }
  
  private void drawDebug(Graphics2D g, Node currentNode, int nodeDistanceFromRoot)
  {
    Color c = g.getColor();
    g.setColor(Color.GREEN.darker().darker());
    g.setFont(g.getFont().deriveFont(Font.PLAIN, 10f));
    g.drawString("" + nodeDistanceFromRoot, currentNode.position.x, currentNode.position.y);
    g.setColor(c);
  }
  

  protected GraphicalWire makeCopy(Circuit backend, Map<Integer, Object> copiedComponents)
  {
    GraphicalWire copy = new GraphicalWire(new ArrayList<Point>(), backend.getWire(wire.getId()), null, null);
    copiedComponents.put(wire.getId(), copy);
    copy.rootNode = rootNode.makeCopy(backend, copiedComponents);
    
    return copy;
  }
  
  
  public boolean mergeWith(GraphicalWire otherWire, Point mergePosition)
  {
    Node nodeAtPos = getNodeAt(mergePosition.x, mergePosition.y);
    Node otherNodeAtPos = otherWire.getNodeAt(mergePosition.x, mergePosition.y);
    
    boolean wiresOverlap = (nodeAtPos != null && otherNodeAtPos != null);
    boolean bothWiresHaveInputs = (hasInput() && otherWire.hasInput());
    if (!wiresOverlap || bothWiresHaveInputs)
      return false;
    
    if (!nodeAtPos.isAt(mergePosition))
      nodeAtPos = addNodeAt(nodeAtPos, mergePosition);
    if (!otherNodeAtPos.isAt(mergePosition))
      otherNodeAtPos = addNodeAt(otherNodeAtPos, mergePosition);
    
    convertToRoot(otherNodeAtPos);
    replaceNode(otherNodeAtPos, nodeAtPos);

    mergeWireConnections(otherWire);
    
    return true;
  }

  private void replaceNode(Node nodeToReplace, Node newNode)
  {
    for (Node child : nodeToReplace.children)
    {
      child.parent = newNode;
      newNode.children.add(child);
    }
    
    if (nodeToReplace.connection != null)
    {
      if (newNode.connection != null)
        throw new RuntimeException("Both nodes can't have a connection since they are both at the same position!");
      
      newNode.connection = nodeToReplace.connection;
      newNode.isInputConnection = nodeToReplace.isInputConnection;
    }
  }

  private Node addNodeAt(Node childOfNewNode, Point nodePosition)
  {
    Node newNode = new Node();
    newNode.position.setLocation(nodePosition);
    newNode.parent = childOfNewNode.parent;
    newNode.children.add(childOfNewNode);
    childOfNewNode.parent.children.remove(childOfNewNode);
    childOfNewNode.parent = newNode;
    childOfNewNode = newNode;
    return childOfNewNode;
  }

  private void mergeWireConnections(GraphicalWire otherWire)
  {
    updateWireConnections(rootNode);

    getWire().mergeWith(otherWire.getWire());
  }
  
  private void updateWireConnections(Node node)
  {
    if (node.connection != null)
    {
      if (node.isInputConnection)
      {
        node.connection.component.setOutput(this, node.connection.pin);
      }
      else
      {
        node.connection.component.setInput(this, node.connection.pin);
      }
    }
    
    for (Node child : node.children)
    {
      updateWireConnections(child);
    }
  }
  
  
  public boolean splitWireAround(Circuit circuit, GraphicalCircuit gcircuit, 
      Rectangle bounds, boolean shouldOptimizeWires)
  {
    // Split wire repeatedly until it can't be split anymore
    while (doSplitWireAround(rootNode, circuit, gcircuit, bounds, shouldOptimizeWires));
    
    if (shouldOptimizeWires)
      optimize();
    
    boolean isEmpty = rootNode.children.isEmpty();
    return isEmpty;
  }
  
  private boolean doSplitWireAround(Node currentNode, Circuit circuit, 
      GraphicalCircuit gcircuit, Rectangle bounds, boolean shouldOptimizeWires)
  {
    for (Node child : currentNode.children)
    {
      Line2D line = new Line2D.Float(currentNode.position.x,
          currentNode.position.y,
          child.position.x,
          child.position.y);
      
      if (bounds.intersectsLine(line))
      {
        Point intersection = findFirstIntersection(bounds, line);
        if (intersection != null)
        {
          Node newNode1 = new Node();
          Node newNode2 = new Node();
          
          newNode1.position.setLocation(intersection.x, intersection.y);
          newNode2.position.setLocation(intersection.x, intersection.y);
          
          currentNode.children.remove(child);
          currentNode.children.add(newNode1);
          child.parent = newNode2;
          newNode1.parent = currentNode;
          newNode2.children.add(child);
          
          createIndependentWire(circuit, gcircuit, newNode2, shouldOptimizeWires);
          
          applyExistingConnections(this);
          
//          if (rootNode.children.isEmpty())
//          {
//            gcircuit.removeWire(this);
//            wasRemoved();
//            circuit.removeWire(wire);
//          }
          
          return true;
        }
      }
      
      if (doSplitWireAround(child, circuit, gcircuit, bounds, shouldOptimizeWires))
        return true;
    }
    
    return false;
  }
  
  private Point findFirstIntersection(Rectangle rectangle, Line2D line)
  {
    boolean isLineHorizontal = line.getY1() == line.getY2();
    
    double x1 = Math.min(line.getX1(), line.getX2());
    double x2 = Math.max(line.getX1(), line.getX2());
    double y1 = Math.min(line.getY1(), line.getY2());
    double y2 = Math.max(line.getY1(), line.getY2());
    
    if (isLineHorizontal)
    {
      if (x1 < rectangle.getMinX() && x2 > rectangle.getMinX())
        return new Point((int)rectangle.getMinX(), (int)y1);
      else if (x1 < rectangle.getMaxX() && x2 > rectangle.getMaxX())
        return new Point((int)rectangle.getMaxX(), (int)y1);
    }
    else
    {
      if (y1 < rectangle.getMinY() && y2 > rectangle.getMinY())
        return new Point((int)x1, (int)rectangle.getMinY());
      else if (y1 < rectangle.getMaxY() && y2 > rectangle.getMaxY())
        return new Point((int)x1, (int)rectangle.getMaxY());
    }
    
    return null;
  }
  
  
  public void removeWireSegmentAt(Circuit circuit, GraphicalCircuit gcircuit, int x, int y)
  {
    Node targetNode = getNodeAt(x, y);
    if (targetNode != null)
    {
      boolean isTargetNodeTheRoot = targetNode == rootNode;
      if (!isTargetNodeTheRoot)
      {
        rootNode = targetNode.parent;
        rootNode.children.remove(targetNode);
        convertToRoot(rootNode);
      }
      else
      {
        // An empty root node makes sure this wire will be deleted later
        Node newRoot = new Node();
        newRoot.connection = rootNode.connection;
        newRoot.isInputConnection = rootNode.isInputConnection;
        rootNode = newRoot;
      }

      applyExistingConnections(this);
      
      if (rootNode.children.isEmpty())
      {
        gcircuit.removeWire(this);
        wasRemoved();
        circuit.removeWire(wire);
      }
      
      boolean isAtExactPosition = targetNode.isAt(new Point(x, y));
      if (isAtExactPosition)
      {
        for (Node child : targetNode.children)
        {
          createIndependentWire(circuit, gcircuit, child, true);
        }
      }
      else
      {
        createIndependentWire(circuit, gcircuit, targetNode, true);
      }
    }
  }

  private void createIndependentWire(Circuit circuit,
      GraphicalCircuit gcircuit, Node child, boolean shouldOptimizeNewWire)
  {
    Wire newWire = new Wire(circuit.nextId());
    GraphicalWire newGWire = new GraphicalWire(new ArrayList<Point>(), newWire, null, null);
    newGWire.rootNode = child;
    newGWire.rootNode.parent = null;
    convertToRoot(child);

    applyExistingConnections(newGWire);
    
    circuit.addWire(newWire);
    gcircuit.addWire(newGWire);

    if (newGWire.rootNode.children.isEmpty())
    {
      gcircuit.removeWire(newGWire);
      newGWire.wasRemoved();
      circuit.removeWire(newWire);
    }
    
    if (shouldOptimizeNewWire)
      newGWire.optimize();
  }

  private void convertToRoot(Node newRootNode)
  {
    new WireRootConverter().convertToRoot(newRootNode);
  }
  
  private void applyExistingConnections(GraphicalWire wire)
  {
    wire.wire.removeAllTargets();
    applyExistingConnections(wire, wire.rootNode);
  }
  private void applyExistingConnections(GraphicalWire wire, Node currentNode)
  {
    if (currentNode.connection != null)
    {
      if (currentNode.isInputConnection)
      {
        currentNode.connection.component.setOutput(wire, currentNode.connection.pin);
      }
      else
      {
        currentNode.connection.component.setInput(wire, currentNode.connection.pin);
        wire.wire.addTarget(currentNode.connection.component.getComponent(), currentNode.connection.pin);
      }
    }
    
    for (Node child : currentNode.children)
      applyExistingConnections(wire, child);
  }
  
  
  public void optimize()
  {
    rootNode = new WireOptimizer().optimize(rootNode);
  }
  
  
  public Wire getWire()
  {
    return wire;
  }
  
  public boolean isSingleNode()
  {
    return rootNode.children.isEmpty();
  }
  
  public boolean isFullyConnected()
  {
    return hasInput() && hasOutput();
  }
  
  public boolean hasInput()
  {
    return hasConnection(rootNode, true);
  }
  
  public boolean hasOutput()
  {
    return hasConnection(rootNode, false);
  }
  
  private boolean hasConnection(Node node, boolean searchForInput)
  {
    if (node.connection != null && (node.isInputConnection == searchForInput))
      return true;
    
    for (Node child : node.children)
    {
      if (hasConnection(child, searchForInput))
        return true;
    }
    
    return false;
  }

  
  public void removeInput()
  {
    removeInput(rootNode);
  }
  
  private boolean removeInput(Node node)
  {
    if (node.connection != null && node.isInputConnection)
    {
      node.connection = null;
      node.isInputConnection = false;
      return true;
    }
    
    for (Node child : node.children)
    {
      if (removeInput(child))
        return true;
    }
    
    return false;
  }
  
  
  public void removeOutput(GraphicalComponent out, int pin)
  {
    wire.removeTarget(out.getComponent(), pin);
    removeOutput(rootNode, out.getComponent().getId(), pin);
  }
  
  private boolean removeOutput(Node node, int cId, int pin)
  {
    if (node.connection != null &&
        node.connection.component.getComponent().getId() == cId &&
        !node.isInputConnection &&
        node.connection.pin == pin)
    {
      node.connection = null;
      return true;
    }
    
    for (Node child : node.children)
    {
      if (removeOutput(child, cId, pin))
        return true;
    }
    
    return false;
  }
  
  
  @Override
  public void wasRemoved()
  {
    wasRemoved(rootNode);
  }
  
  private void wasRemoved(Node node)
  {
    if (node.connection != null)
    {
      if (node.isInputConnection)
      {
        node.connection.component.setOutput(null, node.connection.pin);
      }
      else
      {
        node.connection.component.setInput(null, node.connection.pin);
      }
    }
    
    for (Node child : node.children)
    {
      wasRemoved(child);
    }
  }
  
  
  private static class Connection implements Serializable
  {
    private static final long serialVersionUID = 1625996986406957200L;
    
    public GraphicalComponent component;
    public int pin;
    
    public Connection(GraphicalComponent component, int pin)
    {
      this.component = component;
      this.pin = pin;
    }
    
    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Connection)
      {
        Connection c = (Connection)obj;
        if (c.component.equals(component) && c.pin == pin)
          return true;
      }
      return false;
    }
  }
  
  
  public static class Node implements Serializable
  {
    private static final long serialVersionUID = 2066991428656566435L;

    public Node parent;
    public List<Node> children;
    
    public Point position;
    
    public boolean isInputConnection;
    public Connection connection;
    
    public Node()
    {
      children = new ArrayList<Node>();
      position = new Point();
    }
    
    public Node makeCopy(Circuit backend, Map<Integer, Object> copiedComponents)
    {
      Node copy = new Node();
      copy.position.setLocation(position);
      copy.isInputConnection = isInputConnection;
      if (connection != null)
      {
        GraphicalComponent component = (GraphicalComponent)copiedComponents.get(connection.component.getComponent().getId());
        if (component == null)
          component = connection.component.makeCopy(backend, copiedComponents);
        copy.connection = new Connection(component, connection.pin);
      }
      
      for (Node child : children)
      {
        Node newChild = child.makeCopy(backend, copiedComponents);
        newChild.parent = copy;
        copy.children.add(newChild);
      }
      
      return copy;
    }
    
    public boolean isAt(Point position)
    {
      return this.position.equals(position);
    }
  }
}
