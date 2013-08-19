/*
 * @(#)ObjectKeyTemplate.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)ObjectKeyTemplate.java	1.20 03/01/23

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.core.ORBVersion ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA.ORB ;

/**
 * @author 
 */
public interface ObjectKeyTemplate 
{
    static final int MAGIC_BASE			= 0xAFABCAFE ;

    // Magic used in our object keys for JDK 1.2, 1.3, RMI-IIOP OP,
    // J2EE 1.0-1.2.1.
    static final int JAVAMAGIC_OLD		= MAGIC_BASE ;

    // Magic used only in JDK 1.3.1.  No format changes in object keys.
    static final int JAVAMAGIC_NEW		= MAGIC_BASE + 1 ;

    // New magic used in our object keys for JDK 1.4, J2EE 1.3 and later.
    // Format changes: all object keys have version string; POA key format
    // is changed.
    static final int JAVAMAGIC_NEWER		= MAGIC_BASE + 2 ;

    static final int MAX_MAGIC			= JAVAMAGIC_NEWER ;

    // Beginning in JDK 1.3.1_01, we introduced changes which required
    // the ability to distinguish between JDK 1.3.1 FCS and the patch
    // versions.  See OldJIDLObjectKeyTemplate.
    public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;  

    /**
     * @param objectId
     * @param os
     * @return void
     * @exception 
     * @author 
     * @roseuid 3911CC1701E5
     */
    public void write(ObjectId objectId, OutputStream os);
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 3915F48D01F6
     */
    public int getSubcontractId();

    // public byte[] getId( InputStream is ) ;

    public int getServerId() ;

    public byte[] getAdapterId( ORB orb ) ;

    public ORBVersion getORBVersion() ;
}
