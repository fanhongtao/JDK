/*
 * @(#)ListResourceBundle.java	1.6 97/01/29
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
import java.util.Hashtable;

/**
 * <code>ListResourceBundle</code> is a abstract subclass of
 * <code>ResourceBundle</code> that manages resources for a locale
 * in a convenient and easy to use list. See <code>ResourceBundle</code> for
 * more information about resource bundles in general.
 *
 * <P>
 * Subclasses must override <code>getContents</code> and provide an array,
 * where each item in the array is a pair of objects.
 * The first element of each pair is a <code>String</code> key, and the second
 * is the value associated with that key.
 *
 * <p>
 * In the following example, the keys are of the form "s1"... The actual
 * keys are entirely up to your choice, so long as they are the same as
 * the keys you use in your program to retrieve the objects from the bundle.
 * Keys are case-sensitive. <code>MyResource</code> is the default version
 * of the bundle family, and <code>MyResource_fr</code> is the french version:
 * <blockquote>
 * <pre>
 * //====================
 * class MyResource extends ListResourceBundle {
 * 	public Object[][] getContents() {
 * 		return contents;
 * 	}
 * 	static final Object[][] contents = {
 * 	// LOCALIZE THIS
 * 		{"s1", "3"},		// starting value in choice field
 * 		{"s2", "MyDisk"},	// starting value in string field
 * 		{"s3", "3 Mar 96"},	// starting value in date field
 * 		{"s4", "The disk '{1}' contained {0} on {2}."},	// initial pattern
 * 		{"s5", "0"},		// first choice number
 * 		{"s6", "no files"},	// first choice value
 * 		{"s7", "1"},		// second choice number
 * 		{"s8", "one file"},	// second choice value
 * 		{"s9", "2"},		// third choice number
 * 		{"s10", "{0}|3 files"},	// third choice value
 * 		{"s11", "format threw an exception: {0}"},	// generic exception message
 * 		{"s12", "ERROR"},	// what to show in field in case of error
 * 		{"s14", "Result"},	// label for formatted stuff
 * 		{"s13", "Dialog"},	// standard font
 * 		{"s15", "Pattern"},	// label for standard pattern
 * 		{"s16", new Dimension(1,5)}	// real object, not just string
 * 	// END OF MATERIAL TO LOCALIZE
 * 	};
 * }
 * //====================
 * class MyResource_fr  extends ListResourceBundle {
 * 	public Object[][] getContents() {
 * 		return contents;
    	}
 * 	static final Object[][] contents = {
 * 	// LOCALIZE THIS
 * 		{"s1", "3"},		// starting value in choice field
 * 		{"s2", "MonDisk"},	// starting value in string field
 * 		{"s3", "3 Mar 96"},	// starting value in date field
 * 		{"s4", "Le disk '{1}' a {0} a {2}."},	// initial pattern
 * 		{"s5", "0"},		// first choice number
 * 		{"s6", "pas de files"},	// first choice value
 * 		{"s7", "1"},		// second choice number
 * 		{"s8", "une file"},	// second choice value
 * 		{"s9", "2"},		// third choice number
 * 		{"s10", "{0}|3 files"},	// third choice value
 * 		{"s11", "Le format a jete une exception: {0}"},	// generic exception message
 * 		{"s12", "ERROR"},	// what to show in field in case of error
 * 		{"s14", "Resulte"},	// label for formatted stuff
 * 		{"s13", "Dialogue"},	// standard font
 * 		{"s15", "Pattern"},	// label for standard pattern
 * 		{"s16", new Dimension(1,3)}	// real object, not just string
 * 	// END OF MATERIAL TO LOCALIZE
 * 	};
 * }
 * </pre>
 * </blockquote>
 * @see ResourceBundle
 * @see PropertyResourceBundle
 */
public abstract class ListResourceBundle extends ResourceBundle {
    /**
     * Override of ResourceBundle, same semantics
     */
    public final Object handleGetObject(String key) {
        // lazily load the lookup hashtable.
        if (lookup == null) {
            loadLookup();
        }
        return lookup.get(key); // this class ignores locales
    }

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration getKeys() {
        // lazily load the lookup hashtable.
        if (lookup == null) {
            loadLookup();
        }
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

    /**
     * See class description.
     */
    abstract protected Object[][] getContents();

    // ==================privates====================

    /**
     * We lazily load the lookup hashtable.  This function does the
     * loading.
     */
    private void loadLookup() {
        Object[][] contents = getContents();
        lookup = new Hashtable(contents.length);
        for (int i = 0; i < contents.length; ++i) {
            lookup.put(contents[i][0],contents[i][1]);
        }        
    }
    
    private Hashtable lookup = null;
}
