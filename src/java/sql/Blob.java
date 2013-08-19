/*
 * @(#)Blob.java	1.27 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;
 
/**
 * The representation (mapping) in
 * the Java<sup><font size=-2>TM</font></sup> programming
 * language of an SQL 
 * <code>BLOB</code> value.  An SQL <code>BLOB</code> is a built-in type 
 * that stores a Binary Large Object as a column value in a row of 
 * a database table. By default drivers implement <code>Blob</code> using
 * an SQL <code>locator(BLOB)</code>, which means that a
 * <code>Blob</code> object contains a logical pointer to the
 * SQL <code>BLOB</code> data rather than the data itself.
 * A <code>Blob</code> object is valid for the duration of the
 * transaction in which is was created.
 * 
 * <P>Methods in the interfaces {@link ResultSet}, 
 * {@link CallableStatement}, and {@link PreparedStatement}, such as
 * <code>getBlob</code> and <code>setBlob</code> allow a programmer to 
 * access an SQL <code>BLOB</code> value.
 * The <code>Blob</code> interface provides methods for getting the
 * length of an SQL <code>BLOB</code> (Binary Large Object) value,
 * for materializing a <code>BLOB</code> value on the client, and for
 * determining the position of a pattern of bytes within a 
 * <code>BLOB</code> value. In addition, this interface has methods for updating
 * a <code>BLOB</code> value.
 *
 * @since 1.2
 */

public interface Blob {

  /**
   * Returns the number of bytes in the <code>BLOB</code> value
   * designated by this <code>Blob</code> object.
   * @return length of the <code>BLOB</code> in bytes
   * @exception SQLException if there is an error accessing the
   * length of the <code>BLOB</code>
   * @since 1.2
   */
  long length() throws SQLException;

  /**
   * Retrieves all or part of the <code>BLOB</code>
   * value that this <code>Blob</code> object represents, as an array of
   * bytes.  This <code>byte</code> array contains up to <code>length</code> 
   * consecutive bytes starting at position <code>pos</code>.
   *
   * @param pos the ordinal position of the first byte in the 
   *        <code>BLOB</code> value to be extracted; the first byte is at
   *        position 1
   * @param length the number of consecutive bytes to be copied
   * @return a byte array containing up to <code>length</code> 
   *         consecutive bytes from the <code>BLOB</code> value designated
   *         by this <code>Blob</code> object, starting with the
   *         byte at position <code>pos</code>
   * @exception SQLException if there is an error accessing the
   *            <code>BLOB</code> value
   * @see #setBytes
   * @since 1.2
   */
  byte[] getBytes(long pos, int length) throws SQLException; 

  /**
   * Retrieves the <code>BLOB</code> value designated by this
   * <code>Blob</code> instance as a stream.
   *
   * @return a stream containing the <code>BLOB</code> data
   * @exception SQLException if there is an error accessing the
   *            <code>BLOB</code> value
   * @see #setBinaryStream
   * @since 1.2
   */
  java.io.InputStream getBinaryStream () throws SQLException;

  /** 
   * Retrieves the byte position at which the specified byte array
   * <code>pattern</code> begins within the <code>BLOB</code>
   * value that this <code>Blob</code> object represents.  The
   * search for <code>pattern</code> begins at position
   * <code>start</code>.  
   *
   * @param pattern the byte array for which to search
   * @param start the position at which to begin searching; the
   *        first position is 1
   * @return the position at which the pattern appears, else -1
   * @exception SQLException if there is an error accessing the 
   * <code>BLOB</code>
   * @since 1.2
   */
  long position(byte pattern[], long start) throws SQLException;

  /** 
   * Retrieves the byte position in the <code>BLOB</code> value
   * designated by this <code>Blob</code> object at which 
   * <code>pattern</code> begins.  The search begins at position
   * <code>start</code>.
   *
   * @param pattern the <code>Blob</code> object designating
   * the <code>BLOB</code> value for which to search
   * @param start the position in the <code>BLOB</code> value
   *        at which to begin searching; the first position is 1
   * @return the position at which the pattern begins, else -1
   * @exception SQLException if there is an error accessing the
   *            <code>BLOB</code> value
   * @since 1.2
   */
  long position(Blob pattern, long start) throws SQLException;

    // -------------------------- JDBC 3.0 -----------------------------------

    /**
     * Writes the given array of bytes to the <code>BLOB</code> value that
     * this <code>Blob</code> object represents, starting at position 
     * <code>pos</code>, and returns the number of bytes written.
     *
     * @param pos the position in the <code>BLOB</code> object at which
     *        to start writing
     * @param bytes the array of bytes to be written to the <code>BLOB</code>
     *        value that this <code>Blob</code> object represents
     * @return the number of bytes written
     * @exception SQLException if there is an error accessing the
     *            <code>BLOB</code> value
     * @see #getBytes
     * @since 1.4
     */
    int setBytes(long pos, byte[] bytes) throws SQLException;

    /**
     * Writes all or part of the given <code>byte</code> array to the
     * <code>BLOB</code> value that this <code>Blob</code> object represents
     * and returns the number of bytes written.
     * Writing starts at position <code>pos</code> in the <code>BLOB</code>
     * value; <code>len</code> bytes from the given byte array are written.
     *
     * @param pos the position in the <code>BLOB</code> object at which
     *        to start writing
     * @param bytes the array of bytes to be written to this <code>BLOB</code>
     *        object
     * @param offset the offset into the array <code>bytes</code> at which
     *        to start reading the bytes to be set
     * @param len the number of bytes to be written to the <code>BLOB</code>
     *        value from the array of bytes <code>bytes</code>
     * @return the number of bytes written
     * @exception SQLException if there is an error accessing the
     *            <code>BLOB</code> value
     * @see #getBytes
     * @since 1.4
     */
    int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException;

    /**
     * Retrieves a stream that can be used to write to the <code>BLOB</code> 
     * value that this <code>Blob</code> object represents.  The stream begins
     * at position <code>pos</code>.
     *
     * @param pos the position in the <code>BLOB</code> value at which
     *        to start writing
     * @return a <code>java.io.OutputStream</code> object to which data can 
     *         be written
     * @exception SQLException if there is an error accessing the
     *            <code>BLOB</code> value
     * @see #getBinaryStream
     * @since 1.4
     */
    java.io.OutputStream setBinaryStream(long pos) throws SQLException;

    /**
     * Truncates the <code>BLOB</code> value that this <code>Blob</code>
     * object represents to be <code>len</code> bytes in length.
     *
     * @param len the length, in bytes, to which the <code>BLOB</code> value
     *        that this <code>Blob</code> object represents should be truncated
     * @exception SQLException if there is an error accessing the
     *            <code>BLOB</code> value
     * @since 1.4
     */
    void truncate(long len) throws SQLException;

}


