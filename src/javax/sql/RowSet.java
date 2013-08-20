/*
 * @(#)RowSet.java	1.15 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sql;

import java.sql.*;
import java.io.*;
import java.math.*;
import java.util.*;

/**
 * The interface that adds support to the JDBC API for the
 * JavaBeans<sup><font size=-2>TM</font></sup> component model.
 * A rowset, which can be used as a JavaBeans component in
 * a visual Bean development environment, can be created and
 * configured at design time and executed at run time.  
 * <P>
 * The <code>RowSet</code>
 * interface provides a set of JavaBeans properties that allow a <code>RowSet</code>
 * instance to be configured to connect to a JDBC data source and read
 * some data from the data source.  A group of setter methods (<code>setInt</code>,
 * <code>setBytes</code>, <code>setString</code>, and so on)
 * provide a way to pass input parameters to a rowset's command property.
 * This command is the SQL query the rowset uses when it gets its data from
 * a relational database, which is generally the case.
 * <P>
 * The <code>RowSet</code>
 * interface supports JavaBeans events, allowing other components in an
 * application to be notified when an event occurs on a rowset,
 * such as a change in its value.
 * 
 * <P>The <code>RowSet</code> interface is unique in that it is intended to be
 * implemented using the rest of the JDBC API.  In other words, a
 * <code>RowSet</code> implementation is a layer of software that executes "on top"
 * of a JDBC driver.  Implementations of the <code>RowSet</code> interface can
 * be provided by anyone, including JDBC driver vendors who want to
 * provide a <code>RowSet</code> implementation as part of their JDBC products. 
 * <P>
 * A <code>RowSet</code> object may make a connection with a data source and
 * maintain that connection throughout its life cycle, in which case it is
 * called a <i>connected</i> rowset.  A rowset may also make a connection with
 * a data source, get data from it, and then close the connection. Such a rowset
 * is called a <i>disconnected</i> rowset.  A disconnected rowset may make
 * changes to its data while it is disconnected and then send the changes back
 * to the original source of the data, but it must reestablish a connection to do so.
 * <P>
 * A disconnected rowset may have a reader (a <code>RowSetReader</code> object)
 * and a writer (a <code>RowSetWriter</code> object) associated with it.
 * The reader may be implemented in many different ways to populate a rowset
 * with data, including getting data from a non-relational data source. The
 * writer can also be implemented in many different ways to propagate changes
 * made to the rowset's data back to the underlying data source.
 * <P>
 * Rowsets are easy to use.  The <code>RowSet</code> interface extends the standard
 * <code>java.sql.ResultSet</code> interface.  The <code>RowSetMetaData</code>
 * interface extends the <code>java.sql.ResultSetMetaData</code> interface.
 * Thus, developers familiar
 * with the JDBC API will have to learn a minimal number of new APIs to
 * use rowsets.  In addition, third-party software tools that work with
 * JDBC <code>ResultSet</code> objects will also easily be made to work with rowsets.
 * 
 * @since 1.4
 */

public interface RowSet extends ResultSet {
  
  //-----------------------------------------------------------------------
  // Properties 
  //-----------------------------------------------------------------------

  //-----------------------------------------------------------------------
  // The following properties may be used to create a Connection.
  //-----------------------------------------------------------------------

  /** 
   * Retrieves the url property this <code>RowSet</code> object will use to
   * create a connection if it uses the <code>DriverManager</code>
   * instead of a <code>DataSource</code> object to establish the connection.
   * The default value is <code>null</code>.
   *
   * @return a string url
   * @exception SQLException if a database access error occurs
   * @see #setUrl
   */
  String getUrl() throws SQLException;

  /**
   * Sets the URL this <code>RowSet</code> object will use when it uses the
   * <code>DriverManager</code> to create a connection.
   *
   * Setting this property is optional.  If a URL is used, a JDBC driver
   * that accepts the URL must be loaded by the application before the
   * rowset is used to connect to a database.  The rowset will use the URL
   * internally to create a database connection when reading or writing
   * data.  Either a URL or a data source name is used to create a
   * connection, whichever was specified most recently.
   * 
   * @param url a string value; may be <code>null</code>
   * @exception SQLException if a database access error occurs
   * @see #getUrl
   */
  void setUrl(String url) throws SQLException;

