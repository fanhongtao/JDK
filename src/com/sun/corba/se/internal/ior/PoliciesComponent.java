/*
 * @(#)PoliciesComponent.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/PoliciesComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.ior.TaggedComponent ;
import org.omg.IOP.TAG_POLICIES ;

/**
 * @author 
 */
public class PoliciesComponent extends TaggedComponentBase 
{
    
    /**
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984F0292
     */
    public PoliciesComponent() 
    {
    }
    
    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 391326150314
     */
    public void writeContents(OutputStream arg0) 
    {
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 3913261601B7
     */
    public int getId() 
    {
	return TAG_POLICIES.value ; // 2 in CORBA 2.3.1 13.6.3
    }
}
