/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * The AppContext is a per-SecurityContext table which stores application
 * service instances.  (If you are not writing an application service, or
 * don't know what one is, please do not use this class.)  The AppContext
 * allows applet access to what would otherwise be potentially dangerous
 * services, such as the ability to peek at EventQueues or change the
 * look-and-feel of a Swing application.<p>
 *
 * Most application services use a singleton object to provide their
 * services, either as a default (such as getSystemEventQueue or 
 * getDefaultToolkit) or as static methods with class data (System).
 * The AppContext works with the former method by extending the concept
 * of "default" to be SecurityContext-specific.  Application services
 * lookup their singleton in the AppContext; if it hasn't been created,
 * the service creates the singleton and stores it in the AppContext.<p>
 *
 * For example, here we have a Foo service, with its pre-AppContext
 * code:<p>
 * <code><pre>
 *    public class Foo {
 *        private static Foo defaultFoo = new Foo();
 *
 *        public static Foo getDefaultFoo() {
 *            return defaultFoo;
 *        }
 *
 *    ... Foo service methods
 *    }</pre></code><p>
 *
 * The problem with the above is that the Foo service is global in scope,
 * so that applets and other untrusted code can execute methods on the
 * single, shared Foo instance.  The Foo service therefore either needs
 * to block its use by untrusted code using a SecurityManager test, or
 * restrict its capabilities so that it doesn't matter if untrusted code
 * executes it.<p>
 *
 * Here's the Foo class written to use the AppContext:<p>
 * <code><pre>
 *    public class Foo {
 *        public static Foo getDefaultFoo() {
 *            Foo foo = (Foo)AppContext.getAppContext().get(Foo.class);
 *            if (foo == null) {
 *                foo = new Foo();
 *                getAppContext().put(Foo.class, foo);
 *            }
 *            return foo;
 *        }
 *
 *    ... Foo service methods
 *    }</pre></code><p>
 *
 * Since a separate AppContext exists for each SecurityContext, trusted
 * and untrusted code have access to different Foo instances.  This allows
 * untrusted code access to "system-wide" services -- the service remains
 * within the security "sandbox".  For example, say a malicious applet 
 * wants to peek all of the key events on the EventQueue to listen for
 * passwords; if separate EventQueues are used for each SecurityContext
 * using AppContexts, the only key events that applet will be able to
 * listen to are its own.  A more reasonable applet request would be to
 * change the Swing default look-and-feel; with that default stored in
 * an AppContext, the applet's look-and-feel will change without 
 * disrupting other applets or potentially the browser itself.<p>
 *
 * Because the AppContext is a facility for safely extending application
 * service support to applets, none of its methods may be blocked by a
 * a SecurityManager check in a valid Java implementation.  Applets may
 * therefore safely invoke any of its methods without worry of being 
 * blocked.
 *
 * @author  Thomas Ball
 * @version 1.8 02/06/02
 */
final class AppContext {

    /* Since the contents of an AppContext are unique to each Java 
     * session, this class should never be serialized. */

    /* A map of AppContexts, referenced by SecurityContext.
     * If the map is null then only one context, the systemAppContext,
     * has been referenced so far.
     */
    private static Hashtable security2appContexts = null;

    // A handle to be used when the SecurityContext is null.
    private static Object nullSecurityContext = new Object();
    private static AppContext systemAppContext = 
        new AppContext(nullSecurityContext);

    /*
     * The hashtable associated with this AppContext.  A private delegate
     * is used instead of subclassing Hashtable so as to avoid all of
     * Hashtable's potentially risky methods, such as clear(), elements(),
     * putAll(), etc.  (It probably doesn't need to be final since the
     * class is, but I don't trust the compiler to be that smart.)
     */
    private final Hashtable table;

    /* The last key-pair cache -- comparing to this before doing a 
     * lookup in the table can save some time, at the small cost of
     * one additional pointer comparison. 
     */
    private static Object lastKey;
    private static Object lastValue;

    private AppContext(Object securityContext) {
        table = new Hashtable(2);
        if (securityContext != nullSecurityContext) {
            if (security2appContexts == null) {
                security2appContexts = new Hashtable(2, 0.2f);
            }
            security2appContexts.put(securityContext, this);
        }
    }

    /**
     * Returns the appropriate AppContext for the caller, 
     * as determined by its SecurityContext.  
     *
     * @returns the AppContext for the caller.
     * @see     java.lang.SecurityManager#getSecurityContext
     * @since   1.2
     */
    public static AppContext getAppContext() {
        // Get security context, if any.

        Object securityContext = nullSecurityContext;
/*
 Commenting out until we can reliably compute AppContexts

        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            Object context = sm.getSecurityContext();
            if (context != null) {
                securityContext = context;
            }
        }
        */

        // Map security context to AppContext.
        if (securityContext == nullSecurityContext) {
            return systemAppContext;
        }
        AppContext appContext = 
            (AppContext)security2appContexts.get(securityContext);
        if (appContext == null) {
            appContext = new AppContext(securityContext);
            security2appContexts.put(securityContext, appContext);
        }
        return appContext;
    }

    /**
     * Returns the value to which the specified key is mapped in this context.
     *
     * @param   key   a key in the AppContext.
     * @return  the value to which the key is mapped in this AppContext;
     *          <code>null</code> if the key is not mapped to any value.
     * @see     #put(Object, Object)
     * @since   1.2
     */
    public synchronized Object get(Object key) {
        if (key != lastKey || lastValue == null) {
            lastValue = table.get(key);
            lastKey = key;
        }
        return lastValue;
    }

    /**
     * Maps the specified <code>key</code> to the specified 
     * <code>value</code> in this AppContext.  Neither the key nor the 
     * value can be <code>null</code>.
     * <p>
     * The value can be retrieved by calling the <code>get</code> method 
     * with a key that is equal to the original key. 
     *
     * @param      key     the AppContext key.
     * @param      value   the value.
     * @return     the previous value of the specified key in this 
     *             AppContext, or <code>null</code> if it did not have one.
     * @exception  NullPointerException  if the key or value is
     *               <code>null</code>.
     * @see     #get(Object)
     * @since   1.2
     */
    public synchronized Object put(Object key, Object value) {
        return table.put(key, value);
    }

    /**
     * Removes the key (and its corresponding value) from this 
     * AppContext. This method does nothing if the key is not in the
     * AppContext.
     *
     * @param   key   the key that needs to be removed.
     * @return  the value to which the key had been mapped in this AppContext,
     *          or <code>null</code> if the key did not have a mapping.
     * @since   1.2
     */
    public synchronized Object remove(Object key) {
        return table.remove(key);
    }

    /**
     * Returns a string representation of this AppContext.
     * @since   1.2
     */
    public String toString() {
        Object securityContext = nullSecurityContext;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            Object context = 
                System.getSecurityManager().getSecurityContext();
            if (context != null) {
                securityContext = context;
            }
        }
        String contextName = (securityContext.equals(nullSecurityContext) ?
            "null" : securityContext.toString());
	return getClass().getName() + "[SecurityContext=" + contextName + "]";
    }
}
