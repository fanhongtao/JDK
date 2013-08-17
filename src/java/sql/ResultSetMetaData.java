/*
 * @(#)ResultSetMetaData.java	1.6 98/07/01
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

/**
 * A ResultSetMetaData object can be used to find out about the types 
 * and properties of the columns in a ResultSet.
 */

public interface ResultSetMetaData {

    /**
     * What's the number of columns in the ResultSet?
     *
     * @return the number
     * @exception SQLException if a database-access error occurs.
     */
	int getColumnCount() throws SQLException;

    /**
     * Is the column automatically numbered, thus read-only?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isAutoIncrement(int column) throws SQLException;

    /**
     * Does a column's case matter?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isCaseSensitive(int column) throws SQLException;	

    /**
     * Can the column be used in a where clause?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isSearchable(int column) throws SQLException;

    /**
     * Is the column a cash value?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isCurrency(int column) throws SQLException;

    /**
     * Can you put a NULL in this column?		
     *
     * @param column the first column is 1, the second is 2, ...
     * @return columnNoNulls, columnNullable or columnNullableUnknown
     * @exception SQLException if a database-access error occurs.
     */
	int isNullable(int column) throws SQLException;

    /**
     * Does not allow NULL values.
     */
    int columnNoNulls = 0;

    /**
     * Allows NULL values.
     */
    int columnNullable = 1;

    /**
     * Nullability unknown.
     */
    int columnNullableUnknown = 2;

    /**
     * Is the column a signed number?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isSigned(int column) throws SQLException;

    /**
     * What's the column's normal max width in chars?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return max width
     * @exception SQLException if a database-access error occurs.
     */
	int getColumnDisplaySize(int column) throws SQLException;

    /**
     * What's the suggested column title for use in printouts and
     * displays?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	String getColumnLabel(int column) throws SQLException;	

    /**
     * What's a column's name?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return column name
     * @exception SQLException if a database-access error occurs.
     */
	String getColumnName(int column) throws SQLException;

    /**
     * What's a column's table's schema?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return schema name or "" if not applicable
     * @exception SQLException if a database-access error occurs.
     */
	String getSchemaName(int column) throws SQLException;

    /**
     * What's a column's number of decimal digits?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return precision
     * @exception SQLException if a database-access error occurs.
     */
	int getPrecision(int column) throws SQLException;

    /**
     * What's a column's number of digits to right of the decimal point?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return scale
     * @exception SQLException if a database-access error occurs.
     */
	int getScale(int column) throws SQLException;	

    /**
     * What's a column's table name? 
     *
     * @return table name or "" if not applicable
     * @exception SQLException if a database-access error occurs.
     */
	String getTableName(int column) throws SQLException;

    /**
     * What's a column's table's catalog name?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return column name or "" if not applicable.
     * @exception SQLException if a database-access error occurs.
     */
	String getCatalogName(int column) throws SQLException;

    /**
     * What's a column's SQL type?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return SQL type
     * @exception SQLException if a database-access error occurs.
     * @see Types
     */
	int getColumnType(int column) throws SQLException;

    /**
     * What's a column's data source specific type name?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return type name
     * @exception SQLException if a database-access error occurs.
     */
	String getColumnTypeName(int column) throws SQLException;

    /**
     * Is a column definitely not writable?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isReadOnly(int column) throws SQLException;

    /**
     * Is it possible for a write on the column to succeed?
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isWritable(int column) throws SQLException;

    /**
     * Will a write on the column definitely succeed?	
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isDefinitelyWritable(int column) throws SQLException;
}
