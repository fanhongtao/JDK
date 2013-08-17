/*
 * @(#)MetalScrollPaneUI.java	1.10 98/08/28
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import java.awt.*;
import java.beans.*;
import java.awt.event.*;


/**
 * A Metal L&F implementation of ScrollPaneUI.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @1.10 08/28/98
 * @author Steve Wilson
 */
public class MetalScrollPaneUI extends BasicScrollPaneUI
{

    private PropertyChangeListener scrollBarSwapListener;

    public static ComponentUI createUI(JComponent x) {
	return new MetalScrollPaneUI();
    }

    public void installUI(JComponent c) {

        super.installUI(c);

	JScrollPane sp = (JScrollPane)c;
	JScrollBar hsb = sp.getHorizontalScrollBar();
	JScrollBar vsb = sp.getVerticalScrollBar();
	hsb.putClientProperty( MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
	vsb.putClientProperty( MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

	JScrollPane sp = (JScrollPane)c;
	JScrollBar hsb = sp.getHorizontalScrollBar();
	JScrollBar vsb = sp.getVerticalScrollBar();
	hsb.putClientProperty( MetalScrollBarUI.FREE_STANDING_PROP, null);
	vsb.putClientProperty( MetalScrollBarUI.FREE_STANDING_PROP, null);	
    }


    public void installListeners(JScrollPane scrollPane) {
        super.installListeners(scrollPane);
	scrollBarSwapListener = createScrollBarSwapListener();
	scrollPane.addPropertyChangeListener(scrollBarSwapListener);
    }


    public void uninstallListeners(JScrollPane scrollPane) {
        super.uninstallListeners(scrollPane);

	scrollPane.removePropertyChangeListener(scrollBarSwapListener);
    }

    protected PropertyChangeListener createScrollBarSwapListener() {
        return new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent e) {
		  String propertyName = e.getPropertyName();
		  if (propertyName.equals("verticalScrollBar") ||
		      propertyName.equals("horizontalScrollBar")) {
		        ((JScrollBar)e.getOldValue()).putClientProperty( MetalScrollBarUI.FREE_STANDING_PROP, 
									 null);
		        ((JScrollBar)e.getNewValue()).putClientProperty( MetalScrollBarUI.FREE_STANDING_PROP, 
									 Boolean.FALSE);
		  }	  
	}};
    }

}
