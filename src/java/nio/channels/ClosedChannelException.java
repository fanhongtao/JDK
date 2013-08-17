/*
 * @(#)ClosedChannelException.java	1.9 01/11/19
 *
 * Copyright 2000 by Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio.channels;


/**
 * Checked exception thrown when an attempt is made to invoke or complete an
 * I/O operation upon channel that is closed, or at least closed to that
 * operation.  That this exception is thrown does not necessarily imply that
 * the channel is completely closed.  A socket channel whose write half has
 * been shut down, for example, may still be open for reading.
 *
 * @version 1.9, 01/11/19
 * @since 1.4
 */

public class ClosedChannelException
    extends java.io.IOException
{

    /**
     * Constructs an instance of this class.
     */
    public ClosedChannelException() { }

}
