/*
 * @(#)ZStreamRef.java	1.1 09/12/07
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

/**
 * A reference to the native zlib's z_stream structure.
 */

class ZStreamRef {

    private long address;
    ZStreamRef (long address) {
        this.address = address;
    }

    long address() {
        return address;
    }

    void clear() {
        address = 0;
    }
}
