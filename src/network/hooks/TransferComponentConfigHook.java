/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import graphicalcircuit.GraphicalComponent;
import graphicalcircuit.config.ComponentConfig;
import gui.backend.ProgramBackend;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import database.Database;

public class TransferComponentConfigHook extends ExtendedHook {
  
  public TransferComponentConfigHook(ProgramBackend backend) {
    super ('\u1048', backend);
  }

  @Override
  public void server(Server server, Message message) {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      boolean succeeded = updateConfig(backend.getSession().getServer().getWorkspace().database, message);  
      if (succeeded)
        server.sendToAll(message);
    }
  }

  @Override
  public void client(Client client, Message message) {
    updateConfig(backend.getSession().getClient().getWorkspace().database, message);
  }

  private boolean updateConfig(Database database, Message message) {
    String[] data = message.extract().split("\\" + SEPARATOR);
    int id = integer(data[0]);
    String configData = data[1];
    
    GraphicalComponent component = database.getGraphicalCircuit().getComponent(id);
    if (component != null)
    {
      component.getConfig().decodeFromString(configData);
      return true;
    }
    
    return false;
  }

  public Message createMessage(int componentId, ComponentConfig config)
  {
    return createMessage(
        Integer.toString(componentId) + ExtendedHook.SEPARATOR +
        config.encodeIntoString());
  }
}
