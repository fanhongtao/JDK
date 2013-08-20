/*
 * @(#)SocketFactoryContactInfoListImpl.java	1.5 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.legacy.connection;

import java.util.Iterator;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.impl.transport.CorbaContactInfoListIteratorImpl;

/**
 * @author Harold Carr
 */
public class SocketFactoryContactInfoListImpl 
    extends
	CorbaContactInfoListImpl
{
    // XREVISIT - is this used?
    public SocketFactoryContactInfoListImpl(ORB orb)
    {
	super(orb);
    }

    public SocketFactoryContactInfoListImpl(ORB orb, IOR targetIOR)
    {
	super(orb, targetIOR);
    }

    ////////////////////////////////////////////////////
    //
    // pept.transport.ContactInfoList
    //

    public Iterator iterator()
    {
	return new SocketFactoryContactInfoListIteratorImpl(orb, this);
    }
}

// End of file.
