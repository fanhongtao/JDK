/*
 * @(#)JIDLObjectKeyTemplate.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/JIDLObjectKeyTemplate.java

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.iiop.CDROutputStream ;
import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionFactory ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.OctetSeqHolder ;

/**
 * @author 
 */
public final class JIDLObjectKeyTemplate extends NewObjectKeyTemplateBase
{
    public JIDLObjectKeyTemplate( int magic, int scid, CDRInputStream is,
	OctetSeqHolder osh ) 
    {
	super( magic, scid, is.read_long() );

	osh.value = readObjectKey( is ) ;
	setVersion( is ) ;
    }
    
    /**
     * @param scid
     * @param serverid
     * @return 
     * @exception 
     * @author 
     * @roseuid 3915FF9803AA
     */
    public JIDLObjectKeyTemplate( int scid, int serverid) 
    {
	super( ObjectKeyTemplate.JAVAMAGIC_NEWER, scid, serverid ) ; 
	setORBVersion( ORBVersionFactory.getORBVersion() ) ;
    }
   
    protected void write( OutputStream os )
    {
	os.write_long( getMagic() ) ;
	os.write_long( getSubcontractId() ) ;
	os.write_long( getServerId() ) ;
    }
}
