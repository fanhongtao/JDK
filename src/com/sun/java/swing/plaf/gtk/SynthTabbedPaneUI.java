/*
 * @(#)SynthTabbedPaneUI.java	1.25 03/05/08
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.View;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.util.Hashtable;

// PENDING: nuke this
import javax.swing.plaf.basic.BasicHTML;

/**
 * A Basic L&F implementation of TabbedPaneUI.
 *
 * @version 1.25, 05/08/03 (based on BasicTabbedPaneUI v 1.123)
 * @author Amy Fowler
 * @author Philip Milne
 * @author Steve Wilson
 * @author Tom Santos
 * @author Dave Moore
 */
/**
 * Looks up 'selectedTabPadInsets' from the Style, which will be additional
 * insets for the selected tab.
 */
class SynthTabbedPaneUI extends TabbedPaneUI implements SynthUI, SwingConstants  {
    private static Action scrollTabsForwardAction =
                                new ScrollTabsForwardAction();
    private static Action scrollTabsBackwardAction = 
                                new ScrollTabsBackwardAction();

// Instance variables initialized at installation

    // PENDING: These should be fetched as necessary, eg not maintained as
    // fields but passed around.
    private SynthContext tabAreaContext;
    private TabContext tabContext;
    private SynthContext tabContentContext;

    private SynthStyle style;
    private SynthStyle tabStyle;
    private SynthStyle tabAreaStyle;
    private SynthStyle tabContentStyle;

    private int tabRunOverlay;

    protected JTabbedPane tabPane;


// Transient variables (recalculated each time TabbedPane is layed out)

    protected int tabRuns[] = new int[10];
    protected int runCount = 0;
    protected int selectedRun = -1;
    protected Rectangle rects[] = new Rectangle[0]; 
    protected int maxTabHeight;
    protected int maxTabWidth;

// Listeners

    protected ChangeListener tabChangeListener;
    protected PropertyChangeListener propertyChangeListener;
    protected MouseInputListener mouseInputListener;
    protected FocusListener focusListener;
    // PENDING(api): See comment for ContainerHandler
    private   ContainerListener containerListener;

// Private instance data

    private Insets currentPadInsets = new Insets(0,0,0,0);
    private Insets currentTabAreaInsets = new Insets(0,0,0,0);

    private Component visibleComponent;
    // PENDING(api): See comment for ContainerHandler
    private Vector htmlViews;

    private Hashtable mnemonicToIndexMap;

    /**
     * InputMap used for mnemonics. Only non-null if the JTabbedPane has
     * mnemonics associated with it. Lazily created in initMnemonics.
     */
    private InputMap mnemonicInputMap;

    // For use when tabLayoutPolicy = SCROLL_TAB_LAYOUT
    private ScrollableTabSupport tabScroller;

    /**
     * A rectangle used for general layout calculations in order
     * to avoid constructing many new Rectangles on the fly.
     */
    protected transient Rectangle calcRect = new Rectangle(0,0,0,0);

    /**
     * Number of tabs. When the count differs, the mnemonics are updated.
     */
    // PENDING: This wouldn't be necessary if JTabbedPane had a better
    // way of notifying listeners when the count changed.
    private int tabCount;

    /**
     * Index of the tab the mouse is over.
     */
    private int mouseIndex;

    /**
     * Index of the tab that has focus.
     */
    private boolean selectionFollowsFocus = true;
    private int focusIndex = -1;

    /**
     * Bounds of the tab under the mouse.
     */
    private Rectangle mouseBounds;

// UI creation

    public static ComponentUI createUI(JComponent c) {
        return new SynthTabbedPaneUI();
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("navigateNext", new NextAction());
	map.put("navigatePrevious", new PreviousAction());
	map.put("navigateRight", new RightAction());
	map.put("navigateLeft", new LeftAction());
	map.put("navigateUp", new UpAction());
	map.put("navigateDown", new DownAction());
	map.put("navigatePageUp", new PageUpAction());
	map.put("navigatePageDown", new PageDownAction());
	map.put("requestFocus", new RequestFocusAction());
	map.put("requestFocusForVisibleComponent",
		new RequestFocusForVisibleAction());
        map.put("setSelectedIndex", new SetSelectedIndexAction());
        map.put("selectTabWithFocus", new SelectFocusIndexAction());
	map.put("scrollTabsForwardAction", scrollTabsForwardAction);
        map.put("scrollTabsBackwardAction", scrollTabsBackwardAction);
    }


// UI Installation/De-installation
    
    public void installUI(JComponent c) {
        this.tabPane = (JTabbedPane)c;

        c.setLayout(createLayoutManager());
	installComponents();
        installDefaults(); 
        installListeners();
        installKeyboardActions();
    }

    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
	uninstallComponents();
        c.setLayout(null);
 
