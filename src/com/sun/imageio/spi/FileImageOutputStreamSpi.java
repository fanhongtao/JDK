/*
 * @(#)FileImageOutputStreamSpi.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.spi;

import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;

public class FileImageOutputStreamSpi extends ImageOutputStreamSpi {

    private static final String vendorName = "Sun Microsystems, Inc.";

    private static final String version = "1.0";

    private static final Class outputClass = File.class;

    public FileImageOutputStreamSpi() {
        super(vendorName, version, outputClass);
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates a FileImageOutputStream from a File";
    }

    public ImageOutputStream createOutputStreamInstance(Object output,
                                                        boolean useCache,
                                                        File cacheDir) {
        if (output instanceof File) {
            try {
                return new FileImageOutputStream((File)output);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
