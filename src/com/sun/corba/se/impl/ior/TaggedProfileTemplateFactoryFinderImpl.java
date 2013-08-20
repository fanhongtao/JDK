/*
 * @(#)TaggedProfileTemplateFactoryFinderImpl.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.impl.ior.IdentifiableFactoryFinderBase ;

import org.omg.CORBA_2_3.portable.InputStream ;

import org.omg.CORBA.INTERNAL ;

/**
 * @author 
 */
public class TaggedProfileTemplateFactoryFinderImpl extends
    IdentifiableFactoryFinderBase 
{
    public TaggedProfileTemplateFactoryFinderImpl( ORB orb )
    { 
	super( orb ) ;
    }

    public Identifiable handleMissingFactory( int id, InputStream is) 
    {
	throw wrapper.taggedProfileTemplateFactoryNotFound( new Integer(id) ) ;
    }
}
