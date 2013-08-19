/*
 * @(#)TaggedProfile.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/TaggedProfile.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.ORB ;

/**
 * @author
 */
public interface TaggedProfile extends IdEncapsulation 
{
    
    /**
     * @return org.omg.IOP.TaggedProfile
     * @exception 
     * @author 
     * @roseuid 39808ED1027B
     */
    public org.omg.IOP.TaggedProfile getIOPProfile( ORB orb );
    
    /**
     * @return org.omg.IOP.TaggedComponent[]
     * @exception 
     * @author 
     * @roseuid 39808F2D00F7
     */
    public org.omg.IOP.TaggedComponent[] getIOPComponents( 
	ORB orb, int id );
}
