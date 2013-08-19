/*
 * @(#)CodeSetsComponent.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/CodeSetsComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.ior.TaggedComponentBase ;
import org.omg.IOP.TAG_CODE_SETS ;
import com.sun.corba.se.internal.core.CodeSetComponentInfo ;
import com.sun.corba.se.internal.core.MarshalOutputStream ;

/**
 * @author 
 */
public class CodeSetsComponent extends TaggedComponentBase 
{
    CodeSetComponentInfo csci ;
 
    public boolean equals( Object obj )
    {
	return obj instanceof CodeSetsComponent ;
    }

    // Just use the default toString() method

    /**
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984B035E
     */
    public CodeSetsComponent() 
    {
        // Uses our default code sets (see CodeSetComponentInfo)
	csci = new CodeSetComponentInfo() ;
    }

    public CodeSetsComponent(com.sun.corba.se.internal.core.ORB orb)
    {
        if (orb == null)
            csci = new CodeSetComponentInfo();
        else
            csci = orb.getCodeSetComponentInfo();
    }
    
    public CodeSetComponentInfo getCodeSetComponentInfo()
    {
	return csci ;
    }

    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3913261100DD
     */
    public void writeContents(OutputStream os) 
    {
	csci.write( (MarshalOutputStream)os ) ;
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 391326110368
     */
    public int getId() 
    {
	return TAG_CODE_SETS.value ; // 1 in CORBA 2.3.1 13.6.3
    }
}
