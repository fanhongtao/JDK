/*
 * @(#)GTKRegion.java	1.4 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.plaf.synth.Region;

/**
 * A typesafe enumeration of the distinct rendering portions specific
 * to GTK.
 *
 * @version 1.4, 03/23/10
 * @author Scott Violet
 */
class GTKRegion extends Region {
    public static final Region HANDLE_BOX = new GTKRegion("HandleBox", null,
                                                          true);

    protected GTKRegion(String name, String ui, boolean subregion) {
        super(name, ui, subregion);
    }
}
