/*
 * @(#)SQLInput.java	1.24 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;
 
/**
 * An input stream that contains a stream of values representing an 
 * instance of an SQL structured type or an SQL distinct type.
 * This interface, used only for custom mapping, is used by the driver
 * behind the scenes, and a programmer never directly invokes
 * <code>SQLInput</code> methods. The <i>reader</i> methods 
 * (<code>readLong</code>, <code>readBytes</code>, and so on) 
 * provide a way to read the values in an <code>SQLInput</code> object.
 * The method <code>wasNull</code> is used to determine whether the 
 * the last value read was SQL <code>NULL</code>.
 * <P>When the method <code>getObject</code> is called with an
 * object of a class implementing the interface <code>SQLData</code>,
 * the JDBC driver calls the method <code>SQLData.getSQLType</code>
 * to determine the SQL type of the user-defined type (UDT)
 * being custom mapped. The driver
 * creates an instance of <code>SQLInput</code>, populating it with the
 * attributes of the UDT.  The driver then passes the input
 * stream to the method <code>SQLData.readSQL</code>, which in turn 
 * calls the <code>SQLInput</code> reader methods 
 * in its implementation for reading the
 * attributes from the input stream.
 * @since 1.2
 */

public interface SQLInput {
  

    //================================================================
    // Methods for reading attributes from the stream of SQL data.
    // These methods correspond to the column-accessor methods of
    // java.sql.ResultSet.
    //================================================================

    /**
     * Reads the next attribute in the stream and returns it as a <code>String</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    String readString() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>boolean</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>false</code>
     * @exception SQLException if a database access error occurs
     */
    boolean readBoolean() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>byte</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>0</code>
     * @exception SQLException if a database access error occurs
     */
    byte readByte() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>short</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>0</code>
     * @exception SQLException if a database access error occurs
     */
    short readShort() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as an <code>int</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>0</code>
     * @exception SQLException if a database access error occurs
     */
    int readInt() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>long</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>0</code>
     * @exception SQLException if a database access error occurs
     */
    long readLong() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>float</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>0</code>
     * @exception SQLException if a database access error occurs
     */
    float readFloat() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>double</code> 
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>0</code>
     * @exception SQLException if a database access error occurs
     */
    double readDouble() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>java.math.BigDecimal</code> 
     * object in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.math.BigDecimal readBigDecimal() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as an array of bytes
     * in the Java programming language.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    byte[] readBytes() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>java.sql.Date</code> object.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.sql.Date readDate() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>java.sql.Time</code> object.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.sql.Time readTime() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a <code>java.sql.Timestamp</code> object.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.sql.Timestamp readTimestamp() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a stream of Unicode characters.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.io.Reader readCharacterStream() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a stream of ASCII characters.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.io.InputStream readAsciiStream() throws SQLException;

    /**
     * Reads the next attribute in the stream and returns it as a stream of uninterpreted
     * bytes.
     *
     * @return the attribute; if the value is SQL <code>NULL</code>, returns <code>null</code>
     * @exception SQLException if a database access error occurs
     */
    java.io.InputStream readBinaryStream() throws SQLException;
  
    //================================================================
    // Methods for reading items of SQL user-defined types from the stream.
    //================================================================

    /**
     * Reads the datum at the head of the stream and returns it as an
     * <code>Object</code> in the Java programming language.  The
     * actual type of the object returned is determined by the default type
     * mapping, and any customizations present in this stream's type map.
     *
     * <P>A type map is registered with the stream by the JDBC driver before the
     * stream is passed to the application.
     *
     * <P>When the datum at the head of the stream is an SQL <code>NULL</code>, 
     * the method returns <code>null</code>.  If the datum is an SQL structured or distinct
     * type, it determines the SQL type of the datum at the head of the stream. 
     * If the stream's type map has an entry for that SQL type, the driver
     * constructs an object of the appropriate class and calls the method 
     * <code>SQLData.readSQL</code> on that object, which reads additional data from the 
     * stream, using the protocol described for that method.
     *
     * @return the datum at the head of the stream as an <code>Object</code> in the
     * Java programming language;<code>null</code> if the datum is SQL <code>NULL</code>
     * @exception SQLException if a database access error occurs
     */
    Object readObject() throws SQLException;

    /**
     * Reads an SQL <code>REF</code> value from the stream and returns it as a
     * <code>Ref</code> object in the Java programming language.
     *
     * @return a <code>Ref</code> object representing the SQL <code>REF</code> value
     * at the head of the stream; <code>null</code> if the value read is 
     * SQL <code>NULL</code>
     * @exception SQLException if a database access error occurs
     */
    Ref readRef() throws SQLException;

    /**
     * Reads an SQL <code>BLOB</code> value from the stream and returns it as a
     * <code>Blob</code> object in the Java programming language.
     *
     * @return a <code>Blob</code> object representing data of the SQL <code>BLOB</code> value
     * at the head of the stream; <code>null</code> if the value read is 
     * SQL <code>NULL</code>
     * @exception SQLException if a database access error occurs
     */
    Blob readBlob() throws SQLException;

    /**
     * Reads an SQL <code>CLOB</code> value from the stream and returns it as a
     * <code>Clob</code> object in the Java programming language.
     *
     * @return a <code>Clob</code> object representing data of the SQL <code>CLOB</code> value
     * at the head of the stream; <code>null</code> if the value read is 
     * SQL <code>NULL</code>
     * @exception SQLException if a database access error occurs
     */
    Clob readClob() throws SQLException;

    /**
     * Reads an SQL <code>ARRAY</code> value from the stream and returns it as an
     * <code>Array</code> object in the Java programming language.
     *
     * @return an <code>Array</code> object representing data of the SQL
     * <code>ARRAY</code> value at the head of the stream; <code>null</code>
     * if the value read is SQL <code>NULL</code>
     * @exception SQLException if a database access error occurs
     */
    Array readArray() throws SQLException;

    /**
     * Retrieves whether the last value read was SQL <code>NULL</code>.
     * 
     * @return <code>true</code> if the most recently read SQL value was SQL
     * <code>NULL</code>; <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     * 
     */
    boolean wasNull() throws SQLException;

    //---------------------------- JDBC 3.0 -------------------------
    
    /**
     * Reads an SQL <code>DATALINK</code> value from the stream and returns it as a
     * <code>java.net.URL</code> object in the Java programming language.
     * 
     * @return a <code>java.net.URL</code> object.
     * @exception SQLException if a database access error occurs,
     *            or if a URL is malformed
     * @since 1.4
     */
    java.net.URL readURL() throws SQLException;

}
