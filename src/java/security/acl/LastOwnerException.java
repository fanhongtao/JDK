/*
 * @(#)LastOwnerException.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
