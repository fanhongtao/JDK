/*
 * @(#)ClosedByInterruptException.java	1.9 01/11/19
 *
 * Copyright 2000 by Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio.channels;


/**
 * Checked exception received by a thread when another thread interrupts it
 * while it is blocked in an I/O operation upon a channel.  Before this
 * exception is thrown the channel will have been closed and the interrupt
 * status of the previously-blocked thread will have been set.
 *
 * @version 1.9, 01/11/19
 * @since 1.4
 */

public class ClosedByInterruptException
    extends AsynchronousCloseException
{

    /**
     * Constructs an instance of this class.
     */
    public ClosedByInterruptException() { }

}
