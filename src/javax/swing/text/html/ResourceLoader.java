/*
 * @(#)ResourceLoader.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.6 01/23/03
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
