/*
 * @(#)POAView.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.ior ;

public interface POAView {
    String the_name() ;

    POAView getParent() ;

    int getNumLevels() ;
}
