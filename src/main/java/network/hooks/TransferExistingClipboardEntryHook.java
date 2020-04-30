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
import java.io.IOException;

import network.Workspace;
import snet.Client;
import snet.Server;
import snet.internal.Message;
import sutilities.Debugger;
import database.ClipboardEntry;
import database.ClipboardEntryLoader;
import database.Database;

public class TransferExistingClipboardEntryHook extends AbstractFileTransferHook
{
  public TransferExistingClipboardEntryHook(ProgramBackend backend)
  {
    super('\u1047', backend);
  }

  @Override
  public void server(final Server server, Message message)
  {
  }

  @Override
  public void client(Client client, Message message)
  {
    String moduleData = message.extract();
    
    ClipboardEntry clipboardEntry = loadClipboardEntryFrom(moduleData);
    if (clipboardEntry != null)
    {
      Workspace workspace = backend.getSession().getClient().getWorkspace();
      workspace.clipboard.addEntry(clipboardEntry.getId(), clipboardEntry);
      backend.refreshComponentTree();
    }
  }
  
  private ClipboardEntry loadClipboardEntryFrom(String clipboardData)
  {
    File tempFile = writeDataStringToTempFile(clipboardData);
    
    try
    {
      if (tempFile != null)
      {
        Database database = backend.getSession().getClient().getWorkspace().database;
        ClipboardEntry entry = new ClipboardEntryLoader(tempFile).load(database.getCircuitTypes());
        return entry;
      }
    } catch (ClassNotFoundException e)
    {
      Debugger.error("TransferExistingClipboardEntryHook: loadClipboardEntryFrom()", "Failed to load clipboard entry!", e);
    } catch (IOException e)
    {
      Debugger.error("TransferExistingClipboardEntryHook: loadClipboardEntryFrom()", "Failed to load clipboard entry!", e);
    }
    
    return null;
  }

  public Message createMessage(File fileToTransfer)
  {
    String fileContents = getFileAsString(fileToTransfer);
    
    return createMessage(fileContents);
  }
}
