/*
 * @(#)JDialog.java	1.43 99/04/22
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
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Vector;
import java.io.Serializable;
import javax.accessibility.*;
import java.applet.Applet;

/** 
 * The main class for creating a dialog window. You can use this class
 * to create a custom dialog, or invoke the many static methods
 * in JOptionPane to create a variety of standard dialogs.
 *
 * The JDialog component contains a JRootPane as it's only 
 * child.
 * The <code>contentPane</code> should be the parent of any children of 
 * the JDialog. From the older <code>java.awt.Window</code> object you 
 * would normally do something like this:
 * <PRE>
 *       dialog.add(child);
 * </PRE>
 * Using JDialog the proper semantic is:
 * <PRE>
 *       dialog.getContentPane().add(child);
 * </PRE>
 * The same priniciple holds true for setting layout managers, removing 
 * components, listing children, etc. All these methods should normally 
 * be sent to the <code>contentPane</code> instead of to the JDialog.
 * The <code>contentPane</code> is always non-null. Attempting to set it 
 * to null generates an exception. The default <code>contentPane</code> 
 * has a BorderLayout manager set on it. 
 * <p>
 * Please see the JRootPane documentation for a complete 
 * description of the <code>contentPane</code>, <code>glassPane</code>, 
 * and <code>layeredPane</code> components.
 * <p>
 * NOTE: For 1.1, Modal dialogs are currently constrained to only allow
 * lightweight popup menus (JPopupMenu, JComboBox, JMenuBar) because
 * of window ownership limitations in AWT1.1.   This creates the further
 * limitation of not being able to mix Swing popup components with
 * AWT heavyweight components in a modal dialog since the heavyweight
 * components would always overlap the lightweights, potentially
 * obscuring the popup menu.
 * (A heavyweight component uses a native-platform component (peer)
 * component for its implementation -- AWT components are heavyweight
 * components.)
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JDialog">JDialog</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JOptionPane
 * @see JRootPane
 *
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *    description: A toplevel window for creating dialog boxes.
 *
 * @version 1.43 04/22/99
 * @author David Kloba
 * @author James Gosling
 * @author Scott Violet
 */
public class JDialog extends Dialog implements WindowConstants, Accessible, RootPaneContainer 
{
    private int defaultCloseOperation = HIDE_ON_CLOSE;
    
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
     * Creates a non-modal dialog without a title and without
     * a specified Frame owner.  A shared, hidden frame will be
     * set as the owner of the Dialog.
     */
    public JDialog() {
        this((Frame)null, false);
    }

    /**
     * Creates a non-modal dialog without a title with the
     * specifed Frame as its owner.
     *
     * @param owner the Frame from which the dialog is displayed
     */
    public JDialog(Frame owner) {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner frame.
     *
     * @param owner the Frame from which the dialog is displayed
     * @param modal  true for a modal dialog, false for one that allows
     *               others windows to be active at the same time
     */
    public JDialog(Frame owner, boolean modal) {
        this(owner, null, modal);
    }

    /**
     * Creates a non-modal dialog with the specified title and
     * with the specified owner frame.
     *
     * @param owner the Frame from which the dialog is displayed
     * @param title  the String to display in the dialog's title bar
     */
    public JDialog(Frame owner, String title) {
        this(owner, title, false);     
    }

    /**
     * Creates a modal or non-modal dialog with the specified title 
     * and the specified owner frame.
     * <p>
     * NOTE: Any popup components (JComboBox, JPopupMenu, JMenuBar)
     * created within a modal dialog will be forced to be lightweight.
     *
     * @param owner the frame from which the dialog is displayed
     * @param title  the String to display in the dialog's title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               others windows to be active at the same time
     */
    public JDialog(Frame owner, String title, boolean modal) {
        super(owner == null? SwingUtilities.getSharedOwnerFrame() : owner, 
              title, modal);
        dialogInit();
    }

    /**
     * Creates a non-modal dialog without a title with the
     * specifed Dialog as its owner.
     *
     * @param owner the Dialog from which the dialog is displayed
     */

    public JDialog(Dialog owner) {
        this(owner, false);
    }
  

    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner dialog.
     * <p>
     *
     * @param owner the Dialog from which the dialog is displayed
     * @param modal  true for a modal dialog, false for one that allows
     *               others windows to be active at the same time
     */

    public JDialog(Dialog owner, boolean modal) {
        this(owner, null, modal);
    }
  

    /**
     * Creates a non-modal dialog with the specified title and
     * with the specified owner dialog.
     *
     * @param owner the Dialog from which the dialog is displayed
     * @param title  the String to display in the dialog's title bar
     */

    public JDialog(Dialog owner, String title) {
        this(owner, title, false);     
    }
  

    /**
     * Creates a modal or non-modal dialog with the specified title 
     * and the specified owner frame.
     *
     * @param owner the dialog from which the dialog is displayed
     * @param title  the String to display in the dialog's title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               others windows to be active at the same time
     */

    public JDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        dialogInit();
    }
  


