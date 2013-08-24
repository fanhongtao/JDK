/*
 * @(#)SampleTreeCellRenderer.java	1.20 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)SampleTreeCellRenderer.java	1.20 05/11/17
 */

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class SampleTreeCellRenderer extends JLabel implements TreeCellRenderer
{
    /** Font used if the string to be displayed isn't a font. */
    static protected Font             defaultFont;
    /** Icon to use when the item is collapsed. */
    static protected ImageIcon        collapsedIcon;
    /** Icon to use when the item is expanded. */
    static protected ImageIcon        expandedIcon;

    /** Color to use for the background when selected. */
    static protected final Color SelectedBackgroundColor = Color.yellow;//new Color(0, 0, 128);

    static
    {
	try {
	    defaultFont = new Font("SansSerif", 0, 12);
	} catch (Exception e) {}
	try {
	    collapsedIcon = new ImageIcon(SampleTreeCellRenderer.class.getResource("/resources/images/collapsed.gif"));
            expandedIcon = new ImageIcon(SampleTreeCellRenderer.class.getResource("/resources/images/expanded.gif"));
	} catch (Exception e) {
	    System.out.println("Couldn't load images: " + e);
	}
    }

    /** Whether or not the item that was last configured is selected. */
    protected boolean            selected;

    /**
      * This is messaged from JTree whenever it needs to get the size
      * of the component or it wants to draw it.
      * This attempts to set the font based on value, which will be
      * a TreeNode.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
					  boolean selected, boolean expanded,
					  boolean leaf, int row,
						  boolean hasFocus) {
	Font            font;
	String          stringValue = tree.convertValueToText(value, selected,
					   expanded, leaf, row, hasFocus);

	/* Set the text. */
	setText(stringValue);
	/* Tooltips used by the tree. */
	setToolTipText(stringValue);

	/* Set the image. */
	if(expanded)
	    setIcon(expandedIcon);
	else if(!leaf)
	    setIcon(collapsedIcon);
	else
	    setIcon(null);

	/* Set the color and the font based on the SampleData userObject. */
	SampleData         userObject = (SampleData)((DefaultMutableTreeNode)value)
	                                .getUserObject();
	if(hasFocus)
	    setForeground(Color.cyan);
	else
	    setForeground(userObject.getColor());
	if(userObject.getFont() == null)
	    setFont(defaultFont);
	else
	    setFont(userObject.getFont());

	/* Update the selected flag for the next paint. */
	this.selected = selected;

	return this;
    }

    /**
      * paint is subclassed to draw the background correctly.  JLabel
      * currently does not allow backgrounds other than white, and it
      * will also fill behind the icon.  Something that isn't desirable.
      */
    public void paint(Graphics g) {
	Color            bColor;
	Icon             currentI = getIcon();

	if(selected)
	    bColor = SelectedBackgroundColor;
	else if(getParent() != null)
	    /* Pick background color up from parent (which will come from
	       the JTree we're contained in). */
	    bColor = getParent().getBackground();
	else
	    bColor = getBackground();
	g.setColor(bColor);
	if(currentI != null && getText() != null) {
	    int          offset = (currentI.getIconWidth() + getIconTextGap());

            if (getComponentOrientation().isLeftToRight()) {
                g.fillRect(offset, 0, getWidth() - 1 - offset,
                           getHeight() - 1);
            }
            else {
                g.fillRect(0, 0, getWidth() - 1 - offset, getHeight() - 1);
            }
	}
	else
	    g.fillRect(0, 0, getWidth()-1, getHeight()-1);
	super.paint(g);
    }
}
