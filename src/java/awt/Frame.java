/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.FramePeer;
import java.awt.event.*;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import sun.awt.AppContext;
import java.lang.ref.WeakReference;
import javax.accessibility.*;

/**
 * A Frame is a top-level window with a title and a border.
 * <p>
 * The size of the frame includes any area designated for the
 * border.  The dimensions of the border area can be obtained 
 * using the <code>getInsets</code> method, however, since 
 * these dimensions are platform-dependent, a valid insets
 * value cannot be obtained until the frame is made displayable
 * by either calling <code>pack</code> or <code>show</code>. 
 * Since the border area is included in the overall size of the
 * frame, the border effectively obscures a portion of the frame,
 * constraining the area available for rendering and/or displaying
 * subcomponents to the rectangle which has an upper-left corner
 * location of <code>(insets.left, insets.top)</code>, and has a size of
 * <code>width - (insets.left + insets.right)</code> by 
 * <code>height - (insets.top + insets.bottom)</code>. 
 * <p>
 * The default layout for a frame is BorderLayout.
 * <p>
 * In a multi-screen environment, you can create a <code>Frame</code>
 * on a different screen device by constructing the <code>Frame</code>
 * with {@link Frame(GraphicsConfiguration)} or
 * {@link Frame(String title, GraphicsConfiguration)}.  The
 * <code>GraphicsConfiguration</code> object is one of the
 * <code>GraphicsConfiguration</code> objects of the target screen
 * device.
 * <p>
 * In a virtual device multi-screen environment in which the desktop
 * area could span multiple physical screen devices, the bounds of all
 * configurations are relative to the virtual-coordinate system.  The
 * origin of the virtual-coordinate system is at the upper left-hand
 * corner of the primary physical screen.  Depending on the location
 * of the primary screen in the virtual device, negative coordinates
 * are possible, as shown in the following figure.
 * <p>
 * <img src="doc-files/MultiScreen.gif">
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * In such an environment, when calling <code>setLocation</code>, 
 * you must pass a virtual coordinate to this method.  Similarly, 
 * calling <code>getLocationOnScreen</code> on a <code>Frame</code>
 * returns virtual device coordinates.  Call the <code>getBounds</code>
 * method of a <code>GraphicsConfiguration</code> to find its origin in
 * the virtual coordinate system.
 * <p>
 * The following code sets the
 * location of the <code>Frame</code> at (10, 10) relative
 * to the origin of the physical screen of the corresponding
 * <code>GraphicsConfiguration</code>.  If the bounds of the
 * <code>GraphicsConfiguration</code> is not taken into account, the
 * <code>Frame</code> location would be set at (10, 10) relative to the
 * virtual-coordinate system and would appear on the primary physical
 * screen, which might be different from the physical screen of the
 * specified <code>GraphicsConfiguration</code>.
 *
 * <pre>
 *      Frame f = new Frame(GraphicsConfiguration gc);
 *      Rectangle bounds = gc.getBounds();
 *      f.setLocation(10 + bounds.x, 10 + bounds.y);
 * </pre>
 *
 * <p>
 * Frames are capable of generating the following types of window events:
 * WindowOpened, WindowClosing, WindowClosed, WindowIconified,
 * WindowDeiconified, WindowActivated, WindowDeactivated.
 *
 * @version 	1.109, 02/06/02
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
    * @deprecated   replaced by <code>Cursor.DEFAULT_CURSOR</code>.
    */
    public static final int	DEFAULT_CURSOR   		= Cursor.DEFAULT_CURSOR;


   /**
    * @deprecated   replaced by <code>Cursor.CROSSHAIR_CURSOR</code>.
    */
    public static final int	CROSSHAIR_CURSOR 		= Cursor.CROSSHAIR_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.TEXT_CURSOR</code>.
    */
    public static final int	TEXT_CURSOR 	 		= Cursor.TEXT_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.WAIT_CURSOR</code>.
    */
    public static final int	WAIT_CURSOR	 		= Cursor.WAIT_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.SW_RESIZE_CURSOR</code>.
    */
    public static final int	SW_RESIZE_CURSOR	 	= Cursor.SW_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.SE_RESIZE_CURSOR</code>.
    */
    public static final int	SE_RESIZE_CURSOR	 	= Cursor.SE_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.NW_RESIZE_CURSOR</code>.
    */
    public static final int	NW_RESIZE_CURSOR		= Cursor.NW_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.NE_RESIZE_CURSOR</code>.
    */
    public static final int	NE_RESIZE_CURSOR	 	= Cursor.NE_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.N_RESIZE_CURSOR</code>.
    */
    public static final int	N_RESIZE_CURSOR 		= Cursor.N_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.S_RESIZE_CURSOR</code>.
    */
    public static final int	S_RESIZE_CURSOR 		= Cursor.S_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.W_RESIZE_CURSOR</code>.
    */
    public static final int	W_RESIZE_CURSOR	 		= Cursor.W_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.E_RESIZE_CURSOR</code>.
    */
    public static final int	E_RESIZE_CURSOR			= Cursor.E_RESIZE_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.HAND_CURSOR</code>.
    */
    public static final int	HAND_CURSOR			= Cursor.HAND_CURSOR;

   /**
    * @deprecated   replaced by <code>Cursor.MOVE_CURSOR</code>.
    */
    public static final int	MOVE_CURSOR			= Cursor.MOVE_CURSOR;	

    public static final int     NORMAL          = 0;
    public static final int     ICONIFIED       = 1;

    /**
     * This is the title of the frame.  It can be changed
     * at any time.  <code>title</code> can be null and if
     * this is the case the <code>title</code> = "".
     *
     * @serial
     * @see getTitle()
     * @see setTitle()
     */
    String 	title = "Untitled";
    /**
     * <code>icon</code> is the graphical way we can
     * represent the frame.
     * <code>icon</code> can be null, but obviously if
     * you try to set the icon image <code>icon</code>
     * cannot be null.
     *
     * @serial
     * @see getIconImage()
     * @see setIconImage()
     */
    Image  	icon;
    /**
     * The frames menubar.  If <code>menuBar</code> = null
     * the frame will not have a menubar.
     *
     * @serial
     * @see getMenuBar()
     * @see setMenuBar()
     */
    MenuBar	menuBar;
    /**
     * This field indicates whether the the frame is resizable
     * This property can be changed at any time.
     * <code>resizable</code> will be true if the frame is
     * resizable, otherwise it will be false.
     *
     * @serial
     * @see isResizable()
     */
    boolean	resizable = true;
    /**
     * <code>mbManagement</code> is only used by the Motif implementation.
     *
     * @serial
     */
    boolean	mbManagement = false;	/* used only by the Motif impl.	*/
    private int state = NORMAL;


    /* 
     * The Windows owned by the Frame.
     * Note: in 1.2 this has been superceded by Window.ownedWindowList
     *
     * @serial
     * @see java.awt.Window#ownedWindowList
     */
    Vector ownedWindows;

    /*
     * We insert a weak reference into the Vector of all Frames
     * instead of 'this' so that garbage collection can still take
     * place correctly.
     */
    transient private WeakReference weakThis;

    private static final String base = "frame";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 2673458971256075116L;

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	initIDs();
    }

    /**
     * Constructs a new instance of <code>Frame</code> that is 
     * initially invisible.  The title of the <code>Frame</code>
     * is empty.
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Frame() {
        this("", (GraphicsConfiguration)null);
    }

    /**
     * Create a <code>Frame</code> with the specified 
     * <code>GraphicsConfiguration</code> of
     * a screen device.
     * @param gc the <code>GraphicsConfiguration</code> 
     * of the target screen device. If <code>gc</code> 
     * is <code>null</code>, the system default 
     * <code>GraphicsConfiguration</code> is assumed.
     * @exception IllegalArgumentException if 
     * <code>gc</code> is not from a screen device.
     * @since     1.3
     */
    public Frame(GraphicsConfiguration gc) {
        this("", gc);
    }

    /**
     * Constructs a new, initially invisible <code>Frame</code> object 
     * with the specified title.
     * @param title the title to be displayed in the frame's border.
     *              A <code>null</code> value
     *              is treated as an empty string, "".
     * @exception IllegalArgumentException if gc is not from
     * a screen device.
     * @see java.awt.Component#setSize
     * @see java.awt.Component#setVisible
     * @see java.awt.GraphicsConfiguration#getBounds
     */
    public Frame(String title) {
        this(title, (GraphicsConfiguration)null);
    }
    
    /**
     * Constructs a new, initially invisible <code>Frame</code> object 
     * with the specified title and a 
     * <code>GraphicsConfiguration</code>.
     * @param title the title to be displayed in the frame's border.
     *              A <code>null</code> value
     *              is treated as an empty string, "".
     * @param gc the <code>GraphicsConfiguration</code> 
     * of the target screen device.  If <code>gc</code> is 
     * <code>null</code>, the system default 
     * <code>GraphicsConfiguration</code> is assumed.
     * @exception IllegalArgumentException if <code>gc</code> 
     * is not from a screen device.
     * @see java.awt.Component#setSize
     * @see java.awt.Component#setVisible
     * @see java.awt.GraphicsConfiguration#getBounds
     */
    public Frame(String title, GraphicsConfiguration gc) {
        super(gc);
	this.title = title;
	weakThis = new WeakReference(this);
        addToFrameList();
    }

    /**
     * We have to remove the (hard) reference to weakThis in the
     * Vector, otherwise the WeakReference instance will never get
     * garbage collected.
     */
    protected void finalize() throws Throwable {
        removeFromFrameList();
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
     * Makes this Frame displayable by connecting it to
     * a native screen resource.  Making a frame displayable will
     * cause any of its children to be made displayable.
     * This method is called internally by the toolkit and should
     * not be called directly by programs.
     * @see Component#isDisplayable
     * @see #removeNotify
     */
    public void addNotify() {
	synchronized (getTreeLock()) {
	    if (peer == null) {
		peer = getToolkit().createFrame(this);
	    }
	    MenuBar menuBar = this.menuBar;
	    if (menuBar != null) {
	        mbManagement = true;
		menuBar.addNotify();
		((FramePeer)peer).setMenuBar(menuBar);
	    }
	    super.addNotify();
	}
    }

    /**
     * Gets the title of the frame.  The title is displayed in the
     * frame's border.
     * @return    the title of this frame, or an empty string ("")
     *                if this frame doesn't have a title.
     * @see       java.awt.Frame#setTitle
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title for this frame to the specified string.
     * @param    title    the title to be displayed in the frame's border
     * @param title the title to be displayed in the frame's border.
     *              A <code>null</code> value
     *              is treated as an empty string, "".
     * @see      java.awt.Frame#getTitle
     */
    public synchronized void setTitle(String title) {
	this.title = title;
    	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setTitle(title);
	}
    }

    /**
     * Gets the image to be displayed in the minimized icon
     * for this frame.
     * @return    the icon image for this frame, or <code>null</code> 
     *                    if this frame doesn't have an icon image.
     * @see       java.awt.Frame#setIconImage
     */
    public Image getIconImage() {
	return icon;
    }

    /**
     * Sets the image to displayed in the minimized icon for this frame. 
     * Not all platforms support the concept of minimizing a window.
     * @param     image the icon image to be displayed.
     *            If this parameter is <code>null</code> then the
     *            icon image is set to the default image, which may vary
     *            with platform.            
     * @see       java.awt.Frame#getIconImage
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
     */
    public MenuBar getMenuBar() {
	return menuBar;
    }

    /**
     * Sets the menu bar for this frame to the specified menu bar.
     * @param     mb the menu bar being set.
     *            If this parameter is <code>null</code> then any
     *            existing menu bar on this frame is removed.
     * @see       java.awt.Frame#getMenuBar
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
		    if (valid) {
		        invalidate();
		    }
		    peer.setMenuBar(menuBar);
		}
	    }
	}
    }

    /**
     * Indicates whether this frame is resizable by the user.  
     * By default, all frames are initially resizable. 
     * @return    <code>true</code> if the user can resize this frame; 
     *                        <code>false</code> otherwise.
     * @see       java.awt.Frame#setResizable
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets whether this frame is resizable by the user.  
     * @param    resizable   <code>true</code> if this frame is resizable; 
     *                       <code>false</code> otherwise.
     * @see      java.awt.Frame#isResizable
     */
    public void setResizable(boolean resizable) {
        boolean testvalid = false;

        synchronized (this) {
	    this.resizable = resizable;
	    FramePeer peer = (FramePeer)this.peer;
	    if (peer != null) {
	        peer.setResizable(resizable);
		testvalid = true;
	    }
	}

	// On some platforms, changing the resizable state affects
	// the insets of the Frame. If we could, we'd call invalidate()
	// from the peer, but we need to guarantee that we're not holding
	// the Frame lock when we call invalidate().
	if (testvalid && valid) {
	    invalidate();
	}
    }

    /**
     * Sets the state of this frame.
     * @param  state <code>Frame.ICONIFIED</code> if this frame is in 
     *           iconic state; <code>Frame.NORMAL</code> if this frame is 
     *           in normal state.
     * @see      java.awt.Frame#getState
     */
    public synchronized void setState(int state) {
        this.state = state;
        FramePeer peer = (FramePeer)this.peer;
        if (peer != null) {
            peer.setState(state);
        }
    }

    /**
     * Gets the state of this frame.
     * @return   <code>Frame.ICONIFIED</code> if frame in iconic state;
     *           <code>Frame.NORMAL</code> if frame is in normal state.
     * @see      java.awt.Frame#setState
     */
    public synchronized int getState() {
        FramePeer peer = (FramePeer)this.peer;
        if (peer != null) {
            state = peer.getState();
        }
        return state;
    }

    /**
     * Removes the specified menu bar from this frame.
     * @param    m   the menu component to remove.
     *           If this parameter is <code>null</code> then a
     *           NullPointerException is thrown and no action
     *           is taken.
     */
    public void remove(MenuComponent m) {
	synchronized (getTreeLock()) {
	    if (m == menuBar) {
		menuBar = null;
		FramePeer peer = (FramePeer)this.peer;
		if (peer != null) {
		    mbManagement = true;
		    if (valid) {
		        invalidate();
		    }
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
     * Makes this Frame undisplayable by removing its connection
     * to its native screen resource. Making a Frame undisplayable
     * will cause any of its children to be made undisplayable. 
     * This method is called by the toolkit internally and should
     * not be called directly by programs.
     * @see Component#isDisplayable
     * @see #addNotify
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
	    FramePeer peer = (FramePeer)this.peer;
	    if (peer != null) {
                // get the latest Frame state before disposing
                getState();

                if (menuBar != null) {
	            mbManagement = true;
		    peer.setMenuBar(null);
		    menuBar.removeNotify();
                }
	    }
	    super.removeNotify();
	}
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
    public void setCursor(int cursorType) {
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

    /**
     * Returns an array containing all Frames created by the application.
     * If called from an applet, the array will only include the Frames
     * accessible by that applet.
     * @since 1.2
     */
    public static Frame[] getFrames() {
        synchronized (Frame.class) {
            Frame realCopy[];
            Vector frameList =
		(Vector)AppContext.getAppContext().get(Frame.class);
            if (frameList != null) {
	        // Recall that frameList is actually a Vector of WeakReferences
	        // and calling get() on one of these references may return
	        // null. Make two arrays-- one the size of the Vector 
	        // (fullCopy with size fullSize), and one the size of all
	        // non-null get()s (realCopy with size realSize).
	        int fullSize = frameList.size();
		int realSize = 0;
		Frame fullCopy[] = new Frame[fullSize];

		for (int i = 0; i < fullSize; i++) {
		    fullCopy[realSize] = (Frame) 
		        (((WeakReference) (frameList.elementAt(i))).get());

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
            } else {
                realCopy = new Frame[0];
            }
            return realCopy;
        }
    }

    void addToFrameList() {
        synchronized (Frame.class) {
            Vector frameList = (Vector)appContext.get(Frame.class);
            if (frameList == null) {
                frameList = new Vector();
                appContext.put(Frame.class, frameList);
            }
            frameList.addElement(weakThis);
        }
    }

    void removeFromFrameList() {
        synchronized (Frame.class) {
            Vector frameList = (Vector)appContext.get(Frame.class);
            if (frameList != null) {
                frameList.removeElement(weakThis);
            }
        }
    }

    /* Serialization support.  If there's a MenuBar we restore
     * its (transient) parent field here.  Likewise for top level 
     * windows that are "owned" by this frame.
     */

    /**
     * Frame Serialized Data Version.
     *
     * @serial
     */
    private int frameSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable ItemListener(s) as optional data.
     * The non-serializable ItemListner(s) are detected and
     * no attempt is made to serialize them.
     *
     * @serialData Null terminated sequence of 0 or more pairs.
     *             The pair consists of a String and Object.
     *             The String indicates the type of object and
     *             is one of the following :
     *             itemListenerK indicating and ItemListener object.
     *
     * @see java.awt.Component.itemListenerK
     */
    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();
    }

    /**
     * Read the ObjectInputStream and if it isnt null
     * add a listener to receive item events fired
     * by the Frame.
     * Unrecognised keys or values will be Ignored.
     * @see removeActionListener()
     * @see addActionListener()
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      if (menuBar != null)
	menuBar.parent = this;

      // Ensure 1.1 serialized Frames can read & hook-up
      // owned windows properly
      //
      if (ownedWindows != null) {
          for (int i = 0; i < ownedWindows.size(); i++) {
	      connectOwnedWindow((Window) ownedWindows.elementAt(i));
	  }
          ownedWindows = null;  
      }

      weakThis = new WeakReference(this);
      addToFrameList();
    }
    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();

    /*
     * --- Accessibility Support ---
     *
     */

    /**
     * Gets the AccessibleContext associated with this Frame. 
     * For frames, the AccessibleContext takes the form of an 
     * AccessibleAWTFrame. 
     * A new AccessibleAWTFrame instance is created if necessary.
     *
     * @return an AccessibleAWTFrame that serves as the 
     *         AccessibleContext of this Frame
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTFrame();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>Frame</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to frame user-interface elements.
     */
    protected class AccessibleAWTFrame extends AccessibleAWTWindow {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FRAME;
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
            if (getFocusOwner() != null) {
                states.add(AccessibleState.ACTIVE);
            }
            if (isResizable()) {
                states.add(AccessibleState.RESIZABLE);
            }
            return states;
        }


    } // inner class AccessibleAWTFrame
}
