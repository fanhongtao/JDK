/*
 * @(#)ActionBase.java	1.5 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.fsm ;

import com.sun.corba.se.impl.orbutil.fsm.NameBase ;

public abstract class ActionBase extends NameBase implements Action {
    public ActionBase( String name ) { super( name ) ; } 
}
