/*
 * @(#)MetalComboBoxIcon.java	1.7 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
