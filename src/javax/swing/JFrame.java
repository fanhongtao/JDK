/*
 * @(#)JFrame.java	1.2 00/01/12
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
 * An extended version of java.awt.Frame that adds support for 
 * interposing input and painting behavior in front of the frame's
 * children (see glassPane), support for special children that 
 * are managed by a LayeredPane (see rootPane) and for Swing MenuBars.
 * <p>
 * The JFrame class is slightly incompatible with java.awt.Frame.
 * JFrame contains a JRootPane as it's only child.
 * The <b>contentPane</b> should be the parent of any children of the JFrame.
 * This is different than java.awt.Frame, e.g. to add a child to 
 * an AWT Frame you'd write:
 * <pre>
 *       frame.add(child);
 * </pre>
 * However using JFrame you need to add the child to the JFrames contentPane
 * instead:
 * <pre>
 *       frame.getContentPane().add(child);
 * </pre>
 * The same is true for setting LayoutManagers, removing components,
 * listing children, etc. All these methods should normally be sent to
 * the contentPane() instead of the JFrame itself. The contentPane() will
 * always be non-null. Attempting to set it to null will cause the JFrame
 * to throw an exception. The default contentPane() will have a BorderLayout
 * manager set on it. 
 * <p>
 * Please see the JRootPane documentation for a complete description of
 * the contentPane, glassPane, and layeredPane properties.
 * <p>
 * Unlike its parent class, java.awt.Frame, you can tell a JFrame how to 
 * respond when the user attempts to close the window. The default behavior
 * is to simply hide the JFrame when the user closes the window. To change the
 * default behavior, you invoke the method <code>setDefaultCloseOperation</code>.
 * To make the JFrame remain open unless you handle the window-closing event and 
 * explicitly invoke <code>dispose()</code> (or exit the app, which is also pretty
 * effective), use
 * <code>setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)</code>.
 * That makes the JFrame behave the same as java.awt.Frame. A third option
 * lets you completely dispose of the window when it closes, instead of merely 
 * hiding it. 
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JFrame">JFrame</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JRootPane
 * @see #setDefaultCloseOperation
 * @see java.awt.event.WindowListener#windowClosing
 *
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *    description: A toplevel window which can be minimized to an icon.
 *
 * @version 1.65 04/22/99
 * @author Jeff Dinkins
 * @author Georges Saab
 * @author David Kloba
 */
public class JFrame  extends Frame implements WindowConstants, Accessible, RootPaneContainer
{
    private int defaultCloseOperation = HIDE_ON_CLOSE;

    /**
     * The JRootPane instance that manages the <code>contentPane</code> 
     * and optional <code>menuBar</code> for this frame, as well as the 
     * <code>glassPane</code>.
     *
     * @see JRootPane
     * @see RootPaneContainer
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
     * Constructs a new Frame that is initially invisible.
     *
     * @see Component#setSize
     * @see Component#setVisible
     */
    public JFrame() {
        super();        
        frameInit();
    }

    /**
     * Constructs a new, initially invisible Frame with the specified
     * title.
     *
     * @param title the title for the frame
     * @see Component#setSize
     * @see Component#setVisible
     */
    public JFrame(String title) {
        super(title);
        frameInit();
    }

    /** Called by the constructors to init the JFrame properly. */
    protected void frameInit() {
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        /*if[JDK1.2]
          // workaround for bug 4170760
          enableInputMethods(false);
          end[JDK1.2]*/
        setRootPane(createRootPane());
        setBackground(UIManager.getColor("control"));
        setRootPaneCheckingEnabled(true);
    }

    /** Called by the constructor methods to create the default rootPane. */
    protected JRootPane createRootPane() {
        return new JRootPane();
    }
 
