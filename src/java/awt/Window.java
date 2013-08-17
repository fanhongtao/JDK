/*
 * @(#)Window.java	1.116 01/01/23
 *
 * Copyright 1995-2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */
package java.awt;

import java.awt.peer.WindowPeer;
import java.awt.event.*;
import java.util.Vector;
import java.util.Locale;
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
import sun.security.action.GetPropertyAction;

/**
 * A <code>Window</code> object is a top-level window with no borders and no
 * menubar.  
 * The default layout for a window is <code>BorderLayout</code>.
 * <p>
 * A window must have either a frame, dialog, or another window defined as its
 * owner when it's constructed. 
 * <p>
 * Windows are capable of generating the following window events:
 * WindowOpened, WindowClosed.
 *
 * @version 	1.110, 12/15/98
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @see WindowEvent
 * @see #addWindowListener
 * @see java.awt.BorderLayout
 * @since       JDK1.0
 */
public class Window extends Container {

    /**
     * This represents the warning message that is
     * to be displayed in a non secure window. ie :
     * a window that has a security manager installed for
     * which calling SecurityManager.checkTopLevelWindow()
     * is false.  This message can be displayed anywhere in
     * the window.
     *
     * @serial
     * @see getWarningString()
     */
    String      warningString;
	boolean		nativeActive;

    static final int OPENED = 0x01;

    /**
     * An Integer value representing the Window State.
     *
     * @serial
     * @since JDK1.2
     * @see show()
     */
    int state;

    /**
     * A vector containing all the windows this
     * window currently owns.
     * @since JDK1.2
     * @see getOwnedWindows()
     */
    transient Vector ownedWindowList = new Vector();
    private transient WeakReference weakThis;

    transient WindowListener windowListener;
    private transient boolean active = false;   // == true when Window receives WINDOW_ACTIVATED event
                                                // == false when Window receives WINDOW_DEACTIVATED event
    
    transient InputContext inputContext;

    /**
     * The Focus for the Window in question, and its components.
     *
     * @serial
     * @since JDK1.2
     * @See java.awt.FocusManager
     */
    private FocusManager focusMgr;

    private static final String base = "win";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 4497834738069338734L;


    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	initIDs();
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       accessed from C.
     */
    private static native void initIDs();

    /**
     * Constructs a new window.
     * 
     * <p>First, if there is a security manager, its 
     * <code>checkTopLevelWindow</code> 
     * method is called with <code>this</code> 
     * as its argument
     * to see if it's ok to display the window without a warning banner. 
     * If the default implementation of <code>checkTopLevelWindow</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method with an
     * <code>AWTPermission("showWindowWithoutWarningBanner")<code>
     * permission. It that method raises a SecurityException, 
     * <code>checkTopLevelWindow</code> returns false, otherwise it
     * returns true. If it returns false, a warning banner is created.
     *
     * @see java.lang.SecurityManager#checkTopLevelWindow
     */
    Window() {
	setWarningString();
	this.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	this.focusMgr = new FocusManager(this);
	this.visible = false;
	this.inputContext = InputContext.getInstance();
    }

    /**
     * Constructs a new invisible window.
     * <p>
     * The window is not initially visible. Call the <code>show</code> 
     * method to cause the window to become visible.
     * <p>
     * If there is a security manager, this method first calls 
     * the security manager's <code>checkTopLevelWindow</code> 
     * method with <code>this</code> 
     * as its argument to determine whether or not the window 
     * must be displayed with a warning banner. 
     * 
     * @param     owner   the main application frame.
     * @exception java.lang.IllegalArgumentException if <code>owner</code> 
     *            is <code>null</code>
     * @see       java.awt.Window#show
     * @see       java.awt.Component#setSize
     * @see       java.lang.SecurityManager#checkTopLevelWindow
     */
    public Window(Frame owner) {
	this();
	ownedInit(owner);
    }

