/*
 * @(#)ObjectId.java	1.5 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior;

/**
 * @author Ken Cavanaugh
 */
public interface ObjectId extends Writeable
{
    public byte[] getId() ;
}
