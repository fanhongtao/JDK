/*
 * @(#)JComponent.java	2.96 99/06/11
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing;


import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

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
 * The base class for the Swing components. JComponent provides:
 * <ul>
 * <li>A "pluggable look and feel" (l&f) that can be specified by the
 *     programmer or (optionally) selected by the user at runtime.
 * <li>Components that are designed to be combined and extended in order
 *     to create custom components.
 * <li>Comprehensive keystroke-handling that works with nested components.
 * <li>Action objects, for single-point control of program actions initiated
 *     by multiple components.
 * <li>A border property that implicitly defines the component's insets.
 * <li>The ability to set the preferred, minimim, and maximum size for a
 *     component.
 * <li>ToolTips -- short descriptions that pop up when the cursor lingers
 *     over a component.
 * <li>Autoscrolling -- automatic scrolling in a list, table, or tree that
 *     occurs when the user is dragging the mouse.
 * <li>Simple, easy dialog construction using static methods in the JOptionPane
 *     class that let you display information and query the user.
 * <li>Slow-motion graphics rendering using debugGraphics so you can see
 *     what is being displayed on screen and whether or not it is being
 *     overwritten.
 * <li>Support for Accessibility.
 * <li>Support for international Localization.
 * </ul>
 * For more information on these subjects, see the
 * <a href="package-summary.html#package_description">Swing package description</a>
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
 * @version 2.96 06/11/99
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
     * @se #readObject
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

    /* A "scratch pad" rectangle used by the painting code.
     */
    private transient Rectangle tmpRect;

    /**
     * Constant used for registerKeyboardAction() which
     * means that the command should be invoked when
     * the component has the focus.
     */
    public static final int WHEN_FOCUSED = 0;

    /**
     * Constant used for registerKeyboardAction() which
     * means that the comand should be invoked when the receiving
     * component is an ancestor of the focused component or is
     * itself the focused component.
     */
    public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;

    /**
     * Constant used for registerKeyboardAction() which
     * means that the command should be invoked when
     * the receiving component is in the window that has the focus
     * or is itself the focused component.
     */
    public static final int WHEN_IN_FOCUSED_WINDOW = 2;

    /**
     * Constant used by some of the apis to mean that no condition is defined.
     */
    public static final int UNDEFINED_CONDITION = -1;

    /**
     * The key used by JComponent to access keyboard bindings.
     */
    private static final String KEYBOARD_BINDINGS_KEY = "_KeyboardBindings";

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
    private static final int IS_PRINTING            = 12;
    private static final int IS_PRINTING_ALL        = 13;

    /**
     * Default JComponent constructor.  This constructor does
     * no initialization beyond calling the Container constructor,
     * e.g. the initial layout manager is null.
     */
    public JComponent() {
        super();
        enableEvents(AWTEvent.FOCUS_EVENT_MASK);
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
     * Set the look and feel delegate for this component.
     * JComponent subclasses generally override this method
     * to narrow the argument type, e.g. in JSlider:
     * <pre>
     * public void setUI(SliderUI newUI) {
     *     super.setUI(newUI);
     * }
     *  </pre>
     * <p>
     * Additionaly JComponent subclasses must provide a getUI
     * method that returns the correct type, e.g.
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
     *  description: The component's look and feel delegate
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
     * Return the UIDefaults key used to look up the name of the
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
     * object if neccessary otherwise we just configure the
     * specified graphics objects foreground and font.
     *
     * @return A Graphics object configured for this component
     */
    protected Graphics getComponentGraphics(Graphics g) {
        Graphics componentGraphics = g;
        if (ui != null) {
            if ((DebugGraphics.debugComponentCount() != 0) &&
                    (shouldDebugGraphics() != 0) &&
                    !(g instanceof DebugGraphics)) {
                if(g instanceof SwingGraphics) {
                    if(!(((SwingGraphics)g).subGraphics() instanceof DebugGraphics)) {
                        componentGraphics = new DebugGraphics(((SwingGraphics)g).subGraphics(),this);
                        componentGraphics = SwingGraphics.createSwingGraphics(componentGraphics);
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
     * If the UI delegate is non-null, call its paint
     * method.  We pass the delegate a copy of the Graphics
     * object to protect the rest of the paint code from
     * irrevocable changes (e.g. Graphics.translate()).
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
     * Paint this component's children.
     * If shouldUseBuffer is true, no component ancestor has a buffer and
     * the component children can use a buffer if they have one.
     * Otherwise, one ancestor has a buffer currently in use and children
     * should not use a buffer to paint.
     * @see #paint
     * @see java.awt.Container#paint
     */
    protected void paintChildren(Graphics g) {
        boolean isJComponent;
        Graphics sg = SwingGraphics.createSwingGraphics(g);

        try {
            synchronized(getTreeLock()) {
		boolean printing = getFlag(IS_PRINTING);
                for (int i = getComponentCount() - 1 ; i >= 0 ; i--) {
                    Component comp = getComponent(i);
                    if (comp != null && isLightweightComponent(comp) && 
                        (comp.isVisible() == true)) {
                        Rectangle cr;
                        isJComponent = (comp instanceof JComponent);

                        if(isJComponent) {
                            if(tmpRect == null) {
                                tmpRect = new Rectangle();
                            }
                            cr = tmpRect;
                            ((JComponent)comp).getBounds(cr);
                        } else {
                            cr = comp.getBounds();
                        }

		      
			boolean hitClip = 
			    g.hitClip(cr.x, cr.y, cr.width, cr.height);
		        






                        if (hitClip) {
                            Graphics cg = SwingGraphics.createSwingGraphics(
                                sg, cr.x, cr.y, cr.width, cr.height);
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
            sg.dispose();
        }
    }

    /**
     * Paint the component's border.
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
     * ComponentUI.update() which is called by paintComponent.
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
     * wants to specialize the UI (look and feel) delegates paint
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
            int clipX = clipRect.x;
            int clipY = clipRect.y;
            int clipW = clipRect.width;
            int clipH = clipRect.height;
	    













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

	    boolean printing = getFlag(IS_PRINTING);
            if(!printing && repaintManager.isDoubleBufferingEnabled() &&
               !getFlag(ANCESTOR_USING_BUFFER) && isDoubleBuffered()) {
                int bw,bh;
                int x,y,maxx,maxy;
                offscr = repaintManager.getOffscreenBuffer(this,clipW,clipH);

                Graphics sg = 
                    SwingGraphics.createSwingGraphics(offscr.getGraphics());
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
                if (!rectangleIsObscured(clipX,clipY,clipW,clipH)) {
                    paintComponent(co);
                    paintBorder(co);
                }
                paintChildren(co);
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
    
    public void printAll(Graphics g) {
	setFlag(IS_PRINTING_ALL, true);
	try {
	    print(g);
	}
	finally {
	    setFlag(IS_PRINTING_ALL, false);
	}
    }

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
     * including TAB and SHIFT+TAB. CONTROL + TAB and CONTROL + SHIFT + TAB
     * will move the focus to the next / previous component.
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
     * Return the next focusable component or null if the focus manager
     * should choose the next focusable component automatically
     */
    public Component getNextFocusableComponent() {
        return (Component) getClientProperty(NEXT_FOCUS);
    }

    /**
     *  Set whether the receiving component can obtain the focus by
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

    /** Return whether the receiving component can obtain the focus by
     *  calling requestFocus
     *  @see #setRequestFocusEnabled
     */
    public boolean isRequestFocusEnabled() {
        return (getFlag(REQUEST_FOCUS_DISABLED) ? false : true);
    }

    /** Set focus on the receiving component if isRequestFocusEnabled returns true **/
    public void requestFocus() {

      /* someone other then the focus manager is requesting focus,
	 so we clear the focus manager's idea of focus history */
        FocusManager focusManager = FocusManager.getCurrentManager();
        if (focusManager instanceof DefaultFocusManager) 
	  ((DefaultFocusManager)focusManager).clearHistory();

        if(isRequestFocusEnabled()) {
            super.requestFocus();
        }
    }

    /** Set the focus on the receiving component. This method is for focus managers, you
     *  rarely want to call this method, use requestFocus() enstead.
     */
    public void grabFocus() {
        super.requestFocus();
    }

    /**
     * Set the preferred size of the receiving component.
     * if <code>preferredSize</code> is null, the UI will
     * be asked for the preferred size
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
     * just return it.  If the UI delegates getPreferredSize()
     * method returns a non null then value return that, otherwise
     * defer to the components layout manager.
     *
     * @return the value of the preferredSize property.
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
     * Sets the maximumSize of this component to a constant
     * value.  Subsequent calls to getMaximumSize will always
     * return this value, the components UI will not be asked
     * to compute it.  Setting the maximumSize to null
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
     * If the maximumSize has been set to a non-null value
     * just return it.  If the UI delegates getMaximumSize()
     * method returns a non null value then return that, otherwise
     * defer to the components layout manager.
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
     * Sets the minimumSize of this component to a constant
     * value.  Subsequent calls to getMinimumSize will always
     * return this value, the components UI will not be asked
     * to compute it.  Setting the minimumSize to null
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
     * If the minimumSize has been set to a non-null value
     * just return it.  If the UI delegates getMinimumSize()
     * method returns a non null value then return that, otherwise
     * defer to the components layout manager.
     *
     * @return the value of the minimumSize property.
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
     * Give the UI delegate an opportunity to define the precise
     * shape of this component for the sake of mouse processing.
     *
     * @return true if this component logically contains x,y.
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
     * (e.g. margins and padding) regions for a swing component.
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
     * border's insets, else calls super.getInsets.
     *
     * @return the value of the insets property.
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
     * @param insets the Insets object which can be reused.
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
     * @return the value of the alignmentY property.
     * @see #setAlignmentY
     * @see java.awt.Component#getAlignmentY
     */
    public float getAlignmentY() {
        return (alignmentY != null) ? alignmentY.floatValue() : super.getAlignmentY();
    }

    /**
     * Set the the horizontal alignment.
     *
     * @see #getAlignmentY
     * @beaninfo
     *   description: The preferred vertical alignment of the component
     */
    public void setAlignmentY(float alignmentY) {
        this.alignmentY = new Float(alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f : alignmentY);
    }


    /**
     * Overrides <code>Container.getAlignmentX</code> to return
     * the vertical alignment.
     *
     * @return the value of the alignmentX property.
     * @see #setAlignmentX
     * @see java.awt.Component#getAlignmentX
     */
    public float getAlignmentX() {
        return (alignmentX != null) ? alignmentX.floatValue() : super.getAlignmentX();
    }

    /**
     * Set the the vertical alignment.
     *
     * @see #getAlignmentX
     * @beaninfo
     *   description: The preferred horizontal alignment of the component
     */
    public void setAlignmentX(float alignmentX) {
        this.alignmentX = new Float(alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f : alignmentX);
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
     * or one if its parents.
     */
    int shouldDebugGraphics() {
        return DebugGraphics.shouldComponentDebug(this);
    }

    /**
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
      Hashtable bindings;
      boolean firstKeyboardAction = false;

      synchronized(this) {
        bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
        if(bindings == null) {
          bindings = new Hashtable();
          putClientProperty(KEYBOARD_BINDINGS_KEY,bindings);
          firstKeyboardAction = true;
        }
      }

      synchronized(bindings) {
        bindings.put(aKeyStroke,new KeyboardBinding(anAction,aCommand,aKeyStroke,aCondition));
      }

      /* This is the first time a keyboard binding is added, let's order
       * keyboard events...
       * ALERT: we need to enable events. Adding a listener will not work since
       *        we want our listener to be after all other listeners.
       */
      if(firstKeyboardAction) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
      }

        if (getParent() != null && aCondition == WHEN_IN_FOCUSED_WINDOW) {
	  registerWithKeyboardManager(aKeyStroke);
	}
   
    }

    void registerWithKeyboardManager(KeyStroke aKeyStroke) {

	    KeyboardManager.getCurrentManager().registerKeyStroke(aKeyStroke, this);

    }

    void unregisterWithKeyboardManager(KeyStroke aKeyStroke) {
	  KeyboardManager.getCurrentManager().unregisterKeyStroke(aKeyStroke, this);
    }

    /**
     *  Calls registerKeyboardAction(ActionListener,String,KeyStroke,condition) with a null command.
     */
    public void registerKeyboardAction(ActionListener anAction,KeyStroke aKeyStroke,int aCondition) {
        registerKeyboardAction(anAction,null,aKeyStroke,aCondition);
    }

    private Hashtable keyboardBindings() {
        Hashtable bindings;
        synchronized(this) {
            bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
        }
        return bindings;
    }

    /**
     * Unregister a keyboard action.
     *
     * @see #registerKeyboardAction
     */
    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
        Hashtable bindings = keyboardBindings();
	KeyboardBinding aBinding;

        if(bindings == null)
            return;
        synchronized(bindings) {
            aBinding = (KeyboardBinding)bindings.remove(aKeyStroke);
        }

        if(bindings.size() == 0) {
            /** ALERT. We need a way to disable keyboard events only if there is no
             *        keyboard listener.
             */
        }

	if ( aBinding != null && aBinding.condition ==  WHEN_IN_FOCUSED_WINDOW) {
	    unregisterWithKeyboardManager(aKeyStroke);
	}
    }

    /**
     * Return the KeyStrokes that will initiate registered actions.
     *
     * @return an array of KeyStroke objects
     * @see #registerKeyboardAction
     */
    public KeyStroke[] getRegisteredKeyStrokes() {
        Hashtable bindings = keyboardBindings();
        KeyStroke result[];
        int i;
        Enumeration keys;

        if(bindings == null)
            return new KeyStroke[0];
        synchronized(bindings) {
            result = new KeyStroke[bindings.size()];
            i = 0;
            keys = bindings.keys();
            while(keys.hasMoreElements())
                result[i++] = (KeyStroke) keys.nextElement();
        }
        return result;
    }

    /**
     * Return the condition that determines whether a registered action
     * occurs in response to the specified keystroke.
     *
     * @return the action-keystroke condition
     * @see #registerKeyboardAction
     */
    public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
        Hashtable bindings = keyboardBindings();
        if(bindings == null)
            return UNDEFINED_CONDITION;
        synchronized(bindings) {
            KeyboardBinding kb = (KeyboardBinding) bindings.get(aKeyStroke);
            if(kb != null) {
                return kb.getCondition();
            }
        }
        return UNDEFINED_CONDITION;
    }

    /**
     * Return the object that will perform the action registered for a
     * given keystroke.
     *
     * @return the ActionListener object invoked when the keystroke occurs
     * @see #registerKeyboardAction
     */
    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
        Hashtable bindings = keyboardBindings();

        if(bindings == null)
            return null;
        synchronized(bindings) {
            KeyboardBinding kb = (KeyboardBinding) bindings.get(aKeyStroke);
            if(kb != null) {
                return kb.getAction();
            }
        }
        return null;
    }



    /**
     * Unregister all keyboard actions
     *
     * @see #registerKeyboardAction
     */
    public void resetKeyboardActions() {
      synchronized(this) {
          Hashtable bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
          if(bindings != null) {
              bindings.clear();
          }
      }
      /* ALERT. We need a way to disable keyboard events only if there is no
       *        keyboard listener.
       */
    }

    /**
     * Request the focus for the component that should have the focus
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

    /**
     * Makes the component visible or invisible.
     * Overrides <code>Component.setVisible</code>.
     * 
     * @param aFlag  true to make the component visible
     */
    public void setVisible(boolean aFlag) {
        if(aFlag != isVisible()) {
            super.setVisible(aFlag);
            Container parent = getParent();
            if(parent != null) {
                Rectangle r = getBounds();
                parent.repaint(r.x,r.y,r.width,r.height);
            }

            if (accessibleContext != null) {
	        if (aFlag) {
		    accessibleContext.firePropertyChange(
			    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
			    null, AccessibleState.VISIBLE);
	        } else {
		    accessibleContext.firePropertyChange(
			    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
			    AccessibleState.VISIBLE, null);
	        }
            }
        }
    }

    /**
     * Sets whether or not this component is enabled.
     * A component which is enabled may respond to user input,
     * while a component which is not enabled cannot respond to 
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
     *  description: The enabled state of the component
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
        // foreground already bound in AWT1.2
        if (!SwingUtilities.is1dot2) {
            firePropertyChange("foreground", oldFg, fg);
        }
        if (fg != oldFg) {
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
        // background already bound in AWT1.2
        if (!SwingUtilities.is1dot2) {
            firePropertyChange("background", oldBg, bg);
        }
        if (bg != oldBg) {
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
        if (!SwingUtilities.is1dot2) {
            firePropertyChange("font", oldFont, font);
        }
        if (font != oldFont) {
            revalidate();
        }
    }
        

    /**
     * Identifies whether or not this component can receive the focus.
     * A disabled button, for example, would return false.
     *
     * @return true if this component can receive the focus
     */
    public boolean isFocusTraversable() {
      boolean result = false;
      Hashtable bindings;

      synchronized(this) {
        bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
      }
      if(bindings != null) {
        synchronized(bindings) {
            Enumeration keys = bindings.keys();
            KeyboardBinding b;

            while(keys.hasMoreElements()) {
                b = (KeyboardBinding) bindings.get(keys.nextElement());
                if(b.getCondition() == WHEN_FOCUSED) {
                    result = true;
                    break;
                }
            }
        }
      }
      return result;
    }

    protected void processFocusEvent(FocusEvent e) {
        switch(e.getID()) {
          case FocusEvent.FOCUS_GAINED:
              setFlag(HAS_FOCUS, true);
              break;
          case FocusEvent.FOCUS_LOST:
              setFlag(HAS_FOCUS, false);
              break;
        }

        // Call super *after* setting flag, in case listener calls paint.
        super.processFocusEvent(e);
    }

    /**
     * Process any key events that the component itself
     * recognizes.  This will be called after the focus
     * manager and any interested listeners have been
     * given a chance to steal away the event.  This
     * method will only be called is the event has not
     * yet been consumed.  This method is called prior
     * to the keyboard UI logic.
     * <p>
     * This is implemented to do nothing.  Subclasses would
     * normally override this method if they process some
     * key events themselves.  If the event is processed,
     * it should be consumed.
     */
    protected void processComponentKeyEvent(KeyEvent e) {
    }

    /** Override processKeyEvent to process events **/
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

    KeyboardBinding bindingForKeyStroke(KeyStroke ks,int condition) {
        Hashtable bindings;
        KeyboardBinding b;
        KeyboardBinding result = null;

        // synchronized(this) {
            bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
        // }
        if(bindings != null) {
            // synchronized(bindings) {
                b = (KeyboardBinding) bindings.get(ks);
                // System.out.println("Bindings are " + bindings);
                if(b != null) {
                    ActionListener action = b.getAction();
                    if((action instanceof Action) && !(((Action)action).isEnabled()))
                        action = null;
                    if(action != null) {
                        switch(b.getCondition()) {
                        case WHEN_FOCUSED:
                            if(condition == WHEN_FOCUSED)
                                result = b;
                            break;
                        case WHEN_ANCESTOR_OF_FOCUSED_COMPONENT:
                            if(condition == WHEN_FOCUSED ||
                               condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                                result = b;
                            break;
                        case WHEN_IN_FOCUSED_WINDOW:
                            if(condition == WHEN_FOCUSED ||
                               condition == WHEN_IN_FOCUSED_WINDOW ||
                               condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                                result = b;
                            break;
                        }
                    }
                }
            // }
        }
        return result;
    }

    boolean processKeyBinding(KeyEvent e,int condition,boolean pressed) {
        Hashtable bindings;
        int i,c;
        boolean onKeyRelease = (pressed?false:true);
        KeyboardBinding binding = null;
        KeyStroke ks;

        if(isEnabled()) {
            if(e.getID() == KeyEvent.KEY_TYPED) {
                binding = bindingForKeyStroke((ks=KeyStroke.getKeyStroke(e.getKeyChar())),condition);
            } else {
                binding = bindingForKeyStroke((ks=KeyStroke.getKeyStroke(e.getKeyCode(),e.getModifiers(),
                                                                     onKeyRelease)), condition);
            }
            //System.out.println("e=" + e + "ks is " + ks);
            if(binding != null) {
                ActionListener listener = binding.getAction();
                if(listener != null) {
                    listener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,binding.getCommand()));
                    return true;
                }
            }
        }

        return false;
    }

    boolean processKeyBindings(KeyEvent e,boolean pressed) {
      Container parent;

      /* Do we have a key binding for e? */
      if(processKeyBinding(e,WHEN_FOCUSED,pressed))
        return true;

      /* We have no key binding. Let's try the path from our parent to the window excluded
       * We store the path components so we can avoid asking the same component twice.
       */
      parent = this.getParent();
      while(parent != null && !(parent instanceof Window) && !(parent instanceof Applet) && !(parent instanceof JInternalFrame)) {
	// maybe generalize from JInternalFrame (like isFocusCycleRoot).
	  if(parent instanceof JComponent) {
	      if(((JComponent)parent).processKeyBinding(e,WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,pressed))
		return true;
	  }
	  parent = parent.getParent();
      }

      /* No components between the focused component and the window is actually interested
       * by the key event. Let's try the other JComponent in this window.
       */
      if(parent != null) {
        return JComponent.processKeyBindingsForAllComponents(e,parent,pressed);
      }
      return false;
    }

    static boolean processKeyBindingsForAllComponents(KeyEvent e,Container container, boolean pressed) {

      return KeyboardManager.getCurrentManager().fireKeyboardAction(e, pressed, container);

    /*  int i;
      Component subComponents[];

      if(!container.isVisible() || !container.isEnabled()) {
	  return false;
      }

      if(container instanceof JComponent && !alreadyProcessed.contains(container)) {
          if(((JComponent)container).processKeyBinding(e,WHEN_IN_FOCUSED_WINDOW,pressed))
          return true;
      }

      subComponents = container.getComponents();
      for(i=0 ; i < subComponents.length ; i++) {
	  if(subComponents[i].isVisible() && subComponents[i].isEnabled()) {
	      if(subComponents[i] instanceof Container) {
		  if(processKeyBindingsForAllComponents(e,(Container)subComponents[i],alreadyProcessed,pressed))
		    return true;
	      }
	  }
      }
      return false;*/
    }

    /**
     * Registers the text to display in a tool tip.
     * The text displays when the cursor lingers over the component.
     * <p>
     * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/tooltip.html">How to Use Tool Tips</a>
     * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
     * for further documentation.
     *
     * @param text  The string to display. If the text is null,
     *              the tool tip is turned off for this component.
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
     * Return the tooltip string that has been set with setToolTipText()
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
     * more extensize API to support differing tooltips at different locations,
     * this method should be overridden.
     */
    public String getToolTipText(MouseEvent event) {
        return getToolTipText();
    }

    /**
     * Return the tooltip location in the receiving component coordinate system
     * If null is returned, Swing will choose a location.
     * The default implementation returns null.
     *
     * @param event  the MouseEvent that caused the ToolTipManager to
     *               show the tooltip.
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
     * parent. Components that can service the request, such as a JViewport,
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
     * JViewport
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
                autoscroller.stop();
                autoscroller = null;
            }
        }
    }

    /**
     * Returns <i>true</i> if this component automatically scrolls its
     * contents when dragged, (when contained in a component that supports
     * scrolling, like JViewport
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

    static class KeyboardBinding implements Serializable {
        ActionListener   action;
        String           command;
        KeyStroke keyStroke;
        int        condition;

        KeyboardBinding(ActionListener action,String aCommand,KeyStroke aKeyStroke,int condition) {
            this.action     = action;
            this.command    = aCommand;
            this.keyStroke  = aKeyStroke;
            this.condition  = condition;
        }

        ActionListener getAction() {
            return action;
        }

        String getCommand() {
            return command;
        }

        KeyStroke getKeyStroke() {
            return keyStroke;
        }

        int getCondition() {
            return condition;
        }

        public String toString() {
            return "KeyBinding ("+action+","+keyStroke+","+condition+")";
        }
    }

    // This class is used by the KeyboardState class to provide a single
    // instance which can be stored in the AppContext.
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


    /**
     * --- Accessibility Support ---
     *
     *  JComponent will contain all of the methods in interface Accessible,
     *  though it won't actally implement the interface - that will be up
     *  to the individual objects which extend JComponent.
     */

    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JComponent
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
     * subclassed by component developers.  Due to a restriction that
     * protected inner classes cannot be subclassed outside of a
     * package, this inner class has been made public.  When this
     * restriction is lifted for JDK1.1.7, this class will be made
     * protected.
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public abstract class AccessibleJComponent extends AccessibleContext
        implements Serializable, AccessibleComponent {

	/**
	 * Though the class is abstract, this should be called by
	 * all sub-classes. 
	 */
	protected AccessibleJComponent() {
        }

	protected ContainerListener accessibleContainerHandler = null;

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
	 * Add a PropertyChangeListener to the listener list.
	 *
	 * @param listener  The PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	    if (accessibleContainerHandler == null) {
		accessibleContainerHandler = new AccessibleContainerHandler();
		JComponent.this.addContainerListener(accessibleContainerHandler);
	    }
	    super.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener  The PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	    if (accessibleContainerHandler == null) {
		JComponent.this.removeContainerListener(accessibleContainerHandler);
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
         * Get the accessible name of this object.  This should almost never
         * return java.awt.Component.getName(), as that generally isn't
         * a localized name, and doesn't have meaning for the user.  If the
         * object is fundamentally a text object (e.g. a menu item), the
         * accessible name should be the text of the object (e.g. "save").
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
         * Get the accessible description of this object.  This should be
         * a concise, localized description of what this object is - what
         * is it's meaning to the user.  If the object has a tooltip, the
         * tooltip text may be an appropriate string to return, assuming
         * it contains a concise description of the object (instead of just
         * the name of the object - e.g. a "Save" icon on a toolbar that
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
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SWING_COMPONENT;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            return SwingUtilities.getAccessibleStateSet(JComponent.this);
        }

        /**
         * Get the Accessible parent of this object.  If the parent of this
         * object implements Accessible, this method should simply return
         * getParent().
         *
         * @return the Accessible parent of this object -- can be null if this
         * object does not have an Accessible parent
         */
        public Accessible getAccessibleParent() {
            if (accessibleParent != null) {
                return accessibleParent;
            } else {
                Container parent = getParent();
                if (parent instanceof Accessible) {
                    return (Accessible) parent;
                }
            }
            return null;
        }

        /**
         * Get the index of this object in its accessible parent.
         *
         * @return the index of this object in its parent; -1 if this
         * object does not have an accessible parent.
         * @see #getAccessibleParent
         */
        public int getAccessibleIndexInParent() {
            return SwingUtilities.getAccessibleIndexInParent(JComponent.this);
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
	    // Always delegate to the UI if it exists
	    if (ui != null) {
                return ui.getAccessibleChildrenCount(JComponent.this);
	    } else {
	        return SwingUtilities.getAccessibleChildrenCount(JComponent.this);
	    }
        }

        /**
         * Return the nth Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
	    // Always delegate to the UI if it exists
	    if (ui != null) {
                return ui.getAccessibleChild(JComponent.this, i);
	    } else {
	        return SwingUtilities.getAccessibleChild(JComponent.this, i);
            }
        }

        /**
         * Return the locale of this object.
         *
         * @return the locale of this object
         */
        public Locale getLocale() {
            return JComponent.this.getLocale();
        }

        /**
         * Get the AccessibleComponent associated with this object if one
         * exists.  Otherwise return null.
	 *
	 * @return the component
         */
        public AccessibleComponent getAccessibleComponent() {
            return this;
        }


        // AccessibleComponent methods
        //
        /**
         * Get the background color of this object.
         *
         * @return the background color, if supported, of the object;
         * otherwise, null
         */
        public Color getBackground() {
            return JComponent.this.getBackground();
        }

        // NOTE: IN THE NEXT MAJOR RELEASE, isOpaque WILL MIGRATE
        //       TO java.awt.Component -- ADJUST @SEE LINK BELOW.
        /**
         * Set the background color of this object.
         * (For transparency, see <code>isOpaque</code>.)
         *
         * @param c the new Color for the background
         * @see JComponent#isOpaque
         */
        public void setBackground(Color c) {
            JComponent.this.setBackground(c);
        }

        /**
         * Get the foreground color of this object.
         *
         * @return the foreground color, if supported, of the object;
         * otherwise, null
         */
        public Color getForeground() {
            return JComponent.this.getForeground();
        }

        /**
         * Set the foreground color of this object.
         *
         * @param c the new Color for the foreground
         */
        public void setForeground(Color c) {
            JComponent.this.setForeground(c);
        }

        /**
         * Get the Cursor of this object.
         *
         * @return the Cursor, if supported, of the object; otherwise, null
         */
        public Cursor getCursor() {
            return JComponent.this.getCursor();
        }

        /**
         * Set the Cursor of this object.
         *
         * @param c the new Cursor for the object
         */
        public void setCursor(Cursor cursor) {
            JComponent.this.setCursor(cursor);
        }

        /**
         * Get the Font of this object.
         *
         * @return the Font,if supported, for the object; otherwise, null
         */
        public Font getFont() {
            return JComponent.this.getFont();
        }

        /**
         * Set the Font of this object.
         *
         * @param f the new Font for the object
         */
        public void setFont(Font f) {
            JComponent.this.setFont(f);
        }

        /**
         * Get the FontMetrics of this object.
         *
         * @param f the Font
         * @return the FontMetrics, if supported, the object; otherwise, null
         * @see #getFont
         */
        public FontMetrics getFontMetrics(Font f) {
            return JComponent.this.getFontMetrics(f);
        }

        /**
         * Determine if the object is enabled.
         *
         * @return true if object is enabled; otherwise, false
         */
        public boolean isEnabled() {
            return JComponent.this.isEnabled();
        }

        /**
         * Set the enabled state of the object.
         *
         * @param b if true, enables this object; otherwise, disables it
         */
        public void setEnabled(boolean b) {
            boolean old = JComponent.this.isEnabled();
            JComponent.this.setEnabled(b);
            if (b != old) {
                if (accessibleContext != null) {
                    if (b) {
                        accessibleContext.firePropertyChange(
                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                null, AccessibleState.ENABLED);
                    } else {
                        accessibleContext.firePropertyChange(
                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                AccessibleState.ENABLED, null);
                    }
                }
            }
        }

        /**
         * Determine if the object is visible.  Note: this means that the
         * object intends to be visible; however, it may not in fact be
         * showing on the screen because one of the objects that this object
         * is contained by is not visible.  To determine if an object is
         * showing on the screen, use isShowing().
         *
         * @return true if object is visible; otherwise, false
         */
        public boolean isVisible() {
            return JComponent.this.isVisible();
        }

        /**
         * Set the visible state of the object.
         *
         * @param b if true, shows this object; otherwise, hides it
         */
        public void setVisible(boolean b) {
            boolean old = JComponent.this.isVisible();
            JComponent.this.setVisible(b);
            if (b != old) {
                if (accessibleContext != null) {
                    if (b) {
                        accessibleContext.firePropertyChange(
                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                null, AccessibleState.VISIBLE);
                    } else {
                        accessibleContext.firePropertyChange(
                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                AccessibleState.VISIBLE, null);
                    }
                }
            }
        }

        /**
         * Determine if the object is showing.  This is determined by checking
         * the visibility of the object and ancestors of the object.  Note:
         * this will return true even if the object is obscured by another
         * (for example, it happens to be underneath a menu that was pulled
         * down).
         *
         * @return true if object is showing; otherwise, false
         */
        public boolean isShowing() {
            return JComponent.this.isShowing();
        }

        /**
         * Checks whether the specified point is within this object's bounds,
         * where the point's x and y coordinates are defined to be relative to
         * the coordinate system of the object.
         *
         * @param p the Point relative to the coordinate system of the object
         * @return true if object contains Point; otherwise false
         */
        public boolean contains(Point p) {
            return JComponent.this.contains(p);
        }

        /**
         * Returns the location of the object on the screen.
         *
         * @return location of object on screen -- can be null if this object
         * is not on the screen
         */
        public Point getLocationOnScreen() {
            if (JComponent.this.isShowing()) {
                return JComponent.this.getLocationOnScreen();
            } else {
                return null;
            }
        }

        /**
         * Gets the location of the object relative to the parent in the form
         * of a point specifying the object's top-left corner in the screen's
         * coordinate space.
         *
         * @return An instance of Point representing the top-left corner of
         * the objects's bounds in the coordinate space of the screen; null if
         * this object or its parent are not on the screen
         */
        public Point getLocation() {
            return JComponent.this.getLocation();
        }

        /**
         * Sets the location of the object relative to the parent.
         */
        public void setLocation(Point p) {
            JComponent.this.setLocation(p);
        }

        /**
         * Gets the bounds of this object in the form of a Rectangle object.
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *
         * @return A rectangle indicating this component's bounds; null if
         * this object is not on the screen.
         */
        public Rectangle getBounds() {
            return JComponent.this.getBounds();
        }

        /**
         * Sets the bounds of this object in the form of a Rectangle object.
         * The bounds specify this object's width, height, and location
         * relative to its parent.
         *
         * @param A rectangle indicating this component's bounds
         */
        public void setBounds(Rectangle r) {
            JComponent.this.setBounds(r);
        }

        /**
         * Returns the size of this object in the form of a Dimension object.
         * The height field of the Dimension object contains this objects's
         * height, and the width field of the Dimension object contains this
         * object's width.
         *
         * @return A Dimension object that indicates the size of this
         *         component; null if this object is not on the screen
         */
        public Dimension getSize() {
            return JComponent.this.getSize();
        }

        /**
         * Resizes this object so that it has width width and height.
         *
         * @param d - The dimension specifying the new size of the object.
         */
        public void setSize(Dimension d) {
            JComponent.this.setSize(d);
        }

        /**
         * Returns the Accessible child, if one exists, contained at the local
         * coordinate Point.
         *
         * @param p The point defining the top-left corner of the Accessible,
         * given in the coordinate space of the object's parent.
         * @return the Accessible, if it exists, at the specified location;
         * else null
         */
        public Accessible getAccessibleAt(Point p) {
            return SwingUtilities.getAccessibleAt(JComponent.this, p);
        }

        /**
         * Returns whether this object can accept focus or not.
         *
         * @return true if object can accept focus; otherwise false
         */
        public boolean isFocusTraversable() {
            return JComponent.this.isFocusTraversable();
        }

        /**
         * Requests focus for this object.
         */
        public void requestFocus() {
            JComponent.this.requestFocus();
        }

        /**
         * Adds the specified focus listener to receive focus events from this
         * component.
         *
         * @param l the focus listener
         */
        public void addFocusListener(FocusListener l) {
            JComponent.this.addFocusListener(l);
        }

        /**
         * Removes the specified focus listener so it no longer receives focus
         * events from this component.
         *
         * @param l the focus listener
         */
        public void removeFocusListener(FocusListener l) {
            JComponent.this.removeFocusListener(l);
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
 	}
 	else {
 	    return getClientProperties().get(key);
	}
    }


    /**
     * Add an arbitrary key/value "client property" to this component.
     * <p>
     * The <code>get/putClientProperty<code> methods provide access to 
     * a small per-instance hashtable. Callers can use get/putClientProperty
     * to annotate components that were created by another module, e.g. a 
     * layout manager might store per child constraints this way.  For example:
     * <pre>
     * componentA.putClientProperty("to the left of", componentB);
     * </pre>
     * <p>
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
            getClientProperties().put(key, value);
        } else {
            getClientProperties().remove(key);
        }

        firePropertyChange(key.toString(), oldValue, value);
    }


    /* --- Transitional java.awt.Component Support ---
     *
     * The methods and fields in this section will migrate to
     * java.awt.Component in the next JDK release.
     *
     */

    private SwingPropertyChangeSupport changeSupport;


    /**
     * Returns true if this component is a lightweight, i.e. if it doesn't
     * have a native window system peer.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
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
     * Return the current x coordinate of the components origin.
     * This method is preferable to writing component.getBounds().x,
     * or component.getLocation().x because it doesn't cause any
     * heap allocations.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @return the current x coordinate of the components origin.
     */
    public int getX() { return _bounds.x; }


    /**
     * Return the current y coordinate of the components origin.
     * This method is preferable to writing component.getBounds().y,
     * or component.getLocation().y because it doesn't cause any
     * heap allocations.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @return the current y coordinate of the components origin.
     */
    public int getY() { return _bounds.y; }


    /**
     * Return the current width of this component.
     * This method is preferable to writing component.getBounds().width,
     * or component.getSize().width because it doesn't cause any
     * heap allocations.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @return the current width of this component.
     */
    public int getWidth() { return _bounds.width; }


    /**
     * Return the current height of this component.
     * This method is preferable to writing component.getBounds().height,
     * or component.getSize().height because it doesn't cause any
     * heap allocations.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @return the current height of this component.
     */
    public int getHeight() { return _bounds.height; }


    /**
     * Returns true if this Component has the keyboard focus.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
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
     * rectangular region. A non-opaque component paints only some of
     * its pixels, allowing the pixels underneath it to "show through".
     * A component that does not fully paint its pixels therefore
     * provides a degree of transparency.
     * <p>
     * Subclasses that guarantee to always completely paint their contents should
     * override this method and return true.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @return true if this component is completely opaque.
     * @see #setOpaque
     */
    public boolean isOpaque() {
        return getFlag(IS_OPAQUE);
    }


    /**
     * If true the components background will be filled with the
     * background color. Otherwise, the background is transparent,
     * and whatever is underneath will show through.
     * <p>
     * The default value of this property is false.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @see #isOpaque
     */
    public void setOpaque(boolean isOpaque) {
        boolean oldValue = getFlag(IS_OPAQUE);
        setFlag(IS_OPAQUE, isOpaque);
        firePropertyChange("opaque", oldValue, isOpaque);
    }


    /**
     * If the specified retangle is completely obscured by any of this
     * components opaque children then return true.  Only direct children
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
                y >= childBounds.y && (y + height) <= (childBounds.y + childBounds.height)) {

                if(child instanceof JComponent) {
//		    System.out.println("A) checking opaque: " + ((JComponent)child).isOpaque() + "  " + child);
//		    System.out.print("B) ");
//		    Thread.dumpStack();
                    return ((JComponent)child).isOpaque();
                } else {
                    /** Sometimes a heavy weight can have a bound larger than it's peer size
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
     * <code>visibleRect</code>
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
     * <code>visibleRect</code>
     *
     * @see #getVisibleRect
     */
    public void computeVisibleRect(Rectangle visibleRect) {
        computeVisibleRect(this, visibleRect);
    }


    /**
     * Returns the Component's "visible rectangle" -  the
     * intersection of this components visible rectangle:
     * <pre>
     * new Rectangle(0, 0, getWidth(), getHeight());
     * </pre>
     * and all of its ancestors visible Rectangles.
     *
     * @return the visible rectangle
     */
    public Rectangle getVisibleRect() {
        Rectangle visibleRect = new Rectangle();

        computeVisibleRect(visibleRect);
        return visibleRect;
    }


    /**
     * Support for reporting bound property changes.  If oldValue and
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
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @param propertyName  The programmatic name of the property that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
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
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired in response to setting
     * a bound property, e.g. setFont, setBackground, or setForeground.
     * Note that if the current component is inheriting its foreground,
     * background, or font from its container, then no event will be
     * fired in response to a change in the inherited property.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @param listener  The PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }


    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }


    /**
     * Support for reporting constrained property changes.  This method can be called
     * when a constrained property has changed and it will send the appropriate
     * PropertyChangeEvent to any registered VetoableChangeListeners.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
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
     * Add a VetoableChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @param listener  The VetoableChangeListener to be added
     */
    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
        if (vetoableChangeSupport == null) {
            vetoableChangeSupport = new java.beans.VetoableChangeSupport(this);
        }
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }


    /**
     * Remove a VetoableChangeListener from the listener list.
     * This removes a VetoableChangeListener that was registered
     * for all properties.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @param listener  The VetoableChangeListener to be removed
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
     * @return the top-level Container which this component is in.
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
     * or removed from the Component hierarchy
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
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
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
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
     * Notification to this component that it now has a parent component.
     * When this method is invoked, the chain of parent components is
     * set up with KeyboardAction event listeners.
     *
     * @see #registerKeyboardAction
     */
    public void addNotify() {
        super.addNotify();
        firePropertyChange("ancestor", null, getParent());


        Hashtable bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
	if( bindings == null)
	    return;

	Enumeration iter = bindings.keys();
	while (iter.hasMoreElements()) {
	    KeyStroke ks = (KeyStroke)iter.nextElement();
	    KeyboardBinding aBinding = (KeyboardBinding)bindings.get(ks);
	    if (aBinding.condition == WHEN_IN_FOCUSED_WINDOW) {
	        registerWithKeyboardManager(ks);
	    }
	}
    }


    /**
     * Notification to this component that it no longer has a parent component.
     * When this method is invoked, any KeyboardActions set up in the
     * the chain of parent components are removed.
     *
     * @see #registerKeyboardAction
     */
    public void removeNotify() {
        super.removeNotify();
        // This isn't strictly correct.  The event shouldn't be
        // fired until *after* the parent is set to null.  But
        // we only get notified before that happens
        firePropertyChange("ancestor", getParent(), null);

        Hashtable bindings = (Hashtable) getClientProperty(KEYBOARD_BINDINGS_KEY);
	if( bindings == null)
	    return;

	Enumeration iter = bindings.keys();
	while (iter.hasMoreElements()) {
	    KeyStroke ks = (KeyStroke)iter.nextElement();
	    KeyboardBinding aBinding = (KeyboardBinding)bindings.get(ks);
	    if (aBinding.condition == WHEN_IN_FOCUSED_WINDOW) {
	        unregisterWithKeyboardManager(ks);
	    }
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
     * Support for deferred automatic layout.  
     * <p> 
     * Calls invalidate() and then adds this components validateRoot
     * to a list of components that need to be validated.  Validation
     * will occur after all currently pending events have been dispatched.
     * In other words after this method is called,  the first validateRoot
     * (if any) found when walking up the containment hierarchy of this 
     * component will be validated.
     * By default, JRootPane, JScrollPane, and JTextField return true 
     * from isValidateRoot().
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
     * Returns true if this component tiles its children, i.e. if
     * it can guarantee that the children will not overlap.  The
     * repainting system is substantially more efficient in this
     * common case.  JComponent subclasses that can't make this
     * guarantee, e.g. JLayeredPane, should override this method
     * to return false.
     *
     * @return true if this components children don't overlap
     */
    public boolean isOptimizedDrawingEnabled() {
        return true;
    }


    /**
     * Paint the specified region in this component and all of its
     * descendants that overlap the region, immediately.
     * <p>
     * It's rarely neccessary to call this method.  In most cases it's
     * more efficient to call repaint which defers the actual painting
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
     * Paint the specified region now.
     * <p>
     * This method will migrate to java.awt.Component in the next major JDK release
     */
    public void paintImmediately(Rectangle r) {
        paintImmediately(r.x,r.y,r.width,r.height);
    }

    /**
     * Return whether this component should be guaranteed to be on top.
     * For examples, it would make no sense for Menus to pop up under
     * another component, so they would always return true. Most components
     * will want to return false, hence that is the default.
     */
    // package private
    boolean alwaysOnTop() {
	return false;
    }

    private Rectangle paintImmediatelyClip = new Rectangle(0,0,0,0);

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

	tmpX = tmpY = tmpWidth = tmpHeight = 0;

        paintImmediatelyClip.x = x;
        paintImmediatelyClip.y = y;
        paintImmediatelyClip.width = w;
        paintImmediatelyClip.height = h;

	
	// System.out.println("1) ************* in _paintImmediately for " + this);
	
	boolean ontop = alwaysOnTop() && isOpaque();

	for (c = this; c != null && !(c instanceof Window) && !(c instanceof Applet); c = c.getParent()) {
		if(!ontop && (c instanceof JComponent) &&
                   !(((JComponent)c).isOptimizedDrawingEnabled())) {
		    paintingComponent = (JComponent)c;
		    offsetX = offsetY = 0;
		    hasBuffer = false; /** Get rid of any buffer since we draw from here and
					*  we might draw something larger
					*/
		}
		
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
	
        try {
            g = SwingGraphics.createSwingGraphics(paintingComponent.getGraphics());
        } catch(NullPointerException e) {
            g = null;
            e.printStackTrace();
        }

        if(g == null) {
            System.err.println("In paintImmediately null graphics");
            return;
        }

        if(hasBuffer) {
            Image offscreen = repaintManager.getOffscreenBuffer(bufferedComponent,paintImmediatelyClip.width,paintImmediatelyClip.height);
            paintWithBuffer(paintingComponent,g,paintImmediatelyClip,offscreen);
            g.dispose();
        } else {
	    //System.out.println("has no buffer");
            g.setClip(paintImmediatelyClip.x,paintImmediatelyClip.y,paintImmediatelyClip.width,paintImmediatelyClip.height);
            try {
                paintingComponent.paint(g);
            } finally {
                g.dispose();
            }
        }
    }

    private void paintWithBuffer(JComponent paintingComponent,Graphics g,Rectangle clip,Image offscreen) {
        Graphics og = SwingGraphics.createSwingGraphics(offscreen.getGraphics());
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

    /** Set whether the receiving component should use a buffer to paint.
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

    /** Return whether the receiving component should use a buffer to paint. **/
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
     * effectively calls our writeObject method which takes care of uninstalling
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
	    if (ui != null) {
		ui.uninstallUI(JComponent.this);
	    }
	}
    }


    /**
     * Called by the JComponent constructor.  Adds a fake FocusListener
     * whose real purpose is to uninstall the components UI early.
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
		    p = p.getParent();
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

	/* If there's no ReadObjectCallback for this stream yet, i.e. if
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
    }


    /**
     * Before writing a JComponent to an ObjectOutputStream we temporarily 
     * uninstall its UI.  This is tricky to do because we want to uninstall
     * the UI before any of the JComponents children (or its LayoutManager etc.)
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
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JComponent.
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



