/*
 * %W% %E%
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

package com.sun.corba.se.impl.io;

public class TypeMismatchException extends Error
{
    public TypeMismatchException()
    {
        super();
    }

    public TypeMismatchException(String mssg)
    {
        super(mssg);
    }
}
