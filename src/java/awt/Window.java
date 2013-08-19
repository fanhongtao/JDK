/*
 * @(#)Window.java	1.184 03/01/28
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.applet.Applet;
import java.awt.peer.WindowPeer;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.Vector;
import java.util.Locale;
import java.util.EventListener;
import java.util.Set;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.OptionalDataException;
import java.awt.im.InputContext;
import java.util.ResourceBundle;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.accessibility.*;
import java.beans.PropertyChangeListener;
import sun.security.action.GetPropertyAction;
import sun.awt.DebugHelper;

/**
 * A <code>Window</code> object is a top-level window with no borders and no
 * menubar.  
 * The default layout for a window is <code>BorderLayout</code>.
 * <p>
 * A window must have either a frame, dialog, or another window defined as its
 * owner when it's constructed. 
 * <p>
 * In a multi-screen environment, you can create a <code>Window</code>
 * on a different screen device by constructing the <code>Window</code>
 * with {@link #Window(Window, GraphicsConfiguration)}.  The 
 * <code>GraphicsConfiguration</code> object is one of the 
 * <code>GraphicsConfiguration</code> objects of the target screen device.  
 * <p>
 * In a virtual device multi-screen environment in which the desktop 
 * area could span multiple physical screen devices, the bounds of all
 * configurations are relative to the virtual device coordinate system.  
 * The origin of the virtual-coordinate system is at the upper left-hand 
 * corner of the primary physical screen.  Depending on the location of
 * the primary screen in the virtual device, negative coordinates are 
 * possible, as shown in the following figure.
 * <p>
 * <img src="doc-files/MultiScreen.gif"
 * alt="Diagram shows virtual device containing 4 physical screens. Primary physical screen shows coords (0,0), other screen shows (-80,-100)."
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>  
 * In such an environment, when calling <code>setLocation</code>, 
 * you must pass a virtual coordinate to this method.  Similarly,
 * calling <code>getLocationOnScreen</code> on a <code>Window</code> returns 
 * virtual device coordinates.  Call the <code>getBounds</code> method 
 * of a <code>GraphicsConfiguration</code> to find its origin in the virtual
 * coordinate system.
 * <p>
 * The following code sets the location of a <code>Window</code> 
 * at (10, 10) relative to the origin of the physical screen
 * of the corresponding <code>GraphicsConfiguration</code>.  If the 
 * bounds of the <code>GraphicsConfiguration</code> is not taken 
 * into account, the <code>Window</code> location would be set 
 * at (10, 10) relative to the virtual-coordinate system and would appear
 * on the primary physical screen, which might be different from the
 * physical screen of the specified <code>GraphicsConfiguration</code>.
 *
 * <pre>
 *	Window w = new Window(Window owner, GraphicsConfiguration gc);
 *	Rectangle bounds = gc.getBounds();
 *	w.setLocation(10 + bounds.x, 10 + bounds.y);
 * </pre>
 *
 * <p>
 * Windows are capable of generating the following WindowEvents:
 * WindowOpened, WindowClosed, WindowGainedFocus, WindowLostFocus.
 *
 * @version 	1.184, 01/28/03
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @see WindowEvent
 * @see #addWindowListener
 * @see java.awt.BorderLayout
 * @since       JDK1.0
 */
public class Window extends Container implements Accessible {

    /**
     * This represents the warning message that is
     * to be displayed in a non secure window. ie :
     * a window that has a security manager installed for
     * which calling SecurityManager.checkTopLevelWindow()
     * is false.  This message can be displayed anywhere in
     * the window.
     *
     * @serial
     * @see #getWarningString
     */
    String      warningString;

    /**
     * Holds the reference to the component which last had focus in this window
     * before it lost focus.
     */
    private transient Component temporaryLostComponent;

    static boolean systemSyncLWRequests = false;
    boolean     syncLWRequests = false;
    transient boolean beforeFirstShow = true;

    static final int OPENED = 0x01;

    /**
     * An Integer value representing the Window State.
     *
     * @serial
     * @since 1.2
     * @see #show
     */
    int state;

    /**
     * A vector containing all the windows this
     * window currently owns.
     * @since 1.2
     * @see #getOwnedWindows
     */
    transient Vector ownedWindowList = new Vector();
    private transient WeakReference weakThis;

    private transient boolean showWithParent = false;
    
    transient WindowListener windowListener;
    transient WindowStateListener windowStateListener;
    transient WindowFocusListener windowFocusListener;

    transient InputContext inputContext;
    private transient Object inputContextLock = new Object();

    /**
     * Unused. Maintained for serialization backward-compatibility.
     *
     * @serial
     * @since 1.2
     */
    private FocusManager focusMgr;

    /**
     * Indicates whether this Window can become the focused Window.
     *
     * @serial
     * @see #getFocusableWindowState
     * @see #setFocusableWindowState
     * @since 1.4
     */
    private boolean focusableWindowState = true;

    private static final String base = "win";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 4497834738069338734L;

    private static final DebugHelper dbg = DebugHelper.create(Window.class);

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
 systemSyncLWRequests = ((Boolean)AccessController.doPrivileged(
     new PrivilegedAction() {
                    public Object run() {
                        return new Boolean("true".equals(System.
                                                         getProperty("java.awt.syncLWRequests",
                                                                     "false")));
                    }
                })).booleanValue();
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       accessed from C.
     */
    private static native void initIDs();

    /**
     * Constructs a new window in default size with the 
     * specified <code>GraphicsConfiguration</code>.
     * <p>
     * If there is a security manager, this method first calls 
     * the security manager's <code>checkTopLevelWindow</code> 
     * method with <code>this</code> 
     * as its argument to determine whether or not the window 
     * must be displayed with a warning banner. 
     * @param gc the <code>GraphicsConfiguration</code>
     * of the target screen device.  If <code>gc</code> is
     * <code>null</code>, the system default
     * <code>GraphicsConfiguration</code> is assumed.
     * @exception IllegalArgumentException if <code>gc</code>
     * is not from a screen device.  This exception is always
     * thrown when GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see java.lang.SecurityManager#checkTopLevelWindow 
     */
    Window(GraphicsConfiguration gc) {
        init(gc);
    }

    private void init(GraphicsConfiguration gc) {
        if (GraphicsEnvironment.isHeadless()) {
            throw new IllegalArgumentException("headless environment");
        }

        syncLWRequests = systemSyncLWRequests;

	setWarningString();
	this.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	this.visible = false;
        if (gc == null) {
            this.graphicsConfig =
                GraphicsEnvironment.getLocalGraphicsEnvironment().
             getDefaultScreenDevice().getDefaultConfiguration();
        } else {
            this.graphicsConfig = gc;
        }
        if (graphicsConfig.getDevice().getType() !=
            GraphicsDevice.TYPE_RASTER_SCREEN) {
            throw new IllegalArgumentException("not a screen device");
        }
	setLayout(new BorderLayout());

        /* offset the initial location with the original of the screen */
        /* and any insets                                              */
        Rectangle screenBounds = graphicsConfig.getBounds();
        Insets screenInsets = getToolkit().getScreenInsets(graphicsConfig);          
        int x = getX() + screenBounds.x + screenInsets.left;
        int y = getY() + screenBounds.y + screenInsets.top;
        setLocation(x, y);
    }
    
