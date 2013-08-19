/*
 * @(#)MessageBase.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import java.io.IOException;

import org.omg.CORBA.Principal;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;

import org.omg.IOP.TaggedProfile;
import com.sun.corba.se.internal.iiop.ORB;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.ior.ObjectKeyFactory;
import com.sun.corba.se.internal.ior.IIOPProfile;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.iiop.AddressingDispositionException;

/**
 * This class acts as the base class for the various GIOP message types. This
 * also serves as a factory to create various message types. We currently
 * support GIOP 1.0, 1.1 and 1.2 message types.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public abstract class MessageBase implements Message {

    // This is only used when the giopDebug flag is
    // turned on.
    public byte[] giopHeader;

    // Static methods

    public static Message createFromStream(ORB orb, java.io.InputStream is)
        	throws IOException {
        Message msg = null;
        boolean debug = orb.giopDebugFlag ;

        // Allocate a buffer just large enough for the GIOP header
        byte buf[] = new byte[GIOPMessageHeaderLength];

        readFully(is, buf, 0, GIOPMessageHeaderLength);

        if (debug) {
            ORBUtility.dprint("Message", "createFromStream: type is " + buf[7]);
        }

        // Sanity checks

        /*
         * check for magic corruption
         * check for version incompatibility
         * check if fragmentation is allowed based on mesg type.
            . 1.0 fragmentation disallowed; FragmentMessage is non-existent.
            . 1.1 only {Request, Reply} msgs maybe fragmented.
            . 1.2 only {Request, Reply, LocateRequest, LocateReply} msgs
              maybe fragmented.
        */

        int b1, b2, b3, b4;
        b1 = (buf[0] << 24) & 0xFF000000;
        b2 = (buf[1] << 16) & 0x00FF0000;
        b3 = (buf[2] << 8)  & 0x0000FF00;
        b4 = (buf[3] << 0)  & 0x000000FF;
        int magic = (b1 | b2 | b3 | b4);

        if (magic != GIOPBigMagic) {
            // If Magic is incorrect, it is an error.
            // ACTION : send MessageError and close the connection.
            throw new INTERNAL(MinorCodes.GIOP_MAGIC_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }

        GIOPVersion orbVersion = orb.getGIOPVersion();

        if (debug) {
            ORBUtility.dprint("MessageBase", "Message GIOP version: "
                              + buf[4] + '.' + buf[5]);
            ORBUtility.dprint("MessageBase", "ORB Max GIOP Version: "
                              + orbVersion);
        }

        if ( (buf[4] > orbVersion.getMajor()) ||
             ( (buf[4] == orbVersion.getMajor()) && (buf[5] > orbVersion.getMinor()) )
            ) {
            // For requests, sending ORB should use the version info
            // published in the IOR or may choose to use a <= version
            // for requests. If the version is greater than published version,
            // it is an error.

            // For replies, the ORB should always receive a version it supports
            // or less, but never greater (except for MessageError)

            // ACTION : Send back a MessageError() with the the highest version
            // the server ORB supports, and close the connection.
            if ( buf[7] != GIOPMessageError ) {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
        }

        AreFragmentsAllowed(buf[4], buf[5], buf[6], buf[7]);

        // create appropriate messages types

        switch (buf[7]) {

        case GIOPRequest:
            if (debug) {
                ORBUtility.dprint( "Message",
                        "createFromStream: creating RequestMessage" ) ;
            }
            //msg = new RequestMessage(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                msg = new RequestMessage_1_0(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new RequestMessage_1_1(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new RequestMessage_1_2(orb);
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        case GIOPLocateRequest:
            //msg = new LocateRequestMessage(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                msg = new LocateRequestMessage_1_0(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new LocateRequestMessage_1_1(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new LocateRequestMessage_1_2(orb);
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        case GIOPCancelRequest:
            //msg = new CancelRequestMessage(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                msg = new CancelRequestMessage_1_0();
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new CancelRequestMessage_1_1();
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new CancelRequestMessage_1_2();
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        case GIOPReply:
            //msg = new ReplyMessage(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                msg = new ReplyMessage_1_0(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new ReplyMessage_1_1(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new ReplyMessage_1_2(orb);
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        case GIOPLocateReply:
            //msg = new LocateReplyMessage(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                msg = new LocateReplyMessage_1_0(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new LocateReplyMessage_1_1(orb);
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new LocateReplyMessage_1_2(orb);
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        case GIOPCloseConnection:
        case GIOPMessageError:
            // REVISIT a MessageError  may contain the highest version server
            // can support. In such a case, a new request may be made with the
            // correct version or the connection be simply closed. Note the
            // connection may have been closed by the server.
            //msg = new Message(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                msg = new Message_1_0();
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new Message_1_1();
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new Message_1_1();
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        case GIOPFragment:
            //msg = new FragmentMessage(debug);
            if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
                // not possible (error checking done already)
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x01) ) { // 1.1
                msg = new FragmentMessage_1_1();
            } else if ( (buf[4] == 0x01) && (buf[5] == 0x02) ) { // 1.2
                msg = new FragmentMessage_1_2();
            } else {
                throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;

        default:
            if (debug)
                ORBUtility.dprint("Message", "createFromStream: fell off end!");
            // unknown message type ?
            // ACTION : send MessageError and close the connection
            throw new INTERNAL(MinorCodes.ILLEGAL_GIOP_MSG_TYPE,
                    CompletionStatus.COMPLETED_MAYBE);
        }

        //
        // Initialize the generic GIOP header instance variables.
        //

        if ( (buf[4] == 0x01) && (buf[5] == 0x00) ) { // 1.0
            Message_1_0 msg10 = (Message_1_0) msg;
            msg10.magic = magic;
            msg10.GIOP_version = new GIOPVersion(buf[4], buf[5]);
            msg10.byte_order = (buf[6] == LITTLE_ENDIAN_BIT);
            msg10.message_type = buf[7];
            msg10.message_size = readSize(buf[8], buf[9], buf[10], buf[11],
                                          msg10.isLittleEndian()) +
                                 GIOPMessageHeaderLength;
        } else { // 1.1 & 1.2
            Message_1_1 msg11 = (Message_1_1) msg;
            msg11.magic = magic;
            msg11.GIOP_version = new GIOPVersion(buf[4], buf[5]);
            msg11.flags = buf[6];
            msg11.message_type = buf[7];
            msg11.message_size = readSize(buf[8], buf[9], buf[10], buf[11],
                                          msg11.isLittleEndian()) +
                                 GIOPMessageHeaderLength;
        }

        if (debug) {
            ORBUtility.dprint( "Message",
                    "createFromStream: message construction complete." ) ;

            // For debugging purposes, save the 12 bytes of the header
            ((MessageBase)msg).giopHeader = buf;
        }

        return msg;
    }

    private static RequestMessage createRequest(
            ORB orb, GIOPVersion gv, int request_id,
            boolean response_expected, byte[] object_key, String operation,
            ServiceContexts service_contexts, Principal requesting_principal) {

        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new RequestMessage_1_0(orb, service_contexts, request_id,
                response_expected, object_key, operation, requesting_principal);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new RequestMessage_1_1(orb, service_contexts, request_id,
                response_expected, new byte[] { 0x00, 0x00, 0x00 },
                object_key, operation, requesting_principal);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            // Note: Currently we use response_expected flag to decide if the
            // call is oneway or not. Ideally, it is possible to expect a
            // response on a oneway call too, but we do not support it now.
            byte response_flags = 0x03;
            if (response_expected) {
                response_flags = 0x03;
            } else {
                response_flags = 0x00;
            }
            /*
            // REVISIT The following is the correct way to do it. This gives
            // more flexibility.
            if ((DII::INV_NO_RESPONSE == false) && response_expected) {
                response_flags = 0x03; // regular two-way
            } else if ((DII::INV_NO_RESPONSE == false) && !response_expected) {
                // this condition is not possible
            } else if ((DII::INV_NO_RESPONSE == true) && response_expected) {
                // oneway, but we need response for LocationForwards or
                // SystemExceptions.
                response_flags = 0x01;
            } else if ((DII::INV_NO_RESPONSE == true) && !response_expected) {
                // oneway, no response required
                response_flags = 0x00;
            }
            */
            TargetAddress target = new TargetAddress();
            target.object_key(object_key);
            return new RequestMessage_1_2(orb, request_id, response_flags,
                new byte[] { 0x00, 0x00, 0x00 }, target,
                operation, service_contexts);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }

    }

    public static RequestMessage createRequest(
            ORB orb, GIOPVersion gv, int request_id, boolean response_expected, 
            IOR ior, short addrDisp, String operation,
            ServiceContexts service_contexts, Principal requesting_principal) {
            
        if (addrDisp == KeyAddr.value) {  
            // object key will be used for target addressing
            IIOPProfile profile = ior.getProfile();
    	    ObjectKey objKey = profile.getObjectKey();
	    byte[] object_key = objKey.getBytes(orb);            
            return createRequest(orb, gv, request_id, response_expected, 
                                 object_key, operation, service_contexts,
                                 requesting_principal);            
        }
        
        if (!(gv.equals(GIOPVersion.V1_2))) {        
            // only object_key based target addressing is allowed for 
            // GIOP 1.0 & 1.1
            throw new INTERNAL(
                    "GIOP version error",
                    MinorCodes.GIOP_VERSION_ERROR,
                    CompletionStatus.COMPLETED_NO);
        }
    
        // Note: Currently we use response_expected flag to decide if the
        // call is oneway or not. Ideally, it is possible to expect a
        // response on a oneway call too, but we do not support it now.
        byte response_flags = 0x03;
        if (response_expected) {
            response_flags = 0x03;
        } else {
            response_flags = 0x00;
        }
            
        TargetAddress target = new TargetAddress();            
        if (addrDisp == ProfileAddr.value) { // iop profile will be used
            IIOPProfile profile = ior.getProfile();
            target.profile(profile.getIOPProfile(orb));
        } else if (addrDisp == ReferenceAddr.value) {  // ior will be used
            IORAddressingInfo iorInfo = 
                new IORAddressingInfo(
                    0, // profile index
                    ior.getIOPIOR(orb));
            target.ior(iorInfo);  
        } else { 
            // invalid target addressing disposition value
            throw new INTERNAL("Illegal target address disposition",
                               MinorCodes.ILLEGAL_TARGET_ADDRESS_DISPOSITION,
                               CompletionStatus.COMPLETED_NO);
        }
        
        return new RequestMessage_1_2(orb, request_id, response_flags,
            new byte[] { 0x00, 0x00, 0x00 }, target,
            operation, service_contexts);        
    }
                    
    public static ReplyMessage createReply(
            ORB orb, GIOPVersion gv, int request_id,
            int reply_status, ServiceContexts service_contexts, IOR ior) {

        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new ReplyMessage_1_0(orb, service_contexts, request_id,
                                        reply_status, ior);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new ReplyMessage_1_1(orb, service_contexts, request_id,
                                        reply_status, ior);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            return new ReplyMessage_1_2(orb, request_id, reply_status,
                                        service_contexts, ior);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public static LocateRequestMessage createLocateRequest(
            ORB orb, GIOPVersion gv,
            int request_id, byte[] object_key) {

        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new LocateRequestMessage_1_0(orb, request_id, object_key);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new LocateRequestMessage_1_1(orb, request_id, object_key);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            TargetAddress target = new TargetAddress();
            target.object_key(object_key);
            return new LocateRequestMessage_1_2(orb, request_id, target);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public static LocateReplyMessage createLocateReply(ORB orb, GIOPVersion gv,
            int request_id, int locate_status, IOR ior) {

        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new LocateReplyMessage_1_0(orb, request_id,
                                              locate_status, ior);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new LocateReplyMessage_1_1(orb, request_id,
                                              locate_status, ior);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            return new LocateReplyMessage_1_2(orb, request_id,
                                              locate_status, ior);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public static CancelRequestMessage createCancelRequest(
            GIOPVersion gv, int request_id) {

        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new CancelRequestMessage_1_0(request_id);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new CancelRequestMessage_1_1(request_id);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            return new CancelRequestMessage_1_2(request_id);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public static Message createCloseConnection(GIOPVersion gv) {
        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new Message_1_0(Message.GIOPBigMagic, false,
                                   Message.GIOPCloseConnection, 0);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_1,
                                   FLAG_NO_FRAG_BIG_ENDIAN,
                                   Message.GIOPCloseConnection, 0);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_2,
                                   FLAG_NO_FRAG_BIG_ENDIAN,
                                   Message.GIOPCloseConnection, 0);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public static Message createMessageError(GIOPVersion gv) {
        if (gv.equals(GIOPVersion.V1_0)) { // 1.0
            return new Message_1_0(Message.GIOPBigMagic, false,
                                   Message.GIOPMessageError, 0);
        } else if (gv.equals(GIOPVersion.V1_1)) { // 1.1
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_1,
                                   FLAG_NO_FRAG_BIG_ENDIAN,
                                   Message.GIOPMessageError, 0);
        } else if (gv.equals(GIOPVersion.V1_2)) { // 1.2
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_2,
                                   FLAG_NO_FRAG_BIG_ENDIAN,
                                   Message.GIOPMessageError, 0);
        } else {
            throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public static FragmentMessage createFragmentMessage(GIOPVersion gv) {
        // This method is not currently used.
        // New fragment messages are always created from existing messages.
        // Creating a FragmentMessage from InputStream is done in
        // createFromStream(..)
        return null;
    }

    public static int getRequestId(Message msg) {
        switch (msg.getType()) {
        case GIOPRequest :
            return ((RequestMessage) msg).getRequestId();
        case GIOPReply :
            return ((ReplyMessage) msg).getRequestId();
        case GIOPLocateRequest :
            return ((LocateRequestMessage) msg).getRequestId();
        case GIOPLocateReply :
            return ((LocateReplyMessage) msg).getRequestId();
        case GIOPCancelRequest :
            return ((CancelRequestMessage) msg).getRequestId();
        case GIOPFragment :
            return ((FragmentMessage) msg).getRequestId();
        }

        throw new INTERNAL(MinorCodes.ILLEGAL_GIOP_MSG_TYPE,
                            CompletionStatus.COMPLETED_MAYBE);
    }

    public static void readFully(java.io.InputStream is, byte[] buf,
            int offset, int size) throws IOException {
        int n = 0;
        while (n < size) {
            int bytecount=0;
            int itns=0;

            // The while loop here is to workaround a possible bug
            // in the Solaris JVM where an "interrupted system call"
            // error is thrown as a SocketException instead of being
            // retried inside SocketInputStream.read().
            while ( true ) {
                try {
                    bytecount = is.read(buf, offset + n, size - n);
                break;
                } catch ( java.io.IOException ex ) {
                if ( itns++ >= 5 )
                throw ex;
                }
            }

            if (bytecount < 0)
            throw new IOException();
            n += bytecount;
        }
    }

    /**
     * Set a flag in the given buffer (fragment bit, byte order bit, etc)
     */
    public static void setFlag(byte[] buf, int flag) {
        buf[6] |= flag;
    }

    /**
     * Clears a flag in the given buffer
     */
    public static void clearFlag(byte[] buf, int flag) {
        buf[6] &= (0xFF ^ flag);
    }

    private static void AreFragmentsAllowed(byte major, byte minor, byte flag,
            byte msgType) {

        if ( (major == 0x01) && (minor == 0x00) ) { // 1.0
            if (msgType == GIOPFragment) {
                throw new INTERNAL(MinorCodes.FRAGMENTATION_DISALLOWED,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
        }

        if ( (flag & MORE_FRAGMENTS_BIT) == MORE_FRAGMENTS_BIT ) {
            switch (msgType) {
            case GIOPCancelRequest :
            case GIOPCloseConnection :
            case GIOPMessageError :
                throw new INTERNAL(MinorCodes.FRAGMENTATION_DISALLOWED,
                                    CompletionStatus.COMPLETED_MAYBE);
            case GIOPLocateRequest :
            case GIOPLocateReply :
                if ( (major == 0x01) && (minor == 0x01) ) { // 1.1
                    throw new INTERNAL(MinorCodes.FRAGMENTATION_DISALLOWED,
                                        CompletionStatus.COMPLETED_MAYBE);
                }
                break;
            }
        }

        return;
    }

    /**
     * Construct an ObjectKey from a byte[].
     *
     * @return ObjectKey the object key.
     */
    static ObjectKey extractObjectKey(byte[] objKey, ORB orb) {

        try {
            if (objKey != null) {
                ObjectKey objectKey = ObjectKeyFactory.get().
                                    create(orb, objKey);
                if (objectKey != null) {
                    return objectKey;
                }
            }
        } catch (Exception e) {}

	// This exception is thrown if any exceptions are raised while
	// extracting the object key or if the object key is empty.
        throw new MARSHAL(MinorCodes.INVALID_OBJECT_KEY, 
			  CompletionStatus.COMPLETED_NO);
    }

    /**
     * Extract the object key from TargetAddress.
     *
     * @return ObjectKey the object key.
     */
    static ObjectKey extractObjectKey(TargetAddress target, ORB orb) {
    
        short orbTargetAddrPref = orb.getGIOPTargetAddressPreference();
        short reqAddrDisp = target.discriminator();
            
        switch (orbTargetAddrPref) {
        case ORBConstants.ADDR_DISP_OBJKEY :
            if (reqAddrDisp != KeyAddr.value) {
                throw new AddressingDispositionException(KeyAddr.value);
            }
            break;
        case ORBConstants.ADDR_DISP_PROFILE :
            if (reqAddrDisp != ProfileAddr.value) {
                throw new AddressingDispositionException(ProfileAddr.value);
            }
            break;
        case ORBConstants.ADDR_DISP_IOR :
            if (reqAddrDisp != ReferenceAddr.value) {
                throw new AddressingDispositionException(ReferenceAddr.value);
            }
            break;
        case ORBConstants.ADDR_DISP_HANDLE_ALL :
            break;
        default : 
            throw new INTERNAL(
                "ORB target address preference in extractObjectKey is invalid",
                MinorCodes.ORB_TARGET_ADDR_PREFERENCE_IN_EXTRACT_OBJECTKEY_INVALID,
                CompletionStatus.COMPLETED_NO);
        }    
        
        try {
            switch (reqAddrDisp) {
            case KeyAddr.value :
                byte[] objKey = target.object_key();
                if (objKey != null) { // AddressingDisposition::KeyAddr
                    ObjectKey objectKey = ObjectKeyFactory.get().
                                              create(orb, objKey);
                    if (objectKey != null) {
                       return objectKey;
                   }
                }
                break;
            case ProfileAddr.value :
                IIOPProfile iiopProfile = null;
                TaggedProfile profile = target.profile();
                if (profile != null) { // AddressingDisposition::ProfileAddr
                   iiopProfile = new IIOPProfile(orb, profile);
                   ObjectKey objectKey = iiopProfile.getObjectKey();
                   if (objectKey != null) {
                       return objectKey;
                   }
                }
                break;
            case ReferenceAddr.value :
                IORAddressingInfo iorInfo = target.ior();
                if (iorInfo != null) { // AddressingDisposition::IORAddr
                    profile = iorInfo.ior.profiles[iorInfo.selected_profile_index];
                    iiopProfile = new IIOPProfile(orb, profile);
                    ObjectKey objectKey = iiopProfile.getObjectKey();
                    if (objectKey != null) {
                       return objectKey;
                   }
                }
                break;
            default : // this cannot happen
                // There is no need for a explicit exception, since the
                // TargetAddressHelper.read() would have raised a BAD_OPERATION
                // exception by now.
                break;
            }
        } catch (Exception e) {}

	// This exception is thrown if any exceptions are raised while
	// extracting the object key from the TargetAddress or if all the
	// the valid TargetAddress::AddressingDispositions are empty.
        throw new MARSHAL(MinorCodes.INVALID_OBJECT_KEY, 
			  CompletionStatus.COMPLETED_NO);
    }

    private static int readSize(byte b1, byte b2, byte b3, byte b4,
            boolean littleEndian) {

        int a1, a2, a3, a4;

        if (!littleEndian) {
            a1 = (b1 << 24) & 0xFF000000;
            a2 = (b2 << 16) & 0x00FF0000;
            a3 = (b3 << 8)  & 0x0000FF00;
            a4 = (b4 << 0)  & 0x000000FF;
        } else {
            a1 = (b4 << 24) & 0xFF000000;
            a2 = (b3 << 16) & 0x00FF0000;
            a3 = (b2 << 8)  & 0x0000FF00;
            a4 = (b1 << 0)  & 0x000000FF;
        }

        return (a1 | a2 | a3 | a4);
    }

    static void nullCheck(Object obj) {
        if (obj == null) {
            throw new org.omg.CORBA.MARSHAL(0,
            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public void callback(com.sun.corba.se.internal.iiop.MessageMediator m)
        throws java.io.IOException
    {
        m.handleInput(this);
    }
}
