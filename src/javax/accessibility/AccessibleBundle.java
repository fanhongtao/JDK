/*
 * @(#)AccessibleBundle.java	1.9 98/08/26
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

import java.util.Vector;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Base class used to maintain a strongly typed enumeration.  This is
 * the superclass of {@link AccessibleState} and {@link AccessibleRole}. 
 * <p>The toDisplayString method allows you to obtain the localized string 
 * for a locale independent key from a predefined ResourceBundle for the 
 * keys defined in this class.  This localized string is intended to be
 * readable by humans.
 *
 * @see AccessibleRole
 * @see AccessibleState
 *
 * @version     1.9 08/26/98 21:14:01
 * @author      Willie Walker
 * @author	Peter Korn
 */
public abstract class AccessibleBundle {

    /**
     * The locale independent name of the state.  This is a programmatic 
     * name that is not intended to be read by humans.
     * @see #toDisplayString
     */
    protected String key = null;

    /**
     * Obtain the key as a localized string. 
     * If a localized string cannot be found for the key, the 
     * locale independent key stored in the role will be returned. 
     * This method is intended to be used only by subclasses so that they 
     * can specify their own resource bundles which contain localized
     * strings for their keys.
     * @param resourceBundleName the name of the resource bundle to use for 
     * lookup
     * @param locale the locale for which to obtain a localized string
     * @return a localized String for the key.
     */
    protected String toDisplayString(String resourceBundleName, 
			             Locale locale) {
        // [[[FIXME:  WDW - obtaining resource bundles can be
        // expensive, especially when obtaining them from ASCII
        // properties files.  A time performace improvement can be
        // made here if we cache the resource bundles by locale.
        // We probably should also see if ResourceBundle itself
        // caches these for us.  If it does, it would be nice.]]]
        ResourceBundle resources;
        String displayString = null;

        try {
            resources = ResourceBundle.getBundle(resourceBundleName, 
                                                 locale);
            displayString = resources.getString(key);
        } catch (MissingResourceException mre) {
            System.err.println(mre 
		    + ":  " + resourceBundleName + " not found");
        }

        if (displayString != null) {
	    return displayString;
	} else {
	    return key;
        }
    }

    /**
     * Obtain the key as a localized string. 
     * If a localized string cannot be found for the key, the 
     * locale independent key stored in the role will be returned. 
     *
     * @param locale the locale for which to obtain a localized string
     * @return a localized String for the key.
     */
    public String toDisplayString(Locale locale) {
        return toDisplayString(
		"javax.accessibility.AccessibleResourceBundle", 
                locale);
    }

    /** 
     * Get localized string describing the key using the default locale.
     * @return a localized String describing the key for the default locale
     */
    public String toDisplayString() {
        return toDisplayString(Locale.getDefault());
    }

    /** 
     * Get localized string describing the key using the default locale.
     * @return a localized String describing the key using the default locale
     * @see #toDisplayString
     */
    public String toString() {
        return toDisplayString();
    }
}
