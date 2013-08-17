/*
 * @(#)ResourceLoader.java	1.3 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.text.html;

import java.io.InputStream;

/**
 * Simple class to load resources using the 1.2
 * security model.  Since the html support is loaded
 * lazily, it's resources are potentially fetched with
 * applet code in the call stack.  By providing this 
 * functionality in a class that is only built on 1.2,
 * reflection can be used from the code that is also
 * built on 1.1 to call this functionality (and avoid
 * the evils of preprocessing).  This functionality
 * is called from HTMLEditorKit.getResourceAsStream.
 *
 * @author  Timothy Prinzing
 * @version 1.3 04/22/99
 */
class ResourceLoader implements java.security.PrivilegedAction {

    ResourceLoader(String name) {
	this.name = name;
    }

    public Object run() {
	Object o = HTMLEditorKit.class.getResourceAsStream(name);
	return o;
    }

    public static InputStream getResourceAsStream(String name) {
	java.security.PrivilegedAction a = new ResourceLoader(name);
        return (InputStream) java.security.AccessController.doPrivileged(a);
    }

    private String name;
}
