/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <P>A connection (session) with a specific
 * database. Within the context of a Connection, SQL statements are
 * executed and results are returned.
 *
 * <P>A Connection's database is able to provide information
 * describing its tables, its supported SQL grammar, its stored
 * procedures, the capabilities of this connection, and so on. This
 * information is obtained with the <code>getMetaData</code> method.
 *
 * <P><B>Note:</B> By default the Connection automatically commits
 * changes after executing each statement. If auto commit has been
 * disabled, the method <code>commit</code> must be called explicitly;
 * otherwise, database changes will not be saved.
 *
 * @see DriverManager#getConnection
 * @see Statement 
 * @see ResultSet
 * @see DatabaseMetaData
 <P>
 * Methods that are new in the JDBC 2.0 API are tagged @since 1.2. 
 */
public interface Connection {

    /**
	 * Creates a <code>Statement</code> object for sending
	 * SQL statements to the database.
     * SQL statements without parameters are normally
     * executed using Statement objects. If the same SQL statement 
     * is executed many times, it is more efficient to use a 
     * <code>PreparedStatement</code> object.
	 *<P>
     *
     * Result sets created using the returned <code>Statement</code>
	 * object will by default have forward-only type and read-only concurrency.
     *
     * @return a new Statement object 
     * @exception SQLException if a database access error occurs
     */
    Statement createStatement() throws SQLException;

    /**
	 * Creates a <code>PreparedStatement</code> object for sending
	 * parameterized SQL statements to the database.
	 * 
     * A SQL statement with or without IN parameters can be
     * pre-compiled and stored in a PreparedStatement object. This
     * object can then be used to efficiently execute this statement
     * multiple times.
     *
     * <P><B>Note:</B> This method is optimized for handling
     * parametric SQL statements that benefit from precompilation. If
     * the driver supports precompilation,
	 * the method <code>prepareStatement</code> will send
     * the statement to the database for precompilation. Some drivers
     * may not support precompilation. In this case, the statement may
     * not be sent to the database until the <code>PreparedStatement</code> is
     * executed.  This has no direct effect on users; however, it does
     * affect which method throws certain SQLExceptions.
     *
     *
     * Result sets created using the returned PreparedStatement will have
     * forward-only type and read-only concurrency, by default.
     *
     * @param sql a SQL statement that may contain one or more '?' IN
     * parameter placeholders
     * @return a new PreparedStatement object containing the
     * pre-compiled statement 
     * @exception SQLException if a database access error occurs
     */
    PreparedStatement prepareStatement(String sql)
	    throws SQLException;

    /**
	 * Creates a <code>CallableStatement</code> object for calling
	 * database stored procedures.
     * The <code>CallableStatement</code> object provides
     * methods for setting up its IN and OUT parameters, and
     * methods for executing the call to a stored procedure.
     *
     * <P><B>Note:</B> This method is optimized for handling stored
     * procedure call statements. Some drivers may send the call
     * statement to the database when the method <code>prepareCall</code>
	 * is done; others
     * may wait until the <code>CallableStatement</code> object
	 * is executed. This has no
     * direct effect on users; however, it does affect which method
     * throws certain SQLExceptions.
     *
     *
     * Result sets created using the returned CallableStatement will have
     * forward-only type and read-only concurrency, by default.
     *
     * @param sql a SQL statement that may contain one or more '?'
     * parameter placeholders. Typically this  statement is a JDBC
     * function call escape string.
     * @return a new CallableStatement object containing the
     * pre-compiled SQL statement 
     * @exception SQLException if a database access error occurs
     */
    CallableStatement prepareCall(String sql) throws SQLException;
						
    /**
	 * Converts the given SQL statement into the system's native SQL grammar.
     * A driver may convert the JDBC sql grammar into its system's
     * native SQL grammar prior to sending it; this method returns the
     * native form of the statement that the driver would have sent.
     *
     * @param sql a SQL statement that may contain one or more '?'
     * parameter placeholders
     * @return the native form of this statement
     * @exception SQLException if a database access error occurs
     */
    String nativeSQL(String sql) throws SQLException;

    /**
	 * Sets this connection's auto-commit mode.
     * If a connection is in auto-commit mode, then all its SQL
     * statements will be executed and committed as individual
     * transactions.  Otherwise, its SQL statements are grouped into
     * transactions that are terminated by a call to either
	 * the method <code>commit</code> or the method <code>rollback</code>.
	 * By default, new connections are in auto-commit
     * mode.
     *
     * The commit occurs when the statement completes or the next
     * execute occurs, whichever comes first. In the case of
     * statements returning a ResultSet, the statement completes when
     * the last row of the ResultSet has been retrieved or the
     * ResultSet has been closed. In advanced cases, a single
     * statement may return multiple results as well as output
     * parameter values. In these cases the commit occurs when all results and
     * output parameter values have been retrieved.
     *
     * @param autoCommit true enables auto-commit; false disables
     * auto-commit.  
     * @exception SQLException if a database access error occurs
     */
    void setAutoCommit(boolean autoCommit) throws SQLException;

