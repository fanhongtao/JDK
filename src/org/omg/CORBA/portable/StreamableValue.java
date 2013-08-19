/*
 * @(#)StreamableValue.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA.portable;

/**
 * Defines the base type for all non-boxed IDL valuetypes 
 * that are not custom marshaled.
 *
 * All value types implement ValueBase either directly or 
 * indirectly by implementing either the
 * StreamableValue or CustomValue interface.
 *
 * @author OMG
 * @version 1.11 01/23/03
 */
public interface StreamableValue extends Streamable, ValueBase {

}

