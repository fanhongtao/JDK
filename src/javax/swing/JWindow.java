/*
 * @(#)JWindow.java	1.2 00/01/12
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
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Vector;
import java.io.Serializable;

import javax.accessibility.*;

/** 
 * A JWindow is a container that can be displayed anywhere on the
 * user's desktop. It does not have the title bar, window-management buttons,
 * or other trimmings associated with a JFrame, but it is still a 
 * "first-class citizen" of the user's desktop, and can exist anywhere
 * on it.
 * <p>
 * The JWindow component contains a JRootPane as it's only child.
 * The contentPane() should be the parent of any children of the JWindow.
 * From the older java.awt.Window object you would normally do something like this:<PRE>
 *       window.add(child);
 * </PRE>
 * However, using JWindow you would code:<PRE>
 *       window.getContentPane().add(child);
 * </PRE>
 * The same is true of setting LayoutManagers, removing components,
 * listing children, etc. All these methods should normally be sent to
 * the contentPane() instead of the JWindow itself. The contentPane() will
 * always be non-null. Attempting to set it to null will cause the JWindow
 * to throw an exception. The default contentPane() will have a BorderLayout
 * manager set on it. 
 * <p>
 * Please see the JRootPane documentation for a complete description of
 * the contentPane(), glassPane(), and layeredPane() components.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JWindow">JWindow</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JRootPane
 *
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *    description: A toplevel window which has no system border or controls.
 *
 * @version 1.27 04/22/99
 * @author David Kloba
 */
public class JWindow extends Window implements Accessible, RootPaneContainer 
{
    /**
     * The JRootPane instance that manages the <code>contentPane</code> 
     * and optional <code>menuBar</code> for this frame, as well as the 
     * <code>glassPane</code>.
     *
     * @see #getRootPane
     * @see #setRootPane
     */
    protected JRootPane rootPane;

    /**
     * If true then calls to <code>add</code> and <code>setLayout</code>
     * cause an exception to be thrown.
     *
     * @see #isRootPaneCheckingEnabled
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean rootPaneCheckingEnabled = false;


    /**
     * Creates a window with no specified owner.
     */
    public JWindow() {
        this((Frame)null);
    }

    /**
     * Creates a window with the specified owner frame.
     *
     * @param owner the frame from which the window is displayed
     */
    public JWindow(Frame owner) {
        super(owner == null? SwingUtilities.getSharedOwnerFrame() : owner);     
        windowInit();
    }

    /**
     * Creates a window with the specified owner window.
     *
     * @param owner the window from which the window is displayed
     */

    public JWindow(Window owner) {
        super(owner);     
        windowInit();
    }
  

    /** Called by the constructors to init the JWindow properly. */
    protected void windowInit() {
        setRootPane(createRootPane());
        setRootPaneCheckingEnabled(true);
    }

    /** Called by the constructor methods to create the default rootPane. */
    protected JRootPane createRootPane() {
        return new JRootPane();
    }
 
    /**
     * Returns whether calls to <code>add</code> and 
     * <code>setLayout</code> cause an exception to be thrown. 
     *
     * @return true if <code>add</code> and <code>setLayout</code> 
     *         are checked
     *
     * @see #addImpl
     * @see #setLayout
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean isRootPaneCheckingEnabled() {
        return rootPaneCheckingEnabled;
    }


    /**
     * Determines whether calls to <code>add</code> and 
     * <code>setLayout</code> cause an exception to be thrown. 
     * 
     * @param enabled  a boolean value, true if checking is to be
     *        enabled, which cause the exceptions to be thrown
     *
     * @see #addImpl
     * @see #setLayout
     * @see #isRootPaneCheckingEnabled
     * @beaninfo
     *      hidden: true
     * description: Whether the add and setLayout methods throw exceptions when invoked.
     */
    protected void setRootPaneCheckingEnabled(boolean enabled) {
        rootPaneCheckingEnabled = enabled;
    }


    /**
     * Creates a runtime exception with a message like:
     * <pre>
     * "Do not use JWindow.add() use JWindow.getContentPane().add() instead"
     * </pre>
     *
     * @param op  a String indicating the attempted operation. In the
     *            example above, the operation string is "add"
     */
    private Error createRootPaneException(String op) {
        String type = getClass().getName();
        return new Error(
            "Do not use " + type + "." + op + "() use " 
                          + type + ".getContentPane()." + op + "() instead");
    }


