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

import java.awt.Color;

import snet.Client;
import snet.Server;
import snet.internal.Message;
import configuration.Table;

public class SendInformationChatMessageHook extends ExtendedHook
{
  public SendInformationChatMessageHook(ProgramBackend backend)
  {
    super('\u1019', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    server.sendToAll(message);
  }

  @Override
  public void client(Client client, Message message)
  {
    String[] data = message.extract().split("\\" + SEPARATOR);
    
    String text = data[0];
    backend.putChatMessage(new ChatMessage(Table.get(text), new Color(integer(data[1]))));
    backend.refreshChat();
  }
  
  
  public Message createMessage(String text, Color color)
  {
    return createMessage(text + SEPARATOR + color.getRGB());
  }
}
