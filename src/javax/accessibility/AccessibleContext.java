/*
 * @(#)AccessibleContext.java	1.23 98/08/26
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

package javax.accessibility;

import java.util.Locale;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.IllegalComponentStateException;

/**
 * AccessibleContext represents the minimum information all accessible objects
 * return.  This information includes the accessible name, description, role,
 * and state of the object, as well as information about the parent and 
 * children of the object.  AccessibleContext also contains methods for
 * obtaining more specific accessibility information about a component.
 * If the component supports it, these methods will return an object that
 * implements one or more of the following interfaces:
 * <P><ul>
 * <li>{@link AccessibleAction} - the object can perform one or more actions.  
 * This interface provides the standard mechanism for an assistive
 * technology to determine what those actions are and tell the object
 * to perform those actions.  Any object that can be manipulated should
 * support this interface.
 * <li>{@link AccessibleComponent} - the object has a graphical representation.
 * This interface provides the standard mechanism for an assistive 
 * technology to determine and set the graphical representation of the 
 * object.  Any object that is rendered on the screen should support
 * this interface.
 * <li>{@link  AccessibleSelection} - the object allows its children to be 
 * selected.  This interface provides the standard mechanism for an
 * assistive technology to determine the currently selected children
 * as well as modify the selection set.  Any object that has children
 * that can be selected should support this interface.
 * <li>{@link AccessibleText} - the object presents editable textual information
 * on the display.  This interface provides the standard mechanism for
 * an assistive technology to access that text via its content, attributes,
 * and spatial location.  Any object that contains editable text should
 * support this interface.
 * <li>{@link AccessibleValue} - the object supports a numerical value.  This
 * interface provides the standard mechanism for an assistive technology
 * to determine and set the current value of the object, as well as the
 * minimum and maximum values.  Any object that supports a numerical value
 * should support this interface.</ul>
 * <P>In the future, additional interfaces (e.g., AccessibleTable) may be 
 * added, and the abstract class AccessibleContext will be updated
 * accordingly.
 *
 * @version     1.17 02/04/98 11:50:50
 * @author	Peter Korn
 * @author      Hans Muller
 * @author      Willie Walker
 */
public abstract class AccessibleContext {

