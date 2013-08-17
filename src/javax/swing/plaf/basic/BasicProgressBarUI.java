/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.io.Serializable;


/**
 * A Basic L&F implementation of ProgressBarUI.
 *
 * @version 1.46 02/06/02
 * @author Michael C. Albers
 */
public class BasicProgressBarUI extends ProgressBarUI {
    
    private static final Dimension PREFERRED_INNER_HORIZONTAL = new Dimension(146, 12);
    private static final Dimension PREFERRED_INNER_VERTICAL = new Dimension(12, 146);
    private int cachedPercent;
    private int cellLength, cellSpacing;
    // The "selectionForeground" is the color of the text when it is drawn
    // over a filled area of the progress bar. The "selectionBackground"
    // is for the text over the unfilled progress bar area.
    private Color selectionForeground, selectionBackground;
    protected JProgressBar progressBar;
    protected ChangeListener changeListener;
    
    
    public static ComponentUI createUI(JComponent x) {
	return new BasicProgressBarUI();
    }
    
    public void installUI(JComponent c) {
	progressBar = (JProgressBar)c;
	installDefaults();
	installListeners();
    }
    
    public void uninstallUI(JComponent c) {
	uninstallDefaults();
	uninstallListeners();
	progressBar = null;
    }
    
    protected void installDefaults() {
	progressBar.setOpaque(true);
 	LookAndFeel.installBorder(progressBar,"ProgressBar.border");	
	LookAndFeel.installColorsAndFont(progressBar,
					 "ProgressBar.background",
					 "ProgressBar.foreground",
					 "ProgressBar.font");
	cellLength = UIManager.getInt("ProgressBar.cellLength");
	cellSpacing = UIManager.getInt("ProgressBar.cellSpacing");
	selectionForeground = UIManager.getColor("ProgressBar.selectionForeground");
	selectionBackground = UIManager.getColor("ProgressBar.selectionBackground");
    }
    
    protected void uninstallDefaults() {
 	LookAndFeel.uninstallBorder(progressBar);	
    }
    
    protected void installListeners() {
	changeListener = new ChangeHandler();
	progressBar.addChangeListener(changeListener);
    }	
    
    protected void uninstallListeners() {
	progressBar.removeChangeListener(changeListener);
    }
    
    // Many of the Basic*UI components have the following methods.
    // This component does not have these methods because *ProgressBarUI
    //  is not a compound component and does not accept input.
    //
    // protected void installComponents()
    // protected void uninstallComponents()
    // protected void installKeyboardActions()
    // protected void uninstallKeyboardActions()

    protected Dimension getPreferredInnerHorizontal() {
	return PREFERRED_INNER_HORIZONTAL;
    }
    
    protected Dimension getPreferredInnerVertical() {
	return PREFERRED_INNER_VERTICAL;
    }
    
    /**
     * The "selectionForeground" is the color of the text when it is drawn
     * over a filled area of the progress bar.
     */
    protected Color getSelectionForeground() {
	return selectionForeground;
    }
    
    /**
     * The "selectionBackground" is the color of the text when it is drawn
     * over an unfilled area of the progress bar.
     */
    protected Color getSelectionBackground() {
	return selectionBackground;
    }
    
    private int getCachedPercent() {
	return cachedPercent;
    }
    
    private void setCachedPercent(int cachedPercent) {
	this.cachedPercent = cachedPercent;
    }
    
    /**
     * Returns the width (if HORIZONTAL) or height (if VERTICAL)
     * of each of the indivdual cells/units to be rendered in the
     * progress bar. However, for text rendering simplification and 
     * aesthetic considerations, this function will return 1 when
     * the progress string is being rendered.
     *
     * @return the value representing the spacing between cells
     * @see    #setCellLength
     * @see    JProgressBar#isStringPainted
     */
    protected int getCellLength() {
	if (progressBar.isStringPainted()) {
	    return 1;
	} else {
	    return cellLength;
	}
    }
    
    protected void setCellLength(int cellLen) {
	this.cellLength = cellLen;
    }
    
    /**
     * Returns the spacing between each of the cells/units in the
     * progress bar. However, for text rendering simplification and 
     * aesthetic considerations, this function will return 0 when
     * the progress string is being rendered.
     *
     * @return the value representing the spacing between cells
     * @see    #setCellSpacing
     * @see    JProgressBar#isStringPainted
     */
    protected int getCellSpacing() {
	if (progressBar.isStringPainted()) {
	    return 0;
	} else {
	    return cellSpacing;
	}
    }
    
