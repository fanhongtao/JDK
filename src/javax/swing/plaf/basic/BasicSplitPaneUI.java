/*
 * @(#)BasicSplitPaneUI.java	1.40 98/08/26
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


package javax.swing.plaf.basic;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.peer.ComponentPeer;
import java.beans.*;
import java.util.*;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.ComponentUI;


/**
 * A Basic L&F implementation of the SplitPaneUI.
 *
 * @version 1.40 08/26/98
 * @author Scott Violet
 * @author Steve Wilson
 * @author Ralph Kar
 */
public class BasicSplitPaneUI extends SplitPaneUI
{
    /**
     * The divider used for non-continuous layout is added to the split pane
     * with this object.
     */
    protected static final String NON_CONTINUOUS_DIVIDER =
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
     * LayoutManager that is created and placed into the split pane.
     */
    protected BasicHorizontalLayoutManager layoutManager;


    /**
     * Instance of the divider for this JSplitPane.
     */
    protected BasicSplitPaneDivider divider;


    /**
     * Instance of the PropertyChangeListener for this JSplitPane.
     */
    protected PropertyChangeListener propertyChangeListener;


    /**
     * Instance of the FocusListener for this JSplitPane.
     */
    protected FocusListener focusListener;


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


    /**
     * The keystrokes that the JSplitPane is supposed to handle.
     */
    protected KeyStroke upKey;
    protected KeyStroke downKey;
    protected KeyStroke leftKey;
    protected KeyStroke rightKey;
    protected KeyStroke homeKey;
    protected KeyStroke endKey;
    protected KeyStroke dividerResizeToggleKey;


    /**
     * The handlers that are handling the keystrokes for keyboard navigation.
     */
    protected ActionListener keyboardUpLeftListener;
    protected ActionListener keyboardDownRightListener;
    protected ActionListener keyboardHomeListener;
    protected ActionListener keyboardEndListener;
    protected ActionListener keyboardResizeToggleListener;


    // Private data of the instance
    private int         orientation;
    private int         lastDragLocation;
    private boolean     continuousLayout;
    private boolean     dividerKeyboardResize;
    private boolean     dividerLocationIsSet;  // needed for tracking
                                               // the first occurence of
                                               // setDividerLocation()


    /**
     * Creates a new BasicSplitPaneUI instance
     */
    public static ComponentUI createUI(JComponent x) {
        return new BasicSplitPaneUI();
    }


    /**
     * Installs the UI.
     */
    public void installUI(JComponent c) {
        splitPane = (JSplitPane) c;
        dividerLocationIsSet = false;
        dividerKeyboardResize = false;
        installDefaults();
        installListeners();
        installKeyboardActions();
        setLastDragLocation(-1);
        splitPane.doLayout();
    }


    /**
     * Installs the UI defaults.
     */
    protected void installDefaults(){ 
        LookAndFeel.installBorder(splitPane, "SplitPane.border");

        if (divider == null) divider = createDefaultDivider();
        divider.setBasicSplitPaneUI(this);

        splitPane.setDividerSize(((Integer) (UIManager.get(
            "SplitPane.dividerSize"))).intValue());

        divider.setDividerSize(splitPane.getDividerSize());
        splitPane.add(divider, JSplitPane.DIVIDER);

        setOrientation(splitPane.getOrientation());
        setContinuousLayout(splitPane.isContinuousLayout());

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

        keyboardUpLeftListener = createKeyboardUpLeftListener();
        keyboardDownRightListener = createKeyboardDownRightListener();
        keyboardHomeListener = createKeyboardHomeListener();
        keyboardEndListener = createKeyboardEndListener();
        keyboardResizeToggleListener = createKeyboardResizeToggleListener();
    }


