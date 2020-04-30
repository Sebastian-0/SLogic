/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observer;

import network.Session;

public class ProgramBackend
{
  private GuiUpdater componentTreeUpdater;
  private GuiUpdater interfaceUpdater;
  private GuiUpdater renderingUpdater;
  private GuiUpdater chatUpdater;
  
  private ProgramWindow window;
  
  private Session session;
  
  private List<ChatMessage> availableMessages;
  
  
  public ProgramBackend(ProgramWindow window)
  {
    this.window = window;
    
    componentTreeUpdater = new GuiUpdater();
    interfaceUpdater = new GuiUpdater();
    renderingUpdater = new GuiUpdater();
    chatUpdater = new GuiUpdater();

    session = new Session(this);
    
    availableMessages = Collections.synchronizedList(new ArrayList<ChatMessage>());
  }
  
  
  public void putChatMessage(ChatMessage message)
  {
    availableMessages.add(message);
  }
  
  public void clearChatMessages()
  {
    availableMessages.clear();
  }
  
  
  
  public void addComponentTreeObserver(Observer o)
  {
    componentTreeUpdater.addObserver(o);
  }
  
  public void addInterfaceObserver(Observer o)
  {
    interfaceUpdater.addObserver(o);
  }
  
  public void addRenderingObserver(Observer o)
  {
    renderingUpdater.addObserver(o);
  }
  
  public void addChatObserver(Observer o)
  {
    chatUpdater.addObserver(o);
  }
  
  
  public void refreshInterface()
  {
    interfaceUpdater.setChanged();
    interfaceUpdater.notifyObservers(this);
  }
  
  public void refreshRenderingSurface()
  {
    refreshRenderingSurface(false);
  }
  public void refreshRenderingSurface(boolean hasNewCircuit)
  {
    renderingUpdater.setChanged();
    renderingUpdater.notifyObservers(hasNewCircuit);
  }
  
  public void refreshChat()
  {
    chatUpdater.setChanged();
    chatUpdater.notifyObservers();
  }
  
  public void refreshComponentTree()
  {
    componentTreeUpdater.setChanged();
    componentTreeUpdater.notifyObservers();
  }

  
  
  public ProgramWindow getWindow()
  {
    return window;
  }
  
  public Session getSession()
  {
    return session;
  }
  
  public Collection<ChatMessage> getAvailableMessages()
  { 
    return new ArrayList<ChatMessage>(availableMessages);
  }
}