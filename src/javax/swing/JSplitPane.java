/*
 * @(#)JSplitPane.java	1.47 98/08/28
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

import java.awt.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * JSplitPane is used to divide two (and only two) Components. The two
 * Components are graphically divided based on the look and feel
 * implementation, and the two Components can then be interactively 
 * resized by the user.
 * <p>
 * The two Components can be aligned left to right using
 * <code>JSplitPane.HORIZONTAL_SPLIT</code>, or top to bottom using 
 * <code>JSplitPane.VERTICAL_SPLIT</code>.
 * The preferred way to change the size of the Components is to invoke
 * <code>setDividerLocation</code> where <code>location</code> is either
 * the new x or y position, depending on the orientation of the
 * JSplitPane. If one component changes, JSplitPane also attempts to 
 * reposition the other component.
 * <p>
 * To resize the Components to their preferred sizes invoke
 * <code>resetToPreferredSizes</code>.
 * <p>
 * When the user is resizing the Components the minimum size of the
 * Components is used to determine the maximum/minimum position the
 * Components can be set to. So that if the minimum size of the two
 * components is greater than the size of the splitpane the divider
 * will not allow you to resize it. To alter the minimum size of a
 * JComponent, see {@link JComponent#setMinimumSize}.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JSplitPane">JSplitPane</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see #setDividerLocation
 * @see #resetToPreferredSizes
 *
 * @version 1.47 08/28/98
 * @author Scott Violet
 */
