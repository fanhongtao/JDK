/*
 * @(#)RepositoryIdInterface.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

import org.omg.CORBA.ORB;
import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * Methods on specific instances of RepositoryId.  Hides
 * versioning of our RepositoryId class.
 */
public interface RepositoryIdInterface
{
    Class getClassFromType() throws ClassNotFoundException;

    Class getClassFromType(String codebaseURL)
        throws ClassNotFoundException, MalformedURLException;

    Class getClassFromType(Class expectedType,
                           String codebaseURL) 
        throws ClassNotFoundException, MalformedURLException;

    String getClassName();
}
