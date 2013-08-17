/*
 * %W% %E%
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.Serializable; 
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A component which lets the user switch between a group of components by
 * clicking on a tab with a given title and/or icon.
 * <p>
 * Tabs/components are added to a TabbedPane object by using the addTab and
 * insertTab methods.  A tab is represented by an index corresponding
 * to the position it was added in, where the first tab has an index equal to 0
 * and the last tab has an index equal to the tab count minus 1.
 * <p>
 * The TabbedPane uses a SingleSelectionModel to represent the set
 * of tab indeces and the currently selected index.  If the tab count 
 * is greater than 0, then there will always be a selected index, which
 * by default will be initialized to the first tab.  If the tab count is
 * 0, then the selected index will be -1.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/tabbedpane.html">How to Use Tabbed Panes</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JTabbedPane">JTabbedPane</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *      attribute: isContainer true
 *    description: A component which provides a tab folder metaphor for 
 *                 displaying one component from a set of components.
 *
 * @version %I% %G%
 * @author Dave Moore
 * @author Philip Milne
 * @author Amy Fowler
 *
 * @see SingleSelectionModel
 */
public class JTabbedPane extends JComponent 
       implements Serializable, Accessible, SwingConstants {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TabbedPaneUI";

    /** 
     * Where the tabs are placed.
     * @see #setTabPlacement
     */
    protected int tabPlacement = TOP;
    /** The default selection model */
    protected SingleSelectionModel model;

    private boolean haveRegistered;

    /**
     * The changeListener is the listener we add to the
     * model.  
     */
    protected ChangeListener changeListener = null;

    Vector pages;

    /**
     * Only one ChangeEvent is needed per TabPane instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;

    /**
     * Creates an empty TabbedPane.
     * @see #addTab
     */
    public JTabbedPane() {
        this(TOP);
    }

    /**
     * Creates an empty TabbedPane with the specified tab placement
     * of either: TOP, BOTTOM, LEFT, or RIGHT.
     * @param tabPlacement the placement for the tabs relative to the content
     * @see #addTab
     */
    public JTabbedPane(int tabPlacement) {
        setTabPlacement(tabPlacement);
        pages = new Vector(1);
        setModel(new DefaultSingleSelectionModel());
        updateUI();
    }

    /**
     * Returns the UI object which implements the L&F for this component.
     * @see #setUI
     */
    public TabbedPaneUI getUI() {
        return (TabbedPaneUI)ui;
    }

    /**
     * Sets the UI object which implements the L&F for this component.
     * @param ui the new UI object
     * @see UIDefaults#getUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the tabbedpane's LookAndFeel
     */
    public void setUI(TabbedPaneUI ui) {
        super.setUI(ui);
    }

    /**
     * Notification from the UIManager that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * default UIFactory.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((TabbedPaneUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the UI class that implements the
     * L&F for this component.
     *
     * @return "TabbedPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * We pass ModelChanged events along to the listeners with
     * the tabbedpane (instead of the model itself) as the event source.
     */
    protected class ModelListener implements ChangeListener, Serializable {
        public void stateChanged(ChangeEvent e) {
            fireStateChanged();            
        }       
    }

    /**
     * Subclasses that want to handle ChangeEvents differently
     * can override this to return a subclass of ModelListener or
     * another ChangeListener implementation.
     * 
     * @see #fireStateChanged
     */
    protected ChangeListener createChangeListener() {
        return new ModelListener();
    }

    /**
     * Adds a ChangeListener to this tabbedpane.
     *
     * @param l the ChangeListener to add
     * @see #fireStateChanged
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from this tabbedpane.
     *
     * @param l the ChangeListener to remove
     * @see #fireStateChanged
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
        
    /**
     * Send a ChangeEvent, whose source is this tabbedpane, to
     * each listener.  This method method is called each time 
     * a ChangeEvent is received from the model.
     * 
     * @see #addChangeListener
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }   

    /**
     * Returns the model associated with this tabbedpane.
     *
     * @see #setModel
     */
    public SingleSelectionModel getModel() {
        return model;
    }

    /**
     * Sets the model to be used with this tabbedpane.
     * @param model the model to be used
     * 
     * @see #getModel
     * @beaninfo
     *       bound: true
     * description: The tabbedpane's SingleSelectionModel.
     */
    public void setModel(SingleSelectionModel model) {
        SingleSelectionModel oldModel = getModel();

        if (oldModel != null) {
            oldModel.removeChangeListener(changeListener);
            changeListener = null;
        }

        this.model = model;

        if (model != null) {
            changeListener = createChangeListener();
            model.addChangeListener(changeListener);
        }

        firePropertyChange("model", oldModel, model);
        repaint();
    }

    /**
     * Returns the placement of the tabs for this tabbedpane.
     * @see #setTabPlacement
     */
    public int getTabPlacement() {
        return tabPlacement;
    }

    /**
     * Sets the tab placement for this tabbedpane.
     * Possible values are:<ul>
     * <li>SwingConstants.TOP
     * <li>SwingConstants.BOTTOM
     * <li>SwingConstants.LEFT
     * <li>SwingConstants.RIGHT
     * </ul>
     * The default value, if not set, is TOP.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     *
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *         enum: TOP JTabbedPane.TOP 
     *               LEFT JTabbedPane.LEFT
     *               BOTTOM JTabbedPane.BOTTOM
     *               RIGHT JTabbedPane.RIGHT
     *  description: The tabbedpane's tab placement.
     *
     */
    public void setTabPlacement(int tabPlacement) {
        if (tabPlacement != TOP && tabPlacement != LEFT && 
            tabPlacement != BOTTOM && tabPlacement != RIGHT) {
            throw new IllegalArgumentException("illegal tab placement: must be TOP, BOTTOM, LEFT, or RIGHT");
        }
        if (this.tabPlacement != tabPlacement) {
            int oldValue = this.tabPlacement;
            this.tabPlacement = tabPlacement;
            firePropertyChange("tabPlacement", oldValue, tabPlacement);
            revalidate();
            repaint();
        }
    }

    /**
     * Returns the currently selected index for this tabbedpane.
     * Returns -1 if there is no currently selected tab.
     *
     * @return the index of the selected tab
     * @see #setSelectedIndex
     */    
    public int getSelectedIndex() {
        return model.getSelectedIndex();
    }

    /**
     * Sets the selected index for this tabbedpane.
     *
     * @see #getSelectedIndex
     * @see SingleSelectionModel#setSelectedIndex
     * @beaninfo
     *   preferred: true
     * description: The tabbedpane's selected tab index.
     */
    public void setSelectedIndex(int index) {
        int oldIndex = model.getSelectedIndex();

        model.setSelectedIndex(index);

        if ((oldIndex >= 0) && (oldIndex != index)) {
            Page oldPage = (Page) pages.elementAt(oldIndex);
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                        AccessibleState.SELECTED, null);
            }
        }
        if ((index >= 0) && (oldIndex != index)) {
            Page newPage = (Page) pages.elementAt(index);
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                        null, AccessibleState.SELECTED);
            }
        }
    }

    /**
     * Returns the currently selected component for this tabbedpane.
     * Returns null if there is no currently selected tab.
     *
     * @return the component corresponding to the selected tab
     * @see #setSelectedComponent
     */   
    public Component getSelectedComponent() { 
        int index = getSelectedIndex();
        if (index == -1) {
            return null;
        }
        return getComponentAt(index);
    }

    /**
     * Sets the selected component for this tabbedpane.  This
     * will automatically set the selectedIndex to the index
     * corresponding to the specified component.
     *
     * @see #getSelectedComponent
     * @beaninfo
     *   preferred: true
     * description: The tabbedpane's selected component.
     */
    public void setSelectedComponent(Component c) {
        int index = indexOfComponent(c);
        if (index != -1) {
            setSelectedIndex(index);
        } else {
            throw new IllegalArgumentException("component not found in tabbed pane");
        }
    }

    /**
     * Inserts a <i>component</i>, at <i>index</i>, represented by a
     * <i>title</i> and/or <i>icon</i>, either of which may be null. 
     * Uses java.util.Vector internally, see insertElementAt() 
     * for details of insertion conventions. 
     * @param title the title to be displayed in this tab
     * @param icon the icon to be displayed in this tab
     * @param component The component to be displayed when this tab is clicked. 
     * @param tip the tooltip to be displayed for this tab
     * @param index the position to insert this new tab 
     *
     * @see #addTab
     * @see #removeTabAt  
     */
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        Icon disabledIcon = null;
        if (icon != null && icon instanceof ImageIcon) {
            disabledIcon = new ImageIcon(
                    GrayFilter.createDisabledImage(
                        ((ImageIcon)icon).getImage()));
        }

        // If component already exists, remove corresponding
        // tab so that new tab gets added correctly
        // Note: we are allowing component=null because of compatibility,
        // but we really should throw an exception because much of the
        // rest of the JTabbedPane implementation isn't designed to deal
        // with null components for tabs.
        int i;
        if (component != null && (i = indexOfComponent(component)) != -1) {
            removeTabAt(i);
        }

        pages.insertElementAt(new Page(this, title != null? title : "", icon, disabledIcon,
                                       component, tip), index);
        if (component != null) {
            component.setVisible(false);
            addImpl(component, null, -1);
        }

        if (pages.size() == 1) {
            setSelectedIndex(0);
        }
        if (!haveRegistered && tip != null) {
            ToolTipManager.sharedInstance().registerComponent(this);
            haveRegistered = true;
        }

        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                    null, component);
        }
        revalidate();
        repaint();
    }

    /**
     * Adds a <i>component</i> and <i>tip</i> represented by a <i>title</i> 
     * and/or <i>icon</i>, either of which can be null. 
     * Cover method for insertTab().
     * @param title the title to be displayed in this tab
     * @param icon the icon to be displayed in this tab
     * @param component The component to be displayed when this tab is clicked. 
     * @param tip the tooltip to be displayed for this tab
     * 
     * @see #insertTab
     * @see #removeTabAt  
     */
    public void addTab(String title, Icon icon, Component component, String tip) {
        insertTab(title, icon, component, tip, pages.size()); 
    }

    /**
     * Adds a <i>component</i> represented by a <i>title</i> and/or <i>icon</i>, 
     * either of which can be null. 
     * Cover method for insertTab(). 
     * @param title the title to be displayed in this tab
     * @param icon the icon to be displayed in this tab
     * @param component The component to be displayed when this tab is clicked. 
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
    public void addTab(String title, Icon icon, Component component) {
        insertTab(title, icon, component, null, pages.size()); 
    }

    /**
     * Adds a <i>component</i> represented by a <i>title</i> and no icon. 
     * Cover method for insertTab().
     * @param title the title to be displayed in this tab
     * @param component The component to be displayed when this tab is clicked. 
     * 
     * @see #insertTab
     * @see #removeTabAt  
     */
    public void addTab(String title, Component component) {
        insertTab(title, null, component, null, pages.size()); 
    }

    /**
     * Adds a <i>component</i> with a tab title defaulting to
     * the name of the component.
     * Cover method for insertTab().
     * @param component The component to be displayed when this tab is clicked. 
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
    public Component add(Component component) {
        addTab(component.getName(), component);
        return component;
    }

    /**
     * Adds a <i>component</i> with the specified tab title.
     * Cover method for insertTab().
     * @param title the title to be displayed in this tab
     * @param component The component to be displayed when this tab is clicked.
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
    public Component add(String title, Component component) {
        addTab(title, component);
        return component;
    }

    /**
     * Adds a <i>component</i> at the specified tab index with a tab
     * title defaulting to the name of the component.
     * Cover method for insertTab().
     * @param component The component to be displayed when this tab is clicked.
     * @param index the position to insert this new tab 
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
    public Component add(Component component, int index) {
        insertTab(component.getName(), null, component, null, index);
        return component;
    }

    /**
     * Adds a <i>component</i> to the tabbed pane.  If constraints
     * is a String or an Icon, it will be used for the tab title,
     * otherwise the component's name will be used as the tab title. 
     * Cover method for insertTab().
     * @param component The component to be displayed when this tab is clicked. 
     * @constraints the object to be displayed in the tab
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
    public void add(Component component, Object constraints) {
        if (constraints instanceof String) {
            addTab((String)constraints, component);
        } else if (constraints instanceof Icon) {
            addTab(null, (Icon)constraints, component);
        } else {
            add(component);
        }
    }

    /**
     * Adds a <i>component</i> at the specified tab index.  If constraints
     * is a String or an Icon, it will be used for the tab title,
     * otherwise the component's name will be used as the tab title. 
     * Cover method for insertTab().
     * @param component The component to be displayed when this tab is clicked. 
     * @constraints the object to be displayed in the tab
     * @param index the position to insert this new tab 
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
    public void add(Component component, Object constraints, int index) {
        Icon icon = constraints instanceof Icon? (Icon)constraints : null;
        String title = constraints instanceof String? (String)constraints : null;
        insertTab(title, icon, component, null, index);
    }

    /**
     * Removes the tab at <i>index</i>.
     * After the component associated with <i>index</i> is removed,
     * its visibility is reset to true to ensure it will be visible
     * if added to other containers.
     * @param index the index of the tab to be removed
     *
     * @see #addTab
     * @see #insertTab  
     */
    public void removeTabAt(int index) {            
        // If we are removing the currently selected tab AND
        // it happens to be the last tab in the bunch, then
        // select the previous tab
        int tabCount = getTabCount();
        int selected = getSelectedIndex();
        if (selected >= (tabCount - 1)) {
            setSelectedIndex(selected - 1);
        }

        Component component = getComponentAt(index);

        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                    component, null);
        }
        if (component != null) {
            super.remove(component);
            component.setVisible(true);
        }

        pages.removeElementAt(index);
        revalidate();
        repaint();
    }

    /**
     * Removes the tab which corresponds to the specified component.
     *
     * @param component the component to remove from the tabbedpane
     * @see #addTab
     * @see #removeTabAt  
     */
    public void remove(Component component) {
        int index = indexOfComponent(component);
        if (index != -1) {
            removeTabAt(index);
        }
    }

    /**
     * Removes all the tabs from the tabbedpane.
     *
     * @see #addTab
     * @see #removeTabAt  
     */
    public void removeAll() {
        setSelectedIndex(-1);

        int tabCount = getTabCount();
        for (int i = 0; i < tabCount; i++) {
            Component component = getComponentAt(i);
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                    component, null);
            }
        }
        super.removeAll();
        pages.removeAllElements();
        revalidate();
        repaint();
    }

    /**
     * Returns the number of tabs in this tabbedpane.
     *
     * @return an int specifying the number of tabbed pages
     */
    public int getTabCount() {
        return pages.size();
    }

    /**
     * Returns the number of tab runs currently used to display
     * the tabs. 
     * @return an int giving the number of rows if the tabPlacement
     *         is TOP or BOTTOM and the number of columns if tabPlacement
     *         is LEFT or RIGHT, or 0 if there is no UI set on this
     *         tabbedpane
     */
    public int getTabRunCount() {
        if (ui != null) {
            return ((TabbedPaneUI)ui).getTabRunCount(this);
        }
        return 0;
    }


