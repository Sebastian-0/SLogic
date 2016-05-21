/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui;

import gui.backend.ChatMessage;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import util.SimpleGridBagLayout;
import snet.internal.Message;
import network.hooks.SendUserChatMessageHook;
import configuration.Config;

public class ChatPanel extends JPanel implements Observer
{
  private LogicSim program;
  
  private ChatTextPanel chatWindow;
  private UsersOnlineList userOnline;
  
  private MessageTextField textField;
  private SendMessageButton sendMessageButton;
  
  
  public ChatPanel(LogicSim program)
  {
    this.program = program;
    program.getBackend().addChatObserver(this);
    
    int chatPanelHeight = Integer.parseInt(Config.get(Config.LAST_CHAT_PANEL_HEIGHT, "50"));
    chatWindow = new ChatTextPanel(getFont().deriveFont(Font.BOLD, 12f), chatPanelHeight);
    userOnline = new UsersOnlineList(program);
    
    textField = new MessageTextField(this);
    sendMessageButton = new SendMessageButton(this);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    layout.setInsets(2, 2, 2, 2);
    
    layout.addToGrid(chatWindow       , 0, 0, 1, 1, GridBagConstraints.BOTH, 1, 1);
    layout.addToGrid(userOnline       , 1, 0, 1, 1, GridBagConstraints.BOTH, 0, 1);
    layout.addToGrid(textField        , 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    layout.addToGrid(sendMessageButton, 1, 1, 1, 1, GridBagConstraints.NONE, 0, 0);
  }
  
  
  public void sendMessage()
  {
    if (textField.getText().trim().length() > 0)
    {
      Message message = new SendUserChatMessageHook(null).createMessage(textField.getText().trim());
      
      program.getBackend().getSession().getClient().getNetwork().send(null, message);
    }
    
    textField.setText("");
    textField.requestFocusInWindow();
  }
  
  
  @Override
  public void update(Observable o, Object arg)
  {
    Collection<ChatMessage> messages = program.getBackend().getAvailableMessages(); 
    for (ChatMessage msg : messages)
    {
      chatWindow.addMessage(msg.text, msg.color);
    }
    program.getBackend().clearChatMessages();
    repaint();
  }
}
