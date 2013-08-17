/*
 * @(#)Array.java	1.9 98/09/13
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
 * <p>
 * The mapping in the Java programming language for the SQL type
 * <code>ARRAY</code>.
 * By default, an <code>Array</code> is a transaction duration 
 * reference to an SQL array.  By default, an <code>Array</code>
 * is implemented using an SQL LOCATOR(array) internally.
 */

public interface Array {

  /**
   * Returns the SQL type name of the elements in 
   * the array designated by this <code>Array</code> object.
   * If the elements are a built-in type, it returns
   * the database-specific type name of the elements. 
   * If the elements are a user-defined type (UDT),
   * this method returns the fully-qualified SQL type name.
   * @return a <code>String</code> that is the database-specific
   * name for a built-in base type or the fully-qualified SQL type
   * name for a base type that is a UDT
   * @exception SQLException if an error occurs while attempting
   * to access the type name
   */
  String getBaseTypeName() throws SQLException;

  /**
   * Returns the JDBC type of the elements in the array designated
   * by this <code>Array</code> object.
   * @return a constant from the class {@link java.sql.Types} that is
   * the type code for the elements in the array designated by this
   * <code>Array</code> object.
   * @exception SQLException if an error occurs while attempting
   * to access the base type 
   */
  int getBaseType() throws SQLException;

  /**
   * Retrieves the contents of the SQL array designated by this
   * <code>Array</code> object in the form of an array in the Java
   * programming language. This version of the method <code>getArray</code>
   * uses the type map associated with the connection for customizations of 
   * the type mappings.
   * @return an array in the Java programming language that contains 
   * the ordered elements of the SQL ARRAY object designated by this object
   * @exception SQLException if an error occurs while attempting to
   * access the array
   */
  Object getArray() throws SQLException;

  /**
   * Retrieves the contents of the SQL array designated by this 
   * <code>Array</code>
   * object, using the specified <code>map</code> for type map 
   * customizations.  If the base type of the array does not
   * match a user-defined type in <code>map</code>, the standard
   * mapping is used instead.
   * @param map a <code>java.util.Map</code> object that contains mappings
   *            of SQL type names to classes in the Java programming language
   * @return an array in the Java programming language that contains the ordered 
   *         elements of the SQL array designated by this object
   * @exception SQLException if an error occurs while attempting to 
   *                         access the array
   */
  Object getArray(java.util.Map map) throws SQLException;

  /**
   * Returns an array containing a  slice of the SQL array, beginning with the
   * specified <code>index</code> and containing up to <code>count</code> 
   * successive elements of the SQL array.  This method uses the type-map
   * associated with the connection for customizations of the type-mappings.
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to retrieve
   * @return an array containing up to <code>count</code> consecutive elements 
   * of the SQL array, beginning with element <code>index</code>
   * @exception SQLException if an error occurs while attempting to
   * access the array
   */
  Object getArray(long index, int count) throws SQLException;

  /**
   * Returns an array containing a slice of the SQL array object 
   * designated by this object, beginning with the specified
   * <code>index</code> and containing up to <code>count</code>
   * successive elements of the SQL array.  This method uses 
   * the specified <code>map</code> for type-map customizations
   * unless the base type of the array does not match a user-
   * defined type in <code>map</code>, in which case it 
   * uses the standard mapping.
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to 
   * retrieve
   * @param map a <code>java.util.Map</code> object
   * that contains SQL type names and the classes in
   * the Java programming language to which they are mapped
   * @return an array containing up to <code>count</code>
   * consecutive elements of the SQL array designated by this
   * <code>Array</code> object, beginning with element 
   * <code>index</code>.
   * @exception SQLException if an error occurs while attempting to
   * access the array
   */
  Object getArray(long index, int count, java.util.Map map) 
    throws SQLException;

  /**
   * Returns a result set that contains the elements of the array
   * designated by this <code>Array</code> object.  If appropriate,
   * the elements of the array are mapped using the connection's type 
   * map; otherwise, the standard mapping is used.
   * <p>
   * The result set contains one row for each array element, with
   * two columns in each row.  The second column stores the element
   * value; the first column stores the index into the array for 
   * that element (with the first array element being at index 1). 
   * The rows are in ascending order corresponding to
   * the order of the indices.
   * @return a {@link ResultSet} object containing one row for each
   * of the elements in the array designated by this <code>Array</code>
   * object, with the rows in ascending order based on the indices.
   * @exception SQLException if an error occurs while attempting to
   * access the array
   */
  ResultSet getResultSet () throws SQLException;

  /**
   * Returns a result set that contains the elements of the array
   * designated by this <code>Array</code> object and uses the given
   * <code>map</code> to map the array elements.  If the base
   * type of the array does not match a user-defined type in
   * <code>map</code>, the standard mapping is used instead.
   * <p>
   * The result set contains one row for each array element, with
   * two columns in each row.  The second column stores the element
   * value; the first column stores the index into the array for 
   * that element (with the first array element being at index 1). 
   * The rows are in ascending order corresponding to
   * the order of the indices.
   * @param map contains mapping of SQL user-defined types to 
   * classes in the Java(tm) programming language
   * @return a <code>ResultSet</code> object containing one row for each
   * of the elements in the array designated by this <code>Array</code>
   * object, with the rows in ascending order based on the indices.
   * @exception SQLException if an error occurs while attempting to
   * access the array
   */
  ResultSet getResultSet (java.util.Map map) throws SQLException;

  /**
   * Returns a result set holding the elements of the subarray that
   * starts at index <code>index</code> and contains up to 
   * <code>count</code> successive elements.  This method uses
   * the connection's type map to map the elements of the array if
   * the map contains an entry for the base type. Otherwise, the
   * standard mapping is used.
   * <P>
   * The result set has one row for each element of the SQL array
   * designated by this object, with the first row containing the 
   * element at index <code>index</code>.  The result set has
   * up to <code>count</code> rows in ascending order based on the
   * indices.  Each row has two columns:  The second column stores
   * the element value; the first column stroes the index into the
   * array for that element.
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to retrieve
   * @return a <code>ResultSet</code> object containing up to
   * <code>count</code> consecutive elements of the SQL array
   * designated by this <code>Array</code> object, starting at
   * index <code>index</code>.
   * @exception SQLException if an error occurs while attempting to
   * access the array
   */
  ResultSet getResultSet(long index, int count) throws SQLException;

  /**
   * Returns a result set holding the elements of the subarray that
   * starts at index <code>index</code> and contains up to
   * <code>count</code> successive elements.  This method uses
   * the <code>Map</code> object <code>map</code> to map the elements
   * of the array unless the base type of the array does not match
   * a user-defined type in <code>map</code>, in which case it uses
   * the standard mapping.
   * <P>
   * The result set has one row for each element of the SQL array
   * designated by this object, with the first row containing the
   * element at index <code>index</code>.  The result set has   
   * up to <code>count</code> rows in ascending order based on the
   * indices.  Each row has two columns:  The second column stores  
   * the element value; the first column stroes the index into the
   * array for that element.
   * @param index the array index of the first element to retrieve;
   *              the first element is at index 1
   * @param count the number of successive SQL array elements to retrieve
   * @param map the <code>Map</code> object that contains the mapping
   * of SQL type names to classes in the Java(tm) programming language
   * @return a <code>ResultSet</code> object containing up to               
   * <code>count</code> consecutive elements of the SQL array
   * designated by this <code>Array</code> object, starting at
   * index <code>index</code>.
   * @exception SQLException if an error occurs while attempting to
   * access the array
   *
   */
  ResultSet getResultSet (long index, int count, java.util.Map map)
    throws SQLException;

}


