/*
 * @(#)IIOPConnection.java	1.124 03/01/23
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

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.*; // Once we get beyond 5 does it make sense to do otherwise?
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INTERNAL;

import org.omg.CORBA.Object;

import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.core.EndPoint;
import com.sun.corba.se.internal.core.RequestHandler;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.orbutil.Condition;
import com.sun.corba.se.internal.orbutil.Lock;
import com.sun.corba.se.internal.iiop.messages.Message;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.RequestMessage;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.iiop.messages.FragmentMessage;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A network connection which processes IIOP messages.
 */
public final class IIOPConnection extends Connection 
{
    final static class OutCallDesc
    {
        java.lang.Object done = new java.lang.Object();
        Thread thd;
        SystemException exc;
        IIOPInputStream s;
    }

    final static class DeleteConn extends java.lang.Throwable {
        int minorCode;
        
        DeleteConn (int code) {
            minorCode = code;
        }
    }

    //
    // Connection status
    //
    private static final int OPENING = 1;
    private static final int ESTABLISHED = 2;
    private static final int CLOSE_SENT = 3;
    private static final int CLOSE_RECVD = 4;
    private static final int ABORT = 5;
    
    //
    // Table of pending invocations on this connection indexed by
    // Integer(requestId).  These are only relevant if this is
    // a client.
    //
    // The clientReplyMap maps request ID to an IIOPInputStream.
    // The out_calls map request ID to an OutCallDesc.
    // This is so the client thread can start unmarshaling
    // the reply and remove it from the out_calls map while the
    // ReaderThread can still obtain the input stream to give
    // new fragments.  Only the ReaderThread touches the clientReplyMap,
    // so it doesn't incur synchronization overhead.
    //
    Hashtable out_calls = null;
    ClientResponseImpl theOnly1_1ClientResponseImpl = null;
    Map clientReplyMap = null;

    // This map allows the ORB to ask "have any fragments
    // been sent?" if it catches an exception after already
    // sending at least one fragment and before the last is sent.
    // This can happen on both the client and server side.
    // We want a synchronized Hashtable.
    Hashtable idToFragmentedOutputStream;

    private MessageMediator mediator;

    //
    // Remote address that we're talking to.
    //
    private String threadName;
    protected EndPoint endpoint;
    
    protected int requestCount = 0;
    
    private ServerGIOP server;

    // Server request map: used on the server side of Connection
    // Maps request ID to IIOPInputStream.
    Map serverRequestMap = null;
    ServerRequestImpl theOnly1_1ServerRequestImpl = null;

    // This is a flag associated per connection telling us if the initial set of
    // sending contexts were sent to the receiver already...
    private boolean postInitialContexts = false;
 
    // Remote reference to CodeBase server (supplies FullValueDescription, among other things)
    private IOR codeBaseServerIOR;

    // CodeBase cache for this connection.  This will cache remote operations,
    // handle connecting, and ensure we don't do any remote operations until
    // necessary.
    private CachedCodeBase cachedCodeBase = new CachedCodeBase(this);

    private String getStateString( int state ) 
    {
        synchronized ( stateEvent ){
            switch (state) {
            case OPENING : return "OPENING" ;
            case ESTABLISHED : return "ESTABLISHED" ;
            case CLOSE_SENT : return "CLOSE_SENT" ;
            case CLOSE_RECVD : return "CLOSE_RECVD" ;
            case ABORT : return "ABORT" ;
            default : return "???" ;
            }
        }
    }
    
    public String toString()
    {
        synchronized ( stateEvent ){
            return 
		"Connection[" +
		"type=" + endpoint.getType() +
		" remote_host=" + endpoint.getHostName() +
                " remote_port=" + endpoint.getPort() +
                " state=" + getStateString( state ) + "]" ;
        }
    }
    
    //
    // Various connection state.
    //
    Thread reader;
    int state;

    private java.lang.Object stateEvent = new java.lang.Object();
    private java.lang.Object writeEvent = new java.lang.Object();
    
    private boolean writeLocked;
    
    // These I/O streams are the ONLY ONES that should be used.
    // i.e. Do not directly use the input/output streams that socket gives.
    // This restriction is to allow connections to service multiple
    // protocols (e.g. http for tunneling), which may require the
    // socket's streams to be wrapped in other streams.
    InputStream inputStream;
    OutputStream outputStream;
    
