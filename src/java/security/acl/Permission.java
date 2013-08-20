/*
 * @(#)Permission.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.acl;


/**
 * This interface represents a permission, such as that used to grant
 * a particular type of access to a resource.
 *
 * @author Satish Dharmaraj
 */
public interface Permission {

    /**
     * Returns true if the object passed matches the permission represented 
     * in this interface.
     * 
     * @param another the Permission object to compare with.
     * 
     * @return true if the Permission objects are equal, false otherwise
     */
    public boolean equals(Object another);
    
    /**
     * Prints a string representation of this permission.
     * 
     * @return the string representation of the permission.
     */
    public String toString();

}
