/*
 * @(#)DriverManager.java	1.21 98/09/22
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * <P>The basic service for managing a set of JDBC drivers.
 *
 * <P>As part of its initialization, the DriverManager class will
 * attempt to load the driver classes referenced in the "jdbc.drivers"
 * system property. This allows a user to customize the JDBC Drivers
 * used by their applications. For example in your
 * ~/.hotjava/properties file you might specify:
 * <pre>
 * <CODE>jdbc.drivers=foo.bah.Driver:wombat.sql.Driver:bad.taste.ourDriver</CODE>
 * </pre>
 *
 * A program can also explicitly load JDBC drivers at any time. For
 * example, the my.sql.Driver is loaded with the following statement:
 * <pre>
 * <CODE>Class.forName("my.sql.Driver");</CODE>
 * </pre>
 *
 * <P>When the method <code>getConnection</code> is called,
 * the DriverManager will attempt to
 * locate a suitable driver from amongst those loaded at
 * initialization and those loaded explicitly using the same classloader
 * as the current applet or application.
 *
 * @see Driver
 * @see Connection 
 */
public class DriverManager {

    //--------------------------JDBC 2.0-----------------------------

    /**
     * JDBC 2.0
     *
     * Gets the log writer.  
     *
     * The <code>getLogWriter</code> and <code>setLogWriter</code> 
	 * methods should be used instead
     * of the old <code>get/setlogStream</code> methods.
     */
    public static java.io.PrintWriter getLogWriter() {
      return logWriter;
    }

    /**
     * JDBC 2.0
     *
     * Sets the logging/tracing Writer that is used by the DriverManager
     * and all drivers.
     *
     * There is a minor versioning problem introduced by the introduction
     * of the method </code>setLogWriter</code>.  The 
	 * method <code>setLogWriter</code> cannot create a PrintStream
     * that will be returned by <code>getLogStream</code>---the Java platform does
     * not provide a backward conversion.  So, a new application
     * that uses <code>setLogWriter</code> and also uses a JDBC 1.0 driver that uses
     * <code>getLogStream</code> will likely not see debugging information written 
     * by that driver.
     *
     * @param out the new logging/tracing PrintStream; to disable, set to null
     */
    public static synchronized void setLogWriter(java.io.PrintWriter out) {
      logStream = null;
      logWriter = out;
    }


    //---------------------------------------------------------------

    /**
     * Attempts to establish a connection to the given database URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @param url a database url of the form 
     * <code> jdbc:<em>subprotocol</em>:<em>subname</em></code>
     * @param info a list of arbitrary string tag/value pairs as
     * connection arguments; normally at least a "user" and
     * "password" property should be included
     * @return a Connection to the URL 
     * @exception SQLException if a database access error occurs
     */
    public static synchronized Connection getConnection(String url, 
            java.util.Properties info) throws SQLException {
  
        // Gets the classloader of the code that called this method, may 
	// be null.
	ClassLoader callerCL = DriverManager.getCallerClassLoader();

        return (getConnection(url, info, callerCL));
    }

    /**
     * Attempts to establish a connection to the given database URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @param url a database url of the form 
     * <code>jdbc:<em>subprotocol</em>:<em>subname</em></code>
     * @param user the database user on whose behalf the Connection is being
     *   made
     * @param password the user's password
     * @return a Connection to the URL 
     * @exception SQLException if a database access error occurs
     */
    public static synchronized Connection getConnection(String url, 
		            String user, String password) throws SQLException {
        java.util.Properties info = new java.util.Properties();

        // Gets the classloader of the code that called this method, may 
	// be null.
	ClassLoader callerCL = DriverManager.getCallerClassLoader();

	if (user != null) {
	    info.put("user", user);
	}
	if (password != null) {
	    info.put("password", password);
	}

        return (getConnection(url, info, callerCL));
    }

