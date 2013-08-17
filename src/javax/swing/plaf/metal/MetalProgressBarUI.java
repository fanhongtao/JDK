/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.18 02/06/02
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
                int fillX = barRectX;
                if( !MetalUtils.isLeftToRight(c) ) {
                    fillX += barRectWidth - amountFull;
                }
		// Highlighting
		//     over the unfilled portion
		//     well, draw all the way across; let others draw over it
		g.setColor(MetalLookAndFeel.getControlShadow());
		g.drawLine(barRectX,barRectY,barRectX+barRectWidth-1,barRectY);
		
		//     line on left
		if (fillX == barRectX && amountFull > 0) {
                    // filled area is touching left edge of bar
		    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
		} else {
                    // filled area is not touching left edge of bar
		    g.setColor(MetalLookAndFeel.getControlShadow());
		}
		g.drawLine(barRectX, barRectY,
                           barRectX, barRectY+barRectHeight-1);
		
		//     highlight over the filled portion
                if( amountFull > 0 ) {
                    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                    g.drawLine(fillX, barRectY, fillX+amountFull-1, barRectY);
                }
	    } else { // VERTICAL
		// Highlighting
		//     left of the unfilled portion
		//     well, draw all the way down; let others draw over it
		g.setColor(MetalLookAndFeel.getControlShadow());
		g.drawLine(barRectX, barRectY,
                           barRectX, barRectY+barRectHeight-1);
		
		//     line on bottom
		if ( amountFull <= 0 ) { // haven't begun
		    g.setColor(MetalLookAndFeel.getControlShadow());
		} else { // Some portion of bar is filled
		    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
		}
		g.drawLine(barRectX, barRectY+barRectHeight-1,
			   barRectX+barRectWidth-1, barRectY+barRectHeight-1);
		
		//     left of the filled portion
		//     pick up color from the "line on bottom" above
		g.drawLine(barRectX, barRectY+barRectHeight-1,
			   barRectX, barRectY+barRectHeight-amountFull);
	    }
	}
    }
}
