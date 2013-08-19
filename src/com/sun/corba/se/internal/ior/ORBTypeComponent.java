/*
 * @(#)ORBTypeComponent.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/ORBTypeComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.IOP.TAG_ORB_TYPE ;
import com.sun.corba.se.internal.ior.TaggedComponent ;
import org.omg.CORBA_2_3.portable.OutputStream ;

/**
 * @author 
 */
public class ORBTypeComponent extends TaggedComponentBase 
{
    private int ORBType;
   
    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof ORBTypeComponent))
	    return false ;

	ORBTypeComponent other = (ORBTypeComponent)obj ;

	return ORBType == other.ORBType ;
    }

    public String toString()
    {
	return "ORBTypeComponent[ORBType=" + ORBType + "]" ;
    }

    /**
     * @param arg0
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984F01AA
     */
    public ORBTypeComponent(int ORBType) 
    {
	this.ORBType = ORBType ;
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 3910984F01AE
     */
    public int getId() 
    {
	return TAG_ORB_TYPE.value ; // 0 in CORBA 2.3.1 13.6.3
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 3910984F01AF
     */
    public int getORBType() 
    {
	return ORBType ;
    }
    
    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3910984F01B4
     */
    public void writeContents(OutputStream os) 
    {
	os.write_ulong( ORBType ) ;
    }
}
