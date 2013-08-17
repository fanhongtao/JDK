/*
 * @(#)Frame.java	1.77 98/07/01
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

import java.awt.peer.FramePeer;
import java.awt.event.*;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A Frame is a top-level window with a title and a border.
 * The default layout for a frame is BorderLayout.
 *
 * Frames are capable of generating the following types of window events:
 * WindowOpened, WindowClosing, WindowClosed, WindowIconified,
 * WindowDeiconified, WindowActivated, WindowDeactivated.
 *
 * @version 	1.77, 07/01/98
 * @author 	Sami Shaio
 * @see WindowEvent
 * @see Window#addWindowListener
 * @since       JDK1.0
 */
public class Frame extends Window implements MenuContainer {

    /* Note: These are being obsoleted;  programs should use the Cursor class
     * variables going forward. See Cursor and Component.setCursor.
     */

   /**
    * 
    */
    public static final int	DEFAULT_CURSOR   		= Cursor.DEFAULT_CURSOR;


   /**
    *
    */
    public static final int	CROSSHAIR_CURSOR 		= Cursor.CROSSHAIR_CURSOR;

   /**
    * 
    */
    public static final int	TEXT_CURSOR 	 		= Cursor.TEXT_CURSOR;

   /**
    * 
    */
    public static final int	WAIT_CURSOR	 		= Cursor.WAIT_CURSOR;

   /**
    * 
    */
    public static final int	SW_RESIZE_CURSOR	 	= Cursor.SW_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	SE_RESIZE_CURSOR	 	= Cursor.SE_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	NW_RESIZE_CURSOR		= Cursor.NW_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	NE_RESIZE_CURSOR	 	= Cursor.NE_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	N_RESIZE_CURSOR 		= Cursor.N_RESIZE_CURSOR;

   /**
    *
    */
    public static final int	S_RESIZE_CURSOR 		= Cursor.S_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	W_RESIZE_CURSOR	 		= Cursor.W_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	E_RESIZE_CURSOR			= Cursor.E_RESIZE_CURSOR;

   /**
    * 
    */
    public static final int	HAND_CURSOR			= Cursor.HAND_CURSOR;

   /**
    * 
    */
    public static final int	MOVE_CURSOR			= Cursor.MOVE_CURSOR;	

    String 	title = "Untitled";
    Image  	icon;
    MenuBar	menuBar;
    boolean	resizable = true;
    boolean     mbManagement = false;   /* used only by the Motif impl. */

    /* 
     * The Windows owned by the Frame.
     */
    Vector ownedWindows;

    private static final String base = "frame";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 2673458971256075116L;

    /**
     * Constructs a new instance of <code>Frame</code> that is 
     * initially invisible.
     * @see Component#setSize
     * @see Component#setVisible
     * @since JDK1.0
     */
    public Frame() {
        this("");
    }

    /**
     * Constructs a new, initially invisible <code>Frame</code> object 
     * with the specified title.
     * @param title the title for the frame
     * @see java.awt.Component#setSize
     * @see java.awt.Component#setVisible
     * @since JDK1.0
     */
    public Frame(String title) {
	this.title = title;
	visible = false;
	setLayout(new BorderLayout());
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /** 
     * Adds the specified window to the list of windows owned by
     * the frame.
     * @param window the window to be added
     */
    Window addOwnedWindow(Window window) {
        if (window != null) {
	    if (ownedWindows == null) {
	        ownedWindows = new Vector();
	    }
	    ownedWindows.addElement(window);
	}
	return window;
    }

    /** 
     * Removes the specified window from the list of windows owned by
     * the frame.
     * @param window the window to be added
     */
    void removeOwnedWindow(Window window) {
        if (window != null) {
	    if (ownedWindows != null) {
	      ownedWindows.removeElement(window);
	    }
	}
    }

    /**
     * Creates the Frame's peer.  The peer allows us to change the look
     * of the Frame without changing its functionality.
     * @since JDK1.0
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null)
			peer = getToolkit().createFrame(this);
    	    MenuBar menuBar = this.menuBar;
	    if (menuBar != null) {
	        menuBar.addNotify();
	        ((FramePeer)peer).setMenuBar(menuBar);
	    }
	    super.addNotify();
        }
    }

    /**
     * Gets the title of the frame.
     * @return    the title of this frame, or <code>null</code> 
     *                if this frame doesn't have a title.
     * @see       java.awt.Frame#setTitle
     * @since     JDK1.0
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title for this frame to the specified title.
     * @param    title    the specified title of this frame.
     * @see      java.awt.Frame#getTitle
     * @since    JDK1.0
     */
    public synchronized void setTitle(String title) {
	this.title = title;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setTitle(title);
	}
    }

    /**
     * Gets the icon image for this frame.
     * @return    the icon image for this frame, or <code>null</code> 
     *                    if this frame doesn't have an icon image.
     * @see       java.awt.Frame#setIconImage
     * @since     JDK1.0
     */
    public Image getIconImage() {
	return icon;
    }

