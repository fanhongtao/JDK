/*
 * @(#)InvalidName.java	1.13 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA.ORBPackage;

/**
 * The <code>InvalidName</code> exception is raised when
 * <code>ORB.resolve_initial_references</code> is passed a name
 * for which there is no initial reference.
 *
 * @see org.omg.CORBA.ORB#resolve_initial_references(String)
 * @version 1.6, 03/18/98
 * @since   JDK1.2
 */

public class InvalidName extends org.omg.CORBA.UserException {
    /**
     * Constructs an <code>InvalidName</code> exception with no reason message.
     */
    public InvalidName() {
	super();
    }

    /**
     * Constructs an <code>InvalidName</code> exception with the specified 
     * reason message.
     * @param reason the String containing a reason message
     */
    public InvalidName(String reason) {
	super(reason);
    }
}
