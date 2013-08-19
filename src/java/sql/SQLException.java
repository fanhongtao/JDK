/*
 * @(#)SQLException.java	1.26 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <P>An exception that provides information on a database access
 * error or other errors.
 *
 * <P>Each <code>SQLException</code> provides several kinds of information: 
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, available via the method <code>getMesage</code>.
 *   <LI> a "SQLstate" string, which follows either the XOPEN SQLstate conventions
 *        or the SQL 99 conventions.
 *       The values of the SQLState string are described in the appropriate spec.
 *       The <code>DatabaseMetaData</code> method <code>getSQLStateType</code>
 *       can be used to discover whether the driver returns the XOPEN type or
 *       the SQL 99 type.
 *   <LI> an integer error code that is specific to each vendor.  Normally this will
 *	 be the actual error code returned by the underlying database.
 *   <LI> a chain to a next Exception.  This can be used to provide additional
 * 	 error information.
 * </UL>
 */
public class SQLException extends java.lang.Exception {

    /**
     * Constructs a fully-specified <code>SQLException</code> object.  
     *
     * @param reason a description of the exception 
     * @param SQLState an XOPEN or SQL 99 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     */
    public SQLException(String reason, String SQLState, int vendorCode) {
	super(reason);
	this.SQLState = SQLState;
	this.vendorCode = vendorCode;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogWriter() != null) {
		DriverManager.println("SQLException: SQLState(" + SQLState + 
						") vendor code(" + vendorCode + ")");
		printStackTrace(DriverManager.getLogWriter());
	    }
	}
    }


    /**
     * Constructs an <code>SQLException</code> object with the given reason and 
     * SQLState; the <code>vendorCode</code> field defaults to 0.
     *
     * @param reason a description of the exception 
     * @param SQLState an XOPEN or SQL 99 code identifying the exception 
     */
    public SQLException(String reason, String SQLState) {
	super(reason);
	this.SQLState = SQLState;
	this.vendorCode = 0;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogWriter() != null) {
		printStackTrace(DriverManager.getLogWriter());
		DriverManager.println("SQLException: SQLState(" + SQLState + ")");
	    }
	}
    }

    /**
     * Constructs an <code>SQLException</code> object with a reason;
     * the <code>SQLState</code> field defaults to <code>null</code>, and 
     * the <code>vendorCode</code> field defaults to 0.
     *
     * @param reason a description of the exception 
     */
    public SQLException(String reason) {
	super(reason);
	this.SQLState = null;
	this.vendorCode = 0;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogWriter() != null) {
		printStackTrace(DriverManager.getLogWriter());
	    }
	}
    }

    /**
     * Constructs an <code>SQLException</code> object;
     * the <code>reason</code> field defaults to null, 
     * the <code>SQLState</code> field defaults to <code>null</code>, and 
     * the <code>vendorCode</code> field defaults to 0.
     *
     */
    public SQLException() {
	super();
	this.SQLState = null;
	this.vendorCode = 0;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogWriter() != null) {
		printStackTrace(DriverManager.getLogWriter());
	    }
	}
    }

    /**
     * Retrieves the SQLState for this <code>SQLException</code> object.
     *
     * @return the SQLState value
     */
    public String getSQLState() {
	return (SQLState);
    }	

    /**
     * Retrieves the vendor-specific exception code
     * for this <code>SQLException</code> object.
     *
     * @return the vendor's error code
     */
    public int getErrorCode() {
	return (vendorCode);
    }

    /**
     * Retrieves the exception chained to this 
     * <code>SQLException</code> object.
     *
     * @return the next <code>SQLException</code> object in the chain; 
     *         <code>null</code> if there are none
     * @see #setNextException
     */
    public SQLException getNextException() {
	return (next);
    }

    /**
     * Adds an <code>SQLException</code> object to the end of the chain.
     *
     * @param ex the new exception that will be added to the end of
     *            the <code>SQLException</code> chain
     * @see #getNextException
     */
    public synchronized void setNextException(SQLException ex) {
	SQLException theEnd = this;
	while (theEnd.next != null) {
	    theEnd = theEnd.next;
	}
	theEnd.next = ex;
    }

	/**
	 * @serial
	 */
    private String SQLState;

	/**
	 * @serial
	 */
    private int vendorCode;

	/**
	 * @serial
	 */
    private SQLException next;
}
