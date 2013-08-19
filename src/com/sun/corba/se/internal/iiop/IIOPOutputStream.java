/*
 * @(#)IIOPOutputStream.java	1.69 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.io.IOException;
import java.util.Iterator;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.NO_RESOURCES;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.*;
import com.sun.corba.se.internal.util.Utility; //d11638
import com.sun.corba.se.internal.orbutil.MinorCodes; //d11638
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.iiop.messages.Message;
import com.sun.corba.se.internal.iiop.messages.MessageBase;

import com.sun.corba.se.internal.core.CodeSetConversion;
import com.sun.corba.se.internal.core.OSFCodeSetRegistry;
import com.sun.corba.se.internal.core.CodeSetComponentInfo;

import com.sun.corba.se.internal.orbutil.ORBUtility;

public class IIOPOutputStream extends CDROutputStream
{
    protected Connection conn;
    protected Message msg;

    /* 
    Extraneous constructor
    Marked for removing
    public IIOPOutputStream(ORB orb, Connection c) {
        this(orb,
             orb.getGIOPVersion(),
             c);
    }
    */

    public IIOPOutputStream(GIOPVersion version, ORB orb, Connection c)
    {
        super(orb, version);

        getBufferManager().setIIOPOutputStream(this);

        this.conn = c;
    }

    // this is called during locate msg creation to force no fragmentation
    // for LocateRequest & LocateReply msgs for GIOP version 1.0 & 1.1.
    public IIOPOutputStream(GIOPVersion version, ORB orb, Connection c,
            boolean allowFragmentation) {
        super(orb, version, allowFragmentation);

        getBufferManager().setIIOPOutputStream(this);

        this.conn = c;
    }

    public IIOPOutputStream(IIOPInputStream s) {
        this(s.getGIOPVersion(),
             (com.sun.corba.se.internal.iiop.ORB)s.orb(), 
             s.getConnection());
    }

    public final Connection getConnection() {
	return conn;
    }

    // once request stream is created, its connection should not be altered.
    /*
    public final void setConnection(Connection c) {
	this.conn = c;
    }
    */

    public final Message getMessage() {
	return msg;
    }

    public final void setMessage(Message msg) {
	this.msg = msg;
    }

    // Removes the connection from the cache
    private void deleteCurrentConnection()
    {
        GIOPImpl clientGIOP = (GIOPImpl)(((com.sun.corba.se.internal.core.ORB)orb()).getClientGIOP());
        if (clientGIOP != null)
            clientGIOP.deleteConnection(((IIOPConnection)conn).getEndpoint());
    }

    public IIOPInputStream invoke(boolean isOneway)
    {
        ClientResponse response = null;
        SystemException systemEx = null;

        try {

            // Sends either the entire message or the last fragment
            finishSendingMessage();

            // Call getResponse here even if it's a oneway call in order
            // to remove the out call descriptor
            response = (ClientResponse)((IIOPConnection)conn).getResponse(isOneway,
                                                                          MessageBase.getRequestId(getMessage()));

            if (isOneway)
                return null;

            if (response.isSystemException()) {
                systemEx = response.getSystemException();

                if (systemEx == null)
                    throw new INTERNAL("getSystemException returned null in IIOPOutputStream",
                                       MinorCodes.GET_SYSTEM_EX_RETURNED_NULL,
                                       CompletionStatus.COMPLETED_MAYBE);
            } else
                return (IIOPInputStream)response;

        } catch (SystemException ex) {
            systemEx = ex;
        }

        if (systemEx.completed == CompletionStatus.COMPLETED_NO &&
	    systemEx.getClass() == COMM_FAILURE.class ) {
            deleteCurrentConnection();
        }

        // Always return null even if we got a SystemException
        // for oneways. 
        if (isOneway)
            return null;

        // A response from the server may have service contexts
        if (response != null)
            return (IIOPInputStream)response;
        else
            return new ClientResponseImpl(systemEx);
    }

    public final void finishSendingMessage() {
        getBufferManager().sendMessage();
    }

    /**
     * Write the contents of the CDROutputStream to the specified
     * output stream.  Has the side-effect of pushing any current
     * Message onto the Message list.
     * @param s The output stream to write to.
     */
    public void writeTo(java.io.OutputStream s) throws java.io.IOException {

        //
        // Update the GIOP MessageHeader size field.
        //

        ByteBufferWithInfo bbwi = getByteBufferWithInfo();

        msg.setSize(bbwi.buf, bbwi.getSize());

        com.sun.corba.se.internal.corba.ORB ourORB = 
            (com.sun.corba.se.internal.corba.ORB)orb();

        if (ourORB != null && ourORB.giopDebugFlag) {
            ORBUtility.dprint(this, "Sending message:");
            CDROutputStream_1_0.printBuffer(bbwi);
        }

        s.write(bbwi.buf, 0, bbwi.getSize());

        // TimeStamp connection to indicate it has been used
        // Note granularity of connection usage is assumed for
        // now to be that of a IIOP packet.
        conn.stampTime();
    }

    /** overrides create_input_stream from CDROutputStream */
    public org.omg.CORBA.portable.InputStream create_input_stream()
    {
        return new IIOPInputStream((com.sun.corba.se.internal.iiop.ORB)orb(), getByteBuffer(), getIndex(), isLittleEndian(), msg, conn);
    }

    // static methods

   /*
    * This chooses the right buffering strategy for the locate msg.
    * locate msgs 1.0 & 1.1 :=> grow, 1.2 :=> stream
    */
    public static IIOPOutputStream createIIOPOutputStreamForLocateMsg(
            GIOPVersion giopVersion, ORB orb, Connection c) {
        if (giopVersion.lessThan(GIOPVersion.V1_2)) { // no fragmentation
            return new IIOPOutputStream(giopVersion, orb, c, false);
        } else {
            return new IIOPOutputStream(giopVersion, orb, c);
        }
    }
    
    /**
     * Override the default CDR factory behavior to get the
     * negotiated code sets from the connection.
     *
     * These are only called once per message, the first time needed.
     *
     * In the local case, there is no Connection, so use the
     * local code sets.
     */
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        CodeSetComponentInfo.CodeSetContext codesets = getCodeSets();

        // If the connection doesn't have its negotiated
        // code sets by now, fall back on the defaults defined
        // in CDRInputStream.
        if (codesets == null)
            return super.createCharCTBConverter();
        
        OSFCodeSetRegistry.Entry charSet
            = OSFCodeSetRegistry.lookupEntry(codesets.getCharCodeSet());

        if (charSet == null)
            throw new MARSHAL("Unknown char set: " + charSet,
                              MinorCodes.UNKNOWN_CODESET,
                              CompletionStatus.COMPLETED_NO);

        return CodeSetConversion.impl().getCTBConverter(charSet, 
                                                        isLittleEndian(), 
                                                        false);
    }

    protected CodeSetConversion.CTBConverter createWCharCTBConverter() {

        CodeSetComponentInfo.CodeSetContext codesets = getCodeSets();

        // If the connection doesn't have its negotiated
        // code sets by now, we have to throw an exception.
        // See CORBA formal 00-11-03 13.9.2.6.
        if (codesets == null) {
            if (conn.isServer())
                throw new BAD_PARAM(MinorCodes.NO_CLIENT_WCHAR_CODESET_CTX,
                                    CompletionStatus.COMPLETED_MAYBE);
            else
                throw new INV_OBJREF(MinorCodes.NO_SERVER_WCHAR_CODESET_CMP,
                                     CompletionStatus.COMPLETED_NO);
        }

        OSFCodeSetRegistry.Entry wcharSet
            = OSFCodeSetRegistry.lookupEntry(codesets.getWCharCodeSet());

        if (wcharSet == null)
            throw new MARSHAL("Unknown wchar set: " + wcharSet,
                              MinorCodes.UNKNOWN_CODESET,
                              CompletionStatus.COMPLETED_NO);

        boolean useByteOrderMarkers
            = ((com.sun.corba.se.internal.corba.ORB)orb()).useByteOrderMarkers();

        // With UTF-16:
        //
        // For GIOP 1.2, we can put byte order markers if we want to, and
        // use the default of big endian otherwise.  (See issue 3405b)
        //
        // For GIOP 1.1, we don't use BOMs and use the endianness of
        // the stream.
        if (wcharSet == OSFCodeSetRegistry.UTF_16) {
            if (getGIOPVersion().equals(GIOPVersion.V1_2)) {
                return CodeSetConversion.impl().getCTBConverter(wcharSet, 
                                                                false, 
                                                                useByteOrderMarkers);
            }

            if (getGIOPVersion().equals(GIOPVersion.V1_1)) {
                return CodeSetConversion.impl().getCTBConverter(wcharSet,
                                                                isLittleEndian(),
                                                                false);
            }
        }

        // In the normal case, let the converter system handle it
        return CodeSetConversion.impl().getCTBConverter(wcharSet, 
                                                        isLittleEndian(),
                                                        useByteOrderMarkers);
    }

    // If we're local and don't have a Connection, use the
    // local code sets, otherwise get them from the connection.
    // If the connection doesn't have negotiated code sets
    // yet, then we use ISO8859-1 for char/string and wchar/wstring
    // are illegal.
    private CodeSetComponentInfo.CodeSetContext getCodeSets() {
        if (conn == null)
            return CodeSetComponentInfo.LOCAL_CODE_SETS;
        else
            return conn.getCodeSetContext();
    }
}