    /**
     * Constructs a new invisible window with the specified
     * window as its owner.
     * <p>
     * If there is a security manager, this method first calls 
     * the security manager's <code>checkTopLevelWindow</code> 
     * method with <code>this</code> 
     * as its argument to determine whether or not the window 
     * must be displayed with a warning banner. 
     * 
     * @param     owner   the window to act as owner
     * @exception java.lang.IllegalArgumentException if <code>owner</code> 
     *            is <code>null</code>
     * @see       java.lang.SecurityManager#checkTopLevelWindow
     * @since     JDK1.2
     */
    public Window(Window owner) {
	this();
	ownedInit(owner);
    }

    private void ownedInit(Window owner) {
	if (owner == null) {
	    throw new IllegalArgumentException("null owner window");
	}	
	this.parent = owner;
	this.weakThis = new WeakReference(this);
	owner.addOwnedWindow(weakThis);
	setLayout(new BorderLayout());
    }

    /**
     * Disposes of the input methods and context, and removes the WeakReference
     * which formerly pointed to this Window from the parent's owned Window
     * list.
     */
    protected void finalize() throws Throwable {
        inputContext.dispose();
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
	isPacked = true;

	validate();
    }

    /**
     * Makes the Window visible. If the Window and/or its owner
     * are not yet displayable, both are made displayable.  The 
     * Window will be validated prior to being made visible.  
     * If the Window is already visible, this will bring the Window 
     * to the front.
     * @see       java.awt.Component#isDisplayable
     * @see       java.awt.Window#toFront
     * @see       java.awt.Component#setVisible
     */
    public void show() {
	if (peer == null) {
	    addNotify();
	}
	validate();

	if (visible) {
	    toFront();
	} else {
	    super.show();
	}
        
        // If first time shown, generate WindowOpened event
        if ((state & OPENED) == 0) {
            postWindowEvent(WindowEvent.WINDOW_OPENED);
            state |= OPENED;
        }
    }

    synchronized void postWindowEvent(int id) {
        if (windowListener != null || 
            (eventMask & AWTEvent.WINDOW_EVENT_MASK) != 0) {
            WindowEvent e = new WindowEvent(this, id);
            Toolkit.getEventQueue().postEvent(e);
        }
    }
        
    /**
     * Hide this Window, its subcomponents, and all of its owned children. 
     * The Window and its subcomponents can be made visible again
     * with a call to <code>show</code>. 
     * </p>
     * @see Window#show
     * @see Window#dispose
     */
    public void hide() {
        synchronized(ownedWindowList) {
	    for (int i = 0; i < ownedWindowList.size(); i++) {
	        Window child = (Window) (((WeakReference)
		    (ownedWindowList.elementAt(i))).get());
		if (child != null) {
		    child.hide();
		}
	    }
	}
	super.hide();
    }

