/*
 * @(#)FlowLayout.java	1.46 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * A flow layout arranges components in a left-to-right flow, much
 * like lines of text in a paragraph. Flow layouts are typically used
 * to arrange buttons in a panel. It will arrange
 * buttons left to right until no more buttons fit on the same line.
 * Each line is centered.
 * <p>
 * For example, the following picture shows an applet using the flow
 * layout manager (its default layout manager) to position three buttons:
 * <p>
 * <img src="doc-files/FlowLayout-1.gif"
 * ALT="Graphic of Layout for Three Buttons"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * Here is the code for this applet:
 * <p>
 * <hr><blockquote><pre>
 * import java.awt.*;
 * import java.applet.Applet;
 *
 * public class myButtons extends Applet {
 *     Button button1, button2, button3;
 *     public void init() {
 *         button1 = new Button("Ok");
 *         button2 = new Button("Open");
 *         button3 = new Button("Close");
 *         add(button1);
 *         add(button2);
 *         add(button3);
 *     }
 * }
 * </pre></blockquote><hr>
 * <p>
 * A flow layout lets each component assume its natural (preferred) size.
 *
 * @version 	1.46, 01/23/03
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 * @since       JDK1.0
 */
public class FlowLayout implements LayoutManager, java.io.Serializable {

    /**
     * This value indicates that each row of components
     * should be left-justified.
     */
    public static final int LEFT 	= 0;

    /**
     * This value indicates that each row of components
     * should be centered.
     */
    public static final int CENTER 	= 1;

    /**
     * This value indicates that each row of components
     * should be right-justified.
     */
    public static final int RIGHT 	= 2;

    /**
     * This value indicates that each row of components
     * should be justified to the leading edge of the container's
     * orientation, for example, to the left in left-to-right orientations.
     *
     * @see     java.awt.Component#getComponentOrientation
     * @see     java.awt.ComponentOrientation
     * @since   1.2
     * Package-private pending API change approval
     */
    public static final int LEADING	= 3;

    /**
     * This value indicates that each row of components
     * should be justified to the trailing edge of the container's
     * orientation, for example, to the right in left-to-right orientations.
     *
     * @see     java.awt.Component#getComponentOrientation
     * @see     java.awt.ComponentOrientation
     * @since   1.2
     * Package-private pending API change approval
     */
    public static final int TRAILING = 4;

    /**
     * <code>align</code> is the property that determines
     * how each row distributes empty space.
     * It can be one of the following values:
     * <ul>
     * <code>LEFT</code>
     * <code>RIGHT</code>
     * <code>CENTER</code>
     * <code>LEADING</code>
     * <code>TRAILING</code>
     * </ul>
     *
     * @serial
     * @see #getAlignment
     * @see #setAlignment
     */
    int align;          // This is for 1.1 serialization compatibility

    /**
     * <code>newAlign</code> is the property that determines
     * how each row distributes empty space for the Java 2 platform,
     * v1.2 and greater.
     * It can be one of the following three values:
     * <ul>
     * <code>LEFT</code>
     * <code>RIGHT</code>
     * <code>CENTER</code>
     * <code>LEADING</code>
     * <code>TRAILING</code>
     * </ul>
     *
     * @serial
     * @since 1.2
     * @see #getAlignment
     * @see #setAlignment
     */
    int newAlign;       // This is the one we actually use

    /**
     * The flow layout manager allows a seperation of
     * components with gaps.  The horizontal gap will
     * specify the space between components.
     *
     * @serial
     * @see #getHgap()
     * @see #setHgap(int)
     */
    int hgap;

    /**
     * The flow layout manager allows a seperation of
     * components with gaps.  The vertical gap will
     * specify the space between rows.
     *
     * @serial
     * @see #getHgap()
     * @see #setHgap(int)
     */
    int vgap;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = -7262534875583282631L;

    /**
     * Constructs a new <code>FlowLayout</code> with a centered alignment and a
     * default 5-unit horizontal and vertical gap.
     */
    public FlowLayout() {
	this(CENTER, 5, 5);
    }

