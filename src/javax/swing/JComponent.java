/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;


import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import java.util.EventListener;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import java.applet.Applet;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectInputValidation;
import java.io.InvalidObjectException;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.awt.Graphics2D;

/**
 * The base class for all Swing components
 * except top-level containers.
 * To use a component that inherits from <code>JComponent</code>,
 * you must place the component in a containment hierarchy
 * whose root is a top-level Swing container.
 * Top-level Swing containers --
 * such as <code>JFrame</code>, <code>JDialog</code>, 
 * and <code>JApplet</code> --
 * are specialized components
 * that provide a place for other Swing components to paint themselves.
 * For an explanation of containment hierarchies, see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/overview/hierarchy.html">Swing Components and the Containment Hierarchy</a>,
 * a section in <em>The Java Tutorial</em>.
 *
 * <p>
 * The <code>JComponent</code> class provides:
 * <ul>
 * <li>The base class for both standard and custom components
 *     that use the Swing architecture.
 * <li>A "pluggable look and feel" (L&F) that can be specified by the
 *     programmer or (optionally) selected by the user at runtime.
 *     See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html">How
 *     to Set the Look and Feel</a>
 *     in <em>The Java Tutorial</em>
 *     for more information.
 * <li>Comprehensive keystroke handling.
 *     See the document <a
 * href="http://java.sun.com/products/jfc/tsc/special_report/kestrel/keybindings.html">Keyboard
 *     Bindings in Swing</a>,
 *     an article in <em>The Swing Connection</em>,
 *     for more information.
 * <li>Support for tool tips --
 *     short descriptions that pop up when the cursor lingers
 *     over a component.
 *     See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/tooltip.html">How
 *     to Use Tool Tips</a>
 *     in <em>The Java Tutorial</em>
 *     for more information.
 * <li>Support for accessibility.
 *     <code>JComponent</code> contains all of the methods in the
 *     <code>Accessible</code> interface,
 *     but it doesn't actually implement the interface.  That is the
 *     responsibility of the individual classes
 *     that extend <code>JComponent</code>.
 * <li>Support for component-specific properties.
 *     With the {@link #putClientProperty}
 *     and {@link #getClientProperty} methods,
 *     you can associate name-object pairs
 *     with any object that descends from <code>JComponent</code>.
 * <li>An infrastructure for painting
 *     that includes double buffering and support for borders.
 *     For more information see <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/overview/draw.html">Painting</a> and
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/border.html">How
 *     to Use Borders</a>,
 *     both of which are sections in <em>The Java Tutorial</em>.
 * </ul>
 * For more information on these subjects, see the
 * <a href="package-summary.html#package_description">Swing package description</a> 
 * and <em>The Java Tutorial</em> section
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/jcomponent.html">The JComponent Class</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see KeyStroke
 * @see Action
 * @see #setBorder
 * @see #registerKeyboardAction
 * @see JOptionPane
 * @see #setDebugGraphicsOptions
 * @see #setToolTipText
 * @see #setAutoscrolls
 *
 * @version 2.130 07/09/99
 * @author Hans Muller
 * @author Arnaud Weber
 */
public abstract class JComponent extends Container implements Serializable
{
    /**
     * @see #getUIClassID
     * @see #writeObject
     */
    private static final String uiClassID = "ComponentUI";

    /**
     * @see ReadObjectCallback
     * @see #readObject
     */ 
    private static final Hashtable readObjectCallbacks = new Hashtable(1);


    /* The following fields support set methods for the corresponding
     * java.awt.Component properties.
     */

    private Dimension preferredSize;
    private Dimension minimumSize;
    private Dimension maximumSize;
    private Float alignmentX;
    private Float alignmentY;
    private AncestorNotifier ancestorNotifier;
    Rectangle _bounds = new Rectangle();


    /* Backing store for JComponent properties and listeners
     */

    protected transient ComponentUI ui;
    protected EventListenerList listenerList = new EventListenerList();

    private Hashtable clientProperties;
    private VetoableChangeSupport vetoableChangeSupport;
    private Autoscroller autoscroller;
    private Border border;
    private int flags;

    /* Input verifier for this component */
    private InputVerifier inputVerifier = null;

    private boolean verifyInputWhenFocusTarget = true;

    /* A "scratch pad" rectangle used by the painting code.
     */
    private transient Rectangle tmpRect;

    /** Set in _paintImmediately. Will indicate the child that initiated
     * the painting operation. If paintingChild is opaque, no need to paint
     * any child components after paintingChild. Test used in paintChildren. */
    transient Component         paintingChild;

    /**
     * Constant used for registerKeyboardAction() that
     * means that the command should be invoked when
     * the component has the focus.
     */
    public static final int WHEN_FOCUSED = 0;

    /**
     * Constant used for registerKeyboardAction() that
     * means that the command should be invoked when the receiving
     * component is an ancestor of the focused component or is
     * itself the focused component.
     */
    public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;

    /**
     * Constant used for registerKeyboardAction() that
     * means that the command should be invoked when
     * the receiving component is in the window that has the focus
     * or is itself the focused component.
     */
    public static final int WHEN_IN_FOCUSED_WINDOW = 2;

    /**
     * Constant used by some of the APIs to mean that no condition is defined.
     */
    public static final int UNDEFINED_CONDITION = -1;

    /**
     * The key used by JComponent to access keyboard bindings.
     */
    private static final String KEYBOARD_BINDINGS_KEY = "_KeyboardBindings";

    /**
     * An array of KeyStrokes used for WHEN_IN_FOCUSED_WINDOW are stashed
     * in the client properties under this string.
     */
    private static final String WHEN_IN_FOCUSED_WINDOW_BINDINGS = "_WhenInFocusedWindow";

    /**
     * The comment to display when the cursor is over the component,
     * also known as a "value tip", "flyover help", or "flyover label".
     */
    public static final String TOOL_TIP_TEXT_KEY = "ToolTipText";

    private static final String NEXT_FOCUS = "nextFocus";

    /** Private flags **/
    private static final int REQUEST_FOCUS_DISABLED = 0;
    private static final int IS_DOUBLE_BUFFERED     = 1;
    private static final int ANCESTOR_USING_BUFFER  = 2;
    private static final int IS_PAINTING_TILE       = 3;
    private static final int HAS_FOCUS              = 4;
    private static final int IS_OPAQUE              = 5;
    private static final int KEY_EVENTS_ENABLED     = 6;
    private static final int FOCUS_INPUTMAP_CREATED = 7;
    private static final int ANCESTOR_INPUTMAP_CREATED= 8;
    private static final int WIF_INPUTMAP_CREATED   = 9;
    private static final int ACTIONMAP_CREATED      = 10;
    private static final int CREATED_DOUBLE_BUFFER  = 11;
    private static final int IS_PRINTING            = 12;
    private static final int IS_PRINTING_ALL        = 13;

    /** Used for WHEN_FOCUSED bindings. */
    private InputMap focusInputMap;
    /** Used for WHEN_ANCESTOR_OF_FOCUSED_COMPONENT bindings. */
    private InputMap ancestorInputMap;
    /** Used for WHEN_IN_FOCUSED_KEY bindings. */
    private ComponentInputMap windowInputMap;

    /** ActionMap. */
    private ActionMap actionMap;

    /**
     * Default JComponent constructor.  This constructor does
     * no initialization beyond calling the Container constructor.
     * For example, the initial layout manager is null.
     */
    public JComponent() {
        super();
	/* We enable key events on all components so that accessibility
	   bindings will work everywhere. This is a partial fix to
	   bug #4282211 */
        enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
	enableSerialization();
    }


    /**
     * Resets the UI property to a value from the current look and feel.
     * JComponent subclasses must override this method like this:
     * <pre>
     *   public void updateUI() {
     *      setUI((SliderUI)UIManager.getUI(this);
     *   }
     *  </pre>
     *
     * @see #setUI
     * @see UIManager#getLookAndFeel
     * @see UIManager#getUI
     */
    public void updateUI() {}


    /**
     * Sets the look and feel delegate for this component.
     * JComponent subclasses generally override this method
     * to narrow the argument type. For example, in JSlider:
     * <pre>
     * public void setUI(SliderUI newUI) {
     *     super.setUI(newUI);
     * }
     *  </pre>
     * <p>
     * Additionally JComponent subclasses must provide a getUI
     * method that returns the correct type.  For example:
     * <pre>
     * public SliderUI getUI() {
     *     return (SliderUI)ui;
     * }
     * </pre>
     *
     * @see #updateUI
     * @see UIManager#getLookAndFeel
     * @see UIManager#getUI
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The component's look and feel delegate.
     */
    protected void setUI(ComponentUI newUI) {
        /* We do not check that the UI instance is different
         * before allowing the switch in order to enable the
         * same UI instance *with different default settings*
         * to be installed.
         */
        if (ui != null) {
            ui.uninstallUI(this);
        }
        ComponentUI oldUI = ui;
        ui = newUI;
        if (ui != null) {
            ui.installUI(this);
        }

        firePropertyChange("UI", oldUI, newUI);
        revalidate();
        repaint();
    }


    /**
     * Returns the UIDefaults key used to look up the name of the
     * swing.plaf.ComponentUI class that defines the look and feel
     * for this component.  Most applications will never need to
     * call this method.  Subclasses of JComponent that support
     * pluggable look and feel should override this method to
     * return a UIDefaults key that maps to the ComponentUI subclass 
     * that defines their look and feel.
     *
     * @return The UIDefaults key for a ComponentUI subclass.
     * @see UIDefaults#getUI
     * @beaninfo
     *      expert: true
     * description: UIClassID
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the graphics object used to paint this component.
     * If DebugGraphics is turned on we create a new DebugGraphics
     * object if necessary. Otherwise we just configure the
     * specified graphics object's foreground and font.
     *
     * @return a Graphics object configured for this component
     */
    protected Graphics getComponentGraphics(Graphics g) {
        Graphics componentGraphics = g;
        if (ui != null) {
            if ((DebugGraphics.debugComponentCount() != 0) &&
                    (shouldDebugGraphics() != 0) &&
                    !(g instanceof DebugGraphics)) {
                if(g instanceof SwingGraphics) {
                    if(!(((SwingGraphics)g).subGraphics() instanceof DebugGraphics)) {
                        Graphics dbgGraphics = new DebugGraphics(((SwingGraphics)g).subGraphics(),this);
                        componentGraphics = SwingGraphics.createSwingGraphics(dbgGraphics);
			dbgGraphics.dispose();
                    }
                } else {
                    componentGraphics = new DebugGraphics(g,this);
                }
            }
        }
        componentGraphics.setColor(getForeground());
        componentGraphics.setFont(getFont());

        return componentGraphics;
    }


    /**
     * If the UI delegate is non-null, calls its paint
     * method.  We pass the delegate a copy of the Graphics
     * object to protect the rest of the paint code from
     * irrevocable changes (for example, Graphics.translate()).
     *
     * @see #paint
     */
    protected void paintComponent(Graphics g) {
        if (ui != null) {
            Graphics scratchGraphics = SwingGraphics.createSwingGraphics(g);
            try {
                ui.update(scratchGraphics, this);
            }
            finally {
                scratchGraphics.dispose();
            }
        }
    }

    /**
     * Paints this component's children.
     * If shouldUseBuffer is true, no component ancestor has a buffer and
     * the component children can use a buffer if they have one.
     * Otherwise, one ancestor has a buffer currently in use and children
     * should not use a buffer to paint.
     * @see #paint
     * @see java.awt.Container#paint
     */
    protected void paintChildren(Graphics g) {
        boolean isJComponent;
	Graphics sg = null;

        try {
            synchronized(getTreeLock()) {
		int i = getComponentCount() - 1;
		if (i < 0) {
		    return;
		}
		sg = SwingGraphics.createSwingGraphics(g);
		// If we are only to paint to a specific child, determine
		// its index.
		if (paintingChild != null &&
		    (paintingChild instanceof JComponent) &&
		    ((JComponent)paintingChild).isOpaque()) {
		    for (; i >= 0; i--) {
			if (getComponent(i) == paintingChild){
			    break;
			}
		    }
		}
		if(tmpRect == null) {
		    tmpRect = new Rectangle();
		}
		boolean checkSiblings = (!isOptimizedDrawingEnabled() &&
					 checkIfChildObscuredBySibling());
		Rectangle clipBounds = null;
                if (checkSiblings) {
		    clipBounds = sg.getClipBounds();
		    if (clipBounds == null) {
		        clipBounds = new Rectangle(0, 0, _bounds.width,
			                           _bounds.height);
		    }
                }
		boolean printing = getFlag(IS_PRINTING);
                for (; i >= 0 ; i--) {
                    Component comp = getComponent(i);
                    if (comp != null && isLightweightComponent(comp) && 
                        (comp.isVisible() == true)) {
                        Rectangle cr;
                        isJComponent = (comp instanceof JComponent);

                        if(isJComponent) {
                            cr = tmpRect;
                            ((JComponent)comp).getBounds(cr);
                        } else {
                            cr = comp.getBounds();
                        }

			boolean hitClip = 
			    g.hitClip(cr.x, cr.y, cr.width, cr.height);
                        if (hitClip) {
			    if (checkSiblings && i > 0) {
				int x = cr.x;
				int y = cr.y;
				int width = cr.width;
				int height = cr.height;
				SwingUtilities.computeIntersection
				     (clipBounds.x, clipBounds.y,
				      clipBounds.width, clipBounds.height, cr);
				if(rectangleIsObscuredBySibling(i, cr.x, cr.y,
						      cr.width, cr.height)) {
				    continue;
				}
				cr.x = x;
				cr.y = y;
				cr.width = width;
				cr.height = height;
			    }
                            Graphics cg = SwingGraphics.createSwingGraphics(
                                sg, cr.x, cr.y, cr.width, cr.height);
			    cg.setColor(comp.getForeground());
			    cg.setFont(comp.getFont());
                            boolean shouldSetFlagBack = false;
                            try {
                                if(isJComponent) {
                                    if(getFlag(ANCESTOR_USING_BUFFER)) {
                                        ((JComponent)comp).setFlag(ANCESTOR_USING_BUFFER,true);
                                        shouldSetFlagBack = true;
                                    }
                                    if(getFlag(IS_PAINTING_TILE)) {
                                        ((JComponent)comp).setFlag(IS_PAINTING_TILE,true);
                                        shouldSetFlagBack = true;
                                    }
				    if(!printing) {
					((JComponent)comp).paint(cg);
				    }
				    else {
					if (!getFlag(IS_PRINTING_ALL)) {
					    comp.print(cg);
					}
					else {
					    comp.printAll(cg);
					}
				    }
                                } else {
				    if (!printing) {
					comp.paint(cg);
				    }
				    else {
					if (!getFlag(IS_PRINTING_ALL)) {
					    comp.print(cg);
					}
					else {
					    comp.printAll(cg);
					}
				    }
			    }
                            } finally {
                                cg.dispose();
                                if(shouldSetFlagBack) {
                                    ((JComponent)comp).setFlag(ANCESTOR_USING_BUFFER,false);
                                    ((JComponent)comp).setFlag(IS_PAINTING_TILE,false);
                                }
                            }
                        }
                    }

                }
            }
        } finally {
	    if (sg != null) {
		sg.dispose();
	    }
        }
    }

