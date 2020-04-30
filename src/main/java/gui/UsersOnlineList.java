/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import network.User;

public class UsersOnlineList extends JScrollPane implements Observer
{
  private LogicSim program;
  
  private JList<String> userList;
  private DefaultListModel<String> model;
  
  
  public UsersOnlineList(LogicSim program)
  {
    this.program = program;
    program.getBackend().addChatObserver(this);
    
    model = new DefaultListModel<String>();
    userList = new JList<String>(model);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    setPreferredSize(new Dimension(100, 10));
    setMinimumSize(new Dimension(100, 10));
    setBackground(Color.WHITE);
    setBorder(new LineBorder(Color.GRAY));
    
    setViewportView(userList);
  }
  
  
  @Override
  public void update(Observable o, Object arg)
  {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        Collection<User> users = program.getBackend().getSession().getUsers();
        model.clear();
        for (User user : users)
        {
          model.addElement(user.name);
        }
        repaint();
      }
    });
  }
}
