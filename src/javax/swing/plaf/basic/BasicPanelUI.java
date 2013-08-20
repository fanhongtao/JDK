/*
 * @(#)BasicPanelUI.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.event.*;


/**
 * BasicPanel implementation
 *
 * @version 1.11 12/19/03
 * @author Steve Wilson
 */
public class BasicPanelUI extends PanelUI {

    // Shared UI object
    private static PanelUI panelUI;

    public static ComponentUI createUI(JComponent c) {
	if(panelUI == null) {
            panelUI = new BasicPanelUI();
	}
        return panelUI;
    }

    public void installUI(JComponent c) {
        JPanel p = (JPanel)c;
        super.installUI(p);
        installDefaults(p);
    }

    public void uninstallUI(JComponent c) {
        JPanel p = (JPanel)c;
        uninstallDefaults(p);
        super.uninstallUI(c);
    }

    protected void installDefaults(JPanel p) {
        LookAndFeel.installColorsAndFont(p,
					 "Panel.background",
					 "Panel.foreground",
					 "Panel.font");
        LookAndFeel.installBorder(p,"Panel.border");
        LookAndFeel.installProperty(p, "opaque", Boolean.TRUE);
    }

    protected void uninstallDefaults(JPanel p) {
        LookAndFeel.uninstallBorder(p);
    }
}
