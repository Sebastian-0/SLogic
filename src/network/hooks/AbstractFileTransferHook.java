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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import sutilities.Debugger;

public abstract class AbstractFileTransferHook extends ExtendedHook
{

  public AbstractFileTransferHook(char commandCode, ProgramBackend backend)
  {
    super(commandCode, backend);
  }
  
  protected File writeDataStringToTempFile(String data)
  {
    try
    {
      byte[] fileData = data.getBytes(Charset.forName("ISO-8859-1"));
      
      File tempFile = Files.createTempFile("CLS", ".tmp").toFile();
      tempFile.deleteOnExit();
      
      FileOutputStream fileStream = new FileOutputStream(tempFile);
      fileStream.write(fileData, 0, fileData.length);
      fileStream.close();
      
      return tempFile;
    } catch (IOException e)
    {
      // TODO AbstractTransferFileHook; Ett riktigt felmeddelande? Detta borde aldrig kunna hända?
      Debugger.error("AbstractFileTransferHook: writeDataStringToTempFile()", "Failed to save the transfered data to a file! (implementation: " + getClass().getName() + ")", e);
    }
    
    return null;
  }
  
  protected String getFileAsString(File file)
  {
    try
    {
      byte[] data = Files.readAllBytes(file.toPath());
      return new String(data, Charset.forName("ISO-8859-1"));
    } catch (IOException e)
    {
      // TODO AbstractTransferFileHook; Ett riktigt felmeddelande? Detta borde aldrig kunna hända
      Debugger.error("AbstractFileTransferHook: getFileAsString()", "Failed to read the file " + file.getPath(), e);
    }
    
    return null;
  }
}
