/*
 * @(#)JMenuBar.java	1.76 99/05/03
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.accessibility.*;

/**
 * An implementation of a MenuBar. You add JMenu objects to the
 * menu bar to construct a menu. When the user selects a JMenu
 * object, its associated JPopupMenu is displayed, allowing the
 * user to select one of the JMenuItems on it.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JMenuBar">JMenuBar</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.52 04/09/98
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 * @see JMenu
 * @see JPopupMenu
 * @see JMenuItem
 */
public class JMenuBar extends JComponent implements Accessible,MenuElement
{    
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "MenuBarUI";

    /*
     * Model for the selected subcontrol
     */
    private transient SingleSelectionModel selectionModel;

    private boolean paintBorder           = true;
    private Insets     margin             = null;

    /**
     * Creates a new menu bar.
     */
    public JMenuBar() {
        super();
        setSelectionModel(new DefaultSingleSelectionModel());
        updateUI();
    }

    /**
     * Returns the menubar's current UI.
     * @see #setUI
     */
    public MenuBarUI getUI() {
        return (MenuBarUI)ui;
    }
    
    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui the new MenuBarUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(MenuBarUI ui) {
        super.setUI(ui);
    }
    
    /**
     * Notification from the UIFactory that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * UIFactory.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((MenuBarUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "MenuBarUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the model object that handles single selections.
     *
     * @return the SingleSelectionModel in use
     * @see SingleSelectionModel
     */
    public SingleSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Set the model object to handle single selections.
     *
     * @param model the SingleSelectionModel to use
     * @see SingleSelectionModel
     * @beaninfo
     *       bound: true
     * description: The selection model, recording which child is selected.
     */
    public void setSelectionModel(SingleSelectionModel model) {
	SingleSelectionModel oldValue = selectionModel;
        this.selectionModel = model;
        firePropertyChange("selectionModel", oldValue, selectionModel);
    }


    /**
     * Appends the specified menu to the end of the menu bar.
     *
     * @param c the JMenu component to add
     */
    public JMenu add(JMenu c) {
        super.add(c);
        return c;
    }

    /**
     * Gets the menu at the specified position in the menu bar.
     *
     * @param index  an int giving the position in the menu bar, where
     *               0 is the first position
     * @return the JMenu at that position
     */
    public JMenu getMenu(int index) {
        Component c = getComponentAtIndex(index);
        if (c instanceof JMenu) 
            return (JMenu) c;
        return null;
    }

    /**
     * Returns the number of items in the menu bar.
     *
     * @return the number of items in the menu bar
     */
    public int getMenuCount() {
        return getComponentCount();
    }

    /**
     * Sets the help menu that appears when the user selects the
     * "help" option in the menu bar. This method is not yet implemented.
     *
     * @param menu the JMenu that delivers help to the user
     */
    public void setHelpMenu(JMenu menu) {
        throw new Error("setHelpMenu() not yet implemented.");
    }

    /**
     * Gets the help menu for the menu bar.
     *
     * @return the JMenu that delivers help to the user
     */
    public JMenu getHelpMenu() {
        throw new Error("getHelpMenu() not yet implemented.");
    }

    /**
     * Returns the component at the specified index.
     * This method is obsolete, please use <code>getComponent(int i)</code> instead.
     *
     * @param i an int specifying the position, where 0 = first
     * @return the Component at the position, or null for an
     *         invalid index
     */
    public Component getComponentAtIndex(int i) {	
	return getComponent(i);
    }

