/*
 * @(#)ActionBase.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.fsm ;

import com.sun.corba.se.impl.orbutil.fsm.NameBase ;

public abstract class ActionBase extends NameBase implements Action {
    public ActionBase( String name ) { super( name ) ; } 
}
