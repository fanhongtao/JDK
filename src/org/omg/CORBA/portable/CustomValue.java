/*
 * @(#)CustomValue.java	1.8 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/**
 * Defines the base interface for all custom value types 
 * generated from IDL.
 *
 * All value types implement ValueBase either directly 
 * or indirectly by implementing either the StreamableValue 
 * or CustomValue interface.
 * @author OMG
 * @version 1.8 02/02/00
 */

package org.omg.CORBA.portable;

import org.omg.CORBA.CustomMarshal;

public interface CustomValue extends ValueBase, CustomMarshal {

}