public class JSplitPane extends JComponent implements Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "SplitPaneUI";

    /**
     * Vertical split indicates the Components are split along the
     * y axis, eg the two Components will be split one on top of the other.
     */
    public final static int VERTICAL_SPLIT = 0;

    /**
     * Horizontal split indicates the Components are split along the
     * x axis, eg the two Components will be split one to the left of the
     * other.
     */
    public final static int HORIZONTAL_SPLIT = 1;

    /**
     * Used to add a Component to the left of the other Component.
     */
    public final static String LEFT = "left";

    /**
     * Used to add a Component to the right of the other Component.
     */
    public final static String RIGHT = "right";

    /**
     * Used to add a Component above the other Component.
     */
    public final static String TOP = "top";

    /**
     * Used to add a Component below the other Component.
     */
    public final static String BOTTOM = "bottom";

    /**
     * Used to add a Component that will represent the divider.
     */
    public final static String DIVIDER = "divider";

    /**
     * Bound property name for orientation (horizontal or vertical).
     */
    public final static String ORIENTATION_PROPERTY = "orientation";

    /**
     * Bound property name for continuousLayout.
     */
    public final static String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";

    /**
     * Bound property name for border.
     */
    public final static String DIVIDER_SIZE_PROPERTY = "dividerSize";

    /**
     * Bound property for oneTouchExpandable.
     */
    public final static String ONE_TOUCH_EXPANDABLE_PROPERTY = 
                               "oneTouchExpandable";

    /**
     * Bound property for lastLocation.
     */
    public final static String LAST_DIVIDER_LOCATION_PROPERTY =
                               "lastDividerLocation";

    /**
     * How the views are split.
     */
    protected int orientation;

    /**
     * Whether or not the views are continuously redisplayed while
     * resizing.
     */
    protected boolean continuousLayout;

    /**
     * The left or top component.
     */
    protected Component leftComponent;

    /**
     * The right or bottom component.
     */
    protected Component rightComponent;

    /**
     * Size of the divider.
     */
    protected int dividerSize;

    /**
     * Is a little widget provided to quickly expand/collapse the
     * split pane?
     */
    protected boolean oneTouchExpandable;

    /**
     * Previous location of the split pane.
     */
    protected int lastDividerLocation;

    /**
     * Returns a new JSplitPane configured to arrange the child
     * components side-by-side horizontally with no continuous 
     * layout, using two buttons for the compoents.
     */
    public JSplitPane() {
        this(JSplitPane.HORIZONTAL_SPLIT, false, new JButton("left button"),
             new JButton("right button"));
    }

    /**
     * Returns a new JSplitPane configured with the specified orientation
     * and no continuous layout.
     *
     * @param newOrientation an int specifying the horizontal or vertical
     *        orientation
     */
    public JSplitPane(int newOrientation) {
        this(newOrientation, false);
    }

    /**
     * Returns a new JSplitPane with the specified orientation and
     * redrawing style.
     *
     * @param newOrientation an int specifying the horizontal or vertical
     *        orientation
     * @param newContinuousLayout  a boolean, true for the components to 
     *        redraw continuously as the divider changes position, false
     *        to wait until the divider position stops changing to redraw
     */
    public JSplitPane(int newOrientation, boolean newContinuousLayout) {
        this(newOrientation, newContinuousLayout, null, null);
    }

    /**
     * Returns a new JSplitPane with the specified orientation and
     * with the specifiied components that does not do continuous
     * redrawing.
     *
     * @param newOrientation an int specifying the horizontal or vertical
     *        orientation
     * @param newContinuousLayout  a boolean, true for the components to 
     *        redraw continuously as the divider changes position, false
     *        to wait until the divider position stops changing to redraw
     * @param newLeftComponent the Component that will appear on the left
     *        of a horizontally-split pane, or at the top of a
     *        vertically-split pane.
     */
    public JSplitPane(int newOrientation,
                      Component newLeftComponent, Component newRightComponent){
        this(newOrientation, false, newLeftComponent, newRightComponent);
    }

    /**
     * Returns a new JSplitPane with the specified orientation and
     * redrawing style, and with the specifiied components.
     *
     * @param newOrientation an int specifying the horizontal or vertical
     *        orientation
     * @param newContinuousLayout  a boolean, true for the components to 
     *        redraw continuously as the divider changes position, false
     *        to wait until the divider position stops changing to redraw
     * @param newLeftComponent the Component that will appear on the left
     *        of a horizontally-split pane, or at the top of a
     *        vertically-split pane.
     */
    public JSplitPane(int newOrientation, boolean newContinuousLayout,
                      Component newLeftComponent, Component newRightComponent){
        super();

        setLayout(null);
        orientation = newOrientation;
        if (orientation != HORIZONTAL_SPLIT && orientation != VERTICAL_SPLIT)
            throw new IllegalArgumentException("cannot create JSplitPane, " +
                                               "orientation must be one of " +
                                               "JSplitPane.HORIZONTAL_SPLIT " +
                                               "or JSplitPane.VERTICAL_SPLIT");
        continuousLayout = newContinuousLayout;
        if (newLeftComponent != null)
            setLeftComponent(newLeftComponent);
        if (newRightComponent != null)
            setRightComponent(newRightComponent);
        updateUI();

    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the SplitPaneUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(SplitPaneUI ui) {
        if ((SplitPaneUI)this.ui != ui) {
            super.setUI(ui);
            revalidate();
        }
    }

    /**
     * Returns the SplitPaneUI that is providing the current look and 
     * feel. 
     *
     * @return the SplitPaneUI object that renders this component
     * @beaninfo
     *       expert: true
     *  description: The L&F object that renders this component.
     */
    public SplitPaneUI getUI() {
        return (SplitPaneUI)ui;
    }

    /**
     * Notification from the UIManager that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * UIManager.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((SplitPaneUI)UIManager.getUI(this));
        revalidate();
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "SplitPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *       expert: true
     *  description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Sets the size of the divider.
     *
     * @param newSize an int giving the size of the divider in pixels
     * @beaninfo
     *        bound: true
     *  description: The size of the divider.
     */
    public void setDividerSize(int newSize) {
        int           oldSize = dividerSize;

        if (oldSize != newSize) {
            dividerSize = newSize;
            firePropertyChange(DIVIDER_SIZE_PROPERTY, oldSize, newSize);
        }
    }

    /**
     * Returns the size of the divider.
     *
     * @return an int giving the size of the divider in pixels
     */
    public int getDividerSize() {
        return dividerSize;
    }

    /**
     * Sets the component to the left (or above) the divider.
     *
     * @param comp the Component to display in that position
     */
    public void setLeftComponent(Component comp) {
        if (comp == null) {
            if (leftComponent != null) {
                remove(leftComponent);
                leftComponent = null;
            }
        } else {
            add(comp, JSplitPane.LEFT);
        }
    }

    /**
     * Returns the component to the left (or above) the divider.
     *
     * @return the Component displayed in that position
     * @beaninfo
     *    preferred: true
     *  description: The component to the left (or above) the divider.
     */
    public Component getLeftComponent() {
        return leftComponent;
    }

    /**
     * Sets the component above, or to the left of the divider.
     *
     * @param comp the Component to display in that position
     * @beaninfo
     *  description: The component above, or to the left of the divider.
     */
    public void setTopComponent(Component comp) {
        setLeftComponent(comp);
    }

    /**
     * Returns the component above, or to the left of the divider.
     *
     * @return the Component displayed in that position
     */
    public Component getTopComponent() {
        return leftComponent;
    }

    /**
     * Sets the component to the right (or below) the divider.
     *
     * @param comp the Component to display in that position
     * @beaninfo
     *    preferred: true
     *  description: The component to the right (or below) the divider.
     */
    public void setRightComponent(Component comp) {
        if (comp == null) {
            if (rightComponent != null) {
                remove(rightComponent);
                rightComponent = null;
            }
        } else {
            add(comp, JSplitPane.RIGHT);
        }
    }

    /**
     * Returns the component to the right (or below) the divider.
     *
     * @return the Component displayed in that position
     */
    public Component getRightComponent() {
        return rightComponent;
    }

    /**
     * Sets the component below, or to the right of the divider.
     *
     * @param comp the Component to display in that position
     * @beaninfo
     *  description: The component below, or to the right of the divider.
     */
    public void setBottomComponent(Component comp) {
        setRightComponent(comp);
    }

    /**
     * Returns the component below, or to the right of the divider.
     *
     * @return the Component displayed in that position
     */
    public Component getBottomComponent() {
        return rightComponent;
    }

    /**
     * Determines whether the JSplitPane provides a UI widget
     * on the divider to quickly expand/collapse the divider.
     *
     * @param newValue a boolean, where true means to provide a
     *        collapse/expand widget
     * @beaninfo
     *        bound: true
     *  description: UI widget on the divider to quickly 
     *               expand/collapse the divider.
     */
    public void setOneTouchExpandable(boolean newValue) {
        boolean           oldValue = oneTouchExpandable;

        oneTouchExpandable = newValue;
        firePropertyChange(ONE_TOUCH_EXPANDABLE_PROPERTY, oldValue, newValue);
        repaint();
    }

    /**
     * Returns true if the pane provides a UI widget to collapse/expand
     * the divider.
     *
     * @return true if the split pane provides a collapse/expand widget
     */
    public boolean isOneTouchExpandable() {
        return oneTouchExpandable;
    }

    /**
     * Sets the last location the divider was at to
     * <code>newLastLocation</code>.
     *
     * @param newLastLocation an int specifying the last divider location
     *        in pixels, from the left (or upper) edge of the pane to the 
     *        left (or upper) edge of the divider
     * @beaninfo
     *        bound: true
     *  description: The last location the divider was at.
     */
    public void setLastDividerLocation(int newLastLocation) {
        int               oldLocation = lastDividerLocation;

        lastDividerLocation = newLastLocation;
        firePropertyChange(LAST_DIVIDER_LOCATION_PROPERTY, oldLocation,
                           newLastLocation);
    }
    
    /**
     * Returns the last location the divider was at.
     *
     * @return an int specifying the last divider location as a count
     *       of pixels from the left (or upper) edge of the pane to the 
     *       left (or upper) edge of the divider
     */
    public int getLastDividerLocation() {
        return lastDividerLocation;
    }

    /**
     * Sets the orientation, or how the splitter is divided. The options
     * are:<ul>
     * <li>JSplitPane.VERTICAL_SPLIT  (above/below orientation of components)
     * <li>JSplitPane.HORIZONTAL_SPLIT  (left/right orientation of components)
     * </ul>
     *
     * @param orientation an int specifying the orientation
     * @beaninfo
     *        bound: true
     *  description: The orientation, or how the splitter is divided.
     */
    public void setOrientation(int orientation) {
        if (orientation != VERTICAL_SPLIT && orientation != HORIZONTAL_SPLIT) {
           throw new IllegalArgumentException("JSplitPane: orientation must " +
                                              "be one of " +
                                              "JSplitPane.VERTICAL_SPLIT or " +
                                              "JSplitPane.HORIZONTAL_SPLIT");
        }

        int           oldOrientation = this.orientation;

        this.orientation = orientation;
        firePropertyChange(ORIENTATION_PROPERTY, oldOrientation, orientation);
    }

    /**
     * Returns the orientation.
     * 
     * @return an int giving the orientation
     * @see #setOrientation
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Sets whether or not the child components are continuously
     * redisplayed and layed out during user intervention.
     *
     * @param newContinuousLayout  a boolean, true if the components
     *        are continuously redrawn as the divider changes position
     * @beaninfo
     *        bound: true
     *  description: Whether or not the child components are
     *               continuously redisplayed and layed out during
     *               user intervention.
     */
    public void setContinuousLayout(boolean newContinuousLayout) {
        boolean           oldCD = continuousLayout;

        continuousLayout = newContinuousLayout;
        firePropertyChange(CONTINUOUS_LAYOUT_PROPERTY, oldCD,
                           newContinuousLayout);
    }

    /**
     * Returns true if the child comopnents are continuously redisplayed and
     * layed out during user intervention.
     *
     * @return true if the components are continuously redrawn as the
     *         divider changes position
     */
    public boolean isContinuousLayout() {
        return continuousLayout;
    }

    /**
     * Messaged to relayout the JSplitPane based on the preferred size
     * of the children components.
     */
    public void resetToPreferredSizes() {
        SplitPaneUI         ui = getUI();

        if (ui != null) {
            ui.resetToPreferredSizes(this);
        }
    }

    /**
     * Sets the divider location as a percentage of the JSplitPane's size.
     *
     * @param proportionalLocation  a double-precision floating point value
     *        that specifies a percentage, from zero (top/left) to 1.0
     *        (bottom/right)
     * @exception IllegalArgumentException if the specified location is < 0
     *            or > 1.0
     * @beaninfo
     *  description: The location of the divider.
     */
    public void setDividerLocation(double proportionalLocation) {
        if (proportionalLocation < 0.0 || 
           proportionalLocation > 1.0) {
            throw new IllegalArgumentException("proportional location must " +
                                               "be between 0.0 and 1.0.");
        }
        if (getOrientation() == VERTICAL_SPLIT) {
            setDividerLocation((int)((double)(getHeight() - getDividerSize()) *
                                     proportionalLocation));
        } else {
            setDividerLocation((int)((double)(getWidth() - getDividerSize()) *
                                     proportionalLocation));
        }
    }

    /**
     * Sets the location of the divider. This is passed off to the 
     * look and feel implementation.
     *
     * @param location an int specifying a UI-specific value (typically a 
     *        pixel count)
     */
    public void setDividerLocation(int location) {
        SplitPaneUI         ui = getUI();

        if (ui != null) {
            ui.setDividerLocation(this, location);
        }
    }

    /**
     * Returns the location of the divider from the look and feel
     * implementation.
     *
     * @return an int specifying a UI-specific value (typically a 
     *         pixel count)
     */
    public int getDividerLocation() {
        SplitPaneUI         ui = getUI();

        if (ui != null) {
            return ui.getDividerLocation(this);
        }
        return -1;
    }

    /**
     * Returns the minimum location of the divider from the look and feel
     * implementation.
     *
     * @return an int specifying a UI-specific value for the minimum
     *         location (typically a pixel count)
     * @beaninfo
     *  description: The minimum location of the divider from the L&F.
     */
    public int getMinimumDividerLocation() {
        SplitPaneUI         ui = getUI();

        if (ui != null) {
            return ui.getMinimumDividerLocation(this);
        }
        return -1;
    }

    /**
     * Returns the maximum location of the divider from the look and feel
     * implementation.
     *
     * @return an int specifying a UI-specific value for the maximum
     *         location (typically a pixel count)
     */
    public int getMaximumDividerLocation() {
        SplitPaneUI         ui = getUI();

        if (ui != null) {
            return ui.getMaximumDividerLocation(this);
        }
        return -1;
    }

    /**
     * Removes the child component, <code>component</code> from the
     * pane. Resets the leftComponent or rightComponent instance
     * variable, as necessary.
     * 
     * @param component the Component to remove
     */
    public void remove(Component component) {
        if (component == leftComponent) {
            leftComponent = null;
        } else if (component == rightComponent) {
            rightComponent = null;
        }
        super.remove(component);

        // Update the JSplitPane on the screen
        revalidate();
        repaint();
    }

    /**
     * Removes the Component at the specified index. Updates the
     * leftComponent and rightComponent instance variables
     * as necessary, and then messages super.
     *
     * @param index an int specifying the component to remove, where
     *        1 specifies the left/top component and 2 specifies the 
     *        bottom/right component
     */
    public void remove(int index) {
        Component    comp = getComponent(index);

        if (comp == leftComponent) {
            leftComponent = null;
        } else if (comp == rightComponent) {
            rightComponent = null;
        }
        super.remove(index);

        // Update the JSplitPane on the screen
        revalidate();
        repaint();
    }

    /**
     * Removes all the child components from the receiver. Resets the
     * leftComonent and rightComponent instance variables.
     */
    public void removeAll() {
        leftComponent = rightComponent = null;
        super.removeAll();

        // Update the JSplitPane on the screen
        revalidate();
        repaint();
    }

    /**
     * If <code>constraints</code> identifies the left/top or
     * right/bottom child component, and a component with that identifier
     * was previously added, it will be removed and then <code>comp</code>
     * will be added in its place. If <code>constraints</code> is not
     * one of the known identifers the layout manager may throw an
     * IllegalArgumentException.
     * <p>
     * The possible constraints objects (Strings) are:<ul>
     * <li>JSplitPane.TOP
     * <li>JSplitPane.LEFT
     * <li>JSplitPane.BOTTOM
     * <li>JSplitPane.RIGHT
     * </ul>
     * If the constraints object is null, the component is added in the
     * first available position (left/top if open, else right/bottom).
     * 
     * @param comp        the component to add
     * @param constraints an Object specifying the layout constraints 
     *                    (position) for this component
     * @param index       an int specifying the index in the container's
     *                    list.
     * @exception IllegalArgumentException thrown if the constraints object
     *            does not match an existing component
     * @see java.awt.Container#addImpl(Component, Object, int)
     */
    protected void addImpl(Component comp, Object constraints, int index) 
    {
        Component             toRemove;

        if (constraints != null && !(constraints instanceof String)) {
            throw new IllegalArgumentException("cannot add to layout: " +
                                               "constraint must be a string " +
                                               "(or null)");
        }

        /* If the constraints are null and the left/right component is
           invalid, add it at the left/right component. */
        if (constraints == null) {
            if (getLeftComponent() == null) {
                constraints = JSplitPane.LEFT;
            } else if (getRightComponent() == null) {
                constraints = JSplitPane.RIGHT;
            }
        }
            
        /* Find the Component that already exists and remove it. */
        if (constraints != null && (constraints.equals(JSplitPane.LEFT) ||
                                   constraints.equals(JSplitPane.TOP))) {
            toRemove = getLeftComponent();
            if (toRemove != null) {
                remove(toRemove);
            }
            leftComponent = comp;
            index = -1;
        } else if (constraints != null &&
                   (constraints.equals(JSplitPane.RIGHT) ||
                    constraints.equals(JSplitPane.BOTTOM))) {
            toRemove = getRightComponent();
            if (toRemove != null) {
                remove(toRemove);
            }
            rightComponent = comp;
            index = -1;
        } else if (constraints != null &&
                constraints.equals(JSplitPane.DIVIDER)) {
            index = -1;
        }
        /* LayoutManager should raise for else condition here. */

        super.addImpl(comp, constraints, index);

        // Update the JSplitPane on the screen
        revalidate();
        repaint();
    }

    /**
     * Subclassed to message the UI with finishedPaintingChildren after
     * super has been messaged, as well as painting the border.
     *
     * @param g the Graphics context within which to paint
     */
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        SplitPaneUI        ui = getUI();

        if (ui != null) {
            Graphics           tempG = SwingGraphics.createSwingGraphics(g);
            ui.finishedPaintingChildren(this, tempG);
            tempG.dispose();
        }
    }

    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if ((ui != null) && (getUIClassID().equals(uiClassID))) {
            ui.installUI(this);
        }
    }


    /**
     * Returns a string representation of this JSplitPane. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JSplitPane.
     */
    protected String paramString() {
        String orientationString = (orientation == HORIZONTAL_SPLIT ?
                                    "HORIZONTAL_SPLIT" : "VERTICAL_SPLIT");
        String continuousLayoutString = (continuousLayout ?
                                         "true" : "false");
        String oneTouchExpandableString = (oneTouchExpandable ?
                                           "true" : "false");

        return super.paramString() +
        ",continuousLayout=" + continuousLayoutString +
        ",dividerSize=" + dividerSize +
        ",lastDividerLocation=" + lastDividerLocation +
        ",oneTouchExpandable=" + oneTouchExpandableString +
        ",orientation=" + orientationString;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this Label.
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJSplitPane();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJSplitPane extends AccessibleJComponent 
        implements AccessibleValue {

        /**
         * Get the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state 
         * of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            // FIXME: [[[WDW - Should also add BUSY if this implements
            // Adjustable at some point.  If this happens, we probably
            // should also add actions.]]]
            if (getOrientation() == VERTICAL_SPLIT) {
                states.add(AccessibleState.VERTICAL);
            } else {
                states.add(AccessibleState.HORIZONTAL);
            }
            return states;
        }
    
        /**
         * Get the AccessibleValue associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Get the accessible value of this object.
         *
         * @return a localized String describing the value of this object
         */
        public Number getCurrentAccessibleValue() {
            return new Integer(getDividerLocation());
        }
    
        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set.
         */
        public boolean setCurrentAccessibleValue(Number n) {
            if (n instanceof Integer) {
                setDividerLocation(n.intValue());
                return true;
            } else {
                return false;
            }
        }
    
        /**
         * Get the minimum accessible value of this object.
         *
         * @return The minimum value of this object.
         */
        public Number getMinimumAccessibleValue() {
            return new Integer(getUI().getMinimumDividerLocation(
                                                        JSplitPane.this));
        }
    
        /**
         * Get the maximum accessible value of this object.
         *
         * @return The maximum value of this object.
         */
        public Number getMaximumAccessibleValue() {
            return new Integer(getUI().getMaximumDividerLocation(
                                                        JSplitPane.this));
        }
    
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of 
         * the object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SPLIT_PANE;
        }
    } // inner class AccessibleJSplitPane
}