// Getters for the Pages

    /**
     * Returns the tab title at <i>index</i>.
     *
     * @see #setTitleAt
     */
    public String getTitleAt(int index) {
        return ((Page)pages.elementAt(index)).title;
    }

    /**
     * Returns the tab icon at <i>index</i>.
     *
     * @see #setIconAt
     */
    public Icon getIconAt(int index) {
        return ((Page)pages.elementAt(index)).icon;
    }

    /**
     * Returns the tab disabled icon at <i>index</i>.
     *
     * @see #setDisabledIconAt
     */
    public Icon getDisabledIconAt(int index) {
        return ((Page)pages.elementAt(index)).disabledIcon;
    }

    /**
     * Returns the tab background color at <i>index</i>.
     *
     * @see #setBackgroundAt
     */
    public Color getBackgroundAt(int index) {
        return ((Page)pages.elementAt(index)).getBackground();
    }

    /**
     * Returns the tab foreground color at <i>index</i>.
     *
     * @see #setForegroundAt
     */
    public Color getForegroundAt(int index) {
        return ((Page)pages.elementAt(index)).getForeground();
    }

    /**
     * Returns whether or not the tab at <i>index</i> is
     * currently enabled.
     *
     * @see #setEnabledAt
     */
    public boolean isEnabledAt(int index) {
        return ((Page)pages.elementAt(index)).isEnabled();
    }

    /**
     * Returns the component at <i>index</i>.
     *
     * @see #setComponentAt
     */
    public Component getComponentAt(int index) {
        return ((Page)pages.elementAt(index)).component;
    }

    /**
     * Returns the tab bounds at <i>index</i>.  If the tab at
     * this index is not currently visible in the UI, then returns null.
     * If there is no UI set on this tabbedpane, then returns null.
     */
    public Rectangle getBoundsAt(int index) {
        if (ui != null) {
            return ((TabbedPaneUI)ui).getTabBounds(this, index);
        }
        return null;
    }  

