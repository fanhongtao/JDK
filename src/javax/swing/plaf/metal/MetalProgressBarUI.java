/*
 * @(#)MetalProgressBarUI.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

/**
 * The Metal implementation of ProgressBarUI.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.14 08/28/98
 * @author Michael C. Albers
 */
public class MetalProgressBarUI extends BasicProgressBarUI {

    public static ComponentUI createUI(JComponent c) {
	return new MetalProgressBarUI();
    }

    /**
     * The sole reason for this paint method to even be here is that
     * the JLF/Metal ProgressBar has a bit of special highlighting that
     * needs to get drawn. The core painting is defered to the
     * BasicProgressBar's paint method.
     */ 
    public void paint(Graphics g, JComponent c) {
	super.paint(g,c);

	if (progressBar.isBorderPainted()) {
	    BoundedRangeModel model = progressBar.getModel();
	    int barRectX = 0;
	    int barRectY = 0;
	    int barRectWidth = progressBar.getWidth();
	    int barRectHeight = progressBar.getHeight();
	    Insets b = progressBar.getInsets(); // area for border
	    barRectX += b.left;
	    barRectY += b.top;
	    barRectWidth -= (b.right + barRectX);
	    barRectHeight -= (b.bottom + barRectY);
	    int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
	    
	    if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
		// Highlighting
		//     over the unfilled portion
		//     well, draw all the way across; let others draw over it
		g.setColor(MetalLookAndFeel.getControlShadow());
		g.drawLine(barRectX,barRectY, barRectWidth,barRectY);
		
		//     line on left
		if (model.getValue() == model.getMinimum()) { // haven't begun
		    g.setColor(MetalLookAndFeel.getControlShadow());
		} else { // Some portion of bar is filled
		    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
		}
		g.drawLine(barRectX,barRectY, barRectX,barRectHeight);
		
		//     over the filled portion
		//     get color from "line on left" above
		g.drawLine(barRectX,barRectY, amountFull,barRectY);
	    } else { // VERTICAL
		// Highlighting
		//     left of the unfilled portion
		//     well, draw all the way down; let others draw over it
		g.setColor(MetalLookAndFeel.getControlShadow());
		g.drawLine(barRectX,barRectY, barRectX,barRectHeight);
		
		//     line on bottom
		if (model.getValue() == model.getMinimum()) { // haven't begun
		    g.setColor(MetalLookAndFeel.getControlShadow());
		} else { // Some portion of bar is filled
		    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
		}
		g.drawLine(barRectX,     barRectHeight,
			   barRectWidth, barRectHeight);
		
		//     left of the filled portion
		//     pick up color from the "line on bottom" above
		g.drawLine(barRectX, barRectHeight,
			   barRectX, barRectHeight-amountFull+b.top);
	    }
	}
    }
}