    /**
     * Releases all of the native screen resources used by this Window, 
     * its subcomponents, and all of its owned children. That is, the 
     * resources for these Components will be destroyed, any memory 
     * they consume will be returned to the OS, and they will be marked 
     * as undisplayable.
     * <p>
     * The Window and its subcomponents can be made displayable again
     * by rebuilding the native resources with a subsequent call to
     * <code>pack</code> or <code>show</code>. The states of the recreated
     * Window and its subcomponents will be identical to the states of these
     * objects at the point where the Window was disposed (not accounting for
     * additional modifcations between those actions).
     * </p>
     * @see Component#isDisplayable
     * @see Window#pack
     * @see Window#show
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
		hide();
		removeNotify();
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

    /**
     * Brings this window to the front.
     * Places this window at the top of the stacking order and
     * shows it in front of any other windows.
     * @see       java.awt.Window#toBack
     */
    public void toFront() {
    	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toFront();
	}
    }

    /**
     * Sends this window to the back.
     * Places this window at the bottom of the stacking order and
     * makes the corresponding adjustment to other visible windows.
     * @see       java.awt.Window#toFront
     */
    public void toBack() {
    	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toBack();
	}
    }

    /**
     * Returns the toolkit of this frame.
     * @return    the toolkit of this window.
     * @see       java.awt.Toolkit
     * @see       java.awt.Toolkit#getDefaultToolkit()
     * @see       java.awt.Component#getToolkit()
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
					    "Warning: Applet Window"));
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
     * @since JDK1.2
     */

    public InputContext getInputContext() {
        return inputContext;
    }

    /**
     * Set the cursor image to a specified cursor.
     * @param <code>cursor</code> One of the constants defined
     *            by the <code>Cursor</code> class. If this parameter is null
     *            then the cursor for this window will be set to the type
     *            Cursor.DEFAULT_CURSOR.
     * @see       java.awt.Component#getCursor
     * @see       java.awt.Cursor
     * @since     JDK1.1
     */
    public synchronized void setCursor(Cursor cursor) {
        if (cursor == null) {
            cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }
        super.setCursor(cursor);
    }

    /**
     * Returns the owner of this window.
     */
    public Window getOwner() {
        return (Window)parent;
    }

    /**
     * Return an array containing all the windows this
     * window currently owns.
     * @since JDK1.2
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
	        realCopy = new Frame[realSize];
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
     */ 
    public synchronized void addWindowListener(WindowListener l) {
	if (l == null) {
	    return;
	}
        windowListener = AWTEventMulticaster.add(windowListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified window listener so that it no longer
     * receives window events from this window.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param 	l the window listener
     */ 
    public synchronized void removeWindowListener(WindowListener l) {
	if (l == null) {
	    return;
	}
        windowListener = AWTEventMulticaster.remove(windowListener, l);
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
          default:
            break;
        }
        return super.eventEnabled(e);
    }

    boolean isActive() {
	return active;
    }

    /**
     * Processes events on this window. If the event is an WindowEvent,
     * it invokes the processWindowEvent method, else it invokes its
     * superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof WindowEvent) {
            processWindowEvent((WindowEvent)e);
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
     * a) A WindowListener object is registered via addWindowListener()
     * b) Window events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the window event
     */  
    protected void processWindowEvent(WindowEvent e) {
        if (windowListener != null) {
            switch(e.getID()) {
              case WindowEvent.WINDOW_OPENED:
                windowListener.windowOpened(e);
                break;
              case WindowEvent.WINDOW_CLOSING:
                windowListener.windowClosing(e);
                break;
              case WindowEvent.WINDOW_CLOSED:
                windowListener.windowClosed(e);
                break;
              case WindowEvent.WINDOW_ICONIFIED:
                windowListener.windowIconified(e);
                break;
              case WindowEvent.WINDOW_DEICONIFIED:
                windowListener.windowDeiconified(e);
                break;
              case WindowEvent.WINDOW_ACTIVATED:
                windowListener.windowActivated(e);
                break;
              case WindowEvent.WINDOW_DEACTIVATED:
                windowListener.windowDeactivated(e);
                break;
              default:
                break;
            }
        }
    }

    void preProcessKeyEvent(KeyEvent e) {
        // Dump the list of child windows to System.out.
        if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1 &&
            e.isControlDown() && e.isShiftDown() && 
            e.getID() == KeyEvent.KEY_PRESSED) {
            list(System.out, 0);
        }
    }

    void postProcessKeyEvent(KeyEvent e) {
    	WindowPeer	peer = (WindowPeer)this.peer;
        if (peer == null)
            return;

  	switch(peer.handleFocusTraversalEvent(e)) {
	case WindowPeer.IGNORE_EVENT:
	default:
	    break;
	case WindowPeer.CONSUME_EVENT:
	    e.consume();
	    break;
	case WindowPeer.FOCUS_NEXT:
	    if (focusMgr.focusNext())
		e.consume();
	    break;
	case WindowPeer.FOCUS_PREVIOUS:
	    if (focusMgr.focusPrevious())
		e.consume();
	    break;
	}

	return;
    }

    void setFocusOwner(Component c) {
	focusMgr.setFocusOwner(c);
    }

    void transferFocus(Component base) {
	nextFocus(base);
    }

    /**
     * Returns the child component of this Window which has focus if and 
     * only if this Window is active.
     * @return the component with focus, or null if no children have focus
     * assigned to them.
     */
    public Component getFocusOwner() {
        if (active || nativeActive)
            return focusMgr.getFocusOwner();
        else
            return null;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>transferFocus(Component)</code>.
     */
    void nextFocus(Component base) {
	focusMgr.focusNext(base);
    }

    /*
     * Dispatches an event to this window or one of its sub components.
     * @param e the event
     */
    void dispatchEventImpl(AWTEvent e) {
        switch(e.getID()) {
          case FocusEvent.FOCUS_GAINED:
            setFocusOwner(this);
            break;
          case ComponentEvent.COMPONENT_RESIZED:
            invalidate();
            validate();
            repaint();
            break;
          case WindowEvent.WINDOW_ACTIVATED:
            active = true;
/*
  Calling this messes up focus on Solaris

            focusMgr.activateFocus();
*/
            break;
          case WindowEvent.WINDOW_DEACTIVATED:
            active = false;
            break;
          default:
            break;
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
     * @see java.awt.Component#setVisible(boolean)
    */
    public boolean isShowing() {
	return visible;
    }

    /**
     * Apply the settings in the given ResourceBundle to this Window.
     * Currently, this applies the ResourceBundle's ComponentOrientation
     * to this Window and all components contained within it.
     *
     * @see java.awt.ComponentOrientation
     * @since JDK1.2
     */
    public void applyResourceBundle(ResourceBundle rb) {
        // A package-visible utility on Container does all the work
        applyOrientation(ComponentOrientation.getOrientation(rb));
    }
    
    /**
     * Load the ResourceBundle with the given name using the default locale
     * and apply its settings to this window.
     * Currently, this applies the ResourceBundle's ComponentOrientation
     * to this Window and all components contained within it.
     *
     * @see java.awt.ComponentOrientation
     * @since JDK1.2
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
    private int windowSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable ItemListener(s) as optional data.
     * The non-serializable ItemListener(s) are detected and
     * no attempt is made to serialize them. Write a list of
     * child Windows as optional data.
     *
     * @serialData Null terminated sequence of 0 or more pairs.
     *             The pair consists of a String and Object.
     *             The String indicates the type of object and
     *             is one of the following :
     *             itemListenerK indicating an ItemListener object.
     * @serialData Null terminated sequence of 0 or more pairs.
     *             The pair consists of a String and Object.
     *             The String indicates the type of object and
     *             is one of the following :
     *             ownedWindowK indicating a child Window object.
     *
     * @see AWTEventMulticaster.save(ObjectOutputStream, String, EventListener)
     * @see java.awt.Component.itemListenerK
     * @see java.awt.Component.ownedWindowK
     */
    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, windowListenerK, windowListener);
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
     * Read the default ObjectInputStream, a possibly null listener to
     * receive item events fired by the Window, and a possibly null
     * list of child Windows.
     * Unrecognised keys or values will be Ignored.
     *
     * @see removeActionListener()
     * @see addActionListener()
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();
      ownedWindowList = new Vector();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	  String key = ((String)keyOrNull).intern();

	  if (windowListenerK == key) 
	      addWindowListener((WindowListener)(s.readObject()));

	  else // skip value for unrecognized key
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
      this.inputContext = InputContext.getInstance();
    }

} // class Window


