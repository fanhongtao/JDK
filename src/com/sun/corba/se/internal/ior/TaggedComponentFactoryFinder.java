/*
 * @(#)TaggedComponentFactoryFinder.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/TaggedComponentFactoryFinder.java

package com.sun.corba.se.internal.ior;

import java.util.HashMap;

import com.sun.corba.se.internal.ior.IdEncapsulationFactory ;
import com.sun.corba.se.internal.ior.IdEncapsulation ;
import com.sun.corba.se.internal.ior.GenericTaggedComponent ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA.ORB ;

/**
 * @author 
 */
public class TaggedComponentFactoryFinder implements 
    IdEncapsulationFactoryFinder
{
    private HashMap map;

    // initialize-on-demand holder
    private static class TaggedComponentFactoryFinderHolder {
	static TaggedComponentFactoryFinder value = 
	    new TaggedComponentFactoryFinder() ;
    }

    public static TaggedComponentFactoryFinder getFinder() 
    {
	return TaggedComponentFactoryFinderHolder.value ;
    }

    /**
     * @return 
     * @exception 
     * @author 
     * @roseuid 391098510095
     */
    private TaggedComponentFactoryFinder() 
    {
	map = new HashMap() ;
    }
    
    /**
     * @param arg0
     * @param arg1
     * @return IdEncapsulation
     * @exception 
     * @author 
     * @roseuid 3910985100A9
     */
    public IdEncapsulation create(int id, InputStream is) 
    {
	IdEncapsulationFactory factory = getFactory( id ) ;

	if (factory != null)
	    return factory.create( id, is ) ;
	else 
	    return new GenericTaggedComponent( id, is ) ;
    }
    
    public TaggedComponent create( ORB orb,
	org.omg.IOP.TaggedComponent comp )
    {
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	org.omg.IOP.TaggedComponentHelper.write( os, comp ) ;
	InputStream is = (InputStream)(os.create_input_stream() ) ;
	// Skip the component ID: we just wrote it out above
	is.read_ulong() ;

	return (TaggedComponent)create( comp.tag, is ) ;
    }

    /**
     * @param arg0
     * @return IdEncapsulationFactory
     * @exception 
     * @author 
     * @roseuid 3910985100BD
     */
    private IdEncapsulationFactory getFactory(int id) 
    {
	Integer ident = new Integer( id ) ;
	IdEncapsulationFactory factory = (IdEncapsulationFactory)(map.get( 
	    ident ) ) ;
	return factory ;
    }
    
    /**
     * @param arg0
     * @param arg1
     * @return void
     * @exception 
     * @author 
     * @roseuid 3910985100BF
     */
    public void registerFactory(int id, 
	IdEncapsulationFactory factory) 
    {
	Integer ident = new Integer( id ) ;
	map.put( ident, factory ) ;
    }
}