    /**
     * Constructs a new <code>FlowLayout</code> with the specified
     * alignment and a default 5-unit horizontal and vertical gap.
     * The value of the alignment argument must be one of
     * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>,
     * or <code>FlowLayout.CENTER</code>.
     * @param align the alignment value
     */
    public FlowLayout(int align) {
	this(align, 5, 5);
    }

    /**
     * Creates a new flow layout manager with the indicated alignment
     * and the indicated horizontal and vertical gaps.
     * <p>
     * The value of the alignment argument must be one of
     * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>,
     * or <code>FlowLayout.CENTER</code>.
     * @param      align   the alignment value
     * @param      hgap    the horizontal gap between components
     * @param      vgap    the vertical gap between components
     */
    public FlowLayout(int align, int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
        setAlignment(align);
    }

    /**
     * Gets the alignment for this layout.
     * Possible values are <code>FlowLayout.LEFT</code>,
     * <code>FlowLayout.RIGHT</code>, <code>FlowLayout.CENTER</code>,
     * <code>FlowLayout.LEADING</code>,
     * or <code>FlowLayout.TRAILING</code>.
     * @return     the alignment value for this layout
     * @see        java.awt.FlowLayout#setAlignment
     * @since      JDK1.1
     */
    public int getAlignment() {
	return newAlign;
    }

    /**
     * Sets the alignment for this layout.
     * Possible values are
     * <ul>
     * <li><code>FlowLayout.LEFT</code>
     * <li><code>FlowLayout.RIGHT</code>
     * <li><code>FlowLayout.CENTER</code>
     * <li><code>FlowLayout.LEADING</code>
     * <li><code>FlowLayout.TRAILING</code>
     * </ul>
     * @param      align one of the alignment values shown above
     * @see        #getAlignment()
     * @since      JDK1.1
     */
    public void setAlignment(int align) {
	this.newAlign = align;

        // this.align is used only for serialization compatibility,
        // so set it to a value compatible with the 1.1 version
        // of the class

        switch (align) {
	case LEADING:
            this.align = LEFT;
	    break;
	case TRAILING:
            this.align = RIGHT;
	    break;
        default:
            this.align = align;
	    break;
        }
    }

    /**
     * Gets the horizontal gap between components.
     * @return     the horizontal gap between components
     * @see        java.awt.FlowLayout#setHgap
     * @since      JDK1.1
     */
    public int getHgap() {
	return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components
     * @see        java.awt.FlowLayout#getHgap
     * @since      JDK1.1
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }

    /**
     * Gets the vertical gap between components.
     * @return     the vertical gap between components
     * @see        java.awt.FlowLayout#setVgap
     * @since      JDK1.1
     */
    public int getVgap() {
	return vgap;
    }

