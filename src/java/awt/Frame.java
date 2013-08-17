/*
 * @(#)Frame.java	1.65 97/05/21 Sami Shaio
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

import java.awt.peer.FramePeer;
import java.awt.event.*;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A Frame is a top-level window with a title.
 * The default layout for a frame is BorderLayout.
 *
 * Frames are capable of generating the following types of window events:
 * WindowOpened, WindowClosing, WindowClosed, WindowIconified,
 * WindowDeiconified, WindowActivated, WindowDeactivated.
 * @see WindowEvent
 * @see Window#addWindowListener
 *
 * @version 	1.65, 05/21/97
 * @author 	Sami Shaio
 */
public class Frame extends Window implements MenuContainer {

    /* Note: These are being obsoleted;  programs should use the Cursor class
     * variables going forward. See Cursor and Component.setCursor.
     */
    public static final int	DEFAULT_CURSOR   		= Cursor.DEFAULT_CURSOR;
    public static final int	CROSSHAIR_CURSOR 		= Cursor.CROSSHAIR_CURSOR;
    public static final int	TEXT_CURSOR 	 		= Cursor.TEXT_CURSOR;
    public static final int	WAIT_CURSOR	 		= Cursor.WAIT_CURSOR;
    public static final int	SW_RESIZE_CURSOR	 	= Cursor.SW_RESIZE_CURSOR;
    public static final int	SE_RESIZE_CURSOR	 	= Cursor.SE_RESIZE_CURSOR;
    public static final int	NW_RESIZE_CURSOR		= Cursor.NW_RESIZE_CURSOR;
    public static final int	NE_RESIZE_CURSOR	 	= Cursor.NE_RESIZE_CURSOR;
    public static final int	N_RESIZE_CURSOR 		= Cursor.N_RESIZE_CURSOR;
    public static final int	S_RESIZE_CURSOR 		= Cursor.S_RESIZE_CURSOR;
    public static final int	W_RESIZE_CURSOR	 		= Cursor.W_RESIZE_CURSOR;
    public static final int	E_RESIZE_CURSOR			= Cursor.E_RESIZE_CURSOR;
    public static final int	HAND_CURSOR			= Cursor.HAND_CURSOR;
    public static final int	MOVE_CURSOR			= Cursor.MOVE_CURSOR;	

    String 	title = "Untitled";
    Image  	icon;
    MenuBar	menuBar;
    boolean	resizable = true;

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
     * Constructs a new Frame that is initially invisible.
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Frame() {
        this("");
    }

    /**
     * Constructs a new, initially invisible Frame with the specified 
     * title.
     * @param title the title for the frame
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Frame(String title) {
	this.name = base + nameCounter++;
	this.title = title;
	visible = false;
	setLayout(new BorderLayout());
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
     */
    public void addNotify() {
	peer = getToolkit().createFrame(this);
    	MenuBar menuBar = this.menuBar;
	if (menuBar != null) {
	    menuBar.addNotify();
	    ((FramePeer)peer).setMenuBar(menuBar);
	}
	super.addNotify();
    }

    /**
     * Gets the title of the Frame.
     * @see #setTitle
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title for this Frame to the specified title.
     * @param title the specified title of this Frame
     * @see #getTitle
     */
    public synchronized void setTitle(String title) {
	this.title = title;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setTitle(title);
	}
    }

    /**
     * Returns the icon image for this Frame.
     */
    public Image getIconImage() {
	return icon;
    }

    /**
     * Sets the image to display when this Frame is iconized. Note that
     * not all platforms support the concept of iconizing a window.
     * @param image the icon image to be displayed
     */
    public synchronized void setIconImage(Image image) {
	this.icon = image;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setIconImage(image);
	}
    }

    /**
     * Gets the menu bar for this Frame.
     */
    public MenuBar getMenuBar() {
	return menuBar;
    }

    /**
     * Sets the menubar for this Frame to the specified menubar.
     * @param mb the menubar being set
     */
    public synchronized void setMenuBar(MenuBar mb) {
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
		menuBar.addNotify();
		peer.setMenuBar(menuBar);
	    }
	}
        invalidate();
    }

    /**
     * Returns true if the user can resize the Frame.
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets the resizable flag.
     * @param resizable true if resizable; false otherwise.
     */
    public synchronized void setResizable(boolean resizable) {
	this.resizable = resizable;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setResizable(resizable);
	}
    }

    /**
     * Removes the specified menu bar from this Frame.
     */
    public synchronized void remove(MenuComponent m) {
	if (m == menuBar) {
	    FramePeer peer = (FramePeer)this.peer;
	    if (peer != null) {
		menuBar.removeNotify();
		menuBar.parent = null;
		peer.setMenuBar(null);
	    }
	    menuBar = null;
	} else {
            super.remove(m);
        }
    }

    /**
     * Disposes of the Frame. This method must
     * be called to release the resources that
     * are used for the frame.  All components
     * contained by the frame and all windows
     * owned by the frame will also be destroyed.
     */
    public synchronized void dispose() {
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
     * replaced by Component.setCursor(Cursor).
     */
    public synchronized void setCursor(int cursorType) {
	if (cursorType < DEFAULT_CURSOR || cursorType > MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by Component.getCursor().
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

