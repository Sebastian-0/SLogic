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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;

import util.SimpleGridBagLayout;
import configuration.Table;

public class GeneralSettingsTab extends JPanel implements PreferenceComponent
{
  private LanguageComboBox languageCombo;
  private GateDesignComboBox gateDesignCombo;
  private NumberOfBackupsTextField backupsTextField;

  
  public GeneralSettingsTab(ProgramUI program)
  {
    setBorder(new LineBorder(Color.LIGHT_GRAY));
    setBackground(new Color(244, 244, 244));
    
    JLabel title = new JLabel(Table.get("preferences_general"));
    title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
    
    JLabel languageLabel = new JLabel(Table.get("preferences_language"));
    languageCombo = new LanguageComboBox();
    
    JLabel gateDesignLabel = new JLabel(Table.get("preferences_gate_design"));
    gateDesignCombo = new GateDesignComboBox();

    JLabel backupsLabel = new JLabel(Table.get("preferences_number_of_backups"));
    backupsLabel.setToolTipText("<html>" + Table.get("preferences_number_of_backups_tooltip").replaceAll("\n", "<br />") + "</html>");
    backupsTextField = new NumberOfBackupsTextField(program);
    
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    layout.setInsets(2, 5, 2, 0);
    layout.addToGrid(title, 0, 0, 3, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.setInsets(2, 0, 8, 0);
    layout.addToGrid(new JSeparator(JSeparator.HORIZONTAL), 0, 1, 3, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    
    layout.setInsets(5, 5, 5, 5);
    layout.addToGrid(languageLabel, 0, 2, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(languageCombo, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    layout.addToGrid(gateDesignLabel, 0, 3, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(gateDesignCombo, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    layout.setInsets(15, 5, 5, 5);
    layout.addToGrid(backupsLabel, 0, 4, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(backupsTextField, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    
    JPanel glueComponent = new JPanel();
    glueComponent.setOpaque(false);
    layout.addToGrid(glueComponent, 2, 5, 1, 1, GridBagConstraints.BOTH, 1, 1);
  }
  
  @Override
  public void collectErrors(List<String> targetList)
  {
    languageCombo.collectErrors(targetList);
    gateDesignCombo.collectErrors(targetList);
    backupsTextField.collectErrors(targetList);
  }
  
  @Override
  public boolean saveSettings()
  {
    boolean needsRestart = languageCombo.saveSettings();
    needsRestart |= gateDesignCombo.saveSettings();
    needsRestart |= backupsTextField.saveSettings();
    return needsRestart;
  }
}