    /**
     * Attempts to establish a connection to the given database URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @param url a database url of the form 
     *  <code> jdbc:<em>subprotocol</em>:<em>subname</em></code>
     * @return a Connection to the URL 
     * @exception SQLException if a database access error occurs
     */
    public static synchronized Connection getConnection(String url) 
                                    throws SQLException {

        java.util.Properties info = new java.util.Properties();

        // Gets the classloader of the code that called this method, may 
	// be null.
	ClassLoader callerCL = DriverManager.getCallerClassLoader();

        return (getConnection(url, info, callerCL));
    }

    /**
     * Attempts to locate a driver that understands the given URL.
     * The DriverManager attempts to select an appropriate driver from
     * the set of registered JDBC drivers. 
     *
     * @param url a database url of the form 
     *  jdbc:<em>subprotocol</em>:<em>subname</em>
     * @return a Driver that can connect to the URL 
     * @exception SQLException if a database access error occurs
     */
    public static synchronized Driver getDriver(String url) 
      throws SQLException {
        println("DriverManager.getDriver(\"" + url + "\")");

        if (!initialized) {
            initialize();
        }

        // Gets the classloader of the code that called this method, may 
	// be null.
	ClassLoader callerCL = DriverManager.getCallerClassLoader();

        // Walk through the loaded drivers attempting to locate someone
	// who understands the given URL.
        for (int i = 0; i < drivers.size(); i++) {
            DriverInfo di = (DriverInfo)drivers.elementAt(i);
	    // If the caller does not have permission to load the driver then 
	    // skip it.
            if ( getCallerClass(callerCL, di.driverClassName ) != 
		 di.driverClass ) {
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
   * Registers the given driver with the DriverManager.
   * A newly-loaded driver class should call
   * the method <code>registerDriver</code> to make itself
   * known to the DriverManager.
   *
   * @param driver the new JDBC Driver that is to be registered with the
   *               DriverManager
   * @exception SQLException if a database access error occurs
   */
  public static synchronized void registerDriver(java.sql.Driver driver)
    throws SQLException {
      if (!initialized) {
	initialize();
      }
      
      DriverInfo di = new DriverInfo();
      di.driver = driver;
      di.driverClass = driver.getClass();
      di.driverClassName = di.driverClass.getName();
      drivers.addElement(di);
      println("registerDriver: " + di);
  }

  /**
   * Drops a Driver from the DriverManager's list.  Applets can only
   * deregister Drivers from their own classloaders.
   *
   * @param driver the JDBC Driver to drop 
   * @exception SQLException if a database access error occurs
   */
  public static synchronized void deregisterDriver(Driver driver) 
    throws SQLException {
      // Gets the classloader of the code that called this method, may 
      // be null.
      ClassLoader callerCL = DriverManager.getCallerClassLoader();
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
      
      // If the caller does not have permission to load the driver then 
      // throw a security exception.
      if ( getCallerClass(callerCL, di.driverClassName ) != di.driverClass ) {
	throw new SecurityException();
      }
      
      // Remove the driver.  Other entries in drivers get shuffled down.
      drivers.removeElementAt(i);
      
  }

    /**
     * Retrieves an Enumeration with all of the currently loaded JDBC drivers
     * to which the current caller has access.
     *
     * <P><B>Note:</B> The classname of a driver can be found using
     * <CODE>d.getClass().getName()</CODE>
     *
     * @return the list of JDBC Drivers loaded by the caller's class loader
     */
    public static synchronized java.util.Enumeration getDrivers() {
        java.util.Vector result = new java.util.Vector();

        if (!initialized) {
            initialize();
        }

        // Gets the classloader of the code that called this method, may 
	// be null.
	ClassLoader callerCL = DriverManager.getCallerClassLoader();

        // Walk through the loaded drivers.
        for (int i = 0; i < drivers.size(); i++) {
            DriverInfo di = (DriverInfo)drivers.elementAt(i);
	    // If the caller does not have permission to load the driver then 
	    // skip it.
            if ( getCallerClass(callerCL, di.driverClassName ) != di.driverClass ) {
                println("    skipping: " + di);
                continue;
            }
            result.addElement(di.driver);
        }

        return (result.elements());
    }


    /**
	 * <p>Sets the maximum time in seconds that a driver will wait
     * while attempting to connect to a database.  
     *
     * @param seconds the login time limit in seconds
     */
    public static void setLoginTimeout(int seconds) { 
        loginTimeout = seconds;
    }

    /**
     * Gets the maximum time in seconds that a driver can wait
     * when attempting to log in to a database.
     *
     * @return the driver login time limit in seconds
     */
    public static int getLoginTimeout() {
        return (loginTimeout);
    }

    /**
     * Sets the logging/tracing PrintStream that is used by the DriverManager
     * and all drivers.
     *
     * @param out the new logging/tracing PrintStream; to disable, set to null
     * @deprecated
     */
    public static synchronized void setLogStream(java.io.PrintStream out) {
        logStream = out;
	if ( out != null )
	  logWriter = new java.io.PrintWriter(out);
	else
	  logWriter = null;
    }

    /**
     * Gets the logging/tracing PrintStream that is used by the DriverManager
     * and all drivers.
     *
     * @return the logging/tracing PrintStream; if disabled, is null
     * @deprecated
     */
    public static java.io.PrintStream getLogStream() {
        return logStream;
    }

    /**
     * Prints a message to the current JDBC log stream.
     *
     * @param message a log or tracing message
     */
    public static synchronized void println(String message) {
        if (logWriter != null) {
            logWriter.println(message);

	    // automatic flushing is never enabled, so we must do it ourselves
	    logWriter.flush();
        }
    }

    //------------------------------------------------------------------------

    // Returns the class object that would be created if the code calling the 
    // driver manager had loaded the driver class, or null if the class
    // is inaccessible.
    private static Class getCallerClass(ClassLoader callerClassLoader, 
					String driverClassName) {
      Class callerC = null;

      try {
	callerC = Class.forName(driverClassName, true, callerClassLoader);
      }
      catch (Exception ex) {
	callerC = null;           // being very careful 
      }

      return callerC;
    }

    private static void loadInitialDrivers() {
        String drivers;
        try {
	    drivers = (String) java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("jdbc.drivers"));
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
                Class.forName(driver, true,
			      ClassLoader.getSystemClassLoader());
            } catch (Exception ex) {
                println("DriverManager.Initialize: load failed: " + ex);
            }
        }
    }


  //  Worker method called by the public getConnection() methods.
  private static synchronized Connection getConnection(
	       String url, 
	       java.util.Properties info, 
	       ClassLoader callerCL) throws SQLException 
  {
    if(url == null) {
      throw new SQLException("The url cannot be null", "08001");
    }
    
    println("DriverManager.getConnection(\"" + url + "\")");
    
    if (!initialized) {
      initialize();
    }

    // Walk through the loaded drivers attempting to make a connection.
    // Remember the first exception that gets raised so we can reraise it.
    SQLException reason = null;
    for (int i = 0; i < drivers.size(); i++) {
      DriverInfo di = (DriverInfo)drivers.elementAt(i);
      
      // If the caller does not have permission to load the driver then 
      // skip it.
      if ( getCallerClass(callerCL, di.driverClassName ) != di.driverClass ) {
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
    private static java.io.PrintWriter logWriter = null;
    private static java.io.PrintStream logStream = null;
    private static boolean initialized = false;

    // Returns the caller's class loader, or null if none
    private static native ClassLoader getCallerClassLoader();

}


// DriverInfo is a package-private support class.
class DriverInfo {
  Driver         driver;
  Class          driverClass;
  String         driverClassName;

  public String toString() {
    return ("driver[className=" + driverClassName + "," + driver + "]");
  }
}