class FocusManager implements java.io.Serializable {
    Container focusRoot;
    Component focusOwner;

    /*
     * JDK 1.1 serialVersionUID 
     */
    static final long serialVersionUID = 2491878825643557906L;

    FocusManager(Container cont) {
	focusRoot = cont;
    }

    /* Re-activate the last component with focus if it is still
     * visible and active.
     * If no component had focus yet, assign it to first component
     * capable of receiving it (visible, active, focusable).
     * If no visible, active, focusable components are present,
     * assign focus to the focus root.
     */
    void activateFocus() {
        boolean assigned = false;
        if (focusOwner != null) {
            if ((assigned = assignFocus(focusOwner, false)) != true) {
                assigned = focusNext(focusOwner);
            }
        } else {
            // assign to first component capable of taking it
            assigned = focusForward(focusRoot);
        }
        if (!assigned) {
            focusRoot.requestFocus(); 
        }
    }                
                
     
    synchronized void setFocusOwner(Component c) {
        //System.out.println("FocusManager.setFocusOwner: "+c.name);
        focusOwner = c;
    }

    Component getFocusOwner() {
       return focusOwner;
    }
	
    boolean focusNext() {
       return focusNext(focusOwner);
    }

    boolean focusNext(Component base) {
        synchronized (focusRoot.getTreeLock()) { // BUGID4067845
            Component target = base;
            if (target != null && target.parent != null) {
                //System.out.println("FocusManager.focusNext: owner="+focusOwner);
                do {
                    boolean found = false;
                    Container p = target.parent;
                    Component c;
                    for (int i = 0; i < p.ncomponents; i++) {
                        c = p.component[i];
                        if (found) {
                            if (assignFocus(c)) {
                                return true;
                            }
                            if (c instanceof Container && 
                        			c.isVisible() && 
                        			c.isEnabled()) {
                                if (focusForward((Container)c)) {
                                    return true;
                                }
                            } 	    
                        } else if (c == target) {
                            found = true;	
                        }
                    } 
                    target = p;
                } while (target != focusRoot && target.parent != null);
    		}
            // wrap-around
            if (focusForward(focusRoot)) {
                return true;
            }
    
            return false;		
        }
    }

