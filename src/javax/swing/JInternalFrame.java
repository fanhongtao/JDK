/*
 * @(#)JInternalFrame.java	1.93 00/03/08
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */

package javax.swing;
 
import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.util.EventListener;

import javax.swing.border.Border;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.*;

import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A lightweight object that provides many of the features of
 * a native frame, including dragging, closing, becoming an icon,
 * resizing, title display, and support for a menu bar. Generally,
 * you create an instance and add it to a JDesktopPane. Look and
 * feel specific-actions are then (automatically??) delegated to the 
 * DesktopManager object maintained by the JDesktopPane (as set by
 * the UI).
 * <p>
 * The JInternalFrame <code>contentPane</code> is where you add child components.
 * So, to create a JInternalFrame that has a number of buttons arranged 
 * with a BorderLayout object, you might do something like this:
 * <PRE>
 *    JComponent c = (JComponent) frame.getContentPane();
 *    c.setLayout(new BorderLayout());
 *    c.add(new JButton(), BorderLayout.NORTH);
 *    c.add(new JButton(), BorderLayout.CENTER);
 * </PRE>
 * The <code>contentPane</code> is actually managed by an instance of JRootPane,
 * which also manages a <code>layoutPane</code>, <code>glassPane</code>, and 
 * optional <code>menuBar</code> for the frame. Please see the JRootPane 
 * documentation for a complete description of these components.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JInternalFrame">JInternalFrame</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JDesktopPane
 * @see DesktopManager
 * @see JInternalFrame.JDesktopIcon
 * @see JRootPane
 *
 * @version 1.93 03/08/00
 * @author David Kloba
 * @author Rich Schiavi
 * @beaninfo
 *      attribute: isContainer true
 *      attribute: containerDelegate getContentPane
 *      description: A frame container which is contained within 
 *                   another window.
 */