    /**
     * By default, children may not be added directly to a this component,
     * they must be added to its contentPane instead.  For example:
     * <pre>
     * thisComponent.getContentPane().add(child)
     * </pre>
     * An attempt to add to directly to this component will cause an
     * runtime exception to be thrown.  Subclasses can disable this
     * behavior.
     * 
     * @see #setRootPaneCheckingEnabled
     * @exception Error if called with rootPaneChecking true
     */
    protected void addImpl(Component comp, Object constraints, int index) 
    {
        if(isRootPaneCheckingEnabled()) {
            throw createRootPaneException("add");
        }
        else {
            super.addImpl(comp, constraints, index);
        }
    }

    /** 
     * Removes the specified component from this container.
     * @param comp the component to be removed
     * @see #add
     */
    public void remove(Component comp) {
	if (comp == rootPane) {
	    super.remove(comp);
	} else {
	    // Client mistake, but we need to handle it to avoid a
	    // common object leak in client applications.
	    getContentPane().remove(comp);
	}
    }


    /**
     * By default the layout of this component may not be set,
     * the layout of its contentPane should be set instead.  
     * For example:
     * <pre>
     * thisComponent.getContentPane().setLayout(new BorderLayout())
     * </pre>
     * An attempt to set the layout of this component will cause an
     * runtime exception to be thrown.  Subclasses can disable this
     * behavior.
     * 
     * @see #setRootPaneCheckingEnabled
     * @exception Error if called with rootPaneChecking true
     */
    public void setLayout(LayoutManager manager) {
        if(isRootPaneCheckingEnabled()) {
            throw createRootPaneException("setLayout");
        }
        else {
            super.setLayout(manager);
        }
    }


    /**
     * Returns the rootPane object for this window.
     *
     * @see #setRootPane
     * @see RootPaneContainer#getRootPane
     */
    public JRootPane getRootPane() { 
        return rootPane; 
    }


    /**
     * Sets the rootPane property.  This method is called by the constructor.
     *
     * @param root the rootPane object for this window
     * @see #getRootPane
     *
     * @beaninfo
     *        hidden: true
     *   description: the RootPane object for this window.
     */
    protected void setRootPane(JRootPane root) {
        if(rootPane != null) {
            remove(rootPane);
        }
        rootPane = root;
        if(rootPane != null) {
            boolean checkingEnabled = isRootPaneCheckingEnabled();
            try {
                setRootPaneCheckingEnabled(false);
                add(rootPane, BorderLayout.CENTER);
            }
            finally {
                setRootPaneCheckingEnabled(checkingEnabled);
            }
        }
    }


    /**
     * Returns the contentPane object for this window.
     *
     * @return the Container which is the contentPane
     * @see #setContentPane
     * @see RootPaneContainer#getContentPane
     */
    public Container getContentPane() { 
        return getRootPane().getContentPane(); 
    }

    /**
     * Sets the contentPane property.  This method is called by the constructor.
     * @param contentPane the contentPane object for this window
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is null
     * @see #getContentPane
     * @see RootPaneContainer#setContentPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The client area of the window where child 
     *                  components are normally inserted.
     */
    public void setContentPane(Container contentPane) {
        getRootPane().setContentPane(contentPane);
    }

    /**
     * Returns the layeredPane object for this window.
     *
     * @return the JLayeredPane object
     * @see #setLayeredPane
     * @see RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() { 
        return getRootPane().getLayeredPane(); 
    }

    /**
     * Sets the layeredPane property.  This method is called by the constructor.
     * @param layeredPane the layeredPane object for this window
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is null
     * @see #getLayeredPane
     * @see RootPaneContainer#setLayeredPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The pane which holds the various window layers.
     */
    public void setLayeredPane(JLayeredPane layeredPane) {
        getRootPane().setLayeredPane(layeredPane);
    }

    /**
     * Returns the glassPane object for this window.
     *
     * @return the Component which is the glassPane
     * @see #setGlassPane
     * @see RootPaneContainer#getGlassPane
     */
    public Component getGlassPane() { 
        return getRootPane().getGlassPane(); 
    }

