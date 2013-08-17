/*
 * @(#)SQLInput.java	1.11 98/09/29
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
 * A input stream that contains a stream of values representing an 
 * instance of an SQL structured or distinct type.
 * This interface, used only for custom mapping, is used by the driver
 * behind the scenes, and a programmer never directly invokes
 * <code>SQLInput</code> methods.
 * <P>When the method <code>getObject</code> is called with an
 * object of a class implementing the interface <code>SQLData</code>,
 * the JDBC driver calls the method <code>SQLData.getSQLType</code>
 * to determine the SQL type of the user-defined type (UDT)
 * being custom mapped. The driver
 * creates an instance of <code>SQLInput</code>, populating it with the
 * attributes of the UDT.  The driver then passes the input
 * stream to the method <code>SQLData.readSQL</code>, which in turn 
 * calls the <code>SQLInput.readXXX</code> methods 
 * in its implementation for reading the
 * attributes from the input stream.
 */

public interface SQLInput {
  

  //================================================================
  // Methods for reading attributes from the stream of SQL data.
  // These methods correspond to the column-accessor methods of
  // java.sql.ResultSet.
  //================================================================

  /**
   * Reads the next attribute in the stream as a Java String.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  String readString() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java boolean.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  boolean readBoolean() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java byte.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  byte readByte() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java short.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  short readShort() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java int.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  int readInt() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java long.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  long readLong() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java float.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  float readFloat() throws SQLException;

  /**
   * Reads the next attribute in the stream as a Java double.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  double readDouble() throws SQLException;

  /**
   * Reads the next attribute in the stream as a java.math.BigDecimal object.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.math.BigDecimal readBigDecimal() throws SQLException;

  /**
   * Reads the next attribute in the stream as an array of bytes.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  byte[] readBytes() throws SQLException;

  /**
   * Reads the next attribute in the stream as a java.sql.Date object.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.sql.Date readDate() throws SQLException;

  /**
   * Reads the next attribute in the stream as a java.sql.Time object.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.sql.Time readTime() throws SQLException;

  /**
   * Reads the next attribute in the stream as a java.sql.Timestamp object.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.sql.Timestamp readTimestamp() throws SQLException;

  /**
   * Returns the next attribute in the stream as a stream of Unicode characters.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.io.Reader readCharacterStream() throws SQLException;

  /**
   * Returns the next attribute in the stream as a stream of ASCII characters.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.io.InputStream readAsciiStream() throws SQLException;

  /**
   * Returns the next attribute in the stream as a stream of uninterpreted
   * bytes.
   *
   * @return the attribute; if the value is SQL NULL, return null.
   * @exception SQLException if a database access error occurs
   */
  java.io.InputStream readBinaryStream() throws SQLException;
  
  //================================================================
  // Methods for reading items of SQL user-defined types from the stream.
  //================================================================

  /**
   * Returns the datum at the head of the stream as a Java object.  The 
   * actual type of the object returned is determined by the default type
   * mapping, and any customizations present in this stream's type map.
   *
   * A type map is registered with the stream by the JDBC driver before the
   * stream is passed to the application.
   *
   * When the datum at the head of the stream is an SQL NULL, 
   * the method returns null.  If the datum is an SQL structured or distinct
   * type, it determines the SQL type of the datum at the head of the stream, 
   * constructs an object of the appropriate class, and calls the method 
   * <code>SQLData.readSQL</code> on that object, which reads additional data from the 
   * stream, using the protocol described for that method.
   *
   * @return the datum at the head of the stream as a Java object; null if
   *         the datum is SQL NULL
   * @exception SQLException if a database access error occurs
   */
  Object readObject() throws SQLException;

  /**
   * Reads a REF(&lt;structured-type&gt;) from the stream.
   *
   * @return an object representing data of the SQL REF at the head of the stream
   * @exception SQLException if a database access error occurs
   */
  Ref readRef() throws SQLException;

  /**
   * Reads a BLOB from the stream.
   *
   * @return an object representing the SQL BLOB at the head of the stream
   * @exception SQLException if a database access error occurs
   */
  Blob readBlob() throws SQLException;

  /**
   * Reads a CLOB from the stream.
   *
   * @return an object representing the SQL CLOB at the head of the stream
   * @exception SQLException if a database access error occurs
   */
  Clob readClob() throws SQLException;

  /**
   * Reads an array from the stream.
   *
   * @return an object representing the SQL array at the head of the stream
   * @exception SQLException if a database access error occurs
   */
  Array readArray() throws SQLException;

  /**
   * Determines whether the last value read was null.
   * 
   * @return true if the most recently gotten SQL value was null;
   *         otherwise,  false 
   * @exception SQLException if a database access error occurs
   * 
   */
  boolean wasNull() throws SQLException;

}
