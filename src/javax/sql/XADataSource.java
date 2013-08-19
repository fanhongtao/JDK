/*
 * @(#)XADataSource.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sql;

import java.sql.*;

/**
 * A factory for <code>XAConnection</code> objects that is used internally.  
 * An object that implements the <code>XADataSource</code> interface is
 * typically registered with a naming service that uses the
 * Java Naming and Directory Interface<sup><font size=-3>TM</font></sup>
 * (JNDI). 
 *
 * @since 1.4
 */

public interface XADataSource {

  /**
   * Attempts to establish a physical database connection that can be
   * used in a distributed transaction.
   *
   * @return  an <code>XAConnection</code> object, which represents a
   *          physical connection to a data source, that can be used in
   *          a distributed transaction
   * @exception SQLException if a database access error occurs
   */
  XAConnection getXAConnection() throws SQLException;
      
  /**
   * Attempts to establish a physical database connection, using the given
   * user name and password. The connection that is returned is one that
   * can be used in a distributed transaction.
   *
   * @param user the database user on whose behalf the connection is being made
   * @param password the user's password
   * @return  an <code>XAConnection</code> object, which represents a
   *          physical connection to a data source, that can be used in
   *          a distributed transaction
   * @exception SQLException if a database access error occurs
   */
  XAConnection getXAConnection(String user, String password) 
    throws SQLException;
      
  /**
   * <p>Retrieves the log writer for this <code>XADataSource</code> object.
   *
   * @return the log writer for this data source; <code>null</code> if no log
   *          writer has been set, which means that logging is disabled
   * @exception SQLException if a database access error occurs  
   * @see #setLogWriter
   */
  java.io.PrintWriter getLogWriter() throws SQLException;

  /**
   * Sets the log writer for this <code>XADataSource</code> object
   * to the given <code>java.io.PrintWriter</code> object.
   * <P>
   * The log writer is a character output stream to which all logging
   * and tracing messages for this <code>XADataSource</code> object will be
   * printed.  This includes messages printed by the methods of this
   * object, messages printed by methods of other objects manufactured
   * by this object, and so on.  Messages printed to a log writer that is
   * specific to a data source are not printed to the log writer associated
   * with the <code>java.sql.DriverManager</code> class. When a data source
   * object is created, the log writer is initially <code>null</code>.
   *
   * @param out the new log writer; to disable logging, set to <code>null</code>
   * @exception SQLException if a database access error occurs  
   * @see #getLogWriter
   */
  void setLogWriter(java.io.PrintWriter out) throws SQLException;

  /**
   * <p>Sets the maximum time in seconds that this data source will wait
   * while attempting to connect to a data source.  A value of zero
   * specifies that the timeout is the default system timeout 
   * if there is one; otherwise, it specifies that there is no timeout.
   * When a data source object is created, the login timeout is
   * initially zero.
   *
   * @param seconds the data source login time limit
   * @exception SQLException if a database access error occurs
   * @see #getLoginTimeout
   */
  void setLoginTimeout(int seconds) throws SQLException;
     
  /**
   * Retrieves the maximum time in seconds that this data source can wait
   * while attempting to connect to a data source.  A value of zero
   * means that the timeout is the default system timeout 
   * if there is one; otherwise, it means that there is no timeout.
   * When a data source object is created, the login timeout is
   * initially zero.
   *
   * @return the number of seconds that is the login time limit for this
   *         <code>XADataSource</code> object or zero if there is no
   *         no timeout limit or the timeout limit is the default system
   *         timeout limit if there is one
   * @exception SQLException if a database access error occurs
   * @see #setLoginTimeout
   */
  int getLoginTimeout() throws SQLException;
   
 } 





