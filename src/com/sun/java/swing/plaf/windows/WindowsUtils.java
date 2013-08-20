/*
 * @(#)WindowsUtils.java	1.9 04/04/16
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

/**
 * This is a collection of utility methods needed by the Windows L&F
 *
 * @version 1.9 04/16/04
 * @author Brian Beck
 */

class WindowsUtils {
    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }

    /*
     * Repaints all the components with the mnemonics in the given window and
     * all its owned windows.
     */
    static void repaintMnemonicsInWindow(Window w) {
        if(w == null || !w.isShowing()) {
            return;
        }

        Window[] ownedWindows = w.getOwnedWindows();
        for(int i=0;i<ownedWindows.length;i++) {
            repaintMnemonicsInWindow(ownedWindows[i]);
        }

        repaintMnemonicsInContainer(w);
    }

    /*
     * Repaints all the components with the mnemonics in container.
     * Recursively searches for all the subcomponents.
     */
    static void repaintMnemonicsInContainer(Container cont) {
        Component c;
        for(int i=0; i<cont.getComponentCount(); i++) {
            c = cont.getComponent(i);
            if(c == null || !c.isVisible()) {
                continue;
            }
            if(c instanceof AbstractButton
               && ((AbstractButton)c).getMnemonic() != '\0') {
                c.repaint();
                continue;
            } else if(c instanceof JLabel
                      && ((JLabel)c).getDisplayedMnemonic() != '\0') {
                c.repaint();
                continue;
            }
            if(c instanceof Container) {
                repaintMnemonicsInContainer((Container)c);
            }
        }
    }
}
