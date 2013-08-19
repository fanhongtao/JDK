/*
 * @(#)IIOPInputStream.java	1.60 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.io.IOException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import com.sun.org.omg.SendingContext.CodeBase;

import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;

import com.sun.corba.se.internal.iiop.ByteBufferWithInfo;
import com.sun.corba.se.internal.iiop.messages.Message;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.CodeSetConversion;
import com.sun.corba.se.internal.core.OSFCodeSetRegistry;
import com.sun.corba.se.internal.core.CodeSetComponentInfo;

public class IIOPInputStream extends CDRInputStream
{
    protected Connection conn;
    protected Message msg = null;
    private boolean unmarshaledHeader;

    private void dprint( String msg ) {
	ORBUtility.dprint( this, msg ) ;
    }

    public IIOPInputStream() {
    }

    private IIOPInputStream(IIOPInputStream stream) {
        super(stream);

        this.conn = stream.conn;
        this.msg = stream.msg;
        this.unmarshaledHeader = stream.unmarshaledHeader;
    }

    public CDRInputStream dup() {
        return new IIOPInputStream(this);
    }

    // Used by
    // IIOPOutputStream
    public IIOPInputStream(ORB orb, byte[] msgbuf, int msgsize, boolean endian,
			   Message msg, Connection conn)
    {
        super(orb, msgbuf, msgsize, endian, msg.getGIOPVersion(), false);

        getBufferManager().init(msg);

        unmarshaledHeader = true;

	this.msg = msg;
        this.conn = conn;
    }

    // Used by
    // IIOPConnection
    // ClientResponseImpl
    // ServerRequestImpl
    // ...
    public IIOPInputStream(Connection c, byte[] msgbuf, Message header)
        throws java.io.IOException
    {
        super(c.getORB(),
              msgbuf,
              header.getSize(),
              header.isLittleEndian(),
              header.getGIOPVersion());

        getBufferManager().init(header);
        this.conn = c;

	com.sun.corba.se.internal.corba.ORB theORB = (com.sun.corba.se.internal.corba.ORB)(c.getORB()) ;
	if (theORB.transportDebugFlag)
	    dprint( "Constructing IIOPInputStream object" ) ;
	
        this.msg = header;

        unmarshaledHeader = false;

        setIndex(Message.GIOPMessageHeaderLength);

        setBufferLength(msg.getSize());

	if (theORB.transportDebugFlag)
	    dprint( "Setting the time stamp" ) ;

        c.stampTime();
    }

    /**
     * Unmarshal the extended GIOP header (may be fragmented, so
     * shouldn't be called by the ReaderThread)
     */
    public void unmarshalHeader()
    {
	//if (theORB.transportDebugFlag)
	//    dprint( "Unmarshalling the GIOP message" ) ;
	
        // Unmarshal the extended GIOP message from the buffer.

        if (!unmarshaledHeader) {
            msg.read(this);
            unmarshaledHeader= true;
        }
    }

    public final boolean unmarshaledHeader() {
        return unmarshaledHeader;
    }

    public final Connection getConnection() {
        return conn;
    }

    // once response stream is created, its connection should not be altered.
    /*
    public final void setConnection(Connection c) {
        this.conn = c;
	// to avoid null pointer exception for the local case
	if (c != null) {
            orb(c.getORB());
	}
    }
    */

    public final Message getMessage() {
        return msg;
    }

    /*

      This must not be allowed.  The message should be the same
      regardless of fragmentation.

    public final void setMessage(Message msg) {
        this.msg = msg;
    }
    */

    //
    // We ran out of things to read. We pre-read everything
    // off of the java.io.InputStream in IIOPConnection.createInputStream()
    // so this can't happen normally.
    //
    protected void grow(int align, int n) {
	throw new MARSHAL(MinorCodes.IIOPINPUTSTREAM_GROW,
			  CompletionStatus.COMPLETED_MAYBE);
    }

    public CodeBase getCodeBase() {
        if (conn == null)
            return null;
        else
            return conn.getCodeBase();
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
    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        CodeSetComponentInfo.CodeSetContext codesets = getCodeSets();

        // If the connection doesn't have its negotiated
        // code sets by now, fall back on the defaults defined
        // in CDRInputStream.
        if (codesets == null)
            return super.createCharBTCConverter();
        
        OSFCodeSetRegistry.Entry charSet
            = OSFCodeSetRegistry.lookupEntry(codesets.getCharCodeSet());

        if (charSet == null)
            throw new MARSHAL("Unknown char set: " + charSet,
                              MinorCodes.UNKNOWN_CODESET,
                              CompletionStatus.COMPLETED_NO);

        return CodeSetConversion.impl().getBTCConverter(charSet, isLittleEndian());
    }

    protected CodeSetConversion.BTCConverter createWCharBTCConverter() {

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


        // For GIOP 1.2 and UTF-16, use big endian if there is no byte
        // order marker.  (See issue 3405b)
        //
        // For GIOP 1.1 and UTF-16, use the byte order the stream if
        // there isn't (and there shouldn't be) a byte order marker.
        //
        // GIOP 1.0 doesn't have wchars.  If we're talking to a legacy ORB,
        // we do what our old ORBs did.
        if (wcharSet == OSFCodeSetRegistry.UTF_16) {
            if (getGIOPVersion().equals(GIOPVersion.V1_2))
                return CodeSetConversion.impl().getBTCConverter(wcharSet, false);
        }

        return CodeSetConversion.impl().getBTCConverter(wcharSet, isLittleEndian());
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
