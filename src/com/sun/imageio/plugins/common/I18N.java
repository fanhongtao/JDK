/*
 * @(#)I18N.java	1.4 10/03/23 18:03:43
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.common;

public final class I18N extends I18NImpl {
    private final static String resource_name = "iio-plugin.properties";
    public static String getString(String key) {
        return getString("com.sun.imageio.plugins.common.I18N", resource_name, key);
    }
}
