/*
 * @(#)NewObjectKeyTemplateBase.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)NewObjectKeyTemplateBase.java	1.6 03/01/23

package com.sun.corba.se.internal.ior;

import java.io.IOException ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.iiop.CDROutputStream ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.core.GIOPVersion ;
import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionFactory ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.INTERNAL ;

/**
 * @author 
 */
public abstract class NewObjectKeyTemplateBase extends ObjectKeyTemplateBase 
{
    /**
     * @param scid
     * @param serverid
     * @return 
     * @exception 
     * @author 
     * @roseuid 3915FF9803AA
     */
    public NewObjectKeyTemplateBase( int magic, int scid, int serverid) 
    {
	super( magic, scid, serverid ) ;
	// superclass must set the version, since we don't have the object key here.

	if (magic != ObjectKeyTemplate.JAVAMAGIC_NEWER)
	    throw new INTERNAL() ;
    }
   
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
	super.write( objectId, os ) ;
	getORBVersion().write( os ) ;
    }

    protected void setVersion( CDRInputStream is ) 
    {
	ORBVersion version = ORBVersionFactory.create( is ) ;
	setORBVersion( version ) ;
    }
}
