/*
 * @(#)PropertyResourceBundle.java	1.7 97/01/29
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package java.util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * <code>PropertyResourceBundle</code> is an abstract subclass of
 * <code>ResourceBundle</code> that manages resources for a locale
 * using a set of static strings from a property file. See
 * <code>ResourceBundle</code> for more information about resource
 * bundles in general.
 *
 * <p>
 * The property file contains the keys that you use in your source code
 * in calls to <code>ResourceBundle.getString</code> and similar methods,
 * and their corresponding values, etc.
 * The name of the property file indicates the resource bundle's family
 * and locale.
 *
 * <p>
 * In the following example, the keys are of the form "s1"... The actual
 * keys are entirely up to your choice, so long as they are the same as
 * the keys you use in your program to retrieve the objects from the bundle.
 * Keys are case-sensitive.
 * <blockquote>
 * <pre>
 * s1=3
 * s2=MeinDisk
 * s3=3 Mar 96
 * s4=Der disk '{1}' a {0} a {2}.
 * s5=0
 * s6=keine Datein
 * s7=1
 * s8=ein Datei
 * s9=2
 * s10={0}|3 Datein
 * s11=Der Format worf ein Exception: {0}
 * s12=ERROR
 * s14=Resulte
 * s13=Dialogue
 * s15=Pattern
 * s16=1,3
 * </pre>
 * </blockquote>
 *
 * @see ResourceBundle
 * @see ListResourceBundle
 */
public class PropertyResourceBundle extends ResourceBundle {
    /**
     * Creates a property resource
     * @param file property file to read from.
     */
    public PropertyResourceBundle (InputStream stream) throws IOException {
        lookup.load(stream);
    }

    /**
     * Override of ResourceBundle, same semantics
     */
    public Object handleGetObject(String key) {
        Object obj = lookup.get(key);
        return obj; // once serialization is in place, you can do non-strings
    }

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration getKeys() {
	Enumeration result = null;
	if (parent != null) {
	    Hashtable temp = new Hashtable();
	    for (Enumeration parentKeys = parent.getKeys() ;
		 parentKeys.hasMoreElements() ; /* nothing */) {
		temp.put(parentKeys.nextElement(), this);
	    }
	    for (Enumeration thisKeys = lookup.keys();
		 thisKeys.hasMoreElements() ; /* nothing */) {
		temp.put(thisKeys.nextElement(), this);
	    }
	    result = temp.keys();
	} else {
	    result = lookup.keys();
	}
        return result;
    }
    
    // ==================privates====================

    private Properties lookup = new Properties();
}
