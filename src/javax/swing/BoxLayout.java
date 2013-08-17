/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

package javax.swing;

import java.awt.*;
import java.io.Serializable;
import java.io.PrintStream;

/**
 * A layout manager that allows multiple components to be layed out either
 * vertically or horizontally. The components will not wrap so, for 
 * example, a vertical arrangement of components will stay vertically 
 * arranged when the frame is resized. 
 * <TABLE ALIGN="RIGHT" BORDER="0">
 *    <TR>
 *      <TD ALIGN="CENTER">
 *         <P ALIGN="CENTER"><IMG SRC="doc-files/BoxLayout-1.gif" WIDTH="191" HEIGHT="201" ALIGN="BOTTOM" BORDER="0">
 *      </TD>
 *    </TR>
 * </TABLE>
 * <p>
 * Nesting multiple panels with different combinations of horizontal and 
 * vertical gives an effect similar to GridBagLayout, without the 
 * complexity. The diagram shows two panels arranged horizontally, each 
 * of which contains 3 components arranged vertically.
 * <p>
 * The Box container uses BoxLayout (unlike JPanel, which defaults to flow
 * layout). You can nest multiple boxes and add components to them to get
 * the arrangement you want.
 * <p>
 * The BoxLayout manager that places each of its managed components
 * from left to right or from top to bottom.
 * When you create a BoxLayout, you specify whether its major axis is 
 * the X axis (which means left to right placement) or Y axis (top to 
 * bottom placement). Components are arranged from left to right (or 
 * top to bottom), in the same order as they were added to the container.
 * <p>
 * Instead of using BoxLayout directly, many programs use the Box class.
 * The Box class provides a lightweight container that uses a BoxLayout.
 * Box also provides handy methods to help you use BoxLayout well.
 * <p>
 * BoxLayout attempts to arrange components
 * at their preferred widths (for left to right layout)
 * or heights (for top to bottom layout).
 * For a left to right layout,
 * if not all the components are the same height,
 * BoxLayout attempts to make all the components 
 * as high as the highest component.
 * If that's not possible for a particular component, 
 * then BoxLayout aligns that component vertically,
 * according to the component's Y alignment.
 * By default, a component has an Y alignment of 0.5,
 * which means that the vertical center of the component
 * should have the same Y coordinate as 
 * the vertical centers of other components with 0.5 Y alignment.
 * <p>
 * Similarly, for a vertical layout,
 * BoxLayout attempts to make all components in the column
 * as wide as the widest component;
 * if that fails, it aligns them horizontally
 * according to their X alignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see Box
 * @see Component#getAlignmentX
 * @see Component#getAlignmentY
 *
 * @author   Timothy Prinzing
 * @version  1.26 02/06/02
 */
public class BoxLayout implements LayoutManager2, Serializable {

    /**
     * Specifies that components should be laid out left to right.
     */
    public static final int X_AXIS = 0;
    
    /**
     * Specifies that components should be laid out top to bottom.
     */
    public static final int Y_AXIS = 1;

    /**
     * Creates a layout manager that will lay out components either 
     * left to right or
     * top to bottom,
     * as specified in the <code>axis</code> parameter.  
     *
     * @param target  the container that needs to be laid out
     * @param axis  the axis to lay out components along.
     *              For left-to-right layout, 
     *              specify <code>BoxLayout.X_AXIS</code>;
     *              for top-to-bottom layout, 
     *              specify <code>BoxLayout.Y_AXIS</code>
     *
     * @exception AWTError  if the value of <code>axis</code> is invalid 
     */
    public BoxLayout(Container target, int axis) {
        if (axis != X_AXIS && axis != Y_AXIS) {
            throw new AWTError("Invalid axis");
        }
        this.axis = axis;
        this.target = target;
    }

    /**
     * Constructs a BoxLayout that 
     * produces debugging messages.
     *
     * @param target  the container that needs to be laid out
     * @param axis  the axis to lay out components along; can be either
     *              <code>BoxLayout.X_AXIS</code>
     *              or <code>BoxLayout.Y_AXIS</code>
     * @param dbg  the stream to which debugging messages should be sent,
     *   null if none
     */
    BoxLayout(Container target, int axis, PrintStream dbg) {
        this(target, axis);
        this.dbg = dbg;
    }

