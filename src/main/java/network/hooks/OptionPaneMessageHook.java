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
import gui.backend.ProgramWindow.MessageType;

import java.awt.EventQueue;

import snet.Client;
import snet.Server;
import snet.internal.Message;
import configuration.Table;

public class OptionPaneMessageHook extends ExtendedHook
{
  /**
   * If the message passed to the server consists of one part that should be
   *  looked up in the string table and one part that shouldn't, it is possible
   *  to pass the table key, followed på this separator, followed by the rest of
   *  the message.
   */
  public static final char TABLE_KEY_END_MARKER = '\u6543';
  
  
  public OptionPaneMessageHook(ProgramBackend backend)
  {
    super('\u1011', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    String data[] = message.extract().split("\\" + SEPARATOR);
    final MessageType type = MessageType.getTypeFromNumber(integer(data[0]));
    final String title = data[1];
    final String text = data[2];

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        backend.getWindow().openMessageDialog(format(text), format(title), type);
      }
    });
  }
  
  private String format(String text)
  {
    String result = null;
    int index = text.indexOf(TABLE_KEY_END_MARKER);
    if (index > -1)
    {
      result = Table.get(text.substring(0, index)) + text.substring(index + 1);
    }
    else
    {
      result = Table.get(text);
    }
    
    return result;
  }
  
  
  public Message createMessage(String title, String message, MessageType type)
  {
    return createMessage(type.value + SEPARATOR + title + SEPARATOR + message);
  }
}
