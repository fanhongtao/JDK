/*
 * @(#)Struct.java	1.12 98/09/27
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
 * <p>The standard mapping for an SQL
 * structured type. A <code>Struct</code> object contains a
 * value for each attribute of the SQL structured type that
 * it represents.
 * By default, an instance of<code>Struct</code> is valid as long as the 
 * application has a reference to it.
 */

public interface Struct {

  /**
   * Retrieves the SQL type name of the SQL structured type
   * that this <code>Struct</code> object represents.
   *
   * @returns the fully-qualified type name of the SQL structured 
   *          type for which this <code>Struct</code> object
   *          is the generic representation
   * @exception SQLException if a database access error occurs
   */
  String getSQLTypeName() throws SQLException;

  /**
   * Produces the ordered values of the attributes of the SQL 
   * structurec type that this <code>Struct</code> object represents.
   * This method uses the type map associated with the 
   * connection for customizations of the type mappings.
   * If there is no
   * entry in the connection's type map that matches the structured
   * type that this <code>Struct</code> object represents,
   * the driver uses the standard mapping.
   * <p>
   * Conceptually, this method calls the method
   * <code>getObject</code> on each attribute
   * of the structured type and returns a Java array containing 
   * the result.
   *
   * @return an array containing the ordered attribute values
   * @exception SQLException if a database access error occurs
   */
  Object[] getAttributes() throws SQLException;

  /**
   * Produces the ordered values of the attributes of the SQL 
   * structurec type that this <code>Struct</code> object represents.
   * This method uses the given type map
   * for customizations of the type mappings.
   * If there is no
   * entry in the given type map that matches the structured
   * type that this <code>Struct</code> object represents,
   * the driver uses the standard mapping.
   * <p>
   * Conceptually, this method calls the method
   * <code>getObject</code> on each attribute
   * of the structured type and returns a Java array containing
   * the result.
   *
   * @param map a mapping of SQL type names to Java classes
   * @return an array containing the ordered attribute values
   * @exception SQLException if a database access error occurs
   */
  Object[] getAttributes(java.util.Map map) throws SQLException;
}


