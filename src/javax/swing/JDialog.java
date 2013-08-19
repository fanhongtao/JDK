/*
 * @(#)JDialog.java	1.71 03/01/28
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
 * The same principle holds true for setting layout managers, removing 
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
 * In a multi-screen environment, you can create a <code>JDialog</code>
 * on a different screen device than its owner.  See {@link java.awt.Frame} for
 * more information.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JDialog"><code>JDialog</code> key assignments</a>.
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
 * @see JOptionPane
 * @see JRootPane
 *
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *    description: A toplevel window for creating dialog boxes.
 *
 * @version 1.71 01/28/03
 * @author David Kloba
 * @author James Gosling
 * @author Scott Violet
 */
public class JDialog extends Dialog implements WindowConstants, Accessible, RootPaneContainer 
{
    /**
     * Key into the AppContext, used to check if should provide decorations
     * by default.
     */
    private static final Object defaultLookAndFeelDecoratedKey = 
    	    new StringBuffer("JDialog.defaultLookAndFeelDecorated");

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
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     * 
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog() throws HeadlessException {
        this((Frame)null, false);
    }

    /**
     * Creates a non-modal dialog without a title with the
     * specified <code>Frame</code> as its owner.  If <code>owner</code>
     * is <code>null</code>, a shared, hidden frame will be set as the
     * owner of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Frame owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner <code>Frame</code>.  If <code>owner</code>
     * is <code>null</code>, a shared, hidden frame will be set as the
     * owner of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param modal  true for a modal dialog, false for one that allows
     *               others windows to be active at the same time
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Frame owner, boolean modal) throws HeadlessException {
        this(owner, null, modal);
    }

    /**
     * Creates a non-modal dialog with the specified title and
     * with the specified owner frame.  If <code>owner</code>
     * is <code>null</code>, a shared, hidden frame will be set as the
     * owner of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Frame owner, String title) throws HeadlessException {
        this(owner, title, false);     
    }

    /**
     * Creates a modal or non-modal dialog with the specified title 
     * and the specified owner <code>Frame</code>.  If <code>owner</code>
     * is <code>null</code>, a shared, hidden frame will be set as the
     * owner of this dialog.  All constructors defer to this one.
     * <p>
     * NOTE: Any popup components (<code>JComboBox</code>,
     * <code>JPopupMenu</code>, <code>JMenuBar</code>)
     * created within a modal dialog will be forced to be lightweight.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Frame owner, String title, boolean modal)
        throws HeadlessException {
        super(owner == null? SwingUtilities.getSharedOwnerFrame() : owner, 
              title, modal);
        dialogInit();
    }

    /**
     * Creates a modal or non-modal dialog with the specified title, 
     * owner <code>Frame</code>, and <code>GraphicsConfiguration</code>.
     * 
     * <p>
     * NOTE: Any popup components (<code>JComboBox</code>,
     * <code>JPopupMenu</code>, <code>JMenuBar</code>)
     * created within a modal dialog will be forced to be lightweight.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *                  title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     * @param gc the <code>GraphicsConfiguration</code> 
     * of the target screen device.  If <code>gc</code> is 
     * <code>null</code>, the same
     * <code>GraphicsConfiguration</code> as the owning Frame is used.    
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     * @since 1.4
     */
    public JDialog(Frame owner, String title, boolean modal,
                   GraphicsConfiguration gc) {
        super(owner == null? SwingUtilities.getSharedOwnerFrame() : owner, 
              title, modal, gc);
        dialogInit();
    }

