/*
 * @(#)ComponentUI.java	1.13 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.plaf;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.accessibility.Accessible;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;


public abstract class ComponentUI
{
    public void installUI(JComponent c) {
    }

    public void uninstallUI(JComponent c) {
    }

    public void paint(Graphics g, JComponent c) {
    }

    public void update(Graphics g, JComponent c) {
	if (c.isOpaque()) {
	    g.setColor(c.getBackground());
	    g.fillRect(0, 0, c.getWidth(),c.getHeight());
	}
	paint(g, c);
    }

    public Dimension getPreferredSize(JComponent c) {
	return null;
    }

    public Dimension getMinimumSize(JComponent c) {
	return getPreferredSize(c);
    }

    public Dimension getMaximumSize(JComponent c) {
	return getPreferredSize(c);
    }

    public boolean contains(JComponent c, int x, int y) {
	return c.inside(x, y);
    }

    public static ComponentUI createUI(JComponent c) {
	throw new Error("ComponentUI.createUI not implemented.");
    }

    /**
     * Returns the number of accessible children in the object.  If all
     * of the children of this object implement Accessible, than this
     * method should return the number of children of this object.
     * UI's might wish to override this if they present areas on the
     * screen that can be viewed as components, but actual components
     * are not used for presenting those areas.
     *
     * @see #getAccessibleChild
     * @return the number of accessible children in the object.
     */
    public int getAccessibleChildrenCount(JComponent c) {
        return SwingUtilities.getAccessibleChildrenCount(c);
    }

    /**
     * Return the nth Accessible child of the object.
     * UI's might wish to override this if they present areas on the
     * screen that can be viewed as components, but actual components
     * are not used for presenting those areas.
     *
     * @see #getAccessibleChildrenCount
     * @param i zero-based index of child
     * @return the nth Accessible child of the object
     */
    public Accessible getAccessibleChild(JComponent c, int i) {
        return SwingUtilities.getAccessibleChild(c, i);
    }
}