    /** 
     * Processes key events occurring on this component and, if appropriate,
     * passes them on to components in the frame which have registered 
     * interest in them.
     *
     * @param  e  the key event
     * @see    java.awt.Component#processKeyEvent
     */   
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if(!e.isConsumed()) {
            JComponent.processKeyBindingsForAllComponents(e,this,e.getID() == KeyEvent.KEY_PRESSED);
        }
    }

    /** 
     * Processes window events occurring on this component.
     * Hides the window or disposes of it, as specified by the setting
     * of the <code>defaultCloseOperation</code> property.
     *
     * @param  e  the window event
     * @see    #setDefaultCloseOperation
     * @see    java.awt.Window#processWindowEvent
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            switch(defaultCloseOperation) {
              case HIDE_ON_CLOSE:
                 setVisible(false);
                 break;
              case DISPOSE_ON_CLOSE:
                 setVisible(false);
                 dispose();
                 break;
              case DO_NOTHING_ON_CLOSE:
                 default: 
                 break;
	      case 3: // EXIT_ON_CLOSE:
		System.exit(0);
		break;
            }
        }
    }

//    public void setMenuBar(MenuBar menu) {  
//        throw new IllegalComponentStateException("Please use setJMenuBar() with JFrame.");
//    }

    /**                   
     * Sets the operation which will happen by default when
     * the user initiates a "close" on this frame.
     * The possible choices are defined in the <code>WindowConstants</code>
     * interface:
     * <p>
     * <ul>
     * <li>DO_NOTHING_ON_CLOSE - do not do anything - require the
     * program to handle the operation in the windowClosing
     * method of a registered WindowListener object.
     * <li>HIDE_ON_CLOSE - automatically hide the frame after
     * invoking any registered WindowListener objects
     * <li>DISPOSE_ON_CLOSE - automatically hide and dispose the 
     * frame after invoking any registered WindowListener objects
     * <li>EXIT_ON_CLOSE - Exit the application by way of System.exit.
     * Only use this in applications.
     * </ul>
     * <p>
     * The value is set to HIDE_ON_CLOSE by default.
     * @see #addWindowListener
     * @see #getDefaultCloseOperation
     *
     * @beaninfo
     *   preferred: true
     *        enum: DO_NOTHING_ON_CLOSE WindowConstants.DO_NOTHING_ON_CLOSE
     *              HIDE_ON_CLOSE       WindowConstants.HIDE_ON_CLOSE
     *              DISPOSE_ON_CLOSE    WindowConstants.DISPOSE_ON_CLOSE
     *              EXIT_ON_CLOSE       3
     * description: The frame's default close operation.
     */
    public void setDefaultCloseOperation(int operation) {
        this.defaultCloseOperation = operation;
    }


   /**
    * Returns the operation which occurs when the user
    * initiates a "close" on this frame.
    *
    * @return an int indicating the window-close operation
    * @see #setDefaultCloseOperation
    */
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }


    /** 
     * Just calls <code>paint(g)</code>.  This method was overridden to 
     * prevent an unneccessary call to clear the background.
     *
     * @param g the Graphics context in which to paint
     */
    public void update(Graphics g) {
        paint(g);
    }

   /**
    * Sets the menubar for this frame.
    * @param menubar the menubar being placed in the frame
    *
    * @see #getJMenuBar
    *
    * @beaninfo
    *      hidden: true
    * description: The menubar for accessing pulldown menus from this frame.
    */
    public void setJMenuBar(JMenuBar menubar) {
        getRootPane().setMenuBar(menubar);
    }

   /**
    * Returns the menubar set on this frame.
    *
    * @see #setJMenuBar
    */
    public JMenuBar getJMenuBar() { 
        return getRootPane().getMenuBar(); 
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
     * "Do not use JFrame.add() use JFrame.getContentPane().add() instead"
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
     * Returns the rootPane object for this frame.
     *
     * @see #setRootPane
     * @see RootPaneContainer#getRootPane
     */
    public JRootPane getRootPane() { 
        return rootPane; 
    }


    /**
     * Sets the rootPane property.  This method is called by the constructor.
     * @param root the rootPane object for this frame
     *
     * @see #getRootPane
     *
     * @beaninfo
     *   hidden: true
     * description: the RootPane object for this frame.
     */
    protected void setRootPane(JRootPane root) 
    {
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
     * Returns the contentPane object for this frame.
     *
     * @see #setContentPane
     * @see RootPaneContainer#getContentPane
     */
    public Container getContentPane() { 
        return getRootPane().getContentPane(); 
    }

    /**
     * Sets the contentPane property.  This method is called by the constructor.
     * @param contentPane the contentPane object for this frame
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is null
     * @see #getContentPane
     * @see RootPaneContainer#setContentPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The client area of the frame where child 
     *                  components are normally inserted.
     */
    public void setContentPane(Container contentPane) {
        getRootPane().setContentPane(contentPane);
    }

    /**
     * Returns the layeredPane object for this frame.
     *
     * @see #setLayeredPane
     * @see RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() { 
        return getRootPane().getLayeredPane(); 
    }

    /**
     * Sets the layeredPane property.  This method is called by the constructor.
     * @param layeredPane the layeredPane object for this frame
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the layered pane parameter is null
     * @see #getLayeredPane
     * @see RootPaneContainer#setLayeredPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The pane which holds the various frame layers.
     */
    public void setLayeredPane(JLayeredPane layeredPane) {
        getRootPane().setLayeredPane(layeredPane);
    }

    /**
     * Returns the glassPane object for this frame.
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
     * @param glassPane the glassPane object for this frame
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
     * Returns a string representation of this JFrame. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JFrame.
     */
    protected String paramString() {
        String defaultCloseOperationString;
        if (defaultCloseOperation == HIDE_ON_CLOSE) {
            defaultCloseOperationString = "HIDE_ON_CLOSE";
        } else if (defaultCloseOperation == DISPOSE_ON_CLOSE) {
            defaultCloseOperationString = "DISPOSE_ON_CLOSE";
        } else if (defaultCloseOperation == DO_NOTHING_ON_CLOSE) {
            defaultCloseOperationString = "DO_NOTHING_ON_CLOSE";
        } else if (defaultCloseOperation == 3) {
            defaultCloseOperationString = "EXIT_ON_CLOSE";
        } else defaultCloseOperationString = "";
	String rootPaneString = (rootPane != null ?
				 rootPane.toString() : "");
	String rootPaneCheckingEnabledString = (rootPaneCheckingEnabled ?
						"true" : "false");

	return super.paramString() +
	",defaultCloseOperation=" + defaultCloseOperationString +
	",rootPane=" + rootPaneString +
	",rootPaneCheckingEnabled=" + rootPaneCheckingEnabledString;
    }



/////////////////
// Accessibility support
////////////////

    /** The accessible context property */
    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JFrame
     *
     * @return the AccessibleContext of this JFrame
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJFrame();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the AccessibleRole for this object.
     */
    protected class AccessibleJFrame extends AccessibleContext
        implements Serializable, AccessibleComponent {

        // AccessibleContext methods
        /**
         * Get the accessible name of this object.  
         *
         * @return the localized name of the object -- can be null if this 
         * object does not have a name
         */
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else {
                if (getTitle() == null) {
                    return super.getAccessibleName();
                } else {
                    return getTitle();
                }
            }
        }

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
            AccessibleStateSet states = SwingUtilities.getAccessibleStateSet(JFrame.this);
            if (isResizable()) {
                states.add(AccessibleState.RESIZABLE);
            }
            if (getFocusOwner() != null) {
                states.add(AccessibleState.ACTIVE);
            }   
            // FIXME:  [[[WDW - should also return ICONIFIED and ICONIFIABLE
            // if we can ever figure these out]]]
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
            return SwingUtilities.getAccessibleIndexInParent(JFrame.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return SwingUtilities.getAccessibleChildrenCount(JFrame.this);
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return SwingUtilities.getAccessibleChild(JFrame.this,i);
        }

        /**
         * Return the locale of this object.
         *
         * @return the locale of this object
         */
        public Locale getLocale() {
            return JFrame.this.getLocale();
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
            return JFrame.this.getBackground();
        }

        /**
         * Set the background color of this object.
         *
         * @param c the new Color for the background
         */
        public void setBackground(Color c) {
            JFrame.this.setBackground(c);
        }

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object; 
         * otherwise, null
         */
        public Color getForeground() {
            return JFrame.this.getForeground();
        }

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
            JFrame.this.setForeground(c);
        }

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
            return JFrame.this.getCursor();
        }

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
            JFrame.this.setCursor(cursor);
        }

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
            return JFrame.this.getFont();
        }

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
            JFrame.this.setFont(f);
        }

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see #getFont
         */
        public FontMetrics getFontMetrics(Font f) {
            return JFrame.this.getFontMetrics(f);
        }

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
            return JFrame.this.isEnabled();
        }

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it 
         */
        public void setEnabled(boolean b) {
            JFrame.this.setEnabled(b);
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
            return JFrame.this.isVisible();
        }

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it 
         */
        public void setVisible(boolean b) {
            JFrame.this.setVisible(b);
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
            return JFrame.this.isShowing();
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
            return JFrame.this.contains(p);
        }
    
        /** 
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
            return JFrame.this.getLocationOnScreen();
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
            return JFrame.this.getLocation();
        }

        /** 
         * Sets the location of the object relative to the parent.
         */
        public void setLocation(Point p) {
            JFrame.this.setLocation(p);
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
            return JFrame.this.getBounds();
        }

        /** 
         * Sets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *      
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
            JFrame.this.setBounds(r);
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
            return JFrame.this.getSize();
        }

        /** 
         * Resizes this object so that it has width width and height. 
         *      
         * @param d - The dimension specifying the new size of the object. 
         */
        public void setSize(Dimension d) {
            JFrame.this.setSize(d);
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
            Accessible a;
            AccessibleContext ac;
            AccessibleComponent acmp;
            Point location;
            int nchildren = getAccessibleChildrenCount();
            for (int i=0; i < nchildren; i++) {
                a = getAccessibleChild(i);
                if ((a != null)) {
                    ac = a.getAccessibleContext();
                    if (ac != null) {
                        acmp = ac.getAccessibleComponent();
                        if ((acmp != null) && (acmp.isShowing())) {
                            location = acmp.getLocation();
                            Point np = new Point(p.x-location.x, p.y-location.y)
;
                            if (acmp.contains(np)){
                                return a;
                            }
                        }
                    }
                }
            }
            return (Accessible) JFrame.this;
//          return SwingUtilities.getAccessibleAt(JFrame.this,p);
        }

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
            return JFrame.this.isFocusTraversable();
        }

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
            JFrame.this.requestFocus();
        }

        /**
         * Adds the specified focus listener to receive focus events from this 
         * component. 
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
            JFrame.this.addFocusListener(l);
        }

        /**
         * Removes the specified focus listener so it no longer receives focus 
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
            JFrame.this.removeFocusListener(l);
        }
    } // inner class AccessibleJFrame
}
