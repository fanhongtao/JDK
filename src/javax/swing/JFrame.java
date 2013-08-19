/*
 * @(#)JFrame.java	1.95 03/01/28
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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


/**
 * An extended version of <code>java.awt.Frame</code> that adds support for 
 * the JFC/Swing component architecture.
 * You can find task-oriented documentation about using <code>JFrame</code>
 * in <em>The Java Tutorial</em>, in the section
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/frame.html">How to Make Frames</a>.
 * 
 * <p>
 * The <code>JFrame</code> class is slightly incompatible with <code>Frame</code>.
 * Like all other JFC/Swing top-level containers,
 * a <code>JFrame</code> contains a <code>JRootPane</code> as its only child.
 * The <b>content pane</b> provided by the root pane should,
 * as a rule, contain
 * all the non-menu components displayed by the <code>JFrame</code>.
 * This is different from the AWT <code>Frame</code> case.  
 * For example, to add a child to 
 * an AWT frame you'd write:
 * <pre>
 *       frame.add(child);
 * </pre>
 * However using <code>JFrame</code> you need to add the child
 * to the <code>JFrame's</code> content pane
 * instead:
 * <pre>
 *       frame.getContentPane().add(child);
 * </pre>
 * The same is true for setting layout managers, removing components,
 * listing children, and so on. All these methods should normally be sent to
 * the content pane instead of the JFrame itself. The content pane will
 * always be non-null. Attempting to set it to null will cause the JFrame
 * to throw an exception. The default content pane will have a BorderLayout
 * manager set on it. 
 * <p>
 * Unlike a <code>Frame</code>, a <code>JFrame</code> has some notion of how to 
 * respond when the user attempts to close the window. The default behavior
 * is to simply hide the JFrame when the user closes the window. To change the
 * default behavior, you invoke the method
 * {@link #setDefaultCloseOperation}.
 * To make the <code>JFrame</code> behave the same as a <code>Frame</code>
 * instance, use
 * <code>setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)</code>.
 * <p>
 * For more information on content panes
 * and other features that root panes provide,
 * see <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/toplevel.html">Using Top-Level Containers</a> in <em>The Java Tutorial</em>.
 * <p>
 * In a multi-screen environment, you can create a <code>JFrame</code>
 * on a different screen device.  See {@link java.awt.Frame} for more
 * information.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JFrame"><code>JFrame</code> key assignments</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
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
 * @version 1.95 01/28/03
 * @author Jeff Dinkins
 * @author Georges Saab
 * @author David Kloba
 */
public class JFrame  extends Frame implements WindowConstants, Accessible, RootPaneContainer
{
    /**
     * The exit application default window close operation. If a window
     * has this set as the close operation and is closed in an applet,
     * a <code>SecurityException</code> may be thrown.
     * It is recommended you only use this in an application.
     * <p>
     * @since 1.3
     */
    public static final int EXIT_ON_CLOSE = 3;

    /**
     * Key into the AppContext, used to check if should provide decorations
     * by default.
     */
    private static final Object defaultLookAndFeelDecoratedKey = 
    	    new StringBuffer("JFrame.defaultLookAndFeelDecorated");

    private int defaultCloseOperation = HIDE_ON_CLOSE;

    /**
     * The <code>JRootPane</code> instance that manages the
     * <code>contentPane</code> 
     * and optional <code>menuBar</code> for this frame, as well as the 
     * <code>glassPane</code>.
     *
     * @see JRootPane
     * @see RootPaneContainer
     */
    protected JRootPane rootPane;

    /**
     * If true then calls to <code>add</code> and <code>setLayout</code>
     * will cause an exception to be thrown. The default is false.
     *
     * @see #isRootPaneCheckingEnabled
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean rootPaneCheckingEnabled = false;


    /**
     * Constructs a new frame that is initially invisible.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @see JComponent#getDefaultLocale
     */
    public JFrame() throws HeadlessException {
        super();        
        frameInit();
    }