    protected void setCellSpacing(int cellSpace) {
	this.cellSpacing = cellSpace;
    }
    
    /**
     * This determines the amount of the progress bar that should be filled
     * based on the percent done gathered from the model. This is a common
     * operation so it was abstracted out. It assumes that your progress bar
     * is linear. That is, if you are making a circular progress indicator,
     * you will want to override this method.
     */
    protected int getAmountFull(Insets b, int width, int height) {
	int amountFull = 0;
	BoundedRangeModel model = progressBar.getModel();
	
	if ( (model.getMaximum() - model.getMinimum()) != 0) {
	    if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	        amountFull = (int)Math.round(width *
					     progressBar.getPercentComplete());
	    } else {
	        amountFull = (int)Math.round(height *
					     progressBar.getPercentComplete());
	    }
	}
	return amountFull;
    }
    
    /**
     * All purpose paint method that should do the right thing for almost
     * all linear progress bars. By setting a few values in the defaults
     * table, things should work just fine to paint your progress bar.
     * Naturally, override this if you are making a circular or
     * semi-circular progress bar.
     */
    public void paint(Graphics g, JComponent c) {
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

	int current;
        int cellLength = getCellLength();
        int cellSpacing = getCellSpacing();
	// a cell and its spacing
	int increment = cellLength + cellSpacing;
	// amount of progress to draw
	int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
	
	g.setColor(progressBar.getForeground());
	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            // fillX is the left edge of the region to be filled. 
            int fillX = barRectX;
            boolean leftToRight = BasicGraphicsUtils.isLeftToRight(c);
            if( !leftToRight ) {
                // If the bar fills from the right, adjust fillX
                fillX += barRectWidth - amountFull;
            }
	    // draw the cells
	    if (cellSpacing == 0 && amountFull > 0) {
                // draw one big Rect because there is no space between cells
                g.fillRect(fillX, barRectY, amountFull, barRectHeight);
	    } else {
                // draw each individual cell
                Rectangle oldClip = g.getClipBounds();
                g.setClip(barRectX, barRectY, barRectWidth, barRectHeight);
                
                int numCells = (int)Math.ceil( (double)amountFull/increment );
                if( !leftToRight ) {
                    // Adjust so that filled region is flush to right side.
                    fillX += cellSpacing - (numCells*increment - amountFull);
                }

                for( current=fillX; numCells-->0; current+=increment ) {
                    g.fillRect(current, barRectY, cellLength, barRectHeight);
                }
                g.setClip(oldClip);
            }
	} else { // VERTICAL
            // fillY is the top edge of the region to be filled.
            int fillY = barRectY + barRectHeight - amountFull;
            
	    // draw the cells
	    if (cellSpacing == 0 && amountFull > 0) {
                // draw one big Rect because there is no space between cells
		g.fillRect(barRectX, fillY, barRectWidth, amountFull);
	    } else {
                // draw each individual cell
                Rectangle oldClip = g.getClipBounds();
                g.setClip(barRectX, barRectY, barRectWidth, barRectHeight);

                int numCells = (int)Math.ceil( (double)amountFull/increment );

                // Adjust so that filled region is flush to bottom edge.
                fillY += cellSpacing - (numCells*increment - amountFull);

                for( current=fillY; numCells-->0; current+=increment ) {
                    g.fillRect(barRectX, current, barRectWidth, cellLength);
                }
                g.setClip(oldClip);
	    }
	}
	
	// Deal with possible text painting
	if (progressBar.isStringPainted()) {
	    paintString(g, barRectX, barRectY,
			barRectWidth, barRectHeight,
			amountFull, b);
	}
    }


    protected void paintString(Graphics g, int x, int y,
			       int width, int height,
			       int amountFull, Insets b) {
	String progressString = progressBar.getString();
	g.setFont(progressBar.getFont());
	Point renderLocation = getStringPlacement(g, progressString,
						  x, y, width, height);
	Rectangle oldClip = g.getClipBounds();
	
	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	    g.setColor(getSelectionForeground());
	    g.drawString(progressString, renderLocation.x, renderLocation.y);
	    g.setColor(getSelectionBackground());
            if( BasicGraphicsUtils.isLeftToRight(progressBar) ) {
                g.setClip(x+amountFull, y, width-amountFull, height);
            } else {
                g.setClip(x, y, width-amountFull, height);
            }
	    g.drawString(progressString, renderLocation.x, renderLocation.y);
	} else { // VERTICAL
	    // do this from the top of the bar to the bottom
	    g.setColor(getSelectionBackground());
	    g.drawString(progressString, renderLocation.x, renderLocation.y);
	    g.setColor(getSelectionForeground());
	    g.setClip(x, height-amountFull+b.top, width, height);
	    g.drawString(progressString, renderLocation.x, renderLocation.y);
	}
	g.setClip(oldClip);
    }
    
    
    /**
     * Designate the place where the progress string will be drawn.
     * This implementation places it at the center of the progress
     * bar (in both x and y). Override this if you want to right,
     * left, top, or bottom align the progress string or if you need
     * to nudge it around for any reason.
     */
    protected Point getStringPlacement(Graphics g, String progressString,
				       int x,int y,int width,int height) {
	FontMetrics fontSizer = progressBar.getFontMetrics(
	                                    progressBar.getFont());
	int stringWidth = fontSizer.stringWidth(progressString);
	int stringHeight = fontSizer.getAscent() + fontSizer.getDescent();

	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	    return new Point(x + Math.round(width/2 - stringWidth/2),
			     y + fontSizer.getHeight() - 
			         Math.round(fontSizer.getDescent()/2) );
	} else { // VERTICAL
	    return new Point(x + Math.round(width/2 - stringWidth/2),
			     y + Math.round(height/2 + stringHeight/2) );
	}
    }
    
    
    public Dimension getPreferredSize(JComponent c) {
	Dimension	size;
	Insets		border = progressBar.getInsets();
	FontMetrics     fontSizer = progressBar.getFontMetrics(
         	                                  progressBar.getFont());
	
	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	    size = new Dimension(getPreferredInnerHorizontal());
	    // Ensure that the progress string will fit
	    if (progressBar.isStringPainted()) {
		// I'm doing this for completeness.
		String progString = progressBar.getString();
		int stringWidth = fontSizer.stringWidth(progString);
		if (stringWidth > size.width) {
		    size.width = stringWidth;
		}
		// This uses both Height and Descent to be sure that 
		// there is more than enough room in the progress bar
		// for everything.
		// This does have a strange dependency on 
		// getStringPlacememnt() in a funny way.
		int stringHeight = fontSizer.getHeight() +
		                   fontSizer.getDescent();;
		if (stringHeight > size.height) {
		    size.height = stringHeight;
		}
	    }
	} else {
	    size = new Dimension(getPreferredInnerVertical());
	    // Ensure that the progress string will fit.
	    // I can't, with any honesty, suggest that you ask for
	    //  the progress string on a VERTICAL ProgressBar. If you
	    //  do, you get what you deserve.
	    if (progressBar.isStringPainted()) {
		// This is an attempt to ensure that there will be enough
		// room for the "standard" XXX% string that is provided by
		// progress bar. Yes, it's inefficient.
		String progString = new String("100%");
		int stringWidth = fontSizer.stringWidth(progString);
		if (stringWidth > size.width) {
		    size.width = stringWidth;
		}
		progString = progressBar.getString();
		stringWidth = fontSizer.stringWidth(progString);
		if (stringWidth > size.width) {
		    size.width = stringWidth;
		}
		// This is also for completeness.
		int stringHeight = fontSizer.getHeight() +
		                   fontSizer.getDescent();;
		if (stringHeight > size.height) {
		    size.height = stringHeight;
		}
	    }
	}

	size.width += border.left + border.right;
	size.height += border.top + border.bottom;
	return size;
    }

    /**
     * The Minimum size for this component is 10. The rationale here 
     * is that there should be at least one pixel per 10 percent.
     */
    public Dimension getMinimumSize(JComponent c) {
	Dimension pref = getPreferredSize(progressBar);
	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	    pref.width = 10;
	} else {
	    pref.height = 10;
	}
	return pref;
    }

    public Dimension getMaximumSize(JComponent c) {
	Dimension pref = getPreferredSize(progressBar);
	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	    pref.width = Short.MAX_VALUE;
	} else {
	    pref.height = Short.MAX_VALUE;
	}
	return pref;
    }


    //
    // Change Events
    //
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicProgressBarUI.
     */
    public class ChangeHandler implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    BoundedRangeModel model = progressBar.getModel();
	    int newRange = model.getMaximum() - model.getMinimum();
	    int newPercent;
	    int oldPercent = getCachedPercent();
	    
	    if (newRange > 0) {
		newPercent = (100 * model.getValue()) / newRange;
	    } else {
		newPercent = 0;
	    }
	    
	    if (newPercent != oldPercent) {
		setCachedPercent(newPercent);
		progressBar.repaint();
	    }
	}
    }
}
