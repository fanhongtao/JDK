/*
 * @(#)StateEngineFactory.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl ;

/**
 * Factory for creating the standard state machine implementation.
 *
 * @version @(#)StateEngineFactory.java	1.8 03/12/19
 * @author Ken Cavanaugh
 */
public class StateEngineFactory {
    private StateEngineFactory() {}

    public static StateEngine create()
    {
	return new StateEngineImpl() ;
    }
}