    /**
     * Creates a <code>Frame</code> in the specified
     * <code>GraphicsConfiguration</code> of
     * a screen device and a blank title.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param gc the <code>GraphicsConfiguration</code> that is used
     * 		to construct the new <code>Frame</code>;
     * 		if <code>gc</code> is <code>null</code>, the system
     * 		default <code>GraphicsConfiguration</code> is assumed
     * @exception IllegalArgumentException if <code>gc</code> is not from
     * 		a screen device.  This exception is always thrown when
     *      GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     * @since     1.3
     */
    public JFrame(GraphicsConfiguration gc) {
        super(gc);
        frameInit();
    }

    /**
     * Creates a new, initially invisible <code>Frame</code> with the 
     * specified title.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param title the title for the frame
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @see JComponent#getDefaultLocale
     */
    public JFrame(String title) throws HeadlessException {
        super(title);
        frameInit();
    }
    
    /**
     * Creates a <code>JFrame</code> with the specified title and the
     * specified <code>GraphicsConfiguration</code> of a screen device.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param title the title to be displayed in the
     * 		frame's border. A <code>null</code> value is treated as
     * 		an empty string, "".
     * @param gc the <code>GraphicsConfiguration</code> that is used
     * 		to construct the new <code>JFrame</code> with;
     *		if <code>gc</code> is <code>null</code>, the system
     * 		default <code>GraphicsConfiguration</code> is assumed
     * @exception IllegalArgumentException if <code>gc</code> is not from
     * 		a screen device.  This exception is always thrown when
     *      GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     * @since     1.3
     */
    public JFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        frameInit();
    }

    /** Called by the constructors to init the <code>JFrame</code> properly. */
    protected void frameInit() {
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        setLocale( JComponent.getDefaultLocale() );
        setRootPane(createRootPane());
        setBackground(UIManager.getColor("control"));
        setRootPaneCheckingEnabled(true);
        if (JFrame.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = 
            UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                setUndecorated(true);
                getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            }
        }
        setFocusTraversalPolicy(KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                getDefaultFocusTraversalPolicy());        
    }

    /**
     * Called by the constructor methods to create the default
     * <code>rootPane</code>.
     */
    protected JRootPane createRootPane() {
        return new JRootPane();
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
	      case EXIT_ON_CLOSE:
                  // This needs to match the checkExit call in
                  // setDefaultCloseOperation
		System.exit(0);
		break;
            }
        }
    }

