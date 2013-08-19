/*
 * @(#)Connection.java	1.85 03/01/23
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
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.DATA_CONVERSION;

import org.omg.CORBA.portable.*;

import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.core.EndPoint;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.orbutil.MinorCodes; //d11638
import com.sun.corba.se.internal.orbutil.ORBUtility; //d11638
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.LocateRequestMessage;
import com.sun.corba.se.internal.iiop.messages.LocateReplyMessage;
import com.sun.corba.se.internal.core.CodeSetComponentInfo;
import com.sun.corba.se.internal.core.OSFCodeSetRegistry;

/**
 * Common connection base class.
 */
abstract public class Connection
    implements
        com.sun.corba.se.connection.Connection
{

    // Connection close states
    // REVISIT - rather than define these "intermediate" constants,
    // just use the one from MinorCodes directly where these are
    // referenced.
    public static final int CONN_ABORT = MinorCodes.CONN_ABORT;
    public static final int CONN_REBIND = MinorCodes.CONN_REBIND;

    protected ORB orb;
    protected Socket socket;    // The socket used for this connection.
    protected long timeStamp = 0;
    protected boolean isServer = false;
    protected ConnectionTable connectionTable = null;

    // Negotiated code sets for char and wchar data
    protected CodeSetComponentInfo.CodeSetContext codeSetContext = null;

    void dprint(String msg) {
	ORBUtility.dprint(this, msg);
    }

    public ORB getORB() {
        return orb;
    }

    public Socket getSocket() {
	return socket;
    }

    abstract public IIOPInputStream invoke(IIOPOutputStream s)
	throws SystemException;

    abstract public void delete();

    abstract public java.io.InputStream getInputStream();

    abstract public ServerGIOP getServerGIOP();

    abstract public IIOPInputStream send(IIOPOutputStream s, boolean oneWay);

    abstract public void sendReply(IIOPOutputStream s) throws Exception;


    // All of Following abstract required for Connection cleanup

    abstract public void cleanUp() throws java.lang.Exception;

    abstract public boolean isBusy();

    abstract public void requestBegins();
    abstract public void requestEnds(IIOPInputStream request);

    abstract public void print();

    abstract public void setConnection(Socket _socket, ConnectionTable ctab)
	throws java.lang.Exception;
    abstract public void abortConnection();

    // Indicates whether or not ServiceContexts have even been exchange yet
    abstract public boolean isPostInitialContexts();

    // Sets to true the state that ServiceContexts have indeed been exchanged
    // once already
    abstract public void setPostInitialContexts();

    public IOR locate(int id, byte [] key, IOR ior)
    {
        LocateRequestMessage msg;
        IIOPOutputStream os;
        IIOPInputStream is;

        GIOPVersion requestVersion =
            GIOPVersion.chooseRequestVersion(orb, ior);
        msg = MessageBase.createLocateRequest(orb, requestVersion, id, key);

        // This chooses the right buffering strategy for the locate msg.
        // locate msgs 1.0 & 1.1 :=> grow, 1.2 :=> stream
        //os = orb.newOutputStream(this);
        os = com.sun.corba.se.internal.iiop.IIOPOutputStream.
                createIIOPOutputStreamForLocateMsg(
                        requestVersion, orb, this);
        os.setMessage(msg);
        msg.write(os);

        is = send(os, false);

        LocateReplyMessage reply;
        reply = (LocateReplyMessage) is.getMessage();
        switch (reply.getReplyStatus()) {
	    case LocateReplyMessage.UNKNOWN_OBJECT:
            throw new OBJECT_NOT_EXIST( MinorCodes.LOCATE_UNKNOWN_OBJECT,
					CompletionStatus.COMPLETED_NO );

	    case LocateReplyMessage.OBJECT_HERE:
            return null;

        case LocateReplyMessage.OBJECT_FORWARD:
	    case LocateReplyMessage.OBJECT_FORWARD_PERM: {
            /*
            IOR ior;

            ior = new IOR(orb);
            ior.read(is);

            return ior;
            */
            return reply.getIOR();
	    }
        }

        throw new INTERNAL( MinorCodes.BAD_LOCATE_REQUEST_STATUS,
            		    CompletionStatus.COMPLETED_NO );
    }

    void shutdown() {
        try {
            socket.close();
        } catch (IOException ioex) {}
    }

    public void stampTime() {
	connectionTable.stampTime(this);
    }

    // Sets this connection's code base IOR.  This is done after
    // getting the IOR out of the SendingContext service context.
    // Our ORBs always send this, but it's optional in CORBA.
    public abstract void setCodeBaseIOR(IOR codeBase);

    abstract IOR getCodeBaseIOR();
    abstract CodeBase getCodeBase();

    public synchronized CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
        // Needs to be synchronized for the following case when the client
        // doesn't send the code set context twice, and we have two threads
        // in ServerDelegate processCodeSetContext.
        //
        // Thread A checks to see if there is a context, there is none, so
        //     it calls setCodeSetContext, getting the synch lock.
        // Thread B checks to see if there is a context.  If we didn't synch,
        //     it might decide to outlaw wchar/wstring.
        return codeSetContext;
    }

    public synchronized void setCodeSetContext(CodeSetComponentInfo.CodeSetContext csc) {
        // Check whether or not we should set this.  Technically,
        // someone should only send a duplicate code set service context,
        // but this makes sure we always use the first one we got.
        if (codeSetContext == null) {
            
            if (OSFCodeSetRegistry.lookupEntry(csc.getCharCodeSet()) == null ||
                OSFCodeSetRegistry.lookupEntry(csc.getWCharCodeSet()) == null) {
                // If the client says it's negotiated a code set that
                // isn't a fallback and we never said we support, then
                // it has a bug.
                throw new DATA_CONVERSION(MinorCodes.BAD_CODESETS_FROM_CLIENT,
                                          CompletionStatus.COMPLETED_NO);
            }

            codeSetContext = csc;
        }
    }

    final boolean isServer()
    {
        return isServer;
    }
}
