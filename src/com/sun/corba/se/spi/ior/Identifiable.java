/*
 * @(#)Identifiable.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.corba.se.spi.ior;


/** This interface represents an entity that can be written to an
 * OutputStream and has an identity that is represented by an integer.
 * This identity is essentially the type of the entity, and is used in
 * order to know how to read the entity back from an InputStream.
 * @author Ken Cavanaugh
 */
public interface Identifiable extends Writeable
{
    /** Return the (type) identity of this entity.
     * @return int
     */
    public int getId();
}