    /**
     * Installs the keyboard actions for the UI.
     */
    protected void installKeyboardActions() {
        upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        splitPane.registerKeyboardAction(
                        keyboardUpLeftListener,
                        upKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        downKey = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        splitPane.registerKeyboardAction(
                        keyboardDownRightListener,
                        downKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        leftKey = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        splitPane.registerKeyboardAction(
                        keyboardUpLeftListener,
                        leftKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        rightKey = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        splitPane.registerKeyboardAction(
                        keyboardDownRightListener,
                        rightKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        homeKey = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0);
        splitPane.registerKeyboardAction(
                        keyboardHomeListener,
                        homeKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        endKey = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0);
        splitPane.registerKeyboardAction(
                        keyboardEndListener,
                        endKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dividerResizeToggleKey = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0);
        splitPane.registerKeyboardAction(
                        keyboardResizeToggleListener,
                        dividerResizeToggleKey,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    /**
     * Uninstalls the UI.
     */
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
        dividerLocationIsSet = false;
        dividerKeyboardResize = false;
        splitPane = null;
    }


    /**
     * Uninstalls the UI defaults.
     */
    protected void uninstallDefaults() {
        if(splitPane.getLayout() == layoutManager) {
            splitPane.setLayout(null);
        }

        if(nonContinuousLayoutDivider != null) {
            splitPane.remove(nonContinuousLayoutDivider);
        }

        LookAndFeel.uninstallBorder(splitPane);

        splitPane.remove(divider);
        divider.setBasicSplitPaneUI(null);
        layoutManager = null;
        divider = null;
        nonContinuousLayoutDivider = null;

        setNonContinuousLayoutDivider(null);
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

        keyboardUpLeftListener = null;
        keyboardDownRightListener = null;
        keyboardHomeListener = null;
        keyboardEndListener = null;
        keyboardResizeToggleListener = null;
    }


    /**
     * Uninstalls the keyboard actions for the UI.
     */
    protected void uninstallKeyboardActions() {
        splitPane.unregisterKeyboardAction(upKey);
        splitPane.unregisterKeyboardAction(downKey);
        splitPane.unregisterKeyboardAction(leftKey);
        splitPane.unregisterKeyboardAction(rightKey);
        splitPane.unregisterKeyboardAction(homeKey);
        splitPane.unregisterKeyboardAction(endKey);
        splitPane.unregisterKeyboardAction(dividerResizeToggleKey);
        upKey = null;
        downKey = null;
        leftKey = null;
        rightKey = null;
        homeKey = null;
        endKey = null;
        dividerResizeToggleKey = null;
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
     * Creates a ActionListener for the JSplitPane UI that listens for
     * specific key presses.
     */
    protected ActionListener createKeyboardUpLeftListener() {
        return new KeyboardUpLeftHandler();
    }


    /**
     * Creates a ActionListener for the JSplitPane UI that listens for
     * specific key presses.
     */
    protected ActionListener createKeyboardDownRightListener() {
        return new KeyboardDownRightHandler();
    }


    /**
     * Creates a ActionListener for the JSplitPane UI that listens for
     * specific key presses.
     */
    protected ActionListener createKeyboardHomeListener() {
        return new KeyboardHomeHandler();
    }


    /**
     * Creates a ActionListener for the JSplitPane UI that listens for
     * specific key presses.
     */
    protected ActionListener createKeyboardEndListener() {
        return new KeyboardEndHandler();
    }


    /**
     * Creates a ActionListener for the JSplitPane UI that listens for
     * specific key presses.
     */
    protected ActionListener createKeyboardResizeToggleListener() {
        return new KeyboardResizeToggleHandler();
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
     * Implementation of the PropertyChangeListener
     * that the JSplitPane UI uses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class PropertyHandler implements PropertyChangeListener
    {
        /**
         * Messaged from the JSplitPane the reciever is contained in.
         * May potentially reset the layout manager and cause a
         * validate() to be sent.
         */
        public void propertyChange(PropertyChangeEvent e) {
            if(e.getSource() == splitPane) {
                String changeName = e.getPropertyName();

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
                    layoutManager.resetSizeAt(2);
                    splitPane.revalidate();
                }
            }
        }
    }


    /**
     * Implementation of the FocusListener that the JSplitPane UI uses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class FocusHandler extends FocusAdapter
    {
        public void focusLost(FocusEvent ev) {
            dividerKeyboardResize = false;
            splitPane.repaint();
        }
    }
    

    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardUpLeftHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                setDividerLocation(splitPane,
                                   getDividerLocation(splitPane) -
                                   KEYBOARD_DIVIDER_MOVE_OFFSET);
            }
        }
    }
    

    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardDownRightHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                setDividerLocation(splitPane,
                                   getDividerLocation(splitPane) +
                                   KEYBOARD_DIVIDER_MOVE_OFFSET);
            }
        }
    }
    

    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardHomeHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                setDividerLocation(splitPane, 0);
            }
        }
    }
    

    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardEndHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                if (orientation == JSplitPane.VERTICAL_SPLIT) {
                    setDividerLocation(splitPane, splitPane.getHeight() -
                                       splitPane.getInsets().bottom);
                }
                else {
                    setDividerLocation(splitPane, splitPane.getWidth() -
                                       splitPane.getInsets().right);
                }
            }
        }
    }


    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardResizeToggleHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (!dividerKeyboardResize) {
                splitPane.requestFocus();
                dividerKeyboardResize = true;
                splitPane.repaint();
            }
        }
    }


    /**
     * Returns the divider between the top Components.
     */
    public BasicSplitPaneDivider getDivider() {
        return divider;
    }


    /**
     * Returns the default non continuous layout divider, which is an
     * instanceof Canvas that fills the background in dark gray.
     */
    protected Component createDefaultNonContinuousLayoutDivider() {
        return new Canvas() {
            public void paint(Graphics g) {
                if(!isContinuousLayout() && getLastDragLocation() != -1) {
                    Dimension      size = splitPane.getSize();

                    g.setColor(Color.darkGray);
                    if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                        g.fillRect(0, 0, dividerSize - 1, size.height - 1);
                    } else {
                        g.fillRect(0, 0, size.width - 1, dividerSize - 1);
                    }
                }
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
        if(nonContinuousLayoutDivider != null && splitPane != null) {
            splitPane.remove(nonContinuousLayoutDivider);
        }
        nonContinuousLayoutDivider = newDivider;
        if(nonContinuousLayoutDivider != null && splitPane != null) {
            nonContinuousLayoutDivider.setLocation(-1000, -1000);

            /* Needs to remove all the components and readd them! YECK! */
            Component             leftC = splitPane.getLeftComponent();
            Component             rightC = splitPane.getRightComponent();
            int[]                 sizes = layoutManager.getSizes();

            if(leftC != null)
                splitPane.setLeftComponent(null);
            if(rightC != null)
                splitPane.setRightComponent(null);
            splitPane.remove(divider);
            splitPane.add(nonContinuousLayoutDivider, BasicSplitPaneUI.
                          NON_CONTINUOUS_DIVIDER,
                          splitPane.getComponentCount());
            splitPane.setLeftComponent(leftC);
            splitPane.setRightComponent(rightC);
            splitPane.add(divider, JSplitPane.DIVIDER);
            if(rememberSizes)
                layoutManager.setSizes(sizes);
            splitPane.revalidate();
            splitPane.paintImmediately(splitPane.getX(),
                                       splitPane.getY(),
                                       splitPane.getWidth(),
                                       splitPane.getHeight());
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
     * Creates the default divider.
     */
    public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this);
    }


    /**
     * Messaged to reset the preferred sizes.
     */
    public void resetToPreferredSizes(JSplitPane jc) {
        if(splitPane != null) {
            layoutManager.resetToPreferredSizes();
            splitPane.revalidate();
            layoutManager.layoutContainer(splitPane);
        }
    }


    /**
     * Sets the location of the divider to location.
     */
    public void setDividerLocation(JSplitPane jc, int location) {
        Component leftC = splitPane.getLeftComponent();
        Component rightC = splitPane.getRightComponent();

        if(leftC != null && rightC != null) {
            Insets                insets = splitPane.getInsets();

            // Since location describes the size of the left/top component
            // and not really the divider location the actual divider location
            // that can be retrieved via JSplitPane.getDividerLocation() ends
            // up to be location + getDividerborderSize().
            // That's why we subtract getDividerBorderSize() in the
            // calculations with location so that getDividerLocation()
            // returns the correct value.
            if(insets != null) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    leftC.setSize(Math.max(0, location -
                                           getDividerBorderSize() -
                                           insets.left), 10);
                } else {
                    leftC.setSize(10, Math.max(0, location -
                                               getDividerBorderSize() -
                                               insets.top));
                }
            } else {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    leftC.setSize(Math.max(0, location -
                                           getDividerBorderSize()), 10);
                } else {
                    leftC.setSize(10, Math.max(0, location -
                                               getDividerBorderSize()));
                }
            }
            if (!dividerLocationIsSet) dividerLocationIsSet = true;
            splitPane.revalidate();
            splitPane.repaint();
        }
    }


    /**
     * Returns the location of the divider.
     */
    public int getDividerLocation(JSplitPane jc) {
        if(orientation == JSplitPane.HORIZONTAL_SPLIT)
            return divider.getLocation().x;
        return divider.getLocation().y;
    }


    /**
     * Gets the minimum location of the divider.
     */
    public int getMinimumDividerLocation(JSplitPane jc) {
        int       minLoc = 0;
        Component leftC = splitPane.getLeftComponent();

        if (leftC != null) {
            Insets    insets = splitPane.getInsets();
            Dimension minSize = leftC.getMinimumSize();
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                minLoc = minSize.width;
            } else {
                minLoc = minSize.height;
            }
            minLoc += getDividerBorderSize();
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
            Dimension minSize = rightC.getMinimumSize();
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                maxLoc = splitPaneSize.width - minSize.width;
            } else {
                maxLoc = splitPaneSize.height - minSize.height; 
            }
            maxLoc -= (dividerSize + getDividerBorderSize());
            if(insets != null) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    maxLoc += insets.left;
                } else {
                    maxLoc += insets.top;
                }
            }
        }
        return Math.max(getMinimumDividerLocation(splitPane), maxLoc);
    }


    /**
     * Messaged after the JSplitPane the receiver is providing the look
     * and feel for paints its children.
     */
    public void finishedPaintingChildren(JSplitPane jc, Graphics g) {
        if(jc == splitPane && getLastDragLocation() != -1 &&
           !isContinuousLayout() && !draggingHW) {
            Dimension      size = splitPane.getSize();

            g.setColor(Color.darkGray);
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                g.fillRect(getLastDragLocation(), 0, dividerSize - 1,
                           size.height - 1);
            } else {
                g.fillRect(0, lastDragLocation, size.width - 1,
                           dividerSize - 1);
            }
        }
    }


    /**
     * Messaged to paint the look and feel.
     */
    public void paint(Graphics g, JComponent jc) {
    }


    /**
     * Returns the preferred size for the passed in component,
     * This is passed off to the current layoutmanager.
     */
    public Dimension getPreferredSize(JComponent jc) {
        if(splitPane != null)
            return layoutManager.preferredLayoutSize(splitPane);
        return new Dimension(0, 0);
    }


    /**
     * Returns the minimum size for the passed in component,
     * This is passed off to the current layoutmanager.
     */
    public Dimension getMinimumSize(JComponent jc) {
        if(splitPane != null)
            return layoutManager.minimumLayoutSize(splitPane);
        return new Dimension(0, 0);
    }


    /**
     * Returns the maximum size for the passed in component,
     * This is passed off to the current layoutmanager.
     */
    public Dimension getMaximumSize(JComponent jc) {
        if(splitPane != null)
            return layoutManager.maximumLayoutSize(splitPane);
        return new Dimension(0, 0);
    }


    /**
     * Returns the insets. The insets are returned from the broder insets
     * of the current border.
     */
    public Insets getInsets(JComponent jc) {
        return null;
    }


    /**
     * Resets the layout manager based on orientation and messages it
     * with invalidateLayout to pull in appropriate Components.
     */
    protected void resetLayoutManager() {
        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            layoutManager = new BasicHorizontalLayoutManager();
        } else {
            layoutManager = new BasicVerticalLayoutManager();
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
           !(cPeer instanceof java.awt.peer.LightweightPeer)) {
            draggingHW = true;
        } else if(rightC != null && (cPeer = rightC.getPeer()) != null
                  && !(cPeer instanceof java.awt.peer.LightweightPeer)) {
            draggingHW = true;
        }
        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            setLastDragLocation(divider.getBounds().x);
            dividerSize = divider.getSize().width + 2 * getDividerBorderSize();
            if(!isContinuousLayout() && draggingHW) {
                nonContinuousLayoutDivider.setBounds
                        (getLastDragLocation(), 0, dividerSize,
                         splitPane.getHeight());
            }
        } else {
            setLastDragLocation(divider.getBounds().y);
            dividerSize = divider.getSize().height + 2 *
                          getDividerBorderSize();
            if(!isContinuousLayout() && draggingHW) {
                nonContinuousLayoutDivider.setBounds
                        (0, getLastDragLocation(), splitPane.getWidth(),
                         dividerSize);
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
                Component          leftC = splitPane.getLeftComponent();
                Rectangle          leftBounds = leftC.getBounds();

                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    leftC.setSize(location - leftBounds.x,
                                  leftBounds.height);
                } else {
                    leftC.setSize(leftBounds.width, location - leftBounds.y);
                }
                splitPane.revalidate();
                splitPane.repaint();
                setLastDragLocation(location);
            } else {
                int lastLoc = getLastDragLocation();

                setLastDragLocation(location);
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    int splitHeight = splitPane.getSize().height;

                    if(draggingHW) {
                        nonContinuousLayoutDivider.setLocation(
                            getLastDragLocation(), 0);
                    } else {
                        splitPane.repaint(lastLoc, 0, dividerSize,
                                          splitHeight);
                        splitPane.repaint(location, 0, dividerSize,
                                          splitHeight);
                    }
                } else {
                    int splitWidth = splitPane.getSize().width;

                    if(draggingHW) {
                        nonContinuousLayoutDivider.setLocation(0,
                            getLastDragLocation());
                    } else {
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

            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                int splitHeight = splitPane.getSize().height;

                leftC.setSize(location - leftBounds.x,
                              leftBounds.height);
                if(draggingHW)
                    nonContinuousLayoutDivider.setLocation(-dividerSize, 0);
                splitPane.paintImmediately(location, 0, dividerSize,
                                           splitHeight);
            } else {
                int      splitWidth = splitPane.getSize().width;

                leftC.setSize(leftBounds.width, location - leftBounds.y);
                if(draggingHW)
                    nonContinuousLayoutDivider.setLocation(0, -dividerSize);
                splitPane.paintImmediately(0, location, splitWidth,
                                           dividerSize);
            }
            /* Do the layout. */
            splitPane.revalidate();
            splitPane.repaint();
        }
        splitPane.setLastDividerLocation(beginDragDividerLocation);
    }


    /**
     * Returns the width of one side of the divider border.
     */
    protected int getDividerBorderSize() {
        return 1;
    }


    /**
     * LayoutManager for JSplitPanes that have an orientation of
     * HORIZONTAL_SPLIT.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class BasicHorizontalLayoutManager implements LayoutManager2
    {
        /* left, right, divider. (in this exact order) */
        protected int[]         sizes;
        protected Component[]   components;


        BasicHorizontalLayoutManager() {
            components = new Component[3];
            components[0] = components[1] = components[2] = null;
            sizes = new int[3];
        }


        /**
         * Resets the size of the Component at the passed in location.
         */
        protected void resetSizeAt(int index) {
            sizes[index] = -1;
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
            return c.getPreferredSize().width;
        }


        /**
         * Returns the width of the passed in Components minimum size.
         */
        int getMinimumSizeOfComponent(Component c) {
            return c.getMinimumSize().width;
        }


        /**
         * Returns the width of the passed in component.
         */
        protected int getSizeOfComponent(Component c) {
            return c.getSize().width;
        }


        /**
         * Returns the available width based on the container size and
         * Insets.
         */
        protected int getAvailableSize(Dimension containerSize,
                                       Insets insets) {
            if(insets == null)
                return containerSize.width;
            return (containerSize.width - (insets.left + insets.right + 2 *
                                           getDividerBorderSize()));
        }


        /**
         * Returns the left inset, unless the Insets or null in which case
         * 0 is returned.
         */
        protected int getInitialLocation(Insets insets) {
            if(insets != null)
                return insets.left;
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
                c.setBounds(location, insets.top, size, containerSize.height -
                            (insets.top + insets.bottom));
            } else {
                c.setBounds(location, 0, size, containerSize.height);
            }
        }


        /**
         * Calculates the actual layout.
         */
        public void layoutContainer(Container container) {
            Dimension containerSize = container.getSize();
            // If the splitpane has a zero size then no op out of here.
            // If we execute this function now, we're going to cause ourselves
            // much grief.
            if (containerSize.height == 0 || containerSize.width == 0 ) {
                return;
            }

            Insets  insets = splitPane.getInsets();
            int     counter;
            int     newSize;
            int     totalSize = 0;
            int     availableSize = getAvailableSize(containerSize, insets);
            int     beginLocation;

            // Set the last location
            beginLocation = splitPane.getDividerLocation();

            // Check to see if the size of something has changed and
            // adjust the other Component if it has
            for (counter=0; counter<3; counter++) {
                if (components[counter] != null) {
                    // -1 signifies this is the first time this component
                    // is being layed out and that the preferred size (if
                    // possible) should be asked for
                    if (sizes[counter] == -1) {
                        if ((dividerLocationIsSet) && (counter == 0)) {
                            // If setDividerLoction() was already called before
                            // validation, then the actually set size has to be
                            // used for the upper/left component instead of the
                            // preferred size
                            sizes[counter] = Math.min(availableSize,
                                                      getSizeOfComponent(
                                                      components[counter]));
                        }
                        else {
                            // Retrieve the preferred size of each component
                            sizes[counter] = Math.min(availableSize,
                                                getPreferredSizeOfComponent(
                                                components[counter]));

                            // Use the minimum size for the top/left component
                            // if the preferred size of the top/left component
                            // does not leave enough space to accomodate the
                            // minimum size of the bottom/right component
                            if (counter == 2) {
                                // Is the size of the top/left component plus
                                // the size of the divider bigger than the
                                // minimum size of the bottom/right component?
                                // If so set the yop/left component to its
                                // minimum size.
                                if ((components[0] != null) &&
                                    (components[1] != null)) {
                                    if ((sizes[0] + sizes[counter] +
                                         getMinimumSizeOfComponent(
                                            components[1])) > availableSize) {
                                        sizes[0] = getMinimumSizeOfComponent(
                                                    components[0]);
                                    }
                                }
                            }
                        }
                    } else {
                        newSize = getSizeOfComponent(components[counter]);
                        if (sizes[counter] != newSize) {
                            if (counter == 0) {
                                if (components[1] != null) {
                                    sizes[1] = Math.max(0, sizes[1] +
                                        (sizes[counter] - newSize));
                                }
                            } else if (counter == 1) {
                                if (components[0] != null) {
                                    sizes[0] = Math.max(0, sizes[0] +
                                        (sizes[counter] - newSize));
                                }
                            }
                            sizes[counter] = newSize;
                            // Do this to not adjust the next one, but adjust
                            // the divider
                            if (counter == 0) counter = 1;
                        }
                    }
                }
            }

            for (counter=0; counter<3; counter++) totalSize += sizes[counter];

            // If the width has changed, adjust the right, and then left
            // component (if necessary)
            if (totalSize != availableSize) {
                int toDiff = (availableSize - totalSize);

                if (components[1] != null) {
                    newSize = Math.max(0, sizes[1] + toDiff);
                    if (newSize == 0) {
                        toDiff += sizes[1];
                        sizes[1] = 0;
                        if (components[0] != null)
                            sizes[0] = Math.max(0, sizes[0] + toDiff);
                    } else {
                        sizes[1] = newSize;
                    }
                } else if (components[0] != null) {
                    sizes[0] = Math.max(0, sizes[0] + toDiff);
                }
            }

            // Reset the bounds of each component
            int nextLocation = getInitialLocation(insets);
            int bdSize = getDividerBorderSize();

            counter = 0;
            while (counter < 3) {
                if (components[counter] != null) {
                    setComponentToSize(components[counter], sizes[counter],
                                       nextLocation, insets, containerSize);
                    nextLocation += sizes[counter];
                }
                switch (counter) {
                case 0:
                    counter = 2;
                    nextLocation += bdSize;
                    break;
                case 2:
                    counter = 1;
                    nextLocation += bdSize;
                    break;
                case 1:
                    counter = 3;
                    break;
                }
            }

            if (beginLocation != splitPane.getDividerLocation()) {
                splitPane.setLastDividerLocation(beginLocation);
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
                    sizes[2] = -1;
                } else if(place.equals(JSplitPane.LEFT) ||
                          place.equals(JSplitPane.TOP)) {
                    components[0] = component;
                    sizes[0] = -1;
                } else if(place.equals(JSplitPane.RIGHT) ||
                          place.equals(JSplitPane.BOTTOM)) {
                    components[1] = component;
                    sizes[1] = -1;
                } else if(!place.equals(
                                    BasicSplitPaneUI.NON_CONTINUOUS_DIVIDER))
                    isValid = false;
            } else {
                isValid = false;
            }
            if(!isValid)
                throw new IllegalArgumentException("cannot add to layout: " +
                    "unknown constraint: " +
                    place);
        }


        /**
         * Returns the minimum size needed to contain the children.
         * The width is the sum of all the childrens min widths and
         * the height is the largest of the childrens minimum heights.
         */
        public Dimension minimumLayoutSize(Container container) {
            Dimension   minSize;
            int         minX = 0;
            int         minY = 0;
            Insets      insets = splitPane.getInsets();

            for (int counter=0; counter<3; counter++) {
                if(components[counter] != null) {
                    minSize = components[counter].getMinimumSize();
                    minX += minSize.width;
                    if(minSize.height > minY)
                        minY = minSize.height;
                }
            }
            if(insets != null) {
                minX += insets.left + insets.right;
                minY += insets.bottom + insets.top;
            }
            return new Dimension(minX, minY);
        }


        /**
         * Returns the preferred size needed to contain the children.
         * The width is the sum of all the childrens preferred widths and
         * the height is the largest of the childrens preferred heights.
         */
        public Dimension preferredLayoutSize(Container container) {
            Dimension   preSize;
            int         preX = 0;
            int         preY = 0;
            Insets      insets = splitPane.getInsets();

            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] != null) {
                    preSize = components[counter].getPreferredSize();
                    preX += preSize.width;
                    if(preSize.height > preY)
                        preY = preSize.height;
                }
            }
            if(insets != null) {
                preX += insets.left + insets.right;
                preY += insets.bottom + insets.top;
            }
            return new Dimension(preX, preY);
        }


        /**
         * Removes the specified component from our knowledge.
         */
        public void removeLayoutComponent(Component component) {
            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] == component) {
                    components[counter] = null;
                    sizes[counter] = 0;
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


        /**
         * Resets the cached sizes so that next time this instance is
         * layed out it'll ask for the preferred sizes.
         */
        public void resetToPreferredSizes() {
            for(int counter = 0; counter < 3; counter++)
                sizes[counter] = -1;
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
                        if(children[counter] == null) {
                            sizes[2] = 0;
                        } else {
                            sizes[2] = -1;
                        }
                    } else {
                        components[2] = oldDivider;
                    }
                    break;
                }
            }
            if(components[2] == null) sizes[2] = 0;
        }
    }


    /**
     * LayoutManager used for JSplitPanes with an orientation of
     * VERTICAL_SPLIT.
     * <p>
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class BasicVerticalLayoutManager extends
            BasicHorizontalLayoutManager
    {
        /**
         * Returns the height of the passed in Components preferred size.
         */
        protected int getPreferredSizeOfComponent(Component c) {
            return c.getPreferredSize().height;
        }

        /**
         * Returns the height of the passed in Components minimum size.
         */
        int getMinimumSizeOfComponent(Component c) {
            return c.getMinimumSize().height;
        }


        /**
         * Returns the height of the passed in component.
         */
        protected int getSizeOfComponent(Component c) {
            return c.getSize().height;
        }


        /**
         * Returns the available height based on the container size and
         * Insets.
         */
        protected int getAvailableSize(Dimension containerSize,
                                       Insets insets) {
            if(insets == null)
                return containerSize.height;
            return (containerSize.height - (insets.bottom + insets.top + 2 *
                                            getDividerBorderSize()));
        }


        /**
         * Returns the top inset, unless the Insets or null in which case
         * 0 is returned.
         */
        protected int getInitialLocation(Insets insets) {
            if(insets != null)
                return insets.top;
            return 0;
        }


        /**
         * Sets the height of the component c to be size, placing its
         * x location to insets.left, y to location and width
         * to the containersize.width less the left and right insets.
         */
        protected void setComponentToSize(Component c, int size,
                                          int location, Insets insets,
                                          Dimension containerSize) {
            if(insets != null) {
                c.setBounds(insets.left, location, containerSize.width -
                            (insets.left + insets.right), size);
            } else {
                c.setBounds(0, location, containerSize.width, size);
            }
        }


        /**
         * Returns the minimum size needed to contain the children.
         * The height is the sum of all the childrens min heights and
         * the width is the largest of the childrens minimum widths.
         */
        public Dimension minimumLayoutSize(Container container) {
            Dimension   minSize;
            int         minX = 0;
            int         minY = 0;
            Insets      insets = splitPane.getInsets();

            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] != null) {
                    minSize = components[counter].getMinimumSize();
                    minY += minSize.height;
                    if(minSize.width > minX)
                        minX = minSize.width;
                }
            }
            if(insets != null) {
                minX += insets.left + insets.right;
                minY += insets.bottom + insets.top;
            }
            return new Dimension(minX, minY);
        }


        /**
         * Returns the preferred size needed to contain the children.
         * The height is the sum of all the childrens preferred heights and
         * the width is the largest of the childrens preferred widths.
         */
        public Dimension preferredLayoutSize(Container container) {
            Dimension   preSize;
            int         preX = 0;
            int         preY = 0;
            Insets      insets = splitPane.getInsets();

            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] != null) {
                    preSize = components[counter].getPreferredSize();
                    preY += preSize.height;
                    if(preSize.width > preX)
                        preX = preSize.width;
                }
            }
            if(insets != null) {
                preX += insets.left + insets.right;
                preY += insets.bottom + insets.top;
            }
            return new Dimension(preX, preY);
        }
    }
}
