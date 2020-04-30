/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CircuitClipboard
{
  private Map<String, ClipboardEntry> entries;
  
  
  public CircuitClipboard()
  {
    entries = new HashMap<String, ClipboardEntry>();
  }
  

  public void addEntry(String key, ClipboardEntry entry)
  {
    entries.put(key, entry);
    entry.setId(key);
  }
  
  public void removeEntry(String key)
  {
    entries.remove(key);
  }
  
  public ClipboardEntry getEntry(String key)
  {
    return entries.get(key);
  }
  
  public Collection<ClipboardEntry> getEntries()
  {
    return entries.values();
  }
}
