/*
 * @(#)EnvironmentImpl.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.corba.se.internal.corba;

import org.omg.CORBA.Environment;
import org.omg.CORBA.UserException;
import org.omg.CORBA.ORB;

public class EnvironmentImpl extends Environment {

    private Exception _exc;

    public EnvironmentImpl()
    {
    }
  
    public Exception exception() 
    {
	return _exc;
    }

    public void exception(Exception exc)
    {
	_exc = exc;
    }

    public void clear()
    {
	_exc = null;
    }

}
