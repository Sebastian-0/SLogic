/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import gui.backend.ProgramBackend;

import java.awt.Point;

import network.Workspace;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import database.CircuitClipboard;
import database.ClipboardEntry;

public class CreateClipboardEntryHook extends ExtendedHook
{
  public CreateClipboardEntryHook(ProgramBackend backend)
  {
    super('\u1041', backend);
  }
  

  @Override
  public void server(Server server, Message message)
  {
    ServerConnection receiver = (ServerConnection)message.receiver;
    if (canUserEdit(receiver))
    {
      String[] data = message.extract().split("\\" + SEPARATOR);
      Point start = new Point(integer(data[0]), integer(data[1]));
      Point end = new Point(integer(data[2]), integer(data[3]));
      
      CircuitClipboard clipboard = backend.getSession().getServer().getWorkspace().clipboard;
      clipboard.addEntry(
          receiver.getId(),
          ClipboardEntry.createClipboardEntry(
              backend.getSession().getServer().getWorkspace().database.getCircuit(),
              backend.getSession().getServer().getWorkspace().database.getGraphicalCircuit(),
              start,
              end));
      
      server.sendToAll(createMessage(receiver.getId() + SEPARATOR + message.extract()));
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    String userId = data[0];
    Point start = new Point(integer(data[1]), integer(data[2]));
    Point end = new Point(integer(data[3]), integer(data[4]));

    Workspace workspace = backend.getSession().getClient().getWorkspace();
    CircuitClipboard clipboard = workspace.clipboard;
    clipboard.addEntry(
        userId,
        ClipboardEntry.createClipboardEntry(
            workspace.database.getCircuit(),
            workspace.database.getGraphicalCircuit(),
            start,
            end));

    backend.refreshRenderingSurface();
  }
  
  
  /**
   * Creates a message describing a clipboard creation.
   * @param selectionStart The start of the selection (lowest x/y-coordinates)
   * @param selectionEnd The end of the selection (highest x/y-coordinates)
   * @return A message that can be sent over the network
   */
  public Message createMessage(Point selectionStart, Point selectionEnd)
  {
    return createMessage(
        selectionStart.x + SEPARATOR + selectionStart.y + SEPARATOR +
        selectionEnd.x + SEPARATOR + selectionEnd.y);
  }
}
