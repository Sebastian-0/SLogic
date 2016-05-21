/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.connection;

import gui.backend.ChatMessage;
import gui.backend.ProgramUI;
import gui.dialogs.CancelButton;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import network.Session;
import util.SimpleGridBagLayout;
import configuration.Config;
import configuration.Table;

public class ConnectDialog extends JDialog
{
  private ProgramUI program;
  
  private JLabel ipLabel;
  private JLabel portLabel;
  private JLabel nameLabel;
  
  private JTextField ipField;
  private JTextField portField;
  private JTextField nameField;
  
  private ConnectButton connectButton;
  private CancelButton cancelButton;
  
  public ConnectDialog(ProgramUI program)
  {
    super((Frame) program, Table.get("join_title"));
    
    setModalityType(ModalityType.APPLICATION_MODAL);
    
    this.program = program;
    
    ipLabel = new JLabel(Table.get("join_ip"));
    portLabel = new JLabel(Table.get("join_port"));
    nameLabel = new JLabel(Table.get("join_username"));
    
    ipField = new JTextField(Config.get(Config.CLIENT_IP));
    portField = new JTextField(Config.get(Config.CLIENT_PORT));
    nameField = new JTextField(Config.get(Config.USER_NAME));
    
    ipField.setPreferredSize(new Dimension(100, 20));
    portField.setPreferredSize(new Dimension(50, 20));
    
    ipField.addActionListener(actionListener);
    portField.addActionListener(actionListener);
    nameField.addActionListener(actionListener);
    
    connectButton = new ConnectButton(this);
    cancelButton = new CancelButton(this);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    layout.addToGrid(ipLabel  , 0, 0, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.EAST);
    layout.addToGrid(ipField  , 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    layout.addToGrid(portLabel, 2, 0, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.EAST);
    layout.addToGrid(portField, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    layout.addToGrid(nameLabel, 0, 1, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.EAST);
    layout.addToGrid(nameField, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    layout.setInsets(15, 5, 5, 5);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(connectButton); buttonPanel.add(cancelButton);
    layout.addToGrid(buttonPanel, 0, 2, 4, 1, GridBagConstraints.NONE, 0, 0);
    
    pack();
    
    setResizable(false);
    setLocationRelativeTo((Component) program);
  }
  
  
  public void connect()
  {
    String ip = ipField.getText();
    int port = Integer.parseInt(portField.getText());
    String username = nameField.getText();
    
    Config.put(Config.CLIENT_IP, ip);
    Config.put(Config.CLIENT_PORT, Integer.toString(port));
    Config.put(Config.USER_NAME, username);

    program.getBackend().getSession().connectTo(ip, port, username);
    program.getBackend().putChatMessage(new ChatMessage(Table.get(Session.CHAT_JOINING_SERVER.text), Session.CHAT_JOINING_SERVER.color));
    program.getBackend().refreshChat();
    program.getBackend().refreshInterface();
    
    dispose();
  }
  
  private ActionListener actionListener = new ActionListener() {
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
      connect();
    }
  };
}
