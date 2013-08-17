/*
 * @(#)MetalToolTipUI.java	1.12 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
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
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicToolTipUI;


/**
 * A Metal L&F extension of BasicToolTipUI.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.12 04/22/99
 * @author Steve Wilson
 */
public class MetalToolTipUI extends BasicToolTipUI {

    static MetalToolTipUI sharedInstance = new MetalToolTipUI();
    Font smallFont;			    	     
    static JToolTip tip;
    public static final int padSpaceBetweenStrings = 12;

    public MetalToolTipUI() {
        super();
    }

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
	tip = (JToolTip)c;
	Font f = c.getFont();
	smallFont = new Font( f.getName(), f.getStyle(), f.getSize() - 2 );
    }

    public void paint(Graphics g, JComponent c) {
	super.paint(g, c);

        Font font = c.getFont();
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
	String keyText = getAcceleratorString();
	String tipText = ((JToolTip)c).getTipText();
	if (tipText == null) {
	    tipText = "";
	}
	if (! (keyText.equals(""))) {  // only draw control key if there is one
	    g.setFont(smallFont);
	    g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
	    g.drawString(keyText, 
		         metrics.stringWidth(tipText) + 3 + padSpaceBetweenStrings, 
		         2 + metrics.getAscent());
	}
    }

    public Dimension getPreferredSize(JComponent c) {
	Dimension d = super.getPreferredSize(c);

	String key = getAcceleratorString();
	if (! (key.equals(""))) {
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(smallFont);	
	    d.width += fm.stringWidth(key) + padSpaceBetweenStrings;
	}
        return d;
    }
   
    public String getAcceleratorString() {
        JComponent comp = tip.getComponent();
	if (comp == null) {
	    return "";
	}
	KeyStroke[] keys =comp.getRegisteredKeyStrokes();
	String controlKeyStr = "";

	for (int i = 0; i < keys.length; i++) {
	  char c = (char)keys[i].getKeyCode();
	  int mod = keys[i].getModifiers();
	  int condition =  comp.getConditionForKeyStroke(keys[i]);
	  if ( mod == InputEvent.CTRL_MASK && condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	      controlKeyStr = "cntl+"+(char)keys[i].getKeyCode();
	      break;
	  } else if (mod == InputEvent.ALT_MASK && condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	      controlKeyStr = "alt+"+(char)keys[i].getKeyCode();
	      break;
	  } 
	}
	return controlKeyStr;
    }

}