//    public void setMenuBar(MenuBar menu) {  
//        throw new IllegalComponentStateException("Please use setJMenuBar() with JFrame.");
//    }

    /**                   
     * Sets the operation that will happen by default when
     * the user initiates a "close" on this frame.
     * You must specify one of the following choices:
     * <p>
     * <ul>
     * <li><code>DO_NOTHING_ON_CLOSE</code>
     * (defined in <code>WindowConstants</code>):
     * Don't do anything; require the
     * program to handle the operation in the <code>windowClosing</code>
     * method of a registered <code>WindowListener</code> object.
     *
     * <li><code>HIDE_ON_CLOSE</code>
     * (defined in <code>WindowConstants</code>):
     * Automatically hide the frame after
     * invoking any registered <code>WindowListener</code>
     * objects.
     *
     * <li><code>DISPOSE_ON_CLOSE</code>
     * (defined in <code>WindowConstants</code>):
     * Automatically hide and dispose the 
     * frame after invoking any registered <code>WindowListener</code>
     * objects.
     *
     * <li><code>EXIT_ON_CLOSE</code>
     * (defined in <code>JFrame</code>):
     * Exit the application using the <code>System</code> <code>exit</code> method.
     * Use this only in applications.
     * </ul>
     * <p>
     * The value is set to <code>HIDE_ON_CLOSE</code> by default.
     * <p>
     * <b>Note</b>: When the last displayable window within the
     * Java virtual machine (VM) is disposed of, the VM may
     * terminate.  See <a href="../../java/awt/doc-files/AWTThreadIssues.html">
     * AWT Threading Issues</a> for more information.
     *
     * @param operation the operation which should be performed when the
     *        user closes the frame
     * @exception IllegalArgumentException if defaultCloseOperation value 
     *             isn't one of the above valid values
     * @see #addWindowListener
     * @see #getDefaultCloseOperation
     * @see WindowConstants
     * @throws  SecurityException
     *        if <code>EXIT_ON_CLOSE</code> has been specified and the
     *        <code>SecurityManager</code> will
     *        not allow the caller to invoke <code>System.exit</code>
     * @see        java.lang.Runtime#exit(int)
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     *        enum: DO_NOTHING_ON_CLOSE WindowConstants.DO_NOTHING_ON_CLOSE
     *              HIDE_ON_CLOSE       WindowConstants.HIDE_ON_CLOSE
     *              DISPOSE_ON_CLOSE    WindowConstants.DISPOSE_ON_CLOSE
     *              EXIT_ON_CLOSE       WindowConstants.EXIT_ON_CLOSE
     * description: The frame's default close operation.
     */
    public void setDefaultCloseOperation(int operation) {
	if (operation != DO_NOTHING_ON_CLOSE &&
	    operation != HIDE_ON_CLOSE &&
	    operation != DISPOSE_ON_CLOSE &&
	    operation != EXIT_ON_CLOSE) {
            throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE");
	}
        if (this.defaultCloseOperation != operation) {
            if (operation == EXIT_ON_CLOSE) {
                SecurityManager security = System.getSecurityManager();
                if (security != null) {
                    security.checkExit(0);
                }
            }
            int oldValue = this.defaultCloseOperation;
            this.defaultCloseOperation = operation;
            firePropertyChange("defaultCloseOperation", oldValue, operation);
	}
    }


   /**
    * Returns the operation that occurs when the user
    * initiates a "close" on this frame.
    *
    * @return an integer indicating the window-close operation
    * @see #setDefaultCloseOperation
    */
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }


    /** 
     * Just calls <code>paint(g)</code>.  This method was overridden to 
     * prevent an unnecessary call to clear the background.
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
    * @return the menubar for this frame
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
     *         are checked; false otherwise
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
     * <code>setLayout</code> will cause an exception to be thrown. 
     * 
     * @param enabled  true if checking is to be
     *        enabled, which causes the exceptions to be thrown
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
     * @param op  a <code>String</code> indicating the attempted operation;
     *		in the example above, the operation string is "add"
     */
    private Error createRootPaneException(String op) {
        String type = getClass().getName();
        return new Error(
            "Do not use " + type + "." + op + "() use " 
                          + type + ".getContentPane()." + op + "() instead");
    }


    /**
     * By default, children may not be added directly to this component,
     * they must be added to its contentPane instead.  For example:
     * <pre>
     * thisComponent.getContentPane().add(child)
     * </pre>
     * An attempt to add to directly to this component will cause an
     * runtime exception to be thrown.  Subclasses can disable this
     * behavior.
     * 
     * @param comp the component to be enhanced
     * @param constraints the constraints to be respected
     * @param index the index
     * @exception Error if called with <code>rootPaneChecking</code> true
     * 
     * @see #setRootPaneCheckingEnabled
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
     * thisComponent.getContentPane().setLayout(new GridLayout(1, 2))
     * </pre>
     * An attempt to set the layout of this component will cause an
     * runtime exception to be thrown.  Subclasses can disable this
     * behavior.
     * @param manager the <code>LayoutManager</code>
     * @exception Error if called with <code>rootPaneChecking</code> true
     * 
     * @see #setRootPaneCheckingEnabled
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
     * Returns the <code>rootPane</code> object for this frame.
     * @return the <code>rootPane</code> property
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
     * @param root the <code>rootPane</code> object for this frame
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
     * Returns the <code>contentPane</code> object for this frame.
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
     * <p>
     * Swing's painting architecture requires an opaque <code>JComponent</code>
     * in the containment hiearchy. This is typically provided by the
     * content pane. If you replace the content pane it is recommended you
     * replace it with an opaque <code>JComponent</code>.
     *
     * @param contentPane the <code>contentPane</code> object for this frame
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is <code>null</code>
     * @see #getContentPane
     * @see RootPaneContainer#setContentPane
     * @see JRootPane
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
     * Returns the <code>layeredPane</code> object for this frame.
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
     * @param layeredPane the <code>layeredPane</code> object for this frame
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the layered pane parameter is <code>null</code>
     * @see #getLayeredPane
     * @see RootPaneContainer#setLayeredPane
     *
     * @beaninfo
     *     hidden: true
     *     description: The pane that holds the various frame layers.
     */
    public void setLayeredPane(JLayeredPane layeredPane) {
        getRootPane().setLayeredPane(layeredPane);
    }

    /**
     * Returns the <code>glassPane</code> object for this frame.
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
     * @param glassPane the <code>glassPane</code> object for this frame
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
     * Provides a hint as to whether or not newly created <code>JFrame</code>s
     * should have their Window decorations (such as borders, widgets to
     * close the window, title...) provided by the current look
     * and feel. If <code>defaultLookAndFeelDecorated</code> is true,
     * the current <code>LookAndFeel</code> supports providing window
     * decorations, and the current window manager supports undecorated
     * windows, then newly created <code>JFrame</code>s will have their
     * Window decorations provided by the current <code>LookAndFeel</code>.
     * Otherwise, newly created <code>JFrame</code>s will have their
     * Window decorations provided by the current window manager.
     * <p>
     * You can get the same effect on a single JFrame by doing the following:
     * <pre>
     *    JFrame frame = new JFrame();
     *    frame.setUndecorated(true);
     *    frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
     * </pre>
     *
     * @param defaultLookAndFeelDecorated A hint as to whether or not current
     *        look and feel should provide window decorations
     * @see javax.swing.LookAndFeel#getSupportsWindowDecorations
     * @since 1.4
     */
    public static void setDefaultLookAndFeelDecorated(boolean defaultLookAndFeelDecorated) {
        if (defaultLookAndFeelDecorated) {
            SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.TRUE);
        } else {
            SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.FALSE);
        }
    }


    /**
     * Returns true if newly created <code>JFrame</code>s should have their
     * Window decorations provided by the current look and feel. This is only
     * a hint, as certain look and feels may not support this feature.
     *
     * @return true if look and feel should provide Window decorations.
     * @since 1.4
     */
    public static boolean isDefaultLookAndFeelDecorated() {    
        Boolean defaultLookAndFeelDecorated = 
            (Boolean) SwingUtilities.appContextGet(defaultLookAndFeelDecoratedKey);
        if (defaultLookAndFeelDecorated == null) {
            defaultLookAndFeelDecorated = Boolean.FALSE;
        }
        return defaultLookAndFeelDecorated.booleanValue();
    }

    /**
     * Returns a string representation of this <code>JFrame</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JFrame</code>
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

    /** The accessible context property. */
    protected AccessibleContext accessibleContext = null;

    /**
     * Gets the AccessibleContext associated with this JFrame. 
     * For JFrames, the AccessibleContext takes the form of an 
     * AccessibleJFrame. 
     * A new AccessibleJFrame instance is created if necessary.
     *
     * @return an AccessibleJFrame that serves as the 
     *         AccessibleContext of this JFrame
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJFrame();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JFrame</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to frame user-interface 
     * elements.
     */
    protected class AccessibleJFrame extends AccessibleAWTFrame {

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
            // FIXME:  [[[WDW - should also return ICONIFIED and ICONIFIABLE
            // if we can ever figure these out]]]
            return states;
        }
    } // inner class AccessibleJFrame
}
