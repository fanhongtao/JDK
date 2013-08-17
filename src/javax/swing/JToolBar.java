/*
 * @(#)JToolBar.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.beans.*;

import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Hashtable;


/**
 * JToolBar provides a component which is useful for displaying commonly
 * used Actions or controls.  It can be dragged out into a separate window
 * by the user (unless the floatable property is set to false).  In order
 * for drag-out to work correctly, it is recommended that you add JToolBar
 * instances to one of the four 'sides' of a container whose layout manager 
 * is a BorderLayout, and do not add children to any of the other four 'sides'.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JToolBar">JToolBar</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.70 04/22/99
 * @author Georges Saab
 * @author Jeff Shapiro
 * @see Action
 */
public class JToolBar extends JComponent implements SwingConstants, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolBarUI";

    private    boolean   paintBorder              = true;  
    private    Insets    margin                   = null;
    private    boolean   floatable                = true;
    private    int       orientation              = HORIZONTAL;

    /* registry of listeners created for Action-JButton
     * linkage.  This is needed sot that references can
     * be cleaned up at remove time to allow GC.
     */
    private static Hashtable listenerRegistry = null;


    /**
     * Create a new toolbar.  Orientration defaults to horizontal.
     */
    public JToolBar()
    {
        this( HORIZONTAL );
    }

    /**
     * Create a new toolbar.
     * 
     * @param orientation  The initial orientation (HORIZONTAL/VERTICAL)
     */
    public JToolBar( int orientation )
    {
        checkOrientation( orientation );

	this.orientation = orientation;

	if ( orientation == VERTICAL )
	{
	    this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
	}
	else
	{
            if( SwingUtilities.isLeftToRight(this) ) {
                this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
            } else {
                this.setLayout( new RightToLeftToolBarLayout() );
            }
	}

        addPropertyChangeListener( new PropertyChangeHandler() );

        updateUI();
    }

    /**
     * Returns the toolbar's current UI.
     * @see #setUI
     */
    public ToolBarUI getUI() {
        return (ToolBarUI)ui;
    }
    
    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the ToolBarUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     * description: The menu item's UI delegate
     *       bound: true
     *      expert: true
     *      hidden: true
     */
    public void setUI(ToolBarUI ui) {
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
        setUI((ToolBarUI)UIManager.getUI(this));
        invalidate();
    }



    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "ToolBarUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the index of the specified component.
     * (Note: Separators occupy index positions.)
     *
     * @param c  the Component to find
     * @return an int indicating the component's position, where 0=first
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
     * Returns the component at the specified index.
     *
     * @param i  the component's position, where 0=first
     * @return   the Component at that position, or null for an
     *           invalid index
     *
     */
    public Component getComponentAtIndex(int i) {
        int ncomponents = this.getComponentCount();
        if ( i >= 0 && i < ncomponents) {
            Component[] component = this.getComponents();
            return component[i];
        }
        return null;
    }

     /**
      * Sets the margin between the toolbar's border and
      * its buttons. Setting to null causes the toolbar to
      * use the default margins. The toolbar's default Border
      * object uses this value to create the proper margin.
      * However, if a non-default border is set on the toolbar, 
      * it is that Border object's responsibility to create the
      * appropriate margin space (otherwise this property will 
      * effectively be ignored).
      *
      * @param m an Insets object that defines the space between the 
      *          border and the buttons
      * @see Insets
      * @beaninfo
      * description: The margin between the toolbar's border and contents
      *       bound: true
      *      expert: true
      */
     public void setMargin(Insets m)
     {
         Insets old = margin;
         margin = m;
         firePropertyChange("margin", old, m);
	 revalidate();
	 repaint();
     }
 
     /**
      * Returns the margin between the toolbar's border and
      * its buttons.
      *
      * @return an Insets object containing the margin values
      * @see Insets
      */
     public Insets getMargin()
     {
         if(margin == null) {
             return new Insets(0,0,0,0);
         } else {
             return margin;
         }
     }

     /**
      * Checks whether the border should be painted.
      *
      * @return true if the border should be painted, else false
      * @see #setBorderPainted
      */
     public boolean isBorderPainted()
     {
         return paintBorder;
     }
     
 
     /**
      * Sets whether the border should be painted.
      *
      * @param b if true, the border is painted.
      * @see #isBorderPainted
      * @beaninfo
      * description: Does the toolbar paint its borders?
      *       bound: true
      *      expert: true
      */
     public void setBorderPainted(boolean b)
     {
         if ( paintBorder != b )
         {
	     boolean old = paintBorder;
	     paintBorder = b;
	     firePropertyChange("borderPainted", old, b);
	     revalidate();
	     repaint();
	 }
     }
 
     /**
      * Paint the toolbar's border if BorderPainted property is true.
      * 
      * @param g  the Graphics context in which the painting is done
      * @see JComponent#paint
      * @see JComponent#setBorder
      */
     protected void paintBorder(Graphics g)
     {    
         if (isBorderPainted())
	 {
             super.paintBorder(g);
         }
     }

    /** 
     * Return true if the Toolbar can be dragged out by the user.
     *
     * @return true if the Toolbar can be dragged out by the user
     */
    public boolean isFloatable()
    {
        return floatable;
    }

     /**
      * Sets whether the toolbar can be made to float
      *
      * @param b if true, the toolbar can be dragged out
      * @see #isFloatable
      * @beaninfo
      * description: Can the toolbar be made to float by the user?
      *       bound: true
      *   preferred: true
      */
    public void setFloatable( boolean b )
    {
        if ( floatable != b )
	{
            boolean old = floatable;
	    floatable = b;

	    firePropertyChange("floatable", old, b);
	    revalidate();
	    repaint();
        }
    }

    /**
     * Returns the current orientation of the toolbar
     *
     * @return an int representing the current orientation (HORIZONTAL/VERTICAL)
     * @see #setOrientation
     */
    public int getOrientation()
    {
        return this.orientation;
    }

    /**
     * Set the orientation of the toolbar
     *
     * @param o  The new orientation (HORIZONTAL/VERTICAL)
     * @see #getOrientation
     * @beaninfo
     * description: The current orientation of the toolbar
     *       bound: true
     *   preferred: true
     */
    public void setOrientation( int o )
    {
        checkOrientation( o );

	if ( orientation != o )
	{
	    int old = orientation;
	    orientation = o;

	    if ( o == VERTICAL )
	        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
	    else {
                if( SwingUtilities.isLeftToRight(this) ) {
                    setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
                } else {
                    setLayout( new RightToLeftToolBarLayout() );
                }
            }
	    firePropertyChange("orientation", old, o);
	    revalidate();
	    repaint();
	}
    }


    private void checkOrientation( int orientation )
    {
        switch ( orientation )
	{
            case VERTICAL:
            case HORIZONTAL:
                break;
            default:
                throw new IllegalArgumentException( "orientation must be one of: VERTICAL, HORIZONTAL" );
        }
    }

    /**
     * Appends a toolbar separator of default size to the end of the toolbar.
     */
    public void addSeparator()
    {
        JToolBar.Separator s = new JToolBar.Separator();
        add(s);
    }

    /**
     * Appends a toolbar separator to the end of the toolbar.
     *
     * @param size the size of the separator
     */
    public void addSeparator( Dimension size )
    {
        JToolBar.Separator s = new JToolBar.Separator( size );
        add(s);
    }

    /**
     * Add a new JButton which dispatches the action.
     *
     * @param a the Action object to add as a new menu item
     */
    public JButton add(Action a) {
        JButton b = new JButton((String)a.getValue(Action.NAME),
                                (Icon)a.getValue(Action.SMALL_ICON));
        b.setHorizontalTextPosition(JButton.CENTER);
        b.setVerticalTextPosition(JButton.BOTTOM);
        b.setEnabled(a.isEnabled());
        b.addActionListener(a);
        add(b);
	registerButtonForAction(b, a);
        return b;
    }

    /**
     * Remove the Component from the tool bar.
     * @param comp the component to be removed.
     */
    public void remove(Component comp) {
	super.remove(comp);
	if (comp instanceof JButton) {
	    JButton item = (JButton)comp;
	    unregisterButtonForAction(item);		    
	}
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        if (comp instanceof JButton) {
            ((JButton)comp).setDefaultCapable(false);
        }
    }

    private void registerButtonForAction(JButton b, Action a) {
        PropertyChangeListener actionPropertyChangeListener = 
            createActionChangeListener(b);
	if (listenerRegistry == null) {
	    listenerRegistry = new Hashtable();
	}
	listenerRegistry.put(b, actionPropertyChangeListener);
	listenerRegistry.put(actionPropertyChangeListener, a);
        a.addPropertyChangeListener(actionPropertyChangeListener);
    }

    private void unregisterButtonForAction(JButton item) {
	if (listenerRegistry != null) { 
	    ActionChangedListener p = (ActionChangedListener)listenerRegistry.remove(item);
	    if (p!=null) {
		Action a = (Action)listenerRegistry.remove(p);
		if (a!=null) {
		    item.removeActionListener(a);		
		    a.removePropertyChangeListener(p);
		}
		p.setTarget(null);
	    }
	}
    }

    protected PropertyChangeListener createActionChangeListener(JButton b) {
        return new ActionChangedListener(b);
    }

    private class ActionChangedListener implements PropertyChangeListener {
        JButton button;
        
        ActionChangedListener(JButton b) {
            super();
            setTarget(b);
        }
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (e.getPropertyName().equals(Action.NAME)) {
                String text = (String) e.getNewValue();
                button.setText(text);
                button.repaint();
            } else if (propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean) e.getNewValue();
                button.setEnabled(enabledState.booleanValue());
                button.repaint();
            } else if (e.getPropertyName().equals(Action.SMALL_ICON)) {
                Icon icon = (Icon) e.getNewValue();
                button.setIcon(icon);
                button.invalidate();
                button.repaint();
            } 
        }
	public void setTarget(JButton b) {
	    this.button = b;
	}
    }

    /**
     * A toolbar-specific separator. An object with dimension but
     * no contents used to divide buttons on a toolbar into groups.
     */
    static public class Separator extends JSeparator
    {
        private Dimension separatorSize;

        /** 
         * Create a new toolbar separator with the default size
         * defined by the current look and feel.
	 *
         */
        public Separator()
	{
	    this( null );  // let the UI define the default size
        }

        /** 
         * Create a new toolbar separator with the specified size
	 *
         * @param size the new size of the separator
         */
        public Separator( Dimension size )
	{
	    super( JSeparator.HORIZONTAL );
            setSeparatorSize(size);
        }

        /**
	 * Returns the name of the L&F class that renders this component.
	 *
	 * @return "ToolBarSeparatorUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
        public String getUIClassID()
	{
            return "ToolBarSeparatorUI";
	}

        /** 
         * Set the size of the separator
         *
         * @param size the new size of the separator
         */
        public void setSeparatorSize( Dimension size )
	{
            if (size != null) {
                separatorSize = size;
            } else {
                super.updateUI();
            }
	    this.invalidate();
	}

        /** 
         * Return the size of the separator
         *
         * @return the Dimension object containing the separator's
         *         size (This is a reference, NOT a copy!)
         */
        public Dimension getSeparatorSize()
	{
	    return separatorSize;
	}

        /** 
         * Return the minimum size for the separator
         *
         * @return the Dimension object containing the separator's
         *         minimum size
         */
        public Dimension getMinimumSize()
	{
            return getPreferredSize();
        }

        /** 
         * Return the maximum size for the separator
         *
         * @return the Dimension object containing the separator's
         *         maximum size
         */
        public Dimension getMaximumSize()
	{
            return getPreferredSize();
        }

        /** 
         * Return the preferred size for the separator
         *
         * @return the Dimension object containing the separator's
         *         preferred size
         */
        public Dimension getPreferredSize()
	{
	    return separatorSize.getSize();
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
     * Returns a string representation of this JToolBar. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JToolBar.
     */
    protected String paramString() {
        String paintBorderString = (paintBorder ?
				    "true" : "false");
        String marginString = (margin != null ?
			       margin.toString() : "");
        String floatableString = (floatable ?
				  "true" : "false");
        String orientationString = (orientation == HORIZONTAL ?
                                    "HORIZONTAL" : "VERTICAL");

        return super.paramString() +
        ",floatable=" + floatableString +
        ",margin=" + marginString +
        ",orientation=" + orientationString +
        ",paintBorder=" + paintBorderString;
    }

    
    /*
     * This PropertyChangeListener is used to adjust the default layout
     * manger when the toolBar is given a right-to-left ComponentOrientation.
     * This is a hack to work around the fact that the DefaultMenuLayout
     * (BoxLayout) isn't aware of ComponentOrientation.  When BoxLayout is
     * made aware of ComponentOrientation, this listener will no longer be
     * necessary.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if( name.equals("componentOrientation") ) {
                if( SwingUtilities.isLeftToRight(JToolBar.this) ) {
                    setLayout(new BoxLayout(JToolBar.this, BoxLayout.X_AXIS));
                } else {
                    setLayout(new RightToLeftToolBarLayout());
                }
            }
        }
    }
    
    private static class RightToLeftToolBarLayout
        extends FlowLayout implements UIResource
    {
        private RightToLeftToolBarLayout() {
            super(3/*FlowLayout.LEADING*/, 0, 0);
        }
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
            accessibleContext = new AccessibleJToolBar();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     */
    protected class AccessibleJToolBar extends AccessibleJComponent {

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = SwingUtilities.getAccessibleStateSet(JToolBar.this);
            // FIXME:  [[[WDW - need to add orientation from BoxLayout]]]
            // FIXME:  [[[WDW - need to do SELECTABLE if SelectionModel is added]]]
            return states;
        }
    
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOOL_BAR;
        }
    } // inner class AccessibleJToolBar
}
