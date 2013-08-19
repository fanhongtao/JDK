/*
 * @(#)GenericTaggedProfile.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: G:/space/ws/ServerActivation/src/share/classes/com.sun.corba.se.internal.ior/GenericTaggedProfile.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.ORB ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import com.sun.corba.se.internal.core.GIOPVersion ;

/**
 * @author 
 */
public class GenericTaggedProfile extends GenericIdEncapsulation implements TaggedProfile 
{
    public GenericTaggedProfile( int id, InputStream is ) 
    {
	super( id, is ) ;
    }

    public GenericTaggedProfile( int id, byte[] data ) 
    {
	super( id, data ) ;
    }
    
    /**
     * @return org.omg.IOP.TaggedProfile
     * @exception 
     * @author 
     * @roseuid 3980B6A40343
     */
    public org.omg.IOP.TaggedProfile getIOPProfile( ORB orb ) 
    {
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	write( os ) ;
	InputStream is = (InputStream)(os.create_input_stream()) ;
	return org.omg.IOP.TaggedProfileHelper.read( is ) ;
    }
    
    /**
     * @return org.omg.IOP.TaggedComponent[]
     * @exception 
     * @author 
     * @roseuid 3980B6A4037F
     */
    public org.omg.IOP.TaggedComponent[] getIOPComponents( 
	ORB orb, int id ) 
    {
	return null ;
    }
}
