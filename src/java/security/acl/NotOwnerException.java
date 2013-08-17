/*
 * @(#)NotOwnerException.java	1.11 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
