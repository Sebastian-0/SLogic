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
import network.LogicServer;
import network.User.Privileges;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import configuration.Config;

public class ClientGreetingHook extends ExtendedHook
{

  public ClientGreetingHook(ProgramBackend backend)
  {
    super('\u1010', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
    LogicServer logicServer = backend.getSession().getServer();
    
    String idOfNewUser = ((ServerConnection)message.receiver).getId();
    
    int secureHostIdentifier = integer(message.extract());
    boolean isHost = logicServer.isAcceptableAsHost(secureHostIdentifier);
    if (!isHost && Config.get(Config.USE_PASSWORD_PROTECTION).equals("true"))
    {
      server.send(idOfNewUser, new AuthenticationHook(null).createMessage(Config.get(Config.PROTECTION_LEVEL)));
    }
    else
    {
      logicServer.userConnected(idOfNewUser, Privileges.ReadWrite);
      
      if (isHost && logicServer.getHostId() == null)
        logicServer.setHostId(idOfNewUser);
      
      Message msg = new ClientStatusHook(null).createMessage(Privileges.ReadWrite, isHost);
      server.send(idOfNewUser, msg);
    }
  }

  @Override
  public void client(Client client, Message message)
  {
  }
  
  
  public Message createMessage(int secureHostIdentifier)
  {
    return createMessage(Integer.toString(secureHostIdentifier));
  }
}
