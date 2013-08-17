/*
 * @(#)DatabaseMetaData.java	1.9 98/07/01
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
 * This class provides information about the database as a whole.
 *
 * <P>Many of the methods here return lists of information in ResultSets.
 * You can use the normal ResultSet methods such as getString and getInt 
 * to retrieve the data from these ResultSets.  If a given form of
 * metadata is not available, these methods should throw a SQLException.
 *
 * <P>Some of these methods take arguments that are String patterns.  These
 * arguments all have names such as fooPattern.  Within a pattern String, "%"
 * means match any substring of 0 or more characters, and "_" means match
 * any one character. Only metadata entries matching the search pattern 
 * are returned. If a search pattern argument is set to a null ref, it means 
 * that argument's criteria should be dropped from the search.
 * 
 * <P>A SQLException will be thrown if a driver does not support a meta
 * data method.  In the case of methods that return a ResultSet,
 * either a ResultSet (which may be empty) is returned or a
 * SQLException is thrown.
 */
public interface DatabaseMetaData {

    //----------------------------------------------------------------------
	// First, a variety of minor information about the target database.

    /**
     * Can all the procedures returned by getProcedures be called by the
     * current user?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean allProceduresAreCallable() throws SQLException;

    /**
     * Can all the tables returned by getTable be SELECTed by the
     * current user?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean allTablesAreSelectable() throws SQLException;

    /**
     * What's the url for this database?
     *
     * @return the url or null if it can't be generated
     * @exception SQLException if a database-access error occurs.
     */
	String getURL() throws SQLException;

    /**
     * What's our user name as known to the database?
     *
     * @return our database user name
     * @exception SQLException if a database-access error occurs.
     */
	String getUserName() throws SQLException;

    /**
     * Is the database in read-only mode?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean isReadOnly() throws SQLException;

    /**
     * Are NULL values sorted high?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean nullsAreSortedHigh() throws SQLException;

    /**
     * Are NULL values sorted low?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean nullsAreSortedLow() throws SQLException;

    /**
     * Are NULL values sorted at the start regardless of sort order?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean nullsAreSortedAtStart() throws SQLException;

    /**
     * Are NULL values sorted at the end regardless of sort order?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean nullsAreSortedAtEnd() throws SQLException;

    /**
     * What's the name of this database product?
     *
     * @return database product name
     * @exception SQLException if a database-access error occurs.
     */
	String getDatabaseProductName() throws SQLException;

    /**
     * What's the version of this database product?
     *
     * @return database version
     * @exception SQLException if a database-access error occurs.
     */
	String getDatabaseProductVersion() throws SQLException;

    /**
     * What's the name of this JDBC driver?
     *
     * @return JDBC driver name
     * @exception SQLException if a database-access error occurs.
     */
	String getDriverName() throws SQLException;

    /**
     * What's the version of this JDBC driver?
     *
     * @return JDBC driver version
     * @exception SQLException if a database-access error occurs.
     */
	String getDriverVersion() throws SQLException;

    /**
     * What's this JDBC driver's major version number?
     *
     * @return JDBC driver major version
     */
	int getDriverMajorVersion();

    /**
     * What's this JDBC driver's minor version number?
     *
     * @return JDBC driver minor version number
     */
	int getDriverMinorVersion();

    /**
     * Does the database store tables in a local file?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean usesLocalFiles() throws SQLException;

    /**
     * Does the database use a file for each table?
     *
     * @return true if the database uses a local file for each table
     * @exception SQLException if a database-access error occurs.
     */
	boolean usesLocalFilePerTable() throws SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case sensitive and as a result store them in mixed case?
     *
     * A JDBC-Compliant driver will always return false.
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsMixedCaseIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in upper case?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean storesUpperCaseIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in lower case?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean storesLowerCaseIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in mixed case?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean storesMixedCaseIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case sensitive and as a result store them in mixed case?
     *
     * A JDBC-Compliant driver will always return true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsMixedCaseQuotedIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in upper case?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean storesUpperCaseQuotedIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in lower case?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean storesLowerCaseQuotedIdentifiers() throws SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in mixed case?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean storesMixedCaseQuotedIdentifiers() throws SQLException;

    /**
     * What's the string used to quote SQL identifiers?
     * This returns a space " " if identifier quoting isn't supported.
     *
     * A JDBC-Compliant driver always uses a double quote character.
     *
     * @return the quoting string
     * @exception SQLException if a database-access error occurs.
     */
	String getIdentifierQuoteString() throws SQLException;

    /**
     * Get a comma separated list of all a database's SQL keywords
     * that are NOT also SQL92 keywords.
     *
     * @return the list 
     * @exception SQLException if a database-access error occurs.
     */
	String getSQLKeywords() throws SQLException;

    /**
     * Get a comma separated list of math functions.
     *
     * @return the list
     * @exception SQLException if a database-access error occurs.
     */
	String getNumericFunctions() throws SQLException;

    /**
     * Get a comma separated list of string functions.
     *
     * @return the list
     * @exception SQLException if a database-access error occurs.
     */
	String getStringFunctions() throws SQLException;

    /**
     * Get a comma separated list of system functions.
     *
     * @return the list
     * @exception SQLException if a database-access error occurs.
     */
	String getSystemFunctions() throws SQLException;

    /**
     * Get a comma separated list of time and date functions.
     *
     * @return the list
     * @exception SQLException if a database-access error occurs.
     */
	String getTimeDateFunctions() throws SQLException;

    /**
     * This is the string that can be used to escape '_' or '%' in
     * the string pattern style catalog search parameters.
     *
     * <P>The '_' character represents any single character.
     * <P>The '%' character represents any sequence of zero or 
     * more characters.
     *
     * @return the string used to escape wildcard characters
     * @exception SQLException if a database-access error occurs.
     */
	String getSearchStringEscape() throws SQLException;

