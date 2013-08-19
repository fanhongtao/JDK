/*
 * @(#)JApplet.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * the JFC/Swing component architecture.
 * You can find task-oriented documentation about using <code>JApplet</code>
 * in <em>The Java Tutorial</em>,
 * in the section 
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/applet.html">How to Make Applets</a>.
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
 * <a href="doc-files/Key-Index.html#JApplet"><code>JApplet</code> key assignments</a>.
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
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *    description: Swing's Applet subclass.
 *
 * @version 1.56 01/23/03
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
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>. 
     *
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JApplet() throws HeadlessException {
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

        setLocale( JComponent.getDefaultLocale() );
        setLayout(new BorderLayout());
        setRootPane(createRootPane());
        setRootPaneCheckingEnabled(true);
	
	// This code should be changed after the RFE 4719336 is resolved
	// to not make the applet a FocusCycleRoot, but set it's
	// FocusTraversalPolicy only.
	setFocusCycleRoot(true);
	setFocusTraversalPolicy(KeyboardFocusManager.
				getCurrentKeyboardFocusManager().
				getDefaultFocusTraversalPolicy());
	
        enableEvents(AWTEvent.KEY_EVENT_MASK);
    }


    /** Called by the constructor methods to create the default rootPane. */
    protected JRootPane createRootPane() {
        return new JRootPane();
    }
    
    /** 
     * Just calls <code>paint(g)</code>.  This method was overridden to 
     * prevent an unnecessary call to clear the background.
     */
    public void update(Graphics g) {
        paint(g);
    }


   /**
    * Sets the menubar for this applet.
    * @param menuBar the menubar being placed in the applet
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
     * thisComponent.getContentPane().setLayout(new GridLayout(1, 2))
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
     * Gets the AccessibleContext associated with this JApplet. 
     * For JApplets, the AccessibleContext takes the form of an 
     * AccessibleJApplet. 
     * A new AccessibleJApplet instance is created if necessary.
     *
     * @return an AccessibleJApplet that serves as the 
     *         AccessibleContext of this JApplet
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJApplet();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JApplet</code> class.
     */
    protected class AccessibleJApplet extends AccessibleApplet {
        // everything moved to new parent, AccessibleApplet
    }
}

