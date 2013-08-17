/*
 * @(#)RMISecurityManager.java	1.13 98/09/02
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

package java.rmi;

import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.FileDescriptor;
import java.rmi.server.RMIClassLoader;

/**
 * This class defines a default security policy for RMI applications (not
 * applets).  For code loaded from a class loader, the security manager
 * disables all functions except class definition and access.  This class
 * may be subclassed to implement a different policy.  To set a
 * RMISecurityManager, add the following to an application's main()
 * method: <br>
 * 
 * System.setSecurityManager(new RMISecurityManager()); If no security
 * manager has been set, RMI will only load classes from local system
 * files as defined by CLASSPATH.<p>
 *
 * @version	1.13, 09/02/98
 * @author Roger Riggs
 */
public class RMISecurityManager extends SecurityManager {
    
    /**
     * Construct and initialize.
     */
    public RMISecurityManager() {
	// XXX: DO NOT REMOVE THIS IN 1.1.X. This call indirectly
	// initializes java.security.Security, which cannot be done
	// while a security manager is installed and a classloader is
	// on the stack.
	java.security.Security.getProviders();
    }

    /**
     * True if called indirectly from a loaded class.
     */
    private boolean inLoadedClass() {
	return inClassLoader();
    }

    /**
     * Returns the security context (e.g., a URL).
     */
    public Object getSecurityContext() {
	return RMIClassLoader.getSecurityContext(currentClassLoader());
    }


    /**
     * Loaded classes are not allowed to create class loaders, or even
     * execute any of ClassLoader's methods.
     */
    public synchronized void checkCreateClassLoader() {
	if (inLoadedClass()) {
	    throw new RMISecurityException("classloader");
	}
    }

    /**
     * Loaded classes are not allowed to manipulate threads.
     */
    public synchronized void checkAccess(Thread t) {
	if (inLoadedClass() && classLoaderDepth() == 3) {
	    throw new RMISecurityException("thread");
	}
    }

    /**
     * Loaded classes are not allowed to manipulate thread groups.
     */
    public synchronized void checkAccess(ThreadGroup g) {
	if (inLoadedClass() && classLoaderDepth() == 4) {
	    throw new RMISecurityException("threadgroup");
	}
    }

