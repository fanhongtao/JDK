/*
 * @(#)TaggedProfileFactoryFinder.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/TaggedProfileFactoryFinder.java

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.IdEncapsulationFactoryFinder ;
import com.sun.corba.se.internal.ior.IIOPProfile ;
import com.sun.corba.se.internal.ior.GenericTaggedProfile ;
import org.omg.IOP.TAG_INTERNET_IOP ;
import org.omg.CORBA_2_3.portable.InputStream ;

/**
 * @author 
 */
public class TaggedProfileFactoryFinder implements IdEncapsulationFactoryFinder 
{
    private TaggedProfileFactoryFinder() 
    {
    }

    // initialize-on-demand holder
    private static class TaggedProfileFactoryFinderHolder {
	static TaggedProfileFactoryFinder value = 
	    new TaggedProfileFactoryFinder() ;
    }

    public static TaggedProfileFactoryFinder getFinder() 
    {
	return TaggedProfileFactoryFinderHolder.value ;
    }
    
    /** Reads the TaggedProfile of type id from is.
     * @param id
     * @param is
     * @return IdEncapsulation
     * @exception 
     * @author 
     * @roseuid 39135AC6012F
     */
    public IdEncapsulation create(int id, InputStream is) 
    {
	if (id == TAG_INTERNET_IOP.value)
	    return new IIOPProfile( is ) ;
	else 
	    return new GenericTaggedProfile( id, is ) ;
    }

}
