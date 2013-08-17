/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.accessibility;

import java.util.Enumeration;
import java.util.Hashtable;
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
 * @version     1.12 10/05/99
 * @author      Willie Walker
 * @author	Peter Korn
 * @author	Lynn Monsanto
 */
public abstract class AccessibleBundle {

    private static Hashtable table = null;
    private final String defaultResourceBundleName 
	= "javax.accessibility.resources.accessibility";

    /**
     * The locale independent name of the state.  This is a programmatic 
     * name that is not intended to be read by humans.
     * @see #toDisplayString
     */
    protected String key = null;

    /**
     * Obtains the key as a localized string. 
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

	// loads the resource bundle if necessary
	loadResourceBundle(resourceBundleName, locale);

	// returns the localized string
	Object o = table.get(key);
	if (o != null && o instanceof String) {
	    return (String)o;
	}
	return key;
    }

    /**
     * Obtains the key as a localized string. 
     * If a localized string cannot be found for the key, the 
     * locale independent key stored in the role will be returned. 
     *
     * @param locale the locale for which to obtain a localized string
     * @return a localized String for the key.
     */
    public String toDisplayString(Locale locale) {
        return toDisplayString(defaultResourceBundleName, locale);
    }

    /** 
     * Gets localized string describing the key using the default locale.
     * @return a localized String describing the key for the default locale
     */
    public String toDisplayString() {
        return toDisplayString(Locale.getDefault());
    }

    /** 
     * Gets localized string describing the key using the default locale.
     * @return a localized String describing the key using the default locale
     * @see #toDisplayString
     */
    public String toString() {
        return toDisplayString();
    }

    /*
     * Loads the Accessibility resource bundle if necessary.
     */
    private void loadResourceBundle(String resourceBundleName,
				    Locale locale) {
	if (table == null) {
	    try {
		table = new Hashtable();
		ResourceBundle bundle
		    = ResourceBundle.getBundle(resourceBundleName,
					       locale);
		Enumeration iter = bundle.getKeys();
		while(iter.hasMoreElements()) {
		    String key = (String)iter.nextElement();
		    table.put(key, bundle.getObject(key));
		}
	    } catch (MissingResourceException e) {
		// Just return so toDisplayString() returns the
		// non-localized key.
		return;
	    }
	}
    }

}
