/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)AbstractInterruptibleChannel.java	1.14 03/01/23
 */

package java.nio.channels.spi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.nio.ch.Interruptible;


/**
 * Base implementation class for interruptible channels.
 *
 * <p> This class encapsulates the low-level machinery required to implement
 * the asynchronous closing and interruption of channels.  A concrete channel
 * class must invoke the {@link #begin begin} and {@link #end end} methods
 * before and after, respectively, invoking an I/O operation that might block
 * indefinitely.  In order to ensure that the {@link #end end} method is always
 * invoked, these methods should be used within a
 * <tt>try</tt>&nbsp;...&nbsp;<tt>finally</tt> block: <a name="be">
 *
 * <blockquote><pre>
 * boolean completed = false;
 * try {
 *     begin();
 *     completed = ...;    // Perform blocking I/O operation
 *     return ...;         // Return result
 * } finally {
 *     end(completed);
 * }</pre></blockquote>
 *
 * <p> The <tt>completed</tt> argument to the {@link #end end} method tells
 * whether or not the I/O operation actually completed, that is, whether it had
 * any effect that would be visible to the invoker.  In the case of an
 * operation that reads bytes, for example, this argument should be
 * <tt>true</tt> if, and only if, some bytes were actually transferred into the
 * invoker's target buffer.
 *
 * <p> A concrete channel class must also implement the {@link
 * #implCloseChannel implCloseChannel} method in such a way that if it is
 * invoked while another thread is blocked in a native I/O operation upon the
 * channel then that operation will immediately return, either by throwing an
 * exception or by returning normally.  If a thread is interrupted or the
 * channel upon which it is blocked is asynchronously closed then the channel's
 * {@link #end end} method will throw the appropriate exception.
 *
 * <p> This class performs the synchronization required to implement the {@link
 * java.nio.channels.Channel} specification.  Implementations of the {@link
 * #implCloseChannel implCloseChannel} method need not synchronize against
 * other threads that might be attempting to close the channel.  </p>
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @version 1.14, 03/01/23
 * @since 1.4
 */

public abstract class AbstractInterruptibleChannel
    implements Channel, InterruptibleChannel
{

    private Object closeLock = new Object();
    private volatile boolean open = true;

    /**
     * Initializes a new instance of this class.
     */
    protected AbstractInterruptibleChannel() { }

    /**
     * Closes this channel.
     *
     * <p> If the channel has already been closed then this method returns
     * immediately.  Otherwise it marks the channel as closed and then invokes
     * the {@link #implCloseChannel implCloseChannel} method in order to
     * complete the close operation.  </p>
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public final void close() throws IOException {
	synchronized (closeLock) {
	    if (!open)
		return;
	    open = false;
	    implCloseChannel();
	}
    }

    /**
     * Closes this channel.
     *
     * <p> This method is invoked by the {@link #close close} method in order
     * to perform the actual work of closing the channel.  This method is only
     * invoked if the channel has not yet been closed, and it is never invoked
     * more than once.
     *
     * <p> An implementation of this method must arrange for any other thread
     * that is blocked in an I/O operation upon this channel to return
     * immediately, either by throwing an exception or by returning normally.
     * </p>
     *
     * @throws  IOException
     *          If an I/O error occurs while closing the channel
     */
    protected abstract void implCloseChannel() throws IOException;

    public final boolean isOpen() {
	return open;
    }


    // -- Interruption machinery --

    private Interruptible interruptor;
    private volatile boolean interrupted = false;

    /**
     * Marks the beginning of an I/O operation that might block indefinitely.
     *
     * <p> This method should be invoked in tandem with the {@link #end end}
     * method, using a <tt>try</tt>&nbsp;...&nbsp;<tt>finally</tt> block as
     * shown <a href="#be">above</a>, in order to implement asynchronous
     * closing and interruption for this channel.  </p>
     */
    protected final void begin() {
	if (interruptor == null) {
	    interruptor = new Interruptible() {
		    public void interrupt() {
			synchronized (closeLock) {
			    if (!open)
				return;
			    interrupted = true;
			    open = false;
			    try {
				AbstractInterruptibleChannel.this.implCloseChannel();
			    } catch (IOException x) { }
			}
		    }};
	}
	blockedOn(interruptor);
	if (Thread.currentThread().isInterrupted())
	    interruptor.interrupt();
    }

    /**
     * Marks the end of an I/O operation that might block indefinitely.
     *
     * <p> This method should be invoked in tandem with the {@link #begin
     * begin} method, using a <tt>try</tt>&nbsp;...&nbsp;<tt>finally</tt> block
     * as shown <a href="#be">above</a>, in order to implement asynchronous
     * closing and interruption for this channel.  </p>
     *
     * @param  completed
     *         <tt>true</tt> if, and only if, the I/O operation completed
     *         successfully, that is, had some effect that would be visible to
     *         the operation's invoker
     *
     * @throws  AsynchronousCloseException
     *          If the channel was asynchronously closed
     *
     * @throws  ClosedByInterruptException
     *          If the thread blocked in the I/O operation was interrupted
     */
    protected final void end(boolean completed)
	throws AsynchronousCloseException
    {
	blockedOn(null);
	if (completed) {
	    interrupted = false;
	    return;
	}
	if (interrupted) throw new ClosedByInterruptException();
	if (!open) throw new AsynchronousCloseException();
    }


    // -- Reflection hackery --

    private static Method blockedOnMethod = null;

    static void blockedOn(Interruptible intr) { 	// package-private
	if (blockedOnMethod == null)
	    initBlockedOn();
	try {
	    blockedOnMethod.invoke(Thread.currentThread(),
				   new Object[] { intr });
        } catch (IllegalAccessException x) {
            throw new Error(x);
        } catch (IllegalArgumentException x) {
            throw new Error(x);
        } catch (InvocationTargetException x) {
            throw new Error(x);
        }
    }

    private static void initBlockedOn() {
	AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    try {
			Class th = Class.forName("java.lang.Thread");
			blockedOnMethod
			    = th.getDeclaredMethod("blockedOn",
					new Class[] { Interruptible.class });
			blockedOnMethod.setAccessible(true);
		    } catch (ClassNotFoundException x) {
			throw new Error(x);
		    } catch (NoSuchMethodException x) {
			throw new Error(x);
		    } catch (IllegalArgumentException x) {
			throw new Error(x);
		    } catch (ClassCastException x) {
			throw new Error(x);
		    }
		    return null;
		}});
    }

    // Workaround for apparent VM bug: Sometimes an interrupted thread
    // cannot load a class

    private static class FooChannel extends AbstractInterruptibleChannel {
        protected void implCloseChannel() { }
    }

    static {
        FooChannel fc = new FooChannel();
        fc.begin();
        try {
            fc.end(true);
        } catch (IOException e) { }
    }

}
