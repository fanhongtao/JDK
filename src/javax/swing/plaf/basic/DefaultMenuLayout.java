/*
 * @(#)DefaultMenuLayout.java	1.14 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import java.awt.Container;
import java.awt.Dimension;
import static sun.swing.SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET;

/**
 * The default layout manager for Popup menus and menubars.  This
 * class is an extension of BoxLayout which adds the UIResource tag
 * so that plauggable L&Fs can distinguish it from user-installed
 * layout managers on menus.
 *
 * @version 1.14 03/23/10
 * @author Georges Saab
 */

public class DefaultMenuLayout extends BoxLayout implements UIResource {
    public DefaultMenuLayout(Container target, int axis) {
	super(target, axis);
    }

    public Dimension preferredLayoutSize(Container target) {
        if (target instanceof JPopupMenu) {
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_ARROW_ICON_WIDTH, null);
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_CHECK_ICON_WIDTH, null); 
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_ICON_WIDTH, null);
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_TEXT_WIDTH, null); 
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_ACC_WIDTH, null);
          ((JPopupMenu)target).putClientProperty(
                                 BasicMenuItemUI.MAX_ICON_OFFSET, null);
          ((JPopupMenu)target).putClientProperty(
                                 BASICMENUITEMUI_MAX_TEXT_OFFSET, null);
        }
        return super.preferredLayoutSize(target);
    }
}
