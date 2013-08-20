/*
 * @(#)NullServant.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.oa ;

import org.omg.CORBA.SystemException ;

/** NullServant is used to represent a null servant returned 
 * OAInvocationInfo after a 
 * ObjectAdapter.getInvocationServant( OAInvocationInfo ) call.
 * If the getInvocationServant call could not locate a servant
 * for the ObjectId in the OAInvocationInfo, getServantContainer
 * will contain a NullServant.  Later stages of the request 
 * dispatch may choose either to throw the exception or perform
 * some other action in response to the NullServant result.
 */
public interface NullServant 
{
    /** Obtain the exception that is associated with this 
     * NullServant instance.
     */
    SystemException getException() ;
}
