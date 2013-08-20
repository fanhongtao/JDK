/*
 * @(#)MBeanServerInvocationHandler.java	1.17 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>{@link InvocationHandler} that forwards methods in an MBean's
 * management interface through the MBean server to the MBean.</p>
 *
 * <p>Given an {@link MBeanServerConnection}, the {@link ObjectName}
 * of an MBean within that MBean server, and a Java interface
 * <code>Intf</code> that describes the management interface of the
 * MBean using the patterns for a Standard MBean, this class can be
 * used to construct a proxy for the MBean.  The proxy implements
 * the interface <code>Intf</code> such that all of its methods are
 * forwarded through the MBean server to the MBean.</p>
 *
 * <p>If you have an MBean server <code>mbs</code> containing an MBean
 * with {@link ObjectName} <code>name</code>, and if the MBean's
 * management interface is described by the Java interface
 * <code>Intf</code>, you can construct a proxy for the MBean like
 * this:</p>
 *
 * <pre>
 * Intf proxy = (Intf)
 *     MBeanServerInvocationHandler.{@link #newProxyInstance newProxyInstance}(mbs,
 *                                                   name,
 *                                                   Intf.class,
 *                                                   false);
 * </pre>
 *
 * <p>Suppose, for example, <code>Intf</code> looks like this:</p>
 *
 * <pre>
 * public interface Intf {
 *     public String getSomeAttribute();
 *     public void setSomeAttribute(String value);
 *     public void someOperation(String param1, int param2);
 * }
 * </pre>
 *
 * <p>Then you can execute:</p>
 *
 * <ul>
 *
 * <li><code>proxy.getSomeAttribute()</code> which will result in a
 * call to <code>mbs.</code>{@link MBeanServerConnection#getAttribute
 * getAttribute}<code>(name, "SomeAttribute")</code>.
 *
 * <li><code>proxy.setSomeAttribute("whatever")</code> which will result
 * in a call to <code>mbs.</code>{@link MBeanServerConnection#setAttribute
 * setAttribute}<code>(name, new Attribute("SomeAttribute", "whatever"))</code>.
 *
 * <li><code>proxy.someOperation("param1", 2)</code> which will be
 * translated into a call to <code>mbs.</code>{@link
 * MBeanServerConnection#invoke invoke}<code>(name, "someOperation", &lt;etc&gt;)</code>.
 *
 * </ul>
 *
 * <p>If the last parameter to {@link #newProxyInstance
 * newProxyInstance} is <code>true</code>, then the MBean is assumed
 * to be a {@link NotificationBroadcaster} or {@link
 * NotificationEmitter} and the returned proxy will implement {@link
 * NotificationEmitter}.  A call to {@link
 * NotificationBroadcaster#addNotificationListener} on the proxy will
 * result in a call to {@link
 * MBeanServerConnection#addNotificationListener(ObjectName,
 * NotificationListener, NotificationFilter, Object)}, and likewise
 * for the other methods of {@link NotificationBroadcaster} and {@link
 * NotificationEmitter}.</p>
 *
 * <p>The methods {@link Object#toString()}, {@link Object#hashCode()},
 * and {@link Object#equals(Object)}, when invoked on a proxy using
 * this invocation handler, are forwarded to the MBean server as
 * methods on the proxied MBean.  This will only work if the MBean
 * declares those methods in its management interface.</p>
 *
 * @since 1.5
 * @since.unbundled JMX 1.2
 */
public class MBeanServerInvocationHandler implements InvocationHandler {
    /**
     * <p>Invocation handler that forwards methods through an MBean
     * server.  This constructor may be called instead of relying on
     * {@link #newProxyInstance}, for instance if you need to supply a
     * different {@link ClassLoader} to {@link
     * Proxy#newProxyInstance Proxy.newProxyInstance}.</p>
     *
     * @param connection the MBean server connection through which all
     * methods of a proxy using this handler will be forwarded.
     *
     * @param objectName the name of the MBean within the MBean server
     * to which methods will be forwarded.
     */
    public MBeanServerInvocationHandler(MBeanServerConnection connection,
					ObjectName objectName) {
	this.connection = connection;
	this.objectName = objectName;
	/* Could check here whether the MBean exists.  */
    }

    /**
     * <p>Return a proxy that implements the given interface by
     * forwarding its methods through the given MBean server to the
     * named MBean.</p>
     *
     * <p>This method is equivalent to {@link Proxy#newProxyInstance
     * Proxy.newProxyInstance}<code>(interfaceClass.getClassLoader(),
     * interfaces, handler)</code>.  Here <code>handler</code> is the
     * result of {@link #MBeanServerInvocationHandler new
     * MBeanServerInvocationHandler(connection, objectName)}, and
     * <code>interfaces</code> is an array that has one element if
     * <code>notificationBroadcaster</code> is false and two if it is
     * true.  The first element of <code>interfaces</code> is
     * <code>interfaceClass</code> and the second, if present, is
     * <code>NotificationEmitter.class</code>.
     *
     * @param connection the MBean server to forward to.
     * @param objectName the name of the MBean within
     * <code>connection</code> to forward to.
     * @param interfaceClass the management interface that the MBean
     * exports, which will also be implemented by the returned proxy.
     * @param notificationBroadcaster make the returned proxy
     * implement {@link NotificationEmitter} by forwarding its methods
     * via <code>connection</code>.
     *
     * @return the new proxy instance.
     */
    public static Object newProxyInstance(MBeanServerConnection connection,
					  ObjectName objectName,
					  Class interfaceClass,
					  boolean notificationBroadcaster) {
	final InvocationHandler handler =
	    new MBeanServerInvocationHandler(connection, objectName);
	final Class[] interfaces;
	if (notificationBroadcaster) {
	    interfaces =
		new Class[] {interfaceClass, NotificationEmitter.class};
	} else
	    interfaces = new Class[] {interfaceClass};
	return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				      interfaces,
				      handler);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	final Class methodClass = method.getDeclaringClass();