    /**
     * Gets the current auto-commit state.
     *
     * @return the current state of auto-commit mode
     * @exception SQLException if a database access error occurs
     * @see #setAutoCommit 
     */
    boolean getAutoCommit() throws SQLException;

    /**
     * Makes all changes made since the previous
     * commit/rollback permanent and releases any database locks
     * currently held by the Connection. This method should be
     * used only when auto-commit mode has been disabled.
     *
     * @exception SQLException if a database access error occurs
     * @see #setAutoCommit 
     */
    void commit() throws SQLException;

    /**
     * Drops all changes made since the previous
     * commit/rollback and releases any database locks currently held
     * by this Connection. This method should be used only when auto-
     * commit has been disabled.
     *
     * @exception SQLException if a database access error occurs
     * @see #setAutoCommit 
     */
    void rollback() throws SQLException;

    /**
     * Releases a Connection's database and JDBC resources
	 * immediately instead of waiting for
     * them to be automatically released.
     *
     * <P><B>Note:</B> A Connection is automatically closed when it is
     * garbage collected. Certain fatal errors also result in a closed
     * Connection.
     *
     * @exception SQLException if a database access error occurs
     */
    void close() throws SQLException;

    /**
     * Tests to see if a Connection is closed.
     *
     * @return true if the connection is closed; false if it's still open
     * @exception SQLException if a database access error occurs
     */
    boolean isClosed() throws SQLException;

    //======================================================================
    // Advanced features:

    /**
	 * Gets the metadata regarding this connection's database.
     * A Connection's database is able to provide information
     * describing its tables, its supported SQL grammar, its stored
     * procedures, the capabilities of this connection, and so on. This
     * information is made available through a DatabaseMetaData
     * object.
     *
     * @return a DatabaseMetaData object for this Connection 
     * @exception SQLException if a database access error occurs
     */
    DatabaseMetaData getMetaData() throws SQLException;

    /**
     * Puts this connection in read-only mode as a hint to enable 
     * database optimizations.
     *
     * <P><B>Note:</B> This method cannot be called while in the
     * middle of a transaction.
     *
     * @param readOnly true enables read-only mode; false disables
     * read-only mode.  
     * @exception SQLException if a database access error occurs
     */
    void setReadOnly(boolean readOnly) throws SQLException;

    /**
     * Tests to see if the connection is in read-only mode.
     *
     * @return true if connection is read-only and false otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean isReadOnly() throws SQLException;

    /**
     * Sets a catalog name in order to select 	
     * a subspace of this Connection's database in which to work.
     * If the driver does not support catalogs, it will
     * silently ignore this request.
     *
     * @exception SQLException if a database access error occurs
     */
    void setCatalog(String catalog) throws SQLException;

    /**
     * Returns the Connection's current catalog name.
     *
     * @return the current catalog name or null
     * @exception SQLException if a database access error occurs
     */
    String getCatalog() throws SQLException;

    /**
     * Indicates that transactions are not supported. 
     */
    int TRANSACTION_NONE	     = 0;

    /**
     * Dirty reads, non-repeatable reads and phantom reads can occur.
     * This level allows a row changed by one transaction to be read
     * by another transaction before any changes in that row have been
     * committed (a "dirty read").  If any of the changes are rolled back, 
     * the second transaction will have retrieved an invalid row.
     */
    int TRANSACTION_READ_UNCOMMITTED = 1;

    /**
     * Dirty reads are prevented; non-repeatable reads and phantom
     * reads can occur.  This level only prohibits a transaction
     * from reading a row with uncommitted changes in it.
     */
    int TRANSACTION_READ_COMMITTED   = 2;

    /**
     * Dirty reads and non-repeatable reads are prevented; phantom
     * reads can occur.  This level prohibits a transaction from
     * reading a row with uncommitted changes in it, and it also
     * prohibits the situation where one transaction reads a row,
     * a second transaction alters the row, and the first transaction
     * rereads the row, getting different values the second time
     * (a "non-repeatable read").
     */
    int TRANSACTION_REPEATABLE_READ  = 4;

