/*
 * @(#)TaggedProfileTemplate.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/TaggedProfileTemplate.java

package com.sun.corba.se.internal.ior;

import java.util.List ;
import com.sun.corba.se.internal.ior.Identifiable ;

/**
 * @author 
 */
public interface TaggedProfileTemplate extends Identifiable, List
{
    TaggedProfile create( ObjectId id ) ;
}
