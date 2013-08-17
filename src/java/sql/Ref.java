/*
 * @(#)Ref.java	1.9 98/09/23
 * 
 * Copyright 1998 by Sun Microsystems, Inc.,
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
