/*
 * @(#)JPEGImageReaderResources.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.jpeg;

import java.util.ListResourceBundle;

public class JPEGImageReaderResources extends ListResourceBundle {

    static final Object[][] contents = {
        {Integer.toString(JPEGImageReader.WARNING_NO_EOI),
         "Truncated File - Missing EOI marker"},
        {Integer.toString(JPEGImageReader.WARNING_NO_JFIF_IN_THUMB),
         "JFIF markers not allowed in JFIF JPEG thumbnail; ignored"}
    };

    public JPEGImageReaderResources() {}
         
    public Object[][] getContents() {
        return contents;
    }


}
