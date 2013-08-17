/*
 * @(#)MotifTreeCellRenderer.java	1.10 98/08/28
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

package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;

/**
 * Motif rendered to display a tree cell.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.10 08/28/98
 * @author Jeff Dinkins
 */
public class MotifTreeCellRenderer extends DefaultTreeCellRenderer
{
    static final int LEAF_SIZE = 13;
    static final Icon LEAF_ICON = new IconUIResource(new TreeLeafIcon());

    public MotifTreeCellRenderer() {
	super();
    }

    public static Icon loadLeafIcon() {
	return LEAF_ICON;
    }

    /**
     * Icon for a node with no children.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class TreeLeafIcon implements Icon, Serializable {

	Color bg;
	Color shadow;
	Color highlight;

	public TreeLeafIcon() {
	    bg = UIManager.getColor("Tree.iconBackground");
	    shadow = UIManager.getColor("Tree.iconShadow");
	    highlight = UIManager.getColor("Tree.iconHighlight");
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(bg);
	    g.fillRect(4, 7, 5, 5);

	    g.drawLine(6, 6, 6, 6);
	    g.drawLine(3, 9, 3, 9);
	    g.drawLine(6, 12, 6, 12);
	    g.drawLine(9, 9, 9, 9);

	    g.setColor(highlight);
	    g.drawLine(2, 9, 5, 6);
	    g.drawLine(3, 10, 5, 12);

	    g.setColor(shadow);
	    g.drawLine(6, 13, 10, 9);
	    g.drawLine(9, 8, 7, 6);
	    
	}
	
	public int getIconWidth() {
	    return LEAF_SIZE;
	}
	
	public int getIconHeight() {
	    return LEAF_SIZE;
	}
	
    }
}
