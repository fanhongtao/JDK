/*
 * @(#)MessageMediator.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import java.io.IOException;

// to force 1.3 compiler to pickup the newer location
// - remove once we move to JDK1.4
import com.sun.corba.se.internal.iiop.messages.Message;

import com.sun.corba.se.internal.iiop.messages.*;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.iiop.IIOPConnection.OutCallDesc;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.orbutil.ThreadPool;
import com.sun.corba.se.internal.orbutil.Work;

/**
 * Provides processing for messages without if/switch overhead for versions
 * by using polymorphism and callbacks (double dispatch pattern).  Separates 
 * IIOPConnection from the workings of the  iiop/message system and vice 
 * versa.  Makes it easy to see the behavior for a given {msg type, version}
 * combination.
 *
 * A single thread (the ReaderThread) accesses a MessageMediator instance.
 */
public final class MessageMediator
{
    /** 
     * With GIOP 1.1 replies, we have no guarantee that the request ID
     * was in the first fragment, so we can't signal the client thread
     * to wake up and unmarshal the extended header.  Thus, we must
     * use another thread to do it.
     */
    private static class ReplyProcessor_1_1 implements Work
    {
        private static final String name = "ReplyProcessor 1.1";

        private IIOPConnection conn;
        private IIOPInputStream reply;

        ReplyProcessor_1_1(IIOPConnection conn, IIOPInputStream reply) {
            this.conn = conn;
            this.reply = reply;
        }

        public final String getName() {
            return name;
        }
        
        public void process() 
        {
            // Needs error handling
            reply.unmarshalHeader();

            ReplyMessage msg = (ReplyMessage)reply.getMessage();

            conn.signalReplyReceived(msg.getRequestId(),
                                     reply);
        }
    }    

    public MessageMediator(IIOPConnection conn) 
    {
        this.conn = conn;
    }

    // This could be a singleton, but that would lead to more
    // parameter passing.  As is, there will be one instance per
    // IIOPConnection.
    private IIOPConnection conn;
    private byte[] buf;

    // If this weren't static, there wouldn't be any synchronization overhead
    // when threads on different connections fight for access.  The downside
    // would be that cached threads would only be available per connection.
    // Since there isn't a scheme for expiring cached threads, yet, I've
    // made it static. -eea1 REVISIT
    //
    // You can argue this should be in IIOPConnection;
    private final static ThreadPool threadPool = new ThreadPool();

    private void dprint( String msg ) {
	ORBUtility.dprint( this, msg ) ;
    }

    /**
     * Create the appropriate message type, allocate a byte buffer of the
     * appropriate size, read in the message, and use the callback on the
     * message object to do the processing.
     */
    public final void processRequest()
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Creating message from stream");

        // Read in the message header and create the appropriate type
        // of IIOP message.
        MessageBase msg = (MessageBase)MessageBase.createFromStream(conn.getORB(), 
                                                                    conn.getInputStream());

        // Create a buffer of the correct size.  We don't even have
        // to copy the GIOP header into it since we'll never look
        // at the bytes again.
        this.buf = new byte[msg.getSize()];

        if (conn.getORB().transportDebugFlag)
            dprint("Reading the message fully, size =" + msg.getSize());

        // Read all the data into the buffer
        MessageBase.readFully(conn.getInputStream(), 
                              buf, 
                              MessageBase.GIOPMessageHeaderLength, 
                              msg.getSize() - MessageBase.GIOPMessageHeaderLength);

        if (conn.getORB().giopDebugFlag) {

            // For debugging purposes, copy the 12 bytes of the
            // GIOP header in to the main buffer
            System.arraycopy(msg.giopHeader, 0,
                             this.buf, 0,
                             12);
            
            dprint("Received message:");
            ByteBufferWithInfo bbwi = new ByteBufferWithInfo(this.buf, 0);
            bbwi.buflen = msg.getSize();
            CDRInputStream_1_0.printBuffer(bbwi);
        }

        // Ask the message to call back to the mediator to handle
        // the request.  The mediator does the appropriate thing
        // based on the message.
    