        this.tabPane = null;
    }

    /**
     * Invoked by <code>installUI</code> to create
     * a layout manager object to manage
     * the <code>JTabbedPane</code>.
     *
     * @return a layout manager object
     *
     * @see TabbedPaneLayout
     * @see javax.swing.JTabbedPane#getTabLayoutPolicy
     */
    protected LayoutManager createLayoutManager() {
	if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
	    return new TabbedPaneScrollLayout();
	} else { /* WRAP_TAB_LAYOUT */
            return new TabbedPaneLayout();
	}
    }

    /* In an attempt to preserve backward compatibility for programs
     * which have extended BasicTabbedPaneUI to do their own layout, the
     * UI uses the installed layoutManager (and not tabLayoutPolicy) to
     * determine if scrollTabLayout is enabled.
     */
    private boolean scrollableTabLayoutEnabled() {
	return (tabPane.getLayout() instanceof TabbedPaneScrollLayout);
    }

    /**
     * Creates and installs any required subcomponents for the JTabbedPane.
     * Invoked by installUI.
     *
     * @since 1.4
     */
    protected void installComponents() {
	if (scrollableTabLayoutEnabled()) {
	    if (tabScroller == null) {
		tabScroller = new ScrollableTabSupport(tabPane.getTabPlacement());
	        tabPane.add(tabScroller.viewport);
	        tabPane.add(tabScroller.scrollForwardButton);
	        tabPane.add(tabScroller.scrollBackwardButton);
	    }
	}
    }

    /**
     * Removes any installed subcomponents from the JTabbedPane.
     * Invoked by uninstallUI.
     *
     * @since 1.4
     */
    protected void uninstallComponents() {
	if (scrollableTabLayoutEnabled()) {
	    tabPane.remove(tabScroller.viewport);
	    tabPane.remove(tabScroller.scrollForwardButton);
	    tabPane.remove(tabScroller.scrollBackwardButton);
	    tabScroller = null;
	}
    }

    protected void installDefaults() {
        fetchStyle(tabPane);
    }

    private void fetchStyle(JTabbedPane c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        // Add properties other than JComponent colors, Borders and
        // opacity settings here:
        if (style != oldStyle) {
            tabRunOverlay = 0;
            Integer value = (Integer)context.getStyle().get(
                                 context, "tabRunOverlay");
            if (value != null) {
                tabRunOverlay = value.intValue();
            }
            selectionFollowsFocus = context.getStyle().getBoolean(context, 
                                            "TabbedPane.selectionFollowsFocus",
                                            true);
        }
        context.dispose();

        if (tabContext != null) {
            tabContext.dispose();
        }
        tabContext = (TabContext)getContext(c, Region.TABBED_PANE_TAB,
                                            ENABLED);
        this.tabStyle = SynthLookAndFeel.updateStyle(tabContext, this);

        if (tabAreaContext != null) {
            tabAreaContext.dispose();
        }
        tabAreaContext = getContext(c, Region.TABBED_PANE_TAB_AREA, ENABLED);
        this.tabAreaStyle = SynthLookAndFeel.updateStyle(tabAreaContext, this);

        if (tabContentContext != null) {
            tabContentContext.dispose();
        }
        tabContentContext = getContext(c, Region.TABBED_PANE_CONTENT, ENABLED);
        this.tabContentStyle = SynthLookAndFeel.updateStyle(tabContentContext,
                                                            this);
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(tabPane, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        context = getContext(tabPane, Region.TABBED_PANE_TAB, ENABLED);
        tabStyle.uninstallDefaults(context);
        context.dispose();
        tabStyle = null;

        context = getContext(tabPane,Region.TABBED_PANE_TAB_AREA, ENABLED);
        tabAreaStyle.uninstallDefaults(context);
        context.dispose();
        tabAreaStyle = null;

        context = getContext(tabPane, Region.TABBED_PANE_CONTENT, ENABLED);
        tabContentStyle.uninstallDefaults(context);
        context.dispose();
        tabContentStyle = null;
    }

    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            tabPane.addPropertyChangeListener(propertyChangeListener);
        }
        if ((tabChangeListener = createChangeListener()) != null) {            
            tabPane.addChangeListener(tabChangeListener);
        }
        if ((mouseInputListener = createMouseInputListener()) != null) {
	    if (scrollableTabLayoutEnabled()) { 
		tabScroller.tabPanel.addMouseListener(mouseInputListener);
		tabScroller.tabPanel.addMouseMotionListener(mouseInputListener);
	    } else { // WRAP_TAB_LAYOUT
                tabPane.addMouseListener(mouseInputListener);
                tabPane.addMouseMotionListener(mouseInputListener);
	    }
        }        
        if ((focusListener = createFocusListener()) != null) {
            tabPane.addFocusListener(focusListener);
        }
	// PENDING(api) : See comment for ContainerHandler
        if ((containerListener = new ContainerHandler()) != null) {
            tabPane.addContainerListener(containerListener);
	    if (tabPane.getTabCount()>0) {
		htmlViews = createHTMLVector();
	    }
	}
    }

    protected void uninstallListeners() {
        if (mouseInputListener != null) {
	    if (scrollableTabLayoutEnabled()) { // SCROLL_TAB_LAYOUT
		tabScroller.tabPanel.removeMouseListener(mouseInputListener);
		tabScroller.tabPanel.removeMouseMotionListener(
                            mouseInputListener);
	    } else { // WRAP_TAB_LAYOUT
                tabPane.removeMouseListener(mouseInputListener);
                tabPane.removeMouseMotionListener(mouseInputListener);
	    }
            mouseInputListener = null;
        }
        if (focusListener != null) {
            tabPane.removeFocusListener(focusListener);
            focusListener = null;
        }
	    
	// PENDING(api): See comment for ContainerHandler
        if (containerListener != null) {
            tabPane.removeContainerListener(containerListener);
            containerListener = null;
	    if (htmlViews!=null) {
		htmlViews.removeAllElements();
		htmlViews = null;
	    }
        }
        if (tabChangeListener != null) {
            tabPane.removeChangeListener(tabChangeListener);
            tabChangeListener = null;
        }
        if (propertyChangeListener != null) {
            tabPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
    }

    protected MouseInputListener createMouseInputListener() {
        return new MouseHandler();
    }

    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }    

    protected ChangeListener createChangeListener() {
        return new TabSelectionHandler();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected void installKeyboardActions() {
	InputMap km = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(tabPane, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
					 km);
	km = getInputMap(JComponent.WHEN_FOCUSED);
	SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_FOCUSED, km);

        LazyActionMap.installLazyActionMap(tabPane, SynthTabbedPaneUI.class,
                                           "TabbedPane.actionMap");

	if (scrollableTabLayoutEnabled()) {
	    tabScroller.scrollForwardButton.setAction(
                              scrollTabsForwardAction);
	    tabScroller.scrollBackwardButton.setAction(
                              scrollTabsBackwardAction);
	}
    }

    InputMap getInputMap(int condition) {
        SynthContext context = getContext(tabPane, ENABLED);
        InputMap map = null;
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            map = (InputMap)context.getStyle().get(context,
                                    "TabbedPane.ancestorInputMap");
	}
	else if (condition == JComponent.WHEN_FOCUSED) {
            map = (InputMap)context.getStyle().get(context,
                                    "TabbedPane.focusInputMap");
	}
        context.dispose();
        return map;
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(tabPane, null);
	SwingUtilities.replaceUIInputMap(tabPane, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
					 null);
	SwingUtilities.replaceUIInputMap(tabPane, JComponent.WHEN_FOCUSED,
					 null);
    }

    /**
     * Reloads the mnemonics. This should be invoked when a memonic changes,
     * when the title of a mnemonic changes, or when tabs are added/removed.
     */
    private void updateMnemonics() {
        resetMnemonics();
        for (int counter = tabPane.getTabCount() - 1; counter >= 0;
             counter--) {
            int mnemonic = tabPane.getMnemonicAt(counter);

            if (mnemonic > 0) {
                addMnemonic(counter, mnemonic);
            }
        }
    }

    /**
     * Resets the mnemonics bindings to an empty state.
     */
    private void resetMnemonics() {
        if (mnemonicToIndexMap != null) {
            mnemonicToIndexMap.clear();
            mnemonicInputMap.clear();
        }
    }

    /**
     * Adds the specified mnemonic at the specified index.
     */
    private void addMnemonic(int index, int mnemonic) {
	if (mnemonicToIndexMap == null) {
            initMnemonics();
	}
        mnemonicInputMap.put(KeyStroke.getKeyStroke(mnemonic, Event.ALT_MASK),
                             "setSelectedIndex");
        mnemonicToIndexMap.put(new Integer(mnemonic), new Integer(index));
    }

    /**
     * Installs the state needed for mnemonics.
     */
    private void initMnemonics() {
        mnemonicToIndexMap = new Hashtable();
        mnemonicInputMap = new InputMapUIResource();
        mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(tabPane,
                              JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
        SwingUtilities.replaceUIInputMap(tabPane,
                              JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                                         mnemonicInputMap);
    }

// Geometry

    public Dimension getPreferredSize(JComponent c) {
        // Default to LayoutManager's preferredLayoutSize
        return null;
    }

    public Dimension getMinimumSize(JComponent c) {
	// Default to LayoutManager's minimumLayoutSize
 	return null;
    }
    
    public Dimension getMaximumSize(JComponent c) {
	// Default to LayoutManager's maximumLayoutSize
	return null;
    }


    private void repaintTab(int index) {
    }

    private void updateMouseOver(int x, int y) {
        setMouseOverTab(getTabAtLocation(x, y));
    }

    private void setMouseOverTab(int index) {
        if (index != mouseIndex) {
            if (mouseBounds != null) {
                tabPane.repaint(mouseBounds);
            }
            mouseIndex = index;
            if (index != -1 && index < tabPane.getTabCount()) {
                mouseBounds = tabPane.getBoundsAt(index);
                if (mouseBounds != null) {
                    tabPane.repaint(mouseBounds);
                }
            }
            else {
                mouseBounds = null;
            }
        }
    }

// UI Rendering 

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    public SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c),style, state);
    }

    public SynthContext getContext(JComponent c, Region subregion) {
        return getContext(c, subregion, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, Region subregion, int state){
        SynthStyle style = null;
        Class klass = SynthContext.class;

        if (subregion == Region.TABBED_PANE_TAB) {
            style = tabStyle;
            klass = TabContext.class;
        }
        else if (subregion == Region.TABBED_PANE_TAB_AREA) {
            style = tabAreaStyle;
        }
        else if (subregion == Region.TABBED_PANE_CONTENT) {
            style = tabContentStyle;
        }
        return SynthContext.getContext(klass, c, subregion, style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
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
        int tc = tabPane.getTabCount();

        if (tabCount != tc) {
            tabCount = tc;
            updateMnemonics();
        }

        int selectedIndex = tabPane.getSelectedIndex();
        int tabPlacement = tabPane.getTabPlacement();

        ensureCurrentLayout();

	// Paint tab area
	// If scrollable tabs are enabled, the tab area will be
	// painted by the scrollable tab panel instead.
	//
	if (!scrollableTabLayoutEnabled()) { // WRAP_TAB_LAYOUT
            Insets insets = tabPane.getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = tabPane.getWidth() - insets.left - insets.right;
            int height = tabPane.getHeight() - insets.top - insets.bottom;
            int size;
            switch(tabPlacement) {
            case LEFT:
                width = calculateTabAreaWidth(tabPlacement, runCount,
                                              maxTabWidth);
                break;
            case RIGHT:
                size = calculateTabAreaWidth(tabPlacement, runCount,
                                             maxTabWidth);
                x = x + width - size;
                width = size;
                break;            
            case BOTTOM:
                size = calculateTabAreaHeight(tabPlacement, runCount,
                                              maxTabHeight);
                y = y + height - size;
                height = size;
                break;
            case TOP:
            default:
                height = calculateTabAreaHeight(tabPlacement, runCount,
                                                maxTabHeight);
            }
	    paintTabArea(tabAreaContext, g, tabPlacement,
                         selectedIndex, new Rectangle(x, y, width, height));
	}
	
        // Paint content border
        paintContentBorder(tabContentContext, g, tabPlacement, selectedIndex);
    }


    /**
     * Paints the tabs in the tab area.
     * Invoked by paint().
     * The graphics parameter must be a valid <code>Graphics</code>
     * object.  Tab placement may be either: 
     * <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
     * The selected index must be a valid tabbed pane tab index (0 to
     * tab count - 1, inclusive) or -1 if no tab is currently selected.
     * The handling of invalid parameters is unspecified.
     *
     * @param g the graphics object to use for rendering
     * @param tabPlacement the placement for the tabs within the JTabbedPane
     * @param selectedIndex the tab index of the selected component
     *
     * @since 1.4
     */
    protected void paintTabArea(SynthContext ss, Graphics g,
                                int tabPlacement, int selectedIndex,
                                Rectangle tabAreaBounds) {
        // Paint the tab area.
        SynthLookAndFeel.updateSubregion(ss, g, tabAreaBounds);

        int tabCount = tabPane.getTabCount();

        Rectangle iconRect = new Rectangle(),
                  textRect = new Rectangle();
        Rectangle clipRect = g.getClipBounds();  

        // Paint tabRuns of tabs from back to front
        for (int i = runCount - 1; i >= 0; i--) {
            int start = tabRuns[i];
            int next = tabRuns[(i == runCount - 1)? 0 : i + 1];
            int end = (next != 0? next - 1: tabCount - 1);
            for (int j = start; j <= end; j++) {
                if (rects[j].intersects(clipRect) && selectedIndex != j) {
                    paintTab(tabContext, g, tabPlacement, rects, j, iconRect,
                             textRect);
                }
            }
        }

        if (selectedIndex >= 0) {
            if (rects[selectedIndex].intersects(clipRect)) {
                paintTab(tabContext, g, tabPlacement, rects, selectedIndex,
                         iconRect, textRect);
            }
        }
    }

    protected void paintTab(SynthContext ss, Graphics g,
                            int tabPlacement, Rectangle[] rects, int tabIndex, 
                            Rectangle iconRect, Rectangle textRect) {
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;
        tabContext.update(tabIndex, isSelected, (mouseIndex == tabIndex), 
                                                (focusIndex == tabIndex));

        SynthLookAndFeel.updateSubregion(ss, g, tabRect);
        
        String title = tabPane.getTitleAt(tabIndex);
        Font font = ss.getStyle().getFont(ss);
        FontMetrics metrics = g.getFontMetrics(font);
        Icon icon = getIconForTab(tabIndex);

        layoutLabel(ss, tabPlacement, metrics, tabIndex, title, icon, 
                    tabRect, iconRect, textRect, isSelected);

        paintText(ss, g, tabPlacement, font, metrics, 
                  tabIndex, title, textRect, isSelected);

        paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
    }

    protected void layoutLabel(SynthContext ss, int tabPlacement, 
                               FontMetrics metrics, int tabIndex,
                               String title, Icon icon,
                               Rectangle tabRect, Rectangle iconRect, 
                               Rectangle textRect, boolean isSelected ) {
	View v = getTextViewForTab(tabIndex);
	if (v != null) {
	    tabPane.putClientProperty("html", v);
	}

        textRect.x = textRect.y = iconRect.x = iconRect.y = 0;

        ss.getStyle().getSynthGraphics(ss).layoutText(ss, metrics, title,
                         icon, SwingUtilities.CENTER, SwingUtilities.CENTER,
                         SwingUtilities.LEADING, SwingUtilities.TRAILING,
                         tabRect, iconRect, textRect, getTextIconGap());

	tabPane.putClientProperty("html", null);

        // PENDING: Can these be nuked? Don't fit well...
        // If this is necessary, they should be in a special TextLayout
        // that special cases this behavior.
        int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
        int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
        iconRect.x += xNudge;
        iconRect.y += yNudge;
        textRect.x += xNudge;
        textRect.y += yNudge;
    }

    protected void paintIcon(Graphics g, int tabPlacement,
                             int tabIndex, Icon icon, Rectangle iconRect, 
                             boolean isSelected ) {
        if (icon != null) {
            icon.paintIcon(tabPane, g, iconRect.x, iconRect.y);
        }
    }

    protected void paintText(SynthContext ss,
                             Graphics g, int tabPlacement,
                             Font font, FontMetrics metrics, int tabIndex,
                             String title, Rectangle textRect, 
                             boolean isSelected) {
        g.setFont(font);

	View v = getTextViewForTab(tabIndex);
	if (v != null) {
	    // html
	    v.paint(g, textRect);
	} else {
	    // plain text
            int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

            g.setColor(ss.getStyle().getColor(ss, ColorType.TEXT_FOREGROUND));
            ss.getStyle().getSynthGraphics(ss).paintText(ss, g, title,
                                  textRect, mnemIndex);
	}
    } 


    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        int nudge = 0;
        switch(tabPlacement) {
          case LEFT:
              nudge = isSelected? -1 : 1;
              break;
          case RIGHT:
              nudge = isSelected? 1 : -1;
              break;
          case BOTTOM:
          case TOP:
          default:
              nudge = tabRect.width % 2;
        }
        return nudge;
    }

    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        int nudge = 0;
        switch(tabPlacement) {
           case BOTTOM:
              nudge = isSelected? 1 : -1;
              break;
          case LEFT:
          case RIGHT:
              nudge = tabRect.height % 2;
              break;
          case TOP:
          default:
              nudge = isSelected? -1 : 1;;
        }
        return nudge;
    }

    protected void paintContentBorder(SynthContext ss, Graphics g,
                                      int tabPlacement, int selectedIndex) {
        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        switch(tabPlacement) {
          case LEFT:
              x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
              w -= (x - insets.left);
              break;
          case RIGHT:
              w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
              break;            
          case BOTTOM: 
              h -= calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
              break;
          case TOP:
          default:
              y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
              h -= (y - insets.top);
        }
        SynthLookAndFeel.updateSubregion(ss, g, new Rectangle(x, y, w, h));
    }
         
    private void ensureCurrentLayout() {
        if (!tabPane.isValid()) {
            tabPane.validate();
        } 
	/* If tabPane doesn't have a peer yet, the validate() call will
	 * silently fail.  We handle that by forcing a layout if tabPane
	 * is still invalid.  See bug 4237677.
	 */
        if (!tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout)tabPane.getLayout();
            layout.calculateLayoutInfo();          
        }
    }
    

