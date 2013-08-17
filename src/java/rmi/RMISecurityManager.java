/*
 * @(#)RMISecurityManager.java	1.23 98/07/15
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

package java.rmi;

import java.security.*;

/**
 * <code>RMISecurityManager</code> provides an example security manager for
 * use by RMI applications that use downloaded code.  RMI's class loader will
 * not download any classes from remote locations if no security manager has
 * been set.  <code>RMISecurityManager</code> does not apply to applets, which
 * run under the protection of their browser's security manager.
 *
 * <p>To use the <code>RMISecurityManager</code> in your application , add
 * the following statement to your code (it needs to be executed before RMI
 * can download code from remote hosts, so it most likely needs to appear
 * in the <code>main</code> of your application):
 *
 * <pre>
 * System.setSecurityManager(new RMISecurityManager());
 * </pre>
 *
 * <p>The <code>RMISecurityManager</code> overrides several
 * of <code>java.lang.SecurityManager</code>'s methods that deal with
 * thread or package access.
 *
 * @version 1.23, 07/15/98
 * @author  Roger Riggs
 * @author  Peter Jones
 * @since JDK1.1
 */
public class RMISecurityManager extends SecurityManager {

    /**
     * Constructs a new <code>RMISecurityManager</code>.
     * @since JDK1.1
     */
    public RMISecurityManager() {
    }

    /**
     * Throws a <code>SecurityException</code> if the 
     * calling thread is not allowed to access the package specified by 
     * the argument. 
     * <p>
     * This method is used by the <code>loadClass</code> method of class 
     * loaders. 
     *
     * @param      pkg   the package name.
     * @exception  SecurityException  if the caller does not have
     *             permission to access the specified package.
     * @see        java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    public void checkPackageAccess(final String pkgname) {
	final boolean[] check = { false };
	AccessController.doPrivileged(new PrivilegedAction(){
	    public Object run() {
		int i;
		String pkg = pkgname;
		do {
		    String prop = "package.restrict.access." + pkg;
		    if (Boolean.getBoolean(prop)) {
			check[0] = true;
			break;
		    }
		    if ((i = pkg.lastIndexOf('.')) != -1) {
			pkg = pkg.substring(0, i);
		    }
		} while (i != -1);
		return null;
	    }
	});

    	if (check[0])
	    super.checkPackageAccess(pkgname);
    }
}