	if (methodClass.equals(NotificationBroadcaster.class)
	    || methodClass.equals(NotificationEmitter.class))
	    return invokeBroadcasterMethod(proxy, method, args);

	final String methodName = method.getName();
	final Class[] paramTypes = method.getParameterTypes();
	final Class returnType = method.getReturnType();

	/* Inexplicably, InvocationHandler specifies that args is null
	   when the method takes no arguments rather than a
	   zero-length array.  */
	final int nargs = (args == null) ? 0 : args.length;

	if (methodName.startsWith("get")
	    && methodName.length() > 3
	    && nargs == 0
	    && !returnType.equals(Void.TYPE)) {
	    return connection.getAttribute(objectName,
					   methodName.substring(3));
	}

	if (methodName.startsWith("is")
	    && methodName.length() > 2
	    && nargs == 0
	    && (returnType.equals(Boolean.TYPE)
		|| returnType.equals(Boolean.class))) {
	    return connection.getAttribute(objectName,
					   methodName.substring(2));
	}

	if (methodName.startsWith("set")
	    && methodName.length() > 3
	    && nargs == 1
	    && returnType.equals(Void.TYPE)) {
	    Attribute attr = new Attribute(methodName.substring(3), args[0]);
	    connection.setAttribute(objectName, attr);
	    return null;
	}

	final String[] signature = new String[paramTypes.length];
	for (int i = 0; i < paramTypes.length; i++)
	    signature[i] = paramTypes[i].getName();
	try {
	    return connection.invoke(objectName, methodName, args, signature);
	} catch (MBeanException e) {
	    throw e.getTargetException();
	}
	/* The invoke may fail because it can't get to the MBean, with
	   one of the these exceptions declared by
	   MBeanServerConnection.invoke:
	   - RemoteException: can't talk to MBeanServer;
	   - InstanceNotFoundException: objectName is not registered;
	   - ReflectionException: objectName is registered but does not
	     have the method being invoked.
	   In all of these cases, the exception will be wrapped by the
	   proxy mechanism in an UndeclaredThrowableException unless
	   it happens to be declared in the "throws" clause of the
	   method being invoked on the proxy.
	*/
    }

    private Object invokeBroadcasterMethod(Object proxy, Method method,
					   Object[] args) throws Exception {
	final String methodName = method.getName();
	final int nargs = (args == null) ? 0 : args.length;

	if (methodName.equals("addNotificationListener")) {
	    /* The various throws of IllegalArgumentException here
	       should not happen, since we know what the methods in
	       NotificationBroadcaster and NotificationEmitter
	       are.  */
	    if (nargs != 3) {
		final String msg =
		    "Bad arg count to addNotificationListener: " + nargs;
		throw new IllegalArgumentException(msg);
	    }
	    /* Other inconsistencies will produce ClassCastException
	       below.  */

	    NotificationListener listener = (NotificationListener) args[0];
	    NotificationFilter filter = (NotificationFilter) args[1];
	    Object handback = args[2];
	    connection.addNotificationListener(objectName,
					       listener,
					       filter,
					       handback);
	    return null;

	} else if (methodName.equals("removeNotificationListener")) {

	    /* NullPointerException if method with no args, but that
	       shouldn't happen because removeNL does have args.  */
	    NotificationListener listener = (NotificationListener) args[0];

	    switch (nargs) {
	    case 1:
		connection.removeNotificationListener(objectName, listener);
		return null;

	    case 3:
		NotificationFilter filter = (NotificationFilter) args[1];
		Object handback = args[2];
		connection.removeNotificationListener(objectName,
						      listener,
						      filter,
						      handback);
		return null;

	    default:
		final String msg =
		    "Bad arg count to removeNotificationListener: " + nargs;
		throw new IllegalArgumentException(msg);
	    }

	} else if (methodName.equals("getNotificationInfo")) {

	    if (args != null) {
		throw new IllegalArgumentException("getNotificationInfo has " +
						   "args");
	    }

	    MBeanInfo info = connection.getMBeanInfo(objectName);
	    return info.getNotifications();

	} else {
	    throw new IllegalArgumentException("Bad method name: " +
					       methodName);
	}
    }

    private final MBeanServerConnection connection;
    private final ObjectName objectName;
}
