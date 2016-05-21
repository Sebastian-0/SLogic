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
import java.awt.Point;

import network.Session;
import network.User;
import network.Workspace;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import sutilities.Debugger;
import configuration.Table;
import database.CircuitClipboard;
import database.ClipboardEntry;

public class PasteClipboardEntryHook extends ExtendedHook
{
  public PasteClipboardEntryHook(ProgramBackend backend)
  {
    super('\u1045', backend);
  }
  

  @Override
  public void server(Server server, Message message)
  {
    ServerConnection receiver = (ServerConnection)message.receiver;
    if (canUserEdit(receiver))
    {
      String[] data = message.extract().split("\\" + SEPARATOR);
      Point destination = new Point(integer(data[0]), integer(data[1]));
      
      CircuitClipboard clipboard = backend.getSession().getServer().getWorkspace().clipboard;
      ClipboardEntry entry = clipboard.getEntry(receiver.getId());
      if (entry != null)
      {
        entry.pasteCopy(
            backend.getSession().getServer().getWorkspace().database.getCircuit(),
            backend.getSession().getServer().getWorkspace().database.getGraphicalCircuit(),
            destination);
        
        server.sendToAll(createMessage(receiver.getId() + SEPARATOR + message.extract()));
      }
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    String userId = data[0];
    Point destination = new Point(integer(data[1]), integer(data[2]));

    Workspace workspace = backend.getSession().getClient().getWorkspace();
    CircuitClipboard clipboard = workspace.clipboard;
    ClipboardEntry entry = clipboard.getEntry(userId);
    if (entry != null)
    {
      entry.pasteCopy(
          workspace.database.getCircuit(),
          workspace.database.getGraphicalCircuit(),
          destination);

      backend.refreshRenderingSurface();
    }
    else
    {
      missingClipboardEntryForUser(userId);
    }
  }


  private void missingClipboardEntryForUser(String userId)
  {
    User user = backend.getSession().getUser(userId);
    String name = null;
    if (user != null)
      name = user.name;
    Debugger.error("PasteClipboardHook: client()", "The client is missing the clipboard entry for the user " + name);
    
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        backend.getWindow().openMessageDialog(
            Table.get("error_message_missing_clipboard_entry"),
            Table.get("popup_title_disconnected"),
            MessageType.Error);
      }
    });
     
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_LEFT_SERVER.text), Session.CHAT_LEFT_SERVER.color));
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_HOSTED_SERVER.text), Session.CHAT_HOSTED_SERVER.color));
    backend.getSession().startLocalServer();
    backend.refreshInterface();
    backend.refreshRenderingSurface();
    backend.refreshChat();
  }
  
  
  /**
   * Creates a message describing a paste-event. The specified destination must
   *  be relative to the selection end coordinates specified when the clipboard
   *  entry was created using
   *  {@link CreateClipboardEntryHook#createMessage(Point, Point)}.
   * @param destination The upper left corner of the destination
   * @return A message that can be sent over the network
   */
  public Message createMessage(Point destination)
  {
    return createMessage(destination.x + SEPARATOR + destination.y);
  }
}