    /**
     * Get all the "extra" characters that can be used in unquoted
     * identifier names (those beyond a-z, A-Z, 0-9 and _).
     *
     * @return the string containing the extra characters 
     * @exception SQLException if a database-access error occurs.
     */
	String getExtraNameCharacters() throws SQLException;

    //--------------------------------------------------------------------
    // Functions describing which features are supported.

    /**
     * Is "ALTER TABLE" with add column supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsAlterTableWithAddColumn() throws SQLException;

    /**
     * Is "ALTER TABLE" with drop column supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsAlterTableWithDropColumn() throws SQLException;

    /**
     * Is column aliasing supported? 
     *
     * <P>If so, the SQL AS clause can be used to provide names for
     * computed columns or to provide alias names for columns as
     * required.
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsColumnAliasing() throws SQLException;

    /**
     * Are concatenations between NULL and non-NULL values NULL?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean nullPlusNonNullIsNull() throws SQLException;

    /**
     * Is the CONVERT function between SQL types supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsConvert() throws SQLException;

    /**
     * Is CONVERT between the given SQL types supported?
     *
     * @param fromType the type to convert from
     * @param toType the type to convert to     
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     * @see Types
     */
	boolean supportsConvert(int fromType, int toType) throws SQLException;

    /**
     * Are table correlation names supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsTableCorrelationNames() throws SQLException;

    /**
     * If table correlation names are supported, are they restricted
     * to be different from the names of the tables?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsDifferentTableCorrelationNames() throws SQLException;

    /**
     * Are expressions in "ORDER BY" lists supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsExpressionsInOrderBy() throws SQLException;

    /**
     * Can an "ORDER BY" clause use columns not in the SELECT?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsOrderByUnrelated() throws SQLException;

    /**
     * Is some form of "GROUP BY" clause supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsGroupBy() throws SQLException;

    /**
     * Can a "GROUP BY" clause use columns not in the SELECT?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsGroupByUnrelated() throws SQLException;

    /**
     * Can a "GROUP BY" clause add columns not in the SELECT
     * provided it specifies all the columns in the SELECT?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsGroupByBeyondSelect() throws SQLException;

    /**
     * Is the escape character in "LIKE" clauses supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsLikeEscapeClause() throws SQLException;

    /**
     * Are multiple ResultSets from a single execute supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsMultipleResultSets() throws SQLException;

    /**
     * Can we have multiple transactions open at once (on different
     * connections)?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsMultipleTransactions() throws SQLException;

    /**
     * Can columns be defined as non-nullable?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsNonNullableColumns() throws SQLException;

    /**
     * Is the ODBC Minimum SQL grammar supported?
     *
     * All JDBC-Compliant drivers must return true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsMinimumSQLGrammar() throws SQLException;

    /**
     * Is the ODBC Core SQL grammar supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCoreSQLGrammar() throws SQLException;

    /**
     * Is the ODBC Extended SQL grammar supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsExtendedSQLGrammar() throws SQLException;

    /**
     * Is the ANSI92 entry level SQL grammar supported?
     *
     * All JDBC-Compliant drivers must return true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsANSI92EntryLevelSQL() throws SQLException;

    /**
     * Is the ANSI92 intermediate SQL grammar supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsANSI92IntermediateSQL() throws SQLException;

    /**
     * Is the ANSI92 full SQL grammar supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsANSI92FullSQL() throws SQLException;

    /**
     * Is the SQL Integrity Enhancement Facility supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsIntegrityEnhancementFacility() throws SQLException;

    /**
     * Is some form of outer join supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsOuterJoins() throws SQLException;

    /**
     * Are full nested outer joins supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsFullOuterJoins() throws SQLException;

    /**
     * Is there limited support for outer joins?  (This will be true
     * if supportFullOuterJoins is true.)
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsLimitedOuterJoins() throws SQLException;

    /**
     * What's the database vendor's preferred term for "schema"?
     *
     * @return the vendor term
     * @exception SQLException if a database-access error occurs.
     */
	String getSchemaTerm() throws SQLException;

    /**
     * What's the database vendor's preferred term for "procedure"?
     *
     * @return the vendor term
     * @exception SQLException if a database-access error occurs.
     */
	String getProcedureTerm() throws SQLException;

    /**
     * What's the database vendor's preferred term for "catalog"?
     *
     * @return the vendor term
     * @exception SQLException if a database-access error occurs.
     */
	String getCatalogTerm() throws SQLException;

    /**
     * Does a catalog appear at the start of a qualified table name?
     * (Otherwise it appears at the end)
     *
     * @return true if it appears at the start 
     * @exception SQLException if a database-access error occurs.
     */
	boolean isCatalogAtStart() throws SQLException;

    /**
     * What's the separator between catalog and table name?
     *
     * @return the separator string
     * @exception SQLException if a database-access error occurs.
     */
	String getCatalogSeparator() throws SQLException;

