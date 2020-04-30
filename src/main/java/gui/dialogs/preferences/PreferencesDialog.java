/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.preferences;

import gui.backend.ProgramUI;
import gui.dialogs.CancelButton;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import util.SimpleGridBagLayout;
import configuration.Table;

public class PreferencesDialog extends JDialog
{
  private ProgramUI program;
  
  private GeneralSettingsTab generalSettings;
  private NetworkSettingsTab networkSettings;
  
  private OkButton okButton;
  private CancelButton cancelButton;
  
  
  public PreferencesDialog(ProgramUI program)
  {
    super((Frame) program, Table.get("preferences_title"));
    
    setModalityType(ModalityType.APPLICATION_MODAL);
    
    this.program = program;
    
    generalSettings = new GeneralSettingsTab(program);
    networkSettings = new NetworkSettingsTab(program);
    
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
    tabbedPane.setPreferredSize(new Dimension(440, 210));
    tabbedPane.add(Table.get("preferences_general"), generalSettings);
    tabbedPane.add(Table.get("preferences_network"), networkSettings);
    
    okButton = new OkButton(this);
    cancelButton = new CancelButton(this);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    layout.addToGrid(tabbedPane, 0, 0, 1, 1, GridBagConstraints.BOTH, 1, 1);
    layout.addToGrid(new JSeparator(JSeparator.HORIZONTAL), 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    
    layout.setInsets(2, 2, 2, 2);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton); buttonPanel.add(cancelButton);
    layout.addToGrid(buttonPanel, 0, 2, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.EAST);
    
    pack();
    
    setMinimumSize(getSize());
    
    setLocationRelativeTo((Component) program);
  }
  
  
  public void saveSettings()
  {
    List<String> errors = new ArrayList<String>();
    generalSettings.collectErrors(errors);
    networkSettings.collectErrors(errors);
    
    if (!errors.isEmpty())
    {
      StringBuilder sb = new StringBuilder();
      sb.append("<html>" + Table.get("preferences_errors_must_be_fixed") + ":<br/>");
      for (String error : errors)
      {
        sb.append("- ").append(error).append("<br/>");
      }
      sb.setLength(sb.length()-5);
      sb.append("</html>");
      
      JOptionPane.showMessageDialog(
          (Component) program,
          sb.toString(),
          Table.get("popup_title_incorrect_input"),
          JOptionPane.ERROR_MESSAGE);
    }
    else
    {
      boolean mustRestart = generalSettings.saveSettings();
      mustRestart |= networkSettings.saveSettings();
      
      if (mustRestart)
        JOptionPane.showMessageDialog(
            (Component) program,
            Table.get("preferences_must_restart_to_apply"),
            Table.get("popup_title_must_restart"),
            JOptionPane.INFORMATION_MESSAGE);      
      dispose();
    }
    
    program.getBackend().refreshRenderingSurface();
  }
}
