/*
 * @(#)Principal.java	1.12 97/01/29
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 * @version 1.12, 97/06/20
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
