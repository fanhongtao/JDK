/*
 * @(#)MetalComboBoxIcon.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import java.io.Serializable;
import javax.swing.plaf.basic.BasicComboBoxUI;


/**
 * This utility class draws the horizontal bars which indicate a MetalComboBox
 *
 * @see MetalComboBoxUI
 * @version 1.7 08/26/98
 * @author Tom Santos
 */
public class MetalComboBoxIcon implements Icon, Serializable {
     
    /**
     * Paints the horizontal bars for the 
     */
    public void paintIcon(Component c, Graphics g, int x, int y){
        JComponent component = (JComponent)c;
	int iconWidth = getIconWidth();

	g.translate( x, y );

	g.setColor( component.isEnabled() ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlShadow() );
	g.drawLine( 0, 0, iconWidth - 1, 0 );
	g.drawLine( 1, 1, 1 + (iconWidth - 3), 1 );
	g.drawLine( 2, 2, 2 + (iconWidth - 5), 2 );
	g.drawLine( 3, 3, 3 + (iconWidth - 7), 3 );
	g.drawLine( 4, 4, 4 + (iconWidth - 9), 4 );

	g.translate( -x, -y );
    }
    
    /**
     * stubbed to statify the interface.
     */
    public int getIconWidth() { return 10; }

    /**
     * stubbed to statify the interface.
     */
    public int getIconHeight()  { return 5; }

}