    boolean focusPrevious() {
	return focusPrevious(focusOwner);
    }
    
    boolean focusPrevious(Component base) {
        synchronized (focusRoot.getTreeLock()) { // BUGID4067845
            Component target = base;
            if (target != null && target.parent != null) {
                do {
                    boolean found = false;
                    Container p = target.parent;
		    if (p != null) {
			Component c;
			for (int i = p.ncomponents-1; i >= 0; i--) {
			    c = p.component[i];
			    if (found) {
				if (assignFocus(c)) {
				    return true;
				}
				if (c instanceof Container && 
				    c.isVisible() && 
				    c.isEnabled()) {
				    if (focusBackward((Container)c)) {
					return true;
				    }
				} 	    
			    } else if (c == target) {
				found = true;	
			    }
			} 
		    }
                    target = p;
                } while (target != null && target != focusRoot);
	     
            }
	    // wrap-around
            if (focusBackward(focusRoot)) {
                return true;
            }
            return false;		
        }
    }

    boolean assignFocus(Component c) {
        return assignFocus(c, true);
    }

    synchronized boolean assignFocus(Component c, boolean requireTraversable) {
        if (c.isVisible() && c.isEnabled() &&
            (!requireTraversable || c.isFocusTraversable())) {
            //System.out.println("FocusManager.assignFocus: "+c);
            c.requestFocus();
            return true;
        }
        return false;
    }

    synchronized boolean focusForward(Container cont) {
        for (int i = 0; i < cont.ncomponents; i++) {
            Component c = cont.component[i];
            if (assignFocus(c)) {
                return true;
            }
            if (c instanceof Container && c.isVisible() && c.isEnabled()) {
                if (focusForward((Container)c)) {
                    return true;
                }
            } 
        }
        return false;
    }

    synchronized boolean focusBackward(Container cont) {
        for (int i = cont.ncomponents-1; i >= 0; i--) {
            Component c = cont.component[i];
            if (assignFocus(c)) {
                return true;
            }
            if (c instanceof Container && c.isVisible() && c.isEnabled()) {
                if (focusBackward((Container)c)) {
                    return true;
                }
            } 
        }
        return false;
    }

}
