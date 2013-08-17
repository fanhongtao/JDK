/*
 * @(#)MetalToolTipUI.java	1.17 00/02/02
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
 * @version 1.17 02/02/00
 * @author Steve Wilson
 */
public class MetalToolTipUI extends BasicToolTipUI {

    static MetalToolTipUI sharedInstance = new MetalToolTipUI();
    private Font smallFont;			    	     
    private JToolTip tip;
    public static final int padSpaceBetweenStrings = 12;
    private String acceleratorDelimiter;

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
	acceleratorDelimiter = UIManager.getString( "MenuItem.acceleratorDelimiter" );
	if ( acceleratorDelimiter == null ) { acceleratorDelimiter = "-"; }
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
		         metrics.stringWidth(tipText) + padSpaceBetweenStrings, 
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

	  if ( condition == JComponent.WHEN_IN_FOCUSED_WINDOW &&
	       ( (mod & InputEvent.ALT_MASK) != 0 || (mod & InputEvent.CTRL_MASK) != 0 ||
		 (mod & InputEvent.SHIFT_MASK) != 0 || (mod & InputEvent.META_MASK) != 0 ) )
	  {
	      controlKeyStr = KeyEvent.getKeyModifiersText(mod) +
		              acceleratorDelimiter + (char)keys[i].getKeyCode();
	      break;
	  }
	}

	/* Special case for menu item since they do not register a
	   keyboard action for their mnemonics and they always use Alt */
	if ( controlKeyStr.equals("") && comp instanceof JMenuItem )
	{
	    int mnemonic = ((JMenuItem) comp).getMnemonic();
	    if ( mnemonic != 0 )
	    {
	        controlKeyStr = "Alt" + acceleratorDelimiter + (char) mnemonic;
	    }
	}

	return controlKeyStr;
    }

}
