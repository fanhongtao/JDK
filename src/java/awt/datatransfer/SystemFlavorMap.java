/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

import java.awt.Toolkit;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.Properties;


/**
 * <p>
 * The SystemFlavorMap is an externally configurable map that maps platform native
 * type names (strings) to MIME type strings, and also their associated 
 * DataFlavors.
 * </p>
 * This map is used by the DnD system to map platform data types to MIME
 * types to enable the transfer of objects between Java and the platform via
 * the platform DnD System.
 * </p>
 *
 * @version 1.20, 02/06/02
 * @since 1.2
 *
 */

public final class SystemFlavorMap implements FlavorMap {

    /**
     * constant prefix used to tag Java types converted to native platform type
     */

    private static String JavaMIME = "JAVA_DATAFLAVOR:";

    /*
     * system singleton
     */

    private static final WeakHashMap flavorMaps = new WeakHashMap();

    /**
     * get the default system implementation
     */

    public static FlavorMap getDefaultFlavorMap() {
	ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
	if (contextClassLoader == null)
	    contextClassLoader = ClassLoader.getSystemClassLoader();

	FlavorMap fm;

	synchronized(flavorMaps) {
 	    if ((fm = (FlavorMap)flavorMaps.get(contextClassLoader)) == null) 
		flavorMaps.put(contextClassLoader, fm = new SystemFlavorMap());
	}

	return fm;
    }


    /**
     * construct a SystemFlavorMap
     */

    private SystemFlavorMap() {
	super();

	final Properties defaults[] = {null};
	final boolean shouldReturn[]= {false};

	java.security.AccessController.doPrivileged(
			    new java.security.PrivilegedAction() {
	    public Object run() {
		String     sep      = File.separator;
		String     home     = System.getProperty("java.home");

		String     deffmURL = "file:" +
		                   home   +
				   sep    +
				   "lib"  +
				   sep    +
				   "flavormap.properties";
		try {
		    defaults[0] = new Properties();
		    defaults[0].load(((URL)new URL(deffmURL)).openStream());
		} catch (Exception e) {
		    System.err.println("Exception:" + e + " while loading default flavormap.properties file URL:" + deffmURL);
		    defaults[0] = null;
		}

		nativeToMIME = new Properties(defaults[0]);

		String	    propURLName = Toolkit.getDefaultToolkit().getProperty("AWT.DnD.flavorMapFileURL", null);
		InputStream is              = null;

		if (propURLName != null) try {
		    is = ((URL)new URL(propURLName)).openStream();
		} catch (MalformedURLException fnfe) {
		    System.err.println(getClass().getName() + ": Malformed URL for flavormap: " + propURLName);
		} catch (IOException ioe) {
		    System.err.println(getClass().getName() + ": IOException during open of flavormap: " + propURLName);
		}

		if (is != null) try {
		    nativeToMIME.load(is);
		} catch (IOException ioe) {
		    System.err.println(getClass().getName() + "IOException during load of flavormap: " + propURLName);
		    shouldReturn[0] = true;
		    return null;
		}
		return null;
	    }
	});

	if (shouldReturn[0])
	    return;

	int tsize = nativeToMIME.size() + (defaults[0] != null ? defaults[0].size() : 0);
	int size  = tsize > 0 ? tsize : 1;


        flavorToNative = new HashMap(size);
        nativeToFlavor = new HashMap(size);

	if (tsize > 0) {
	    Enumeration e = nativeToMIME.propertyNames();

	    while (e.hasMoreElements()) {
	        String     natives = (String)e.nextElement();
		String	   mime    = (String)nativeToMIME.getProperty(natives);
		DataFlavor df      = null;

		// we try the first form of the constructor to see if
		// the Mime Type fully specifies a MIME for a DataFlavor
		// if that fails we'll construct using another form that
		// enforces a default representation class and HPN

		try {
	            df = new DataFlavor(mime);
		} catch (Exception ee) {
		    try {
		        df = new DataFlavor(mime, (String)null);
		    } catch (Exception eee) {
			eee.printStackTrace();
		    	continue;
		    }
		}

		flavorToNative.put(df.getMimeType(), natives);
		nativeToFlavor.put(natives, df);
	    }
	}
    }

    /**
     * map flavors to native data types names
     */

    public synchronized Map getNativesForFlavors(DataFlavor[] flavors) {
	int i;

	if (flavors == null) return (Map)flavorToNative.clone();

	if (flavors.length == 0) return null;

	Map map = new HashMap(flavors.length);

	for (i = 0; i < flavors.length; i++) {
	    DataFlavor df  = flavors[i];
	    String     mts = df.getMimeType();
	    String     n   = (String)flavorToNative.get(mts);

	    if (n == null) {
		String emts = encodeJavaMIMEType(mts);
	
		map.put(df, emts);

		nativeToFlavor.put(emts, df);
		flavorToNative.put(mts, emts);
	    } else {
		map.put(df, n);
	    }
	}

	return map;
    }

    /**
     * map natives to corresponding flavors
     */

    public synchronized Map getFlavorsForNatives(String[] natives) {
	int nonnull = 0;
	int i;

	if (natives == null) return (Map)nativeToFlavor.clone();

	if (natives.length == 0) return new HashMap();

	Map map = new HashMap(natives.length);

	for (i = 0; i < natives.length ; i++) {
	    String     n  = natives[i];
	    DataFlavor df = null;

	    if (isJavaMIMEType(natives[i])) {
		String emts = natives[i];

		if ((df = (DataFlavor)nativeToFlavor.get(emts)) == null) {
		    String mts = decodeJavaMIMEType(natives[i]);

		    try {
			df = new DataFlavor(mts);
		    } catch (Exception e) {
			System.err.println("Exception \"" + e.getClass().getName() + ": " + e.getMessage()  + "\"while constructing DataFlavor for: " + mts);
			df = null;
		    }

		    if (df != null) {
			nativeToFlavor.put(emts, df);
			flavorToNative.put(df.getMimeType(), emts);
		    }
		}
	    } else df = (DataFlavor)nativeToFlavor.get(n);

	    if (df != null) map.put(n, df);
	}

	return map;
    }

    /**
     * @return encode a Java MIMEType for use as a native type name
     */

    public static String encodeJavaMIMEType(String mimeType) {
	return JavaMIME + mimeType;
    }

    /**
     * @return encode a Java MIMEType for use as a native type name
     */

    public static String encodeDataFlavor(DataFlavor df) {
	return JavaMIME + df.getMimeType();
    }

    /**
     * @return if the native type string is an encoded  Java MIMEType
     */

    public static  boolean isJavaMIMEType(String atom) {
	return atom != null && atom.startsWith(JavaMIME, 0);
    }

    /**
     * @return the decoded Java MIMEType string
     */

    public static String decodeJavaMIMEType(String atom) {
	if (!isJavaMIMEType(atom)) return null;

	return atom.substring(JavaMIME.length(), atom.length()).trim();
    }

    /**
     * @return the decoded Java MIMEType as a DataFlavor
     */

    public static DataFlavor decodeDataFlavor(String atom) throws ClassNotFoundException {
	if (!isJavaMIMEType(atom)) return null;

	return new DataFlavor(atom.substring(JavaMIME.length(), atom.length()).trim());
    }

    /*
     * fields
     */

    private Properties nativeToMIME;

    private HashMap  flavorToNative;

    private HashMap  nativeToFlavor;
}
