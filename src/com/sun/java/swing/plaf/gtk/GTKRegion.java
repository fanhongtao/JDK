/*
 * @(#)GTKRegion.java	1.3 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.plaf.synth.Region;

/**
 * A typesafe enumeration of the distinct rendering portions specific
 * to GTK.
 *
 * @version 1.3, 11/17/05
 * @author Scott Violet
 */
class GTKRegion extends Region {
    public static final Region HANDLE_BOX = new GTKRegion("HandleBox", null,
                                                          true);

    protected GTKRegion(String name, String ui, boolean subregion) {
        super(name, ui, subregion);
    }
}