    /**
     * Paints the component's border.
     *
     * @see #paint
     * @see #setBorder
     */
    protected void paintBorder(Graphics g) {
        Border border = getBorder();
        if (border != null) {
            border.paintBorder(this, g, 0, 0, getWidth(), getHeight());
        }
    }


    /**
     * Calls paint(g).  Doesn't clear the background but see
     * ComponentUI.update(), which is called by paintComponent.
     *
     * @see #paint
     * @see #paintComponent
     * @see javax.swing.plaf.ComponentUI
     */
    public void update(Graphics g) {
        paint(g);
    }


    /**
     * This method is invoked by Swing to draw components.
     * Applications should not invoke paint directly,
     * but should instead use the <code>repaint</code> method to
     * schedule the component for redrawing.
     * <p>
     * This method actually delegates the work of painting to three
     * protected methods: <code>paintComponent</code>, <code>paintBorder</code>,
     * and <code>paintChildren</code>.  They're called in the order
     * listed to ensure that children appear on top of component itself.
     * Generally speaking, the component and its children should not
     * paint in the insets area allocated to the border. Subclasses can
     * just override this method, as always.  A subclass that just
     * wants to specialize the UI (look and feel) delegate's paint
     * method should just override <code>paintComponent</code>.
     *
     * @see #paintComponent
     * @see #paintBorder
     * @see #paintChildren
     * @see #getComponentGraphics
     * @see #repaint
     */
    public void paint(Graphics g) {
	boolean shouldClearPaintFlags = false;

        if ((getWidth() <= 0) || (getHeight() <= 0)) {
            return;
        }

        Graphics componentGraphics = getComponentGraphics(g);
        Graphics co = SwingGraphics.createSwingGraphics(componentGraphics);
        try {
            Image offscr = null;
            RepaintManager repaintManager = RepaintManager.currentManager(this);
	    Rectangle clipRect = co.getClipBounds();
            int clipX;
            int clipY;
            int clipW;
            int clipH;
	    if (clipRect == null) {
                clipX = clipY = 0;
		clipW = _bounds.width;
		clipH = _bounds.height;
            }
	    else {
	        clipX = clipRect.x;
		clipY = clipRect.y;
		clipW = clipRect.width;
		clipH = clipRect.height;
            }

            if(clipW > getWidth()) {
                clipW = getWidth();
            }
            if(clipH > getHeight()) {
                clipH = getHeight();
            }

            if(getParent() != null && !(getParent() instanceof JComponent)) {
                adjustPaintFlags();
                shouldClearPaintFlags = true;
            }

	    int bw,bh;
	    boolean printing = getFlag(IS_PRINTING);
            if(!printing && repaintManager.isDoubleBufferingEnabled() &&
               !getFlag(ANCESTOR_USING_BUFFER) && isDoubleBuffered() &&
                (offscr = repaintManager.getOffscreenBuffer
		 (this,clipW,clipH)) != null &&
	        (bw = offscr.getWidth(null)) > 0 &&
	        (bh = offscr.getHeight(null)) > 0) {
                int x,y,maxx,maxy;

		Graphics osg = offscr.getGraphics();
                Graphics sg = 
                    SwingGraphics.createSwingGraphics(osg);
		osg.dispose();
                try {
                    sg.translate(-clipX,-clipY);

                    bw = offscr.getWidth(null);
                    bh = offscr.getHeight(null);

                    if (bw > clipW) {
                        bw = clipW;
                    }
                    if (bh > clipH) {
                        bh = clipH;
                    }

                    setFlag(ANCESTOR_USING_BUFFER,true);
                    setFlag(IS_PAINTING_TILE,true);
                    for(x = 0, maxx = clipW; x < maxx ;  x += bw ) {
                        for(y=0, maxy = clipH; y < maxy ; y += bh) {
                            if((y+bh) >= maxy && (x+bw) >= maxx)
                                setFlag(IS_PAINTING_TILE,false);
                            sg.translate(-x,-y);
                            sg.setClip(clipX+x,clipY + y,bw,bh);
                            if(!rectangleIsObscured(clipX,clipY,bw,bh)) {
                                paintComponent(sg);
                                paintBorder(sg);
                            }
                            paintChildren(sg);
                            co.drawImage(offscr,clipX + x,clipY + y,this);
                            sg.translate(x,y);
                        }
                    }
                } finally {
                    setFlag(ANCESTOR_USING_BUFFER,false);
                    setFlag(IS_PAINTING_TILE,false);
                    sg.dispose();
                }
            } else {
		// Will ocassionaly happen in 1.2, especially when printing.
		if (clipRect == null) {
		    co.setClip(clipX, clipY, clipW, clipH);
		}

                if (!rectangleIsObscured(clipX,clipY,clipW,clipH)) {
		    if (!printing) {
			paintComponent(co);
			paintBorder(co);
		    }
		    else {
			printComponent(co);
			printBorder(co);
		    }
                }
		if (!printing) {
		    paintChildren(co);
		}
		else {
		    printChildren(co);
		}
            }
        } finally {
            co.dispose();
            if(shouldClearPaintFlags) {
                setFlag(ANCESTOR_USING_BUFFER,false);
                setFlag(IS_PAINTING_TILE,false);
                setFlag(IS_PRINTING,false);
                setFlag(IS_PRINTING_ALL,false);
            }
        }
    }
    
    private void adjustPaintFlags() {
	JComponent jparent = null;
	Container parent;
	for(parent = getParent() ; parent != null ; parent =
	    parent.getParent()) {
	    if(parent instanceof JComponent) {
		jparent = (JComponent) parent;
		if(jparent.getFlag(ANCESTOR_USING_BUFFER))
		  setFlag(ANCESTOR_USING_BUFFER, true);
		if(jparent.getFlag(IS_PAINTING_TILE))
		  setFlag(IS_PAINTING_TILE, true);
		if(jparent.getFlag(IS_PRINTING))
		  setFlag(IS_PRINTING, true);
		if(jparent.getFlag(IS_PRINTING_ALL))
		  setFlag(IS_PRINTING_ALL, true);
		break;
	    }
	}
    }

    /**
     * Invoke this method to print the receiver. This method invokes
     * <code>print</code> on the receiver.
     *
     * @see #print
     * @see #printComponent
     * @see #printBorder
     * @see #printChildren
     */
    public void printAll(Graphics g) {
	setFlag(IS_PRINTING_ALL, true);
	try {
	    print(g);
	}
	finally {
	    setFlag(IS_PRINTING_ALL, false);
	}
    }

    /**
     * Invoke this method to print the receiver. This method will
     * result in invocations to <code>printComponent</code>,
     * <code>printBorder</code> and <code>printChildren</code>. It is
     * not recommended that you override this method, instead override
     * one of the previously metioned methods. This method sets the
     * receivers state such that the double buffer will not be used, eg
     * painting will be done directly on the passed in Graphics.
     *
     * @see #printComponent
     * @see #printBorder
     * @see #printChildren
     */
    public void print(Graphics g) {
	setFlag(IS_PRINTING, true);
	try {
	    paint(g);
	}
	finally {
	    setFlag(IS_PRINTING, false);
	}
    }
    
    /**
     * This is invoked during a printing operation. This is implemented to
     * invoke <code>paintComponent</code> on the receiver. Override this
     * if you wish to add special painting behavior when printing.
     *
     * @see #print
     * @since 1.3
     */
    protected void printComponent(Graphics g) {
	paintComponent(g);
    }

    /**
     * Prints this component's children. This is implemented to invoke
     * <code>paintChildren</code> on the receiver. Override this if you
     * wish to print the children differently than painting.
     *
     * @see #print
     * @since 1.3
     */
    protected void printChildren(Graphics g) {
	paintChildren(g);
    }

    /**
     * Prints the component's border. This is implemented to invoke
     * <code>paintBorder</code> on the receiver. Overrides this if you
     * wish to print the border differently that it is painted.
     *
     * @see #print
     * @since 1.3
     */
    protected void printBorder(Graphics g) {
	paintBorder(g);
    }


    /**
     *  Returns true if the receiving component is currently painting a tile.
     *  If this method returns true, paint will be called again for another
     *  tile. This method returns false if you are not painting a tile or
     *  if the last tile is painted.
     *  Use this method to keep some state you might need between tiles.
     */
    public boolean isPaintingTile() {
        return getFlag(IS_PAINTING_TILE);
    }


    /**
     * Override this method and return true if your component is the root of
     * of a component tree with its own focus cycle.
     */
    public boolean isFocusCycleRoot() {
        return false;
    }

    /**
     * Override this method and return true if your JComponent manages focus.
     * If your component manages focus, the focus manager will handle your
     * component's children. All key event will be sent to your key listener
     * including TAB and SHIFT+TAB. CONTROL+TAB and CONTROL+SHIFT+TAB
     * will move the focus to the next or previous component.
     */
    public boolean isManagingFocus() {
        return false;
    }

    /**
     * Specifies the next component to get the focus after this one,
     * for example, when the tab key is pressed. Invoke this method
     * to override the default focus-change sequence.
     * @beaninfo
     *      expert: true
     * description: The next component to get focus after this one.
     */
    public void setNextFocusableComponent(Component aComponent) {
        putClientProperty(NEXT_FOCUS,aComponent);
    }

    /**
     * Returns the next focusable component or null if the focus manager
     * should choose the next focusable component automatically.
     */
    public Component getNextFocusableComponent() {
        return (Component) getClientProperty(NEXT_FOCUS);
    }

    /**
     *  Sets whether the receiving component can obtain the focus by
     *  calling requestFocus. The default value is true.
     *  Note: Setting this property to false will not prevent the focus
     *  manager from setting the focus to this component, it will prevent
     *  the component from getting the focus when the focus is requested
     *  explicitly. Override isFocusTraversable and return false if the
     *  component should never get the focus.
     * @beaninfo
     *      expert: true
     * description: Whether the component can obtain the focus by calling requestFocus.
     */
    public void setRequestFocusEnabled(boolean aFlag) {
        setFlag(REQUEST_FOCUS_DISABLED,(aFlag ? false:true));
    }

    /** Returns whether the receiving component can obtain the focus by
     *  calling requestFocus.
     *  @see #setRequestFocusEnabled
     */
    public boolean isRequestFocusEnabled() {
        return (getFlag(REQUEST_FOCUS_DISABLED) ? false : true);
    }

    /** Sets focus on the receiving component if isRequestFocusEnabled
     *  returns true and the component doesn't already have focus. **/
    public void requestFocus() {

      /* someone other then the focus manager is requesting focus,
	 so we clear the focus manager's idea of focus history */
        FocusManager focusManager = FocusManager.getCurrentManager();
        if (focusManager instanceof DefaultFocusManager) 
	  ((DefaultFocusManager)focusManager).clearHistory();

	if (isRequestFocusEnabled()) grabFocus();
    }

    /** Sets the focus on the receiving component if it doesn't already
     *  have it. This method is for focus managers. You
     *  rarely want to call this method; use requestFocus() instead.
     */
    public void grabFocus() {
        if (hasFocus()) return;
	JRootPane rootPane = getRootPane();
	JComponent lastFocus =
	  (rootPane == null)? null : rootPane.getCurrentFocusOwner();
	InputVerifier iv =
	  (lastFocus == null)? null : lastFocus.getInputVerifier();

	if (!verifyInputWhenFocusTarget) {
	  super.requestFocus();
	} else if ((iv == null) || iv.shouldYieldFocus(lastFocus)) {
	  super.requestFocus();
	}
    }


    /** 
     * Set the value to indicate whether input verifier for the
     * current focus owner will be called before this component requests
     * focus. The default is true. Set to false on components such as a
     * Cancel button or a scrollbar, which should activate even if the
     * input in the current focus owner is not "passed" by the input
     * verifier for that component.
     *
     * @param new value for the verifyInputWhenFocusTarget property
               
     * @see InputVerifier
     * @see #setInputVerifier
     * @see #getInputVerifier
     * @see #getVerifyInputWhenFocusTarget
     *
     * @since 1.3
     */
    public void setVerifyInputWhenFocusTarget(boolean flag) {
        verifyInputWhenFocusTarget = flag;
    }

    /** 
     * Get the value that indicates whether the input verifier for the 
     * current focus owner will be called before this component requests
     * focus.
     *          
     * @return value of the verifyInputWhenFocusTarget property
     *   
     * @see InputVerifier
     * @see #setInputVerifier
     * @see #getInputVerifier
     * @see #setVerifyInputWhenFocusTarget
     *
     *  @since 1.3
     */
    public boolean getVerifyInputWhenFocusTarget() {
        return verifyInputWhenFocusTarget;
    }

    /**
     * Sets the preferred size of the receiving component.
     * If <code>preferredSize</code> is null, the UI will
     * be asked for the preferred size.
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The preferred size of the component.
     */
    public void setPreferredSize(Dimension preferredSize) {
	Dimension old = this.preferredSize;
        this.preferredSize = preferredSize;
        firePropertyChange("preferredSize", old, preferredSize);
    }


    /**
     * If the preferredSize has been set to a non-null value
     * just returns it.  If the UI delegate's getPreferredSize()
     * method returns a non null value then return that; otherwise
     * defer to the component's layout manager.
     *
     * @return the value of the preferredSize property
     * @see #setPreferredSize
     */
    public Dimension getPreferredSize() {
        if (preferredSize != null) {
            return preferredSize;
        }
        Dimension size = null;
        if (ui != null) {
            size = ui.getPreferredSize(this);
        }
        return (size != null) ? size : super.getPreferredSize();
    }


