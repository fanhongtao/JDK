/*
 * @(#)ResultSetMetaData.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.sql;

/**
 * An object that can be used to find out about the types 
 * and properties of the columns in a ResultSet.
 */

public interface ResultSetMetaData {

    /**
     * Returns the number of columns in this ResultSet.
     *
     * @return the number of columns
     * @exception SQLException if a database access error occurs
     */
	int getColumnCount() throws SQLException;

    /**
     * Indicates whether the column is automatically numbered, thus read-only.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isAutoIncrement(int column) throws SQLException;

    /**
     * Indicates whether a column's case matters.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isCaseSensitive(int column) throws SQLException;	

    /**
     * Indicates whether the column can be used in a where clause.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isSearchable(int column) throws SQLException;

    /**
     * Indicates whether the column is a cash value.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isCurrency(int column) throws SQLException;

    /**
     * Indicates the nullability of values in the designated column.		
     *
     * @param column the first column is 1, the second is 2, ...
     * @return the nullability status of the given column; one of columnNoNulls,
	 *          columnNullable or columnNullableUnknown
     * @exception SQLException if a database access error occurs
     */
	int isNullable(int column) throws SQLException;

    /**
     * Column does not allow NULL values.
     */
    int columnNoNulls = 0;

    /**
     * Column allows NULL values.
     */
    int columnNullable = 1;

    /**
     * Nullability of column values is unknown.
     */
    int columnNullableUnknown = 2;

    /**
     * Indicates whether values in the column are signed numbers.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isSigned(int column) throws SQLException;

    /**
     * Indicates the column's normal max width in chars.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return the normal maximum number of characters allowed as the width
	 *          of the designated column
     * @exception SQLException if a database access error occurs
     */
	int getColumnDisplaySize(int column) throws SQLException;

    /**
     * Gets the suggested column title for use in printouts and
     * displays.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return the suggested column title
     * @exception SQLException if a database access error occurs
     */
	String getColumnLabel(int column) throws SQLException;	

    /**
     * Gets a column's name.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return column name
     * @exception SQLException if a database access error occurs
     */
	String getColumnName(int column) throws SQLException;

    /**
     * Gets a column's table's schema.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return schema name or "" if not applicable
     * @exception SQLException if a database access error occurs
     */
	String getSchemaName(int column) throws SQLException;

    /**
     * Gets a column's number of decimal digits.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return precision
     * @exception SQLException if a database access error occurs
     */
	int getPrecision(int column) throws SQLException;

    /**
     * Gets a column's number of digits to right of the decimal point.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return scale
     * @exception SQLException if a database access error occurs
     */
	int getScale(int column) throws SQLException;	

    /**
     * Gets a column's table name. 
     *
     * @param column the first column is 1, the second is 2, ...
     * @return table name or "" if not applicable
     * @exception SQLException if a database access error occurs
     */
	String getTableName(int column) throws SQLException;

    /**
     * Gets a column's table's catalog name.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return column name or "" if not applicable.
     * @exception SQLException if a database access error occurs
     */
	String getCatalogName(int column) throws SQLException;

    /**
     * Retrieves a column's SQL type.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return SQL type from java.sql.Types
     * @exception SQLException if a database access error occurs
     * @see Types
     */
	int getColumnType(int column) throws SQLException;

    /**
     * Retrieves a column's database-specific type name.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return type name used by the database. If the column type is
	 * a user-defined type, then a fully-qualified type name is returned.
     * @exception SQLException if a database access error occurs
     */
	String getColumnTypeName(int column) throws SQLException;

    /**
     * Indicates whether a column is definitely not writable.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isReadOnly(int column) throws SQLException;

    /**
     * Indicates whether it is possible for a write on the column to succeed.
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isWritable(int column) throws SQLException;

    /**
     * Indicates whether a write on the column will definitely succeed.	
     *
     * @param column the first column is 1, the second is 2, ...
     * @return true if so
     * @exception SQLException if a database access error occurs
     */
	boolean isDefinitelyWritable(int column) throws SQLException;

    //--------------------------JDBC 2.0-----------------------------------

    /**
     * JDBC 2.0
     *
     * <p>Returns the fully-qualified name of the Java class whose instances 
     * are manufactured if the method <code>ResultSet.getObject</code>
	 * is called to retrieve a value 
     * from the column.  <code>ResultSet.getObject</code> may return a subclass of the
     * class returned by this method.
	 *
	 * @return the fully-qualified name of the class in the Java programming
	 *         language that would be used by the method 
	 * <code>ResultSet.getObject</code> to retrieve the value in the specified
	 * column. This is the class name used for custom mapping.
     * @exception SQLException if a database access error occurs
     */
    String getColumnClassName(int column) throws SQLException;
}
