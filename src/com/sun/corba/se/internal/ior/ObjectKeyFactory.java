/*
 * @(#)ObjectKeyFactory.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)ObjectKeyFactory.java	1.15 00/10/26

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.ORB ;
import org.omg.CORBA.OctetSeqHolder ;

import com.sun.corba.se.internal.orbutil.ORBConstants ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.JIDLObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.POAObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.WireObjectKeyTemplate ;
import com.sun.corba.se.internal.corba.EncapsInputStream ;

/**
 * @author 
 */
public class ObjectKeyFactory 
{
    private ObjectKeyFactory() 
    {
    }
    
    private static ObjectKeyFactory factory = null ;

    // initialize-on-demand holder
    private static class ObjectKeyFactoryHolder {
	static ObjectKeyFactory value = 
	    new ObjectKeyFactory() ;
    }

    static public ObjectKeyFactory get() 
    {
	return ObjectKeyFactoryHolder.value ;
    }

    private static final int CDR_LONG_SIZE = 4 ;

    /**
     * @param byte[]
     * @return ObjectKey
     * @exception 
     * @author 
     * @roseuid 391604E40147
     */
    public ObjectKey create(ORB orb, byte[] key) 
    {
	OctetSeqHolder osh = new OctetSeqHolder() ;

        // WARNING: If we move away from using an encapsulation, we must
        // update OldJIDLObjectKeyTemplate since it assumes it can
        // compare stream position and key length.
	EncapsInputStream is = new EncapsInputStream( orb, key, key.length ) ;
	ObjectKeyTemplate template = null ;
	
	// If key is too small to contain a MAGIC long, fall through
	// to the WireObjectKeyTemplate case
	if (key.length >= CDR_LONG_SIZE) {
	    is.mark(0) ;
	    int magic = is.read_long() ;
		    
	    // If key is too small to contain a MAGIC long followed by an SCID long,
	    // fall through to the WireObjectKeyTemplate case.
	    if ((key.length >= 2*CDR_LONG_SIZE) && (magic >= ObjectKeyTemplate.MAGIC_BASE) 
		&& (magic <= ObjectKeyTemplate.MAX_MAGIC)) {
		int scid = is.read_long() ;

		if ((scid >= ORBConstants.FIRST_POA_SCID) && 
		    (scid <= ORBConstants.MAX_POA_SCID)) {
		    if (magic >= ObjectKeyTemplate.JAVAMAGIC_NEWER)
			template = new POAObjectKeyTemplate( magic, scid, is, osh ) ;
		    else
			template = new OldPOAObjectKeyTemplate( magic, scid, is, osh ) ;
		} else if ((scid >= 0) && (scid < ORBConstants.FIRST_POA_SCID)) {
		    if (magic >= ObjectKeyTemplate.JAVAMAGIC_NEWER)
			template = new JIDLObjectKeyTemplate( magic, scid, is, osh ) ;
		    else
			template = new OldJIDLObjectKeyTemplate( key, magic, scid, is, osh );
		}
	    }

	    // If we did not successfully construct a template here, reset the 
	    // stream so that WireObjectKeyTemplate can correctly construct the
	    // object key.
	    if (template == null)
		is.reset() ;
	}

	if (template == null)
	    template = new WireObjectKeyTemplate( is, osh ) ;

	// byte[] id = template.getId( is ) ;
	ObjectId oid = new ObjectId( osh.value ) ;
	return new ObjectKey( template, oid ) ;
    }
}
