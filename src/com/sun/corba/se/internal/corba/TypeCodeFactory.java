/*
 * @(#)TypeCodeFactory.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;

public interface TypeCodeFactory {
    void setTypeCode(String id, TypeCodeImpl code);
    TypeCodeImpl getTypeCode(String id);
}
