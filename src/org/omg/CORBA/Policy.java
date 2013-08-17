/*
 * @(#)Policy.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */


package org.omg.CORBA;


/** The Policy interface provides a mechanism for ORBs and Object Services
 *  to allow access to certain choices that affect their operation.
 *  This information is accessed in a structured manner using interfaces
 *  derived from the org.omg.CORBA.Policy interface.
 */

public interface Policy extends org.omg.CORBA.Object
{
    /** Returns the constant value that corresponds
    * to the type of the policy object. The values of PolicyTypes are allocated by OMG.
    * New values for PolicyType should be obtained from OMG by sending mail
    * to request@omg.org. In general the constant values that are allocated
    * are defined in conjunction with the definition of the corresponding Policy object.
    * @return the constant value that corresponds to the type of the policy object
    */
    public int policy_type();

    /** Copies this policy object. The copy does not retain
    *  any relationships that the policy had with any domain, or object.
    * @return the copy of the policy object
    */
    public org.omg.CORBA.Policy copy();

    /** Destroys this policy object. It is the responsibility
     *  of the policy object to determine whether it can be destroyed.
     */
    public void destroy();
}
