/*
 * @(#)AclNotFoundException.java	1.14 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.acl;

/**
 * This is an exception that is thrown whenever a reference is made to a 
 * non-existent ACL (Access Control List).
 *
 * @author 	Satish Dharmaraj
 */
public class AclNotFoundException extends Exception {

    private static final long serialVersionUID = 5684295034092681791L;

    /**
     * Constructs an AclNotFoundException.
     */
    public AclNotFoundException() {
    }

}
