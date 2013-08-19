/*
 * @(#)TaggedComponent.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/TaggedComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.ORB ;

/**
 * @author
 */
public interface TaggedComponent extends IdEncapsulation 
{
    
    /**
     * @return org.omg.IOP.TaggedComponent
     * @exception 
     * @author 
     * @roseuid 39808F7602D3
     */
    public org.omg.IOP.TaggedComponent getIOPComponent( ORB orb );
}
