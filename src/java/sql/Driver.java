/*
 * @(#)Driver.java	1.6 98/07/01
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
 * <P>The Java SQL framework allows for multiple database drivers.
 *
 * <P>Each driver should supply a class that implements
 * the Driver interface.
 *
 * <P>The DriverManager will try to load as many drivers as it can
 * find and then for any given connection request, it will ask each
 * driver in turn to try to connect to the target URL.
 *
 * <P>It is strongly recommended that each Driver class should be
 * small and standalone so that the Driver class can be loaded and
 * queried without bringing in vast quantities of supporting code.
 *
 * <P>When a Driver class is loaded, it should create an instance of
 * itself and register it with the DriverManager. This means that a
 * user can load and register a driver by doing
 * Class.forName("foo.bah.Driver").
 *
 * @see DriverManager
 * @see Connection 
 */
public interface Driver {

    /**
     * Try to make a database connection to the given URL.
     * The driver should return "null" if it realizes it is the wrong kind
     * of driver to connect to the given URL.  This will be common, as when
     * the JDBC driver manager is asked to connect to a given URL it passes
     * the URL to each loaded driver in turn.
     *
     * <P>The driver should raise a SQLException if it is the right 
     * driver to connect to the given URL, but has trouble connecting to
     * the database.
     *
     * <P>The java.util.Properties argument can be used to passed arbitrary
     * string tag/value pairs as connection arguments.
     * Normally at least "user" and "password" properties should be
     * included in the Properties.
     *
     * @param url The URL of the database to connect to
     * @param info a list of arbitrary string tag/value pairs as
     * connection arguments; normally at least a "user" and
     * "password" property should be included
     * @return a Connection to the URL
     * @exception SQLException if a database-access error occurs.
     */
    Connection connect(String url, java.util.Properties info)
        throws SQLException;

    /**
     * Returns true if the driver thinks that it can open a connection
     * to the given URL.  Typically drivers will return true if they
     * understand the subprotocol specified in the URL and false if
     * they don't.
     *
     * @param url The URL of the database.
     * @return True if this driver can connect to the given URL.  
     * @exception SQLException if a database-access error occurs.
     */
    boolean acceptsURL(String url) throws SQLException;


    /**
     * <p>The getPropertyInfo method is intended to allow a generic GUI tool to 
     * discover what properties it should prompt a human for in order to get 
     * enough information to connect to a database.  Note that depending on
     * the values the human has supplied so far, additional values may become
     * necessary, so it may be necessary to iterate though several calls
     * to getPropertyInfo.
     *
     * @param url The URL of the database to connect to.
     * @param info A proposed list of tag/value pairs that will be sent on
     *          connect open.
     * @return An array of DriverPropertyInfo objects describing possible
     *          properties.  This array may be an empty array if no properties
     *          are required.
     * @exception SQLException if a database-access error occurs.
     */
    DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info)
			 throws SQLException;


    /**
     * Get the driver's major version number. Initially this should be 1.
     */
    int getMajorVersion();

    /**
     * Get the driver's minor version number. Initially this should be 0.
     */
    int getMinorVersion();


    /**
     * Report whether the Driver is a genuine JDBC COMPLIANT (tm) driver.
     * A driver may only report "true" here if it passes the JDBC compliance
     * tests, otherwise it is required to return false.
     *
     * JDBC compliance requires full support for the JDBC API and full support
     * for SQL 92 Entry Level.  It is expected that JDBC compliant drivers will
     * be available for all the major commercial databases.
     *
     * This method is not intended to encourage the development of non-JDBC
     * compliant drivers, but is a recognition of the fact that some vendors
     * are interested in using the JDBC API and framework for lightweight
     * databases that do not support full database functionality, or for
     * special databases such as document information retrieval where a SQL
     * implementation may not be feasible.
     */
    boolean jdbcCompliant();
} 

