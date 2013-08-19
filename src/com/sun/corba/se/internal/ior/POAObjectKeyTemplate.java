/*
 * @(#)POAObjectKeyTemplate.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)POAObjectKeyTemplate.java	1.21 03/01/23

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.ActivationIDL.POANameHelper ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionFactory ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.POAId ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.OctetSeqHolder ;
import java.util.Iterator ;

/**
 * @author 
 */
public final class POAObjectKeyTemplate extends NewObjectKeyTemplateBase 
{
    private String orbid ;
    private POAId poaid ;
    private byte[] adapterId ;

    public boolean equals( Object obj )
    {
	if (!super.equals( obj ))
	    return false ;

	if (!(obj instanceof POAObjectKeyTemplate))
	    return false ;

	POAObjectKeyTemplate other = (POAObjectKeyTemplate)obj ;

	return (orbid.equals( other.orbid )) && (poaid.equals( other.poaid )) ;
    }
   
    public byte[] getAdapterId( ORB orb )
    {
	return (byte[])(adapterId.clone()) ;
    }

    private byte[] computeAdapterId()
    {
	// write out serverid, orbid, poaid
	ByteBuffer buff = new ByteBuffer() ;

	buff.append( getServerId() ) ;
	buff.append( orbid ) ;

	buff.append( poaid.getNumLevels() ) ;
	Iterator iter = poaid.iterator() ;
	while (iter.hasNext()) {
	    String comp = (String)(iter.next()) ;
	    buff.append( comp ) ;
	}

	buff.trimToSize() ;

	return buff.toArray() ;
    }

    public POAObjectKeyTemplate( int magic, int scid, CDRInputStream is,
	OctetSeqHolder osh ) 
    {
	// Read server id from input stream for superclass constructor
	super( magic, scid, is.read_long() ) ;
	orbid = is.read_string() ;
	String[] temp = POANameHelper.read( is ) ;
	poaid = new POAIdArray( temp ) ;
	
	osh.value = readObjectKey( is ) ;
	setVersion( is ) ;

	adapterId = computeAdapterId() ;
    }
    
    /**
     * @param scid
     * @param serverid
     * @param orbid
     * @param poaid
     * @return 
     * @exception 
     * @author 
     * @roseuid 3915F507004D
     */
    public POAObjectKeyTemplate( int scid, int serverid, String orbid, 
	POAId poaid) 
    {
	super( ObjectKeyTemplate.JAVAMAGIC_NEWER, scid, serverid ) ;
	this.orbid = orbid ;
	this.poaid = poaid ;
	setORBVersion( ORBVersionFactory.getORBVersion() ) ;

	adapterId = computeAdapterId() ;
    }
    
    /**
     * @param objectId
     * @param os
     * @return void
     * @exception 
     * @author 
     * @roseuid 3915FFEA00EB
     */
    public void write(OutputStream os) 
    {
	os.write_long( getMagic() ) ;
	os.write_long( getSubcontractId() ) ;
	os.write_long( getServerId() ) ;
	os.write_string( orbid ) ;
	poaid.write( os ) ;
    }

    public String getORBId()
    {
	return orbid ;
    }

    public POAId getPOAId()
    {
	return poaid ;
    }
}