  /**
   * Retrieves the logical name that identifies the data source for this
   * <code>RowSet</code> object.  Users should set
   * either the url property or the data source name property.  The rowset will use
   * the property that was set more recently to get a connection.
   *
   * @return a data source name
   * @see #setDataSourceName
   * @see #setUrl
   */
  String getDataSourceName();

  /**
   * Sets the data source name property for this <code>RowSet</code> object to the 
   * given <code>String</code>.
   * <P>
   * The value of the data source name property can be used to do a lookup of
   * a <code>DataSource</code> object that has been registered with a naming
   * service.  After being retrieved, the <code>DataSource</code> object can be
   * used to create a connection to the data source that it represents.
   *
   * @param name the logical name of the data source for this <code>RowSet</code>
   *        object
   * @exception SQLException if a database access error occurs
   * @see #getDataSourceName
   */
  void setDataSourceName(String name) throws SQLException;

  /** 
   * Retrieves the username used to create a database connection for this
   * <code>RowSet</code> object.  
   * The username property is set at run time before calling the method 
   * <code>execute</code>.  It is 
   * not usually part of the serialized state of a <code>RowSet</code> object.
   *
   * @return the username property
   * @see #setUsername
   */
  String getUsername();

  /**
   * Sets the username property for this <code>RowSet</code> object to the 
   * given <code>String</code>.
   *
   * @param name a user name
   * @exception SQLException if a database access error occurs
   * @see #getUsername
   */
  void setUsername(String name) throws SQLException;

  /** 
   * Retrieves the password used to create a database connection. 
   * The password property is set at run time before calling the method
   * <code>execute</code>.  It is not usually part of the serialized state 
   * of a <code>RowSet</code> object.
   *
   * @return the password for making a database connection
   * @see #setPassword
   */
  String getPassword();

  /**
   * Sets the database password for this <code>RowSet</code> object to
   * the given <code>String</code>.
   *
   * @param password the password string
   * @exception SQLException if a database access error occurs
   * @see #getPassword
   */
  void setPassword(String password) throws SQLException;

  /** 
   * Retrieves the transaction isolation level set for this
   * <code>RowSet</code> object.
   *
   * @return the transaction isolation level; one of
   *      <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
   *      <code>Connection.TRANSACTION_READ_COMMITTED</code>,
   *      <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
   *      <code>Connection.TRANSACTION_SERIALIZABLE</code>
   * @see #setTransactionIsolation
   */
  int getTransactionIsolation();

  /**
   * Sets the transaction isolation level for this <code>RowSet</code> obejct.
   *
   * @param level the transaction isolation level; one of
   *      <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
   *      <code>Connection.TRANSACTION_READ_COMMITTED</code>,
   *      <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
   *      <code>Connection.TRANSACTION_SERIALIZABLE</code>
   * @exception SQLException if a database access error occurs
   * @see #getTransactionIsolation
   */
  void setTransactionIsolation(int level) throws SQLException;

  /**
   * Retrieves the <code>Map</code> object associated with this
   * <code>RowSet</code> object, which specifies the custom mapping
   * of SQL user-defined types, if any.  The default is for the
   * type map to be empty.
   *
   * @return a <code>java.util.Map</code> object containing the names of
   *         SQL user-defined types and the Java classes to which they are
   *         to be mapped
   *
   * @exception SQLException if a database access error occurs
   * @see #setTypeMap
   */
  java.util.Map<String,Class<?>> getTypeMap() throws SQLException;

  /**
   * Installs the given <code>java.util.Map</code> object as the default 
   * type map for this <code>RowSet</code> object. This type map will be
   * used unless another type map is supplied as a method parameter.
   *
   * @param map  a <code>java.util.Map</code> object containing the names of
   *         SQL user-defined types and the Java classes to which they are
   *         to be mapped
   * @exception SQLException if a database access error occurs
   * @see #getTypeMap
   */
  void setTypeMap(java.util.Map<String,Class<?>> map) throws SQLException;

  //-----------------------------------------------------------------------
  // The following properties may be used to create a Statement.
  //-----------------------------------------------------------------------

  /** 
   * Retrieves this <code>RowSet</code> object's command property.
   *
   * The command property contains a command string, which must be an SQL
   * query, that can be executed to fill the rowset with data.  
   * The default value is <code>null</code>.
   *
   * @return the command string; may be <code>null</code>
   * @see #setCommand
   */
  String getCommand();

