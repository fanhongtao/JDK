/*
 * @(#)IIOPProfileTemplate.java	1.15 03/12/19 
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior.iiop;

import java.util.List ;
import java.util.Iterator ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate ;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion ;
import com.sun.corba.se.spi.orb.ORB ;

/**
 * IIOPProfileTemplate represents the parts of an IIOPProfile that are independent
 * of the object identifier.  It is a container of tagged components.
 */
public interface IIOPProfileTemplate extends TaggedProfileTemplate
{
    /** Return the GIOP version of this profile.
    */
    public GIOPVersion getGIOPVersion() ;

    /** Return the IIOP address from the IIOP profile.  This is called the 
    * primary address here since other addresses may be contained in 
    * components.
    */
    public IIOPAddress getPrimaryAddress()  ;
}
