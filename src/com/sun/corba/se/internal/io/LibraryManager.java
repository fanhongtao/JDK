/*
 * @(#)LibraryManager.java	1.31 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.io;

import java.security.AccessController;
//import sun.security.action.LoadLibraryAction;
import java.security.PrivilegedAction;

public class LibraryManager
{
    private static boolean attempted = false;

    private static int majorVersion = 1;
    private static int minorVersion = 11; /*sun.4296963 ibm.11861*/

    native private static int getMajorVersion();

    native private static int getMinorVersion();

    public static boolean load()
    {
	// First check if the ioser library has already been loaded
	// by other code in this VM using System.load() 
	// or System.loadLibrary().
	try {
	    if ( getMajorVersion() == majorVersion 
		 && getMinorVersion() == minorVersion ) {
		attempted = true;
		return true ;
	    }
	} catch ( java.lang.UnsatisfiedLinkError ule ) { 
	}

	// Now try to load the ioser library
        try{
            String libName = "ioser12";

            try{
                AccessController.doPrivileged(new LoadLibraryAction(libName));
            } catch(java.lang.UnsatisfiedLinkError ule1) {
		if (!attempted){
		    System.out.println( "ERROR! Shared library " + libName + 
					" could not be found.");
		}

		throw ule1;
            }

            if ((!attempted) &&
                ((getMajorVersion() != majorVersion) || 
		 (getMinorVersion() != minorVersion))) {
                System.out.println( "WARNING : The " + libName + 
				    " library is not the correct version.");
                System.out.println("          Expected v" + 
				   majorVersion + "." + minorVersion +
				   " but loaded v" + 
				   getMajorVersion() + "." + getMinorVersion() + "\n");
                System.out.println(
				   "          *** YOU ARE ADVISED TO USE EXPECTED VERSION ***");
            }

            attempted = true;

            return true;
        } catch(Error e){
            attempted = true;
            return false;
        }
    }

    private static native boolean setEnableOverride(Class targetClass, Object instance);
}

// For some reason it doesn't work with the public class
// sun.security.action.LoadLibraryAction
class LoadLibraryAction implements PrivilegedAction {
    private String libname;

    public LoadLibraryAction (String libname) {
        this.libname = libname;
    }
    public Object run() {
        System.loadLibrary(libname);
        return null;
    }
}
