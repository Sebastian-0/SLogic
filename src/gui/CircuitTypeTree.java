/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import graphicalcircuit.CircuitType;
import graphicalcircuit.CircuitType.Category;
import graphicalcircuit.types.ModuleType;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import network.Workspace;
import configuration.Table;
import database.Database;

public class CircuitTypeTree extends JTree implements Observer
{
  private LogicSim program;
  private Workspace workspace;

  private DefaultMutableTreeNode moduleNode;
  
  public CircuitTypeTree(LogicSim program)
  {
    this.program = program;
    workspace = program.getBackend().getSession().getClient().getWorkspace();
    program.getBackend().addComponentTreeObserver(this);
    
    DefaultMutableTreeNode treeRootNode = new DefaultMutableTreeNode(Table.get("tree_root"));
    addSubFolder(treeRootNode, Table.get("tree_category_io"), Category.IO);
    addSubFolder(treeRootNode, Table.get("tree_category_gates"), Category.GATE);
    addSubFolder(treeRootNode, Table.get("tree_category_registers"), Category.REGISTER);
    moduleNode = addSubFolder(treeRootNode, Table.get("tree_category_modules"), Category.MODULE);
    addSubFolder(treeRootNode, Table.get("tree_category_other"), Category.OTHER);
    setModel(new DefaultTreeModel(treeRootNode));
    setEditable(false);

    expandRow(2);
    expandRow(1);

    InputMap map = getInputMap(JComponent.WHEN_FOCUSED);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "none");
    
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    addTreeSelectionListener(treeListener);
    addMouseListener(mouseListener);
    setCellRenderer(new CustomCellRenderer());
    
    ToolTipManager.sharedInstance().registerComponent(this);
  }
  
  private DefaultMutableTreeNode addSubFolder(DefaultMutableTreeNode root, String folderName, Category category)
  {
    DefaultMutableTreeNode subFolder = new DefaultMutableTreeNode(folderName);
    Database database = workspace.database;
    for (CircuitType type : database.getCircuitTypes())
    {
      if (type.getCategory() == category)
      {
        makeNodeFor(subFolder, type);
      }
    }
    root.add(subFolder);
    
    return subFolder;
  }
  
  private void makeNodeFor(DefaultMutableTreeNode parent, CircuitType type)
  {
    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(type);
    parent.add(newNode);
  }
  
  
  @Override
  public void update(Observable o, Object arg)
  {
    moduleNode.removeAllChildren();
    Database database = workspace.database;
    for (CircuitType type : database.getCircuitTypes())
    {
      if (type.getCategory() == Category.MODULE)
      {
        makeNodeFor(moduleNode, type);
      }
    }
    
    ((DefaultTreeModel)getModel()).reload(moduleNode);
  }
  
  
  @Override
  public String getToolTipText(MouseEvent event)
  {
    TreePath tp = getPathForLocation(event.getX(), event.getY());
    
    if (tp != null)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
      
      Object userObject = node.getUserObject();
      if (userObject instanceof ModuleType)
      {
        String description = ((ModuleType) userObject).getDescription();
        return "<html>" + description.replaceAll("\n", "<br>") + "</html>";
      }
    }
    
    return null;
  }

  
  
  private TreeSelectionListener treeListener = new TreeSelectionListener()
  {
    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
      
      if (node != null && node.getUserObject() instanceof CircuitType)
      {
        workspace.selectedCircuitType = (CircuitType) node.getUserObject();
      }
      else
      {
        workspace.selectedCircuitType = null;
      }
      program.repaint();
    }
  };
  
  
  // Used to force the tooltips of this component to never disappear
  private MouseListener mouseListener = new MouseAdapter() {
    
    private final int defaultTooltipTime = ToolTipManager.sharedInstance().getDismissDelay();
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
      ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
      ToolTipManager.sharedInstance().setDismissDelay(defaultTooltipTime);
    }
  };
  
  
  private static class CustomCellRenderer extends DefaultTreeCellRenderer
  {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (value instanceof DefaultMutableTreeNode)
      {
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
        if (userObject instanceof CircuitType)
          setIcon(UIManager.getIcon("Tree.leafIcon"));
        else
          setIcon(UIManager.getIcon("Tree.openIcon"));
      }
      
      return this;
    }
  }
}