    /**
     * Can a schema name be used in a data manipulation statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSchemasInDataManipulation() throws SQLException;

    /**
     * Can a schema name be used in a procedure call statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSchemasInProcedureCalls() throws SQLException;

    /**
     * Can a schema name be used in a table definition statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSchemasInTableDefinitions() throws SQLException;

    /**
     * Can a schema name be used in an index definition statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSchemasInIndexDefinitions() throws SQLException;

    /**
     * Can a schema name be used in a privilege definition statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSchemasInPrivilegeDefinitions() throws SQLException;

    /**
     * Can a catalog name be used in a data manipulation statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCatalogsInDataManipulation() throws SQLException;

    /**
     * Can a catalog name be used in a procedure call statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCatalogsInProcedureCalls() throws SQLException;

    /**
     * Can a catalog name be used in a table definition statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCatalogsInTableDefinitions() throws SQLException;

    /**
     * Can a catalog name be used in an index definition statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCatalogsInIndexDefinitions() throws SQLException;

    /**
     * Can a catalog name be used in a privilege definition statement?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException;


    /**
     * Is positioned DELETE supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsPositionedDelete() throws SQLException;

    /**
     * Is positioned UPDATE supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsPositionedUpdate() throws SQLException;

    /**
     * Is SELECT for UPDATE supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSelectForUpdate() throws SQLException;

    /**
     * Are stored procedure calls using the stored procedure escape
     * syntax supported?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsStoredProcedures() throws SQLException;

    /**
     * Are subqueries in comparison expressions supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSubqueriesInComparisons() throws SQLException;

    /**
     * Are subqueries in 'exists' expressions supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSubqueriesInExists() throws SQLException;

    /**
     * Are subqueries in 'in' statements supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSubqueriesInIns() throws SQLException;

    /**
     * Are subqueries in quantified expressions supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsSubqueriesInQuantifieds() throws SQLException;

    /**
     * Are correlated subqueries supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsCorrelatedSubqueries() throws SQLException;

    /**
     * Is SQL UNION supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsUnion() throws SQLException;

    /**
     * Is SQL UNION ALL supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsUnionAll() throws SQLException;

    /**
     * Can cursors remain open across commits? 
     * 
     * @return true if cursors always remain open; false if they might not remain open
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsOpenCursorsAcrossCommit() throws SQLException;

    /**
     * Can cursors remain open across rollbacks?
     * 
     * @return true if cursors always remain open; false if they might not remain open
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsOpenCursorsAcrossRollback() throws SQLException;

    /**
     * Can statements remain open across commits?
     * 
     * @return true if statements always remain open; false if they might not remain open
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsOpenStatementsAcrossCommit() throws SQLException;

    /**
     * Can statements remain open across rollbacks?
     * 
     * @return true if statements always remain open; false if they might not remain open
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsOpenStatementsAcrossRollback() throws SQLException;

	

    //----------------------------------------------------------------------
    // The following group of methods exposes various limitations 
    // based on the target database with the current driver.
    // Unless otherwise specified, a result of zero means there is no
    // limit, or the limit is not known.
	
    /**
     * How many hex characters can you have in an inline binary literal?
     *
     * @return max literal length
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxBinaryLiteralLength() throws SQLException;

    /**
     * What's the max length for a character literal?
     *
     * @return max literal length
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxCharLiteralLength() throws SQLException;

    /**
     * What's the limit on column name length?
     *
     * @return max literal length
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxColumnNameLength() throws SQLException;

    /**
     * What's the maximum number of columns in a "GROUP BY" clause?
     *
     * @return max number of columns
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxColumnsInGroupBy() throws SQLException;

    /**
     * What's the maximum number of columns allowed in an index?
     *
     * @return max columns
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxColumnsInIndex() throws SQLException;

    /**
     * What's the maximum number of columns in an "ORDER BY" clause?
     *
     * @return max columns
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxColumnsInOrderBy() throws SQLException;

    /**
     * What's the maximum number of columns in a "SELECT" list?
     *
     * @return max columns
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxColumnsInSelect() throws SQLException;

    /**
     * What's the maximum number of columns in a table?
     *
     * @return max columns
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxColumnsInTable() throws SQLException;

    /**
     * How many active connections can we have at a time to this database?
     *
     * @return max connections
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxConnections() throws SQLException;

    /**
     * What's the maximum cursor name length?
     *
     * @return max cursor name length in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxCursorNameLength() throws SQLException;

    /**
     * What's the maximum length of an index (in bytes)?	
     *
     * @return max index length in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxIndexLength() throws SQLException;

    /**
     * What's the maximum length allowed for a schema name?
     *
     * @return max name length in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxSchemaNameLength() throws SQLException;

    /**
     * What's the maximum length of a procedure name?
     *
     * @return max name length in bytes 
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxProcedureNameLength() throws SQLException;

    /**
     * What's the maximum length of a catalog name?
     *
     * @return max name length in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxCatalogNameLength() throws SQLException;

    /**
     * What's the maximum length of a single row?
     *
     * @return max row size in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxRowSize() throws SQLException;

    /**
     * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY
     * blobs?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean doesMaxRowSizeIncludeBlobs() throws SQLException;

    /**
     * What's the maximum length of a SQL statement?
     *
     * @return max length in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxStatementLength() throws SQLException;

    /**
     * How many active statements can we have open at one time to this
     * database?
     *
     * @return the maximum 
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxStatements() throws SQLException;

    /**
     * What's the maximum length of a table name?
     *
     * @return max name length in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxTableNameLength() throws SQLException;

    /**
     * What's the maximum number of tables in a SELECT?
     *
     * @return the maximum
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxTablesInSelect() throws SQLException;

    /**
     * What's the maximum length of a user name?
     *
     * @return max name length  in bytes
     * @exception SQLException if a database-access error occurs.
     */
	int getMaxUserNameLength() throws SQLException;

    //----------------------------------------------------------------------

    /**
     * What's the database's default transaction isolation level?  The
     * values are defined in java.sql.Connection.
     *
     * @return the default isolation level 
     * @exception SQLException if a database-access error occurs.
     * @see Connection
     */
	int getDefaultTransactionIsolation() throws SQLException;

    /**
     * Are transactions supported? If not, commit is a noop and the
     * isolation level is TRANSACTION_NONE.
     *
     * @return true if transactions are supported 
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsTransactions() throws SQLException;

    /**
     * Does the database support the given transaction isolation level?
     *
     * @param level the values are defined in java.sql.Connection
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     * @see Connection
     */
	boolean supportsTransactionIsolationLevel(int level)
							throws SQLException;

