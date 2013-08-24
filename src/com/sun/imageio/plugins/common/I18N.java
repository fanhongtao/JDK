/*
 * @(#)I18N.java	1.3 05/11/17 14:14:58
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.common;

public final class I18N extends I18NImpl {
    private final static String resource_name = "iio-plugin.properties";
    public static String getString(String key) {
        return getString("com.sun.imageio.plugins.common.I18N", resource_name, key);
    }
}
