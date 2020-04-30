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

public class NetworkSettingsTab extends JPanel implements PreferenceComponent
{
  private UsernameTextField usernameTextField;
  private HostPortTextField hostPortTextField;
  
  private PasswordProtectionCheckBox passwordProtectionCheckBox;
  private ProtectionLevelComboBox protectionLevelCombo;
  private PasswordTextField passwordTextField;


  public NetworkSettingsTab(ProgramUI program)
  {
    setBorder(new LineBorder(Color.LIGHT_GRAY));
    setBackground(new Color(244, 244, 244));
    
    JLabel title = new JLabel(Table.get("preferences_network"));
    title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
    
    JLabel usernameLabel = new JLabel(Table.get("preferences_username"));
    usernameTextField = new UsernameTextField(program);
    
    JLabel hostPortLabel = new JLabel(Table.get("preferences_server_port"));
    hostPortTextField = new HostPortTextField();
    
    passwordProtectionCheckBox = new PasswordProtectionCheckBox(program);
    JLabel protectionLevelLabel = new JLabel(Table.get("preferences_protection_level"));
    protectionLevelLabel.setToolTipText(Table.get("preferences_protection_level_tooltip"));
    protectionLevelCombo = new ProtectionLevelComboBox(program, passwordProtectionCheckBox);
    
    JLabel passwordLabel = new JLabel(Table.get("preferences_password"));
    passwordTextField = new PasswordTextField(program, passwordProtectionCheckBox);
    
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(this);
    
    layout.setInsets(2, 5, 2, 0);
    layout.addToGrid(title, 0, 0, 3, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.setInsets(2, 0, 8, 0);
    layout.addToGrid(new JSeparator(JSeparator.HORIZONTAL), 0, 1, 3, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    
    layout.setInsets(5, 5, 5, 5);
    layout.addToGrid(usernameLabel, 0, 2, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(usernameTextField, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    layout.addToGrid(hostPortLabel, 0, 3, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(hostPortTextField, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    
    layout.setInsets(10, 5, 5, 5);
    layout.addToGrid(passwordProtectionCheckBox, 0, 4, 2, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.setInsets(2, 25, 5, 5);
    layout.addToGrid(protectionLevelLabel, 0, 5, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(passwordLabel, 0, 6, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.setInsets(2, 5, 5, 5);
    layout.addToGrid(protectionLevelCombo, 1, 5, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(passwordTextField, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    
    JPanel glueComponent = new JPanel();
    glueComponent.setOpaque(false);
    layout.addToGrid(glueComponent, 2, 7, 1, 1, GridBagConstraints.VERTICAL, 0, 1);
  }
  
  
  @Override
  public void collectErrors(List<String> targetList)
  {
    usernameTextField.collectErrors(targetList);
    hostPortTextField.collectErrors(targetList);
    
    passwordProtectionCheckBox.collectErrors(targetList);
    protectionLevelCombo.collectErrors(targetList);
    passwordTextField.collectErrors(targetList);
  }
  
  @Override
  public boolean saveSettings()
  {
    boolean needsRestart = false;

    needsRestart |= usernameTextField.saveSettings();
    needsRestart |= hostPortTextField.saveSettings();
    
    needsRestart |= passwordProtectionCheckBox.saveSettings();
    needsRestart |= protectionLevelCombo.saveSettings();
    needsRestart |= passwordTextField.saveSettings();
    
    return needsRestart;
  }
}
