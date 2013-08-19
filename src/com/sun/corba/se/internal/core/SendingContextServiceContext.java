/*
 * @(#)SendingContextServiceContext.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.core.ServiceContext ;
import com.sun.corba.se.internal.core.IOR ;
import com.sun.corba.se.internal.core.MarshalOutputStream ;

public class SendingContextServiceContext extends ServiceContext {
    public SendingContextServiceContext( IOR ior )
    {
	this.ior = ior ;
    }

    public SendingContextServiceContext(InputStream is, GIOPVersion gv)
    {
	super(is, gv) ;
	ior = new IOR( in ) ;
    }

    // Required SERVICE_CONTEXT_ID and getId definitions
    public static final int SERVICE_CONTEXT_ID = 6 ;
    public int getId() { return SERVICE_CONTEXT_ID ; }

    public void writeData( OutputStream os ) throws SystemException
    {
	ior.write( os ) ;
    }

    public IOR getIOR() 
    {
	return ior ;
    }

    private IOR ior = null ;

    public String toString() 
    {
	return "SendingContexServiceContext[ ior=" + ior + " ]" ;
    }
}
