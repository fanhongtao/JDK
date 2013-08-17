/*
 * @(#)SQLWarning.java	1.6 98/07/01
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
 * <P>The SQLWarning class provides information on a database access
 * warnings. Warnings are silently chained to the object whose method
 * caused it to be reported.
 *
 * @see Connection#getWarnings
 * @see Statement#getWarnings
 * @see ResultSet#getWarnings 
 */
public class SQLWarning extends SQLException {

    /**
     * Construct a fully specified SQLWarning.
     *
     * @param reason a description of the warning 
     * @param SQLState an XOPEN code identifying the warning
     * @param vendorCode a database vendor specific warning code
     */
     public SQLWarning(String reason, String SQLstate, int vendorCode) {
	super(reason, SQLstate, vendorCode);
	DriverManager.println("SQLWarning: reason(" + reason + 
			      ") SQLstate(" + SQLstate + 
			      ") vendor code(" + vendorCode + ")");
    }


    /**
     * Construct an SQLWarning with a reason and SQLState;
     * vendorCode defaults to 0.
     *
     * @param reason a description of the warning 
     * @param SQLState an XOPEN code identifying the warning
     */
    public SQLWarning(String reason, String SQLstate) {
	super(reason, SQLstate);
	DriverManager.println("SQLWarning: reason(" + reason + 
				  ") SQLState(" + SQLstate + ")");
    }

    /**
     * Construct an SQLWarning with a reason; SQLState defaults to
     * null and vendorCode defaults to 0.
     *
     * @param reason a description of the warning 
     */
    public SQLWarning(String reason) {
	super(reason);
	DriverManager.println("SQLWarning: reason(" + reason + ")");
    }

    /**
     * Construct an SQLWarning ; reason defaults to null, SQLState
     * defaults to null and vendorCode defaults to 0.
     *
     */
    public SQLWarning() {
	super();
	DriverManager.println("SQLWarning: ");
    }


    /**
     * Get the warning chained to this one
     *
     * @return the next SQLException in the chain, null if none
     */
    public SQLWarning getNextWarning() {
	try {
	    return ((SQLWarning)getNextException());
	} catch (ClassCastException ex) {
	    // The chained value isn't a SQLWarning.
	    // This is a programming error by whoever added it to
	    // the SQLWarning chain.  We throw a Java "Error".
	    throw new Error("SQLWarning chain holds value that is not a SQLWarning");
	}
    }

    /**
     * Add an SQLWarning to the end of the chain.
     *
     * @param w the new end of the SQLException chain
     */
    public void setNextWarning(SQLWarning w) {
	setNextException(w);
    }

}
