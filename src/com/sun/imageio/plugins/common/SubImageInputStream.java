/*
 * @(#)SubImageInputStream.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.ImageInputStream;

/**
 * @version 0.5 
 */
public class SubImageInputStream extends ImageInputStreamImpl {

    ImageInputStream stream;
    long startingPos;
    int startingLength;
    int length;

    public SubImageInputStream(ImageInputStream stream, int length) 
        throws IOException {
        this.stream = stream;
        this.startingPos = stream.getStreamPosition();
        this.startingLength = this.length = length;
    }

    public int read() throws IOException {
        if (length == 0) { // Local EOF
            return -1;
        } else {
            --length;
            return stream.read();
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (length == 0) { // Local EOF
            return -1;
        }

        len = Math.min(len, length);
        int bytes = stream.read(b, off, len);
        length -= bytes;
        return bytes;
    }

    public long length() {
        return startingLength;
    }

    public void seek(long pos) throws IOException {
        stream.seek(pos - startingPos);
        streamPos = pos;
    }
}
