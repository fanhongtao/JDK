/*
 * @(#)JPEGImageWriterSpi.java	1.5 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.jpeg;

import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOException;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.Locale;

public class JPEGImageWriterSpi extends ImageWriterSpi {

    private static String [] readerSpiNames = 
        {"com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi"};

    private boolean registered = false;
    
    public JPEGImageWriterSpi() {
        super(JPEG.vendor,
              JPEG.version,
              JPEG.names,
              JPEG.suffixes,
              JPEG.MIMETypes,
              "com.sun.imageio.plugins.jpeg.JPEGImageWriter",
              STANDARD_OUTPUT_TYPE,
              readerSpiNames,
              true,
              JPEG.nativeStreamMetadataFormatName,
              JPEG.nativeStreamMetadataFormatClassName,
              null, null,
              true,
              JPEG.nativeImageMetadataFormatName,
              JPEG.nativeImageMetadataFormatClassName,
              null, null
              );
    }

    public String getDescription(Locale locale) {
        return "Standard JPEG Image Writer";
    }

    public void onRegistration(ServiceRegistry registry,
                               Class category) {
        if (registered) {
            return;
        }
        try {
            java.security.AccessController.doPrivileged(
                new sun.security.action.LoadLibraryAction("jpeg"));
        } catch (Throwable e) { // Fail on any Throwable
            // if it can't be loaded, deregister and return
            registry.deregisterServiceProvider(this);
            return;
        }

        registered = true;
    }

    public boolean isFormatLossless() {
        return false;
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        // Indexed images should be expanded before writing
        ColorModel cm = type.getColorModel();
        if (cm instanceof IndexColorModel) {
            return false;
        }
        // Anything else should be encodable given the right metadata
        return true;
    }

    public ImageWriter createWriterInstance(Object extension)
        throws IIOException {
        return new JPEGImageWriter(this);
    }
}
