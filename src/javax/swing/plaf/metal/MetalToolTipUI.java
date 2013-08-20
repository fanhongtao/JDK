/*
 * @(#)MetalToolTipUI.java	1.28 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import com.sun.java.swing.SwingUtilities2;
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
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.28 12/19/03
 * @author Steve Wilson
 */
public class MetalToolTipUI extends BasicToolTipUI {

    static MetalToolTipUI sharedInstance = new MetalToolTipUI();
    private Font smallFont;			    	     
    // Refer to note in getAcceleratorString about this field.
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

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        tip = null;
    }

    public void paint(Graphics g, JComponent c) {
        JToolTip tip = (JToolTip)c;

	super.paint(g, c);

        Font font = c.getFont();
        FontMetrics metrics = SwingUtilities2.getFontMetrics(c, g, font);
	String keyText = getAcceleratorString(tip);
	String tipText = tip.getTipText();
	if (tipText == null) {
	    tipText = "";
	}
	if (! (keyText.equals(""))) {  // only draw control key if there is one
	    g.setFont(smallFont);
	    g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
	    SwingUtilities2.drawString(tip, g, keyText,
                         SwingUtilities2.stringWidth(
                         tip, metrics, tipText) + padSpaceBetweenStrings, 
		         2 + metrics.getAscent());
	}
    }

    public Dimension getPreferredSize(JComponent c) {
	Dimension d = super.getPreferredSize(c);

	String key = getAcceleratorString((JToolTip)c);
	if (! (key.equals(""))) {
            FontMetrics fm = c.getFontMetrics(smallFont);	
	    d.width += SwingUtilities2.stringWidth(c, fm, key) +
                            padSpaceBetweenStrings;
	}
        return d;
    }

    protected boolean isAcceleratorHidden() {
        Boolean b = (Boolean)UIManager.get("ToolTip.hideAccelerator");
        return b != null && b.booleanValue();
    }

    private String getAcceleratorString(JToolTip tip) {
        this.tip = tip;

        String retValue = getAcceleratorString();

        this.tip = null;
        return retValue;
    }

    // NOTE: This requires the tip field to be set before this is invoked.
    // As MetalToolTipUI is shared between all JToolTips the tip field is
    // set appropriately before this is invoked. Unfortunately this means
    // that subclasses that randomly invoke this method will see varying
    // results. If this becomes an issue, MetalToolTipUI should no longer be
    // shared.
    public String getAcceleratorString() {
        if (tip == null || isAcceleratorHidden()) {
            return "";
        }
        JComponent comp = tip.getComponent();
	if (comp == null) {
	    return "";
	}

	KeyStroke[] keys;

	if (comp instanceof JTabbedPane) {
	  TabbedPaneUI ui = ( (JTabbedPane)(comp) ).getUI();
	  if (ui instanceof MetalTabbedPaneUI) {		
	    // if comp is instance of JTabbedPane and have 'metal' look-and-feel
	    // detect tab the mouse is over now
	    int rolloverTabIndex = ( (MetalTabbedPaneUI)ui ).getRolloverTabIndex();
       	    if  (rolloverTabIndex == -1)
    	      keys = new KeyStroke[0];
    	    else {
    	      // detect mnemonic for this tab
    	      int mnemonic = ((JTabbedPane)comp).getMnemonicAt(rolloverTabIndex);
    	      if (mnemonic == -1)
 	        keys = new KeyStroke[0];
 	      else {
 	        // and store it as mnemonic for the component
                KeyStroke keyStroke = KeyStroke.getKeyStroke(mnemonic, Event.ALT_MASK);
                keys = new KeyStroke[1];
                keys[0] = keyStroke;
              }
            }
          }
          else
            keys = comp.getRegisteredKeyStrokes();
        }
	else
	  keys = comp.getRegisteredKeyStrokes();

	String controlKeyStr = "";

	for (int i = 0; i < keys.length; i++) {
	  int mod = keys[i].getModifiers();
	  int condition =  comp.getConditionForKeyStroke(keys[i]);

	  if ( condition == JComponent.WHEN_IN_FOCUSED_WINDOW )
	  {
	      controlKeyStr = KeyEvent.getKeyModifiersText(mod) +
                              acceleratorDelimiter +
                              KeyEvent.getKeyText(keys[i].getKeyCode());
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
	        controlKeyStr =
                    KeyEvent.getKeyModifiersText(KeyEvent.ALT_MASK) +
                    acceleratorDelimiter + (char)mnemonic;
	    }
	}

	return controlKeyStr;
    }

}