    /**
     * Are both data definition and data manipulation statements
     * within a transaction supported?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsDataDefinitionAndDataManipulationTransactions()
							 throws SQLException;
    /**
     * Are only data manipulation statements within a transaction
     * supported?
     *
     * @return true if so
     * @exception SQLException if a database-access error occurs.
     */
	boolean supportsDataManipulationTransactionsOnly()
							throws SQLException;
    /**
     * Does a data definition statement within a transaction force the
     * transaction to commit?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean dataDefinitionCausesTransactionCommit()
							throws SQLException;
    /**
     * Is a data definition statement within a transaction ignored?
     *
     * @return true if so 
     * @exception SQLException if a database-access error occurs.
     */
	boolean dataDefinitionIgnoredInTransactions()
							throws SQLException;


    /**
     * Get a description of stored procedures available in a
     * catalog.
     *
     * <P>Only procedure descriptions matching the schema and
     * procedure name criteria are returned.  They are ordered by
     * PROCEDURE_SCHEM, and PROCEDURE_NAME.
     *
     * <P>Each procedure description has the the following columns:
     *  <OL>
     *	<LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
     *	<LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
     *	<LI><B>PROCEDURE_NAME</B> String => procedure name
     *  <LI> reserved for future use
     *  <LI> reserved for future use
     *  <LI> reserved for future use
     *	<LI><B>REMARKS</B> String => explanatory comment on the procedure
     *	<LI><B>PROCEDURE_TYPE</B> short => kind of procedure:
     *      <UL>
     *      <LI> procedureResultUnknown - May return a result
     *      <LI> procedureNoResult - Does not return a result
     *      <LI> procedureReturnsResult - Returns a result
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param procedureNamePattern a procedure name pattern 
     * @return ResultSet - each row is a procedure description 
     * @exception SQLException if a database-access error occurs.
     * @see #getSearchStringEscape 
     */
	ResultSet getProcedures(String catalog, String schemaPattern,
			String procedureNamePattern) throws SQLException;

    /**
     * PROCEDURE_TYPE - May return a result.
     */
	int procedureResultUnknown	= 0;
    /**
     * PROCEDURE_TYPE - Does not return a result.
     */
	int procedureNoResult		= 1;
    /**
     * PROCEDURE_TYPE - Returns a result.
     */
	int procedureReturnsResult	= 2;

    /**
     * Get a description of a catalog's stored procedure parameters
     * and result columns.
     *
     * <P>Only descriptions matching the schema, procedure and
     * parameter name criteria are returned.  They are ordered by
     * PROCEDURE_SCHEM and PROCEDURE_NAME. Within this, the return value,
     * if any, is first. Next are the parameter descriptions in call
     * order. The column descriptions follow in column number order.
     *
     * <P>Each row in the ResultSet is a parameter description or
     * column description with the following fields:
     *  <OL>
     *	<LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
     *	<LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
     *	<LI><B>PROCEDURE_NAME</B> String => procedure name
     *	<LI><B>COLUMN_NAME</B> String => column/parameter name 
     *	<LI><B>COLUMN_TYPE</B> Short => kind of column/parameter:
     *      <UL>
     *      <LI> procedureColumnUnknown - nobody knows
     *      <LI> procedureColumnIn - IN parameter
     *      <LI> procedureColumnInOut - INOUT parameter
     *      <LI> procedureColumnOut - OUT parameter
     *      <LI> procedureColumnReturn - procedure return value
     *      <LI> procedureColumnResult - result column in ResultSet
     *      </UL>
     *  <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *	<LI><B>TYPE_NAME</B> String => SQL type name
     *	<LI><B>PRECISION</B> int => precision
     *	<LI><B>LENGTH</B> int => length in bytes of data
     *	<LI><B>SCALE</B> short => scale
     *	<LI><B>RADIX</B> short => radix
     *	<LI><B>NULLABLE</B> short => can it contain NULL?
     *      <UL>
     *      <LI> procedureNoNulls - does not allow NULL values
     *      <LI> procedureNullable - allows NULL values
     *      <LI> procedureNullableUnknown - nullability unknown
     *      </UL>
     *	<LI><B>REMARKS</B> String => comment describing parameter/column
     *  </OL>
     *
     * <P><B>Note:</B> Some databases may not return the column
     * descriptions for a procedure. Additional columns beyond
     * REMARKS can be defined by the database.
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema 
     * @param procedureNamePattern a procedure name pattern 
     * @param columnNamePattern a column name pattern 
     * @return ResultSet - each row is a stored procedure parameter or 
     *      column description 
     * @exception SQLException if a database-access error occurs.
     * @see #getSearchStringEscape 
     */
	ResultSet getProcedureColumns(String catalog,
			String schemaPattern,
			String procedureNamePattern, 
			String columnNamePattern) throws SQLException;

    /**
     * COLUMN_TYPE - nobody knows.
     */
	int procedureColumnUnknown = 0;

    /**
     * COLUMN_TYPE - IN parameter.
     */
	int procedureColumnIn = 1;

    /**
     * COLUMN_TYPE - INOUT parameter.
     */
	int procedureColumnInOut = 2;

    /**
     * COLUMN_TYPE - OUT parameter.
     */
	int procedureColumnOut = 4;
    /**
     * COLUMN_TYPE - procedure return value.
     */
	int procedureColumnReturn = 5;

    /**
     * COLUMN_TYPE - result column in ResultSet.
     */
	int procedureColumnResult = 3;

    /**
     * TYPE NULLABLE - does not allow NULL values.
     */
    int procedureNoNulls = 0;

    /**
     * TYPE NULLABLE - allows NULL values.
     */
    int procedureNullable = 1;

    /**
     * TYPE NULLABLE - nullability unknown.
     */
    int procedureNullableUnknown = 2;


    /**
     * Get a description of tables available in a catalog.
     *
     * <P>Only table descriptions matching the catalog, schema, table
     * name and type criteria are returned.  They are ordered by
     * TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.
     *
     * <P>Each table description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     *			"VIEW",	"SYSTEM TABLE", "GLOBAL TEMPORARY", 
     *			"LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *	<LI><B>REMARKS</B> String => explanatory comment on the table
     *  </OL>
     *
     * <P><B>Note:</B> Some databases may not return information for
     * all tables.
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param tableNamePattern a table name pattern 
     * @param types a list of table types to include; null returns all types 
     * @return ResultSet - each row is a table description
     * @exception SQLException if a database-access error occurs.
     * @see #getSearchStringEscape 
     */
	ResultSet getTables(String catalog, String schemaPattern,
		String tableNamePattern, String types[]) throws SQLException;

