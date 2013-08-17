/*
 * @(#)FeatureDescriptor.java	1.16 98/07/01
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
 * The FeatureDescriptor class is the common baseclass for PropertyDescriptor,
 * EventSetDescriptor, and MethodDescriptor, etc.
 * <p>
 * It supports some common information that can be set and retrieved for
 * any of the introspection descriptors.
 * <p>
 * In addition it provides an extension mechanism so that arbitrary
 * attribute/value pairs can be associated with a design feature.
 */

public class FeatureDescriptor {


    public FeatureDescriptor() {
    }

    /**
     * @return The programmatic name of the property/method/event
     */
    public String getName() {
	return name;
    }

    /**
     * @param name  The programmatic name of the property/method/event
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return The localized display name for the property/method/event.
     *	This defaults to the same as its programmatic name from getName.
     */
    public String getDisplayName() {
	if (displayName == null) {
	    return getName();
	}
	return displayName;
    }

    /**
     * @param displayName  The localized display name for the
     *		property/method/event.
     */
    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    /**
     * The "expert" flag is used to distinguish between those features that are
     * intended for expert users from those that are intended for normal users.
     *
     * @return True if this feature is intended for use by experts only.
     */
    public boolean isExpert() {
	return expert;
    }

    /**
     * The "expert" flag is used to distinguish between features that are
     * intended for expert users from those that are intended for normal users.
     *
     * @param expert True if this feature is intended for use by experts only.
     */
    public void setExpert(boolean expert) {
	this.expert = expert;
    }

    /**
     * The "hidden" flag is used to identify features that are intended only
     * for tool use, and which should not be exposed to humans.
     *
     * @return True if this feature should be hidden from human users.
     */
    public boolean isHidden() {
	return hidden;
    }

    /**
     * The "hidden" flag is used to identify features that are intended only
     * for tool use, and which should not be exposed to humans.
     *
     * @param hidden  True if this feature should be hidden from human users.
     */
    public void setHidden(boolean hidden) {
	this.hidden = hidden;
    }

    /**
     * @return  A localized short description associated with this 
     *   property/method/event.  This defaults to be the display name.
     */
    public String getShortDescription() {
	if (shortDescription == null) {
	    return getDisplayName();
	}
	return shortDescription;
    }

    /**
     * You can associate a short descriptive string with a feature.  Normally
     * these descriptive strings should be less than about 40 characters.
     * @param text  A (localized) short description to be associated with
     * this property/method/event.
     */
    public void setShortDescription(String text) {
	shortDescription = text;
    }

    /**
     * Associate a named attribute with this feature.
     * @param attributeName  The locale-independent name of the attribute
     * @param value  The value.
     */
    public void setValue(String attributeName, Object value) {
	if (table == null) {
	    table = new java.util.Hashtable();
	}
	table.put(attributeName, value);
    }

    /**
     * Retrieve a named attribute with this feature.
     * @param attributeName  The locale-independent name of the attribute
     * @return  The value of the attribute.  May be null if
     *	   the attribute is unknown.
     */
    public Object getValue(String attributeName) {
	if (table == null) {
	   return null;
	}
	return table.get(attributeName);
    }

    /**
     * @return  An enumeration of the locale-independent names of any 
     *    attributes that have been registered with setValue.
     */
    public java.util.Enumeration attributeNames() {
	if (table == null) {
	    table = new java.util.Hashtable();
	}
	return table.keys();
    }

    /**
     * Package-private constructor,
     * Merge information from two FeatureDescriptors.
     * The merged hidden and expert flags are formed by or-ing the values.
     * In the event of other conflicts, the second argument (y) is
     * given priority over the first argument (x).
     * @param x  The first (lower priority) MethodDescriptor
     * @param y  The second (higher priority) MethodDescriptor
     */
    FeatureDescriptor(FeatureDescriptor x, FeatureDescriptor y) {
	expert = x.expert | y.expert;
	hidden = x.hidden | y.hidden;
	name = y.name;
	shortDescription = x.shortDescription;
	if (y.shortDescription != null) {
	    shortDescription = y.shortDescription;
	}
	displayName = x.displayName;
	if (y.displayName != null) {
	    displayName = y.displayName;
	}
	addTable(x.table);
	addTable(y.table);
    }

    private void addTable(java.util.Hashtable t) {
	if (t == null) {
	    return;
	}
	java.util.Enumeration keys = t.keys();
	while (keys.hasMoreElements()) {
	    String key = (String)keys.nextElement();
	    Object value = t.get(key);
	    setValue(key, value);
	}
    }

    private boolean expert;
    private boolean hidden;
    private String shortDescription;
    private String name;
    private String displayName;
    private java.util.Hashtable table;
}
