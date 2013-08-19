/*
 * @(#)OldPOAObjectKeyTemplate.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)OldPOAObjectKeyTemplate.java	1.7 03/01/23

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionImpl ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.INTERNAL ;
import org.omg.CORBA.OctetSeqHolder ;

/**
 * @author 
 */
public final class OldPOAObjectKeyTemplate extends OldObjectKeyTemplateBase 
{
    private int orbid ;
    private int poaid ;

    public boolean equals( Object obj )
    {
	if (!super.equals( obj ))
	    return false ;

	if (!(obj instanceof POAObjectKeyTemplate))
	    return false ;

	OldPOAObjectKeyTemplate other = (OldPOAObjectKeyTemplate)obj ;

	return (orbid == other.orbid) && (poaid == other.poaid) ;
    }
    
    public OldPOAObjectKeyTemplate( int magic, int scid, InputStream is,
	OctetSeqHolder osh ) 
    {
	super( magic, scid, is.read_long() ) ; 
	orbid = is.read_long() ;
	poaid = is.read_long() ;
	osh.value = readObjectKey( is ) ;
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
    public OldPOAObjectKeyTemplate( int magic, int scid, int serverid, 
	int orbid, int poaid) 
    {
	super( magic, scid, serverid ) ;
	this.orbid = orbid ;
	this.poaid = poaid ;
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
	os.write_long( orbid ) ;
	os.write_long( poaid ) ;
    }
 
    public int getORBId()
    {
	return orbid ;
    }

    public int getPOAId()
    {
	return poaid ;
    }

    public ORBVersion getORBVersion()
    {
	if (getMagic() == ObjectKeyTemplate.JAVAMAGIC_OLD)
	    return ORBVersionImpl.OLD ;
	else if (getMagic() == ObjectKeyTemplate.JAVAMAGIC_NEW)
	    return ORBVersionImpl.NEW ;
	else
	    throw new INTERNAL() ;
    }
}