    /**
     * Indicates that a child has changed its layout related information,
     * and thus any cached calculations should be flushed.
     * <p>
     * This method is called by AWT when the invalidate method is called
     * on the Container.  Since the invalidate method may be called 
     * asynchronously to the event thread, this method may be called
     * asynchronously.
     *
     * @param target  the affected container
     *
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     */
    public synchronized void invalidateLayout(Container target) {
        checkContainer(target);
        xChildren = null;
        yChildren = null;
        xTotal = null;
        yTotal = null;
    }

    /**
     * Not used by this class.
     *
     * @param name the name of the component
     * @param comp the component
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Not used by this class.
     *
     * @param comp the component
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Not used by this class.
     *
     * @param comp the component
     * @param constraints constraints
     */
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    /**
     * Returns the preferred dimensions for this layout, given the components
     * in the specified target container.
     *
     * @param target  the container that needs to be laid out
     * @return the dimensions >= 0 && <= Integer.MAX_VALUE
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     * @see Container
     * @see #minimumLayoutSize
     * @see #maximumLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
	Dimension size;
	synchronized(this) {
	    checkContainer(target);
	    checkRequests();
	    size = new Dimension(xTotal.preferred, yTotal.preferred);
	}

        Insets insets = target.getInsets();
        size.width = (int) Math.min((long) size.width + (long) insets.left + (long) insets.right, Integer.MAX_VALUE);
        size.height = (int) Math.min((long) size.height + (long) insets.top + (long) insets.bottom, Integer.MAX_VALUE);
        return size;
    }

    /**
     * Returns the minimum dimensions needed to lay out the components
     * contained in the specified target container.
     *
     * @param target  the container that needs to be laid out 
     * @return the dimensions >= 0 && <= Integer.MAX_VALUE
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     * @see #preferredLayoutSize
     * @see #maximumLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
	Dimension size;
	synchronized(this) {
	    checkContainer(target);
	    checkRequests();
	    size = new Dimension(xTotal.minimum, yTotal.minimum);
	}

        Insets insets = target.getInsets();
        size.width = (int) Math.min((long) size.width + (long) insets.left + (long) insets.right, Integer.MAX_VALUE);
        size.height = (int) Math.min((long) size.height + (long) insets.top + (long) insets.bottom, Integer.MAX_VALUE);
        return size;
    }

    /**
     * Returns the maximum dimensions the target container can use
     * to lay out the components it contains.
     *
     * @param target  the container that needs to be laid out 
     * @return the dimenions >= 0 && <= Integer.MAX_VALUE
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     * @see #preferredLayoutSize
     * @see #minimumLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
	Dimension size;
	synchronized(this) {
	    checkContainer(target);
	    checkRequests();
	    size = new Dimension(xTotal.maximum, yTotal.maximum);
	}

        Insets insets = target.getInsets();
        size.width = (int) Math.min((long) size.width + (long) insets.left + (long) insets.right, Integer.MAX_VALUE);
        size.height = (int) Math.min((long) size.height + (long) insets.top + (long) insets.bottom, Integer.MAX_VALUE);
        return size;
    }

    /**
     * Returns the alignment along the X axis for the container.
     * If the box is horizontal, the default
     * alignment will be returned. Otherwise, the alignment needed
     * to place the children along the X axis will be returned.
     *
     * @param target  the container
     * @return the alignment >= 0.0f && <= 1.0f
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     */
    public synchronized float getLayoutAlignmentX(Container target) {
        checkContainer(target);
        checkRequests();
        return xTotal.alignment;
    }

    /**
     * Returns the alignment along the Y axis for the container.
     * If the box is vertical, the default
     * alignment will be returned. Otherwise, the alignment needed
     * to place the children along the Y axis will be returned.
     *
     * @param target  the container
     * @return the alignment >= 0.0f && <= 1.0f
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     */
    public synchronized float getLayoutAlignmentY(Container target) {
        checkContainer(target);
        checkRequests();
        return yTotal.alignment;
    }