    /** Called by the constructors to init the JDialog properly. */
    protected void dialogInit() {
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        setRootPane(createRootPane());
        setRootPaneCheckingEnabled(true);
    }

    /** Called by the constructor methods to create the default rootPane. */
    protected JRootPane createRootPane() {
        return new JRootPane();
    }

    /** 
     * Processes key events occurring on this component and, if appropriate,
     * passes them on to components in the dialog which have registered 
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
     * Handles window events depending on the state of the
     * <code>defaultCloseOperation</code> property.
     *
     * @see #setDefaultCloseOperation
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
              case 3:
                 System.exit(0);
                 break;
              case DO_NOTHING_ON_CLOSE:
                 default: 
                 break;
            }
        }
    }
 

    /**
     * Sets the operation which will happen by default when
     * the user initiates a "close" on this dialog.
     * The possible choices are:
     * <ul>
     * <li>DO_NOTHING_ON_CLOSE - do not do anything - require the
     * program to handle the operation in the windowClosing
     * method of a registered WindowListener object.
     * <li>HIDE_ON_CLOSE - automatically hide the dialog after
     * invoking any registered WindowListener objects
     * <li>DISPOSE_ON_CLOSE - automatically hide and dispose the 
     * dialog after invoking any registered WindowListener objects
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
     * description: The dialog's default close operation.
     */
    public void setDefaultCloseOperation(int operation) {
        this.defaultCloseOperation = operation;
    }

   /**
    * Returns the operation which occurs when the user
    * initiates a "close" on this dialog.
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
     */
    public void update(Graphics g) {
        paint(g);
    }

   /**
    * Sets the menubar for this dialog.
    * @param menubar the menubar being placed in the dialog
    *
    * @see #getJMenuBar
    *
    * @beaninfo
    *      hidden: true
    * description: The menubar for accessing pulldown menus from this dialog.
    */
    public void setJMenuBar(JMenuBar menu) {
        getRootPane().setMenuBar(menu);
    }

   /**
    * Returns the menubar set on this dialog.
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
     * @beaninfo
     *   hidden: true
     * description: Whether the add and setLayout methods throw exceptions when invoked.
     */
    protected void setRootPaneCheckingEnabled(boolean enabled) {
        rootPaneCheckingEnabled = enabled;
    }

    /**
     * Create an runtime exception with a message like:
     * <pre>
     * "Do not use JDialog.add() use JDialog.getContentPane().add() instead"
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
     * Returns the rootPane object for this dialog.
     *
     * @see #setRootPane
     * @see RootPaneContainer#getRootPane
     */
    public JRootPane getRootPane() { 
        return rootPane; 
    }


