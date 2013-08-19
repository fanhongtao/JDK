/*
 * @(#)UnknownServiceContext.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.internal.core.ServiceContext ;

public class UnknownServiceContext extends ServiceContext {
    public UnknownServiceContext( int id, byte[] data ) 
    {
	this.id = id ;
	this.data = data ;
    }

    public UnknownServiceContext( int id, InputStream is ) 
    {
	this.id = id ;

	int len = is.read_long();
	data = new byte[len];
	is.read_octet_array(data,0,len);
    }

    public int getId() { return id ; }

    public void writeData( OutputStream os ) throws SystemException
    {
    }

    public void write( OutputStream os , GIOPVersion gv) 
        throws SystemException 
    {
	os.write_long( id ) ;
	os.write_long( data.length ) ;
	os.write_octet_array( data, 0, data.length ) ;
    }

    public byte[] getData() 
    {
	return data ;
    }

    private int id = -1 ;
    private byte[] data = null ;
}

