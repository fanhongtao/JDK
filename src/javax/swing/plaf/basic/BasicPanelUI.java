/*
 * @(#)BasicPanelUI.java	1.4 98/08/26
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
 * @version 1.4 08/26/98
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
        super.uninstallUI(c);

    }

    protected void installDefaults(JPanel p) {
        LookAndFeel.installColorsAndFont(p,
					 "Panel.background",
					 "Panel.foreground",
					 "Panel.font");
        LookAndFeel.installBorder(p,"Panel.border");
    }

    protected void uninstallDefaults(JPanel p) {
        LookAndFeel.uninstallBorder(p);
    }
}
