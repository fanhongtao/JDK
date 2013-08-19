/*
 * @(#)DefaultSocketFactory.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.connection.EndPointInfo;
import com.sun.corba.se.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.connection.ORBSocketFactory;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.iiop.EndPointImpl;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.IIOPAddress ;
import com.sun.corba.se.internal.orbutil.MinorCodes;

public class DefaultSocketFactory 
    implements 
	ORBSocketFactory
{
    public DefaultSocketFactory()
    {
    }

    public ServerSocket createServerSocket(String type, int port)
	throws
	    IOException
    {
	if (! type.equals(ORBSocketFactory.IIOP_CLEAR_TEXT)) {
	    throw new COMM_FAILURE(
                "DefaultSocketFactory.createServerSocket only handles " +
		"IIOP_CLEAR_TEXT, given: " + type,
		MinorCodes.DEFAULT_CREATE_SERVER_SOCKET_GIVEN_NON_IIOP_CLEAR_TEST,
		CompletionStatus.COMPLETED_NO);
	}
				   
	return new ServerSocket(port);
    }

    public EndPointInfo getEndPointInfo(ORB orb,
					IOR ior,
					EndPointInfo endPointInfo)
    {
        IIOPProfileTemplate temp = ior.getProfile().getTemplate() ;
	IIOPAddress primary = temp.getPrimaryAddress() ;

	return new EndPointImpl(ORBSocketFactory.IIOP_CLEAR_TEXT,
				primary.getPort(),
				primary.getHost().toLowerCase());
    }

    public Socket createSocket(EndPointInfo endPointInfo)
	throws
	    IOException,
	    GetEndPointInfoAgainException
    {
	return new Socket(endPointInfo.getHost(), endPointInfo.getPort());
    }
}
