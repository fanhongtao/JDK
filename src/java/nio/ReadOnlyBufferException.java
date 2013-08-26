/*
 * @(#)ReadOnlyBufferException.java	1.14 01/05/02
 *
 * Copyright (c) 2000, Oracle and/or its affiliates. All rights reserved.
 * 
 * This software is the proprietary information of Oracle.
 * Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio;


/**
 * Unchecked exception thrown when a content-mutation method such as
 * <tt>put</tt> or <tt>compact</tt> is invoked upon a read-only buffer.
 *
 * @version 1.14, 01/05/02
 * @since 1.4
 */

public class ReadOnlyBufferException
    extends UnsupportedOperationException
{

    /**
     * Constructs an instance of this class.
     */
    public ReadOnlyBufferException() { }

}
