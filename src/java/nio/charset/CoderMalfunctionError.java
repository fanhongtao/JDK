/*
 * @(#)CoderMalfunctionError.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.nio.charset;


/**
 * Error thrown when the {@link CharsetDecoder#decodeLoop decodeLoop} method of
 * a {@link CharsetDecoder}, or the {@link CharsetEncoder#encodeLoop
 * encodeLoop} method of a {@link CharsetEncoder}, throws an unexpected
 * exception.
 *
 * @version 1.5, 03/12/19
 * @since 1.4
 */

public class CoderMalfunctionError
    extends Error
{

    /**
     * Initializes an instance of this class.
     *
     * @param  cause
     *         The unexpected exception that was thrown
     */
    public CoderMalfunctionError(Exception cause) {
	super(cause);
    }

}
