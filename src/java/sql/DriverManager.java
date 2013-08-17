/*
 * @(#)DriverManager.java	1.6 98/07/01
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

package java.sql;

/**
 * <P>The DriverManager provides a basic service for managing a set of
 * JDBC drivers.
 *
 * <P>As part of its initialization, the DriverManager class will
 * attempt to load the driver classes referenced in the "jdbc.drivers"
 * system property. This allows a user to customize the JDBC Drivers
 * used by their applications. For example in your
 * ~/.hotjava/properties file you might specify:
 * <CODE>jdbc.drivers=foo.bah.Driver:wombat.sql.Driver:bad.taste.ourDriver</CODE>
 *
 * A program can also explicitly load JDBC drivers at any time. For
 * example, the my.sql.Driver is loaded with the following statement:
 * <CODE>Class.forName("my.sql.Driver");</CODE>
 *
 * <P>When getConnection is called the DriverManager will attempt to
 * locate a suitable driver from amongst those loaded at
 * initialization and those loaded explicitly using the same classloader
 * as the current applet or application.
 *
 * @see Driver
 * @see Connection 
 */
public class DriverManager {

    /**
     * Attempt to establish a connection to the given database URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @param url a database url of the form jdbc:<em>subprotocol</em>:<em>subname</em>
     * @param info a list of arbitrary string tag/value pairs as
     * connection arguments; normally at least a "user" and
     * "password" property should be included
     * @return a Connection to the URL 
     * @exception SQLException if a database-access error occurs.
     */
    public static synchronized Connection getConnection(String url, 
            java.util.Properties info) throws SQLException {
	if(url == null) {
	    throw new SQLException("The url cannot be null", "08001");
	}

        println("DriverManager.getConnection(\"" + url + "\")");

        if (!initialized) {
            initialize();
        }

        // Figure out the current security context.
        Object currentSecurityContext = getSecurityContext();

        // Walk through the loaded drivers attempting to make a connection.
        // Remember the first exception that gets raised so we can reraise it.
        SQLException reason = null;
        for (int i = 0; i < drivers.size(); i++) {
            DriverInfo di = (DriverInfo)drivers.elementAt(i);
            // if the driver isn't part of the base system and doesn't come
            // from the same security context as the current caller, skip it.
            if (di.securityContext != null && 
                        di.securityContext != currentSecurityContext) {
                println("    skipping: " + di);
                continue;
            }
            try {
                println("    trying " + di);
                Connection result = di.driver.connect(url, info);
                if (result != null) {
                    // Success!
                    println("getConnection returning " + di);
                    return (result);
                }
            } catch (SQLException ex) {
                if (reason == null) {
                    reason = ex;
                }
            }
        }

        // if we got here nobody could connect.
        if (reason != null)    {
            println("getConnection failed: " + reason);
            throw reason;
        }

        println("getConnection: no suitable driver");
        throw new SQLException("No suitable driver", "08001");
    }

    /**
     * Attempt to establish a connection to the given database URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @param url a database url of the form jdbc:<em>subprotocol</em>:<em>subname</em>
     * @param user the database user on whose behalf the Connection is being made
     * @param password the user's password
     * @return a Connection to the URL 
     * @exception SQLException if a database-access error occurs.
     */
    public static synchronized Connection getConnection(String url, 
		            String user, String password) throws SQLException {
        java.util.Properties info = new java.util.Properties();
	if (user != null) {
	    info.put("user", user);
	}
	if (password != null) {
	    info.put("password", password);
	}
        return (getConnection(url, info));
    }

    /**
     * Attempt to establish a connection to the given database URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @param url a database url of the form jdbc:<em>subprotocol</em>:<em>subname</em>
     * @return a Connection to the URL 
     * @exception SQLException if a database-access error occurs.
     */
    public static synchronized Connection getConnection(String url) 
                                    throws SQLException {
        java.util.Properties info = new java.util.Properties();
        return (getConnection(url, info));
    }

    /**
     * Attempt to locate a driver that understands the given URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers. 
     *
     * @param url a database url of the form jdbc:<em>subprotocol</em>:<em>subname</em>
     * @return a Driver that can connect to the URL 
     * @exception SQLException if a database-access error occurs.
     */
    public static Driver getDriver(String url) throws SQLException {
        println("DriverManager.getDriver(\"" + url + "\")");

        if (!initialized) {
            initialize();
        }

        // Figure out the current security context.
        Object currentSecurityContext = getSecurityContext();

        // Walk through the loaded drivers attempting to locate someone
	// who understands the given URL.
        for (int i = 0; i < drivers.size(); i++) {
            DriverInfo di = (DriverInfo)drivers.elementAt(i);
            // If the driver isn't part of the base system and doesn't come
            // from the same security context as the current caller, skip it.
            if (di.securityContext != null && 
                        di.securityContext != currentSecurityContext) {
                println("    skipping: " + di);
                continue;
            }
            try {
                println("    trying " + di);
		if (di.driver.acceptsURL(url)) {
		    // Success!
                    println("getDriver returning " + di);
                    return (di.driver);
                }
            } catch (SQLException ex) {
		// Drop through and try the next driver.
            }
        }

        println("getDriver: no suitable driver");
        throw new SQLException("No suitable driver", "08001");
    }


