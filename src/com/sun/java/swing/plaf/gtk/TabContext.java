/*
 * @(#)TabContext.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;

/**
 * @version 1.6, 01/23/03
 * @author Scott Violet
 */
class TabContext extends SynthContext {
    private int tabIndex;

    public int getTabIndex() {
        return tabIndex;
    }

    void update(int index, boolean selected, boolean isMouseOver, boolean hasFocus) {
        tabIndex = index;
        JTabbedPane tp = (JTabbedPane)getComponent();
        int state = 0;
        if (!tp.isEnabledAt(index)) {
	    state |= SynthConstants.DISABLED;
        }
        else if (selected) {
            state |= (SynthConstants.ENABLED | SynthConstants.SELECTED);
        }
        else if (isMouseOver) {
            state |= (SynthConstants.ENABLED | SynthConstants.MOUSE_OVER);
        }
        else {
            state = SynthLookAndFeel.getComponentState(tp);
	    state &= ~SynthConstants.FOCUSED; // don't use tabbedpane focus state
        }
	if (hasFocus) {
	    state |= SynthConstants.FOCUSED; // individual tab has focus
	}
	setComponentState(state);
    }
}
