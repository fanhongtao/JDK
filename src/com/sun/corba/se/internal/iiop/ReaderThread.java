/*
 * @(#)ReaderThread.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 * 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
package com.sun.corba.se.internal.iiop;

import java.io.IOException;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.core.GIOPVersion;

/**
 * A thread class to handle reading incoming messages off the wire
 */
final class ReaderThread extends Thread
{
    private boolean keepRunning = true;
    private boolean debug ;

    private void dprint( String msg ) 
    {
	ORBUtility.dprint( this, msg ) ;
    }

    private IIOPConnection c;

    public ReaderThread(ThreadGroup g, IIOPConnection c, String name, boolean debug )
    {
	super(g, name);
        this.c = c;
	this.debug = debug ;
    }

    public ReaderThread(IIOPConnection c, String name, boolean debug )
    {
	super(name);
        this.c = c;
	this.debug = debug ;
    }

    public IIOPConnection getCurrentConnection(){
        return c;
    }

    /**
     */
    public void run() {
        while (keepRunning) {
            try {

                c.processInput();

            } catch (IOException ex) {
                if (debug) {
                    dprint( "IOException in createInputStream: " + ex ) ;
                    ex.printStackTrace() ;
                }
                
                // Close the connection, inform all threads
                // and stop this thread.
                c.purge_calls(Connection.CONN_ABORT, true, false);
                keepRunning = false;

                /*

                  // This doesn't seem to be thrown.  A thread.stop is called (which should
                  // be changed.


            } catch (IIOPConnection.DeleteConn dc) {
		if (debug) {
		    dprint( "DeleteConn thrown while reading request: " + dc ) ;
		    dc.printStackTrace() ;
		}

		// Close the connection, inform all threads
		// and stop this thread.
                c.purge_calls (dc.minorCode, true, false);


                */

            } catch (ThreadDeath td) {
		if (debug) {
		    dprint( "ThreadDeath thrown while reading request: " + td ) ;
		    td.printStackTrace() ;
		}

                try {
		    // Close the connection and inform all threads.
                    c.purge_calls(Connection.CONN_ABORT, false, false);
                } finally {
                    throw td;
                }
            } catch (Throwable ex) {
		if (debug) {
		    dprint( "Exception thrown while reading request: " + ex ) ;
		    ex.printStackTrace() ;
		}

                try {
                    if (ex instanceof org.omg.CORBA.INTERNAL) {                    
                        c.sendMessageError(GIOPVersion.DEFAULT_VERSION);
                    }
                } catch (IOException e) {}
                
                // Close the connection and inform all threads.  The
                // ReaderThread exits.  If we don't close the connection,
                // clients will hang forever.
                c.purge_calls(Connection.CONN_ABORT, false, false);
                keepRunning = false;
                return;
            }
        }
    }

    /**
     * reader.shutdown() does the following
     * 1. Sets keepRunning to false, which will terminate the thread
     * 2. Closes the connection.InputStream() which may result in IOException
     *    if thread is running, which would ultimately result in purging the
     *    connection.
     * 3. Attempts to start the thread. This may have any of the following 
     *    effects
     *    3-1: IllegalThreadStateException if the thread is already running,
     *         if this happens we simply neglect the exception.
     *    3-2: Just starts and stops the thread because of keepRunning == false
     *         condition. This helps in garbage collection if the thread was
     *         instantiated and was never started.
     */     
    synchronized void shutdown() {
        keepRunning = false;
        try {
	    java.io.InputStream inStream = c.getInputStream();
            inStream.close();
        } catch( Exception e ) {
            // We neglect exception, because it is just an attempt to
            // unblock the c.processInput() call.
        }
        // This extra step is neccessary to make sure that the Thread will
        // be deleted from the Threadgroup if it is not previously started.
        // There will be a chance of Memory leak if the Thread object is 
        // instantantiated, but never started.
        try {
            this.start( );
        } catch( IllegalThreadStateException e ) {
            // Just neglect the exception, this exception was raised because
            // the thread was already started. If it was already started,
            // thread will be shutdown because 'keepRunning' is set to false. 
        }
    }
}
