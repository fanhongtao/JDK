/*
 * @(#)POAView.java	1.3 01/12/04
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.ior ;

public interface POAView {
    String the_name() ;

    POAView getParent() ;

    int getNumLevels() ;
}
