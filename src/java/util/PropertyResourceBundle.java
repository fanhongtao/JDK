/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 */

package java.util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * <code>PropertyResourceBundle</code> is a concrete subclass of
 * <code>ResourceBundle</code> that manages resources for a locale
 * using a set of static strings from a property file. See
 * {@link ResourceBundle ResourceBundle} for more information about resource
 * bundles. See {@link Properties Properties} for more information
 * about properties files, in particular the
 * <a href="Properties.html#encoding">information on character encodings</a>.
 *
 * <p>
 * Unlike other types of resource bundle, you don't subclass
 * <code>PropertyResourceBundle</code>.  Instead, you supply properties
 * files containing the resource data.  <code>ResourceBundle.getBundle()</code>
 * will automatically look for the appropriate properties file and create a
 * <code>PropertyResourceBundle</code> that refers to it.  The resource
 * bundle name that you pass to <code>ResourceBundle.getBundle()</code> is
 * the file name of the properties file, not the class name of the object that
 * is returned.
 *
 * <p>
 * For example, if you say <code>ResourceBundle.getBundle("MyResources",
 * new Locale("fr", "FR"));</code> the resource bundle lookup mechanism
 * will search the class path for a file called
 * <code>MyResources_fr_FR.properties</code>.
 *
 * <p>
 * If a real class and a properties file with a particular name both exist,
 * the class wins; the properties file will only be used if there is no class
 * with the desired name.
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
 * @see Properties
 * @since JDK1.1
 */
public class PropertyResourceBundle extends ResourceBundle {
    /**
     * Creates a property resource
     * @param stream property file to read from.
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
            final Enumeration myKeys = lookup.keys();
            final Enumeration parentKeys = parent.getKeys();

            result = new Enumeration() {
                public boolean hasMoreElements() {
                    if (temp == null)
                        nextElement();
                    return temp != null;
                }

                public Object nextElement() {
                    Object returnVal = temp;
                    if (myKeys.hasMoreElements())
                        temp = myKeys.nextElement();
                    else {
                        temp = null;
                        while (temp == null && parentKeys.hasMoreElements()) {
                            temp = parentKeys.nextElement();
                            if (lookup.containsKey(temp))
                                temp = null;
                        }
                    }
                    return returnVal;
                }

                Object temp = null;
            };
        } else {
            result = lookup.keys();
        }
        return result;
    }

    // ==================privates====================

    private Properties lookup = new Properties();
}
