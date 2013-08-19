/*
 * @(#)StandardIIOPProfileTemplate.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import java.io.StringWriter;
import java.io.IOException;

import java.util.Iterator ;

import javax.rmi.CORBA.Util ;

import com.sun.corba.se.internal.ior.IIOPAddress ;
import com.sun.corba.se.internal.ior.JavaCodebaseComponent ;
import com.sun.corba.se.internal.ior.CodeSetsComponent ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.IIOPAddressImpl ;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;

import org.omg.IOP.TAG_JAVA_CODEBASE ;
import org.omg.IOP.TAG_INTERNET_IOP ;

public class StandardIIOPProfileTemplate extends IIOPProfileTemplate
{
    public StandardIIOPProfileTemplate(
	IIOPAddress addr, int major, int minor, 
	ObjectKeyTemplate ktemp, Object servant, ORB factory )
    {
        super((byte)major, (byte)minor, addr, ktemp);

        // Version 1.0 of the IIOP Profile does not have
        // tagged components.
        if (minor > 0 || major > 1) {
            if (servant != null) {
                String codebase = Util.getCodebase(servant.getClass());
                if (codebase != null)
                    add( new JavaCodebaseComponent(codebase) ) ;
            }
            
            add( new CodeSetsComponent( factory ) ) ;
        }
    }
}
