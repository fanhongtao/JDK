/*
 * @(#)GTKRegion.java	1.1 03/10/14
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

/**
 * A typesafe enumeration of the distinct rendering portions specific
 * to GTK.
 *
 * @version 1.1, 10/14/03
 * @author Scott Violet
 */
class GTKRegion extends Region {
    public static final Region HANDLE_BOX = new Region("HandleBox");

    GTKRegion(String key) {
        super(key);
    }
}
