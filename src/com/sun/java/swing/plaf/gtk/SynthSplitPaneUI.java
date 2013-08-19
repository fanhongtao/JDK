/*
 * @(#)SynthSplitPaneUI.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.beans.*;
import java.util.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;


/**
 * A Basic L&F implementation of the SplitPaneUI.
 *
 * @version 1.11, 01/23/03 (based on BasicSplitPaneUI v 1.72)
 * @author Scott Violet
 * @author Steve Wilson
 * @author Ralph Kar
 */
class SynthSplitPaneUI extends SplitPaneUI implements SynthUI {
    /**
     * The divider used for non-continuous layout is added to the split pane
     * with this object.
     */
    private static final String NON_CONTINUOUS_DIVIDER =
        "nonContinuousDivider";


    /**
     * How far (relativ) the divider does move when it is moved around by
     * the cursor keys on the keyboard.
     */
    protected static int KEYBOARD_DIVIDER_MOVE_OFFSET = 3;


    /**
     * JSplitPane instance this instance is providing
     * the look and feel for.
     */
    protected JSplitPane splitPane;


    /**
     * Instance of the divider for this JSplitPane.
     */
    protected Divider divider;


    /**
     * Instance of the PropertyChangeListener for this JSplitPane.
     */
    protected PropertyChangeListener propertyChangeListener;


    /**
     * Instance of the FocusListener for this JSplitPane.
     */
    protected FocusListener focusListener;


    /**
     * Keys to use for forward focus traversal when the JComponent is
     * managing focus.
     */
    private static Set managingFocusForwardTraversalKeys;

    /**
     * Keys to use for backward focus traversal when the JComponent is
     * managing focus.
     */
    private static Set managingFocusBackwardTraversalKeys;


    /**
     * The size of the divider while the dragging session is valid.
     */
    protected int dividerSize;


    /**
     * Instance for the shadow of the divider when non continuous layout
     * is being used.
     */
    protected Component nonContinuousLayoutDivider;


    /**
     * Set to true in startDragging if any of the children
     * (not including the nonContinuousLayoutDivider) are heavy weights.
     */
    protected boolean draggingHW;


    /**
     * Location of the divider when the dragging session began.
     */
    protected int beginDragDividerLocation;


    // Private data of the instance
    private int         orientation;
    private int         lastDragLocation;
    private boolean     continuousLayout;
    private boolean     dividerKeyboardResize;
    private boolean     dividerLocationIsSet;  // needed for tracking
                                               // the first occurrence of
                                               // setDividerLocation()
    private boolean rememberPaneSizes;

    /** Indicates that we have painted once. */
    // This is used by the LayoutManager to determine when it should use
    // the divider location provided by the JSplitPane. This is used as there
    // is no way to determine when the layout process has completed.
    boolean             painted;
    /** If true, setDividerLocation does nothing. */
    boolean             ignoreDividerLocationChange;

    /**
     * Style for the JSplitPane.
     */
    private SynthStyle style;
    /**
     * Style for the divider.
     */
    private SynthStyle dividerStyle;

    /**
     * Size of the one touch buttons.
     */
    private int oneTouchSize;

    /**
     * Amount to offset one touch buttons.
     */
    private int oneTouchOffset;


    /**
     * Creates a new SynthSplitPaneUI instance
     */
    public static ComponentUI createUI(JComponent x) {
        return new SynthSplitPaneUI();
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
        map.put("negativeIncrement", new KeyboardUpLeftAction());
	map.put("positiveIncrement", new KeyboardDownRightAction());
	map.put("selectMin", new KeyboardHomeAction());
	map.put("selectMax", new KeyboardEndAction());
	map.put("startResize", new KeyboardResizeToggleAction());
	map.put("toggleFocus", new ToggleSideFocusAction());
	map.put("focusOutForward", new MoveFocusOutAction(1));
	map.put("focusOutBackward", new MoveFocusOutAction(-1));
    }


    /**
     * Installs the UI.
     */
    public void installUI(JComponent c) {
        splitPane = (JSplitPane) c;
        dividerLocationIsSet = false;
        dividerKeyboardResize = false;
        installComponents();
        installDefaults();
        installListeners();
        installKeyboardActions();
        setLastDragLocation(-1);
    }

    protected void installComponents() {
        divider = new Divider(splitPane);
        splitPane.add(divider, JSplitPane.DIVIDER);
    }

    /**
     * Installs the UI defaults.
     */
    protected void installDefaults() {
        setOrientation(splitPane.getOrientation());
        setContinuousLayout(splitPane.isContinuousLayout());

        fetchStyle(splitPane);

        resetLayoutManager();

        /* Install the nonContinuousLayoutDivider here to avoid having to
        add/remove everything later. */
        if(nonContinuousLayoutDivider == null) {
            setNonContinuousLayoutDivider(
                                createDefaultNonContinuousLayoutDivider(),
                                true);
        } else {
            setNonContinuousLayoutDivider(nonContinuousLayoutDivider, true);
        }

	// focus forward traversal key
	if (managingFocusForwardTraversalKeys==null) {
	    managingFocusForwardTraversalKeys = new TreeSet();
	    managingFocusForwardTraversalKeys.add(
		KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
	}
	splitPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
					managingFocusForwardTraversalKeys);
	// focus backward traversal key
	if (managingFocusBackwardTraversalKeys==null) {
	    managingFocusBackwardTraversalKeys = new TreeSet();
	    managingFocusBackwardTraversalKeys.add(
		KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
	}
	splitPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
					managingFocusBackwardTraversalKeys);
    }

    private void fetchStyle(JSplitPane splitPane) {
        SynthContext context = getContext(splitPane, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            splitPane.setDividerSize(((Integer)style.get(
                    context,"SplitPaneDivider.size")).intValue());
            divider.setDividerSize(splitPane.getDividerSize());
        }
        context.dispose();

        context = getContext(splitPane, Region.SPLIT_PANE_DIVIDER, ENABLED);
        dividerStyle = SynthLookAndFeel.updateStyle(context, this);
        divider.setOpaque(dividerStyle.isOpaque(context));
        oneTouchSize = dividerStyle.getInt(context,
                                   "SplitPaneDivider.oneTouchButtonSize", 6);
        oneTouchOffset = dividerStyle.getInt(context,
                                   "SplitPaneDivider.oneTouchOffset", 6);
        context.dispose();
    }

