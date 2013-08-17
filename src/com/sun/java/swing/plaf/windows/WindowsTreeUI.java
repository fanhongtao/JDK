/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.*;

import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.plaf.*;


/**
 * A Windows tree.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.17 02/06/02
 * @author Scott Violet
 */
public class WindowsTreeUI extends BasicTreeUI {

    public static ComponentUI createUI( JComponent c )
      {
	return new WindowsTreeUI();
      }

    protected void paintVerticalLine( Graphics g, JComponent c, int x, int top, int bottom )
      {
	drawDashedVerticalLine( g, x, top, bottom );
      }

    protected void paintHorizontalLine( Graphics g, JComponent c, int y, int left, int right )
      {
	drawDashedHorizontalLine( g, y, left, right );
      }


    static protected final int HALF_SIZE = 4;
    static protected final int SIZE = 9;

    /**
     * The minus sign button icon
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class ExpandedIcon implements Icon, Serializable {
        static public Icon createExpandedIcon() {
	    return new ExpandedIcon();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    Color     backgroundColor = c.getBackground();

	    if(backgroundColor != null)
		g.setColor(backgroundColor);
	    else
		g.setColor(Color.white);
	    g.fillRect(x, y, SIZE-1, SIZE-1);
	    g.setColor(Color.gray);
	    g.drawRect(x, y, SIZE-1, SIZE-1);
	    g.setColor(Color.black);
	    g.drawLine(x + 2, y + HALF_SIZE, x + (SIZE - 3), y + HALF_SIZE);
	}
	public int getIconWidth() { return SIZE; }
	public int getIconHeight() { return SIZE; }
    }

    /**
     * The plus sign button icon
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class CollapsedIcon extends ExpandedIcon {
        static public Icon createCollapsedIcon() {
	    return new CollapsedIcon();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    super.paintIcon(c, g, x, y);
	    g.drawLine(x + HALF_SIZE, y + 2, x + HALF_SIZE, y + (SIZE - 3));
	}
    }

}
