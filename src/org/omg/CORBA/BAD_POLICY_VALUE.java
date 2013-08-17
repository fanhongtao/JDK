/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
* Contains the value used to indicate a policy value that is
* incorrect for a valid policy type in a call to the
* <code>create_policy</code> method defined in the ORB class.
*
* @version 1.8 02/06/02
*/
public interface BAD_POLICY_VALUE {
    /** 
    * The value used to represent a bad policy value error 
    * in a <code>PolicyError</code> exception.
    * @see org.omg.CORBA.PolicyError
    */
    final short value = (short) (3L);
};
