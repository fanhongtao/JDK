/*
 * @(#)TaggedComponentBase.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: G:/space/ws/ServerActivation/src/share/classes/com.sun.corba.se.internal.ior/TaggedComponentBase.java

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA.ORB ;

/**
 * @author 
 */
public abstract class TaggedComponentBase extends IdEncapsulationBase 
    implements TaggedComponent 
{
    public org.omg.IOP.TaggedComponent getIOPComponent( 
	ORB orb )
    {
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	write( os ) ;
	InputStream is = (InputStream)(os.create_input_stream() ) ;
	return org.omg.IOP.TaggedComponentHelper.read( is ) ;
    }
}
