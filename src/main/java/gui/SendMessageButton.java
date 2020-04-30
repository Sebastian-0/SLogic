/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import configuration.Table;

public class SendMessageButton extends JButton
{
  private ChatPanel chatPanel;
  
  public SendMessageButton(ChatPanel chatPanel)
  {
    super(Table.get("chat_send_button"));
    this.chatPanel = chatPanel;
    addActionListener(actionListener);
  }
  
  
  private ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      chatPanel.sendMessage();
    }
  };
}
