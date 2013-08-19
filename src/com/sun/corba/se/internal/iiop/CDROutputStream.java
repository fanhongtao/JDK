/*
 * @(#)CDROutputStream.java	1.24 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.CodeSetConversion;
import com.sun.corba.se.internal.core.OSFCodeSetRegistry;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.MinorCodes;

/**
 * This is delegates to the real implementation.
 */
public abstract class CDROutputStream
    extends org.omg.CORBA_2_3.portable.OutputStream
    implements com.sun.corba.se.internal.core.MarshalOutputStream,
               org.omg.CORBA.DataOutputStream
{
    private CDROutputStreamBase impl;

    // We can move this out somewhere later.  For now, it serves its purpose
    // to create a concrete CDR delegate based on the GIOP version.
    private static class OutputStreamFactory {
        
        public static CDROutputStreamBase newOutputStream(GIOPVersion version)
        {
            switch(version.intValue())
            {
                case GIOPVersion.VERSION_1_0:
                    return new CDROutputStream_1_0();
                case GIOPVersion.VERSION_1_1:
                    return new CDROutputStream_1_1();
                case GIOPVersion.VERSION_1_2:
                    return new CDROutputStream_1_2();
                default:
                    // REVISIT - what is appropriate?  INTERNAL exceptions
                    // are really hard to track later.
                    throw new org.omg.CORBA.INTERNAL();
            }
        }
    }

    // Called by IIOPOutputStream
    public CDROutputStream(org.omg.CORBA.ORB orb,
                           GIOPVersion version) 
    {
        impl = OutputStreamFactory.newOutputStream(version);

        BufferManagerWrite bufMgr
            = BufferManagerFactory.newBufferManagerWrite(version, orb);

        impl.init(orb, bufMgr);
        impl.setParent(this);
    }

    // Required by EncapsOutputStream
    public CDROutputStream(org.omg.CORBA.ORB orb,
                           GIOPVersion version,
                           boolean allowFragmentation,
                           boolean littleEndian) {
        
        int size;
        if (orb != null)
            size = ((com.sun.corba.se.internal.corba.ORB)orb).getGIOPBufferSize();
        else
            size = ORBConstants.GIOP_DEFAULT_BUFFER_SIZE;

        BufferManagerWrite bufMgr;
        if (!allowFragmentation)
            bufMgr = new BufferManagerWriteGrow(size);
        else
            bufMgr = BufferManagerFactory.newBufferManagerWrite(version, orb, size);

        impl = OutputStreamFactory.newOutputStream(version);
        
        impl.init(orb, littleEndian, bufMgr.getInitialBufferSize(), bufMgr);
        impl.setParent(this);
    }

    public CDROutputStream(org.omg.CORBA.ORB orb,
                           GIOPVersion version,
                           int size,
                           boolean allowFragmentation) {

        BufferManagerWrite bufMgr;
        if (!allowFragmentation)
            bufMgr = new BufferManagerWriteGrow(size);
        else
            bufMgr = BufferManagerFactory.newBufferManagerWrite(version, orb, size);

        impl = OutputStreamFactory.newOutputStream(version);
        
        impl.init(orb, bufMgr);
        impl.setParent(this);
    }
        
    // This is used by 
    // ServiceContext for creating encapsulations of correct version encoding.
    // ADDED (Ram J) (05/20/2000)
    public CDROutputStream(org.omg.CORBA.ORB orb, GIOPVersion version,
            boolean allowFragmentation) {

        if (allowFragmentation == false) {
            impl = OutputStreamFactory.newOutputStream(version);

            // A grow strategy is used for encoding encapsulations.
            // streaming behaviour is incorrect.
            BufferManagerWrite bufMgr = null;
            if (orb == null || !(orb instanceof com.sun.corba.se.internal.corba.ORB)) {
                bufMgr = new BufferManagerWriteGrow(ORBConstants.GIOP_DEFAULT_BUFFER_SIZE);
            } else {
                int defaultBufSize = ((com.sun.corba.se.internal.corba.ORB) orb).getGIOPBufferSize();
                bufMgr = new BufferManagerWriteGrow(defaultBufSize);
            }

            impl.init(orb, bufMgr);
            impl.setParent(this);
        } else {
            impl = OutputStreamFactory.newOutputStream(version);

            BufferManagerWrite bufMgr
                = BufferManagerFactory.newBufferManagerWrite(version, orb);

            impl.init(orb, bufMgr);
            impl.setParent(this);
        }
    }

    // org.omg.CORBA.portable.OutputStream

    // Provided by IIOPOutputStream and EncapsOutputStream
    public abstract org.omg.CORBA.portable.InputStream create_input_stream();

    public final void write_boolean(boolean value) {
        impl.write_boolean(value);
    }
    public final void write_char(char value) {
        impl.write_char(value);
    }
    public final void write_wchar(char value) {
        impl.write_wchar(value);
    }
    public final void write_octet(byte value) {
        impl.write_octet(value);
    }
    public final void write_short(short value) {
        impl.write_short(value);
    }
    public final void write_ushort(short value) {
        impl.write_ushort(value);
    }
    public final void write_long(int value) {
        impl.write_long(value);
    }
    public final void write_ulong(int value) {
        impl.write_ulong(value);
    }
    public final void write_longlong(long value) {
        impl.write_longlong(value);
    }
    public final void write_ulonglong(long value) {
        impl.write_ulonglong(value);
    }
    public final void write_float(float value) {
        impl.write_float(value);
    }
    public final void write_double(double value) {
        impl.write_double(value);
    }
    public final void write_string(String value) {
        impl.write_string(value);
    }
    public final void write_wstring(String value) {
        impl.write_wstring(value);
    }

    public final void write_boolean_array(boolean[] value, int offset, int length) {
        impl.write_boolean_array(value, offset, length);
    }
    public final void write_char_array(char[] value, int offset, int length) {
        impl.write_char_array(value, offset, length);
    }
    public final void write_wchar_array(char[] value, int offset, int length) {
        impl.write_wchar_array(value, offset, length);
    }
    public final void write_octet_array(byte[] value, int offset, int length) {
        impl.write_octet_array(value, offset, length);
    }
    public final void write_short_array(short[] value, int offset, int length) {
        impl.write_short_array(value, offset, length);
    }
    public final void write_ushort_array(short[] value, int offset, int length) {
        impl.write_ushort_array(value, offset, length);
    }
    public final void write_long_array(int[] value, int offset, int length) {
        impl.write_long_array(value, offset, length);
    }
    public final void write_ulong_array(int[] value, int offset, int length) {
        impl.write_ulong_array(value, offset, length);
    }
    public final void write_longlong_array(long[] value, int offset, int length) {
        impl.write_longlong_array(value, offset, length);
    }
    public final void write_ulonglong_array(long[] value, int offset, int length) {
        impl.write_ulonglong_array(value, offset, length);
    }
    public final void write_float_array(float[] value, int offset, int length) {
        impl.write_float_array(value, offset, length);
    }
    public final void write_double_array(double[] value, int offset, int length) {
        impl.write_double_array(value, offset, length);
    }
    public final void write_Object(org.omg.CORBA.Object value) {
        impl.write_Object(value);
    }
    public final void write_TypeCode(TypeCode value) {
        impl.write_TypeCode(value);
    }
    public final void write_any(Any value) {
        impl.write_any(value);
    }

    public final void write_Principal(Principal value) {
        impl.write_Principal(value);
    }

    public final void write(int b) throws java.io.IOException {
        impl.write(b);
    }
    
    public final void write_fixed(java.math.BigDecimal value) {
        impl.write_fixed(value);
    }

    public final void write_Context(org.omg.CORBA.Context ctx,
			      org.omg.CORBA.ContextList contexts) {
        impl.write_Context(ctx, contexts);
    }

    public final org.omg.CORBA.ORB orb() {
        return impl.orb();
    }

    // org.omg.CORBA_2_3.portable.OutputStream
    public final void write_value(java.io.Serializable value) {
        impl.write_value(value);
    }

    public final void write_value(java.io.Serializable value, java.lang.Class clz) {
        impl.write_value(value, clz);
    }

    public final void write_value(java.io.Serializable value, String repository_id) {
        impl.write_value(value, repository_id);
    }

    public final void write_value(java.io.Serializable value, 
                            org.omg.CORBA.portable.BoxedValueHelper factory) {
        impl.write_value(value, factory);
    }

    public final void write_abstract_interface(java.lang.Object obj) {
        impl.write_abstract_interface(obj);
    }

    // java.io.OutputStream
    public final void write(byte b[]) throws IOException {
        impl.write(b);
    }

    public final void write(byte b[], int off, int len) throws IOException {
        impl.write(b, off, len);
    }

    public final void flush() throws IOException {
        impl.flush();
    }

    public final void close() throws IOException {
        impl.close();
    }

    // com.sun.corba.se.internal.core.MarshalOutputStream
    public final void start_block() {
        impl.start_block();
    }

    public final void end_block() {
        impl.end_block();
    }

    public final void putEndian() {
        impl.putEndian();
    }

    // Overridden by IIOPOutputStream
    public void writeTo(java.io.OutputStream stream) throws IOException {
        impl.writeTo(stream);
    }

    public final byte[] toByteArray() {
        return impl.toByteArray();
    }

    // org.omg.CORBA.DataOutputStream
    public final void write_Abstract (java.lang.Object value) {
        impl.write_Abstract(value);
    }

    public final void write_Value (java.io.Serializable value) {
        impl.write_Value(value);
    }

    public final void write_any_array(org.omg.CORBA.Any[] seq, int offset, int length) {
        impl.write_any_array(seq, offset, length);
    }

    // org.omg.CORBA.portable.ValueBase
    public final String[] _truncatable_ids() {
        return impl._truncatable_ids();
    }

    // Other
    protected final int getSize() {
        return impl.getSize();
    }

    protected final int getIndex() {
        return impl.getIndex();
    }

    protected int getRealIndex(int index) {
        // Used in indirections. Overridden by TypeCodeOutputStream.
        return index;
    }

    protected final void setIndex(int value) {
        impl.setIndex(value);
    }

    protected final byte[] getByteBuffer() {
        return impl.getByteBuffer();
    }

    protected final void setByteBuffer(byte value[]) {
        impl.setByteBuffer(value);
    }

    public final boolean isLittleEndian() {
        return impl.isLittleEndian();
    }

    protected final ByteBufferWithInfo getByteBufferWithInfo() {
        return impl.getByteBufferWithInfo();
    }

    protected final void setByteBufferWithInfo(ByteBufferWithInfo bbwi) {
        impl.setByteBufferWithInfo(bbwi);
    }

    protected final BufferManagerWrite getBufferManager() {
        return impl.getBufferManager();
    }

    public final void write_fixed(java.math.BigDecimal bigDecimal, short digits, short scale) {
        impl.write_fixed(bigDecimal, digits, scale);
    }

    public final void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream s) {
        impl.writeOctetSequenceTo(s);
    }

    public final GIOPVersion getGIOPVersion() {
        return impl.getGIOPVersion();
    }

    public final void writeIndirection(int tag, int posIndirectedTo) {
        impl.writeIndirection(tag, posIndirectedTo);
    }

    // Use Latin-1 for GIOP 1.0 or when code set negotiation was not
    // performed.
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
    }

    // Subclasses must decide what to do here.  It's inconvenient to
    // make the class and this method abstract because of dup().
    protected abstract CodeSetConversion.CTBConverter createWCharCTBConverter();

    protected final void freeInternalCaches() {
        impl.freeInternalCaches();
    }

    void printBuffer() {
        impl.printBuffer();
    }

    public void alignOnBoundary(int octetBoundary) {
        impl.alignOnBoundary(octetBoundary);
    }
}