    /**
     * Sets the image to display when this frame is iconized. 
     * Not all platforms support the concept of iconizing a window.
     * @param     image the icon image to be displayed
     * @see       java.awt.Frame#getIconImage
     * @since     JDK1.0
     */
    public synchronized void setIconImage(Image image) {
	this.icon = image;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setIconImage(image);
	}
    }

    /**
     * Gets the menu bar for this frame.
     * @return    the menu bar for this frame, or <code>null</code> 
     *                   if this frame doesn't have a menu bar.
     * @see       java.awt.Frame#setMenuBar
     * @since     JDK1.0
     */
    public MenuBar getMenuBar() {
	return menuBar;
    }

    /**
     * Sets the menu bar for this frame to the specified menu bar.
     * @param     mb the menu bar being set
     * @see       java.awt.Frame#getMenuBar
     * @since     JDK1.0
     */
    public void setMenuBar(MenuBar mb) {
        synchronized (getTreeLock()) {
	    if (menuBar == mb) {
	        return;
	    }
	    if ((mb != null) && (mb.parent != null)) {
	        mb.parent.remove(mb);
	    }
	    if (menuBar != null) {
	        remove(menuBar);
	    }
	    menuBar = mb;
	    if (menuBar != null) {
	        menuBar.parent = this;

	        FramePeer peer = (FramePeer)this.peer;
	        if (peer != null) {
		    mbManagement = true;
		    menuBar.addNotify();
		    peer.setMenuBar(menuBar);
	        }
	    }
            invalidate();
        }
    }

    /**
     * Indicates whether this frame is resizable.  
     * By default, all frames are initially resizable. 
     * @return    <code>true</code> if the user can resize this frame; 
     *                        <code>false</code> otherwise.
     * @see       java.awt.Frame#setResizable
     * @since     JDK1.0
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets the resizable flag, which determines whether 
     * this frame is resizable. 
     * By default, all frames are initially resizable. 
     * @param    resizable   <code>true</code> if this frame is resizable; 
     *                       <code>false</code> otherwise.
     * @see      java.awt.Frame#isResizable
     * @since    JDK1.0
     */
    public synchronized void setResizable(boolean resizable) {
	this.resizable = resizable;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setResizable(resizable);
	}
    }

    /**
     * Removes the specified menu bar from this frame.
     * @param    m   the menu component to remove.
     * @since    JDK1.0
     */
    public void remove(MenuComponent m) {
        synchronized (getTreeLock()) {
	    if (m == menuBar) {
	        menuBar = null;
	        FramePeer peer = (FramePeer)this.peer;
	        if (peer != null) {
		    mbManagement = true;
		    peer.setMenuBar(null);
		    m.removeNotify();
	        }
 		m.parent = null;
	    } else {
                super.remove(m);
            }
        }
    }

    /**
     * Disposes of the Frame. This method must
     * be called to release the resources that
     * are used for the frame.  All components
     * contained by the frame and all windows
     * owned by the frame will also be destroyed.
     * @since JDK1.0
     */
    public void dispose() {     // synch removed.
      synchronized (getTreeLock()) {
	if (ownedWindows != null) {
	  int ownedWindowCount = ownedWindows.size();
	  Window ownedWindowCopy[] = new Window[ownedWindowCount];
	  ownedWindows.copyInto(ownedWindowCopy);
	  for (int i = 0; i < ownedWindowCount ; i++) {
	    ownedWindowCopy[i].dispose();
	  }
	}
	if (menuBar != null) {
	    remove(menuBar);
	    menuBar = null;
	}
      }
      super.dispose();
    }

    void postProcessKeyEvent(KeyEvent e) {
        if (menuBar != null && menuBar.handleShortcut(e)) {
            e.consume();
            return;
        }
        super.postProcessKeyEvent(e);
    }

    /**
     * Returns the parameter String of this Frame.
     */
    protected String paramString() {
	String str = super.paramString();
	if (resizable) {
	    str += ",resizable";
	}
	if (title != null) {
	    str += ",title=" + title;
	}
	return str;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>Component.setCursor(Cursor)</code>.
     */
    public synchronized void setCursor(int cursorType) {
	if (cursorType < DEFAULT_CURSOR || cursorType > MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>Component.getCursor()</code>.
     */
    public int getCursorType() {
	return (getCursor().getType());
    }


    /* Serialization support.  If there's a MenuBar we restore
     * its (transient) parent field here.  Likewise for top level 
     * windows that are "owned" by this frame.
     */

    private int frameSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      if (menuBar != null)
	menuBar.parent = this;

      if (ownedWindows != null) {
	for(int i = 0; i < ownedWindows.size(); i++) {
	  Window child = (Window)(ownedWindows.elementAt(i));
	  child.parent = this;
	}
      }
    }
}

