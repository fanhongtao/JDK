/*
 * @(#)SQLException.java	1.6 98/07/01
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
 * <P>The SQLException class provides information on a database access
 * error.
 *
 * <P>Each SQLException provides several kinds of information: 
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, and is available via the getMesage() method
 *   <LI> A "SQLstate" string which follows the XOPEN SQLstate conventions.
 *       The values of the SQLState string as described in the XOPEN SQL spec.
 *   <LI> An integer error code that is vendor specific.  Normally this will
 *	 be the actual error code returned by the underlying database.
 *   <LI> A chain to a next Exception.  This can be used to provided additional
 * 	 error information.
 * </UL>
 */
public class SQLException extends java.lang.Exception {

    /**
     * Construct a fully-specified SQLException  
     *
     * @param reason a description of the exception 
     * @param SQLState an XOPEN code identifying the exception
     * @param vendorCode a database vendor specific exception code
     */
    public SQLException(String reason, String SQLState, int vendorCode) {
	super(reason);
	this.SQLState = SQLState;
	this.vendorCode = vendorCode;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogStream() != null) {
		DriverManager.println("SQLException: SQLState(" + SQLState + 
						") vendor code(" + vendorCode + ")");
		printStackTrace(DriverManager.getLogStream());
	    }
	}
    }


    /**
     * Construct an SQLException with a reason and SQLState;
     * vendorCode defaults to 0.
     *
     * @param reason a description of the exception 
     * @param SQLState an XOPEN code identifying the exception 
     */
    public SQLException(String reason, String SQLState) {
	super(reason);
	this.SQLState = SQLState;
	this.vendorCode = 0;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogStream() != null) {
		printStackTrace(DriverManager.getLogStream());
		DriverManager.println("SQLException: SQLState(" + SQLState + ")");
	    }
	}
    }

    /**
     * Construct an SQLException with a reason; SQLState defaults to
     * null and vendorCode defaults to 0.
     *
     * @param reason a description of the exception 
     */
    public SQLException(String reason) {
	super(reason);
	this.SQLState = null;
	this.vendorCode = 0;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogStream() != null) {
		printStackTrace(DriverManager.getLogStream());
	    }
	}
    }

    /**
     * Construct an SQLException; reason defaults to null, SQLState
     * defaults to null and vendorCode defaults to 0.
     * */
    public SQLException() {
	super();
	this.SQLState = null;
	this.vendorCode = 0;
	if (!(this instanceof SQLWarning)) {
	    if (DriverManager.getLogStream() != null) {
		printStackTrace(DriverManager.getLogStream());
	    }
	}
    }

    /**
     * Get the SQLState
     *
     * @return the SQLState value
     */
    public String getSQLState() {
	return (SQLState);
    }	

    /**
     * Get the vendor specific exception code
     *
     * @return the vendor's error code
     */
    public int getErrorCode() {
	return (vendorCode);
    }

    /**
     * Get the exception chained to this one. 
     *
     * @return the next SQLException in the chain, null if none
     */
    public SQLException getNextException() {
	return (next);
    }

    /**
     * Add an SQLException to the end of the chain.
     *
     * @param ex the new end of the SQLException chain
     */
    public synchronized void setNextException(SQLException ex) {
	SQLException theEnd = this;
	while (theEnd.next != null) {
	    theEnd = theEnd.next;
	}
	theEnd.next = ex;
    }

    private String SQLState;
    private int vendorCode;
    private SQLException next;
}