    /**
     * Creates a non-modal dialog without a title with the
     * specified <code>Dialog</code> as its owner.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Dialog owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner dialog.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Dialog owner, boolean modal) throws HeadlessException {
        this(owner, null, modal);
    }

    /**
     * Creates a non-modal dialog with the specified title and
     * with the specified owner dialog.
     * <p>
     * This constructor sets the component's locale property to the value 
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Dialog owner, String title) throws HeadlessException {
        this(owner, title, false);     
    }

    /**
     * Creates a modal or non-modal dialog with the specified title 
     * and the specified owner frame. 
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     *
     * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JDialog(Dialog owner, String title, boolean modal)
        throws HeadlessException {
        super(owner, title, modal);
        dialogInit();
    }

    /**
     * Creates a modal or non-modal dialog with the specified title, 
     * owner <code>Dialog</code>, and <code>GraphicsConfiguration</code>.
     * 
     * <p>
     * NOTE: Any popup components (<code>JComboBox</code>,
     * <code>JPopupMenu</code>, <code>JMenuBar</code>)
     * created within a modal dialog will be forced to be lightweight.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.     
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     * @param title  the <code>String</code> to display in the dialog's
     *			title bar
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     * @param gc the <code>GraphicsConfiguration</code> 
     * of the target screen device.  If <code>gc</code> is 
     * <code>null</code>, the same
     * <code>GraphicsConfiguration</code> as the owning Dialog is used.    
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     * returns true.
     * @since 1.4
     */
    public JDialog(Dialog owner, String title, boolean modal,
                   GraphicsConfiguration gc) throws HeadlessException {

        super(owner, title, modal, gc);
        dialogInit();
    }

    /**
     * Called by the constructors to init the <code>JDialog</code> properly.
     */
    protected void dialogInit() {
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        setLocale( JComponent.getDefaultLocale() );
        setRootPane(createRootPane());
        setRootPaneCheckingEnabled(true);
        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = 
            UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                setUndecorated(true);
                getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
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
     * <li><code>DO_NOTHING_ON_CLOSE</code> - do not do anything - require the
     * program to handle the operation in the <code>windowClosing</code>
     * method of a registered <code>WindowListener</code> object.
     * <li><code>HIDE_ON_CLOSE</code> - automatically hide the dialog after
     * invoking any registered <code>WindowListener</code> objects
     * <li><code>DISPOSE_ON_CLOSE</code> - automatically hide and dispose the 
     * dialog after invoking any registered <code>WindowListener</code> objects
     * </ul>
     * <p>
     * The value is set to <code>HIDE_ON_CLOSE</code> by default.
     * <p>
     * <b>Note</b>: When the last displayable window within the
     * Java virtual machine (VM) is disposed of, the VM may
     * terminate.  See <a href="../../java/awt/doc-files/AWTThreadIssues.html">
     * AWT Threading Issues</a> for more information.
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
     * prevent an unnecessary call to clear the background.
     *
     * @param g  the <code>Graphics</code> context in which to paint
     */
    public void update(Graphics g) {
        paint(g);
    }

   /**
    * Sets the menubar for this dialog.
    *
    * @param menu the menubar being placed in the dialog
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
     * By default, children may not be added directly to this component,
     * they must be added to its <code>contentPane</code> instead.
     * For example:
     * <pre>
     * thisComponent.getContentPane().add(child)
     * </pre>
     * An attempt to add to directly to this component will cause an
     * runtime exception to be thrown if <code>rootPaneCheckingEnabled</code>
     * is true. Subclasses can disable this behavior.
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
     *
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
     * runtime exception to be thrown if <code>rootPaneCheckingEnabled</code>
     * is true.  Subclasses can disable this behavior.
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
     *
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
     *
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
     * @see JRootPane
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
     *
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
     *
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
     * Provides a hint as to whether or not newly created <code>JDialog</code>s
     * should have their Window decorations (such as borders, widgets to
     * close the window, title...) provided by the current look
     * and feel. If <code>defaultLookAndFeelDecorated</code> is true,
     * the current <code>LookAndFeel</code> supports providing window
     * decorations, and the current window manager supports undecorated
     * windows, then newly created <code>JDialog</code>s will have their
     * Window decorations provided by the current <code>LookAndFeel</code>.
     * Otherwise, newly created <code>JDialog</code>s will have their
     * Window decorations provided by the current window manager.
     * <p>
     * You can get the same effect on a single JDialog by doing the following:
     * <pre>
     *    JDialog dialog = new JDialog();
     *    dialog.setUndecorated(true);
     *    dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
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
     * Returns true if newly created <code>JDialog</code>s should have their
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