    /**
     * Called by the AWT <!-- XXX CHECK! --> when the specified container
     * needs to be laid out.
     *
     * @param target  the container to lay out
     *
     * @exception AWTError  if the target isn't the container specified to the
     *                      BoxLayout constructor
     */
    public void layoutContainer(Container target) {
	checkContainer(target);
	int nChildren = target.getComponentCount();
	int[] xOffsets = new int[nChildren];
	int[] xSpans = new int[nChildren];
	int[] yOffsets = new int[nChildren];
	int[] ySpans = new int[nChildren];
	    
	Dimension alloc = target.getSize();
	Insets in = target.getInsets();
	alloc.width -= in.left + in.right;
	alloc.height -= in.top + in.bottom;

	// determine the child placements
	synchronized(this) {
	    checkRequests();
        
	    if (axis == X_AXIS) {
		SizeRequirements.calculateTiledPositions(alloc.width, xTotal,
							 xChildren, xOffsets,
							 xSpans);
		SizeRequirements.calculateAlignedPositions(alloc.height, yTotal,
							   yChildren, yOffsets,
							   ySpans);
	    } else {
		SizeRequirements.calculateAlignedPositions(alloc.width, xTotal,
							   xChildren, xOffsets,
							   xSpans);
		SizeRequirements.calculateTiledPositions(alloc.height, yTotal,
							 yChildren, yOffsets,
							 ySpans);
	    }
	}

        // flush changes to the container
        for (int i = 0; i < nChildren; i++) {
            Component c = target.getComponent(i);
            c.setBounds((int) Math.min((long) in.left + (long) xOffsets[i], Integer.MAX_VALUE),
                        (int) Math.min((long) in.top + (long) yOffsets[i], Integer.MAX_VALUE),
                        xSpans[i], ySpans[i]);

        }
        if (dbg != null) {
            for (int i = 0; i < nChildren; i++) {
                Component c = target.getComponent(i);
                dbg.println(c.toString());
                dbg.println("X: " + xChildren[i]);
                dbg.println("Y: " + yChildren[i]);
            }
        }
            
    }

    void checkContainer(Container target) {
        if (this.target != target) {
            throw new AWTError("BoxLayout can't be shared");
        }
    }
    
    void checkRequests() {
        if (xChildren == null || yChildren == null) {
            // The requests have been invalidated... recalculate
            // the request information.
            int n = target.getComponentCount();
            xChildren = new SizeRequirements[n];
            yChildren = new SizeRequirements[n];
            for (int i = 0; i < n; i++) {
                Component c = target.getComponent(i);
		if (!c.isVisible()) {
		    xChildren[i] = new SizeRequirements(0,0,0, c.getAlignmentX());
		    yChildren[i] = new SizeRequirements(0,0,0, c.getAlignmentY());
		    continue;
		}
                Dimension min = c.getMinimumSize();
                Dimension typ = c.getPreferredSize();
                Dimension max = c.getMaximumSize();
                xChildren[i] = new SizeRequirements(min.width, typ.width, 
                                                    max.width, 
                                                    c.getAlignmentX());
                yChildren[i] = new SizeRequirements(min.height, typ.height, 
                                                    max.height, 
                                                    c.getAlignmentY());
            }
            
            if (axis == X_AXIS) {
                xTotal = SizeRequirements.getTiledSizeRequirements(xChildren);
                yTotal = SizeRequirements.getAlignedSizeRequirements(yChildren);
            } else {
                xTotal = SizeRequirements.getAlignedSizeRequirements(xChildren);
                yTotal = SizeRequirements.getTiledSizeRequirements(yChildren);
            }
        }
    }
            
    private int axis;
    private Container target;

    private transient SizeRequirements[] xChildren;
    private transient SizeRequirements[] yChildren;
    private transient SizeRequirements xTotal;
    private transient SizeRequirements yTotal;
    
    private transient PrintStream dbg;
}

