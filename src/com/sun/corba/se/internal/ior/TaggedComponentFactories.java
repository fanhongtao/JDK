/*
 * @(#)TaggedComponentFactories.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.ior ;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.internal.core.MarshalInputStream ;

import com.sun.corba.se.internal.ior.CodeSetsComponent ;
import com.sun.corba.se.internal.ior.TaggedComponentFactoryFinder ;
import com.sun.corba.se.internal.ior.IdEncapsulation ;
import com.sun.corba.se.internal.ior.IdEncapsulationFactory ;
import com.sun.corba.se.internal.ior.IIOPAddressImpl ;
import com.sun.corba.se.internal.ior.AlternateIIOPAddressComponent ;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS ;
import org.omg.IOP.TAG_CODE_SETS ;
import org.omg.IOP.TAG_JAVA_CODEBASE ;
import org.omg.IOP.TAG_ORB_TYPE ;
import org.omg.IOP.TAG_POLICIES ;

abstract class ComponentFactoryBase implements IdEncapsulationFactory {
    public final IdEncapsulation create( int id, InputStream in ) 
    {
	InputStream is = IdEncapsulationBase.getEncapsulationStream( in ) ;
	return readContents( is ) ;
    }

    abstract IdEncapsulation readContents( InputStream is ) ;
}

public class TaggedComponentFactories {
    private TaggedComponentFactories() {}

    // This must be called during ORB initialization
    public static void registerFactories()
    {
	TaggedComponentFactoryFinder finder = 
	    TaggedComponentFactoryFinder.getFinder() ;

	IdEncapsulationFactory factory ;

	// register AlternateIIOPAddressComponent factory
	factory = new ComponentFactoryBase() {
		public IdEncapsulation readContents( InputStream in ) 
		{
		    // ignore the ID here
		    IIOPAddress addr = new IIOPAddressImpl( in ) ;
		    return new AlternateIIOPAddressComponent(
			addr ) ;
		}
	    } ;
	finder.registerFactory( TAG_ALTERNATE_IIOP_ADDRESS.value, 
	    factory ) ;

	// register CodeSetsComponent factory
	factory = new ComponentFactoryBase() {
		public IdEncapsulation readContents( InputStream in ) 
		{
		    // ignore the ID here
		    CodeSetsComponent csc = new CodeSetsComponent() ;
		    csc.csci.read( (MarshalInputStream)in ) ;
		    return csc ;
		}
	    } ;

	finder.registerFactory( TAG_CODE_SETS.value, 
	    factory ) ;

	// register JavaCodeBaseComponent factory
	factory = new ComponentFactoryBase() {
		public IdEncapsulation readContents( InputStream in ) 
		{
		    // ignore the ID here
		    String url = in.read_string() ;
		    return new JavaCodebaseComponent( url ) ;
		}
	    } ;

	finder.registerFactory( TAG_JAVA_CODEBASE.value,
	    factory ) ;

	// register ORBTypeComponent factory
	factory = new ComponentFactoryBase() {
		public IdEncapsulation readContents( InputStream in ) 
		{
		    // ignore the ID here
		    int type = in.read_ulong() ;
		    return new ORBTypeComponent( type ) ;
		}
	    } ;

	finder.registerFactory( TAG_ORB_TYPE.value, 
	    factory ) ;

	// register PoliciesComponent factory
	/* Leave this out for now until we take a good
	* look at the entire policy picture.
	factory = new ComponentFactoryBase() {
		IdEncapsulation readContents( InputStream in ) 
		{
		}
	    } ;

	finder.registerFactory( TAG_POLICIES.value, 
	    factory ) ;
	*/
    }
}