    /**
     * Get the schema names available in this database.  The results
     * are ordered by schema name.
     *
     * <P>The schema column is:
     *  <OL>
     *	<LI><B>TABLE_SCHEM</B> String => schema name
     *  </OL>
     *
     * @return ResultSet - each row has a single String column that is a
     * schema name 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getSchemas() throws SQLException;

    /**
     * Get the catalog names available in this database.  The results
     * are ordered by catalog name.
     *
     * <P>The catalog column is:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => catalog name
     *  </OL>
     *
     * @return ResultSet - each row has a single String column that is a
     * catalog name 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getCatalogs() throws SQLException;

    /**
     * Get the table types available in this database.  The results
     * are ordered by table type.
     *
     * <P>The table type is:
     *  <OL>
     *	<LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     *			"VIEW",	"SYSTEM TABLE", "GLOBAL TEMPORARY", 
     *			"LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *  </OL>
     *
     * @return ResultSet - each row has a single String column that is a
     * table type 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getTableTypes() throws SQLException;

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>Only column descriptions matching the catalog, schema, table
     * and column name criteria are returned.  They are ordered by
     * TABLE_SCHEM, TABLE_NAME and ORDINAL_POSITION.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>COLUMN_NAME</B> String => column name
     *	<LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *	<LI><B>TYPE_NAME</B> String => Data source dependent type name
     *	<LI><B>COLUMN_SIZE</B> int => column size.  For char or date
     *	    types this is the maximum number of characters, for numeric or
     *	    decimal types this is precision.
     *	<LI><B>BUFFER_LENGTH</B> is not used.
     *	<LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *	<LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *	<LI><B>NULLABLE</B> int => is NULL allowed?
     *      <UL>
     *      <LI> columnNoNulls - might not allow NULL values
     *      <LI> columnNullable - definitely allows NULL values
     *      <LI> columnNullableUnknown - nullability unknown
     *      </UL>
     *	<LI><B>REMARKS</B> String => comment describing column (may be null)
     * 	<LI><B>COLUMN_DEF</B> String => default value (may be null)
     *	<LI><B>SQL_DATA_TYPE</B> int => unused
     *	<LI><B>SQL_DATETIME_SUB</B> int => unused
     *	<LI><B>CHAR_OCTET_LENGTH</B> int => for char types the 
     *       maximum number of bytes in the column
     *	<LI><B>ORDINAL_POSITION</B> int	=> index of column in table 
     *      (starting at 1)
     *	<LI><B>IS_NULLABLE</B> String => "NO" means column definitely 
     *      does not allow NULL values; "YES" means the column might 
     *      allow NULL values.  An empty string means nobody knows.
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param tableNamePattern a table name pattern 
     * @param columnNamePattern a column name pattern 
     * @return ResultSet - each row is a column description
     * @exception SQLException if a database-access error occurs.
     * @see #getSearchStringEscape 
     */
	ResultSet getColumns(String catalog, String schemaPattern,
		String tableNamePattern, String columnNamePattern)
					throws SQLException;
    /**
     * COLUMN NULLABLE - might not allow NULL values.
     */
    int columnNoNulls = 0;

    /**
     * COLUMN NULLABLE - definitely allows NULL values.
     */
    int columnNullable = 1;

    /**
     * COLUMN NULLABLE - nullability unknown.
     */
    int columnNullableUnknown = 2;

    /**
     * Get a description of the access rights for a table's columns.
     *
     * <P>Only privileges matching the column name criteria are
     * returned.  They are ordered by COLUMN_NAME and PRIVILEGE.
     *
     * <P>Each privilige description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>COLUMN_NAME</B> String => column name
     *	<LI><B>GRANTOR</B> => grantor of access (may be null)
     *	<LI><B>GRANTEE</B> String => grantee of access
     *	<LI><B>PRIVILEGE</B> String => name of access (SELECT, 
     *      INSERT, UPDATE, REFRENCES, ...)
     *	<LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted 
     *      to grant to others; "NO" if not; null if unknown 
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @param columnNamePattern a column name pattern 
     * @return ResultSet - each row is a column privilege description
     * @exception SQLException if a database-access error occurs.
     * @see #getSearchStringEscape 
     */
	ResultSet getColumnPrivileges(String catalog, String schema,
		String table, String columnNamePattern) throws SQLException;

    /**
     * Get a description of the access rights for each table available
     * in a catalog. Note that a table privilege applies to one or
     * more columns in the table. It would be wrong to assume that
     * this priviledge applies to all columns (this may be true for
     * some systems but is not true for all.)
     *
     * <P>Only privileges matching the schema and table name
     * criteria are returned.  They are ordered by TABLE_SCHEM,
     * TABLE_NAME, and PRIVILEGE.
     *
     * <P>Each privilige description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>GRANTOR</B> => grantor of access (may be null)
     *	<LI><B>GRANTEE</B> String => grantee of access
     *	<LI><B>PRIVILEGE</B> String => name of access (SELECT, 
     *      INSERT, UPDATE, REFRENCES, ...)
     *	<LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted 
     *      to grant to others; "NO" if not; null if unknown 
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param tableNamePattern a table name pattern 
     * @return ResultSet - each row is a table privilege description
     * @exception SQLException if a database-access error occurs.
     * @see #getSearchStringEscape 
     */
	ResultSet getTablePrivileges(String catalog, String schemaPattern,
				String tableNamePattern) throws SQLException;

