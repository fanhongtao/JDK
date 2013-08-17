/*
 * @(#)Component.java	1.3 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.awt;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Locale;
import java.awt.peer.ComponentPeer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.awt.event.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.beans.PropertyChangeListener;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.dnd.DropTarget;

import sun.security.action.GetPropertyAction;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.ConstrainableGraphics;

/**
 * A <em>component</em> is an object having a graphical representation
 * that can be displayed on the screen and that can interact with the
 * user. Examples of components are the buttons, checkboxes, and scrollbars
 * of a typical graphical user interface. <p>
 * The <code>Component</code> class is the abstract superclass of
 * the nonmenu-related Abstract Window Toolkit components. Class
 * <code>Component</code> can also be extended directly to create a
 * lightweight component. A lightweight component is a component that is
 * not associated with a native opaque window.
 *
 * @version 	1.226, 12/15/98
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 */
public abstract class Component implements ImageObserver, MenuContainer,
    Serializable
{
    /**
     * The peer of the component. The peer implements the component's
     * behaviour. The peer is set when the Component is added to a
     * container that also is a peer.
     * @see #addNotify
     * @see #removeNotify
     */
    transient ComponentPeer peer;

    /**
     * The parent of the object. It may be null for top-level components.
     * @see #getParent
     */
    transient Container parent;

    /**
     * The AppContext of the component.  This is set in the constructor
     * and never changes.
     */
    transient AppContext appContext;

    /**
     * The x position of the component in the parent's coordinate system.
     *
     * @serial
     * @see #getLocation
     */
    int x;

    /**
     * The y position of the component in the parent's coordinate system.
     *
     * @serial
     * @see #getLocation
     */
    int y;

    /**
     * The width of the component.
     *
     * @serial
     * @see #getSize
     */
    int width;

    /**
     * The height of the component.
     *
     * @serial
     * @see #getSize
     */
    int height;

    /**
     * The foreground color for this component.
     * foreground color can be null.
     *
     * @serial
     * @see #getForeground
     * @see #setForeground
     */
    Color	foreground;

    /**
     * The background color for this component.
     * background can be null.
     *
     * @serial
     * @see #getBackground
     * @see #setBackground
     */
    Color	background;

    /**
     * The font used by this component.
     * The font can be null.
     *
     * @serial
     * @see #getFont
     * @see #setFont
     */
    Font	font;

    /**
     * The font which the peer is currently using. (null if no peer exists.)
     */
    Font        peerFont;

    /**
     * The cursor displayed when pointer is over this component.
     * cursor must always be a non-null cursor image.
     *
     * @serial
     * @see #getCursor
     * @see #setCursor
     */
    Cursor	cursor;

    /**
     * The locale for the component.
     *
     * @serial
     * @see #getLocale
     * @see #setLocale
     */
    Locale      locale;

    /**
     * True when the object is visible. An object that is not
     * visible is not drawn on the screen.
     *
     * @serial
     * @see #isVisible
     * @see #setVisible
     */
    boolean visible = true;

    /**
     * True when the object is enabled. An object that is not
     * enabled does not interact with the user.
     *
     * @serial
     * @see #isEnabled
     * @see #setEnabled
     */
    boolean enabled = true;

    /**
     * True when the object is valid. An invalid object needs to
     * be layed out. This flag is set to false when the object
     * size is changed.
     *
     * @serial
     * @see #isValid
     * @see #validate
     * @see #invalidate
     */
    boolean valid = false;

    /**
     * The DropTarget associated with this Component.
     *
     * @since JDK 1.2
     * @serial 
     * @see #setDropTarget
     * @see #getDropTarget
     */
    DropTarget dropTarget;


    /**
     * True if this component has enabled focus events and currently
     * has the focus.
     *
     * @serial
     * @see #hasFocus
     * @see #processFocusEvent
     */
    boolean hasFocus = false;

    /**
     * @serial
     * @see add()
     */
    Vector popups;

    /**
     * A components name.
     * This field can be null.
     *
     * @serial
     * @see getName()
     * @see setName()
     */
    private String name;
  
    /**
     * A bool to determine whether the name has
     * been set explicitly. nameExplicitlySet will
     * be false if the name has not been set and
     * true if it has.
     *
     * @serial
     * @see getName()
     * @see setName()
     */
    private boolean nameExplicitlySet = false;

    /**
     * The locking object for AWT component-tree and layout operations.
     *
     * @see #getTreeLock
     */
    static final Object LOCK = new AWTTreeLock();
    static class AWTTreeLock {}

    /**
     * Internal, cached size information.
     * (This field perhaps should have been transient).
     *
     * @serial
     */
    Dimension minSize;

    /** Internal, cached size information
     * (This field perhaps should have been transient).
     *
     * @serial
     */
    Dimension prefSize;

    /**
     * The orientation for this component.
     * @see #getComponentOrientation
     * @see #setComponentOrientation(java.awt.ComponentOrientation)
     */
    transient ComponentOrientation componentOrientation
                = ComponentOrientation.UNKNOWN;

    /**
     * newEventsOnly will be true if the event is
     * one of the event types enabled for the component.
     * It will then allow for normal processing to
     * continue.  If it is false the event is passed
     * to the components parent and up the ancestor
     * tree until the event has been consumed.
     *
     * @serial
     * @see dispatchEvent()
     */
    boolean newEventsOnly = false;
    transient ComponentListener componentListener;
    transient FocusListener focusListener;
    transient KeyListener keyListener;
    transient MouseListener mouseListener;
    transient MouseMotionListener mouseMotionListener;
    transient InputMethodListener inputMethodListener;

    /** Internal, constants for serialization */
    final static String actionListenerK = "actionL";
    final static String adjustmentListenerK = "adjustmentL";
    final static String componentListenerK = "componentL";
    final static String containerListenerK = "containerL";
    final static String focusListenerK = "focusL";
    final static String itemListenerK = "itemL";
    final static String keyListenerK = "keyL";
    final static String mouseListenerK = "mouseL";
    final static String mouseMotionListenerK = "mouseMotionL";
    final static String textListenerK = "textL";
    final static String ownedWindowK = "ownedL";
    final static String windowListenerK = "windowL";
    final static String inputMethodListenerK = "inputMethodL";

    /**
     * The eventMask is ONLY set by subclasses via enableEvents.
     * The mask should NOT be set when listeners are registered
     * so that we can distinguish the difference between when
     * listeners request events and subclasses request them.
     * One bit is used to indicate whether input methods are
     * enabled; this bit is set by enableInputMethods and is
     * on by default.
     *
     * @serial
     * @see enableInputMethods()
     */
    long eventMask = AWTEvent.INPUT_METHODS_ENABLED_MASK;

    // enable for assertion checking
    private final static boolean assert = false;

    /**
     * Static properties for incremental drawing.
     * @see #imageUpdate
     */
    static boolean isInc;
    static int incRate;
    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	/* initialize JNI field and method ids */
	initIDs();

	String s = (String) java.security.AccessController.doPrivileged(
               new GetPropertyAction("awt.image.incrementaldraw"));
	isInc = (s == null || s.equals("true"));

	s = (String) java.security.AccessController.doPrivileged(
               new GetPropertyAction("awt.image.redrawrate"));
	incRate = (s != null) ? Integer.parseInt(s) : 100;
    }

    /**
     * Ease-of-use constant for <code>getAlignmentY()</code>.  Specifies an
     * alignment to the top of the component.
     * @see     #getAlignmentY
     */
    public static final float TOP_ALIGNMENT = 0.0f;

    /**
     * Ease-of-use constant for <code>getAlignmentY</code> and
     * <code>getAlignmentX</code>. Specifies an alignment to
     * the center of the component
     * @see     #getAlignmentX
     * @see     #getAlignmentY
     */
    public static final float CENTER_ALIGNMENT = 0.5f;

    /**
     * Ease-of-use constant for <code>getAlignmentY</code>.  Specifies an
     * alignment to the bottom of the component.
     * @see     #getAlignmentY
     */
    public static final float BOTTOM_ALIGNMENT = 1.0f;

    /**
     * Ease-of-use constant for <code>getAlignmentX</code>.  Specifies an
     * alignment to the left side of the component.
     * @see     #getAlignmentX
     */
    public static final float LEFT_ALIGNMENT = 0.0f;

    /**
     * Ease-of-use constant for <code>getAlignmentX</code>.  Specifies an
     * alignment to the right side of the component.
     * @see     #getAlignmentX
     */
    public static final float RIGHT_ALIGNMENT = 1.0f;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -7644114512714619750L;

    /**
     * If any PropertyChangeListeners have been registered, the
     * changeSupport field describes them.
     *
     * @serial
     * @since JDK 1.2
     * @see addPropertyChangeListener()
     * @see removePropertyChangeListener()
     * @see firePropertyChange()
     */
    private java.beans.PropertyChangeSupport changeSupport;

    boolean isPacked = false;

    /**
     * Constructs a new component. Class <code>Component</code> can be
     * extended directly to create a lightweight component that does not
     * utilize an opaque native window. A lightweight component must be
     * hosted by a native container somewhere higher up in the component
     * tree (for example, by a <code>Frame</code> object).
     */
    protected Component() {
	appContext = AppContext.getAppContext();
	SunToolkit.insertTargetMapping(this, appContext);
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return null; // For strict compliance with prior JDKs, a Component
                     // that doesn't set its name should return null from
                     // getName()
    }

    /**
     * Gets the name of the component.
     * @return This component's name.
     * @see    #setName
     * @since JDK1.1
     */
    public String getName() {
        if (name == null && !nameExplicitlySet) {
            synchronized(this) {
                if (name == null && !nameExplicitlySet)
                    name = constructComponentName();
            }
        }
        return name;
    }

    /**
     * Sets the name of the component to the specified string.
     * @param name  The string that is to be this 
     * component's name.
     * @see #getName
     * @since JDK1.1
     */
    public void setName(String name) {
        synchronized(this) {
            this.name = name;
            nameExplicitlySet = true;
        }
    }

    /**
     * Gets the parent of this component.
     * @return The parent container of this component.
     * @since JDK1.0
     */
    public Container getParent() {
	return getParent_NoClientCode();
    }

    // NOTE: This method may be called by privileged threads.
    //       This functionality is implemented in a package-private method 
    //       to insure that it cannot be overridden by client subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final Container getParent_NoClientCode() {
	return parent;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * programs should not directly manipulate peers.
     * replaced by <code>boolean isDisplayable()</code>.
     */
    public ComponentPeer getPeer() {
	return peer;
    }

    /**
     * Associate a DropTarget with this Component.
     *
     * @param dt The DropTarget
     */

    public synchronized void setDropTarget(DropTarget dt) {
	if (dt == dropTarget || (dropTarget != null && dropTarget.equals(dt)))
	    return;

	DropTarget old;

	if ((old = dropTarget) != null) {
	    if (peer != null) dropTarget.removeNotify(peer);

	    DropTarget t = dropTarget;

	    dropTarget = null;

	    try {
	        t.setComponent(null);
	    } catch (IllegalArgumentException iae) {
		// ignore it.
	    }
	}

	// if we have a new one, and we have a peer, add it!

	if ((dropTarget = dt) != null) {
	    try {
	        dropTarget.setComponent(this);
	        if (peer != null) dropTarget.addNotify(peer);
	    } catch (IllegalArgumentException iae) {
		if (old != null) {
		    try {
	        	old.setComponent(this);
	        	if (peer != null) dropTarget.addNotify(peer);
		    } catch (IllegalArgumentException iae1) {
			// ignore it!
		    }
		}
	    }
	}
    }

    /**
     * Get the DropTarget associated with this Component
     */

    public synchronized DropTarget getDropTarget() { return dropTarget; }

    /**
     * Gets the locking object for AWT component-tree and layout
     * Gets this component's locking object (the object that owns the thread
     * sychronization monitor) for AWT component-tree and layout
     * operations.
     * @return This component's locking object.
     */
    public final Object getTreeLock() {
	return LOCK;
    }

    /**
     * Gets the toolkit of this component. Note that
     * the frame that contains a component controls which
     * toolkit is used by that component. Therefore if the component
     * is moved from one frame to another, the toolkit it uses may change.
     * @return  The toolkit of this component.
     * @since JDK1.0
     */
    public Toolkit getToolkit() {
	return getToolkitImpl();
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final Toolkit getToolkitImpl() {
      	ComponentPeer peer = this.peer;
 	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)){
	    return peer.getToolkit();
	}
	Container parent = this.parent;
	if (parent != null) {
	    return parent.getToolkitImpl();
	}
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Determines whether this component is valid. A component is valid
     * when it is correctly sized and positioned within its parent
     * container and all its children are also valid. Components are
     * invalidated when they are first shown on the screen.
     * @return <code>true</code> if the component is valid; <code>false</code>
     * otherwise.
     * @see #validate
     * @see #invalidate
     * @since JDK1.0
     */
    public boolean isValid() {
	return (peer != null) && valid;
    }

    /**
     * Determines whether this component is displayable. A component is 
     * displayable when it is connected to a native screen resource.
     * <p>
     * A component is made displayable either when it is added to
     * a displayable containment hierarchy or when its containment
     * hierarchy is made displayable.
     * A containment hierarchy is made displayable when its ancestor 
     * window is either packed or made visible.
     * <p>
     * A component is made undisplayable either when it is removed from
     * a displayable containment hierarchy or when its containment hierarchy
     * is made undisplayable.  A containment hierarchy is made 
     * undisplayable when its ancestor window is disposed.
     *
     * @return <code>true</code> if the component is displayable; 
     * <code>false</code> otherwise.
     * @see java.awt.Container#add(java.awt.Component)
     * @see java.awt.Window#pack
     * @see java.awt.Window#show
     * @see java.awt.Container#remove(java.awt.Component)
     * @see java.awt.Window#dispose
     * @since JDK1.2
     */
    public boolean isDisplayable() {
	return getPeer() != null;
    }

    /**
     * Determines whether this component should be visible when its
     * parent is visible. Components are 
     * initially visible, with the exception of top level components such 
     * as <code>Frame</code> objects.
     * @return <code>true</code> if the component is visible;
     * <code>false</code> otherwise.
     * @see #setVisible
     * @since JDK1.0
     */
    public boolean isVisible() {
	return visible;
    }

    /**
     * Determines whether this component is showing on screen. This means
     * that the component must be visible, and it must be in a container
     * that is visible and showing.
     * @return <code>true</code> if the component is showing;
     * <code>false</code> otherwise.
     * @see #setVisible
     * @since JDK1.0
     */
    public boolean isShowing() {
	if (visible && (peer != null)) {
    	    Container parent = this.parent;
	    return (parent == null) || parent.isShowing();
	}
	return false;
    }

    /**
     * Determines whether this component is enabled. An enabled component
     * can respond to user input and generate events. Components are
     * enabled initially by default. A component may be enabled or disabled by
     * calling its <code>setEnabled</code> method.
     * @return <code>true</code> if the component is enabled;
     * <code>false</code> otherwise.
     * @see #setEnabled
     * @since JDK1.0
     */
    public boolean isEnabled() {
	return isEnabledImpl();
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final boolean isEnabledImpl() {
	return enabled;
    }

    /**
     * Enables or disables this component, depending on the value of the
     * parameter <code>b</code>. An enabled component can respond to user
     * input and generate events. Components are enabled initially by default.
     * @param     b   If <code>true</code>, this component is 
     *            enabled; otherwise this component is disabled.
     * @see #isEnabled
     * @since JDK1.1
     */
    public void setEnabled(boolean b) {
    	enable(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEnabled(boolean)</code>.
     */
    public void enable() {
    	if (enabled != true) {
	    synchronized (getTreeLock()) {
		enabled = true;
		ComponentPeer peer = this.peer;
		if (peer != null) {
		    peer.enable();
		}
	    }
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEnabled(boolean)</code>.
     */
    public void enable(boolean b) {
	if (b) {
	    enable();
	} else {
	    disable();
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEnabled(boolean)</code>.
     */
    public void disable() {
    	if (enabled != false) {
	    synchronized (getTreeLock()) {
		enabled = false;
		ComponentPeer peer = this.peer;
		if (peer != null) {
		    peer.disable();
		}
	    }
	}
    }

    /**
     * Returns true if this component is painted to an offscreen image
     * ("buffer") that's copied to the screen later.  Component
     * subclasses that support double buffering should override this
     * method to return true if double buffering is enabled.
     * 
     * @return false by default
     */
    public boolean isDoubleBuffered() {
        return false;
    }

    /**
     * Enables or disables input method support for this component. If input
     * method support is enabled and the component also processes key events,
     * incoming events are offered to
     * the current input method and will only be processed by the component or
     * dispatched to its listeners if the input method does not consume them.
     * By default, input method support is enabled.
     *
     * @param enable true to enable, false to disable.
     * @see java.awt.Component#processKeyEvent
     * @since JDK1.2
     */
    public void enableInputMethods(boolean enable) {
        if (enable) {
	    if ((eventMask & AWTEvent.INPUT_METHODS_ENABLED_MASK) != 0)
		return;

	    // If this component already has focus, then activate the
	    // input method by dispatching a synthesized focus gained
	    // event.
	    if (hasFocus() == true) {
		InputContext inputContext = getInputContext();
		if (inputContext != null) {
		    FocusEvent focusGainedEvent = new FocusEvent(this,
							 FocusEvent.FOCUS_GAINED);
		    inputContext.dispatchEvent(focusGainedEvent);
		}
	    }

            eventMask |= AWTEvent.INPUT_METHODS_ENABLED_MASK;
        } else {
	    if (areInputMethodsEnabled()) {
		InputContext inputContext = getInputContext();
		if (inputContext != null) {
		    inputContext.endComposition();
		    inputContext.removeNotify(this);
		}
	    }
            eventMask &= ~AWTEvent.INPUT_METHODS_ENABLED_MASK;
        }
    }

    /**
     * Shows or hides this component depending on the value of parameter
     * <code>b</code>.
     * @param b  If <code>true</code>, shows this component; 
     * otherwise, hides this component.
     * @see #isVisible
     * @since JDK1.1
     */
    public void setVisible(boolean b) {
    	show(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setVisible(boolean)</code>.
     */
    public void show() {
	if (visible != true) {
	    synchronized (getTreeLock()) {
		visible = true;
    	    	ComponentPeer peer = this.peer;
		if (peer != null) {
		    peer.show();
		    if (peer instanceof java.awt.peer.LightweightPeer) {
			repaint();
		    }
		}
                if (componentListener != null ||
                    (eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0) {
                    ComponentEvent e = new ComponentEvent(this,
                                     ComponentEvent.COMPONENT_SHOWN);
                    Toolkit.getEventQueue().postEvent(e);
                }
	    }
    	    Container parent = this.parent;
	    if (parent != null) {
		parent.invalidate();
	    }
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setVisible(boolean)</code>.
     */
    public void show(boolean b) {
	if (b) {
	    show();
	} else {
	    hide();
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setVisible(boolean)</code>.
     */
    public void hide() {
	if (visible != false) {
	    synchronized (getTreeLock()) {
		visible = false;
    	    	ComponentPeer peer = this.peer;
		if (peer != null) {
		    peer.hide();
		    if (peer instanceof java.awt.peer.LightweightPeer) {
			repaint();
		    }
		}
                if (componentListener != null ||
                    (eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0) {
                    ComponentEvent e = new ComponentEvent(this,
                                     ComponentEvent.COMPONENT_HIDDEN);
                    Toolkit.getEventQueue().postEvent(e);
                }
	    }
    	    Container parent = this.parent;
	    if (parent != null) {
		parent.invalidate();
	    }
	}
    }

    /**
     * Gets the foreground color of this component.
     * @return This component's foreground color. If this component does
     * not have a foreground color, the foreground color of its parent
     * is returned.
     * @see #java.awt.Component#setForeground(java.awt.Color)
     * @since JDK1.0
     */
    public Color getForeground() {
      	Color foreground = this.foreground;
	if (foreground != null) {
	    return foreground;
	}
    	Container parent = this.parent;
	return (parent != null) ? parent.getForeground() : null;
    }

    /**
     * Sets the foreground color of this component.
     * @param c The color to become this component's 
     * foreground color.
     * If this parameter is null then this component will inherit
     * the foreground color of its parent.
     * @see #getForeground
     * @since JDK1.0
     */
    public void setForeground(Color c) {
	Color oldColor = foreground;
	ComponentPeer peer = this.peer;
	foreground = c;
	if (peer != null) {
	    c = getForeground();
	    if (c != null) {
		peer.setForeground(c);
	    }
	}
	// This is a bound property, so report the change to
	// any registered listeners.  (Cheap if there are none.)
	firePropertyChange("foreground", oldColor, c);
    }

    /**
     * Gets the background color of this component.
     * @return This component's background color. If this component does
     * not have a background color, the background color of its parent
     * is returned.
     * @see java.awt.Component#setBackground(java.awt.Color)
     * @since JDK1.0
     */
    public Color getBackground() {
        Color background = this.background;
	if (background != null) {
	    return background;
	}
    	Container parent = this.parent;
	return (parent != null) ? parent.getBackground() : null;
    }

    /**
     * Sets the background color of this component.
     * @param c The color to become this component's color.
     *        If this parameter is null then this component will inherit
     *        the background color of its parent.
     * background color.
     * @see #getBackground
     * @since JDK1.0
     */
    public void setBackground(Color c) {
	Color oldColor = background;
	ComponentPeer peer = this.peer;
	background = c;
	if (peer != null) {
	    c = getBackground();
	    if (c != null) {
		peer.setBackground(c);
	    }
	}
	// This is a bound property, so report the change to
	// any registered listeners.  (Cheap if there are none.)
	firePropertyChange("background", oldColor, c);
    }

    /**
     * Gets the font of this component.
     * @return This component's font. If a font has not been set
     * for this component, the font of its parent is returned.
     * @see #setFont
     * @since JDK1.0
     */
    public Font getFont() {
        Font font = this.font;
	if (font != null) {
	    return font;
	}
    	Container parent = this.parent;
	return (parent != null) ? parent.getFont() : null;
    }

    // NOTE: This method may be called by privileged threads.
    //       This functionality is implemented in a package-private method 
    //       to insure that it cannot be overridden by client subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final Font getFont_NoClientCode() {
        Font font = this.font;
	if (font != null) {
	    return font;
	}
    	Container parent = this.parent;
	return (parent != null) ? parent.getFont_NoClientCode() : null;
    }

    /**
     * Sets the font of this component.
     * @param f The font to become this component's font.
     * If this parameter is null then this component will inherit
     * the font of its parent.
     * @see #getFont
     * @since JDK1.0
     */
    public void setFont(Font f) {
	synchronized (this) {
	    Font oldFont = font;
	    ComponentPeer peer = this.peer;
	    font = f;
	    if (peer != null) {
	        f = getFont();
		if (f != null) {
		    peer.setFont(f);
		    peerFont = f;
		}
	    }
	    // This is a bound property, so report the change to
	    // any registered listeners.  (Cheap if there are none.)
	    firePropertyChange("font", oldFont, font);
	}

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Gets the locale of this component.
     * @return This component's locale. If this component does not
     * have a locale, the locale of its parent is returned.
     * @see #setLocale
     * @exception IllegalComponentStateException If the Component
     * does not have its own locale and has not yet been added to
     * a containment hierarchy such that the locale can be determined
     * from the containing parent.
     * @since  JDK1.1
     */
    public Locale getLocale() {
        Locale locale = this.locale;
	if (locale != null) {
	  return locale;
	}
    	Container parent = this.parent;

	if (parent == null) {
	    throw new IllegalComponentStateException("This component must have a parent in order to determine its locale");
	} else {
	    return parent.getLocale();
	}
    }

    /**
     * Sets the locale of this component.
     * @param l The locale to become this component's locale.
     * @see #getLocale
     * @since JDK1.1
     */
    public void setLocale(Locale l) {
	locale = l;

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Gets the instance of <code>ColorModel</code> used to display
     * the component on the output device.
     * @return The color model used by this component.
     * @see java.awt.image.ColorModel
     * @see java.awt.peer.ComponentPeer#getColorModel()
     * @see java.awt.Toolkit#getColorModel()
     * @since JDK1.0
     */
    public ColorModel getColorModel() {
    	ComponentPeer peer = this.peer;
	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)) {
	    return peer.getColorModel();
	}
	return getToolkit().getColorModel();
    }

    /**
     * Gets the location of this component in the form of a
     * point specifying the component's top-left corner.
     * The location will be relative to the parent's coordinate space.
     * @return An instance of <code>Point</code> representing
     * the top-left corner of the component's bounds in the coordinate
     * space of the component's parent.
     * @see #setLocation
     * @see #getLocationOnScreen
     * @since JDK1.1
     */
    public Point getLocation() {
	return location();
    }

    /**
     * Gets the location of this component in the form of a point
     * specifying the component's top-left corner in the screen's
     * coordinate space.
     * @return An instance of <code>Point</code> representing
     * the top-left corner of the component's bounds in the
     * coordinate space of the screen.
     * @see #setLocation
     * @see #getLocation
     */
    public Point getLocationOnScreen() {
	synchronized (getTreeLock()) {
	    if (peer != null && isShowing()) {
		if (peer instanceof java.awt.peer.LightweightPeer) {
		    // lightweight component location needs to be translated
		    // relative to a native component.
		    Container host = getNativeContainer();
		    Point pt = host.peer.getLocationOnScreen();
		    for(Component c = this; c != host; c = c.getParent()) {
			pt.x += c.x;
			pt.y += c.y;
		    }
		    return pt;
		} else {
		    Point pt = peer.getLocationOnScreen();
		    return pt;
		}
	    } else {
	        throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
	    }
	}
    }


    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getLocation()</code>.
     */
    public Point location() {
	return new Point(x, y);
    }

    /**
     * Moves this component to a new location. The top-left corner of
     * the new location is specified by the <code>x</code> and <code>y</code>
     * parameters in the coordinate space of this component's parent.
     * @param x The <i>x</i>-coordinate of the new location's 
     * top-left corner in the parent's coordinate space.
     * @param y The <i>y</i>-coordinate of the new location's 
     * top-left corner in the parent's coordinate space.
     * @see #getLocation
     * @see #setBounds
     * @since JDK1.1
     */
    public void setLocation(int x, int y) {
	move(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setLocation(int, int)</code>.
     */
    public void move(int x, int y) {
	setBounds(x, y, width, height);
    }

    /**
     * Moves this component to a new location. The top-left corner of
     * the new location is specified by point <code>p</code>. Point
     * <code>p</code> is given in the parent's coordinate space.
     * @param p The point defining the top-left corner 
     * of the new location, given in the coordinate space of this 
     * component's parent.
     * @see #getLocation
     * @see #setBounds
     * @since JDK1.1
     */
    public void setLocation(Point p) {
    	setLocation(p.x, p.y);
    }

    /**
     * Returns the size of this component in the form of a
     * <code>Dimension</code> object. The <code>height</code>
     * field of the <code>Dimension</code> object contains
     * this component's height, and the <code>width</code>
     * field of the <code>Dimension</code> object contains
     * this component's width.
     * @return A <code>Dimension</code> object that indicates the
     * size of this component.
     * @see #setSize
     * @since JDK1.1
     */
    public Dimension getSize() {
	return size();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getSize()</code>.
     */
    public Dimension size() {
	return new Dimension(width, height);
    }

    /**
     * Resizes this component so that it has width <code>width</code>
     * and <code>height</code>.
     * @param width The new width of this component in pixels.
     * @param height The new height of this component in pixels.
     * @see #getSize
     * @see #setBounds
     * @since JDK1.1
     */
    public void setSize(int width, int height) {
	resize(width, height);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setSize(int, int)</code>.
     */
    public void resize(int width, int height) {
	setBounds(x, y, width, height);
    }

    /**
     * Resizes this component so that it has width <code>d.width</code>
     * and height <code>d.height</code>.
     * @param d The dimension specifying the new size 
     * of this component.
     * @see #setSize
     * @see #setBounds
     * @since JDK1.1
     */
    public void setSize(Dimension d) {
	resize(d);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setSize(Dimension)</code>.
     */
    public void resize(Dimension d) {
	setSize(d.width, d.height);
    }

    /**
     * Gets the bounds of this component in the form of a
     * <code>Rectangle</code> object. The bounds specify this
     * component's width, height, and location relative to
     * its parent.
     * @return A rectangle indicating this component's bounds.
     * @see #setBounds
     * @see #getLocation
     * @see #getSize
     */
    public Rectangle getBounds() {
	return bounds();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getBounds()</code>.
     */
    public Rectangle bounds() {
	return new Rectangle(x, y, width, height);
    }

    /**
     * Moves and resizes this component. The new location of the top-left
     * corner is specified by <code>x</code> and <code>y</code>, and the
     * new size is specified by <code>width</code> and <code>height</code>.
     * @param x The new <i>x</i>-coordinate of this component.
     * @param y The new <i>y</i>-coordinate of this component.
     * @param width The new <code>width</code> of this component.
     * @param height The new <code>height</code> of this 
     * component.
     * @see java.awt.Component#getBounds
     * @see java.awt.Component#setLocation(int, int)
     * @see java.awt.Component#setLocation(java.awt.Point)
     * @see java.awt.Component#setSize(int, int)
     * @see java.awt.Component#setSize(java.awt.Dimension)
     * @JDK1.1
     */
    public void setBounds(int x, int y, int width, int height) {
	reshape(x, y, width, height);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setBounds(int, int, int, int)</code>.
     */
    public void reshape(int x, int y, int width, int height) {
	synchronized (getTreeLock()) {
	    boolean resized = (this.width != width) || (this.height != height);
            boolean moved = (this.x != x) || (this.y != y);
	    boolean isLightweight = peer instanceof java.awt.peer.LightweightPeer;

            if (resized) {
                isPacked = false;
            }
	    if (resized || moved) {
		if (isLightweight && visible) {
                    // Have the parent redraw the area this component occupied.
		    repaint();
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (peer != null) {
		    if (isLightweight) {
			peer.setBounds(x, y, width, height);
		    } else {
			// native peer might be offset by more than direct
			// parent since parent might be lightweight.
			int nativeX = x;
			int nativeY = y;
			for(Component c = parent; (c != null) &&
			    (c.peer instanceof java.awt.peer.LightweightPeer);
			    c = c.parent) {

			    nativeX += c.x;
			    nativeY += c.y;
			}
			peer.setBounds(nativeX, nativeY, width, height);
		    }
		    if (resized) {
			invalidate();

                        if (componentListener != null ||
                           (eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0) {
                            ComponentEvent e = new ComponentEvent(this,
                                     ComponentEvent.COMPONENT_RESIZED);
                            Toolkit.getEventQueue().postEvent(e);
                        }
		    }
                    if (moved &&
                        (componentListener != null ||
                         (eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0)) {
                            ComponentEvent e = new ComponentEvent(this,
                                     ComponentEvent.COMPONENT_MOVED);
                            Toolkit.getEventQueue().postEvent(e);
                    }
		    if (parent != null && parent.valid) {
			parent.invalidate();
		    }
		}
                if (isLightweight && visible) {
                    // Have the parent redraw the area this component *now* occupies.
                    repaint();
                }
	    }
	}
    }

    /**
     * Moves and resizes this component to conform to the new
     * bounding rectangle <code>r</code>. This component's new
     * position is specified by <code>r.x</code> and <code>r.y</code>,
     * and its new size is specified by <code>r.width</code> and
     * <code>r.height</code>
     * @param r The new bounding rectangle for this component.
     * @see       java.awt.Component#getBounds
     * @see       java.awt.Component#setLocation(int, int)
     * @see       java.awt.Component#setLocation(java.awt.Point)
     * @see       java.awt.Component#setSize(int, int)
     * @see       java.awt.Component#setSize(java.awt.Dimension)
     * @since     JDK1.1
     */
    public void setBounds(Rectangle r) {
    	setBounds(r.x, r.y, r.width, r.height);
    }


    /**
     * Return the current x coordinate of the components origin.
     * This method is preferable to writing component.getBounds().x,
     * or component.getLocation().x because it doesn't cause any
     * heap allocations.
     *
     * @return the current x coordinate of the components origin.
     * @since JDK1.2
     */
    public int getX() {
	return x;
    }


    /**
     * Return the current y coordinate of the components origin.
     * This method is preferable to writing component.getBounds().y,
     * or component.getLocation().y because it doesn't cause any
     * heap allocations.
     *
     * @return the current y coordinate of the components origin.
     * @since JDK1.2
     */
    public int getY() {
	return y;
    }


    /**
     * Return the current width of this component.
     * This method is preferable to writing component.getBounds().width,
     * or component.getSize().width because it doesn't cause any
     * heap allocations.
     *
     * @return the current width of this component.
     * @since JDK1.2
     */
    public int getWidth() {
	return width;
    }


    /**
     * Return the current height of this component.
     * This method is preferable to writing component.getBounds().height,
     * or component.getSize().height because it doesn't cause any
     * heap allocations.
     *
     * @return the current height of this component.
     * @since JDK1.2
     */
    public int getHeight() {
	return height;
    }

    /** 
     * Store the bounds of this component into "return value" <b>rv</b> and 
     * return <b>rv</b>.  If rv is null a new Rectangle is allocated.
     * This version of getBounds() is useful if the caller
     * wants to avoid allocating a new Rectangle object on the heap.
     * 
     * @param rv the return value, modified to the components bounds
     * @return rv
     */
    public Rectangle getBounds(Rectangle rv) {
        if (rv == null) {
            return new Rectangle(getX(), getY(), getWidth(), getHeight());
        }
        else {
            rv.setBounds(getX(), getY(), getWidth(), getHeight());
            return rv;
        }
    }

    /**
     * Store the width/height of this component into "return value" <b>rv</b> 
     * and return <b>rv</b>.   If rv is null a new Dimension object is
     * allocated.  This version of getSize() is useful if the 
     * caller wants to avoid allocating a new Dimension object on the heap.
     * 
     * @param rv the return value, modified to the components size
     * @return rv
     */
    public Dimension getSize(Dimension rv) {
        if (rv == null) {
            return new Dimension(getWidth(), getHeight());
        }
        else {
            rv.setSize(getWidth(), getHeight());
            return rv;
        }
    }

    /**
     * Store the x,y origin of this component into "return value" <b>rv</b> 
     * and return <b>rv</b>.   If rv is null a new Point is allocated.
     * This version of getLocation() is useful if the 
     * caller wants to avoid allocating a new Point object on the heap.
     * 
     * @param rv the return value, modified to the components location
     * @return rv
     */
    public Point getLocation(Point rv) {
        if (rv == null) {
            return new Point(getX(), getY());
        }
        else {
            rv.setLocation(getX(), getY());
            return rv;
        }
    }

    /**
     * Returns true if this component is completely opaque, returns
     * false by default.
     * <p>
     * An opaque component paints every pixel within its
     * rectangular region. A non-opaque component paints only some of
     * its pixels, allowing the pixels underneath it to "show through".
     * A component that does not fully paint its pixels therefore
     * provides a degree of transparency.  Only lightweight
     * components can be transparent.
     * <p>
     * Subclasses that guarantee to always completely paint their
     * contents should override this method and return true.  All
     * of the "heavyweight" AWT components are opaque.
     *
     * @return true if this component is completely opaque.
     * @see #isLightweight
     * @since JDK1.2
     */
    public boolean isOpaque() {
	return !isLightweight();
    }


    /**
     * A lightweight component doesn't have a native toolkit peer.
     * Subclasses of Component and Container, other than the ones
     * defined in this package like Button or Scrollbar, are lightweight.
     * All of the Swing components are lightweights.
     *
     * @return true if this component doesn't have a native peer
     * @since JDK1.2
     */
    public boolean isLightweight() {
        return getPeer() instanceof java.awt.peer.LightweightPeer;
    }


    /**
     * Gets the preferred size of this component.
     * @return A dimension object indicating this component's preferred size.
     * @see #getMinimumSize
     * @see java.awt.LayoutManager
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }


    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize()</code>.
     */
    public Dimension preferredSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
    	 * is available.
	 */

    	Dimension dim = prefSize;
    	if (dim != null && isValid()) {
	    return dim;
	}

	synchronized (getTreeLock()) {
	    prefSize = (peer != null) ?
			   peer.preferredSize() :
			   getMinimumSize();
	    return prefSize;
	}
    }

    /**
     * Gets the mininimum size of this component.
     * @return A dimension object indicating this component's minimum size.
     * @see #getPreferredSize
     * @see java.awtLayoutManager
     */
    public Dimension getMinimumSize() {
      	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize()</code>.
     */
    public Dimension minimumSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
    	 * is available.
	 */
    	Dimension dim = minSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	synchronized (getTreeLock()) {
	    minSize = (peer != null) ?
			  peer.minimumSize() :
			  size();
	    return minSize;
	}
    }

    /**
     * Gets the maximum size of this component.
     * @return A dimension object indicating this component's maximum size.
     * @see #getMinimumSize
     * @see #getPreferredSize
     * @see LayoutManager
     */
    public Dimension getMaximumSize() {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getAlignmentX() {
	return CENTER_ALIGNMENT;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getAlignmentY() {
	return CENTER_ALIGNMENT;
    }

    /**
     * Prompts the layout manager to lay out this component. This is
     * usually called when the component (more specifically, container)
     * is validated.
     * @see #validate
     * @see LayoutManager
     */
    public void doLayout() {
    	layout();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>doLayout()</code>.
     */
    public void layout() {
    }

    /**
     * Ensures that this component has a valid layout.  This method is
     * primarily intended to operate on instances of <code>Container</code>.
     * @see       java.awt.Component#invalidate
     * @see       java.awt.Component#doLayout()
     * @see       java.awt.LayoutManager
     * @see       java.awt.Container#validate
     * @since     JDK1.0
     */
    public void validate() {
        if (!valid) {
	    synchronized (getTreeLock()) {
	        ComponentPeer peer = this.peer;
	        if (!valid && peer != null) {
		    Font newfont = getFont();
		    Font oldfont = peerFont;
		    if (newfont != oldfont && (oldfont == null
                                                || !oldfont.equals(newfont))) {
		        peer.setFont(newfont);
			peerFont = newfont;
		    }
		}
	    }
	    valid = true;
	}
    }

    /**
     * Invalidates this component.  This component and all parents
     * above it are marked as needing to be laid out.  This method can
     * be called often, so it needs to execute quickly.
     * @see       java.awt.Component#validate
     * @see       java.awt.Component#doLayout
     * @see       java.awt.LayoutManager
     * @since     JDK1.0
     */
    public void invalidate() {
	synchronized (getTreeLock()) {
	    /* Nullify cached layout and size information.
	     * For efficiency, propagate invalidate() upwards only if
	     * some other component hasn't already done so first.
    	     */
	    valid = false;
    	    prefSize = null;
    	    minSize = null;
	    if (parent != null && parent.valid) {
		parent.invalidate();
	    }
	}
    }

    /**
     * Creates a graphics context for this component. This method will
     * return <code>null</code> if this component is currently not on
     * the screen.
     * @return A graphics context for this component, or <code>null</code>
     *             if it has none.
     * @see       java.awt.Component#paint
     * @since     JDK1.0
     */
    public Graphics getGraphics() {
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    // This is for a lightweight component, need to
	    // translate coordinate spaces and clip relative
	    // to the parent.
	    Graphics g = parent.getGraphics();
	    if (g instanceof ConstrainableGraphics) {
		((ConstrainableGraphics) g).constrain(x, y, width, height);
	    } else {
		g.translate(x,y);
		g.setClip(0, 0, width, height);
	    }
	    g.setFont(getFont());
	    return g;
	} else {
	    ComponentPeer peer = this.peer;
	    return (peer != null) ? peer.getGraphics() : null;
	}
    }

    /**
     * Gets the font metrics for the specified font.
     * @param font The font for which font metrics is to be 
     * obtained.
     * @return The font metrics for <code>font</code>.
     * @param     font   the font.
     * @return    the font metrics for the specified font.
     * @see       java.awt.Component#getFont
     * @see       java.awt.Component#getPeer()
     * @see       java.awt.peer.ComponentPeer#getFontMetrics(java.awt.Font)
     * @see       java.awt.Toolkit#getFontMetrics(java.awt.Font)
     * @since     JDK1.0
     */
    public FontMetrics getFontMetrics(Font font) {
        if (sun.java2d.loops.RasterOutputManager.usesPlatformFont()) {
            if (peer != null &&
                !(peer instanceof java.awt.peer.LightweightPeer)) {
                return peer.getFontMetrics(font);
            }
        }
        if (parent != null) {
            Graphics g = parent.getGraphics();
            if (g != null) {
                return g.getFontMetrics(font);
            }
        }

        return getToolkit().getFontMetrics(font);
    }

    /**
     * Sets the cursor image to the specified cursor.  This cursor
     * image is displayed when the <code>contains</code> method for
     * this component returns true for the current cursor location.
     * Setting the cursor of a <code>Container</code> causes that cursor 
     * to be displayed within all of the container's subcomponents,
     * except for those that have a non-null cursor. 
     * 
     * @param cursor One of the constants defined 
     *        by the <code>Cursor</code> class.
     *        If this parameter is null then this component will inherit
     *        the cursor of its parent.
     * @see       java.awt.Component#getCursor
     * @see       java.awt.Component#contains
     * @see       java.awt.Toolkit#createCustomCursor
     * @see       java.awt.Cursor
     * @since     JDK1.1
     */
    public synchronized void setCursor(Cursor cursor) {
	this.cursor = cursor;
    	ComponentPeer peer = this.peer;
        if (peer instanceof java.awt.peer.LightweightPeer) {
            getNativeContainer().updateCursor(this);
        } else if (peer != null) {
	    peer.setCursor(cursor);
	}
    }

    /**
     * Gets the cursor set in the component. If the component does
     * not have a cursor set, the cursor of its parent is returned.
     * If no Cursor is set in the entire hierarchy, Cursor.DEFAULT_CURSOR is
     * returned.
     * @see #setCursor
     * @since      JDK1.1
     */
    public Cursor getCursor() {
        Cursor cursor = this.cursor;
        if (cursor != null) {
            return cursor;
        }
        Container parent = this.parent;
        if (parent != null) {
            return parent.getCursor();
        } else {
            return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    Cursor getIntrinsicCursor() {
	return cursor;
    }

    /**
     * Paints this component.  This method is called when the contents
     * of the component should be painted in response to the component
     * first being shown or damage needing repair.  The clip rectangle
     * in the Graphics parameter will be set to the area which needs
     * to be painted.
     * @param g The graphics context to use for painting.
     * @see       java.awt.Component#update
     * @since     JDK1.0
     */
    public void paint(Graphics g) {
    }

    /**
     * Updates this component.
     * <p>
     * The AWT calls the <code>update</code> method in response to a
     * call to <code>repaint</code. The appearance of the
     * component on the screen has not changed since the last call to
     * <code>update</code> or <code>paint</code>. You can assume that
     * the background is not cleared.
     * <p>
     * The <code>update</code>method of <code>Component</code>
     * does the following:
     * <p>
     * <blockquote><ul>
     * <li>Clears this component by filling it
     *      with the background color.
     * <li>Sets the color of the graphics context to be
     *     the foreground color of this component.
     * <li>Calls this component's <code>paint</code>
     *     method to completely redraw this component.
     * </ul></blockquote>
     * <p>
     * The origin of the graphics context, its
     * (<code>0</code>,&nbsp;<code>0</code>) coordinate point, is the
     * top-left corner of this component. The clipping region of the
     * graphics context is the bounding rectangle of this component.
     * @param g the specified context to use for updating.
     * @see       java.awt.Component#paint
     * @see       java.awt.Component#repaint()
     * @since     JDK1.0
     */
    public void update(Graphics g) {
        if ((this instanceof java.awt.Canvas) ||
            (this instanceof java.awt.Panel)  ||
            (this instanceof java.awt.Frame)  ||
            (this instanceof java.awt.Dialog) ||
            (this instanceof java.awt.Window)) {

            g.clearRect(0, 0, width, height);
        }
        paint(g);
    }

    /**
     * Paints this component and all of its subcomponents.
     * <p>
     * The origin of the graphics context, its
     * (<code>0</code>,&nbsp;<code>0</code>) coordinate point, is the
     * top-left corner of this component. The clipping region of the
     * graphics context is the bounding rectangle of this component.
     * @param     g   the graphics context to use for painting.
     * @see       java.awt.Component#paint
     * @since     JDK1.0
     */
    public void paintAll(Graphics g) {
	ComponentPeer peer = this.peer;
	if (visible && (peer != null)) {
	    validate();
	    if (peer instanceof java.awt.peer.LightweightPeer) {
		paint(g);
	    } else {
		peer.paint(g);
	    }
	}
    }

    /**
     * Repaints this component.
     * <p>
     * This method causes a call to this component's <code>update</code>
     * method as soon as possible.
     * @see       java.awt.Component#update(java.awt.Graphics)
     * @since     JDK1.0
     */
    public void repaint() {
        /*
	getToolkit().getEventQueue().removeSourceEvents(this, PaintEvent.PAINT);
	*/
	repaint(0, 0, 0, width, height);
    }

    /**
     * Repaints the component. This will result in a
     * call to <code>update</code> within <em>tm</em> milliseconds.
     * @param tm maximum time in milliseconds before update
     * @see #paint
     * @see java.awt.Component#update(java.awt.Graphics)
     * @since JDK1.0
     */
    public void repaint(long tm) {
	repaint(tm, 0, 0, width, height);
    }

    /**
     * Repaints the specified rectangle of this component.
     * <p>
     * This method causes a call to this component's <code>update</code>
     * method as soon as possible.
     * @param     x   the <i>x</i> coordinate.
     * @param     y   the <i>y</i> coordinate.
     * @param     width   the width.
     * @param     height  the height.
     * @see       java.awt.Component#update(java.awt.Graphics)
     * @since     JDK1.0
     */
    public void repaint(int x, int y, int width, int height) {
	repaint(0, x, y, width, height);
    }

    /**
     * Repaints the specified rectangle of this component within
     * <code>tm</code> milliseconds.
     * <p>
     * This method causes a call to this component's
     * <code>update</code> method.
     * @param     tm   maximum time in milliseconds before update.
     * @param     x    the <i>x</i> coordinate.
     * @param     y    the <i>y</i> coordinate.
     * @param     width    the width.
     * @param     height   the height.
     * @see       java.awt.Component#update(java.awt.Graphics)
     * @since     JDK1.0
     */
    public void repaint(long tm, int x, int y, int width, int height) {
	if (this.peer instanceof java.awt.peer.LightweightPeer) {
	    // Needs to be translated to parent coordinates since
	    // a parent native container provides the actual repaint
	    // services.  Additionally, the request is restricted to
	    // the bounds of the component.
	    int px = this.x + ((x < 0) ? 0 : x);
	    int py = this.y + ((y < 0) ? 0 : y);
	    int pwidth = (width > this.width) ? this.width : width;
	    int pheight = (height > this.height) ? this.height : height;
	    parent.repaint(tm, px, py, pwidth, pheight);
	} else {
	    ComponentPeer peer = this.peer;
	    if ((peer != null) && (width > 0) && (height > 0)) {
		peer.repaint(tm, x, y, width, height);
	    }
	}
    }

    /**
     * Prints this component. Applications should override this method
     * for components that must do special processing before being
     * printed or should be printed differently than they are painted.
     * <p>
     * The default implementation of this method calls the
     * <code>paint</code> method.
     * <p>
     * The origin of the graphics context, its
     * (<code>0</code>,&nbsp;<code>0</code>) coordinate point, is the
     * top-left corner of this component. The clipping region of the
     * graphics context is the bounding rectangle of this component.
     * @param     g   the graphics context to use for printing.
     * @see       java.awt.Component#paint(java.awt.Graphics)
     * @since     JDK1.0
     */
    public void print(Graphics g) {
	paint(g);
    }

    /**
     * Prints this component and all of its subcomponents.
     * <p>
     * The origin of the graphics context, its
     * (<code>0</code>,&nbsp;<code>0</code>) coordinate point, is the
     * top-left corner of this component. The clipping region of the
     * graphics context is the bounding rectangle of this component.
     * @param     g   the graphics context to use for printing.
     * @see       java.awt.Component#print(java.awt.Graphics)
     * @since     JDK1.0
     */
    public void printAll(Graphics g) {
	ComponentPeer peer = this.peer;
	if (visible && (peer != null)) {
	    validate();
	    Graphics cg = g.create(0, 0, width, height);
	    cg.setFont(getFont());
	    try {
	        if (peer instanceof java.awt.peer.LightweightPeer) {
		    lightweightPrint(cg);
		}
		else {
		    peer.print(cg);
		}
	    } finally {
	        cg.dispose();
	    }
	}
    }

    /**
     * Simulates the peer callbacks into java.awt for printing of
     * lightweight Components.
     * @param     g   the graphics context to use for printing.
     * @see       #printAll
     */
    void lightweightPrint(Graphics g) {
        print(g);
    }

    /**
     * Prints all the heavyweight subcomponents.
     */
    void printHeavyweightComponents(Graphics g) {
    }

    /**
     * Repaints the component when the image has changed.
     * This <code>imageUpdate</code> method of an <code>ImageObserver</code>
     * is called when more information about an
     * image which had been previously requested using an asynchronous
     * routine such as the <code>drawImage</code> method of
     * <code>Graphics</code> becomes available.
     * See the definition of <code>imageUpdate</code> for
     * more information on this method and its arguments.
     * <p>
     * The <code>imageUpdate</code> method of <code>Component</code>
     * incrementally draws an image on the component as more of the bits
     * of the image are available.
     * <p>
     * If the system property <code>awt.image.incrementalDraw</code>
     * is missing or has the value <code>true</code>, the image is
     * incrementally drawn, If the system property has any other value,
     * then the image is not drawn until it has been completely loaded.
     * <p>
     * Also, if incremental drawing is in effect, the value of the
     * system property <code>awt.image.redrawrate</code> is interpreted
     * as an integer to give the maximum redraw rate, in milliseconds. If
     * the system property is missing or cannot be interpreted as an
     * integer, the redraw rate is once every 100ms.
     * <p>
     * The interpretation of the <code>x</code>, <code>y</code>,
     * <code>width</code>, and <code>height</code> arguments depends on
     * the value of the <code>infoflags</code> argument.
     * @param     img   the image being observed.
     * @param     infoflags   see <code>imageUpdate</code> for more information.
     * @param     x   the <i>x</i> coordinate.
     * @param     y   the <i>y</i> coordinate.
     * @param     width    the width.
     * @param     height   the height.
     * @return    <code>true</code> if the flags indicate that the
     *            image is completely loaded;
     *            <code>false</code> otherwise.
     * @see     java.awt.image.ImageObserver
     * @see     java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
     * @see     java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     * @see     java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
     * @see     java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
     * @see     java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since   JDK1.0
     */
    public boolean imageUpdate(Image img, int flags,
			       int x, int y, int w, int h) {
	int rate = -1;
	if ((flags & (FRAMEBITS|ALLBITS)) != 0) {
	    rate = 0;
	} else if ((flags & SOMEBITS) != 0) {
	    if (isInc) {
		try {
		    rate = incRate;
		    if (rate < 0)
			rate = 0;
		} catch (Exception e) {
		    rate = 100;
		}
	    }
	}
	if (rate >= 0) {
	    repaint(rate, 0, 0, width, height);
	}
	return (flags & (ALLBITS|ABORT)) == 0;
    }

    /**
     * Creates an image from the specified image producer.
     * @param     producer  the image producer
     * @return    the image produced.
     * @since     JDK1.0
     */
    public Image createImage(ImageProducer producer) {
    	ComponentPeer peer = this.peer;
	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)) {
	    return peer.createImage(producer);
	}
	return getToolkit().createImage(producer);
    }

    /**
     * Creates an off-screen drawable image
     *     to be used for double buffering.
     * @param     width the specified width.
     * @param     height the specified height.
     * @return    an off-screen drawable image,
     *            which can be used for double buffering.
     * @since     JDK1.0
     */
    public Image createImage(int width, int height) {
    	ComponentPeer peer = this.peer;
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    return parent.createImage(width, height);
	} else {
	    return (peer != null) ? peer.createImage(width, height) : null;
	}
    }

    /**
     * Prepares an image for rendering on this component.  The image
     * data is downloaded asynchronously in another thread and the
     * appropriate screen representation of the image is generated.
     * @param     image   the <code>Image</code> for which to
     *                    prepare a screen representation.
     * @param     observer   the <code>ImageObserver</code> object
     *                       to be notified as the image is being prepared.
     * @return    <code>true</code> if the image has already been fully prepared;
                  <code>false</code> otherwise.
     * @since     JDK1.0
     */
    public boolean prepareImage(Image image, ImageObserver observer) {
        return prepareImage(image, -1, -1, observer);
    }

    /**
     * Prepares an image for rendering on this component at the
     * specified width and height.
     * <p>
     * The image data is downloaded asynchronously in another thread,
     * and an appropriately scaled screen representation of the image is
     * generated.
     * @param     image    the instance of <code>Image</code>
     *            for which to prepare a screen representation.
     * @param     width    the width of the desired screen representation.
     * @param     height   the height of the desired screen representation.
     * @param     observer   the <code>ImageObserver</code> object
     *            to be notified as the image is being prepared.
     * @return    <code>true</code> if the image has already been fully prepared;
                  <code>false</code> otherwise.
     * @see       java.awt.image.ImageObserver
     * @since     JDK1.0
     */
    public boolean prepareImage(Image image, int width, int height,
				ImageObserver observer) {
    	ComponentPeer peer = this.peer;
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    return parent.prepareImage(image, width, height, observer);
	} else {
	    return (peer != null)
		? peer.prepareImage(image, width, height, observer)
		: getToolkit().prepareImage(image, width, height, observer);
	}
    }

    /**
     * Returns the status of the construction of a screen representation
     * of the specified image.
     * <p>
     * This method does not cause the image to begin loading. An
     * application must use the <code>prepareImage</code> method
     * to force the loading of an image.
     * <p>
     * Information on the flags returned by this method can be found
     * with the discussion of the <code>ImageObserver</code> interface.
     * @param     image   the <code>Image</code> object whose status
     *            is being checked.
     * @param     observer   the <code>ImageObserver</code>
     *            object to be notified as the image is being prepared.
     * @return  the bitwise inclusive <b>OR</b> of
     *            <code>ImageObserver</code> flags indicating what
     *            information about the image is currently available.
     * @see      java.awt.Component#prepareImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     * @see      java.awt.Toolkit#checkImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     * @see      java.awt.image.ImageObserver
     * @since    JDK1.0
     */
    public int checkImage(Image image, ImageObserver observer) {
        return checkImage(image, -1, -1, observer);
    }

    /**
     * Returns the status of the construction of a screen representation
     * of the specified image.
     * <p>
     * This method does not cause the image to begin loading. An
     * application must use the <code>prepareImage</code> method
     * to force the loading of an image.
     * <p>
     * The <code>checkImage</code> method of <code>Component</code>
     * calls its peer's <code>checkImage</code> method to calculate
     * the flags. If this component does not yet have a peer, the
     * component's toolkit's <code>checkImage</code> method is called
     * instead.
     * <p>
     * Information on the flags returned by this method can be found
     * with the discussion of the <code>ImageObserver</code> interface.
     * @param     image   the <code>Image</code> object whose status
     *                    is being checked.
     * @param     width   the width of the scaled version
     *                    whose status is to be checked.
     * @param     height  the height of the scaled version
     *                    whose status is to be checked.
     * @param     observer   the <code>ImageObserver</code> object
     *                    to be notified as the image is being prepared.
     * @return    the bitwise inclusive <b>OR</b> of
     *            <code>ImageObserver</code> flags indicating what
     *            information about the image is currently available.
     * @see      java.awt.Component#prepareImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     * @see      java.awt.Toolkit#checkImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     * @see      java.awt.image.ImageObserver#_top_
     * @since    JDK1.0
     */
    public int checkImage(Image image, int width, int height,
			  ImageObserver observer) {
    	ComponentPeer peer = this.peer;
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    return parent.checkImage(image, width, height, observer);
	} else {
	    return (peer != null)
		? peer.checkImage(image, width, height, observer)
		: getToolkit().checkImage(image, width, height, observer);
	}
    }

    /**
     * Checks whether this component "contains" the specified point,
     * where <code>x</code> and <code>y</code> are defined to be
     * relative to the coordinate system of this component.
     * @param     x   the <i>x</i> coordinate of the point.
     * @param     y   the <i>y</i> coordinate of the point.
     * @see       java.awt.Component#getComponentAt(int, int)
     * @since     JDK1.1
     */
    public boolean contains(int x, int y) {
    	return inside(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by contains(int, int).
     */
    public boolean inside(int x, int y) {
	return (x >= 0) && (x < width) && (y >= 0) && (y < height);
    }

    /**
     * Checks whether this component "contains" the specified point,
     * where the point's <i>x</i> and <i>y</i> coordinates are defined
     * to be relative to the coordinate system of this component.
     * @param     p     the point.
     * @see       java.awt.Component#getComponentAt(java.awt.Point)
     * @since     JDK1.1
     */
    public boolean contains(Point p) {
	return contains(p.x, p.y);
    }

    /**
     * Determines if this component or one of its immediate
     * subcomponents contains the (<i>x</i>,&nbsp;<i>y</i>) location,
     * and if so, returns the containing component. This method only
     * looks one level deep. If the point (<i>x</i>,&nbsp;<i>y</i>) is
     * inside a subcomponent that itself has subcomponents, it does not
     * go looking down the subcomponent tree.
     * <p>
     * The <code>locate</code> method of <code>Component</code> simply
     * returns the component itself if the (<i>x</i>,&nbsp;<i>y</i>)
     * coordinate location is inside its bounding box, and <code>null</code>
     * otherwise.
     * @param     x   the <i>x</i> coordinate.
     * @param     y   the <i>y</i> coordinate.
     * @return    the component or subcomponent that contains the
     *                (<i>x</i>,&nbsp;<i>y</i>) location;
     *                <code>null</code> if the location
     *                is outside this component.
     * @see       java.awt.Component#contains(int, int)
     * @since     JDK1.0
     */
    public Component getComponentAt(int x, int y) {
	return locate(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getComponentAt(int, int).
     */
    public Component locate(int x, int y) {
	return contains(x, y) ? this : null;
    }

    /**
     * Returns the component or subcomponent that contains the
     * specified point.
     * @param     p   the point.
     * @see       java.awt.Component#contains
     * @since     JDK1.1
     */
    public Component getComponentAt(Point p) {
	return getComponentAt(p.x, p.y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>dispatchEvent(AWTEvent e)</code>.
     */
    public void deliverEvent(Event e) {
	postEvent(e);
    }

    /**
     * Dispatches an event to this component or one of its sub components.
     * Calls processEvent() before returning for 1.1-style events which
     * have been enabled for the Component.
     * @param e the event
     */
    public final void dispatchEvent(AWTEvent e) {
        dispatchEventImpl(e);
    }

    void dispatchEventImpl(AWTEvent e) {
        int id = e.getID();

	/*
	 * 0. Allow the Toolkit to pass this to AWTEventListeners.
	 */
	Toolkit.getDefaultToolkit().notifyAWTEventListeners(e);

        /*
	 * 1. Allow input methods to process the event
	 */
	if (areInputMethodsEnabled()
	        && (
		    // For passive clients of the input method framework
	            // we need to pass on InputMethodEvents since some host
	            // input method adapters send them through the Java
	            // event queue instead of directly to the component,
	            // and the input context also handles the Java root window
	            ((e instanceof InputMethodEvent) && (getInputMethodRequests() == null))
	            ||
	            // Otherwise, we only pass on low-level events, because
	            // a) input methods shouldn't know about semantic events
	            // b) passing on the events takes time
	            // c) isConsumed() is always true for semantic events.
	            // We exclude paint events since they may be numerous and shouldn't matter.
	            (e instanceof ComponentEvent) && !(e instanceof PaintEvent))) {
            InputContext inputContext = getInputContext();
            if (inputContext != null) {
                inputContext.dispatchEvent(e);
	        if (e.isConsumed()) {
	            return;
	        }
	    }
        }

        /*
         * 2. Pre-process any special events before delivery
         */
        switch(id) {
          // Handling of the PAINT and UPDATE events is now done in the
          // peer's handleEvent() method so the background can be cleared
          // selectively for non-native components on Windows only.
          // - Fred.Ecks@Eng.sun.com, 5-8-98

          case FocusEvent.FOCUS_GAINED:
            if (parent != null && !(this instanceof Window)) {
                parent.setFocusOwner(this);
            }
            break;

          case FocusEvent.FOCUS_LOST:
	  break;

          case KeyEvent.KEY_PRESSED:
          case KeyEvent.KEY_RELEASED:
            Container p = (Container)((this instanceof Container) ? this : parent);
            if (p != null) {
                p.preProcessKeyEvent((KeyEvent)e);
                if (e.isConsumed()) {
                    return;
                }
            }
            break;

/*
          case MouseEvent.MOUSE_PRESSED:
            if (isFocusTraversable()) {
                requestFocus();
            }
            break;
            */
          default:
            break;
        }

        /*
         * 3. Deliver event for normal processing
         */
        if (newEventsOnly) {
            // Filtering needs to really be moved to happen at a lower
            // level in order to get maximum performance gain;  it is
            // here temporarily to ensure the API spec is honored.
            //
            if (eventEnabled(e)) {
                processEvent(e);
            }

        } else if (!(e instanceof MouseEvent && !postsOldMouseEvents())) {
            //
            // backward compatibility
            //
            Event olde = e.convertToOld();
            if (olde != null) {
                int key = olde.key;
                int modifiers = olde.modifiers;

                postEvent(olde);
                if (olde.isConsumed()) {
                    e.consume();
                }
                // if target changed key or modifier values, copy them
                // back to original event
                //
                switch(olde.id) {
                  case Event.KEY_PRESS:
                  case Event.KEY_RELEASE:
                  case Event.KEY_ACTION:
                  case Event.KEY_ACTION_RELEASE:
                    if (olde.key != key) {
                       ((KeyEvent)e).setKeyChar(olde.getKeyEventChar());
                    }
                    if (olde.modifiers != modifiers) {
                       ((KeyEvent)e).setModifiers(olde.modifiers);
                    }
                    break;
                  default:
                    break;
                }
            }
        }

        /*
         * 4. If no one has consumed a key event, propagate it
         * up the containment hierarchy to ensure that menu shortcuts
         * and keyboard traversal will work properly.
         */
        if (!e.isConsumed() && e instanceof java.awt.event.KeyEvent) {
            Container p = (Container)((this instanceof Container) ? this : parent);
            if (p != null) {
                p.postProcessKeyEvent((KeyEvent)e);
            }
        }

        /*
         * 5. Allow the peer to process the event
         */
        if (peer != null) {
            peer.handleEvent(e);
        }
    } // dispatchEventImpl()

    boolean areInputMethodsEnabled() {
        // in 1.2, we assume input method support is required for all
        // components that handle key events, but components can turn off
        // input methods by calling enableInputMethods(false).
        return ((eventMask & AWTEvent.INPUT_METHODS_ENABLED_MASK) != 0) &&
            ((eventMask & AWTEvent.KEY_EVENT_MASK) != 0 || keyListener != null);
    }

    // REMIND: remove when filtering is handled at lower level
    boolean eventEnabled(AWTEvent e) {
        switch(e.id) {
          case ComponentEvent.COMPONENT_MOVED:
          case ComponentEvent.COMPONENT_RESIZED:
          case ComponentEvent.COMPONENT_SHOWN:
          case ComponentEvent.COMPONENT_HIDDEN:
            if ((eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0 ||
                componentListener != null) {
                return true;
            }
            break;
          case FocusEvent.FOCUS_GAINED:
          case FocusEvent.FOCUS_LOST:
            if ((eventMask & AWTEvent.FOCUS_EVENT_MASK) != 0 ||
                focusListener != null) {
                return true;
            }
            break;
          case KeyEvent.KEY_PRESSED:
          case KeyEvent.KEY_RELEASED:
          case KeyEvent.KEY_TYPED:
            if ((eventMask & AWTEvent.KEY_EVENT_MASK) != 0 ||
                keyListener != null) {
                return true;
            }
            break;
          case MouseEvent.MOUSE_PRESSED:
          case MouseEvent.MOUSE_RELEASED:
          case MouseEvent.MOUSE_ENTERED:
          case MouseEvent.MOUSE_EXITED:
          case MouseEvent.MOUSE_CLICKED:
            if ((eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0 ||
                mouseListener != null) {
                return true;
            }
            break;
          case MouseEvent.MOUSE_MOVED:
          case MouseEvent.MOUSE_DRAGGED:
            if ((eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0 ||
                mouseMotionListener != null) {
                return true;
            }
            break;
          case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED:
          case InputMethodEvent.CARET_POSITION_CHANGED:
            if ((eventMask & AWTEvent.INPUT_METHOD_EVENT_MASK) != 0 ||
                    inputMethodListener != null) {
                return true;
            }
            break;
          default:
            break;
        }
        //
        // Always pass on events defined by external programs.
        //
        if (e.id > AWTEvent.RESERVED_ID_MAX) {
            return true;
        }
        return false;
    }

    /**
     * Returns the Window subclass that contains this object. Will
     * return the object itself, if it is a window.
     */
    private Window getWindowForObject(Object obj) {
	if (obj instanceof Component) {
	    while (obj != null) {
	        if (obj instanceof Window) {
		    return (Window)obj;
		}
		obj = ((Component)obj).getParent();
	    }
        }
	return null;
    } // getWindowForObject()

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by dispatchEvent(AWTEvent).
     */
    public boolean postEvent(Event e) {
	ComponentPeer peer = this.peer;

	if (handleEvent(e)) {
            e.consume();
	    return true;
	}

	Component parent = this.parent;
	int eventx = e.x;
	int eventy = e.y;
	if (parent != null) {
	    e.translate(x, y);
	    if (parent.postEvent(e)) {
                e.consume();
	        return true;
	    }
	    // restore coords
   	    e.x = eventx;
	    e.y = eventy;
	}
	return false;
    }

    // Event source interfaces

    /**
     * Adds the specified component listener to receive component events from
     * this component.
     * If l is null, no exception is thrown and no action is performed.
     * @param    l   the component listener.
     * @see      java.awt.event.ComponentEvent
     * @see      java.awt.event.ComponentListener
     * @see      java.awt.Component#removeComponentListener
     * @since    JDK1.1
     */
    public synchronized void addComponentListener(ComponentListener l) {
	if (l == null) {
	    return;
	}
        componentListener = AWTEventMulticaster.add(componentListener, l);
        newEventsOnly = true;
    }
    /**
     * Removes the specified component listener so that it no longer
     * receives component events from this component. This method performs 
     * no function, nor does it throw an exception, if the listener 
     * specified by the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     * @param    l   the component listener.
     * @see      java.awt.event.ComponentEvent
     * @see      java.awt.event.ComponentListener
     * @see      java.awt.Component#addComponentListener
     * @since    JDK1.1
     */
    public synchronized void removeComponentListener(ComponentListener l) {
	if (l == null) {
	    return;
	}
        componentListener = AWTEventMulticaster.remove(componentListener, l);
    }

    /**
     * Adds the specified focus listener to receive focus events from
     * this component when this component gains input focus.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the focus listener.
     * @see      java.awt.event.FocusEvent
     * @see      java.awt.event.FocusListener
     * @see      java.awt.Component#removeFocusListener
     * @since    JDK1.1
     */
    public synchronized void addFocusListener(FocusListener l) {
	if (l == null) {
	    return;
	}
        focusListener = AWTEventMulticaster.add(focusListener, l);
        newEventsOnly = true;

	// if this is a lightweight component, enable focus events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.FOCUS_EVENT_MASK);
	}
    }

    /**
     * Removes the specified focus listener so that it no longer
     * receives focus events from this component. This method performs 
     * no function, nor does it throw an exception, if the listener 
     * specified by the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the focus listener.
     * @see      java.awt.event.FocusEvent
     * @see      java.awt.event.FocusListener
     * @see      java.awt.Component#addFocusListener
     * @since    JDK1.1
     */
    public synchronized void removeFocusListener(FocusListener l) {
	if (l == null) {
	    return;
	}
        focusListener = AWTEventMulticaster.remove(focusListener, l);
    }

    /**
     * Adds the specified key listener to receive key events from
     * this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the key listener.
     * @see      java.awt.event.KeyEvent
     * @see      java.awt.event.KeyListener
     * @see      java.awt.Component#removeKeyListener
     * @since    JDK1.1
     */
    public synchronized void addKeyListener(KeyListener l) {
	if (l == null) {
	    return;
	}
        keyListener = AWTEventMulticaster.add(keyListener, l);
        newEventsOnly = true;

	// if this is a lightweight component, enable key events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.KEY_EVENT_MASK);
	}
    }

    /**
     * Removes the specified key listener so that it no longer
     * receives key events from this component. This method performs 
     * no function, nor does it throw an exception, if the listener 
     * specified by the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the key listener.
     * @see      java.awt.event.KeyEvent
     * @see      java.awt.event.KeyListener
     * @see      java.awt.Component#addKeyListener
     * @since    JDK1.1
     */
    public synchronized void removeKeyListener(KeyListener l) {
	if (l == null) {
	    return;
	}
        keyListener = AWTEventMulticaster.remove(keyListener, l);
    }

    /**
     * Adds the specified mouse listener to receive mouse events from
     * this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the mouse listener.
     * @see      java.awt.event.MouseEvent
     * @see      java.awt.event.MouseListener
     * @see      java.awt.Component#removeMouseListener
     * @since    JDK1.1
     */
    public synchronized void addMouseListener(MouseListener l) {
	if (l == null) {
	    return;
	}
        mouseListener = AWTEventMulticaster.add(mouseListener,l);
        newEventsOnly = true;

	// if this is a lightweight component, enable mouse events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
    }

    /**
     * Removes the specified mouse listener so that it no longer
     * receives mouse events from this component. This method performs 
     * no function, nor does it throw an exception, if the listener 
     * specified by the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the mouse listener.
     * @see      java.awt.event.MouseEvent
     * @see      java.awt.event.MouseListener
     * @see      java.awt.Component#addMouseListener
     * @since    JDK1.1
     */
    public synchronized void removeMouseListener(MouseListener l) {
	if (l == null) {
	    return;
	}
        mouseListener = AWTEventMulticaster.remove(mouseListener, l);
    }

    /**
     * Adds the specified mouse motion listener to receive mouse motion events from
     * this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the mouse motion listener.
     * @see      java.awt.event.MouseMotionEvent
     * @see      java.awt.event.MouseMotionListener
     * @see      java.awt.Component#removeMouseMotionListener
     * @since    JDK1.1
     */
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
	if (l == null) {
	    return;
	}
        mouseMotionListener = AWTEventMulticaster.add(mouseMotionListener,l);
        newEventsOnly = true;

	// if this is a lightweight component, enable mouse events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
    }

    /**
     * Removes the specified mouse motion listener so that it no longer
     * receives mouse motion events from this component. This method performs 
     * no function, nor does it throw an exception, if the listener 
     * specified by the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the mouse motion listener.
     * @see      java.awt.event.MouseMotionEvent
     * @see      java.awt.event.MouseMotionListener
     * @see      java.awt.Component#addMouseMotionListener
     * @since    JDK1.1
     */
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
	if (l == null) {
	    return;
	}
        mouseMotionListener = AWTEventMulticaster.remove(mouseMotionListener, l);
    }

    /**
     * Adds the specified input method listener to receive
     * input method events from this component. A component will
     * only receive input method events from input methods
     * if it also overrides getInputMethodRequests to return an
     * InputMethodRequests instance.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the input method listener.
     * @see      java.awt.event.InputMethodEvent
     * @see      java.awt.event.InputMethodListener
     * @see      java.awt.Component#removeInputMethodListener
     * @see      java.awt.Component#getInputMethodRequests
     * @since    JDK1.2
     */
    public synchronized void addInputMethodListener(InputMethodListener l) {
	if (l == null) {
	    return;
	}
        inputMethodListener = AWTEventMulticaster.add(inputMethodListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified input method listener so that it no longer receives
     * input method events from this component. This method performs 
     * no function, nor does it throw an exception, if the listener 
     * specified by the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the input method listener.
     * @see      java.awt.event.InputMethodEvent
     * @see      java.awt.event.InputMethodListener
     * @see      java.awt.Component#addInputMethodListener
     * @since    JDK1.2
     */
    public synchronized void removeInputMethodListener(InputMethodListener l) {
	if (l == null) {
	    return;
	}
        inputMethodListener = AWTEventMulticaster.remove(inputMethodListener, l);
    }

    /**
     * Gets the input method request handler which supports
     * requests from input methods for this component. A component
     * that supports on-the-spot text input must override this
     * method to return an InputMethodRequests instance. At the same
     * time, it also has to handle input method events.
     *
     * @return the input method request handler for this component,
     * null by default.
     * @see #addInputMethodListener
     * @since JDK1.2
     */
    public InputMethodRequests getInputMethodRequests() {
        return null;
    }

    /**
     * Gets the input context used by this component for handling the communication
     * with input methods when text is entered in this component. By default, the
     * input context used for the parent component is returned. Components may
     * override this to return a private input context.
     *
     * @return The input context used by this component. Null if no context can
     * be determined.
     * @since JDK1.2
     */
    public InputContext getInputContext() {
        Container parent = this.parent;
        if (parent == null) {
            return null;
        } else {
            return parent.getInputContext();
        }
    }

    /**
     * Enables the events defined by the specified event mask parameter
     * to be delivered to this component.
     * <p>
     * Event types are automatically enabled when a listener for
     * that event type is added to the component.
     * <p>
     * This method only needs to be invoked by subclasses of
     * <code>Component</code> which desire to have the specified event
     * types delivered to <code>processEvent</code> regardless of whether
     * or not a listener is registered.
     * @param      eventsToEnable   the event mask defining the event types.
     * @see        java.awt.Component#processEvent
     * @see        java.awt.Component#disableEvents
     * @since      JDK1.1
     */
    protected final void enableEvents(long eventsToEnable) {
        eventMask |= eventsToEnable;
        newEventsOnly = true;

	// if this is a lightweight component, enable mouse events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(eventMask);
	}
    }

    /**
     * Disables the events defined by the specified event mask parameter
     * from being delivered to this component.
     * @param      eventsToDisable   the event mask defining the event types
     * @see        java.awt.Component#enableEvents
     * @since      JDK1.1
     */
    protected final void disableEvents(long eventsToDisable) {
        eventMask &= ~eventsToDisable;
    }

    /**
     * Potentially coalesce an event being posted with an existing
     * event.  This method is called by EventQueue.postEvent if an
     * event with the same ID as the event to be posted is found in
     * the queue (both events must have this component as their source).
     * This method either returns a coalesced event which replaces
     * the existing event (and the new event is then discarded), or
     * null to indicate that no combining should be done (add the
     * second event to the end of the queue).  Either event parameter
     * may be modified and returned, as the other one is discarded
     * unless null is returned.
     * <p>
     * This implementation of coalesceEvents coalesces two event types:
     * mouse move (and drag) events, and paint (and update) events.
     * For mouse move events the last event is always returned, causing
     * intermediate moves to be discarded. For paint events where the
     * update rectangle of one paint event is completely contained within
     * the update rectangle of the other paint event, the event with the
     * smaller rectangle is discarded.
     *
     * @param  existingEvent  the event already on the EventQueue.
     * @param  newEvent       the event being posted to the EventQueue.
     * @return a coalesced event, or null indicating that no coalescing
     *         was done.
     */
    protected AWTEvent coalesceEvents(AWTEvent existingEvent,
                                      AWTEvent newEvent) {
        int id = existingEvent.getID();
        if (assert) {
            // Enable assert to perform this extra sanity check, but it's too
            // expensive for normal operation.
            if (id != newEvent.getID() ||
                !(existingEvent.getSource().equals(newEvent.getSource()))) {
                // Only coalesce events of the same type and source.
                return null;
            }
        }

        switch (id) {
          case Event.MOUSE_MOVE:
          case Event.MOUSE_DRAG: {
              MouseEvent e = (MouseEvent)existingEvent;
              if (e.getModifiers() == ((MouseEvent)newEvent).getModifiers()) {
                  // Just return the newEvent, causing the old to be
                  // discarded.
                  return newEvent;
              }
              break;
          }
          case PaintEvent.PAINT:
          case PaintEvent.UPDATE: {
              // This approach to coalescing paint events seems to be
              // better than any heuristic for unioning rectangles.
 
              PaintEvent existingPaintEvent = (PaintEvent) existingEvent;
              PaintEvent newPaintEvent = (PaintEvent) newEvent;
              Rectangle existingRect = existingPaintEvent.getUpdateRect();
              Rectangle newRect = newPaintEvent.getUpdateRect();
              
              if (existingRect.contains(newRect)) {
                  return existingEvent;
              }
              if (newRect.contains(existingRect)) {
                  return newEvent;
              }
 
              break;
          }
        }
        return null;
    }

    /**
     * Processes events occurring on this component. By default this
     * method calls the appropriate
     * <code>process&lt;event&nbsp;type&gt;Event</code>
     * method for the given class of event.
     * @param     e the event.
     * @see       java.awt.Component#processComponentEvent
     * @see       java.awt.Component#processFocusEvent
     * @see       java.awt.Component#processKeyEvent
     * @see       java.awt.Component#processMouseEvent
     * @see       java.awt.Component#processMouseMotionEvent
     * @see       java.awt.Component#processInputMethodEvent
     * @since     JDK1.1
     */
    protected void processEvent(AWTEvent e) {

        //System.err.println("Component.processNewEvent:" + e);
        if (e instanceof FocusEvent) {
            processFocusEvent((FocusEvent)e);

        } else if (e instanceof MouseEvent) {
            switch(e.getID()) {
              case MouseEvent.MOUSE_PRESSED:
              case MouseEvent.MOUSE_RELEASED:
              case MouseEvent.MOUSE_CLICKED:
              case MouseEvent.MOUSE_ENTERED:
              case MouseEvent.MOUSE_EXITED:
                processMouseEvent((MouseEvent)e);
                break;
              case MouseEvent.MOUSE_MOVED:
              case MouseEvent.MOUSE_DRAGGED:
                processMouseMotionEvent((MouseEvent)e);
                break;
            }

        } else if (e instanceof KeyEvent) {
            processKeyEvent((KeyEvent)e);

        } else if (e instanceof ComponentEvent) {
            processComponentEvent((ComponentEvent)e);
        } else if (e instanceof InputMethodEvent) {
            processInputMethodEvent((InputMethodEvent)e);
        }
    }

    /**
     * Processes component events occurring on this component by
     * dispatching them to any registered
     * <code>ComponentListener</code> objects.
     * <p>
     * This method is not called unless component events are
     * enabled for this component. Component events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>A <code>ComponentListener</code> object is registered
     * via <code>addComponentListener</code>.
     * <li>Component events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the component event.
     * @see         java.awt.event.ComponentEvent
     * @see         java.awt.event.ComponentListener
     * @see         java.awt.Component#addComponentListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */
    protected void processComponentEvent(ComponentEvent e) {
	ComponentListener listener = componentListener;
        if (listener != null) {
            int id = e.getID();
            switch(id) {
              case ComponentEvent.COMPONENT_RESIZED:
                listener.componentResized(e);
                break;
              case ComponentEvent.COMPONENT_MOVED:
                listener.componentMoved(e);
                break;
              case ComponentEvent.COMPONENT_SHOWN:
                listener.componentShown(e);
                break;
              case ComponentEvent.COMPONENT_HIDDEN:
                listener.componentHidden(e);
                break;
            }
        }
    }

    /**
     * Processes focus events occurring on this component by
     * dispatching them to any registered
     * <code>FocusListener</code> objects.
     * <p>
     * This method is not called unless focus events are
     * enabled for this component. Focus events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>A <code>FocusListener</code> object is registered
     * via <code>addFocusListener</code>.
     * <li>Focus events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the focus event.
     * @see         java.awt.event.FocusEvent
     * @see         java.awt.event.FocusListener
     * @see         java.awt.Component#addFocusListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */
    protected void processFocusEvent(FocusEvent e) {
        FocusListener listener = focusListener;
        if (listener != null) {
            int id = e.getID();
            switch(id) {
              case FocusEvent.FOCUS_GAINED:
                listener.focusGained(e);
                break;
              case FocusEvent.FOCUS_LOST:
                listener.focusLost(e);
                break;
            }
        }
    }

    /**
     * Processes key events occurring on this component by
     * dispatching them to any registered
     * <code>KeyListener</code> objects.
     * <p>
     * This method is not called unless key events are
     * enabled for this component. Key events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>A <code>KeyListener</code> object is registered
     * via <code>addKeyListener</code>.
     * <li>Key events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the key event.
     * @see         java.awt.event.KeyEvent
     * @see         java.awt.event.KeyListener
     * @see         java.awt.Component#addKeyListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */
    protected void processKeyEvent(KeyEvent e) {
	KeyListener listener = keyListener;
        if (listener != null) {
            int id = e.getID();
            switch(id) {
              case KeyEvent.KEY_TYPED:
                listener.keyTyped(e);
                break;
              case KeyEvent.KEY_PRESSED:
                listener.keyPressed(e);
                break;
              case KeyEvent.KEY_RELEASED:
                listener.keyReleased(e);
                break;
            }
        }
    }

    /**
     * Processes mouse events occurring on this component by
     * dispatching them to any registered
     * <code>MouseListener</code> objects.
     * <p>
     * This method is not called unless mouse events are
     * enabled for this component. Mouse events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>A <code>MouseListener</code> object is registered
     * via <code>addMouseListener</code>.
     * <li>Mouse events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the mouse event.
     * @see         java.awt.event.MouseEvent
     * @see         java.awt.event.MouseListener
     * @see         java.awt.Component#addMouseListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */
    protected void processMouseEvent(MouseEvent e) {
	MouseListener listener = mouseListener;
        if (listener != null) {
            int id = e.getID();
            switch(id) {
              case MouseEvent.MOUSE_PRESSED:
                listener.mousePressed(e);
                break;
              case MouseEvent.MOUSE_RELEASED:
                listener.mouseReleased(e);
                break;
              case MouseEvent.MOUSE_CLICKED:
                listener.mouseClicked(e);
                break;
              case MouseEvent.MOUSE_EXITED:
                listener.mouseExited(e);
                break;
              case MouseEvent.MOUSE_ENTERED:
                listener.mouseEntered(e);
                break;
            }
        }
    }

    /**
     * Processes mouse motion events occurring on this component by
     * dispatching them to any registered
     * <code>MouseMotionListener</code> objects.
     * <p>
     * This method is not called unless mouse motion events are
     * enabled for this component. Mouse motion events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>A <code>MouseMotionListener</code> object is registered
     * via <code>addMouseMotionListener</code>.
     * <li>Mouse motion events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the mouse motion event.
     * @see         java.awt.event.MouseMotionEvent
     * @see         java.awt.event.MouseMotionListener
     * @see         java.awt.Component#addMouseMotionListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */
    protected void processMouseMotionEvent(MouseEvent e) {
	MouseMotionListener listener = mouseMotionListener;
        if (listener != null) {
            int id = e.getID();
            switch(id) {
              case MouseEvent.MOUSE_MOVED:
                listener.mouseMoved(e);
                break;
              case MouseEvent.MOUSE_DRAGGED:
                listener.mouseDragged(e);
                break;
            }
        }
    }

    boolean postsOldMouseEvents() {
        return false;
    }

    /**
     * Processes input method events occurring on this component by
     * dispatching them to any registered
     * <code>InputMethodListener</code> objects.
     * <p>
     * This method is not called unless input method events
     * are enabled for this component. Input method events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>An <code>InputMethodListener</code> object is registered
     * via <code>addInputMethodListener</code>.
     * <li>Input method events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the input method event
     * @see         java.awt.event.InputMethodEvent
     * @see         java.awt.event.InputMethodListener
     * @see         java.awt.Component#addInputMethodListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.2
     */
    protected void processInputMethodEvent(InputMethodEvent e) {
	InputMethodListener listener = inputMethodListener;
        if (listener != null) {
            int id = e.getID();
            switch (id) {
              case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED:
                listener.inputMethodTextChanged(e);
                break;
              case InputMethodEvent.CARET_POSITION_CHANGED:
                listener.caretPositionChanged(e);
                break;
            }
        }
    }

    /**
     * @deprecated As of JDK version 1.1
     * replaced by processEvent(AWTEvent).
     */
    public boolean handleEvent(Event evt) {
	switch (evt.id) {
	  case Event.MOUSE_ENTER:
	    return mouseEnter(evt, evt.x, evt.y);

	  case Event.MOUSE_EXIT:
	    return mouseExit(evt, evt.x, evt.y);

	  case Event.MOUSE_MOVE:
	    return mouseMove(evt, evt.x, evt.y);

	  case Event.MOUSE_DOWN:
	    return mouseDown(evt, evt.x, evt.y);

	  case Event.MOUSE_DRAG:
	    return mouseDrag(evt, evt.x, evt.y);

	  case Event.MOUSE_UP:
	    return mouseUp(evt, evt.x, evt.y);

	  case Event.KEY_PRESS:
	  case Event.KEY_ACTION:
	    return keyDown(evt, evt.key);

	  case Event.KEY_RELEASE:
	  case Event.KEY_ACTION_RELEASE:
	    return keyUp(evt, evt.key);

	  case Event.ACTION_EVENT:
	    return action(evt, evt.arg);
	  case Event.GOT_FOCUS:
	    return gotFocus(evt, evt.arg);
	  case Event.LOST_FOCUS:
	    return lostFocus(evt, evt.arg);
	}
        return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processMouseEvent(MouseEvent).
     */
    public boolean mouseDown(Event evt, int x, int y) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processMouseMotionEvent(MouseEvent).
     */
    public boolean mouseDrag(Event evt, int x, int y) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processMouseEvent(MouseEvent).
     */
    public boolean mouseUp(Event evt, int x, int y) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processMouseMotionEvent(MouseEvent).
     */
    public boolean mouseMove(Event evt, int x, int y) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processMouseEvent(MouseEvent).
     */
    public boolean mouseEnter(Event evt, int x, int y) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processMouseEvent(MouseEvent).
     */
    public boolean mouseExit(Event evt, int x, int y) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processKeyEvent(KeyEvent).
     */
    public boolean keyDown(Event evt, int key) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processKeyEvent(KeyEvent).
     */
    public boolean keyUp(Event evt, int key) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * should register this component as ActionListener on component
     * which fires action events.
     */
    public boolean action(Event evt, Object what) {
	return false;
    }

    /**
     * Makes this Component displayable by connecting it to a
     * native screen resource.  
     * This method is called internally by the toolkit and should
     * not be called directly by programs.
     * @see       java.awt.Component#isDisplayable
     * @see       java.awt.Component#removeNotify
     * @since JDK1.0
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    ComponentPeer peer = this.peer;
	    if (peer == null || peer instanceof java.awt.peer.LightweightPeer){
	        if (peer == null) {
		    // Update both the Component's peer variable and the local
		  // variable we use for thread safety.
		  this.peer = peer = getToolkit().createComponent(this);
		}

		// This is a lightweight component which means it won't be
		// able to get window-related events by itself.  If any
		// have been enabled, then the nearest native container must
		// be enabled.
		long mask = 0;
		if ((mouseListener != null) || ((eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0)) {
		    mask |= AWTEvent.MOUSE_EVENT_MASK;
		}
		if ((mouseMotionListener != null) ||
		    ((eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0)) {
		    mask |= AWTEvent.MOUSE_MOTION_EVENT_MASK;
		}
		if (focusListener != null || (eventMask & AWTEvent.FOCUS_EVENT_MASK) != 0) {
		    mask |= AWTEvent.FOCUS_EVENT_MASK;
		}
		if (keyListener != null || (eventMask & AWTEvent.KEY_EVENT_MASK) != 0) {
		    mask |= AWTEvent.KEY_EVENT_MASK;
		}
		if (mask != 0) {
		    parent.proxyEnableEvents(mask);
		}
	    } else {
	        // It's native.  If the parent is lightweight it
	        // will need some help.
	        Container parent = this.parent;
		if (parent != null && parent.peer instanceof java.awt.peer.LightweightPeer) {
		    new NativeInLightFixer();
		}
	    }
	    invalidate();

	    int npopups = (popups != null? popups.size() : 0);
	    for (int i = 0 ; i < npopups ; i++) {
	        PopupMenu popup = (PopupMenu)popups.elementAt(i);
		popup.addNotify();
	    }
	    for (Component p = getParent(); p != null; p = p.getParent())
                if (p instanceof Window) {
		    if (((Window)p).getWarningString() == null) {
		        //!CQ set newEventsOnly if appropriate/possible?
		    }
		    break;
		}
	    
	    if (dropTarget != null) dropTarget.addNotify(peer);

	    peerFont = getFont();
	}
    }

    /** 
     * Makes this Component undisplayable by destroying it native
     * screen resource. 
     * This method is called by the toolkit internally and should
     * not be called directly by programs.
     * @see       java.awt.Component#isDisplayable
     * @see       java.awt.Component#addNotify
     * @since JDK1.0
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
	    int npopups = (popups != null? popups.size() : 0);
	    for (int i = 0 ; i < npopups ; i++) {
	        PopupMenu popup = (PopupMenu)popups.elementAt(i);
		popup.removeNotify();
	    }
	    // If there is any input context for this component, notify
	    // that this component is being removed. (This has to be done
	    // before hiding peer.)
	    if (areInputMethodsEnabled()) {
	        InputContext inputContext = getInputContext();
		if (inputContext != null) {
		    inputContext.removeNotify(this);
		}
	    }

	    ComponentPeer p = peer;
	    if (p != null) {

	        if (dropTarget != null) dropTarget.removeNotify(peer);

		// Hide peer first to stop system events such as cursor moves.
		if (visible) {
		    p.hide();
		}

		peer = null; // Stop peer updates.
		peerFont = null;

		Toolkit.getEventQueue().removeSourceEvents(this);


		p.dispose();
	    }
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processFocusEvent(FocusEvent).
     */
    public boolean gotFocus(Event evt, Object what) {
	return false;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by processFocusEvent(FocusEvent).
     */
    public boolean lostFocus(Event evt, Object what) {
	return false;
    }

    /**
     * Returns the value of a flag that indicates whether
     * this component can be traversed using
     * Tab or Shift-Tab keyboard focus traversal.  If this method
     * returns "false", this component may still request the keyboard
     * focus using <code>requestFocus()</code>, but it will not automatically
     * be assigned focus during tab traversal.
     * @return    <code>true</code> if this component is
     *            focus-traverable; <code>false</code> otherwise.
     * @since     JDK1.1
     */
    public boolean isFocusTraversable() {
    	ComponentPeer peer = this.peer;
	if (peer != null) {
	    return peer.isFocusTraversable();
	}
	return false;
    }

    /**
     * Requests that this component get the input focus.
     * The component must be visible
     * on the screen for this request to be granted
     * @see FocusEvent
     * @see #addFocusListener
     * @see #processFocusEvent
     * @see #isFocusTraversable
     * @since JDK1.0
     */
    public void requestFocus() {
    	ComponentPeer peer = this.peer;
	if (peer != null) {
	    if (peer instanceof java.awt.peer.LightweightPeer) {
		parent.proxyRequestFocus(this);
	    } else {
		peer.requestFocus();
                Toolkit.getEventQueue().changeKeyEventFocus(this);
	    }
	}
    }

    /**
     * Transfers the focus to the next component.
     * @see       java.awt.Component#requestFocus
     * @since     JDK1.1s
     */
     public void transferFocus() {
    	nextFocus();
     }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by transferFocus().
     */
     public void nextFocus() {
    	Container parent = this.parent;
	if (parent != null) {
	    parent.transferFocus(this);
	}
     }

    /**
     * Returns true if this Component has the keyboard focus.
     *
     * @return true if this Component has the keyboard focus.
     * @since JDK1.2
     */
    public boolean hasFocus() {
	if ((eventMask & AWTEvent.FOCUS_EVENT_MASK) != 0) {
	    return hasFocus;
	}
	else {
	    for (Container p = getParent(); p != null; p = p.getParent()) {
		if (p instanceof Window) {
		    return ((Window)p).getFocusOwner() == this;
		}
	    }
	    return false;
	}
    }

    /**
     * Adds the specified popup menu to the component.
     * @param     popup the popup menu to be added to the component.
     * @see       java.awt.Component#remove(java.awt.MenuComponent)
     * @since     JDK1.1
     */
    public synchronized void add(PopupMenu popup) {
	if (popup.parent != null) {
	    popup.parent.remove(popup);
	}
        if (popups == null) {
            popups = new Vector();
        }
	popups.addElement(popup);
	popup.parent = this;

	if (peer != null) {
	    if (popup.peer == null) {
		popup.addNotify();
	    }
	}
    }

    /**
     * Removes the specified popup menu from the component.
     * @param     popup the popup menu to be removed.
     * @see       java.awt.Component#add(java.awt.PopupMenu)
     * @since     JDK1.1
     */
    public synchronized void remove(MenuComponent popup) {
        if (popups != null) {
	    int index = popups.indexOf(popup);
	    if (index >= 0) {
	        PopupMenu pmenu = (PopupMenu)popup;
	        if (pmenu.peer != null) {
		    pmenu.removeNotify();
		}
		pmenu.parent = null;
		popups.removeElementAt(index);
		if (popups.size() == 0) {
		    popups = null;
		}
	    }
	}
    }

    /**
     * Returns a string representing the state of this component. This 
     * method is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between 
     * implementations. The returned string may be empty but may not be 
     * <code>null</code>.
     * 
     * @return  a string representation of this component's state.
     * @since     JDK1.0
     */
    protected String paramString() {
        String thisName = getName();
	String str = (thisName != null? thisName : "") + "," + x + "," + y + "," + width + "x" + height;
	if (!valid) {
	    str += ",invalid";
	}
	if (!visible) {
	    str += ",hidden";
	}
	if (!enabled) {
	    str += ",disabled";
	}
	return str;
    }

    /**
     * Returns a string representation of this component and its values.
     * @return    a string representation of this component.
     * @since     JDK1.0
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }

    /**
     * Prints a listing of this component to the standard system output
     * stream <code>System.out</code>.
     * @see       java.lang.System#out
     * @since     JDK1.0
     */
    public void list() {
	list(System.out, 0);
    }

    /**
     * Prints a listing of this component to the specified output
     * stream.
     * @param    out   a print stream.
     * @since    JDK1.0
     */
    public void list(PrintStream out) {
	list(out, 0);
    }

    /**
     * Prints out a list, starting at the specified indention, to the
     * specified print stream.
     * @param     out      a print stream.
     * @param     indent   number of spaces to indent.
     * @see       java.io.PrintStream#println(java.lang.Object)
     * @since     JDK1.0
     */
    public void list(PrintStream out, int indent) {
	for (int i = 0 ; i < indent ; i++) {
	    out.print("  ");
	}
	out.println(this);
    }

    /**
     * Prints a listing to the specified print writer.
     * @param  out  The print writer to print to.
     * @since JDK1.1
     */
    public void list(PrintWriter out) {
	list(out, 0);
    }

    /**
     * Prints out a list, starting at the specified indention, to
     * the specified print writer.
     * @param out The print writer to print to.
     * @param indent The number of spaces to indent.
     * @see       java.io.PrintStream#println(java.lang.Object)
     * @since JDK1.1
     */
    public void list(PrintWriter out, int indent) {
	for (int i = 0 ; i < indent ; i++) {
	    out.print("  ");
	}
	out.println(this);
    }

    /*
     * Fetch the native container somewhere higher up in the component
     * tree that contains this component.
     */
    Container getNativeContainer() {
	Container p = parent;
	while (p != null && p.peer instanceof java.awt.peer.LightweightPeer) {
	    p = p.getParent();
	}
	return p;
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired in response to an
     * explicit setFont, setBackground, or SetForeground on the
     * current component.  Note that if the current component is
     * inheriting its foreground, background, or font from its
     * container, then no event will be fired in response to a
     * change in the inherited property.
     *
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param    listener  The PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(
				PropertyChangeListener listener) {
	if (listener == null) {
	    return;
	}
	if (changeSupport == null) {
	    changeSupport = new java.beans.PropertyChangeSupport(this);
	}
	changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    public synchronized void removePropertyChangeListener(
				PropertyChangeListener listener) {
	if (listener == null) {
	    return;
	}
	if (changeSupport == null) {
	    return;
	}
	changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     *
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(
				String propertyName,
				PropertyChangeListener listener) {
	if (listener == null) {
	    return;
	}
	if (changeSupport == null) {
	    changeSupport = new java.beans.PropertyChangeSupport(this);
	}
	changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    public synchronized void removePropertyChangeListener(
				String propertyName,
				PropertyChangeListener listener) {
	if (listener == null) {
	    return;
	}
	if (changeSupport == null) {
	    return;
	}
	changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Support for reporting bound property changes.  This method can be called
     * when a bound property has changed and it will send the appropriate
     * PropertyChangeEvent to any registered PropertyChangeListeners.
     */
    protected void firePropertyChange(String propertyName,
					Object oldValue, Object newValue) {
        java.beans.PropertyChangeSupport changeSupport = this.changeSupport;
	if (changeSupport == null) {
	    return;
	}
	changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    /* Serialization support.
     */
    /**
     * Component Serialized Data Version.
     *
     * @serial
     */
    private int componentSerializedDataVersion = 2;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable ItemListener(s) as optional data.
     * The non-serializable ItemListener(s) are detected and
     * no attempt is made to serialize them.
     *
     * @serialData Null terminated sequence of 0 or more pairs.
     *             The pair consists of a String and Object.
     *             The String indicates the type of object and
     *             is one of the following :
     *             itemListenerK indicating and ItemListener object.
     *
     * @see AWTEventMulticaster.save(ObjectOutputStream, String, EventListener)
     * @see java.awt.Component.itemListenerK
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, componentListenerK, componentListener);
      AWTEventMulticaster.save(s, focusListenerK, focusListener);
      AWTEventMulticaster.save(s, keyListenerK, keyListener);
      AWTEventMulticaster.save(s, mouseListenerK, mouseListener);
      AWTEventMulticaster.save(s, mouseMotionListenerK, mouseMotionListener);
      AWTEventMulticaster.save(s, inputMethodListenerK, inputMethodListener);

      s.writeObject(null);
      s.writeObject(componentOrientation);
    }

    /**
     * Read the ObjectInputStream and if it isnt null
     * add a listener to receive item events fired
     * by the components.
     * Unrecognised keys or values will be Ignored.
     *
     * @see removeActionListener()
     * @see addActionListener()
     */
    private void readObject(ObjectInputStream s)
        throws ClassNotFoundException, IOException
    {
        s.defaultReadObject();

	appContext = AppContext.getAppContext();
	SunToolkit.insertTargetMapping(this, appContext);

        Object keyOrNull;
        while(null != (keyOrNull = s.readObject())) {
	    String key = ((String)keyOrNull).intern();

	    if (componentListenerK == key)
	        addComponentListener((ComponentListener)(s.readObject()));

	    else if (focusListenerK == key)
	        addFocusListener((FocusListener)(s.readObject()));

	    else if (keyListenerK == key)
	        addKeyListener((KeyListener)(s.readObject()));

	    else if (mouseListenerK == key)
	        addMouseListener((MouseListener)(s.readObject()));

	    else if (mouseMotionListenerK == key)
	        addMouseMotionListener((MouseMotionListener)(s.readObject()));

	    else if (inputMethodListenerK == key)
	        addInputMethodListener((InputMethodListener)(s.readObject()));

	    else // skip value for unrecognized key
	        s.readObject();

        }
        
        // Read the component's orientation if it's present
        Object orient = null;

        try {
            orient = s.readObject();
        } catch (java.io.OptionalDataException e) {
            // JDK 1.1 instances will not have this optional data.
            // e.eof will be true to indicate that there is no more
            // data available for this object.
	    // If e.eof is not true, throw the exception as it
	    // might have been caused by reasons unrelated to 
	    // componentOrientation.
	    
	    if (!e.eof)  {
		throw (e);
	    }
        }

        if (orient != null) {
            componentOrientation = (ComponentOrientation)orient;
        } else {
            componentOrientation = ComponentOrientation.UNKNOWN;
        }

	if (popups != null) {
	    int npopups = popups.size();
	    for (int i = 0 ; i < npopups ; i++) {
		PopupMenu popup = (PopupMenu)popups.elementAt(i);
		popup.parent = this;
	    }
	}
    }
    
    /**
     * Set the language-sensitive orientation that is to be used to order
     * the elements or text within this component.  Language-sensitive
     * LayoutManager and Component subclasses will use this property to
     * determine how to lay out and draw components.
     * <p>
     * At construction time, a component's orientation is set to
     * ComponentOrientation.UNKNOWN, indicating that it has not been specified
     * explicitly.  The UNKNOWN orientation behaves the same as
     * ComponentOrientation.LEFT_TO_RIGHT.
     * <p>
     * To set the orientation of a single component, use this method.
     * To apply a ResourceBundle's orientation to an entire component
     * hierarchy, use java.awt.Window.applyResourceBundle.
     *
     * @see java.awt.ComponentOrientation
     * @see java.awt.Window#ApplyResourceBundle(java.util.ResourceBundle)
     *
     * @author Laura Werner, IBM
     */
    public void setComponentOrientation(ComponentOrientation o) {
        ComponentOrientation oldValue = componentOrientation;
        componentOrientation = o;

	// This is a bound property, so report the change to
	// any registered listeners.  (Cheap if there are none.)
	firePropertyChange("componentOrientation", oldValue, o);

	// This could change the preferred size of the Component.
	if (valid) {
	    invalidate();
	}
    }

    /**
     * Retrieve the language-sensitive orientation that is to be used to order
     * the elements or text within this component.  LayoutManager and Component
     * subclasses that wish to respect orientation should call this method to
     * get the component's orientation before performing layout or drawing.
     *
     * @see java.awt.ComponentOrientation
     *
     * @author Laura Werner, IBM
     */
    public ComponentOrientation getComponentOrientation() {
        return componentOrientation;
    }


    /**
     * This odd class is to help out a native component that has been
     * embedded in a lightweight component.  Moving lightweight
     * components around and changing their visibility is not seen
     * by the native window system.  This is a feature for lightweights,
     * but a problem for native components that depend upon the
     * lightweights.  An instance of this class listens to the lightweight
     * parents of an associated native component (the outer class).
     *
     * @author  Timothy Prinzing
     */
    private final class NativeInLightFixer implements ComponentListener, ContainerListener {

	NativeInLightFixer() {
	    lightParents = new Vector();
	    Container p = parent;
	    // stash a reference to the components that are being observed so that
	    // we can reliably remove ourself as a listener later.
	    for (; p.peer instanceof java.awt.peer.LightweightPeer; p = p.parent) {

		// register listeners and stash a reference
		p.addComponentListener(this);
		p.addContainerListener(this);
		lightParents.addElement(p);
	    }
	    // register with the native host (native parent of associated native)
	    // to get notified if the top-level lightweight is removed.
	    nativeHost = p;
	    p.addContainerListener(this);

	    // kick start the fixup.  Since the event isn't looked at
	    // we can simulate movement notification.
	    componentMoved(null);
	}

	// --- ComponentListener -------------------------------------------

	/**
	 * Invoked when one of the lightweight parents has been resized.
	 * This doesn't change the position of the native child so it
	 * is ignored.
	 */
        public void componentResized(ComponentEvent e) {
	}

	/**
	 * Invoked when one of the lightweight parents has been moved.
	 * The native peer must be told of the new position which is
	 * relative to the native container that is hosting the
	 * lightweight components.
	 */
        public void componentMoved(ComponentEvent e) {
	    synchronized (getTreeLock()) {
		int nativeX = x;
		int nativeY = y;
		for(Component c = parent; (c != null) &&
			(c.peer instanceof java.awt.peer.LightweightPeer);
		    c = c.parent) {

		    nativeX += c.x;
		    nativeY += c.y;
		}
		if (peer != null) {
		    peer.setBounds(nativeX, nativeY, width, height);
		}
	    }
	}

	/**
	 * Invoked when a lightweight parent component has been
	 * shown.  The associated native component must also be
	 * shown if it hasn't had an overriding hide done on it.
	 */
        public void componentShown(ComponentEvent e) {
	    if (isShowing()) {
		synchronized (getTreeLock()) {
		    if (peer != null) {
			peer.show();
		    }
		}
	    }
	}

	/**
	 * Invoked when component has been hidden.
	 */
        public void componentHidden(ComponentEvent e) {
	    if (visible) {
		synchronized (getTreeLock()) {
		    if (peer != null) {
			peer.hide();
		    }
		}
	    }
	}

	// --- ContainerListener ------------------------------------

	/**
	 * Invoked when a component has been added to a lightweight
	 * parent.  This doesn't effect the native component.
	 */
        public void componentAdded(ContainerEvent e) {
	}

	/**
	 * Invoked when a lightweight parent has been removed.
	 * This means the services of this listener are no longer
	 * required and it should remove all references (ie
	 * registered listeners).
	 */
        public void componentRemoved(ContainerEvent e) {
	    Component c = e.getChild();
	    if (c == Component.this) {
		removeReferences();
	    } else {
		int n = lightParents.size();
		for (int i = 0; i < n; i++) {
		    Container p = (Container) lightParents.elementAt(i);
		    if (p == c) {
			removeReferences();
			break;
		    }
		}
	    }
	}

	/**
	 * Remove references to this object so it can be
	 * garbage collected.
	 */
	void removeReferences() {
	    int n = lightParents.size();
	    for (int i = 0; i < n; i++) {
		Container c = (Container) lightParents.elementAt(i);
		c.removeComponentListener(this);
		c.removeContainerListener(this);
	    }
	    nativeHost.removeContainerListener(this);
	}

	Vector lightParents;
	Container nativeHost;
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();

}