    /**
     * Sets the vertical gap between components.
     * @param vgap the vertical gap between components
     * @see        java.awt.FlowLayout#getVgap
     * @since      JDK1.1
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout. Not used by this class.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout. Not used by
     * this class.
     * @param comp the component to remove
     * @see       java.awt.Container#removeAll
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the preferred dimensions for this layout given the 
     * <i>visible</i> components in the specified target container.
     * @param target the component which needs to be laid out
     * @return    the preferred dimensions to lay out the
     *            subcomponents of the specified container
     * @see Container
     * @see #minimumLayoutSize
     * @see       java.awt.Container#getPreferredSize
     */
    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
	Dimension dim = new Dimension(0, 0);
	int nmembers = target.getComponentCount();
        boolean firstVisibleComponent = true;

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		Dimension d = m.getPreferredSize();
		dim.height = Math.max(dim.height, d.height);
                if (firstVisibleComponent) {
                    firstVisibleComponent = false;
                } else {
                    dim.width += hgap;
                }
		dim.width += d.width;
	    }
	}
	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right + hgap*2;
	dim.height += insets.top + insets.bottom + vgap*2;
	return dim;
      }
    }

    /**
     * Returns the minimum dimensions needed to layout the <i>visible</i>
     * components contained in the specified target container.
     * @param target the component which needs to be laid out
     * @return    the minimum dimensions to lay out the
     *            subcomponents of the specified container
     * @see #preferredLayoutSize
     * @see       java.awt.Container
     * @see       java.awt.Container#doLayout
     */
    public Dimension minimumLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
	Dimension dim = new Dimension(0, 0);
	int nmembers = target.getComponentCount();

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		Dimension d = m.getMinimumSize();
		dim.height = Math.max(dim.height, d.height);
		if (i > 0) {
		    dim.width += hgap;
		}
		dim.width += d.width;
	    }
	}
	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right + hgap*2;
	dim.height += insets.top + insets.bottom + vgap*2;
	return dim;
      }
    }

    /**
     * Centers the elements in the specified row, if there is any slack.
     * @param target the component which needs to be moved
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimensions
     * @param height the height dimensions
     * @param rowStart the beginning of the row
     * @param rowEnd the the ending of the row
     */
    private void moveComponents(Container target, int x, int y, int width, int height,
                                int rowStart, int rowEnd, boolean ltr) {
      synchronized (target.getTreeLock()) {
	switch (newAlign) {
	case LEFT:
	    x += ltr ? 0 : width;
	    break;
	case CENTER:
	    x += width / 2;
	    break;
	case RIGHT:
	    x += ltr ? width : 0;
	    break;
	case LEADING:
	    break;
	case TRAILING:
	    x += width;
	    break;
	}
	for (int i = rowStart ; i < rowEnd ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
	        if (ltr) {
        	    m.setLocation(x, y + (height - m.height) / 2);
	        } else {
	            m.setLocation(target.width - x - m.width, y + (height - m.height) / 2);
                }
                x += m.width + hgap;
	    }
	}
      }
    }

    /**
     * Lays out the container. This method lets each component take
     * its preferred size by reshaping the components in the
     * target container in order to satisfy the alignment of
     * this <code>FlowLayout</code> object.
     * @param target the specified component being laid out
     * @see Container
     * @see       java.awt.Container#doLayout
     */
    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
	Insets insets = target.getInsets();
	int maxwidth = target.width - (insets.left + insets.right + hgap*2);
	int nmembers = target.getComponentCount();
	int x = 0, y = insets.top + vgap;
	int rowh = 0, start = 0;

        boolean ltr = target.getComponentOrientation().isLeftToRight();

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.visible) {
		Dimension d = m.getPreferredSize();
		m.setSize(d.width, d.height);

		if ((x == 0) || ((x + d.width) <= maxwidth)) {
		    if (x > 0) {
			x += hgap;
		    }
		    x += d.width;
		    rowh = Math.max(rowh, d.height);
		} else {
		    moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, i, ltr);
		    x = d.width;
		    y += vgap + rowh;
		    rowh = d.height;
		    start = i;
		}
	    }
	}
	moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers, ltr);
      }
    }

    //
    // the internal serial version which says which version was written
    // - 0 (default) for versions before the Java 2 platform, v1.2
    // - 1 for version >= Java 2 platform v1.2, which includes "newAlign" field
    //
    private static final int currentSerialVersion = 1;
    /**
     * This represent the <code>currentSerialVersion</code>
     * which is bein used.  It will be one of two values :
     * <code>0</code> versions before Java 2 platform v1.2..
     * <code>1</code> versions after  Java 2 platform v1.2..
     *
     * @serial
     * @since 1.2
     */
    private int serialVersionOnStream = currentSerialVersion;

    /**
     * Reads this object out of a serialization stream, handling
     * objects written by older versions of the class that didn't contain all
     * of the fields we use now..
     */
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();

        if (serialVersionOnStream < 1) {
            // "newAlign" field wasn't present, so use the old "align" field.
            setAlignment(this.align);
        }
        serialVersionOnStream = currentSerialVersion;
    }

    /**
     * Returns a string representation of this <code>FlowLayout</code>
     * object and its values.
     * @return     a string representation of this layout
     */
    public String toString() {
	String str = "";
	switch (align) {
	  case LEFT:        str = ",align=left"; break;
	  case CENTER:      str = ",align=center"; break;
	  case RIGHT:       str = ",align=right"; break;
	  case LEADING:     str = ",align=leading"; break;
	  case TRAILING:    str = ",align=trailing"; break;
	}
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
    }


}
