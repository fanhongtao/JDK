/*
 * @(#)Window.java	1.78 98/10/06
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
import sun.awt.im.InputContext;


/**
 * A <code>Window</code> object is a top-level window with no borders and no
 * menubar. It could be used to implement a pop-up menu.
 * The default layout for a window is <code>BorderLayout</code>.
 * A <code>Window</code> object blocks input to other application 
 * windows when it is shown.
 * <p>
 * Windows are capable of generating the following window events:
 * WindowOpened, WindowClosed.
 *
 * @version 	1.78, 10/06/98
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @see WindowEvent
 * @see #addWindowListener
 * @see java.awt.BorderLayout
 * @since       JDK1.0
 */
public class Window extends Container {
    String      warningString;

    static final int OPENED = 0x01;
    int state;
    transient WindowListener windowListener;
    private transient boolean active;
    
    transient InputContext inputContext;

    private FocusManager focusMgr;

    private static final String base = "win";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 4497834738069338734L;

    Window() {
	setWarningString();
	this.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	this.focusMgr = new FocusManager(this);
	this.visible = false;
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Constructs a new invisible window.
     * <p>
     * The window is not initially visible. Call the <code>show</code> 
     * method to cause the window to become visible.
     * @param     parent   the main application frame.
     * @see       java.awt.Window#show
     * @see       java.awt.Component#setSize
     * @since     JDK1.0
     */
    public Window(Frame parent) {
	this();
	if (parent == null) {
	    throw new IllegalArgumentException("null parent frame");
	}	
	this.parent = parent;
	parent.addOwnedWindow(this);
	setLayout(new BorderLayout());
    }

    /**
     * Creates the Window's peer.  The peer allows us to modify the
     * appearance of the Window without changing its functionality.
     */
    public void addNotify() {
      synchronized (getTreeLock()) {
	if (peer == null) {
	    peer = getToolkit().createWindow(this);
	}
	super.addNotify();
      }
    }

    /**
     * Causes subcomponents of this window to be laid out at their
     * preferred size.
     * @since     JDK1.0
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
	validate();
    }

    /**
     * Shows this window, and brings it to the front.
     * <p>
     * If this window is not yet visible, <code>show</code> 
     * makes it visible. If this window is already visible, 
     * then this method brings it to the front. 
     * @see       java.awt.Window#toFront
     * @see       java.awt.Component#setVisible
     * @since     JDK1.0
     */
    public void show() {
    	Container parent = this.parent;
	if (parent != null && parent.getPeer() == null) {
	    parent.addNotify();
	}
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
     * Disposes of this window. This method must
     * be called to release the resources that
     * are used for the window.
     * @since JDK1.0
     */
    public void dispose() {
      synchronized (getTreeLock()) {
        if (inputContext != null) {
            InputContext toDispose = inputContext;
            inputContext = null;
            toDispose.dispose();
        }
	hide();
	removeNotify();
 	if (parent != null) {
	    Frame parent = (Frame) this.parent;
	    parent.removeOwnedWindow(this);
 	} 
        postWindowEvent(WindowEvent.WINDOW_CLOSED);
      }
    }

    /**
     * Brings this window to the front.
     * Places this window at the top of the stacking order and
     * shows it in front of any other windows.
     * @see       java.awt.Window#toBack
     * @since     JDK1.0
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
     * @since     JDK1.0
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
     * @since     JDK1.0
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
     * @since     JDK1.0
     */
    public final String getWarningString() {
	return warningString;
    }

    private void setWarningString() {
	warningString = null;
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    if (!sm.checkTopLevelWindow(this)) {
		warningString = System.getProperty("awt.appletWarning", 
						   "Warning: Applet Window");
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
     */

    synchronized InputContext getInputContext() {
 
        if (inputContext == null) {
            inputContext = InputContext.getInstance();
        }

        return inputContext;
    }

    /**
     * Set the cursor image to a predefined cursor.
     * @param <code>cursor</code> One of the constants defined 
     *            by the <code>Cursor</code> class. If this parameter is null 
     *            then the cursor for this window will be set to the type 
     *            Cursor.DEFAULT_CURSOR .
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
     * Adds the specified window listener to receive window events from
     * this window.
     * @param l the window listener
     */ 
    public synchronized void addWindowListener(WindowListener l) {
        windowListener = AWTEventMulticaster.add(windowListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified window listener so that it no longer
     * receives window events from this window.
     * @param l the window listener
     */ 
    public synchronized void removeWindowListener(WindowListener l) {
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

    /* Handle TAB and Shift-TAB events. */
    private boolean handleTabEvent(KeyEvent e) {
        if (e.getKeyCode() != '\t' || (e.getSource() instanceof TextArea)) {
            return false;
        }
	if ((e.getModifiers() & ~InputEvent.SHIFT_MASK) > 0) {
	    return false;
	}
        int id = e.getID();
	if (id == KeyEvent.KEY_RELEASED || id == KeyEvent.KEY_TYPED) {
	    return true;
	}
	if (e.isShiftDown()) {
	    return focusMgr.focusPrevious();
	} else {
	    return focusMgr.focusNext();
	}
    }

    void preProcessKeyEvent(KeyEvent e) {
        // Dump the list of child windows to System.out.
        if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1 &&
            e.isControlDown() && e.isShiftDown()) {
            list(System.out, 0);
        }
    }

    void postProcessKeyEvent(KeyEvent e) {
        if (handleTabEvent(e)) {
            e.consume();
            return;
        }
    }

    void setFocusOwner(Component c) {
	focusMgr.setFocusOwner(c);
    }

    void transferFocus(Component base) {
	nextFocus(base);
    }

    boolean isActive() {
	return active;
    }

    /**
     * Returns the child component of this Window which has focus if and 
     * only if this Window is active.
     * @return the component with focus, or null if no children have focus
     * assigned to them.
     */
    public Component getFocusOwner() {
        if (active)
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

    /* Serialization support.  If there's a MenuBar we restore
     * its (transient) parent field here.
     */

    private int windowSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, windowListenerK, windowListener);
      s.writeObject(null);
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	String key = ((String)keyOrNull).intern();

	if (windowListenerK == key) 
	  addWindowListener((WindowListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
      setWarningString();
    }

}


class FocusManager implements java.io.Serializable {
    Container focusRoot;
    Component focusOwner; //Bug #4101153 : a backout for b fix made for 
							//bug # 4092347

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
                    target = p;
                } while (target != focusRoot);

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
