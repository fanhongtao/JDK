/*
 * @(#)JSeparator.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;

import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of a Menu Separator -- a divider between menu items
 * that breaks them up into logical groupings.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *      attribute: isContainer false
 * @version 1.34 11/19/98
 * @author Georges Saab
 * @author Jeff Shapiro
 */
public class JSeparator extends JComponent implements SwingConstants, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "SeparatorUI";

    private int orientation = HORIZONTAL;

    /** Create a new horizontal separator. */
    public JSeparator()
    {
        this( HORIZONTAL );
    }

    /**
     * Create a new separator with the specified horizontal or
     * vertical orientation. 
     *
     * @param orientation an int specifying SwingConstants.HORIZONTAL or
     *        SwingConstants.VERTICAL
     */
    public JSeparator( int orientation )
    {
        checkOrientation( orientation );
	this.orientation = orientation;

        updateUI();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the SeparatorUI object that renders this component
     */
    public SeparatorUI getUI() {
        return (SeparatorUI)ui;
    }
    
    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the SeparatorUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     * description: The menu item's UI delegate
     *       bound: true
     *      expert: true
     *      hidden: true
     */
    public void setUI(SeparatorUI ui) {
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
        setUI((SeparatorUI)UIManager.getUI(this));
    }
    

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "SeparatorUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
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
     * Returns the orientation of this separator.
     *
     * @return   The value of the orientation property, one of the 
     *           following constants defined in <code>SwingConstants</code>:
     *           <code>VERTICAL</code>, or
     *           <code>HORIZONTAL</code>.
     *
     * @see SwingConstants
     * @see #setOrientation
     */
    public int getOrientation() {
        return this.orientation;
    }

    /**
     * Sets the orientation of the separator.
     * The default value of this property is HORIZONTAL.
     * 
     * @see SwingConstants
     * @see #getOrientation
     * @beaninfo
     *        bound: true
     *    preferred: true
     *         enum: HORIZONTAL SwingConstants.HORIZONTAL
     *               VERTICAL   SwingConstants.VERTICAL
     *    attribute: visualUpdate true
     *  description: The orientation of the separator.
     */
    public void setOrientation( int orientation ) {
        if (this.orientation == orientation) {
            return;
        }
        int oldValue = this.orientation;
        checkOrientation( orientation );
        this.orientation = orientation;
        firePropertyChange("orientation", oldValue, orientation);
        revalidate();
	repaint();
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
     * Returns a string representation of this JSeparator. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JSeparator.
     */
    protected String paramString() {
	String orientationString = (orientation == HORIZONTAL ?
				    "HORIZONTAL" : "VERTICAL");

	return super.paramString() +
	",orientation=" + orientationString;
    }

    /**
     * Identifies whether or not this component can receive the focus.
     * JSeparators cannot recieve focus.
     *
     * @return false
     */
    public boolean isFocusTraversable()
    {
        return false;
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
            accessibleContext = new AccessibleJSeparator();
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
    protected class AccessibleJSeparator extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SEPARATOR;
        }
    }
}
