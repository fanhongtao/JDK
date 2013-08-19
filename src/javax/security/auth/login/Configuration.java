/*
 * @(#)Configuration.java	1.55 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.security.auth.login;

import javax.security.auth.AuthPermission;
 
import java.io.*;
import java.util.*;
import java.net.URL;
import java.security.PrivilegedActionException;
 
/**
 * <p> This is an abstract class for representing the configuration of
 * LoginModules under an application.  The <code>Configuration</code> specifies
 * which LoginModules should be used for a particular application, and in what
 * order the LoginModules should be invoked.
 * This abstract class needs to be subclassed to provide an implementation
 * which reads and loads the actual <code>Configuration</code>.
 *
 * <p> When the <code>LoginContext</code> needs to read the Configuration
 * to determine which LoginModules are configured for a particular
 * application, <i>appName</i>, it makes the following calls:
 * <pre>
 *	config = Configuration.getConfiguration();
 *	entries = config.getAppConfigurationEntry(appName);
 * </pre>
 *
 * <p> A login configuration contains the following information.
 * Note that this example only represents the default syntax for the
 * <code>Configuration</code>.  Subclass implementations of this class
 * may implement alternative syntaxes and may retrieve the
 * <code>Configuration</code> from any source such as files, databases,
 * or servers.
 *
 * <pre>
 *      Application {
 *	      ModuleClass  Flag    ModuleOptions;
 *	      ModuleClass  Flag    ModuleOptions;
 *	      ModuleClass  Flag    ModuleOptions;
 *      };
 *      Application {
 *	      ModuleClass  Flag    ModuleOptions;
 *	      ModuleClass  Flag    ModuleOptions;
 *      };
 *      other {
 *	      ModuleClass  Flag    ModuleOptions;
 *	      ModuleClass  Flag    ModuleOptions;
 *      };
 * </pre>
 *
 * <p> Each entry in the <code>Configuration</code> is indexed via an
 * application name, <i>Application</i>, and contains a list of
 * LoginModules configured for that application.  Each <code>LoginModule</code>
 * is specified via its fully qualified class name.
 * Authentication proceeds down the module list in the exact order specified.
 * If an application does not have specific entry,
 * it defaults to the specific entry for "<i>other</i>".
 *
 * <p> The <i>Flag</i> value controls the overall behavior as authentication
 * proceeds down the stack.  The following represents a description of the
 * valid values for <i>Flag</i> and their respective semantics:
 *
 * <pre>
 *      1) Required     - The <code>LoginModule</code> is required to succeed.
 *			If it succeeds or fails, authentication still continues
 *			to proceed down the <code>LoginModule</code> list.
 *
 *      2) Requisite    - The <code>LoginModule</code> is required to succeed.
 *			If it succeeds, authentication continues down the
 *			<code>LoginModule</code> list.  If it fails,
 *			control immediately returns to the application
 *			(authentication does not proceed down the
 *			<code>LoginModule</code> list).
 *
 *      3) Sufficient   - The <code>LoginModule</code> is not required to
 *			succeed.  If it does succeed, control immediately
 *			returns to the application (authentication does not
 *			proceed down the <code>LoginModule</code> list).
 *			If it fails, authentication continues down the
 *			<code>LoginModule</code> list.
 *
 *      4) Optional     - The <code>LoginModule</code> is not required to
 *			succeed.  If it succeeds or fails,
 *			authentication still continues to proceed down the
 *			<code>LoginModule</code> list.
 * </pre>
 *
 * <p> The overall authentication succeeds only if all <i>Required</i> and
 * <i>Requisite</i> LoginModules succeed.  If a <i>Sufficient</i>
 * <code>LoginModule</code> is configured and succeeds,
 * then only the <i>Required</i> and <i>Requisite</i> LoginModules prior to 
 * that <i>Sufficient</i> <code>LoginModule</code> need to have succeeded for
 * the overall authentication to succeed. If no <i>Required</i> or
 * <i>Requisite</i> LoginModules are configured for an application,
 * then at least one <i>Sufficient</i> or <i>Optional</i>
 * <code>LoginModule</code> must succeed.
 *
 * <p> <i>ModuleOptions</i> is a space separated list of
 * <code>LoginModule</code>-specific values which are passed directly to
 * the underlying LoginModules.  Options are defined by the
 * <code>LoginModule</code> itself, and control the behavior within it.
 * For example, a <code>LoginModule</code> may define options to support
 * debugging/testing capabilities.  The correct way to specify options in the
 * <code>Configuration</code> is by using the following key-value pairing:
 * <i>debug="true"</i>.  The key and value should be separated by an
 * 'equals' symbol, and the value should be surrounded by double quotes.
 * If a String in the form, ${system.property}, occurs in the value,
 * it will be expanded to the value of the system property.
 * Note that there is no limit to the number of
 * options a <code>LoginModule</code> may define.
 *
 * <p> The following represents an example <code>Configuration</code> entry
 * based on the syntax above:
 *
 * <pre>
 * Login {
 *   com.sun.security.auth.module.UnixLoginModule required;
 *   com.sun.security.auth.module.Krb5LoginModule optional
 *                   useTicketCache="true"
 *                   ticketCache="${user.home}${/}tickets";
 * };
 * </pre>
 *
 * <p> This <code>Configuration</code> specifies that an application named,
 * "Login", requires users to first authenticate to the
 * <i>com.sun.security.auth.module.UnixLoginModule</i>, which is
 * required to succeed.  Even if the <i>UnixLoginModule</i>
 * authentication fails, the
 * <i>com.sun.security.auth.module.Krb5LoginModule</i>
 * still gets invoked.  This helps hide the source of failure.
 * Since the <i>Krb5LoginModule</i> is <i>Optional</i>, the overall
 * authentication succeeds only if the <i>UnixLoginModule</i>
 * (<i>Required</i>) succeeds.
 *
 * <p> Also note that the LoginModule-specific options,
 * <i>useTicketCache="true"</i> and
 * <i>ticketCache=${user.home}${/}tickets"</i>,
 * are passed to the <i>Krb5LoginModule</i>.
 * These options instruct the <i>Krb5LoginModule</i> to
 * use the ticket cache at the specified location.
 * The system properties, <i>user.home</i> and <i>/</i>
 * (file.separator), are expanded to their respective values.
 *
 * <p> The default Configuration implementation can be changed by setting the
 * value of the "login.configuration.provider" security property (in the Java
 * security properties file) to the fully qualified name of
 * the desired Configuration implementation class.
 * The Java security properties file is located in the file named
 * &lt;JAVA_HOME&gt;/lib/security/java.security, where &lt;JAVA_HOME&gt;
 * refers to the directory where the JDK was installed.
 *
 * @version 1.55, 01/23/03
 * @see javax.security.auth.login.LoginContext
 */
