/*
 * @(#)ConnectionPoolDataSource.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sql;

import java.sql.SQLException;


/**
 * A factory for <code>PooledConnection</code>
 * objects.  An object that implements this interface will typically be
 * registered with a naming service that is based on the
 * Java<sup><font size=-2>TM</font></sup> Naming and Directory Interface
 * (JNDI).
 *
 * @since 1.4
 */

public interface ConnectionPoolDataSource {

  /**
   * Attempts to establish a physical database connection that can
   * be used as a pooled connection.
   *
   * @return  a <code>PooledConnection</code> object that is a physical
   *         connection to the database that this
   *         <code>ConnectionPoolDataSource</code> object represents
   * @exception SQLException if a database access error occurs
   */
  PooledConnection getPooledConnection() throws SQLException;
      
  /**
   * Attempts to establish a physical database connection that can
   * be used as a pooled connection.
   *
   * @param user the database user on whose behalf the connection is being made
   * @param password the user's password
   * @return  a <code>PooledConnection</code> object that is a physical
   *         connection to the database that this
   *         <code>ConnectionPoolDataSource</code> object represents
   * @exception SQLException if a database access error occurs
   */
  PooledConnection getPooledConnection(String user, String password) 
    throws SQLException;
      
  /**
   * Retrieves the log writer for this <code>ConnectionPoolDataSource</code>
   * object.  
   * <p>The log writer is a character output stream to which all logging
   * and tracing messages for this <code>ConnectionPoolDataSource</code> object
   * are printed.  This includes messages printed by the methods of this
   * object, messages printed by methods of other objects manufactured
   * by this object, and so on.  Messages printed to a data source-
   * specific log writer are not printed to the log writer associated
   * with the <code>java.sql.DriverManager</code> class.  When a data
   * source object is created, the log writer is initially null; in other 
   * words, the default is for logging to be disabled.
   *
   * @return the log writer for this  <code>ConnectionPoolDataSource</code> 
   *         object or <code>null</code> if logging is disabled
   * @exception SQLException if a database access error occurs  
   * @see #setLogWriter
   */
  java.io.PrintWriter getLogWriter() throws SQLException;

  /**
   * Sets the log writer for this <code>ConnectionPoolDataSource</code>
   * object to the given <code>java.io.PrintWriter</code> object.  
   *
   * <p>The log writer is a character output stream to which all logging
   * and tracing messages for this <code>ConnectionPoolDataSource</code>
   * object are printed.  This includes messages printed by the methods of this
   * object, messages printed by methods of other objects manufactured
   * by this object, and so on.  Messages printed to a data source-
   * specific log writer are not printed to the log writer associated
   * with the <code>java.sql.Drivermanager</code> class.  When a data
   * source object is created, the log writer is initially null; in other 
   * words, the default is for logging to be disabled.
   *
   * @param out the new log writer; <code>null</code> to disable logging
   * @exception SQLException if a database access error occurs  
   * @see #getLogWriter
   */
  void setLogWriter(java.io.PrintWriter out) throws SQLException;

  /**
   * Sets the maximum time in seconds that this 
   * <code>ConnectionPoolDataSource</code> object will wait
   * while attempting to connect to a database.  A value of zero
   * specifies that the timeout is the default system timeout 
   * if there is one; otherwise, it specifies that there is no timeout.
   * When a <code>ConnectionPoolDataSource</code> object is created,
   * the login timeout is initially zero.
   *
   * @param seconds the data source login time limit
   * @exception SQLException if a database access error occurs.
   * @see #getLoginTimeout
   */
  void setLoginTimeout(int seconds) throws SQLException;
     
  /**
   * Retrieves the maximum time in seconds that this 
   * <code>ConnectionPoolDataSource</code> object will wait
   * while attempting to connect to a database.  A value of zero
   * means that the timeout is the default system timeout 
   * if there is one; otherwise, it means that there is no timeout.
   * When a <code>DataSource</code> object is created, its login timeout is
   * initially zero.
   *
   * @return the data source login time limit
   * @exception SQLException if a database access error occurs.
   * @see #setLoginTimeout
   */
  int getLoginTimeout() throws SQLException;
   
 } 