    /**
     * Sets the maximum size of this component to a constant
     * value.  Subsequent calls to getMaximumSize will always
     * return this value; the component's UI will not be asked
     * to compute it.  Setting the maximum size to null
     * restores the default behavior.
     *
     * @see #getMaximumSize
     * @beaninfo
     *       bound: true
     * description: The maximum size of the component.
     */
    public void setMaximumSize(Dimension maximumSize) {
	Dimension old = this.maximumSize;
        this.maximumSize = maximumSize;
        firePropertyChange("maximumSize", old, maximumSize);
    }


    /**
     * If the maximum size has been set to a non-null value
     * just returns it.  If the UI delegate's getMaximumSize()
     * method returns a non null value then return that; otherwise
     * defer to the component's layout manager.
     *
     * @return the value of the maximumSize property.
     * @see #setMaximumSize
     */
    public Dimension getMaximumSize() {
        if (maximumSize != null) {
            return maximumSize;
        }
        Dimension size = null;
        if (ui != null) {
            size = ui.getMaximumSize(this);
        }
        return (size != null) ? size : super.getMaximumSize();
    }


    /**
     * Sets the minimum size of this component to a constant
     * value.  Subsequent calls to getMinimumSize will always
     * return this value; the component's UI will not be asked
     * to compute it.  Setting the minimum size to null
     * restores the default behavior.
     *
     * @see #getMinimumSize
     * @beaninfo
     *       bound: true
     * description: The minimum size of the component.
     */
    public void setMinimumSize(Dimension minimumSize) {
	Dimension old = this.minimumSize;
        this.minimumSize = minimumSize;
        firePropertyChange("minimumSize", old, minimumSize);
    }

    /**
     * If the minimum size has been set to a non-null value
     * just returns it.  If the UI delegate's getMinimumSize()
     * method returns a non-null value then return that; otherwise
     * defer to the component's layout manager.
     *
     * @return the value of the minimumSize property
     * @see #setMinimumSize
     */
    public Dimension getMinimumSize() {
        if (minimumSize != null) {
            return minimumSize;
        }
        Dimension size = null;
        if (ui != null) {
            size = ui.getMinimumSize(this);
        }
        return (size != null) ? size : super.getMinimumSize();
    }

    /**
     * Returns true if the minimum size has been set to a non-null 
     * value otherwise returns false.
     */ 
    public boolean isMinimumSizeSet() { 
	return minimumSize != null; 
    }

    /**
     * Returns true if the preferred size has been set to a non-null 
     * value otherwise returns false.
     */ 
    public boolean isPreferredSizeSet() { 
	return preferredSize != null; 
    }

    /**
     * Returns true if the maximum size has been set to a non-null 
     * value otherwise returns false.
     */ 
    public boolean isMaximumSizeSet() { 
	return maximumSize != null; 
    }

    /**
     * Gives the UI delegate an opportunity to define the precise
     * shape of this component for the sake of mouse processing.
     *
     * @return true if this component logically contains x,y
     * @see java.awt.Component#contains(int, int)
     */
    public boolean contains(int x, int y) {
        return (ui != null) ? ui.contains(this, x, y) : super.contains(x, y);
    }


    /**
     * Sets the border of this component.  The Border object is
     * responsible for defining the insets for the component
     * (overriding any insets set directly on the component) and
     * for optionally rendering any border decorations within the
     * bounds of those insets.  Borders should be used (rather
     * than insets) for creating both decorative and non-decorative
     * (such as margins and padding) regions for a swing component.
     * Compound borders can be used to nest multiple borders within a
     * single component.
     * <p>
     * This is a bound property.
     *
     * @param border the border to be rendered for this component
     * @see Border
     * @see CompoundBorder
     * @beaninfo
     *        bound: true
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The component's border.
     */
    public void setBorder(Border border) {
        Border         oldBorder = this.border;

        this.border = border;
        firePropertyChange("border", oldBorder, border);
        if (border != oldBorder) {
            if (border == null || oldBorder == null ||
                !(border.getBorderInsets(this).equals(oldBorder.getBorderInsets(this)))) {
                revalidate();
            } 
            repaint();
        }
    }

    /**
     * Returns the border of this component or null if no border is
     * currently set.
     *
     * @return the border object for this component
     * @see #setBorder
     */
    public Border getBorder() {
        return border;
    }

    /**
     * If a border has been set on this component, returns the
     * border's insets; otherwise calls super.getInsets.
     *
     * @return the value of the insets property
     * @see #setBorder
     */
    public Insets getInsets() {
        if (border != null) {
            return border.getBorderInsets(this);
        }
        return super.getInsets();
    }

    /**
     * Returns an Insets object containing this component's inset
     * values.  The passed-in Insets object will be reused if possible.
     * Calling methods cannot assume that the same object will be returned,
     * however.  All existing values within this object are overwritten.
     *
     * @param insets the Insets object, which can be reused
     * @see #getInsets
     * @beaninfo
     *   expert: true
     */
    public Insets getInsets(Insets insets) {
        if (border != null) {
            if (border instanceof AbstractBorder) {
                return ((AbstractBorder)border).getBorderInsets(this, insets);
            } else {
                // Can't reuse border insets because the Border interface
                // can't be enhanced.
                return border.getBorderInsets(this);
            }
        } else {
            // super.getInsets() always returns an Insets object with
            // all of its value zeroed.  No need for a new object here.
            insets.left = insets.top = insets.right = insets.bottom = 0;
            return insets;
        }
    }

    /**
     * Overrides <code>Container.getAlignmentY</code> to return
     * the horizontal alignment.
     *
     * @return the value of the alignmentY property
     * @see #setAlignmentY
     * @see java.awt.Component#getAlignmentY
     */
    public float getAlignmentY() {
        return (alignmentY != null) ? alignmentY.floatValue() : super.getAlignmentY();
    }

    /**
     * Sets the the horizontal alignment.
     *
     * @see #getAlignmentY
     * @beaninfo
     *   description: The preferred vertical alignment of the component.
     */
    public void setAlignmentY(float alignmentY) {
        this.alignmentY = new Float(alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f : alignmentY);
    }


    /**
     * Overrides <code>Container.getAlignmentX</code> to return
     * the vertical alignment.
     *
     * @return the value of the alignmentX property
     * @see #setAlignmentX
     * @see java.awt.Component#getAlignmentX
     */
    public float getAlignmentX() {
        return (alignmentX != null) ? alignmentX.floatValue() : super.getAlignmentX();
    }

    /**
     * Sets the the vertical alignment.
     *
     * @see #getAlignmentX
     * @beaninfo
     *   description: The preferred horizontal alignment of the component.
     */
    public void setAlignmentX(float alignmentX) {
        this.alignmentX = new Float(alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f : alignmentX);
    }

    /**
     * Sets the input verifier for this component.
     *
     * @since 1.3
     * @see InputVerifier
     */
    public void setInputVerifier(InputVerifier inputVerifier) {
        this.inputVerifier = inputVerifier;
    }

    /**
     * Returns the input verifier for this component.
     *
     * @since 1.3
     * @see InputVerifier
     */
    public InputVerifier getInputVerifier() {
        return inputVerifier;
    }

    /**
     * Returns this component's graphics context, which lets you draw
     * on a component. Use this method get a Graphics object and
     * then invoke oeprations on that object to draw on the component.
     */
    public Graphics getGraphics() {
        if (shouldDebugGraphics() != 0) {
            DebugGraphics graphics = new DebugGraphics(super.getGraphics(),
                                                       this);
            return graphics;
        }
        return super.getGraphics();
    }


    /** Enables or disables diagnostic information about every graphics
      * operation performed within the component or one of its children. The
      * value of <b>debugOptions</b> determines how the component should
      * display this information:
      * <ul>
      * <li>DebugGraphics.LOG_OPTION - causes a text message to be printed.
      * <li>DebugGraphics.FLASH_OPTION - causes the drawing to flash several
      * times.
      * <li>DebugGraphics.BUFFERED_OPTION - creates an ExternalWindow that
      * displays the operations performed on the View's offscreen buffer.
      * </ul>
      * <b>debug</b> is bitwise OR'd into the current value.
      * DebugGraphics.NONE_OPTION disables debugging.
      * A value of 0 causes no changes to the debugging options.
      * @beaninfo
      *   preferred: true
      *        enum: NONE_OPTION DebugGraphics.NONE_OPTION
      *              LOG_OPTION DebugGraphics.LOG_OPTION
      *              FLASH_OPTION DebugGraphics.FLASH_OPTION
      *              BUFFERED_OPTION DebugGraphics.BUFFERED_OPTION
      * description: Diagnostic options for graphics operations.
      */
    public void setDebugGraphicsOptions(int debugOptions) {
        DebugGraphics.setDebugOptions(this, debugOptions);
    }

    /** Returns the state of graphics debugging.
      * @see #setDebugGraphicsOptions
      */
    public int getDebugGraphicsOptions() {
        return DebugGraphics.getDebugOptions(this);
    }


    /**
     * Returns <b>true</b> if debug information is enabled for this JComponent
     * or one of its parents.
     */
    int shouldDebugGraphics() {
        return DebugGraphics.shouldComponentDebug(this);
    }

    /**
     * This method is now obsolete, please use a combination of
     * <code>getActionMap()</code> and <code>getInputMap()</code> for
     * similiar behavior. For example, to bind the KeyStroke
     * <code>aKeyStroke</code> to the Action <code>anAction</code> now
     * use:
     * <pre>
     *   component.getInputMap().put(aKeyStroke, aCommand);
     *   component.getActionMap().put(aCommmand, anAction);
     * </pre>
     * The above assumes you want the binding to be applicable for
     * <code>WHEN_FOCUSED</code>. To register bindings for other focus
     * states use the <code>getInputMap</code> method that takes an integer.
     * <p>
     * Register a new keyboard action.
     * <b>anAction</b> will be invoked if a key event matching <b>aKeyStroke</b> occurs
     * and <b>aCondition</b> is verified. The KeyStroke object defines a
     * particular combination of a keyboard key and one or more modifiers
     * (alt, shift, ctrl, meta).
     * <p>
     * The <b>aCommand</b> will be set in the delivered event if specified.
     * <p>
     * The Condition can be one of:
     * <blockquote>
     * <DL>
     * <DT>WHEN_FOCUSED
     * <DD>The action will be invoked only when the keystroke occurs
     *     while the component has the focus.
     * <DT>WHEN_IN_FOCUSED_WINDOW
     * <DD>The action will be invoked when the keystroke occurs while
     *     the component has the focus or if the component is in the
     *     window that has the focus. Note that the component need not
     *     be an immediate descendent of the window -- it can be
     *     anywhere in the window's containment hierarchy. In other
     *     words, whenever <em>any</em> component in the window has the focus,
     *     the action registered with this component is invoked.
     * <DT>WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
     * <DD>The action will be invoked when the keystroke occurs while the
     *     component has the focus or if the component is an ancestor of
     *     the component that has the focus.
     * </DL>
     * </blockquote>
     * <p>
     * The combination of keystrokes and conditions lets you define high
     * level (semantic) action events for a specified keystroke+modifier
     * combination (using the KeyStroke class) and direct to a parent or
     * child of a component that has the focus, or to the component itself.
     * In other words, in any hierarchical structure of components, an
     * arbitrary key-combination can be immediately directed to the
     * appropriate component in the hierarchy, and cause a specific method
     * to be invoked (usually by way of adapter objects).
     * <p>
     * If an action has already been registered for the receiving
     * container, with the same charCode and the same modifiers,
     * <b>anAction</b> will replace the action.
     *
     * @see KeyStroke
     */
    public void registerKeyboardAction(ActionListener anAction,String aCommand,KeyStroke aKeyStroke,int aCondition) {

	InputMap inputMap = getInputMap(aCondition, true);

	if (inputMap != null) {
	    ActionMap actionMap = getActionMap(true);
	    ActionStandin action = new ActionStandin(anAction, aCommand);
	    inputMap.put(aKeyStroke, action);
	    if (actionMap != null) {
		actionMap.put(action, action);
	    }
	}
    }

    /**
     * Registers any bound <code>WHEN_IN_FOCUSED_WINDOW</code> actions with
     * the KeyboardManager. If <code>onlyIfNew</code> is true only actions
     * that haven't been registered are pushed to the KeyboardManager;
     * otherwise all actions are pushed to the KeyboardManager.
     */
    private void registerWithKeyboardManager(boolean onlyIfNew) {
	InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW, false);
	KeyStroke[] strokes;
	Hashtable registered = (Hashtable)getClientProperty
	                        (WHEN_IN_FOCUSED_WINDOW_BINDINGS);

