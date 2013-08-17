/*
 * @(#)CellRendererPane.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Vector;

import javax.accessibility.*;

/** 
 * This class is inserted in between cell renderers and the components that 
 * use them.  It just exists to thwart the repaint() and invalidate() methods 
 * which would otherwise propogate up the tree when the renderer was configured.
 * It's used by the implementations of JTable, JTree, and JList.  For example,
 * here's how CellRendererPane is used in the code the paints each row
 * in a JList:
 * <pre>
 *   cellRendererPane = new CellRendererPane();
 *   ...
 *   Component rendererComponent = renderer.getListCellRendererComponent();
 *   renderer.configureListCellRenderer(dataModel.getElementAt(row), row);
 *   cellRendererPane.paintComponent(g, rendererComponent, this, x, y, w, h);
 * </pre>
 * <p>
 * A renderer component must override isShowing() and unconditionally return
 * true to work correctly because the Swing paint does nothing for components
 * with isShowing false.  
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.28 09/01/98
 * @author Hans Muller
 */
public class CellRendererPane extends Container implements Accessible
{
    /**
     * Construct a CellRendererPane object.
     */
    public CellRendererPane() {
	super();
	setLayout(null);
	setVisible(false);
    }

    /** 
     * Overridden to avoid propogating a invalidate up the tree when the
     * cell renderer child is configured.
     */
    public void invalidate() { }


    /** 
     * Shouldn't be called.
     */
    public void paint(Graphics g) { }

  
    /**
     * Shouldn't be called.
     */
    public void update(Graphics g) { }


    /** 
     * If the specified component is already a child of this then we don't
     * bother doing anything - stacking order doesn't matter for cell
     * renderer components (CellRendererPane doesn't paint anyway).<
     */
    protected void addImpl(Component x, Object constraints, int index) {
	if (x.getParent() == this) {
	    return;
	}
	else {
	    super.addImpl(x, constraints, index);
	}
    }


    /** 
     * Paint a cell renderer component c on graphics object g.  Before the component
     * is drawn it's reparented to this (if that's neccessary), it's bounds 
     * are set to w,h and the graphics object is (effectively) translated to x,y.  
     * If it's a JComponent, double buffering is temporarily turned off. After 
     * the component is painted it's bounds are reset to -w, -h, 0, 0 so that, if 
     * it's the last renderer component painted, it will not start consuming input.  
     * The Container p is the component we're actually drawing on, typically it's 
     * equal to this.getParent(). If shouldValidate is true the component c will be 
     * validated before painted.
     */
    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h, boolean shouldValidate) {
	if (c == null) {
	    if (p != null) {
		Color oldColor = g.getColor();
		g.setColor(p.getBackground());
		g.fillRect(x, y, w, h);
		g.setColor(oldColor);
	    }
	    return;
	}

	if (c.getParent() != this) {
	    this.add(c);
	}

	c.setBounds(x, y, w, h);

	if(shouldValidate) {
	    c.validate();
	}

	boolean wasDoubleBuffered = false;
	if ((c instanceof JComponent) && ((JComponent)c).isDoubleBuffered()) {
	    wasDoubleBuffered = true;
	    ((JComponent)c).setDoubleBuffered(false);
	}

	Graphics cg = SwingGraphics.createSwingGraphics(g, x, y, w, h);
	try {
	    c.paint(cg);
	}
	finally {
	    cg.dispose();
	}

	if ((c instanceof JComponent) && wasDoubleBuffered) {
	    ((JComponent)c).setDoubleBuffered(true);
	}

	if (c instanceof JComponent) {
	    JComponent jc = (JComponent)c;
	    jc.setDoubleBuffered(wasDoubleBuffered);
	}

	c.setBounds(-w, -h, 0, 0);
    }


    /**
     * Calls this.paintComponent(g, c, p, x, y, w, h, false).
     */
    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h) {
	paintComponent(g, c, p, x, y, w, h, false);
    }


    /**
     * Calls this.paintComponent() with the rectangles x,y,width,height fields.
     */
    public void paintComponent(Graphics g, Component c, Container p, Rectangle r) {
	paintComponent(g, c, p, r.x, r.y, r.width, r.height);
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
	removeAll();
	s.defaultWriteObject();
    }


