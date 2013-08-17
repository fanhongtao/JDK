/*
 * @(#)Provider.java	1.21 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security;

import java.io.*;
import java.util.*;

/**
 * This class represents a "provider" for the
 * Java Security API.  A provider implements some or all parts of
 * Java Security, including:<ul>
 *
 * <li>Algorithms (such as DSA, RSA, MD5 or SHA-1).
 *
 * <li>Key generation and management facilities (such as for
 * algorithm-specific keys).
 *
 * </ul>
 *
 * <p>Each provider has a name and a version number, and is configured
 * in each runtime it is installed in. 
 * 
 * <p>There is a default provider that comes standard with the JDK. It is
 * called the SUN Provider.
 * 
 * See <a href =
 * "../guide/security/CryptoSpec.html#Provider">The Provider Class</a> 
 * in the "Java Cryptography Architecture API Specification &amp; Reference"
 * for information about how providers work and how to install them.
 * 
 * @version 	1.19, 01/30/97
 * @author Benjamin Renaud */

public abstract class Provider extends Properties {

    private String name;
    private String info;
    private double version;

    /**
     * Constructs a provider with the specified name, version number,
     * and information.
     *
     * @param name the provider name.
     *
     * @param version the provider version number.
     * 
     * @param info a description of the provider and its services.
     */
    protected Provider(String name, double version, String info) {
	this.name = name;
	this.version = version;
	this.info = info;
    }

    /**
     * Constructs a provider with the specified name. Assigns it
     * version 1.0.
     *
     * @param name the provider name.  
     */
    Provider(String name) {
	this(name, 1.0, "no information available");
    }

    /**
     * Returns the name of this provider.     
     * 
     * @return the name of this provider.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the version number for this provider.     
     * 
     * @return the version number for this provider.
     */
    public double getVersion() {
	return version;
    }

    /**
     * Returns a human-readable description of the provider and its
     * services.  This may return an HTML page, with relevant links.
     *
     * @return a description of the provider and its services.  
     */
    public String getInfo() {
	return info;
    }


    static Provider loadProvider(String name) {
	
	try {
	    Class cl = Class.forName(name);
	    Object instance = cl.newInstance();

	    if (instance instanceof Provider) {
		return (Provider)instance;
	    }

	} catch (Exception e) {
	    debug("error loading provider " + name, e);
	}
	return null;
    }


    /**
     * Returns a string with the name and the version number
     * of this provider.     
     * 
     * @return the string with the name and the version number
     * for this provider.
     */
    public String toString() {
	return name + " version " + version;
    }

    private static void debug(String msg) {
	Security.debug(msg);
    }

    private static void debug(String msg, Throwable t) {
	Security.debug(msg, t);
    }
}

