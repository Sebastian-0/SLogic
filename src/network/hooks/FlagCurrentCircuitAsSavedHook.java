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

import java.io.File;

import network.Workspace;
import snet.Client;
import snet.Server;
import snet.internal.Message;

public class FlagCurrentCircuitAsSavedHook extends ExtendedHook
{
  public FlagCurrentCircuitAsSavedHook(ProgramBackend backend)
  {
    super('\u1027', backend);
  }

  @Override
  public void server(Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    Workspace workspace = backend.getSession().getClient().getWorkspace();
    if (!workspace.hasSaveLocation())
      workspace.currentSaveLocation = new File("");
  }

}