    /**
     * Get a description of a table's optimal set of columns that
     * uniquely identifies a row. They are ordered by SCOPE.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *	<LI><B>SCOPE</B> short => actual scope of result
     *      <UL>
     *      <LI> bestRowTemporary - very temporary, while using row
     *      <LI> bestRowTransaction - valid for remainder of current transaction
     *      <LI> bestRowSession - valid for remainder of current session
     *      </UL>
     *	<LI><B>COLUMN_NAME</B> String => column name
     *	<LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     *	<LI><B>TYPE_NAME</B> String => Data source dependent type name
     *	<LI><B>COLUMN_SIZE</B> int => precision
     *	<LI><B>BUFFER_LENGTH</B> int => not used
     *	<LI><B>DECIMAL_DIGITS</B> short	 => scale
     *	<LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column 
     *      like an Oracle ROWID
     *      <UL>
     *      <LI> bestRowUnknown - may or may not be pseudo column
     *      <LI> bestRowNotPseudo - is NOT a pseudo column
     *      <LI> bestRowPseudo - is a pseudo column
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @param scope the scope of interest; use same values as SCOPE
     * @param nullable include columns that are nullable?
     * @return ResultSet - each row is a column description 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getBestRowIdentifier(String catalog, String schema,
		String table, int scope, boolean nullable) throws SQLException;
	
    /**
     * BEST ROW SCOPE - very temporary, while using row.
     */
	int bestRowTemporary   = 0;

    /**
     * BEST ROW SCOPE - valid for remainder of current transaction.
     */
	int bestRowTransaction = 1;

    /**
     * BEST ROW SCOPE - valid for remainder of current session.
     */
	int bestRowSession     = 2;

    /**
     * BEST ROW PSEUDO_COLUMN - may or may not be pseudo column.
     */
	int bestRowUnknown	= 0;

    /**
     * BEST ROW PSEUDO_COLUMN - is NOT a pseudo column.
     */
	int bestRowNotPseudo	= 1;

    /**
     * BEST ROW PSEUDO_COLUMN - is a pseudo column.
     */
	int bestRowPseudo	= 2;

    /**
     * Get a description of a table's columns that are automatically
     * updated when any value in a row is updated.  They are
     * unordered.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *	<LI><B>SCOPE</B> short => is not used
     *	<LI><B>COLUMN_NAME</B> String => column name
     *	<LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     *	<LI><B>TYPE_NAME</B> String => Data source dependent type name
     *	<LI><B>COLUMN_SIZE</B> int => precision
     *	<LI><B>BUFFER_LENGTH</B> int => length of column value in bytes
     *	<LI><B>DECIMAL_DIGITS</B> short	 => scale
     *	<LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column 
     *      like an Oracle ROWID
     *      <UL>
     *      <LI> versionColumnUnknown - may or may not be pseudo column
     *      <LI> versionColumnNotPseudo - is NOT a pseudo column
     *      <LI> versionColumnPseudo - is a pseudo column
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @return ResultSet - each row is a column description 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getVersionColumns(String catalog, String schema,
				String table) throws SQLException;
	
    /**
     * VERSION COLUMNS PSEUDO_COLUMN - may or may not be pseudo column.
     */
	int versionColumnUnknown	= 0;

    /**
     *  VERSION COLUMNS PSEUDO_COLUMN - is NOT a pseudo column.
     */
	int versionColumnNotPseudo	= 1;

    /**
     *  VERSION COLUMNS PSEUDO_COLUMN - is a pseudo column.
     */
	int versionColumnPseudo	= 2;

    /**
     * Get a description of a table's primary key columns.  They
     * are ordered by COLUMN_NAME.
     *
     * <P>Each primary key column description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>COLUMN_NAME</B> String => column name
     *	<LI><B>KEY_SEQ</B> short => sequence number within primary key
     *	<LI><B>PK_NAME</B> String => primary key name (may be null)
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param table a table name
     * @return ResultSet - each row is a primary key column description 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getPrimaryKeys(String catalog, String schema,
				String table) throws SQLException;

    /**
     * Get a description of the primary key columns that are
     * referenced by a table's foreign key columns (the primary keys
     * imported by a table).  They are ordered by PKTABLE_CAT,
     * PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
     *
     * <P>Each primary key column description has the following columns:
     *  <OL>
     *	<LI><B>PKTABLE_CAT</B> String => primary key table catalog 
     *      being imported (may be null)
     *	<LI><B>PKTABLE_SCHEM</B> String => primary key table schema
     *      being imported (may be null)
     *	<LI><B>PKTABLE_NAME</B> String => primary key table name
     *      being imported
     *	<LI><B>PKCOLUMN_NAME</B> String => primary key column name
     *      being imported
     *	<LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
     *	<LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
     *	<LI><B>FKTABLE_NAME</B> String => foreign key table name
     *	<LI><B>FKCOLUMN_NAME</B> String => foreign key column name
     *	<LI><B>KEY_SEQ</B> short => sequence number within foreign key
     *	<LI><B>UPDATE_RULE</B> short => What happens to 
     *       foreign key when primary is updated:
     *      <UL>
     *      <LI> importedNoAction - do not allow update of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - change imported key to agree 
     *               with primary key update
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been updated
     *      <LI> importedKeySetDefault - change imported key to default values 
     *               if its primary key has been updated
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      </UL>
     *	<LI><B>DELETE_RULE</B> short => What happens to 
     *      the foreign key when primary is deleted.
     *      <UL>
     *      <LI> importedKeyNoAction - do not allow delete of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - delete rows that import a deleted key
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been deleted
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      <LI> importedKeySetDefault - change imported key to default if 
     *               its primary key has been deleted
     *      </UL>
     *	<LI><B>FK_NAME</B> String => foreign key name (may be null)
     *	<LI><B>PK_NAME</B> String => primary key name (may be null)
     *	<LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key 
     *      constraints be deferred until commit
     *      <UL>
     *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
     *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition 
     *      <LI> importedKeyNotDeferrable - see SQL92 for definition 
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param table a table name
     * @return ResultSet - each row is a primary key column description 
     * @exception SQLException if a database-access error occurs.
     * @see #getExportedKeys 
     */
	ResultSet getImportedKeys(String catalog, String schema,
				String table) throws SQLException;

