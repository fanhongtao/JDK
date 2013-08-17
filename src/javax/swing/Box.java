/*
 * @(#)Box.java	1.29 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.io.Serializable;
import javax.accessibility.*;

/**
 * A lightweight container 
 * that uses a BoxLayout object as its layout manager.
 * Box provides several class methods
 * that are useful for containers using BoxLayout --
 * even non-Box containers.
 *
 * <p>
 *
 * The Box class can create several kinds
 * of invisible components 
 * that affect layout:
 * glue, struts, and rigid areas.
 * If all the components your Box contains 
 * have a fixed size,
 * you might want to use a glue component
 * (returned by <code>createGlue</code>)
 * to control the components' positions.
 * If you need a fixed amount of space between two components,
 * try using a strut
 * (<code>createHorizontalStrut</code> or <code>createVerticalStrut</code>).
 * If you need an invisible component
 * that always takes up the same amount of space,
 * get it by invoking <code>createRigidArea</code>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see BoxLayout
 *
 * @author  Timothy Prinzing
 * @version 1.29 08/28/98
 */
public class Box extends Container implements Accessible {

    /**
     * Creates a <code>Box</code> that displays its components
     * along the the specified axis.
     *
     * @param axis  can be either <code>BoxLayout.X_AXIS</code>
     *              (to display components from left to right) or
     *              <code>BoxLayout.Y_AXIS</code>
     *              (to display them from top to bottom)
     * @see #createHorizontalBox
     * @see #createVerticalBox
     */
    public Box(int axis) {
	super();
	super.setLayout(new BoxLayout(this, axis));
    }

    /**
     * Creates a <code>Box</code> that displays its components
     * from left to right.
     *
     * @return the box
     */
    public static Box createHorizontalBox() {
	return new Box(BoxLayout.X_AXIS);
    }

    /**
     * Creates a <code>Box</code> that displays its components
     * from top to bottom.
     *
     * @return the box
     */
    public static Box createVerticalBox() {
	return new Box(BoxLayout.Y_AXIS);
    }

    /**
     * Creates an invisible component that's always the specified size.
     * <!-- WHEN WOULD YOU USE THIS AS OPPOSED TO A STRUT? -->
     *
     * @param d the dimensions of the invisible component
     * @return the component
     * @see #createGlue
     * @see #createHorizontalStrut
     * @see #createVerticalStrut
     */
    public static Component createRigidArea(Dimension d) {
	return new Filler(d, d, d);
    }

    /**
     * Creates an invisible, fixed-width component.
     * In a horizontal box, 
     * you typically use this method 
     * to force a certain amount of space between two components.
     * In a vertical box,
     * you might use this method 
     * to force the box to be at least the specified width.
     * The invisible component has no height
     * unless excess space is available,
     * in which case it takes its share of available space,
     * just like any other component that has no maximum height.
     *
     * @param width the width of the invisible component, in pixels >= 0
     * @return the component
     * @see #createVerticalStrut
     * @see #createGlue
     * @see #createRigidArea
     */
    public static Component createHorizontalStrut(int width) {
	// PENDING(jeff) change to Integer.MAX_VALUE. This hasn't been done
	// to date because BoxLayout alignment breaks.
	return new Filler(new Dimension(width,0), new Dimension(width,0), 
			  new Dimension(width, Short.MAX_VALUE));
    }

    /**
     * Creates an invisible, fixed-height component.
     * In a vertical box, 
     * you typically use this method
     * to force a certain amount of space between two components.
     * In a horizontal box,
     * you might use this method 
     * to force the box to be at least the specified height.
     * The invisible component has no width
     * unless excess space is available,
     * in which case it takes its share of available space,
     * just like any other component that has no maximum width.
     *
     * @param height the height of the invisible component, in pixels >= 0
     * @return the component
     * @see #createHorizontalStrut
     * @see #createGlue
     * @see #createRigidArea
     */
    public static Component createVerticalStrut(int height) {
	// PENDING(jeff) change to Integer.MAX_VALUE. This hasn't been done
	// to date because BoxLayout alignment breaks.
	return new Filler(new Dimension(0,height), new Dimension(0,height), 
			  new Dimension(Short.MAX_VALUE, height));
    }

