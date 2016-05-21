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

import java.awt.EventQueue;

import network.Session;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import configuration.Table;

public class ServerClosingHook extends ExtendedHook
{
  public ServerClosingHook(ProgramBackend backend)
  {
    super('\u1017', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    final String text = message.extract();
    
    if (backend.getSession().getClient().isConnected())
    {
      backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_SERVER_CLOSED.text), Session.CHAT_SERVER_CLOSED.color));
      backend.refreshChat();
      
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run()
        {
          backend.getWindow().openMessageDialog(text, Table.get("popup_title_disconnected"), MessageType.Information);
          
          backend.getSession().startLocalServer();
          backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
          backend.refreshInterface();
          backend.refreshRenderingSurface();
          backend.refreshChat();
        }
      });
    }
  }
}
