/*
 * @(#)ObjectKeyTemplateBase.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)ObjectKeyTemplateBase.java	1.10 00/10/11

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import com.sun.corba.se.internal.core.ORBVersion ;
import org.omg.CORBA.ORB ;

/**
 * @author 
 */
public abstract class ObjectKeyTemplateBase implements ObjectKeyTemplate 
{
    private ORBVersion version ;
    private int magic ;
    private int scid ;
    private int serverid ;

    /**
     * @param scid
     * @param serverid
     * @return 
     * @exception 
     * @author 
     * @roseuid 3915FF9803AA
     */
    public ObjectKeyTemplateBase( int magic, int scid, int serverid) 
    {
	this.magic = magic ;
	this.scid = scid ;
	this.serverid = serverid ;
    }

    public boolean equals( Object obj ) 
    {
	if (!(obj instanceof ObjectKeyTemplateBase))
	    return false ;

	ObjectKeyTemplateBase other = (ObjectKeyTemplateBase)obj ;

	return (magic == other.magic) && (scid == other.scid) && 
	    (serverid == other.serverid) && (version.equals( other.version )) ;
    }
   
    public byte[] getAdapterId( ORB orb )
    {
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	write( os ) ;
	byte[] result = os.toByteArray() ;
	return result ;
    }

    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 3915FFEC0102
     */
    public int getSubcontractId() 
    {
	return scid ;
    }

    public int getServerId()
    {
	return serverid ;
    }
/*
    public byte[] getId( InputStream is )
    {
	int len = is.read_long() ;
	byte[] result = new byte[ len ] ;

	is.read_octet_array( result, 0, len ) ;

	return result ;
    }
*/
    /**
     * @param objectId
     * @param os
     * @return void
     * @exception 
     * @author 
     * @roseuid 3915FFEB0287
     */
    public void write(ObjectId objectId, OutputStream os) 
    {
	write( os ) ;
	byte[] data = objectId.getId() ;
	os.write_long( data.length ) ;
	os.write_octet_array( data, 0, data.length ) ;
    }

    abstract protected void write( OutputStream os ) ;
   
    protected int getMagic()
    {
	return magic ;
    }

    // All subclasses should set the version in their constructors.
    protected void setORBVersion( ORBVersion version )
    {
	this.version = version ;
    }

    public ORBVersion getORBVersion()
    {
	return version ;
    }

    protected byte[] readObjectKey( InputStream is ) 
    {
	int len = is.read_long() ;
	byte[] result = new byte[len] ;
	is.read_octet_array( result, 0, len ) ;
	return result ;
    }
}
