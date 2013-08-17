/*
 * @(#)MotifSplitPaneDivider.java	1.10 98/08/28
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

import java.awt.*;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;


/**
 * Divider used for Motif split pane.
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
public class MotifSplitPaneDivider extends BasicSplitPaneDivider
{

    public static final int minimumThumbSize = 6;
    public static final int defaultDividerSize = 18;

    protected  static final int pad = 6;

    protected int hThumbWidth = 12;
    protected int hThumbHeight = 18;
    protected int vThumbWidth = 18;
    protected int vThumbHeight = 12;

    protected Color highlightColor;
    protected Color shadowColor;
    protected Color focusedColor;

    /**
     * Creates a new Motif SplitPaneDivider
     */
    public MotifSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
        highlightColor = UIManager.getColor("SplitPane.highlight");
        shadowColor = UIManager.getColor("SplitPane.shadow");
        focusedColor = UIManager.getColor("SplitPane.activeThumb");
        setDividerSize(hThumbWidth + pad);
    }

    /**
     * overrides to hardcode the size of the divider
     * PENDING(jeff) - rewrite JSplitPane so that this ins't needed
     */
    public void setDividerSize(int newSize) {
        if (newSize < pad + minimumThumbSize) {
            setDividerSize(pad + minimumThumbSize);
        } else {
            vThumbHeight = hThumbWidth = newSize - pad;
            super.setDividerSize(newSize);
        }                  
    }

    /**
      * Paints the divider.
      */
    // PENDING(jeff) - the thumb's location and size is currently hard coded.
    // It should be dynamic.
    public void paint(Graphics g) {
        Color               bgColor = getBackground();
        Dimension           size = getSize();

        // fill
        g.setColor(getBackground());
        g.fillRect(0, 0, size.width, size.height);

        if(getBasicSplitPaneUI().getOrientation() ==
           JSplitPane.HORIZONTAL_SPLIT) {
            int center = size.width/2;
            int x = size.width/2 - hThumbWidth/2;
            int y = 30;  // PENDING(jeff) - don't hard code this.

            // split line
            g.setColor(shadowColor);
            g.drawLine(center-1, 0, center-1, size.height);

            g.setColor(highlightColor);
            g.drawLine(center, 0, center, size.height);

            // draw thumb
            g.setColor((splitPane.hasFocus()) ? focusedColor :
                                                getBackground());
            g.fillRect(x+1, y+1, hThumbWidth-2, hThumbHeight-1);

            g.setColor(highlightColor);
            g.drawLine(x, y, x+hThumbWidth-1, y);       // top
            g.drawLine(x, y+1, x, y+hThumbHeight-1);    // left

            g.setColor(shadowColor);
            g.drawLine(x+1, y+hThumbHeight-1,
                       x+hThumbWidth-1, y+hThumbHeight-1);      // bottom
            g.drawLine(x+hThumbWidth-1, y+1,
                       x+hThumbWidth-1, y+hThumbHeight-2);      // right

        } else {
            int center = size.height/2;
            int x = size.width - 40; // PENDING(jeff) - don't hard code this
            int y = size.height/2 - vThumbHeight/2;

            // split line
            g.setColor(shadowColor);
            g.drawLine(0, center-1, size.width, center-1);

            g.setColor(highlightColor);
            g.drawLine(0, center, size.width, center);

            // draw thumb
            g.setColor((splitPane.hasFocus()) ? focusedColor :
                                                getBackground());
            g.fillRect(x+1, y+1, vThumbWidth-1, vThumbHeight-1);

            g.setColor(highlightColor);
            g.drawLine(x, y, x+vThumbWidth, y);    // top
            g.drawLine(x, y+1, x, y+vThumbHeight); // left

            g.setColor(shadowColor);
            g.drawLine(x+1, y+vThumbHeight,
                       x+vThumbWidth, y+vThumbHeight);          // bottom
            g.drawLine(x+vThumbWidth, y+1,
                       x+vThumbWidth, y+vThumbHeight-1);        // right
        }
        super.paint(g);

    }

    /**
      * The minimums size is the same as the preferredSize
      */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