    /**
     * A newly loaded driver class should call registerDriver to make itself
     * known to the DriverManager.
     *
     * @param driver the new JDBC Driver 
     * @exception SQLException if a database-access error occurs.
     */
    public static synchronized void registerDriver(java.sql.Driver driver)
                        throws SQLException {
        if (!initialized) {
            initialize();
        }
        DriverInfo di = new DriverInfo();
        di.driver = driver;
        di.className = driver.getClass().getName();
        // Note our current securityContext.
        di.securityContext = getSecurityContext();
        drivers.addElement(di);
        println("registerDriver: " + di);
    }


    /**
     * Drop a Driver from the DriverManager's list.  Applets can only
     * deregister Drivers from their own classloader.
     *
     * @param driver the JDBC Driver to drop 
     * @exception SQLException if a database-access error occurs.
     */
    public static void deregisterDriver(Driver driver) throws SQLException {
        // Figure out the current security context.
        Object currentSecurityContext = getSecurityContext();
        println("DriverManager.deregisterDriver: " + driver);

        // Walk through the loaded drivers.
        int i;
        DriverInfo di = null;
        for (i = 0; i < drivers.size(); i++) {
            di = (DriverInfo)drivers.elementAt(i);
            if (di.driver == driver) {
                break;
            }
        }
        // If we can't find the driver just return.
        if (i >= drivers.size()) {
            println("    couldn't find driver to unload");
            return;
        }

            // If an applet is trying to free a driver from somewhere else
        // throw a security exception.
        if (currentSecurityContext != null &&
                di.securityContext != currentSecurityContext) {
            throw new SecurityException();
        }

        // Remove the driver.  Other entries in drivers get shuffled down.
        drivers.removeElementAt(i);
    
    }

    /**
     * Return an Enumeration of all the currently loaded JDBC drivers
     * which the current caller has access to.
     *
     * <P><B>Note:</B> The classname of a driver can be found using
     * <CODE>d.getClass().getName()</CODE>
     *
     * @return the list of JDBC Drivers loaded by the caller's class loader
     */
    public static java.util.Enumeration getDrivers() {
        java.util.Vector result = new java.util.Vector();

        if (!initialized) {
            initialize();
        }

        // Figure out the current security context.
        Object currentSecurityContext = getSecurityContext();

        // Walk through the loaded drivers.
        for (int i = 0; i < drivers.size(); i++) {
            DriverInfo di = (DriverInfo)drivers.elementAt(i);
            // if the driver isn't part of the base system and doesn't come
            // from the same security context as the current caller, skip it.
            if (di.securityContext != null && 
                        di.securityContext != currentSecurityContext) {
                println("    skipping: " + di);
                continue;
            }
            result.addElement(di.driver);
        }

        return (result.elements());
    }


    /**
     * Set the maximum time in seconds that all drivers can wait
     * when attempting to log in to a database.
     *
     * @param seconds the driver login time limit
     */
    public static void setLoginTimeout(int seconds) {
        loginTimeout = seconds;
    }

    /**
     * Get the maximum time in seconds that all drivers can wait
     * when attempting to log in to a database.
     *
     * @return the driver login time limit
     */
    public static int getLoginTimeout() {
        return (loginTimeout);
    }


    /**
     * Set the logging/tracing PrintStream that is used by the DriverManager
     * and all drivers.
     *
     * @param out the new logging/tracing PrintStream; to disable, set to null
     */
    public static void setLogStream(java.io.PrintStream out) {
        logStream = out;
    }

    /**
     * Get the logging/tracing PrintStream that is used by the DriverManager
     * and all drivers.
     *
     * @return the logging/tracing PrintStream; if disabled, is null
     */
    public static java.io.PrintStream getLogStream() {
        return (logStream);
    }

    /**
     * Print a message to the current JDBC log stream
     *
     * @param message a log or tracing message
     */
    public static void println(String message) {
        if (logStream != null) {
            logStream.println(message);
        }
    }

    //-------------------------------------------------------------------------

    private static Object getSecurityContext() {
        // Get the securityContext for our caller.  For applets this
        // will be the applet classloader base URL.
        SecurityManager security = System.getSecurityManager();    
        if (security == null) {
            return (null);
        }
        return (security.getSecurityContext());
    }

    private static void loadInitialDrivers() {
        String drivers;
        try {
            drivers = System.getProperty("jdbc.drivers");
        } catch (Exception ex) {
            drivers = null;
        }
        println("DriverManager.initialize: jdbc.drivers = " + drivers);
        if (drivers == null) {
            return;
        }
        while (drivers.length() != 0) {
            int x = drivers.indexOf(':');
            String driver;
            if (x < 0) {
                driver = drivers;
                drivers = "";
            } else {
                driver = drivers.substring(0, x);
                drivers = drivers.substring(x+1);
            }
            if (driver.length() == 0) {
                continue;
            }
            try {
                println("DriverManager.Initialize: loading " + driver);
                Class.forName(driver);
            } catch (Exception ex) {
                println("DriverManager.Initialize: load failed: " + ex);
            }
        }
    }

    // Class initialization.
    static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        loadInitialDrivers();
        println("JDBC DriverManager initialized");
    }

    // Prevent the DriverManager class from being instantiated.
    private DriverManager(){}

    private static java.util.Vector drivers = new java.util.Vector();
    private static int loginTimeout = 0;
    private static java.io.PrintStream logStream = null;
    private static boolean initialized = false;

}


// DriverInfo is a package-private support class.
class DriverInfo {
    Driver         driver;
    Object        securityContext;
    String        className;

    public String toString() {
        return ("driver[className=" + className + ",context=" +
        securityContext + "," + driver + "]");
    }
}
