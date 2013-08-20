/*
 * @(#)ObjectId.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