   /**
    * Constant used to determine when the accessibleName property has
    * changed.  The old value in the PropertyChangeEvent will be the old 
    * accessibleName and the new value will be the new accessibleName.
    *
    * @see #getAccessibleName
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_NAME_PROPERTY = "AccessibleName";

   /**
    * Constant used to determine when the accessibleDescription property has
    * changed.  The old value in the PropertyChangeEvent will be the
    * old accessibleDescription and the new value will be the new
    * accessibleDescription.
    *
    * @see #getAccessibleDescription
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_DESCRIPTION_PROPERTY = "AccessibleDescription";

   /**
    * Constant used to determine when the accessibleStateSet property has 
    * changed.  The old value will be the old AccessibleState and the new
    * value will be the new AccessibleState in the accessibleStateSet.  
    * For example, if a component that supports the vertical and horizontal
    * states changes its orientation from vertical to horizontal, the old
    * value will be AccessibleState.VERTICAL and the new value will be
    * AccessibleState.HORIZONTAL.  Please note that either value can also 
    * be null.  For example, when a component changes from being enabled 
    * to disabled, the old value will be AccessibleState.ENABLED
    * and the new value will null.
    *
    * @see #getAccessibleStateSet
    * @see AccessibleState
    * @see AccessibleStateSet
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_STATE_PROPERTY = "AccessibleState";

   /**
    * Constant used to determine when the accessibleValue property has
    * changed.  The old value in the PropertyChangeEvent will be a Number 
    * representing the old value and the new value will be a Number 
    * representing the new value.
    *
    * @see #getAccessibleValue
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_VALUE_PROPERTY = "AccessibleValue";

   /**
    * Constant used to determine when the accessibleSelection has changed.
    * The old and new values in the PropertyChangeEvent are currently 
    * reserved for future use.
    *
    * @see #getAccessibleSelection
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_SELECTION_PROPERTY = "AccessibleSelection";

   /**
    * Constant used to determine when the accessibleText has changed.
    * The old and new values in the PropertyChangeEvent are currently 
    * reserved for future use.
    *
    * @see #getAccessibleText
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_TEXT_PROPERTY = "AccessibleText";

   /**
    * Constant used to determine when the accessibleText caret has changed.
    * has changed.  The old value in the PropertyChangeEvent will be an
    * integer representing the old caret position, and the new value will 
    * be an integer representing the new/current caret position.
    *
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_CARET_PROPERTY = "AccessibleCaret";

   /**
    * Constant used to determine when the visual appearance of the object
    * has changed.  The old and new values in the PropertyChangeEvent are 
    * currently reserved for future use.
    *
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_VISIBLE_DATA_PROPERTY = "AccessibleVisibleData";

   /**
    * Constant used to determine when Accessible children are added/removed
    * from the object.  If an Accessible child is being added, the old
    * value will be null and the new value the Accessible child.  If an
    * Accessible child is being removed, the old value will be the Accessible
    * child, and the new value will be null.
    *
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_CHILD_PROPERTY = "AccessibleChild";

   /**
    * Constant used to determine when the active descendant of a component
    * has changed.  The active descendant is used for objects such as 
    * list, tree, and table, which may have transient children.  When the
    * active descendant has changed, the old value of the property change
    * event will be the Accessible representing the previous active child, and 
    * the new value will be the Accessible representing the current active
    * child.
    *
    * @see #addPropertyChangeListener
    */
   public static final String ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY = "AccessibleActiveDescendant";

    /** 
     * The accessible parent of this object.
     *
     * @see #getAccessibleParent
     * @see #setAccessibleParent
     */
    protected Accessible accessibleParent = null;

    /**
     * A localized String containing the name of the object.
     *
     * @see #getAccessibleName
     * @see #setAccessibleName 
     */
    protected String accessibleName = null;

    /**
     * A localized String containing the description of the object.
     *
     * @see #getAccessibleDescription
     * @see #setAccessibleDescription 
     */
    protected String accessibleDescription = null;

    /**
     * Used to handle the listener list for property change events.
     *
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see #firePropertyChangeListener
     */
    private PropertyChangeSupport accessibleChangeSupport = null;

    /**
     * Get the accessibleName property of this object.  The accessibleName
     * property of an object is a localized String that designates the purpose
     * of the object.  For example, the accessibleName property of a label
     * or button might be the text of the label or button itself.  In the
     * case of an object that doesn't display its name, the accessibleName
     * should still be set.  For example, in the case of a text field used
     * to enter the name of a city, the accessibleName for the en_US locale
     * could be 'city.'
     *
     * @return the localized name of the object; null if this 
     * object does not have a name
     *
     * @see #setAccessibleName
     */
    public String getAccessibleName() {
	return accessibleName;
    }
	
    /**
     * Set the localized accessible name of this object.  Changing the
     * name will cause a PropertyChangeEvent to be fired for the
     * ACCESSIBLE_NAME_PROPERTY property.
     *
     * @param s the new localized name of the object.
     *
     * @see #getAccessibleName
     * @see #addPropertyChangeListener
     */
    public void setAccessibleName(String s) {
	String oldName = accessibleName;
	accessibleName = s;
	firePropertyChange(ACCESSIBLE_NAME_PROPERTY,oldName,accessibleName);
    }

    /**
     * Get the accessibleDescription property of this object.  The
     * accessibleDescription property of this object is a short localized
     * phrase describing the purpose of the object.  For example, in the 
     * case of a 'Cancel' button, the accessibleDescription could be
     * 'Ignore changes and close dialog box.'
     *
     * @return the localized description of the object; null if 
     * this object does not have a description
     *
     * @see #setAccessibleDescription
     */
    public String getAccessibleDescription() {
	return accessibleDescription;
    }