    /**
     * Creates an invisible "glue" component 
     * that can be useful in a Box
     * whose visible components have a maximum width
     * (for a horizontal box)
     * or height (for a vertical box).
     * You can think of the glue component
     * as being a gooey substance
     * that expands as much as necessary
     * to fill the space between its neighboring components.
     *
     * <p>
     *
     * For example, suppose you have
     * a horizontal box that contains two fixed-size components.
     * If the box gets extra space,
     * the fixed-size components won't become larger,
     * so where does the extra space go?
     * Without glue,
     * the extra space goes to the right of the second component.
     * If you put glue between the fixed-size components,
     * then the extra space goes there.
     * If you put glue before the first fixed-size component,
     * the extra space goes there,
     * and the fixed-size components are shoved against the right
     * edge of the box.
     * If you put glue before the first fixed-size component
     * and after the second fixed-size component,
     * the fixed-size components are centered in the box.
     *
     * <p>
     *
     * To use glue,
     * call <code>Box.createGlue</code>
     * and add the returned component to a container.
     * The glue component has no minimum or preferred size,
     * so it takes no space unless excess space is available.
     * If excess space is available, 
     * then the glue component takes its share of available
     * horizontal or vertical space,
     * just like any other component that has no maximum width or height.
     *
     * @return the component
     */
    public static Component createGlue() {
	// PENDING(jeff) change to Integer.MAX_VALUE. This hasn't been done
	// to date because BoxLayout alignment breaks.
	return new Filler(new Dimension(0,0), new Dimension(0,0), 
			  new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }

    /**
     * Creates a horizontal glue component.
     *
     * @return the component
     */
    public static Component createHorizontalGlue() {
	// PENDING(jeff) change to Integer.MAX_VALUE. This hasn't been done
	// to date because BoxLayout alignment breaks.
	return new Filler(new Dimension(0,0), new Dimension(0,0), 
			  new Dimension(Short.MAX_VALUE, 0));
    }

    /**
     * Creates a vertical glue component.
     *
     * @return the component
     */
    public static Component createVerticalGlue() {
	// PENDING(jeff) change to Integer.MAX_VALUE. This hasn't been done
	// to date because BoxLayout alignment breaks.
	return new Filler(new Dimension(0,0), new Dimension(0,0), 
			  new Dimension(0, Short.MAX_VALUE));
    }

    /**
     * Throws an AWTError, since a Box can use only a BoxLayout.
     *
     * @param l the layout manager to use
     */
    public void setLayout(LayoutManager l) {
	throw new AWTError("Illegal request");
    }


    /**
     * An implementation of a lightweight component that participates in
     * layout but has no view.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class Filler extends Component implements Accessible {

	/**
	 * Constructor to create shape with the given size ranges.
	 *
	 * @param min   Minimum size
	 * @param pref  Preferred size
	 * @param max   Maximum size
	 */
        public Filler(Dimension min, Dimension pref, Dimension max) {
	    reqMin = min;
	    reqPref = pref;
	    reqMax = max;
	}

	/**
	 * Change the size requests for this shape.  An invalidate() is
	 * propagated upward as a result so that layout will eventually
	 * happen with using the new sizes.
	 *
	 * @param min   Value to return for getMinimumSize
	 * @param pref  Value to return for getPreferredSize
	 * @param max   Value to return for getMaximumSize
	 */
        public void changeShape(Dimension min, Dimension pref, Dimension max) {
	    reqMin = min;
	    reqPref = pref;
	    reqMax = max;
	    invalidate();
	}

	// ---- Component methods ------------------------------------------

        /**
         * Returns the minimum size of the component.
         *
         * @return the size
         */
        public Dimension getMinimumSize() {
	    return reqMin;
	}

        /**
         * Returns the preferred size of the component.
         *
         * @return the size
         */
        public Dimension getPreferredSize() {
	    return reqPref;
	}

        /**
         * Returns the maximum size of the component.
         *
         * @return the size
         */
        public Dimension getMaximumSize() {
	    return reqMax;
	}

	// ---- member variables ---------------------------------------

        private Dimension reqMin;
        private Dimension reqPref;
        private Dimension reqMax;

/////////////////
// Accessibility support for Box$Filler
////////////////

        /**
         * The currently set AccessibleContext object.
         */
        protected AccessibleContext accessibleContext = null;

        /**
         * Gets the AccessibleContext associated with this Component.
         * Creates a new context if necessary.
         *
         * @return the AccessibleContext of this Component
         */
        public AccessibleContext getAccessibleContext() {
	    if (accessibleContext == null) {
		accessibleContext = new AccessibleBoxFiller();
	    }
	    return accessibleContext;
        }

	protected class AccessibleBoxFiller extends AccessibleContext
	    implements Serializable, AccessibleComponent {

            // AccessibleContext methods
            //
            /**
             * Gets the role of this object.
             *
             * @return an instance of AccessibleRole describing the role of
             *   the object (AccessibleRole.FILLER)
             * @see AccessibleRole
             */
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.FILLER;
            }

            /**
             * Gets the state of this object.
             *
             * @return an instance of AccessibleStateSet containing the current 
             *   state set of the object
             * @see AccessibleState
             */
            public AccessibleStateSet getAccessibleStateSet() {
                return SwingUtilities.getAccessibleStateSet(Filler.this);
            }
    
            /**
             * Get the Accessible parent of this object.  If the parent of this
             * object implements Accessible, this method should simply return
             * getParent().
             *
             * @return the Accessible parent of this object; null if this
             *   object does not have an Accessible parent
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
             * Gets the index of this object in its accessible parent. 
             *
             * @return the index of this object in its parent >= 0; -1 if this 
             *   object does not have an accessible parent.
             * @see #getAccessibleParent
             */
            public int getAccessibleIndexInParent() {
                return SwingUtilities.getAccessibleIndexInParent(Filler.this);
            }
    
            /**
             * Returns the number of accessible children in the object.  If all
             * of the children of this object implement Accessible, then this
             * method should return the number of children of this object.
             *
             * @return the number of accessible children in the object >= 0
             */
            public int getAccessibleChildrenCount() {
                return SwingUtilities.getAccessibleChildrenCount(Filler.this);
            }
    
            /**
             * Returns the nth Accessible child of the object.  
             *
             * @param i zero-based index of child
             * @return the nth Accessible child of the object, null if none
             */
            public Accessible getAccessibleChild(int i) {
                return SwingUtilities.getAccessibleChild(Filler.this,i);
            }
        
            /**
             * Returns the locale of this object.
             *
             * @return the locale of this object
             */
            public Locale getLocale() {
                return Filler.this.getLocale();
            }
    
            /**
             * Gets the AccessibleComponent associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the component
             */
	    public AccessibleComponent getAccessibleComponent() {
		return this;
	    }
    
    
            // AccessibleComponent methods
            //
            /**
             * Gets the background color of this object.
             *
             * @return the background color, if supported, of the object; 
             * otherwise, null
             */
            public Color getBackground() {
                return Filler.this.getBackground();
            }
    
	    // NOTE: IN THE NEXT MAJOR RELEASE, isOpaque WILL MIGRATE
	    //       TO java.awt.Component -- ADJUST @SEE LINK BELOW.
            /**
             * Sets the background color of this object.
	     * (For transparency, see <code>isOpaque</code>.)
             *
             * @param c the new Color for the background, null if none
	     * @s JComponent#isOpaque
             */
            public void setBackground(Color c) {
                Filler.this.setBackground(c);
            }
    
            /**
             * Gets the foreground color of this object.
             *
             * @return the foreground color, if supported, of the object; 
             * otherwise, null
             */
            public Color getForeground() {
                return Filler.this.getForeground();
            }
    
            /**
             * Sets the foreground color of this object.
             *
             * @param c the new Color for the foreground, null if none
             */
            public void setForeground(Color c) {
                Filler.this.setForeground(c);
            }
    
            /**
             * Gets the Cursor of this object.
             *
             * @return the Cursor, if supported, of the object; otherwise, null
             */
            public Cursor getCursor() {
                return Filler.this.getCursor();
            }
    
            /**
             * Set the Cursor of this object.
             *
             * @param cursor the new Cursor for the object, null if none
             */
            public void setCursor(Cursor cursor) {
                Filler.this.setCursor(cursor);
            }
    
            /**
             * Gets the Font of this object.
             *
             * @return the Font,if supported, for the object; otherwise, null
             */
            public Font getFont() {
                return Filler.this.getFont();
            }
    
            /**
             * Sets the Font of this object.
             *
             * @param f the new Font for the object, null if none
             */
            public void setFont(Font f) {
                Filler.this.setFont(f);
            }
    
            /**
             * Gets the FontMetrics of this object.
             *
             * @param f the Font, null if none
             * @return the FontMetrics, if supported, the object;
             *   otherwise, null
             * @see getFont
             */
            public FontMetrics getFontMetrics(Font f) {
                return Filler.this.getFontMetrics(f);
            }
    
            /**
             * Determines if the object is enabled.
             *
             * @return true if object is enabled; otherwise, false
             */
            public boolean isEnabled() {
                return Filler.this.isEnabled();
            }
    
            /**
             * Sets the enabled state of the object.
             *
             * @param b if true, enables this object; otherwise, disables it 
             */
            public void setEnabled(boolean b) {
		boolean old = Filler.this.isEnabled();
		Filler.this.setEnabled(b);
		if (b != old) {
		    if (accessibleContext != null) {
			if (b) {
			    accessibleContext.firePropertyChange(
				    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
				    null, AccessibleState.ENABLED);
			} else {
			    accessibleContext.firePropertyChange(
				    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
				    AccessibleState.ENABLED, null);
			}
		    }
		}
            }
            
            /**
             * Determines if the object is visible.  Note: this means that the
             * object intends to be visible; however, it may not in fact be
             * showing on the screen because one of the objects that this object
             * is contained by is not visible.  To determine if an object is
             * showing on the screen, use isShowing().
             *
             * @return true if object is visible; otherwise, false
             */
            public boolean isVisible() {
                return Filler.this.isVisible();
            }
    
            /**
             * Sets the visible state of the object.
             *
             * @param b if true, shows this object; otherwise, hides it 
             */
            public void setVisible(boolean b) {
		boolean old = Filler.this.isVisible();
		Filler.this.setVisible(b);
		if (b != old) {
		    if (accessibleContext != null) {
			if (b) {
			    accessibleContext.firePropertyChange(
				    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
				    null, AccessibleState.VISIBLE);
			} else {
			    accessibleContext.firePropertyChange(
                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                AccessibleState.VISIBLE, null);
			}
		    }
		}
            }
    
            /**
             * Determines if the object is showing.  This is determined by
             * checking the visibility of the object and ancestors of the
             * object.  Note: this will return true even if the object is
             * obscured by another (for example, it happens to be
             * underneath a menu that was pulled down).
             *
             * @return true if object is showing; otherwise, false
             */
            public boolean isShowing() {
                return Filler.this.isShowing();
            }
    
            /** 
             * Checks whether the specified point is within this object's
             * bounds, where the point's x and y coordinates are defined to
             * be relative to the coordinate system of the object. 
             *
             * @param p the Point relative to the coordinate system of
             *   the object
             * @return true if object contains Point; otherwise false
             */
            public boolean contains(Point p) {
                return Filler.this.contains(p);
            }
        
            /** 
             * Returns the location of the object on the screen.
             *
             * @return location of object on screen; can be null if this object
             * is not on the screen
             */
            public Point getLocationOnScreen() {
		if (Filler.this.isShowing()) {
		    return Filler.this.getLocationOnScreen();
		} else {
		    return null;
		}
            }
    
            /** 
             * Gets the location of the object relative to the parent in the
             * form of a point specifying the object's top-left corner in
             * the screen's coordinate space.
             *
             * @return An instance of Point representing the top-left corner of 
             * the objects's bounds in the coordinate space of the screen;
             * null if this object or its parent are not on the screen
             */
            public Point getLocation() {
                return Filler.this.getLocation();
            }
    
            /** 
             * Sets the location of the object relative to the parent.
             *
             * @param p the location to be set
             */
            public void setLocation(Point p) {
                Filler.this.setLocation(p);
            }
    
            /** 
             * Gets the bounds of this object in the form of a Rectangle
             * object.  The bounds specify this object's width, height,
             * and location relative to its parent. 
             *
             * @return A rectangle indicating this component's bounds; null if 
             * this object is not on the screen.
             */
            public Rectangle getBounds() {
                return Filler.this.getBounds();
            }
    
            /** 
             * Sets the bounds of this object in the form of a Rectangle
             * object.  The bounds specify this object's width, height,
             * and location relative to its parent.
             *      
             * @param r a rectangle indicating this component's bounds
             */
            public void setBounds(Rectangle r) {
                Filler.this.setBounds(r);
            }
    
            /** 
             * Returns the size of this object in the form of a Dimension
             * object.  The height field of the Dimension object contains
             * this objects's height, and the width field of the Dimension
             * object contains this object's width. 
             *
             * @return A Dimension object that indicates the size of this 
             * component; null if this object is not on the screen
             */
            public Dimension getSize() {
                return Filler.this.getSize();
            }
    
            /** 
             * Resizes this object.
             *      
             * @param d - The dimension specifying the new size of the object. 
             */
            public void setSize(Dimension d) {
                Filler.this.setSize(d);
            }
    
            /**
             * Returns the Accessible child, if one exists, contained at the
             * local coordinate Point.
             *
             * @param p The point defining the top-left corner of the
             * Accessible, given in the coordinate space of the object's 
	     * parent. 
             * @return the Accessible, if it exists, at the specified location; 
             *   else null
             */
            public Accessible getAccessibleAt(Point p) {
                return SwingUtilities.getAccessibleAt(Filler.this,p);
            }
    
            /**
             * Returns whether this object can accept focus or not.
             *
             * @return true if object can accept focus; otherwise false
             */
            public boolean isFocusTraversable() {
                return Filler.this.isFocusTraversable();
            }
    
            /**
             * Requests focus for this object.
             */
            public void requestFocus() {
                Filler.this.requestFocus();
            }
    
            /**
             * Adds the specified listener to receive focus events from this 
             * component. 
             *
             * @param l the focus listener
             */
            public void addFocusListener(FocusListener l) {
                Filler.this.addFocusListener(l);
            }
    
            /**
             * Removes the specified listener so it no longer receives focus 
             * events from this component.
             *
             * @param l the focus listener
             */
            public void removeFocusListener(FocusListener l) {
                Filler.this.removeFocusListener(l);
            }
        }
    }

