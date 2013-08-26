/*
 * @(#)DefaultMenuLayout.java	1.11 10/04/26
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import java.awt.Container;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.multi.MultiPopupMenuUI;

/**
 * The default layout manager for Popup menus and menubars.  This
 * class is an extension of BoxLayout which adds the UIResource tag
 * so that plauggable L&Fs can distinguish it from user-installed
 * layout managers on menus.
 *
 * Derived from javax.swing.plaf.basic.DefaultMenuLayout
 *
 * @version 1.11 04/26/10
 * @author Georges Saab
 */

class DefaultMenuLayout extends BoxLayout implements UIResource {
    public DefaultMenuLayout(Container target, int axis) {
	super(target, axis);
    }

    @Override
    public void invalidateLayout(Container target) {
        if (target instanceof JPopupMenu) {
            ComponentUI ui = ((JPopupMenu) target).getUI();
            ComponentUI[] uis = ui instanceof MultiPopupMenuUI
                    ? ((MultiPopupMenuUI) ui).getUIs()
                    : new ComponentUI[] { ui };
            for (ComponentUI u: uis) {
                if (u instanceof SynthPopupMenuUI) {
                    ((SynthPopupMenuUI) u).resetAlignmentHints();
                }
            }
        }
        super.invalidateLayout(target);
    }
}