    /**
     * Set the accessible description of this object.  Changing the
     * name will cause a PropertyChangeEvent to be fired for the
     * ACCESSIBLE_DESCRIPTION_PROPERTY property.
     *
     * @param s the new localized description of the object
     *
     * @see #setAccessibleName
     * @see #addPropertyChangeListener
     */
    public void setAccessibleDescription(String s) {
	String oldDescription = accessibleDescription;
	accessibleDescription = s;
	firePropertyChange(ACCESSIBLE_DESCRIPTION_PROPERTY,
			   oldDescription,accessibleDescription);
    }

    /**
     * Get the role of this object.  The role of the object is the generic
     * purpose or use of the class of this object.  For example, the role
     * of a push button is AccessibleRole.PUSH_BUTTON.  The roles in 
     * AccessibleRole are provided so component developers can pick from
     * a set of predefined roles.  This enables assistive technologies to
     * provide a consistent interface to various tweaked subclasses of 
     * components (e.g., use AccessibleRole.PUSH_BUTTON for all components
     * that act like a push button) as well as distinguish between sublasses
     * that behave differently (e.g., AccessibleRole.CHECK_BOX for check boxes
     * and AccessibleRole.RADIO_BUTTON for radio buttons).
     * <p>Note that the AccessibleRole class is also extensible, so 
     * custom component developers can define their own AccessibleRole's
     * if the set of predefined roles is inadequate.
     *
     * @return an instance of AccessibleRole describing the role of the object
     * @see AccessibleRole
     */
    public abstract AccessibleRole getAccessibleRole();
    
    /**
     * Get the state set of this object.  The AccessibleStateSet of an object
     * is composed of a set of unique AccessibleState's.  A change in the 
     * AccessibleStateSet of an object will cause a PropertyChangeEvent to 
     * be fired for the ACCESSIBLE_STATE_PROPERTY property.
     *
     * @return an instance of AccessibleStateSet containing the 
     * current state set of the object
     * @see AccessibleStateSet
     * @see AccessibleState
     * @see #addPropertyChangeListener
     */
    public abstract AccessibleStateSet getAccessibleStateSet();

    /**
     * Get the Accessible parent of this object.
     *
     * @return the Accessible parent of this object; null if this
     * object does not have an Accessible parent
     */
    public Accessible getAccessibleParent() {
	return accessibleParent;
    }

    /**
     * Set the Accessible parent of this object.  This is meant to be used
     * only in the situations where the actual component's parent should 
     * not be treated as the component's accessible parent and is a method 
     * that should only be called by the parent of the accessible child. 
     *
     * @param a - Accessible to be set as the parent	
     */
    public void setAccessibleParent(Accessible a) {
        accessibleParent = a;
    }

    /**
     * Get the 0-based index of this object in its accessible parent.
     *
     * @return the 0-based index of this object in its parent; -1 if this 
     * object does not have an accessible parent.
     *
     * @see #getAccessibleParent 
     * @see #getAccessibleChildrenCount
     * @see #getAccessibleChild
     */
    public abstract int getAccessibleIndexInParent();

    /**
     * Returns the number of accessible children of the object.
     *
     * @return the number of accessible children of the object.
     */
    public abstract int getAccessibleChildrenCount();

    /**
     * Return the specified Accessible child of the object.  The Accessible
     * children of an Accessible object are zero-based, so the first child 
     * of an Accessible child is at index 0, the second child is at index 1,
     * and so on.
     *
     * @param i zero-based index of child
     * @return the Accessible child of the object
     * @see #getAccessibleChildrenCount
     */
    public abstract Accessible getAccessibleChild(int i);

