/*
 * @(#)EncapsInputStream.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;

import org.omg.CORBA.ORB;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.orbutil.MinorCodes;

/**
 * Encapsulations are supposed to explicitly define their
 * code sets and GIOP version.  The original resolution to issue 2784 
 * said that the defaults were UTF-8 and UTF-16, but that was not
 * agreed upon.
 *
 * These streams currently use CDR 1.2 with ISO8859-1 for char/string and
 * UTF16 for wchar/wstring.  If no byte order marker is available,
 * the endianness of the encapsulation is used.
 *
 * When more encapsulations arise that have their own special code
 * sets defined, we can make all constructors take such parameters.
 */
public class EncapsInputStream extends CDRInputStream
{
    // corba/EncapsOutputStream
    // corba/ORBSingleton
    // iiop/ORB
    public EncapsInputStream(ORB orb, byte[] buf, int size, boolean littleEndian) {
        // CDR 1.2 encoding UTF8/UTF16 (without a BOM) and grow strategy
        super(orb, buf, size, littleEndian, GIOPVersion.V1_2, false);
        performORBVersionSpecificInit();
    }

    // corba/ORBSingleton
    // iiop/ORB
    public EncapsInputStream(ORB orb) {
        super(orb, false, GIOPVersion.V1_2, false);
        performORBVersionSpecificInit();
    }

    // corba/AnyImpl
    public EncapsInputStream(EncapsInputStream eis) {
        super(eis);
        performORBVersionSpecificInit();
    }

    // corba/ORBSingleton
    // iiop/ORB
    public EncapsInputStream(ORB orb, byte[] data, int size) {
        super(orb, data, size, false, GIOPVersion.V1_2, false);
        performORBVersionSpecificInit();
    }

    // CDREncapsCodec
    public EncapsInputStream(ORB orb, byte[] data, int size, GIOPVersion version) {
        super(orb, data, size, false, version, false);
        performORBVersionSpecificInit();
    }

    /**
     * Full constructor with a CodeBase parameter useful for
     * unmarshaling RMI-IIOP valuetypes (technically against the
     * intention of an encapsulation, but necessary due to OMG
     * issue 4795.  Used by ServiceContexts.
     */
    public EncapsInputStream(ORB orb, 
                             byte[] data, 
                             int size, 
                             GIOPVersion version, 
                             CodeBase codeBase) {
        super(orb, data, size, false, version, false);

        this.codeBase = codeBase;

        performORBVersionSpecificInit();
    }

    public CDRInputStream dup() {
        return new EncapsInputStream(this);
    }

    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1);
    }

    protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
        // Wide characters don't exist in GIOP 1.0
        if (getGIOPVersion().equals(GIOPVersion.V1_0))
            throw new MARSHAL(MinorCodes.WCHAR_DATA_IN_GIOP_1_0,
                              CompletionStatus.COMPLETED_MAYBE);

        // In GIOP 1.1, we shouldn't have byte order markers.  Take the order
        // of the stream if we don't see them.
        if (getGIOPVersion().equals(GIOPVersion.V1_1))
            return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16,
                                                            isLittleEndian());

        // Assume anything else adheres to GIOP 1.2 requirements.
        //
        // Our UTF_16 converter will work with byte order markers, and if
        // they aren't present, it will use the provided endianness.
        //
        // With no byte order marker, it's big endian in GIOP 1.2.  
        // formal 00-11-03 15.3.16.
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16,
                                                        false);
    }

    public CodeBase getCodeBase() {
        return codeBase;
    }

    private CodeBase codeBase;
}
