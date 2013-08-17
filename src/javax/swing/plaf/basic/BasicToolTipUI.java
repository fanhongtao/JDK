/*
 * @(#)BasicToolTipUI.java	1.26 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.ToolTipUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;


/**
 * Standard tool tip L&F.
 * <p>
 *
 * @version 1.26 08/26/98
 * @author Dave Moore
 */
public class BasicToolTipUI extends ToolTipUI
{
    static BasicToolTipUI sharedInstance = new BasicToolTipUI();

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public BasicToolTipUI() {
        super();
    }

    public void installUI(JComponent c) {
	installDefaults(c);
	installListeners(c);
    }

    public void uninstallUI(JComponent c) {
	// REMIND: this is NOT getting called
	uninstallDefaults(c);
	uninstallListeners(c);
    }

    protected void installDefaults(JComponent c){
	LookAndFeel.installColorsAndFont(c, "ToolTip.background",
					 "ToolTip.foreground",
					 "ToolTip.font");
        LookAndFeel.installBorder(c, "ToolTip.border");
    }
    
    protected void uninstallDefaults(JComponent c){
	LookAndFeel.uninstallBorder(c);
    }

    protected void installListeners(JComponent c) {
    }

    protected void uninstallListeners(JComponent c) {
    }

    public void paint(Graphics g, JComponent c) {
        Font font = c.getFont();
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        Dimension size = c.getSize();
        g.setColor(c.getBackground());
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(c.getForeground());
        g.setFont(font);
	// fix for bug 4153892
	String tipText = ((JToolTip)c).getTipText();
	if (tipText != null)
	  g.drawString(((JToolTip)c).getTipText(), 3, 2 + metrics.getAscent());
    }

    public Dimension getPreferredSize(JComponent c) {
        Font font = c.getFont();
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
	// fix for bug 4094153
	String tipText = ((JToolTip)c).getTipText();
	if (tipText == null)
	  return new Dimension(6,metrics.getHeight() + 4);
	else
	  return new Dimension(metrics.stringWidth(tipText) + 6,
			       metrics.getHeight() + 4);
    }

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

}
