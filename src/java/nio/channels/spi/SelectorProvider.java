/*
 * @(#)SelectorProvider.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.nio.channels.spi;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import sun.misc.Service;
import sun.misc.ServiceConfigurationError;
import sun.security.action.GetPropertyAction;


/**
 * Service-provider class for selectors and selectable channels.
 *
 * <p> A selector provider is a concrete subclass of this class that has a
 * zero-argument constructor and implements the abstract methods specified
 * below.  A given invocation of the Java virtual machine maintains a single
 * system-wide default provider instance, which is returned by the {@link
 * #provider provider} method.  The first invocation of that method will locate
 * the default provider as specified below.
 *
 * <p> The system-wide default provider is used by the static <tt>open</tt>
 * methods of the {@link java.nio.channels.DatagramChannel#open
 * DatagramChannel}, {@link java.nio.channels.Pipe#open Pipe}, {@link
 * java.nio.channels.Selector#open Selector}, {@link
 * java.nio.channels.ServerSocketChannel#open ServerSocketChannel}, and {@link
 * java.nio.channels.SocketChannel#open SocketChannel} classes.  A program may
 * make use of a provider other than the default provider by instantiating that
 * provider and then directly invoking the <tt>open</tt> methods defined in
 * this class.
 *
 * <p> All of the methods in this class are safe for use by multiple concurrent
 * threads.  </p>
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @version 1.20, 03/01/23
 * @since 1.4
 */

public abstract class SelectorProvider {

    private static final Object lock = new Object();
    private static SelectorProvider provider = null;

    /**
     * Initializes a new instance of this class.  </p>
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     *          {@link RuntimePermission}<tt>("selectorProvider")</tt>
     */
    protected SelectorProvider() {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPermission(new RuntimePermission("selectorProvider"));
    }

    private static boolean loadProviderFromProperty() {
	String cn = System.getProperty("java.nio.channels.spi.SelectorProvider");
	if (cn == null)
	    return false;
	try {
	    Class c = Class.forName(cn, true,
				    ClassLoader.getSystemClassLoader());
	    provider = (SelectorProvider)c.newInstance();
	    return true;
	} catch (ClassNotFoundException x) {
	    throw new ServiceConfigurationError(x);
	} catch (IllegalAccessException x) {
	    throw new ServiceConfigurationError(x);
	} catch (InstantiationException x) {
	    throw new ServiceConfigurationError(x);
	} catch (SecurityException x) {
	    throw new ServiceConfigurationError(x);
	}
    }

    private static boolean loadProviderAsService() {
	Iterator i = Service.providers(SelectorProvider.class,
				       ClassLoader.getSystemClassLoader());
	for (;;) {
	    try {
		if (!i.hasNext())
		    return false;
		provider = (SelectorProvider)i.next();
		return true;
	    } catch (ServiceConfigurationError sce) {
		if (sce.getCause() instanceof SecurityException) {
		    // Ignore the security exception, try the next provider
		    continue;
		}
		throw sce;
	    }
	}
    }

    /**
     * Returns the system-wide default selector provider for this invocation of
     * the Java virtual machine.
     *
     * <p> The first invocation of this method locates the default provider
     * object as follows: </p>
     *
     * <ol>
     *
     *   <li><p> If the system property
     *   <tt>java.nio.channels.spi.SelectorProvider</tt> is defined then it is
     *   taken to be the fully-qualified name of a concrete provider class.
     *   The class is loaded and instantiated; if this process fails then an
     *   unspecified error is thrown.  </p></li>
     *
     *   <li><p> If a provider class has been installed in a jar file that is
     *   visible to the system class loader, and that jar file contains a
     *   provider-configuration file named
     *   <tt>java.nio.channels.spi.SelectorProvider</tt> in the resource
     *   directory <tt>META-INF/services</tt>, then the first class name
     *   specified in that file is taken.  The class is loaded and
     *   instantiated; if this process fails then an unspecified error is
     *   thrown.  </p></li>
     *
     *   <li><p> Finally, if no provider has been specified by any of the above
     *   means then the system-default provider class is instantiated and the
     *   result is returned.  </p></li>
     *
     * </ol>
     *
     * <p> Subsequent invocations of this method return the provider that was
     * returned by the first invocation.  </p>
     *
     * @return  The system-wide default selector provider
     */
    public static SelectorProvider provider() {
	synchronized (lock) {
	    if (provider != null)
		return provider;
	    return (SelectorProvider)AccessController
		.doPrivileged(new PrivilegedAction() {
			public Object run() {
			    if (loadProviderFromProperty())
				return provider;
			    if (loadProviderAsService())
				return provider;
			    provider = sun.nio.ch.DefaultSelectorProvider.create();
			    return provider;
			}
		    });
	}
    }

    /**
     * Opens a datagram channel.  </p>
     *
     * @return  The new channel
     */
    public abstract DatagramChannel openDatagramChannel()
	throws IOException;

    /**
     * Opens a pipe.  </p>
     *
     * @return  The new pipe
     */
    public abstract Pipe openPipe()
	throws IOException;

    /**
     * Opens a selector.  </p>
     *
     * @return  The new selector
     */
    public abstract AbstractSelector openSelector()
	throws IOException;

    /**
     * Opens a server-socket channel.  </p>
     *
     * @return  The new channel
     */
    public abstract ServerSocketChannel openServerSocketChannel()
	throws IOException;

    /**
     * Opens a socket channel. </p>
     *
     * @return  The new channel
     */
    public abstract SocketChannel openSocketChannel()
	throws IOException;

}
