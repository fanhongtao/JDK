/*
 * @(#)IORTemplate.java	1.11 01/12/04
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IORTemplate.java

package com.sun.corba.se.internal.ior;

import java.util.Iterator ;

import org.omg.IOP.TAG_INTERNET_IOP ;

import com.sun.corba.se.internal.ior.IdentifiableContainerBase ;

/**
 * This class is a container of TaggedProfileTemplates.
 * @author 
 */
public class IORTemplate extends IdentifiableContainerBase
{
    /** Ensure that this IORTemplate and all of its profiles can not be
    * modified.  This overrides the method inherited from 
    * FreezableList through IdentifiableContainerBase.
    * Only IIOPProfileTemplates can be frozen at this point.
    */
    public void makeImmutable()
    {
	Iterator iter = iteratorById( TAG_INTERNET_IOP.value ) ;
	while (iter.hasNext()) {
	    Object obj = iter.next() ;
	    if (obj instanceof IIOPProfileTemplate) {
		IIOPProfileTemplate temp = (IIOPProfileTemplate)obj ;
		temp.makeImmutable() ;
	    }
	}
	super.makeImmutable() ;
    }
}