    /**
     * Sets the rootPane property.  This method is called by the constructor.
     * @param root the rootPane object for this dialog
     *
     * @see #getRootPane
     *
     * @beaninfo
     *   hidden: true
     * description: the RootPane object for this dialog.
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
     * Returns the contentPane object for this dialog.
     *
     * @see #setContentPane
     * @see RootPaneContainer#getContentPane
     */
    public Container getContentPane() { 
        return getRootPane().getContentPane(); 
    }


   /**
     * Sets the contentPane property.  This method is called by the constructor.
     * @param contentPane the contentPane object for this dialog
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is null
     * @see #getContentPane
     * @see RootPaneContainer#setContentPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The client area of the dialog where child 
     *                  components are normally inserted.
     */
    public void setContentPane(Container contentPane) {
        getRootPane().setContentPane(contentPane);
    }

    /**
     * Returns the layeredPane object for this dialog.
     *
     * @see #setLayeredPane
     * @see RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() { 
        return getRootPane().getLayeredPane(); 
    }

    /**
     * Sets the layeredPane property.  This method is called by the constructor.
     * @param layeredPane the layeredPane object for this dialog
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the layered pane parameter is null
     * @see #getLayeredPane
     * @see RootPaneContainer#setLayeredPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The pane which holds the various dialog layers.
     */
    public void setLayeredPane(JLayeredPane layeredPane) {
        getRootPane().setLayeredPane(layeredPane);
    }

    /**
     * Returns the glassPane object for this dialog.
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
     * @param glassPane the glassPane object for this dialog
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
     * Sets the location of the dialog relative to the specified
     * component. If the component is not currently showing, the 
     * dialog is centered on the screen.
     *
     * @param c  the component in relation to which the dialog's location
     *           is determined
     */
    public void setLocationRelativeTo(Component c) {
        Container root=null;

        if (c != null) {
            if (c instanceof Window || c instanceof Applet) {
               root = (Container)c;
            } else {
                Container parent;
                for(parent = c.getParent() ; parent != null ; parent = parent.getParent()) {
                    if (parent instanceof Window || parent instanceof Applet) {
                        root = parent;
                        break;
                    }
                }
            }
        }

        if((c != null && !c.isShowing()) || root == null ||
           !root.isShowing()) {
            Dimension         paneSize = getSize();
            Dimension         screenSize = getToolkit().getScreenSize();

            setLocation((screenSize.width - paneSize.width) / 2,
                        (screenSize.height - paneSize.height) / 2);
        } else {
            Dimension invokerSize = c.getSize();
            Point invokerScreenLocation;

            // If this method is called directly after a call to
            // setLocation() on the "root", getLocationOnScreen()
            // may return stale results (Bug#4181562), so we walk
            // up the tree to calculate the position instead
            // (unless "root" is an applet, where we cannot walk
            // all the way up to a toplevel window)
            //
            if (root instanceof Applet) {
                invokerScreenLocation = c.getLocationOnScreen();
            } else {
                invokerScreenLocation = new Point(0,0);
                Component tc = c;
                while (tc != null) {
                    Point tcl = tc.getLocation();
                    invokerScreenLocation.x += tcl.x;
                    invokerScreenLocation.y += tcl.y;
                    if (tc == root) {
                        break;
                    }
                    tc = tc.getParent();  
                }              
            }                


            Rectangle           dialogBounds = getBounds();
            int                 dx = invokerScreenLocation.x+((invokerSize.width-dialogBounds.width)>>1);
            int                 dy = invokerScreenLocation.y+((invokerSize.height - dialogBounds.height)>>1);
            Dimension           ss = getToolkit().getScreenSize();

            if (dy+dialogBounds.height>ss.height) {
                dy = ss.height-dialogBounds.height;
                dx = invokerScreenLocation.x<(ss.width>>1) ? invokerScreenLocation.x+invokerSize.width :
                    invokerScreenLocation.x-dialogBounds.width;
            }
            if (dx+dialogBounds.width>ss.width) dx = ss.width-dialogBounds.width;
            if (dx<0) dx = 0;
            if (dy<0) dy = 0;
            setLocation(dx, dy);
        }
    }


