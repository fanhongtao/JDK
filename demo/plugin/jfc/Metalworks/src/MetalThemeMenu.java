/*
 * @(#)MetalThemeMenu.java	1.7 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class describes a theme using "green" colors.
 *
 * @version 1.7 12/03/01
 * @author Steve Wilson
 */
public class MetalThemeMenu extends JMenu implements ActionListener{

  MetalTheme[] themes;
  public MetalThemeMenu(String name, MetalTheme[] themeArray) {
    super(name);
    themes = themeArray;
    ButtonGroup group = new ButtonGroup();
    for (int i = 0; i < themes.length; i++) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem( themes[i].getName() );
	group.add(item);
        add( item );
	item.setActionCommand(i+"");
	item.addActionListener(this);
	if ( i == 0)
	    item.setSelected(true);
    }

  }

  public void actionPerformed(ActionEvent e) {
    String numStr = e.getActionCommand();
    MetalTheme selectedTheme = themes[ Integer.parseInt(numStr) ];
    MetalLookAndFeel.setCurrentTheme(selectedTheme);
    try {
	UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (Exception ex) {
        System.out.println("Failed loading Metal");
	System.out.println(ex);
    }

  }

}