// TabbedPaneUI methods

    /**
     * Returns the bounds of the specified tab index.  The bounds are
     * with respect to the JTabbedPane's coordinate space.
     */
    public Rectangle getTabBounds(JTabbedPane pane, int i) { 
        ensureCurrentLayout();
	Rectangle tabRect = new Rectangle();
        return getTabBounds(i, tabRect);
    }

    public int getTabRunCount(JTabbedPane pane) {
	ensureCurrentLayout();
        return runCount;
    }

    /**
     * Returns the tab index which intersects the specified point
     * in the JTabbedPane's coordinate space.
     */
    public int tabForCoordinate(JTabbedPane pane, int x, int y) {
        ensureCurrentLayout();
	Point p = new Point(x, y);

	if (scrollableTabLayoutEnabled()) {
	    translatePointToTabPanel(x, y, p);
	}
        int tabCount = tabPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (rects[i].contains(p.x, p.y)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the bounds of the specified tab in the coordinate space
     * of the JTabbedPane component.  This is required because the tab rects
     * are by default defined in the coordinate space of the component where
     * they are rendered, which could be the JTabbedPane
     * (for WRAP_TAB_LAYOUT) or a ScrollableTabPanel (SCROLL_TAB_LAYOUT).
     * This method should be used whenever the tab rectangle must be relative
     * to the JTabbedPane itself and the result should be placed in a
     * designated Rectangle object (rather than instantiating and returning
     * a new Rectangle each time). The tab index parameter must be a valid 
     * tabbed pane tab index (0 to tab count - 1, inclusive).  The destination
     * rectangle parameter must be a valid <code>Rectangle</code> instance.
     * The handling of invalid parameters is unspecified.
     *
     * @param tabIndex the index of the tab
     * @param dest the rectangle where the result should be placed
     * @return the resulting rectangle
     *
     * @since 1.4
     */
    protected Rectangle getTabBounds(int tabIndex, Rectangle dest) {
	dest.width = rects[tabIndex].width;
	dest.height = rects[tabIndex].height;

	if (scrollableTabLayoutEnabled()) { // SCROLL_TAB_LAYOUT
	    // Need to translate coordinates based on viewport location & 
	    // view position
	    Point vpp = tabScroller.viewport.getLocation();
	    Point viewp = tabScroller.viewport.getViewPosition();
	    dest.x = rects[tabIndex].x-vpp.x-viewp.x;
	    dest.y = rects[tabIndex].y-vpp.y-viewp.y;

	} else { // WRAP_TAB_LAYOUT
	    dest.x = rects[tabIndex].x;
	    dest.y = rects[tabIndex].y;
	}
	return dest;
    }

    /**
     * Returns the tab index which intersects the specified point
     * in the coordinate space of the component where the
     * tabs are actually rendered, which could be the JTabbedPane
     * (for WRAP_TAB_LAYOUT) or a ScrollableTabPanel (SCROLL_TAB_LAYOUT).
     */
    private int getTabAtLocation(int x, int y) {
	ensureCurrentLayout();

        int tabCount = tabPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (rects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the tab closest to the passed in location, note
     * that the returned tab may not contain the location x,y.
     */
    private int getClosestTab(int x, int y) {
        int min = 0;
        int tabCount = Math.min(rects.length, tabPane.getTabCount());
        int max = tabCount;
        int tabPlacement = tabPane.getTabPlacement();
        boolean useX = (tabPlacement == TOP || tabPlacement == BOTTOM);
        int want = (useX) ? x : y;

        while (min != max) {
            int current = (max + min) / 2;
            int minLoc;
            int maxLoc;

            if (useX) {
                minLoc = rects[current].x;
                maxLoc = minLoc + rects[current].width;
            }
            else {
                minLoc = rects[current].y;
                maxLoc = minLoc + rects[current].height;
            }
            if (want < minLoc) {
                max = current;
                if (min == max) {
                    return Math.max(0, current - 1);
                }
            }
            else if (want >= maxLoc) {
                min = current;
                if (max - min <= 1) {
                    return Math.max(current + 1, tabCount - 1);
                }
            }
            else {
                return current;
            }
        }
        return min;
    }

    /**
     * Returns a point which is translated from the specified point in the
     * JTabbedPane's coordinate space to the coordinate space of the
     * ScrollableTabPanel.  This is used for SCROLL_TAB_LAYOUT ONLY.
     */
    private Point translatePointToTabPanel(int srcx, int srcy, Point dest) {
	Point vpp = tabScroller.viewport.getLocation();
	Point viewp = tabScroller.viewport.getViewPosition();
	dest.x = srcx + vpp.x + viewp.x;
	dest.y = srcy + vpp.y + viewp.y;
	return dest;
    }

// BasicTabbedPaneUI methods

    protected Component getVisibleComponent() {
        return visibleComponent;
    }

    protected void setVisibleComponent(Component component) {
        if (visibleComponent != null && visibleComponent != component &&
                visibleComponent.getParent() == tabPane) {
            visibleComponent.setVisible(false);
        }
        if (component != null && !component.isVisible()) {
            component.setVisible(true);
        }
        visibleComponent = component;
    }

    protected void assureRectsCreated(int tabCount) {
        int rectArrayLen = rects.length; 
        if (tabCount != rectArrayLen ) {
            Rectangle[] tempRectArray = new Rectangle[tabCount];
            System.arraycopy(rects, 0, tempRectArray, 0, 
                             Math.min(rectArrayLen, tabCount));
            rects = tempRectArray;
            for (int rectIndex = rectArrayLen; rectIndex < tabCount; rectIndex++) {
                rects[rectIndex] = new Rectangle();
            }
        } 

    }

    protected void expandTabRunsArray() {
        int rectLen = tabRuns.length;
        int[] newArray = new int[rectLen+10];
        System.arraycopy(tabRuns, 0, newArray, 0, runCount);
        tabRuns = newArray;
    }

    protected int getRunForTab(int tabCount, int tabIndex) {
        for (int i = 0; i < runCount; i++) {
            int first = tabRuns[i];
            int last = lastTabInRun(tabCount, i);
            if (tabIndex >= first && tabIndex <= last) {
                return i;
            }
        }
        return 0;
    }

    protected int lastTabInRun(int tabCount, int run) {
        if (runCount == 1) {
            return tabCount - 1;
        }
        int nextRun = (run == runCount - 1? 0 : run + 1);
        if (tabRuns[nextRun] == 0) {
            return tabCount - 1;
        }
        return tabRuns[nextRun]-1;
    }

    protected int getTabRunOverlay(int tabPlacement) {
        return tabRunOverlay;
    }

    protected int getTabRunIndent(int tabPlacement, int run) {
        return 0;
    }

    protected boolean shouldPadTabRun(int tabPlacement, int run) {
        return runCount > 1;
    }

    protected boolean shouldRotateTabRuns(int tabPlacement) {
        return true;
    }

    protected Icon getIconForTab(int tabIndex) {
        return (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex))?
                          tabPane.getDisabledIconAt(tabIndex) : tabPane.getIconAt(tabIndex);
    }

    /** 
     * Returns the text View object required to render stylized text (HTML) for
     * the specified tab or null if no specialized text rendering is needed
     * for this tab. This is provided to support html rendering inside tabs.
     *
     * @param tabIndex the index of the tab
     * @return the text view to render the tab's text or null if no
     *         specialized rendering is required
     *
     * @since 1.4
     */
    protected View getTextViewForTab(int tabIndex) {
	if (htmlViews != null) {
	    return (View)htmlViews.elementAt(tabIndex);
	}
	return null;
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
	int height = 0;
	View v = getTextViewForTab(tabIndex);
	if (v != null) {
	    // html
	    height += (int)v.getPreferredSpan(View.Y_AXIS);
	} else {
	    // plain text
	    height += fontHeight;
	}
        Icon icon = getIconForTab(tabIndex);
        Insets tabInsets = getTabInsets(tabPlacement, tabIndex);

        if (icon != null) {
            height = Math.max(height, icon.getIconHeight());
        }
        height += tabInsets.top + tabInsets.bottom + 2;

        return height;
    } 

    protected int calculateMaxTabHeight(int tabPlacement) {
        FontMetrics metrics = getFontMetrics(tabContext.getStyle().getFont(
                                             tabContext));
        int tabCount = tabPane.getTabCount();
        int result = 0; 
        int fontHeight = metrics.getHeight();
        for(int i = 0; i < tabCount; i++) {
            result = Math.max(calculateTabHeight(tabPlacement, i, fontHeight), result);
        }
        return result; 
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex,
                                    FontMetrics metrics) {
        Icon icon = getIconForTab(tabIndex);
        Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
        int width = tabInsets.left + tabInsets.right + 3;

        if (icon != null) {
            // PENDING: This should call to SynthIcon for size.
            width += icon.getIconWidth() + getTextIconGap();
        }
	View v = getTextViewForTab(tabIndex);
	if (v != null) {
	    // html
	    width += (int)v.getPreferredSpan(View.X_AXIS);
	} else {
	    // plain text
	    String title = tabPane.getTitleAt(tabIndex);
            width += tabContext.getStyle().getSynthGraphics(tabContext).
                        computeStringWidth(tabContext, metrics.getFont(),
                        metrics, title);
	}
	
        return width;
    }

    private int getTextIconGap() {
        Integer gap = (Integer)tabContext.getStyle().get(tabContext,"textIconGap");

        if (gap == null) {
            return 0;
        }
        return gap.intValue();
    }

    protected int calculateMaxTabWidth(int tabPlacement) {
        FontMetrics metrics = getFontMetrics(tabContext.getStyle().getFont(
                                     tabContext));
        int tabCount = tabPane.getTabCount();
        int result = 0; 
        for(int i = 0; i < tabCount; i++) {
            result = Math.max(calculateTabWidth(tabPlacement, i, metrics),
                              result);
        }
        return result; 
    }

    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount,
                                         int maxTabHeight) {
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
        int tabRunOverlay = getTabRunOverlay(tabPlacement);
        return (horizRunCount > 0? 
                horizRunCount * (maxTabHeight-tabRunOverlay) + tabRunOverlay + 
                tabAreaInsets.top + tabAreaInsets.bottom : 
                0);
    }

    protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount,
                                        int maxTabWidth) { 
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
        int tabRunOverlay = getTabRunOverlay(tabPlacement);
        return (vertRunCount > 0? 
                vertRunCount * (maxTabWidth-tabRunOverlay) + tabRunOverlay + 
                tabAreaInsets.left + tabAreaInsets.right : 
                0);
    }

    protected Insets getTabInsets(int tabPlacement, int tabIndex) {
        tabContext.update(tabIndex, false, (mouseIndex == tabIndex),
                                           (focusIndex == tabIndex));
        return tabContext.getStyle().getInsets(tabContext, null);
    }

    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        Insets insets = (Insets)tabContext.getStyle().get(tabContext,
                                                "selectedTabPadInsets");

        if (insets == null) {
            return new Insets(0, 0, 0, 0);
        }
        Insets result = new Insets(0, 0, 0, 0);
        rotateInsets(insets, new Insets(0, 0, 0, 0), tabPlacement);
        return result;
    }

    protected Insets getTabAreaInsets(int tabPlacement) {
        return tabAreaContext.getStyle().getInsets(tabAreaContext, null);
    }

    protected Insets getContentBorderInsets(int tabPlacement) {
        return tabContentContext.getStyle().getInsets(tabContentContext,
                                                          null);
    }
    
    protected FontMetrics getFontMetrics() {
        return getFontMetrics(tabContext.getStyle().getFont(tabContext));
    }

    protected FontMetrics getFontMetrics(Font font) {
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }


