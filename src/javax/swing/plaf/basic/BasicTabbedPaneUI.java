/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.View;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
/**
 * A Basic L&F implementation of TabbedPaneUI.
 *
 * @version 1.87 06/08/99
 * @author Amy Fowler
 * @author Philip Milne
 * @author Steve Wilson
 * @author Tom Santos
 * @author Dave Moore
 */
public class BasicTabbedPaneUI extends TabbedPaneUI implements SwingConstants {


// Instance variables initialized at installation

    protected JTabbedPane tabPane;

    protected Color highlight;
    protected Color lightHighlight;
    protected Color shadow;
    protected Color darkShadow;
    protected Color focus;

    protected int textIconGap;

    protected int tabRunOverlay;

    protected Insets tabInsets;
    protected Insets selectedTabPadInsets;
    protected Insets tabAreaInsets;
    protected Insets contentBorderInsets;

    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of Java 2 platform v1.3.
     */
    protected KeyStroke upKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of Java 2 platform v1.3.
     */
    protected KeyStroke downKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of Java 2 platform v1.3.
     */
    protected KeyStroke leftKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of Java 2 platform v1.3.
     */
    protected KeyStroke rightKey;


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
    protected MouseListener mouseListener;
    protected FocusListener focusListener;
    // PENDING(api): See comment for ContainerHandler
    private   ContainerListener containerListener;

// Private instance data

    private Insets currentPadInsets = new Insets(0,0,0,0);
    private Insets currentTabAreaInsets = new Insets(0,0,0,0);

    private Component visibleComponent;
    // PENDING(api): See comment for ContainerHandler
    private Vector htmlViews;

// UI creation

    public static ComponentUI createUI(JComponent c) {
        return new BasicTabbedPaneUI();
    }

// UI Installation/De-installation
    
    public void installUI(JComponent c) {
        this.tabPane = (JTabbedPane)c;

        c.setLayout(createLayoutManager());
        installDefaults(); 
        installListeners();
        installKeyboardActions();
    }

    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
        c.setLayout(null);
 
