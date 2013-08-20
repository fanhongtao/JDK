/*
 * @(#)AttributeValue.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import sun.awt.DebugHelper;

abstract class AttributeValue {
    private final int value;
    private final String[] names;

    private static final DebugHelper dbg =
        DebugHelper.create(AttributeValue.class);

    protected AttributeValue(int value, String[] names) {
        if (dbg.on) {
	    dbg.assertion(value >= 0 && names != null && value < names.length);
	}
        this.value = value;
	this.names = names;
    }
    // This hashCode is used by the sun.awt implementation as an array
    // index.
    public int hashCode() {
        return value;
    }
    public String toString() {
        return names[value];
    }
}