// Setters for the Pages

    /**
     * Sets the title at <i>index</i> to <i>title</i> which can be null. 
     * An internal exception is raised if there is no tab at that index.
     * @param index the tab index where the title should be set 
     * @param title the title to be displayed in the tab
     *
     * @see #getTitleAt
     * @beaninfo
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The title at the specified tab index.
     */
    public void setTitleAt(int index, String title) {
        String oldTitle =((Page)pages.elementAt(index)).title;
        ((Page)pages.elementAt(index)).title = title;

        if ((oldTitle != title) && (accessibleContext != null)) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                    oldTitle, title);
        }
        if (title == null || oldTitle == null ||
            !title.equals(oldTitle)) {
            revalidate();
            repaint();
        }
    }

    /**
     * Sets the icon at <i>index</i> to <i>icon</i> which can be null.
     * An internal exception is raised if there is no tab at that index. 
     * @param index the tab index where the icon should be set 
     * @param icon the icon to be displayed in the tab
     *
     * @see #getIconAt
     * @beaninfo
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The icon at the specified tab index.
     */
    public void setIconAt(int index, Icon icon) {
        Icon oldIcon = ((Page)pages.elementAt(index)).icon;
        ((Page)pages.elementAt(index)).icon = icon;

        AccessibleContext ac = getAccessibleContext();
        // Fire the accessibility Visible data change
        if ((oldIcon != icon) && (accessibleContext != null)) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                    oldIcon, icon);
        }
        if (icon != oldIcon) {
            revalidate();
            repaint();
        }
    }

    /**
     * Sets the disabled icon at <i>index</i> to <i>icon</i> which can be null.
     * An internal exception is raised if there is no tab at that index. 
     * @param index the tab index where the disabled icon should be set 
     * @param icon the icon to be displayed in the tab when disabled
     *
     * @see #getDisabledIconAt
     * @beaninfo
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The disabled icon at the specified tab index.
     */
    public void setDisabledIconAt(int index, Icon disabledIcon) {
        Icon oldIcon = ((Page)pages.elementAt(index)).disabledIcon;
        ((Page)pages.elementAt(index)).disabledIcon = disabledIcon;
        if (disabledIcon != oldIcon && !isEnabledAt(index)) {
            revalidate();
            repaint();
        }
    }

    /**
     * Sets the background color at <i>index</i> to <i>background</i> 
     * which can be null, in which case the tab's background color
     * will default to the background color of the tabbedpane.
     * An internal exception is raised if there is no tab at that index.
     * @param index the tab index where the background should be set 
     * @param background the color to be displayed in the tab's background
     *
     * @see #getBackgroundAt
     * @beaninfo
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The background color at the specified tab index.
     */
    public void setBackgroundAt(int index, Color background) {
        Color oldBg = ((Page)pages.elementAt(index)).background;
        ((Page)pages.elementAt(index)).setBackground(background);
        if (background == null || oldBg == null ||
            !background.equals(oldBg)) {
            Rectangle tabBounds = getBoundsAt(index);
            if (tabBounds != null) {
                repaint(tabBounds);
            }
        }
    }

    /**
     * Sets the foreground color at <i>index</i> to <i>foreground</i> 
     * which can be null, in which case the tab's foreground color
     * will default to the foreground color of this tabbedpane.
     * An internal exception is raised if there is no tab at that index. 
     * @param index the tab index where the foreground should be set 
     * @param foreground the color to be displayed as the tab's foreground
     *
     * @see #getForegroundAt
     * @beaninfo
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The foreground color at the specified tab index.
     */
    public void setForegroundAt(int index, Color foreground) {
        Color oldFg = ((Page)pages.elementAt(index)).foreground;
        ((Page)pages.elementAt(index)).setForeground(foreground);
        if (foreground == null || oldFg == null ||
            !foreground.equals(oldFg)) {
            Rectangle tabBounds = getBoundsAt(index);
            if (tabBounds != null) {
                repaint(tabBounds);
            }
        }
    }

    /**
     * Sets whether or not the tab at <i>index</i> is enabled.
     * An internal exception is raised if there is no tab at that index.
     * @param index the tab index which should be enabled/disabled
     * @param enabled whether or not the tab should be enabled
     *
     * @see #isEnabledAt 
     */
    public void setEnabledAt(int index, boolean enabled) {
        boolean oldEnabled = ((Page)pages.elementAt(index)).isEnabled();
        ((Page)pages.elementAt(index)).setEnabled(enabled);
        if (enabled != oldEnabled) {
            repaint(getBoundsAt(index));
        }
    }

    /**
     * Sets the component at <i>index</i> to <i>component</i>.  
     * An internal exception is raised if there is no tab at that index.
     * @param index the tab index where this component is being placed
     * @param component the component for the tab
     *
     * @see #getComponentAt  
     * @beaninfo
     *    attribute: visualUpdate true
     *  description: The component at the specified tab index.
     */
    public void setComponentAt(int index, Component component) {
        Page page = (Page)pages.elementAt(index);
        if (component != page.component) {
            if (page.component != null) {
                // REMIND(aim): this is really silly;
                // why not if (page.component.getParent() == this) remove(component)
                synchronized(getTreeLock()) {
                    int count = getComponentCount();
                    Component children[] = getComponents();
                    for (int i = 0; i < count; i++) {
                        if (children[i] == page.component) {
                            remove(i);
                        }
                    }
                }
            }
            page.component = component;
            component.setVisible(getSelectedIndex() == index);
            addImpl(component, null, -1);
            
            revalidate();
        }
    }

    /**
     * Returns the first tab index with a given <i>title</i>, 
     * Returns -1 if no tab has this title. 
     * @param title the title for the tab
     */
    public int indexOfTab(String title) {
        for(int i = 0; i < getTabCount(); i++) { 
            if (getTitleAt(i).equals(title == null? "" : title)) { 
                return i;
            }
        }
        return -1; 
    }

    /**
     * Returns the first tab index with a given <i>icon</i>.
     * Returns -1 if no tab has this icon.
     * @param icon the icon for the tab
     */
    public int indexOfTab(Icon icon) {
        for(int i = 0; i < getTabCount(); i++) { 
            if (getIconAt(i).equals(icon)) { 
                return i;
            }
        }
        return -1; 
    }

    /**
     * Returns the index of the tab for the specified component.
     * Returns -1 if there is no tab for this component.
     * @param component the component for the tab
     */
    public int indexOfComponent(Component component) {
        for(int i = 0; i < getTabCount(); i++) {
            Component c = getComponentAt(i);
            if (c != null) {
                if (c.equals(component)) { 
                    return i;
                }
            } else if (c == component) {
                return i;
            }
        }
        return -1; 
    }

    /**
     * Returns the tooltip text for the component determined by the
     * mouse event location.
     *
     * @param event  the MouseEvent that tells where the cursor is lingering
     * @return the String with the tooltip text
     */
    public String getToolTipText(MouseEvent event) {
        if (ui != null) {
            int index = ((TabbedPaneUI)ui).tabForCoordinate(this, event.getX(), event.getY());

            if (index != -1) {
                return ((Page)pages.elementAt(index)).tip;
            }
        }
        return super.getToolTipText(event);
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
     * Returns a string representation of this JTabbedPane. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JTabbedPane.
     */
    protected String paramString() {
        String tabPlacementString;
        if (tabPlacement == TOP) {
            tabPlacementString = "TOP";
        } else if (tabPlacement == BOTTOM) {
            tabPlacementString = "BOTTOM";
        } else if (tabPlacement == LEFT) {
            tabPlacementString = "LEFT";
        } else if (tabPlacement == RIGHT) {
            tabPlacementString = "RIGHT";
        } else tabPlacementString = "";
        String haveRegisteredString = (haveRegistered ?
				       "true" : "false");

	return super.paramString() +
        ",haveRegistered=" + haveRegisteredString +
        ",tabPlacement=" + tabPlacementString;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJTabbedPane();
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
    protected class AccessibleJTabbedPane extends AccessibleJComponent 
        implements AccessibleSelection, ChangeListener {

        /**
         *  Constructs an AccessibleJTabbedPane
         */
        public AccessibleJTabbedPane() {
            super();
            JTabbedPane.this.model.addChangeListener(this);
        }

        public void stateChanged(ChangeEvent e) {
            Object o = e.getSource();
            firePropertyChange(AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
                               null, o);
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of 
         *          the object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PAGE_TAB_LIST;
        }

        /**
         * Returns the number of accessible children in the object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return getTabCount();
        }

        /**
         * Return the specified Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            if (i < 0 || i >= getTabCount()) {
                return null;
            }
            return (Accessible) pages.elementAt(i);
        }

        /**
         * Get the AccessibleSelection associated with this object if one
         * exists.  Otherwise return null.
         *
         * @return the AccessibleSelection, or null
         */
        public AccessibleSelection getAccessibleSelection() {
           return this;
        }

        public Accessible getAccessibleAt(Point p) {
            int tab = ((TabbedPaneUI) ui).tabForCoordinate(JTabbedPane.this, 
                                                           p.x, p.y);
            if (tab == -1) {
                tab = getSelectedIndex();
            }
            return getAccessibleChild(tab);
        }

        public int getAccessibleSelectionCount() {
            return 1;
        }

        public Accessible getAccessibleSelection(int i) {
            int index = getSelectedIndex();
            if (index == -1) {
                return null;
            }
            return (Accessible) pages.elementAt(index);
        }

        public boolean isAccessibleChildSelected(int i) {
            return (i == getSelectedIndex());
        }

        public void addAccessibleSelection(int i) {
           setSelectedIndex(i);
        }

        public void removeAccessibleSelection(int i) {
           // can't do
        }

        public void clearAccessibleSelection() {
           // can't do
        }

        public void selectAllAccessibleSelection() {
           // can't do
        }
    }

    private class Page extends AccessibleContext 
        implements Serializable, Accessible, AccessibleComponent {
        String title;
        Color background;
        Color foreground;
        Icon icon;
        Icon disabledIcon;
        JTabbedPane parent;
        Component component;
        String tip;
        boolean enabled = true;
        boolean needsUIUpdate;

        Page(JTabbedPane parent, 
             String title, Icon icon, Icon disabledIcon, Component component, String tip) {
            this.title = title;
            this.icon = icon;
            this.disabledIcon = disabledIcon;
            this.parent = parent;
            this.setAccessibleParent(parent);
            this.component = component;
            this.tip = tip;
            if (component instanceof Accessible) {
                AccessibleContext ac;
                ac = ((Accessible) component).getAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleParent(this);
                }
            }
        }
 
        /////////////////
        // Accessibility support
        ////////////////

        public AccessibleContext getAccessibleContext() {
            return this;
        }


        // AccessibleContext methods

        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else if (title != null) {
                return title;
            }
            return null;
        }

        public String getAccessibleDescription() {
            if (accessibleDescription != null) {
                return accessibleDescription;
            } else if (tip != null) {
                return tip;
            }
            return null;
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PAGE_TAB;
        }

        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states;
            states = parent.getAccessibleContext().getAccessibleStateSet();
            states.add(AccessibleState.SELECTABLE);
            int i = parent.indexOfTab(title);
            if (i == parent.getSelectedIndex()) {
                states.add(AccessibleState.SELECTED);
            }       
            return states;
        }

        public int getAccessibleIndexInParent() {
            return parent.indexOfTab(title);
        }

        public int getAccessibleChildrenCount() {
            if (component instanceof Accessible) {
                return 1;
            } else {
                return 0;
            }
        }       

        public Accessible getAccessibleChild(int i) {
            if (component instanceof Accessible) {
                return (Accessible) component;
            } else {
                return null;
            }
        }

        public Locale getLocale() {
            return parent.getLocale();
        }

        public AccessibleComponent getAccessibleComponent() {
            return this;
        }


        // AccessibleComponent methods

        public Color getBackground() {
            return background != null? background : parent.getBackground();
        }

        public void setBackground(Color c) {
            background = c;
        }

        public Color getForeground() {
            return foreground != null? foreground : parent.getForeground();
        }

        public void setForeground(Color c) {
            foreground = c;
        }

        public Cursor getCursor() {
            return parent.getCursor();
        }

        public void setCursor(Cursor c) {
            parent.setCursor(c);
        }

        public Font getFont() {
            return parent.getFont();
        }

        public void setFont(Font f) {
            parent.setFont(f);
        }

        public FontMetrics getFontMetrics(Font f) {
            return parent.getFontMetrics(f);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean b) {
            enabled = b;
        }

        public boolean isVisible() {
            return parent.isVisible();
        }

        public void setVisible(boolean b) {
            parent.setVisible(b);
        }

        public boolean isShowing() {
            return parent.isShowing();
        }

        public boolean contains(Point p) {
            Rectangle r = getBounds();
            return r.contains(p);
        }

        public Point getLocationOnScreen() {
             Point parentLocation = parent.getLocationOnScreen();
             Point componentLocation = getLocation();
             componentLocation.translate(parentLocation.x, parentLocation.y);
             return componentLocation;
        }
    
        public Point getLocation() {
             Rectangle r = getBounds();
             return new Point(r.x, r.y);
        }
    
        public void setLocation(Point p) {
            // do nothing
        }

        public Rectangle getBounds() {
            return parent.getUI().getTabBounds(parent, 
                                               parent.indexOfTab(title));
        }

        public void setBounds(Rectangle r) {
            // do nothing
        }

        public Dimension getSize() {
            Rectangle r = getBounds();
            return new Dimension(r.width, r.height);
        }

        public void setSize(Dimension d) {
            // do nothing
        }

        public Accessible getAccessibleAt(Point p) {
            if (component instanceof Accessible) {
                return (Accessible) component;
            } else {
                return null;
            }
        }

        public boolean isFocusTraversable() {
            return false;
        }

        public void requestFocus() {
            // do nothing
        }

        public void addFocusListener(FocusListener l) {
            // do nothing
        }

        public void removeFocusListener(FocusListener l) {
            // do nothing
        }
    }
}
