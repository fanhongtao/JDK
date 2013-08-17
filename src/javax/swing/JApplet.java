/*
 * @(#)JApplet.java	1.34 98/08/28
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
import java.applet.Applet;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Vector;
import java.io.Serializable;
import javax.accessibility.*;

/**
 * An extended version of java.applet.Applet that adds support for 
 * interposing input and painting behavior in front of the applets
 * children (see glassPane), support for special children that 
 * are managed by a LayeredPane (see rootPane) and for Swing MenuBars.
 * <p>
 * The JApplet class is slightly incompatible with java.applet.Applet.
 * JApplet contains a JRootPane as it's only child.
 * The <b>contentPane</b> should be the parent of any children of the JApplet.
 * This is different than java.applet.Applet, e.g. to add a child to 
 * an an java.applet.Applet you'd write:
 * <pre>
 *       applet.add(child);
 * </pre>
 * However using JApplet you need to add the child to the JApplet's contentPane
 * instead:
 * <pre>
 *       applet.getContentPane().add(child);
 * </pre>
 * The same is true for setting LayoutManagers, removing components,
 * listing children, etc. All these methods should normally be sent to
 * the contentPane() instead of the JApplet itself. The contentPane() will
 * always be non-null. Attempting to set it to null will cause the JApplet
 * to throw an exception. The default contentPane() will have a BorderLayout
 * manager set on it. 
 * <p>
 * Please see the JRootPane documentation for a complete description of
 * the contentPane, glassPane, and layeredPane properties.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JApplet">JApplet</a> key assignments.
 * <p>
 * Both Netscape Communicator and Internet Explorer 4.0 unconditionally
 * print an error message to the Java console when an applet attempts
 * to access the AWT system event queue.  Swing applets do this once,
 * to check if access is permitted.  To prevent the warning message in
 * a production applet one can set a client property called 
 * "defeatSystemEventQueueCheck" on the JApplets RootPane to any 
 * non null value, e.g:
 * <pre>
 * JRootPane rp = myJApplet.getRootPane();
 * rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
 * </pre>
 * We hope that future versions of the browsers will not have this 
 * limitation and we'll be able to retire this hack.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *    description: Swing's Applet subclass.
 *
 * @version 1.34 08/28/98
 * @author Arnaud Weber
 */
public class JApplet extends Applet implements Accessible, RootPaneContainer 
{
    /**
     * @see #getRootPane
     * @see #setRootPane
     */
    protected JRootPane rootPane;

    /**
     * @see #isRootPaneCheckingEnabled
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean rootPaneCheckingEnabled = false;

    /**
     * Creates a swing applet instance.
     */
    public JApplet() {
        super();
	// Check the timerQ and restart if necessary.
	TimerQueue q = TimerQueue.sharedInstance();
	if(q != null) {
	    synchronized(q) {
		if(!q.running)
		    q.start();
	    }
	}

	/* Workaround for bug 4155072.  The shared double buffer image
	 * may hang on to a reference to this applet; unfortunately 
	 * Image.getGraphics() will continue to call JApplet.getForeground()
	 * and getBackground() even after this applet has been destroyed.
	 * So we ensure that these properties are non-null here.
	 */
	setForeground(Color.black);
	setBackground(Color.white);

        setLayout(new BorderLayout());
        setRootPane(createRootPane());
        setRootPaneCheckingEnabled(true);
    }


