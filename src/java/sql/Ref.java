/*
 * @(#)Ref.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;
 
/**
 * JDBC 2.0
 *
 * A reference to an SQL structured type value in the database.  A
 * Ref can be saved to persistent storage.  A Ref is dereferenced by
 * passing it as a parameter to an SQL statement and executing the 
 * statement.
 */

public interface Ref {

  /**
   * Gets the fully-qualified SQL structured type name of the 
   * referenced item.
   * 
   * @return fully-qualified SQL structured type name of the referenced item.
   * @exception SQLException if a database access error occurs
   */
  String getBaseTypeName() throws SQLException;

}
