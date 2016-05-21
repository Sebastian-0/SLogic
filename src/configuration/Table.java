/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import sutilities.BasicUtils;
import sutilities.Debugger;

/**
 * A class that contains the current language table. String keys are passed to
 *  this class to get the current mapped text associated with that key. The
 *  languages are specified within special language table zip files or folders.
 *  All files for a specific language should be stored inside a folder or a zipfile.
 *  These files can either be text files or table files (files with the .table extension).
 *  For text files the entire file will be mapped with the file name as the key.
 *  Table files should contain rows of data formatted as <code>key_name = value</code>.
 *  <br />
 *  <br />
 *  Example of folder/zip file structure:
 *  <p>
 *  <code>
 *  &emsp;English.lang
 *  <br />&emsp;-> game_strings.table
 *  <br />&emsp;-> credits.txt
 *  </code>
 *  </p>
 * @author Sebastian Hjelm, Jakob Hjelm
 */
public class Table
{
  private static List<Table> tables;
  private static Table       currentTable;
  
  private String                  tableName;
  private HashMap<String, String> mappings;
  
  
  static
  {
    tables = new ArrayList<Table>();
  }
  
	
	private Table()
	{
	  mappings = new HashMap<String, String>();
	}
  
  
  /**
   * Returns the text associated with the specified key, using the current
   *  language.
   * @param key The key of the text to return
   * @return The text associated with the key, or the key if no association was
   *  found
   */
	public static String get(String key)
	{
	  if (currentTable != null)
	  {
	    String result = currentTable.mappings.get(key);
	    if (result != null)
	      return result;
	  }

	  return key;
	}
	
	/**
	 * Returns a list of all available language tables.
	 * @return All available language tables
	 */
	public static List<Table> getLanguages() { return tables; }
	
