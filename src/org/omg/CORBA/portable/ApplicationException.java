/*
 * @(#)ApplicationException.java	1.2 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package org.omg.CORBA.portable;

public class ApplicationException extends Exception {
    /**
     * Construct an ApplicationException object.
     * @param id the repository id of the user exception.
     * @param ins the stream which contains the user exception data.
     */
    public ApplicationException(String id,
				InputStream ins) {
	this.id = id;
	this.ins = ins;
    }

    public String getId() {
	return id;
    }

    public InputStream getInputStream() {
	return ins;
    }

    private String id;
    private InputStream ins;
}