  /**
   * Sets this <code>RowSet</code> object's command property to the given
   * SQL query.
   *
   * This property is optional
   * when a rowset gets its data from a data source that does not support
   * commands, such as a spreadsheet. 
   *
   * @param cmd the SQL query that will be used to get the data for this
   *        <code>RowSet</code> object; may be <code>null</code>
   * @exception SQLException if a database access error occurs
   * @see #getCommand
   */
  void setCommand(String cmd) throws SQLException;

  /** 
   * Retrieves whether this <code>RowSet</code> object is read-only.
   * If updates are possible, the default is for a rowset to be
   * updatable.
   * <P>
   * Attempts to update a read-only rowset will result in an
   * <code>SQLException</code> being thrown. 
   *
   * @return <code>true</code> if this <code>RowSet</code> object is
   *         read-only; <code>false</code> if it is updatable 
   * @see #setReadOnly
   */
  boolean isReadOnly();

  /**
   * Sets whether this <code>RowSet</code> object is read-only to the
   * given <code>boolean</code>.
   *
   * @param value <code>true</code> if read-only; <code>false</code> if 
   *        updatable
   * @exception SQLException if a database access error occurs
   * @see #isReadOnly
   */
  void setReadOnly(boolean value) throws SQLException;

  /**
   * Retrieves the maximum number of bytes that may be returned 
   * for certain column values. 
   * This limit applies only to <code>BINARY</code>,
   * <code>VARBINARY</code>, <code>LONGVARBINARYBINARY</code>, <code>CHAR</code>,
   * <code>VARCHAR</code>, and <code>LONGVARCHAR</code> columns.
   * If the limit is exceeded, the excess data is silently discarded.
   *
   * @return the current maximum column size limit; zero means that there
   *          is no limit
   * @exception SQLException if a database access error occurs
   * @see #setMaxFieldSize
   */
  int getMaxFieldSize() throws SQLException;
    
  /**
   * Sets the maximum number of bytes that can be returned for a column
   * value to the given number of bytes.
   * This limit applies only to <code>BINARY</code>,
   * <code>VARBINARY</code>, <code>LONGVARBINARYBINARY</code>, <code>CHAR</code>,
   * <code>VARCHAR</code>, and <code>LONGVARCHAR</code> columns.
   * If the limit is exceeded, the excess data is silently discarded.
   * For maximum portability, use values greater than 256.
   *
   * @param max the new max column size limit in bytes; zero means unlimited 
   * @exception SQLException if a database access error occurs
   * @see #getMaxFieldSize
   */
  void setMaxFieldSize(int max) throws SQLException;

  /**
   * Retrieves the maximum number of rows that this <code>RowSet</code>
   * object can contain.
   * If the limit is exceeded, the excess rows are silently dropped.
   *
   * @return the current maximum number of rows that this <code>RowSet</code>
   *         object can contain; zero means unlimited
   * @exception SQLException if a database access error occurs
   * @see #setMaxRows
   */
  int getMaxRows() throws SQLException;

  /**
   * Sets the maximum number of rows that this <code>RowSet</code>
   * object can contain to the specified number.
   * If the limit is exceeded, the excess rows are silently dropped.
   *
   * @param max the new maximum number of rows; zero means unlimited 
   * @exception SQLException if a database access error occurs
   * @see #getMaxRows
   */
  void setMaxRows(int max) throws SQLException;

  /**
   * Retrieves whether escape processing is enabled for this
   * <code>RowSet</code> object.  
   * If escape scanning is enabled, which is the default, the driver will do
   * escape substitution before sending an SQL statement to the database.
   *
   * @return <code>true</code> if escape processing is enabled; 
   *         <code>false</code> if it is disabled
   * @exception SQLException if a database access error occurs
   * @see #setEscapeProcessing
   */
  boolean getEscapeProcessing() throws SQLException;

  /**
   * Sets escape processing for this <code>RowSet</code> object on or
   * off. If escape scanning is on (the default), the driver will do
   * escape substitution before sending an SQL statement to the database.
   *
   * @param enable <code>true</code> to enable escape processing;
   *        <code>false</code> to disable it
   * @exception SQLException if a database access error occurs
   * @see #getEscapeProcessing
   */
  void setEscapeProcessing(boolean enable) throws SQLException;