/////////////////
// Accessibility support
////////////////

    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this CellRendererPane
     *
     * @return the AccessibleContext of this CellRendererPane
     */
    public AccessibleContext getAccessibleContext() {
	if (accessibleContext == null) {
	    accessibleContext = new AccessibleCellRendererPane();
	}
	return accessibleContext;
    }


    protected class AccessibleCellRendererPane extends AccessibleContext
	implements Serializable, AccessibleComponent 
    {

        // AccessibleContext methods
        //
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
	 * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
	    return AccessibleRole.PANEL;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
	 * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
	    return SwingUtilities.getAccessibleStateSet(CellRendererPane.this);
        }

        /**
         * Get the Accessible parent of this object.  If the parent of this
         * object implements Accessible, this method should simply return
         * getParent().
         *
         * @return the Accessible parent of this object -- can be null if this
         * object does not have an Accessible parent
         */
        public Accessible getAccessibleParent() {
	    if (accessibleParent != null) {
		return accessibleParent;
	    } else {
            	Container parent = getParent();
            	if (parent instanceof Accessible) {
                    return (Accessible) parent;
		}
            }
            return null;
        }

        /**
         * Get the index of this object in its accessible parent. 
         *
         * @return the index of this object in its parent; -1 if this 
         * object does not have an accessible parent.
         * @see #getAccessibleParent
         */
        public int getAccessibleIndexInParent() {
	    return SwingUtilities.getAccessibleIndexInParent(CellRendererPane.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
	    return SwingUtilities.getAccessibleChildrenCount(CellRendererPane.this);
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
    	    return SwingUtilities.getAccessibleChild(CellRendererPane.this,i);
        }

        /**
         * Return the locale of this object.
	 *
         * @return the locale of this object
         */
        public Locale getLocale() {
    	    return CellRendererPane.this.getLocale();
        }

        /**
         * Get the AccessibleComponent associated with this object if one
         * exists.  Otherwise return null.
         */
	public AccessibleComponent getAccessibleComponent() {
	    return this;
	}


        // AccessibleComponent methods
        //
        /**
         * Get the background color of this object.
         *
         * @return the background color, if supported, of the object; 
         * otherwise, null
         */
        public Color getBackground() {
	    return CellRendererPane.this.getBackground();
	}

        /**
         * Set the background color of this object.
         *
         * @param c the new Color for the background
         */
        public void setBackground(Color c) {
	    CellRendererPane.this.setBackground(c);
	}

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object; 
         * otherwise, null
         */
        public Color getForeground() {
	    return CellRendererPane.this.getForeground();
	}

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
	    CellRendererPane.this.setForeground(c);
	}

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
	    return CellRendererPane.this.getCursor();
	}

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
	    CellRendererPane.this.setCursor(cursor);
	}

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
	    return CellRendererPane.this.getFont();
	}

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
	    CellRendererPane.this.setFont(f);
	}

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see #getFont
         */
        public FontMetrics getFontMetrics(Font f) {
	    return CellRendererPane.this.getFontMetrics(f);
	}

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
	    return CellRendererPane.this.isEnabled();
	}

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it 
         */
        public void setEnabled(boolean b) {
	    CellRendererPane.this.setEnabled(b);
	}
	
        /**
         * Determine if the object is visible.  Note: this means that the
         * object intends to be visible; however, it may not in fact be
         * showing on the screen because one of the objects that this object
         * is contained by is not visible.  To determine if an object is
         * showing on the screen, use isShowing().
         *
         * @return true if object is visible; otherwise, false
         */
        public boolean isVisible() {
	    return CellRendererPane.this.isVisible();
	}

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it 
         */
        public void setVisible(boolean b) {
	    CellRendererPane.this.setVisible(b);
	}

        /**
         * Determine if the object is showing.  This is determined by checking
         * the visibility of the object and ancestors of the object.  Note: 
	 * this will return true even if the object is obscured by another 
	 * (for example, it happens to be underneath a menu that was pulled 
	 * down).
         *
         * @return true if object is showing; otherwise, false
         */
        public boolean isShowing() {
	    return CellRendererPane.this.isShowing();
	}

        /** 
         * Checks whether the specified point is within this object's bounds,
         * where the point's x and y coordinates are defined to be relative to 
	 * the coordinate system of the object. 
         *
         * @param p the Point relative to the coordinate system of the object
         * @return true if object contains Point; otherwise false
         */
        public boolean contains(Point p) {
	    return CellRendererPane.this.contains(p);
	}
    
        /** 
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
	    return CellRendererPane.this.getLocationOnScreen();
	}

        /** 
         * Gets the location of the object relative to the parent in the form 
         * of a point specifying the object's top-left corner in the screen's 
         * coordinate space.
         *
         * @return An instance of Point representing the top-left corner of 
	 * the objects's bounds in the coordinate space of the screen; null if
         * this object or its parent are not on the screen
         */
	public Point getLocation() {
	    return CellRendererPane.this.getLocation();
	}

        /** 
         * Sets the location of the object relative to the parent.
         */
        public void setLocation(Point p) {
	    CellRendererPane.this.setLocation(p);
	}

        /** 
         * Gets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent. 
         *
         * @return A rectangle indicating this component's bounds; null if 
	 * this object is not on the screen.
         */
        public Rectangle getBounds() {
	    return CellRendererPane.this.getBounds();
	}

        /** 
         * Sets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *	
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
	    CellRendererPane.this.setBounds(r);
	}

        /** 
         * Returns the size of this object in the form of a Dimension object. 
         * The height field of the Dimension object contains this objects's
         * height, and the width field of the Dimension object contains this 
	 * object's width. 
         *
         * @return A Dimension object that indicates the size of this 
	 * component; null if this object is not on the screen
         */
        public Dimension getSize() {
	    return CellRendererPane.this.getSize();
	}

        /** 
         * Resizes this object so that it has width width and height. 
         *	
         * @param d - The dimension specifying the new size of the object. 
         */
        public void setSize(Dimension d) {
	    CellRendererPane.this.setSize(d);
	}

        /**
         * Returns the Accessible child, if one exists, contained at the local
	 * coordinate Point.
         *
         * @param p The point defining the top-left corner of the Accessible, 
	 * given in the coordinate space of the object's parent. 
         * @return the Accessible, if it exists, at the specified location; 
	 * else null
         */
        public Accessible getAccessibleAt(Point p) {
	    return SwingUtilities.getAccessibleAt(CellRendererPane.this,p);
	}

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
	    return CellRendererPane.this.isFocusTraversable();
	}

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
	    CellRendererPane.this.requestFocus();
    	}

        /**
         * Adds the specified focus listener to receive focus events from this 
         * component. 
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
	    CellRendererPane.this.addFocusListener(l);
	}

        /**
         * Removes the specified focus listener so it no longer receives focus 
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
	    CellRendererPane.this.removeFocusListener(l);
	}
    } // inner class AccessibleCellRendererPane
}