public class JInternalFrame extends JComponent implements 
        Accessible, WindowConstants,
        RootPaneContainer
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "InternalFrameUI";

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
     */
    protected boolean rootPaneCheckingEnabled = false;

    /** The frame can be closed. */
    protected boolean closable;
    /** The frame has been closed. */
    protected boolean isClosed;
    /** The frame can be expanded to the size of the desktop pane. */
    protected boolean maximizable;
    /** 
     * The frame has been expanded to its maximum size.
     * @see #maximizable
     */
    protected boolean isMaximum;   
    /** 
     * The frame can "iconized" (shrunk down and displayed as
     * an icon-image). 
     * @see JInternalFrame.JDesktopIcon
     */
    protected boolean iconable;
    /** 
     * The frame has been iconized. 
     * @see #iconable
     */
    protected boolean isIcon;   
    /** The frame's size can be changed. */
    protected boolean resizable;
    /** The frame is currently selected. */
    protected boolean isSelected;
    /** The icon shown in the top-left corner of the frame. */
    protected Icon frameIcon;
    /** The title displayed in the frame's title bar. */
    protected String  title;
    /** 
     * The icon that is displayed when the frame is iconized.
     * @see #iconable
     */
    protected JDesktopIcon desktopIcon;

    private boolean opened;

    private int defaultCloseOperation = HIDE_ON_CLOSE;

    /** Bound property name. */
    public final static String CONTENT_PANE_PROPERTY = "contentPane";
    /** Bound property name. */
    public final static String MENU_BAR_PROPERTY = "menuBar";
    /** Bound property name. */
    public final static String TITLE_PROPERTY = "title";
    /** Bound property name. */
    public final static String LAYERED_PANE_PROPERTY = "layeredPane";
    /** Bound property name. */
    public final static String ROOT_PANE_PROPERTY = "rootPane";
    /** Bound property name. */
    public final static String GLASS_PANE_PROPERTY = "glassPane";
    /** Bound property name. */
    public final static String FRAME_ICON_PROPERTY = "frameIcon";

    /** Constrained property name indicated that this frame has selected status. */
    public final static String IS_SELECTED_PROPERTY = "selected";
    /** Constrained property name indicating that the frame is closed. */
    public final static String IS_CLOSED_PROPERTY = "closed";
    /** Constrained property name indicating that the frame is maximized. */
    public final static String IS_MAXIMUM_PROPERTY = "maximum";
    /** Constrained property name indicating that the frame is iconified. */
    public final static String IS_ICON_PROPERTY = "icon";


    /** 
     * Creates a non-resizable, non-closable, non-maximizable,
     * non-iconifiable JInternalFrame with no title.
     */
    public JInternalFrame() {
        this("", false, false, false, false);
    }

    /** 
     * Creates a non-resizable, non-closable, non-maximizable,
     * non-iconifiable JInternalFrame with the specified title.
     *
     * @param title  the String to display in the title bar.
     */
    public JInternalFrame(String title) {
        this(title, false, false, false, false);
    }

    /** 
     * Creates a non-closable, non-maximizable, non-iconifiable 
     * JInternalFrame with the specified title and with resizability 
     * specified.
     *
     * @param title      the String to display in the title bar.
     * @param resizable  if true, the frame can be resized
     */
    public JInternalFrame(String title, boolean resizable) {
        this(title, resizable, false, false, false);
    }

    /** 
     * Creates a non-maximizable, non-iconifiable JInternalFrame with the
     * specified title and with resizability and closability specified.
     *
     * @param title      the String to display in the title bar.
     * @param resizable  if true, the frame can be resized
     * @param closable   if true, the frame can be closed
     */
    public JInternalFrame(String title, boolean resizable, boolean closable) {
        this(title, resizable, closable, false, false);
    }

    /** 
     * Creates a non-iconifiable JInternalFrame with the specified title 
     * and with resizability, closability, and maximizability specified.
     *
     * @param title       the String to display in the title bar.
     * @param resizable   if true, the frame can be resized
     * @param closable    if true, the frame can be closed
     * @param maximizable if true, the frame can be maximized
     */
    public JInternalFrame(String title, boolean resizable, boolean closable,
                          boolean maximizable) {
        this(title, resizable, closable, maximizable, false);
    }

    /** 
     * Creates a JInternalFrame with the specified title and 
     * with resizability, closability, maximizability, and iconifiability
     * specified.
     *
     * @param title       the String to display in the title bar.
     * @param resizable   if true, the frame can be resized
     * @param closable    if true, the frame can be closed
     * @param maximizable if true, the frame can be maximized
     * @param iconifiable if true, the frame can be iconified
     */
    public JInternalFrame(String title, boolean resizable, boolean closable, 
                                boolean maximizable, boolean iconifiable) {
        
        setRootPane(createRootPane());
        setLayout(new BorderLayout());
        this.title = title;
        this.resizable = resizable;
        this.closable = closable;
        this.maximizable = maximizable;
        isMaximum = false;
        this.iconable = iconifiable;                         
        isIcon = false;
        setRootPaneCheckingEnabled(true);
        desktopIcon = new JDesktopIcon(this);
	updateUI();
    }

    /** 
     * Called by the constructor to set up the JRootPane.
     * @see JRootPane
     */
    protected JRootPane createRootPane() {
        return new JRootPane();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the InternalFrameUI object that renders this component
     */
    public InternalFrameUI getUI() {
        return (InternalFrameUI)ui;
    }

    /**
     * Sets the UI delegate for this JInternalFrame.
     * @beaninfo
     *     expert: true
     *     description: The InternalFrameUI implementation that 
     *                  defines the labels look and feel.
     */
    public void setUI(InternalFrameUI ui) {
        boolean checkingEnabled = isRootPaneCheckingEnabled();
        try {
            setRootPaneCheckingEnabled(false);
            super.setUI(ui);
        }
        finally {
            setRootPaneCheckingEnabled(checkingEnabled);
        }
    }

    /**
     * Notification from the UIManager that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * UIManager.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((InternalFrameUI)UIManager.getUI(this));
        invalidate();
        if (desktopIcon != null) {
            desktopIcon.updateUIWhenHidden();
        }
    }

    /* This method is called if updateUI was called on the associated
     * JDesktopIcon.  It's necessary to avoid infinite recursion.
     */
    void updateUIWhenHidden() {
        setUI((InternalFrameUI)UIManager.getUI(this));
        invalidate();
        Component[] children = getComponents();
        if (children != null) {
            for(int i = 0; i < children.length; i++) {
                SwingUtilities.updateComponentTreeUI(children[i]);
            }
        }
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "InternalFrameUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *     description: UIClassID
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Returns whether calls to <code>add</code> and 
     * <code>setLayout</code> cause an exception to be thrown. 
     *
     * @return true if <code>add</code> and <code>setLayout</code> 
     *         are checked
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
	int oldCount = getComponentCount();
	super.remove(comp);
	if (oldCount == getComponentCount()) {
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


//////////////////////////////////////////////////////////////////////////
/// Property Methods
//////////////////////////////////////////////////////////////////////////

    /**
     * Returns the current JMenuBar for this JInternalFrame, or null
     * if no menu bar has been set.
     *
     * @deprecated As of Swing version 1.0.3,
     * replaced by <code>getJMenuBar()</code>.
     */
    public JMenuBar getMenuBar() {
      return getRootPane().getMenuBar();
    }

    /**
     * Returns the current JMenuBar for this JInternalFrame, or null
     * if no menu bar has been set.
     *
     * @return  the JMenuBar used by this frame
     * @see #setJMenuBar
     */
    public JMenuBar getJMenuBar() {
	return getRootPane().getJMenuBar();
    }
    
    /**
     * Sets the JMenuBar for this JInternalFrame.
     *
     * @param m  the JMenuBar to use in this frame
     * @see #getJMenuBar
     * @deprecated As of Swing version 1.0.3
     *  replaced by <code>setJMenuBar(JMenuBar m)</code>.
     */
    public void setMenuBar(JMenuBar m) {
        JMenuBar oldValue = getMenuBar();
        getRootPane().setJMenuBar(m);
        firePropertyChange(MENU_BAR_PROPERTY, oldValue, m);     
    }

    /**
     * Sets the JMenuBar for this JInternalFrame.
     *
     * @param m  the JMenuBar to use in this frame
     * @see #getJMenuBar
     * @beaninfo
     *     preferred: true
     *     description: The menubar for accessing pulldown menus 
     *                  from this frame.
     */
    public void setJMenuBar(JMenuBar m){
        JMenuBar oldValue = getMenuBar();
        getRootPane().setJMenuBar(m);
        firePropertyChange(MENU_BAR_PROPERTY, oldValue, m);     
    }

    // implements javax.swing.RootPaneContainer
    public Container getContentPane() {
        return getRootPane().getContentPane();
    }


    /**
     * Sets this JInternalFrame's content pane.
     * 
     * @param contentPane the contentPane object for this frame
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the content pane parameter is null
     * @see RootPaneContainer#getContentPane
     * @beaninfo
     *     bound: true
     *     hidden: true
     *     description: The client area of the frame where child 
     *                  components are normally inserted.
     */
    public void setContentPane(Container c) {
        Container oldValue = getContentPane();
        getRootPane().setContentPane(c);
        firePropertyChange(CONTENT_PANE_PROPERTY, oldValue, c);
    }

    /**
     * Returns the layeredPane object for this frame.
     *
     * @see RootPaneContainer#setLayeredPane
     * @see RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() { 
        return getRootPane().getLayeredPane(); 
    }


    /**
     * Sets this JInternalFrame's layeredPane property.
     * @param layeredPane the layeredPane object for this frame
     *
     * @exception java.awt.IllegalComponentStateException (a runtime
     *            exception) if the layered pane parameter is null
     * @see RootPaneContainer#setLayeredPane
     * @beaninfo
     *     hidden: true
     *     bound: true
     *     description: The pane which holds the various desktop layers.
     */
    public void setLayeredPane(JLayeredPane layered) {
        JLayeredPane oldValue = getLayeredPane();
        getRootPane().setLayeredPane(layered);
        firePropertyChange(LAYERED_PANE_PROPERTY, oldValue, layered);   
    }

    /**
     * Returns the glassPane object for this frame.
     *
     * @see RootPaneContainer#setGlassPane
     */
    public Component getGlassPane() { 
        return getRootPane().getGlassPane(); 
    }


    /**
     * Sets this JInternalFrame's glassPane property.
     * @param glassPane the glassPane object for this frame
     * @see RootPaneContainer#getGlassPane
     * @beaninfo
     *     hidden: true
     *     description: A transparent pane used for menu rendering.
     */
    public void setGlassPane(Component glass) {
        Component oldValue = getGlassPane();
        getRootPane().setGlassPane(glass);
        firePropertyChange(GLASS_PANE_PROPERTY, oldValue, glass);       
    }

    /**
     * Returns the rootPane object for this frame.
     *
     * @see RootPaneContainer#getRootPane
     */
    public JRootPane getRootPane() { 
        return rootPane; 
    }


    /**
     * Set the rootPane property.  This method is called by the constructor.
     * @beaninfo
     *     hidden: true
     *     beaninfo: The rootPane used by this frame.
     */
    protected void setRootPane(JRootPane root) {
        if(rootPane != null) {
            remove(rootPane);
        }
        JRootPane oldValue = getRootPane();
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
        firePropertyChange(ROOT_PANE_PROPERTY, oldValue, root); 
    }


    /**
     * Set the visible state of the object.
     *
     * @param b if true, shows this object; otherwise, hides it 
     */
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    /** 
     * Set that this JInternalFrame can be closed by some user action.
     * @param b a boolean value, where true means the frame can be closed 
     * @beaninfo
     *     preferred: true
     *           bound: true
     *     description: Indicates whether this frame can be closed.
     */
    public void setClosable(boolean b) {
        Boolean oldValue = closable ? Boolean.TRUE : Boolean.FALSE; 
        Boolean newValue = b ? Boolean.TRUE : Boolean.FALSE;
        closable = b;
        firePropertyChange("closable", oldValue, newValue);
    }
 
    /** 
     * Returns whether this JInternalFrame be closed by some user action. 
     * @return true if the frame can be closed 
     */
    public boolean isClosable() {
        return closable;
    }

    /** 
     * Returns whether this JInternalFrame is currently closed. 
     * @return true if the frame is closed 
     */
    public boolean isClosed() {
        return isClosed;
    }

    /** 
     * Calling this method with a value of <code>true</code> to close
     * the frame.
     *
     * @param b a boolean, where true means "close the frame"
     * @exception PropertyVetoException when the attempt to set the 
     *            property is vetoed by the receiver.
     * @beaninfo
     *           bound: true
     *     constrained: true
     *     description: Indicates that the frame has been closed.
     */
    public void setClosed(boolean b) throws PropertyVetoException {
        if (isClosed == b) {
            return;
        }

        Boolean oldValue = isClosed ? Boolean.TRUE : Boolean.FALSE; 
        Boolean newValue = b ? Boolean.TRUE : Boolean.FALSE;
        fireVetoableChange(IS_CLOSED_PROPERTY, oldValue, newValue);
        isClosed = b;
        if (isClosed) {
            /* Dispatch a closed event to any listeners.  We can't post
             * an event since firing IS_CLOSED_PROPERTY causes this
             * frame to be removed from its parent, which causes any
             * of its events on the EventQueue to get purged.
             */
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSING);
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
	  opened = false;
        } else if (!opened) {
	    fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_OPENED);
            opened = true;
        }
        firePropertyChange(IS_CLOSED_PROPERTY, oldValue, newValue);
    }

    /** 
     * Set that the JInternalFrame can be resized by some user action.
     *
     * @param b  a boolean, where true means the frame can be resized 
     * @beaninfo
     *     preferred: true
     *           bound: true
     *     description: Determines whether the frame can be resized 
     *                  by the user.
     */
    public void setResizable(boolean b) {
        Boolean oldValue = resizable ? Boolean.TRUE : Boolean.FALSE; 
        Boolean newValue = b ? Boolean.TRUE : Boolean.FALSE;
        resizable = b;
        firePropertyChange("resizable", oldValue, newValue);
    }
 
    /** 
     * Returns whether the JInternalFrame can be resized by some user action.
     *
     * @return true if the frame can be resized
     */ 
    public boolean isResizable() {
        // don't allow resizing when maximized.
        return isMaximum ? false : resizable; 
    }

    /** 
     * Set that the JInternalFrame can be made an icon by some user action. 
     *
     * @param b  a boolean, where true means the frame can be iconified 
     * @beaninfo:
     *     preferred: true
     *     bound: true
     *     description: Determines whether this frame can be iconified.
     */
    public void setIconifiable(boolean b) {
        iconable = b;
    }
 
    /** 
     * Returns whether the JInternalFrame can be iconified by some user action.
     *
     * @return true if the frame can be iconified
     */ 
    public boolean isIconifiable() {
        return iconable; 
    }

    /** 
     * Returns whether the JInternalFrame is currently iconified.
     *
     * @return true if the frame is iconified
     */ 
    public boolean isIcon() {
        return isIcon;
    }

    /** 
     * Iconizes and deconizes the frame.
     *
     * @param b a boolean, where true means to iconify the frame and
     *          false means to deiconify it
     * @exception PropertyVetoException when the attempt to set the 
     *            property is vetoed by the receiver.
     * @beaninfo
     *           bound: true
     *     constrained: true
     *     description: The image displayed when this frame is minimized.
     */
    public void setIcon(boolean b) throws PropertyVetoException {
        if (isIcon == b) {
            return;
        }

        Boolean oldValue = isIcon ? Boolean.TRUE : Boolean.FALSE; 
        Boolean newValue = b ? Boolean.TRUE : Boolean.FALSE;
        fireVetoableChange(IS_ICON_PROPERTY, oldValue, newValue);
        isIcon = b;
        firePropertyChange(IS_ICON_PROPERTY, oldValue, newValue);
	if (b)
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_ICONIFIED);
	else
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_DEICONIFIED);
    }

    /** 
     * Set that the JInternalFrame can be maximized by some user action.
     *
     * @param b a boolean  where true means the frame can be maximized 
     * @beaninfo
     *         bound: true
     *     preferred: true
     *     description: Determines whether this frame can be maximized.
     */
    public void setMaximizable(boolean b) {
        Boolean oldValue = maximizable ? Boolean.TRUE : Boolean.FALSE; 
        Boolean newValue = b ? Boolean.TRUE : Boolean.FALSE;
        maximizable = b;
        firePropertyChange("maximizable", oldValue, newValue);
    }
 
    /** 
     * Returns whether the JInternalFrame can be maximized by some user action.
     *
     * @return true if the frame can be maximized 
     */
    public boolean isMaximizable() {
        return maximizable; 
    }

    /** 
     * Returns whether the JInternalFrame is currently maximized.
     *
     * @return true if the frame is maximized 
     */
    public boolean isMaximum() {
        return isMaximum;
    }

    /**
     * Maximizes and restores the frame.  A maximized frame is resized to
     * fully fit the JDesktopPane area associated with the JInternalFrame.
     * A restored frame's size is set to the JInternalFrame's actual size.
     *
     * @param b  a boolean, where true maximizes the frame and false
     *           restores it
     * @exception PropertyVetoException when the attempt to set the 
     *            property is vetoed by the receiver.
     * @beaninfo
     *     constrained: true
     *     description: Indicates whether the frame is maximized.
     */
    public void setMaximum(boolean b) throws PropertyVetoException {
        if (isMaximum == b) {
            return;
        }

        Boolean oldValue = isMaximum ? Boolean.TRUE : Boolean.FALSE;
        Boolean newValue = b ? Boolean.TRUE : Boolean.FALSE;
        fireVetoableChange(IS_MAXIMUM_PROPERTY, oldValue, newValue);
        isMaximum = b;
        firePropertyChange(IS_MAXIMUM_PROPERTY, oldValue, newValue);
    }

    /**
     * Returns the title of the JInternalFrame.
     *
     * @return a String containing the frame's title
     * @see #setTitle
     */
    public String getTitle() {
        return title;
    }

    /** 
     * Sets the JInternalFrame title. 
     * @see #getTitle
     *
     * @param title  the String to display in the title bar
     * @beaninfo:
     *     preferred: true
     *     bound: true
     *     description: The text displayed in the title bar.
     */
    public void setTitle(String title) {
        String oldValue = this.title;
        this.title = title;
        firePropertyChange(TITLE_PROPERTY, oldValue, title);
    }

    /**
     * Selects and deselects the JInternalFrame.
     * A JInternalFrame normally draws it's title bar differently if it is
     * the selected frame, which indicates to the user that this 
     * internalFrame has the focus.
     *
     * @param selected  a boolean, where true means the frame is selected
     *                  (currently active) and false means it is not
     * @exception PropertyVetoException when the attempt to set the 
     *            property is vetoed by the receiver.
     * @beaninfo
     *     constrained: true
     *           bound: true
     *     description: Indicates whether this frame is currently 
     *                  the active frame.
     */
    public void setSelected(boolean selected) throws PropertyVetoException {
        if (isSelected == selected) {
            return;
        }

        Boolean oldValue = isSelected ? Boolean.TRUE : Boolean.FALSE;
        Boolean newValue = selected ? Boolean.TRUE : Boolean.FALSE;
        fireVetoableChange(IS_SELECTED_PROPERTY, oldValue, newValue);
        isSelected = selected;
        firePropertyChange(IS_SELECTED_PROPERTY, oldValue, newValue);
	if (isSelected)
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_ACTIVATED);
	else
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_DEACTIVATED);	  
        repaint();
    }

    /**
     * Returns whether the JInternalFrame is the currently "selected" or
     * active frame.
     *
     * @return true if the frame is currently selected (active)
     * @see #setSelected
     */
    public boolean isSelected() {
        return isSelected;
    } 

    /** 
     * Sets an image to be displayed in the titlebar of the frame (usually
     * in the top-left corner).
     * This image is not the <code>desktopIcon</code> object, which 
     * is the image displayed in the JDesktop when the frame is iconified.
     *
     * Passing null to this function is valid, but the L&F can choose the
     * appropriate behavior for that situation, such as displaying no icon
     * or a default icon for the L&F.
     *
     * @param icon the Icon to display in the title bar
     * @see #getFrameIcon
     * @beaninfo
     *           bound: true
     *     description: The icon shown in the top-left corner of the frame.
     */
  public void setFrameIcon(Icon icon) {
        Icon oldIcon = frameIcon;
        frameIcon = icon;
        firePropertyChange(FRAME_ICON_PROPERTY, oldIcon, icon);  
    }

    /** 
     * Returns the image displayed in the title bar of the frame (usually
     * in the top-left corner).
     * 
     * @return the Icon displayed in the title bar
     * @see #setFrameIcon
     */
    public Icon getFrameIcon()  {
        return frameIcon;
    }

    /** Convenience method that moves this component to position 0 if it's 
      * parent is a JLayeredPane.
      */
    public void moveToFront() {
        if(getParent() != null && getParent() instanceof JLayeredPane) {
            JLayeredPane l =  (JLayeredPane)getParent();
            l.moveToFront(this);
        }
    }

    /** Convenience method that moves this component to position -1 if it's 
      * parent is a JLayeredPane.
      */
    public void moveToBack() {
        if(getParent() != null && getParent() instanceof JLayeredPane) {
            JLayeredPane l =  (JLayeredPane)getParent();
            l.moveToBack(this);
        }
    }

    /** 
     * Convenience method for setting the layer attribute of this component.
     *
     * @param layer  an Integer object specifying this frame's desktop layer
     * @see JLayeredPane
     * @beaninfo
     *     expert: true
     *     description: Specifies what desktop layer is used.
     */
    public void setLayer(Integer layer) {
        if(getParent() != null && getParent() instanceof JLayeredPane) {
            // Normally we want to do this, as it causes the LayeredPane
            // to draw properly.
            JLayeredPane p = (JLayeredPane)getParent();
            p.setLayer(this, layer.intValue(), p.getPosition(this));
        } else {
             // Try to do the right thing
             JLayeredPane.putLayer(this, layer.intValue());
             if(getParent() != null)
                getParent().repaint(_bounds.x, _bounds.y, 
                                    _bounds.width, _bounds.height);
        }
    }

    /** Convenience method for getting the layer attribute of this component.
     *
     * @return  an Integer object specifying this frame's desktop layer
     * @see JLayeredPane
      */
    public int getLayer() {
        return JLayeredPane.getLayer(this);
    }

    /** Convenience method that searchs the anscestor heirarchy for a 
      * JDesktop instance. If JInternalFrame finds none, the desktopIcon
      * tree is searched.
      *
      * @return the JDesktopPane this frame belongs to, or null if none
      *         is found
      */
    public JDesktopPane getDesktopPane() { 
        Container p;

        // Search upward for desktop
        p = getParent();
        while(p != null && !(p instanceof JDesktopPane))
            p = p.getParent();
        
        if(p == null) {
           // search it's icon parent for desktop
           p = getDesktopIcon().getParent();
           while(p != null && !(p instanceof JDesktopPane))
                p = p.getParent();
        }

        return (JDesktopPane)p; 
    }

    /**
     * Sets the JDesktopIcon associated with this JInternalFrame.
     *
     * @param d the JDesktopIcon to display on the desktop
     * @see #getDesktopIcon
     * @beaninfo
     *           bound: true
     *     description: The icon shown when this frame is minimized.
     */
    public void setDesktopIcon(JDesktopIcon d) { 
	JDesktopIcon oldValue = getDesktopIcon();
	desktopIcon = d; 
	firePropertyChange("desktopIcon", oldValue, d);
    }

    /** 
     * Returns the JDesktopIcon used when this JInternalFrame is iconified.
     *
     * @return the JDesktopIcon displayed on the desktop
     * @see #setDesktopIcon
     */
    public JDesktopIcon getDesktopIcon() { 
        return desktopIcon; 
    }


    /*
     * Creates a new EventDispatchThread to dispatch events from. This
     * method returns when stopModal is invoked.
     */
    synchronized void startModal() {
	/* Since all input will be blocked until this dialog is dismissed,
	 * make sure its parent containers are visible first (this component
	 * is tested below).  This is necessary for JApplets, because
	 * because an applet normally isn't made visible until after its
	 * start() method returns -- if this method is called from start(),
	 * the applet will appear to hang while an invisible modal frame
	 * waits for input.
	 */
	if (isVisible() && !isShowing()) {
	    Container parent = this.getParent();
	    while (parent != null) {
		if (parent.isVisible() == false) {
		    parent.setVisible(true);
		}
		parent = parent.getParent();
	    }
	}

        try {
            if (SwingUtilities.isEventDispatchThread()) {
                EventQueue theQueue = getToolkit().getSystemEventQueue();
                while (isVisible()) {
                    // This is essentially the body of EventDispatchThread
                    AWTEvent event = theQueue.getNextEvent();
                    Object src = event.getSource();
                    // can't call theQueue.dispatchEvent, so I pasted it's body here
                    /*if (event instanceof ActiveEvent) {
                      ((ActiveEvent) event).dispatch();
                      } else */ if (src instanceof Component) {
                          ((Component) src).dispatchEvent(event);
                      } else if (src instanceof MenuComponent) {
                          ((MenuComponent) src).dispatchEvent(event);
                      } else {
                          System.err.println("unable to dispatch event: " + event);
                      }
                }
            } else
                while (isVisible())
                    wait();
        } catch(InterruptedException e){}
    }
  
    /*
     * Stops the event dispatching loop created by a previous call to
     * <code>startModal</code>.
     */
    synchronized void stopModal() {
        notifyAll();
    }
  
    /**
     * Moves and resizes this component.  Unlike other components,
     * this implementation also forces re-layout, so that frame
     * decorations such as the title bar are always redisplayed.
     *
     * @param x  an int giving the component's new horizontal position
     *           measured in pixels from the left of its container
     * @param y  an int giving the component's new vertical position,
     *           measured in pixels from the bottom of its container
     * @param width  an int giving the component's new width in pixels
     * @param height an int giving the component's new height in pixels
     */
    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        validate();
        repaint();
    }

