/*
 * @(#)WindowsProgressBarUI.java	1.21 03/04/22
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;


/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.21 04/22/03
 * @author Michael C. Albers
 */
public class WindowsProgressBarUI extends BasicProgressBarUI
{

    private Rectangle boxRect;

    public static ComponentUI createUI(JComponent x) {
	return new WindowsProgressBarUI();
    }


    protected void installDefaults() {
	super.installDefaults();

	if (XPStyle.getXP() != null) {
	    progressBar.setOpaque(false);
	    progressBar.setBorder(null);
	}
    }

    protected Dimension getPreferredInnerHorizontal() {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    return xp.getDimension("progress.bar.normalsize");
	} else {
	    return super.getPreferredInnerHorizontal();
	}
    }
    
    protected Dimension getPreferredInnerVertical() {
	if (XPStyle.getXP() != null) {
	    Dimension d = getPreferredInnerHorizontal();
	    return new Dimension(d.height, d.width); // Reverse values
	} else {
	    return super.getPreferredInnerVertical();
	}
    }
    
    protected void paintDeterminate(Graphics g, JComponent c) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    boolean vertical = (progressBar.getOrientation() == JProgressBar.VERTICAL);
	    int barRectWidth = progressBar.getWidth();
	    int barRectHeight = progressBar.getHeight()-1;
	    // amount of progress to draw
	    int amountFull = getAmountFull(null, barRectWidth, barRectHeight);

	    paintXPBackground(g, vertical, barRectWidth, barRectHeight);
	    // Paint progress
	    if (progressBar.isStringPainted()) {
		// Do not paint the standard stripes from the skin, because they obscure
		// the text
		g.setColor(progressBar.getForeground());
		barRectHeight -= 2;
		barRectWidth -= 2;
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke((float)(vertical ? barRectWidth : barRectHeight),
					     BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		if (!vertical) {
		    g2.drawLine(2,              barRectHeight / 2 + 1,
				amountFull - 2, barRectHeight / 2 + 1);
		    paintString(g, 0, 0, barRectWidth, barRectHeight, amountFull, null);
		} else {
		    g2.drawLine(barRectWidth/2 + 1, barRectHeight + 1,
				barRectWidth/2 + 1, barRectHeight + 1 - amountFull + 2);
		    paintString(g, 2, 2, barRectWidth, barRectHeight, amountFull, null);
		}

	    } else {
		XPStyle.Skin skin =
		    xp.getSkin(vertical ? "progress.chunkvert" : "progress.chunk");
		int thickness;
		if (vertical) {
		    thickness = barRectWidth - 5;
		} else {
		    thickness = barRectHeight - 5;
		}

		int chunkSize = xp.getInt("progress.progresschunksize", 2);
		int spaceSize = xp.getInt("progress.progressspacesize", 0);
		int nChunks = (amountFull-4) / (chunkSize + spaceSize);

		// See if we can squeeze in an extra chunk without spacing after
		if (spaceSize > 0 && (nChunks * (chunkSize + spaceSize) + chunkSize) < (amountFull-4)) {
		    nChunks++;
		}

		for (int i = 0; i < nChunks; i++) {
		    if (vertical) {
			skin.paintSkin(g,
				       3, barRectHeight - i * (chunkSize + spaceSize) - chunkSize - 2,
				       thickness, chunkSize, 0);
		    } else {
			skin.paintSkin(g,
				       4 + i * (chunkSize + spaceSize), 2,
				       chunkSize, thickness, 0);
		    }
		}
	    }
	} else {
	    super.paintDeterminate(g, c);
	}
    }


    protected void paintIndeterminate(Graphics g, JComponent c) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    boolean vertical = (progressBar.getOrientation() == JProgressBar.VERTICAL);
	    int barRectWidth = progressBar.getWidth();
	    int barRectHeight = progressBar.getHeight()-1;
	    paintXPBackground(g, vertical, barRectWidth, barRectHeight);

	    // Paint the bouncing box.
	    boxRect = getBox(boxRect);
	    if (boxRect != null) {
		g.setColor(progressBar.getForeground());
		if (!vertical) {
		    g.fillRect(boxRect.x, boxRect.y + 2, boxRect.width, boxRect.height - 4);
		} else {
		    g.fillRect(boxRect.x + 2, boxRect.y, boxRect.width - 3, boxRect.height);
		}
		if (progressBar.isStringPainted()) {
		    if (!vertical) {
			paintString(g, -1, -1, barRectWidth, barRectHeight, 0, null);
		    } else {
			paintString(g, 1, 1, barRectWidth, barRectHeight, 0, null);
		    }
		}
	    }
	} else {
	    super.paintIndeterminate(g, c);
	}
    }

    private void paintXPBackground(Graphics g, boolean vertical,
				   int barRectWidth, int barRectHeight) {
	XPStyle xp = XPStyle.getXP();
	String category = vertical ? "progress.barvert" : "progress.bar";
	XPStyle.Skin skin = xp.getSkin(category);

	// Paint background
	Color fillColor = xp.getColor(category + ".fillcolorhint", null);
	if (fillColor != null) {
	    g.setColor(fillColor);
	    g.fillRect(2, 2, barRectWidth-4, barRectHeight-4);
	}
	skin.paintSkin(g, 0, 0, barRectWidth, barRectHeight, 0);
    }
}

