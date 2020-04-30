/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.menubar;

import gui.backend.ChatMessage;
import gui.backend.ProgramBackend;
import gui.backend.ProgramUI;

import java.util.Observable;
import java.util.Observer;

import network.Session;
import configuration.Table;

public class DisconnectMenuItem extends AbstractMenuItem implements Observer
{
  public DisconnectMenuItem(ProgramUI window)
  {
    super(Table.get("menubar_disconnect"), window);
    
    window.getBackend().addInterfaceObserver(this);

    setEnabled(window.getBackend().getSession().isClient());
  }
  
  @Override
  public void update(Observable o, Object arg)
  {
    ProgramBackend backend = (ProgramBackend)arg;
    setEnabled(backend.getSession().isClient());
  }

  @Override
  protected void doAction(ProgramUI program)
  {
    program.getBackend().putChatMessage(new ChatMessage(Table.get(Session.CHAT_LEFT_SERVER.text), Session.CHAT_LEFT_SERVER.color));
    program.getBackend().putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
    program.getBackend().getSession().startLocalServer();
    program.getBackend().refreshInterface();
    program.getBackend().refreshRenderingSurface();
    program.getBackend().refreshChat();
  }
}