public abstract class Configuration {

    private static Configuration configuration;
    private static ClassLoader contextClassLoader;

    static {
	contextClassLoader =
		(ClassLoader)java.security.AccessController.doPrivileged
		(new java.security.PrivilegedAction() {
		public Object run() {
		    return Thread.currentThread().getContextClassLoader();	
		}
	});
    };

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected Configuration() { }

    /**
     * Get the current Login Configuration.
     *
     * <p>
     *
     * @return the current Login Configuration.
     *
     * @exception SecurityException if the caller does not have permission
     *				to retrieve the Configuration.
     *
     * @see #setConfiguration
     */
    public static synchronized Configuration getConfiguration() {

	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPermission(new AuthPermission("getLoginConfiguration"));

	if (configuration == null) {
	    String config_class = null;
	    config_class = (String)
		java.security.AccessController.doPrivileged
		(new java.security.PrivilegedAction() {
		public Object run() {
		    return java.security.Security.getProperty
				("login.configuration.provider");
		}
	    });
	    if (config_class == null) {
		config_class = "com.sun.security.auth.login.ConfigFile";
	    }
 
	    try {
		final String finalClass = config_class;
		configuration = (Configuration)
		    java.security.AccessController.doPrivileged
		    (new java.security.PrivilegedExceptionAction() {
		    public Object run() throws ClassNotFoundException,
					InstantiationException,
					IllegalAccessException {
			return Class.forName
				(finalClass,
				true,
				contextClassLoader).newInstance();
		    }
		});
	    } catch (PrivilegedActionException e) {
		Exception ee = e.getException();
		if (ee instanceof InstantiationException) {
		    throw (SecurityException) new
			SecurityException
				("Configuration error:" +
				 ee.getCause().getMessage() + 
				 "\n").initCause(ee.getCause());
		} else {
		    throw (SecurityException) new
			SecurityException
				("Configuration error: " +
				 ee.toString() + 
				 "\n").initCause(ee);
		}
	    }
	}
	return configuration;
    }
    
    /**
     * Set the current Login <code>Configuration</code>.
     *
     * <p>
     *
     * @param configuration the new <code>Configuration</code>
     *
     * @exception SecurityException if the current thread does not have
     *			Permission to set the <code>Configuration</code>.
     *
     * @see #getConfiguration
     */
    public static void setConfiguration(Configuration configuration) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPermission(new AuthPermission("setLoginConfiguration"));
	Configuration.configuration = configuration;
    }

    /**
     * Retrieve an array of AppConfigurationEntries which corresponds to
     *		the configuration of LoginModules for this application.
     *
     * <p>
     *
     * @param applicationName the name used to index the Configuration.
     * 
     * @return an array of AppConfigurationEntries which corresponds to
     *		the configuration of LoginModules for this
     *		application, or null if this application has no configured
     *		LoginModules.
     */
    public abstract AppConfigurationEntry[] getAppConfigurationEntry
    (String applicationName);

    /**
     * Refresh and reload the Configuration.
     *
     * <p> This method causes this object to refresh/reload its current
     * Configuration. This is implementation-dependent.
     * For example, if the Configuration object is stored
     * a file, calling <code>refresh</code> will cause the file to be re-read.
     *
     * <p>
     *
     * @exception SecurityException if the caller does not have permission
     *				to refresh the Configuration.
     */
    public abstract void refresh();
}
