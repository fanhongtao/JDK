/*
 * @(#)IdEncapsulationFactoryFinder.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IdEncapsulationFactoryFinder.java

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.IdEncapsulation ;
import org.omg.CORBA_2_3.portable.InputStream ;

/**
 * @author 
 */
public interface IdEncapsulationFactoryFinder 
{
    
    /**
     * @param id
     * @param is
     * @return IdEncapsulation
     * @exception 
     * @author 
     * @roseuid 3910985100A9
     */
    public IdEncapsulation create(int id, InputStream is);
}
