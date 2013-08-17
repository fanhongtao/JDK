/*
 * @(#)CallableStatement.java	1.8 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

import java.math.BigDecimal;

/**
 * <P>CallableStatement is used to execute SQL stored procedures.
 *
 * <P>JDBC provides a stored procedure SQL escape that allows stored
 * procedures to be called in a standard way for all RDBMS's. This
 * escape syntax has one form that includes a result parameter and one
 * that does not. If used, the result parameter must be registered as
 * an OUT parameter. The other parameters may be used for input,
 * output or both. Parameters are refered to sequentially, by
 * number. The first parameter is 1.
 *
 * <P><CODE>
 * {?= call <procedure-name>[<arg1>,<arg2>, ...]}<BR>
 * {call <procedure-name>[<arg1>,<arg2>, ...]}
 * </CODE>
 *    
 * <P>IN parameter values are set using the set methods inherited from
 * PreparedStatement. The type of all OUT parameters must be
 * registered prior to executing the stored procedure; their values
 * are retrieved after execution via the get methods provided here.
 *
 * <P>A Callable statement may return a ResultSet or multiple
 * ResultSets. Multiple ResultSets are handled using operations
 * inherited from Statement.
 *
 * <P>For maximum portability, a call's ResultSets and update counts
 * should be processed prior to getting the values of output
 * parameters.
 *
 * @see Connection#prepareCall
 * @see ResultSet 
 */
public interface CallableStatement extends PreparedStatement {

    /**
     * Before executing a stored procedure call, you must explicitly
     * call registerOutParameter to register the java.sql.Type of each
     * out parameter.
     *
     * <P><B>Note:</B> When reading the value of an out parameter, you
     * must use the getXXX method whose Java type XXX corresponds to the
     * parameter's registered SQL type.
     *
     * @param parameterIndex the first parameter is 1, the second is 2,...
     * @param sqlType SQL type code defined by java.sql.Types;
     * for parameters of type Numeric or Decimal use the version of
     * registerOutParameter that accepts a scale value
     * @exception SQLException if a database-access error occurs.
     * @see Type 
     */
    void registerOutParameter(int parameterIndex, int sqlType)
	    throws SQLException;

    /**
     * Use this version of registerOutParameter for registering
     * Numeric or Decimal out parameters.
     *
     * <P><B>Note:</B> When reading the value of an out parameter, you
     * must use the getXXX method whose Java type XXX corresponds to the
     * parameter's registered SQL type.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param sqlType use either java.sql.Type.NUMERIC or java.sql.Type.DECIMAL
     * @param scale a value greater than or equal to zero representing the 
     *              desired number of digits to the right of the decimal point
     * @exception SQLException if a database-access error occurs.
     * @see Type 
     */
    void registerOutParameter(int parameterIndex, int sqlType, int scale)
	    throws SQLException;

    /**
     * An OUT parameter may have the value of SQL NULL; wasNull reports 
     * whether the last value read has this special value.
     *
     * <P><B>Note:</B> You must first call getXXX on a parameter to
     * read its value and then call wasNull() to see if the value was
     * SQL NULL.
     *
     * @return true if the last parameter read was SQL NULL 
     * @exception SQLException if a database-access error occurs.
     */
    boolean wasNull() throws SQLException;

    /**
     * Get the value of a CHAR, VARCHAR, or LONGVARCHAR parameter as a Java String.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is null
     * @exception SQLException if a database-access error occurs.
     */
    String getString(int parameterIndex) throws SQLException;

    /**
     * Get the value of a BIT parameter as a Java boolean.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is false
     * @exception SQLException if a database-access error occurs.
     */
    boolean getBoolean(int parameterIndex) throws SQLException;

    /**
     * Get the value of a TINYINT parameter as a Java byte.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is 0
     * @exception SQLException if a database-access error occurs.
     */
    byte getByte(int parameterIndex) throws SQLException;

    /**
     * Get the value of a SMALLINT parameter as a Java short.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is 0
     * @exception SQLException if a database-access error occurs.
     */
    short getShort(int parameterIndex) throws SQLException;

    /**
     * Get the value of an INTEGER parameter as a Java int.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is 0
     * @exception SQLException if a database-access error occurs.
     */
    int getInt(int parameterIndex) throws SQLException;

    /**
     * Get the value of a BIGINT parameter as a Java long.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is 0
     * @exception SQLException if a database-access error occurs.
     */
    long getLong(int parameterIndex) throws SQLException;

    /**
     * Get the value of a FLOAT parameter as a Java float.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is 0
     * @exception SQLException if a database-access error occurs.
     */
    float getFloat(int parameterIndex) throws SQLException;

    /**
     * Get the value of a DOUBLE parameter as a Java double.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is 0
     * @exception SQLException if a database-access error occurs.
     */
    double getDouble(int parameterIndex) throws SQLException;

    /** 
     * Get the value of a NUMERIC parameter as a java.math.BigDecimal object.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     *
     * @param scale a value greater than or equal to zero representing the
     * desired number of digits to the right of the decimal point 
     *
     * @return the parameter value; if the value is SQL NULL, the result is
     * null 
     * @exception SQLException if a database-access error occurs.
     */
    BigDecimal getBigDecimal(int parameterIndex, int scale) 
    throws SQLException;

    /**
     * Get the value of a SQL BINARY or VARBINARY parameter as a Java byte[]
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is null
     * @exception SQLException if a database-access error occurs.
     */
    byte[] getBytes(int parameterIndex) throws SQLException;

    /**
     * Get the value of a SQL DATE parameter as a java.sql.Date object
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is null
     * @exception SQLException if a database-access error occurs.
     */
    java.sql.Date getDate(int parameterIndex) throws SQLException;

    /**
     * Get the value of a SQL TIME parameter as a java.sql.Time object.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is null
     * @exception SQLException if a database-access error occurs.
     */
    java.sql.Time getTime(int parameterIndex) throws SQLException;

    /**
     * Get the value of a SQL TIMESTAMP parameter as a java.sql.Timestamp object.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @return the parameter value; if the value is SQL NULL, the result is null
     * @exception SQLException if a database-access error occurs.
     */
    java.sql.Timestamp getTimestamp(int parameterIndex) 
	    throws SQLException;

    //----------------------------------------------------------------------
    // Advanced features:


    /**
     * Get the value of a parameter as a Java object.
     *
     * <p>This method returns a Java object whose type coresponds to the SQL
     * type that was registered for this parameter using registerOutParameter.
     *
     * <p>Note that this method may be used to read
     * datatabase-specific, abstract data types. This is done by
     * specifying a targetSqlType of java.sql.types.OTHER, which
     * allows the driver to return a database-specific Java type.
     *
     * @param parameterIndex The first parameter is 1, the second is 2, ...
     * @return A java.lang.Object holding the OUT parameter value.
     * @exception SQLException if a database-access error occurs.
     * @see Types 
     */
    Object getObject(int parameterIndex) throws SQLException;

}

