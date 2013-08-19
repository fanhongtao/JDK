/*
 * @(#)DefaultMenuLayout.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import java.awt.Container;
import java.awt.Dimension;

/**
 * The default layout manager for Popup menus and menubars.  This
 * class is an extension of BoxLayout which adds the UIResource tag
 * so that plauggable L&Fs can distinguish it from user-installed
 * layout managers on menus.
 *
 * Derived from javax.swing.plaf.basic.DefaultMenuLayout
 *
 * @version 1.5 01/23/03
 * @author Georges Saab
 */

class DefaultMenuLayout extends BoxLayout implements UIResource {
    public DefaultMenuLayout(Container target, int axis) {
	super(target, axis);
    }

    public void invalidateLayout(Container target) {
        if (target instanceof JPopupMenu) {
            SynthPopupMenuUI popupUI = (SynthPopupMenuUI)((JPopupMenu)target).
                                  getUI();

            popupUI.resetAcceleratorWidths();
        }
        super.invalidateLayout(target);
    }
}
