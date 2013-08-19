/*
 * @(#)RMISecurityManager.java	1.29 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.29, 01/23/03
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
}