        msg.callback(this);
    }

    // (Currently this handles message types that we don't create classes for)
    public final void handleInput(MessageBase header)
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling other GIOP message: " + header.getType());

        switch(header.getType()) 
        {
            case Message.GIOPCloseConnection:
                if (conn.getORB().transportDebugFlag)
                    dprint("Connection.processInput: got CloseConn, purging");
                conn.purge_calls(Connection.CONN_REBIND, true, false);
                break;
            case Message.GIOPMessageError:
                if (conn.getORB().transportDebugFlag)
                    dprint("Received MessageError, purging");
                conn.purge_calls(MinorCodes.RECV_MSG_ERROR, true, false);
                break;
            default:
                if (conn.getORB().transportDebugFlag)
                    dprint("Connection: bad message type" + header.getType());

                throw new INTERNAL(MinorCodes.BAD_GIOP_REQUEST_TYPE,
                                   CompletionStatus.COMPLETED_NO);
        }
    }

    
    // Request messages -----------------------------

    public final void handleInput(RequestMessage_1_0 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.0 request");

        IIOPInputStream is = new ServerRequestImpl(conn, buf, header);

        threadPool.addWork(new RequestProcessor(conn.getServerGIOP().getRequestHandler(),
                                                conn,
                                                is));
    }

    public final void handleInput(RequestMessage_1_1 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.1 request");

        IIOPInputStream is = new ServerRequestImpl(conn, buf, header);

        // More fragments are coming to complete this request message
        // add stream to the serverRequestMap
        if (header.moreFragmentsToFollow())
            conn.theOnly1_1ServerRequestImpl = (ServerRequestImpl)is;

        threadPool.addWork(new RequestProcessor(conn.getServerGIOP().getRequestHandler(),
                                                conn,
                                                is));
    }

    public final void handleInput(RequestMessage_1_2 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.2 request");

        IIOPInputStream is = new ServerRequestImpl(conn, buf, header);

        header.unmarshalRequestID(buf);

        // More fragments are coming to complete this request message
        // add stream to the serverRequestMap
        if (header.moreFragmentsToFollow())
            conn.serverRequestMap.put(new Integer(header.getRequestId()), is);

        threadPool.addWork(new RequestProcessor(conn.getServerGIOP().getRequestHandler(),
                                                conn,
                                                is));
    }

    // Reply messages ---------------------------------

    public final void handleInput(ReplyMessage_1_0 header) 
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.0 reply");

        IIOPInputStream is = new ClientResponseImpl(conn, buf, header);

        is.unmarshalHeader();

        conn.signalReplyReceived(header.getRequestId(), is);
    }

    public final void handleInput(ReplyMessage_1_1 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.1 reply");

        IIOPInputStream is = new ClientResponseImpl(conn, buf, header);

        // More fragments are coming to complete this reply, so keep
        // a reference to the InputStream so we can add the fragments
        if (header.moreFragmentsToFollow()) {
            conn.theOnly1_1ClientResponseImpl = (ClientResponseImpl)is;
            
            // In 1.1, we can't assume that we have the request ID in the
            // first fragment.  Thus, another thread is used to unmarshal
            // the extended header and wake up the client thread.
            threadPool.addWork(new ReplyProcessor_1_1(conn, is));

        } else {

            // If this is the only fragment, then we know the request
            // ID is here.  Thus, we can unmarshal the extended header
            // and wake up the client thread without using a third
            // thread as above.
            is.unmarshalHeader();

            conn.signalReplyReceived(header.getRequestId(), is);
        }
    }

    public final void handleInput(ReplyMessage_1_2 header) 
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.2 reply");

        IIOPInputStream is = new ClientResponseImpl(conn, buf, header);

        // We know that the request ID is in the first fragment
        header.unmarshalRequestID(buf);

        // More fragments are coming to complete this reply, so keep
        // a reference to the InputStream so we can add the fragments
        if (header.moreFragmentsToFollow())
            conn.clientReplyMap.put(new Integer(header.getRequestId()), is);

        conn.signalReplyReceived(header.getRequestId(), is);
    }

    // Locate request messages ------------------------

    // Versions 1.0 and 1.1 cannot be fragmented, so the implementation can be
    // the same here.
    public final void handleInput(LocateRequestMessage_1_0 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.0 LocateRequest");

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);

        threadPool.addWork(new RequestProcessor(conn.getServerGIOP().getRequestHandler(),
                                                conn,
                                                is));
    }

    public final void handleInput(LocateRequestMessage_1_1 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.1 LocateRequest");

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);

        threadPool.addWork(new RequestProcessor(conn.getServerGIOP().getRequestHandler(),
                                                conn,
                                                is));
    }

    public final void handleInput(LocateRequestMessage_1_2 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.2 LocateRequest");

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);

        header.unmarshalRequestID(buf);

        // More fragments are coming to complete this request message
        // add stream to the serverRequestMap
        if (header.moreFragmentsToFollow())
            conn.serverRequestMap.put(new Integer(header.getRequestId()), is);

        threadPool.addWork(new RequestProcessor(conn.getServerGIOP().getRequestHandler(),
                                                conn,
                                                is));
    }

    // Locate reply messages ------------------------

    public final void handleInput(LocateReplyMessage_1_0 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.0 LocateReply");

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);

        is.unmarshalHeader();

        conn.signalReplyReceived(header.getRequestId(), is);
    }

    public final void handleInput(LocateReplyMessage_1_1 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.1 LocateReply");

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);

        is.unmarshalHeader();

        // Fragmented LocateReplies are not allowed in 1.1
        conn.signalReplyReceived(header.getRequestId(), is);
    }

    public final void handleInput(LocateReplyMessage_1_2 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.2 LocateReply");

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);

        header.unmarshalRequestID(buf);

        // More fragments are coming to complete this reply, so keep
        // a reference to the InputStream so we can add the fragments
        if (header.moreFragmentsToFollow())
            conn.clientReplyMap.put(new Integer(header.getRequestId()), is);

        conn.signalReplyReceived(header.getRequestId(), is);
    }

    // Fragment messages ----------------------------

    public final void handleInput(FragmentMessage_1_1 header)
        throws IOException 
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.1 Fragment.  Last? " + header.moreFragmentsToFollow());

        IIOPInputStream is;

        if (conn.isServer())
            is = conn.theOnly1_1ServerRequestImpl;
        else
            is = conn.theOnly1_1ClientResponseImpl;

        // if there is no inputstream available, then discard the message
        // fragment. This can happen
        // 1. if a fragment message is received prior
        // to receiving the original request/reply message. Very unlikely.
        // 2. if a fragment message is received after the reply has been sent
        //    (early replies)
        // Note: In the case of early replies, the fragments received during
        // the request processing (which are never unmarshaled), will eventually
        // be discarded by the GC.
        if (is == null) {
            return;
        }

        is.getBufferManager().processFragment(buf, header);

        if (!conn.isServer()) {

            // Is it a last fragment of a reply ?
            if (!header.moreFragmentsToFollow()) {

                // It is not the responsibility of this thread to remove
                // the OutCallDesc from out_calls -- only the client thread
                // should do that.
                conn.theOnly1_1ClientResponseImpl = null;
            }
        }
    }

    public final void handleInput(FragmentMessage_1_2 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag)
            dprint("Handling GIOP 1.2 Fragment.  Last? " + header.moreFragmentsToFollow());

        // Unusual paradox:  We know it's a 1.2 fragment, we have the
        // data, but we need the IIOPInputStream instance to unmarshal the
        // request ID... but we need the request ID to get the IIOPInputStream
        // instance.

        header.unmarshalRequestID(buf);

        Integer requestId = new Integer(header.getRequestId());
        IIOPInputStream is;

        if (conn.isServer())
            is = (IIOPInputStream)conn.serverRequestMap.get(requestId);
        else
            is = (IIOPInputStream)conn.clientReplyMap.get(requestId);

        if (is == null) {
            return;
        }

        is.getBufferManager().processFragment(buf, header);

        if (!conn.isServer()) {

            // Is it a last fragment of a reply?
            if (!header.moreFragmentsToFollow()) {

                // It is not the responsibility of this thread to remove
                // the OutCallDesc from out_calls -- only the client thread
                // should do that.
                conn.clientReplyMap.remove(requestId);
            }
        }
    }

    // Cancel request messages -----------------------

    private final void processCancelRequest(int cancelReqId) {

        // The GIOP version of CancelRequest does not matter, since
        // CancelRequest_1_0 could be sent to cancel a request which
        // has a different GIOP version.

        /*
         * CancelRequest processing logic :
         *
         *  - find the request with matching requestId
         *
         *  - call cancelProcessing() in BufferManagerRead [BMR]
         *
         *  - the hope is that worker thread would call BMR.underflow()
         *    to wait for more fragments to come in. When BMR.underflow() is
         *    called, if a CancelRequest had already arrived,  
	 *    the worker thread would throw ThreadDeath,
         *    else the thread would wait to be notified of the
         *    arrival of a new fragment or CancelRequest. Upon notification,
         *    the woken up thread would check to see if a CancelRequest had
         *    arrived and if so throw a ThreadDeath or it will continue to
         *    process the received fragment.
         *
         *  - if all the fragments had been received prior to CancelRequest
         *    then the worker thread would never block in BMR.underflow().
         *    So, setting the abort flag in BMR has no effect. The request
         *    processing will complete normally.
         *
         *  - in the case where the server has received enough fragments to 
	 *    start processing the request and the server sends out 
	 *    an early reply. In such a case if the CancelRequest arrives 
	 *    after the reply has been sent, it has no effect.
         */

        if (!conn.isServer()) {
          return; // we do not support bi-directional giop yet, ignore.
        }

        // Try to get hold of the InputStream buffer.
        // In the case of 1.0 requests there is no way to get hold of
        // InputStream. Try out the 1.1 and 1.2 cases.

        // was the request 1.2 ?
        IIOPInputStream is = (IIOPInputStream) conn.serverRequestMap.get(
                                                    new Integer(cancelReqId));

        if (is == null) { // was the request 1.1 ?

            is = conn.theOnly1_1ServerRequestImpl;

            if (is == null) {
                // either the request was 1.0
                // or an early reply has already been sent
                // or request processing is over
                // or its a spurious CancelRequest
                return; // do nothing.
            }

            Message msg = is.getMessage();
            if (msg.getType() != Message.GIOPRequest) {
                // this should not be true. Fragmented 1.1 messages can
                // only be request messages.
                return; // do nothing
            }

            int requestId = ((RequestMessage) msg).getRequestId();

            if (requestId == 0) { // special case
                // this means that
                // 1. the 1.1 requests' requestId has not been received
                //    i.e., a CancelRequest was received even before the
                //    1.1 request was received. The spec disallows this.
                // 2. or the 1.1 request has a requestId 0.
                //
                // It is a little tricky to distinguish these two. So, be
                // conservative and do not cancel the request. Downside is that
                // 1.1 requests with requestId of 0 will never be cancelled.
                return; // do nothing
            }

            // at this point we do have a valid requestId for the 1.1 request

            if (requestId != cancelReqId) {
                // A spurious CancelRequest has been received.
                return; // do nothing
            }
        }

        // at this point we have chosen a request to be cancelled. But we
        // do not know if the target object's method has been invoked or not.
        // Request input stream being available simply means that the request
        // processing is not over yet. simply set the abort flag in the
        // BMRS and hope that the worker thread would notice it (this can
        // happen only if the request stream is being unmarshalled and the
        // target's method has not been invoked yet). This guarantees
        // that the requests which have been dispatched to the
        // target's method will never be cancelled.

        BufferManagerReadStream bufferManager = (BufferManagerReadStream)
                                                    is.getBufferManager();
        bufferManager.cancelProcessing(cancelReqId);
    }

    // Currently the same for all versions, but separate methods since
    // CancelRequestMessage is an interface.
    public final void handleInput(CancelRequestMessage_1_0 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag) {
            dprint("Handling GIOP 1.0 CancelRequest");
        }

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);
        header.read(is);

        this.processCancelRequest(header.getRequestId());
    }

    public final void handleInput(CancelRequestMessage_1_1 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag) {
            dprint("Handling GIOP 1.1 CancelRequest");
        }

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);
        header.read(is);

        this.processCancelRequest(header.getRequestId());
    }

    public final void handleInput(CancelRequestMessage_1_2 header)
        throws IOException
    {
        if (conn.getORB().transportDebugFlag) {
            dprint("Handling GIOP 1.2 CancelRequest");
        }

        IIOPInputStream is = new IIOPInputStream(conn, buf, header);
        header.read(is);
        
        this.processCancelRequest(header.getRequestId());
    }
}
