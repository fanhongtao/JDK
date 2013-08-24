/*
 * @(#)RunTime.java	1.16 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package org.omg.SendingContext;

/** Defines the base class that represents the Sending Context of a 
* request.  The sending context provides access to information about 
* the runtime environment of the originator of a GIOP message.  For example,
* when a value type is marshalled on a GIOP Request message, the receiver
* of the value type may need to ask the sender about the CodeBase for the
* implementation of the value type.
* @version 1.16 11/17/05
*/
public interface RunTime extends RunTimeOperations, org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity 
{
} 
