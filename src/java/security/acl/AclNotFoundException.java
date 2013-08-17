/*
 * @(#)AclNotFoundException.java	1.9 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.acl;

/**
 * This is an exception that is thrown whenever a reference is made to a 
 * non-existent ACL (Access Control List).
 *
 * @author 	Satish Dharmaraj
 */
public class AclNotFoundException extends Exception {

    /**
     * Constructs an AclNotFoundException.
     */
    public AclNotFoundException() {
    }

}
