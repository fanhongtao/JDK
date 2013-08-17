/*
 * @(#)Window.java	1.61 97/03/03 Arthur van Hoff
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
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


/**
 * A Window is a top-level window with no borders and no
 * menubar. It could be used to implement a pop-up menu.
 * The default layout for a window is BorderLayout.
 *
 * Windows are capable of generating the following window events:
 * WindowOpened, WindowClosed.
 * @see WindowEvent
 * @see #addWindowListener
 *
 * @version 	1.61, 03/03/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Window extends Container {
    String      warningString;

    static final int OPENED = 0x01;
    int state;
    transient WindowListener windowListener;

    private FocusManager focusMgr;

    private static final String base = "win";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 4497834738069338734L;

    Window() {
	this.name = base + nameCounter++;
	SecurityManager sm = System.getSecurityManager();
	if ((sm != null) && !sm.checkTopLevelWindow(this)) {
	    warningString = System.getProperty("awt.appletWarning",
					       "Warning: Applet Window");
	}
	this.focusMgr = new FocusManager(this);
	this.visible = false;
    }

    /**
     * Constructs a new Window initialized to an invisible state. 
     *
     * @param parent the owner of the dialog
     * @see Component#setSize
     * @see #show
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
	if (peer == null) {
	    peer = getToolkit().createWindow(this);
	}
	super.addNotify();
    }

    /**
     * Packs the components of the Window.
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
     * Shows the Window. This will bring the window to the
     * front if the window is already visible.
     * @see Component#hide
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
     * Disposes of the Window. This method must
     * be called to release the resources that
     * are used for the window.
     */
    public void dispose() {
	hide();
	removeNotify();
 	if (parent != null) {
	    Frame parent = (Frame) this.parent;
	    parent.removeOwnedWindow(this);
 	} 
        postWindowEvent(WindowEvent.WINDOW_CLOSED);
    }

    /**
     * Places this window at the top of the stacking order and
     * shows it in front of any other windows.
     */
    public void toFront() {
    	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toFront();
	}
    }

    /**
     * Places this window at the bottom of the stacking order and
     * makes the corresponding adjustment to other visible windows.
     */
    public void toBack() {
    	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toBack();
	}
    }

    /**
     * Returns the toolkit of this frame.
     * @see Toolkit
     */
    public Toolkit getToolkit() {
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Gets the warning string for this window. This is
     * a string that will be displayed somewhere in the
     * visible area of windows that are not secure.
     */
    public final String getWarningString() {
	return warningString;
    }

    /** 
     * Gets the Locale for the window, if it has been set.
     * If no Locale has been set, then the default Locale 
     * is returned.
     */

    public Locale getLocale() {
      if (this.locale == null) {
	return Locale.getDefault();
      }
      return this.locale;
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

    /**
     * Returns the child component of this Window which has focus if and 
     * only if this Window is active.
     * @return the component with focus, or null if no children have focus
     * assigned to them.
     */
    public Component getFocusOwner() {
        return focusMgr.getFocusOwner();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by transferFocus(Component).
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
/*
  Calling this messes up focus on Solaris

          case WindowEvent.WINDOW_ACTIVATED:
            focusMgr.activateFocus();
            break;
*/
          default:
            break;
        }
        super.dispatchEventImpl(e);
    }

    /**
     * @deprecated As of JDK version 1.1
     * replaced by dispatchEvent(AWTEvent).
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
    }

}


class FocusManager implements java.io.Serializable {
    Container focusRoot;
    Component focusOwner;

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
                
     
    void setFocusOwner(Component c) {
        //System.out.println("FocusManager.setFocusOwner: "+c.name);
	synchronized (Component.LOCK) {
	    focusOwner = c;
	}
    }

    Component getFocusOwner() {
       return focusOwner;
    }
	
    boolean focusNext() {
       return focusNext(focusOwner);
    }

    boolean focusNext(Component base) {
        synchronized (Component.LOCK) {
	    Component target = base;
	    if (target == null || target.parent == null) {
		return false;
	    } 
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
			if (c instanceof Container && c.isVisible() && c.isEnabled()) {
			    if (focusForward((Container)c)) {
				return true;
			    }
			} 	    
		    } else if (c == target) {
			found = true;	
		    }
		} 
		target = p;
	    } while (target != focusRoot);

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
	synchronized (Component.LOCK) {
	    Component target = base;
	    if (target == null || target.parent == null) {
		return false;
	    }       
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
			if (c instanceof Container && c.isVisible() && c.isEnabled()) {
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

    boolean assignFocus(Component c, boolean requireTraversable) {
	synchronized (Component.LOCK) {
	    if (c.isVisible() && c.isEnabled() &&
                (!requireTraversable || c.isFocusTraversable())) {
	        //System.out.println("FocusManager.assignFocus: "+c);
                c.requestFocus();
		return true;
	    }
	    return false;
	}
    }

    boolean focusForward(Container cont) {
	synchronized (Component.LOCK) {
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
    }

    boolean focusBackward(Container cont) {
	synchronized (Component.LOCK) {
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

}
