/*
 * @(#)PooledConnection.java	1.9 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An object that provides hooks for connection pool management.  
 * A <code>PooledConnection</code> object
 * represents a physical connection to a data source.  The connection
 * can be recycled rather than being closed when an application is
 * finished with it, thus reducing the number of connections that
 * need to be made.
 * <P>
 * An application programmer does not use the <code>PooledConnection</code>
 * interface directly; rather, it is used by a middle tier infrastructure
 * that manages the pooling of connections.
 * 
 * @since 1.4 
 */

public interface PooledConnection {

  /**
   * Creates an object handle for the physical connection that
   * this <code>PooledConnection</code> object represents.  The object
   * returned is a temporary handle used by application code to refer to
   * a physical connection (this <code>PooldedConnection</code> object) 
   * that is being pooled.
   *
   * @return  a <code>Connection</code> object that is a handle to
   *          this <code>PooledConnection</code> object
   * @exception SQLException if a database access error occurs
   */
  Connection getConnection() throws SQLException;
      
  /**
   * Closes the physical connection that this <code>PooledConnection</code>
   * object represents.
   *
   * @exception SQLException if a database access error occurs
   */
  void close() throws SQLException;
      
  /**
   * Registers the given event listener so that it will be notified
   * when an event occurs on this <code>PooledConnection</code> object.
   *
   * @param listener a component that has implemented the
   *        <code>ConnectionEventListener</code> interface and wants to be
   *        notified when the connection is closed or has an error;
   *        generally, a connection pool manager
   * @see #removeConnectionEventListener
   */
  void addConnectionEventListener(ConnectionEventListener listener);

  /**
   * Removes the given event listener from the list of components that
   * will be notified when an event occurs on this
   * <code>PooledConnection</code> object.
   *
   * @param listener a component that has implemented the
   *        <code>ConnectionEventListener</code> interface and been
   *        been registered as a listener; generally, a connection pool manager
   * @see #addConnectionEventListener
   */
  void removeConnectionEventListener(ConnectionEventListener listener);

 } 
