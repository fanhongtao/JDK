/*
 * @(#)CorbaContactInfoList.java	1.16 04/06/21
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR ;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher ;

import com.sun.corba.se.pept.transport.ContactInfoList ;

/**
 * @author Harold Carr
 */
public interface CorbaContactInfoList
    extends
	ContactInfoList
{
    public void setTargetIOR(IOR ior);
    public IOR getTargetIOR();

    public void setEffectiveTargetIOR(IOR locatedIor);
    public IOR getEffectiveTargetIOR();

    public LocalClientRequestDispatcher getLocalClientRequestDispatcher();

    public int hashCode();
}

// End of file.
