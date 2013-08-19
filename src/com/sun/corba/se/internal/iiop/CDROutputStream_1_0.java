/*
 * @(#)CDROutputStream_1_0.java	1.107 03/01/23
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.VM_CUSTOM;
import org.omg.CORBA.VM_TRUNCATABLE;
import org.omg.CORBA.VM_NONE;

import java.rmi.Remote;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.core.MarshalOutputStream;

import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.corba.TypeCodeImpl;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.TypeCodePackage.BadKind;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;
import com.sun.corba.se.internal.orbutil.CacheTable;
import com.sun.corba.se.internal.orbutil.ORBUtility;

import com.sun.corba.se.internal.orbutil.RepositoryIdStrings;
import com.sun.corba.se.internal.orbutil.RepositoryIdUtility;
import com.sun.corba.se.internal.orbutil.RepositoryIdFactory;

import com.sun.corba.se.internal.core.ORBVersionImpl;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.util.Utility;

import com.sun.corba.se.internal.iiop.ByteBufferWithInfo;

import javax.rmi.CORBA.Util;
import java.math.BigDecimal;
import javax.rmi.CORBA.ValueHandler;

import com.sun.corba.se.internal.core.CodeSetConversion;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

public class CDROutputStream_1_0 extends CDROutputStreamBase
{
    private static final int INDIRECTION_TAG = 0xffffffff;

    protected boolean littleEndian;
    protected BufferManagerWrite bufferManagerWrite;
    ByteBufferWithInfo bbwi;

    protected com.sun.corba.se.internal.corba.ORB orb;
    protected boolean debug = false;
	
    protected int blockSizeIndex = -1;
    protected int blockSizePosition = 0;

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final String kWriteMethod = "write";

    // Codebase cache
    private CacheTable codebaseCache = null;

    // Value cache
    private CacheTable valueCache = null;

    // Repository ID cache
    private CacheTable repositoryIdCache = null;

    // Write end flag
    private int end_flag = 0;

    // Beginning with the resolution to interop issue 4328,
    // only enclosing chunked valuetypes are taken into account
    // when computing the nesting level.  However, we still need
    // the old computation around for interoperability with our
    // older ORBs.
    private int chunkedValueNestingLevel = 0;

    // Nesting custom marshaling - used to tell whether we can skip chunking
    private boolean mustChunk = false;

    // In block marker
    protected boolean inBlock = false;

    // Last end tag position
    private int end_flag_position = 0;
    private int end_flag_index = 0;

    // ValueHandler
    private ValueHandler valueHandler = null;

    // Repository ID handlers
    private RepositoryIdUtility repIdUtil;
    private RepositoryIdStrings repIdStrs;

    // Code set converters (created when first needed)
    private CodeSetConversion.CTBConverter charConverter;
    private CodeSetConversion.CTBConverter wcharConverter;
    
    protected void init(org.omg.CORBA.ORB orb,
                        boolean littleEndian,
                        int size,
                        BufferManagerWrite bufferManager)
    {
        this.orb = (com.sun.corba.se.internal.corba.ORB)orb;

        if (this.orb != null)
            debug = this.orb.transportDebugFlag;

        this.littleEndian = littleEndian;
        this.bufferManagerWrite = bufferManager;
        this.bbwi = bufferManagerWrite.getInitialBuffer(size);

        createRepositoryIdHandlers();
    }

    private final void createRepositoryIdHandlers()
    {
        if (orb != null) {
            // Get the appropriate versions based on the ORB version.  The
            // ORB versioning info is only in the corba ORB.
            repIdUtil 
                = RepositoryIdFactory.getRepIdUtility(orb);
            repIdStrs 
                = RepositoryIdFactory.getRepIdStringsFactory(orb);
        } else {
            // Get the latest versions
            repIdUtil = RepositoryIdFactory.getRepIdUtility();
            repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
        }
    }

    public BufferManagerWrite getBufferManager()
    {
	return bufferManagerWrite;
    }

    public byte[] toByteArray() {
    	byte[] it;

    	it = new byte[bbwi.index];
    	System.arraycopy(bbwi.buf, 0, it, 0, bbwi.index);

    	return it;
    }

    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_0;
    }

    protected void handleSpecialChunkBegin(int requiredSize)
    {
        // No-op for GIOP 1.0
    }

    protected void handleSpecialChunkEnd()
    {
        // No-op for GIOP 1.0
    }

    protected final int computeAlignment(int align) {
        if (align > 1) {
            int incr = bbwi.index & (align - 1);
            if (incr != 0)
                return align - incr;
        }

        return 0;
    }

    protected void alignAndReserve(int align, int n) {

        bbwi.index += computeAlignment(align);

        if (bbwi.index + n  > bbwi.buflen)
            grow(align, n);
    }

    //
    // Default implementation of grow.  Subclassers may override this.
    // Always grow the single buffer. This needs to delegate
    // fragmentation policy for IIOP 1.1.
    //
    protected void grow(int align, int n) 
    {
        bbwi.needed = n;

        bufferManagerWrite.overflow(bbwi);
    }

    public final void putEndian() throws SystemException {
    	write_boolean(littleEndian);
    }

    public final boolean littleEndian() {
    	return littleEndian;
    }

    void freeInternalCaches() {
	if (codebaseCache != null)
	    codebaseCache.done();

	if (valueCache != null)
	    valueCache.done();
		
	if (repositoryIdCache != null)
	    repositoryIdCache.done();
    }

    // No such type in java
    public final void write_longdouble(double x) 
    {
	throw new NO_IMPLEMENT(MinorCodes.SEND_DEFERRED_NOTIMPLEMENTED,
			       CompletionStatus.COMPLETED_MAYBE);
    }

    public void write_octet(byte x) 
    {
        if (bbwi.index + 1 > bbwi.buflen)
            alignAndReserve(1, 1);

    	bbwi.buf[bbwi.index++] = x;
    }

    public final void write_boolean(boolean x)
    {
	write_octet(x? (byte)1:(byte)0);
    }

    public void write_char(char x) 
    {
        CodeSetConversion.CTBConverter converter = getCharConverter();

        converter.convert(x);

        // CORBA formal 99-10-07 15.3.1.6: "In the case of multi-byte encodings
        // of characters, a single instance of the char type may only
        // hold one octet of any multi-byte character encoding."
        if (converter.getNumBytes() > 1)
            throw new DATA_CONVERSION(MinorCodes.INVALID_SINGLE_CHAR_CTB,
                                      CompletionStatus.COMPLETED_MAYBE);

        write_octet(converter.getBytes()[0]);
    }

    // These wchar methods are only used when talking to
    // legacy ORBs, now.
    private final void writeLittleEndianWchar(char x) {
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    }

    private final void writeBigEndianWchar(char x) {
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    }

    private final void writeLittleEndianShort(short x) {
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    }

    private final void writeBigEndianShort(short x) {
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    }

    private final void writeLittleEndianLong(int x) {
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 16) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 24) & 0xFF);
    }

    private final void writeBigEndianLong(int x) {
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 24) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 16) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    }

    private final void writeLittleEndianLongLong(long x) {
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 16) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 24) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 32) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 40) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 48) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 56) & 0xFF);
    }

    private final void writeBigEndianLongLong(long x) {
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 56) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 48) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 40) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 32) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 24) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 16) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)((x >>> 8) & 0xFF);
    	bbwi.buf[bbwi.index++] = (byte)(x & 0xFF);
    }

    public void write_wchar(char x)
    {
        // Don't allow transmission of wchar/wstring data with
        // foreign ORBs since it's against the spec.
        if (ORBUtility.isForeignORB(orb)) {
            throw new MARSHAL(MinorCodes.WCHAR_DATA_IN_GIOP_1_0,
                              CompletionStatus.COMPLETED_MAYBE);
        }

        // If it's one of our legacy ORBs, do what they did:
    	alignAndReserve(2, 2);
	
    	if (littleEndian) {
    	    writeLittleEndianWchar(x);
    	} else {
    	    writeBigEndianWchar(x);
    	}
    }

    public void write_short(short x) 
    {
    	alignAndReserve(2, 2);
    	
    	if (littleEndian) {
    	    writeLittleEndianShort(x);
    	} else {
    	    writeBigEndianShort(x);
    	}
    }

    public final void write_ushort(short x)
    {
	write_short(x);
    }

    public void write_long(int x) 
    {
        alignAndReserve(4, 4);

    	if (littleEndian) {
    	    writeLittleEndianLong(x);
    	} else {
    	    writeBigEndianLong(x);
    	}
    }

    public final void write_ulong(int x)
    {
	write_long(x);
    }

    public void write_longlong(long x) 
    {
    	alignAndReserve(8, 8);

    	if (littleEndian) {
    	    writeLittleEndianLongLong(x);
    	} else {
    	    writeBigEndianLongLong(x);
    	}
    }

    public final void write_ulonglong(long x)
    {
	write_longlong(x);
    }

    public final void write_float(float x) 
    {
	write_long(Float.floatToIntBits(x));
    }

    public final void write_double(double x) 
    {
	write_longlong(Double.doubleToLongBits(x));
    }

    public void write_string(String value)
    {
        if (value == null) {
	    throw new BAD_PARAM(MinorCodes.NULL_PARAM,
                                CompletionStatus.COMPLETED_MAYBE);
        }

        CodeSetConversion.CTBConverter converter = getCharConverter();

        converter.convert(value);

        // A string is encoded as an unsigned CORBA long for the
        // number of bytes to follow (including a terminating null).
        // There is only one octet per character in the string.
    	int len = converter.getNumBytes() + 1;

        handleSpecialChunkBegin(computeAlignment(4) + 4 + len);

        write_long(len);

        internalWriteOctetArray(converter.getBytes(), 0, converter.getNumBytes());

        // Write the null ending
        write_octet((byte)0);

        handleSpecialChunkEnd();
    }

    public void write_wstring(String value)
    {
        if (value == null)
	    throw new BAD_PARAM(MinorCodes.NULL_PARAM,
                                CompletionStatus.COMPLETED_MAYBE);

        // Don't allow transmission of wchar/wstring data with
        // foreign ORBs since it's against the spec.
        if (ORBUtility.isForeignORB(orb)) {
            throw new MARSHAL(MinorCodes.WCHAR_DATA_IN_GIOP_1_0,
                              CompletionStatus.COMPLETED_MAYBE);
        }
            
        // When talking to our legacy ORBs, do what they did:
    	int len = value.length() + 1;

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(4 + (len * 2) + computeAlignment(4));

        write_long(len);

        for (int i = 0; i < len - 1; i++)
            write_wchar(value.charAt(i));

        // Write the null ending
        write_short((short)0);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    // Performs no checks and doesn't tamper with chunking
    void internalWriteOctetArray(byte[] value, int offset, int length)
    {
    	int n = offset;

    	while (n < length+offset) {
    	    int avail;
    	    int bytes;
    	    int wanted;

            if (bbwi.index + 1 > bbwi.buflen)
        	alignAndReserve(1, 1);
    	    avail = bbwi.buf.length - bbwi.index;
    	    wanted = (length + offset) - n;
    	    bytes = (wanted < avail) ? wanted : avail;
    	    System.arraycopy(value, n, bbwi.buf, bbwi.index, bytes);
    	    bbwi.index += bytes;
    	    n += bytes;
    	}
    }
    
    public final void write_octet_array(byte b[], int offset, int length)
    {
        if ( b == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, 
                                CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(length);

        internalWriteOctetArray(b, offset, length);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public void write_Principal(Principal p)
    {
    	write_long(p.name().length);
    	write_octet_array(p.name(), 0, p.name().length);
    }

    public void write_any(Any any) 
    {
        if ( any == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

    	write_TypeCode(any.type());
    	any.write_value(parent);
    }

    public void write_TypeCode(TypeCode tc)
    {
        if ( tc == null ) {

            throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);
	}
        TypeCodeImpl tci;
        if (tc instanceof TypeCodeImpl) {
	    tci = (TypeCodeImpl)tc;
	}
        else {
	    tci = new TypeCodeImpl(orb, tc);
	}

        tci.write_value(parent);
    }
 
    public void write_Object(org.omg.CORBA.Object ref)
    {
        if (ref == null) {
            IOR.NULL.write(parent);
            return;
        }
	
        // IDL to Java formal 01-06-06 1.21.4.2
        if (ref instanceof org.omg.CORBA.LocalObject)
            throw new MARSHAL("write_Object called with LocalObject",
                              MinorCodes.WRITE_LOCAL_OBJECT,
                              CompletionStatus.COMPLETED_MAYBE);
	
        ObjectImpl oi = (ObjectImpl)ref;
	ClientSubcontract rep = null;
        try {
            // If no delegate set:
            // org.omg.CORBA.portable.ObjectImpl throws BAD_OPERATION
            // org.omg.PortableServer.Servant throws BAD_INV_ORDER
            rep = (ClientSubcontract) oi._get_delegate();
        } catch ( BAD_OPERATION ex ) {
            debugPrintThrowable(ex);
        } catch ( BAD_INV_ORDER ex ) {
            debugPrintThrowable(ex);
        }
        if ( rep == null ) {
	    // no delegate: servant was not connected to ORB
			
	    if (ref instanceof javax.rmi.CORBA.Stub) {
                try {
		    ((javax.rmi.CORBA.Stub)ref).connect(orb);
		} catch (java.rmi.RemoteException e) {
                    debugPrintThrowable(e);
		    throw new MARSHAL("Error connecting servant: "
                                      + e.getMessage(),
                                      MinorCodes.CONNECTING_SERVANT,
                                      CompletionStatus.COMPLETED_MAYBE);
		}
	    } else {
		orb.connect(ref);
	    }
	    rep = (ClientSubcontract) oi._get_delegate();
        }
	
        IOR ior = rep.marshal();
	ior.write(parent);
	return;
    }

    // ------------ RMI related methods --------------------------

    public void write_abstract_interface(java.lang.Object obj) {
	boolean corbaObject = false; // Assume value type.
	org.omg.CORBA.Object theObject = null;
	    
	// Is it a CORBA.Object?
	    
	if (obj != null && obj instanceof org.omg.CORBA.Object) {
	        
	    // Yes.
	        
	    theObject = (org.omg.CORBA.Object)obj;
	    corbaObject = true;	        
	}
	    
	// Write our flag...
	    
	write_boolean(corbaObject);
	    
	// Now write out the object...
	    
	if (corbaObject) {
	    write_Object(theObject);
	} else {
	    try {
		write_value((java.io.Serializable)obj);
	    } catch(ClassCastException cce) {
		if (obj instanceof java.io.Serializable)
		    throw cce;
		else
                    ORBUtility.throwNotSerializableForCorba(obj.getClass().getName());
	    }
	}
    }

    public void write_value(Serializable object, Class clz) {

	write_value(object); 
    }

    public void write_value(Serializable object, String repository_id) {

	// Handle null references
	if (object == null) {
	    // Write null tag and return
	    write_long(0);
	    return;
	}

	// Handle shared references
	if ((valueCache != null) && valueCache.containsKey(object)) {
            writeIndirection(INDIRECTION_TAG, valueCache.getVal(object));
	    return;
	} 
		
	Class clazz = object.getClass();
	boolean oldMustChunk = mustChunk;

	// _REVISIT_ should this be done when mustChunk is false?
	if (inBlock)
	    end_block();

	// Handle arrays
	if (clazz.isArray())
	    {
		if (valueHandler == null)
		    valueHandler = ORBUtility.createValueHandler(orb); //d11638

		if (mustChunk) {
		    // Write value_tag
		    int indirection = writeValueTag(true, false, Util.getCodebase(clazz));
				
		    // Write repository ID
		    write_repositoryId(repIdStrs.createSequenceRepID(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    start_block();
		    end_flag--;
                    chunkedValueNestingLevel--;
		    valueHandler.writeValue(parent, object);
		    end_block();
				
		    // Write end tag
		    writeEndTag(true);
		}
		else {
		    // Write value_tag
		    int indirection = writeValueTag(false, false, Util.getCodebase(clazz));
				
		    // Write repository ID
		    write_repositoryId(repIdStrs.createSequenceRepID(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    end_flag--;
		    valueHandler.writeValue(parent, object);
				
		    // Write end tag
		    writeEndTag(false);
		}
			
	    }
	// Handle IDL Value types
	else if (object instanceof org.omg.CORBA.portable.ValueBase)
	    {
		// _REVISIT_ could check to see whether chunking really needed 
		mustChunk = true;
			
		// Write value_tag
		int indirection = writeValueTag(true, false, Util.getCodebase(clazz));
			
		// Get rep id
		String repId = ((ValueBase)object)._truncatable_ids()[0];
			
		// Write rep id
		write_repositoryId(repId);
			
		// Add indirection for object to indirection table
		updateIndirectionTable(indirection, object, object);
			
		// Write Value chunk
		start_block();
		end_flag--;
                chunkedValueNestingLevel--;
		writeIDLValue(object, repId);
		end_block();
			
		// Write end tag
		writeEndTag(true);
	    }
	else if (shouldWriteAsIDLEntity(object))
	    {
		writeIDLEntity((IDLEntity)object);
	    }
	else if (object instanceof java.lang.String)
	    {
		if (mustChunk) {
		    // Write value_tag
		    int indirection = writeValueTag(true, false, null);
				
		    // Write WStringValue's repository ID
		    write_repositoryId(repIdStrs.getWStringValueRepId());
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    start_block();
		    end_flag--;
                    chunkedValueNestingLevel--;
		    write_wstring((java.lang.String)object);
		    end_block();
				
		    // Write end tag
		    writeEndTag(true);
		}
		else {
		    int indirection = writeValueTag(false, false, null);
				
		    // Write WStringValue's repository ID
		    write_repositoryId(repIdStrs.getWStringValueRepId());
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    end_flag--;
		    write_wstring((java.lang.String)object);
				
		    // Write end tag
		    writeEndTag(false);
		}
			
	    }
	else if (object instanceof java.lang.Class)
	    {
		writeClass(repository_id, (Class)object);

	    }
	else // Not a CORBA Value Type
	    {
		if (valueHandler == null)
		    valueHandler = ORBUtility.createValueHandler(orb); //d11638

		Serializable key = object;
		object = valueHandler.writeReplace(key);
		
		if (object == null) {
		    // Write null tag and return
		    write_long(0);
		    return;
		}
		
		if (object != key) {
		    if ((valueCache != null) && valueCache.containsKey(object)) {
                        writeIndirection(INDIRECTION_TAG, valueCache.getVal(object));
			return;
		    }

		    clazz = object.getClass();
		}

		if (mustChunk || valueHandler.isCustomMarshaled(clazz)) {

		    mustChunk = true;
				
		    // Write value_tag
		    int indirection = writeValueTag(true, false, Util.getCodebase(clazz));
				
		    // Write rep. id
		    write_repositoryId(repIdStrs.createForJavaType(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, key);
				
		    // Write Value chunk
		    end_flag--;
                    chunkedValueNestingLevel--;
		    start_block();
		    valueHandler.writeValue(parent, object);
		    end_block();
				
		    // Write end tag
		    writeEndTag(true);
		}
		else {
		    // Write value_tag
		    int indirection = writeValueTag(false, false, Util.getCodebase(clazz));
				
		    // Write rep. id
		    write_repositoryId(repIdStrs.createForJavaType(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, key);
				
		    // Write Value chunk
		    end_flag--;
		    valueHandler.writeValue(parent, object);
				
		    // Write end tag
		    writeEndTag(false);
		}
			
	    }
		
	mustChunk = oldMustChunk;

	// Check to see if we need to start another block for a
	// possible outer value
	if (mustChunk)
	    start_block();

    }

    public void write_value(Serializable object)
    {

	// Handle null references
	if (object == null) {
	    // Write null tag and return
	    write_long(0);
	    return;
	}

	// Handle shared references
	if ((valueCache != null) && valueCache.containsKey(object)) {
            writeIndirection(INDIRECTION_TAG, valueCache.getVal(object));
	    return;
	}
	
	Class clazz = object.getClass();
	boolean oldMustChunk = mustChunk;

	// _REVISIT_ should this be done when mustChunk is false?
	if (inBlock)
	    end_block();

	// Handle arrays
	if (clazz.isArray())
	    {
		if (valueHandler == null)
		    valueHandler = ORBUtility.createValueHandler(orb); //d11638

		if (mustChunk) {
		    // Write value_tag
		    int indirection = writeValueTag(true, false, Util.getCodebase(clazz));
				
		    // Write repository ID
		    write_repositoryId(repIdStrs.createSequenceRepID(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    start_block();
		    end_flag--;
                    chunkedValueNestingLevel--;
		    valueHandler.writeValue(parent, object);
		    end_block();
				
		    // Write end tag
		    writeEndTag(true);
		}
		else {
		    // Write value_tag
		    int indirection = writeValueTag(false, false, Util.getCodebase(clazz));
				
		    // Write repository ID
		    write_repositoryId(repIdStrs.createSequenceRepID(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    end_flag--;
		    valueHandler.writeValue(parent, object);
				
		    // Write end tag
		    writeEndTag(false);
				
		}
			
	    }
	// Handle IDL Value types
	else if (object instanceof org.omg.CORBA.portable.ValueBase)
	    {
		// _REVISIT_ could check to see whether chunking really needed 
		mustChunk = true;
			
		// Write value_tag
		int indirection = writeValueTag(true, false, Util.getCodebase(clazz));
				
		// Get rep id
		String repId = ((ValueBase)object)._truncatable_ids()[0];
			
		// Write rep id
		write_repositoryId(repId);
			
		// Add indirection for object to indirection table
		updateIndirectionTable(indirection, object, object);
			
		// Write Value chunk
		start_block();
		end_flag--;
                chunkedValueNestingLevel--;
			
		writeIDLValue(object, repId);
			
		end_block();
			
		writeEndTag(true);
	    }
	else if (shouldWriteAsIDLEntity(object))
	    {
		writeIDLEntity((IDLEntity)object);
	    }
	else if (object instanceof java.lang.String)
	    {
			
		// Write value_tag
		//   Are we nested ?
		if (mustChunk) { 
		    int indirection = writeValueTag(true, false, null);
				
		    // Write WStringValue's repository ID
		    write_repositoryId(repIdStrs.getWStringValueRepId());
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    start_block();
		    end_flag--;
                    chunkedValueNestingLevel--;
		    write_wstring((java.lang.String)object);
		    end_block();
				
		    // Write end tag
		    writeEndTag(true);
		}
		else {
		    int indirection = writeValueTag(false, false, null);
				
		    // Write WStringValue's repository ID
		    write_repositoryId(repIdStrs.getWStringValueRepId());
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, object);
				
		    // Write Value chunk
		    end_flag--;
		    write_wstring((java.lang.String)object);
				
		    // Write end tag
		    writeEndTag(false);
				
		}
	    }
	else if (object instanceof java.lang.Class)
	    {
                writeClass(repIdStrs.getClassDescValueRepId(), (Class)object);
	    }
	else // Not a CORBA Value Type
	    {
		if (valueHandler == null)
		    valueHandler = ORBUtility.createValueHandler(orb); //d11638

		Serializable key = object;
		object = valueHandler.writeReplace(key);
		
		if (object == null) {
		    // Write null tag and return
		    write_long(0);
		    return;
		}
		
		if (object != key) {
		    if ((valueCache != null) && valueCache.containsKey(object)) {
                        writeIndirection(INDIRECTION_TAG, valueCache.getVal(object));
			return;
		    }

		    clazz = object.getClass();
		}

		if (mustChunk || valueHandler.isCustomMarshaled(clazz)) {

		    mustChunk = true;

		    // Write value_tag
		    int indirection = writeValueTag(true, false, Util.getCodebase(clazz));
				
		    // Write rep. id
		    write_repositoryId(repIdStrs.createForJavaType(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, key);
				
		    // Write Value chunk
		    end_flag--;
                    chunkedValueNestingLevel--;
		    start_block();
				
		    valueHandler.writeValue(parent, object);
				
		    end_block();
				
		    // Write end tag
		    writeEndTag(true);
		}
		else {
		    // Write value_tag
		    int indirection = writeValueTag(false, false, Util.getCodebase(clazz));
				
		    // Write rep. id
		    write_repositoryId(repIdStrs.createForJavaType(clazz));
				
		    // Add indirection for object to indirection table
		    updateIndirectionTable(indirection, object, key);
				
		    // Write Value chunk
		    end_flag--;
				
		    valueHandler.writeValue(parent, object);
				
		    // Write end tag
		    writeEndTag(false);
				
		}
	    }
		
	mustChunk = oldMustChunk;

	// Check to see if we need to start another block for a
	// possible outer value
	if (mustChunk)
	    start_block();
		
    }

    public void write_value(Serializable object, org.omg.CORBA.portable.BoxedValueHelper factory)
    {
        // Handle null references
        if (object == null) {
            // Write null tag and return
            write_long(0);
            return;
        }
        
        // Handle shared references
	if ((valueCache != null) && valueCache.containsKey(object)) {
            writeIndirection(INDIRECTION_TAG, valueCache.getVal(object));
	    return;
	} 

	boolean oldMustChunk = mustChunk;
	boolean isCustom = false;
	if (factory instanceof ValueHelper) {
	    short modifier;
	    try {
		modifier = ((ValueHelper)factory).get_type().type_modifier();
	    } catch(BadKind ex) {  // tk_value_box
		modifier = VM_NONE.value;
	    }  
	    if (object instanceof CustomMarshal &&
	        modifier == VM_CUSTOM.value) {
		isCustom = true;
		mustChunk = true;
	    }
	    if (modifier == VM_TRUNCATABLE.value)
		mustChunk = true;
	}

	if (mustChunk) {
			
	    if (inBlock)
		end_block();
			
	    // Write value_tag
	    int indirection = writeValueTag(true, false, Util.getCodebase(object.getClass()));
			
	    write_repositoryId(factory.get_id());
			
	    // Add indirection for object to indirection table
	    updateIndirectionTable(indirection, object, object);
			
	    // Write Value chunk
	    start_block();
	    end_flag--;
            chunkedValueNestingLevel--;
	    if (isCustom)
		((CustomMarshal)object).marshal(parent);
	    else 
		factory.write_value(parent, object);
	    end_block();
			
	    // Write end tag
	    writeEndTag(true);
	}
	else {
	    // Write value_tag
	    int indirection = writeValueTag(false, false, Util.getCodebase(object.getClass()));
			
	    write_repositoryId(factory.get_id());
			
	    // Add indirection for object to indirection table
	    updateIndirectionTable(indirection, object, object);
			
	    // Write Value chunk
	    end_flag--;
	    // no need to test for custom on the non-chunked path
	    factory.write_value(parent, object);
			
	    // Write end tag
	    writeEndTag(false);
	}

	mustChunk = oldMustChunk;

	// Check to see if we need to start another block for a
	// possible outer value
	if (mustChunk)
	    start_block();

    }
	
    public int get_offset() {
	return bbwi.index;
    }

    public void start_block() {
        if (debug)
            debugPrintMessage("CDROutputStream_1_0 start_block, index" + bbwi.index);

        //Move inBlock=true to after write_long since write_long might
        //trigger grow which will lead to erroneous behavior with a
        //missing blockSizeIndex.
	//inBlock = true;

	// Save space in the buffer for block size
	write_long(0);

        //Has to happen after write_long since write_long could
        //trigger grow which is overridden by supper classes to 
        //depend on inBlock.
        inBlock = true; 

        blockSizePosition = get_offset();

	// Remember where to put the size of the endblock less 4
	blockSizeIndex = bbwi.index;

        if (debug)
            debugPrintMessage("CDROutputStream_1_0 start_block, blockSizeIndex " 
                              + blockSizeIndex);

    }

    // Utility method which will hopefully decrease chunking complexity
    // by allowing us to end_block and update chunk lengths without
    // calling alignAndReserve.  Otherwise, it's possible to get into
    // recursive scenarios which lose the chunking state.
    protected void writeLongWithoutAlign(int x) {
    	if (littleEndian) {
    	    writeLittleEndianLong(x);
    	} else {
    	    writeBigEndianLong(x);
        }
    }

    public void end_block() {
        if (debug)
            debugPrintMessage("CDROutputStream_1_0.java end_block");

	if (!inBlock)
	    return;

        if (debug)
            debugPrintMessage("CDROutputStream_1_0.java end_block, in a block");

	inBlock = false;

	// Test to see if the block was of zero length
	// If so, remove the block instead of ending it
	// (This can happen if the last field written 
	//  in a value was another value)
	if (get_offset() == blockSizePosition) {
            // Need to assert that blockSizeIndex == bbwi.index?  REVISIT

	    bbwi.index = bbwi.index - 4;
	    blockSizeIndex = -1;
            blockSizePosition = -1;
	    return;
	}

	int oldSize = bbwi.index;
	bbwi.index = blockSizeIndex - 4;
        writeLongWithoutAlign(oldSize - blockSizeIndex);
	bbwi.index = oldSize;
	blockSizeIndex = -1;
        blockSizePosition = -1;

        // System.out.println("      post end_block: " + get_offset() + " " + bbwi.index);
    }
    
    public org.omg.CORBA.ORB orb()
    {
        return orb;    
    }

    // ------------ End RMI related methods --------------------------
    
    public final void write_boolean_array(boolean[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(length);

        for (int i = 0; i < length; i++)
            write_boolean(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public final void write_char_array(char[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(length);

        for (int i = 0; i < length; i++)
            write_char(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public void write_wchar_array(char[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(computeAlignment(2) + (length * 2));

        for (int i = 0; i < length; i++)
            write_wchar(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public final void write_short_array(short[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(computeAlignment(2) + (length * 2));

        for (int i = 0; i < length; i++)
            write_short(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public final void write_ushort_array(short[]value, int offset, int length) {
    	write_short_array(value, offset, length);
    }

    public final void write_long_array(int[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(computeAlignment(4) + (length * 4));

        for (int i = 0; i < length; i++)
            write_long(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public final void write_ulong_array(int[]value, int offset, int length) {
    	write_long_array(value, offset, length);
    }

    public final void write_longlong_array(long[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(computeAlignment(8) + (length * 8));

        for (int i = 0; i < length; i++)
            write_longlong(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public final void write_ulonglong_array(long[]value, int offset, int length) {
    	write_longlong_array(value, offset, length);
    }

    public final void write_float_array(float[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(computeAlignment(4) + (length * 4));

        for (int i = 0; i < length; i++)
            write_float(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public final void write_double_array(double[]value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);

        // This will only have an effect if we're already chunking
        handleSpecialChunkBegin(computeAlignment(8) + (length * 8));

        for (int i = 0; i < length; i++)
            write_double(value[offset + i]);

        // This will only have an effect if we're already chunking
        handleSpecialChunkEnd();
    }

    public void write_string_array(String[] value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);
	    
    	for(int i = 0; i < length; i++)
    	    write_string(value[offset + i]);
    }
    
    public void write_wstring_array(String[] value, int offset, int length) {
        if ( value == null )
	    throw new BAD_PARAM(MinorCodes.NULL_PARAM, CompletionStatus.COMPLETED_MAYBE);
	    
    	for(int i = 0; i < length; i++)
    	    write_wstring(value[offset + i]);
    }

    public final void write_any_array(org.omg.CORBA.Any value[], int offset, int length)
    {
    	for(int i = 0; i < length; i++) 
    	    write_any(value[offset + i]);
    }

    //--------------------------------------------------------------------//
    // CDROutputStream state management.
    //

    /**
     * Write the contents of the CDROutputStream to the specified output stream.
     * @param s The output stream to write to.
     */
    public void writeTo(java.io.OutputStream s) throws java.io.IOException {
	s.write(bbwi.buf, 0, bbwi.index);
    }

    public void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream s) {
    	s.write_long(bbwi.index);
    	s.write_octet_array(bbwi.buf, 0, bbwi.index);
    }

    public final int getSize() {
    	return bbwi.index;
    }

    public int getIndex() {
        return bbwi.index;
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void setIndex(int value) {
        bbwi.index = value;
    }

    public ByteBufferWithInfo getByteBufferWithInfo() {
        return bbwi;
    }

    public void setByteBufferWithInfo(ByteBufferWithInfo bbwi) {
        this.bbwi = bbwi;
    }

    public byte[] getByteBuffer() {
        return bbwi.buf;
    }

    public void setByteBuffer(byte[] value) {
        bbwi.buf = value;
    }

    private final void updateIndirectionTable(int indirection, java.lang.Object object,
                                              java.lang.Object key) {
	// int indirection = get_offset();
	if (valueCache == null)
	    valueCache = new CacheTable(true);
	valueCache.put(object, indirection);
	if (key != object)
	    valueCache.put(key, indirection);
    }

    private final void write_repositoryId(String id) {
        // Use an indirection if available
        if (repositoryIdCache != null && repositoryIdCache.containsKey(id)) {
            writeIndirection(INDIRECTION_TAG, repositoryIdCache.getVal(id));
	    return;
        }

        // Write it as a string.  Note that we have already done the
        // special case conversion of non-Latin-1 characters to escaped
        // Latin-1 sequences in RepositoryId.

        // It's not a good idea to cache them now that we can have
        // multiple code sets.
        write_string(id);
    }

    private void write_codebase(String str, int pos) {
        if (codebaseCache != null && codebaseCache.containsKey(str)) {
            writeIndirection(INDIRECTION_TAG, codebaseCache.getVal(str));
        }
        else {
	    write_string(str);
            if (codebaseCache == null)
        	codebaseCache = new CacheTable(true);
            codebaseCache.put(str, pos);
        }
    }

    private final int writeValueTag(boolean chunkIt, boolean repNotWritten, 
				    String codebase) {
	int indirection = 0;

	if (chunkIt && repNotWritten){
	    if (codebase == null) {
		write_long(repIdUtil.getStandardRMIChunkedNoRepStrId());
		indirection = get_offset() - 4;
	    } else {			
		write_long(repIdUtil.getCodeBaseRMIChunkedNoRepStrId());
		indirection = get_offset() - 4;
		write_codebase(codebase, get_offset());
	    }
	} else if (chunkIt && !repNotWritten){
	    if (codebase == null) {
		write_long(repIdUtil.getStandardRMIChunkedId());
		indirection = get_offset() - 4;
	    } else {			
		write_long(repIdUtil.getCodeBaseRMIChunkedId());
		indirection = get_offset() - 4;
		write_codebase(codebase, get_offset());
	    }
	} else {
	    if (codebase == null) {
		write_long(repIdUtil.getStandardRMIUnchunkedId());
		indirection = get_offset() - 4;
	    } else {			
		write_long(repIdUtil.getCodeBaseRMIUnchunkedId());
		indirection = get_offset() - 4;
		write_codebase(codebase, get_offset());
	    }
	}

        return indirection;
    }

    private void writeIDLValue(Serializable object, String repID)
    {
    	if (object instanceof StreamableValue) {
	    ((StreamableValue)object)._write(parent);

	} else if (object instanceof CustomValue) {
	    ((CustomValue)object).marshal(parent);

	} else {
	    BoxedValueHelper helper = Utility.getHelper(object.getClass(), null, repID);
	    boolean isCustom = false;
	    if (helper instanceof ValueHelper && object instanceof CustomMarshal) {
		try {
		    if (((ValueHelper)helper).get_type().type_modifier() == VM_CUSTOM.value)
		        isCustom = true;
	        } catch(BadKind ex) {
                    debugPrintThrowable(ex);
		    throw new org.omg.CORBA.MARSHAL();
		}  
	    }
	    if (isCustom)
		((CustomMarshal)object).marshal(parent);
	    else
		helper.write_value(parent, object);
	}
    }

    // Handles end tag compaction...
    private void writeEndTag(boolean chunked){
		
	if (chunked) {
	    if (get_offset() == end_flag_position) {

                if (bbwi.index == end_flag_index) {

                    // We are exactly at the same position and index as the
                    // end of the last end tag.  Thus, we can back up over it
                    // and compact the tags.
                    bbwi.index -= 4; 

                } else {

                    // Special case in which we're at the beginning of a new
                    // fragment, but the position is the same.  We can't back up,
                    // so we just write the new end tag without compaction.  This
                    // occurs when a value ends and calls start_block to open a
                    // continuation chunk, but it's called at the very end of
                    // a fragment.
                }
            }

            writeNestingLevel();

            // Remember the last index and position.  These are only used when chunking.
            end_flag_index = bbwi.index;
            end_flag_position = get_offset();

            chunkedValueNestingLevel++;
        }

        // Increment the nesting level
	end_flag++;
    }

    /**
     * Handles ORB versioning of the end tag.  Should only
     * be called if chunking.
     *
     * If talking to our older ORBs (Standard Extension,
     * Kestrel, and Ladybird), write the end flag that takes
     * into account all enclosing valuetypes.
     *
     * If talking a newer or foreign ORB, or if the orb
     * instance is null, write the end flag that only takes
     * into account the enclosing chunked valuetypes.
     */
    private void writeNestingLevel() {
        if (orb == null ||
            ORBVersionImpl.FOREIGN.equals(orb.getORBVersion()) ||
            ORBVersionImpl.NEWER.compareTo(orb.getORBVersion()) <= 0) {

            write_long(chunkedValueNestingLevel);

        } else {
            write_long(end_flag);
        }
    }

    private void writeClass(String repository_id, Class clz) {

	if (mustChunk) {
	    // Write value_tag
	    int indirection = writeValueTag(true, false, null);
	    updateIndirectionTable(indirection, clz, clz);
            			
	    // Write repository ID
	    if (repository_id != null)
		write_repositoryId(repository_id);
	    else {
                write_repositoryId(repIdStrs.getClassDescValueRepId());
	    }
			
	    // Write Value chunk
	    start_block();
	    end_flag--;
            chunkedValueNestingLevel--;

            writeClassBody(clz);

	    end_block();
			
	    // Write end tag
	    writeEndTag(true);
	}
	else {
	    // Write value_tag
	    int indirection = writeValueTag(false, false, null);
	    updateIndirectionTable(indirection, clz, clz);
			
	    // Write repository ID
	    if (repository_id != null)
		write_repositoryId(repository_id);
	    else {
                write_repositoryId(repIdStrs.getClassDescValueRepId());
	    }
			
	    // Write Value chunk
	    end_flag--;

            writeClassBody(clz);

	    // Write end tag
	    writeEndTag(false);
	}
    }

    // Pre-Merlin/J2EE 1.3 ORBs wrote the repository ID
    // and codebase strings in the wrong order.  This handles
    // backwards compatibility.
    private void writeClassBody(Class clz) {
        if (orb == null ||
            ORBVersionImpl.FOREIGN.equals(orb.getORBVersion()) ||
            ORBVersionImpl.NEWER.compareTo(orb.getORBVersion()) <= 0) {

	    write_value(Util.getCodebase(clz));
	    write_value(repIdStrs.createForAnyType(clz));
        } else {

	    write_value(repIdStrs.createForAnyType(clz));
	    write_value(Util.getCodebase(clz));
        }
    }

    // Casts and returns an Object as a Serializable
    // This is required for JDK 1.1 only to avoid VerifyErrors when
    // passing arrays as Serializable
    // private java.io.Serializable make_serializable(java.lang.Object object)
    // {
    //	 return (java.io.Serializable)object;
    // }

    private boolean shouldWriteAsIDLEntity(Serializable object)
    {
	return ((object instanceof IDLEntity) && (!(object instanceof ValueBase)) &&
		(!(object instanceof org.omg.CORBA.Object)));
			
    }
	
    private void writeIDLEntity(IDLEntity object) {

	// _REVISIT_ could check to see whether chunking really needed 
	mustChunk = true;

	String repository_id = repIdStrs.createForJavaType(object);
	Class clazz = object.getClass();
	String codebase = Util.getCodebase(clazz); 
		
	// Write value_tag
	int indirection = writeValueTag(true, false, codebase);
	updateIndirectionTable(indirection, object, object);
		
	// Write rep. id
	write_repositoryId(repository_id);
		
	// Write Value chunk
	end_flag--;
        chunkedValueNestingLevel--;
	start_block();

	// Write the IDLEntity using reflection 
	try {
            ClassLoader clazzLoader = (clazz == null ? null : clazz.getClassLoader());
	    final Class helperClass = Utility.loadClassForClass(clazz.getName()+"Helper", codebase,
                                                   clazzLoader, clazz, clazzLoader);
	    final Class argTypes[] = {org.omg.CORBA.portable.OutputStream.class, clazz};
            // getDeclaredMethod requires RuntimePermission accessDeclaredMembers
            // if a different class loader is used (even though the javadoc says otherwise)
            Method writeMethod = null;
            try {
                writeMethod = (Method)AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public java.lang.Object run() throws NoSuchMethodException {
                            return helperClass.getDeclaredMethod(kWriteMethod, argTypes);
                        }
                    }
                );
            } catch (PrivilegedActionException pae) {
                // this gets caught below
                throw (NoSuchMethodException)pae.getException();
            }
	    java.lang.Object args[] = {parent, object};
	    writeMethod.invoke(null, args);
	} catch (ClassNotFoundException cnfe) {
            debugPrintThrowable(cnfe);
	    throw new org.omg.CORBA.MARSHAL(cnfe.getMessage());
	} catch(NoSuchMethodException nsme) {
            debugPrintThrowable(nsme);
	    throw new org.omg.CORBA.MARSHAL(nsme.getMessage());
	} catch(IllegalAccessException iae) {
            debugPrintThrowable(iae);
	    throw new org.omg.CORBA.MARSHAL(iae.getMessage());
	} catch(InvocationTargetException ite) {
            debugPrintThrowable(ite);
	    throw new org.omg.CORBA.MARSHAL(ite.getMessage());
	}
	end_block();
		
	// Write end tag
	writeEndTag(true);
    }
    
    /* DataOutputStream methods */

    public void write_Abstract (java.lang.Object value) {
        write_abstract_interface(value);
    }

    public void write_Value (java.io.Serializable value) {
        write_value(value);
    }

    // This will stay a custom add-on until the java-rtf issue is resolved.
    // Then it should be declared in org.omg.CORBA.portable.OutputStream.
    //
    // Pads the string representation of bigDecimal with zeros to fit the given
    // digits and scale before it gets written to the stream.
    public void write_fixed(java.math.BigDecimal bigDecimal, short digits, short scale) {
        String string = bigDecimal.toString();
        String integerPart;
        String fractionPart;
        StringBuffer stringBuffer;

        // Get rid of the sign
        if (string.charAt(0) == '-' || string.charAt(0) == '+') {
            string = string.substring(1);
        }

        // Determine integer and fraction parts
        int dotIndex = string.indexOf('.');
        if (dotIndex == -1) {
            integerPart = string;
            fractionPart = null;
        } else if (dotIndex == 0 ) {
            integerPart = null;
            fractionPart = string;
        } else {
            integerPart = string.substring(0, dotIndex);
            fractionPart = string.substring(dotIndex + 1);
        }

        // Pad both parts with zeros as necessary
        stringBuffer = new StringBuffer(digits);
        if (fractionPart != null) {
            stringBuffer.append(fractionPart);
        }
        while (stringBuffer.length() < scale) {
            stringBuffer.append('0');
        }
        if (integerPart != null) {
            stringBuffer.insert(0, integerPart);
        }
        while (stringBuffer.length() < digits) {
            stringBuffer.insert(0, '0');
        }

        // This string contains no sign or dot
        this.write_fixed(stringBuffer.toString(), bigDecimal.signum());
    }

    // This method should be remove by the java-rtf issue.
    // Right now the scale and digits information of the type code is lost.
    public void write_fixed(java.math.BigDecimal bigDecimal) {
        // This string might contain sign and/or dot
        this.write_fixed(bigDecimal.toString(), bigDecimal.signum());
    }

    // The string may contain a sign and dot
    public void write_fixed(String string, int signum) {
        int stringLength = string.length();
        // Each octet contains (up to) two decimal digits
        byte doubleDigit = 0;
        char ch;
        byte digit;

        // First calculate the length of the string without optional sign and dot
        int numDigits = 0;
        for (int i=0; i<stringLength; i++) {
            ch = string.charAt(i);
            if (ch == '-' || ch == '+' || ch == '.')
                continue;
            numDigits++;
        }
        for (int i=0; i<stringLength; i++) {
            ch = string.charAt(i);
            if (ch == '-' || ch == '+' || ch == '.')
                continue;
            digit = (byte)Character.digit(ch, 10);
            if (digit == -1) {
                debugPrintMessage("Digit is -1, throwing MARSHAL");
                throw new MARSHAL();
            }
            // If the fixed type has an odd number of decimal digits,
            // then the representation begins with the first (most significant) digit.
            // Otherwise, this first half-octet is all zero, and the first digit
            // is in the second half-octet.
            if (numDigits % 2 == 0) {
                doubleDigit |= digit;
                this.write_octet(doubleDigit);
                doubleDigit = 0;
            } else {
                doubleDigit |= (digit << 4);
            }
            numDigits--;
        }
        // The sign configuration, in the last half-octet of the representation,
        // is 0xD for negative numbers and 0xC for positive and zero values
        if (signum == -1) {
            doubleDigit |= 0xd;
        } else {
            doubleDigit |= 0xc;
        }
        this.write_octet(doubleDigit);
    }

    private final static String _id = "IDL:omg.org/CORBA/DataOutputStream:1.0";
    private final static String[] _ids = { _id };

    public String[] _truncatable_ids() {
        if (_ids == null)
            return null;

        return (String[])_ids.clone();
    }

    /* for debugging */

    public void printBuffer() {
        CDROutputStream_1_0.printBuffer(this.bbwi);
    }

    public static void printBuffer(ByteBufferWithInfo bbwi) {

        System.out.println("+++++++ Output Buffer ++++++++");
        System.out.println();
        System.out.println("Current index: " + bbwi.index);
        System.out.println("Total length : " + bbwi.buflen);
        System.out.println();

        char[] charBuf = new char[16];

        try {

            for (int i = 0; i < bbwi.index; i += 16) {
                
                int j = 0;
                
                // For every 16 bytes, there is one line
                // of output.  First, the hex output of
                // the 16 bytes with each byte separated
                // by a space.
                while (j < 16 && j + i < bbwi.index) {
                    int k = bbwi.buf[i + j];
                    if (k < 0)
                        k = 256 + k;
                    String hex = Integer.toHexString(k);
                    if (hex.length() == 1)
                        hex = "0" + hex;
                    System.out.print(hex + " ");
                    j++;
                }
                
                // Add any extra spaces to align the
                // text column in case we didn't end
                // at 16
                while (j < 16) {
                    System.out.print("   ");
                    j++;
                }
                
                // Now output the ASCII equivalents.  Non-ASCII
                // characters are shown as periods.
                int x = 0;

                while (x < 16 && x + i < bbwi.index) {
                    if (Character.isLetterOrDigit((char)bbwi.buf[i + x]))
                        charBuf[x] = (char)bbwi.buf[i + x];
                    else
                        charBuf[x] = '.';
                    x++;
                }
                System.out.println(new String(charBuf, 0, x));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.out.println("++++++++++++++++++++++++++++++");
    }

    public void writeIndirection(int tag, int posIndirectedTo)
    {
        // Must ensure that there are no chunks between the tag
        // and the actual indirection value.  This isn't talked about
        // in the spec, but seems to cause headaches in our code.
        // At the very least, this method isolates the indirection code
        // that was duplicated so often.

        handleSpecialChunkBegin(computeAlignment(4) + 8);

        // write indirection tag
        write_long(tag);

        // write indirection
        // Use parent.getRealIndex() so that it can be overridden by TypeCodeOutputStreams
/*
        System.out.println("CDROutputStream_1_0 writing indirection pos " + posIndirectedTo +
                           " - real index " + parent.getRealIndex(get_offset()) + " = " +
                           (posIndirectedTo - parent.getRealIndex(get_offset())));
*/
        write_long(posIndirectedTo - parent.getRealIndex(get_offset()));

        handleSpecialChunkEnd();
    }

    protected CodeSetConversion.CTBConverter getCharConverter() {
        if (charConverter == null)
            charConverter = parent.createCharCTBConverter();
        
        return charConverter;
    }

    protected CodeSetConversion.CTBConverter getWCharConverter() {
        if (wcharConverter == null)
            wcharConverter = parent.createWCharCTBConverter();
    
        return wcharConverter;
    }

    protected void debugPrintThrowable(Throwable t) {
        if (debug && t != null)
            t.printStackTrace();
    }

    protected void debugPrintMessage(String msg) {
        if (debug)
            ORBUtility.dprint(this, msg);
    }

    void alignOnBoundary(int octetBoundary) {
        alignAndReserve(octetBoundary, 0);
    }
}
