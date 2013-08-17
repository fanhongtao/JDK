/*
 * @(#)NotOwnerException.java	1.7 98/07/01
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

package java.security.acl;

/**
 * This is an exception that is thrown whenever the modification of an object  
 * (such as an Access Control List) is only allowed to be done by an owner of
 * the object, but the Principal attempting the modification is not an owner.  
 *
 * @author 	Satish Dharmaraj
 */
public class NotOwnerException extends Exception {

    /**
     * Constructs a NotOwnerException.
     */
    public NotOwnerException() {
    }
}
