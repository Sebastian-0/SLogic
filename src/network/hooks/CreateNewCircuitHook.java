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
import network.Session;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import configuration.Table;

public class CreateNewCircuitHook extends ExtendedHook
{
  public CreateNewCircuitHook(ProgramBackend backend)
  {
    super('\u1028', backend);
  }
  

  @Override
  public void server(Server server, Message message)
  {
    String senderId = ((ServerConnection)message.receiver).getId();
    if (senderId.equals(backend.getSession().getServer().getHostId()))
    {
      backend.getSession().getServer().getWorkspace().createEmptyCircuit();
      server.sendToAll(message);
    }
    else
    {
      Message error = new OptionPaneMessageHook(null).createMessage(
          "popup_title_error",
          "error_message_only_host_can_make_new_circuit",
          MessageType.Error);
      server.send(senderId, error);
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    backend.getSession().getClient().getWorkspace().createEmptyCircuit();
    backend.putChatMessage(new ChatMessage(Table.get(Session.CHAT_NEW_CIRCUIT.text), Session.CHAT_NEW_CIRCUIT.color));
    backend.refreshChat();
    backend.refreshRenderingSurface(true);
  }
}