  /**
   * Retrieves the maximum number of seconds the driver will wait for
   * a statement to execute.
   * If this limit is exceeded, an <code>SQLException</code> is thrown.
   *
   * @return the current query timeout limit in seconds; zero means 
   *          unlimited 
   * @exception SQLException if a database access error occurs
   * @see #setQueryTimeout
   */
  int getQueryTimeout() throws SQLException;

  /**
   * Sets the maximum time the driver will wait for
   * a statement to execute to the given number of seconds.
   * If this limit is exceeded, an <code>SQLException</code> is thrown.
   *
   * @param seconds the new query timeout limit in seconds; zero means 
   *        that there is no limit
   * @exception SQLException if a database access error occurs
   * @see #getQueryTimeout
   */
  void setQueryTimeout(int seconds) throws SQLException;

  /**
   * Sets the type of this <code>RowSet</code> object to the given type.
   * This method is used to change the type of a rowset, which is by
   * default read-only and non-scrollable.
   *
   * @param type one of the <code>ResultSet</code> constants specifying a type:
   *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
   *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
   *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
   * @exception SQLException if a database access error occurs
   * @see java.sql.ResultSet#getType
   */
  void setType(int type) throws SQLException;

  /**
   * Sets the concurrency of this <code>RowSet</code> object to the given
   * concurrency level. This method is used to change the concurrency level
   * of a rowset, which is by default <code>ResultSet.CONCUR_READ_ONLY</code>
   *
   * @param concurrency one of the <code>ResultSet</code> constants specifying a
   *        concurrency level:  <code>ResultSet.CONCUR_READ_ONLY</code> or
   *        <code>ResultSet.CONCUR_UPDATABLE</code>
   * @exception SQLException if a database access error occurs
   * @see ResultSet#getConcurrency
   */
  void setConcurrency(int concurrency) throws SQLException;
  
  //-----------------------------------------------------------------------
  // Parameters
  //-----------------------------------------------------------------------

