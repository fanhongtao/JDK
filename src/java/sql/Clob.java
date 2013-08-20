/*
 * @(#)Clob.java	1.29 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;
 
/**
 * The mapping in the Java<sup><font size=-2>TM</font></sup> programming language 
 * for the SQL <code>CLOB</code> type.
 * An SQL <code>CLOB</code> is a built-in type
 * that stores a Character Large Object as a column value in a row of
 * a database table.
 * By default drivers implement a <code>Clob</code> object using an SQL
 * <code>locator(CLOB)</code>, which means that a <code>Clob</code> object 
 * contains a logical pointer to the SQL <code>CLOB</code> data rather than
 * the data itself. A <code>Clob</code> object is valid for the duration
 * of the transaction in which it was created.
 * <P>The <code>Clob</code> interface provides methods for getting the
 * length of an SQL <code>CLOB</code> (Character Large Object) value,
 * for materializing a <code>CLOB</code> value on the client, and for
 * searching for a substring or <code>CLOB</code> object within a
 * <code>CLOB</code> value.
 * Methods in the interfaces {@link ResultSet},
 * {@link CallableStatement}, and {@link PreparedStatement}, such as
 * <code>getClob</code> and <code>setClob</code> allow a programmer to
 * access an SQL <code>CLOB</code> value.  In addition, this interface
 * has methods for updating a <code>CLOB</code> value.
 * 
 * @since 1.2
 */

public interface Clob {

  /**
   * Retrieves the number of characters 
   * in the <code>CLOB</code> value
   * designated by this <code>Clob</code> object.
   *
   * @return length of the <code>CLOB</code> in characters
   * @exception SQLException if there is an error accessing the
   *            length of the <code>CLOB</code> value
   * @since 1.2
   */
  long length() throws SQLException;

  /**
   * Retrieves a copy of the specified substring 
   * in the <code>CLOB</code> value
   * designated by this <code>Clob</code> object.
   * The substring begins at position
   * <code>pos</code> and has up to <code>length</code> consecutive
   * characters.
   *
   * @param pos the first character of the substring to be extracted.
   *            The first character is at position 1.
   * @param length the number of consecutive characters to be copied
   * @return a <code>String</code> that is the specified substring in
   *         the <code>CLOB</code> value designated by this <code>Clob</code> object
   * @exception SQLException if there is an error accessing the
   *            <code>CLOB</code> value
   * @since 1.2
   */
  String getSubString(long pos, int length) throws SQLException;

  /**
   * Retrieves the <code>CLOB</code> value designated by this <code>Clob</code>
   * object as a <code>java.io.Reader</code> object (or as a stream of
   * characters).
   *
   * @return a <code>java.io.Reader</code> object containing the 
   *         <code>CLOB</code> data
   * @exception SQLException if there is an error accessing the 
   *            <code>CLOB</code> value
   * @see #setCharacterStream
   * @since 1.2
   */
  java.io.Reader getCharacterStream() throws SQLException;

  /**
   * Retrieves the <code>CLOB</code> value designated by this <code>Clob</code>
   * object as an ascii stream.
   *
   * @return a <code>java.io.InputStream</code> object containing the 
   *         <code>CLOB</code> data
   * @exception SQLException if there is an error accessing the 
   *            <code>CLOB</code> value
   * @see #setAsciiStream
   * @since 1.2
   */
  java.io.InputStream getAsciiStream() throws SQLException;

  /** 
   * Retrieves the character position at which the specified substring 
   * <code>searchstr</code> appears in the SQL <code>CLOB</code> value
   * represented by this <code>Clob</code> object.  The search 
   * begins at position <code>start</code>.
   *
   * @param searchstr the substring for which to search 
   * @param start the position at which to begin searching; the first position
   *              is 1
   * @return the position at which the substring appears or -1 if it is not
   *         present; the first position is 1
   * @exception SQLException if there is an error accessing the       
   *            <code>CLOB</code> value
   * @since 1.2
   */
  long position(String searchstr, long start) throws SQLException;

  /** 
   * Retrieves the character position at which the specified  
   * <code>Clob</code> object <code>searchstr</code> appears in this 
   * <code>Clob</code> object.  The search begins at position 
   * <code>start</code>.
   *
   * @param searchstr the <code>Clob</code> object for which to search
   * @param start the position at which to begin searching; the first
   *              position is 1
   * @return the position at which the <code>Clob</code> object appears 
   *              or -1 if it is not present; the first position is 1
   * @exception SQLException if there is an error accessing the 
   *            <code>CLOB</code> value
   * @since 1.2
   */
  long position(Clob searchstr, long start) throws SQLException;

    //---------------------------- jdbc 3.0 -----------------------------------

    /**
     * Writes the given Java <code>String</code> to the <code>CLOB</code>
     * value that this <code>Clob</code> object designates at the position 
     * <code>pos</code>.
     *
     * @param pos the position at which to start writing to the <code>CLOB</code>
     *         value that this <code>Clob</code> object represents
     * @param str the string to be written to the <code>CLOB</code>
     *        value that this <code>Clob</code> designates
     * @return the number of characters written
     * @exception SQLException if there is an error accessing the 
     *            <code>CLOB</code> value
     *
     * @since 1.4
     */
    int setString(long pos, String str) throws SQLException;

    /**
     * Writes <code>len</code> characters of <code>str</code>, starting 
     * at character <code>offset</code>, to the <code>CLOB</code> value
     * that this <code>Clob</code> represents.
     *
     * @param pos the position at which to start writing to this
     *        <code>CLOB</code> object
     * @param str the string to be written to the <code>CLOB</code> 
     *        value that this <code>Clob</code> object represents
     * @param offset the offset into <code>str</code> to start reading
     *        the characters to be written
     * @param len the number of characters to be written
     * @return the number of characters written
     * @exception SQLException if there is an error accessing the 
     *            <code>CLOB</code> value
     *
     * @since 1.4
     */
    int setString(long pos, String str, int offset, int len) throws SQLException;

    /**
     * Retrieves a stream to be used to write Ascii characters to the
     * <code>CLOB</code> value that this <code>Clob</code> object represents, 
     * starting at position <code>pos</code>.
     *
     * @param pos the position at which to start writing to this
     *        <code>CLOB</code> object
     * @return the stream to which ASCII encoded characters can be written
     * @exception SQLException if there is an error accessing the 
     *            <code>CLOB</code> value
     * @see #getAsciiStream
     *
     * @since 1.4
     */
    java.io.OutputStream setAsciiStream(long pos) throws SQLException;

    /**
     * Retrieves a stream to be used to write a stream of Unicode characters 
     * to the <code>CLOB</code> value that this <code>Clob</code> object
     * represents, at position <code>pos</code>.
     *
     * @param  pos the position at which to start writing to the
     *        <code>CLOB</code> value
     *
     * @return a stream to which Unicode encoded characters can be written
     * @exception SQLException if there is an error accessing the 
     *            <code>CLOB</code> value
     * @see #getCharacterStream
     *
     * @since 1.4
     */
    java.io.Writer setCharacterStream(long pos) throws SQLException;

    /**
     * Truncates the <code>CLOB</code> value that this <code>Clob</code> 
     * designates to have a length of <code>len</code> 
     * characters.
     * @param len the length, in bytes, to which the <code>CLOB</code> value
     *        should be truncated
     * @exception SQLException if there is an error accessing the 
     *            <code>CLOB</code> value
     *
     * @since 1.4
     */
    void truncate(long len) throws SQLException;

}

