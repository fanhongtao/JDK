/*
 * @(#)ContextImpl.java	1.21 03/01/23
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

import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.NVList;

public final class ContextImpl extends Context {

    private org.omg.CORBA.ORB _orb;

    public ContextImpl(org.omg.CORBA.ORB orb) 
    {
        _orb = orb;
    }

    public ContextImpl(Context parent) 
    {
        throw new NO_IMPLEMENT();
    }
    
    public String context_name() 
    {
        throw new NO_IMPLEMENT();
    }

    public Context parent() 
    {
        throw new NO_IMPLEMENT();
    }

    public Context create_child(String name) 
    {
        throw new NO_IMPLEMENT();
    }

    public void set_one_value(String propName, Any propValue) 
    {
        throw new NO_IMPLEMENT();
    }

    public void set_values(NVList values) 
    {
        throw new NO_IMPLEMENT();
    }


    public void delete_values(String propName) 
    {
        throw new NO_IMPLEMENT();
    }

    public NVList get_values(String startScope, 
			     int opFlags, 
			     String propName) 
    {
        throw new NO_IMPLEMENT();
    }

};