    /**
     * IMPORT KEY UPDATE_RULE and DELETE_RULE - for update, change
     * imported key to agree with primary key update; for delete,
     * delete rows that import a deleted key.
     */
	int importedKeyCascade	= 0;

    /**
     * IMPORT KEY UPDATE_RULE and DELETE_RULE - do not allow update or
     * delete of primary key if it has been imported.  
     */
	int importedKeyRestrict = 1;

    /**
     * IMPORT KEY UPDATE_RULE and DELETE_RULE - change imported key to
     * NULL if its primary key has been updated or deleted.
     */
	int importedKeySetNull  = 2;

    /**
     * IMPORT KEY UPDATE_RULE and DELETE_RULE - do not allow update or
     * delete of primary key if it has been imported.  
     */
	int importedKeyNoAction = 3;

    /**
     * IMPORT KEY UPDATE_RULE and DELETE_RULE - change imported key to
     * default values if its primary key has been updated or deleted.
     */
	int importedKeySetDefault  = 4;

    /**
     * IMPORT KEY DEFERRABILITY - see SQL92 for definition
     */
	int importedKeyInitiallyDeferred  = 5;

    /**
     * IMPORT KEY DEFERRABILITY - see SQL92 for definition
     */
	int importedKeyInitiallyImmediate  = 6;

    /**
     * IMPORT KEY DEFERRABILITY - see SQL92 for definition
     */
	int importedKeyNotDeferrable  = 7;

    /**
     * Get a description of the foreign key columns that reference a
     * table's primary key columns (the foreign keys exported by a
     * table).  They are ordered by FKTABLE_CAT, FKTABLE_SCHEM,
     * FKTABLE_NAME, and KEY_SEQ.
     *
     * <P>Each foreign key column description has the following columns:
     *  <OL>
     *	<LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
     *	<LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
     *	<LI><B>PKTABLE_NAME</B> String => primary key table name
     *	<LI><B>PKCOLUMN_NAME</B> String => primary key column name
     *	<LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
     *      being exported (may be null)
     *	<LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
     *      being exported (may be null)
     *	<LI><B>FKTABLE_NAME</B> String => foreign key table name
     *      being exported
     *	<LI><B>FKCOLUMN_NAME</B> String => foreign key column name
     *      being exported
     *	<LI><B>KEY_SEQ</B> short => sequence number within foreign key
     *	<LI><B>UPDATE_RULE</B> short => What happens to 
     *       foreign key when primary is updated:
     *      <UL>
     *      <LI> importedNoAction - do not allow update of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - change imported key to agree 
     *               with primary key update
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been updated
     *      <LI> importedKeySetDefault - change imported key to default values 
     *               if its primary key has been updated
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      </UL>
     *	<LI><B>DELETE_RULE</B> short => What happens to 
     *      the foreign key when primary is deleted.
     *      <UL>
     *      <LI> importedKeyNoAction - do not allow delete of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - delete rows that import a deleted key
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been deleted
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      <LI> importedKeySetDefault - change imported key to default if 
     *               its primary key has been deleted
     *      </UL>
     *	<LI><B>FK_NAME</B> String => foreign key name (may be null)
     *	<LI><B>PK_NAME</B> String => primary key name (may be null)
     *	<LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key 
     *      constraints be deferred until commit
     *      <UL>
     *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
     *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition 
     *      <LI> importedKeyNotDeferrable - see SQL92 for definition 
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param table a table name
     * @return ResultSet - each row is a foreign key column description 
     * @exception SQLException if a database-access error occurs.
     * @see #getImportedKeys 
     */
	ResultSet getExportedKeys(String catalog, String schema,
				String table) throws SQLException;

    /**
     * Get a description of the foreign key columns in the foreign key
     * table that reference the primary key columns of the primary key
     * table (describe how one table imports another's key.) This
     * should normally return a single foreign key/primary key pair
     * (most tables only import a foreign key from a table once.)  They
     * are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and
     * KEY_SEQ.
     *
     * <P>Each foreign key column description has the following columns:
     *  <OL>
     *	<LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
     *	<LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
     *	<LI><B>PKTABLE_NAME</B> String => primary key table name
     *	<LI><B>PKCOLUMN_NAME</B> String => primary key column name
     *	<LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
     *      being exported (may be null)
     *	<LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
     *      being exported (may be null)
     *	<LI><B>FKTABLE_NAME</B> String => foreign key table name
     *      being exported
     *	<LI><B>FKCOLUMN_NAME</B> String => foreign key column name
     *      being exported
     *	<LI><B>KEY_SEQ</B> short => sequence number within foreign key
     *	<LI><B>UPDATE_RULE</B> short => What happens to 
     *       foreign key when primary is updated:
     *      <UL>
     *      <LI> importedNoAction - do not allow update of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - change imported key to agree 
     *               with primary key update
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been updated
     *      <LI> importedKeySetDefault - change imported key to default values 
     *               if its primary key has been updated
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      </UL>
     *	<LI><B>DELETE_RULE</B> short => What happens to 
     *      the foreign key when primary is deleted.
     *      <UL>
     *      <LI> importedKeyNoAction - do not allow delete of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - delete rows that import a deleted key
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been deleted
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      <LI> importedKeySetDefault - change imported key to default if 
     *               its primary key has been deleted
     *      </UL>
     *	<LI><B>FK_NAME</B> String => foreign key name (may be null)
     *	<LI><B>PK_NAME</B> String => primary key name (may be null)
     *	<LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key 
     *      constraints be deferred until commit
     *      <UL>
     *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
     *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition 
     *      <LI> importedKeyNotDeferrable - see SQL92 for definition 
     *      </UL>
     *  </OL>
     *
     * @param primaryCatalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param primarySchema a schema name pattern; "" retrieves those
     * without a schema
     * @param primaryTable the table name that exports the key
     * @param foreignCatalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param foreignSchema a schema name pattern; "" retrieves those
     * without a schema
     * @param foreignTable the table name that imports the key
     * @return ResultSet - each row is a foreign key column description 
     * @exception SQLException if a database-access error occurs.
     * @see #getImportedKeys 
     */
	ResultSet getCrossReference(
		String primaryCatalog, String primarySchema, String primaryTable,
		String foreignCatalog, String foreignSchema, String foreignTable
		) throws SQLException;