  /** 
   * The <code>RowSet</code> setter methods are used to set any input parameters
   * needed by the <code>RowSet</code> object's command.
   * Parameters are set at run time, as opposed to design time.
   */

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's SQL
   * command to SQL <code>NULL</code>.
   *
   * <P><B>Note:</B> You must specify the parameter's SQL type.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param sqlType a SQL type code defined by <code>java.sql.Types</code>
   * @exception SQLException if a database access error occurs
   */
  void setNull(int parameterIndex, int sqlType) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's SQL
   * command to SQL <code>NULL</code>. This version of the method <code>setNull</code>
   * should  be used for SQL user-defined types (UDTs) and <code>REF</code> type
   * parameters.  Examples of UDTs include: <code>STRUCT</code>, <code>DISTINCT</code>,
   * <code>JAVA_OBJECT</code>, and named array types.
   *
   * <P><B>Note:</B> To be portable, applications must give the
   * SQL type code and the fully qualified SQL type name when specifying
   * a NULL UDT or <code>REF</code> parameter.  In the case of a UDT,
   * the name is the type name of the parameter itself.  For a <code>REF</code> 
   * parameter, the name is the type name of the referenced type.  If 
   * a JDBC driver does not need the type code or type name information, 
   * it may ignore it.     
   *
   * Although it is intended for UDT and <code>REF</code> parameters,
   * this method may be used to set a null parameter of any JDBC type.
   * If the parameter does not have a user-defined or <code>REF</code> type,
   * the typeName parameter is ignored.
   *
   *
   * @param paramIndex the first parameter is 1, the second is 2, ...
   * @param sqlType a value from <code>java.sql.Types</code>
   * @param typeName the fully qualified name of an SQL UDT or the type
   *        name of the SQL structured type being referenced by a <code>REF</code>
   *        type; ignored if the parameter is not a UDT or <code>REF</code> type
   * @exception SQLException if a database access error occurs
   */
  void setNull (int paramIndex, int sqlType, String typeName) 
    throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>boolean</code> value. The driver converts this to
   * an SQL <code>BIT</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setBoolean(int parameterIndex, boolean x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>byte</code> value. The driver converts this to
   * an SQL <code>TINYINT</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setByte(int parameterIndex, byte x) throws SQLException;
  
  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>short</code> value. The driver converts this to
   * an SQL <code>SMALLINT</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setShort(int parameterIndex, short x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>int</code> value. The driver converts this to
   * an SQL <code>INTEGER</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setInt(int parameterIndex, int x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>long</code> value. The driver converts this to
   * an SQL <code>BIGINT</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setLong(int parameterIndex, long x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>float</code> value. The driver converts this to
   * an SQL <code>REAL</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setFloat(int parameterIndex, float x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>double</code> value. The driver converts this to
   * an SQL <code>DOUBLE</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setDouble(int parameterIndex, double x) throws SQLException;
  
  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.math.BigDeciaml</code> value.
   * The driver converts this to
   * an SQL <code>NUMERIC</code> value before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java <code>String</code> value. Before sending it to the 
   * database, the driver converts this to an SQL <code>VARCHAR</code> or
   * <code>LONGVARCHAR</code> value, depending on the argument's size relative
   * to the driver's limits on <code>VARCHAR</code> values.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setString(int parameterIndex, String x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given Java array of <code>byte</code> values. Before sending it to the 
   * database, the driver converts this to an SQL <code>VARBINARY</code> or
   * <code>LONGVARBINARY</code> value, depending on the argument's size relative
   * to the driver's limits on <code>VARBINARY</code> values.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value 
   * @exception SQLException if a database access error occurs
   */
  void setBytes(int parameterIndex, byte x[]) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.sql.Date</code> value. The driver converts this to
   * an SQL <code>DATE</code> value before sending it to the database, using the
   * default <code>java.util.Calendar</code> to calculate the date.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setDate(int parameterIndex, java.sql.Date x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.sql.Time</code> value. The driver converts this to
   * an SQL <code>TIME</code> value before sending it to the database, using the
   * default <code>java.util.Calendar</code> to calculate it.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @exception SQLException if a database access error occurs
   */
  void setTime(int parameterIndex, java.sql.Time x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.sql.Timestamp</code> value. The driver converts this to
   * an SQL <code>TIMESTAMP</code> value before sending it to the database, using the
   * default <code>java.util.Calendar</code> to calculate it.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value 
   * @exception SQLException if a database access error occurs
   */
  void setTimestamp(int parameterIndex, java.sql.Timestamp x)
    throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.io.InputStream</code> value. 
   * It may be more practical to send a very large ASCII value via a
   * <code>java.io.InputStream</code> rather than as a <code>LONGVARCHAR</code> 
   * parameter. The driver will read the data from the stream
   * as needed until it reaches end-of-file.   
   * 
   * <P><B>Note:</B> This stream object can either be a standard
   * Java stream object or your own subclass that implements the
   * standard interface.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the Java input stream that contains the ASCII parameter value
   * @param length the number of bytes in the stream 
   * @exception SQLException if a database access error occurs
   */
  void setAsciiStream(int parameterIndex, java.io.InputStream x, int length)
    throws SQLException;
  
  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.io.InputStream</code> value. 
   * It may be more practical to send a very large binary value via a
   * <code>java.io.InputStream</code> rather than as a <code>LONGVARBINARY</code> 
   * parameter. The driver will read the data from the stream
   * as needed until it reaches end-of-file.   
   * 
   * <P><B>Note:</B> This stream object can either be a standard
   * Java stream object or your own subclass that implements the
   * standard interface.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the java input stream which contains the binary parameter value
   * @param length the number of bytes in the stream 
   * @exception SQLException if a database access error occurs
   */
  void setBinaryStream(int parameterIndex, java.io.InputStream x, 
		       int length) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * to the given <code>java.io.Reader</code> value. 
   * It may be more practical to send a very large UNICODE value via a
   * <code>java.io.Reader</code> rather than as a <code>LONGVARCHAR</code> 
   * parameter. The driver will read the data from the stream
   * as needed until it reaches end-of-file.   
   * 
   * <P><B>Note:</B> This stream object can either be a standard
   * Java stream object or your own subclass that implements the
   * standard interface.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param reader the <code>Reader</code> object that contains the UNICODE data
   *        to be set
   * @param length the number of characters in the stream 
   * @exception SQLException if a database access error occurs
   */
  void setCharacterStream(int parameterIndex,
       			  Reader reader,
			  int length) throws SQLException;
  
  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given Java <code>Object</code>.  For integral values, the 
   * <code>java.lang</code> equivalent objects should be used (for example,
   * an instance of the class <code>Integer</code> for an <code>int</code>).
   *
   * <p>The given Java object will be converted to the targetSqlType
   * before being sent to the database.
   * <P>
   * If the object is of a class implementing <code>SQLData</code>,
   * the rowset should call the method <code>SQLData.writeSQL</code>
   * to write the object to an <code>SQLOutput</code> data stream.
   * If the object is an instance of a class implementing the <code>Ref</code>,
   * <code>Struct</code>, <code>Array</code>, <code>Blob</code>,
   * or <code>Clob</code> interfaces,
   * the driver uses the default mapping to the corresponding SQL type.
   *
   * <p>Note that this method may be used to pass datatabase-specific
   * abstract data types. 
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the object containing the input parameter value
   * @param targetSqlType the SQL type (as defined in <code>java.sql.Types</code>) 
   *        to be sent to the database. The scale argument may further qualify this
   *        type.
   * @param scale for <code>java.sql.Types.DECIMAL</code> or
   *        <code>java.sql.Types.NUMERIC</code> types, this is the number of
   *         digits after the decimal point.  For all other types, this value
   *         will be ignored.
   * @exception SQLException if a database access error occurs
   * @see java.sql.Types 
   */
  void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
            throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with a Java <code>Object</code>.  For integral values, the 
   * <code>java.lang</code> equivalent objects should be used.
   * This method is like <code>setObject</code> above, but the scale used is the scale
   * of the second parameter.  Scalar values have a scale of zero.  Literal
   * values have the scale present in the literal.  
   * <P>
   * Even though it is supported, it is not recommended that this method
   * be called with floating point input values.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the object containing the input parameter value
   * @param targetSqlType the SQL type (as defined in <code>java.sql.Types</code>) 
   *        to be sent to the database
   * @exception SQLException if a database access error occurs
   */
  void setObject(int parameterIndex, Object x, 
		 int targetSqlType) throws SQLException;
  
  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with a Java <code>Object</code>.  For integral values, the 
   * <code>java.lang</code> equivalent objects should be used.
   *
   * <p>The JDBC specification provides a standard mapping from
   * Java Object types to SQL types.  The driver will convert the 
   * given Java object to its standard SQL mapping before sending it
   * to the database.
   *
   * <p>Note that this method may be used to pass datatabase-specific
   * abstract data types by using a driver-specific Java type.
   *
   * If the object is of a class implementing <code>SQLData</code>,
   * the rowset should call the method <code>SQLData.writeSQL</code>
   * to write the object to an <code>SQLOutput</code> data stream.
   * If the object is an instance of a class implementing the <code>Ref</code>,
   * <code>Struct</code>, <code>Array</code>, <code>Blob</code>,
   * or <code>Clob</code> interfaces,
   * the driver uses the default mapping to the corresponding SQL type.
   * <P>
   * An exception is thrown if there is an ambiguity, for example, if the
   * object is of a class implementing more than one of these interfaces.
   *
   * @param parameterIndex The first parameter is 1, the second is 2, ...
   * @param x The object containing the input parameter value 
   * @exception SQLException if a database access error occurs
   */
  void setObject(int parameterIndex, Object x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>Ref</code> value.  The driver will convert this
   * to the appropriate <code>REF(&lt;structured-type&gt;)</code> value.
   *
   * @param i the first parameter is 1, the second is 2, ...
   * @param x an object representing data of an SQL <code>REF</code> type
   * @exception SQLException if a database access error occurs
   */
  void setRef (int i, Ref x) throws SQLException;

  /** 
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>Blob</code> value.  The driver will convert this
   * to the <code>BLOB</code> value that the <code>Blob</code> object
   * represents before sending it to the database.
   *
   * @param i the first parameter is 1, the second is 2, ...
   * @param x an object representing a BLOB
   * @exception SQLException if a database access error occurs
   */
  void setBlob (int i, Blob x) throws SQLException;

  /** 
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>Clob</code> value.  The driver will convert this
   * to the <code>CLOB</code> value that the <code>Clob</code> object
   * represents before sending it to the database.
   *
   * @param i the first parameter is 1, the second is 2, ...
   * @param x an object representing a CLOB
   * @exception SQLException if a database access error occurs
   */
  void setClob (int i, Clob x) throws SQLException;
  
  /** 
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>Array</code> value.  The driver will convert this
   * to the <code>ARRAY</code> value that the <code>Array</code> object
   * represents before sending it to the database.
   *
   * @param i the first parameter is 1, the second is 2, ...
   * @param x an object representing an SQL array
   * @exception SQLException if a database access error occurs
   */
  void setArray (int i, Array x) throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>java.sql.Date</code> value.  The driver will convert this
   * to an SQL <code>DATE</code> value, using the given <code>java.util.Calendar</code>
   * object to calculate the date.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @param cal the <code>java.util.Calendar</code> object to use for calculating the date
   * @exception SQLException if a database access error occurs
   */
  void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
    throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>java.sql.Time</code> value.  The driver will convert this
   * to an SQL <code>TIME</code> value, using the given <code>java.util.Calendar</code>
   * object to calculate it, before sending it to the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value
   * @param cal the <code>java.util.Calendar</code> object to use for calculating the time
   * @exception SQLException if a database access error occurs
   */
  void setTime(int parameterIndex, java.sql.Time x, Calendar cal) 
    throws SQLException;

  /**
   * Sets the designated parameter in this <code>RowSet</code> object's command
   * with the given  <code>java.sql.Timestamp</code> value.  The driver will
   * convert this to an SQL <code>TIMESTAMP</code> value, using the given
   * <code>java.util.Calendar</code> object to calculate it, before sending it to the
   * database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param x the parameter value 
   * @param cal the <code>java.util.Calendar</code> object to use for calculating the 
   *        timestamp
   * @exception SQLException if a database access error occurs
   */
  void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal)
    throws SQLException;

  /**
   * Clears the parameters set for this <code>RowSet</code> object's command.
   * <P>In general, parameter values remain in force for repeated use of a
   * <code>RowSet</code> object. Setting a parameter value automatically clears its
   * previous value.  However, in some cases it is useful to immediately
   * release the resources used by the current parameter values, which can
   * be done by calling the method <code>clearParameters</code>.
   *
   * @exception SQLException if a database access error occurs
   */
  void clearParameters() throws SQLException;

  //---------------------------------------------------------------------
  // Reading and writing data
  //---------------------------------------------------------------------

  /**
   * Fills this <code>RowSet</code> object with data.  
   * <P>
   * The <code>execute</code> method may use the following properties 
   * to create a connection for reading data: url, data source name, 
   * user name, password, transaction isolation, and type map.
   * 
   * The <code>execute</code> method  may use the following properties 
   * to create a statement to execute a command:
   * command, read only, maximum field size, 
   * maximum rows, escape processing, and query timeout.
   * <P>
   * If the required properties have not been set, an exception is 
   * thrown.  If this method is successful, the current contents of the rowset are 
   * discarded and the rowset's metadata is also (re)set.  If there are 
   * outstanding updates, they are ignored.   
   * <P>
   * If this <code>RowSet</code> object does not maintain a continuous connection 
   * with its source of data, it may use a reader (a <code>RowSetReader</code> 
   * object) to fill itself with data.  In this case, a reader will have been
   * registered with this <code>RowSet</code> object, and the method 
   * <code>execute</code> will call on the reader's <code>readData</code>
   * method as part of its implementation.
   *
   * @exception SQLException if a database access error occurs or any of the
   *            properties necessary for making a connection and creating
   *            a statement have not been set
   */
  void execute() throws SQLException;

  //--------------------------------------------------------------------
  // Events
  //--------------------------------------------------------------------

  /**
   * Registers the given listener so that it will be notified of events
   * that occur on this <code>RowSet</code> object.
   *
   * @param listener a component that has implemented the <code>RowSetListener</code>
   *        interface and wants to be notified when events occur on this
   *        <code>RowSet</code> object
   * @see #removeRowSetListener
   */
  void addRowSetListener(RowSetListener listener);

  /**
   * Removes the specified listener from the list of components that will be
   * notified when an event occurs on this <code>RowSet</code> object.
   *
   * @param listener a component that has been registered as a listener for this
   *        <code>RowSet</code> object
   * @see #addRowSetListener
   */
  void removeRowSetListener(RowSetListener listener);
 
}