    /** 
     * Gets the locale of the component. If the component does not have a 
     * locale, then the locale of its parent is returned.  
     *
     * @return This component's locale.  If this component does not have 
     * a locale, the locale of its parent is returned.
     *
     * @exception IllegalComponentStateException 
     * If the Component does not have its own locale and has not yet been 
     * added to a containment hierarchy such that the locale can be
     * determined from the containing parent. 
     */
    public abstract Locale getLocale() throws IllegalComponentStateException;

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all Accessible properties and will
     * be called when those properties change.
     *
     * @see #ACCESSIBLE_NAME_PROPERTY
     * @see #ACCESSIBLE_DESCRIPTION_PROPERTY
     * @see #ACCESSIBLE_STATE_PROPERTY
     * @see #ACCESSIBLE_VALUE_PROPERTY
     * @see #ACCESSIBLE_SELECTION_PROPERTY
     * @see #ACCESSIBLE_TEXT_PROPERTY
     * @see #ACCESSIBLE_VISIBLE_DATA_PROPERTY
     *
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (accessibleChangeSupport == null) {
            accessibleChangeSupport = new PropertyChangeSupport(this);
        }
        accessibleChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (accessibleChangeSupport != null) {
            accessibleChangeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Get the AccessibleAction associated with this object that supports
     * one or more actions. 
     *
     * @return AccessibleAction if supported by object; else return null
     * @see AccessibleAction
     */
    public AccessibleAction getAccessibleAction() {
	return null;
    }

    /**
     * Get the AccessibleComponent associated with this object that has a 
     * graphical representation.
     *
     * @return AccessibleComponent if supported by object; else return null
     * @see AccessibleComponent
     */
    public AccessibleComponent getAccessibleComponent() {
	return null;
    }

    /**
     * Get the AccessibleSelection associated with this object which allows its
     * Accessible children to be selected.  
     * 
     * @return AccessibleSelection if supported by object; else return null
     * @see AccessibleSelection
     */
    public AccessibleSelection getAccessibleSelection() {
	return null;
    }

//    /**
//     * Get the AccessibleTable associated with this object if one
//     * exists.  Otherwise return null.
//     */
//    public AccessibleTable getAccessibleTable() {
//        return null;
//    }

    /**
     * Get the AccessibleText associated with this object presently editable
     * text on the display.
     *
     * @return AccessibleText if supported by object; else return null
     * @see AccessibleText
     */
    public AccessibleText getAccessibleText() {
	return null;
    }

    /**
     * Get the AccessibleValue associated with this object that supports a 
     * Numerical value. 
     * 
     * @return AccessibleValue if supported by object; else return null 
     * @see AccessibleValue
     */
    public AccessibleValue getAccessibleValue() {
	return null;
    }

    /**
     * Support for reporting bound property changes.  If oldValue and 
     * newValue are not equal and the PropertyChangeEvent listener list 
     * is not empty, then fire a PropertyChange event to each listener.
     * In general, this is for use by the Accessible objects themselves
     * and should not be called by an application program.
     * @param propertyName  The programmatic name of the property that
     * was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @see java.beans.PropertyChangeSupport
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see #ACCESSIBLE_NAME_PROPERTY
     * @see #ACCESSIBLE_DESCRIPTION_PROPERTY
     * @see #ACCESSIBLE_STATE_PROPERTY
     * @see #ACCESSIBLE_VALUE_PROPERTY
     * @see #ACCESSIBLE_SELECTION_PROPERTY
     * @see #ACCESSIBLE_TEXT_PROPERTY
     * @see #ACCESSIBLE_VISIBLE_DATA_PROPERTY
     */
    public void firePropertyChange(String propertyName, 
				   Object oldValue, 
				   Object newValue) {
        if (accessibleChangeSupport != null) {
	    accessibleChangeSupport.firePropertyChange(propertyName, 
						       oldValue, 
						       newValue);
	}
    }
}
