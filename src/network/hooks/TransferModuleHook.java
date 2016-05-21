/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network.hooks;

import graphicalcircuit.Module;
import gui.backend.ProgramBackend;
import gui.backend.ProgramWindow.DialogAnswer;
import gui.backend.ProgramWindow.MessageOptions;
import gui.backend.ProgramWindow.MessageType;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import snet.Client;
import snet.Server;
import snet.internal.Message;
import snet.internal.ServerConnection;
import sutilities.Debugger;
import configuration.Config;
import configuration.Table;
import database.ModuleLoader;

public class TransferModuleHook extends AbstractFileTransferHook
{
  public TransferModuleHook(ProgramBackend backend)
  {
    super('\u1035', backend);
  }

  @Override
  public void server(final Server server, Message message)
  {
    if (canUserEdit((ServerConnection)message.receiver))
    {
      final String senderId = ((ServerConnection)message.receiver).getId();
      final String moduleData = message.extract();
      
      EventQueue.invokeLater(new Runnable() {
        
        @Override
        public void run()
        {
          Module module = loadModuleFrom(moduleData);
          if (module != null)
          {
            try // TODO TransferModuleHook; Hosten ska få frågan skickad till sig på något sätt
            {
              File fileToSaveTo = new File(Config.get(Config.MODULE_FOLDER_PATH) + "/" + module.getName() + "." + Config.MODULE_EXTENSION);
              if (fileToSaveTo.exists() && !backend.getSession().getServer().getHostId().equals(senderId))
              {
                DialogAnswer result = backend.getWindow().openOptionDialog(
                    MessageFormat.format(Table.get("transfer_module_overwrite_message"), fileToSaveTo.getName()),
                    Table.get("transfer_module_overwrite_title"),
                    MessageOptions.YesNo,
                    MessageType.Question);
                if (result != DialogAnswer.Yes)
                {
                  Message msg = new OptionPaneMessageHook(null).createMessage(
                      "transfer_module_denied_title",
                      "transfer_module_denied_message",
                      MessageType.Error);
                  server.send(senderId, msg);
                  return;
                }
              }
              
              backend.getSession().getServer().saveModuleType(fileToSaveTo, module);
              backend.getSession().getServer().addModuleType(module);
              server.sendToAll(createMessage(moduleData));
            } catch (IOException e)
            {
              Debugger.error("TransferModuleHook: server()", "Failed to save the module " + module.getName() + "!", e);
              Message msg = new OptionPaneMessageHook(null).createMessage(
                  "popup_title_error",
                  "transfer_module_failed_to_save",
                  MessageType.Error);
              server.send(senderId, msg);
            }
          }
        }
      });
    }
  }

  @Override
  public void client(Client client, Message message)
  {
    String moduleData = message.extract();
    
    Module module = loadModuleFrom(moduleData);
    if (module != null)
    {
      backend.getSession().getClient().getWorkspace().database.addModuleType(module);
      backend.refreshComponentTree();
    }
  }
  
  private Module loadModuleFrom(String moduleData)
  {
    File tempFile = writeDataStringToTempFile(moduleData);
    
    try
    {
      if (tempFile != null)
      {
        Module module = new ModuleLoader(tempFile).load();
        return module;
      }
    } catch (ClassNotFoundException e)
    {
      // TODO TransferModuleHook; Ett riktigt felmeddelande? Detta borde aldrig kunna hända?
      Debugger.error("TransferModuleHook: loadModuleFrom()", "Failed to load the module!", e);
    } catch (IOException e)
    {
      // TODO TransferModuleHook; Ett riktigt felmeddelande? Detta borde aldrig kunna hända?
      Debugger.error("TransferModuleHook: loadModuleFrom()", "Failed to load the module!", e);
    }
    
    return null;
  }

  public Message createMessage(File fileToTransfer)
  {
    String fileContents = getFileAsString(fileToTransfer);
    
    return createMessage(fileContents);
  }
}
