/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)MetalThemeMenu.java	1.9 03/01/23
 */
 

import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class describes a theme using "green" colors.
 *
 * @version 1.9 01/23/03
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