    /**
     * Get a description of all the standard SQL types supported by
     * this database. They are ordered by DATA_TYPE and then by how
     * closely the data type maps to the corresponding JDBC SQL type.
     *
     * <P>Each type description has the following columns:
     *  <OL>
     *	<LI><B>TYPE_NAME</B> String => Type name
     *	<LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     *	<LI><B>PRECISION</B> int => maximum precision
     *	<LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal 
     *      (may be null)
     *	<LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal 
            (may be null)
     *	<LI><B>CREATE_PARAMS</B> String => parameters used in creating 
     *      the type (may be null)
     *	<LI><B>NULLABLE</B> short => can you use NULL for this type?
     *      <UL>
     *      <LI> typeNoNulls - does not allow NULL values
     *      <LI> typeNullable - allows NULL values
     *      <LI> typeNullableUnknown - nullability unknown
     *      </UL>
     *	<LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive?
     *	<LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
     *      <UL>
     *      <LI> typePredNone - No support
     *      <LI> typePredChar - Only supported with WHERE .. LIKE
     *      <LI> typePredBasic - Supported except for WHERE .. LIKE
     *      <LI> typeSearchable - Supported for all WHERE ..
     *      </UL>
     *	<LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
     *	<LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
     *	<LI><B>AUTO_INCREMENT</B> boolean => can it be used for an 
     *      auto-increment value?
     *	<LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name 
     *      (may be null)
     *	<LI><B>MINIMUM_SCALE</B> short => minimum scale supported
     *	<LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
     *	<LI><B>SQL_DATA_TYPE</B> int => unused
     *	<LI><B>SQL_DATETIME_SUB</B> int => unused
     *	<LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
     *  </OL>
     *
     * @return ResultSet - each row is a SQL type description 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getTypeInfo() throws SQLException;
	
    /**
     * TYPE NULLABLE - does not allow NULL values.
     */
    int typeNoNulls = 0;

    /**
     * TYPE NULLABLE - allows NULL values.
     */
    int typeNullable = 1;

    /**
     * TYPE NULLABLE - nullability unknown.
     */
    int typeNullableUnknown = 2;

    /**
     * TYPE INFO SEARCHABLE - No support.
     */
	int typePredNone = 0;

    /**
     * TYPE INFO SEARCHABLE - Only supported with WHERE .. LIKE.
     */
	int typePredChar = 1;

    /**
     * TYPE INFO SEARCHABLE -  Supported except for WHERE .. LIKE.
     */
	int typePredBasic = 2;

    /**
     * TYPE INFO SEARCHABLE - Supported for all WHERE ...
     */
	int typeSearchable  = 3;

    /**
     * Get a description of a table's indices and statistics. They are
     * ordered by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
     *
     * <P>Each index column description has the following columns:
     *  <OL>
     *	<LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *	<LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *	<LI><B>TABLE_NAME</B> String => table name
     *	<LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique? 
     *      false when TYPE is tableIndexStatistic
     *	<LI><B>INDEX_QUALIFIER</B> String => index catalog (may be null); 
     *      null when TYPE is tableIndexStatistic
     *	<LI><B>INDEX_NAME</B> String => index name; null when TYPE is 
     *      tableIndexStatistic
     *	<LI><B>TYPE</B> short => index type:
     *      <UL>
     *      <LI> tableIndexStatistic - this identifies table statistics that are
     *           returned in conjuction with a table's index descriptions
     *      <LI> tableIndexClustered - this is a clustered index
     *      <LI> tableIndexHashed - this is a hashed index
     *      <LI> tableIndexOther - this is some other style of index
     *      </UL>
     *	<LI><B>ORDINAL_POSITION</B> short => column sequence number 
     *      within index; zero when TYPE is tableIndexStatistic
     *	<LI><B>COLUMN_NAME</B> String => column name; null when TYPE is 
     *      tableIndexStatistic
     *	<LI><B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending, 
     *      "D" => descending, may be null if sort sequence is not supported; 
     *      null when TYPE is tableIndexStatistic	
     *	<LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then 
     *      this is the number of rows in the table; otherwise, it is the 
     *      number of unique values in the index.
     *	<LI><B>PAGES</B> int => When TYPE is  tableIndexStatisic then 
     *      this is the number of pages used for the table, otherwise it 
     *      is the number of pages used for the current index.
     *	<LI><B>FILTER_CONDITION</B> String => Filter condition, if any.  
     *      (may be null)
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those without a schema
     * @param table a table name  
     * @param unique when true, return only indices for unique values; 
     *     when false, return indices regardless of whether unique or not 
     * @param approximate when true, result is allowed to reflect approximate 
     *     or out of data values; when false, results are requested to be 
     *     accurate
     * @return ResultSet - each row is an index column description 
     * @exception SQLException if a database-access error occurs.
     */
	ResultSet getIndexInfo(String catalog, String schema, String table,
			boolean unique, boolean approximate)
					throws SQLException;

    /**
     * INDEX INFO TYPE - this identifies table statistics that are
     * returned in conjuction with a table's index descriptions
     */
	short tableIndexStatistic = 0;

    /**
     * INDEX INFO TYPE - this identifies a clustered index
     */
	short tableIndexClustered = 1;

    /**
     * INDEX INFO TYPE - this identifies a hashed index
     */
	short tableIndexHashed    = 2;

    /**
     * INDEX INFO TYPE - this identifies some other form of index
     */
	short tableIndexOther     = 3;
 }