    /** Called by the constructor methods to create the default rootPane. */
    protected JRootPane createRootPane() {
        return new JRootPane();
    }
    
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if(!e.isConsumed()) {
            JComponent.processKeyBindingsForAllComponents(e,this,e.getID() == KeyEvent.KEY_PRESSED);
        }
    }


    /** 
     * Just calls <code>paint(g)</code>.  This method was overridden to 
     * prevent an unneccessary call to clear the background.
     */
    public void update(Graphics g) {
        paint(g);
    }


   /**
    * Sets the menubar for this applet.
    * @param menubar the menubar being placed in the applet
    *
    * @see #getJMenuBar
    *
    * @beaninfo
    *      hidden: true
    * description: The menubar for accessing pulldown menus from this applet.
    */
    public void setJMenuBar(JMenuBar menuBar) {
        getRootPane().setMenuBar(menuBar);
    }

   /**
    * Returns the menubar set on this applet.
    *
    * @see #setJMenuBar
    */
    public JMenuBar getJMenuBar() {
        return getRootPane().getMenuBar();
    }


    /**
     * @return true if add and setLayout should be checked
     * @see #addImpl
     * @see #setLayout
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean isRootPaneCheckingEnabled() {
        return rootPaneCheckingEnabled;
    }


    /**
     * If true then calls to add() and setLayout() will cause an exception
     * to be thrown.  
     *
     * @see #addImpl
     * @see #setLayout
     * @see #isRootPaneCheckingEnabled
     */
    protected void setRootPaneCheckingEnabled(boolean enabled) {
        rootPaneCheckingEnabled = enabled;
    }


    /**
     * Create an runtime exception with a message like:
     * <pre>
     * "Do not use JApplet.add() use JApplet.getContentPane().add() instead"
     * </pre>
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
     * thiComponent.getContentPane().add(child)
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
     * By default the layout of this component may not be set,
     * the layout of its contentPane should be set instead.  
     * For example:
     * <pre>
     * thiComponent.getContentPane().setLayout(new BorderLayout())
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
     * Returns the rootPane object for this applet.
     *
     * @see #setRootPane
     * @see RootPaneContainer#getRootPane
     */
    public JRootPane getRootPane() { 
        return rootPane; 
    }


    /**
     * Sets the rootPane property.  This method is called by the constructor.
     * @param root the rootPane object for this applet
     *
     * @see #getRootPane
     *
     * @beaninfo
     *   hidden: true
     * description: the RootPane object for this applet.
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
     * Returns the contentPane object for this applet.
     *
     * @see #setContentPane
     * @see RootPaneContainer#getContentPane
     */
    public Container getContentPane() { 
        return getRootPane().getContentPane(); 
    }

   /**
     * Sets the contentPane property.  This method is called by the constructor.
     * @param contentPane the contentPane object for this applet
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is null
     * @see #getContentPane
     * @see RootPaneContainer#setContentPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The client area of the applet where child 
     *                  components are normally inserted.
     */
    public void setContentPane(Container contentPane) {
        getRootPane().setContentPane(contentPane);
    }

    /**
     * Returns the layeredPane object for this applet.
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the layered pane parameter is null
     * @see #setLayeredPane
     * @see RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() { 
        return getRootPane().getLayeredPane(); 
    }

    /**
     * Sets the layeredPane property.  This method is called by the constructor.
     * @param layeredPane the layeredPane object for this applet
     *
     * @see #getLayeredPane
     * @see RootPaneContainer#setLayeredPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The pane which holds the various applet layers.
     */
    public void setLayeredPane(JLayeredPane layeredPane) {
        getRootPane().setLayeredPane(layeredPane);
    }

    /**
     * Returns the glassPane object for this applet.
     *
     * @see #setGlassPane
     * @see RootPaneContainer#getGlassPane
     */
    public Component getGlassPane() { 
        return getRootPane().getGlassPane(); 
    }

    /**
     * Sets the glassPane property. 
     * This method is called by the constructor.
     * @param glassPane the glassPane object for this applet
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
     * Returns a string representation of this JApplet. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JApplet.
     */
    protected String paramString() {
	String rootPaneString = (rootPane != null ?
				 rootPane.toString() : "");
	String rootPaneCheckingEnabledString = (rootPaneCheckingEnabled ?
						"true" : "false");

	return super.paramString() +
	",rootPane=" + rootPaneString +
	",rootPaneCheckingEnabled=" + rootPaneCheckingEnabledString;
    }



/////////////////
// Accessibility support
////////////////

    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JApplet
     *
     * @return the AccessibleContext of this JApplet
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJApplet();
        }
        return accessibleContext;
    }

    protected class AccessibleJApplet extends AccessibleContext
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
            return AccessibleRole.FRAME;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = SwingUtilities.getAccessibleStateSet(JApplet.this);
            states.add(AccessibleState.ACTIVE);
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
            return SwingUtilities.getAccessibleIndexInParent(JApplet.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return SwingUtilities.getAccessibleChildrenCount(JApplet.this);
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return SwingUtilities.getAccessibleChild(JApplet.this,i);
        }

        /**
         * Return the locale of this object.
         *
         * @return the locale of this object
         */
        public Locale getLocale() {
            return JApplet.this.getLocale();
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
            return JApplet.this.getBackground();
        }

        /**
         * Set the background color of this object.
         *
         * @param c the new Color for the background
         */
        public void setBackground(Color c) {
            JApplet.this.setBackground(c);
        }

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object; 
         * otherwise, null
         */
        public Color getForeground() {
            return JApplet.this.getForeground();
        }

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
            JApplet.this.setForeground(c);
        }

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
            return JApplet.this.getCursor();
        }

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
            JApplet.this.setCursor(cursor);
        }

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
            return JApplet.this.getFont();
        }

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
            JApplet.this.setFont(f);
        }

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see getFont
         */
        public FontMetrics getFontMetrics(Font f) {
            return JApplet.this.getFontMetrics(f);
        }

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
            return JApplet.this.isEnabled();
        }

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it 
         */
        public void setEnabled(boolean b) {
            JApplet.this.setEnabled(b);
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
            return JApplet.this.isVisible();
        }

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it 
         */
        public void setVisible(boolean b) {
            JApplet.this.setVisible(b);
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
            return JApplet.this.isShowing();
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
            return JApplet.this.contains(p);
        }
    
        /** 
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
            return JApplet.this.getLocationOnScreen();
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
            return JApplet.this.getLocation();
        }

        /** 
         * Sets the location of the object relative to the parent.
         */
        public void setLocation(Point p) {
            JApplet.this.setLocation(p);
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
            return JApplet.this.getBounds();
        }

        /** 
         * Sets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *      
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
            JApplet.this.setBounds(r);
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
            return JApplet.this.getSize();
        }

        /** 
         * Resizes this object so that it has width width and height. 
         *      
         * @param d - The dimension specifying the new size of the object. 
         */
        public void setSize(Dimension d) {
            JApplet.this.setSize(d);
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
            return SwingUtilities.getAccessibleAt(JApplet.this,p);
        }

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
            return JApplet.this.isFocusTraversable();
        }

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
            JApplet.this.requestFocus();
        }

        /**
         * Adds the specified focus listener to receive focus events from this 
         * component. 
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
            JApplet.this.addFocusListener(l);
        }

        /**
         * Removes the specified focus listener so it no longer receives focus 
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
            JApplet.this.removeFocusListener(l);
        }
    } // inner class AccessibleJApplet
}