// Tab Navigation methods

    protected void navigateSelectedTab(int direction) {
        int tabPlacement = tabPane.getTabPlacement();
        int current = selectionFollowsFocus? tabPane.getSelectedIndex() :
	    focusIndex;
        int tabCount = tabPane.getTabCount();

        // If we have no tabs then don't navigate.
        if (tabCount <= 0) {
            return;
        }

        int offset;
        switch(tabPlacement) {
	  case NEXT:
	      selectNextTab(current);
	      break;
	  case PREVIOUS:
	      selectPreviousTab(current);
	      break;
          case LEFT:
          case RIGHT:
              switch(direction) {
                case NORTH:
                    selectPreviousTabInRun(current);
                    break;
                case SOUTH:
                    selectNextTabInRun(current);
                    break;
                case WEST:
                    offset = getTabRunOffset(tabPlacement, tabCount, current, false);
                    selectAdjacentRunTab(tabPlacement, current, offset);
                    break;
                case EAST:
                    offset = getTabRunOffset(tabPlacement, tabCount, current, true);
                    selectAdjacentRunTab(tabPlacement, current, offset);
                    break;
                default:
              }
              break;
          case BOTTOM:
          case TOP:
          default:
              switch(direction) {
                case NORTH:
                    offset = getTabRunOffset(tabPlacement, tabCount, current, false);
                    selectAdjacentRunTab(tabPlacement, current, offset);
                    break;
                case SOUTH:
                    offset = getTabRunOffset(tabPlacement, tabCount, current, true);
                    selectAdjacentRunTab(tabPlacement, current, offset);
                    break;
                case EAST:
                    selectNextTabInRun(current);
                    break;
                case WEST:
                    selectPreviousTabInRun(current);
                    break;
                default:
              }
        }
    }

    protected void selectNextTabInRun(int current) {
        int tabCount = tabPane.getTabCount();
        int tabIndex = getNextTabIndexInRun(tabCount, current);

	while(tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
	    tabIndex = getNextTabIndexInRun(tabCount, tabIndex);
	}
	navigateTo(tabIndex);
    }

    protected void selectPreviousTabInRun(int current) {
        int tabCount = tabPane.getTabCount();
        int tabIndex = getPreviousTabIndexInRun(tabCount, current);

	while(tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
	    tabIndex = getPreviousTabIndexInRun(tabCount, tabIndex);
	}
	navigateTo(tabIndex);
    }

    protected void selectNextTab(int current) {
        int tabIndex = getNextTabIndex(current);
        
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getNextTabIndex(tabIndex);
        }
        navigateTo(tabIndex);
    }

    protected void selectPreviousTab(int current) {
        int tabIndex = getPreviousTabIndex(current);
        
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getPreviousTabIndex(tabIndex);
        }
        navigateTo(tabIndex);
    }

    protected void selectAdjacentRunTab(int tabPlacement, 
                                        int tabIndex, int offset) {
        if ( runCount < 2 ) {
            return; 
        }
        int newIndex;
        Rectangle r = rects[tabIndex]; 
        switch(tabPlacement) {
          case LEFT:
          case RIGHT:
              newIndex = getTabAtLocation(r.x + r.width/2 + offset,
                                       r.y + r.height/2);
              break;
          case BOTTOM:  
          case TOP:
          default:
              newIndex = getTabAtLocation(r.x + r.width/2, 
                                       r.y + r.height/2 + offset);
        }
        if (newIndex != -1) {
            while (!tabPane.isEnabledAt(newIndex) && newIndex != tabIndex) {
                newIndex = getNextTabIndex(newIndex);
            }        
            navigateTo(newIndex);
        }
    }

    private void navigateTo(int index) {
	if (selectionFollowsFocus) {
            setFocusIndex(index);
	    tabPane.setSelectedIndex(index);
	} else {
            // Just move focus (not selection)
            int oldFocusIndex = focusIndex;
	    setFocusIndex(index);
	    tabPane.repaint(getTabBounds(tabPane, focusIndex));
            if (oldFocusIndex != -1) {
		tabPane.repaint(getTabBounds(tabPane, oldFocusIndex));
	    }
	}
    }

    void setFocusIndex(int index) {
        focusIndex = index;
    }

    int getFocusIndex() {
        return focusIndex;
    }

    protected int getTabRunOffset(int tabPlacement, int tabCount, 
                                  int tabIndex, boolean forward) {
        int run = getRunForTab(tabCount, tabIndex);
        int offset;
        switch(tabPlacement) {
          case LEFT: {
              if (run == 0) {
                  offset = (forward? 
                            -(calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth)-maxTabWidth) :
                            -maxTabWidth);

              } else if (run == runCount - 1) {
                  offset = (forward?
                            maxTabWidth :
                            calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth)-maxTabWidth);
              } else {
                  offset = (forward? maxTabWidth : -maxTabWidth);
              }
              break;
          }
          case RIGHT: {
              if (run == 0) {
                  offset = (forward? 
                            maxTabWidth :
                            calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth)-maxTabWidth);
              } else if (run == runCount - 1) {
                  offset = (forward?
                            -(calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth)-maxTabWidth) :
                            -maxTabWidth);
              } else {
                  offset = (forward? maxTabWidth : -maxTabWidth);
              } 
              break;
          }
          case BOTTOM: {
              if (run == 0) {
                  offset = (forward? 
                            maxTabHeight :
                            calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)-maxTabHeight);
              } else if (run == runCount - 1) {
                  offset = (forward?
                            -(calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)-maxTabHeight) :
                            -maxTabHeight);
              } else {
                  offset = (forward? maxTabHeight : -maxTabHeight);
              } 
              break;
          }
          case TOP:
          default: {
              if (run == 0) {
                  offset = (forward? 
                            -(calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)-maxTabHeight) :
                            -maxTabHeight);
              } else if (run == runCount - 1) {
                  offset = (forward?
                            maxTabHeight :
                            calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)-maxTabHeight);
              } else {
                  offset = (forward? maxTabHeight : -maxTabHeight);
              }
          }
        }
        return offset;
    }

    protected int getPreviousTabIndex(int base) {
        int tabIndex = (base - 1 >= 0? base - 1 : tabPane.getTabCount() - 1);
        return (tabIndex >= 0? tabIndex : 0);
    }

    protected int getNextTabIndex(int base) {
        return (base+1)%tabPane.getTabCount();
    }

    protected int getNextTabIndexInRun(int tabCount, int base) {
        if (runCount < 2) {
	    return getNextTabIndex(base);
	}
	int currentRun = getRunForTab(tabCount, base);
	int next = getNextTabIndex(base);
	if (next == tabRuns[getNextTabRun(currentRun)]) {
	    return tabRuns[currentRun];
	}
	return next;
    }

    protected int getPreviousTabIndexInRun(int tabCount, int base) {
        if (runCount < 2) {
	    return getPreviousTabIndex(base);
	}
	int currentRun = getRunForTab(tabCount, base);
	if (base == tabRuns[currentRun]) {
	    int previous = tabRuns[getNextTabRun(currentRun)]-1;
	    return (previous != -1? previous : tabCount-1);
	}
	return getPreviousTabIndex(base);
    }

    protected int getPreviousTabRun(int baseRun) {
        int runIndex = (baseRun - 1 >= 0? baseRun - 1 : runCount - 1);
        return (runIndex >= 0? runIndex : 0);
    }

    protected int getNextTabRun(int baseRun) {
        return (baseRun+1)%runCount;
    }

    protected static void rotateInsets(Insets topInsets, Insets targetInsets, int targetPlacement) {
        
        switch(targetPlacement) {
          case LEFT:
              targetInsets.top = topInsets.left;
              targetInsets.left = topInsets.top;
              targetInsets.bottom = topInsets.right;
              targetInsets.right = topInsets.bottom;
              break;
          case BOTTOM:
              targetInsets.top = topInsets.bottom;
              targetInsets.left = topInsets.left;
              targetInsets.bottom = topInsets.top;
              targetInsets.right = topInsets.right;              
              break;
          case RIGHT:
              targetInsets.top = topInsets.left;
              targetInsets.left = topInsets.bottom;
              targetInsets.bottom = topInsets.right;
              targetInsets.right = topInsets.top;
              break;
          case TOP:
          default:
              targetInsets.top = topInsets.top;
              targetInsets.left = topInsets.left;
              targetInsets.bottom = topInsets.bottom;
              targetInsets.right = topInsets.right;              
        }
    }  

   // REMIND(aim,7/29/98): This method should be made
   // protected in the next release where
   // API changes are allowed
   //
   boolean requestFocusForVisibleComponent() {
        Component visibleComponent = getVisibleComponent();
        if (visibleComponent.isFocusTraversable()) {
             visibleComponent.requestFocus();
             return true;
        } else if (visibleComponent instanceof JComponent) {
             if (((JComponent)visibleComponent).requestDefaultFocus()) {
                 return true;
             }
        }
        return false;
    }
  


    private static class RightAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(EAST);
        }
    };
    
    private static class LeftAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(WEST);
        }
    };

    private static class UpAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(NORTH);
        }
    };

    private static class DownAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(SOUTH);
        }
    };

    private static class NextAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(NEXT);
        }
    };

    private static class PreviousAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(PREVIOUS);
        }
    };

    private static class PageUpAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
	    int tabPlacement = pane.getTabPlacement();
	    if (tabPlacement == TOP|| tabPlacement == BOTTOM) {
		ui.navigateSelectedTab(WEST); 
	    } else {
		ui.navigateSelectedTab(NORTH);
	    }
        }
    };

    private static class PageDownAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
	    int tabPlacement = pane.getTabPlacement();
	    if (tabPlacement == TOP || tabPlacement == BOTTOM) {
		ui.navigateSelectedTab(EAST); 
	    } else {
		ui.navigateSelectedTab(SOUTH);
	    }
        }
    };

    private static class RequestFocusAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
            pane.requestFocus();
        }
    };

    private static class RequestFocusForVisibleAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            ui.requestFocusForVisibleComponent();
        }
    };


    /**
     * Selects a tab in the JTabbedPane based on the String of the
     * action command. The tab selected is based on the first tab that
     * has a mnemonic matching the first character of the action command.
     */
    private static class SetSelectedIndexAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane)e.getSource();

            if (pane != null && (pane.getUI() instanceof SynthTabbedPaneUI)) {
                SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
                String command = e.getActionCommand();

                if (command != null && command.length() > 0) {
                    int mnemonic = (int)e.getActionCommand().charAt(0);
                    if (mnemonic >= 'a' && mnemonic <='z') {
                        mnemonic  -= ('a' - 'A');
                    }
                    Integer index = (Integer)ui.mnemonicToIndexMap.
                                 get(new Integer(mnemonic));
                    if (index != null && pane.isEnabledAt(index.intValue())) {
                        pane.setSelectedIndex(index.intValue());
                    }
                }
	    }
        }
    };

    private static class SelectFocusIndexAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane)e.getSource();
            SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();
            int focusIndex = ui.getFocusIndex();
            if (focusIndex != -1) {
                pane.setSelectedIndex(focusIndex);
	    }
	}
    };

    private static class ScrollTabsForwardAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = null;
	    Object src = e.getSource();
	    if (src instanceof JTabbedPane) {
		pane = (JTabbedPane)src;
	    } else if (src instanceof ScrollableTabButton) {
	        pane = (JTabbedPane)((ScrollableTabButton)src).getParent();
	    } else {
		return; // shouldn't happen
	    }
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();		

	    if (ui.scrollableTabLayoutEnabled()) {
		ui.tabScroller.scrollForward(pane.getTabPlacement());
	    }
        }
    }

    private static class ScrollTabsBackwardAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = null;
	    Object src = e.getSource();
	    if (src instanceof JTabbedPane) {
		pane = (JTabbedPane)src;
	    } else if (src instanceof ScrollableTabButton) {
	        pane = (JTabbedPane)((ScrollableTabButton)src).getParent();
	    } else {
		return; // shouldn't happen
	    }
	    SynthTabbedPaneUI ui = (SynthTabbedPaneUI)pane.getUI();		

	    if (ui.scrollableTabLayoutEnabled()) {
                ui.tabScroller.scrollBackward(pane.getTabPlacement());
	    }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of SynthTabbedPaneUI.
     */  
    class TabbedPaneLayout implements LayoutManager {

        public void addLayoutComponent(String name, Component comp) {}
    
        public void removeLayoutComponent(Component comp) {}
    
        public Dimension preferredLayoutSize(Container parent) {
            return calculateSize(false);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return calculateSize(true);
        }

        protected Dimension calculateSize(boolean minimum) {
            int tabPlacement = tabPane.getTabPlacement();
            Insets insets = tabPane.getInsets();
            Insets contentInsets = getContentBorderInsets(tabPlacement);
	    Insets tabAreaInsets = getTabAreaInsets(tabPlacement);

            Dimension zeroSize = new Dimension(0,0);
            int height = contentInsets.top + contentInsets.bottom; 
            int width = contentInsets.left + contentInsets.right;
            int cWidth = 0;
            int cHeight = 0;

	    // Determine minimum size required to display largest
	    // child in each dimension
	    //
            for (int i = 0; i < tabPane.getTabCount(); i++) {
                Component component = tabPane.getComponentAt(i);
		if (component != null) {
                    Dimension size = zeroSize;
                    size = minimum? component.getMinimumSize() : 
                                component.getPreferredSize();
                      
                    if (size != null) {
                        cHeight = Math.max(size.height, cHeight);
                        cWidth = Math.max(size.width, cWidth);
                    }
		}
            }
	    // Add content border insets to minimum size
            width += cWidth;
            height += cHeight;
            int tabExtent = 0;

	    // Calculate how much space the tabs will need, based on the
	    // minimum size required to display largest child + content border
	    //
            switch(tabPlacement) {
              case LEFT:
              case RIGHT:
	          height = Math.max(height, calculateMaxTabHeight(tabPlacement) + 
				             tabAreaInsets.top + tabAreaInsets.bottom);
                  tabExtent = preferredTabAreaWidth(tabPlacement, height);
                  width += tabExtent;
                  break;
              case TOP:
              case BOTTOM:
              default:
	          width = Math.max(width, calculateMaxTabWidth(tabPlacement) +
				             tabAreaInsets.left + tabAreaInsets.right);
                  tabExtent = preferredTabAreaHeight(tabPlacement, width);
                  height += tabExtent;
            }
            return new Dimension(width + insets.left + insets.right, 
                             height + insets.bottom + insets.top);

        }

        protected int preferredTabAreaHeight(int tabPlacement, int width) {
            FontMetrics metrics = getFontMetrics();
            int tabCount = tabPane.getTabCount();
            int total = 0;
            if (tabCount > 0) {
                int rows = 1;
                int x = 0;

                int maxTabHeight = calculateMaxTabHeight(tabPlacement);
        
                for (int i = 0; i < tabCount; i++) {
                    int tabWidth = calculateTabWidth(tabPlacement, i, metrics);

                    if (x != 0 && x + tabWidth > width) {
                        rows++;
                        x = 0;
                    } 
                    x += tabWidth;
                }
                total = calculateTabAreaHeight(tabPlacement, rows, maxTabHeight);
            }
            return total;
        }

        protected int preferredTabAreaWidth(int tabPlacement, int height) {
            FontMetrics metrics = getFontMetrics();
            int tabCount = tabPane.getTabCount();
            int total = 0;
            if (tabCount > 0) {
                int columns = 1;
                int y = 0;
                int fontHeight = metrics.getHeight();

                maxTabWidth = calculateMaxTabWidth(tabPlacement);
        
                for (int i = 0; i < tabCount; i++) {
                    int tabHeight = calculateTabHeight(tabPlacement, i, fontHeight);

                    if (y != 0 && y + tabHeight > height) { 
                        columns++;
                        y = 0;
                    }
                    y += tabHeight;
                }                    
                total = calculateTabAreaWidth(tabPlacement, columns, maxTabWidth);
            }
            return total;
        }

        public void layoutContainer(Container parent) {
            setMouseOverTab(-1);

            int tabPlacement = tabPane.getTabPlacement();
            Insets insets = tabPane.getInsets();
            int selectedIndex = tabPane.getSelectedIndex();
            Component visibleComponent = getVisibleComponent();

            calculateLayoutInfo();

            if (selectedIndex < 0) {
                if (visibleComponent != null) {
                    // The last tab was removed, so remove the component
                    setVisibleComponent(null);
                }
            } else {
                int cx, cy, cw, ch;
                int totalTabWidth = 0;
                int totalTabHeight = 0;
                Insets contentInsets = getContentBorderInsets(tabPlacement);

                Component selectedComponent = tabPane.getComponentAt(selectedIndex);
                boolean shouldChangeFocus = false;

                // In order to allow programs to use a single component
                // as the display for multiple tabs, we will not change
                // the visible compnent if the currently selected tab
                // has a null component.  This is a bit dicey, as we don't
                // explicitly state we support this in the spec, but since
                // programs are now depending on this, we're making it work.
                //
                if (selectedComponent != null) {
                    if (selectedComponent != visibleComponent &&
                            visibleComponent != null) {
                        if (SwingUtilities.findFocusOwner(visibleComponent) != null) {
                           shouldChangeFocus = true;
                        }
                    }                   
                    setVisibleComponent(selectedComponent);
                }

                Rectangle bounds = tabPane.getBounds();
                int numChildren = tabPane.getComponentCount();

                if (numChildren > 0) {

                    switch(tabPlacement) {
                      case LEFT:
                        totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        cx = insets.left + totalTabWidth + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        break;
                      case RIGHT:
                        totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        break;
                      case BOTTOM:
                        totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
                        break;                   
                      case TOP:
                     default:
                       totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                       cx = insets.left + contentInsets.left;
                       cy = insets.top + totalTabHeight + contentInsets.top;
                    }
                
                    cw = bounds.width - totalTabWidth -
                                 insets.left - insets.right - 
                                 contentInsets.left - contentInsets.right;
                    ch = bounds.height - totalTabHeight -
                                 insets.top - insets.bottom -
                                 contentInsets.top - contentInsets.bottom;

                    for (int i=0; i < numChildren; i++) {
			Component child = tabPane.getComponent(i);
                        child.setBounds(cx, cy, cw, ch);
                    }
                }

                if (shouldChangeFocus) {
                    if (!requestFocusForVisibleComponent()) {
                       tabPane.requestFocus();
                    }
                }
            }
        }

        public void calculateLayoutInfo() {
            int tabCount = tabPane.getTabCount(); 
            assureRectsCreated(tabCount);
            calculateTabRects(tabPane.getTabPlacement(), tabCount);    
        }

        protected void calculateTabRects(int tabPlacement, int tabCount) {
            FontMetrics metrics = getFontMetrics();
            Dimension size = tabPane.getSize();
            Insets insets = tabPane.getInsets(); 
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
            int fontHeight = metrics.getHeight();
            int selectedIndex = tabPane.getSelectedIndex();
            int tabRunOverlay;
            int i, j;
            int x, y;
            int returnAt;
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
	    boolean leftToRight = SynthLookAndFeel.isLeftToRight(tabPane);

            //
            // Calculate bounds within which a tab run must fit
            //
            switch(tabPlacement) {
              case LEFT:
                  maxTabWidth = calculateMaxTabWidth(tabPlacement);
                  x = insets.left + tabAreaInsets.left;
                  y = insets.top + tabAreaInsets.top;
                  returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                  break;
              case RIGHT:
                  maxTabWidth = calculateMaxTabWidth(tabPlacement);
                  x = size.width - insets.right - tabAreaInsets.right - maxTabWidth;
                  y = insets.top + tabAreaInsets.top;
                  returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                  break;
              case BOTTOM:
                  maxTabHeight = calculateMaxTabHeight(tabPlacement);
                  x = insets.left + tabAreaInsets.left;
                  y = size.height - insets.bottom - tabAreaInsets.bottom - maxTabHeight;
                  returnAt = size.width - (insets.right + tabAreaInsets.right);
                  break;
              case TOP:
              default:
                  maxTabHeight = calculateMaxTabHeight(tabPlacement);
                  x = insets.left + tabAreaInsets.left;
                  y = insets.top + tabAreaInsets.top;
                  returnAt = size.width - (insets.right + tabAreaInsets.right);
                  break;
            }

            tabRunOverlay = getTabRunOverlay(tabPlacement);

            runCount = 0;
            selectedRun = -1;

            if (tabCount == 0) {
                return;
            }

            // Run through tabs and partition them into runs
            Rectangle rect;
            for (i = 0; i < tabCount; i++) {
                rect = rects[i];

                if (!verticalTabRuns) {
                    // Tabs on TOP or BOTTOM....
                    if (i > 0) {
                        rect.x = rects[i-1].x + rects[i-1].width;
                    } else {
                        tabRuns[0] = 0;
                        runCount = 1;
                        maxTabWidth = 0;
                        rect.x = x;
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, metrics);
                    maxTabWidth = Math.max(maxTabWidth, rect.width);

                    // Never move a TAB down a run if it is in the first column. 
                    // Even if there isn't enough room, moving it to a fresh 
                    // line won't help.
                    if (rect.x != 2 + insets.left && rect.x + rect.width > returnAt) {
                        if (runCount > tabRuns.length - 1) {
                            expandTabRunsArray();
                        }
                        tabRuns[runCount] = i;
                        runCount++;
                        rect.x = x;
                    }
                    // Initialize y position in case there's just one run
                    rect.y = y;
                    rect.height = maxTabHeight/* - 2*/;

                } else {
                    // Tabs on LEFT or RIGHT...
                    if (i > 0) {
                        rect.y = rects[i-1].y + rects[i-1].height;
                    } else {
                        tabRuns[0] = 0;
                        runCount = 1;
                        maxTabHeight = 0;
                        rect.y = y;
                    }
                    rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
                    maxTabHeight = Math.max(maxTabHeight, rect.height);

                    // Never move a TAB over a run if it is in the first run. 
                    // Even if there isn't enough room, moving it to a fresh 
                    // column won't help.
                    if (rect.y != 2 + insets.top && rect.y + rect.height > returnAt) {
                        if (runCount > tabRuns.length - 1) {
                            expandTabRunsArray();
                        }
                        tabRuns[runCount] = i;
                        runCount++;
                        rect.y = y;
                    }
                    // Initialize x position in case there's just one column
                    rect.x = x;
                    rect.width = maxTabWidth/* - 2*/;

                }            
                if (i == selectedIndex) {
                    selectedRun = runCount - 1;
                }
            }

            if (runCount > 1) {
                // Re-distribute tabs in case last run has leftover space
                normalizeTabRuns(tabPlacement, tabCount, verticalTabRuns? y : x, returnAt);

                selectedRun = getRunForTab(tabCount, selectedIndex);

                // Rotate run array so that selected run is first
                if (shouldRotateTabRuns(tabPlacement)) {
                    rotateTabRuns(tabPlacement, selectedRun);
                }
            }

            // Step through runs from back to front to calculate
            // tab y locations and to pad runs appropriately
            for (i = runCount - 1; i >= 0; i--) {
                int start = tabRuns[i];
                int next = tabRuns[i == (runCount - 1)? 0 : i + 1];
                int end = (next != 0? next - 1 : tabCount - 1);
                if (!verticalTabRuns) {
                    for (j = start; j <= end; j++) {
                        rect = rects[j];
                        rect.y = y;
                        rect.x += getTabRunIndent(tabPlacement, i);
                    }
                    if (shouldPadTabRun(tabPlacement, i)) {
                        padTabRun(tabPlacement, start, end, returnAt);
                    }
                    if (tabPlacement == BOTTOM) {
                        y -= (maxTabHeight - tabRunOverlay);
                    } else {
                        y += (maxTabHeight - tabRunOverlay);
                    }
                } else {
                    for (j = start; j <= end; j++) {
                        rect = rects[j];
                        rect.x = x;
                        rect.y += getTabRunIndent(tabPlacement, i);
                    }
                    if (shouldPadTabRun(tabPlacement, i)) {
                        padTabRun(tabPlacement, start, end, returnAt);
                    }
                    if (tabPlacement == RIGHT) {
                        x -= (maxTabWidth - tabRunOverlay);
                    } else {
                        x += (maxTabWidth - tabRunOverlay);
                    }
                }           
            }

            // Pad the selected tab so that it appears raised in front
            padSelectedTab(tabPlacement, selectedIndex);

	    // if right to left and tab placement on the top or
	    // the bottom, flip x positions and adjust by widths
	    if (!leftToRight && !verticalTabRuns) {
	        int rightMargin = size.width 
                                  - (insets.right + tabAreaInsets.right);
		for (i = 0; i < tabCount; i++) {
		    rects[i].x = rightMargin - rects[i].x - rects[i].width;
		}
	    }
        }


       /* 
       * Rotates the run-index array so that the selected run is run[0]
       */
        protected void rotateTabRuns(int tabPlacement, int selectedRun) {
            for (int i = 0; i < selectedRun; i++) {
                int save = tabRuns[0];
                for (int j = 1; j < runCount; j++) { 
                    tabRuns[j - 1] = tabRuns[j];
                }
                tabRuns[runCount-1] = save;
            }        
        }

        protected void normalizeTabRuns(int tabPlacement, int tabCount, 
                                     int start, int max) {
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
            int run = runCount - 1;
            boolean keepAdjusting = true;
            double weight = 1.25;

            // At this point the tab runs are packed to fit as many
            // tabs as possible, which can leave the last run with a lot
            // of extra space (resulting in very fat tabs on the last run).
            // So we'll attempt to distribute this extra space more evenly
            // across the runs in order to make the runs look more consistent.
            //
            // Starting with the last run, determine whether the last tab in
            // the previous run would fit (generously) in this run; if so,
            // move tab to current run and shift tabs accordingly.  Cycle
            // through remaining runs using the same algorithm.  
            //
            while (keepAdjusting) {
                int last = lastTabInRun(tabCount, run);
                int prevLast = lastTabInRun(tabCount, run-1);
                int end;
                int prevLastLen;

                if (!verticalTabRuns) {
                    end = rects[last].x + rects[last].width;
                    prevLastLen = (int)(maxTabWidth*weight);
                } else {
                    end = rects[last].y + rects[last].height;
                    prevLastLen = (int)(maxTabHeight*weight*2);
                }
 
                // Check if the run has enough extra space to fit the last tab
                // from the previous row...
                if (max - end > prevLastLen) {

                    // Insert tab from previous row and shift rest over
                    tabRuns[run] = prevLast;
                    if (!verticalTabRuns) {
                        rects[prevLast].x = start;
                    } else {
                        rects[prevLast].y = start;
                    }
                    for (int i = prevLast+1; i <= last; i++) {
                        if (!verticalTabRuns) {
                            rects[i].x = rects[i-1].x + rects[i-1].width;
                        } else {
                            rects[i].y = rects[i-1].y + rects[i-1].height;
                        }
                    } 
 
                } else if (run == runCount - 1) {
                    // no more room left in last run, so we're done!
                    keepAdjusting = false;
                }
                if (run - 1 > 0) {
                    // check previous run next...
                    run -= 1;
                } else {
                    // check last run again...but require a higher ratio
                    // of extraspace-to-tabsize because we don't want to
                    // end up with too many tabs on the last run!
                    run = runCount - 1;
                    weight += .25;
                }
            }                       
        }

        protected void padTabRun(int tabPlacement, int start, int end, int max) {
            Rectangle lastRect = rects[end];
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                int runWidth = (lastRect.x + lastRect.width) - rects[start].x;
                int deltaWidth = max - (lastRect.x + lastRect.width);
                float factor = (float)deltaWidth / (float)runWidth;

                for (int j = start; j <= end; j++) {
                    Rectangle pastRect = rects[j];
                    if (j > start) {
                        pastRect.x = rects[j-1].x + rects[j-1].width;
                    }
                    pastRect.width += Math.round((float)pastRect.width * factor);
                }
                lastRect.width = max - lastRect.x;
            } else {
                int runHeight = (lastRect.y + lastRect.height) - rects[start].y;
                int deltaHeight = max - (lastRect.y + lastRect.height);
                float factor = (float)deltaHeight / (float)runHeight;

                for (int j = start; j <= end; j++) {
                    Rectangle pastRect = rects[j];
                    if (j > start) {
                        pastRect.y = rects[j-1].y + rects[j-1].height;
                    }
                    pastRect.height += Math.round((float)pastRect.height * factor);
                }
                lastRect.height = max - lastRect.y;
            }
        } 

        protected void padSelectedTab(int tabPlacement, int selectedIndex) {

           if (selectedIndex >= 0) {
                Rectangle selRect = rects[selectedIndex];
                Insets padInsets = getSelectedTabPadInsets(tabPlacement);
                selRect.x -= padInsets.left;            
                selRect.width += (padInsets.left + padInsets.right);
                selRect.y -= padInsets.top;
                selRect.height += (padInsets.top + padInsets.bottom);
           }
        } 
    }

    private class TabbedPaneScrollLayout extends TabbedPaneLayout {

        protected int preferredTabAreaHeight(int tabPlacement, int width) {
            return calculateMaxTabHeight(tabPlacement);
        }

        protected int preferredTabAreaWidth(int tabPlacement, int height) {
	    return calculateMaxTabWidth(tabPlacement);
        }

        public void layoutContainer(Container parent) {
            int tabPlacement = tabPane.getTabPlacement();
	    int tabCount = tabPane.getTabCount();
            Insets insets = tabPane.getInsets();
            int selectedIndex = tabPane.getSelectedIndex();
            Component visibleComponent = getVisibleComponent();

            calculateLayoutInfo();

            if (selectedIndex < 0) {
                if (visibleComponent != null) {
                    // The last tab was removed, so remove the component
                    setVisibleComponent(null);
                }
            } else {
                Component selectedComponent = tabPane.getComponentAt(selectedIndex);
                boolean shouldChangeFocus = false;

                // In order to allow programs to use a single component
                // as the display for multiple tabs, we will not change
                // the visible compnent if the currently selected tab
                // has a null component.  This is a bit dicey, as we don't
                // explicitly state we support this in the spec, but since
                // programs are now depending on this, we're making it work.
                //
                if (selectedComponent != null) {
                    if (selectedComponent != visibleComponent &&
                            visibleComponent != null) {
                        if (SwingUtilities.findFocusOwner(visibleComponent) != null) {
                            shouldChangeFocus = true;
                        }                   
                    } 
                    setVisibleComponent(selectedComponent);
                }
		int tx, ty, tw, th; // tab area bounds
                int cx, cy, cw, ch; // content area bounds
                Insets contentInsets = getContentBorderInsets(tabPlacement);
                Rectangle bounds = tabPane.getBounds();
                int numChildren = tabPane.getComponentCount();

                if (numChildren > 0) {
                    switch(tabPlacement) {
                      case LEFT:
			// calculate tab area bounds
                        tw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
			th = bounds.height - insets.top - insets.bottom;
			tx = insets.left;
			ty = insets.top;

			// calculate content area bounds
                        cx = tx + tw + contentInsets.left;
                        cy = ty + contentInsets.top;
			cw = bounds.width - insets.left - insets.right - tw -
			         contentInsets.left - contentInsets.right;
			ch = bounds.height - insets.top - insets.bottom - 
                                 contentInsets.top - contentInsets.bottom;
                        break;
                      case RIGHT:
			// calculate tab area bounds
                        tw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
			th = bounds.height - insets.top - insets.bottom;
			tx = bounds.width - insets.right - tw;
			ty = insets.top;

			// calculate content area bounds
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
			cw = bounds.width - insets.left - insets.right - tw -
			         contentInsets.left - contentInsets.right;
			ch = bounds.height - insets.top - insets.bottom - 
                                 contentInsets.top - contentInsets.bottom;
                        break;
                      case BOTTOM:
			// calculate tab area bounds
			tw = bounds.width - insets.left - insets.right;
                        th = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
			tx = insets.left;
			ty = bounds.height - insets.bottom - th;

			// calculate content area bounds
                        cx = insets.left + contentInsets.left;
                        cy = insets.top + contentInsets.top;
			cw = bounds.width - insets.left - insets.right - 
			         contentInsets.left - contentInsets.right;
			ch = bounds.height - insets.top - insets.bottom - th -
                                 contentInsets.top - contentInsets.bottom;
                        break;                   
                      case TOP:
                      default:
			// calculate tab area bounds
			tw = bounds.width - insets.left - insets.right;
                        th = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
			tx = insets.left;
			ty = insets.top;

			// calculate content area bounds
                        cx = tx + contentInsets.left;
                        cy = ty + th + contentInsets.top;
			cw = bounds.width - insets.left - insets.right - 
			         contentInsets.left - contentInsets.right;
			ch = bounds.height - insets.top - insets.bottom - th -
                                 contentInsets.top - contentInsets.bottom;
                    }

                    for (int i=0; i < numChildren; i++) {
			Component child = tabPane.getComponent(i);

    	                if (child instanceof ScrollableTabViewport) {
		            JViewport viewport = (JViewport)child;
			    Rectangle viewRect = viewport.getViewRect();
			    int vw = tw;
			    int vh = th;
		            switch(tabPlacement) {
		              case LEFT:
		              case RIGHT:
				int totalTabHeight = rects[tabCount-1].y + rects[tabCount-1].height;
				if (totalTabHeight > th) {
				    // Allow space for scrollbuttons
				    vh = Math.max(th - 36, 36);
			            if (totalTabHeight - viewRect.y <= vh) {
				          // Scrolled to the end, so ensure the viewport size is
					  // such that the scroll offset aligns with a tab
			                  vh = totalTabHeight - viewRect.y;
			             }
		                } 
				break;
      		              case BOTTOM:
		              case TOP:
		              default: 
		                int totalTabWidth = rects[tabCount-1].x + rects[tabCount-1].width;
                                if (totalTabWidth > tw) {
			              // Need to allow space for scrollbuttons
			              vw = Math.max(tw - 36, 36);;
			              if (totalTabWidth - viewRect.x <= vw) {
				          // Scrolled to the end, so ensure the viewport size is
					  // such that the scroll offset aligns with a tab
			                  vw = totalTabWidth - viewRect.x;
			              }
		                }	
		            }	
		            child.setBounds(tx, ty, vw, vh);			  
	            
	                } else if (child instanceof ScrollableTabButton) {
		            ScrollableTabButton scrollbutton = (ScrollableTabButton)child;
			    Dimension bsize = scrollbutton.getPreferredSize();
			    int bx = 0;
			    int by = 0;
			    int bw = bsize.width;
			    int bh = bsize.height;
			    boolean visible = false;
		
		            switch(tabPlacement) {
		              case LEFT:
		              case RIGHT:
				int totalTabHeight = rects[tabCount-1].y + rects[tabCount-1].height;
				if (totalTabHeight > th) {
				    int dir = scrollbutton.scrollsForward()? SOUTH : NORTH;
				    scrollbutton.setDirection(dir);
				    visible = true;
				    bx = (tabPlacement == LEFT? tx + tw - bsize.width : tx);
				    by = dir == SOUTH?
					bounds.height - insets.bottom - bsize.height :
					bounds.height - insets.bottom - 2*bsize.height;
				}
				break;  				    

		              case BOTTOM:
		              case TOP:
		              default:
		                int totalTabWidth = rects[tabCount-1].x + rects[tabCount-1].width;

		                if (totalTabWidth > tw) {
				    int dir = scrollbutton.scrollsForward()? EAST : WEST;
				    scrollbutton.setDirection(dir);
				    visible = true;
				    bx = dir == EAST? 
				        bounds.width - insets.left - bsize.width :
					bounds.width - insets.left - 2*bsize.width;
				    by = (tabPlacement == TOP? ty + th - bsize.height : ty);
		                } 
		            }
			    child.setVisible(visible);
			    if (visible) {
			        child.setBounds(bx, by, bw, bh);
			    }

	                } else {
			    // All content children...
                            child.setBounds(cx, cy, cw, ch);
	                }
		    }
                    if (shouldChangeFocus) {
                        if (!requestFocusForVisibleComponent()) {
                           tabPane.requestFocus();
                        }
		    }
                }
            }
        }

        protected void calculateTabRects(int tabPlacement, int tabCount) {
            FontMetrics metrics = getFontMetrics();
            Dimension size = tabPane.getSize();
            Insets insets = tabPane.getInsets(); 
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
            int fontHeight = metrics.getHeight();
            int selectedIndex = tabPane.getSelectedIndex();
            int i, j;
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
	    boolean leftToRight = SynthLookAndFeel.isLeftToRight(tabPane);
	    int x = tabAreaInsets.left;
	    int y = tabAreaInsets.top;
	    int totalWidth = 0;
	    int totalHeight = 0;

            //
            // Calculate bounds within which a tab run must fit
            //
            switch(tabPlacement) {
              case LEFT:
              case RIGHT:
                  maxTabWidth = calculateMaxTabWidth(tabPlacement);
                  break;
              case BOTTOM:
              case TOP:
              default:
                  maxTabHeight = calculateMaxTabHeight(tabPlacement);
            }

            runCount = 0;
            selectedRun = -1;

            if (tabCount == 0) {
                return;
            }

	    selectedRun = 0;
	    runCount = 1;

            // Run through tabs and lay them out in a single run
            Rectangle rect;
            for (i = 0; i < tabCount; i++) {
                rect = rects[i];

                if (!verticalTabRuns) {
                    // Tabs on TOP or BOTTOM....
                    if (i > 0) {
                        rect.x = rects[i-1].x + rects[i-1].width;
                    } else {
                        tabRuns[0] = 0;
                        maxTabWidth = 0;
			totalHeight += maxTabHeight;
                        rect.x = x;
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, metrics);
		    totalWidth = rect.x + rect.width;
                    maxTabWidth = Math.max(maxTabWidth, rect.width);

                    rect.y = y;
                    rect.height = maxTabHeight/* - 2*/;

                } else {
                    // Tabs on LEFT or RIGHT...
                    if (i > 0) {
                        rect.y = rects[i-1].y + rects[i-1].height;
                    } else {
                        tabRuns[0] = 0;
                        maxTabHeight = 0;
			totalWidth = maxTabWidth;
                        rect.y = y;
                    }
                    rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
		    totalHeight = rect.y + rect.height;
                    maxTabHeight = Math.max(maxTabHeight, rect.height);

                    rect.x = x;
                    rect.width = maxTabWidth/* - 2*/;

                }            
            }

	    // if right to left and tab placement on the top or
	    // the bottom, flip x positions and adjust by widths
	    if (!leftToRight && !verticalTabRuns) {
	        int rightMargin = size.width 
                                  - (insets.right + tabAreaInsets.right);
		for (i = 0; i < tabCount; i++) {
		    rects[i].x = rightMargin - rects[i].x - rects[i].width;
		}
	    }
            //tabPanel.setSize(totalWidth, totalHeight);
            tabScroller.tabPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
        }
    }

    private class ScrollableTabSupport implements ChangeListener {
        public ScrollableTabViewport viewport;
	public ScrollableTabPanel tabPanel;
        public ScrollableTabButton scrollForwardButton;
        public ScrollableTabButton scrollBackwardButton;
        public int leadingTabIndex;

	private Point tabViewPosition = new Point(0,0);

        ScrollableTabSupport(int tabPlacement) {
            viewport = new ScrollableTabViewport();  
            tabPanel = new ScrollableTabPanel();
            viewport.setView(tabPanel);
	    viewport.addChangeListener(this);

	    if (tabPlacement == TOP || tabPlacement == BOTTOM) {
	        scrollForwardButton = new ScrollableTabButton(EAST);
		scrollBackwardButton = new ScrollableTabButton(WEST);

	    } else { // tabPlacement = LEFT || RIGHT
	        scrollForwardButton = new ScrollableTabButton(SOUTH);
		scrollBackwardButton = new ScrollableTabButton(NORTH);
	    }
        }

        public void scrollForward(int tabPlacement) {
            Dimension viewSize = viewport.getViewSize();
	    Rectangle viewRect = viewport.getViewRect();

	    if (tabPlacement == TOP || tabPlacement == BOTTOM) {
	        if (viewRect.width >= viewSize.width - viewRect.x) {
		    return; // no room left to scroll
		}
	    } else { // tabPlacement == LEFT || tabPlacement == RIGHT
		if (viewRect.height >= viewSize.height - viewRect.y) {
		    return;
		}
	    }
	    setLeadingTabIndex(tabPlacement, leadingTabIndex+1);
	}

        public void scrollBackward(int tabPlacement) {
	    if (leadingTabIndex == 0) {
		return; // no room left to scroll
	    }
	    setLeadingTabIndex(tabPlacement, leadingTabIndex-1);
	}

        public void setLeadingTabIndex(int tabPlacement, int index) {
	    leadingTabIndex = index;	   
            Dimension viewSize = viewport.getViewSize();
	    Rectangle viewRect = viewport.getViewRect();

            switch(tabPlacement) {
	      case TOP:
	      case BOTTOM:
	        tabViewPosition.x = leadingTabIndex == 0? 0 : rects[leadingTabIndex].x;

	        if ((viewSize.width - tabViewPosition.x) < viewRect.width) {
		    // We've scrolled to the end, so adjust the viewport size
		    // to ensure the view position remains aligned on a tab boundary
		    Dimension extentSize = new Dimension(viewSize.width - tabViewPosition.x, 
						         viewRect.height);
	  	    viewport.setExtentSize(extentSize);
	        }
	        break;
	      case LEFT:
	      case RIGHT:
	        tabViewPosition.y = leadingTabIndex == 0? 0 : rects[leadingTabIndex].y;

	        if ((viewSize.height - tabViewPosition.y) < viewRect.height) {
	        // We've scrolled to the end, so adjust the viewport size
	        // to ensure the view position remains aligned on a tab boundary
	             Dimension extentSize = new Dimension(viewRect.width, 
		 	  		                  viewSize.height - tabViewPosition.y);
		     viewport.setExtentSize(extentSize);
	        }
	    }
	    viewport.setViewPosition(tabViewPosition);
        }

        public void stateChanged(ChangeEvent e) {
	    JViewport viewport = (JViewport)e.getSource();
	    int tabPlacement = tabPane.getTabPlacement();
	    int tabCount = tabPane.getTabCount();
	    Rectangle vpRect = viewport.getBounds();
            Dimension viewSize = viewport.getViewSize();
	    Rectangle viewRect = viewport.getViewRect();

            leadingTabIndex = getClosestTab(viewRect.x, viewRect.y);

            // If the tab isn't right aligned, adjust it.
            if (leadingTabIndex + 1 < tabCount) {
                switch (tabPlacement) {
                case TOP:
                case BOTTOM:
                    if (rects[leadingTabIndex].x < viewRect.x) {
                        leadingTabIndex++;
                    }
                    break;
                case LEFT:
                case RIGHT:
                    if (rects[leadingTabIndex].y < viewRect.y) {
                        leadingTabIndex++;
                    }
                    break;
                }
            }
	    Insets contentInsets = getContentBorderInsets(tabPlacement);
	    switch(tabPlacement) {
	      case LEFT:
		  tabPane.repaint(vpRect.x+vpRect.width, vpRect.y,
				  contentInsets.left, vpRect.height);
		  scrollBackwardButton.setEnabled(viewRect.y > 0);
		  scrollForwardButton.setEnabled(leadingTabIndex < tabCount-1 &&
						 viewSize.height-viewRect.y > viewRect.height);
		  break;
	      case RIGHT:
		  tabPane.repaint(vpRect.x-contentInsets.right, vpRect.y,
				  contentInsets.right, vpRect.height);
		  scrollBackwardButton.setEnabled(viewRect.y > 0);
		  scrollForwardButton.setEnabled(leadingTabIndex < tabCount-1 &&
						 viewSize.height-viewRect.y > viewRect.height);
		  break;
	      case BOTTOM:
		  tabPane.repaint(vpRect.x, vpRect.y-contentInsets.bottom,
				  vpRect.width, contentInsets.bottom);
		  scrollBackwardButton.setEnabled(viewRect.x > 0);
		  scrollForwardButton.setEnabled(leadingTabIndex < tabCount-1 &&
						 viewSize.width-viewRect.x > viewRect.width);
		  break;
	      case TOP:
	      default:
		  tabPane.repaint(vpRect.x, vpRect.y+vpRect.height,
				  vpRect.width, contentInsets.top);
		  scrollBackwardButton.setEnabled(viewRect.x > 0);
		  scrollForwardButton.setEnabled(leadingTabIndex < tabCount-1 &&
						 viewSize.width-viewRect.x > viewRect.width);
	    }
	}

        public String toString() {
	    return new String("viewport.viewSize="+viewport.getViewSize()+"\n"+
	                      "viewport.viewRectangle="+viewport.getViewRect()+"\n"+
			      "leadingTabIndex="+leadingTabIndex+"\n"+
	                      "tabViewPosition="+tabViewPosition);
	}

    }

    private class ScrollableTabViewport extends JViewport implements UIResource {
        public ScrollableTabViewport() {
	    super();
	    setScrollMode(SIMPLE_SCROLL_MODE);
	}
    }

    private class ScrollableTabPanel extends JPanel implements UIResource {
        public ScrollableTabPanel() {
	    setLayout(null);
        }
        public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    SynthTabbedPaneUI.this.paintTabArea(
                tabAreaContext, g, tabPane.getTabPlacement(),
                tabPane.getSelectedIndex(), getBounds());
            
	}
    }

    private class ScrollableTabButton extends SynthArrowButton implements
            UIResource, SwingConstants {
        public ScrollableTabButton(int direction) {
            super(direction);
        }

        public boolean scrollsForward() {
            return getDirection() == EAST || getDirection() == SOUTH;
        }
    }

