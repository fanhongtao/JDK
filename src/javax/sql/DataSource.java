/*
 * @(#)DataSource.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;

/** 
 * <p>A factory for connections to the physical data source that this
 * <code>DataSource</code> object represents.  An alternative to the
 * <code>DriverManager</code> facility, a <code>DataSource</code> object
 * is the preferred means of getting a connection. An object that implements
 * the <code>DataSource</code> interface will typically be
 * registered with a naming service based on the 
 * Java<sup><font size=-2>TM</font></sup> Naming and Directory (JNDI) API.
 * <P>
 * The <code>DataSource</code> interface is implemented by a driver vendor.
 * There are three types of implementations:
 * <OL>
 *   <LI>Basic implementation -- produces a standard <code>Connection</code> 
 *       object
 *   <LI>Connection pooling implementation -- produces a <code>Connection</code>
 *       object that will automatically participate in connection pooling.  This
 *       implementation works with a middle-tier connection pooling manager.
 *   <LI>Distributed transaction implementation -- produces a
 *       <code>Connection</code> object that may be used for distributed
 *       transactions and almost always participates in connection pooling. 
 *       This implementation works with a middle-tier 
 *       transaction manager and almost always with a connection 
 *       pooling manager.
 * </OL>
 * <P>
 * A <code>DataSource</code> object has properties that can be modified
 * when necessary.  For example, if the data source is moved to a different
 * server, the property for the server can be changed.  The benefit is that
 * because the data source's properties can be changed, any code accessing
 * that data source does not need to be changed.
 * <P>
 * A driver that is accessed via a <code>DataSource</code> object does not 
 * register itself with the <code>DriverManager</code>.  Rather, a
 * <code>DataSource</code> object is retrieved though a lookup operation
 * and then used to create a <code>Connection</code> object.  With a basic
 * implementation, the connection obtained through a <code>DataSource</code>
 * object is identical to a connection obtained through the
 * <code>DriverManager</code> facility.
 *
 * @since 1.4
 */

public interface DataSource {

  /**
   * <p>Attempts to establish a connection with the data source that
   * this <code>DataSource</code> object represents.
   *
   * @return  a connection to the data source
   * @exception SQLException if a database access error occurs
   */
  Connection getConnection() throws SQLException;
      
  /**
   * <p>Attempts to establish a connection with the data source that
   * this <code>DataSource</code> object represents.
   *
   * @param username the database user on whose behalf the connection is 
   *  being made
   * @param password the user's password
   * @return  a connection to the data source
   * @exception SQLException if a database access error occurs
   */
  Connection getConnection(String username, String password) 
    throws SQLException;
      
  /**
   * <p>Retrieves the log writer for this <code>DataSource</code>
   * object.
   *
   * <p>The log writer is a character output stream to which all logging
   * and tracing messages for this data source will be
   * printed.  This includes messages printed by the methods of this
   * object, messages printed by methods of other objects manufactured
   * by this object, and so on.  Messages printed to a data source
   * specific log writer are not printed to the log writer associated
   * with the <code>java.sql.Drivermanager</code> class.  When a
   * <code>DataSource</code> object is
   * created, the log writer is initially null; in other words, the
   * default is for logging to be disabled.
   *
   * @return the log writer for this data source or null if
   *        logging is disabled
   * @exception SQLException if a database access error occurs  
   * @see #setLogWriter
   */
  java.io.PrintWriter getLogWriter() throws SQLException;

  /**
   * <p>Sets the log writer for this <code>DataSource</code>
   * object to the given <code>java.io.PrintWriter</code> object.
   *
   * <p>The log writer is a character output stream to which all logging
   * and tracing messages for this data source will be
   * printed.  This includes messages printed by the methods of this
   * object, messages printed by methods of other objects manufactured
   * by this object, and so on.  Messages printed to a data source-
   * specific log writer are not printed to the log writer associated
   * with the <code>java.sql.Drivermanager</code> class. When a 
   * <code>DataSource</code> object is created the log writer is
   * initially null; in other words, the default is for logging to be
   * disabled.
   *
   * @param out the new log writer; to disable logging, set to null
   * @exception SQLException if a database access error occurs  
   * @see #getLogWriter
   */
  void setLogWriter(java.io.PrintWriter out) throws SQLException;

  /**
   * <p>Sets the maximum time in seconds that this data source will wait
   * while attempting to connect to a database.  A value of zero
   * specifies that the timeout is the default system timeout 
   * if there is one; otherwise, it specifies that there is no timeout.
   * When a <code>DataSource</code> object is created, the login timeout is
   * initially zero.
   *
   * @param seconds the data source login time limit
   * @exception SQLException if a database access error occurs.
   * @see #getLoginTimeout
   */
  void setLoginTimeout(int seconds) throws SQLException;
     
  /**
   * Gets the maximum time in seconds that this data source can wait
   * while attempting to connect to a database.  A value of zero
   * means that the timeout is the default system timeout 
   * if there is one; otherwise, it means that there is no timeout.
   * When a <code>DataSource</code> object is created, the login timeout is
   * initially zero.
   *
   * @return the data source login time limit
   * @exception SQLException if a database access error occurs.
   * @see #setLoginTimeout
   */
  int getLoginTimeout() throws SQLException;
} 





