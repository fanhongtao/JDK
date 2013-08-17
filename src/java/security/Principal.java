/*
 * @(#)Principal.java	1.16 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This interface represents a principal. A principal can be an
 * individual, a corporation, a program thread; anything which can
 * have an identity. See the <a href="../guide/security/Acl.html">ACL
 * white paper</a> for more information.
 *
 * @see Identity
 * @see Certificate
 * @see java.security.acl.Acl
 * @see java.security.acl.Group
 *
 * @version 1.16, 01/12/10
 * @author Satish Dharmaraj 
 */
public interface Principal {

    /**
     * Compares this principal to the specified object.  Returns true
     * if the object passed in matches the principal represented by
     * the implementation of this interface.  
     *
     * @param another the principal to compare with.
     * 
     * @return true if
     * the principal passed in is the same as that encapsulated by
     * this principal, false otherwise.
     */
    public boolean equals(Object another);
    
    /**
     * Returns a string representation of this principal.  
     *
     * @return a string representation of this principal.
     */
    public String toString();

    /**
     * Returns a hashcode for this principal.
     *
     * @return a hashcode for this principal.
     */
    public int hashCode();

    /**  
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    public String getName();
}
