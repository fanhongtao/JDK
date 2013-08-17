/*
 * @(#)BasicViewportUI.java	1.3 98/08/26
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
 * BasicViewport implementation
 *
 * @version 1.1 05/01/98
 * @author Rich Schiavi
 */
public class BasicViewportUI extends ViewportUI {

    // Shared UI object
    private static ViewportUI viewportUI;

    public static ComponentUI createUI(JComponent c) {
	if(viewportUI == null) {
            viewportUI = new BasicViewportUI();
	}
        return viewportUI;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        installDefaults(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

    }

    protected void installDefaults(JComponent c) {
        LookAndFeel.installColorsAndFont(c,
					 "Viewport.background",
					 "Viewport.foreground",
					 "Viewport.font");
    }

    protected void uninstallDefaults(JComponent c) {
    }
}
