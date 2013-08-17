/*
 * @(#)MotifPopupMenuUI.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;


/**
 * A Motif L&F implementation of PopupMenuUI. 
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.16 04/22/99
 * @author Georges Saab
 * @author Rich Schiavi
 */
public class MotifPopupMenuUI extends BasicPopupMenuUI {
    private static Border border = null;
    private Font titleFont = null;

    public static ComponentUI createUI(JComponent x) {
	return new MotifPopupMenuUI();
    }

    /* This has to deal with the fact that the title may be wider than
       the widest child component.
       */
    public Dimension getPreferredSize(JComponent c) {
	LayoutManager layout = c.getLayout();
	Dimension d = layout.preferredLayoutSize(c);
	String title = ((JPopupMenu)c).getLabel();
	if (titleFont == null) {
	    UIDefaults table = UIManager.getLookAndFeelDefaults();
	    titleFont = table.getFont("PopupMenu.font");
	}
	FontMetrics fm = c.getFontMetrics(titleFont);
	int         stringWidth = 0;
        
        if (title!=null) {
            stringWidth +=fm.stringWidth(title);
        }

	if (d.width < stringWidth) {
	    d.width = stringWidth + 8;
	    Insets i = c.getInsets();
	    if (i!=null) {
		d.width += i.left + i.right;
	    }
	    if (border != null) {
		i = border.getBorderInsets(c);
		d.width += i.left + i.right;
	    }

	    return d;
	}
	return null;
    }

    public void installDefaults() {
	// This should all go in the table, but can't until we can
	// have API Change to add the borders to the factory...
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	if (border == null) {
	    border = new CompoundUIResourceBorder(
                         new MotifBorders.BevelBorder(true,
					table.getColor("controlShadow"),
					table.getColor("controlLtHighlight")),
			 new MotifPopupMenuBorder(
                                        table.getFont("PopupMenu.font"),
                                        table.getColor("PopupMenu.background"),
                                        table.getColor("PopupMenu.foreground"),
                                        table.getColor("controlShadow"),
                                        table.getColor("controlLtHighlight")
					      ));
	}
	table.put("PopupMenu.border", border);
	super.installDefaults();
    }
    protected ChangeListener createChangeListener(JPopupMenu m) {
	return new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {}
	};
    }


    private static class CompoundUIResourceBorder extends CompoundBorder implements UIResource {
	public CompoundUIResourceBorder(Border a, Border b) {
	    super(a,b);
	}
    }

    /*
     *  Fix to 4187004:
     * This class is currently private since API changes are not allowed.
     * When they are, it should be moved to MotifBorderFactory and made
     * public.  At that time, the installDefaults method above can
     * be removed and the defaults table in MotifLookAndFeel updated.
     */
    private static class MotifPopupMenuBorder extends AbstractBorder implements UIResource {
	protected Font   font;
	protected Color  background;
	protected Color  foreground;
	protected Color  shadowColor;
	protected Color  highlightColor;

	// Space between the border and text
	static protected final int TEXT_SPACING = 2;

	// Space for the separator under the title
	static protected final int GROOVE_HEIGHT = 2;

	/**
	 * Creates a MotifPopupMenuBorder instance 
	 * 
	 */
	public MotifPopupMenuBorder(
				    Font titleFont,
				    Color bgColor,
				    Color fgColor,
				    Color shadow,
				    Color highlight)       {
	    this.font = titleFont;
	    this.background = bgColor;
	    this.foreground = fgColor;
	    this.shadowColor = shadow;
	    this.highlightColor = highlight;
	}
	
	/**
	 * Paints the border for the specified component with the 
	 * specified position and size.
	 * @param c the component for which this border is being painted
	 * @param g the paint graphics
	 * @param x the x position of the painted border
	 * @param y the y position of the painted border
	 * @param width the width of the painted border
	 * @param height the height of the painted border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	    
	    Font origFont = g.getFont();
	    Color origColor = g.getColor();

	    String title = ((JPopupMenu)c).getLabel();
	    if (title == null) {
		return;
	    }

	    g.setFont(font);
	    
	    FontMetrics fm = g.getFontMetrics();
	    int         fontHeight = fm.getHeight();
	    int         descent = fm.getDescent();
	    int         ascent = fm.getAscent();
	    Point       textLoc = new Point();
	    int         stringWidth = fm.stringWidth(title);
	    
	    textLoc.y = y + ascent + TEXT_SPACING;
	    textLoc.x = x + ((width - stringWidth) / 2);
	    
	    g.setColor(background);
	    g.fillRect(textLoc.x - TEXT_SPACING, textLoc.y - (fontHeight-descent),
		       stringWidth + (2 * TEXT_SPACING), fontHeight - descent);
	    g.setColor(foreground);
	    g.drawString(title, textLoc.x, textLoc.y);
	    
	    MotifGraphicsUtils.drawGroove(g, x, textLoc.y + TEXT_SPACING, 
					  width, GROOVE_HEIGHT,
                                          shadowColor, highlightColor);

	    g.setFont(origFont);
	    g.setColor(origColor);
	}
	
	/**
	 * Returns the insets of the border.
	 * @param c the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c) {
	    return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}
	
	/** 
	 * Reinitialize the insets parameter with this Border's current Insets. 
	 * @param c the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
	    FontMetrics fm;
	    int         descent = 0;
	    int         ascent = 16;

	    String title = ((JPopupMenu)c).getLabel();
	    if (title == null) {
		insets.left = insets.top = insets.right = insets.bottom = 0;
		return insets;
	    }

	    fm = c.getFontMetrics(font);
	    
	    if(fm != null) {
		descent = fm.getDescent();
		ascent = fm.getAscent();
	    }
	    
	    insets.top += ascent + descent + TEXT_SPACING + GROOVE_HEIGHT;
	    return insets;
	}
			
    }

}