    /**
     * Dirty reads, non-repeatable reads and phantom reads are prevented.
     * This level includes the prohibitions in
     * TRANSACTION_REPEATABLE_READ and further prohibits the 
     * situation where one transaction reads all rows that satisfy
     * a WHERE condition, a second transaction inserts a row that
     * satisfies that WHERE condition, and the first transaction
     * rereads for the same condition, retrieving the additional
     * "phantom" row in the second read.
     */
    int TRANSACTION_SERIALIZABLE     = 8;

    /**
     * Attempts to change the transaction
     * isolation level to the one given.
	 * The constants defined in the interface <code>Connection</code>
	 * are the possible transaction isolation levels.
     *
     * <P><B>Note:</B> This method cannot be called while
     * in the middle of a transaction.
     *
     * @param level one of the TRANSACTION_* isolation values with the
     * exception of TRANSACTION_NONE; some databases may not support
     * other values
     * @exception SQLException if a database access error occurs
     * @see DatabaseMetaData#supportsTransactionIsolationLevel 
     */
    void setTransactionIsolation(int level) throws SQLException;

    /**
     * Gets this Connection's current transaction isolation level.
     *
     * @return the current TRANSACTION_* mode value
     * @exception SQLException if a database access error occurs
     */
    int getTransactionIsolation() throws SQLException;

    /**
     * Returns the first warning reported by calls on this Connection.
     *
     * <P><B>Note:</B> Subsequent warnings will be chained to this
     * SQLWarning.
     *
     * @return the first SQLWarning or null 
     * @exception SQLException if a database access error occurs
     */
    SQLWarning getWarnings() throws SQLException;

    /**
     * Clears all warnings reported for this <code>Connection</code> object.	
     * After a call to this method, the method <code>getWarnings</code>
	 * returns null until a new warning is
     * reported for this Connection.  
     *
     * @exception SQLException if a database access error occurs
     */
    void clearWarnings() throws SQLException;


    //--------------------------JDBC 2.0-----------------------------

    /**
     *
	 * Creates a <code>Statement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
     * This method is the same as the <code>createStatement</code> method
	 * above, but it allows the default result set
     * type and result set concurrency type to be overridden.
     *
     * @param resultSetType a result set type; see ResultSet.TYPE_XXX
     * @param resultSetConcurrency a concurrency type; see ResultSet.CONCUR_XXX
     * @return a new Statement object 
     * @exception SQLException if a database access error occurs
	 * @since 1.2
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API</a>
     */
    Statement createStatement(int resultSetType, int resultSetConcurrency) 
      throws SQLException;

    /**
     *
	 * Creates a <code>PreparedStatement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
     * This method is the same as the <code>prepareStatement</code> method
	 * above, but it allows the default result set
     * type and result set concurrency type to be overridden.
     *
     * @param resultSetType a result set type; see ResultSet.TYPE_XXX
     * @param resultSetConcurrency a concurrency type; see ResultSet.CONCUR_XXX
     * @return a new PreparedStatement object containing the
     * pre-compiled SQL statement 
     * @exception SQLException if a database access error occurs
	 * @since 1.2
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API</a>
     */
     PreparedStatement prepareStatement(String sql, int resultSetType, 
					int resultSetConcurrency)
       throws SQLException;

    /**
     *
	 * Creates a <code>CallableStatement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
     * This method is the same as the <code>prepareCall</code> method
	 * above, but it allows the default result set
     * type and result set concurrency type to be overridden.
     *
     * @param resultSetType a result set type; see ResultSet.TYPE_XXX
     * @param resultSetConcurrency a concurrency type; see ResultSet.CONCUR_XXX
     * @return a new CallableStatement object containing the
     * pre-compiled SQL statement 
     * @exception SQLException if a database access error occurs
	 * @since 1.2
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API</a>
     */
    CallableStatement prepareCall(String sql, int resultSetType, 
				 int resultSetConcurrency) throws SQLException;

    /**
     *
     * Gets the type map object associated with this connection.
     * Unless the application has added an entry to the type map,
	 * the map returned will be empty.
	 *
	 * @return the <code>java.util.Map</code> object associated 
	 *         with this <code>Connection</code> object
	 * @since 1.2
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API</a>
     */
    java.util.Map getTypeMap() throws SQLException;

    /**
     * Installs the given type map as the type map for
     * this connection.  The type map will be used for the
	 * custom mapping of SQL structured types and distinct types.
	 *
	 * @param the <code>java.util.Map</code> object to install
	 *        as the replacement for this <code>Connection</code>
	 *        object's default type map
	 * @since 1.2
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API</a>
     */
    void setTypeMap(java.util.Map map) throws SQLException;
}







