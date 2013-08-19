/*
 * @(#)RequestProcessor.java	1.20 03/01/23
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

import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.RequestHandler;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.core.GIOPVersion;

import com.sun.corba.se.internal.orbutil.Work;

import com.sun.corba.se.internal.iiop.messages.*;

/**
 * Replaces CachedWorkerThread -- handles Requests and LocateRequests, and can
 * be run in a pooled thread.
 */
final class RequestProcessor implements Work
{
    private static final String name = "RequestProcessor";

    private RequestHandler handler;
    private IIOPConnection conn;
    private IIOPInputStream request;

    RequestProcessor(RequestHandler h, IIOPConnection c, IIOPInputStream is) {
        handler = h;
        conn = c;
        request = is;
    }

    public final String getName() {
        return name;
    }

    public void process()
    {
        IIOPOutputStream response = null;

        Message msg = request.getMessage();

        ORB orb = conn.getORB() ;

        conn.requestBegins();

        try {

            switch (msg.getType()) {

            case Message.GIOPRequest: {
                ServerRequest srequest = (ServerRequest)request;

                try {

                    // Doesn't do anything if the header was already
                    // unmarshaled
                    request.unmarshalHeader();

                    // Here is the actual dispatch to the ORB which
                    // dispatches to the subcontract which dispatches to the
                    // skeleton which finally dispatches to the user's servant.

                    response = (IIOPOutputStream)handler.process(srequest);

                    if (srequest.isOneWay()) {
                        return;
                    }
                } catch (SystemException ex) {

                    // If we haven't unmarshaled the header, we probably don't
                    // have enough information to even send back a reply.
                    // REVISIT
                    if (!request.unmarshaledHeader())
                        return;

                    // Caught a system exception marshal it back to the client
                    //
                    // ex.printStackTrace();
                    // conn.dprint("run: got exception: " + ex);

                    // Shouldn't we send back the service contexts?? REVISIT
                    try {
                        response = (IIOPOutputStream)
                            srequest.createSystemExceptionResponse(ex, null);
                    } catch (Exception e) {
                        return; // marshaling exception error; return;
                    }

                } catch (AddressingDispositionException ex) {

                    // create a response containing the expected target
                    // addressing disposition.
                    
                    RequestMessage rmsg = (RequestMessage) msg;
                    int requestId = rmsg.getRequestId();                    

                    ReplyMessage replyMsg = MessageBase.createReply(
                                    orb, rmsg.getGIOPVersion(),
                                    rmsg.getRequestId(),
                                    ReplyMessage.NEEDS_ADDRESSING_MODE, 
                                    null, null);

                    response = new IIOPOutputStream(
                                    request.getGIOPVersion(),
                                    request.getConnection().getORB(),
                                    request.getConnection());
                    response.setMessage(replyMsg);
                    replyMsg.write(response);
                    AddressingDispositionHelper.write(
                        response, ex.expectedAddrDisp());
                    
                } catch (RequestCanceledException ex) {

		    // Creating an exception response causes the
		    // poa current stack, the interceptor stacks, etc.
		    // to be balanced.  It also notifies interceptors
		    // that the request was cancelled.

		    SystemException sex = 
			new TRANSIENT("RequestCanceled",
				      MinorCodes.REQUEST_CANCELED,
				      CompletionStatus.COMPLETED_NO);
		    srequest.createSystemExceptionResponse(sex, null);

		    // NOTE: the response stream is not returned.
		    // It is only created for its side-effects.

                    // The worker thread can now process further requests.
                    return;
                } catch (Throwable ex) {

		    // NOTE: We do not trap ThreadDeath above Throwable.
		    // There is no reason to stop the thread.  It is
		    // just a worker thread.  The ORB never throws
		    // ThreadDeath.  Client code may (e.g., in ServantManagers,
		    // interceptors, or servants) but that should not
		    // effect the ORB threads.  So it is just handled
		    // generically.

                    //
                    // Caught an unknown Exception or Error, create a
                    // SystemException and marshal it back to the client
                    //
                    // conn.dprint("run: got exception: " + ex);
                    // ex.printStackTrace();

                    // If we haven't unmarshaled the header, we probably don't
                    // have enough information to even send back a reply.
                    // REVISIT  (send closeconnection)
                    if (!request.unmarshaledHeader())
                        return;

                    SystemException exc = new UNKNOWN(MinorCodes.UNKNOWN_SERVER_ERROR,
                                                      CompletionStatus.COMPLETED_MAYBE);
                    try {
                        response = (IIOPOutputStream)
                            srequest.createSystemExceptionResponse(exc, null);
                    } catch (Exception e) {
                        return; // marshaling exception error; return;
                    }
                }
                break;
            }
            case Message.GIOPLocateRequest: {

                LocateRequestMessage lmsg = null;
                LocateReplyMessage reply = null;
                int requestId = -1;
                GIOPVersion giopVersion = null;
                IOR ior = null;
                short addrDisp = -1;
                
                try {
                    request.unmarshalHeader();

                    lmsg = (LocateRequestMessage) msg;
                    requestId = lmsg.getRequestId();
                    giopVersion = lmsg.getGIOPVersion();

                    ior = handler.locate(lmsg.getObjectKey());

                    if ( ior == null ) {
                        reply = MessageBase.createLocateReply(
                                    orb, giopVersion, requestId,
                                    LocateReplyMessage.OBJECT_HERE, null);

                    } else {
                        reply = MessageBase.createLocateReply(
                                    orb, giopVersion, requestId,
                                    LocateReplyMessage.OBJECT_FORWARD, ior);
                    }

                    // Shouldn't we also catch SystemExceptions as above?  REVISIT
                    
                } catch (AddressingDispositionException ex) {

                    // create a response containing the expected target
                    // addressing disposition.
                    
                    lmsg = (LocateRequestMessage) msg;
                    requestId = lmsg.getRequestId();                    
                    giopVersion = lmsg.getGIOPVersion();
                    
                    reply = MessageBase.createLocateReply(
                                orb, giopVersion,     
                                requestId,
                                LocateReplyMessage.LOC_NEEDS_ADDRESSING_MODE, 
                                null);                                   
                    addrDisp = ex.expectedAddrDisp();
                    
                } catch (RequestCanceledException ex) {
                    return; // no need to send reply
                } catch ( Exception ex ) {

                    // REVISIT If exception is not OBJECT_NOT_EXIST, it should
                    // have a different reply

                    // This handles OBJECT_NOT_EXIST exceptions thrown in
                    // the subcontract or obj manager. Send back UNKNOWN_OBJECT.

                    reply = MessageBase.createLocateReply(
                                    orb, giopVersion, requestId,
                                    LocateReplyMessage.UNKNOWN_OBJECT, null);
                }

                // This chooses the right buffering strategy for the locate msg.
                // locate msgs 1.0 & 1.1 :=> grow, 1.2 :=> stream
                response = IIOPOutputStream.createIIOPOutputStreamForLocateMsg(
                                giopVersion, request.getConnection().getORB(),
                                request.getConnection());

                reply.write(response);
                response.setMessage(reply);
                if (ior != null) {
                    ior.write(response);
                }
                if (addrDisp != -1) {
                    AddressingDispositionHelper.write(response, addrDisp);
                }
                break;
            }

            default:
                //
                // Any other message type is unexpected and is a fatal
                // error for this connection ??
                //
                return;

            } // end switch (msgType)


            // Send the reply on the wire
            // Only RequestMessages and LocateRequestMessages have replies
            conn.sendReply(response);

        } catch(Exception e) {
            // REVISIT This block should catch Throwable
            // and possibly do close connection processing.
	    // Most likely it should do an assert since we should not
	    // ever make it here.
        } finally {
            conn.requestEnds(request);
        }
    }
}
