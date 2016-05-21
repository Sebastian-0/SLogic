/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import gui.backend.ChatMessage;
import gui.backend.ProgramBackend;
import gui.backend.ProgramWindow.MessageType;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import network.Session;
import network.User.Privileges;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import configuration.Config;
import configuration.PasswordProtectionLevel;
import configuration.Table;

public class AuthenticationHook extends ExtendedHook
{
  public AuthenticationHook(ProgramBackend backend)
  {
    super('\u1042', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    String userId = ((ServerConnection)message.receiver).getId();
    
    char answerCode = message.extract().charAt(0);
    
    boolean allowReadOnlyUsers = PasswordProtectionLevel.getTypeByName(Config.get(Config.PROTECTION_LEVEL)) == PasswordProtectionLevel.AllowRead;
    if (answerCode == '1' && allowReadOnlyUsers)
    {
      backend.getSession().getServer().userConnected(userId, Privileges.Read);
      
      Message msg = new ClientStatusHook(null).createMessage(Privileges.Read, false);
      server.send(userId, msg);
    }
    else
    {
      String specifiedPassword = message.extract().substring(1);
      String requestedPassword = Config.get(Config.PASSWORD, "");
  
      if (specifiedPassword.equals(requestedPassword))
      {
        backend.getSession().getServer().userConnected(userId, Privileges.ReadWrite);
        
        Message msg = new ClientStatusHook(null).createMessage(Privileges.ReadWrite, false);
        server.send(userId, msg);
      }
      else
      {
        Message errorMsg = new ClientDroppedHook(null).createMessage(
            "password_incorrect_title",
            "password_incorrect_message",
            MessageType.Error);
        server.send(userId, errorMsg);
        server.drop(userId);
      }
    }
  }

  @Override
  public void client(final Client client, Message message)
  {
    final PasswordProtectionLevel protectionLevel = PasswordProtectionLevel.getTypeByName(message.extract());
    
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        JLabel passwordLabel = new JLabel(Table.get("password_enter_password"));
        JPasswordField passwordField = new JPasswordField();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(passwordLabel, BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);

        String password = "";
        int answerCode = 0;
        if (protectionLevel == PasswordProtectionLevel.AllowRead)
        {
          int result = JOptionPane.showOptionDialog(
              JFrame.getFrames()[0],
              panel,
              Table.get("password_title"),
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              new String[] { Table.get("password_join"), Table.get("password_skip_password"), Table.get("button_cancel") },
              null);
          
          if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION)
          {
            disconnectFromServer();
            return;
          }
          else if (result == JOptionPane.NO_OPTION)
          {
            answerCode = 1;
          }
          
          password = new String(passwordField.getPassword());
        }
        else
        {
          int result = JOptionPane.showOptionDialog(
              JFrame.getFrames()[0],
              panel,
              Table.get("password_title"),
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              new String[] { Table.get("password_join"), Table.get("button_cancel") },
              null);
          
          if (result != JOptionPane.YES_OPTION)
          {
            disconnectFromServer();
            return;
          }
          
          password = new String(passwordField.getPassword());
        }
        
        client.send(null, createMessage(answerCode + password));
        // Send user name to server
        backend.getSession().getClient().setUsername(Config.get(Config.USER_NAME));
      }

      private void disconnectFromServer()
      {
        backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
        backend.getSession().startLocalServer();
        backend.refreshInterface();
        backend.refreshRenderingSurface();
        backend.refreshChat();
      }
    });
  }
}
