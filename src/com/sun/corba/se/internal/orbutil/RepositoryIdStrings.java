/*
 * @(#)RepositoryIdStrings.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

import java.io.Serializable;
import com.sun.corba.se.internal.io.TypeMismatchException;

/**
 * Factory methods for creating various repository ID strings
 * and instances.
 */
public interface RepositoryIdStrings
{
    String createForAnyType(Class type);
    
    String createForJavaType(Serializable ser)
        throws TypeMismatchException;
    
    String createForJavaType(Class clz)
        throws TypeMismatchException;
    
    String createSequenceRepID(java.lang.Object ser);
    
    String createSequenceRepID(java.lang.Class clazz);
    
    RepositoryIdInterface getFromString(String repIdString);

    String getClassDescValueRepId();
    String getWStringValueRepId();
}
