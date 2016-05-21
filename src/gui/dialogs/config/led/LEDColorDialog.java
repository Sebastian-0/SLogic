/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package gui.dialogs.config.led;

import graphicalcircuit.config.ComponentConfig;
import graphicalcircuit.config.LEDConfig;
import gui.dialogs.CancelButton;
import gui.dialogs.config.ConfigDialog;
import gui.dialogs.config.OkButton;
import gui.dialogs.config.OkButton.Closable;

import java.awt.Dialog.ModalityType;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import util.SimpleGridBagLayout;
import snet.internal.Message;
import network.hooks.TransferComponentConfigHook;
import configuration.Table;

public class LEDColorDialog implements ConfigDialog, Closable {

  private JFrame parent;

  private JDialog dialog;

  private ColorChooserRadioButton onColor;
  private ColorChooserRadioButton offColor;
  private ColorChooserRadioButton currentChooserButton;

  private JColorChooser colorChooser;
  
  private boolean saveSettings;

  
  public LEDColorDialog(JFrame parent) {
    this.parent = parent;

    initializeComponents();
  }

  private void initializeComponents() {
    onColor = new OnColorRadioButton(this);
    offColor = new OffColorRadioButton(this);
    
    ButtonGroup group = new ButtonGroup();
    group.add(onColor);
    group.add(offColor);
    
    colorChooser = new JColorChooser();
    AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
    colorChooser.setChooserPanels(new AbstractColorChooserPanel[] {
        panels[1],
        panels[0]
    });
    
    
    dialog = new JDialog(parent, Table.get("config_dialog_led_title"));
    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
    
    SimpleGridBagLayout layout = new SimpleGridBagLayout(dialog);
    layout.addToGrid(onColor                              , 0, 0, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(offColor                             , 1, 0, 1, 1, GridBagConstraints.NONE, 0, 0, GridBagConstraints.WEST);
    layout.addToGrid(new JSeparator(JSeparator.HORIZONTAL), 0, 1, 2, 1, GridBagConstraints.HORIZONTAL, 1, 0);
    layout.addToGrid(colorChooser                         , 0, 2, 2, 1, GridBagConstraints.BOTH, 1, 1);
    layout.setInsets(5, 5, 5, 5);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(new OkButton(this)); buttonPanel.add(new CancelButton(dialog));
    layout.addToGrid(buttonPanel, 0, 3, 2, 1, GridBagConstraints.NONE, 0, 0);
    
    
    dialog.pack();
  }

  @Override
  public Message open(int componentId, ComponentConfig config) {
    LEDConfig ledConfig = (LEDConfig)config;
    
    saveSettings = false;

    onColor.setColor(ledConfig.onColor);
    offColor.setColor(ledConfig.offColor);

    currentChooserButton = onColor;
    currentChooserButton.setSelected(true);
    colorChooser.setColor(currentChooserButton.getColor());
    
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (saveSettings)
    {
      currentChooserButton.setColor(colorChooser.getColor());
      ledConfig.onColor = onColor.getColor();
      ledConfig.offColor = offColor.getColor();
      return new TransferComponentConfigHook(null).createMessage(componentId, ledConfig);
    }
    
    return null;
  }
  
  protected void changeCurrentButton(ColorChooserRadioButton button)
  {
    currentChooserButton.setColor(colorChooser.getColor());
    currentChooserButton = button;
    colorChooser.setColor(button.getColor());
    dialog.repaint();
  }
  
  @Override
  public void saveAndClose() {
    saveSettings = true;
    dialog.dispose();
  }
}
