/*
 * @(#)ZipError.java	1.1 06/07/31
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

/**
 * Signals that an unrecoverable error has occurred.
 *
 * @author  Dave Bristor
 * @version 1.1, 07/31/06
 * @since   1.6
 */
public class ZipError extends InternalError {
    private static final long serialVersionUID = 853973422266861979L;

    /**
     * Constructs a ZipError with the given detail message.
     * @param s the {@code String} containing a detail message
     */
    public ZipError(String s) {
        super(s);
    }
}
