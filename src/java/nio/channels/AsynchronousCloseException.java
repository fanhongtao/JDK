/*
 * @(#)AsynchronousCloseException.java	1.9 01/11/19
 *
 * Copyright 2000 by Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio.channels;


/**
 * Checked exception received by a thread when another thread closes the
 * channel or the part of the channel upon which it is blocked in an I/O
 * operation.
 *
 * @version 1.9, 01/11/19
 * @since 1.4
 */

public class AsynchronousCloseException
    extends ClosedChannelException
{

    /**
     * Constructs an instance of this class.
     */
    public AsynchronousCloseException() { }

}
