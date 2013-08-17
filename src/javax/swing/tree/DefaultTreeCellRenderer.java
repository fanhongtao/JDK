/*
 * @(#)DefaultTreeCellRenderer.java	1.31 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.tree;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;

/**
 * Displays an entry in a tree.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 * @version 1.31 04/22/99
 * @author Rob Davis
 * @author Ray Ryan
 * @author Scott Violet
 */
public class DefaultTreeCellRenderer extends JLabel implements TreeCellRenderer
{
    /** Is the value currently selected. */
    protected boolean selected;
    // These two ivars will be made protected later.
    /** True if has focus. */
    private boolean hasFocus;
    /** True if draws focus border around icon as well. */
    private boolean drawsFocusBorderAroundIcon;

    // Icons
    /** Icon used to show non-leaf nodes that aren't expanded. */
    transient protected Icon closedIcon;

    /** Icon used to show leaf nodes. */
    transient protected Icon leafIcon;

    /** Icon used to show non-leaf nodes that are expanded. */
    transient protected Icon openIcon;

    // Colors
    /** Color to use for the foreground for selected nodes. */
    protected Color textSelectionColor;

    /** Color to use for the foreground for non-selected nodes. */
    protected Color textNonSelectionColor;

    /** Color to use for the background when a node is selected. */
    protected Color backgroundSelectionColor;

    /** Color to use for the background when the node isn't selected. */
    protected Color backgroundNonSelectionColor;

    /** Color to use for the background when the node isn't selected. */
    protected Color borderSelectionColor;

    /**
      * Returns a new instance of DefaultTreeCellRenderer.  Alignment is
      * set to left aligned. Icons and text color are determined from the
      * UIManager.
      */
    public DefaultTreeCellRenderer() {
	setHorizontalAlignment(JLabel.LEFT);

	setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
	setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	setOpenIcon(UIManager.getIcon("Tree.openIcon"));

	setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
	Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
	drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).
				      booleanValue());
    }


    /**
      * Returns the default icon, for the current laf, that is used to
      * represent non-leaf nodes that are expanded.
      */
    public Icon getDefaultOpenIcon() {
	return UIManager.getIcon("Tree.openIcon");
    }

    /**
      * Returns the default icon, for the current laf, that is used to
      * represent non-leaf nodes that are not expanded.
      */
    public Icon getDefaultClosedIcon() {
	return UIManager.getIcon("Tree.closedIcon");
    }

    /**
      * Returns the default icon, for the current laf, that is used to
      * represent leaf nodes.
      */
    public Icon getDefaultLeafIcon() {
	return UIManager.getIcon("Tree.leafIcon");
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are expanded.
      */
    public void setOpenIcon(Icon newIcon) {
	openIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are expanded.
      */
    public Icon getOpenIcon() {
	return openIcon;
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are not expanded.
      */
    public void setClosedIcon(Icon newIcon) {
	closedIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are not
      * expanded.
      */
    public Icon getClosedIcon() {
	return closedIcon;
    }

    /**
      * Sets the icon used to represent leaf nodes.
      */
    public void setLeafIcon(Icon newIcon) {
	leafIcon = newIcon;
    }

    /**
      * Returns the icon used to represent leaf nodes.
      */
    public Icon getLeafIcon() {
	return leafIcon;
    }

    /**
      * Sets the color the text is drawn with when the node is selected.
      */
    public void setTextSelectionColor(Color newColor) {
	textSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is selected.
      */
    public Color getTextSelectionColor() {
	return textSelectionColor;
    }

    /**
      * Sets the color the text is drawn with when the node isn't selected.
      */
    public void setTextNonSelectionColor(Color newColor) {
	textNonSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node isn't selected.
      */
    public Color getTextNonSelectionColor() {
	return textNonSelectionColor;
    }

    /**
      * Sets the color to use for the background if node is selected.
      */
    public void setBackgroundSelectionColor(Color newColor) {
	backgroundSelectionColor = newColor;
    }


    /**
      * Returns the color to use for the background if node is selected.
      */
    public Color getBackgroundSelectionColor() {
	return backgroundSelectionColor;
    }

    /**
      * Sets the background color to be used for non selected nodes.
      */
    public void setBackgroundNonSelectionColor(Color newColor) {
	backgroundNonSelectionColor = newColor;
    }

    /**
      * Returns the background color to be used for non selected nodes.
      */
    public Color getBackgroundNonSelectionColor() {
	return backgroundNonSelectionColor;
    }

    /**
      * Sets the color to use for the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the color the border is drawn.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }

    /**
     * Subclassed to only accept the font if it isn't a FontUIResource.
     */
    public void setFont(Font font) {
	if(font instanceof FontUIResource)
	    font = null;
	super.setFont(font);
    }

    /**
     * Subclassed to only accept the color if it isn't a ColorUIResource.
     */
    public void setBackground(Color color) {
	if(color instanceof ColorUIResource)
	    color = null;
	super.setBackground(color);
    }

    /**
      * Configures the renderer based on the passed in components.
      * The value is set from messaging value with toString().
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {
	String         stringValue = tree.convertValueToText(value, sel,
					  expanded, leaf, row, hasFocus);

	this.hasFocus = hasFocus;
	setText(stringValue);
	if(sel)
	    setForeground(getTextSelectionColor());
	else
	    setForeground(getTextNonSelectionColor());
	// There needs to be a way to specify disabled icons.
	if (!tree.isEnabled()) {
	    setEnabled(false);
	    if (leaf) {
		setDisabledIcon(getLeafIcon());
	    } else if (expanded) {
		setDisabledIcon(getOpenIcon());
	    } else {
		setDisabledIcon(getClosedIcon());
	    }
	}
	else {
	    setEnabled(true);
	    if (leaf) {
		setIcon(getLeafIcon());
	    } else if (expanded) {
		setIcon(getOpenIcon());
	    } else {
		setIcon(getClosedIcon());
	    }
	}
	    
	selected = sel;

	return this;
    }

    /**
      * Paints the value.  The background is filled based on selected.
      */
    public void paint(Graphics g) {
	Color bColor;

	if(selected) {
	    bColor = getBackgroundSelectionColor();
	} else {
	    bColor = getBackgroundNonSelectionColor();
	    if(bColor == null)
		bColor = getBackground();
	}
	int imageOffset = -1;
	if(bColor != null) {
	    Icon currentI = getIcon();

	    imageOffset = getLabelStart();
	    g.setColor(bColor);
	    g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset,
		       getHeight());
	}
	if (hasFocus) {
	    if (drawsFocusBorderAroundIcon) {
		imageOffset = 0;
	    }
	    else if (imageOffset == -1) {
		imageOffset = getLabelStart();
	    }
	    Color       bsColor = getBorderSelectionColor();

	    if (bsColor != null) {
		g.setColor(bsColor);
		g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset,
			   getHeight() - 1);
	    }
	}
	super.paint(g);
    }

    private int getLabelStart() {
	Icon currentI = getIcon();
	if(currentI != null && getText() != null) {
	    return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
	}
	return 0;
    }

    /**
     * Overrides <code>JComponent.getPreferredSize</code> to
     * return slightly wider preferred size value.
     */
    public Dimension getPreferredSize() {
	Dimension        retDimension = super.getPreferredSize();

	if(retDimension != null)
	    retDimension = new Dimension(retDimension.width + 3,
					 retDimension.height);
	return retDimension;
    }

}
