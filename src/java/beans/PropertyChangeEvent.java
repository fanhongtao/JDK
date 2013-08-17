/*
 * @(#)PropertyChangeEvent.java	1.22 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

/**
 * A "PropertyChange" event gets delivered whenever a bean changes a "bound"
 * or "constrained" property.  A PropertyChangeEvent object is sent as an
 * argument to the PropertyChangeListener and VetoableChangeListener methods.
 * <P>
 * Normally PropertyChangeEvents are accompanied by the name and the old
 * and new value of the changed property.  If the new value is a builtin
 * type (such as int or boolean) it must be wrapped as the 
 * corresponding java.lang.* Object type (such as Integer or Boolean).
 * <P>
 * Null values may be provided for the old and the new values if their
 * true values are not known.
 * <P>
 * An event source may send a null object as the name to indicate that an
 * arbitrary set of if its properties have changed.  In this case the
 * old and new values should also be null.
 */

public class PropertyChangeEvent extends java.util.EventObject {

    /**
     * @param source  The bean that fired the event.
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public PropertyChangeEvent(Object source, String propertyName,
				     Object oldValue, Object newValue) {
	super(source);
	this.propertyName = propertyName;
	this.newValue = newValue;
	this.oldValue = oldValue;
    }

    /**
     * @return  The programmatic name of the property that was changed.
     *		May be null if multiple properties have changed.
     */
    public String getPropertyName() {
	return propertyName;
    }
    
    /**
     * @return  The new value for the property, expressed as an Object.
     *		May be null if multiple properties have changed.
     */
    public Object getNewValue() {
	return newValue;
    }

    /**
     * @return  The old value for the property, expressed as an Object.
     *		May be null if multiple properties have changed.
     */
    public Object getOldValue() {
	return oldValue;
    }

    /**
     * @param propagationId  The propagationId object for the event.
     */
    public void setPropagationId(Object propagationId) {
	this.propagationId = propagationId;
    }

    /**
     * The "propagationId" field is reserved for future use.  In Beans 1.0
     * the sole requirement is that if a listener catches a PropertyChangeEvent
     * and then fires a PropertyChangeEvent of its own, then it should
     * make sure that it propagates the propagationId field from its
     * incoming event to its outgoing event.
     *
     * @return the propagationId object associated with a bound/constrained
     *		property update.
     */
    public Object getPropagationId() {
	return propagationId;
    }

    private String propertyName;
    private Object newValue;
    private Object oldValue;
    private Object propagationId;
}