    /**
     * Sets the glassPane property. 
     * This method is called by the constructor.
     * @param glassPane the glassPane object for this window
     *
     * @see #getGlassPane
     * @see RootPaneContainer#setGlassPane
     *
     * @beaninfo
     *     hidden: true
     *     description: A transparent pane used for menu rendering.
     */
    public void setGlassPane(Component glassPane) {
        getRootPane().setGlassPane(glassPane);
    }


    /**
     * Returns a string representation of this JWindow. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JWindow.
     */
    protected String paramString() {
        String rootPaneCheckingEnabledString = (rootPaneCheckingEnabled ?
						"true" : "false");

        return super.paramString() +
	",rootPaneCheckingEnabled=" + rootPaneCheckingEnabledString;
    }


/////////////////
// Accessibility support
////////////////

    /** The accessible context property */
    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JWindow
     *
     * @return the AccessibleContext of this JWindow
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJWindow();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the AccessibleRole for this object.
     */
    protected class AccessibleJWindow extends AccessibleContext
        implements Serializable, AccessibleComponent {

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
            return AccessibleRole.WINDOW;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = SwingUtilities.getAccessibleStateSet(JWindow.this);
            if (getFocusOwner() != null) {
                states.add(AccessibleState.ACTIVE);
            }
            return states;
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
            return SwingUtilities.getAccessibleIndexInParent(JWindow.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return SwingUtilities.getAccessibleChildrenCount(JWindow.this);
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return SwingUtilities.getAccessibleChild(JWindow.this,i);
        }

        /**
         * Return the locale of this object.
         *
         * @return the locale of this object
         */
        public Locale getLocale() {
            return JWindow.this.getLocale();
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
            return JWindow.this.getBackground();
        }

        /**
         * Set the background color of this object.
         *
         * @param c the new Color for the background
         */
        public void setBackground(Color c) {
            JWindow.this.setBackground(c);
        }

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object; 
         * otherwise, null
         */
        public Color getForeground() {
            return JWindow.this.getForeground();
        }

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
            JWindow.this.setForeground(c);
        }

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
            return JWindow.this.getCursor();
        }

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
            JWindow.this.setCursor(cursor);
        }

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
            return JWindow.this.getFont();
        }

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
            JWindow.this.setFont(f);
        }

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see #getFont
         */
        public FontMetrics getFontMetrics(Font f) {
            return JWindow.this.getFontMetrics(f);
        }

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
            return JWindow.this.isEnabled();
        }

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it 
         */
        public void setEnabled(boolean b) {
            JWindow.this.setEnabled(b);
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
            return JWindow.this.isVisible();
        }

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it 
         */
        public void setVisible(boolean b) {
            JWindow.this.setVisible(b);
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
            return JWindow.this.isShowing();
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
            return JWindow.this.contains(p);
        }
    
        /** 
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
            return JWindow.this.getLocationOnScreen();
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
            return JWindow.this.getLocation();
        }

        /** 
         * Sets the location of the object relative to the parent.
	 * 
	 * @param p the Point object specifying the location of the
	 *          object's upper left corner
         */
        public void setLocation(Point p) {
            JWindow.this.setLocation(p);
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
            return JWindow.this.getBounds();
        }

        /** 
         * Sets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *      
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
            JWindow.this.setBounds(r);
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
            return JWindow.this.getSize();
        }

        /** 
         * Resizes this object so that it has width width and height. 
         *      
         * @param d - The dimension specifying the new size of the object. 
         */
        public void setSize(Dimension d) {
            JWindow.this.setSize(d);
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
            return SwingUtilities.getAccessibleAt(JWindow.this,p);
        }

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
            return JWindow.this.isFocusTraversable();
        }

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
            JWindow.this.requestFocus();
        }

        /**
         * Adds the specified focus listener to receive focus events from this 
         * component. 
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
            JWindow.this.addFocusListener(l);
        }

        /**
         * Removes the specified focus listener so it no longer receives focus 
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
            JWindow.this.removeFocusListener(l);
        }
    } // inner class AccessibleJWindow
}

