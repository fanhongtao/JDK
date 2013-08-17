/*
 * @(#)WindowsInternalFrameUI.java	1.17 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Toolkit;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.ComponentUI;

/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsInternalFrameUI extends BasicInternalFrameUI
{
    public void installDefaults() {
        super.installDefaults();
        frame.setBorder(UIManager.getBorder("InternalFrame.border"));
    }

    public void uninstallDefaults() {
        frame.setBorder(null);
        super.uninstallDefaults();
    }

    public static ComponentUI createUI(JComponent b)    {
        return new WindowsInternalFrameUI((JInternalFrame)b);
    }

    public WindowsInternalFrameUI(JInternalFrame w){
	super(w);
    }

    protected DesktopManager createDesktopManager(){
        return new WindowsDesktopManager();
    }

    protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new WindowsInternalFrameTitlePane(w);
        return titlePane;
    }
}
