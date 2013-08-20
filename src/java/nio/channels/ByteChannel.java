/*
 * @(#)ByteChannel.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.nio.channels;

import java.io.IOException;


/**
 * A channel that can read and write bytes.  This interface simply unifies
 * {@link ReadableByteChannel} and {@link WritableByteChannel}; it does not
 * specify any new operations.
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @version 1.10, 03/12/19
 * @since 1.4
 */

public interface ByteChannel
    extends ReadableByteChannel, WritableByteChannel
{

}
