/*
 * @(#)TypeCodeFactory.java	1.15 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.corba;

public interface TypeCodeFactory {
    void setTypeCode(String id, TypeCodeImpl code);

    TypeCodeImpl getTypeCode(String id);

    void setTypeCodeForClass( Class c, TypeCodeImpl tcimpl ) ;

    TypeCodeImpl getTypeCodeForClass( Class c ) ;
}