    /**
     * Called after client creates a connection to server
     * or after server accepts an incoming connection.
     * @param host The remote host pointed to by this connection.
     * @param port The remote port used by this connection.
     */
    public IIOPConnection(ORB orb, ServerGIOP server,
                          ConnectionTable ctab, EndPoint ep)
    {
	this.orb = orb;
	this.server = server;
	this.connectionTable = ctab;
        this.endpoint = ep;
        this.codeBaseServerIOR = null;
        String host = endpoint.getHostName();
        int port = endpoint.getPort();
        threadName = "JavaIDL Reader for " + host + ":" + port;

        mediator = new MessageMediator(this);

        // Only do the next two because we're a client
        clientReplyMap = new HashMap();
        out_calls = new Hashtable();

	// Both client and servers.
	idToFragmentedOutputStream = new Hashtable();
        
        final ThreadGroup finalThreadGroup = orb.threadGroup;
        final String finalThreadName = threadName;
        final IIOPConnection finalThis = this;
        final boolean finalTransportDebugFlag = orb.transportDebugFlag;
        try {
            AccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    reader = new ReaderThread(finalThreadGroup, finalThis, finalThreadName, finalTransportDebugFlag);
                    return null;
                }
            });
        } catch (SecurityException e) {
            //
            // For some reason we're not allowed to create a new thread
            // in the same thread group that the ORB was initialized in.
            // Fall back on creating the thread in the calling thread's
            // group.
            //
            AccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    reader = new ReaderThread(finalThis, finalThreadName, finalTransportDebugFlag);
                    return null;
                }
            });
        }
        
        synchronized ( stateEvent ){
            state = OPENING;
        }
    }
    
    /**
     * Called only from ConnectionTable.get() after server accepts an
     * incoming connection.
     * @param sock The socket for this connection.
     * @param inputStream The inputstream to use. It may be different
     * from a socket's inputstream.
     * @param outputStream The outputstream to use. It may be different
     * from a socket's outputstream.
     */
    public IIOPConnection(ORB orb, 
                          ServerGIOP server,
                          EndPoint ep, 
                          Socket sock, 
                          InputStream inputStream, 
                          OutputStream outputStream,
                          ConnectionTable ct)
    {
        this(orb, server, ct, ep); 

        mediator = new MessageMediator(this);
        
        this.socket = sock;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.connectionTable = ct;
        
        isServer = true;
         
        // Only create the serverRequestMap for servers
        serverRequestMap = Collections.synchronizedMap(new HashMap());

	// Both client and servers.
	idToFragmentedOutputStream = new Hashtable();

        state = ESTABLISHED;
        
        // Catch exceptions since setDaemon can cause a
        // security exception to be thrown under netscape
        // in the Applet mode
        try {
            AccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    reader.setDaemon(true);
                    return null;
                }
            });
        } catch (Exception e) {}
    
        reader.start();
    }

    public synchronized boolean isPostInitialContexts() {
        return postInitialContexts;
    }

    // Can never be unset...
    public synchronized void setPostInitialContexts(){
        postInitialContexts = true;
    }
    
    public java.io.InputStream getInputStream() {
        return inputStream;
    }
    
    public ServerGIOP getServerGIOP() {
        return server;
    }
    
    String getHost() {
        return endpoint.getHostName();
    }
    
    int getPort() {
        return endpoint.getPort();
    }
    
    EndPoint getEndpoint() {
        return endpoint;
    }
    
    /**
     * Read in the IIOP message from the network's InputStream and
     * create an IIOPInputStream object.
     * Called from ReaderThread only.
     *
     * The protocol of use by ReaderThread has been changed
     * so that non-null return values are not expected
     * for Fragment message types.
     * 
     */

    public final void processInput() throws Exception
    {
        mediator.processRequest();
    }

    /**
     * Signal the client thread that the given request has been received,
     * and set the input stream on its out call descriptor.
     */
    void signalReplyReceived(int requestId, IIOPInputStream is) 
    {
        Integer id = new Integer(requestId);
        OutCallDesc call = (OutCallDesc) out_calls.get(id);

        // This is an interesting case.  It could mean that someone sent us a
        // reply message, but we don't know what request it was for.  That
        // would probably call for an error.  However, there's another case
        // that's normal and we should think about --
        //
        // If the unmarshaling thread does all of its work inbetween the time
        // the ReaderThread gives it the last fragment and gets to the
        // out_calls.get line, then it will also be null, so just return;
        if (call == null)
            return;

        // Set the reply IIOPInputStream and signal the client thread
        // that the reply has been received.
        // The thread signalled will remove outcall descriptor if appropriate.
        // Otherwise, it'll be removed when last fragment for it has been put on
        // BufferManagerRead's queue.
        synchronized (call.done) {
            call.s = is;
            call.done.notify();
        }
    }

    /**
     * Wake up the outstanding requests on the connection, and hand them
     * COMM_FAILURE exception with a given minor code. Also, delete connection
     * from connection table and stop the reader thread. Note that this should only
     * ever be called by the Reader thread for this connection.
     * @param minor_code The minor code for the COMM_FAILURE major code.
     * @param die Kill the reader thread (this thread) before exiting.
     */
    void purge_calls(int minor_code, boolean die, boolean lockHeld)
    {
        OutCallDesc call;

        if (orb.transportDebugFlag) {
            dprint("purge_calls: starting: code = " + minor_code
                   + " die = " + die);
	}

        //
        // If this invocation is a result of ThreadDeath caused
        // by a previous execution of this routine, just exit.
        //
        synchronized ( stateEvent ){
            if ((state == ABORT) || (state == CLOSE_RECVD)) {
                if (orb.transportDebugFlag)
                    dprint("purge_calls: exiting duplicate invocation");
                return;
            }
        }

        //
        // Grab the writeLock (freeze the calls)
        //
        try {
            if (!lockHeld)
                writeLock();
        } catch (SystemException ex) {
            if (orb.transportDebugFlag)
                dprint("purge_calls: caught exception " + ex + "; continuing");
        }

        //
        // Mark the state of the connection and determine the request status
        //
        org.omg.CORBA.CompletionStatus completion_status;

        synchronized ( stateEvent ){
            if (minor_code ==  Connection.CONN_REBIND) {
                state = CLOSE_RECVD;
                completion_status = CompletionStatus.COMPLETED_NO;
            } else {
                state = ABORT;
                completion_status = CompletionStatus.COMPLETED_MAYBE;
            }
            stateEvent.notifyAll();
        }
    

        //
        // Close the socket (if its not already closed)
        //
        try {
            // if theres no socket/connection, this is just ignored
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception ex) {
        }

        SystemException comm_failure_exc =
            new COMM_FAILURE(minor_code, completion_status);

        // Signal all threads with outstanding requests on this
        // connection and give them the COMM_FAILURE exception.
        java.util.Enumeration e = out_calls.elements();
        while(e.hasMoreElements()) {
            call = (OutCallDesc) e.nextElement();
        
            synchronized(call.done){
                call.s = null;
                call.exc = comm_failure_exc;
                call.done.notify();
            }
        }

        //
        // delete connection from cache and stop the reader thread
        //
        connectionTable.deleteConn(endpoint);

        //
        // Signal all the waiters of the writeLock.
        // There are 4 types of writeLock waiters:
        // 1. Send waiters:
        // 2. SendReply waiters:
        // 3. cleanUp waiters:
        // 4. purge_call waiters:
        //
        writeUnlock();

    }


    /**
     * Sets up an established connection
     */
    public void setConnection(Socket _socket, ConnectionTable ctab)
        throws Exception
    {
        socket = _socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        connectionTable = ctab;

	
        synchronized ( stateEvent ){
            state = ESTABLISHED;
        
            // Catch exceptions since setDaemon can cause a
            // security exception to be thrown under netscape
            // in the Applet mode
            try {
                AccessController.doPrivileged(new PrivilegedAction() {
                    public java.lang.Object run() {
                        reader.setDaemon(true);
                        return null;
                    }
                });
            }
            catch (Exception e) {}
        
            reader.start();
            stateEvent.notifyAll();
        }

    }

    /**
     * Changes state of connection to aborted, notifying waiters
     */
    public void abortConnection()
    {
        synchronized ( stateEvent ){
        
            state = ABORT;
            ((ReaderThread) reader).shutdown();
            stateEvent.notifyAll();
        }
    
    }

    /**
     * Sets the writeLock for this connection.
     * If the writeLock is already set by someone else, block till the
     * writeLock is released and can set by us.
     * IMPORTANT: this connection's lock must be acquired before
     * setting the writeLock and must be unlocked after setting the writeLock.
     */
    protected boolean writeLock()
    {

        // Keep looping till we can set the writeLock.
        while ( true ) {
            synchronized ( stateEvent ){
                switch ( state ) {
                
                case OPENING:
                    try {
                        stateEvent.wait();
                    } catch (InterruptedException ie) {};
                    // Loop back
                    break;
                
                case ESTABLISHED:
                    synchronized (writeEvent) {
                        if (!writeLocked) {
                            writeLocked = true;
                            return true;
                        }
                    
                        try {
                            writeEvent.wait();
                        } catch (InterruptedException ie) {};
                    }
                    // Loop back
                    break;
                
                    //
                    // XXX
                    // Need to distinguish between client and server roles
                    // here probably.
                    //
                case ABORT:
                    throw new COMM_FAILURE( MinorCodes.WRITE_ERROR_SEND,
                                            CompletionStatus.COMPLETED_NO);
                     
                case CLOSE_RECVD:
                    // the connection has been closed or closing
                    // ==> throw rebind exception
                
                    throw new COMM_FAILURE( MinorCodes.CONN_CLOSE_REBIND,
                                            CompletionStatus.COMPLETED_NO);
                
                default:
                    if (orb.transportDebugFlag)
                        dprint("Connection:writeLock: weird state");
                
                    delete(Connection.CONN_ABORT);
                    return false;
                }
            }
        }
    }

    /**
     * Release the write lock on this connection.
     */
    protected void writeUnlock()
    {
        synchronized (writeEvent) {
            writeLocked = false;
            writeEvent.notify(); // wake up one guy waiting to write
        }
    }


    public void delete()
    {
        delete(Connection.CONN_ABORT);
    }

    void delete(int code)
    {
        DeleteConn dc = new DeleteConn(code);
        reader.stop(dc);
    }


    /** Send a two-way IIOP message to the server.
     */
    public IIOPInputStream invoke(IIOPOutputStream s)
        throws SystemException
    {
        return send(s, false);
    }

    /**
     * In 1.1 and 1.2, it seeds the response to be
     * continued if it's not the last fragment.
     */
    IIOPInputStream getResponse(boolean isOneway, int requestID)
    {

        IIOPInputStream returnStream = null;

        Integer requestId = new Integer(requestID);

        OutCallDesc call = (OutCallDesc)out_calls.get(requestId);

        if (isOneway) {
            out_calls.remove(requestId);
            return null;
        }

        // It's very important that only the client thread
        // removes its OutCallDesc from the table.
        if (call == null)
            throw new INTERNAL(MinorCodes.NULL_OUT_CALL,
                               CompletionStatus.COMPLETED_MAYBE);

        synchronized(call.done) {

            while (call.s == null && call.exc == null) {
                // Wait for the reply from the server.
                // The ReaderThread reads in the reply IIOP message
                // and signals us.
                try {
                    call.done.wait();
                } catch (InterruptedException ie) {};
            }

            // Remove this request ID from the out call descriptor map.
            // The ReaderThread can continue to add fragments because it
            // still has access to the input stream via the clientReplyMap.
            out_calls.remove(requestId);

            if (call.exc != null) {
                throw call.exc;
            }

            returnStream = call.s;
        }

        // REVISIT -- exceptions from unmarshaling code will
        // go up through this client thread!

        // If the header was already unmarshaled, this won't
        // do anything
        if (returnStream != null)
            returnStream.unmarshalHeader();

        return returnStream;
    }

    void createOutCallDescriptor(int requestId)
    {
        // Temporary solution -- check if we're a server or not
        if (!isServer) {
            Integer requestID = new Integer(requestId);
        
            OutCallDesc call = new OutCallDesc();
            call.thd = Thread.currentThread();

            out_calls.put(requestID, call);
        }
    }

    public void removeOutCallDescriptor(int requestId)
    {
        if (!isServer) {
            Integer requestID = new Integer(requestId);
            out_calls.remove(requestID);
        }
    }

    // Assumes the caller handles writeLock and writeUnlock
    void sendWithoutLock(IIOPOutputStream os)
    {
        // Don't we need to check for CloseConnection
        // here?  REVISIT

        try {

            // Write the fragment/message
            os.writeTo(outputStream);
            outputStream.flush();

        } catch (IOException e1) {

            /*
             * ADDED(Ram J) 10/13/2000 In the event of an IOException, try
             * sending a CancelRequest for regular requests / locate requests
             */

            // Since IIOPOutputStream's msgheader is set only once, and not
            // altered during sending multiple fragments, the original 
            // msgheader will always have the requestId.
	    // REVISIT This could be optimized to send a CancelRequest only
	    // if any fragments had been sent already.

            Message msg = os.getMessage();
            if (msg.getType() == Message.GIOPRequest ||
                    msg.getType() == Message.GIOPLocateRequest) {
                GIOPVersion requestVersion = msg.getGIOPVersion();
                int requestId = MessageBase.getRequestId(msg);
                try {
                    sendCancelRequest(requestVersion, requestId);
                } catch (IOException e2) {
                    // most likely an abortive connection closure.
                    // ignore, since nothing more can be done.
                }
            }

            // REVISIT When a send failure happens, purge_calls() need to be 
            // called to ensure that the connection is properly removed from
            // further usage (ie., cancelling pending requests with COMM_FAILURE 
            // with an appropriate minor_code CompletionStatus.MAY_BE).            

            // Relying on the IIOPOutputStream (as noted below) is not 
            // sufficient as it handles COMM_FAILURE only for the final 
            // fragment (during invoke processing). Note that COMM_FAILURE could 
            // happen while sending the initial fragments. 
            // Also the IIOPOutputStream does not properly close the connection.
            // It simply removes the connection from the table. An orderly
            // closure is needed (ie., cancel pending requests on the connection
            // COMM_FAILURE as well.
            
            // IIOPOutputStream will cleanup the connection info when it
            // sees this exception.
            throw new COMM_FAILURE(MinorCodes.WRITE_ERROR_SEND,
				   CompletionStatus.COMPLETED_NO);
        }
    }

    /** Send an IIOP message to the server.
     *  If not oneway, wait for the reply.
     */
    public IIOPInputStream send(IIOPOutputStream s, boolean oneWay)
    {
        /*
        writeLock();

        createOutCallDescriptor(MessageBase.getRequestId(s.getMessage()));

        try {

            sendWithoutLock(s);

        } finally {

            writeUnlock();

        }
        */
        // This will force all fragments to be sent.
        s.finishSendingMessage();
        return getResponse(oneWay, MessageBase.getRequestId(s.getMessage()));
    }

    public void sendReply(IIOPOutputStream os)
        throws Exception
    {
        os.finishSendingMessage();
    }

    /** public IOR locate(byte[] key) is now in Connection.java */

    /***************************************************************************
    * The following methods are for dealing with Connection cleaning for
    * better scalability of servers in high network load conditions.
    ***************************************************************************/

    /**
     * Send a CancelRequest message. This does not lock the connection, so the
     * caller needs to ensure this method is called appropriately.
     *
     * @exception IOException if an I/O error occurs (could be due to abortive
     *                        connection closure).
     */
    public void sendCancelRequest(GIOPVersion giopVersion, int requestId)
            throws IOException {

        Message msg = MessageBase.createCancelRequest(giopVersion, requestId);

        IIOPOutputStream os = new IIOPOutputStream(giopVersion, orb, this);
        os.setMessage(msg);
        msg.write(os);

        os.writeTo(outputStream);
        outputStream.flush();
    }

    public void sendCancelRequestWithLock(GIOPVersion giopVersion,
					  int requestId)
            throws 
		IOException 
    {
	writeLock();
	try {
	    sendCancelRequest(giopVersion, requestId);
	} finally {
	    writeUnlock();
	}
    }

    /**
     * Send a CloseConnection message. This does not lock the connection, so the
     * caller needs to ensure this method is called appropriately.
     *
     * @exception IOException if an I/O error occurs (could be due to abortive
     *                        connection closure).
     */
    public void sendCloseConnection(GIOPVersion giopVersion)
            throws IOException {
        Message msg = MessageBase.createCloseConnection(giopVersion);

        IIOPOutputStream os = new IIOPOutputStream(giopVersion, orb, this);
        os.setMessage(msg);
        msg.write(os);

        os.writeTo(outputStream);
        outputStream.flush();
    }

    /**
     * Send a MessageError message. This does not lock the connection, so the
     * caller needs to ensure this method is called appropriately.
     *
     * @exception IOException if an I/O error occurs (could be due to abortive
     *                        connection closure).
     */
    public void sendMessageError(GIOPVersion giopVersion)
            throws IOException {

        Message msg =
            MessageBase.createMessageError(giopVersion);

        IIOPOutputStream os = new IIOPOutputStream(giopVersion, orb, this);
        os.setMessage(msg);
        msg.write(os);

        os.writeTo(outputStream);
        outputStream.flush();
    }

    public boolean isBusy()
    {
        // Note: Hashtable.size() is not synchronized
        if (requestCount > 0 || out_calls.size() > 0)
            return true;
        else
            return false;
    }


    /**
     * Cleans up this Connection.
     * This is called from ConnectionTable, from the ListenerThread.
     * Note:it is possible for this to be called more than once
     */
    public synchronized void cleanUp() throws Exception
    {
        writeLock();

        // REVISIT It will be good to have a read lock on the reader thread
        // before we proceed further, to avoid the reader thread (server side)
        // from processing requests. This avoids the risk that a new request
        // will be accepted by ReaderThread while the ListenerThread is 
        // attempting to close this connection.
        
        if (requestCount > 0 || out_calls.size() > 0) { // we are busy!
            writeUnlock();
            throw new Exception();
        }
        
        try {
            
            sendCloseConnection(GIOPVersion.V1_0);
            synchronized ( stateEvent ){
                state = CLOSE_SENT;
                stateEvent.notifyAll();
            }

            // stop the reader without causing it to do purge_calls
            Exception ex = new Exception();
            reader.stop(ex);

            // this also does writeUnlock();
            purge_calls(Connection.CONN_REBIND, false, true);

        } catch (Exception ex) {}
    }

    /** It is possible for a Close Connection to have been
     ** sent here, but we will not check for this. A "lazy"
     ** Exception will be thrown in the Worker thread after the
     ** incoming request has been processed even though the connection
     ** is closed before the request is processed. This is o.k because
     ** it is a boundary condition. To prevent it we would have to add
     ** more locks which would reduce performance in the normal case.
     **/
    public synchronized void requestBegins()
    {
        requestCount++;
    }

    public synchronized void requestEnds(IIOPInputStream request)
    {
        if (request.getGIOPVersion().equals(GIOPVersion.V1_2))
            serverRequestMap.remove(new Integer(MessageBase.getRequestId(request.getMessage())));
        if (request.getGIOPVersion().equals(GIOPVersion.V1_1))
            theOnly1_1ServerRequestImpl = null;       

        requestCount--;
    }

    void shutdown()
    {
        // The order is important here. First make sure that the thread knows what to do
        // after the socket closes before we close it.
        ((ReaderThread)reader).shutdown();
        super.shutdown();
    }

    public void print()
    {
        System.out.println("Connection for " + endpoint.getHostName() +
                           " @ " + endpoint.getPort());
        System.out.println("    Time stamp = " + timeStamp);
        boolean alive = reader.isAlive();
        if (alive)
            System.out.println(" Reader is Alive");
        else
            System.out.println(" Reader is not Alive");
    }

    // Begin Code Base methods ---------------------------------------
    //
    // Set this connection's code base IOR.  The IOR comes from the
    // SendingContext.  This is an optional service context, but all
    // JavaSoft ORBs send it.
    //
    // The set and get methods don't need to be synchronized since the
    // first possible get would occur during reading a valuetype, and
    // that would be after the set.
    public final void setCodeBaseIOR(IOR ior) {
        codeBaseServerIOR = ior;
    }

    final IOR getCodeBaseIOR() {
        return codeBaseServerIOR;
    }

    // Get a CodeBase stub to use in unmarshaling.  The CachedCodeBase
    // won't connect to the remote codebase unless it's necessary.
    final CodeBase getCodeBase() {
        return cachedCodeBase;
    }

    // End Code Base methods -----------------------------------------

    final void createIdToFragmentedOutputStreamEntry (
        int requestID,
	IIOPOutputStream outputStream)
    {
	idToFragmentedOutputStream.put(new Integer(requestID),
				       outputStream);
    }

    public final IIOPOutputStream getIdToFragmentedOutputStreamEntry(
        int requestID)
    {
	return (IIOPOutputStream)
	    idToFragmentedOutputStream.get(new Integer(requestID));
    }

    public final void removeIdToFragmentedOutputStreamEntry(int requestID)
    {
	idToFragmentedOutputStream.remove(new Integer(requestID));
    }
}
