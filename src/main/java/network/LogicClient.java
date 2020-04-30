/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network;

import network.User.Privileges;
import network.hooks.ClientGreetingHook;
import network.hooks.SetUsernameHook;
import snet.Client;
import snet.ClientLifecycleListener;
import snet.internal.ConnectionManagerInterface.DisconnectReason;

public class LogicClient
{
  private Client client;
  
  private Workspace workspace;
  
  private int secureHostIdentifier;
  private Privileges privileges;
  private String username;

  private ClientListener clientListener;
  
  
  public LogicClient(ClientListener listener)
  {
    client = new Client(connectionListener);
    client.setUseTcpNoDelay(true);
    
    workspace = new Workspace();
    
    this.clientListener = listener;
    username = "A User";
  }
  
  
  /**
   * Sets the identifier number sent to the server when joining. This number is used to
   *  identify who the host is, only the host knows about this number. 
   * @param secureHostIdentifier The host identifier number
   */
  public void setSecureHostIdentifier(int secureHostIdentifier)
  {
    this.secureHostIdentifier = secureHostIdentifier;
  }
  
  public void setPrivileges(Privileges privileges)
  {
    this.privileges = privileges;
  }
  
  public void setUsername(String name)
  {
    username = name;
    if (client.isConnected())
    {
      client.send(null, new SetUsernameHook(null).createMessage(username));
    }
  }
  
  
  public void connectTo(String host, int port)
  {
    client.disconnect();
    client.start(host, port);
  }
  
  public void disconnect()
  {
    client.disconnect();

    workspace.database.clear();
    workspace.database.clearModuleTypes();
    workspace.tool.reset();
  }
  
  
  public Client getNetwork()
  {
    return client;
  }
  
  public Workspace getWorkspace() {
    return workspace;
  }
  
  public Privileges getPrivileges()
  {
    return privileges;
  }
  
  public boolean isConnected()
  {
    return client.isConnected();
  }
  
  
  private ClientLifecycleListener connectionListener = new ClientLifecycleListener() 
  {
    @Override
    public void failedToEstablishConnection() 
    {
      clientListener.connectionFailed();
    }

    @Override
    public void connected()
    {
      client.send(null, new ClientGreetingHook(null).createMessage(secureHostIdentifier));
      client.send(null, new SetUsernameHook(null).createMessage(username));
      
      clientListener.connected();
    }

    @Override
    public void disconnected(DisconnectReason reason)
    {
      clientListener.disconnected(reason);
    }
  };
}