    /**
     * Loaded classes are not allowed to exit the VM.
     */
    public synchronized void checkExit(int status) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("exit", String.valueOf(status));
	}
    }

    /**
     * Loaded classes are not allowed to fork processes.
     */
    public synchronized void checkExec(String cmd){
	if (inLoadedClass()) {
	    throw new RMISecurityException("exec", cmd);
	}
    }

    /**
     * Loaded classes are not allowed to link dynamic libraries.
     */
    public synchronized void checkLink(String lib){
	switch (classLoaderDepth()) {
	  case 2: // Runtime.load
	  case 3: // System.loadLibrary
	    throw new RMISecurityException("link", lib);
	  default:
	    break;
	}
    }

    /**
     * Loaded classes are not allowed to access the system properties list.
     */
    public synchronized void checkPropertiesAccess() {
	if (classLoaderDepth() == 2) {
	    throw new RMISecurityException("properties");
	}
    }

    /**
     * Loaded classes can access the system property named by <i>key</i>
     * only if its twin <i>key.rmi</i> property is set to true.
     * For example, the property <code>java.home</code> can be read by
     * loaded classes only if <code>java.home.rmi</code> is <code>true</code>.
     */
    public synchronized void checkPropertyAccess(String key) {
	if (classLoaderDepth() == 2) {
	    if (!"true".equalsIgnoreCase(System.getProperty(key + ".rmi"))) {
		throw new RMISecurityException("properties");
            }
	}
    }

    /**
     * Check if a loaded class can read a particular file.
     */
    public synchronized void checkRead(String file) {
	if (inLoadedClass())
	    throw new RMISecurityException("file.read", file);
    }

    /**
     * No file reads are valid from a loaded class.
     * @exception  RMISecurityException If called from a loaded class.
     */
    public void checkRead(String file, Object context) {
	if (inLoadedClass())
	    throw new RMISecurityException("file.read", file);
    }

    /**
     * Check if a loaded class can write a particular file.
     * @exception  RMISecurityException If called from a loaded class.
     */
    public synchronized void checkWrite(String file) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("file.write", file);
	}
    }

    /**
     * Check if a file with the specified system dependent
     * file name can be deleted.
     * @param file the system dependent file name
     * @exception  RMISecurityException If the file is not found.
     */
    public void checkDelete(String file) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("file.delete", file);
	}
    }

    /**
     * Loaded classes are not allowed to open descriptors for reading unless
     * it is done through a socket, in which case other access
     * restrictions still apply.
     */
    public synchronized void checkRead(FileDescriptor fd) {
	if ((inLoadedClass() && !inClass("java.net.SocketInputStream"))
	    || (!fd.valid()) ) {
	    throw new RMISecurityException("fd.read");
	}
    }

    /**
     * Loaded classes are not allowed to open descriptors for writing unless
     * it is done through a socket, in which case other access
     * restrictions still apply.
     */
    public synchronized void checkWrite(FileDescriptor fd) {
	if ( (inLoadedClass() && !inClass("java.net.SocketOutputStream")) 
	     || (!fd.valid()) ) {
	    throw new RMISecurityException("fd.write");
	}
    }

    /**
     * For now loaded classes can't listen on any port.
     */
    public synchronized void checkListen(int port) {
	if (inLoadedClass() && port != 0 && (port < 1024 || port > 65535)) {
	    throw new RMISecurityException("socket.listen", String.valueOf(port));
	}
    }

    /**
     * For now loaded classes can't accept connections on any port.
     */
    public synchronized void checkAccept(String host, int port) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("socket.accept", host + ":" + String.valueOf(port));
	}
    }

    /**
     * Checks to see if current execution context is allowed to use
     * (join/leave/send/receive) IP multicast (disallowed from loaded classes).
     */
    public void checkMulticast(InetAddress maddr) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("checkmulticast");
	}
    }

    /**
     * Checks to see if current execution context is allowed to use
     * (join/leave/send/receive) IP multicast (disallowed from loaded classes).
     */
    public void checkMulticast(InetAddress maddr, byte ttl) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("checkmulticast");
	}
    }
    
    /**
     * Loaded classes can make connections if called through the RMI transport.
     */
    public synchronized void checkConnect(String host, int port) {
	if (!inLoadedClass()) {
	    return;
	}
	
	// REMIND: This is only appropriate for our transport
	// implementation.
	int depth = classDepth("sun.rmi.transport.tcp.TCPChannel");
	if (depth > 1) {
	    // Called through our RMI transport
	    return;
	} else {
	    Object url = getSecurityContext();
	    if (url != null && url instanceof java.net.URL) {
		checkConnect(((URL)url).getHost(), host);
	    } else {
		throw new RMISecurityException("checkConnect",
					       "To " + host + ":" + port);
	    }
	}
    }
    
    private synchronized void checkConnect(String fromHost, String toHost) {
	try {
	    inCheck = true;
	    InetAddress toHostAddr, fromHostAddr;
	    if (!fromHost.equals(toHost)) {
		try {
		    // the only time we allow non-matching strings
		    // is when IPs and the IPs match.
		    toHostAddr = InetAddress.getByName(toHost);
		    fromHostAddr = InetAddress.getByName(fromHost);
			
		    if (fromHostAddr.equals(toHostAddr)) {
			return;
		    } else {
			throw new RMISecurityException("checkConnect",
						       "To " + toHost);
		    }
		} catch (UnknownHostException e) {
		    throw new RMISecurityException("checkConnect",
						   "To " + toHost);
		}  
	    } else {
		try {
		    toHostAddr = InetAddress.getByName(toHost);
			
		    // strings match: if we have IP, we're homefree, 
		    return;
		} catch (UnknownHostException e) {
		    throw new RMISecurityException("checkConnect",
						   "To " + toHost);
		}
	    }
	} finally {
	    inCheck = false;
	}
    }
    
    /**
     * Loaded classes can make connections if
     * called through the RMI transport.
     */
    public void checkConnect(String host, int port, Object context) {
	checkConnect(host, port);
	if (context != null) {
	    if (context instanceof URL) {
		checkConnect(((URL)context).getHost(), host);
	    } else {
	       throw new RMISecurityException("checkConnect (unknown context)",
					       "To " + host);
	    }
	}
    }

    /**
     * Allow caller to create top-level windows.
     * Allow loaded classes to create windows with warnings.
     */
    public synchronized boolean checkTopLevelWindow(Object window) {
	if (inLoadedClass())
	    return false;
	return true;
    }

    /**
     * Check if a loaded class can access a package.
     */
    public synchronized void checkPackageAccess(String pkg) {

	if (!inLoadedClass())
	    return;
	int i = pkg.indexOf('.');

	while (i > 0) {
	    String subpkg = pkg.substring(0, i);
	    if (Boolean.getBoolean("package.restrict.access." + subpkg)) {
		throw new RMISecurityException("checkpackageaccess", pkg);
	    }
	    i = pkg.indexOf('.', i + 1);
	}
    }

    /**
     * Check if a loaded class can define classes in a package.
     */
    public synchronized void checkPackageDefinition(String pkg) {

	if (!inLoadedClass())
	    return;
	int i = pkg.indexOf('.');

	while (i > 0) {
	    String subpkg = pkg.substring(0, i);
	    if (Boolean.getBoolean("package.restrict.definition." + subpkg)) {
		throw new RMISecurityException("checkpackagedefinition", pkg);
	    }
	    i = pkg.indexOf('.', i + 1);
	}
    }

    /**
     * Check if a loaded class can set a networking-related object factory.
     */
    public synchronized void checkSetFactory() {
	if (inLoadedClass()) {
	    throw new RMISecurityException("cannotsetfactory");
	}
    }

    /**
     * Disallow printing from loaded classes.
     */
    public void checkPrintJobAccess() {
	if (inLoadedClass()) {
	    throw new RMISecurityException("getPrintJob");
	}
    }

    /**
     * Checks to see if an client can get access to the System Clipboard
     * (disallowed from loaded classes).
     */
    public void checkSystemClipboardAccess() {
	if (inLoadedClass()) {
	    throw new RMISecurityException("checksystemclipboardaccess");
	}
	
    }

    /**
     * Checks to see if an client can get access to the AWT event queue
     * (disallowed from loaded classes).
     */
    public void checkAwtEventQueueAccess() {
	if (inLoadedClass()) {
	    throw new RMISecurityException("checkawteventqueueaccess");
	}
    }

    /**
     * Check if client is allowed reflective access to a member or a set
     * of members for the specified class.  Once initial access is granted,
     * the reflected members can be queried for identifying information, but
     * can only be <strong>used</strong> (via get, set, invoke, or
     * newInstance) with standard Java language access control.
     *
     * <p>The policy is to dent <em>untrusted</em> clients access to
     * <em>declared</em> members of classes other than those loaded via
     * the same class loader.  All other accesses are granted.
     */
    public void checkMemberAccess(Class clazz, int which) {
	if (which != java.lang.reflect.Member.PUBLIC) {
	    ClassLoader currentLoader = currentClassLoader();
	    if (currentLoader != null) {
		if (currentLoader != clazz.getClassLoader()) {
		    throw new RMISecurityException("checkmemberaccess");
		}
	    }
	}
    }

    /**
     * Loaded classes cannot perform security provider operations.
     */
    public void checkSecurityAccess(String provider) {
	if (inLoadedClass()) {
	    throw new RMISecurityException("checksecurityaccess", provider);
	}
    }
}
