/*
 * @(#)DefaultMenuLayout.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

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
 * @version 1.8 12/19/03
 * @author Georges Saab
 */

public class DefaultMenuLayout extends BoxLayout implements UIResource {
    public DefaultMenuLayout(Container target, int axis) {
	super(target, axis);
    }

    public Dimension preferredLayoutSize(Container target) {
        if (target instanceof JPopupMenu) {
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_TEXT_WIDTH, null); 
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_ACC_WIDTH, null);
        }
        return super.preferredLayoutSize(target);
    }
}
