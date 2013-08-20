/*
 * @(#)PrincipalImpl.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.corba.se.impl.corba;


public class PrincipalImpl extends org.omg.CORBA.Principal
{
    private byte[] value;

    public void name(byte[] value)
    {
	this.value = value;
    }

    public byte[] name()
    {
	return value;
    }
}
