/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * to create a custom dialog, or invoke the many class methods
 * in {@link JOptionPane} to create a variety of standard dialogs.
 * For information about creating dialogs, see
 * <em>The Java Tutorial</em> section
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html">How
 * to Make Dialogs</a>.
 *
 * <p>
 *
 * The <code>JDialog</code> component contains a <code>JRootPane</code>
 * as its only child.
 * The <code>contentPane</code> should be the parent of any children of the
 * <code>JDialog</code>. From the older <code>java.awt.Window</code> object
 * you would normally do something like this:
 * <PRE>
 *       dialog.add(child);
 * </PRE>
 * Using <code>JDialog</code> the proper semantic is:
 * <PRE>
 *       dialog.getContentPane().add(child);
 * </PRE>
 * The same priniciple holds true for setting layout managers, removing 
 * components, listing children, etc. All these methods should normally be
 * sent to the <code>contentPane</code> instead of to the <code>JDialog</code>.
 * The <code>contentPane</code> is always non-<code>null</code>.
 * Attempting to set it to <code>null</code> generates an exception.
 * The default <code>contentPane</code> has a <code>BorderLayout</code>
 * manager set on it. 
 * <p>
 * Please see the <code>JRootPane</code> documentation for a complete 
 * description of the <code>contentPane</code>, <code>glassPane</code>, 
 * and <code>layeredPane</code> components.
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
 * @version 1.54 02/06/02
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
     * Creates a non-modal dialog without a title and without a specified
     * <code>Frame</code> owner.  A shared, hidden frame will be
     * set as the owner of the dialog.
     */
    public JDialog() {
        this((Frame)null, false);
    }

    /**
     * Creates a non-modal dialog without a title with the
     * specifed <code>Frame</code> as its owner.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     */
    public JDialog(Frame owner) {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner <code>Frame</code>.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
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
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     */
    public JDialog(Frame owner, String title) {
        this(owner, title, false);     
    }

    /**
     * Creates a modal or non-modal dialog with the specified title 
     * and the specified owner <code>Frame</code>.  All constructors
     * defer to this one.
     * <p>
     * NOTE: Any popup components (<code>JComboBox</code>,
     * <code>JPopupMenu</code>, <code>JMenuBar</code>)
     * created within a modal dialog will be forced to be lightweight.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     */
    public JDialog(Frame owner, String title, boolean modal) {
        super(owner == null? SwingUtilities.getSharedOwnerFrame() : owner, 
              title, modal);
        dialogInit();
    }

    /**
     * Creates a non-modal dialog without a title with the
     * specifed <code>Dialog</code> as its owner.
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     */
    public JDialog(Dialog owner) {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner dialog.
     * <p>
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     */
    public JDialog(Dialog owner, boolean modal) {
        this(owner, null, modal);
    }

    /**
     * Creates a non-modal dialog with the specified title and
     * with the specified owner dialog.
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     */
    public JDialog(Dialog owner, String title) {
        this(owner, title, false);     
    }

    /**
     * Creates a modal or non-modal dialog with the specified title 
     * and the specified owner frame.
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     */
    public JDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        dialogInit();
    }


    /** Called by the constructors to init the <code>JDialog</code> properly. */
    protected void dialogInit() {
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        setRootPane(createRootPane());
        setRootPaneCheckingEnabled(true);
    }

    /**
     * Called by the constructor methods to create the default
     * <code>rootPane</code>.
     */
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
     * program to handle the operation in the <code>windowClosing</code>
     * method of a registered <code>WindowListener</code> object.
     * <li>HIDE_ON_CLOSE - automatically hide the dialog after
     * invoking any registered <code>WindowListener</code> objects
     * <li>DISPOSE_ON_CLOSE - automatically hide and dispose the 
     * dialog after invoking any registered <code>WindowListener</code> objects
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
    * @return an integer indicating the window-close operation
    * @see #setDefaultCloseOperation
    */
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }


    /** 
     * Calls <code>paint(g)</code>.  This method was overridden to 
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
     * Returns true if the methods <code>add</code> and <code>setLayout</code>
     * should be checked.
     *
     * @return true if <code>add</code> and <code>setLayout</code> should
     *		be checked
     * @see #addImpl
     * @see #setLayout
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean isRootPaneCheckingEnabled() {
        return rootPaneCheckingEnabled;
    }


    /**
     * If true then calls to <code>add</code> and <code>setLayout</code>
     * will cause an exception to be thrown.  
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
     * Creates a message that can be used as a runtime exception.  The
     * message will look like the following:
     * <pre>
     * "Do not use JDialog.add() use JDialog.getContentPane().add() instead"
     * </pre>
     * @param op a <code>String</code> containing the attempted operation
     * @return an <code>Error</code> containing the constructed string
     */
    private Error createRootPaneException(String op) {
        String type = getClass().getName();
        return new Error(
            "Do not use " + type + "." + op + "() use " 
                          + type + ".getContentPane()." + op + "() instead");
    }


    /**
     * By default, children may not be added directly to a this component,
     * they must be added to its <code>contentPane</code> instead.
     * For example:
     * <pre>
     * thisComponent.getContentPane().add(child)
     * </pre>
     * An attempt to add to directly to this component will cause an
     * runtime exception to be thrown if rootPaneCheckingEnabled is true. 
     * Subclasses can disable this behavior.
     * 
     * @param comp  the <code>Component</code> to be enhanced
     * @param constraints the constraints to be respected
     * @param index the index (an integer)
     * @see #setRootPaneCheckingEnabled
     * @exception Error if called with rootPaneCheckingEnabled true
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
     * the layout of its <code>contentPane</code> should be set instead.  
     * For example:
     * <pre>
     * thisComponent.getContentPane().setLayout(new BorderLayout())
     * </pre>
     * An attempt to set the layout of this component will cause an
     * runtime exception to be thrown if rootPaneCheckingEnabled is true.  
     * Subclasses can disable this behavior.
     * 
     * @see #setRootPaneCheckingEnabled
     * @param manager the <code>LayoutManager</code>
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
     * Returns the <code>rootPane</code> object for this dialog.
     *
     * @see #setRootPane
     * @see RootPaneContainer#getRootPane
     */
    public JRootPane getRootPane() { 
        return rootPane; 
    }


    /**
     * Sets the <code>rootPane</code> property. 
     * This method is called by the constructor.
     * @param root the <code>rootPane</code> object for this dialog
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
     * Returns the <code>contentPane</code> object for this dialog.
     * @return the <code>contentPane</code> property
     *
     * @see #setContentPane
     * @see RootPaneContainer#getContentPane
     */
    public Container getContentPane() { 
        return getRootPane().getContentPane(); 
    }


   /**
     * Sets the <code>contentPane</code> property. 
     * This method is called by the constructor.
     *
     * @param contentPane the <code>contentPane</code> object for this dialog
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is <code>null</code>
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
     * Returns the <code>layeredPane</code> object for this dialog.
     * @return the <code>layeredPane</code> property
     *
     * @see #setLayeredPane
     * @see RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() { 
        return getRootPane().getLayeredPane(); 
    }

    /**
     * Sets the <code>layeredPane</code> property.  
     * This method is called by the constructor.
     *
     * @param layeredPane the new <code>layeredPane</code> property
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
     * Returns the <code>glassPane</code> object for this dialog.
     * @return the <code>glassPane</code> property
     *
     * @see #setGlassPane
     * @see RootPaneContainer#getGlassPane
     */
    public Component getGlassPane() { 
        return getRootPane().getGlassPane(); 
    }

    /**
     * Sets the <code>glassPane</code> property. 
     * This method is called by the constructor.
     *
     * @param glassPane the <code>glassPane</code> object for this dialog
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
     * Returns a string representation of this <code>JDialog</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JDialog</code>.
     */
    protected String paramString() {
        String defaultCloseOperationString;
        if (defaultCloseOperation == HIDE_ON_CLOSE) {
            defaultCloseOperationString = "HIDE_ON_CLOSE";
        } else if (defaultCloseOperation == DISPOSE_ON_CLOSE) {
            defaultCloseOperationString = "DISPOSE_ON_CLOSE";
        } else if (defaultCloseOperation == DO_NOTHING_ON_CLOSE) {
            defaultCloseOperationString = "DO_NOTHING_ON_CLOSE";
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
     * Gets the AccessibleContext associated with this JDialog. 
     * For JDialogs, the AccessibleContext takes the form of an 
     * AccessibleJDialog. 
     * A new AccessibleJDialog instance is created if necessary.
     *
     * @return an AccessibleJDialog that serves as the 
     *         AccessibleContext of this JDialog
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJDialog();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JDialog</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to dialog user-interface 
     * elements.
     */
    protected class AccessibleJDialog extends AccessibleAWTDialog { 
        
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
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
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
    } // inner class AccessibleJDialog
}
