/*
 * @(#)EncapsOutputStream.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;

import org.omg.CORBA.ORB;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.iiop.CDROutputStream;
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
public class EncapsOutputStream extends CDROutputStream
{
    // corba/ORB
    // corba/ORBSingleton
    // iiop/ORB
    // iiop/GIOPImpl
    // corba/AnyImpl
    public EncapsOutputStream(ORB orb) {
        // GIOP version 1.2 with no fragmentation, big endian,
        // UTF8 for char data and UTF-16 for wide char data;
        super(orb, GIOPVersion.V1_2, false);
    }

    // CDREncapsCodec
    //
    // REVISIT.  A UTF-16 encoding with GIOP 1.1 will not work
    // with byte order markers.
    public EncapsOutputStream(ORB orb, GIOPVersion version) {
        super(orb, version, false);
    }    

    // Used by IIOPProfileTemplate
    // 
    public EncapsOutputStream(ORB orb, boolean isLittleEndian) {
        super(orb, GIOPVersion.V1_2, false, isLittleEndian);
    }

    public EncapsOutputStream(ORB orb, int size) {
        super(orb, GIOPVersion.V1_2, size, false);
    }

    public org.omg.CORBA.portable.InputStream create_input_stream() {
        freeInternalCaches();

        return new EncapsInputStream(orb(),
                                     getByteBuffer(),
                                     getSize(),
                                     isLittleEndian());
    }
    
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
    }

    protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
        if (getGIOPVersion().equals(GIOPVersion.V1_0))
            throw new MARSHAL(MinorCodes.WCHAR_DATA_IN_GIOP_1_0,
                              CompletionStatus.COMPLETED_MAYBE);            

        // In the case of GIOP 1.1, we take the byte order of the stream and don't
        // use byte order markers since we're limited to a 2 byte fixed width encoding.
        if (getGIOPVersion().equals(GIOPVersion.V1_1))
            return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16,
                                                            isLittleEndian(),
                                                            false);

        // Assume anything else meets GIOP 1.2 requirements
        //
        // Use byte order markers?  If not, use big endian in GIOP 1.2.  
        // (formal 00-11-03 15.3.16)
        com.sun.corba.se.internal.corba.ORB ourOrb
            = (com.sun.corba.se.internal.corba.ORB)orb();

        boolean useBOM = ourOrb.useByteOrderMarkersInEncapsulations();

        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, 
                                                        false, 
                                                        useBOM);
    }
}