///////////////////////////
// Frame/Window equivalents
///////////////////////////

    /**
     * Adds the specified internal frame listener to receive internal frame events from
     * this internal frame.
     * @param l the internal frame listener
     */ 
    public void addInternalFrameListener(InternalFrameListener l) {  // remind: sync ??
      listenerList.add(InternalFrameListener.class, l);
      // remind: needed?
      enableEvents(0);   // turn on the newEventsOnly flag in Component.
    }

    /**
     * Removes the specified internal frame listener so that it no longer
     * receives internal frame events from this internal frame.
     * @param l the internal frame listener
     */ 
    public void removeInternalFrameListener(InternalFrameListener l) {  // remind: sync??
      listenerList.remove(InternalFrameListener.class, l);
    }

    // remind: name ok? all one method ok? need to be synchronized?
    protected void fireInternalFrameEvent(int id){  
      Object[] listeners = listenerList.getListenerList();
      InternalFrameEvent e = null;
      for (int i = listeners.length -2; i >=0; i -= 2){
	if (listeners[i] == InternalFrameListener.class){
	  if (e == null){ 
	    e = new InternalFrameEvent(this, id);
	    //	    System.out.println("InternalFrameEvent: " + e.paramString());
	  }
	  switch(e.getID()) {
	  case InternalFrameEvent.INTERNAL_FRAME_OPENED:
	    ((InternalFrameListener)listeners[i+1]).internalFrameOpened(e);
	    break;
	  case InternalFrameEvent.INTERNAL_FRAME_CLOSING:
	    ((InternalFrameListener)listeners[i+1]).internalFrameClosing(e);
	    break;
	  case InternalFrameEvent.INTERNAL_FRAME_CLOSED:
	    ((InternalFrameListener)listeners[i+1]).internalFrameClosed(e);
	    break;
	  case InternalFrameEvent.INTERNAL_FRAME_ICONIFIED:
	    ((InternalFrameListener)listeners[i+1]).internalFrameIconified(e);
	    break;
	  case InternalFrameEvent.INTERNAL_FRAME_DEICONIFIED:
	    ((InternalFrameListener)listeners[i+1]).internalFrameDeiconified(e);
	    break;
	  case InternalFrameEvent.INTERNAL_FRAME_ACTIVATED:
	    ((InternalFrameListener)listeners[i+1]).internalFrameActivated(e);
	    break;
	  case InternalFrameEvent.INTERNAL_FRAME_DEACTIVATED:
	    ((InternalFrameListener)listeners[i+1]).internalFrameDeactivated(e);
	    break;
	  default:
	    break;
	  }
	}
      }
      if (id == InternalFrameEvent.INTERNAL_FRAME_CLOSING) {
	  doDefaultCloseAction();
      }
    }

    private void doDefaultCloseAction() {
        switch(defaultCloseOperation) {
          case HIDE_ON_CLOSE:
              try {
                  setClosed(true);
              } catch (PropertyVetoException pve) {}
              break;
          case DISPOSE_ON_CLOSE:
              try {
                  setClosed(true);
                  dispose();  // only executes if close wasn't vetoed.
              } catch (PropertyVetoException pve) {}
              break;
	  case 3: // EXIT_ON_CLOSE:
	    System.exit(0);
	    break;
          case DO_NOTHING_ON_CLOSE:
          default: 
              break;
        }
    }

    /**
     * Sets the operation which will happen by default when
     * the user initiates a "close" on this window.
     * The possible choices are:
     * <p>
     * <ul>
     * <li>DO_NOTHING_ON_CLOSE - do not do anything - require the
     * program to handle the operation in the windowClosing
     * method of a registered InternalFrameListener object.
     * <li>HIDE_ON_CLOSE - automatically hide the window after
     * invoking any registered InternalFrameListener objects
     * <li>DISPOSE_ON_CLOSE - automatically hide and dispose the 
     * window after invoking any registered InternalFrameListener objects
     * <li>EXIT_ON_CLOSE - Exit the application by way of System.exit.
     * Only use this in applications.
     * </ul>
     * <p>
     * The value is set to HIDE_ON_CLOSE by default.
     * @see #addInternalFrameListener
     * @see #getDefaultCloseOperation
     */
    public void setDefaultCloseOperation(int operation) {
        this.defaultCloseOperation = operation;
    }

   /**
    * Returns the default operation which occurs when the user
    * initiates a "close" on this window.
    * @see #setDefaultCloseOperation
    */
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    /**
     * Causes subcomponents of this JInternalFrame to be laid out at their
     * preferred size.
     * @see       java.awt.Window#pack
     */
    public void pack() {
        Container parent = getParent();
        if (parent != null && parent.getPeer() == null) {
            parent.addNotify();
            addNotify();
        }
        setSize(getPreferredSize());
        validate();
    }

    /**
     * Shows this internal frame, and brings it to the front.
     * <p>
     * If this window is not yet visible, <code>show</code> 
     * makes it visible. If this window is already visible, 
     * then this method brings it to the front. 
     * @see       java.awt.Window#show
     * @see       java.awt.Window#toFront
     * @see       java.awt.Component#setVisible
     */
    public void show() {
        Container parent = getParent();
        if (parent != null && parent.getPeer() == null) {
            parent.addNotify();
            addNotify();
        }
        validate();

	// bug 4149505
	if (!opened) {
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_OPENED);
	  opened = true;
	}

        if (isVisible()) {
	    toFront();
	} else {
            super.show();
        }
        if (!isSelected()) {
            try {
                setSelected(true);
            } catch (PropertyVetoException pve) {}
        }
    }

    /**
     * Disposes of this internal frame. If the frame is not already
     * closed, a frame-closed event is posted.
     */
    public void dispose() {
        if (isVisible()) {
            setVisible(false);
        }
        if (isSelected()) {
            try {
                setSelected(false);
            } catch (PropertyVetoException pve) {}
        }
        if (!isClosed) {
	  firePropertyChange(IS_CLOSED_PROPERTY, Boolean.FALSE, Boolean.TRUE);
	  fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
        }
    }

    /**
     * Brings this internal frame to the front.
     * Places this internal frame  at the top of the stacking order
     * and makes the corresponding adjustment to other visible windows.
     * @see       java.awt.Window#toFront
     * @see       #moveToFront
     */
    public void toFront() {
        moveToFront();
    }

    /**
     * Sends this internal frame to the back.
     * Places this internal frame  at the bottom of the stacking order
     * and makes the corresponding adjustment to other visible windows.
     * @see       java.awt.Window#toBack
     * @see       #moveToBack
     */
    public void toBack() {
        moveToBack();
    }

    /**
     * Gets the warning string that is displayed with this window. 
     * Since an internal frame is always secure (since it's fully
     * contained within a window which might need a warning string)
     * this method always returns null.
     * @return    null
     * @see       java.awt.Window#getWarningString
     */
    public final String getWarningString() {
        return null;
    }


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    boolean old = isRootPaneCheckingEnabled();
	    try {
		setRootPaneCheckingEnabled(false);
		ui.installUI(this);
	    }
	    finally {
		setRootPaneCheckingEnabled(old);
	    }
	}
    }


    /**
     * Returns a string representation of this JInternalFrame. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JInternalFrame.
     */
    protected String paramString() {
	String rootPaneString = (rootPane != null ?
				 rootPane.toString() : "");
	String rootPaneCheckingEnabledString = (rootPaneCheckingEnabled ?
						"true" : "false");
	String closableString = (closable ? "true" : "false");
	String isClosedString = (isClosed ? "true" : "false");
	String maximizableString = (maximizable ? "true" : "false");
	String isMaximumString = (isMaximum ? "true" : "false");
	String iconableString = (iconable ? "true" : "false");
	String isIconString = (isIcon ? "true" : "false");
	String resizableString = (resizable ? "true" : "false");
	String isSelectedString = (isSelected ? "true" : "false");
	String frameIconString = (frameIcon != null ?
				  frameIcon.toString() : "");
	String titleString = (title != null ?
			      title : "");
	String desktopIconString = (desktopIcon != null ?
				    desktopIcon.toString() : "");
	String openedString = (opened ? "true" : "false");
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

	return super.paramString() +
	",closable=" + closableString +
	",defaultCloseOperation=" + defaultCloseOperationString +
	",desktopIcon=" + desktopIconString +
	",frameIcon=" + frameIconString +
	",iconable=" + iconableString +
	",isClosed=" + isClosedString +
	",isIcon=" + isIconString +
	",isMaximum=" + isMaximumString +
	",isSelected=" + isSelectedString +
	",maximizable=" + maximizableString +
	",opened=" + openedString +
	",resizable=" + resizableString +
	",rootPane=" + rootPaneString +
	",rootPaneCheckingEnabled=" + rootPaneCheckingEnabledString +
	",title=" + titleString;
    }

    // ======= begin optimized frame dragging defence code ==============

    boolean isDragging = false;
    boolean danger = false;

    protected void paintComponent(Graphics g) {
      if (isDragging) {
	//	   System.out.println("ouch");
         danger = true;
      }

      super.paintComponent(g);
   }

    // ======= end optimized frame dragging defence code ==============

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJInternalFrame();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJInternalFrame extends AccessibleJComponent 
        implements AccessibleValue {

        /**
         * Get the accessible name of this object.  This should almost never
         * return java.awt.Component.getName(), as that generally isn't
         * a localized name, and doesn't have meaning for the user.  If the
         * object is fundamentally a text object (e.g. a menu item), the
         * accessible name should be the text of the object (e.g. "save").
         * If the object has a tooltip, the tooltip text may also be an
         * appropriate String to return.
         *
         * @return the localized name of the object -- can be null if this 
         * object does not have a name
         * @see #setAccessibleName
         */
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else {
                return getTitle();
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
            return AccessibleRole.INTERNAL_FRAME;
        }

        /**
         * Get the AccessibleValue associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }


        //
        // AccessibleValue methods
        //

        /**
         * Get the value of this object as a Number.
         *
         * @return value of the object -- can be null if this object does not
         * have a value
         */
        public Number getCurrentAccessibleValue() {
            return new Integer(getLayer());
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set.
         */
        public boolean setCurrentAccessibleValue(Number n) {
            if (n instanceof Integer) {
                setLayer((Integer) n);
                return true;
            } else {
                return false;
            }
        }

        /**
         * Get the minimum value of this object as a Number.
         *
         * @return Minimum value of the object; null if this object does not
         * have a minimum value
         */
        public Number getMinimumAccessibleValue() {
            return new Integer(Integer.MIN_VALUE);
        }

        /**
         * Get the maximum value of this object as a Number.
         *
         * @return Maximum value of the object; null if this object does not
         * have a maximum value
         */
        public Number getMaximumAccessibleValue() {
            return new Integer(Integer.MAX_VALUE);
        }

    } // AccessibleJInternalFrame

    /**
     * This component represents an iconified version of a JInternalFrame.
     * This API should NOT BE USED by Swing applications, as it will go
     * away in future versions of Swing as its functionality is moved into
     * JInternalFrame.  This class is public only so that UI objects can
     * display a desktop icon.  If an application wants to display a
     * desktop icon, it should create a JInternalFrame instance and
     * iconify it.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with 
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @author David Kloba
     */
    static public class JDesktopIcon extends JComponent implements Accessible
    {
        JInternalFrame internalFrame;

        /** Create an icon for an internal frame
         * @param f  the JInternalFrame for which the icon is created
         */
        public JDesktopIcon(JInternalFrame f) {
            setInternalFrame(f);
            updateUI();
        }

        /**
         * Returns the L&F object that renders this component.
         *
         * @return the DesktopIconUI object that renders this component
         */
        public DesktopIconUI getUI() {
            return (DesktopIconUI)ui;
        }

        /**
         * Sets the L&F object that renders this component.
         *
         * @param ui  the DesktopIconUI L&F object
         * @see UIDefaults#getUI
         */
        public void setUI(DesktopIconUI ui) {
            super.setUI(ui);
        }

        /** 
         * Returns the JInternalFrame that this DesktopIcon is 
         * associated with. 
         * @return the JInternalFrame this icon is associated with 
         */
        public JInternalFrame getInternalFrame() {
            return internalFrame;
        }

        /** 
         * Sets the JInternalFrame that this DesktopIcon is 
         * associated with.
         * @param f  the JInternalFrame this icon is associated with 
         */
        public void setInternalFrame(JInternalFrame f) {
            internalFrame = f;
        }

        /** Convience method to ask the icon for the Desktop object
         * it belongs to.
         * @return the JDesktopPane that contains this icon's internal
         *         frame, or null if none found
         */
        public JDesktopPane getDesktopPane() {
            if(getInternalFrame() != null)
                return getInternalFrame().getDesktopPane();
            return null;
        }

        /**
         * Notification from the UIManager that the L&F has changed. 
         * Replaces the current UI object with the latest version from the 
         * UIManager.
         *
         * @see JComponent#updateUI
         */
        public void updateUI() {
            boolean hadUI = (ui != null);
            setUI((DesktopIconUI)UIManager.getUI(this));
            invalidate();

            Dimension r = getPreferredSize();
            setSize(r.width, r.height);
            

            if (internalFrame != null && internalFrame.getUI() != null) {  // don't do this if UI not created yet
                SwingUtilities.updateComponentTreeUI(internalFrame);
            }
        }

        /* This method is called if updateUI was called on the associated
         * JInternalFrame.  It's necessary to avoid infinite recursion.
         */
        void updateUIWhenHidden() {
            /* Update this UI and any associated internal frame */
            setUI((DesktopIconUI)UIManager.getUI(this));
            invalidate();
            Component[] children = getComponents();
            if (children != null) {
                for(int i = 0; i < children.length; i++) {
                    SwingUtilities.updateComponentTreeUI(children[i]);
                }
            }
        }

        /**
         * Returns the name of the L&F class that renders this component.
         *
         * @return "DesktopIconUI"
         * @see JComponent#getUIClassID
         * @see UIDefaults#getUI
         */
        public String getUIClassID() {
            return "DesktopIconUI";
        }

       /////////////////
       // Accessibility support
       ////////////////

        /**
         * Get the AccessibleContext associated with this JComponent
         *
         * @return the AccessibleContext of this JComponent
         */
        public AccessibleContext getAccessibleContext() {
            if (accessibleContext == null) {
                accessibleContext = new AccessibleJDesktopIcon();
            }
            return accessibleContext;
        }

        /**
         * The class used to obtain the accessible role for this object.
         * <p>
         * <strong>Warning:</strong>
         * Serialized objects of this class will not be compatible with
         * future Swing releases.  The current serialization support is appropriate
         * for short term storage or RMI between applications running the same
         * version of Swing.  A future release of Swing will provide support for
         * long term persistence.
         */
        protected class AccessibleJDesktopIcon extends AccessibleJComponent 
            implements AccessibleValue {

            /**
             * Get the role of this object.
             *
             * @return an instance of AccessibleRole describing the role of the 
             * object
             * @see AccessibleRole
             */
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.DESKTOP_ICON;
            }

            /**
             * Get the AccessibleValue associated with this object if one
             * exists.  Otherwise return null.
             */
            public AccessibleValue getAccessibleValue() {
                return this;
            }

            //
            // AccessibleValue methods
            //

            /**
             * Get the value of this object as a Number.
             *
             * @return value of the object -- can be null if this object does not
             * have a value
             */
            public Number getCurrentAccessibleValue() {
                AccessibleContext a = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
                AccessibleValue v = a.getAccessibleValue();
                if (v != null) {
                    return v.getCurrentAccessibleValue();
                } else {
                    return null;
                }
            }

            /**
             * Set the value of this object as a Number.
             *
             * @return True if the value was set.
             */
            public boolean setCurrentAccessibleValue(Number n) {
                AccessibleContext a = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
                AccessibleValue v = a.getAccessibleValue();
                if (v != null) {
                    return v.setCurrentAccessibleValue(n);
                } else {
                    return false;
                }
            }

            /**
             * Get the minimum value of this object as a Number.
             *
             * @return Minimum value of the object; null if this object does not
             * have a minimum value
             */
            public Number getMinimumAccessibleValue() {
                AccessibleContext a = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
                if (a instanceof AccessibleValue) {
                    return ((AccessibleValue)a).getMinimumAccessibleValue();
                } else {
                    return null;
                }
            }

            /**
             * Get the maximum value of this object as a Number.
             *
             * @return Maximum value of the object; null if this object does not
             * have a maximum value
             */
            public Number getMaximumAccessibleValue() {
                AccessibleContext a = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
                if (a instanceof AccessibleValue) {
                    return ((AccessibleValue)a).getMaximumAccessibleValue();
                } else {
                    return null;
                }
            }

        } // AccessibleJDesktopIcon
    }
}