	/**
	 * Returns the language table with the specified name. The name of the language
	 *  table is set to the name of the file/folder (without extension) it was loaded
	 *  from.
	 * @param name The name of the language table
	 * @return The language table with the specified name, or <code>null</code>
	 */
	public static Table getLanguage(String name)
	{
	  for (Table table : tables)
	  {
	    if (table.getName().equals(name))
	      return table;
	  }
	  
	  return null;
	}
	
	
	/**
	 * Returns the current active language table.
	 * @return the current active language table
	 */
	public static Table getLanguage() { return currentTable; }

  
  /**
   * Sets the current language table to the specified table. Also changes the
   *  current {@code Locale} to the one specified within the table.
   * @param languageTable The file containing the new language table
   * @throws NullPointerException If the specified table is null
   */
  public static void setLanguage(Table languageTable)
  {
    currentTable = languageTable;
    
    if (currentTable != null)
    {
      Locale.setDefault(new Locale(get("java_locale_language"),
          get("java_locale_country")));
      
      UIManager.put("OptionPane.yesButtonText"   , get("optionpane_yes"));
      UIManager.put("OptionPane.noButtonText"    , get("optionpane_no"));
      UIManager.put("OptionPane.cancelButtonText", get("optionpane_cancel"));
      UIManager.put("OptionPane.okButtonText"    , get("optionpane_ok"));

      UIManager.put("ColorChooser.swatchesNameText"   , get("colorchooser_swatches_name"));
      UIManager.put("ColorChooser.swatchesRecentText" , get("colorchooser_swatches_recent"));
      UIManager.put("ColorChooser.hsvNameText"        , get("colorchooser_hsv_name"));
      UIManager.put("ColorChooser.hsvHueText"         , get("colorchooser_hsv_hue"));
      UIManager.put("ColorChooser.hsvSaturationText"  , get("colorchooser_hsv_saturation"));
      UIManager.put("ColorChooser.hsvValueText"       , get("colorchooser_hsv_value"));
      UIManager.put("ColorChooser.hsvTransparencyText", get("colorchooser_hsv_transparency"));
      UIManager.put("ColorChooser.sampleText"         , get("colorchooser_sample_text"));
      UIManager.put("ColorChooser.previewText"        , get("colorchooser_preview_text"));
      
      UIManager.put("FileChooser.fileNameLabelText"      , get("filechooser_file_name"));
      UIManager.put("FileChooser.filesOfTypeLabelText"   , get("filechooser_files_of_type"));
      UIManager.put("FileChooser.cancelButtonText"       , get("filechooser_cancel"));
      UIManager.put("FileChooser.cancelButtonToolTipText", get("filechooser_cancel_tooltip"));
      UIManager.put("FileChooser.directoryOpenButtonText", get("filechooser_open_folder"));
      UIManager.put("FileChooser.directoryOpenButtonToolTipText", get("filechooser_open_folder_tooltip"));
      
      UIManager.put("FileChooser.lookInLabelText"             , get("filechooser_look_in"));
      UIManager.put("FileChooser.upFolderToolTipText"         , get("filechooser_up_folder_tooltip"));
      UIManager.put("FileChooser.homeFolderToolTipText"       , get("filechooser_home_folder_tooltip"));
      UIManager.put("FileChooser.newFolderToolTipText"        , get("filechooser_new_folder_tooltip"));
      UIManager.put("FileChooser.listViewButtonToolTipText"   , get("filechooser_list_view_tooltip"));
      UIManager.put("FileChooser.detailsViewButtonToolTipText", get("filechooser_detailed_view_tooltip"));
      
      UIManager.put("FileChooser.fileNameHeaderText", get("filechooser_file_name_header"));
      UIManager.put("FileChooser.fileSizeHeaderText", get("filechooser_file_size_header"));
      UIManager.put("FileChooser.fileDateHeaderText", get("filechooser_date_modified_header"));
      
      UIManager.put("FileChooser.viewMenuLabelText", get("filechooser_view_item"));
      UIManager.put("FileChooser.refreshActionLabelText", get("filechooser_refresh_item"));
      UIManager.put("FileChooser.newFolderActionLabelText", get("filechooser_new_folder_item"));
      UIManager.put("FileChooser.listViewActionLabelText", get("filechooser_view_list_item"));
      UIManager.put("FileChooser.detailsViewActionLabelText", get("filechooser_view_details_item"));
      
      UIManager.put("FileChooser.acceptAllFileFilterText", get("filechooser_accept_all_filter"));
      
      UIManager.put("FileChooser.renameErrorTitleText", get("filechooser_rename_error_title"));
      UIManager.put("FileChooser.renameErrorText", get("filechooser_rename_error_message"));
      
      UIManager.put("FileChooser.newFolderErrorText",get("filechooser_new_folder_error_message"));

      UIManager.put("FileChooser.newFolderParentDoesntExistTitleText", get("filechooser_new_folder_error_no_parent_title"));
      UIManager.put("FileChooser.newFolderParentDoesntExistText", get("filechooser_new_folder_error_no_parent_message"));
      
      if (!isValidLocale(Locale.getDefault().toString()))
      {
        // TODO Table; Flytta detta felmeddelandet till en GUI-klass?
        JOptionPane.showMessageDialog(JFrame.getFrames()[0],
            get("invalid_locale_data") + ": \"" + Locale.getDefault() + "\"",
            get("popup_title_error") + " - Invalid locale in language file",
            JOptionPane.ERROR_MESSAGE);
        
        Locale.setDefault(Locale.UK);
      }
    }
    else
    {
      throw new NullPointerException("The table can't be null!");
    }
  }
  
  
  /**
   * Loads all language files from the specified folder and stores them in
   *  this class. Only zip files or folders which have the specified suffix will be loaded.
   * @param langTableFolder The source folder
   * @param suffix The suffix for the language files/folders.
   * @throws IllegalArgumentException If the source folder doesn't exist
   */
  public static void loadLanguages(File langTableFolder, String suffix)
  {
    if (!suffix.startsWith("."))
      suffix = "." + suffix;
    
    if (langTableFolder != null && langTableFolder.isDirectory())
    {
      File[] languages = langTableFolder.listFiles();
      for (File file : languages)
      {
        if (file.getName().endsWith(suffix))
        {
          Table table = new Table();
          table.tableName = file.getName().substring(0, file.getName().length() - suffix.length());
        
          loadStringTableData(file, table);
          
          tables.add(table);
        }
      }
      
    }
    else 
    {
      if (langTableFolder != null)
        throw new IllegalArgumentException("Invalid source file: " + langTableFolder.getAbsolutePath());
    }
  }
  
  
  
