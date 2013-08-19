/*
 * @(#)OldObjectKeyTemplateBase.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)OldObjectKeyTemplateBase.java	1.4 00/10/11

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionImpl ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.INTERNAL ;

/**
 * @author 
 */
public abstract class OldObjectKeyTemplateBase extends ObjectKeyTemplateBase 
{
    /**
     * @param scid
     * @param serverid
     * @return 
     * @exception 
     * @author 
     * @roseuid 3915FF9803AA
     */
    public OldObjectKeyTemplateBase( int magic, int scid, int serverid ) 
    {
	super( magic, scid, serverid ) ;

	// set version based on magic
	if (magic == JAVAMAGIC_OLD)
	    setORBVersion( ORBVersionImpl.OLD ) ;
	else if (magic == JAVAMAGIC_NEW)
	    setORBVersion( ORBVersionImpl.NEW ) ;
	else // any other magic should not be here
	    throw new INTERNAL() ;
    }
}
