/*
 * @(#)DuplicateServiceContext.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

/** Exception used by ServiceContexts object to report that a
* service context with the requested id is already present.
*/
public class DuplicateServiceContext extends Exception
{
    public DuplicateServiceContext()
    {
        super();
    }

    public DuplicateServiceContext(String mssg)
    {
        super(mssg);
    }
}