    /**
     * Installs the event listeners for the UI.
     */
    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) !=
            null) {
            splitPane.addPropertyChangeListener(propertyChangeListener);
        }

        if ((focusListener = createFocusListener()) != null) {
            splitPane.addFocusListener(focusListener);
        }
    }


    /**
     * Installs the keyboard actions for the UI.
     */
    protected void installKeyboardActions() {
	InputMap km = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(splitPane, JComponent.
				       WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				       km);
        LazyActionMap.installLazyActionMap(splitPane, SynthSplitPaneUI.class,
                                           "SplitPane.actionMap");
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            SynthContext context = getContext(splitPane, ENABLED);
	    InputMap map = (InputMap)context.getStyle().get(context,
                                               "SplitPane.ancestorInputMap");
            context.dispose();
            return map;
	}
	return null;
    }

    /**
     * Uninstalls the UI.
     */
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
        uninstallComponents();
        dividerLocationIsSet = false;
        dividerKeyboardResize = false;
        splitPane = null;
    }

    private void uninstallComponents() {
        if(nonContinuousLayoutDivider != null) {
            splitPane.remove(nonContinuousLayoutDivider);
        }
        splitPane.remove(divider);
        divider = null;
    }


    /**
     * Uninstalls the UI defaults.
     */
    protected void uninstallDefaults() {
        if (splitPane.getLayout() instanceof UIResource) {
            splitPane.setLayout(null);
        }

        SynthContext context = getContext(splitPane, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        context = getContext(splitPane, Region.SPLIT_PANE_DIVIDER, ENABLED);
        dividerStyle.uninstallDefaults(context);
        context.dispose();
        dividerStyle = null;

        nonContinuousLayoutDivider = null;

        setNonContinuousLayoutDivider(null);

	// sets the focus forward and backward traversal keys to null
	// to restore the defaults
	splitPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
	splitPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
    }


    /**
     * Uninstalls the event listeners for the UI.
     */
    protected void uninstallListeners() {
        if (propertyChangeListener != null) {
            splitPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
        if (focusListener != null) {
            splitPane.removeFocusListener(focusListener);
            focusListener = null;
        }
    }


    /**
     * Uninstalls the keyboard actions for the UI.
     */
    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(splitPane, null);
	SwingUtilities.replaceUIInputMap(splitPane, JComponent.
				      WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				      null);
    }


    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private SynthContext getContext(JComponent c, Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    private SynthContext getContext(JComponent c, Region region, int state) {
        return SynthContext.getContext(SynthContext.class, c, region,
                                       style, state);
    }

    private int getComponentState(JComponent c, Region subregion) {
        int state = SynthLookAndFeel.getComponentState(c);

        if (divider.isMouseOver()) {
            state |= MOUSE_OVER;
        }
        return state;
    }

    /**
     * Creates a PropertyChangeListener for the JSplitPane UI.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyHandler();
    }


    /**
     * Creates a FocusListener for the JSplitPane UI.
     */
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }


    /**
     * Returns the orientation for the JSplitPane.
     */
    public int getOrientation() {
        return orientation;
    }


    /**
     * Set the orientation for the JSplitPane.
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }


    /**
     * Determines wether the JSplitPane is set to use a continuous layout.
     */
    public boolean isContinuousLayout() {
        return continuousLayout;
    }


    /**
     * Turn continuous layout on/off.
     */
    public void setContinuousLayout(boolean b) {
        continuousLayout = b;
    }


    /**
     * Returns the last drag location of the JSplitPane.
     */
    public int getLastDragLocation() {
        return lastDragLocation;
    }


    /**
     * Set the last drag location of the JSplitPane.
     */
    public void setLastDragLocation(int l) {
        lastDragLocation = l;
    }

    /**
     * @return increment via keyboard methods.
     */
    int getKeyboardMoveIncrement() {
	return KEYBOARD_DIVIDER_MOVE_OFFSET;
    }

    /**
     * Implementation of the PropertyChangeListener
     * that the JSplitPane UI uses.
     */
    class PropertyHandler implements PropertyChangeListener
    {
        /**
         * Messaged from the <code>JSplitPane</code> the receiver is
         * contained in.  May potentially reset the layout manager and cause a
         * <code>validate</code> to be sent.
         */
        public void propertyChange(PropertyChangeEvent e) {
            if(e.getSource() == splitPane) {
                String changeName = e.getPropertyName();

                if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                    fetchStyle((JSplitPane)e.getSource());
                }
                if(changeName.equals(JSplitPane.ORIENTATION_PROPERTY)) {
                    orientation = splitPane.getOrientation();
                    resetLayoutManager();
                } else if(changeName.equals(
                                    JSplitPane.CONTINUOUS_LAYOUT_PROPERTY)) {
                    setContinuousLayout(splitPane.isContinuousLayout());
                    if(!isContinuousLayout()) {
                        if(nonContinuousLayoutDivider == null) {
                            setNonContinuousLayoutDivider(
                                createDefaultNonContinuousLayoutDivider(),
                                true);
                        } else if(nonContinuousLayoutDivider.getParent() ==
                                  null) {
                            setNonContinuousLayoutDivider(
                                nonContinuousLayoutDivider,
                                true);
                        }
                    }
                } else if(changeName.equals(JSplitPane.DIVIDER_SIZE_PROPERTY)){
                    divider.setDividerSize(splitPane.getDividerSize());
		    dividerSize = divider.getDividerSize();
                    splitPane.revalidate();
		    splitPane.repaint();
                }
            }
        }
    }


    /**
     * Implementation of the FocusListener that the JSplitPane UI uses.
     */
    class FocusHandler extends FocusAdapter
    {
        public void focusGained(FocusEvent ev) {
            dividerKeyboardResize = true;
            splitPane.repaint();
        }

        public void focusLost(FocusEvent ev) {
            dividerKeyboardResize = false;
            splitPane.repaint();
        }
    }
    

    static class KeyboardUpLeftAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
	    JSplitPane splitPane = (JSplitPane)ev.getSource();
	    SynthSplitPaneUI ui = (SynthSplitPaneUI)splitPane.getUI();
            if (ui.dividerKeyboardResize) {
		splitPane.setDividerLocation(Math.max(0,ui.getDividerLocation
				  (splitPane) - ui.getKeyboardMoveIncrement()));
            }
        }
    }


    static class KeyboardDownRightAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
	    JSplitPane splitPane = (JSplitPane)ev.getSource();
	    SynthSplitPaneUI ui = (SynthSplitPaneUI)splitPane.getUI();
            if (ui.dividerKeyboardResize) {
                splitPane.setDividerLocation(ui.getDividerLocation(splitPane) +
					     ui.getKeyboardMoveIncrement());
            }
        }
    }
    

    static class KeyboardHomeAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
	    JSplitPane splitPane = (JSplitPane)ev.getSource();
	    SynthSplitPaneUI ui = (SynthSplitPaneUI)splitPane.getUI();
            if (ui.dividerKeyboardResize) {
                splitPane.setDividerLocation(0);
            }
        }
    }
    

    static class KeyboardEndAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
	    JSplitPane splitPane = (JSplitPane)ev.getSource();
	    SynthSplitPaneUI ui = (SynthSplitPaneUI)splitPane.getUI();
            if (ui.dividerKeyboardResize) {
		Insets   insets = splitPane.getInsets();
		int      bottomI = (insets != null) ? insets.bottom : 0;
		int      rightI = (insets != null) ? insets.right : 0;

                if (ui.orientation == JSplitPane.VERTICAL_SPLIT) {
                    splitPane.setDividerLocation(splitPane.getHeight() -
                                       bottomI);
                }
                else {
                    splitPane.setDividerLocation(splitPane.getWidth() -
						 rightI);
                }
            }
        }
    }

    static class KeyboardResizeToggleAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
	    JSplitPane splitPane = (JSplitPane)ev.getSource();
	    SynthSplitPaneUI ui = (SynthSplitPaneUI)splitPane.getUI();
            if (!ui.dividerKeyboardResize) {
                splitPane.requestFocus();
	    } else {
		JSplitPane parentSplitPane =
		    (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, splitPane);
		if (parentSplitPane!=null) {
		    parentSplitPane.requestFocus();
		}
            }
        }
    }


    /**
     * ActionListener that will focus on the opposite component that
     * has focus. EG if the left side has focus, this will transfer focus
     * to the right component.
     */
    static class ToggleSideFocusAction extends AbstractAction {
	public void actionPerformed(ActionEvent ae) {
	    JSplitPane splitPane = (JSplitPane)ae.getSource();
	    Component left = splitPane.getLeftComponent();
	    Component right = splitPane.getRightComponent();

	    KeyboardFocusManager manager =
		KeyboardFocusManager.getCurrentKeyboardFocusManager();
	    Component focus = manager.getFocusOwner();
	    Component focusOn = getNextSide(splitPane, focus);
	    if (focusOn != null) {
		// don't change the focus if the new focused component belongs
		// to the same splitpane and the same side
		if ( focus!=null &&
		     ( (SwingUtilities.isDescendingFrom(focus, left) &&
			SwingUtilities.isDescendingFrom(focusOn, left)) ||
		       (SwingUtilities.isDescendingFrom(focus, right) &&
			SwingUtilities.isDescendingFrom(focusOn, right)) ) ) {
		    return;
		}
 		SynthLookAndFeel.compositeRequestFocus(focusOn);
	    }
	}

	private Component getNextSide(JSplitPane splitPane, Component focus) {
	    Component left = splitPane.getLeftComponent();
	    Component right = splitPane.getRightComponent();
	    Component next = null;
	    if (focus!=null && SwingUtilities.isDescendingFrom(focus, left) &&
		right!=null) {
		next = getFirstAvailableComponent(right);
		if (next != null) {
		    return next;
		}
	    }
	    JSplitPane parentSplitPane = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, splitPane);
	    if (parentSplitPane!=null) {
		// focus next side of the parent split pane
		next = getNextSide(parentSplitPane, focus);
	    } else {
		next = getFirstAvailableComponent(left);
		if (next == null) {
		    next = getFirstAvailableComponent(right);
		}
	    }
	    return next;
	}

	private Component getFirstAvailableComponent(Component c) {
	    if (c!=null && c instanceof JSplitPane) {
		JSplitPane sp = (JSplitPane)c;
		Component left = getFirstAvailableComponent(sp.getLeftComponent());
		if (left != null) {
		    c = left;
		} else {
		    c = getFirstAvailableComponent(sp.getRightComponent());
		}
	    }
	    return c;
	}
    }

    /**
     * Action that will move focus out of the splitpane to the next
     * component (if present) if direction > 0 or to the previous
     * component (if present) if direction < 0
     */
    static class MoveFocusOutAction extends AbstractAction {

	private int direction;

	public MoveFocusOutAction(int newDirection) {
	    direction = newDirection;
	}

	public void actionPerformed(ActionEvent ae) {
	    JSplitPane splitPane = (JSplitPane)ae.getSource();
	    Container rootAncestor = splitPane.getFocusCycleRootAncestor();
	    FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
	    Component focusOn = (direction > 0) ?
		policy.getComponentAfter(rootAncestor, splitPane) :
		policy.getComponentBefore(rootAncestor, splitPane);
	    HashSet focusFrom = new HashSet();
	    if (splitPane.isAncestorOf(focusOn)) {
		do {
		    focusFrom.add(focusOn);
		    rootAncestor = focusOn.getFocusCycleRootAncestor();
		    policy = rootAncestor.getFocusTraversalPolicy();
		    focusOn = (direction > 0) ?
			policy.getComponentAfter(rootAncestor, focusOn) :
			policy.getComponentBefore(rootAncestor, focusOn);
		} while (splitPane.isAncestorOf(focusOn) &&
			 !focusFrom.contains(focusOn));
	    }
	    if ( focusOn!=null && !splitPane.isAncestorOf(focusOn) ) {
		focusOn.requestFocus();
	    }
	}
    }
    
    /**
     * Returns the default non continuous layout divider, which is an
     * instanceof Canvas that fills the background in dark gray.
     */
    protected Component createDefaultNonContinuousLayoutDivider() {
        // PENDING: make this lightweight.
        return new Canvas() {
            public void paint(Graphics g) {
                paintDragDivider(g, 0, 0, getWidth(), getHeight());
/*
                if(!isContinuousLayout() && getLastDragLocation() != -1) {
                    Dimension      size = splitPane.getSize();

                    g.setColor(Color.darkGray);
                    if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                        g.fillRect(0, 0, dividerSize - 1, size.height - 1);
                    } else {
                        g.fillRect(0, 0, size.width - 1, dividerSize - 1);
                    }
                }
*/
            }
        };
    }


    /**
     * Sets the divider to use when the splitPane is configured to
     * not continuously layout. This divider will only be used during a
     * dragging session. It is recommended that the passed in component
     * be a heavy weight.
     */
    protected void setNonContinuousLayoutDivider(Component newDivider) {
        setNonContinuousLayoutDivider(newDivider, true);
    }


    /**
     * Sets the divider to use.
     */
    protected void setNonContinuousLayoutDivider(Component newDivider,
        boolean rememberSizes) {
        rememberPaneSizes = rememberSizes;
        if(nonContinuousLayoutDivider != null && splitPane != null) {
            splitPane.remove(nonContinuousLayoutDivider);
        }
        nonContinuousLayoutDivider = newDivider;
    }

    private void addHeavyweightDivider() {
        // PENDING: nuke this.
        if(nonContinuousLayoutDivider != null && splitPane != null) {

            /* Needs to remove all the components and re-add them! YECK! */
	    // This is all done so that the nonContinuousLayoutDivider will
	    // be drawn on top of the other components, without this, one
	    // of the heavyweights will draw over the divider!
            Component             leftC = splitPane.getLeftComponent();
            Component             rightC = splitPane.getRightComponent();
	    int                   lastLocation = splitPane.
		                              getDividerLocation();

            if(leftC != null)
                splitPane.setLeftComponent(null);
            if(rightC != null)
                splitPane.setRightComponent(null);
            splitPane.remove(divider);
            splitPane.add(nonContinuousLayoutDivider, SynthSplitPaneUI.
                          NON_CONTINUOUS_DIVIDER,
                          splitPane.getComponentCount());
            splitPane.setLeftComponent(leftC);
            splitPane.setRightComponent(rightC);
            splitPane.add(divider, JSplitPane.DIVIDER);
            if(rememberPaneSizes) {
		splitPane.setDividerLocation(lastLocation);
	    } 
        }
 
    }


    /**
     * Returns the divider to use when the splitPane is configured to
     * not continuously layout. This divider will only be used during a
     * dragging session.
     */
    public Component getNonContinuousLayoutDivider() {
        return nonContinuousLayoutDivider;
    }


    /**
     * Returns the splitpane this instance is currently contained
     * in.
     */
    public JSplitPane getSplitPane() {
        return splitPane;
    }


    /**
     * Messaged to reset the preferred sizes.
     */
    public void resetToPreferredSizes(JSplitPane jc) {
        if(splitPane != null) {
            LayoutManager layout = jc.getLayout();
            if (layout instanceof SplitPaneLayoutManager) {
                ((SplitPaneLayoutManager)layout).resetToPreferredSizes();
            }
            splitPane.revalidate();
	    splitPane.repaint();
        }
    }


    /**
     * Sets the location of the divider to location.
     */
    public void setDividerLocation(JSplitPane jc, int location) {
	if (!ignoreDividerLocationChange) {
	    dividerLocationIsSet = true;
	    splitPane.revalidate();
	    splitPane.repaint();
	}
	else {
	    ignoreDividerLocationChange = false;
	}
    }


    /**
     * Returns the location of the divider, which may differ from what
     * the splitpane thinks the location of the divider is.
     */
    public int getDividerLocation(JSplitPane jc) {
        if(orientation == JSplitPane.HORIZONTAL_SPLIT)
            return divider.getX();
        return divider.getY();
    }


    /**
     * Gets the minimum location of the divider.
     */
    public int getMinimumDividerLocation(JSplitPane jc) {
        int       minLoc = 0;
        Component leftC = splitPane.getLeftComponent();

        if ((leftC != null) && (leftC.isVisible())) {
            Insets    insets = splitPane.getInsets();
            Dimension minSize = leftC.getMinimumSize();
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                minLoc = minSize.width;
            } else {
                minLoc = minSize.height;
            }
            if(insets != null) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    minLoc += insets.left;
                } else {
                    minLoc += insets.top;
                }
            }
        }
        return minLoc;
    }


    /**
     * Gets the maximum location of the divider.
     */
    public int getMaximumDividerLocation(JSplitPane jc) {
        Dimension splitPaneSize = splitPane.getSize();
        int       maxLoc = 0;
        Component rightC = splitPane.getRightComponent();

        if (rightC != null) {
            Insets    insets = splitPane.getInsets();
            Dimension minSize = new Dimension(0, 0);
            if (rightC.isVisible()) {
                minSize = rightC.getMinimumSize();
            }
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                maxLoc = splitPaneSize.width - minSize.width;
            } else {
                maxLoc = splitPaneSize.height - minSize.height; 
            }
            maxLoc -= dividerSize;
            if(insets != null) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    maxLoc -= insets.right;
                } else {
                    maxLoc -= insets.top;
                }
            }
        }
        return Math.max(getMinimumDividerLocation(splitPane), maxLoc);
    }


    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
	if (!painted && splitPane.getDividerLocation()<0) {
	    ignoreDividerLocationChange = true;
	    splitPane.setDividerLocation(getDividerLocation(splitPane));
	}
	painted = true;
    }


    private void paintDragDivider(Graphics g, int x, int y, int w, int h) {
        SynthContext context = getContext(splitPane,Region.SPLIT_PANE_DIVIDER);
        context.setComponentState(((context.getComponentState() | MOUSE_OVER) ^
                                   MOUSE_OVER) | PRESSED);
        SynthPainter painter = (SynthPainter)context.getStyle().
                               get(context, "SplitPane.dragPainter");

        if (painter != null) {
            painter.paint(context, "foreground", g, x, y, w, h);
        }
        context.dispose();
    }

    /**
     * Messaged after the JSplitPane the receiver is providing the look
     * and feel for paints its children.
     */
    public void finishedPaintingChildren(JSplitPane jc, Graphics g) {
        if(jc == splitPane && getLastDragLocation() != -1 &&
                              !isContinuousLayout() && !draggingHW) {
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                paintDragDivider(g, getLastDragLocation(), 0, dividerSize - 1,
                                 splitPane.getHeight() - 1);
            } else {
                paintDragDivider(g, 0, lastDragLocation,
                                 splitPane.getWidth() - 1, dividerSize - 1);
            }
        }
    }

    /**
     * Resets the layout manager based on orientation and messages it
     * with invalidateLayout to pull in appropriate Components.
     */
    protected void resetLayoutManager() {
        SplitPaneLayoutManager layoutManager;

        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            layoutManager = new SplitPaneLayoutManager(0);
        } else {
            layoutManager = new SplitPaneLayoutManager(1);
        }
        splitPane.setLayout(layoutManager);
        layoutManager.updateComponents();
        splitPane.revalidate();
        splitPane.repaint();
    }


    /**
     * Should be messaged before the dragging session starts, resets
     * lastDragLocation and dividerSize.
     */
    protected void startDragging() {
        Component       leftC = splitPane.getLeftComponent();
        Component       rightC = splitPane.getRightComponent();
        ComponentPeer   cPeer;

        beginDragDividerLocation = getDividerLocation(splitPane);
        draggingHW = false;
        if(leftC != null && (cPeer = leftC.getPeer()) != null &&
           !(cPeer instanceof LightweightPeer)) {
            draggingHW = true;
        } else if(rightC != null && (cPeer = rightC.getPeer()) != null
                  && !(cPeer instanceof LightweightPeer)) {
            draggingHW = true;
        }
        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            setLastDragLocation(divider.getBounds().x);
            dividerSize = divider.getSize().width;
            if(!isContinuousLayout() && draggingHW) {
                nonContinuousLayoutDivider.setBounds
                        (getLastDragLocation(), 0, dividerSize,
                         splitPane.getHeight());
                addHeavyweightDivider();
            }
        } else {
            setLastDragLocation(divider.getBounds().y);
            dividerSize = divider.getSize().height;
            if(!isContinuousLayout() && draggingHW) {
                nonContinuousLayoutDivider.setBounds
                        (0, getLastDragLocation(), splitPane.getWidth(),
                         dividerSize);
                addHeavyweightDivider();
            }
        }
    }


    /**
     * Messaged during a dragging session to move the divider to the
     * passed in location. If continuousLayout is true the location is
     * reset and the splitPane validated.
     */
    protected void dragDividerTo(int location) {
        if(getLastDragLocation() != location) {
            if(isContinuousLayout()) {
		splitPane.setDividerLocation(location);
                setLastDragLocation(location);
            } else {
                int lastLoc = getLastDragLocation();

                setLastDragLocation(location);
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    if(draggingHW) {
                        nonContinuousLayoutDivider.setLocation(
                            getLastDragLocation(), 0);
                    } else {
			int   splitHeight = splitPane.getHeight();
                        splitPane.repaint(lastLoc, 0, dividerSize,
                                          splitHeight);
                        splitPane.repaint(location, 0, dividerSize,
                                          splitHeight);
                    }
                } else {
                    if(draggingHW) {
                        nonContinuousLayoutDivider.setLocation(0,
                            getLastDragLocation());
                    } else {
			int    splitWidth = splitPane.getWidth();

                        splitPane.repaint(0, lastLoc, splitWidth,
                                          dividerSize);
                        splitPane.repaint(0, location, splitWidth,
                                          dividerSize);
                    }
                }
            }
        }
    }


    /**
     * Messaged to finish the dragging session. If not continuous display
     * the dividers location will be reset.
     */
    protected void finishDraggingTo(int location) {
        dragDividerTo(location);
        setLastDragLocation(-1);
        if(!isContinuousLayout()) {
            Component   leftC = splitPane.getLeftComponent();
            Rectangle   leftBounds = leftC.getBounds();

	    if (draggingHW) {
		if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    nonContinuousLayoutDivider.setLocation(-dividerSize, 0);
		}
		else {
                    nonContinuousLayoutDivider.setLocation(0, -dividerSize);
		}
		splitPane.remove(nonContinuousLayoutDivider);
	    }
	    splitPane.setDividerLocation(location);
        }
    }


    /**
     * LayoutManager for JSplitPanes that have an orientation of
     * HORIZONTAL_SPLIT.
     */
    private class SplitPaneLayoutManager implements LayoutManager2,UIResource {
        /* left, right, divider. (in this exact order) */
        protected int[]         sizes;
        protected Component[]   components;
	/** Size of the splitpane the last time laid out. */
	private int             lastSplitPaneSize;
	/** True if resetToPreferredSizes has been invoked. */
	private boolean         doReset;
	/** Axis, 0 for horizontal, or 1 for veritcal. */
	private int             axis;


        SplitPaneLayoutManager(int axis) {
	    this.axis = axis;
            components = new Component[3];
            components[0] = components[1] = components[2] = null;
            sizes = new int[3];
        }

	//
	// LayoutManager
	//

	/**
         * Does the actual layout.
         */
        public void layoutContainer(Container container) {
            Dimension   containerSize = container.getSize();

            // If the splitpane has a zero size then no op out of here.
            // If we execute this function now, we're going to cause ourselves
            // much grief.
            if (containerSize.height <= 0 || containerSize.width <= 0 ) {
		lastSplitPaneSize = 0;
                return;
            }

	    int         spDividerLocation = splitPane.getDividerLocation();
            Insets      insets = splitPane.getInsets();
	    int         availableSize = getAvailableSize(containerSize,
							 insets);
	    int         newSize = getSizeForPrimaryAxis(containerSize);
	    int         beginLocation = getDividerLocation(splitPane);
	    int         dOffset = getSizeForPrimaryAxis(insets, true);
	    Dimension   dSize = (components[2] == null) ? null :
		                 components[2].getPreferredSize();

	    if ((doReset && !dividerLocationIsSet) || spDividerLocation < 0) {
		resetToPreferredSizes(availableSize);
	    }
	    else if (lastSplitPaneSize <= 0 ||
		     availableSize == lastSplitPaneSize || !painted ||
		     (dSize != null &&
		      getSizeForPrimaryAxis(dSize) != sizes[2])) {
		if (dSize != null) {
		    sizes[2] = getSizeForPrimaryAxis(dSize);
		}
		else {
		    sizes[2] = 0;
		}
		setDividerLocation(spDividerLocation - dOffset, availableSize);
		dividerLocationIsSet = false;
	    }
	    else if (availableSize != lastSplitPaneSize) {
		distributeSpace(availableSize - lastSplitPaneSize, true);
	    }
	    doReset = false;
	    dividerLocationIsSet = false;
	    lastSplitPaneSize = availableSize;

            // Reset the bounds of each component
            int nextLocation = getInitialLocation(insets);
	    int counter = 0;

            while (counter < 3) {
                if (components[counter] != null &&
		    components[counter].isVisible()) {
                    setComponentToSize(components[counter], sizes[counter],
                                       nextLocation, insets, containerSize);
                    nextLocation += sizes[counter];
                }
                switch (counter) {
                case 0:
                    counter = 2;
                    break;
                case 2:
                    counter = 1;
                    break;
                case 1:
                    counter = 3;
                    break;
                }
            }
	    if (painted) {
		// This is tricky, there is never a good time for us
		// to push the value to the splitpane, painted appears to
		// the best time to do it. What is really needed is
		// notification that layout has completed.
		int      newLocation = getDividerLocation(splitPane);

		if (newLocation != (spDividerLocation - dOffset)) {
		    int  lastLocation = splitPane.getLastDividerLocation();

		    ignoreDividerLocationChange = true;
		    try {
			splitPane.setDividerLocation(newLocation);
			// This is not always needed, but is rather tricky
			// to determine when... The case this is needed for
			// is if the user sets the divider location to some
			// bogus value, say 0, and the actual value is 1, the
			// call to setDividerLocation(1) will preserve the
			// old value of 0, when we really want the divider
			// location value  before the call. This is needed for
			// the one touch buttons.
			splitPane.setLastDividerLocation(lastLocation);
		    } finally {
			ignoreDividerLocationChange = false;
		    }
		}
	    }
        }


        /**
         * Adds the component at place.  Place must be one of
         * JSplitPane.LEFT, RIGHT, TOP, BOTTOM, or null (for the
         * divider).
         */
        public void addLayoutComponent(String place, Component component) {
            boolean isValid = true;

            if(place != null) {
                if(place.equals(JSplitPane.DIVIDER)) {
                    /* Divider. */
                    components[2] = component;
                    sizes[2] = getSizeForPrimaryAxis(component.
						     getPreferredSize());
                } else if(place.equals(JSplitPane.LEFT) ||
                          place.equals(JSplitPane.TOP)) {
                    components[0] = component;
                    sizes[0] = 0;
                } else if(place.equals(JSplitPane.RIGHT) ||
                          place.equals(JSplitPane.BOTTOM)) {
                    components[1] = component;
                    sizes[1] = 0;
                } else if(!place.equals(
                                    SynthSplitPaneUI.NON_CONTINUOUS_DIVIDER))
                    isValid = false;
            } else {
                isValid = false;
            }
            if(!isValid)
                throw new IllegalArgumentException("cannot add to layout: " +
                    "unknown constraint: " +
                    place);
	    doReset = true;
        }


        /**
         * Returns the minimum size needed to contain the children.
         * The width is the sum of all the childrens min widths and
         * the height is the largest of the childrens minimum heights.
         */
        public Dimension minimumLayoutSize(Container container) {
            int         minPrimary = 0;
            int         minSecondary = 0;
            Insets      insets = splitPane.getInsets();

            for (int counter=0; counter<3; counter++) {
                if(components[counter] != null) {
                    Dimension   minSize = components[counter].getMinimumSize();
		    int         secSize = getSizeForSecondaryAxis(minSize);

                    minPrimary += getSizeForPrimaryAxis(minSize);
                    if(secSize > minSecondary)
                        minSecondary = secSize;
                }
            }
            if(insets != null) {
                minPrimary += getSizeForPrimaryAxis(insets, true) +
		              getSizeForPrimaryAxis(insets, false);
		minSecondary += getSizeForSecondaryAxis(insets, true) +
		              getSizeForSecondaryAxis(insets, false);
            }
	    if (axis == 0) {
		return new Dimension(minPrimary, minSecondary);
	    }
	    return new Dimension(minSecondary, minPrimary);
        }


        /**
         * Returns the preferred size needed to contain the children.
         * The width is the sum of all the childrens preferred widths and
         * the height is the largest of the childrens preferred heights.
         */
        public Dimension preferredLayoutSize(Container container) {
            int         prePrimary = 0;
            int         preSecondary = 0;
            Insets      insets = splitPane.getInsets();

            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] != null) {
		    Dimension   preSize = components[counter].
			                  getPreferredSize();
		    int         secSize = getSizeForSecondaryAxis(preSize);

                    prePrimary += getSizeForPrimaryAxis(preSize);
                    if(secSize > preSecondary)
                        preSecondary = secSize;
                }
            }
            if(insets != null) {
                prePrimary += getSizeForPrimaryAxis(insets, true) +
		              getSizeForPrimaryAxis(insets, false);
		preSecondary += getSizeForSecondaryAxis(insets, true) +
		              getSizeForSecondaryAxis(insets, false);
            }
	    if (axis == 0) {
		return new Dimension(prePrimary, preSecondary);
	    }
	    return new Dimension(preSecondary, prePrimary);
        }


        /**
         * Removes the specified component from our knowledge.
         */
        public void removeLayoutComponent(Component component) {
            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] == component) {
                    components[counter] = null;
                    sizes[counter] = 0;
		    doReset = true;
                }
            }
        }


        //
        // LayoutManager2
        //


        /**
         * Adds the specified component to the layout, using the specified
         * constraint object.
         * @param comp the component to be added
         * @param constraints  where/how the component is added to the layout.
         */
        public void addLayoutComponent(Component comp, Object constraints) {
            if ((constraints == null) || (constraints instanceof String)) {
                addLayoutComponent((String)constraints, comp);
            } else {
                throw new IllegalArgumentException("cannot add to layout: " +
                                                   "constraint must be a " +
                                                   "string (or null)");
            }
        }


        /**
         * Returns the alignment along the x axis.  This specifies how
         * the component would like to be aligned relative to other 
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentX(Container target) {
            return 0.0f;
        }


        /**
         * Returns the alignment along the y axis.  This specifies how
         * the component would like to be aligned relative to other 
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentY(Container target) {
            return 0.0f;
        }


        /**
         * Does nothing. If the developer really wants to change the
         * size of one of the views JSplitPane.resetToPreferredSizes should
         * be messaged.
         */
        public void invalidateLayout(Container c) {
        }


        /**
         * Returns the maximum layout size, which is Integer.MAX_VALUE
         * in both directions.
         */
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }


	//
	// New methods.
	//

        /**
         * Marks the receiver so that the next time this instance is
         * laid out it'll ask for the preferred sizes.
         */
        public void resetToPreferredSizes() {
	    doReset = true;
        }

        /**
         * Resets the size of the Component at the passed in location.
         */
        protected void resetSizeAt(int index) {
            sizes[index] = 0;
	    doReset = true;
        }


        /**
         * Sets the sizes to <code>newSizes</code>.
         */
        protected void setSizes(int[] newSizes) {
            System.arraycopy(newSizes, 0, sizes, 0, 3);
        }


        /**
         * Returns the sizes of the components.
         */
        protected int[] getSizes() {
            int[]         retSizes = new int[3];

            System.arraycopy(sizes, 0, retSizes, 0, 3);
            return retSizes;
        }


        /**
         * Returns the width of the passed in Components preferred size.
         */
        protected int getPreferredSizeOfComponent(Component c) {
	    return getSizeForPrimaryAxis(c.getPreferredSize());
        }


        /**
         * Returns the width of the passed in Components minimum size.
         */
        int getMinimumSizeOfComponent(Component c) {
	    return getSizeForPrimaryAxis(c.getMinimumSize());
        }


        /**
         * Returns the width of the passed in component.
         */
        protected int getSizeOfComponent(Component c) {
	    return getSizeForPrimaryAxis(c.getSize());
        }


        /**
         * Returns the available width based on the container size and
         * Insets.
         */
        protected int getAvailableSize(Dimension containerSize,
                                       Insets insets) {
            if(insets == null)
                return getSizeForPrimaryAxis(containerSize);
            return (getSizeForPrimaryAxis(containerSize) - 
		    (getSizeForPrimaryAxis(insets, true) +
		     getSizeForPrimaryAxis(insets, false)));
        }


        /**
         * Returns the left inset, unless the Insets are null in which case
         * 0 is returned.
         */
        protected int getInitialLocation(Insets insets) {
            if(insets != null)
                return getSizeForPrimaryAxis(insets, true);
            return 0;
        }


        /**
         * Sets the width of the component c to be size, placing its
         * x location at location, y to the insets.top and height
         * to the containersize.height less the top and bottom insets.
         */
        protected void setComponentToSize(Component c, int size,
                                          int location, Insets insets,
                                          Dimension containerSize) {
            if(insets != null) {
		if (axis == 0) {
		    c.setBounds(location, insets.top, size,
				containerSize.height -
				(insets.top + insets.bottom));
		}
		else {
		    c.setBounds(insets.left, location, containerSize.width -
				(insets.left + insets.right), size);
		}
            }
	    else {
                if (axis == 0) {
		    c.setBounds(location, 0, size, containerSize.height);
		}
		else {
		    c.setBounds(0, location, containerSize.width, size);
		}
            }
        }

	/**
	 * If the axis == 0, the width is returned, otherwise the height.
	 */
	int getSizeForPrimaryAxis(Dimension size) {
	    if (axis == 0) {
		return size.width;
	    }
	    return size.height;
	}

	/**
	 * If the axis == 0, the width is returned, otherwise the height.
	 */
	int getSizeForSecondaryAxis(Dimension size) {
	    if (axis == 0) {
		return size.height;
	    }
	    return size.width;
	}

	/**
	 * Returns a particular value of the inset identified by the
	 * axis and <code>isTop</code><p>
	 *   axis isTop
	 *    0    true    - left
	 *    0    false   - right
	 *    1    true    - top
	 *    1    false   - bottom
	 */
	int getSizeForPrimaryAxis(Insets insets, boolean isTop) {
	    if (axis == 0) {
		if (isTop) {
		    return insets.left;
		}
		return insets.right;
	    }
	    if (isTop) {
		return insets.top;
	    }
	    return insets.bottom;
	} 

	/**
	 * Returns a particular value of the inset identified by the
	 * axis and <code>isTop</code><p>
	 *   axis isTop
	 *    0    true    - left
	 *    0    false   - right
	 *    1    true    - top
	 *    1    false   - bottom
	 */
	int getSizeForSecondaryAxis(Insets insets, boolean isTop) {
	    if (axis == 0) {
		if (isTop) {
		    return insets.top;
		}
		return insets.bottom;
	    }
	    if (isTop) {
		return insets.left;
	    }
	    return insets.right;
	} 

        /**
         * Determines the components. This should be called whenever
         * a new instance of this is installed into an existing
         * SplitPane.
         */
        protected void updateComponents() {
            Component comp;

            comp = splitPane.getLeftComponent();
            if(components[0] != comp) {
                components[0] = comp;
                if(comp == null) {
                    sizes[0] = 0;
                } else {
                    sizes[0] = -1;
                }
            }

            comp = splitPane.getRightComponent();
            if(components[1] != comp) {
                components[1] = comp;
                if(comp == null) {
                    sizes[1] = 0;
                } else {
                    sizes[1] = -1;
                }
            }

            /* Find the divider. */
            Component[] children = splitPane.getComponents();
            Component   oldDivider = components[2];

            components[2] = null;
            for(int counter = children.length - 1; counter >= 0; counter--) {
                if(children[counter] != components[0] &&
                   children[counter] != components[1] &&
                   children[counter] != nonContinuousLayoutDivider) {
                    if(oldDivider != children[counter]) {
                        components[2] = children[counter];
                    } else {
                        components[2] = oldDivider;
                    }
                    break;
                }
            }
            if(components[2] == null) {
		sizes[2] = 0;
	    }
	    else {
		sizes[2] = getSizeForPrimaryAxis(components[2].getPreferredSize());
	    }
        }

	/**
	 * Resets the size of the first component to <code>leftSize</code>,
	 * and the right component to the remainder of the space.
	 */
	void setDividerLocation(int leftSize, int availableSize) {
	    boolean          lValid = (components[0] != null &&
				       components[0].isVisible());
	    boolean          rValid = (components[1] != null &&
				       components[1].isVisible());
	    boolean          dValid = (components[2] != null && 
				       components[2].isVisible());
	    int              max = availableSize;

	    if (dValid) {
		max -= sizes[2];
	    }
	    leftSize = Math.max(0, Math.min(leftSize, max));
	    if (lValid) {
		if (rValid) {
		    sizes[0] = leftSize;
		    sizes[1] = max - leftSize;
		}
		else {
		    sizes[0] = max;
		    sizes[1] = 0;
		}
	    }
	    else if (rValid) {
		sizes[1] = max;
		sizes[0] = 0;
	    }
	}

	/**
	 * Returns an array of the minimum sizes of the components.
	 */
	int[] getPreferredSizes() {
	    int[]         retValue = new int[3];

	    for (int counter = 0; counter < 3; counter++) {
		if (components[counter] != null &&
		    components[counter].isVisible()) {
		    retValue[counter] = getPreferredSizeOfComponent
			                (components[counter]);
		}
		else {
		    retValue[counter] = -1;
		}
	    }
	    return retValue;
	}

	/**
	 * Returns an array of the minimum sizes of the components.
	 */
	int[] getMinimumSizes() {
	    int[]         retValue = new int[3];

	    for (int counter = 0; counter < 2; counter++) {
		if (components[counter] != null &&
		    components[counter].isVisible()) {
		    retValue[counter] = getMinimumSizeOfComponent
			                (components[counter]);
		}
		else {
		    retValue[counter] = -1;
		}
	    }
	    retValue[2] = (components[2] != null) ?
 		getMinimumSizeOfComponent(components[2]) : -1;
	    return retValue;
	}

	/**
	 * Resets the components to their preferred sizes.
	 */
	void resetToPreferredSizes(int availableSize) {
	    // Set the sizes to the preferred sizes (if fits), otherwise
	    // set to min sizes and distribute any extra space.
	    int[]       testSizes = getPreferredSizes();
	    int         totalSize = 0;

	    for (int counter = 0; counter < 3; counter++) {
		if (testSizes[counter] != -1) {
		    totalSize += testSizes[counter];
		}
	    }
	    if (totalSize > availableSize) {
		testSizes = getMinimumSizes();

		totalSize = 0;
		for (int counter = 0; counter < 3; counter++) {
		    if (testSizes[counter] != -1) {
			totalSize += testSizes[counter];
		    }
		}
	    }
	    setSizes(testSizes);
	    distributeSpace(availableSize - totalSize, false);
	}

	/**
	 * Distributes <code>space</code> between the two components 
	 * (divider won't get any extra space) based on the weighting. This
	 * attempts to honor the min size of the components.
         *
         * @param keepHidden if true and one of the components is 0x0
         *                   it gets none of the extra space
	 */
	void distributeSpace(int space, boolean keepHidden) {
	    boolean          lValid = (components[0] != null &&
				       components[0].isVisible());
	    boolean          rValid = (components[1] != null &&
				       components[1].isVisible());

            if (keepHidden) {
                if (lValid && getSizeForPrimaryAxis(
                                 components[0].getSize()) == 0) {
                    lValid = false;
                    if (rValid && getSizeForPrimaryAxis(
                                     components[1].getSize()) == 0) {
                        // Both aren't valid, force them both to be valid
                        lValid = true;
                    }
                }
                else if (rValid && getSizeForPrimaryAxis(
                                   components[1].getSize()) == 0) {
                    rValid = false;
                }
            }
	    if (lValid && rValid) {
		double        weight = splitPane.getResizeWeight();
		int           lExtra = (int)(weight * (double)space);
		int           rExtra = (space - lExtra);

		sizes[0] += lExtra;
		sizes[1] += rExtra;

		int           lMin = getMinimumSizeOfComponent(components[0]);
		int           rMin = getMinimumSizeOfComponent(components[1]);
		boolean       lMinValid = (sizes[0] >= lMin);
		boolean       rMinValid = (sizes[1] >= rMin);

		if (!lMinValid && !rMinValid) {
		    if (sizes[0] < 0) {
			sizes[1] += sizes[0];
			sizes[0] = 0;
		    }
		    else if (sizes[1] < 0) {
			sizes[0] += sizes[1];
			sizes[1] = 0;
		    }
		}
		else if (!lMinValid) {
		    if (sizes[1] - (lMin - sizes[0]) < rMin) {
			// both below min, just make sure > 0
			if (sizes[0] < 0) {
			    sizes[1] += sizes[0];
			    sizes[0] = 0;
			}
		    }
		    else {
			sizes[1] -= (lMin - sizes[0]);
			sizes[0] = lMin;
		    }
		}
		else if (!rMinValid) {
		    if (sizes[0] - (rMin - sizes[1]) < lMin) {
			// both below min, just make sure > 0
			if (sizes[1] < 0) {
			    sizes[0] += sizes[1];
			    sizes[1] = 0;
			}
		    }
		    else {
			sizes[0] -= (rMin - sizes[1]);
			sizes[1] = rMin;
		    }
		}
		if (sizes[0] < 0) {
		    sizes[0] = 0;
		}
		if (sizes[1] < 0) {
		    sizes[1] = 0;
		}
	    }
	    else if (lValid) {
		sizes[0] = Math.max(0, sizes[0] + space);
	    }
	    else if (rValid) {
		sizes[1] = Math.max(0, sizes[1] + space);
	    }
	}
    }


    /**
     * Cursor used for HORIZONTAL_SPLIT splitpanes.
     */
    private static final Cursor horizontalCursor =
                            Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

    /**
     * Cursor used for VERTICAL_SPLIT splitpanes.
     */
    private static final Cursor verticalCursor =
                            Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);

    /**
     * Default cursor.
     */
    private static final Cursor defaultCursor =
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);



    // * @(#)BasicSplitPaneDivider.java	1.45 01/12/03
    private class Divider extends JComponent implements PropertyChangeListener{
        /**
         * Handles mouse dragging message to do the actual dragging.
         */
        protected DragController dragger;

        /**
         * Size of the divider.
         */
        protected int dividerSize = 0;

        /**
         * Divider that is used for noncontinuous layout mode.
         */
        protected Component hiddenDivider;

        /**
         * Handles mouse events from both this class, and the split pane.
         * Mouse events are handled for the splitpane since you want to be able
         * to drag when clicking on the border of the divider, which is not 
         * drawn by the divider.
         */
        protected MouseHandler mouseHandler;

        /**
         * Button for quickly toggling the left component.
         */
        protected SynthArrowButton leftButton;

        /**
         * Button for quickly toggling the right component.
         */
        protected SynthArrowButton rightButton;

        /**
         * True while the mouse is over the divider.
         */
        private boolean mouseOver;

        /**
         * Creates an instance of Divider. Registers this
         * instance for mouse events and mouse dragged events.
         */
        public Divider(JSplitPane splitPane) {
            setLayout(new DividerLayout());
            int orientation = splitPane.getOrientation();
            setCursor((orientation == JSplitPane.HORIZONTAL_SPLIT) ?
                      horizontalCursor : verticalCursor);

            mouseHandler = new MouseHandler();
            splitPane.addMouseListener(mouseHandler);
            splitPane.addMouseMotionListener(mouseHandler);
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            splitPane.addPropertyChangeListener(this);
            if (splitPane.isOneTouchExpandable()) {
                oneTouchExpandableChanged();
            }
        }

        public String getUIClassID() {
            return "SplitPaneDividerUI";
        }

        /**
         * Sets the size of the divider to <code>newSize</code>. That is
         * the width if the splitpane is <code>HORIZONTAL_SPLIT</code>, or
         * the height of <code>VERTICAL_SPLIT</code>.
         */
        public void setDividerSize(int newSize) {
            dividerSize = newSize;
        }


        /**
         * Returns the size of the divider, that is the width if the splitpane
         * is HORIZONTAL_SPLIT, or the height of VERTICAL_SPLIT.
         */
        public int getDividerSize() {
            return dividerSize;
        }

        /**
         * Returns dividerSize x dividerSize
         */
        public Dimension getPreferredSize() {
            // Ideally this would return the size from the layout manager,
            // but that could result in the layed out size being different from
            // the dividerSize, which may break developers as well as
            // SynthSplitPaneUI.
            if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                return new Dimension(getDividerSize(), 1);
            }
            return new Dimension(1, getDividerSize());
        }

        /**
         * Returns dividerSize x dividerSize
         */
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        /**
         * Returns true if the mouse is over the divider.
         */
        public boolean isMouseOver() {
            return mouseOver;
        }

        /**
         * Property change event, presumably from the JSplitPane, will message
         * updateOrientation if necessary.
         */
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getSource() == splitPane) {
                if (e.getPropertyName().equals(
                                       JSplitPane.ORIENTATION_PROPERTY)) {
                    orientation = splitPane.getOrientation();
                    setCursor((orientation == JSplitPane.HORIZONTAL_SPLIT) ?
                              horizontalCursor : verticalCursor);
                    if (leftButton != null) {
                        leftButton.setDirection(mapDirection(true));
                    }
                    if (rightButton != null) {
                        rightButton.setDirection(mapDirection(false));
                    }
                    revalidate();
                }
                else if (e.getPropertyName().equals(JSplitPane.
                                             ONE_TOUCH_EXPANDABLE_PROPERTY)) {
                    oneTouchExpandableChanged();
                }
            }
        }


        public void paintComponent(Graphics g) {
            SynthContext context = getContext(splitPane,
                                              Region.SPLIT_PANE_DIVIDER);
            Rectangle bounds = getBounds();
            bounds.x = bounds.y = 0;
            SynthLookAndFeel.updateSubregion(context, g, bounds);

            SynthPainter foreground = (SynthPainter)context.getStyle().
                                      get(context, "foreground");

            if (foreground != null) {
                foreground.paint(context, "foreground", g, 0, 0, getWidth(),
                                 getHeight());
            }
            context.dispose();
        }


        /**
         * Messaged when the oneTouchExpandable value of the JSplitPane the
         * receiver is contained in changes. Will create the
         * <code>leftButton</code> and <code>rightButton</code> if they
         * are null. invalidates the receiver as well.
         */
        protected void oneTouchExpandableChanged() {
            if (splitPane.isOneTouchExpandable() &&
                          leftButton == null && rightButton == null) {
                /* Create the left button and add an action listener to
                   expand/collapse it. */
                leftButton = createLeftOneTouchButton();
                leftButton.setDirection(mapDirection(true));
                if (leftButton != null) {
                    leftButton.addActionListener(new OneTouchActionHandler(
                                                     true));
                }


                /* Create the right button and add an action listener to
                   expand/collapse it. */
                rightButton = createRightOneTouchButton();
                if (rightButton != null) {
                    rightButton.setDirection(mapDirection(false));
                    rightButton.addActionListener(new OneTouchActionHandler
                                                  (false));
                }

                if (leftButton != null && rightButton != null) {
                    add(leftButton);
                    add(rightButton);
                }
            }
            revalidate();
        }


        private int mapDirection(boolean isLeft) {
            if (isLeft) {
                if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT){
                    return SwingConstants.WEST;
                }
                return SwingConstants.NORTH;
            }
            if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT){
                return SwingConstants.EAST;
            }
            return SwingConstants.SOUTH;
        }


        /**
         * Creates and return an instance of JButton that can be used to
         * collapse the left component in the split pane.
         */
        protected SynthArrowButton createLeftOneTouchButton() {
            SynthArrowButton b = new SynthArrowButton(SwingConstants.NORTH);

            b.setName("SplitPaneDivider.leftOneTouchButton");
            b.setMinimumSize(new Dimension(oneTouchSize, oneTouchSize));
            b.setCursor(defaultCursor);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setRequestFocusEnabled(false);
            return b;
        }


        /**
         * Creates and return an instance of JButton that can be used to
         * collapse the right component in the split pane.
         */
        protected SynthArrowButton createRightOneTouchButton() {
            SynthArrowButton b = new SynthArrowButton(SwingConstants.NORTH);

            b.setMinimumSize(new Dimension(oneTouchSize, oneTouchSize));
            b.setCursor(defaultCursor);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setRequestFocusEnabled(false);
            return b;
        }


        /**
         * MouseHandler is responsible for converting mouse events
         * (released, dragged...) into the appropriate DragController 
         * methods.
         * <p>
         */
        protected class MouseHandler extends MouseAdapter
                             implements MouseMotionListener {
            /**
             * Starts the dragging session by creating the appropriate instance
             * of DragController.
             */
            public void mousePressed(MouseEvent e) {
                if ((e.getSource() == Divider.this ||
                          e.getSource() == splitPane) &&
                          dragger == null && splitPane.isEnabled()) {
                    Component            newHiddenDivider = 
                                     getNonContinuousLayoutDivider();

                    if (hiddenDivider != newHiddenDivider) {
                        if (hiddenDivider != null) {
                            hiddenDivider.removeMouseListener(this);
                            hiddenDivider.removeMouseMotionListener(this);
                        }
                        hiddenDivider = newHiddenDivider;
                        if (hiddenDivider != null) {
                            hiddenDivider.addMouseMotionListener(this);
                            hiddenDivider.addMouseListener(this);
                        }
                    }
                    if (splitPane.getLeftComponent() != null &&
                             splitPane.getRightComponent() != null) {
                        if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                            dragger = new DragController(e);
                        }
                        else {
                            dragger = new VerticalDragController(e);
                        }
                        if (!dragger.isValid()) {
                            dragger = null;
                        }
                        else {
                            startDragging();
                            dragger.continueDrag(e);
                        }
                    }
                    e.consume();
                }
            }


            /**
             * If dragger is not null it is messaged with completeDrag.
             */
            public void mouseReleased(MouseEvent e) {
                if (dragger != null) {
                    if (e.getSource() == splitPane) {
                        dragger.completeDrag(e.getX(), e.getY());
                    }
                    else if (e.getSource() == Divider.this) {
                        Point   ourLoc = getLocation();

                        dragger.completeDrag(e.getX() + ourLoc.x,
                                             e.getY() + ourLoc.y);
                    }
                    else if (e.getSource() == hiddenDivider) {
                        Point   hDividerLoc = hiddenDivider.getLocation();
                        int     ourX = e.getX() + hDividerLoc.x;
                        int     ourY = e.getY() + hDividerLoc.y;

                        dragger.completeDrag(ourX, ourY);
                    }
                    dragger = null;
                    e.consume();
                }
            }


            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == Divider.this) {
                    mouseOver = true;
                    repaint();
                }
            }

            public void mouseExited(MouseEvent e) {
                if (e.getSource() == Divider.this) {
                    mouseOver = false;
                    repaint();
                }
            }

            //
            // MouseMotionListener
            //

            /**
             * If dragger is not null it is messaged with continueDrag.
             */
            public void mouseDragged(MouseEvent e) {
                if (dragger != null) {
                    if (e.getSource() == splitPane) {
                        dragger.continueDrag(e.getX(), e.getY());
                    }
                    else if (e.getSource() == Divider.this) {
                        Point   ourLoc = getLocation();
                    
                        dragger.continueDrag(e.getX() + ourLoc.x,
                                             e.getY() + ourLoc.y);
                    }
                    else if (e.getSource() == hiddenDivider) {
                        Point   hDividerLoc = hiddenDivider.getLocation();
                        int     ourX = e.getX() + hDividerLoc.x;
                        int     ourY = e.getY() + hDividerLoc.y;
                    
                        dragger.continueDrag(ourX, ourY);
                    }
                    e.consume();
                }
            }


            /**
             *  Resets the cursor based on the orientation.
             */
            public void mouseMoved(MouseEvent e) {
            }
        }


        /**
         * Handles the events during a dragging session for a
         * HORIZONTAL_SPLIT oriented split pane. This continually
         * messages <code>dragDividerTo</code> and then when done messages
         * <code>finishDraggingTo</code>. When an instance is created it should be
         * messaged with <code>isValid</code> to insure that dragging can happen
         * (dragging won't be allowed if the two views can not be resized).
         */
        protected class DragController {
            /**
             * Initial location of the divider.
             */
            int initialX;

            /**
             * Maximum and minimum positions to drag to.
             */
            int maxX, minX;

            /**
             * Initial location the mouse down happened at.
             */
            int offset;


            protected DragController(MouseEvent e) {
                JSplitPane  splitPane = getSplitPane();
                Component   leftC = splitPane.getLeftComponent();
                Component   rightC = splitPane.getRightComponent();

                initialX = getLocation().x;
                if (e.getSource() == Divider.this) {
                    offset = e.getX();
                }
                else { // splitPane
                    offset = e.getX() - initialX;
                }
                if (leftC == null || rightC == null || offset < -1 ||
                    offset >= getSize().width) {
                    // Don't allow dragging.
                    maxX = -1;
                }
                else {
                    Insets      insets = splitPane.getInsets();

                    if (leftC.isVisible()) {
                        minX = leftC.getMinimumSize().width;
                        if (insets != null) {
                            minX += insets.left;
                        }
                    }
                    else {
                        minX = 0;
                    }
                    if (rightC.isVisible()) {
                        int right = (insets != null) ? insets.right : 0;
                        maxX = Math.max(0, splitPane.getSize().width -
                                        (getSize().width + right) -
                                        rightC.getMinimumSize().width);
                    }
                    else {
                        int right = (insets != null) ? insets.right : 0;
                        maxX = Math.max(0, splitPane.getSize().width -
                                        (getSize().width + right));
                    }
                    if (maxX < minX) minX = maxX = 0;
                }
            }


            /**
             * Returns true if the dragging session is valid.
             */
            protected boolean isValid() {
                return (maxX > 0);
            }


            /**
             * Returns the new position to put the divider at based on
             * the passed in MouseEvent.
             */
            protected int positionForMouseEvent(MouseEvent e) {
                int newX = (e.getSource() == Divider.this) ?
                           (e.getX() + getLocation().x) : e.getX();

                newX = Math.min(maxX, Math.max(minX, newX - offset));
                return newX;
            }


            /**
             * Returns the x argument, since this is used for horizontal
             * splits.
             */
            protected int getNeededLocation(int x, int y) {
                int newX;

                newX = Math.min(maxX, Math.max(minX, x - offset));
                return newX;
            }


            protected void continueDrag(int newX, int newY) {
                dragDividerTo(getNeededLocation(newX, newY));
            }


            /**
             * Messages dragDividerTo with the new location for the mouse
             * event.
             */
            protected void continueDrag(MouseEvent e) {
                dragDividerTo(positionForMouseEvent(e));
            }


            protected void completeDrag(int x, int y) {
                finishDraggingTo(getNeededLocation(x, y));
            }


            /**
             * Messages finishDraggingTo with the new location for the mouse
             * event.
             */
            protected void completeDrag(MouseEvent e) {
                finishDraggingTo(positionForMouseEvent(e));
            }
        } // End of Divider.DragController


        /**
         * Handles the events during a dragging session for a
         * VERTICAL_SPLIT oriented split pane. This continually
         * messages <code>dragDividerTo</code> and then when done messages
         * <code>finishDraggingTo</code>. When an instance is created it should be
         * messaged with <code>isValid</code> to insure that dragging can happen
         * (dragging won't be allowed if the two views can not be resized).
         */
        protected class VerticalDragController extends DragController {
            /* DragControllers ivars are now in terms of y, not x. */
            protected VerticalDragController(MouseEvent e) {
                super(e);
                JSplitPane splitPane = getSplitPane();
                Component  leftC = splitPane.getLeftComponent();
                Component  rightC = splitPane.getRightComponent();

                initialX = getLocation().y;
                if (e.getSource() == Divider.this) {
                    offset = e.getY();
                }
                else {
                    offset = e.getY() - initialX;
                }
                if (leftC == null || rightC == null || offset < -1 ||
                    offset > getSize().height) {
                    // Don't allow dragging.
                    maxX = -1;
                }
                else {
                    Insets     insets = splitPane.getInsets();

                    if (leftC.isVisible()) {
                        minX = leftC.getMinimumSize().height;
                        if (insets != null) {
                            minX += insets.top;
                        }
                    }
                    else {
                        minX = 0;
                    }
                    if (rightC.isVisible()) {
                        int    bottom = (insets != null) ? insets.bottom : 0;

                        maxX = Math.max(0, splitPane.getSize().height -
                                        (getSize().height + bottom) -
                                        rightC.getMinimumSize().height);
                    }
                    else {
                        int    bottom = (insets != null) ? insets.bottom : 0;

                        maxX = Math.max(0, splitPane.getSize().height -
                                        (getSize().height + bottom));
                    }
                    if (maxX < minX) minX = maxX = 0;
                }
            }


            /**
             * Returns the y argument, since this is used for vertical
             * splits.
             */
            protected int getNeededLocation(int x, int y) {
                int newY;

                newY = Math.min(maxX, Math.max(minX, y - offset));
                return newY;
            }


            /**
             * Returns the new position to put the divider at based on
             * the passed in MouseEvent.
             */
            protected int positionForMouseEvent(MouseEvent e) {
                int newY = (e.getSource() == Divider.this) ?
		        (e.getY() + getLocation().y) : e.getY();

                newY = Math.min(maxX, Math.max(minX, newY - offset));
                return newY;
            }
        } // End of Dividier.VerticalDragController


        /**
         * Used to layout a <code>Divider</code>.
         * Layout for the divider
         * involves appropriately moving the left/right buttons around.
         * <p>
         */
        protected class DividerLayout implements LayoutManager {
            public void layoutContainer(Container c) {
                if (leftButton != null && rightButton != null &&
                                  c == Divider.this) {
                    if (splitPane.isOneTouchExpandable()) {
                        Insets insets = c.getInsets();

                        if (orientation == JSplitPane.VERTICAL_SPLIT) {
                            int extraX = (insets != null) ? insets.left : 0;
                            int blockSize = getHeight();

                            if (insets != null) {
                                blockSize -= (insets.top + insets.bottom);
                                blockSize = Math.max(blockSize, 0);
                            }
                            blockSize = Math.min(blockSize, oneTouchSize);

                            int y = (c.getSize().height - blockSize) / 2;

                            leftButton.setBounds(extraX + oneTouchOffset, y,
                                                 blockSize * 2, blockSize);
                            rightButton.setBounds(extraX + oneTouchOffset +
                                                  oneTouchSize * 2, y,
                                                  blockSize * 2, blockSize);
                        }
                        else {
                            int extraY = (insets != null) ? insets.top : 0;
                            int blockSize = getWidth();

                            if (insets != null) {
                                blockSize -= (insets.left + insets.right);
                                blockSize = Math.max(blockSize, 0);
                            }
                            blockSize = Math.min(blockSize, oneTouchSize);

                            int x = (c.getSize().width - blockSize) / 2;

                            leftButton.setBounds(x, extraY + oneTouchOffset,
                                                 blockSize, blockSize * 2);
                            rightButton.setBounds(x, extraY + oneTouchOffset+
                                                  oneTouchSize * 2,
                                                  blockSize, blockSize * 2);
                        }
                    }
                    else {
                        leftButton.setBounds(-5, -5, 1, 1);
                        rightButton.setBounds(-5, -5, 1, 1);
                    }
                }
            }

            public Dimension minimumLayoutSize(Container c) {
                // PENDING: resolve!
                // NOTE: This isn't really used, refer to
                // Divider.getPreferredSize for the reason.
                // I leave it in hopes of having this used at some point.
                if (c != Divider.this || splitPane == null) {
                    return new Dimension(0,0);
                }
                Dimension buttonMinSize = null;

                if (splitPane.isOneTouchExpandable() && leftButton != null) {
                    buttonMinSize = leftButton.getMinimumSize();
                }

                Insets insets = getInsets();
                int width = getDividerSize();
                int height = width;

                if (orientation == JSplitPane.VERTICAL_SPLIT) {
                    if (buttonMinSize != null) {
                        int size = buttonMinSize.height;
                        if (insets != null) {
                            size += insets.top + insets.bottom;
                        }
                        height = Math.max(height, size);
                    }
                    width = 1;
                }
                else {
                    if (buttonMinSize != null) {
                        int size = buttonMinSize.width;
                        if (insets != null) {
                            size += insets.left + insets.right;
                        }
                        width = Math.max(width, size);
                    }
                    height = 1;
                }
                return new Dimension(width, height);
            }


            public Dimension preferredLayoutSize(Container c) {
                return minimumLayoutSize(c);
            }


            public void removeLayoutComponent(Component c) {}

            public void addLayoutComponent(String string, Component c) {}
        } // End of class Divider.DividerLayout


        /**
         * Listeners installed on the one touch expandable buttons.
         */
        private class OneTouchActionHandler implements ActionListener {
            /** True indicates the resize should go the minimum (top or left)
             * vs false which indicates the resize should go to the maximum.
             */
            private boolean toMinimum;

            OneTouchActionHandler(boolean toMinimum) {
                this.toMinimum = toMinimum;
            }

            public void actionPerformed(ActionEvent e) {
                Insets  insets = splitPane.getInsets();
                int     lastLoc = splitPane.getLastDividerLocation();
                int     currentLoc = getDividerLocation(splitPane);
                int     newLoc;

                // We use the location from the UI directly, as the location
                // the JSplitPane itself maintains is not necessarly correct.
                if (toMinimum) {
                    if (orientation == JSplitPane.VERTICAL_SPLIT) {
                        if (currentLoc >= (splitPane.getHeight() -
                                           insets.bottom - getHeight()))
                            newLoc = lastLoc;
                        else
                            newLoc = insets.top;
                    }
                    else {
                        if (currentLoc >= (splitPane.getWidth() -
                                           insets.right - getWidth()))
                            newLoc = lastLoc;
                        else
                            newLoc = insets.left;
                    }
                }
                else {
                    if (orientation == JSplitPane.VERTICAL_SPLIT) {
                        if (currentLoc == insets.top)
                            newLoc = lastLoc;
                        else
                            newLoc = splitPane.getHeight() - getHeight() -
                                insets.top;
                    }
                    else {
                        if (currentLoc == insets.left)
                            newLoc = lastLoc;
                        else
                            newLoc = splitPane.getWidth() - getWidth() - 
			         insets.left;
                    }
                }
                if (currentLoc != newLoc) {
                    splitPane.setDividerLocation(newLoc);
                    // We do this in case the dividers notion of the location
                    // differs from the real location.
                    splitPane.setLastDividerLocation(currentLoc);
                }
            }
        } // End of class Divider.LeftActionListener
    }
}
