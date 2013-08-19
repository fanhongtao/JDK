/*
 * @(#)AlternateIIOPAddressComponent.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/AlternateIIOPAddressComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.ior.TaggedComponentBase ;
import com.sun.corba.se.internal.ior.IIOPAddress ;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS ;

/**
 * @author 
 */
public class AlternateIIOPAddressComponent extends TaggedComponentBase 
{
    private IIOPAddress addr ;

    public boolean equals( Object obj )
    {
	if (!(obj instanceof AlternateIIOPAddressComponent))
	    return false ;

	AlternateIIOPAddressComponent other = 
	    (AlternateIIOPAddressComponent)obj ;

	return addr.equals( other.addr ) ;
    }

    public String toString()
    {
	return "AlternateIIOPAddressComponent[addr=" + addr + "]" ;
    }

    /**
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984B0281
     */
    public AlternateIIOPAddressComponent( IIOPAddress addr ) 
    {
	this.addr = addr ;
    }
    
    public IIOPAddress getAddress()
    {
	return addr ;
    }

    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3913260F0103
     */
    public void writeContents(OutputStream os) 
    {
	addr.write( os ) ;
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 3913260F03B6
     */
    public int getId() 
    {
	return TAG_ALTERNATE_IIOP_ADDRESS.value ; // 3 in CORBA 2.3.1 13.6.3
    }
}