  private static void loadStringTableData(File file, Table targetTable)
  {
    // Encoding and decoding was implemented with ideas and help from
    // 'jayunit100' at http://stackoverflow.com/questions/8612511/utf-8-issue-in-java-code
    Charset charsetE = Charset.forName("windows-1252");
    CharsetEncoder encoder = charsetE.newEncoder();

    Charset charsetD = Charset.forName("UTF-8");
    CharsetDecoder decoder = charsetD.newDecoder();
    
    BufferedReader r = null;
    
    List<String> tableErrors = new ArrayList<String>();
    
    try
    {
      if (file.isDirectory())
      {
        for (File f : file.listFiles())
        {
          r = new BufferedReader(new FileReader(f));
          
          loadFile(r, f.getName(), targetTable, tableErrors, encoder, decoder);
          
          BasicUtils.closeSilently(r);
        }
      }
      else if (file.isFile())
      {
        ZipFile zip = new ZipFile(file);
        
        Enumeration<? extends ZipEntry> zipEntries = zip.entries();
        while (zipEntries.hasMoreElements())
        {
          ZipEntry entry = zipEntries.nextElement();
          
          r = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
          
          loadFile(r, entry.getName(), targetTable, tableErrors, encoder, decoder);
          
          BasicUtils.closeSilently(r);
        }
        
        zip.close();
      }
    }
    catch (FileNotFoundException e)
    {
      Debugger.error("Table: loadStringTableData(file)", "Error, StringTable \"" + file.getPath() + "\" file was removed while reading!", e);
      return;
    }
    catch (ZipException e)
    {
      Debugger.error("Table: loadStringTableData(file)", "Error, StringTable \"" + file.getPath() + "\" is not a valid zip file!", e);
      return;
    }
    catch (IOException e)
    {
      Debugger.error("Table: loadStringTableData(file)", "Error, StringTable \"" + file.getPath() + "\" could not be read!", e);
      return;
    }
    finally
    {
      if (r != null)
        BasicUtils.closeSilently(r);
    }
    
    if (!tableErrors.isEmpty())
    {
      StringBuffer buffer = new StringBuffer();
      for (String string : tableErrors)
      {
        buffer.append("\n");
        buffer.append(string);
      }
      
      Debugger.error("Table: loadStringTableData(file)", "The string table \"" + file.getPath() + "\" is not formatted properly:" + buffer.toString());
      
      // TODO Table; Flytta detta felmeddelandet till GUI-klass?
      JOptionPane.showMessageDialog(
          JFrame.getFrames()[0],
          "<html>" + "The string table \"" + file.getPath() + "\" is not formatted properly:" + buffer.toString().replaceAll("\n", "<br />") + "</html>",
          "String table initialization error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
  

  
  
  /**
   * Loads the table data from the file linked to the <code>BufferedReader</code>.
   * <br />If the file has the extension <code>.table</code> it's contents will
   * be treated as a table of data to load (formatted as <code>key_name = value</code>).
   * For files with any other extension the file's name will be mapped as the key
   * for the entire contents of the file.
   * @param reader A buffered reader linked to the file to read.
   * @param fileName The name of the file (with file extension).
   * @param targetTable The table to add the loaded data to.
   * @param tableErrors A list to store possible errors in.
   * @param encoder A charset encoder to use when decoding the data to UTF-8 format.
   * @param decoder A charset decoder to use when decoding the data to UTF-8 format.
   */
  private static void loadFile(BufferedReader reader, String fileName, Table targetTable,
      List<String> tableErrors, CharsetEncoder encoder, CharsetDecoder decoder)
  {
    if (fileName.endsWith(".table"))
    {
      String line = null;
      while (true)
      {
        line = null;
        try { line = reader.readLine(); } catch (IOException e1) { }
        if (line == null)
          break;
        line = line.trim();
        
        String[] components = line.split("//");
        
        if (components.length > 1)
          line = components[0].trim();
        
        try
        {
          if (!line.trim().equals(""))
          {
            String key = line.substring(0, line.lastIndexOf('=')).trim();
            String value = line.substring(line.lastIndexOf('=') + 1, line.length()).trim();
  
            String result = decodeString(encoder, decoder, value);
  
            result = result.replaceAll("\\\\\\\\", "\\\\").replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
            
            targetTable.mappings.put(key, result);
          }
        }
        catch (IndexOutOfBoundsException e)
        {
          tableErrors.add("- Line: \"" + line + "\"");
        }
      }
    }
    else
    {
      String        key  = fileName.substring(0, fileName.lastIndexOf('.'));
      StringBuilder data = new StringBuilder();
      
      String line = null;
      while (true)
      {
        line = null;
        line = null;
        try { line = reader.readLine(); } catch (IOException e1) { }
        if (line == null)
          break;
        data.append(decodeString(encoder, decoder, line) + "\n");
      }
      
      targetTable.mappings.put(key, data.toString());
    }
  }
  
  
  /**
   * Decodes the provided string to UTF-8 using the provided encoder and decoder.
   * @param encoder The encoder to use.
   * @param decoder The decoder to use.
   * @param encodedString The string to decode.
   * @return The decoded string.
   */
  private static String decodeString(CharsetEncoder encoder, CharsetDecoder decoder, String encodedString)
  {
    try
    {
      ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(encodedString));
      CharBuffer cbuf = decoder.decode(bbuf);
      
      encodedString = cbuf.toString();
    }
    catch (CharacterCodingException e)
    {
      // Ignore exception :D
    }
    
    return encodedString;
  }
  
  
  private static boolean isValidLocale(String value)
  {
    Locale[] locales = Locale.getAvailableLocales();
    for (Locale l : locales)
    {
      if (value.equals(l.toString()))
      {
        return true;
      }
    }
    return false;
  }

  
  /**
   * Returns the name of this table. The name is determined when the table is
   *  loaded from the file system, and is specified to be the name of the source
   *  file.
   * @return The name of this table
   */
  public String getName() { return tableName; }
  
  /**
   * Returns the text associated with the specified key in this language.
   * @param key The key of the text to return
   * @return The text associated with the key, or the key if no association was
   *  found
   */
  public String getTableString(String key)
  {
    String result = mappings.get(key);
    if (result != null)
      return result;
    return key;
  }
  
  
  @Override
  public String toString() {
    return getName();
  }
}