    /**
     * Constructs a new window in the default size.
     * 
     * <p>First, if there is a security manager, its 
     * <code>checkTopLevelWindow</code> 
     * method is called with <code>this</code> 
     * as its argument
     * to see if it's ok to display the window without a warning banner. 
     * If the default implementation of <code>checkTopLevelWindow</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method
     * with an <code>AWTPermission("showWindowWithoutWarningBanner")</code>
     * permission. It that method raises a SecurityException, 
     * <code>checkTopLevelWindow</code> returns false, otherwise it
     * returns true. If it returns false, a warning banner is created.
     *
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see java.lang.SecurityManager#checkTopLevelWindow
     */
    Window() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        init((GraphicsConfiguration)null);
    }
    
    /**
     * Constructs a new invisible window with the specified
     * <code>Frame</code> as its owner. The Window will not be focusable 
     * unless its owner is showing on the screen.
     * <p>
     * If there is a security manager, this method first calls 
     * the security manager's <code>checkTopLevelWindow</code> 
     * method with <code>this</code> 
     * as its argument to determine whether or not the window 
     * must be displayed with a warning banner. 
     * 
     * @param owner the <code>Frame</code> to act as owner
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if 
     *    <code>owner</code> is <code>null</code>; this exception
     *    is always thrown when <code>GraphicsEnvironment.isHeadless</code>
     *    returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see java.lang.SecurityManager#checkTopLevelWindow
     * @see #isShowing
     */
    public Window(Frame owner) {
        this(owner == null ? (GraphicsConfiguration)null :
            owner.getGraphicsConfiguration());
	ownedInit(owner);
    }

    /**
     * Constructs a new invisible window with the specified
     * <code>Window</code> as its owner. The Window will not be focusable 
     * unless its nearest owning Frame or Dialog is showing on the screen.
     * <p>
     * If there is a security manager, this method first calls 
     * the security manager's <code>checkTopLevelWindow</code> 
     * method with <code>this</code> 
     * as its argument to determine whether or not the window 
     * must be displayed with a warning banner. 
     * 
     * @param     owner   the <code>Window</code> to act as owner
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code> 
     *            is <code>null</code>.  This exception is always thrown
     *            when GraphicsEnvironment.isHeadless() returns true.
     * @see       java.awt.GraphicsEnvironment#isHeadless
     * @see       java.lang.SecurityManager#checkTopLevelWindow
     * @see       #isShowing
     * @since     1.2
     */
    public Window(Window owner) {
        this(owner == null ? (GraphicsConfiguration)null :
            owner.getGraphicsConfiguration());
	ownedInit(owner);
    }

    /**
     * Constructs a new invisible window with the specified
     * window as its owner and a 
     * <code>GraphicsConfiguration</code> of a screen device. The Window will
     * not be focusable unless its nearest owning Frame or Dialog is showing on
     * the screen.
     * <p>
     * If there is a security manager, this method first calls 
     * the security manager's <code>checkTopLevelWindow</code> 
     * method with <code>this</code> 
     * as its argument to determine whether or not the window 
     * must be displayed with a warning banner.
     *
     * @param     owner   the window to act as owner
     * @param gc the <code>GraphicsConfiguration</code>
     *   of the target screen device; if <code>gc</code> is 
     *   <code>null</code>, the system default 
     *   <code>GraphicsConfiguration</code> is assumed
     * @throws IllegalArgumentException if
     *            <code>owner</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>gc</code> is not from
     *   a screen device; this exception is always thrown when
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see       java.awt.GraphicsEnvironment#isHeadless
     * @see       java.lang.SecurityManager#checkTopLevelWindow
     * @see       GraphicsConfiguration#getBounds
     * @see       #isShowing
     * @since     1.3
     */
    public Window(Window owner, GraphicsConfiguration gc) {
        this(gc);
	ownedInit(owner);
    }

    private void ownedInit(Window owner) {
	if (owner == null) {
	    throw new IllegalArgumentException("null owner window");
	}	
	this.parent = owner;
	this.weakThis = new WeakReference(this);
	owner.addOwnedWindow(weakThis);
    }

    /**
     * Disposes of the input methods and context, and removes the WeakReference
     * which formerly pointed to this Window from the parent's owned Window
     * list.
     */
    protected void finalize() throws Throwable {
	if (parent != null) {
	    ((Window)parent).removeOwnedWindow(weakThis);
	}
	super.finalize();
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        synchronized (getClass()) {
	    return base + nameCounter++;
	}
    }

    /**
     * Makes this Window displayable by creating the connection to its
     * native screen resource.  
     * This method is called internally by the toolkit and should
     * not be called directly by programs.
     * @see Component#isDisplayable
     * @see Container#removeNotify
     * @since JDK1.0
     */
    public void addNotify() {
	synchronized (getTreeLock()) {
	    Container parent = this.parent;
	    if (parent != null && parent.getPeer() == null) {
	        parent.addNotify();
	    }

	    if (peer == null)
		peer = getToolkit().createWindow(this);
	    super.addNotify();
	}
    }

    /**
     * Causes this Window to be sized to fit the preferred size
     * and layouts of its subcomponents.  If the window and/or its owner
     * are not yet displayable, both are made displayable before
     * calculating the preferred size.  The Window will be validated
     * after the preferredSize is calculated.
     * @see Component#isDisplayable
     */
    public void pack() {
	Container parent = this.parent;
	if (parent != null && parent.getPeer() == null) {
	    parent.addNotify();
	}
	if (peer == null) {
	    addNotify();
	}
	setSize(getPreferredSize());

        if(beforeFirstShow) {
            isPacked = true;
        }

	validate();
    }

    /**
     * Makes the Window visible. If the Window and/or its owner
     * are not yet displayable, both are made displayable.  The 
     * Window will be validated prior to being made visible.  
     * If the Window is already visible, this will bring the Window 
     * to the front.
     * @see       Component#isDisplayable
     * @see       #toFront
     * @see       Component#setVisible
     */
    public void show() {
	if (peer == null) {
	    addNotify();
	}
	validate();

	if (visible) {
	    toFront();
	} else {
            beforeFirstShow = false;
	    super.show();
	    for (int i = 0; i < ownedWindowList.size(); i++) {
                Window child = (Window) (((WeakReference)
                    (ownedWindowList.elementAt(i))).get());
                        if ((child != null) && child.showWithParent) {
                            child.show();
                            child.showWithParent = false;
                        }       // endif
            }   // endfor

	}
        
        // If first time shown, generate WindowOpened event
        if ((state & OPENED) == 0) {
            postWindowEvent(WindowEvent.WINDOW_OPENED);
            state |= OPENED;
        }
    }

    synchronized void postWindowEvent(int id) {
        if (windowListener != null
            || (eventMask & AWTEvent.WINDOW_EVENT_MASK) != 0
            ||  Toolkit.enabledOnToolkit(AWTEvent.WINDOW_EVENT_MASK)) {
            WindowEvent e = new WindowEvent(this, id);
            Toolkit.getEventQueue().postEvent(e);
        }
    }

    /**
     * Hide this Window, its subcomponents, and all of its owned children. 
     * The Window and its subcomponents can be made visible again
     * with a call to <code>show</code>. 
     * </p>
     * @see #show
     * @see #dispose
     */
    public void hide() {
        synchronized(ownedWindowList) {
	    for (int i = 0; i < ownedWindowList.size(); i++) {
	        Window child = (Window) (((WeakReference)
		    (ownedWindowList.elementAt(i))).get());
		if ((child != null) && child.visible) {
                    child.hide();
                    child.showWithParent = true;
                }
	    }
	}
	super.hide();
    }

    final void clearMostRecentFocusOwnerOnHide() {
        /* do nothing */
    }
    /**
     * Releases all of the native screen resources used by this
     * <code>Window</code>, its subcomponents, and all of its owned
     * children. That is, the resources for these <code>Component</code>s
     * will be destroyed, any memory they consume will be returned to the
     * OS, and they will be marked as undisplayable.
     * <p>
     * The <code>Window</code> and its subcomponents can be made displayable
     * again by rebuilding the native resources with a subsequent call to
     * <code>pack</code> or <code>show</code>. The states of the recreated
     * <code>Window</code> and its subcomponents will be identical to the
     * states of these objects at the point where the <code>Window</code>
     * was disposed (not accounting for additional modifications between
     * those actions).
     * <p>
     * <b>Note</b>: When the last displayable window
     * within the Java virtual machine (VM) is disposed of, the VM may
     * terminate.  See <a href="doc-files/AWTThreadIssues.html">
     * AWT Threading Issues</a> for more information.
     * @see Component#isDisplayable
     * @see #pack
     * @see #show
     */
    public void dispose() {
    class DisposeAction implements Runnable {
        public void run() {
            synchronized(ownedWindowList) {
                for (int i = 0; i < ownedWindowList.size(); i++) {
                    Window child = (Window) (((WeakReference)
                                   (ownedWindowList.elementAt(i))).get());
                    if (child != null) {
                        child.dispose();
                    }
                }
            }
            setVisible(false);
            beforeFirstShow = true;
            removeNotify();
            synchronized (inputContextLock) {
                if (inputContext != null) {
                    inputContext.dispose();
                    inputContext = null;
                }
            }
            clearCurrentFocusCycleRootOnHide();
        }
    }

	DisposeAction action = new DisposeAction();
	if (EventQueue.isDispatchThread()) {
	    action.run();
	}
	else {
	    try {
	        EventQueue.invokeAndWait(action);
	    }
	    catch (InterruptedException e) {
	        System.err.println("Disposal was interrupted:");
		e.printStackTrace();
	    }
	    catch (InvocationTargetException e) {
	        System.err.println("Exception during disposal:");
		e.printStackTrace();
	    }
	}
	// Execute outside the Runnable because postWindowEvent is
	// synchronized on (this). We don't need to synchronize the call
	// on the EventQueue anyways.
	postWindowEvent(WindowEvent.WINDOW_CLOSED);
    }

    /*
     * Should only be called while holding the tree lock.
     * It's overridden here because parent == owner in Window,
     * and we shouldn't adjust counter on owner
     */
    void adjustListeningChildrenOnParent(long mask, int num) {
    }

    // Should only be called while holding tree lock
    void adjustDecendantsOnParent(int num) {
        // do nothing since parent == owner and we shouldn't 
        // ajust counter on owner
    }

    /**
     * If this Window is visible, brings this Window to the front and may make
     * it the focused Window.
     * <p>
     * Places this Window at the top of the stacking order and shows it in
     * front of any other Windows in this VM. No action will take place if this
     * Window is not visible. Some platforms do not allow Windows which own
     * other Windows to appear on top of those owned Windows. Some platforms
     * may not permit this VM to place its Windows above windows of native
     * applications, or Windows of other VMs. This permission may depend on
     * whether a Window in this VM is already focused. Every attempt will be
     * made to move this Window as high as possible in the stacking order;
     * however, developers should not assume that this method will move this
     * Window above all other windows in every situation.
     * <p>
     * Because of variations in native windowing systems, no guarantees about
     * changes to the focused and active Windows can be made. Developers must
     * never assume that this Window is the focused or active Window until this
     * Window receives a WINDOW_GAINED_FOCUS or WINDOW_ACTIVATED event. On
     * platforms where the top-most window is the focused window, this method
     * will <b>probably</b> focus this Window, if it is not already focused. On
     * platforms where the stacking order does not typically affect the focused
     * window, this method will <b>probably</b> leave the focused and active
     * Windows unchanged.
     * <p>
     * If this method causes this Window to be focused, and this Window is a
     * Frame or a Dialog, it will also become activated. If this Window is
     * focused, but it is not a Frame or a Dialog, then the first Frame or
     * Dialog that is an owner of this Window will be activated.
     *
     * @see       #toBack
     */
    public void toFront() {
        if (visible) {
	    WindowPeer peer = (WindowPeer)this.peer;
	    if (peer != null) {
	        peer.toFront();
	    }
	}
    }

    /**
     * If this Window is visible, sends this Window to the back and may cause
     * it to lose focus or activation if it is the focused or active Window.
     * <p>
     * Places this Window at the bottom of the stacking order and shows it
     * behind any other Windows in this VM. No action will take place is this
     * Window is not visible. Some platforms do not allow Windows which are
     * owned by other Windows to appear below their owners. Every attempt will
     * be made to move this Window as low as possible in the stacking order;
     * however, developers should not assume that this method will move this
     * Window below all other windows in every situation.
     * <p>
     * Because of variations in native windowing systems, no guarantees about
     * changes to the focused and active Windows can be made. Developers must
     * never assume that this Window is no longer the focused or active Window
     * until this Window receives a WINDOW_LOST_FOCUS or WINDOW_DEACTIVATED
     * event. On platforms where the top-most window is the focused window,
     * this method will <b>probably</b> cause this Window to lose focus. In
     * that case, the next highest, focusable Window in this VM will receive
     * focus. On platforms where the stacking order does not typically affect
     * the focused window, this method will <b>probably</b> leave the focused
     * and active Windows unchanged.
     *
     * @see       #toFront
     */
    public void toBack() {
        if (visible) {
	    WindowPeer peer = (WindowPeer)this.peer;
	    if (peer != null) {
	        peer.toBack();
	    }
	}
    }

    /**
     * Returns the toolkit of this frame.
     * @return    the toolkit of this window.
     * @see       Toolkit
     * @see       Toolkit#getDefaultToolkit
     * @see       Component#getToolkit
     */
    public Toolkit getToolkit() {
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Gets the warning string that is displayed with this window. 
     * If this window is insecure, the warning string is displayed 
     * somewhere in the visible area of the window. A window is 
     * insecure if there is a security manager, and the security 
     * manager's <code>checkTopLevelWindow</code> method returns 
     * <code>false</code> when this window is passed to it as an
     * argument.
     * <p>
     * If the window is secure, then <code>getWarningString</code>
     * returns <code>null</code>. If the window is insecure, this
     * method checks for the system property 
     * <code>awt.appletWarning</code> 
     * and returns the string value of that property. 
     * @return    the warning string for this window.
     * @see       java.lang.SecurityManager#checkTopLevelWindow(java.lang.Object)
     */
    public final String getWarningString() {
	return warningString;
    }

    private void setWarningString() {
	warningString = null;
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    if (!sm.checkTopLevelWindow(this)) {
		// make sure the privileged action is only
		// for getting the property! We don't want the
		// above checkTopLevelWindow call to always succeed!
		warningString = (String) AccessController.doPrivileged(
		      new GetPropertyAction("awt.appletWarning",
					    "Java Applet Window"));
	    }
	}
    }

    /** 
     * Gets the <code>Locale</code> object that is associated 
     * with this window, if the locale has been set.
     * If no locale has been set, then the default locale 
     * is returned.
     * @return    the locale that is set for this window.
     * @see       java.util.Locale
     * @since     JDK1.1
     */

    public Locale getLocale() {
      if (this.locale == null) {
	return Locale.getDefault();
      }
      return this.locale;
    }

    /**
     * Gets the input context for this window. A window always has an input context,
     * which is shared by subcomponents unless they create and set their own.
     * @see Component#getInputContext
     * @since 1.2
     */

    public InputContext getInputContext() {
        if (inputContext == null) {
            synchronized (inputContextLock) {
                if (inputContext == null) {
                    inputContext = InputContext.getInstance();
                }
            }
        }
        return inputContext;
    }

    /**
     * Set the cursor image to a specified cursor.
     * @param     cursor One of the constants defined
     *            by the <code>Cursor</code> class. If this parameter is null
     *            then the cursor for this window will be set to the type
     *            Cursor.DEFAULT_CURSOR.
     * @see       Component#getCursor
     * @see       Cursor
     * @since     JDK1.1
     */
    public void setCursor(Cursor cursor) {
        if (cursor == null) {
            cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }
        super.setCursor(cursor);
    }

    /**
     * Returns the owner of this window.
     * @since 1.2
     */
    public Window getOwner() {
        return (Window)parent;
    }

    /**
     * Return an array containing all the windows this
     * window currently owns.
     * @since 1.2
     */
    public Window[] getOwnedWindows() {
        Window realCopy[];

	synchronized(ownedWindowList) {
	    // Recall that ownedWindowList is actually a Vector of
	    // WeakReferences and calling get() on one of these references
	    // may return null. Make two arrays-- one the size of the
	    // Vector (fullCopy with size fullSize), and one the size of 
	    // all non-null get()s (realCopy with size realSize).
	    int fullSize = ownedWindowList.size();
	    int realSize = 0;
	    Window fullCopy[] = new Window[fullSize];

	    for (int i = 0; i < fullSize; i++) {
	        fullCopy[realSize] = (Window) (((WeakReference)
		    (ownedWindowList.elementAt(i))).get());

		if (fullCopy[realSize] != null) {
		    realSize++;
		}
	    }

	    if (fullSize != realSize) {
	        realCopy = new Window[realSize];
		System.arraycopy(fullCopy, 0, realCopy, 0, realSize);
	    } else {
	        realCopy = fullCopy;
	    }
	}

        return realCopy;
    }

    /**
     * Adds the specified window listener to receive window events from
     * this window.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param 	l the window listener
     * @see #removeWindowListener
     * @see #getWindowListeners
     */ 
    public synchronized void addWindowListener(WindowListener l) {
	if (l == null) {
	    return;
	}
        newEventsOnly = true;
        windowListener = AWTEventMulticaster.add(windowListener, l);
    }

    /**
     * Adds the specified window state listener to receive window
     * events from this window.  If <code>l</code> is </code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param   l the window state listener
     * @see #removeWindowStateListener
     * @see #getWindowStateListeners
     * @since 1.4
     */
    public synchronized void addWindowStateListener(WindowStateListener l) {
        if (l == null) {
            return;
        }
        windowStateListener = AWTEventMulticaster.add(windowStateListener, l);
        newEventsOnly = true;
    }

    /**
     * Adds the specified window focus listener to receive window events
     * from this window.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param   l the window focus listener
     * @see #removeWindowFocusListener
     * @see #getWindowFocusListeners
     */
    public synchronized void addWindowFocusListener(WindowFocusListener l) {
        if (l == null) {
            return;
        }
        windowFocusListener = AWTEventMulticaster.add(windowFocusListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified window listener so that it no longer
     * receives window events from this window.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param 	l the window listener
     * @see #addWindowListener
     * @see #getWindowListeners
     */ 
    public synchronized void removeWindowListener(WindowListener l) {
	if (l == null) {
	    return;
	}
        windowListener = AWTEventMulticaster.remove(windowListener, l);
    }

    /**
     * Removes the specified window state listener so that it no
     * longer receives window events from this window.  If
     * <code>l</code> is <code>null</code>, no exception is thrown and
     * no action is performed.
     *
     * @param   l the window state listener
     * @see #addWindowStateListener
     * @see #getWindowStateListeners
     * @since 1.4
     */
    public synchronized void removeWindowStateListener(WindowStateListener l) {
        if (l == null) {
            return;
        }
        windowStateListener = AWTEventMulticaster.remove(windowStateListener, l);
    }

    /**
     * Removes the specified window focus listener so that it no longer
     * receives window events from this window.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param   l the window focus listener
     * @see #addWindowFocusListener
     * @see #getWindowFocusListeners
     */
    public synchronized void removeWindowFocusListener(WindowFocusListener l) {
        if (l == null) {
            return;
        }
        windowFocusListener = AWTEventMulticaster.remove(windowFocusListener, l);
    }

    /**
     * Returns an array of all the window listeners
     * registered on this window.
     *
     * @return all of this window's <code>WindowListener</code>s
     *         or an empty array if no window
     *         listeners are currently registered
     *
     * @see #addWindowListener
     * @see #removeWindowListener
     * @since 1.4
     */
    public synchronized WindowListener[] getWindowListeners() {
        return (WindowListener[])(getListeners(WindowListener.class));
    }

    /**
     * Returns an array of all the window focus listeners
     * registered on this window.
     *
     * @return all of this window's <code>WindowFocusListener</code>s
     *         or an empty array if no window focus
     *         listeners are currently registered
     *
     * @see #addWindowFocusListener
     * @see #removeWindowFocusListener
     * @since 1.4
     */
    public synchronized WindowFocusListener[] getWindowFocusListeners() {
        return (WindowFocusListener[])(getListeners(WindowFocusListener.class));
    }

    /**
     * Returns an array of all the window state listeners
     * registered on this window.
     *
     * @return all of this window's <code>WindowStateListener</code>s
     *         or an empty array if no window state
     *         listeners are currently registered
     *
     * @see #addWindowStateListener
     * @see #removeWindowStateListener
     * @since 1.4
     */
    public synchronized WindowStateListener[] getWindowStateListeners() {
        return (WindowStateListener[])(getListeners(WindowStateListener.class));
    }


    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>Window</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>Window</code> <code>w</code>
     * for its window listeners with the following code:
     *
     * <pre>WindowListener[] wls = (WindowListener[])(w.getListeners(WindowListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this window,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getWindowListeners
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	EventListener l = null; 
        if (listenerType == WindowFocusListener.class) {
            l = windowFocusListener;
	} else if (listenerType == WindowStateListener.class) {
            l = windowStateListener;
        } else if (listenerType == WindowListener.class) {
            l = windowListener;
        } else {
            return super.getListeners(listenerType);
        }
        return (EventListener[])AWTEventMulticaster.getListeners(l, listenerType);
    }

    // REMIND: remove when filtering is handled at lower level
    boolean eventEnabled(AWTEvent e) {
        switch(e.id) {
          case WindowEvent.WINDOW_OPENED:
          case WindowEvent.WINDOW_CLOSING:
          case WindowEvent.WINDOW_CLOSED:
          case WindowEvent.WINDOW_ICONIFIED:
          case WindowEvent.WINDOW_DEICONIFIED:
          case WindowEvent.WINDOW_ACTIVATED:
          case WindowEvent.WINDOW_DEACTIVATED:
            if ((eventMask & AWTEvent.WINDOW_EVENT_MASK) != 0 ||
                windowListener != null) {
                return true;
            }
            return false;
          case WindowEvent.WINDOW_GAINED_FOCUS:
          case WindowEvent.WINDOW_LOST_FOCUS:
            if ((eventMask & AWTEvent.WINDOW_FOCUS_EVENT_MASK) != 0 ||
                windowFocusListener != null) {
                return true;
            }
            return false;
          case WindowEvent.WINDOW_STATE_CHANGED:
            if ((eventMask & AWTEvent.WINDOW_STATE_EVENT_MASK) != 0 ||
                windowStateListener != null) {
                return true;
            }
            return false;
          default:
            break;
        }
        return super.eventEnabled(e);
    }

    /**
     * Processes events on this window. If the event is an
     * <code>WindowEvent</code>, it invokes the
     * <code>processWindowEvent</code> method, else it invokes its
     * superclass's <code>processEvent</code>.
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof WindowEvent) {
            switch (e.getID()) {
                case WindowEvent.WINDOW_OPENED:
                case WindowEvent.WINDOW_CLOSING:
                case WindowEvent.WINDOW_CLOSED:
                case WindowEvent.WINDOW_ICONIFIED:
                case WindowEvent.WINDOW_DEICONIFIED:
                case WindowEvent.WINDOW_ACTIVATED:
                case WindowEvent.WINDOW_DEACTIVATED:
                    processWindowEvent((WindowEvent)e);
                    break;
                case WindowEvent.WINDOW_GAINED_FOCUS:
                case WindowEvent.WINDOW_LOST_FOCUS:
                    processWindowFocusEvent((WindowEvent)e);
                    break;
                case WindowEvent.WINDOW_STATE_CHANGED:
		    processWindowStateEvent((WindowEvent)e);
                default:
                    break;
            }
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes window events occurring on this window by
     * dispatching them to any registered WindowListener objects.
     * NOTE: This method will not be called unless window events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * <ul>
     * <li>A WindowListener object is registered via
     *     <code>addWindowListener</code>
     * <li>Window events are enabled via <code>enableEvents</code>
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param e the window event
     * @see Component#enableEvents
     */  
    protected void processWindowEvent(WindowEvent e) {
        WindowListener listener = windowListener;
        if (listener != null) {
            switch(e.getID()) {
                case WindowEvent.WINDOW_OPENED:
                    listener.windowOpened(e);
                    break;
                case WindowEvent.WINDOW_CLOSING:
                    listener.windowClosing(e);
                    break;
                case WindowEvent.WINDOW_CLOSED:
                    listener.windowClosed(e);
                    break;
                case WindowEvent.WINDOW_ICONIFIED:
                    listener.windowIconified(e);
                    break;
                case WindowEvent.WINDOW_DEICONIFIED:
                    listener.windowDeiconified(e);
                    break;
                case WindowEvent.WINDOW_ACTIVATED:
                    listener.windowActivated(e);
                    break;
                case WindowEvent.WINDOW_DEACTIVATED:
                    listener.windowDeactivated(e);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Processes window focus event occuring on this window by
     * dispatching them to any registered WindowFocusListener objects.
     * NOTE: this method will not be called unless window focus events
     * are enabled for this window. This happens when one of the
     * following occurs:
     * <ul>
     * <li>a WindowFocusListener is registered via
     *     <code>addWindowFocusListener</code>
     * <li>Window focus events are enabled via <code>enableEvents</code>
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param e the window focus event
     * @see Component#enableEvents
     */
    protected void processWindowFocusEvent(WindowEvent e) {
        WindowFocusListener listener = windowFocusListener;
        if (listener != null) {
            switch (e.getID()) {
                case WindowEvent.WINDOW_GAINED_FOCUS:
                    listener.windowGainedFocus(e);
                    break;
                case WindowEvent.WINDOW_LOST_FOCUS:
                    listener.windowLostFocus(e);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Processes window state event occuring on this window by
     * dispatching them to any registered <code>WindowStateListener</code>
     * objects.
     * NOTE: this method will not be called unless window state events
     * are enabled for this window.  This happens when one of the
     * following occurs:
     * <ul>
     * <li>a <code>WindowStateListener</code> is registered via
     *    <code>addWindowStateListener</code>
     * <li>window state events are enabled via <code>enableEvents</code>
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param e the window state event
     * @see java.awt.Component#enableEvents
     * @since 1.4
     */
    protected void processWindowStateEvent(WindowEvent e) {
        WindowStateListener listener = windowStateListener;
	if (listener != null) {
	    switch (e.getID()) {
		case WindowEvent.WINDOW_STATE_CHANGED:
		    listener.windowStateChanged(e);
		    break;
		default:
		    break;
	    }
	}
    }

    /**
     * Implements a debugging hook -- checks to see if
     * the user has typed <i>control-shift-F1</i>.  If so,
     * the list of child windows is dumped to <code>System.out</code>.
     * @param e  the keyboard event
     */
    void preProcessKeyEvent(KeyEvent e) {
        // Dump the list of child windows to System.out.
        if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1 &&
            e.isControlDown() && e.isShiftDown() && 
            e.getID() == KeyEvent.KEY_PRESSED) {
            list(System.out, 0);
        }
    }

    void postProcessKeyEvent(KeyEvent e) {
	// Do nothing
    }
  
    /**
     * Returns the child Component of this Window that has focus if this Window
     * is focused; returns null otherwise.
     *
     * @return the child Component with focus, or null if this Window is not
     *         focused
     * @see #getMostRecentFocusOwner
     * @see #isFocused
     */
    public Component getFocusOwner() {
	return (isFocused())
	    ? KeyboardFocusManager.getCurrentKeyboardFocusManager().
	          getFocusOwner()
	    : null;
    }
 
    /**
     * Returns the child Component of this Window that will receive the focus
     * when this Window is focused. If this Window is currently focused, this
     * method returns the same Component as <code>getFocusOwner()</code>. If
     * this Window is not focused, then the child Component that most recently 
     * requested focus will be returned. If no child Component has ever
     * requested focus, and this is a focusable Window, then this Window's
     * initial focusable Component is returned. If no child Component has ever
     * requested focus, and this is a non-focusable Window, null is returned.
     *
     * @return the child Component that will receive focus when this Window is
     *         focused
     * @see #getFocusOwner
     * @see #isFocused
     * @see #isFocusableWindow
     * @since 1.4
     */
    public Component getMostRecentFocusOwner() {
	if (isFocused()) {
	    return getFocusOwner();
	} else {
	    Component mostRecent =
		KeyboardFocusManager.getMostRecentFocusOwner(this);
	    if (mostRecent != null) {
		return mostRecent;
	    } else {
		return (isFocusableWindow())
		    ? getFocusTraversalPolicy().getInitialComponent(this)
		    : null;
	    }
        }
    }
  
    /**
     * Returns whether this Window is active. Only a Frame or a Dialog may be
     * active. The native windowing system may denote the active Window or its
     * children with special decorations, such as a highlighted title bar. The
     * active Window is always either the focused Window, or the first Frame or
     * Dialog that is an owner of the focused Window.
     *
     * @return whether this is the active Window.
     * @see #isFocused
     * @since 1.4
     */
    public boolean isActive() {
	return (KeyboardFocusManager.getCurrentKeyboardFocusManager().
		getActiveWindow() == this);
    }
  
    /**
     * Returns whether this Window is focused. If there exists a focus owner,
     * the focused Window is the Window that is, or contains, that focus owner.
     * If there is no focus owner, then no Window is focused.
     * <p>
     * If the focused Window is a Frame or a Dialog it is also the active
     * Window. Otherwise, the active Window is the first Frame or Dialog that
     * is an owner of the focused Window.
     *
     * @return whether this is the focused Window.
     * @see #isActive
     * @since 1.4
     */
    public boolean isFocused() {
	return (KeyboardFocusManager.getCurrentKeyboardFocusManager().
		getGlobalFocusedWindow() == this);
    }
  
    /**
     * Gets a focus traversal key for this Window. (See <code>
     * setFocusTraversalKeys</code> for a full description of each key.)
     * <p>
     * If the traversal key has not been explicitly set for this Window,
     * then this Window's parent's traversal key is returned. If the
     * traversal key has not been explicitly set for any of this Window's
     * ancestors, then the current KeyboardFocusManager's default traversal key
     * is returned.
     *
     * @param id one of KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *         KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @return the AWTKeyStroke for the specified key
     * @see Container#setFocusTraversalKeys
     * @see KeyboardFocusManager#FORWARD_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#BACKWARD_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#UP_CYCLE_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#DOWN_CYCLE_TRAVERSAL_KEYS
     * @throws IllegalArgumentException if id is not one of
     *         KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *         KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @since 1.4
     */
    public Set getFocusTraversalKeys(int id) {
	if (id < 0 || id >= KeyboardFocusManager.TRAVERSAL_KEY_LENGTH) {
	    throw new IllegalArgumentException("invalid focus traversal key identifier");
	}
  
        // Okay to return Set directly because it is an unmodifiable view
        Set keystrokes = (focusTraversalKeys != null)
            ? focusTraversalKeys[id]
            : null;

	if (keystrokes != null) {
	    return keystrokes;
	} else {
	    return KeyboardFocusManager.getCurrentKeyboardFocusManager().
		getDefaultFocusTraversalKeys(id);
	}
    }
  
    /**
     * Does nothing because Windows must always be roots of a focus traversal
     * cycle. The passed-in value is ignored.
     *
     * @param focusCycleRoot this value is ignored
     * @see #isFocusCycleRoot
     * @see Container#setFocusTraversalPolicy
     * @see Container#getFocusTraversalPolicy
     * @since 1.4
     */
    public final void setFocusCycleRoot(boolean focusCycleRoot) {
    }
  
    /**
     * Always returns <code>true</code> because all Windows must be roots of a
     * focus traversal cycle.
     *
     * @return <code>true</code>
     * @see #setFocusCycleRoot
     * @see Container#setFocusTraversalPolicy
     * @see Container#getFocusTraversalPolicy
     * @since 1.4
     */
    public final boolean isFocusCycleRoot() {
	return true;
    }
  
    /**
     * Always returns <code>null</code> because Windows have no ancestors; they
     * represent the top of the Component hierarchy.
     *
     * @return <code>null</code>
     * @see Container#isFocusCycleRoot()
     * @since 1.4
     */
    public final Container getFocusCycleRootAncestor() {
	return null;
    }
 
    /**
     * Returns whether this Window can become the focused Window, that is,
     * whether this Window or any of its subcomponents can become the focus
     * owner. For a Frame or Dialog to be focusable, its focusable Window state
     * must be set to <code>true</code>. For a Window which is not a Frame or
     * Dialog to be focusable, its focusable Window state must be set to
     * <code>true</code>, its nearest owning Frame or Dialog must be
     * showing on the screen, and it must contain at least one Component in
     * its focus traversal cycle. If any of these conditions is not met, then
     * neither this Window nor any of its subcomponents can become the focus
     * owner.
     *
     * @return <code>true</code> if this Window can be the focused Window;
     *         <code>false</code> otherwise
     * @see #getFocusableWindowState
     * @see #setFocusableWindowState
     * @see #isShowing
     * @see Component#isFocusable
     * @since 1.4
     */
    public final boolean isFocusableWindow() {
        // If a Window/Frame/Dialog was made non-focusable, then it is always
        // non-focusable.
        if (!getFocusableWindowState()) {
            return false;
        }

        // All other tests apply only to Windows.
        if (this instanceof Frame || this instanceof Dialog) {
            return true;
        }

        // A Window must have at least one Component in its root focus
        // traversal cycle to be focusable.
        if (getFocusTraversalPolicy().getDefaultComponent(this) == null) {
            return false;
        }

        // A Window's nearest owning Frame or Dialog must be showing on the
        // screen.
        for (Window owner = getOwner(); owner != null;
             owner = owner.getOwner())
        {
            if (owner instanceof Frame || owner instanceof Dialog) {
                return owner.isShowing();
            }
        }

        return false;
    }

    /**
     * Returns whether this Window can become the focused Window if it meets
     * the other requirements outlined in <code>isFocusableWindow</code>. If
     * this method returns <code>false</code>, then
     * <code>isFocusableWindow</code> will return <code>false</code> as well.
     * If this method returns <code>true</code>, then
     * <code>isFocusableWindow</code> may return <code>true</code> or
     * <code>false</code> depending upon the other requirements which must be
     * met in order for a Window to be focusable.
     * <p>
     * By default, all Windows have a focusable Window state of
     * <code>true</code>.
     *
     * @return whether this Window can be the focused Window
     * @see #isFocusableWindow
     * @see #setFocusableWindowState
     * @see #isShowing
     * @see Component#setFocusable
     * @since 1.4
     */
    public boolean getFocusableWindowState() {
        return focusableWindowState;
    }

    /**
     * Sets whether this Window can become the focused Window if it meets
     * the other requirements outlined in <code>isFocusableWindow</code>. If
     * this Window's focusable Window state is set to <code>false</code>, then
     * <code>isFocusableWindow</code> will return <code>false</code>. If this
     * Window's focusable Window state is set to <code>true</code>, then
     * <code>isFocusableWindow</code> may return <code>true</code> or
     * <code>false</code> depending upon the other requirements which must be
     * met in order for a Window to be focusable.
     * <p>
     * Setting a Window's focusability state to <code>false</code> is the
     * standard mechanism for an application to identify to the AWT a Window
     * which will be used as a floating palette or toolbar, and thus should be
     * a non-focusable Window.
     *
     * @param focusableWindowState whether this Window can be the focused
     *        Window
     * @see #isFocusableWindow
     * @see #getFocusableWindowState
     * @see #isShowing
     * @see Component#setFocusable
     * @since 1.4
     */
    public void setFocusableWindowState(boolean focusableWindowState) {
	boolean oldFocusableWindowState;
	synchronized (this) {
	    oldFocusableWindowState = this.focusableWindowState;
	    this.focusableWindowState = focusableWindowState;
	}
	firePropertyChange("focusableWindowState", oldFocusableWindowState,
			   focusableWindowState);
	if (oldFocusableWindowState && !focusableWindowState && isFocused()) {
	    for (Window owner = (Window)getParent();
		 owner != null;
		 owner = (Window)owner.getParent())
		{
		    Component toFocus =
			KeyboardFocusManager.getMostRecentFocusOwner(owner);
		    if (toFocus != null && toFocus.requestFocus(false)) {
			return;
		    }
		}
	    KeyboardFocusManager.getCurrentKeyboardFocusManager().
		clearGlobalFocusOwner();
	}
    }
 
    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     *    <li>this Window's font ("font")</li>
     *    <li>this Window's background color ("background")</li>
     *    <li>this Window's foreground color ("foreground")</li>
     *    <li>this Window's focusability ("focusable")</li>
     *    <li>this Window's focus traversal keys enabled state
     *        ("focusTraversalKeysEnabled")</li>
     *    <li>this Window's Set of FORWARD_TRAVERSAL_KEYS
     *        ("forwardFocusTraversalKeys")</li>
     *    <li>this Window's Set of BACKWARD_TRAVERSAL_KEYS
     *        ("backwardFocusTraversalKeys")</li>
     *    <li>this Window's Set of UP_CYCLE_TRAVERSAL_KEYS
     *        ("upCycleFocusTraversalKeys")</li>
     *    <li>this Window's Set of DOWN_CYCLE_TRAVERSAL_KEYS
     *        ("downCycleFocusTraversalKeys")</li>
     *    <li>this Window's focus traversal policy ("focusTraversalPolicy")
     *        </li>
     *    <li>this Window's focusable Window state ("focusableWindowState")
     *        </li>
     * </ul>
     * Note that if this Window is inheriting a bound property, then no
     * event will be fired in response to a change in the inherited property.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param    listener  the PropertyChangeListener to be added
     *
     * @see Component#removePropertyChangeListener
     * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	super.addPropertyChangeListener(listener);
    }
 
    /**
     * Adds a PropertyChangeListener to the listener list for a specific
     * property. The specified property may be user-defined, or one of the
     * following:
     * <ul>
     *    <li>this Window's font ("font")</li>
     *    <li>this Window's background color ("background")</li>
     *    <li>this Window's foreground color ("foreground")</li>
     *    <li>this Window's focusability ("focusable")</li>
     *    <li>this Window's focus traversal keys enabled state
     *        ("focusTraversalKeysEnabled")</li>
     *    <li>this Window's Set of FORWARD_TRAVERSAL_KEYS
     *        ("forwardFocusTraversalKeys")</li>
     *    <li>this Window's Set of BACKWARD_TRAVERSAL_KEYS
     *        ("backwardFocusTraversalKeys")</li>
     *    <li>this Window's Set of UP_CYCLE_TRAVERSAL_KEYS
     *        ("upCycleFocusTraversalKeys")</li>
     *    <li>this Window's Set of DOWN_CYCLE_TRAVERSAL_KEYS
     *        ("downCycleFocusTraversalKeys")</li>
     *    <li>this Window's focus traversal policy ("focusTraversalPolicy")
     *        </li>
     *    <li>this Window's focusable Window state ("focusableWindowState")
     *        </li>
     * </ul>
     * Note that if this Window is inheriting a bound property, then no
     * event will be fired in response to a change in the inherited property.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param propertyName one of the property names listed above
     * @param listener the PropertyChangeListener to be added
     *
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @see Component#removePropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName,
					  PropertyChangeListener listener) {
	super.addPropertyChangeListener(propertyName, listener);
    }
 
    /**
     * Dispatches an event to this window or one of its sub components.
     * @param e the event
     */
    void dispatchEventImpl(AWTEvent e) {
	if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
	    invalidate();
	    validate();
	}
	super.dispatchEventImpl(e);
    }
  
    /**
     * @deprecated As of JDK version 1.1
     * replaced by <code>dispatchEvent(AWTEvent)</code>.
     */
    public boolean postEvent(Event e) {
        if (handleEvent(e)) {
            e.consume();
            return true;
        }
        return false;
    }

    /**
     * Checks if this Window is showing on screen.
     * @see Component#setVisible
    */
    public boolean isShowing() {
	return visible;
    }

    /**
     * Applies the ResourceBundle's ComponentOrientation
     * to this Window and all components contained within it.
     *
     * @see ComponentOrientation
     * @since 1.2
     *
     * @deprecated As of J2SE 1.4, replaced by
     * {@link Component#applyComponentOrientation Component.applyComponentOrientation}.
     */
    public void applyResourceBundle(ResourceBundle rb) {
        applyComponentOrientation(ComponentOrientation.getOrientation(rb));
    }
    
    /**
     * Loads the ResourceBundle with the given name using the default locale
     * and applies its ComponentOrientation
     * to this Window and all components contained within it.
     *
     * @see ComponentOrientation
     * @since 1.2
     *
     * @deprecated As of J2SE 1.4, replaced by
     * {@link Component#applyComponentOrientation Component.applyComponentOrientation}.
     */
    public void applyResourceBundle(String rbName) {
        applyResourceBundle(ResourceBundle.getBundle(rbName));
    }

   /* 
    * Support for tracking all windows owned by this window
    */
    void addOwnedWindow(WeakReference weakWindow) {
        if (weakWindow != null) {
	    synchronized(ownedWindowList) {
	        // this if statement should really be an assert, but we don't
	        // have asserts...
	        if (!ownedWindowList.contains(weakWindow)) {
		    ownedWindowList.addElement(weakWindow);
		}
	    }
	}
    }

    void removeOwnedWindow(WeakReference weakWindow) {
        if (weakWindow != null) {
	    // synchronized block not required since removeElement is
	    // already synchronized
	    ownedWindowList.removeElement(weakWindow);
	}
    }

    void connectOwnedWindow(Window child) {
        WeakReference weakChild = new WeakReference(child);
	child.weakThis = weakChild;
	child.parent = this;
	addOwnedWindow(weakChild);
    }
    
    /**
     * The window serialized data version.
     *
     * @serial
     */
    private int windowSerializedDataVersion = 2;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable <code>WindowListener</code>s and
     * <code>WindowFocusListener</code>s as optional data.
     * Writes a list of child windows as optional data.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @serialData <code>null</code> terminated sequence of
     *    0 or more pairs; the pair consists of a <code>String</code>
     *    and and <code>Object</code>; the <code>String</code>
     *    indicates the type of object and is one of the following:
     *    <code>windowListenerK</code> indicating a
     *      <code>WindowListener</code> object;
     *    <code>windowFocusWindowK</code> indicating a
     *      <code>WindowFocusListener</code> object;
     *    <code>ownedWindowK</code> indicating a child
     *      <code>Window</code> object
     *
     * @see AWTEventMulticaster#save(java.io.ObjectOutputStream, java.lang.String, java.util.EventListener)
     * @see Component#windowListenerK
     * @see Component#windowFocusListenerK
     * @see Component#ownedWindowK
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        synchronized (this) {
	    // Update old focusMgr fields so that our object stream can be read
	    // by previous releases
	    focusMgr = new FocusManager();
	    focusMgr.focusRoot = this;
	    focusMgr.focusOwner = getMostRecentFocusOwner();

	    s.defaultWriteObject();

	    // Clear fields so that we don't keep extra references around
	    focusMgr = null;

	    AWTEventMulticaster.save(s, windowListenerK, windowListener);
            AWTEventMulticaster.save(s, windowFocusListenerK, windowFocusListener);
            AWTEventMulticaster.save(s, windowStateListenerK, windowStateListener);
	}

	s.writeObject(null);

	synchronized (ownedWindowList) {
	    for (int i = 0; i < ownedWindowList.size(); i++) {
	        Window child = (Window) (((WeakReference)
	            (ownedWindowList.elementAt(i))).get());
		if (child != null) {
		    s.writeObject(ownedWindowK);
		    s.writeObject(child);
		}
	    }
	}
	s.writeObject(null);
    }

    /**
     * Reads the <code>ObjectInputStream</code> and an optional
     * list of listeners to receive various events fired by
     * the component; also reads a list of
     * (possibly <code>null</code>) child windows.
     * Unrecognized keys or values will be ignored.
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #writeObject
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException
    {
      GraphicsEnvironment.checkHeadless();
      s.defaultReadObject();

      if (windowSerializedDataVersion < 2) {
	  // Translate old-style focus tracking to new model. For 1.4 and
	  // later releases, we'll rely on the Window's initial focusable
	  // Component.
	  if (focusMgr != null) {
	      if (focusMgr.focusOwner != null) {
		  KeyboardFocusManager.
		      setMostRecentFocusOwner(this, focusMgr.focusOwner);
	      }
	  }
  
	  // This field is non-transient and relies on default serialization.
	  // However, the default value is insufficient, so we need to set
	  // it explicitly for object data streams prior to 1.4.
        focusableWindowState = true;
      }
  
      // 1.4 doesn't use this field, so just null it out.
      focusMgr = null;
  
      ownedWindowList = new Vector();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	  String key = ((String)keyOrNull).intern();

          if (windowListenerK == key) {
	      addWindowListener((WindowListener)(s.readObject()));
          } else if (windowFocusListenerK == key) {
              addWindowFocusListener((WindowFocusListener)(s.readObject()));
          } else if (windowStateListenerK == key) {
              addWindowStateListener((WindowStateListener)(s.readObject()));
	  } else // skip value for unrecognized key
	      s.readObject();
      }

      try {
	  while (null != (keyOrNull = s.readObject())) {
	      String key = ((String)keyOrNull).intern();

	      if (ownedWindowK == key)
		  connectOwnedWindow((Window) s.readObject());

	      else // skip value for unrecognized key
		  s.readObject();
	  }
      }
      catch (OptionalDataException e) {
	  // 1.1 serialized form
	  // ownedWindowList will be updated by Frame.readObject
      }

      setWarningString();
      inputContextLock = new Object();

      // Deserialized Windows are not yet visible.
      visible = false;
    }

    /*
     * --- Accessibility Support ---
     *
     */

    /**
     * Gets the AccessibleContext associated with this Window. 
     * For windows, the AccessibleContext takes the form of an 
     * AccessibleAWTWindow. 
     * A new AccessibleAWTWindow instance is created if necessary.
     *
     * @return an AccessibleAWTWindow that serves as the 
     *         AccessibleContext of this Window
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTWindow();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>Window</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to window user-interface elements.
     */
    protected class AccessibleAWTWindow extends AccessibleAWTContainer {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see javax.accessibility.AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.WINDOW;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see javax.accessibility.AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (getFocusOwner() != null) {
                states.add(AccessibleState.ACTIVE);
            }
            return states;
        }

    } // inner class AccessibleAWTWindow

    /**
     * This method returns the GraphicsConfiguration used by this Window.
     */
    public GraphicsConfiguration getGraphicsConfiguration() {
		//NOTE: for multiscreen, this will need to take into account
		//which screen the window is on/mostly on instead of returning the
		//default or constructor argument config.
        synchronized(getTreeLock()) {
            if (graphicsConfig == null  && !GraphicsEnvironment.isHeadless()) {
                graphicsConfig =
                    GraphicsEnvironment. getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().
                    getDefaultConfiguration();
            }
            return graphicsConfig;
	    }
    }

    /**
     * Reset this Window's GraphicsConfiguration to match its peer.
     */
    void resetGC() {
        if (!GraphicsEnvironment.isHeadless()) {
            // use the peer's GC 
            setGCFromPeer();
            // if it's still null, use the default
            if (graphicsConfig == null) {
                graphicsConfig = GraphicsEnvironment.
                    getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().
                    getDefaultConfiguration();
            }
            if (dbg.on) {
                dbg.println("+ Window.resetGC(): new GC is \n+ " + graphicsConfig + "\n+ this is " + this);
            }
        }
    }

    /**
     * Sets the location of the window relative to the specified
     * component. If the component is not currently showing,
     * or <code>c</code> is <code>null</code>, the 
     * window is centered on the screen.  If the bottom of the
     * component is offscreen, the window is placed to the
     * side of the <code>Component</code> that is closest
     * to the center of the screen.  So if the 
     * <code>Component</code> is on the right part of the
     * screen, the <code>Window</code> is placed to its left,
     * and visa versa.
     *
     * @param c  the component in relation to which the window's location
     *           is determined
     * @since 1.4
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


            Rectangle  windowBounds = getBounds();
            int        dx = invokerScreenLocation.x+((invokerSize.width-windowBounds.width)>>1);
            int        dy = invokerScreenLocation.y+((invokerSize.height - windowBounds.height)>>1);
            Dimension  ss = getToolkit().getScreenSize();

            if (dy+windowBounds.height>ss.height) {
                dy = ss.height-windowBounds.height;
                dx = invokerScreenLocation.x<(ss.width>>1) ? invokerScreenLocation.x+invokerSize.width :
                    invokerScreenLocation.x-windowBounds.width;
            }
            if (dx+windowBounds.width>ss.width) dx = ss.width-windowBounds.width;
            if (dx<0) dx = 0;
            if (dy<0) dy = 0;
            setLocation(dx, dy);
        }
    }
    
    /**
     * Overridden from Component.  Top-level Windows should not propagate a
     * MouseWheelEvent beyond themselves into their owning Windows.
     */
    void deliverMouseWheelToAncestor(MouseWheelEvent e) {}

    /**
     * Overridden from Component.  Top-level Windows don't dispatch to ancestors
     */
    boolean dispatchMouseWheelToAncestor(MouseWheelEvent e) {return false;}

    /**
     * Creates a new strategy for multi-buffering on this component.
     * Multi-buffering is useful for rendering performance.  This method
     * attempts to create the best strategy available with the number of
     * buffers supplied.  It will always create a <code>BufferStrategy</code>
     * with that number of buffers.
     * A page-flipping strategy is attempted first, then a blitting strategy
     * using accelerated buffers.  Finally, an unaccelerated blitting
     * strategy is used.
     * <p>
     * Each time this method is called,
     * the existing buffer strategy for this component is discarded.
     * @param numBuffers number of buffers to create
     * @exception IllegalArgumentException if numBuffers is less than 1.
     * @exception IllegalStateException if the component is not displayable
     * @see #isDisplayable
     * @see #getBufferStrategy
     * @since 1.4
     */
    public void createBufferStrategy(int numBuffers) {
        super.createBufferStrategy(numBuffers);
    }
    
    /**
     * Creates a new strategy for multi-buffering on this component with the
     * required buffer capabilities.  This is useful, for example, if only
     * accelerated memory or page flipping is desired (as specified by the
     * buffer capabilities).
     * <p>
     * Each time this method
     * is called, the existing buffer strategy for this component is discarded.
     * @param numBuffers number of buffers to create, including the front buffer
     * @param caps the required capabilities for creating the buffer strategy;
     * cannot be <code>null</code>
     * @exception AWTException if the capabilities supplied could not be
     * supported or met; this may happen, for example, if there is not enough
     * accelerated memory currently available, or if page flipping is specified
     * but not possible.
     * @exception IllegalArgumentException if numBuffers is less than 1, or if
     * caps is <code>null</code>
     * @see #getBufferStrategy
     * @since 1.4
     */
    public void createBufferStrategy(int numBuffers,
        BufferCapabilities caps) throws AWTException {
        super.createBufferStrategy(numBuffers, caps);
    }
    
    /**
     * @return the buffer strategy used by this component
     * @see #createBufferStrategy
     * @since 1.4
     */
    public BufferStrategy getBufferStrategy() {
        return super.getBufferStrategy();
    }

    Component getTemporaryLostComponent() {
        return temporaryLostComponent;
    }
    Component setTemporaryLostComponent(Component component) {
        Component previousComp = temporaryLostComponent;
        // Check that "component" is an acceptable focus owner and don't store it otherwise 
        // - or later we will have problems with opposite while handling  WINDOW_GAINED_FOCUS
        if (component == null 
            || (component.isDisplayable() && component.isVisible() && component.isEnabled() && component.isFocusable()))
        {
            temporaryLostComponent = component;
        } else {
            temporaryLostComponent = null;
        }
        return previousComp;
    }
} // class Window


/**
 * This class is no longer used, but is maintained for Serialization
 * backward-compatibility.
 */
class FocusManager implements java.io.Serializable {
    Container focusRoot;
    Component focusOwner;

    /*
     * JDK 1.1 serialVersionUID 
     */
    static final long serialVersionUID = 2491878825643557906L;
}
