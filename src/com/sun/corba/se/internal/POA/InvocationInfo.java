/*
 * @(#)InvocationInfo.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.POA;

import java.util.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class InvocationInfo{

    // These fields are to support standard OMG APIs.

    private POA    _poa; 
    private byte[] _id;

    /// These fields are to support the POA implementation.

    private Servant      _servant;
    private CookieHolder _cookieHolder;
    private String       _operation;

    private boolean _preInvokeCalled;
    private boolean _postInvokeCalled;

    public InvocationInfo(POA poa, byte[] id, CookieHolder cookieHolder,
			  String operation)
    {
        _poa = poa;
        _id  = id;
	_servant      = null;
	_cookieHolder = cookieHolder;
	_operation    = operation;

	_preInvokeCalled  = false;
	_postInvokeCalled = false;
    }

    //getters
    public POA     poa()             { return _poa;}
    public byte[]  id()              { return _id ;}
    public Servant      getServant()      { return _servant; }
    public CookieHolder getCookieHolder() { return _cookieHolder; }
    public String       getOperation()    { return _operation; }
    public boolean preInvokeCalled()  { return _preInvokeCalled; }
    public boolean postInvokeCalled() { return _postInvokeCalled; }

    //setters
    public void setPreInvokeCalled()  { _preInvokeCalled  = true; }
    public void setPostInvokeCalled() { _postInvokeCalled = true; }
    public void setServant(Servant servant) { _servant = servant; }
}