	if (inputMap != null) {
	    // Push any new KeyStrokes to the KeyboardManager.
	    strokes = inputMap.allKeys();
	    if (strokes != null) {
		for (int counter = strokes.length - 1; counter >= 0;
		     counter--) {
		    if (!onlyIfNew || registered == null ||
			registered.get(strokes[counter]) == null) {
			registerWithKeyboardManager(strokes[counter]);
		    }
		    if (registered != null) {
			registered.remove(strokes[counter]);
		    }
		}
	    }
	}
	else {
	    strokes = null;
	}
	// Remove any old ones.
	if (registered != null && registered.size() > 0) {
	    Enumeration keys = registered.keys();

	    while (keys.hasMoreElements()) {
		KeyStroke ks = (KeyStroke)keys.nextElement();
		unregisterWithKeyboardManager(ks);
	    }
	    registered.clear();
	}
	// Updated the registered Hashtable.
	if (strokes != null && strokes.length > 0) {
	    if (registered == null) {
		registered = new Hashtable(strokes.length);
		putClientProperty(WHEN_IN_FOCUSED_WINDOW_BINDINGS, registered);
	    }
	    for (int counter = strokes.length - 1; counter >= 0; counter--) {
		registered.put(strokes[counter], strokes[counter]);
	    }
	}
	else {
	    putClientProperty(WHEN_IN_FOCUSED_WINDOW_BINDINGS, null);
	}
    }

    /**
     * Unregisters all the previously registered WHEN_IN_FOCUSED_WINDOW
     * KeyStroke bindings.
     */
    private void unregisterWithKeyboardManager() {
	Hashtable registered = (Hashtable)getClientProperty
	                        (WHEN_IN_FOCUSED_WINDOW_BINDINGS);

	if (registered != null && registered.size() > 0) {
	    Enumeration keys = registered.keys();

	    while (keys.hasMoreElements()) {
		KeyStroke ks = (KeyStroke)keys.nextElement();
		unregisterWithKeyboardManager(ks);
	    }
	}
	putClientProperty(WHEN_IN_FOCUSED_WINDOW_BINDINGS, null);
    }

    /**
     * Invoked from ComponentInputMap when its bindings change. If
     * <code>inputMap</code> is the current windowInputMap (or a parent of
     * the window InputMap is) the KeyboardManager is notified of the
     * new bindings.
     */
    void componentInputMapChanged(ComponentInputMap inputMap) {
	InputMap km = getInputMap(WHEN_IN_FOCUSED_WINDOW, false);

	while (km != inputMap && km != null) {
	    km = (ComponentInputMap)km.getParent();
	}
	if (km != null) {
	    registerWithKeyboardManager(false);
	}
    }

    private void registerWithKeyboardManager(KeyStroke aKeyStroke) {
	KeyboardManager.getCurrentManager().registerKeyStroke(aKeyStroke,this);
    }

    private void unregisterWithKeyboardManager(KeyStroke aKeyStroke) {
	KeyboardManager.getCurrentManager().unregisterKeyStroke(aKeyStroke,
								this);
    }

    /**
     * This method is now obsolete, please use a combination of
     * <code>getActionMap()</code> and <code>getInputMap()</code> for
     * similiar behavior.
     */
    public void registerKeyboardAction(ActionListener anAction,KeyStroke aKeyStroke,int aCondition) {
        registerKeyboardAction(anAction,null,aKeyStroke,aCondition);
    }

    /**
     * This method is now obsolete. To unregister an existing binding
     * you can either remove the binding from the ActionMap/InputMap, or
     * place a dummy binding the InputMap. Removing the binding from
     * the InputMap allows bindings in parent InputMaps to be active,
     * whereas putting a dummy binding in the InputMap effectively disables
     * the binding from ever happening.
     * <p>
     * Unregisters a keyboard action.
     * This will remove the binding from the ActionMap (if it exists) as well
     * as the InputMaps.
     */
    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
	ActionMap am = getActionMap(false);
	for (int counter = 0; counter < 3; counter++) {
	    InputMap km = getInputMap(counter, false);
	    if (km != null) {
		Object actionID = km.get(aKeyStroke);

		if (am != null && actionID != null) {
		    am.remove(actionID);
		}
		km.remove(aKeyStroke);
	    }
	}
    }

    /**
     * Returns the KeyStrokes that will initiate registered actions.
     *
     * @return an array of KeyStroke objects
     * @see #registerKeyboardAction
     */
    public KeyStroke[] getRegisteredKeyStrokes() {
	int[] counts = new int[3];
	KeyStroke[][] strokes = new KeyStroke[3][];

	for (int counter = 0; counter < 3; counter++) {
	    InputMap km = getInputMap(counter, false);
	    strokes[counter] = (km != null) ? km.allKeys() : null;
	    counts[counter] = (strokes[counter] != null) ?
		               strokes[counter].length : 0;
	}
	KeyStroke[] retValue = new KeyStroke[counts[0] + counts[1] +
					    counts[2]];
	for (int counter = 0, last = 0; counter < 3; counter++) {
	    if (counts[counter] > 0) {
		System.arraycopy(strokes[counter], 0, retValue, last,
				 counts[counter]);
		last += counts[counter];
	    }
	}
	return retValue;
    }

    /**
     * Returns the condition that determines whether a registered action
     * occurs in response to the specified keystroke.
     * <p>For Java 2 platform v1.3, a KeyStroke can be associated with more
     * than one condition. For example, 'a' could be bound for the two
     * conditions WHEN_FOCUSED and WHEN_IN_FOCUSED_WINDOW condition.
     *
     * @return the action-keystroke condition
     */
    public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
	for (int counter = 0; counter < 3; counter++) {
	    InputMap inputMap = getInputMap(counter, false);
	    if (inputMap != null && inputMap.get(aKeyStroke) != null) {
		return counter;
	    }
	}
	return UNDEFINED_CONDITION;
    }

    /**
     * Returns the object that will perform the action registered for a
     * given keystroke.
     *
     * @return the ActionListener object invoked when the keystroke occurs
     */
    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
	ActionMap am = getActionMap(false);

	if (am == null) {
	    return null;
	}
	for (int counter = 0; counter < 3; counter++) {
	    InputMap inputMap = getInputMap(counter, false);
	    if (inputMap != null) {
		Object actionBinding = inputMap.get(aKeyStroke);

		if (actionBinding != null) {
		    Action action = am.get(actionBinding);
		    if (action instanceof ActionStandin) {
			return ((ActionStandin)action).actionListener;
		    }
		    return action;
		}
	    }
	}
	return null;
    }

    /**
     * Unregisters all the bindings in the first tier InputMaps and
     * ActionMap. This has the effect of removing any local bindings,
     * and allowing the bindings defined in parent InputMap/ActionMaps
     * (the UI is usually defined in the second tier) to persist.
     */
    public void resetKeyboardActions() {
	// Keys
	for (int counter = 0; counter < 3; counter++) {
	    InputMap inputMap = getInputMap(counter, false);

	    if (inputMap != null) {
		inputMap.clear();
	    }
	}

	// Actions
	ActionMap am = getActionMap(false);

	if (am != null) {
	    am.clear();
	}
    }

    /**
     * Sets the InputMap to use under the condition <code>condition</code> to
     * <code>map</code>. A null value implies you
     * do not want any bindings to be used, even from the UI. This will
     * not reinstall the UI InputMap (if there was one). Condition is
     * one of <code>WHEN_IN_FOCUSED_WINDOW</code>,
     * <code>WHEN_FOCUSED</code> or
     * <code>WHEN_ANCESTOR_OF_FOCUSED_COMPONENT</code>. If condition
     * is <code>WHEN_IN_FOCUSED_WINDOW</code> and <code>map</code> is not
     * a ComponentInputMap, an IllegalArgumentException will be thrown.
     * Similarly, if <code>condition</code> is not one of the values just
     * mentioned an IllegalArgumentException will be thrown.
     *
     * @param condition one of WHEN_IN_FOCUSED_WINDOW, WHEN_FOCUSED,
     *        WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
     * @since 1.3
     */
    public final void setInputMap(int condition, InputMap map) {
	switch (condition) {
	case WHEN_IN_FOCUSED_WINDOW:
	    if (map != null && !(map instanceof ComponentInputMap)) {
		throw new IllegalArgumentException("WHEN_IN_FOCUSED_WINDOW InputMaps must be of type ComponentInputMap");
	    }
	    windowInputMap = (ComponentInputMap)map;
	    setFlag(WIF_INPUTMAP_CREATED, true);
	    registerWithKeyboardManager(false);
	    break;
	case WHEN_ANCESTOR_OF_FOCUSED_COMPONENT:
	    ancestorInputMap = map;
	    setFlag(ANCESTOR_INPUTMAP_CREATED, true);
	    break;
	case WHEN_FOCUSED:
	    focusInputMap = map;
	    setFlag(FOCUS_INPUTMAP_CREATED, true);
	    break;
	default:
	    throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
	}
    }

    /**
     * Returns the InputMap that is used during <code>condition</code>.
     *
     * @param condition one of WHEN_IN_FOCUSED_WINDOW, WHEN_FOCUSED,
     *        WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
     * @since 1.3
     */
    public final InputMap getInputMap(int condition) {
	return getInputMap(condition, true);
    }

    /**
     * Returns the InputMap that is used when the receiver has focus.
     * This is convenience method for <code>getInputMap(WHEN_FOCUSED)</code>.
     *
     * @since JDK1.3
     */
    public final InputMap getInputMap() {
	return getInputMap(WHEN_FOCUSED, true);
    }

    /**
     * Sets the ActionMap to <code>am</code>. This does not set the
     * parent of the <code>am</code> to be the ActionMap from the UI
     * (if there was one), it is up to the caller to have done this.
     * 
     * @since 1.3
     */
    public final void setActionMap(ActionMap am) {
	actionMap = am;
	setFlag(ACTIONMAP_CREATED, true);
    }

    /**
     * Returns the ActionMap used to determine what Action to fire for
     * particular KeyStroke binding. The returned ActionMap, unless otherwise
     * set, will have the ActionMap from the UI set as the parent.
     *
     * @since 1.3
     */
    public final ActionMap getActionMap() {
	return getActionMap(true);
    }

    /**
     * Returns the InputMap to use for condition <code>condition</code>.
     * If the InputMap hasn't been created, and <code>create</code> is
     * true, it will be created.
     */
    final InputMap getInputMap(int condition, boolean create) {
	switch (condition) {
	case WHEN_FOCUSED:
	    if (getFlag(FOCUS_INPUTMAP_CREATED)) {
		return focusInputMap;
	    }
	    // Hasn't been created yet.
	    if (create) {
		InputMap km = new InputMap();
		setInputMap(condition, km);
		return km;
	    }
	    break;
	case WHEN_ANCESTOR_OF_FOCUSED_COMPONENT:
	    if (getFlag(ANCESTOR_INPUTMAP_CREATED)) {
		return ancestorInputMap;
	    }
	    // Hasn't been created yet.
	    if (create) {
		InputMap km = new InputMap();
		setInputMap(condition, km);
		return km;
	    }
	    break;
	case WHEN_IN_FOCUSED_WINDOW:
	    if (getFlag(WIF_INPUTMAP_CREATED)) {
		return windowInputMap;
	    }
	    // Hasn't been created yet.
	    if (create) {
		ComponentInputMap km = new ComponentInputMap(this);
		setInputMap(condition, km);
		return km;
	    }
	    break;
	default:
	    throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
	}
	return null;
    }

    /**
     * Finds the appropriate ActionMap. The arguments are
     * same as in <code>getInputMap</code> (with the exception of the
     * condition flag).
     */
    final ActionMap getActionMap(boolean create) {
	if (getFlag(ACTIONMAP_CREATED)) {
	    return actionMap;
	}
	// Hasn't been created.
	if (create) {
	    ActionMap am = new ActionMap();
	    setActionMap(am);
	    return am;
	}
	return null;
    }

    /**
     * Requests the focus for the component that should have the focus
     * by default. The default implementation will recursively request
     * the focus on the first component that is focus-traversable.
     *
     * @return false if the focus has not been set, otherwise
     *         return true
     */
    public boolean requestDefaultFocus() {
        Component ca[] = getComponents();
        int i;
        for(i=0 ; i < ca.length ; i++) {
            if(ca[i].isFocusTraversable()) {
                if(ca[i] instanceof JComponent) {
                    ((JComponent)ca[i]).grabFocus();
                } else {
                    ca[i].requestFocus();
                }
                return true;
            }
            if(ca[i] instanceof JComponent && !((JComponent)ca[i]).isManagingFocus()) {
                if(((JComponent)(ca[i])).requestDefaultFocus()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearFocusOwners() {
      JRootPane rootPane = getRootPane();
      if (rootPane != null) {
        if (rootPane.getCurrentFocusOwner() == this) {
          /* reset focus owner since this is becoming invisible */
          rootPane.setCurrentFocusOwner(null);
        }
        if (rootPane.getPreviousFocusOwner() == this) {
          rootPane.setPreviousFocusOwner(null);
        }
      }
    } 
   
    /**
     * Makes the component visible or invisible.
     * Overrides <code>Component.setVisible</code>.
     * 
     * @param aFlag  true to make the component visible
     *
     * @beaninfo
     *    attribute: visualUpdate true
     */
    public void setVisible(boolean aFlag) {
        if(aFlag != isVisible()) {
            super.setVisible(aFlag);
            Container parent = getParent();
            if(parent != null) {
                Rectangle r = getBounds();
                parent.repaint(r.x,r.y,r.width,r.height);
            }
	    // Some (all should) LayoutManagers do not consider components
	    // that are not visible. As such we need to revalidate when the
	    // visible bit changes.
	    revalidate();
	    if (!aFlag) {
	      clearFocusOwners();
	    }
        }
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setVisible(boolean)</code>.
     */
    public void hide() {
      super.hide();
      clearFocusOwners();
    }

    /**
     * Sets whether or not this component is enabled.
     * A component that is enabled may respond to user input,
     * while a component that is not enabled cannot respond to 
     * user input.  Some components may alter their visual
     * representation when they are disabled in order to 
     * provide feedback to the user that they cannot take input.
     *
     * @see java.awt.Component#isEnabled
     *
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The enabled state of the component.
     */
    public void setEnabled(boolean enabled) {
        boolean oldEnabled = isEnabled();
        super.setEnabled(enabled);
	if (!enabled && hasFocus()) {
	    FocusManager.getCurrentManager().focusPreviousComponent(this);
	}
        firePropertyChange("enabled", oldEnabled, enabled);
        if (enabled != oldEnabled) {
            repaint();
        }
    }

    /**
     * Sets the foreground color of this component.
     *
     * @see java.awt.Component#getForeground
     *
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The foreground color of the component.
     */
    public void setForeground(Color fg) {
        Color oldFg = getForeground();
	super.setForeground(fg);
	if ((oldFg != null) ? !oldFg.equals(fg) : ((fg != null) && !fg.equals(oldFg))) {
	    // foreground already bound in AWT1.2
	    repaint();
	}
    }

    /**
     * Sets the background color of this component.
     *
     * @see java.awt.Component#getBackground
     *
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The background color of the component.
     */
    public void setBackground(Color bg) {
	Color oldBg = getBackground();
	super.setBackground(bg);
	if ((oldBg != null) ? !oldBg.equals(bg) : ((bg != null) && !bg.equals(oldBg))) {
	    // background already bound in AWT1.2
	    repaint();
	}
    }

    /**
     * Sets the font for this component.
     *
     * @see java.awt.Component#getFont
     *
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The font for the component.
     */
    public void setFont(Font font) {
        Font oldFont = getFont();
        super.setFont(font);
        // font already bound in AWT1.2
        if (font != oldFont) {
            revalidate();
	    repaint();
        }
    }
        

    /**
     * Identifies whether or not this component can receive the focus.
     * A disabled button, for example, would return false.
     *
     * @return true if this component can receive the focus
     */
    public boolean isFocusTraversable() {
      InputMap inputMap = getInputMap(WHEN_FOCUSED, false);
      while (inputMap != null && inputMap.size() == 0) {
	  inputMap = inputMap.getParent();
      }
      return (inputMap != null);
    }

    protected void processFocusEvent(FocusEvent e) {
        switch(e.getID()) {
          case FocusEvent.FOCUS_GAINED:
              setFlag(HAS_FOCUS, true);
	      if (getRootPane() != null) {
		getRootPane().setCurrentFocusOwner(this);
	      }
              break;
          case FocusEvent.FOCUS_LOST:
              setFlag(HAS_FOCUS, false);
	      if (getRootPane() != null) {
		getRootPane().setPreviousFocusOwner(this);
		getRootPane().setCurrentFocusOwner(null);
	      }
              break;
        }

        // Call super *after* setting flag, in case listener calls paint.
        super.processFocusEvent(e);
    }

    /**
     * Processes any key events that the component itself
     * recognizes.  This is called after the focus
     * manager and any interested listeners have been
     * given a chance to steal away the event.  This
     * method is called only if the event has not
     * yet been consumed.  This method is called prior
     * to the keyboard UI logic.
     * <p>
     * This method is implemented to do nothing.  Subclasses would
     * normally override this method if they process some
     * key events themselves.  If the event is processed,
     * it should be consumed.
     */
    protected void processComponentKeyEvent(KeyEvent e) {
    }

    /** Overrides processKeyEvent to process events. **/
    protected void processKeyEvent(KeyEvent e) {
      // focus manager gets to steal the event if it wants it.
      boolean result;
      boolean shouldProcessKey = false;
      if(FocusManager.isFocusManagerEnabled()) {
          FocusManager focusManager = FocusManager.getCurrentManager();
          focusManager.processKeyEvent(this,e);
          if(e.isConsumed()) {
              return;
          }
      }

      // This gives the key event listeners a crack at the event
      super.processKeyEvent(e);

      // give the component itself a crack at the event
      if (! e.isConsumed()) {
          processComponentKeyEvent(e);
      }

      if(e.getID() == KeyEvent.KEY_PRESSED) {
          shouldProcessKey = true;
          if(!KeyboardState.keyIsPressed(e.getKeyCode()))
              KeyboardState.registerKeyPressed(e.getKeyCode());
      } else if(e.getID() == KeyEvent.KEY_RELEASED) {
          if(KeyboardState.keyIsPressed(e.getKeyCode())) {
              shouldProcessKey = true;
              KeyboardState.registerKeyReleased(e.getKeyCode());
          }
      } else if(e.getID() == KeyEvent.KEY_TYPED) {
          shouldProcessKey = true;
      }

      if(e.isConsumed()) {
        return;
      }

      // (PENDING) Hania & Steve - take out this block?  Do we need to do this pressed stuff?
      // And, shouldProcessKey, do we need it?
      if(shouldProcessKey && e.getID() == KeyEvent.KEY_PRESSED) {
        result = processKeyBindings(e,true);
        if(result)
          e.consume();
      } else if(shouldProcessKey && e.getID() == KeyEvent.KEY_RELEASED) {
          result = processKeyBindings(e,false);
          if(result) {
              e.consume();
          }
      } else if(shouldProcessKey && e.getID() == KeyEvent.KEY_TYPED) {
          result = processKeyBindings(e,false);
          if(result) {
              e.consume();
          }
      }
    }

    /**
     * Invoked to process the key bindings for <code>ks</code> as the result
     * of the KeyEvent <code>e</code>. This obtains
     * the appropriate InputMap, gets the binding, gets the action from
     * the ActionMap, and then (if the action is found and the receiver
     * is enabled) invokes notifyAction to notify the action.
     *
     * @return true if there was a binding to an action, and the action
     *         was enabled
     *
     * @since 1.3
     */
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
	InputMap map = getInputMap(condition, false);
	ActionMap am = getActionMap(false);

        if(map != null && am != null && isEnabled()) {
	    Object binding = map.get(ks);
	    Action action = (binding == null) ? null : am.get(binding);
	    if (action != null) {
		return SwingUtilities.notifyAction(action, ks, e, this,
						   e.getModifiers());
	    }
	}
        return false;
    }

    /**
     * This is invoked as the result of a KeyEvent that was not consumed by
     * the FocusManager, KeyListeners, or the component. It will first try
     * WHEN_FOCUSED bindings, then WHEN_ANCESTOR_OF_FOCUSED_COMPONENT bindings,
     * and finally WHEN_IN_FOCUSED_WINDOW bindings.
     */
    boolean processKeyBindings(KeyEvent e, boolean pressed) {
      KeyStroke ks;

      // Get the KeyStroke
      if (e.getID() == KeyEvent.KEY_TYPED) {
	  ks = KeyStroke.getKeyStroke(e.getKeyChar());
      }
      else {
	  ks = KeyStroke.getKeyStroke(e.getKeyCode(),e.getModifiers(),
				    (pressed ? false:true));
      }

      /* Do we have a key binding for e? */
      if(processKeyBinding(ks, e, WHEN_FOCUSED, pressed))
	  return true;

      /* We have no key binding. Let's try the path from our parent to the
       * window excluded. We store the path components so we can avoid
       * asking the same component twice.
       */
      Container parent = this;
      while (parent != null && !(parent instanceof Window) &&
	     !(parent instanceof Applet)) {
	  if(parent instanceof JComponent) {
	      if(((JComponent)parent).processKeyBinding(ks, e,
			       WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, pressed))
		  return true;
	  }
	  // This is done so that the children of a JInternalFrame are
	  // given precedence for WHEN_IN_FOCUSED_WINDOW bindings before
	  // other components WHEN_IN_FOCUSED_WINDOW bindings. This also gives
	  // more precedence to the WHEN_IN_FOCUSED_WINDOW bindings of the
	  // JInternalFrame's children vs the
	  // WHEN_ANCESTOR_OF_FOCUSED_COMPONENT bindings of the parents.
	  // maybe generalize from JInternalFrame (like isFocusCycleRoot).
	  if ((parent instanceof JInternalFrame) &&
	      JComponent.processKeyBindingsForAllComponents(e,parent,pressed)){
	      return true;
	  }
	  parent = parent.getParent();
      }

      /* No components between the focused component and the window is
       * actually interested by the key event. Let's try the other
       * JComponent in this window.
       */
      if(parent != null) {
        return JComponent.processKeyBindingsForAllComponents(e,parent,pressed);
      }
      return false;
    }

    static boolean processKeyBindingsForAllComponents(KeyEvent e,
				      Container container, boolean pressed) {
	return KeyboardManager.getCurrentManager().fireKeyboardAction
	                       (e, pressed, container);
    }

    /**
     * Registers the text to display in a tool tip.
     * The text displays when the cursor lingers over the component.
     * <p>
     * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/tooltip.html">How to Use Tool Tips</a>
     * in <em>The Java Tutorial</em>
     * for further documentation.
     *
     * @param text  the string to display; if the text is null,
     *              the tool tip is turned off for this component
     * @see #TOOL_TIP_TEXT_KEY
     * @beaninfo
     *   preferred: true
     * description: The text to display in a tool tip.
     */
    public void setToolTipText(String text) {
        putClientProperty(TOOL_TIP_TEXT_KEY, text);
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        if (text != null) {
            toolTipManager.registerComponent(this);
        } else {
            toolTipManager.unregisterComponent(this);
        }
    }

    /**
     * Returns the tooltip string that has been set with setToolTipText().
     *
     * @return the text of the tool tip
     * @see #TOOL_TIP_TEXT_KEY
     */
    public String getToolTipText() {
        return (String)getClientProperty(TOOL_TIP_TEXT_KEY);
    }


    /**
     * Returns the string to be used as the tooltip for <i>event</i>.  By default
     * this returns any string set using setToolTipText().  If a component provides
     * more extensive API to support differing tooltips at different locations,
     * this method should be overridden.
     */
    public String getToolTipText(MouseEvent event) {
        return getToolTipText();
    }

    /**
     * Returns the tooltip location in this component's coordinate system.
     * If null is returned, Swing will choose a location.
     * The default implementation returns null.
     *
     * @param event  the MouseEvent that caused the ToolTipManager to
     *               show the tooltip
     */
    public Point getToolTipLocation(MouseEvent event) {
        return null;
    }


    /**
     * Returns the instance of JToolTip that should be used to display the tooltip.
     * Components typically would not override this method, but it can be used to
     * cause different tooltips to be displayed differently.
     */
    public JToolTip createToolTip() {
        JToolTip tip = new JToolTip();
        tip.setComponent(this);
        return tip;
    }

    /**
     * Forwards the <b>scrollRectToVisible()</b> message to the JComponent's
     * parent. Components that can service the request, such as JViewport,
     * override this method and perform the scrolling.
     *
     * @see JViewport
     */
    public void scrollRectToVisible(Rectangle aRect) {
        Container parent;
        int dx = getX(), dy = getY();

        for (parent = getParent();
                 !(parent == null) &&
                 !(parent instanceof JComponent) &&
                 !(parent instanceof CellRendererPane);
             parent = parent.getParent()) {
             Rectangle bounds = parent.getBounds();

             dx += bounds.x;
             dy += bounds.y;
        }

        if (!(parent == null) && !(parent instanceof CellRendererPane)) {
            aRect.x += dx;
            aRect.y += dy;
            ((JComponent)parent).scrollRectToVisible(aRect);
            aRect.x -= dx;
            aRect.y -= dy;
        }
    }

    /**
     * If <i>true</i> this component will automatically scroll its contents when
     * dragged, if contained in a component that supports scrolling, such as
     * JViewport.
     *
     * @see JViewport
     * @see #getAutoscrolls
     *
     * @beaninfo
     *      expert: true
     * description: Whether this component automatically scrolls its contents when dragged.
     */
    public void setAutoscrolls(boolean autoscrolls) {
        if (autoscrolls) {
            if (autoscroller == null) {
                autoscroller = new Autoscroller(this);
            }
        } else {
            if (autoscroller != null) {
                autoscroller.dispose();
                autoscroller = null;
            }
        }
    }

    /**
     * Returns <i>true</i> if this component automatically scrolls its
     * contents when dragged (when contained in a component that supports
     * scrolling, like JViewport).
     *
     * @see JViewport
     * @see #setAutoscrolls
     */
    public boolean getAutoscrolls() {
        return autoscroller != null;
    }


    protected void processMouseMotionEvent(MouseEvent e) {
        boolean dispatch = true;
        if (autoscroller != null) {
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                // We don't want to do the drags when the mouse moves if we're
                // autoscrolling.  It makes it feel spastic.
                dispatch = !autoscroller.timer.isRunning();
                autoscroller.mouseDragged(e);
            }
        }
        if (dispatch) {
            super.processMouseMotionEvent(e);
        }
    }

    // Inner classes can't get at this method from a super class
    void superProcessMouseMotionEvent(MouseEvent e) {
        super.processMouseMotionEvent(e);
    }

    /**
     * This is invoked by the RepaintManager if <code>createImage</code>
     * is called on the receiver.
     */
    void setCreatedDoubleBuffer(boolean newValue) {
	setFlag(CREATED_DOUBLE_BUFFER, newValue);
    }

    /**
     * Returns true if the RepaintManager created the double buffer image
     * from the receiver.
     */
    boolean getCreatedDoubleBuffer() {
	return getFlag(CREATED_DOUBLE_BUFFER);
    }

    /**
     * ActionStandin is used as a standin for ActionListeners that are
     * added via <code>registerKeyboardAction</code>.
     */
    final class ActionStandin implements Action {
	private final ActionListener actionListener;
	private final String command;
	// This will be non-null if actionListener is an Action.
	private final Action action;

	ActionStandin(ActionListener actionListener, String command) {
	    this.actionListener = actionListener;
	    if (actionListener instanceof Action) {
		this.action = (Action)actionListener;
	    }
	    else {
		this.action = null;
	    }
	    this.command = command;
	}

	public Object getValue(String key) {
	    if (key != null) {
		if (key.equals(Action.ACTION_COMMAND_KEY)) {
		    return command;
		}
		if (action != null) {
		    return action.getValue(key);
		}
		if (key.equals(NAME)) {
		    return "ActionStandin";
		}
	    }
	    return null;
	}

	public boolean isEnabled() {
            if (actionListener == null) {
                // This keeps the old semantics where
                // registerKeyboardAction(null) would essentialy remove
                // the binding. We don't remove the binding from the
                // InputMap as that would still allow parent InputMaps
                // bindings to be accessed.
                return false;
            }
	    if (action == null) {
		return true;
	    }
	    return action.isEnabled();
	}

	public void actionPerformed(ActionEvent ae) {
            if (actionListener != null) {
                actionListener.actionPerformed(ae);
            }
	}

	// We don't allow any values to be added.
	public void putValue(String key, Object value) {}

	// Does nothing, our enabledness is determiend from our asociated
	// action.
	public void setEnabled(boolean b) { }

	public void addPropertyChangeListener
                    (PropertyChangeListener listener) {}
	public void removePropertyChangeListener
	                  (PropertyChangeListener listener) {}
    }


    // This class is used by the KeyboardState class to provide a single
    // instance that can be stored in the AppContext.
    static final class IntVector {
        int array[] = null;
        int count = 0;
        int capacity = 0;

        int size() {
            return count;
        }

        int elementAt(int index) {
            return array[index];
        }

        void addElement(int value) {
            if (count == capacity) {
                capacity = (capacity + 2) * 2;
                int[] newarray = new int[capacity];
                if (count > 0) {
                    System.arraycopy(array, 0, newarray, 0, count);
                }
                array = newarray;
            }
            array[count++] = value;
        }

        void setElementAt(int value, int index) {
            array[index] = value;
        }
    }

    static class KeyboardState implements Serializable {
        private static final Object keyCodesKey =
            JComponent.KeyboardState.class;

        // Get the array of key codes from the AppContext.
        static IntVector getKeyCodeArray() {
            IntVector iv =
                (IntVector)SwingUtilities.appContextGet(keyCodesKey);
            if (iv == null) {
                iv = new IntVector();
                SwingUtilities.appContextPut(keyCodesKey, iv);
            }
            return iv;
        }

        static void registerKeyPressed(int keyCode) {
            IntVector kca = getKeyCodeArray();
            int count = kca.size();
            int i;
            for(i=0;i<count;i++) {
                if(kca.elementAt(i) == -1){
                    kca.setElementAt(keyCode, i);
                    return;
                }
            }
            kca.addElement(keyCode);
        }

        static void registerKeyReleased(int keyCode) {
            IntVector kca = getKeyCodeArray();
            int count = kca.size();
            int i;
            for(i=0;i<count;i++) {
                if(kca.elementAt(i) == keyCode) {
                    kca.setElementAt(-1, i);
                    return;
                }
            }
        }

        static boolean keyIsPressed(int keyCode) {
            IntVector kca = getKeyCodeArray();
            int count = kca.size();
            int i;
            for(i=0;i<count;i++) {
                if(kca.elementAt(i) == keyCode) {
                    return true;
                }
            }
            return false;
        }
    }


    /*
     * --- Accessibility Support ---
     */

    /**
     * overridden to ensure Accessibility support
     */
    public void enable() {
        if (isEnabled() != true) {
            super.enable();
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                    null, AccessibleState.ENABLED);
            }
        }
    }

    /**
     * overridden to ensure Accessibility support
     */
    public void disable() {
        if (isEnabled() != false) {
            super.disable();
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                    AccessibleState.ENABLED, null);
            }
        }
    }

    /** The AccessibleContext associated with this JComponent. */
    protected AccessibleContext accessibleContext = null;

    /**
     * Gets the AccessibleContext associated with this JComponent.
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        return accessibleContext;
    }

    /**
     * Inner class of JComponent used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by component developers.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public abstract class AccessibleJComponent 
        extends AccessibleAWTContainer
    {

	/**
	 * Though the class is abstract, this should be called by
	 * all sub-classes. 
	 */
	protected AccessibleJComponent() {
            super();
        }

	protected ContainerListener accessibleContainerHandler = null;
    	protected FocusListener accessibleFocusHandler = null;

	/**
	 * Fire PropertyChange listener, if one is registered,
	 * when children added/removed.
	 */
	protected class AccessibleContainerHandler 
	    implements ContainerListener {
	    public void componentAdded(ContainerEvent e) {
		Component c = e.getChild();
		if (c != null && c instanceof Accessible) {
		    AccessibleJComponent.this.firePropertyChange(
			AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, 
			null, ((Accessible) c).getAccessibleContext());
		}
	    }
	    public void componentRemoved(ContainerEvent e) {
		Component c = e.getChild();
		if (c != null && c instanceof Accessible) {
		    AccessibleJComponent.this.firePropertyChange(
			AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, 
			((Accessible) c).getAccessibleContext(), null); 
		}
	    }
	}

	/**
	 * Fire PropertyChange listener, if one is registered,
	 * when focus events happen
	 */
	protected class AccessibleFocusHandler implements FocusListener {
	   public void focusGained(FocusEvent event) {
	       if (accessibleContext != null) {
		    accessibleContext.firePropertyChange(
			AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
			null, AccessibleState.FOCUSED);
		}
	    }
	    public void focusLost(FocusEvent event) {
		if (accessibleContext != null) {
		    accessibleContext.firePropertyChange(
			AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
			AccessibleState.FOCUSED, null);
		}
	    }
	} // inner class AccessibleFocusHandler


	/**
	 * Adds a PropertyChangeListener to the listener list.
	 *
	 * @param listener  the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	    if (accessibleFocusHandler == null) {
		accessibleFocusHandler = new AccessibleFocusHandler();
		JComponent.this.addFocusListener(accessibleFocusHandler);
	    }
	    if (accessibleContainerHandler == null) {
		accessibleContainerHandler = new AccessibleContainerHandler();
		JComponent.this.addContainerListener(accessibleContainerHandler);
	    }
	    super.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener  the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	    if (accessibleFocusHandler != null) {
		JComponent.this.removeFocusListener(accessibleFocusHandler);
		accessibleFocusHandler = null;
	    }
	    super.removePropertyChangeListener(listener);
	}



	/**
         * Recursively search through the border hierarchy (if it exists)
	 * for a TitledBorder with a non-null title.  This does a depth
         * first search on first the inside borders then the outside borders.
         * The assumption is that titles make really pretty inside borders
         * but not very pretty outside borders in compound border situations.
	 * It's rather arbitrary, but hopefully decent UI programmers will
         * not create multiple titled borders for the same component.
	 */
        protected String getBorderTitle(Border b) {
	    String s;
	    if (b instanceof TitledBorder) {
	        return ((TitledBorder) b).getTitle();
	    } else if (b instanceof CompoundBorder) {
	        s = getBorderTitle(((CompoundBorder) b).getInsideBorder());
		if (s == null) {
		    s = getBorderTitle(((CompoundBorder) b).getOutsideBorder());
		}
		return s;
	    } else {
	        return null;
	    }
	}
	        
        // AccessibleContext methods
        //
        /**
         * Gets the accessible name of this object.  This should almost never
         * return java.awt.Component.getName(), as that generally isn't
         * a localized name, and doesn't have meaning for the user.  If the
         * object is fundamentally a text object (such as a menu item), the
         * accessible name should be the text of the object (for example, 
         * "save").
         * If the object has a tooltip, the tooltip text may also be an
         * appropriate String to return.
         *
         * @return the localized name of the object -- can be null if this
         *         object does not have a name
         * @see AccessibleContext#setAccessibleName
         */
        public String getAccessibleName() {
	    String name = accessibleName;

	    // fallback to the titled border if it exists
	    //
            if (name == null) {
		name = getBorderTitle(getBorder());
            }

	    // fallback to the label labeling us if it exists
	    //
            if (name == null) {
		Object o = getClientProperty(JLabel.LABELED_BY_PROPERTY);
		if (o instanceof Accessible) {
		    AccessibleContext ac = ((Accessible) o).getAccessibleContext();
		    if (ac != null) {
			name = ac.getAccessibleName();
		    }
		}
	    }
	    return name;
        }

        /**
         * Gets the accessible description of this object.  This should be
         * a concise, localized description of what this object is - what
         * is its meaning to the user.  If the object has a tooltip, the
         * tooltip text may be an appropriate string to return, assuming
         * it contains a concise description of the object (instead of just
         * the name of the object - for example a "Save" icon on a toolbar that
         * had "save" as the tooltip text shouldn't return the tooltip
         * text as the description, but something like "Saves the current
         * text document" instead).
         *
         * @return the localized description of the object -- can be null if
         * this object does not have a description
         * @see AccessibleContext#setAccessibleDescription
         */
        public String getAccessibleDescription() {
	    String description = accessibleDescription;

	    // fallback to the tool tip text if it exists
	    //
            if (description == null) {
                try {
                    description = getToolTipText(null);
                } catch (Exception e) {
                    // Just in case the subclass overrode the
                    // getToolTipText method and actually
                    // requires a MouseEvent.
                    // [[[FIXME:  WDW - we probably should require this
                    // method to take a MouseEvent and just pass it on
                    // to getToolTipText.  The swing-feedback traffic
                    // leads me to believe getToolTipText might change,
                    // though, so I was hesitant to make this change at
                    // this time.]]]
                }
            }

	    // fallback to the label labeling us if it exists
	    //
            if (description == null) {
		Object o = getClientProperty(JLabel.LABELED_BY_PROPERTY);
		if (o instanceof Accessible) {
		    AccessibleContext ac = ((Accessible) o).getAccessibleContext();
		    if (ac != null) {
			description = ac.getAccessibleDescription();
		    }
		}
	    }

	    return description;
        }

        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SWING_COMPONENT;
        }

        /**
         * Gets the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
	    AccessibleStateSet states = super.getAccessibleStateSet();
	    if (JComponent.this.isOpaque()) {
		states.add(AccessibleState.OPAQUE);
	    }
	    return states;
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return super.getAccessibleChildrenCount();
        }

        /**
         * Returns the nth Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return super.getAccessibleChild(i);
        }

    } // inner class AccessibleJComponent


    
    /**
     * @return a small Hashtable
     * @see #putClientProperty
     * @see #getClientProperty
     */
    private Dictionary getClientProperties() {
        if (clientProperties == null) {
            clientProperties = new Hashtable(2);
        }
        return clientProperties;
    }


    private static final String htmlKey = "html";
    private Object htmlView;

    /**
     * Returns the value of the property with the specified key.  Only
     * properties added with <code>putClientProperty</code> will return
     * a non-null value.  
     * 
     * @return the value of this property or null
     * @see #putClientProperty
     */
    public final Object getClientProperty(Object key) {
         if(clientProperties == null) {
 	    return null;
 	} else if (key==htmlKey) {
	    // System.out.println("Retrieving HTML Key");
	    return htmlView;
	} else {
 	    return getClientProperties().get(key);
	}
    }
    
    /**
     * Adds an arbitrary key/value "client property" to this component.
     * <p>
     * The <code>get/putClientProperty</code> methods provide access to 
     * a small per-instance hashtable. Callers can use get/putClientProperty
     * to annotate components that were created by another module. For example, a
     * layout manager might store per child constraints this way. For example:
     * <pre>
     * componentA.putClientProperty("to the left of", componentB);
     * </pre>
     * If value is null this method will remove the property.
     * Changes to client properties are reported with PropertyChange
     * events.  The name of the property (for the sake of PropertyChange
     * events) is <code>key.toString()</code>.  
     * <p>
     * The clientProperty dictionary is not intended to support large 
     * scale extensions to JComponent nor should be it considered an 
     * alternative to subclassing when designing a new component.
     * 
     * @see #getClientProperty
     * @see #addPropertyChangeListener
     */
    public final void putClientProperty(Object key, Object value) {
        Object oldValue = getClientProperties().get(key);

        if (value != null) {
            if (key==htmlKey) {
		htmlView=value;
	    } else {
		getClientProperties().put(key, value);
	        firePropertyChange(key.toString(), oldValue, value);
	    }
        } else if (oldValue != null) {
            getClientProperties().remove(key);
	    firePropertyChange(key.toString(), oldValue, value);
        }
	else if (key == htmlKey) {
	    htmlView = null;
	}
    }


    /* --- Transitional java.awt.Component Support ---
     * The methods and fields in this section will migrate to
     * java.awt.Component in the next JDK release.
     */

    private SwingPropertyChangeSupport changeSupport;


    /**
     * Returns true if this component is a lightweight, that is, if it doesn't
     * have a native window system peer.
     *
     * @return true if this component is a lightweight
     */
    public static boolean isLightweightComponent(Component c) {
        return c.getPeer() instanceof java.awt.peer.LightweightPeer;
    }


    /**
     * Moves and resizes this component.
     *
     * @see java.awt.Component#setBounds
     */
    public void reshape(int x, int y, int w, int h) {
	if (_bounds.x == x && _bounds.y == y && _bounds.width == w &&
	    _bounds.height == h) {
	    // No change.
	    return;
	}
        if(isShowing()) {
            /* If there is an intersection between the new bounds and the old
             * one, refresh only the visible rects
             */
            if(!((_bounds.x + _bounds.width <= x) ||
                 (_bounds.y + _bounds.height <= y) ||
                 (_bounds.x >= (x + w)) ||
                 (_bounds.y >= (y + h)))) {
                Rectangle[] rev = SwingUtilities.computeDifference(getBounds(),
                                                                   new Rectangle(x,y,w,h));
                int i,c;
                Container parent = getParent();
                for(i=0,c=rev.length ; i < c ; i++) {
                    parent.repaint(rev[i].x,rev[i].y,rev[i].width,rev[i].height);
                    // System.out.println("Repaint " + rev[i]);
                }
            } else {
                getParent().repaint(_bounds.x,_bounds.y,_bounds.width,_bounds.height);
            }
        }
        _bounds.setBounds(x, y, w, h);
        super.reshape(x, y, w, h);
    }


    /**
     * Stores the bounds of this component into "return value" <b>rv</b> and
     * returns <b>rv</b>.  If rv is null a new Rectangle is allocated.
     * This version of getBounds() is useful if the caller
     * wants to avoid allocating a new Rectangle object on the heap.
     *
     * @param rv the return value, modified to the component's bounds
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
     * Stores the width/height of this component into "return value" <b>rv</b>
     * and returns <b>rv</b>.   If rv is null a new Dimension object is
     * allocated.  This version of getSize() is useful if the
     * caller wants to avoid allocating a new Dimension object on the heap.
     *
     * @param rv the return value, modified to the component's size
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
     * Stores the x,y origin of this component into "return value" <b>rv</b>
     * and returns <b>rv</b>.   If rv is null a new Point is allocated.
     * This version of getLocation() is useful if the
     * caller wants to avoid allocating a new Point object on the heap.
     *
     * @param rv the return value, modified to the component's location
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
     * Returns the current x coordinate of the component's origin.
     * This method is preferable to writing component.getBounds().x,
     * or component.getLocation().x because it doesn't cause any
     * heap allocations.
     *
     * @return the current x coordinate of the component's origin
     */
    public int getX() { return _bounds.x; }


    /**
     * Returns the current y coordinate of the component's origin.
     * This method is preferable to writing component.getBounds().y,
     * or component.getLocation().y because it doesn't cause any
     * heap allocations.
     *
     * @return the current y coordinate of the component's origin
     */
    public int getY() { return _bounds.y; }


    /**
     * Returns the current width of this component.
     * This method is preferable to writing component.getBounds().width,
     * or component.getSize().width because it doesn't cause any
     * heap allocations.
     *
     * @return the current width of this component.
     */
    public int getWidth() { return _bounds.width; }


    /**
     * Returns the current height of this component.
     * This method is preferable to writing component.getBounds().height,
     * or component.getSize().height because it doesn't cause any
     * heap allocations.
     *
     * @return the current height of this component.
     */
    public int getHeight() { return _bounds.height; }


    /**
     * Returns true if this Component has the keyboard focus.
     *
     * @return true if this Component has the keyboard focus.
     */
    public boolean hasFocus() {
        return getFlag(HAS_FOCUS);
    }


    /**
     * Returns true if this component is completely opaque.
     * <p>
     * An opaque component paints every pixel within its
     * rectangular bounds. A non-opaque component paints only a subset of
     * its pixels or none at all, allowing the pixels underneath it to
     * "show through".  Therefore, a component that does not fully paint
     * its pixels provides a degree of transparency.
     * <p>
     * Subclasses that guarantee to always completely paint their contents
     * should override this method and return true.
     *
     * @return true if this component is completely opaque
     * @see #setOpaque
     */
    public boolean isOpaque() {
        return getFlag(IS_OPAQUE);
    }


    /**
     * If true the component paints every pixel within its bounds. 
     * Otherwise, the component may not paint some or all of its
     * pixels, allowing the underlying pixels to show through.
     * <p>
     * The default value of this property is false for <code>JComponent</code>.
     * However, the default value for this property on most standard
     * <code>JComponent</code> subclasses (such as <code>JButton</code> and
     * <code>JTree</code>) is look-and-feel dependent.
     *
     * @see #isOpaque
     * @beaninfo
     *        bound: true
     *       expert: true
     *  description: The component's opacity
     */
    public void setOpaque(boolean isOpaque) {
        boolean oldValue = getFlag(IS_OPAQUE);
        setFlag(IS_OPAQUE, isOpaque);
        firePropertyChange("opaque", oldValue, isOpaque);
    }


    /**
     * If the specified retangle is completely obscured by any of this
     * component's opaque children then returns true.  Only direct children
     * are considered, more distant descendants are ignored.  A JComponent
     * is opaque if JComponent.isOpaque() returns true, other lightweight
     * components are always considered transparent, and heavyweight components
     * are always considered opaque.
     *
     * @return true if the specified rectangle is obscured by an opaque child
     */
    boolean rectangleIsObscured(int x,int y,int width,int height)
    {
        int numChildren = getComponentCount();

        for(int i = 0; i < numChildren; i++) {
            Component child = getComponent(i);
            Rectangle childBounds;

            if (child instanceof JComponent) {
                childBounds = ((JComponent)child)._bounds;
            } else {
                childBounds = child.getBounds();
            }

            if (x >= childBounds.x && (x + width) <= (childBounds.x + childBounds.width) &&
                y >= childBounds.y && (y + height) <= (childBounds.y + childBounds.height) && child.isVisible()) {

                if(child instanceof JComponent) {
//		    System.out.println("A) checking opaque: " + ((JComponent)child).isOpaque() + "  " + child);
//		    System.out.print("B) ");
//		    Thread.dumpStack();
                    return ((JComponent)child).isOpaque();
                } else {
                    /** Sometimes a heavy weight can have a bound larger than its peer size
                     *  so we should always draw under heavy weights
                     */
                    return false;
                }
            }
        }

        return false;
    }


    /**
     * Returns the Component's "visible rect rectangle" -  the
     * intersection of the visible rectangles for this component
     * and all of its ancestors.  The return value is stored in
     * <code>visibleRect</code>.
     *
     * @see #getVisibleRect
     */
    static final void computeVisibleRect(Component c, Rectangle visibleRect) {
        Container p = c.getParent();
        Rectangle bounds = c.getBounds();

        if (p == null || p instanceof Window || p instanceof Applet) {
            visibleRect.setBounds(0, 0, bounds.width, bounds.height);
        } else {
            computeVisibleRect(p, visibleRect);
            visibleRect.x -= bounds.x;
            visibleRect.y -= bounds.y;
            SwingUtilities.computeIntersection(0,0,bounds.width,bounds.height,visibleRect);
        }
    }


    /**
     * Returns the Component's "visible rect rectangle" -  the
     * intersection of the visible rectangles for this component
     * and all of its ancestors.  The return value is stored in
     * <code>visibleRect</code>.
     *
     * @see #getVisibleRect
     */
    public void computeVisibleRect(Rectangle visibleRect) {
        computeVisibleRect(this, visibleRect);
    }


    /**
     * Returns the Component's "visible rectangle" -  the
     * intersection of this component's visible rectangle:
     * <pre>
     * new Rectangle(0, 0, getWidth(), getHeight());
     * </pre>
     * and all of its ancestors' visible Rectangles.
     *
     * @return the visible rectangle
     */
    public Rectangle getVisibleRect() {
        Rectangle visibleRect = new Rectangle();

        computeVisibleRect(visibleRect);
        return visibleRect;
    }


    /**
     * Supports reporting bound property changes.  If oldValue and
     * newValue are not equal and the PropertyChangeEvent listener list
     * isn't empty, then fire a PropertyChange event to each listener.
     * This method has an overloaded method for each primitive type.  For
     * example, here's how to write a bound property set method whose
     * value is an int:
     * <pre>
     * public void setFoo(int newValue) {
     *     int oldValue = foo;
     *     foo = newValue;
     *     firePropertyChange("foo", oldValue, newValue);
     * }
     * </pre>
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue  the old value of the property
     * @param newValue  the new value of the property
     * @see java.beans.PropertyChangeSupport
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /*
     * PENDING(hmuller) in JDK1.2 the following firePropertyChange overloads
     * should additional check for a non-empty listener list with
     * changeSupport.hasListeners(propertyName) before calling firePropertyChange.
     */

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Byte(oldValue), new Byte(newValue));
        }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
     public void firePropertyChange(String propertyName, char oldValue, char newValue) {
         if ((changeSupport != null) && (oldValue != newValue)) {
             changeSupport.firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
         }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Short(oldValue), new Short(newValue));
        }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
        }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
        }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
        }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
        }
    }

    /**
     * Reports a bound property change.
     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if ((changeSupport != null) && (oldValue != newValue)) {
            changeSupport.firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
        }
    }


    /**
     * Adds a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired in response to setting
     * a bound property, such as setFont, setBackground, or setForeground.
     * Note that if the current component is inheriting its foreground,
     * background, or font from its container, then no event will be
     * fired in response to a change in the inherited property.
     *
     * @param listener  the PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }


    /**
     * Adds a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     *
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param propertyName  the name of the property to listen on
     * @param listener  the PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(
				String propertyName,
				PropertyChangeListener listener) {
	if (listener == null) {
	    return;
	}
	if (changeSupport == null) {
	    changeSupport = new SwingPropertyChangeSupport(this);
	}
	changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  the PropertyChangeListener to be removed
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }


    /**
     * Removes a PropertyChangeListener for a specific property.
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param propertyName  the name of the property that was listened on
     * @param listener  the PropertyChangeListener to be removed
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
     * Supports reporting constrained property changes.  This method can be called
     * when a constrained property has changed and it will send the appropriate
     * PropertyChangeEvent to any registered VetoableChangeListeners.
     *
     * @exception PropertyVetoException when the attempt to set the property is vetoed
     *            by the receiver.
     */
    protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue)
        throws java.beans.PropertyVetoException
    {
        if (vetoableChangeSupport == null) {
            return;
        }
        vetoableChangeSupport.fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * Adds a VetoableChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener  the VetoableChangeListener to be added
     */
    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
        if (vetoableChangeSupport == null) {
            vetoableChangeSupport = new java.beans.VetoableChangeSupport(this);
        }
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }


    /**
     * Removes a VetoableChangeListener from the listener list.
     * This removes a VetoableChangeListener that was registered
     * for all properties.
     *
     * @param listener  the VetoableChangeListener to be removed
     */
    public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
        if (vetoableChangeSupport == null) {
            return;
        }
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }


    /**
     * Returns the top-level ancestor of this component (either the
     * containing Window or Applet), or null if this component has not
     * been added to any container.
     *
     * @return the top-level Container that this component is in
     */
    public Container getTopLevelAncestor() {
        for(Container p = this; p != null; p = p.getParent()) {
            if(p instanceof Window || p instanceof Applet) {
                return p;
            }
        }
        return null;
    }


    /**
     * Registers <i>listener</i> so that it will receive AncestorEvents
     * when it or any of its ancestors move or are made visible / invisible.
     * Events are also sent when the component or its ancestors are added
     * or removed from the containment hierarchy.
     *
     * @see AncestorEvent
     */
    public void addAncestorListener(AncestorListener listener) {
        if (ancestorNotifier == null) {
            ancestorNotifier = new AncestorNotifier(this);
        }
        ancestorNotifier.addAncestorListener(listener);
    }

    /**
     * Unregisters <i>listener</i> so that it will no longer receive
     * AncestorEvents
     *
     * @see #addAncestorListener
     */
    public void removeAncestorListener(AncestorListener listener) {
        if (ancestorNotifier == null) {
            return;
        }
        ancestorNotifier.removeAncestorListener(listener);
        if (ancestorNotifier.listenerList.getListenerList().length == 0) {
            ancestorNotifier.removeAllListeners();
            ancestorNotifier = null;
        }
    }

    /**
     * Return an array of all the listeners that were added to this JComponent
     * with addXXXListener(), where XXX is the name of the <code>listenerType</code>
     * argument.  For example, to get all of the MouseListeners for the
     * given Component <code>c</code>, one would write:
     * <pre>
     * MouseListener[] mls = (MouseListener[])(c.getListeners(MouseListener.class))
     * </pre>
     * If no such listener list exists, then an empty array is returned.
     * 
     * @returns all of the listeners for this JComponent.
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	EventListener[] result = listenerList.getListeners(listenerType); 
	if (result.length == 0) { 
	    return super.getListeners(listenerType); 
	}
	return result; 
    }

    /**
     * Notifies this component that it now has a parent component.
     * When this method is invoked, the chain of parent components is
     * set up with KeyboardAction event listeners.
     *
     * @see #registerKeyboardAction
     */
    public void addNotify() {
        super.addNotify();
        firePropertyChange("ancestor", null, getParent());

	registerWithKeyboardManager(false);
    }


    /**
     * Notifies this component that it no longer has a parent component.
     * When this method is invoked, any KeyboardActions set up in the
     * the chain of parent components are removed.
     *
     * @see #registerKeyboardAction
     */
    public void removeNotify() {
        super.removeNotify();
	clearFocusOwners();
        // This isn't strictly correct.  The event shouldn't be
        // fired until *after* the parent is set to null.  But
        // we only get notified before that happens
        firePropertyChange("ancestor", getParent(), null);

	unregisterWithKeyboardManager();

	if (getCreatedDoubleBuffer()) {
	    RepaintManager.currentManager(this).resetDoubleBuffer();
	    setCreatedDoubleBuffer(false);
	}
    }


    /**
     * Adds the specified region to the dirty region list if the component
     * is showing.  The component will be repainted after all of the
     * currently pending events have been dispatched.
     *
     * @see java.awt.Component#isShowing
     * @see RepaintManager#addDirtyRegion
     */
    public void repaint(long tm, int x, int y, int width, int height) {
        RepaintManager.currentManager(this).addDirtyRegion(this, x, y, width, height);
    }


    /**
     * Adds the specified region to the dirty region list if the component
     * is showing.  The component will be repainted after all of the
     * currently pending events have been dispatched.
     *
     * @see java.awt.Component#isShowing
     * @see RepaintManager#addDirtyRegion
     */
    public void repaint(Rectangle r) {
        repaint(0,r.x,r.y,r.width,r.height);
    }


    /**
     * Supports deferred automatic layout.  
     * <p> 
     * Calls invalidate() and then adds this component's validateRoot
     * to a list of components that need to be validated.  Validation
     * will occur after all currently pending events have been dispatched.
     * In other words after this method is called,  the first validateRoot
     * (if any) found when walking up the containment hierarchy of this 
     * component will be validated.
     * By default, <code>JRootPane</code>, <code>JScrollPane</code>,
     * and <code>JTextField</code> return true 
     * from <code>isValidateRoot</code>.
     * <p>
     * This method will automatically be called on this component 
     * when a property value changes such that size, location, or 
     * internal layout of this component has been affected.  This automatic
     * updating differs from the AWT because programs generally no
     * longer need to invoke validate() to get the contents of the
     * GUI to update. 
     * <p>
     *
     * @see java.awt.Component#invalidate
     * @see java.awt.Container#validate
     * @see #isValidateRoot
     * @see RepaintManager#addInvalidComponent
     */
    public void revalidate() {
        if (getParent() == null) {
            invalidate();
        }
        else if (SwingUtilities.isEventDispatchThread()) {
            invalidate();
            RepaintManager.currentManager(this).addInvalidComponent(this);
        }
        else {
            Runnable callRevalidate = new Runnable() {
                public void run() {
                    revalidate();
                }
            };
            SwingUtilities.invokeLater(callRevalidate);
        }
    }

    /**
     * If this method returns true, revalidate() calls by descendants of
     * this component will cause the entire tree beginning with this root
     * to be validated.  Returns false by default.  JScrollPane overrides
     * this method and returns true.
     *
     * @return false
     * @see #revalidate
     * @see java.awt.Component#invalidate
     * @see java.awt.Container#validate
     */
    public boolean isValidateRoot() {
        return false;
    }


    /**
     * Returns true if this component tiles its children -- that is, if
     * it can guarantee that the children will not overlap.  The
     * repainting system is substantially more efficient in this
     * common case.  JComponent subclasses that can't make this
     * guarantee, such as JLayeredPane, should override this method
     * to return false.
     *
     * @return true if this component's children don't overlap
     */
    public boolean isOptimizedDrawingEnabled() {
        return true;
    }


    /**
     * Paints the specified region in this component and all of its
     * descendants that overlap the region, immediately.
     * <p>
     * It's rarely necessary to call this method.  In most cases it's
     * more efficient to call repaint, which defers the actual painting
     * and can collapse redundant requests into a single paint call.
     * This method is useful if one needs to update the display while
     * the current event is being dispatched.
     *
     * @see #repaint
     */
    public void paintImmediately(int x,int y,int w, int h) {
        Component c = this;
        Component parent;
        Rectangle bounds;

        if(!isShowing()) {
            return;
        }
        while(!((JComponent)c).isOpaque()) {
            parent = c.getParent();
            if(parent != null) {
                if(c instanceof JComponent) {
                    bounds = ((JComponent)c)._bounds;
                } else {
                    bounds = c.getBounds();
                }
                x += bounds.x;
                y += bounds.y;
                c = parent;
            } else {
                break;
            }

            if(!(c instanceof JComponent)) {
                break;
            }
        }
        if(c instanceof JComponent) {
            ((JComponent)c)._paintImmediately(x,y,w,h);
        } else {
            c.repaint(x,y,w,h);
        }
    }

    /**
     * Paints the specified region now.
     */
    public void paintImmediately(Rectangle r) {
        paintImmediately(r.x,r.y,r.width,r.height);
    }

    /**
     * Returns whether this component should be guaranteed to be on top.
     * For examples, it would make no sense for Menus to pop up under
     * another component, so they would always return true. Most components
     * will want to return false, hence that is the default.
     */
    // package private
    boolean alwaysOnTop() {
	return false;
    }

    private Rectangle paintImmediatelyClip = new Rectangle(0,0,0,0);

    void setPaintingChild(Component paintingChild) {
	this.paintingChild = paintingChild;
    }

    void _paintImmediately(int x, int y, int w, int h) {
        Graphics g;
        Container c;
        Rectangle b;

	int tmpX, tmpY, tmpWidth, tmpHeight;
        int offsetX=0,offsetY=0;

        boolean hasBuffer = false;

        JComponent bufferedComponent = null;
        JComponent paintingComponent = this;

        RepaintManager repaintManager = RepaintManager.currentManager(this);
	// parent Container's up to Window or Applet. First container is
	// the direct parent. Note that in testing it was faster to 
	// alloc a new Vector vs keeping a stack of them around, and gc
	// seemed to have a minimal effect on this.
	Vector path = new Vector(7);
	int pIndex = -1;
	int pCount = 0;

	tmpX = tmpY = tmpWidth = tmpHeight = 0;

        paintImmediatelyClip.x = x;
        paintImmediatelyClip.y = y;
        paintImmediatelyClip.width = w;
        paintImmediatelyClip.height = h;

	
	// System.out.println("1) ************* in _paintImmediately for " + this);
	
	boolean ontop = alwaysOnTop() && isOpaque();

	for (c = this; c != null && !(c instanceof Window) && !(c instanceof Applet); c = c.getParent()) {
	        path.addElement(c);
		if(!ontop && (c instanceof JComponent) &&
                   !(((JComponent)c).isOptimizedDrawingEnabled())) {
		    paintingComponent = (JComponent)c;
		    pIndex = pCount;
		    offsetX = offsetY = 0;
		    hasBuffer = false; /** Get rid of any buffer since we draw from here and
					*  we might draw something larger
					*/
		}
		pCount++;
		
		// look to see if the parent (and therefor this component)
		// is double buffered
		if(repaintManager.isDoubleBufferingEnabled() &&
		   (c instanceof JComponent) && ((JComponent)c).isDoubleBuffered()) {
		    hasBuffer = true;
		    bufferedComponent = (JComponent) c;
		}

		// if we aren't on top, include the parent's clip 
		if(!ontop) {
		    if(c instanceof JComponent) {
			b = ((JComponent)c)._bounds;
		    } else {
			b = c.getBounds();
		    }
		    tmpWidth = b.width;
		    tmpHeight = b.height;
		    SwingUtilities.computeIntersection(tmpX,tmpY,tmpWidth,tmpHeight,paintImmediatelyClip);
		    paintImmediatelyClip.x += b.x;
		    paintImmediatelyClip.y += b.y;
		    offsetX += b.x;
		    offsetY += b.y;
		}
	}
	
        if(c == null || c.getPeer() == null) {
            return;
        }

	// If the clip width or height is negative, don't bother painting
	if(paintImmediatelyClip.width <= 0 || paintImmediatelyClip.height <= 0) {
	    return;
	}

        paintImmediatelyClip.x -= offsetX;
        paintImmediatelyClip.y -= offsetY;
	
	// Notify the Components that are going to be painted of the
	// child component to paint to.
	if(paintingComponent != this) {
	    Component comp;
	    int i = pIndex;
	    for(; i > 0 ; i--) {
		comp = (Component) path.elementAt(i);
		if(comp instanceof JComponent) {
		    ((JComponent)comp).setPaintingChild
			               ((Component)path.elementAt(i-1));
		}
	    }
	}

	try {
	    try {
	        Graphics pcg = paintingComponent.getGraphics();
		g = SwingGraphics.createSwingGraphics(pcg);
		pcg.dispose();
	    } catch(NullPointerException e) {
		g = null;
		e.printStackTrace();
	    }

	    if(g == null) {
		System.err.println("In paintImmediately null graphics");
		return;
	    }

	    Image offscreen;
	    if(hasBuffer && (offscreen = repaintManager.getOffscreenBuffer
			     (bufferedComponent,paintImmediatelyClip.width,
			      paintImmediatelyClip.height)) != null &&
	       offscreen.getWidth(null) > 0 &&
	       offscreen.getHeight(null) > 0) {
		paintWithBuffer(paintingComponent,g,paintImmediatelyClip,
				offscreen);
		g.dispose();
	    } else {
		//System.out.println("has no buffer");
		g.setClip(paintImmediatelyClip.x,paintImmediatelyClip.y,
		       paintImmediatelyClip.width,paintImmediatelyClip.height);
		try {
		    paintingComponent.paint(g);
		} finally {
		    g.dispose();
		}
	    }
	}
	finally {
	    // Reset the painting child for the parent components.
	    if(paintingComponent != this) {
		Component comp;
		int i = pIndex;
		for(; i > 0 ; i--) {
		    comp = (Component) path.elementAt(i);
		    if(comp instanceof JComponent) {
			((JComponent)comp).setPaintingChild(null);
		    }
		}
	    }
	    path.removeAllElements();
	}
    }

    private void paintWithBuffer(JComponent paintingComponent,Graphics g,Rectangle clip,Image offscreen) {
        Graphics osg = offscreen.getGraphics();
        Graphics og = SwingGraphics.createSwingGraphics(osg);
	osg.dispose();
        int bw = offscreen.getWidth(null);
        int bh = offscreen.getHeight(null);
        int x,y,maxx,maxy;

        if(bw > clip.width) {
            bw = clip.width;
        }
        if(bh > clip.height) {
            bh = clip.height;
        }

        try {
            paintingComponent.setFlag(ANCESTOR_USING_BUFFER,true);
            paintingComponent.setFlag(IS_PAINTING_TILE,true);
            for(x = clip.x, maxx = clip.x+clip.width;
                x < maxx ;  x += bw ) {
                for(y=clip.y, maxy = clip.y + clip.height;
                    y < maxy ; y += bh) {
                    if((y+bh) >= maxy && (x+bw) >= maxx) {
                        paintingComponent.setFlag(IS_PAINTING_TILE,false);
                    }
                    og.translate(-x,-y);
                    og.setClip(x,y,bw,bh);
                    paintingComponent.paint(og);
                    g.setClip(x,y,bw,bh);
                    g.drawImage(offscreen,x,y,paintingComponent);
                    og.translate(x,y);
                }
            }
        } finally {
            paintingComponent.setFlag(ANCESTOR_USING_BUFFER,false);
            paintingComponent.setFlag(IS_PAINTING_TILE,false);
            og.dispose();
        }
    }

    /**
     *  Returns true if the component at index compIndex is obscured by
     *  an opaque sibling that is painted after it.
     *  The rectangle is in the receiving component coordinate system.
     */
    // NOTE: This will tweak tmpRect!
    boolean rectangleIsObscuredBySibling(int compIndex, int x, int y,
					 int width, int height) {
	int i;
	Component sibling;
	Rectangle siblingRect;

	for(i = compIndex - 1 ; i >= 0 ; i--) {
	    sibling = getComponent(i);
	    if(!sibling.isVisible())
		continue;
	    if(sibling instanceof JComponent) {
		if(!((JComponent)sibling).isOpaque())
		    continue;
		siblingRect = ((JComponent)sibling).getBounds(tmpRect);
	    }
	    else {
		siblingRect = sibling.getBounds();
	    }
	    // NOTE(sky): I could actually intersect x,y,width,height here.
	    // This tests for COMPLETE obscuring by another component,
	    // if multiple siblings obscure the region true should be
	    // returned.
	    if (x >= siblingRect.x && (x + width) <=
		(siblingRect.x + siblingRect.width) &&
		y >= siblingRect.y && (y + height) <=
		(siblingRect.y + siblingRect.height)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns true, which implies that before checking if a child should
     * be painted it is first check that the child is not obscured by another 
     * sibling. This is only checked if <code>isOptimizedDrawingEnabled</code>
     * returns false.
     */
    boolean checkIfChildObscuredBySibling() {
	return true;
    }


    private void setFlag(int aFlag, boolean aValue) {
        if(aValue) {
            flags |= (1 << aFlag);
        } else {
            flags &= ~(1 << aFlag);
        }
    }

    private boolean getFlag(int aFlag) {
        int mask = (1 << aFlag);
        return ((flags & mask) == mask);
    }


    /** Buffering **/

    /** Sets whether the receiving component should use a buffer to paint.
     *  If set to true, all the drawing from this component will be done
     *  in an offscreen painting buffer. The offscreen painting buffer will
     *  the be copied onto the screen.
     *  Swing's painting system always use a maximum of one double buffer.
     *  If a Component is buffered and one of its ancestor is also buffered,
     *  the ancestor buffer will be used.
     */
    public void setDoubleBuffered(boolean aFlag) {
        setFlag(IS_DOUBLE_BUFFERED,aFlag);
    }

    /** Returns whether the receiving component should use a buffer to paint. **/
    public boolean isDoubleBuffered() {
        return getFlag(IS_DOUBLE_BUFFERED);
    }

    /**
     * Returns the JRootPane ancestor for a component
     *
     * @return the JRootPane that contains this component,
     *         or null if no JRootPane is found
     */
    public JRootPane getRootPane() {
        return SwingUtilities.getRootPane(this);
    }


    /** Serialization **/

    /**
     * This class is used to give us an oppportunity to uninstall the UI
     * before java.awt.Container.writeObject() runs.  The enableSerialization()
     * method belows adds an instance of this class to the FocusListener
     * field in java.awt.Component.  The java.awt.Component.writeObject()
     * method, which always runs before java.awt.Container.writeObject(),
     * effectively calls our writeObject method, which takes care of uninstalling
     * the UI.
     * <p>
     * A FocusListener is used (any listener would do) because all Swing 
     * components listen for Focus events anyway.
     * <p>
     * Yes this is a hack.  Unfortunately we don't know of a better way to make 
     * the UI property transient and work correctly on JDK1.1 VMs.
     */
    private class EnableSerializationFocusListener implements FocusListener, Serializable 
    {
	public void focusGained(FocusEvent e) {}
	public void focusLost(FocusEvent e) {}
	private void writeObject(ObjectOutputStream s) throws IOException {
	    s.defaultWriteObject();
            JComponent.this.compWriteObjectNotify();

	}
    }

    /* Called from the EnableSerializationFocusListener to do any Swing-specific
     * pre-serialization configuration.
     */
    void compWriteObjectNotify() {
        if (ui != null) {
	    ui.uninstallUI(this);
	}
        /* JTableHeader is in a separate package, which prevents it from
         * being able to override this package-private method the way the
         * other components can.  We don't want to make this method protected
         * because it would introduce public-api for a less-than-desirable
         * serialization scheme, so we compromise with this 'instanceof' hack
         * for now.
         */
        if (getToolTipText() != null || 
            this instanceof javax.swing.table.JTableHeader) {
            ToolTipManager.sharedInstance().unregisterComponent(JComponent.this);
        }            
    }

    /**
     * Called by the JComponent constructor.  Adds a fake FocusListener
     * whose real purpose is to uninstall the component's UI early.
     */
    void enableSerialization() {
	addFocusListener(new EnableSerializationFocusListener());
    }

    /**
     * This object is the ObjectInputStream callback that's called after
     * a complete graph of objects (including at least one JComponent)
     * has been read.  It sets the UI property of each Swing component 
     * that was read to the current default with updateUI().  
     * <p> 
     * As each  component is read in we keep track of the current set of 
     * root components here, in the roots vector.  Note that there's only one 
     * ReadObjectCallback per ObjectInputStream, they're stored in
     * the static readObjectCallbacks hashtable.
     * 
     * @see java.io.ObjectInputStream#registerValidation
     * @see SwingUtilities#updateComponentTreeUI
     */
    private class ReadObjectCallback implements ObjectInputValidation 
    {
	private final Vector roots = new Vector(1);
	private final ObjectInputStream inputStream;

	ReadObjectCallback(ObjectInputStream s) throws Exception {
	    inputStream = s;
	    s.registerValidation(this, 0);
	}

	/**
	 * This is the method that's called after the entire graph
	 * of objects has been read in.  It initializes
	 * the UI property of all of the copmonents with
	 * SwingUtilities.updateComponentTreeUI().
	 */
	public void validateObject() throws InvalidObjectException {
	    try {
		for(int i = 0; i < roots.size(); i++) {
		    JComponent root = (JComponent)(roots.elementAt(i));
		    SwingUtilities.updateComponentTreeUI(root);
		}
	    }
	    finally {
		readObjectCallbacks.remove(inputStream);
	    }
	}

	/**
	 * If c isn't a descendant of a component we've already
	 * seen, then add it to the roots Vector.
	 */
	private void registerComponent(JComponent c)
        {
    	    /* If the Component c is a descendant of one of the
	     * existing roots (or it IS an existing root), we're done.
	     */
	    for(int i = 0; i < roots.size(); i++) {
		JComponent root = (JComponent)roots.elementAt(i);
		for(Component p = c; p != null; p = p.getParent()) {
		    if (p == root) {
			return;
		    }
		}
	    }
	    
	    /* Otherwise: if Component c is an ancestor of any of the 
	     * existing roots then remove them and add c (the "new root") 
	     * to the roots vector.
	     */
	    for(int i = 0; i < roots.size(); i++) {
		JComponent root = (JComponent)roots.elementAt(i);
		for(Component p = root.getParent(); p != null; p = p.getParent()) {
		    if (p == c) {
			roots.removeElementAt(i--); // !!
			break;
		    }
		}
	    }
	    
	    roots.addElement(c);
	}
    }


    /**
     * We use the ObjectInputStream "registerValidation" callback to 
     * update the UI for the entire tree of components after they've
     * all been read in.
     * 
     * @see ReadObjectCallback
     */
    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException 
    {
        s.defaultReadObject();

	/* If there's no ReadObjectCallback for this stream yet, that is, if
	 * this is the first call to JComponent.readObject() for this
	 * graph of objects, then create a callback and stash it
	 * in the readObjectCallbacks table.  Note that the ReadObjectCallback
	 * constructor takes care of calling s.registerValidation().
	 */
	ReadObjectCallback cb = (ReadObjectCallback)(readObjectCallbacks.get(s));
	if (cb == null) {
	    try {
		readObjectCallbacks.put(s, cb = new ReadObjectCallback(s));
	    }
	    catch (Exception e) {
		throw new IOException(e.toString());
	    }
	}
	cb.registerComponent(this);

        if (getToolTipText() != null) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
    }


    /**
     * Before writing a JComponent to an ObjectOutputStream we temporarily 
     * uninstall its UI.  This is tricky to do because we want to uninstall
     * the UI before any of the JComponent's children (or its LayoutManager etc.)
     * are written, and we don't want to restore the UI until the most derived
     * JComponent subclass has been been stored.  
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JComponent. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JComponent
     */
    protected String paramString() {
        String preferredSizeString = (preferredSize != null ?
				      preferredSize.toString() : "");
        String minimumSizeString = (minimumSize != null ?
				    minimumSize.toString() : "");
        String maximumSizeString = (maximumSize != null ?
				    maximumSize.toString() : "");
        String borderString = (border != null ?
			       border.toString() : "");

        return super.paramString() +
        ",alignmentX=" + alignmentX +
        ",alignmentY=" + alignmentY +
        ",border=" + borderString +
	",flags=" + flags +             // should beef this up a bit
        ",maximumSize=" + maximumSizeString +
        ",minimumSize=" + minimumSizeString +
        ",preferredSize=" + preferredSizeString;
    }

}