    /**
     * Returns the index of the specified component.
     *
     * @param c  the Component to find
     * @return an int giving the component's position, where 0 = first
     */
    public int getComponentIndex(Component c) {
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            if (comp == c) 
                return i;
        }
        return -1;
    }

    /**
     * Sets the currently selected component, producing a
     * a change to the selection model.
     *
     * @param sel the Component to select
     */
    public void setSelected(Component sel) {    
        SingleSelectionModel model = getSelectionModel();
        int index = getComponentIndex(sel);
        model.setSelectedIndex(index);
    }

    /**
     * Returns true if the MenuBar currently has a component selected
     *
     * @return true if a selection has been made, else false
     */
    public boolean isSelected() {       
        return selectionModel.isSelected();
    }

    /** 
     * Returns true if the Menubar's border should be painted.
     *
     * @return  true if the border should be painted, else false
     */
    public boolean isBorderPainted() {
        return paintBorder;
    }

    /**
     * Sets whether the border should be painted.
     * @param b if true and border property is not null, the border is painted.
     * @see #isBorderPainted
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether the border should be painted.
     */
    public void setBorderPainted(boolean b) {
        boolean oldValue = paintBorder;
        paintBorder = b;
        firePropertyChange("borderPainted", oldValue, paintBorder);
        if (b != oldValue) {
            revalidate();
            repaint();
        }
    }

    /**
     * Paint the menubar's border if BorderPainted property is true.
     * 
     * @param g the Graphics context to use for painting
     * @see JComponent#paint
     * @see JComponent#setBorder
     */
    protected void paintBorder(Graphics g) {    
        if (isBorderPainted()) {
            super.paintBorder(g);
        }
    }

    /**
     * Sets the margin between the menubar's border and
     * its menus. Setting to null will cause the menubar to
     * use the default margins.
     *
     * @param margin an Insets object containing the margin values
     * @see Insets
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The space between the menubar's border and its contents
     */
    public void setMargin(Insets m) {
        Insets old = margin;
        this.margin = m;
        firePropertyChange("margin", old, m);
        if (old == null || !m.equals(old)) {
            revalidate();
            repaint();
        }
    }

    /**
     * Returns the margin between the menubar's border and
     * its menus.
     * 
     * @return an Insets object containing the margin values
     * @see Insets
     */
    public Insets getMargin() {
        if(margin == null) {
            return new Insets(0,0,0,0);
        } else {
            return margin;
        }
    }


    /**
     * Implemented to be a MenuElement -- does nothing. 
     *
     * @see #getSubElements
     */
    public void processMouseEvent(MouseEvent event,MenuElement path[],MenuSelectionManager manager) {
    }

    /**
     * Implemented to be a MenuElement -- does nothing.
     *
     * @see #getSubElements
     */
    public void processKeyEvent(KeyEvent e,MenuElement path[],MenuSelectionManager manager) {
    }

    /** 
     * Implemented to be a MenuElement -- does nothing.
     *
     * @see #getSubElements
     */
    public void menuSelectionChanged(boolean isIncluded) {
    }
    
    /** 
     * Implemented to be a MenuElement -- returns the menus in this menu 
     * bar. This is the reason for implementing the MenuElement
     * interface -- so that the menu bar can be treated the same as
     * other menu elements.
     */
    public MenuElement[] getSubElements() {
        MenuElement result[];
        Vector tmp = new Vector();
        int c = getComponentCount();
        int i;
        Component m;

        for(i=0 ; i < c ; i++) {
            m = getComponent(i);
            if(m instanceof MenuElement)
                tmp.addElement(m);
        }

        result = new MenuElement[tmp.size()];
        for(i=0,c=tmp.size() ; i < c ; i++) 
            result[i] = (MenuElement) tmp.elementAt(i);
        return result;
    }
    
    /** 
     * Implemented to be a MenuElement. Returns this object. 
     *
     * @return the current Component (this)
     * @see #getSubElements
     */
    public Component getComponent() {
        return this;
    }


    /**
     * Returns a string representation of this JMenuBar. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JMenuBar.
     */
    protected String paramString() {
	String paintBorderString = (paintBorder ?
				    "true" : "false");
	String marginString = (margin != null ?
			       margin.toString() : "");

	return super.paramString() +
	",margin=" + marginString +
	",paintBorder=" + paintBorderString;
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
            accessibleContext = new AccessibleJMenuBar();
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
    protected class AccessibleJMenuBar extends AccessibleJComponent 
        implements AccessibleSelection {

        /**
         * Get the accessible state set of this object.
         *
         * @return an instance of AccessibleState containing the current state 
         *         of the object
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            return states;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_BAR;
        }

        /**
         * Get the AccessibleSelection associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }

        /**
         * Returns 1 if a menu is currently selected in this menu bar.
         *
         * @return 1 if a menu is currently selected, else 0
         */
         public int getAccessibleSelectionCount() {
            if (isSelected()) {
                return 1;
            } else {
                return 0;
            }
         }
    
        /**
         * Returns the currently selected menu if one is selected, 
         * otherwise null.
         */
         public Accessible getAccessibleSelection(int i) {
            if (isSelected()) {
		if (i != 0) {	// single selection model for JMenuBar
		    return null;
		}
                int j = getSelectionModel().getSelectedIndex();
                if (getComponentAtIndex(j) instanceof Accessible) {
                    return (Accessible) getComponentAtIndex(j);
                }           
            }
            return null;
         }

        /**
         * Returns true if the current child of this object is selected.
         *
         * @param i the zero-based index of the child in this Accessible 
         * object.
         * @see AccessibleContext#getAccessibleChild
         */
        public boolean isAccessibleChildSelected(int i) {
            return (i == getSelectionModel().getSelectedIndex());
        }

        /**
         * Selects the nth menu in the menu bar, forcing it to
	 * pop up.  If another menu is popped up, this will force
	 * it to close.  If the nth menu is already selected, this 
	 * method has no effect.
         *
         * @param i the zero-based index of selectable items
         * @see #getAccessibleStateSet
         */
        public void addAccessibleSelection(int i) {
	    // first close up any open menu
            int j = getSelectionModel().getSelectedIndex();
	    if (i == j) {
		return;
	    }
	    if (j >= 0 && j < getMenuCount()) {
                JMenu menu = getMenu(j);
                if (menu != null) {
		    MenuSelectionManager.defaultManager().setSelectedPath(null);
//		    menu.setPopupMenuVisible(false);
                }
	    }
	    // now popup the new menu
            getSelectionModel().setSelectedIndex(i);
	    JMenu menu = getMenu(i);
	    if (menu != null) {
		MenuElement me[] = new MenuElement[3];
		me[0] = JMenuBar.this;
		me[1] = menu;
		me[2] = menu.getPopupMenu();
		MenuSelectionManager.defaultManager().setSelectedPath(me);
//		menu.setPopupMenuVisible(true);
	    }
        }
    
        /**
         * Removes the nth selected item in the object from the object's
         * selection.  If the nth item isn't currently selected, this
         * method has no effect.  Otherwise, it closes the popup menu.
         *
         * @param i the zero-based index of selectable items
         */
        public void removeAccessibleSelection(int i) {
	    if (i >= 0 && i < getMenuCount()) {
		JMenu menu = getMenu(i);
		if (menu != null) {
		    MenuSelectionManager.defaultManager().setSelectedPath(null);
//		    menu.setPopupMenuVisible(false);
		}
		getSelectionModel().setSelectedIndex(-1);
	    }
        }
    
        /**
         * Clears the selection in the object, so that nothing in the
         * object is selected.  This will close any open menu.
         */
        public void clearAccessibleSelection() {
	    int i = getSelectionModel().getSelectedIndex();
	    if (i >= 0 && i < getMenuCount()) {
		JMenu menu = getMenu(i);
		if (menu != null) {
		    MenuSelectionManager.defaultManager().setSelectedPath(null);
//		    menu.setPopupMenuVisible(false);
		}
	    }
            getSelectionModel().setSelectedIndex(-1);
        }

        /**
         * Normally causes every selected item in the object to be selected
         * if the object supports multiple selections.  This method
	 * makes no sense in a menu bar, and so does nothing.
         */
        public void selectAllAccessibleSelection() {
        } 
    } // internal class AccessibleJMenuBar


    /**
     * Returns true to indicate that this component manages focus
     * events internally.
     *
     * @return true
     */
    public boolean isManagingFocus() {
        return true;
    }

    KeyboardBinding bindingForKeyStroke(KeyStroke ks,int condition) {
        // Does it exist for the MenuBar?
        KeyboardBinding kbb =  super.bindingForKeyStroke(ks, condition);
        if (kbb != null)
            return kbb;

        int i;
        Component subComponents[];

        subComponents = getComponents();
        for(i=0 ; i < subComponents.length ; i++) {
            // If 
            if(subComponents[i] instanceof JMenu) {
                kbb = bindingForKeyStrokeRecursive(subComponents[i], ks, condition);
            }
            if (kbb != null)
                return kbb;
        }       
        return null;
    }

    static KeyboardBinding bindingForKeyStrokeRecursive(Component c,
                                                        KeyStroke ks,int condition) {
        KeyboardBinding kbb = null;

        if (c==null)
            return null;

        if (c instanceof JComponent) {
            kbb = ((JComponent)c).bindingForKeyStroke(ks, condition);       
            if (kbb != null)
                return kbb;
        }

        if (c instanceof JMenu) {
            JMenu m = (JMenu)c;
            int i;
            Component subComponents[];
            
            subComponents = m.getMenuComponents();
	    if (subComponents != null) {
		for(i=0 ; i < subComponents.length ; i++) {
		    if(subComponents[i] instanceof JMenuItem) {
			kbb = bindingForKeyStrokeRecursive(subComponents[i], 
							   ks, condition);
		    }               
		    if (kbb != null)
			return kbb;
		}
	    }
        }
        return kbb;
    }

    /**
     * Overrides <code>JComponent.addNotify</code> to register this
     * menu bar with the current {@link KeyboardManager}.
     */
    public void addNotify() {
        super.addNotify();
	KeyboardManager.getCurrentManager().registerMenuBar(this);
    }

    /**
     * Overrides <code>JComponent.removeNotify</code> to unregister this
     * menu bar with the current {@link KeyboardManager}.
     */
    public void removeNotify() {
        super.removeNotify();
	KeyboardManager.getCurrentManager().unregisterMenuBar(this);
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        Object[] kvData = new Object[4];
        int n = 0;

        if (selectionModel instanceof Serializable) {
            kvData[n++] = "selectionModel";
            kvData[n++] = selectionModel;
        }

        s.writeObject(kvData);
    }


    /**
     * See JComponent.readObject() for information about serialization
     * in Swing.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException 
    {
        s.defaultReadObject();
        Object[] kvData = (Object[])(s.readObject());

        for(int i = 0; i < kvData.length; i += 2) {
            if (kvData[i] == null) {
                break;
            }
            else if (kvData[i].equals("selectionModel")) {
                selectionModel = (SingleSelectionModel)kvData[i + 1];
            }
        }

	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }
}

