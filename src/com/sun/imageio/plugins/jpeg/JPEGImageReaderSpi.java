/*
 * @(#)JPEGImageReaderSpi.java	1.9 09/05/07
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.jpeg;

import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.IIOException;

public class JPEGImageReaderSpi extends ImageReaderSpi {

    private static String [] writerSpiNames = 
        {"com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi"};

    private boolean registered = false;
    
    public JPEGImageReaderSpi() {
        super(JPEG.vendor,
              JPEG.version,
              JPEG.names,
              JPEG.suffixes,
              JPEG.MIMETypes,
              "com.sun.imageio.plugins.jpeg.JPEGImageReader",
              new Class[] { ImageInputStream.class },
              writerSpiNames,
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

    public void onRegistration(ServiceRegistry registry,
                               Class<?> category) {
        if (registered) {
            return;
        }
        try {
            java.security.AccessController.doPrivileged(
                new sun.security.action.LoadLibraryAction("jpeg"));
            // Stuff it all into one lib for first pass
            //java.security.AccessController.doPrivileged(
            //new sun.security.action.LoadLibraryAction("imageioIJG"));
        } catch (Throwable e) { // Fail on any Throwable
            // if it can't be loaded, deregister and return
            registry.deregisterServiceProvider(this);
            return;
        }

        registered = true;
    }

    public String getDescription(Locale locale) {
        return "Standard JPEG Image Reader";
    }

    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof ImageInputStream)) {
            return false;
        }
        ImageInputStream iis = (ImageInputStream) source;
        iis.mark();
        // If the first two bytes are a JPEG SOI marker, it's probably
        // a JPEG file.  If they aren't, it definitely isn't a JPEG file.
        int byte1 = iis.read();
        int byte2 = iis.read();
        iis.reset();
        if ((byte1 == 0xFF) && (byte2 == JPEG.SOI)) {
            return true;
        }
        return false;
    }
    
    public ImageReader createReaderInstance(Object extension) 
        throws IIOException {
        return new JPEGImageReader(this);
    }

}