    /**
     * Returns a string representation of this JDialog. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JDialog.
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

    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JDialog
     *
     * @return the AccessibleContext of this JDialog
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJDialog();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the AccessibleRole for this object.
     */
    protected class AccessibleJDialog extends AccessibleContext 
        implements Serializable, AccessibleComponent {

        
        // AccessibleContext methods
        //
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
            return AccessibleRole.DIALOG;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = SwingUtilities.getAccessibleStateSet(JDialog.this);
            if (isResizable()) {
                states.add(AccessibleState.RESIZABLE);
            }
            if (getFocusOwner() != null) {
                states.add(AccessibleState.ACTIVE);
            }
            if (isModal()) {
                states.add(AccessibleState.MODAL);
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
            return SwingUtilities.getAccessibleIndexInParent(JDialog.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return SwingUtilities.getAccessibleChildrenCount(JDialog.this);
        }

        /**
         * Return the nth Accessible child of the object.  
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return SwingUtilities.getAccessibleChild(JDialog.this,i);
        }

        /**
         * Return the locale of this object.
         *
         * @return the locale of this object
         */
        public Locale getLocale() {
            return JDialog.this.getLocale();
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
            return JDialog.this.getBackground();
        }

        /**
         * Set the background color of this object.
         *
         * @param c the new Color for the background
         */
        public void setBackground(Color c) {
            JDialog.this.setBackground(c);
        }

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object; 
         * otherwise, null
         */
        public Color getForeground() {
            return JDialog.this.getForeground();
        }

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
            JDialog.this.setForeground(c);
        }

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
            return JDialog.this.getCursor();
        }

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
            JDialog.this.setCursor(cursor);
        }

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
            return JDialog.this.getFont();
        }

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
            JDialog.this.setFont(f);
        }

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see #getFont
         */
        public FontMetrics getFontMetrics(Font f) {
            return JDialog.this.getFontMetrics(f);
        }

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
            return JDialog.this.isEnabled();
        }

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it 
         */
        public void setEnabled(boolean b) {
            JDialog.this.setEnabled(b);
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
            return JDialog.this.isVisible();
        }

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it 
         */
        public void setVisible(boolean b) {
            JDialog.this.setVisible(b);
        }

        /**
         * Determine if the object is showing. This is determined by checking
         * the visibility of the object and ancestors of the object.  Note: 
         * this will return true even if the object is obscured by another 
         * (for example, it happens to be underneath a menu that was pulled 
         * down).
         *
         * @return true if object is showing; otherwise, false
         */
        public boolean isShowing() {
            return JDialog.this.isShowing();
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
            return JDialog.this.contains(p);
        }
    
        /** 
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
            return JDialog.this.getLocationOnScreen();
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
            return JDialog.this.getLocation();
        }

        /** 
         * Sets the location of the object relative to the parent.
         */
        public void setLocation(Point p) {
            JDialog.this.setLocation(p);
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
            return JDialog.this.getBounds();
        }

        /** 
         * Sets the bounds of this object in the form of a Rectangle object. 
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *      
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
            JDialog.this.setBounds(r);
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
            return JDialog.this.getSize();
        }

        /** 
         * Resizes this object so that it has width width and height. 
         *      
         * @param d - The dimension specifying the new size of the object. 
         */
        public void setSize(Dimension d) {
            JDialog.this.setSize(d);
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
            return SwingUtilities.getAccessibleAt(JDialog.this,p);
        }

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
            return JDialog.this.isFocusTraversable();
        }

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
            JDialog.this.requestFocus();
        }

        /**
         * Adds the specified focus listener to receive focus events from this 
         * component. 
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
            JDialog.this.addFocusListener(l);
        }

        /**
         * Removes the specified focus listener so it no longer receives focus 
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
            JDialog.this.removeFocusListener(l);
        }
    } // inner class AccessibleJDialog
}
