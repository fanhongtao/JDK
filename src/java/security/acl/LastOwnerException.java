/*
 * @(#)LastOwnerException.java	1.11 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.acl;

/**
 * This is an exception that is thrown whenever an attempt is made to delete
 * the last owner of an Access Control List.  
 *  
 * @see java.security.acl.Owner#deleteOwner
 *
 * @author Satish Dharmaraj 
 */
public class LastOwnerException extends Exception {

    /**
     * Constructs a LastOwnerException.
     */
    public LastOwnerException() {
    }
}
