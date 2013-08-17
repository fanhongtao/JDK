/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

import java.util.Hashtable;
import java.util.ArrayList;
import java.net.URL;

import sun.security.util.Debug;

/** 
 * This class extends ClassLoader with additional support for defining
 * classes with an associated code source and permissions which are
 * retrieved by the system policy by default.
 *
 * @version 1.74, 02/06/02
 * @author  Li Gong 
 * @author  Roland Schemers
 */
public class SecureClassLoader extends ClassLoader {
    /*
     * If initialization succeed this is set to true and security checks will
     * succeed. Otherwise the object is not initialized and the object is
     * useless.
     */
    private boolean initialized = false;

    // Hashtable that maps CodeSource to ProtectionDomain
    private Hashtable pdcache = new Hashtable(11);

    private static final Debug debug = Debug.getInstance("scl");

    /**
     * Creates a new SecureClassLoader using the specified parent
     * class loader for delegation.
     *
     * <p>If there is a security manager, this method first
     * calls the security manager's <code>checkCreateClassLoader</code> 
     * method  to ensure creation of a class loader is allowed.
     * <p>
     * @param parent the parent ClassLoader
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkCreateClassLoader</code> method doesn't allow 
     *             creation of a class loader.
     * @see SecurityManager#checkCreateClassLoader
     */
    protected SecureClassLoader(ClassLoader parent) {
	super(parent);
	// this is to make the stack depth consistent with 1.1
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkCreateClassLoader();
	}
	initialized = true;
    }

    /**
     * Creates a new SecureClassLoader using the default parent class
     * loader for delegation.
     *
     * <p>If there is a security manager, this method first
     * calls the security manager's <code>checkCreateClassLoader</code> 
     * method  to ensure creation of a class loader is allowed.
     *
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkCreateClassLoader</code> method doesn't allow 
     *             creation of a class loader.
     * @see SecurityManager#checkCreateClassLoader
     */
    protected SecureClassLoader() {
	super();
	// this is to make the stack depth consistent with 1.1
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkCreateClassLoader();
	}
	initialized = true;
    }

    /**
     * Converts an array of bytes into an instance of class Class,
     * with an optional CodeSource. Before the
     * class can be used it must be resolved.
     * <p>
     * If a non-null CodeSource is supplied and a Policy provider is installed,
     * Policy.getPermissions() is invoked in order to associate a
     * ProtectionDomain with the class being defined.
     * <p>
     * @param name the name of the class
     * @param b the class bytes
     * @param off the start offset of the class bytes
     * @param len the length of the class bytes
     * @param cs the associated CodeSource, or null if none
     * @return the <code>Class</code> object created from the data,
     *         and optional CodeSource.
     */
    protected final Class defineClass(String name, byte[] b, int off, int len,
				      CodeSource cs)
    {
	if (cs == null)
	    return defineClass(name, b, off, len);
	else 
	    return defineClass(name, b, off, len, getProtectionDomain(cs));
    }

    /**
     * Returns the permissions for the given CodeSource object.
     * The default implementation of this method invokes the
     * java.security.Policy.getPermissions method to get the permissions
     * granted by the policy to the specified CodeSource.
     * <p>
     * This method is invoked by the defineClass method which takes
     * a CodeSource as an argument when it is constructing the
     * ProtectionDomain for the class being defined.
     * <p>
     * The constructed ProtectionDomain is cached by the SecureClassLoader.
     * The contents of the cache persist for the lifetime of the
     * SecureClassLoader instance. This persistence inhibits Policy.refresh()
     * from influencing the protection domains already in the cache for a 
     * given CodeSource.
     * <p>
     * @param codesource the codesource.
     *
     * @return the permissions granted to the codesource.
     *
     */
    protected PermissionCollection getPermissions(CodeSource codesource)
    {
	check();
	Policy p = Policy.getPolicyNoCheck();

	PermissionCollection perms;
	if (p == null) {
	    return null;
	} else {
	    perms = p.getPermissions(codesource);
	}
	return perms;
    }

    /*
     * Returned cached ProtectionDomain for the specified CodeSource.
     */
    private ProtectionDomain getProtectionDomain(CodeSource cs) {
	if (cs == null)
	    return null;

	ProtectionDomain pd = (ProtectionDomain)pdcache.get(cs);
	if (pd == null) {
	    synchronized (pdcache) {
		pd = (ProtectionDomain)pdcache.get(cs);
		if (pd == null) {

		    PermissionCollection perms = getPermissions(cs);
		    if (debug != null) {
			debug.println(" getPermissions "+ cs);
			debug.println("  "+perms);
			debug.println("");
		    }
		    pd = new ProtectionDomain(cs, perms);

		    if (pd != null) {
			pdcache.put(cs, pd);
		    }
		}
	    }
	}
	return pd;
    }

    /*
     * Check to make sure the class loader has been initialized.
     */
    private void check() { 
	if (!initialized) {
	    throw new SecurityException("ClassLoader object not initialized");
	}
    }

}
