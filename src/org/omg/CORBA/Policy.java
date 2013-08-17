/*
 * @(#)Policy.java	1.2 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


package org.omg.CORBA;


/** The Policy interface provides a mechanism for ORBs and Object Services
 *  to allow access to certain choices that affect their operation.
 *  This information is accessed in a structured manner using interfaces
 *  derived from the org.omg.CORBA.Policy interface.
 */

public interface Policy extends org.omg.CORBA.Object
{
    /** This readonly attribute returns the constant value
     *  that corresponds to the type of the Policy object.
     */
    public int policy_type();

    /** This operation copies the policy object. The copy does not retain
     *  any relationships that the policy had with any domain, or object.
     */
    public org.omg.CORBA.Policy copy();

    /** This operation destroys the policy object. It is the responsibility
     *  of the policy object to determine whether it can be destroyed.
     */
    public void destroy();
}
