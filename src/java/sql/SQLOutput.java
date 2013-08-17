/*
 * @(#)SQLOutput.java	1.11 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * JDBC 2.0
 * The output stream for writing the attributes of a user-defined
 * type back to the database.  This interface, used 
 * only for custom mapping, is used by the driver, and its
 * methods are never directly invoked by a programmer.
 * <p>When an object of a class implementing interface
 * <code>SQLData</code> is passed as an argument to an SQL statement, the
 * JDBC driver calls <code>SQLData.getSQLType</code> to
 * determine the  kind of SQL
 * datum being passed to the database.
 * The driver then creates an instance of <code>SQLOutput</code> and
 * passes it to the method <code>SQLData.writeSQL</code>.
 * The method <code>writeSQL</code> in turn calls the
 * appropriate <code>SQLOutput.writeXXX</code> methods 
 * to write data from the <code>SQLData</code> object to
 * the <code>SQLOutput</code> output stream as the 
 * representation of an SQL user-defined type.
 */

 public interface SQLOutput {

  //================================================================
  // Methods for writing attributes to the stream of SQL data.
  // These methods correspond to the column-accessor methods of
  // java.sql.ResultSet.
  //================================================================

  /**
   * Writes the next attribute to the stream as a Java String.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeString(String x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java boolean.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeBoolean(boolean x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java byte.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeByte(byte x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java short.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeShort(short x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java int.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeInt(int x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java long.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeLong(long x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java float.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeFloat(float x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a Java double.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeDouble(double x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a java.math.BigDecimal object.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeBigDecimal(java.math.BigDecimal x) throws SQLException;

  /**
   * Writes the next attribute to the stream as an array of bytes.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeBytes(byte[] x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a java.sql.Date object.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeDate(java.sql.Date x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a java.sql.Time object.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeTime(java.sql.Time x) throws SQLException;

  /**
   * Writes the next attribute to the stream as a java.sql.Timestamp object.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeTimestamp(java.sql.Timestamp x) throws SQLException;

  /**
   * Returns the next attribute to the stream as a stream of Unicode characters.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeCharacterStream(java.io.Reader x) throws SQLException;

  /**
   * Returns the next attribute to the stream as a stream of ASCII characters.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeAsciiStream(java.io.InputStream x) throws SQLException;

  /**
   * Returns the next attribute to the stream as a stream of uninterpreted
   * bytes.
   *
   * @param x the value to pass to the database.
   * @exception SQLException if a database access error occurs
   */
  void writeBinaryStream(java.io.InputStream x) throws SQLException;
  
  //================================================================
  // Methods for writing items of SQL user-defined types to the stream.
  // These methods pass objects to the database as values of SQL
  // Structured Types, Distinct Types, Constructed Types, and Locator
  // Types.  They decompose the Java object(s) and write leaf data
  // items using the methods above.
  //================================================================

  /**
   * Writes to the stream the data contained in the given 
   * <code>SQLData</code> object.
   * When the <code>SQLData</code> object is null, this
   * method writes an SQL NULL to the stream.  
   * Otherwise, it calls the <code>SQLData.writeSQL</code>
   * method of the given object, which 
   * writes the object's attributes to the stream.
   * The implementation of the method <code>SQLData.writeSQ</code>
   * calls the appropriate <code>SQLOutput.writeXXX</code> method(s)
   * for writing each of the object's attributes in order.
   * The attributes must be read from an <code>SQLInput</code>
   * input stream and written to an <code>SQLOutput</code>
   * output stream in the same order in which they were
   * listed in the SQL definition of the user-defined type.
   * 
   * @param x the object representing data of an SQL structured or
   * distinct type
   * @exception SQLException if a database access error occurs
   */
  void writeObject(SQLData x) throws SQLException;

  /**
   * Writes a REF(&lt;structured-type&gt;) to the stream.
   *
   * @param x an object representing data of an SQL REF Type
   * @exception SQLException if a database access error occurs
   */
  void writeRef(Ref x) throws SQLException;

  /**
   * Writes a BLOB to the stream.
   *
   * @param x an object representing a BLOB
   * @exception SQLException if a database access error occurs
   */
  void writeBlob(Blob x) throws SQLException;

  /**
   * Writes a CLOB to the stream.
   *
   * @param x an object representing a CLOB
   * @exception SQLException if a database access error occurs
   */
  void writeClob(Clob x) throws SQLException;

  /**
   * Writes a structured-type to the stream.
   *
   * @param x an object representing data of a Structured Type
   * @exception SQLException if a database access error occurs
   */
  void writeStruct(Struct x) throws SQLException;

  /**
   * Writes an array to the stream.
   *
   * @param x an object representing an SQL array
   * @exception SQLException if a database access error occurs
   */
  void writeArray(Array x) throws SQLException;

}
 
