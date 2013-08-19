/*
 * @(#)GetEndPointInfoAgainException.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.connection;

/**
 * This exception is raised by <code>ORBSocketFactory.createSocket</code>.
 * It informs the ORB that it should call
 * <code>ORBSocketFactory.getEndPointInfo</code> again with the
 * given <code>endPointInfo</code> object as an argument (i.e., a cookie).
 *
 */

public class GetEndPointInfoAgainException
    extends Exception
{
    private EndPointInfo endPointInfo;

    public GetEndPointInfoAgainException(EndPointInfo endPointInfo)
    {
        this.endPointInfo = endPointInfo;
    }

    public EndPointInfo getEndPointInfo()
    {
        return endPointInfo;
    }
}