/////////////////
// Accessibility support for Box
////////////////

    /**
     * The currently set AccessibleContext object.
     */
    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JComponent.
     * Creates a new context if necessary.
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
	if (accessibleContext == null) {
	    accessibleContext = new AccessibleBox();
	}
	return accessibleContext;
    }

    protected class AccessibleBox extends AccessibleContext
	implements Serializable, AccessibleComponent {

        // AccessibleContext methods
        //
        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
	 *   object (AccessibleRole.FILLER)
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FILLER;
        }

        /**
         * Gets the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
	 * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
	    return SwingUtilities.getAccessibleStateSet(Box.this);
        }

        /**
         * Gets the Accessible parent of this object.  If the parent of this
         * object implements Accessible, this method should simply return
         * getParent().
         *
         * @return the Accessible parent of this object -- can be null if this
         *   object does not have an Accessible parent
         */
        public Accessible getAccessibleParent() {
            Container parent = getParent();
            if (parent instanceof Accessible) {
                return (Accessible) parent;
            } else {
                return null;
            }
        }

        /**
         * Gets the index of this object in its accessible parent. 
         *
         * @return the index of this object in its parent >= 0; -1 if this 
         *   object does not have an accessible parent.
         * @see #getAccessibleParent
         */
        public int getAccessibleIndexInParent() {
	    return SwingUtilities.getAccessibleIndexInParent(Box.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, then this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object >= 0.
         */
        public int getAccessibleChildrenCount() {
	    return SwingUtilities.getAccessibleChildrenCount(Box.this);
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object, or null
         */
        public Accessible getAccessibleChild(int i) {
    	    return SwingUtilities.getAccessibleChild(Box.this,i);
        }

        /**
         * Returns the locale of this object.
	 *
         * @return the locale of this object
         */
        public Locale getLocale() {
    	    return Box.this.getLocale();
        }

        /**
         * Gets the AccessibleComponent associated with this object if one
         * exists.  Otherwise return null.
         *
         * @return the component
         */
	public AccessibleComponent getAccessibleComponent() {
	    return this;
	}


        // AccessibleComponent methods
        //
        /**
         * Gets the background color of this object.
         *
         * @return the background color, if supported, of the object; 
         * otherwise, null
         */
        public Color getBackground() {
	    return Box.this.getBackground();
	}

        /**
         * Sets the background color of this object.
         *
         * @param c the new Color for the background, null if none
         */
        public void setBackground(Color c) {
	    Box.this.setBackground(c);
	}

        /**
         * Gets the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object; 
         *   otherwise, null
         */
        public Color getForeground() {
	    return Box.this.getForeground();
	}

        /**
         * Sets the foreground color of this object.
         *
         * @param c the new Color for the foreground, null if none
         */
        public void setForeground(Color c) {
	    Box.this.setForeground(c);
	}

        /**
         * Gets the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
	    return Box.this.getCursor();
	}

        /**
         * Sets the Cursor of this object.
         *
         * @param cursor the new Cursor for the object, null if none
         */
        public void setCursor(Cursor cursor) {
	    Box.this.setCursor(cursor);
	}

        /**
         * Gets the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
	    return Box.this.getFont();
	}

        /**
         * Sets the Font of this object.
         *
         * @param f the new Font for the object, null if none
         */
        public void setFont(Font f) {
	    Box.this.setFont(f);
	}

        /**
         * Gets the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see getFont
         */
        public FontMetrics getFontMetrics(Font f) {
	    return Box.this.getFontMetrics(f);
	}

        /**
         * Determines if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
	    return Box.this.isEnabled();
	}

        /**
         * Sets the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it 
         */
        public void setEnabled(boolean b) {
	    Box.this.setEnabled(b);
	}
	
        /**
         * Determines if the object is visible.  Note: this means that the
         * object intends to be visible; however, it may not in fact be
         * showing on the screen because one of the objects that this object
         * is contained by is not visible.  To determine if an object is
         * showing on the screen, use isShowing().
         *
         * @return true if object is visible; otherwise, false
         */
        public boolean isVisible() {
	    return Box.this.isVisible();
	}

        /**
         * Sets the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it 
         */
        public void setVisible(boolean b) {
	    Box.this.setVisible(b);
	}

        /**
         * Determines if the object is showing.  This is determined by checking
         * the visibility of the object and ancestors of the object.  Note: 
	 * this will return true even if the object is obscured by another 
	 * (for example, it happens to be underneath a menu that was pulled 
	 * down).
         *
         * @return true if object is showing; otherwise, false
         */
        public boolean isShowing() {
	    return Box.this.isShowing();
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
	    return Box.this.contains(p);
	}
    
        /** 
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
	    return Box.this.getLocationOnScreen();
	}

        /** 
         * Gets the location of the object relative to the parent in the form 
         * of a point specifying the object's top-left corner in the screen's 
         * coordinate space.
         *
         * @return An instance of Point representing the top-left corner of 
	 *   the objects's bounds in the coordinate space of the screen; null if
         *   this object or its parent are not on the screen
         */
	public Point getLocation() {
	    return Box.this.getLocation();
	}

        /** 
         * Sets the location of the object relative to the parent.
         *
         * @param p the location to be set
         */
        public void setLocation(Point p) {
	    Box.this.setLocation(p);
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
	    return Box.this.getBounds();
	}

        /** 
         * Sets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *	
         * @param r a rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
	    Box.this.setBounds(r);
	}

        /** 
         * Returns the size of this object in the form of a Dimension object. 
         * The height field of the Dimension object contains this objects's
         * height, and the width field of the Dimension object contains this 
	 * object's width. 
         *
         * @return A Dimension object that indicates the size of this 
	 *   component; null if this object is not on the screen
         */
        public Dimension getSize() {
	    return Box.this.getSize();
	}

        /** 
         * Resizes this object.
         *	
         * @param d - The dimension specifying the new size of the object. 
         */
        public void setSize(Dimension d) {
	    Box.this.setSize(d);
	}

        /**
         * Returns the Accessible child, if one exists, contained at the local
	 * coordinate Point.
         *
         * @param p The point defining the top-left corner of the Accessible, 
	 *   given in the coordinate space of the object's parent. 
         * @return the Accessible, if it exists, at the specified location; 
	 *   else null
         */
        public Accessible getAccessibleAt(Point p) {
	    return SwingUtilities.getAccessibleAt(Box.this,p);
	}

        /**
         * Determines whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
	    return Box.this.isFocusTraversable();
	}

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
	    Box.this.requestFocus();
    	}

        /**
         * Adds the specified focus listener to receive focus events from this 
         * component. 
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
	    Box.this.addFocusListener(l);
	}

        /**
         * Removes the specified focus listener so it no longer receives focus 
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
	    Box.this.removeFocusListener(l);
	}
    } // inner class AccessibleBox
}
