/*
 * @(#)NoSuchServiceContext.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

/** Exception used by ServiceContexts object to report that no
* service context matches the requested id.
*/
public class NoSuchServiceContext extends Exception
{
    public NoSuchServiceContext()
    {
        super();
    }

    public NoSuchServiceContext(String mssg)
    {
        super(mssg);
    }
}