// Controller: event listeners

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of SynthTabbedPaneUI.
     */  
    class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    String name = e.getPropertyName();
            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle(pane);
            }
            if ("mnemonicAt".equals(name)) {
                updateMnemonics();
                pane.repaint();
            }
            else if ("displayedMnemonicIndexAt".equals(name)) {
                pane.repaint();
            }
	    else if ( name.equals("indexForTitle") ) {
		int index = ((Integer)e.getNewValue()).intValue();
		String title = tabPane.getTitleAt(index);
		if (BasicHTML.isHTMLString(title)) {
		    if (htmlViews==null) {    // Initialize vector
			htmlViews = createHTMLVector();
		    } else {                  // Vector already exists
			View v = BasicHTML.createHTMLView(tabPane, title);
			htmlViews.setElementAt(v, index);
		    }
		} else {
		    if (htmlViews != null && htmlViews.elementAt(index) != null) {
			htmlViews.setElementAt(null, index); 
		    }
		}    
                updateMnemonics();
	    } else if (name.equals("tabLayoutPolicy")) {
                // PENDING: YIKES!
	        SynthTabbedPaneUI.this.uninstallUI(pane);
		SynthTabbedPaneUI.this.installUI(pane);
	    }
	}
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of SynthTabbedPaneUI.
     */  
    class TabSelectionHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JTabbedPane tabPane = (JTabbedPane)e.getSource();
            tabPane.revalidate();
            tabPane.repaint();

            if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
                int index = tabPane.getSelectedIndex();
                if (index < rects.length && index != -1) {
                    tabScroller.tabPanel.scrollRectToVisible(rects[index]);
                }
            }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of SynthTabbedPaneUI.
     */  
    class MouseHandler extends MouseInputAdapter {
        public void mousePressed(MouseEvent e) {
            if (!tabPane.isEnabled()) {
                return;
            }
            int tabIndex = getTabAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex != tabPane.getSelectedIndex()) {
                    tabPane.setSelectedIndex(tabIndex);
                }
                if (tabPane.isRequestFocusEnabled() && tabIndex != focusIndex) {
                    tabPane.requestFocus();
                    int oldFocusIndex = focusIndex;
                    setFocusIndex(tabIndex);
                    tabPane.repaint(getTabBounds(tabPane, focusIndex));
                    if (oldFocusIndex != -1) {
                        tabPane.repaint(getTabBounds(tabPane, oldFocusIndex));
		    }
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
            updateMouseOver(e.getX(), e.getY());
        }

        public void mouseMoved(MouseEvent e) {
            updateMouseOver(e.getX(), e.getY());
        }

        public void mouseExited(MouseEvent e) {
            setMouseOverTab(-1);
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of SynthTabbedPaneUI.
     */  
    class FocusHandler extends FocusAdapter {
        public void focusGained(FocusEvent e) { 
           JTabbedPane tabPane = (JTabbedPane)e.getSource();
           int tabCount = tabPane.getTabCount();
	   if (focusIndex == -1) {
               setFocusIndex(tabPane.getSelectedIndex());
	   }
           if (focusIndex != -1 && tabCount > 0
                   && tabCount == rects.length) {
               tabPane.repaint(getTabBounds(tabPane, focusIndex));
           }
        }            
        public void focusLost(FocusEvent e) {
           JTabbedPane tabPane = (JTabbedPane)e.getSource();
           int tabCount = tabPane.getTabCount();
           if (focusIndex != -1 && tabCount > 0
                   && tabCount == rects.length) {
               //PENDING(aim): this gets called unexplicably when an unselected
               // tab is clicked when the tabbedpane doesn't have focus. 
               // need to investigate further!
               tabPane.repaint(getTabBounds(tabPane, focusIndex));
	       setFocusIndex(-1);
           }
        }
    }

    /* GES 2/3/99:
       The container listener code was added to support HTML
       rendering of tab titles.
       
       Ideally, we would be able to listen for property changes
       when a tab is added or its text modified.  At the moment 
       there are no such events because the Beans spec doesn't
       allow 'indexed' property changes (i.e. tab 2's text changed 
       from A to B).
       
       In order to get around this, we listen for tabs to be added
       or removed by listening for the container events.  we then
       queue up a runnable (so the component has a chance to complete
       the add) which checks the tab title of the new component to see
       if it requires HTML rendering.
       
       The Views (one per tab title requiring HTML rendering) are
       stored in the htmlViews Vector, which is only allocated after
       the first time we run into an HTML tab.  Note that this vector
       is kept in step with the number of pages, and nulls are added
       for those pages whose tab title do not require HTML rendering.
       
       This makes it easy for the paint and layout code to tell
       whether to invoke the HTML engine without having to check
       the string during time-sensitive operations.
       
       When we have added a way to listen for tab additions and
       changes to tab text, this code should be removed and
       replaced by something which uses that.  */
    
    private class ContainerHandler implements ContainerListener {
	public void componentAdded(ContainerEvent e) {
	    JTabbedPane tp = (JTabbedPane)e.getContainer();
	    Component child = e.getChild();
	    if (child instanceof UIResource) {
		return;
	    }
	    int index = tp.indexOfComponent(child);
	    String title = tp.getTitleAt(index);
	    boolean isHTML = BasicHTML.isHTMLString(title);
            if (isHTML) {
                if (htmlViews==null) {    // Initialize vector
                    htmlViews = createHTMLVector();
                } else {                  // Vector already exists
                    View v = BasicHTML.createHTMLView(tp, title);
                    htmlViews.insertElementAt(v, index);
                }
            } else {                             // Not HTML
                if (htmlViews!=null) {           // Add placeholder
                    htmlViews.insertElementAt(null, index);
                }                                // else nada!
            }
        }
	public void componentRemoved(ContainerEvent e) {
	    JTabbedPane tp = (JTabbedPane)e.getContainer();
	    Component child = e.getChild();
	    if (child instanceof UIResource) {
		return;
	    }
            // NOTE 4/15/2002 (joutwate):
            // This fix is implemented using client properties since there is
            // currently no IndexPropertyChangeEvent.  Once
            // IndexPropertyChangeEvents have been added this code should be
            // modified to use it.
            Integer indexObj =
                (Integer)tp.getClientProperty("__index_to_remove__");
            if (indexObj != null) {
                int index = indexObj.intValue();
                if (htmlViews != null && htmlViews.size()>=index) {
                    htmlViews.removeElementAt(index);
                }	    
            }
	}
    }
    
    private Vector createHTMLVector() {
	Vector htmlViews = new Vector();
	int count = tabPane.getTabCount();
	if (count>0) {
	    for (int i=0 ; i<count; i++) {
		String title = tabPane.getTitleAt(i);
		if (BasicHTML.isHTMLString(title)) {
		    htmlViews.addElement(BasicHTML.createHTMLView(tabPane, title));
		} else {
		    htmlViews.addElement(null);
		}
	    }
	}
	return htmlViews;
    }
}


