/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of a menu separator -- a divider between menu items
 * that breaks them up into logical groupings.
 * Instead of using <code>JSeparator</code> directly,
 * you can use the <code>JMenu</code> or <code>JPopupMenu</code>
 * <code>addSeparator</code> method
 * to create and add a separator.
 *
 * <p>
 *
 * For more information and examples see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/menu.html">How to Use Menus</a>,
 * a section in <em>The Java Tutorial.</em>
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
 *    description: A divider between menu items.
 *
 * @version 1.43 02/06/02
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

    /** Creates a new horizontal separator. */
    public JSeparator()
    {
        this( HORIZONTAL );
    }

    /**
     * Creates a new separator with the specified horizontal or
     * vertical orientation. 
     *
     * @param orientation an integer specifying
     *		<code>SwingConstants.HORIZONTAL</code> or
     *          <code>SwingConstants.VERTICAL</code>
     * @exception IllegalArgumentException if <code>orientation</code>
     *		is neither <code>SwingConstants.HORIZONTAL</code> nor
     *		<code>SwingConstants.VERTICAL</code>
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
     * Notification from the <code>UIFactory</code> that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * <code>UIFactorys</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((SeparatorUI)UIManager.getUI(this));
    }
    

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "SeparatorUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /** 
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more 
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
     * @param orientation  either <code>SwingConstants.HORIZONTAL</code>
     *			or <code>SwingConstants.VERTICAL</code>
     * @exception IllegalArgumentException  if <code>orientation</code>
     *		is neither <code>SwingConstants.HORIZONTAL</code>
     *		nor <code>SwingConstants.VERTICAL</code>
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
     * Returns a string representation of this <code>JSeparator</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JSeparator</code>
     */
    protected String paramString() {
	String orientationString = (orientation == HORIZONTAL ?
				    "HORIZONTAL" : "VERTICAL");

	return super.paramString() +
	",orientation=" + orientationString;
    }

    /**
     * Identifies whether or not this component can receive the focus.
     * <code>JSeparator</code>s cannot recieve focus.
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
     * Gets the AccessibleContext associated with this JSeparator. 
     * For separators, the AccessibleContext takes the form of an 
     * AccessibleJSeparator. 
     * A new AccessibleJSeparator instance is created if necessary.
     *
     * @return an AccessibleJSeparator that serves as the 
     *         AccessibleContext of this JSeparator
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJSeparator();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JSeparator</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to separator user-interface elements.
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
