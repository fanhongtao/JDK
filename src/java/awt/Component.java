/*
 * @(#)Component.java	1.146 97/05/21
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Locale;
import java.awt.peer.ComponentPeer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.awt.event.*;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A generic Abstract Window Toolkit component. 
 *
 * @version 	1.146, 05/21/97
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
     * The parent of the object. It may be null for toplevel components.
     * @see #getParent
     */
    transient Container parent;

    /**
     * The x position of the component in the parent's coordinate system.
     * @see #getLocation
     */
    int x;

    /**
     * The y position of the component in the parent's coordinate system.
     * @see #getLocation
     */
    int y;

    /**
     * The width of the component.
     * @see #getSize
     */
    int width;

    /**
     * The height of the component.
     * @see #getSize
     */
    int height;

    /**
     * The foreground color for this component.
     * @see #getForeground
     * @see #setForeground
     */
    Color	foreground;

    /**
     * The background color for this component.
     * @see #getBackground
     * @see #setBackground
     */
    Color	background;

    /**
     * The font used by this component.
     * @see #getFont
     * @see #setFont
     */
    Font	font;

    /**
     * The cursor displayed when pointer is over this component.
     * @see #getCursor
     * @see #setCursor
     */
    Cursor	cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    /**
     * The locale for the component.
     * @see #getLocale
     * @see #setLocale
     */
    Locale      locale;

    /**
     * True when the object is visible. An object that is not
     * visible is not drawn on the screen.
     * @see #isVisible
     * @see #setVisible
     */
    boolean visible = true;

    /**
     * True when the object is enabled. An object that is not
     * enabled does not interact with the user.
     * @see #isEnabled
     * @see #setEnabled
     */
    boolean enabled = true;

    /** 
     * True when the object is valid. An invalid object needs to
     * be layed out. This flag is set to false when the object
     * size is changed.
     * @see #isValid
     * @see #validate
     * @see #invalidate
     */
    boolean valid = false;

    Vector popups;

    String name;

    /**
     * The locking object for AWT component-tree and layout operations.
     * To obtain this lock object from outside the java.awt package,
     * use the getTreeLock method.
     *
     * @see #getTreeLock
     */
    static final Object LOCK = new Object();

    /** Internal, cached size information */
    Dimension minSize;

    /** Internal, cached size information */
    Dimension prefSize;

    boolean newEventsOnly = false;
    transient ComponentListener componentListener;
    transient FocusListener focusListener;
    transient KeyListener keyListener;
    transient MouseListener mouseListener;
    transient MouseMotionListener mouseMotionListener;

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
    final static String windowListenerK = "windowL";


    // The eventMask is ONLY set by subclasses via enableEvents.
    // The mask should NOT be set when listeners are registered
    // so that we can distinguish the difference between when
    // listeners request events and subclasses request them.
    long eventMask;

    /**
     * Static properties for incremental drawing.
     * @see #imageUpdate
     */
    static boolean isInc;
    static int incRate;
    static {
	String s;

	s = System.getProperty("awt.image.incrementaldraw");
	isInc = (s == null || s.equals("true"));

	s = System.getProperty("awt.image.redrawrate");
	incRate = (s != null) ? Integer.parseInt(s) : 100;
    }

    /**
     * Ease of use constant for getAlignmentY().  Specifies an
     * alignment to the top of the component.
     */
    public static final float TOP_ALIGNMENT = 0.0f;

    /**
     * Ease of use constant for getAlignmentY() and getAlignmentX().
     * Specifies an alignment to the center of the component.
     */
    public static final float CENTER_ALIGNMENT = 0.5f;

    /**
     * Ease of use constant for getAlignmentY().  Specifies an
     * alignment to the bottom of the component.
     */
    public static final float BOTTOM_ALIGNMENT = 1.0f;

    /**
     * Ease of use constant for getAlignmentX().  Specifies an
     * alignment to the left side of the component.
     */
    public static final float LEFT_ALIGNMENT = 0.0f;

    /**
     * Ease of use constant for getAlignmentX().  Specifies an
     * alignment to the right side of the component.
     */
    public static final float RIGHT_ALIGNMENT = 1.0f;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -7644114512714619750L;

    /**
     * Constructs a new Component. Components can be extended directly, 
     * but are lightweight in this case and must be hosted by a native
     * container somewhere higher up in the component tree (such as 
     * a Frame for example).
     */
    protected Component() {
    }

    /**
     * Gets the name of the component.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component to the specified string.
     * @param name  the name of the component.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the parent of the component.
     */
    public Container getParent() {
	return parent;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * programs should not directly manipulate peers.
     */
    public ComponentPeer getPeer() {
	return peer;
    }

    /**
     * Gets the locking object for AWT component-tree and layout
     * operations.
     */
    public final Object getTreeLock() {
	return LOCK;
    }

    /**
     * Gets the toolkit of the component. This toolkit is
     * used to create the peer for this component.  Note that
     * the Frame which contains a Component controls which
     * toolkit is used so if the Component has not yet been
     * added to a Frame or if it is later moved to a different
     * Frame, the toolkit it uses may change.
     */
    public Toolkit getToolkit() {
      	ComponentPeer peer = this.peer;
	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)){
	    return peer.getToolkit();
	}
	Container parent = this.parent;
	if (parent != null) {
	    return parent.getToolkit();
	}
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Checks if this Component is valid. Components are invalidated when
     * they are first shown on the screen.
     * @see #validate
     * @see #invalidate
     */
    public boolean isValid() {
	return (peer != null) && valid;
    }

    /**
     * Checks if this Component is visible. Components are initially visible 
     * (with the exception of top level components such as Frame).
     * @see #setVisible
     */
    public boolean isVisible() {
	return visible;
    }

    /**
     * Checks if this Component is showing on screen. This means that the 
     * component must be visible, and it must be in a container that is 
     * visible and showing.
     * @see #setVisible
     */
    public boolean isShowing() {
	if (visible && (peer != null)) {
    	    Container parent = this.parent;
	    return (parent == null) || parent.isShowing();
	}
	return false;
    }

    /**
     * Checks if this Component is enabled. Components are initially enabled.
     * @see #setEnabled
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Enables a component.
     * @see #isEnabled
     */
    public void setEnabled(boolean b) {
    	enable(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setEnabled(boolean).
     */
    public void enable() {
    	if (enabled != true) {
	    synchronized (this) {
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
     * replaced by setEnabled(boolean).
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
     * replaced by setEnabled(boolean).
     */
    public void disable() {
    	if (enabled != false) {
	    synchronized (this) {
		enabled = false;
		ComponentPeer peer = this.peer;
		if (peer != null) {
		    peer.disable();
		}
	    }
	}
    }

    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see #isVisible
     */
    public void setVisible(boolean b) {
    	show(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setVisible(boolean).
     */
    public void show() {
	if (visible != true) {
	    synchronized (Component.LOCK) {
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
     * replaced by setVisible(boolean).
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
     * replaced by setVisible(boolean).
     */
    public void hide() {
	if (visible != false) {
	    synchronized (Component.LOCK) {
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
     * Gets the foreground color. If the component does
     * not have a foreground color, the foreground color
     * of its parent is returned.
     * @see #setForeground
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
     * Sets the foreground color.
     * @param c the Color
     * @see #getForeground
     */
    public void setForeground(Color c) {
	ComponentPeer peer = this.peer;
	foreground = c;
	if (peer != null) {
	    c = getForeground();
	    if (c != null) {
		peer.setForeground(c);
	    }
	}
    }

    /**
     * Gets the background color. If the component does
     * not have a background color, the background color
     * of its parent is returned.
     * @see #setBackground
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
     * Sets the background color.
     * @param c the Color
     * @see #getBackground
     */
    public void setBackground(Color c) {
	ComponentPeer peer = this.peer;
	background = c;
	if (peer != null) {
	    c = getBackground();
	    if (c != null) {
		peer.setBackground(c);
	    }
	}
    }

    /**
     * Gets the font of the component. If the component does
     * not have a font, the font of its parent is returned.
     * @see #setFont
     */
    public Font getFont() {
        Font font = this.font;
	if (font != null) {
	    return font;
	}
    	Container parent = this.parent;
	return (parent != null) ? parent.getFont() : null;
    }

    /** 
     * Sets the font of the component.
     * @param f the font
     * @see #getFont
     */
    public synchronized void setFont(Font f) {
    	ComponentPeer peer = this.peer;
	font = f;
	if (peer != null) {
	    f = getFont();
	    if (f != null) {
		peer.setFont(f);
	    }
	}
    }

    /**
     * Gets the locale of the component. If the component does
     * not have a locale, the locale of its parent is returned.
     * @see #setLocale
     * @exception IllegalComponentStateException If the Component 
     * does not have its own locale and has not yet been added to
     * a containment hierarchy such that the locale can be determined
     * from the containing parent.
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
     * Sets the locale of the component.
     * @param l the locale
     * @see #getLocale
     */
    public void setLocale(Locale l) {
	locale = l;
    }

    /**
     * Gets the ColorModel used to display the component on the output device.
     * @see ColorModel
     */
    public ColorModel getColorModel() {
    	ComponentPeer peer = this.peer;
	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)) {
	    return peer.getColorModel();
	}
	return getToolkit().getColorModel();
    }

    /** 
     * Returns the current location of this component.
     * The location will be in the parent's coordinate space.
     * @see #setLocation
     */
    public Point getLocation() {
	return location();
    }

    /** 
     * Returns the current location of this component in the screen's 
     * coordinate space.
     * @see #setLocation
     * @see #getLocation
     */
    public Point getLocationOnScreen() {
	synchronized (Component.LOCK) {
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
     * replaced by getLocation().
     */
    public Point location() {
	return new Point(x, y);
    }

    /** 
     * Moves the Component to a new location. The x and y coordinates
     * are in the parent's coordinate space.
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #getLocation
     * @see #setBounds
     */
    public void setLocation(int x, int y) {
	move(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1, 
     * replaced by setLocation(int, int).
     */
    public void move(int x, int y) {
	setBounds(x, y, width, height);
    }

    /** 
     * Moves the Component to a new location. The point p is given in
     * the parent's coordinate space.
     * @param p the new location for the coordinate
     * @see #getLocation
     * @see #setBounds
     */
    public void setLocation(Point p) {
    	setLocation(p.x, p.y);
    }

    /** 
     * Returns the current size of this component.
     * @see #setSize
     */
    public Dimension getSize() {
	return size();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by getSize().
     */
    public Dimension size() {
	return new Dimension(width, height);
    }

    /**
     * Resizes the Component to the specified width and height.
     * @param width the width of the component
     * @param height the height of the component
     * @see #getSize
     * @see #setBounds
     */
    public void setSize(int width, int height) {
	resize(width, height);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setSize(int, int).
     */
    public void resize(int width, int height) {
	setBounds(x, y, width, height);
    }

    /** 
     * Resizes the Component to the specified dimension.
     * @param d the component dimension
     * @see #setSize
     * @see #setBounds
     */
    public void setSize(Dimension d) {
	resize(d);
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by setSize(Dimension).
     */
    public void resize(Dimension d) {
	setSize(d.width, d.height);
    }

    /** 
     * Returns the current bounds of this component.
     * @see #setBounds
     */
    public Rectangle getBounds() {
	return bounds();
    }

    /**
     * @deprecated As of JDK version 1.1, 
     * replaced by getBounds().
     */
    public Rectangle bounds() {
	return new Rectangle(x, y, width, height);
    }

    /** 
     * Reshapes the Component to the specified bounding box.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the component
     * @param height the height of the component
     * @see #getBounds
     * @see #setLocation
     * @see #setSize
     */
    public void setBounds(int x, int y, int width, int height) {
	reshape(x, y, width, height);
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by setBounds(int, int, int, int).
     */
    public void reshape(int x, int y, int width, int height) {
	synchronized (Component.LOCK) {
	    boolean resized = (this.width != width) || (this.height != height);
            boolean moved = (this.x != x) || (this.y != y);
	    boolean isLightweight = peer instanceof java.awt.peer.LightweightPeer;

	    if (resized || moved) {
		if (isLightweight) {
		    repaint();
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (peer != null) {
		    if (peer instanceof java.awt.peer.LightweightPeer) {
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
		    if (isLightweight) {
			repaint();
		    }
		}
	    }
	}
    }

    /** 
     * Reshapes the Component to the specified bounding box.
     * @param r the new bounding rectangle for the component
     * @see #getBounds
     * @see #setLocation
     * @see #setSize
     */
    public void setBounds(Rectangle r) {
    	setBounds(r.x, r.y, r.width, r.height);
    }

    /** 
     * Returns the preferred size of this component.
     * @see #getMinimumSize
     * @see LayoutManager
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize().
     */
    public Dimension preferredSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
    	 * is available.
	 */

    	Dimension dim = prefSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
        
	synchronized (Component.LOCK) {
	    prefSize = (peer != null) ?
			   peer.preferredSize() :
			   getMinimumSize();
	    return prefSize;
	}
    }

    /**
     * Returns the mininimum size of this component.
     * @see #getPreferredSize
     * @see LayoutManager
     */
    public Dimension getMinimumSize() {
      	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize().
     */
    public Dimension minimumSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
    	 * is available.
	 */
    	Dimension dim = minSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	synchronized (Component.LOCK) {
	    minSize = (peer != null) ?
			  peer.minimumSize() :
			  size();
	    return minSize;
	}
    }

    /** 
     * Returns the maximum size of this component.
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
     * Lays out the component. This is usually called when the
     * component (more specifically, container) is validated.
     * @see #validate
     * @see LayoutManager
     */
    public void doLayout() {
    	layout();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by doLayout().
     */
    public void layout() {
    }

    /** 
     * Ensures that a component has a valid layout.  This method is
     * primarily intended to operate on Container instances.
     * @see #invalidate
     * @see #doLayout
     * @see LayoutManager
     * @see Container#validate
     */
    public void validate() {
	if (!valid) {
	    synchronized (Component.LOCK) {
		valid = true;
	    }
	}
    }

    /** 
     * Invalidates the component.  The component and all parents
     * above it are marked as needing to be laid out.  This method can
     * be called often, so it needs to execute quickly.
     * @see #validate
     * @see #doLayout
     * @see LayoutManager
     */
    public void invalidate() {
	synchronized (Component.LOCK) {
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
     * Gets a Graphics context for this component. This method will
     * return null if the component is currently not on the screen.
     * @see #paint
     */
    public Graphics getGraphics() {
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    // This is for a lightweight component, need to 
	    // translate coordinate spaces and clip relative
	    // to the parent.
	    Graphics g = parent.getGraphics();
	    g.translate(x,y);
	    g.setClip(0, 0, width, height);
	    return g;
	} else {
	    ComponentPeer peer = this.peer;
	    return (peer != null) ? peer.getGraphics() : null;
	}
    }

    /**
     * Gets the font metrics for this component.
     * @param font the font
     * @see #getFont
     */
    public FontMetrics getFontMetrics(Font font) {
    	ComponentPeer peer = this.peer;
	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)) {
	    return peer.getFontMetrics(font);
	}
    	return getToolkit().getFontMetrics(font);
    }

    /**
     * Set the cursor image to a predefined cursor.
     * @see Cursor
     * @param cursorType one of the cursor constants defined above.
     */
    public synchronized void setCursor(Cursor cursor) {
	this.cursor = cursor;
    	ComponentPeer peer = this.peer;
	if (peer != null) {
	    peer.setCursor(cursor);
	}
    }

    /**
     * Gets the cursor set on this component.
     */
    public Cursor getCursor() {
	return cursor;
    }

    /** 
     * Paints the component.  This method is called when the contents
     * of the component should be painted in response to the component
     * first being shown or damage needing repair.  The clip rectangle
     * in the Graphics parameter will be set to the area which needs
     * to be painted.
     * @param g the specified Graphics window
     * @see #update
     */
    public void paint(Graphics g) {
    }

    /** 
     * Updates the component. This method is called in
     * response to a call to repaint. You can assume that
     * the background is not cleared.
     * @param g the specified Graphics window
     * @see #paint
     * @see #repaint
     */
    public void update(Graphics g) {
	if (! (peer instanceof java.awt.peer.LightweightPeer)) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, width, height);
	    g.setColor(getForeground());
	}
	paint(g);
    }

    /**
     * Paints the component and its subcomponents.
     * @param g the specified Graphics window
     * @see #paint
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
     * Repaints the component. This will result in a
     * call to update as soon as possible.
     * @see #paint
     */
    public void repaint() {
	repaint(0, 0, 0, width, height);
    }

    /** 
     * Repaints the component. This will result in a
     * call to update within <em>tm</em> milliseconds.
     * @param tm maximum time in milliseconds before update
     * @see #paint
     */
    public void repaint(long tm) {
	repaint(tm, 0, 0, width, height);
    }

    /** 
     * Repaints part of the component. This will result in a
     * call to update as soon as possible.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width 
     * @param height the height 
     * @see #repaint
     */
    public void repaint(int x, int y, int width, int height) {
	repaint(0, x, y, width, height);
    }

    /** 
     * Repaints part of the component. This will result in a
     * call to update width <em>tm</em> millseconds.
     * @param tm maximum time in milliseconds before update
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width 
     * @param height the height 
     * @see #repaint
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
     * Prints this component. The default implementation of this
     * method calls paint.
     * @param g the specified Graphics window
     * @see #paint
     */
    public void print(Graphics g) {
	paint(g);
    }

    /**
     * Prints the component and its subcomponents.
     * @param g the specified Graphics window
     * @see #print
     */
    public void printAll(Graphics g) {
	ComponentPeer peer = this.peer;
	if (visible && (peer != null)) {
	    validate();
	    peer.print(g);
	}
    }

    /**
     * Repaints the component when the image has changed.
     * @return true if image has changed; false otherwise.
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
     * @param producer the image producer
     */
    public Image createImage(ImageProducer producer) {
    	ComponentPeer peer = this.peer;
	if ((peer != null) && ! (peer instanceof java.awt.peer.LightweightPeer)) {
	    return peer.createImage(producer);
	}
	return getToolkit().createImage(producer);
    }

    /**
     * Creates an off-screen drawable Image to be used for double buffering.
     * @param width the specified width
     * @param height the specified height
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
     * Prepares an image for rendering on this Component.  The image
     * data is downloaded asynchronously in another thread and the
     * appropriate screen representation of the image is generated.
     * @param image the Image to prepare a screen representation for
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return true if the image has already been fully prepared
     * @see ImageObserver
     */
    public boolean prepareImage(Image image, ImageObserver observer) {
        return prepareImage(image, -1, -1, observer);
    }

    /**
     * Prepares an image for rendering on this Component at the
     * specified width and height.  The image data is downloaded
     * asynchronously in another thread and an appropriately scaled
     * screen representation of the image is generated.
     * @param image the Image to prepare a screen representation for
     * @param width the width of the desired screen representation
     * @param height the height of the desired screen representation
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return true if the image has already been fully prepared
     * @see ImageObserver
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
     * This method does not cause the image to begin loading. Use the
     * prepareImage method to force the loading of an image.
     * @param image the Image to check the status of
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return the boolean OR of the ImageObserver flags for the
     *         data that is currently available
     * @see ImageObserver
     * @see #prepareImage
     */
    public int checkImage(Image image, ImageObserver observer) {
        return checkImage(image, -1, -1, observer);
    }

    /**
     * Returns the status of the construction of a scaled screen
     * representation of the specified image.
     * This method does not cause the image to begin loading, use the
     * prepareImage method to force the loading of an image.
     * @param image the Image to check the status of
     * @param width the width of the scaled version to check the status of
     * @param height the height of the scaled version to check the status of
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return the boolean OR of the ImageObserver flags for the
     *         data that is currently available
     * @see ImageObserver
     * @see #prepareImage
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
     * Checks whether this component "contains" the specified (x, y)
     * location, where x and y are defined to be relative to the 
     * coordinate system of this component.  
     * @param x the x coordinate 
     * @param y the y coordinate
     * @see #getComponentAt
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
     * where x and y in the point are defined to be relative to the 
     * coordinate system of this component.  
     * @param p the point
     * @see #getComponentAt
     */
    public boolean contains(Point p) {
	return contains(p.x, p.y);
    }

    /** 
     * Returns the component or subcomponent that contains the x,y location.
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #contains
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
     * @param p the point
     * @see #contains
     */
    public Component getComponentAt(Point p) {
	return getComponentAt(p.x, p.y);
    }

    /**
     * @deprecated As of JDK version 1.1
     */
    public void deliverEvent(Event e) {
	postEvent(e);
    }

    /**
     * Dispatches an event to this component or one of its sub components.
     * @param e the event
     */
    public final void dispatchEvent(AWTEvent e) {
        dispatchEventImpl(e);
    }

    void dispatchEventImpl(AWTEvent e) {
        int id = e.getID();

        /*
         * 1. Pre-process any special events before delivery
         */
        switch(id) {
          case PaintEvent.PAINT: 
          case PaintEvent.UPDATE: 
            Graphics g = getGraphics();
            if (g == null) {
                return;
            }
            Rectangle r = ((PaintEvent)e).getUpdateRect();
            g.clipRect(r.x, r.y, r.width, r.height);
            if (id == PaintEvent.PAINT) {
                paint(g);
            } else {
                update(g);
            }
            g.dispose();
            return;
 
          case FocusEvent.FOCUS_GAINED:
            if (parent != null && !(this instanceof Window)) {
                parent.setFocusOwner(this);
            }
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
         * 2. Deliver event for normal processing
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
         * 3. If no one has consumed a key event, propagate it
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
         * 4. Allow the peer to process the event
         */
        if (peer != null) {
            peer.handleEvent(e);
        }
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
     * Adds the specified component listener to receive component events
     * from this component.
     * @param l the component listener
     */  
    public synchronized void addComponentListener(ComponentListener l) {
        componentListener = AWTEventMulticaster.add(componentListener, l);
        newEventsOnly = true;
    }
    /**
     * Removes the specified listener so it no longer receives component
     * events from this component.
     * @param l the component listener
     */ 
    public synchronized void removeComponentListener(ComponentListener l) {
        componentListener = AWTEventMulticaster.remove(componentListener, l);
    }        

    /**
     * Adds the specified focus listener to receive focus events
     * from this component.
     * @param l the focus listener
     */      
    public synchronized void addFocusListener(FocusListener l) {
        focusListener = AWTEventMulticaster.add(focusListener, l);
        newEventsOnly = true;

	// if this is a lightweight component, enable focus events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.FOCUS_EVENT_MASK);
	}
    }

    /**
     * Removes the specified focus listener so it no longer receives focus
     * events from this component.
     * @param l the focus listener
     */ 
    public synchronized void removeFocusListener(FocusListener l) {
        focusListener = AWTEventMulticaster.remove(focusListener, l);
    }   

    /**
     * Adds the specified key listener to receive key events
     * from this component.
     * @param l the key listener
     */      
    public synchronized void addKeyListener(KeyListener l) {
        keyListener = AWTEventMulticaster.add(keyListener, l);
        newEventsOnly = true;

	// if this is a lightweight component, enable key events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.KEY_EVENT_MASK);
	}
    }

    /**
     * Removes the specified key listener so it no longer receives key
     * events from this component.
     * @param l the key listener
     */ 
    public synchronized void removeKeyListener(KeyListener l) {
        keyListener = AWTEventMulticaster.remove(keyListener, l);
    }  

    /**
     * Adds the specified mouse listener to receive mouse events
     * from this component.
     * @param l the mouse listener
     */      
    public synchronized void addMouseListener(MouseListener l) {
        mouseListener = AWTEventMulticaster.add(mouseListener,l);
        newEventsOnly = true;
	
	// if this is a lightweight component, enable mouse events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
    }

    /**
     * Removes the specified mouse listener so it no longer receives mouse
     * events from this component.
     * @param l the mouse listener
     */ 
    public synchronized void removeMouseListener(MouseListener l) {
        mouseListener = AWTEventMulticaster.remove(mouseListener, l);
    }

    /**
     * Adds the specified mouse motion listener to receive mouse motion events
     * from this component.
     * @param l the mouse motion listener
     */  
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        mouseMotionListener = AWTEventMulticaster.add(mouseMotionListener,l);
        newEventsOnly = true;
	
	// if this is a lightweight component, enable mouse events
	// in the native container.
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    parent.proxyEnableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
    }

    /**
     * Removes the specified mouse motion listener so it no longer 
     * receives mouse motion events from this component.
     * @param l the mouse motion listener
     */ 
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        mouseMotionListener = AWTEventMulticaster.remove(mouseMotionListener, l);
    }    

    /**
     * Enables the events defined by the specified event mask parameter
     * to be delivered to this component.  Event types are automatically
     * enabled when a listener for that type is added to the component,
     * therefore this method only needs to be invoked by subclasses of
     * a component which desire to have the specified event types 
     * delivered to processEvent regardless of whether a listener is
     * registered.
     * @param eventsToEnable the event mask defining the event types
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
     * @param eventsToDisable the event mask defining the event types
     */
    protected final void disableEvents(long eventsToDisable) {
        eventMask &= ~eventsToDisable;  
    }  

    /** 
     * Processes events occurring on this component.  By default this
     * method will call the appropriate processXXXEvent method for the 
     * class of event.  This method will not be called unless and event
     * type is enabled for this component; this happens when one of the
     * following occurs:
     * a) A listener object is registered for that event type
     * b) The event type is enabled via enableEvents()
     * Classes overriding this method should call super.processEvent()
     * to ensure default event processing continues normally.
     * @see #enableEvents
     * @see #processComponentEvent
     * @see #processFocusEvent
     * @see #processKeyEvent
     * @see #processMouseEvent
     * @see #processMouseMotionEvent
     * @param e the event
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
        }
    }
    
    /** 
     * Processes component events occurring on this component by
     * dispatching them to any registered ComponentListener objects.
     * NOTE: This method will not be called unless component events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A ComponentListener object is registered via addComponentListner()
     * b) Component events are enabled via enableEvents()
     * Classes overriding this method should call super.processComponentEvent()
     * to ensure default event processing continues normally.
     * @param e the component event
     */   
    protected void processComponentEvent(ComponentEvent e) {
        if (componentListener != null) {
            int id = e.getID();
            switch(id) {
              case ComponentEvent.COMPONENT_RESIZED:
                componentListener.componentResized(e);
                break;
              case ComponentEvent.COMPONENT_MOVED:
                componentListener.componentMoved(e);
                break;
              case ComponentEvent.COMPONENT_SHOWN:
                componentListener.componentShown(e);
                break;
              case ComponentEvent.COMPONENT_HIDDEN:
                componentListener.componentHidden(e);
                break;
            }
        }
    }

    /** 
     * Processes focus events occurring on this component by
     * dispatching them to any registered FocusListener objects.
     * NOTE: This method will not be called unless focus events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A FocusListener object is registered via addFocusListener()
     * b) Focus events are enabled via enableEvents()
     * Classes overriding this method should call super.processFocusEvent()
     * to ensure default event processing continues normally.
     * @param e the focus event
     */       
    protected void processFocusEvent(FocusEvent e) {
        if (focusListener != null) {
            int id = e.getID();
            switch(id) {
              case FocusEvent.FOCUS_GAINED:
                focusListener.focusGained(e);
                break;
              case FocusEvent.FOCUS_LOST:
                focusListener.focusLost(e);
                break;
            }
        }
    }

    /** 
     * Processes key events occurring on this component by
     * dispatching them to any registered KeyListener objects.
     * NOTE: This method will not be called unless key events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A KeyListener object is registered via addKeyListener()
     * b) Key events are enabled via enableEvents()
     * Classes overriding this method should call super.processKeyEvent()
     * to ensure default event processing continues normally.
     * @param e the key event
     */   
    protected void processKeyEvent(KeyEvent e) {
        if (keyListener != null) {
            int id = e.getID();
            switch(id) {
              case KeyEvent.KEY_TYPED:
                keyListener.keyTyped(e);
                break;
              case KeyEvent.KEY_PRESSED:
                keyListener.keyPressed(e);
                break;
              case KeyEvent.KEY_RELEASED:
                keyListener.keyReleased(e);
                break;
            }
        } 
    }

    /** 
     * Processes mouse events occurring on this component by
     * dispatching them to any registered MouseListener objects.
     * NOTE: This method will not be called unless mouse events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A MouseListener object is registered via addMouseListener()
     * b) Mouse events are enabled via enableEvents()
     * Classes overriding this method should call super.processMouseEvent()
     * to ensure default event processing continues normally.
     * @param e the mouse event
     */   
    protected void processMouseEvent(MouseEvent e) {
        if (mouseListener != null) {
            int id = e.getID();
            switch(id) {
              case MouseEvent.MOUSE_PRESSED:
                mouseListener.mousePressed(e);
                break;
              case MouseEvent.MOUSE_RELEASED:
                mouseListener.mouseReleased(e);
                break;
              case MouseEvent.MOUSE_CLICKED:
                mouseListener.mouseClicked(e);
                break;
              case MouseEvent.MOUSE_EXITED:
                mouseListener.mouseExited(e);
                break;
              case MouseEvent.MOUSE_ENTERED:
                mouseListener.mouseEntered(e);
                break;
            }
        }            
    }

    /** 
     * Processes mouse motion events occurring on this component by
     * dispatching them to any registered MouseMotionListener objects.
     * NOTE: This method will not be called unless mouse motion events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A MouseMotionListener object is registered via addMouseMotionListener()
     * b) Mouse Motion events are enabled via enableEvents()
     * Classes overriding this method should call super.processMouseMotionEvent()
     * to ensure default event processing continues normally.
     * @param e the mouse motion event
     */   
    protected void processMouseMotionEvent(MouseEvent e) {
        if (mouseMotionListener != null) {
            int id = e.getID();
            switch(id) {
              case MouseEvent.MOUSE_MOVED:
                mouseMotionListener.mouseMoved(e);
                break;
              case MouseEvent.MOUSE_DRAGGED:
                mouseMotionListener.mouseDragged(e);
                break;
            }
        }            
    }

    boolean postsOldMouseEvents() {
        return false;
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
     * Notifies the Component that it has been added to a container
     * and if a peer is required, it should be created.
     * This method should be called by Container.add, and not by user
     * code directly.
     * @see #removeNotify
     */
    public void addNotify() {
	if (peer == null) {
	    peer = getToolkit().createComponent(this);

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
    }

    /** 
     * Notifies the Component that it has been removed from its
     * container and if a peers ecists, it destroys it.
     * This method should be called by Container.remove, and not by user
     * code directly.
     * @see #addNotify
     */
    public void removeNotify() {
        int npopups = (popups != null? popups.size() : 0);
	for (int i = 0 ; i < npopups ; i++) {
	    PopupMenu popup = (PopupMenu)popups.elementAt(i);
	    popup.removeNotify();
	}
	if (peer != null) {
            ComponentPeer p = peer;
            p.hide();    // Hide peer first to stop system events such as cursor moves.
            peer = null; // Stop peer updates.
            Toolkit.getEventQueue().removeSourceEvents(this);
            p.dispose();
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
     * Returns whether this component can be traversed using
     * Tab or Shift-Tab keyboard focus traversal.  If this method
     * returns "false", this component may still request the keyboard
     * focus using requestFocus(), but it will not automatically
     * be assigned focus during tab traversal.
     */
    public boolean isFocusTraversable() {
    	ComponentPeer peer = this.peer;
	if (peer != null) {
	    return peer.isFocusTraversable();
	}
	return false;
    }

    /** 
     * Requests the input focus. A FocusGained event will be delivered
     * if this request succeeds.  The component must be visible
     * on the screen for this request to be granted. 
     * @see FocusEvent
     * @see #addFocusListener
     * @see #processFocusEvent
     * @see #isFocusTraversable
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
     * @see #requestFocus
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
     * Adds the specified popup menu to the component.
     * @param popup the popup menu to be added to the component
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
     * @param popup the popup menu to be removed
     */
    public synchronized void remove(MenuComponent popup) {
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

    /**
     * Returns the parameter String of this Component.
     */
    protected String paramString() {
	String str = (name != null? name : "") + "," + x + "," + y + "," + width + "x" + height;
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
     * Returns the String representation of this Component's values.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }

    /**
     * Prints a listing to System.out.
     */
    public void list() {
	list(System.out, 0);
    }

    /**
     * Prints a listing to the specified print stream.
     * @param out the Stream name
     */
    public void list(PrintStream out) {
	list(out, 0);
    }

    /**
     * Prints out a list, starting at the specified indention, to the specified 
     * print stream.
     * @param out the Stream name
     * @param indent the start of the list 
     */
    public void list(PrintStream out, int indent) {
	for (int i = 0 ; i < indent ; i++) {
	    out.print("  ");
	}
	out.println(this);
    }

    /**
     * Prints a listing to the specified print writer.
     */
    public void list(PrintWriter out) {
	list(out, 0);
    }

    /**
     * Prints out a list, starting at the specified indention, to the specified 
     * print writer.
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

    /* Serialization support.  
     */

    private int componentSerializedDataVersion = 1;

    private void writeObject(ObjectOutputStream s) 
      throws IOException 
    {
      s.defaultWriteObject();
      
      AWTEventMulticaster.save(s, componentListenerK, componentListener);
      AWTEventMulticaster.save(s, focusListenerK, focusListener);
      AWTEventMulticaster.save(s, keyListenerK, keyListener);
      AWTEventMulticaster.save(s, mouseListenerK, mouseListener);
      AWTEventMulticaster.save(s, mouseMotionListenerK, mouseMotionListener);

      s.writeObject(null);
    }

    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();
      
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

	else // skip value for unrecognized key
	  s.readObject();

      }
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
	    synchronized (Component.LOCK) {
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
	}

	/**
	 * Invoked when a lightweight parent component has been 
	 * shown.  The associated native component must also be
	 * shown if it hasn't had an overriding hide done on it.
	 */
        public void componentShown(ComponentEvent e) {
	    if (isShowing()) {
		synchronized (Component.LOCK) {
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
		synchronized (Component.LOCK) {
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
}