        this.tabPane = null;
    }

    protected LayoutManager createLayoutManager() {
        return new TabbedPaneLayout();
    }

    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background",
                                    "TabbedPane.foreground", "TabbedPane.font");     
        highlight = UIManager.getColor("TabbedPane.highlight");
        lightHighlight = UIManager.getColor("TabbedPane.lightHighlight");
        shadow = UIManager.getColor("TabbedPane.shadow");
        darkShadow = UIManager.getColor("TabbedPane.darkShadow");
        focus = UIManager.getColor("TabbedPane.focus");

        textIconGap = UIManager.getInt("TabbedPane.textIconGap");
        tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
        tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
        contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");

    }

    protected void uninstallDefaults() {
        highlight = null;
        lightHighlight = null;
        shadow = null;
        darkShadow = null;
        focus = null;
        tabInsets = null;
        selectedTabPadInsets = null;
        tabAreaInsets = null;
        contentBorderInsets = null;
    }

    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            tabPane.addPropertyChangeListener(propertyChangeListener);
        }
        if ((tabChangeListener = createChangeListener()) != null) {            
            tabPane.addChangeListener(tabChangeListener);
        }
        if ((mouseListener = createMouseListener()) != null) {
            tabPane.addMouseListener(mouseListener);
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
        if (mouseListener != null) {
            tabPane.removeMouseListener(mouseListener);
            mouseListener = null;
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

    protected MouseListener createMouseListener() {
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
	ActionMap am = getActionMap();

	SwingUtilities.replaceUIActionMap(tabPane, am);
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    return (InputMap)UIManager.get("TabbedPane.ancestorInputMap");
	}
	else if (condition == JComponent.WHEN_FOCUSED) {
	    return (InputMap)UIManager.get("TabbedPane.focusInputMap");
	}
	return null;
    }

    ActionMap getActionMap() {
	ActionMap map = (ActionMap)UIManager.get("TabbedPane.actionMap");

	if (map == null) {
	    map = createActionMap();
	    if (map != null) {
		UIManager.put("TabbedPane.actionMap", map);
	    }
	}
	return map;
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("navigateRight", new RightAction());
	map.put("navigateLeft", new LeftAction());
	map.put("navigateUp", new UpAction());
	map.put("navigateDown", new DownAction());
	map.put("navigatePageUp", new PageUpAction());
	map.put("navigatePageDown", new PageDownAction());
	map.put("requestFocus", new RequestFocusAction());
	map.put("requestFocusForVisibleComponent",
		new RequestFocusForVisibleAction());
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

// UI Rendering 

    public void paint(Graphics g, JComponent c) {
        int selectedIndex = tabPane.getSelectedIndex();
        int tabPlacement = tabPane.getTabPlacement();
        int tabCount = tabPane.getTabCount();

        ensureCurrentLayout();

        Rectangle iconRect = new Rectangle(),
                  textRect = new Rectangle();
        Rectangle clipRect = g.getClipBounds();  

        Insets insets = tabPane.getInsets();

        // Paint tabRuns of tabs from back to front
        for (int i = runCount - 1; i >= 0; i--) {
            int start = tabRuns[i];
            int next = tabRuns[(i == runCount - 1)? 0 : i + 1];
            int end = (next != 0? next - 1: tabCount - 1);
            for (int j = start; j <= end; j++) {
                if (rects[j].intersects(clipRect)) {
                    paintTab(g, tabPlacement, rects, j, iconRect, textRect);
                }
            }
        }

        // Paint selected tab if its in the front run
        // since it may overlap other tabs
        if (selectedIndex >= 0 && getRunForTab(tabCount, selectedIndex) == 0) {
            if (rects[selectedIndex].intersects(clipRect)) {
                paintTab(g, tabPlacement, rects, selectedIndex, iconRect, textRect);
            }
        }

        // Paint content border
        paintContentBorder(g, tabPlacement, selectedIndex);

    }

    protected void paintTab(Graphics g, int tabPlacement,
                            Rectangle[] rects, int tabIndex, 
                            Rectangle iconRect, Rectangle textRect) {
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;

        paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, 
                           tabRect.width, tabRect.height, isSelected);
        paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, 
                       tabRect.width, tabRect.height, isSelected);
        
        String title = tabPane.getTitleAt(tabIndex);
        Font font = tabPane.getFont();
        FontMetrics metrics = g.getFontMetrics(font);
        Icon icon = getIconForTab(tabIndex);

        layoutLabel(tabPlacement, metrics, tabIndex, title, icon, 
                    tabRect, iconRect, textRect, isSelected);

        paintText(g, tabPlacement, font, metrics, 
                  tabIndex, title, textRect, isSelected);

        paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);

        paintFocusIndicator(g, tabPlacement, rects, tabIndex, 
                  iconRect, textRect, isSelected);
    }

    protected void layoutLabel(int tabPlacement, 
                               FontMetrics metrics, int tabIndex,
                               String title, Icon icon,
                               Rectangle tabRect, Rectangle iconRect, 
                               Rectangle textRect, boolean isSelected ) {
        textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
        SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
                                           metrics, title, icon,
                                           SwingUtilities.CENTER,
                                           SwingUtilities.CENTER,
                                           SwingUtilities.CENTER,
                                           SwingUtilities.TRAILING,
                                           tabRect,
                                           iconRect,
                                           textRect,
                                           textIconGap);

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

    protected void paintText(Graphics g, int tabPlacement,
                             Font font, FontMetrics metrics, int tabIndex,
                             String title, Rectangle textRect, 
                             boolean isSelected) {

        g.setFont(font);

	View v;
	if (htmlViews!=null &&
	    (v = (View)htmlViews.elementAt(tabIndex))!=null) {
	    v.paint(g, textRect);
	} else {
	    if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
		g.setColor(tabPane.getForegroundAt(tabIndex));
		g.drawString(title,
			     textRect.x,
			     textRect.y + metrics.getAscent());
		
	    } else { // tab disabled
		g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
		g.drawString(title, 
			     textRect.x, textRect.y + metrics.getAscent());
		g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
		g.drawString(title, 
			     textRect.x - 1, textRect.y + metrics.getAscent() - 1);
	    }
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

    protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                       Rectangle[] rects, int tabIndex, 
                                       Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        if (tabPane.hasFocus() && isSelected) {
            int x, y, w, h;
            g.setColor(focus);
            switch(tabPlacement) {
              case LEFT:
                  x = tabRect.x + 3;
                  y = tabRect.y + 3;
                  w = tabRect.width - 5;
                  h = tabRect.height - 6;
                  break;
              case RIGHT:
                  x = tabRect.x + 2;
                  y = tabRect.y + 3;
                  w = tabRect.width - 5;
                  h = tabRect.height - 6;
                  break;
              case BOTTOM:
                  x = tabRect.x + 3;
                  y = tabRect.y + 2;
                  w = tabRect.width - 6;
                  h = tabRect.height - 5;
                  break;
              case TOP:
              default:
                  x = tabRect.x + 3;
                  y = tabRect.y + 3;
                  w = tabRect.width - 6;
                  h = tabRect.height - 5;
            }
            BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        }
    }

    /**
      * this function draws the border around each tab
      * note that this function does now draw the background of the tab.
      * that is done elsewhere
      */
    protected void paintTabBorder(Graphics g, int tabPlacement,
                                  int tabIndex,
                                  int x, int y, int w, int h, 
                                  boolean isSelected ) {
        g.setColor(lightHighlight);  

        switch (tabPlacement) {
          case LEFT:
              g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight
              g.drawLine(x, y+2, x, y+h-3); // left highlight
              g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
              g.drawLine(x+2, y, x+w-1, y); // top highlight

              g.setColor(shadow);
              g.drawLine(x+2, y+h-2, x+w-1, y+h-2); // bottom shadow

              g.setColor(darkShadow);
              g.drawLine(x+2, y+h-1, x+w-1, y+h-1); // bottom dark shadow
              break;
          case RIGHT:
              g.drawLine(x, y, x+w-3, y); // top highlight

              g.setColor(shadow);
              g.drawLine(x, y+h-2, x+w-3, y+h-2); // bottom shadow
              g.drawLine(x+w-2, y+2, x+w-2, y+h-3); // right shadow

              g.setColor(darkShadow);
              g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right dark shadow
              g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
              g.drawLine(x+w-1, y+2, x+w-1, y+h-3); // right dark shadow
              g.drawLine(x, y+h-1, x+w-3, y+h-1); // bottom dark shadow
              break;              
          case BOTTOM:
              g.drawLine(x, y, x, y+h-3); // left highlight
              g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight

              g.setColor(shadow);
              g.drawLine(x+2, y+h-2, x+w-3, y+h-2); // bottom shadow
              g.drawLine(x+w-2, y, x+w-2, y+h-3); // right shadow

              g.setColor(darkShadow);
              g.drawLine(x+2, y+h-1, x+w-3, y+h-1); // bottom dark shadow
              g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
              g.drawLine(x+w-1, y, x+w-1, y+h-3); // right dark shadow
              break;
          case TOP:
          default:           
              g.drawLine(x, y+2, x, y+h-1); // left highlight
              g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
              g.drawLine(x+2, y, x+w-3, y); // top highlight              

              g.setColor(shadow);  
              g.drawLine(x+w-2, y+2, x+w-2, y+h-1); // right shadow

              g.setColor(darkShadow); 
              g.drawLine(x+w-1, y+2, x+w-1, y+h-1); // right dark-shadow
              g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right shadow
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex,
                                      int x, int y, int w, int h, 
                                      boolean isSelected ) {
        g.setColor(tabPane.getBackgroundAt(tabIndex));
        switch(tabPlacement) {
          case LEFT:
              g.fillRect(x+1, y+1, w-2, h-3);
              break;
          case RIGHT:
              g.fillRect(x, y+1, w-2, h-3);
              break;
          case BOTTOM:
              g.fillRect(x+1, y, w-3, h-1);
              break;
          case TOP:
          default:
              g.fillRect(x+1, y+1, w-3, h-1);
        }
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
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
        paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h); 
        paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h); 

    }
         
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                         int selectedIndex, 
                                         int x, int y, int w, int h) {

        g.setColor(lightHighlight);

        if (tabPlacement != TOP || selectedIndex < 0 || 
            (rects[selectedIndex].y + rects[selectedIndex].height + 1 < y)) {
            g.drawLine(x, y, x+w-2, y);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x, y, selRect.x - 1, y);
            if (selRect.x + selRect.width < x + w - 2) {
                g.drawLine(selRect.x + selRect.width, y, 
                           x+w-2, y);
            } else {
                g.setColor(shadow); 
                g.drawLine(x+w-2, y, x+w-2, y);
            }
        }
    }

    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) { 
        g.setColor(lightHighlight); 
        if (tabPlacement != LEFT || selectedIndex < 0 ||
           (rects[selectedIndex].x + rects[selectedIndex].width + 1< x)) {
            g.drawLine(x, y, x, y+h-2);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x, y, x, selRect.y - 1);
            if (selRect.y + selRect.height < y + h - 2) {
                g.drawLine(x, selRect.y + selRect.height, 
                           x, y+h-2);
            } 
        }
    }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) { 
        g.setColor(shadow);
        if (tabPlacement != BOTTOM || selectedIndex < 0 ||
            (rects[selectedIndex].y - 1 > h)) {
            g.drawLine(x+1, y+h-2, x+w-2, y+h-2);
            g.setColor(darkShadow);
            g.drawLine(x, y+h-1, x+w-1, y+h-1);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x+1, y+h-2, selRect.x - 1, y+h-2);
            g.setColor(darkShadow);
            g.drawLine(x, y+h-1, selRect.x - 1, y+h-1);
            if (selRect.x + selRect.width < x + w - 2) {
                g.setColor(shadow);
                g.drawLine(selRect.x + selRect.width, y+h-2, x+w-2, y+h-2);
                g.setColor(darkShadow);
                g.drawLine(selRect.x + selRect.width, y+h-1, x+w-1, y+h-1);
            } 
        }

    }

    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) { 

        g.setColor(shadow);
        if (tabPlacement != RIGHT || selectedIndex < 0 ||
            rects[selectedIndex].x - 1 > w) {
            g.drawLine(x+w-2, y+1, x+w-2, y+h-3);
            g.setColor(darkShadow);
            g.drawLine(x+w-1, y, x+w-1, y+h-1);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x+w-2, y+1, x+w-2, selRect.y - 1);
            g.setColor(darkShadow);
            g.drawLine(x+w-1, y, x+w-1, selRect.y - 1);

            if (selRect.y + selRect.height < y + h - 2) {
                g.setColor(shadow);
                g.drawLine(x+w-2, selRect.y + selRect.height, 
                           x+w-2, y+h-2);
                g.setColor(darkShadow);
                g.drawLine(x+w-1, selRect.y + selRect.height, 
                           x+w-1, y+h-2);
            } 
        }
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

    public Rectangle getTabBounds(JTabbedPane pane, int i) { 
        ensureCurrentLayout();
        return new Rectangle(rects[i]);
    }

    public int getTabRunCount(JTabbedPane pane) {
        return runCount;
    }

    public int tabForCoordinate(JTabbedPane pane, int x, int y) {
        ensureCurrentLayout();
        int tabCount = tabPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (rects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

// BasicTabbedPaneUI methods

    protected Component getVisibleComponent() {
        return visibleComponent;
    }

    protected void setVisibleComponent(Component component) {
        if (visibleComponent == component) {
            return;
        }
        if (visibleComponent != null) {
            visibleComponent.setVisible(false);
        }
        if (component != null) {
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
        return ((!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) && 
                          tabPane.getDisabledIconAt(tabIndex) != null)?
                          tabPane.getDisabledIconAt(tabIndex) : tabPane.getIconAt(tabIndex);
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
	int height = 0;
	View v;
	if ( (htmlViews!=null) && 
	     ((v = (View)htmlViews.elementAt(tabIndex)) != null)) {
	    height += (int)v.getPreferredSpan(View.Y_AXIS);
	} else {
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
        FontMetrics metrics = getFontMetrics();
        int tabCount = tabPane.getTabCount();
        int result = 0; 
        int fontHeight = metrics.getHeight();
        for(int i = 0; i < tabCount; i++) {
            result = Math.max(calculateTabHeight(tabPlacement, i, fontHeight), result);
        }
        return result; 
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        Icon icon = getIconForTab(tabIndex);
        Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
        int width = tabInsets.left + tabInsets.right + 3;
	View v;

        if (icon != null) {
            width += icon.getIconWidth() + textIconGap;
        }
	if ((htmlViews!=null) && 
	    ((v = (View)htmlViews.elementAt(tabIndex)) != null)) {
	    width += (int)v.getPreferredSpan(View.X_AXIS);
	} else {
	    String title = tabPane.getTitleAt(tabIndex);
	    width += SwingUtilities.computeStringWidth(metrics, title);
	}
	
        return width;
    }
    
    protected int calculateMaxTabWidth(int tabPlacement) {
        FontMetrics metrics = getFontMetrics();
        int tabCount = tabPane.getTabCount();
        int result = 0; 
        for(int i = 0; i < tabCount; i++) {
            result = Math.max(calculateTabWidth(tabPlacement, i, metrics), result);
        }
        return result; 
    }

    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
        int tabRunOverlay = getTabRunOverlay(tabPlacement);
        return (horizRunCount > 0? 
                horizRunCount * (maxTabHeight-tabRunOverlay) + tabRunOverlay + 
                tabAreaInsets.top + tabAreaInsets.bottom : 
                0);
    }

    protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) { 
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
        int tabRunOverlay = getTabRunOverlay(tabPlacement);
        return (vertRunCount > 0? 
                vertRunCount * (maxTabWidth-tabRunOverlay) + tabRunOverlay + 
                tabAreaInsets.left + tabAreaInsets.right : 
                0);
    }

    protected Insets getTabInsets(int tabPlacement, int tabIndex) {
        return tabInsets;
    }

    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        rotateInsets(selectedTabPadInsets, currentPadInsets, tabPlacement);
        return currentPadInsets;
    }

    protected Insets getTabAreaInsets(int tabPlacement) {
        rotateInsets(tabAreaInsets, currentTabAreaInsets, tabPlacement);
        return currentTabAreaInsets;
    }

    protected Insets getContentBorderInsets(int tabPlacement) {
        return contentBorderInsets;
    }
    
    protected FontMetrics getFontMetrics() {
        Font font = tabPane.getFont();
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }


// Tab Navigation methods

    protected void navigateSelectedTab(int direction) {
        int tabPlacement = tabPane.getTabPlacement();
        int current = tabPane.getSelectedIndex();
        int tabCount = tabPane.getTabCount();
        int offset;
        switch(tabPlacement) {
          case LEFT:
          case RIGHT:
              switch(direction) {
                case NORTH:
                    selectPreviousTab(current);
                    break;
                case SOUTH:
                    selectNextTab(current);
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
                    selectNextTab(current);
                    break;
                case WEST:
                    selectPreviousTab(current);
                    break;
                default:
              }
        }
    }

    protected void selectNextTab(int current) {
        int tabIndex = getNextTabIndex(current);
        
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getNextTabIndex(tabIndex);
        }
        tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectPreviousTab(int current) {
        int tabIndex = getPreviousTabIndex(current);
        
        while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getPreviousTabIndex(tabIndex);
        }
        tabPane.setSelectedIndex(tabIndex);
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
              newIndex = tabForCoordinate(tabPane, r.x + r.width/2 + offset,
                                          r.y + r.height/2);
              break;
          case BOTTOM:  
          case TOP:
          default:
              newIndex = tabForCoordinate(tabPane, r.x + r.width/2, 
                                        r.y + r.height/2 + offset);
        }
        if (newIndex != -1) {
            while (!tabPane.isEnabledAt(newIndex) && newIndex != tabIndex) {
                newIndex = getNextTabIndex(newIndex);
            }        
            tabPane.setSelectedIndex(newIndex);
        }
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

    protected static void rotateInsets(Insets topInsets, Insets targetInsets, int targetPlacement) {
        
        switch(targetPlacement) {
          case LEFT:
              targetInsets.top = topInsets.right;
              targetInsets.left = topInsets.top;
              targetInsets.bottom = topInsets.left;
              targetInsets.right = topInsets.bottom;
              break;
          case BOTTOM:
              targetInsets.top = topInsets.bottom;
              targetInsets.left = topInsets.right;
              targetInsets.bottom = topInsets.top;
              targetInsets.right = topInsets.left;              
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
  
   // REMIND(aim,7/29/98): These actions should be broken
   // out into protected inner classes in the next release where
   // API changes are allowed

    private static class RightAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(EAST);
        }
    };
    
    private static class LeftAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(WEST);
        }
    };

    private static class UpAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(NORTH);
        }
    };

    private static class DownAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
            ui.navigateSelectedTab(SOUTH);
        }
    };

    private static class PageUpAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
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
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
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
	    BasicTabbedPaneUI ui = (BasicTabbedPaneUI)pane.getUI();
            ui.requestFocusForVisibleComponent();
        }
    };

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class TabbedPaneLayout implements LayoutManager {

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
            Insets borderInsets = getContentBorderInsets(tabPlacement);

            Dimension zeroSize = new Dimension(0,0);
            int height = borderInsets.top + borderInsets.bottom; 
            int width = borderInsets.left + borderInsets.right;
            int cWidth = 0;
            int cHeight = 0;

            for (int i = 0; i < tabPane.getTabCount(); i++) {
                Component component = tabPane.getComponentAt(i);
                Dimension size = zeroSize;
                size = minimum? component.getMinimumSize() : 
                                component.getPreferredSize();
                      
                if (size != null) {
                    cHeight = Math.max(size.height, cHeight);
                    cWidth = Math.max(size.width, cWidth);
                }
            }
            width += cWidth;
            height += cHeight;
            int tabExtent = 0;

            switch(tabPlacement) {
              case LEFT:
              case RIGHT:
                  tabExtent = preferredTabAreaWidth(tabPlacement, height);
                  width += tabExtent;
                  break;
              case TOP:
              case BOTTOM:
              default:
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
                Insets borderInsets = getContentBorderInsets(tabPlacement);

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
                    if (selectedComponent != visibleComponent) {
                        if (visibleComponent != null) {
                            if (SwingUtilities.findFocusOwner(visibleComponent) != null) {
                               shouldChangeFocus = true;
                            }
                        }                   
                        setVisibleComponent(selectedComponent);
                    } 
                }

                Rectangle bounds = tabPane.getBounds();
                int numChildren = tabPane.getComponentCount();

                if (numChildren > 0) {

                    switch(tabPlacement) {
                      case LEFT:
                        totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        cx = insets.left + totalTabWidth + borderInsets.left;
                        cy = insets.top + borderInsets.top;
                        break;
                      case RIGHT:
                        totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        cx = insets.left + borderInsets.left;
                        cy = insets.top + borderInsets.top;
                        break;
                      case BOTTOM:
                        totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        cx = insets.left + borderInsets.left;
                        cy = insets.top + borderInsets.top;
                        break;                   
                      case TOP:
                     default:
                       totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                       cx = insets.left + borderInsets.left;
                       cy = insets.top + totalTabHeight + borderInsets.top;
                    }
                
                    cw = bounds.width - totalTabWidth -
                                 insets.left - insets.right -
                                 borderInsets.left - borderInsets.right;
                    ch = bounds.height - totalTabHeight -
                                 insets.top - insets.bottom -
                                 borderInsets.top - borderInsets.bottom;

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
	    boolean leftToRight = BasicGraphicsUtils.isLeftToRight(tabPane);

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
        

// Controller: event listeners

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {

        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class TabSelectionHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JTabbedPane tabPane = (JTabbedPane)e.getSource();
            tabPane.revalidate();
            tabPane.repaint();
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            JTabbedPane tabPane = (JTabbedPane)e.getSource();
            if (!tabPane.isEnabled()) {
                return;
            }
            int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY());
            if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex == tabPane.getSelectedIndex()) {
                    tabPane.requestFocus();
                    tabPane.repaint(rects[tabIndex]);
                } else {
                    tabPane.setSelectedIndex(tabIndex);
                }
            }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class FocusHandler extends FocusAdapter {
        public void focusGained(FocusEvent e) { 
           JTabbedPane tabPane = (JTabbedPane)e.getSource();
           // To get around bug in JDK1.1.5 where FocusEvents are
           // not properly posted asynchronously to the queue
           int tabCount = tabPane.getTabCount();
           if (tabCount > 0 && tabCount == rects.length) {
               tabPane.repaint(rects[tabPane.getSelectedIndex()]);
           }
        }            
        public void focusLost(FocusEvent e) {
           JTabbedPane tabPane = (JTabbedPane)e.getSource();
           // To get around bug in JDK1.1.5 where FocusEvents are
           // not properly posted asynchronously to the queue
           int tabCount = tabPane.getTabCount();
           if (tabCount > 0 && tabCount == rects.length) {
               tabPane.repaint(rects[tabPane.getSelectedIndex()]);
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
	    int index = tp.indexOfComponent(child);
	    if (htmlViews != null && htmlViews.size()>=index) {
		htmlViews.removeElementAt(index);
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


