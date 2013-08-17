/*
 * @(#)NativeLibLoader.java	1.3 98/09/21
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

package java.awt.event;

class NativeLibLoader {

    /**
     * This is copied from java.awt.Toolkit since we need the library
     * loaded in sun.awt.image also:
     *
     * WARNING: This is a temporary workaround for a problem in the
     * way the AWT loads native libraries. A number of classes in this
     * package (sun.awt.image) have a native method, initIDs(),
     * which initializes
     * the JNI field and method ids used in the native portion of
     * their implementation.
     *
     * Since the use and storage of these ids is done by the
     * implementation libraries, the implementation of these method is
     * provided by the particular AWT implementations
     * (i.e. "Toolkit"s/Peer), such as Motif, Win32 or Tiny. The
     * problem is that this means that the native libraries must be
     * loaded by the java.* classes, which do not necessarily know the
     * names of the libraries to load. A better way of doing this
     * would be to provide a separate library which defines java.awt.*
     * initIDs, and exports the relevant symbols out to the
     * implementation libraries.
     * 
     * For now, we know it's done by the implementation, and we assume
     * that the name of the library is "awt".  -br.
     */
    static void loadLibraries() {
	java.security.AccessController.doPrivileged(
		new sun.security.action.LoadLibraryAction("awt"));
    }
}
