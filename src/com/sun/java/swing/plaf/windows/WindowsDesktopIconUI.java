/*
 * @(#)WindowsDesktopIconUI.java	1.14 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.border.*;



/**
 * Windows icon for a minimized window on the desktop.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsDesktopIconUI extends BasicDesktopIconUI {

    private int width;

    // BasicDesktopIconUI.iconPane should be protected!
    private JComponent windowsIconPane;

    public static ComponentUI createUI(JComponent c) {
        return new WindowsDesktopIconUI();
    }

    public WindowsDesktopIconUI() {
    }

    public void installDefaults() {
        super.installDefaults();
        width = UIManager.getInt("DesktopIcon.width");
    }

    protected void installComponents() {
        windowsIconPane = new WindowsInternalFrameTitlePane(frame);
        desktopIcon.setLayout(new BorderLayout());
        desktopIcon.add(windowsIconPane, BorderLayout.CENTER);
    }

    protected void uninstallComponents() {
        desktopIcon.remove(windowsIconPane);
        desktopIcon.setLayout(null);
        windowsIconPane = null;
    }

    public Dimension getPreferredSize(JComponent c) {
        // Windows desktop icons can not be resized.  Therefore, we should
        // always return the minimum size of the desktop icon. See
        // getMinimumSize(JComponent c).
        return getMinimumSize(c);
    }

    public Dimension getMinimumSize(JComponent c) {
        // Windows desktop icons are restricted to a width of 160 pixels by
        // default.  This value is retrieved by the DesktopIcon.width property.
        Dimension dim = new Dimension(windowsIconPane.getMinimumSize());
        Border border = frame.getBorder();
        if (border != null) {
            dim.height += border.getBorderInsets(frame).bottom +
                          border.getBorderInsets(frame).top;
        }
        dim.width = width;
        return dim;
    }

    public Dimension getMaximumSize(JComponent c) {
        // Windows desktop icons can not be resized.  Therefore, we should
        // always return the minimum size of the desktop icon. See
        // getMinimumSize(JComponent c).
        return getMinimumSize(c);
    }
}
