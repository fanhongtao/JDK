/*
 * @(#)CDRInputStream_1_0.java	1.81 03/01/23
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
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.OptionalDataException;
import java.io.IOException;

import java.util.Stack;

import java.net.URL;
import java.net.MalformedURLException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.rmi.Remote;
import java.rmi.StubNotFoundException;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.MARSHAL;

import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import com.sun.corba.se.internal.corba.ServerDelegate;
import com.sun.corba.se.internal.corba.PrincipalImpl;
import com.sun.corba.se.internal.corba.TypeCodeImpl;


import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.SubcontractRegistry;
import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.ORBVersionImpl;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.core.CodeSetConversion;

import com.sun.corba.se.internal.util.Utility;

import com.sun.corba.se.internal.orbutil.RepositoryIdStrings;
import com.sun.corba.se.internal.orbutil.RepositoryIdInterface;
import com.sun.corba.se.internal.orbutil.RepositoryIdUtility;
import com.sun.corba.se.internal.orbutil.RepositoryIdFactory;

import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.orbutil.CacheTable;
import com.sun.corba.se.internal.orbutil.MinorCodes;

import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;

import com.sun.org.omg.CORBA.portable.ValueHelper;

import com.sun.org.omg.SendingContext.CodeBase;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

public class CDRInputStream_1_0 extends CDRInputStreamBase 
    implements RestorableInputStream
{
    private static final String kReadMethod = "read";
    private static final int maxBlockLength = 0x7fffff00;

    protected BufferManagerRead bufferManagerRead;
    protected ByteBufferWithInfo bbwi;

    // Set to the ORB's transportDebugFlag value.  This value is
    // used if the ORB is null.
    private boolean debug = false;

    protected boolean littleEndian;
    protected com.sun.corba.se.internal.corba.ORB orb;
    protected ValueHandler valueHandler = null;

    // Value cache
    private CacheTable valueCache = null;
    
    // Repository ID cache
    private CacheTable repositoryIdCache = null;

    // codebase cache
    private CacheTable codebaseCache = null;

    // Current Class Stack (repository Ids of current class being read)
    // private Stack currentStack = null;

    // Length of current chunk, or a large positive number if not in a chunk
    protected int blockLength = maxBlockLength;

    // Read end flag (value nesting depth)
    protected int end_flag = 0;

    // Beginning with the resolution to interop issue 4328,
    // only enclosing chunked valuetypes are taken into account
    // when computing the nesting level.  However, we still need
    // the old computation around for interoperability with our
    // older ORBs.
    private int chunkedValueNestingLevel = 0;

    // Flag used to determine whether blocksize was zero
    // private int checkForNullBlock = -1;

    // In block flag
    // private boolean inBlock = false;

    // Indicates whether we are inside a value
    // private boolean outerValueDone = true;

    // Int used by read_value(Serializable) that is set by this class
    // before calling ValueFactory.read_value
    protected int valueIndirection = 0;

    // Int set by readStringOrIndirection to communicate the actual
    // offset of the string length field back to the caller
    protected int stringIndirection = 0;

    // Flag indicating whether we are unmarshalling a chunked value
    protected boolean isChunked = false;

    // Repository ID handlers
    private RepositoryIdUtility repIdUtil;
    private RepositoryIdStrings repIdStrs;

    // Code set converters (created when first needed)
    private CodeSetConversion.BTCConverter charConverter;
    private CodeSetConversion.BTCConverter wcharConverter;
    
    // Template method
    public CDRInputStreamBase dup() {

        CDRInputStreamBase result;

        try {
            result = (CDRInputStreamBase)this.getClass().newInstance();
        } catch (InstantiationException e) {
            debugPrintThrowable(e);
            throw new INTERNAL();
        } catch (IllegalAccessException e) {
            debugPrintThrowable(e);
            throw new INTERNAL();
        }

        result.init(this.orb,
                    this.bbwi.buf,
                    this.bbwi.buflen,
                    this.littleEndian,
                    this.bufferManagerRead);

        ((CDRInputStream_1_0)result).bbwi.index = this.bbwi.index;

        return result;
    }

    /**
     * NOTE:  size passed to init means buffer size
     */

    public void init(org.omg.CORBA.ORB orb, 
                     byte[] data, 
                     int size, 
                     boolean littleEndian,
                     BufferManagerRead bufferManager) 
    {
        this.orb = (com.sun.corba.se.internal.corba.ORB)orb;
        this.littleEndian = littleEndian;
        this.bufferManagerRead = bufferManager;
        this.bbwi = new ByteBufferWithInfo(data, 0);
        this.bbwi.buflen = size;
        this.markAndResetHandler = bufferManagerRead.getMarkAndResetHandler();

        // The ORB seems to be null in some cases due to primitive
        // TypeCodeImpls (which are singletons) not having ORB instances.
        if (orb != null)
            debug = ((com.sun.corba.se.internal.corba.ORB)orb).transportDebugFlag;
    }

    // See description in CDRInputStream
    void performORBVersionSpecificInit() {
        createRepositoryIdHandlers();
    }

    private final void createRepositoryIdHandlers()
    {
        if (orb != null) {
            // Get the appropriate versions based on the ORB version.  The
            // ORB versioning info is only in the corba ORB.
            repIdUtil = RepositoryIdFactory.getRepIdUtility(orb);
            repIdStrs = RepositoryIdFactory.getRepIdStringsFactory(orb);
        } else {
            // Get the latest versions
            repIdUtil = RepositoryIdFactory.getRepIdUtility();
            repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
        }
    }

    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_0;
    }

    protected final int computeAlignment(int align) {
        if (align > 1) {
            int incr = bbwi.index & (align - 1);
            if (incr != 0)
                return align - incr;
        }

        return 0;
    }

    public int getSize()
    {
        return bbwi.index;
    }


    protected void checkBlockLength() {
	// Since chunks can end at arbitrary points (though not within
	// primitive CDR types, arrays of primitives, strings, or wstrings),
	// we must check here for termination of the current chunk.
	// This also takes care of terminating an open chunk when a
	// value tag is encountered.
        if (!isChunked)
            return;

	if (blockLength == get_offset()) {
	    blockLength = maxBlockLength;
	    start_block();

	} else if (blockLength < get_offset()) {
	    // current chunk has overflowed
	    throw new MARSHAL("Chunk overflow at offset " + get_offset(),
			      com.sun.corba.se.internal.orbutil.MinorCodes.CHUNK_OVERFLOW, 
                              CompletionStatus.COMPLETED_NO);
	}
    }

    protected void alignAndCheck(int align, int n) {

        checkBlockLength();

        bbwi.index += computeAlignment(align);

    	if (bbwi.index + n > bbwi.buflen)
            grow(align, n);
    }

    //
    // This can be overridden....
    //
    protected void grow(int align, int n) {
                
        bbwi.needed = n;

        bbwi = bufferManagerRead.underflow(bbwi);
    }

    //
    // Marshal primitives.
    //

    public final void consumeEndian() {
	littleEndian = read_boolean();
    }

    // No such type in java
    public final double read_longdouble() {
	throw new NO_IMPLEMENT(com.sun.corba.se.internal.orbutil.MinorCodes.SEND_DEFERRED_NOTIMPLEMENTED,
			       CompletionStatus.COMPLETED_MAYBE);
    }

    public final boolean read_boolean() {
	return (read_octet() != 0);
    }

    public final char read_char() {
        alignAndCheck(1, 1);

        return getConvertedChars(1, getCharConverter())[0];
    }

    public char read_wchar() {

        // Don't allow transmission of wchar/wstring data with
        // foreign ORBs since it's against the spec.
        if (ORBUtility.isForeignORB((com.sun.corba.se.internal.corba.ORB)orb)) {
            throw new MARSHAL(MinorCodes.WCHAR_DATA_IN_GIOP_1_0,
                              CompletionStatus.COMPLETED_MAYBE);
        }

        // If we're talking to one of our legacy ORBs, do what
        // they did:
        int b1, b2;

	alignAndCheck(2, 2);

	if (littleEndian) {
	    b2 = bbwi.buf[bbwi.index++] & 0x00FF;
	    b1 = bbwi.buf[bbwi.index++] & 0x00FF;
	} else {
	    b1 = bbwi.buf[bbwi.index++] & 0x00FF;
	    b2 = bbwi.buf[bbwi.index++] & 0x00FF;
	}

	return (char)((b1 << 8) + (b2 << 0));
    }

    public final byte read_octet() {

        alignAndCheck(1, 1);

    	return bbwi.buf[bbwi.index++];
    }

    public final short read_short() {
    	int b1, b2;

    	alignAndCheck(2, 2);

    	if (littleEndian) {
    	    b2 = (bbwi.buf[bbwi.index++] << 0) & 0x000000FF;
    	    b1 = (bbwi.buf[bbwi.index++] << 8) & 0x0000FF00;
    	} else {
    	    b1 = (bbwi.buf[bbwi.index++] << 8) & 0x0000FF00;
    	    b2 = (bbwi.buf[bbwi.index++] << 0) & 0x000000FF;
    	}

    	return (short)(b1 | b2);
    }

    public final short read_ushort() {
	return read_short();
    }

    public final int read_long() {
    	int b1, b2, b3, b4;

        alignAndCheck(4, 4);

    	if (littleEndian) {
    	    b4 = bbwi.buf[bbwi.index++] & 0xFF;
    	    b3 = bbwi.buf[bbwi.index++] & 0xFF;
    	    b2 = bbwi.buf[bbwi.index++] & 0xFF;
    	    b1 = bbwi.buf[bbwi.index++] & 0xFF;
    	} else {
    	    b1 = bbwi.buf[bbwi.index++] & 0xFF;
    	    b2 = bbwi.buf[bbwi.index++] & 0xFF;
    	    b3 = bbwi.buf[bbwi.index++] & 0xFF;
    	    b4 = bbwi.buf[bbwi.index++] & 0xFF;
    	}

    	return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    public final int read_ulong() {
	return read_long();
    }

    public final long read_longlong() {
    	long i1, i2;

    	alignAndCheck(8, 8);

    	if (littleEndian) {
    	    i2 = read_long() & 0xFFFFFFFFL;
    	    i1 = (long)read_long() << 32;
    	} else {
    	    i1 = (long)read_long() << 32;
    	    i2 = read_long() & 0xFFFFFFFFL;
    	}

    	return (i1 | i2);
    }

    public final long read_ulonglong() {
	return read_longlong();
    }

    public final float read_float() {
	return Float.intBitsToFloat(read_long());
    }

    public final double read_double() {
	return Double.longBitsToDouble(read_longlong());
    }

    protected final void checkForNegativeLength(int length) {
        if (length < 0)
            throw new MARSHAL("Bad string length: " + length,
                              MinorCodes.NEGATIVE_STRING_LENGTH,
                              CompletionStatus.COMPLETED_MAYBE);
    }

    protected final String readStringOrIndirection(boolean allowIndirection) {

    	int len = read_long();

        //
        // Check for indirection
        //
        if (allowIndirection) {
            if (len == 0xffffffff)
		return null;
	    else
		stringIndirection = get_offset() - 4;
	}

        checkForNegativeLength(len);

        if (orb != null && ORBUtility.isLegacyORB((com.sun.corba.se.internal.corba.ORB)orb))
            return legacyReadString(len);
        else
            return internalReadString(len);
    }

    private final String internalReadString(int len) {
    	// Workaround for ORBs which send string lengths of
    	// zero to mean empty string.
        // IMPORTANT: Do not replace 'new String("")' with "", it may result
        // in a Serialization bug (See serialization.zerolengthstring) and
        // bug id: 4728756 for details 
    	if (len == 0)
    	    return new String("");

        char[] result = getConvertedChars(len - 1, getCharConverter());

        // Skip over the 1 byte null
        read_octet();

        return new String(result, 0, getCharConverter().getNumChars());
    }

    private final String legacyReadString(int len) {

    	//
    	// Workaround for ORBs which send string lengths of
    	// zero to mean empty string.
    	//
        // IMPORTANT: Do not replace 'new String("")' with "", it may result
        // in a Serialization bug (See serialization.zerolengthstring) and
        // bug id: 4728756 for details 
    	if (len == 0)
    	    return new String("");

        len--;
        char[] c = new char[len];

    	int n = 0;
    	while (n < len) {
    	    int avail;
    	    int bytes;
    	    int wanted;

    	    avail = bbwi.buflen - bbwi.index;
            if (avail <= 0) {
                grow(1, 1);
                avail = bbwi.buflen - bbwi.index;
            }
    	    wanted = len - n;
    	    bytes = (wanted < avail) ? wanted : avail;
            for (int i=0; i<bytes; i++) {
        	c[n+i] = (char) (bbwi.buf[bbwi.index+i] & 0xFF);
            }
    	    bbwi.index += bytes;
    	    n += bytes;
    	}

        //
        // Skip past terminating null byte
        //
        if (bbwi.index + 1 > bbwi.buflen)
            alignAndCheck(1, 1);
    	bbwi.index++;

    	return new String(c);
    }

    public final String read_string() {
        return readStringOrIndirection(false);
    }

    public String read_wstring() {
        // Don't allow transmission of wchar/wstring data with
        // foreign ORBs since it's against the spec.
        if (ORBUtility.isForeignORB((com.sun.corba.se.internal.corba.ORB)orb)) {
            throw new MARSHAL(MinorCodes.WCHAR_DATA_IN_GIOP_1_0,
                              CompletionStatus.COMPLETED_MAYBE);
        }

    	int len = read_long();

    	//
    	// Workaround for ORBs which send string lengths of
    	// zero to mean empty string.
    	//
        // IMPORTANT: Do not replace 'new String("")' with "", it may result
        // in a Serialization bug (See serialization.zerolengthstring) and
        // bug id: 4728756 for details 
    	if (len == 0)
    	    return new String("");

        checkForNegativeLength(len);

        len--;
        char[] c = new char[len];

        for (int i = 0; i < len; i++)
            c[i] = read_wchar();

        // skip the two null terminator bytes
        read_wchar();
        // bbwi.index += 2;

        return new String(c);
    }

    public final void read_octet_array(byte[] b, int offset, int length) {
    	if ( b == null )
    	    throw new BAD_PARAM();

        // Must call alignAndCheck at least once to ensure
        // we aren't at the end of a chunk.  Of course, we
        // should only call it if we actually need to read
        // something, otherwise we might end up with an
        // exception at the end of the stream.
        if (length == 0)
            return;

        alignAndCheck(1, 1);

    	int n = offset;
    	while (n < length+offset) {
    	    int avail;
    	    int bytes;
    	    int wanted;

    	    avail = bbwi.buflen - bbwi.index;
            if (avail <= 0) {
                grow(1, 1);
                avail = bbwi.buflen - bbwi.index;
            }
    	    wanted = (length + offset) - n;
    	    bytes = (wanted < avail) ? wanted : avail;
    	    System.arraycopy(bbwi.buf, bbwi.index, b, n, bytes);
    	    bbwi.index += bytes;
    	    n += bytes;
    	}
    }

    public Principal read_Principal() {
    	int len = read_long();
    	byte[] pvalue = new byte[len];
    	read_octet_array(pvalue,0,len);

    	Principal p = new PrincipalImpl();
    	p.name(pvalue);	
    	return p;
    }

    public TypeCode read_TypeCode() {
        TypeCodeImpl tc = new TypeCodeImpl(orb);
        tc.read_value(parent);
	return tc;
    }
  
    public Any read_any() {
        Any any = orb.create_any();
        TypeCodeImpl tc = new TypeCodeImpl(orb);

        // read off the typecode

        // REVISIT We could avoid this try-catch if we could peek the typecode kind
        // off this stream and see if it is a tk_value.
        // Looking at the code we know that for tk_value the Any.read_value() below
        // ignores the tc argument anyway (except for the kind field).
        // But still we would need to make sure that the whole typecode, including
        // encapsulations, is read off.
        try {
            tc.read_value(parent);
        } catch (MARSHAL ex) {
            if (tc.kind().value() != TCKind._tk_value)
                throw ex;
            // We can be sure that the whole typecode encapsulation has been read off.
            //System.out.println("Error reading value tc " + tc + ", falling back on ValueHandler");
            debugPrintThrowable(ex);
        }
        // read off the value of the any
        any.read_value(parent, tc);

        return any;
    }

    public org.omg.CORBA.Object read_Object() { 
        return read_Object(null);
    }

    // ------------ RMI related methods --------------------------

    // IDL to Java ptc-00-01-08 1.21.4.1
    //
    // The clz argument to read_Object can be either a stub
    // Class or the "Class object for the RMI/IDL interface type 
    // that is statically expected."
    public org.omg.CORBA.Object read_Object(Class clz) {
	IOR ior = new IOR(parent) ;

        if (clz == null || ObjectImpl.class.isAssignableFrom(clz)) {
            // We were given null or a stub class
            return CDRInputStream_1_0.internalIORToObject(ior, clz, orb);
        } else {
            // We must have been given the Class object for the RMI/IDL
            // interface
            try {
                Class stubClass 
                    = Utility.loadStubClass(ior.getTypeId(),
                                            ior.getCodebase(),
                                            clz);

                return CDRInputStream_1_0.internalIORToObject(ior, 
                                                              stubClass, 
                                                              orb);
            } catch (ClassNotFoundException cnfe) {
                // Failed to load the stub class.
                throw new MARSHAL("Failed to load stub for " 
                                  + ior.getTypeId()
                                  + " with Class " 
                                  + (clz == null ? "null" : clz.getName()),
                                  MinorCodes.READ_OBJECT_EXCEPTION, 
                                  CompletionStatus.COMPLETED_NO);
            }
        }
    }

    /*
     * This is used as a general utility (e.g., the PortableInterceptor
     * implementation uses it.  NOTE:  The Class passed in must be the
     * Stub Class.
     */
    public static org.omg.CORBA.Object internalIORToObject(
        IOR ior, Class stubClass, com.sun.corba.se.internal.core.ORB orb)
    {
        if (ior.is_nil()) 
            return null;
	    
	if (ior.isLocal()) {
	    // Ok so far. Can we get a valid servant?
	    ServerSubcontract sc = ior.getServerSubcontract() ;

	    if (sc != null && (sc.isServantSupported())) {
		java.lang.Object servant = sc.getServant(ior);
		if (servant != null ) {
			    
		    // Got a valid servant. Is it a Tie?
		    if (servant instanceof Tie) {
			// Yes, so it is a local servant. Load a stub
			// for it using the codebase from the IOR and
			// the delegate from the tie.
			String codebase = ior.getCodebase();
			org.omg.CORBA.Object objref = (org.omg.CORBA.Object)
			    Utility.loadStub((Tie)servant,stubClass,codebase,false);
			    
			// If we managed to load a stub, return it, otherwise we
			// must fail...
			if (objref != null) {
			    return objref;   
			} else {
			    throw new MARSHAL(
				com.sun.corba.se.internal.orbutil.MinorCodes.READ_OBJECT_EXCEPTION, 
				CompletionStatus.COMPLETED_NO);
			}
		    } else if (servant instanceof org.omg.CORBA.Object) {
			if (servant instanceof org.omg.CORBA.portable.InvokeHandler) {
			    // Old ImplBase stubs are CORBA.Objects.
			    // However, they are not stubs.
			    return createDelegate(ior, stubClass, orb);
			} else {
			    // No, so assume IDL style stub...
			    return (org.omg.CORBA.Object) servant;
			}
		    } else
			throw new INTERNAL( MinorCodes.BAD_SERVANT_READ_OBJECT,
			    CompletionStatus.COMPLETED_NO ) ;
		}
            }
    	}

	return createDelegate(ior, stubClass, orb);
    }

    protected static org.omg.CORBA.Object createDelegate(
        IOR ior, Class stubClass, com.sun.corba.se.internal.core.ORB orb)
    {
	SubcontractRegistry registry = orb.getSubcontractRegistry() ;
	ObjectKeyTemplate temp = ior.getProfile().getTemplate().getObjectKeyTemplate() ;
    	ClientSubcontract rep = registry.getClientSubcontract(temp);
    	rep.unmarshal(ior);
    	rep.setOrb(orb);

        // Load stub, set the delegate and return it...
            
        return loadStub(ior,stubClass,(Delegate)rep);

    }

    protected static ObjectImpl loadStub(IOR ior, 
                                         Class stubClass,
                                         Delegate delegate) {	                                    
        
        // Use the stubClass, if we have it...
        
        if (stubClass != null) {
            try {
                return newStub(stubClass,delegate);
            } catch (Throwable e) {
                if (e instanceof ThreadDeath) {
                    throw (ThreadDeath) e;
                }
            }
	} else {
            
            // Try to load from the ior...

            try {         
		String repID = ior.getTypeId();
		
		// If the repID is "", fall thru to returning the default
		// stub, otherwise try to load the class...
				
		if (repID.length() > 0) {
		    String codebase = ior.getCodebase();

                    Class clz = Utility.loadStubClass(repID, codebase, null); //d11638

                    return newStub(clz,delegate);
                }

            } catch (Throwable e) {
                if (e instanceof ThreadDeath) {
                    throw (ThreadDeath) e;
                }
            }

            // Return the "default" stub...
            	    
            ObjectImpl objref = new org.omg.CORBA_2_3.portable.ObjectImpl() {
                public String[] _ids() {
		    String[] typeids = new String[1];
		    typeids[0] = "IDL:omg.org/CORBA/Object:1.0";
		    return typeids;
                }
	    };
        	    
    	    objref._set_delegate(delegate);
    	    return objref;
	}
        
        // We failed...

	throw new MARSHAL(com.sun.corba.se.internal.orbutil.MinorCodes.READ_OBJECT_EXCEPTION, 
			  CompletionStatus.COMPLETED_NO);
    }
 
    protected static ObjectImpl newStub(Class stubClass,Delegate delegate) 
	throws InstantiationException,
	       IllegalAccessException,
	       NoSuchMethodException,
	       InvocationTargetException {
        // What kind of stub do we have?
        
	if (java.rmi.Remote.class.isAssignableFrom(stubClass)) {
	        
	    // RMI. Instantiate, set delegate and return...
	        
            ObjectImpl objref = (ObjectImpl) stubClass.newInstance();
    	    objref._set_delegate(delegate);
    	    return objref;
	}
	    
	// RMI Abstract OR IDL. Instantiate, set delegate and return...
	        
        try {
                    
            // Try creating the stub using the default constructor...

            ObjectImpl result = (ObjectImpl)stubClass.newInstance();
            result._set_delegate(delegate);
            return result;
        } catch (Throwable e) {
            if (e instanceof ThreadDeath) {
                throw (ThreadDeath) e;
            }
        }
        
        // Try creating the stub using the delegate constructor...

        Class[] intArgsClass = new Class[] {org.omg.CORBA.portable.Delegate.class};
        java.lang.Object[]intArgs = new java.lang.Object[] {delegate};
        java.lang.reflect.Constructor intArgsConstructor;
        intArgsConstructor = stubClass.getConstructor(intArgsClass);
        return (ObjectImpl)intArgsConstructor.newInstance(intArgs);
    }

    public java.lang.Object read_abstract_interface() {
        return read_abstract_interface(null);
    }

    public java.lang.Object read_abstract_interface(java.lang.Class clz) {
    	boolean object = read_boolean();
        if (object) {
            return read_Object(clz);
        } else {
            return read_value();
	}
    }

    public Serializable read_value() {

        // Read value tag
        int vType = readValueTag();
		
        if (vType == 0)
            return null; // value is null
        else if (vType == 0xffffffff) { // Indirection tag
            int indirection = read_long() + get_offset() - 4;
            if (valueCache != null && valueCache.containsVal(indirection))
		{
		    java.io.Serializable cachedValue = (java.io.Serializable)valueCache.getKey(indirection);
		    return cachedValue;
		}
            else {
		throw new IndirectionException(indirection);
	    }
	}
        else {
	    int indirection = get_offset() - 4;

	    // end_block();

	    boolean saveIsChunked = isChunked;
	    isChunked = repIdUtil.isChunkedEncoding(vType);

	    java.lang.Object value = null;
			
	    String codebase_URL = null;			
	    if (repIdUtil.isCodeBasePresent(vType)){
		codebase_URL = read_codebase_URL();
	    }
			
            // Read repository id
            String repositoryIDString = null; 
			
	    switch(repIdUtil.getTypeInfo(vType)){
	    case RepositoryIdUtility.NO_TYPE_INFO :
                throw new MARSHAL("read_value() with no repository ID info",
                                  MinorCodes.READ_VALUE_AND_NO_REP_ID,
                                  CompletionStatus.COMPLETED_MAYBE);
	    case RepositoryIdUtility.SINGLE_REP_TYPE_INFO :
		repositoryIDString = read_repositoryId(); 
		break;
	    case RepositoryIdUtility.PARTIAL_LIST_TYPE_INFO :
		repositoryIDString = read_repositoryIds();
		break;
	    }

	    // indirection = get_offset();

	    start_block();
	    end_flag--;
            if (isChunked)
                chunkedValueNestingLevel--;

            if (repositoryIDString.equals(repIdStrs.getWStringValueRepId()))
		{
		    value = read_wstring();
		}       
            else if (repositoryIDString.equals(repIdStrs.getClassDescValueRepId())) {

	            // read the class either with the wrong RepId or the
	            // correct RepId for the classDesc
		    value = readClass();
		}       
            else {

                Class valueClass = getClassFromString(repositoryIDString,
                                                      codebase_URL);

                if ((valueClass != null) && 
		    org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(valueClass)) {
					
		    value = readIDLValue(indirection, repositoryIDString, valueClass, codebase_URL);

		} else {

                    try {
	                // cannot cache this since this value will be different
	                if (valueHandler == null) {

                            valueHandler = ORBUtility.createValueHandler(orb);
	                }

			value = valueHandler.readValue(parent, indirection, valueClass, 
						       repositoryIDString, getCodeBase());
                    } catch(Exception ex) {
                        debugPrintThrowable(ex);

                        throw new org.omg.CORBA.MARSHAL("Unable to read value from underlying bridge : " 
                                                        + ex.getMessage(),
                                                        MinorCodes.VALUEHANDLER_READ_EXCEPTION,
                                                        CompletionStatus.COMPLETED_MAYBE);
                    } catch(Error e) {
                        debugPrintThrowable(e);

                        throw new org.omg.CORBA.MARSHAL("Unable to read value from underlying bridge : " 
                                                        + e.getMessage(),
                                                        MinorCodes.VALUEHANDLER_READ_ERROR,
                                                        CompletionStatus.COMPLETED_MAYBE);
                    }
                }
            }
	    
	    handleEndOfValue();
	    readEndTag();

	    // Put into valueCache
	    if (valueCache == null)
		valueCache = new CacheTable(false);
	    valueCache.put(value, indirection);
	
	    // allow for possible continuation chunk
	    isChunked = saveIsChunked;
	    start_block();

	    return (java.io.Serializable)value;
        }
    }

    public Serializable read_value(Class expectedType) {

        // Read value tag
        int vType = readValueTag();
		
        if (vType == 0)
            return null; // value is null
        else if (vType == 0xffffffff) { // Indirection tag
            int indirection = read_long() + get_offset() - 4;
            if (valueCache != null && valueCache.containsVal(indirection))
		{
		    java.io.Serializable cachedValue = (java.io.Serializable)valueCache.getKey(indirection);
		    return cachedValue;
		}
            else {
		throw new IndirectionException(indirection);
	    }
	}
        else {
	    int indirection = get_offset() - 4;

	    // end_block();

	    boolean saveIsChunked = isChunked;
	    isChunked = repIdUtil.isChunkedEncoding(vType);
			
	    java.lang.Object value = null;
			
	    String codebase_URL = null;			
	    if (repIdUtil.isCodeBasePresent(vType)){
		codebase_URL = read_codebase_URL();
	    }
			
            // Read repository id
            String repositoryIDString = null; 
			
	    switch(repIdUtil.getTypeInfo(vType)){
	    case RepositoryIdUtility.NO_TYPE_INFO :
                // Throw an exception if we have no repository ID info and
                // no expectedType to work with.  Otherwise, how would we
                // know what to unmarshal?
                if (expectedType == null)
                    throw new MARSHAL("Expected type null and no repository ID info",
                                      MinorCodes.EXPECTED_TYPE_NULL_AND_NO_REP_ID,
                                      CompletionStatus.COMPLETED_MAYBE);

		repositoryIDString = repIdStrs.createForAnyType(expectedType);
		break;
	    case RepositoryIdUtility.SINGLE_REP_TYPE_INFO :
		repositoryIDString = read_repositoryId(); 
		break;
	    case RepositoryIdUtility.PARTIAL_LIST_TYPE_INFO :
		repositoryIDString = read_repositoryIds();
		break;
		 			
	    }

	    // indirection = get_offset();

	    start_block();
	    end_flag--;
            if (isChunked)
                chunkedValueNestingLevel--;

            if (repositoryIDString.equals(repIdStrs.getWStringValueRepId()))
		{
		    value = read_wstring();
		}       
            else if (repositoryIDString.equals(repIdStrs.getClassDescValueRepId()))
		{
	            // read in the class whether with the old ClassDesc or the
	            // new one
		    value = readClass();
		}       
            else {
				
                Class valueClass = expectedType;

                // By this point, either the expectedType or repositoryIDString
                // is guaranteed to be non-null.
                if (expectedType == null || 
                    !repositoryIDString.equals(repIdStrs.createForAnyType(expectedType))) {

                    valueClass = getClassFromString(repositoryIDString,
                                                    codebase_URL,
                                                    expectedType);
                }

                if ((valueClass != null) && 
		    org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(valueClass)) {
					
		    value =  readIDLValue(indirection, repositoryIDString, valueClass, codebase_URL);

		} else {

                    try {
	                if (valueHandler == null) {
                            valueHandler = ORBUtility.createValueHandler(orb);
	                }

			value = valueHandler.readValue(parent, indirection, valueClass, 
						       repositoryIDString, getCodeBase());
                    
                    } catch(Exception ex) {
                        debugPrintThrowable(ex);
                        throw new org.omg.CORBA.MARSHAL("Unable to read value from underlying bridge : " 
                                                        + ex.getMessage(),
                                                        MinorCodes.VALUEHANDLER_READ_EXCEPTION,
                                                        CompletionStatus.COMPLETED_MAYBE);
                    } catch(Error e){
                        debugPrintThrowable(e);
                        throw new org.omg.CORBA.MARSHAL("Unable to read value from underlying bridge : " 
                                                        + e.getMessage(),
                                                        MinorCodes.VALUEHANDLER_READ_ERROR,
                                                        CompletionStatus.COMPLETED_MAYBE);
                    }

                }
            }
	    
	    handleEndOfValue();
	    readEndTag();

	    // Put into valueCache
	    if (valueCache == null)
		valueCache = new CacheTable(false);
	    valueCache.put(value, indirection);
	
	    // allow for possible continuation chunk
	    isChunked = saveIsChunked;
	    start_block();

	    return (java.io.Serializable)value;
        }		
    }

    public Serializable read_value(BoxedValueHelper factory) {

        // Read value tag
        int vType = readValueTag();

        if (vType == 0)
            return null; // value is null
        else if (vType == 0xffffffff) { // Indirection tag
            int indirection = read_long() + get_offset() - 4;
            if (valueCache != null && valueCache.containsVal(indirection))
		{
		    java.io.Serializable cachedValue = (java.io.Serializable)valueCache.getKey(indirection);
		    return cachedValue;
		}
            else {
		throw new IndirectionException(indirection);
	    }
	}
        else {
	    int indirection = get_offset() - 4;

	    // end_block();

	    boolean saveIsChunked = isChunked;
	    isChunked = repIdUtil.isChunkedEncoding(vType);

	    java.lang.Object value = null;

	    String codebase_URL = null;			
	    if (repIdUtil.isCodeBasePresent(vType)){
		codebase_URL = read_codebase_URL();
	    }

            // Read repository id
            String repositoryIDString = null; 

	    switch(repIdUtil.getTypeInfo(vType)){
	    case RepositoryIdUtility.NO_TYPE_INFO :
		throw new org.omg.CORBA.MARSHAL("No class description available (value_tag indicates no type information present)");
	    case RepositoryIdUtility.SINGLE_REP_TYPE_INFO :
		repositoryIDString = read_repositoryId(); 
		break;
	    case RepositoryIdUtility.PARTIAL_LIST_TYPE_INFO :
		repositoryIDString = read_repositoryIds(); 
		break;
					
	    }

            // Compare rep. ids to see if we should use passed helper
            if (!repositoryIDString.equals(factory.get_id()))
		factory = Utility.getHelper(null, codebase_URL, repositoryIDString);

	    start_block();
	    end_flag--;
            if (isChunked)
                chunkedValueNestingLevel--;
	    
	    if (factory instanceof ValueHelper) {
		value = readIDLValueWithHelper((ValueHelper)factory, indirection);
	    } else {
		valueIndirection = indirection;  // for callback
		value = factory.read_value(parent);
	    }

	    handleEndOfValue();
	    readEndTag();

	    // Put into valueCache
	    if (valueCache == null)
		valueCache = new CacheTable(false);
	    valueCache.put(value, indirection);
	
	    // allow for possible continuation chunk
	    isChunked = saveIsChunked;
	    start_block();

            return (java.io.Serializable)value;
        }
    }

    private boolean isCustomType(ValueHelper helper) {
	try{
	    TypeCode tc = helper.get_type();
	    int kind = tc.kind().value();
	    if (kind == TCKind._tk_value) {
		return (tc.type_modifier() == org.omg.CORBA.VM_CUSTOM.value);
	    }
	}
	catch(BadKind ex) {
	    throw new org.omg.CORBA.MARSHAL(MinorCodes.BAD_KIND,
                                            CompletionStatus.COMPLETED_MAYBE);
	}

	return false;
    }

    // This method is actually called indirectly by 
    // read_value(String repositoryId).
    // Therefore, it is not a truly independent read call that handles
    // header information itself.
    public java.io.Serializable read_value(java.io.Serializable value) {

	// Put into valueCache using valueIndirection
	if (valueCache == null)
	    valueCache = new CacheTable(false);
	valueCache.put(value, valueIndirection);

	if (value instanceof StreamableValue)
	    ((StreamableValue)value)._read(parent);
	else if (value instanceof CustomValue)
	    ((CustomValue)value).unmarshal(parent);
			
	return value;
    }

    public java.io.Serializable read_value(java.lang.String repositoryId) {

	// if (inBlock)
	//    end_block();

        // Read value tag
        int vType = readValueTag();

        if (vType == 0)
            return null; // value is null
        else if (vType == 0xffffffff) { // Indirection tag
            int indirection = read_long() + get_offset() - 4;
            if (valueCache != null && valueCache.containsVal(indirection))
		{
		    java.io.Serializable cachedValue = (java.io.Serializable)valueCache.getKey(indirection);
		    return cachedValue;
		}
            else {
		throw new IndirectionException(indirection);
	    }
	}
        else {
	    int indirection = get_offset() - 4;

	    // end_block();

	    boolean saveIsChunked = isChunked;
	    isChunked = repIdUtil.isChunkedEncoding(vType);

	    java.lang.Object value = null;

	    String codebase_URL = null;			
	    if (repIdUtil.isCodeBasePresent(vType)){
		codebase_URL = read_codebase_URL();
	    }

            // Read repository id
            String repositoryIDString = null; 

	    switch(repIdUtil.getTypeInfo(vType)){
	    case RepositoryIdUtility.NO_TYPE_INFO :
		repositoryIDString = repositoryId; 
		break;
	    case RepositoryIdUtility.SINGLE_REP_TYPE_INFO :
		repositoryIDString = read_repositoryId(); 
		break;
	    case RepositoryIdUtility.PARTIAL_LIST_TYPE_INFO :
		repositoryIDString = read_repositoryIds(); 
		break;
					
	    }

	    ValueFactory factory = Utility.getFactory(null, codebase_URL, orb, repositoryIDString);

	    start_block();
	    end_flag--;
            if (isChunked)
                chunkedValueNestingLevel--;

	    valueIndirection = indirection;  // for callback
	    value = factory.read_value(parent);

	    handleEndOfValue();
	    readEndTag();

	    // Put into valueCache
	    if (valueCache == null)
		valueCache = new CacheTable(false);
	    valueCache.put(value, indirection);
	
	    // allow for possible continuation chunk
	    isChunked = saveIsChunked;
	    start_block();

            return (java.io.Serializable)value;
        }		
    }

    private Class readClass() {

        String codebases = null, classRepId = null;

        if (orb == null ||
            ORBVersionImpl.FOREIGN.equals(orb.getORBVersion()) ||
            ORBVersionImpl.NEWER.compareTo(orb.getORBVersion()) <= 0) {

            codebases = (String)read_value(java.lang.String.class);
            classRepId = (String)read_value(java.lang.String.class);
        } else {
            // Pre-Merlin/J2EE 1.3 ORBs wrote the repository ID
            // and codebase strings in the wrong order.
            classRepId = (String)read_value(java.lang.String.class);
            codebases = (String)read_value(java.lang.String.class);
        }

        if (debug) {
            debugPrintMessage("readClass codebases: " 
                              + codebases
                              + " rep Id: "
                              + classRepId);
        }

        Class cl = null;

        RepositoryIdInterface repositoryID 
            = repIdStrs.getFromString(classRepId);
        
        try {
            cl = repositoryID.getClassFromType(codebases);
        } catch(ClassNotFoundException cnfe){
            debugPrintThrowable(cnfe);
            throw new org.omg.CORBA.MARSHAL("Unable to load Class "
                                            + repositoryID.getClassName()
                                            + " : " + cnfe.getMessage(),
                                            MinorCodes.CNFE_READ_CLASS,
                                            CompletionStatus.COMPLETED_MAYBE);
        }

        catch(MalformedURLException me){
            debugPrintThrowable(me);
            throw new org.omg.CORBA.MARSHAL("Unable to load Class "
                                            + repositoryID.getClassName()
                                            + " : " + me.getMessage(),
                                            MinorCodes.MALFORMED_URL,
                                            CompletionStatus.COMPLETED_MAYBE);
        }

	return cl;
    }

    private java.lang.Object readIDLValueWithHelper(ValueHelper helper, int indirection) {
	// look for two-argument static read method
	Method readMethod;
	try {
	    Class argTypes[] = {org.omg.CORBA.portable.InputStream.class, helper.get_class()};
	    readMethod = helper.getClass().getDeclaredMethod(kReadMethod, argTypes);
	}
	catch(NoSuchMethodException nsme) { // must be boxed value helper
	    java.lang.Object result = helper.read_value(parent);
	    return result;
	}

	// found two-argument read method, so must be non-boxed value...
	// ...create a blank instance
	java.lang.Object val = null;
	try {
	    val = helper.get_class().newInstance();
	} 
	catch(java.lang.InstantiationException ie){
            debugPrintThrowable(ie);
	    throw new org.omg.CORBA.MARSHAL(ie.getMessage());
	}	
	catch(IllegalAccessException iae){ 
	    // Value's constructor is protected or private
	    //
	    // So, use the helper to read the value.
	    //
	    // NOTE : This means that in this particular case a recursive ref.
	    // would fail.
	    return helper.read_value(parent);
	}

	// add blank instance to cache table
        if (valueCache == null)
            valueCache = new CacheTable(false);
	valueCache.put(val, indirection);

	// if custom type, call unmarshal method
	if (val instanceof CustomMarshal && isCustomType(helper)) {
            ((CustomMarshal)val).unmarshal(parent);
	    return val;
	}

	// call two-argument read method using reflection
	try {
	    java.lang.Object args[] = {parent, val};
	    readMethod.invoke(helper, args);
            return val;
	}
	catch(IllegalAccessException iae2){
            debugPrintThrowable(iae2);
	    throw new org.omg.CORBA.MARSHAL(iae2.getMessage());
	}
	catch(InvocationTargetException ite){
            debugPrintThrowable(ite);
	    throw new org.omg.CORBA.MARSHAL(ite.getMessage());
	}
    }

    private java.lang.Object readBoxedIDLEntity(Class clazz, String codebase)
    {
	try {
            ClassLoader clazzLoader = (clazz == null ? null : clazz.getClassLoader());
	    final Class helperClass = Utility.loadClassForClass(clazz.getName()+"Helper", codebase,
                                                   clazzLoader, clazz, clazzLoader);
	    final Class argTypes[] = {org.omg.CORBA.portable.InputStream.class};
            // getDeclaredMethod requires RuntimePermission accessDeclaredMembers
            // if a different class loader is used (even though the javadoc says otherwise)
            Method readMethod = null;
            try {
                readMethod = (Method)AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public java.lang.Object run() throws NoSuchMethodException {
                            return helperClass.getDeclaredMethod(kReadMethod, argTypes);
                        }
                    }
                );
            } catch (PrivilegedActionException pae) {
                // this gets caught below
                throw (NoSuchMethodException)pae.getException();
            }
	    java.lang.Object args[] = {parent};
	    return readMethod.invoke(null, args);

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
    }

    private java.lang.Object readIDLValue(int indirection, String repId,
					  Class clazz, String codebase)
    {					
	if (StreamableValue.class.isAssignableFrom(clazz) ||
    	    CustomValue.class.isAssignableFrom(clazz)) {

	    // use new-style OBV support (factory object)
	    ValueFactory factory = Utility.getFactory(clazz, codebase, orb, repId);
	    valueIndirection = indirection;  // for callback

	    return factory.read_value(parent);

	} else if (ValueBase.class.isAssignableFrom(clazz)) {

	    // use old-style OBV support (helper object)
	    BoxedValueHelper helper = Utility.getHelper(clazz, codebase, repId);
	    if (helper instanceof ValueHelper)
		return readIDLValueWithHelper((ValueHelper)helper, indirection);
	    else
		return helper.read_value(parent);
	
	} else {

	    // must be a boxed IDLEntity, so make a reflective call to the
	    // helper's static read method...
	    return readBoxedIDLEntity(clazz, codebase);
	}

    }

    /**
     * End tags are only written for chunked valuetypes.
     *
     * Before Merlin, our ORBs wrote end tags which took into account
     * all enclosing valuetypes.  This was changed by an interop resolution
     * (see details around chunkedValueNestingLevel) to only include
     * enclosing chunked types.
     *
     * ORB versioning and end tag compaction are handled here.
     */
    private void readEndTag() {
        if (isChunked) {

            // Read the end tag
            int anEndTag = read_long();

            // End tags should always be negative, and the outermost
            // enclosing chunked valuetype should have a -1 end tag.
            if (anEndTag >= 0) {
                if (anEndTag >= maxBlockLength) {
                    // A custom marshaled valuetype left extra data
                    // on the wire, and that data had another
                    // nested value inside of it.  We've just
                    // read the value tag of that nested value.
                    //
                    // In an attempt to get by it, we'll try to call
                    // read_value() to get the nested value off of
                    // the wire.  Afterwards, we must call handleEndOfValue
                    // to read any further chunks that the containing
                    // valuetype might still have after the nested
                    // value.  Finally, we make a recursive call to
                    // readEndTag to read the end tag of the
                    // containing value, or do this again if there
                    // are more nested values.
                    bbwi.index -= 4;
                    read_value();
                    handleEndOfValue();
                    readEndTag();
                    return;
                } else {
                    // We read something that wasn't big enough to be
                    // a value tag.  This is an error.
                    throw new MARSHAL("Read non-negative end tag: "
                                      + anEndTag + " at " + (get_offset() - 4),
                                      MinorCodes.POSITIVE_END_TAG,
                                      CompletionStatus.COMPLETED_MAYBE);
                }
            }

            // If the ORB is null, or if we're sure we're talking to
            // a foreign ORB, Merlin, or something more recent, we
            // use the updated end tag computation, and are more strenuous
            // about the values.
            if (orb == null ||
                ORBVersionImpl.FOREIGN.equals(orb.getORBVersion()) ||
                ORBVersionImpl.NEWER.compareTo(orb.getORBVersion()) <= 0) {

                // If the end tag we read was less than what we were expecting,
                // then the sender must think it's sent more enclosing 
                // chunked valuetypes than we have.  Throw an exception.
                if (anEndTag < chunkedValueNestingLevel)
                    throw new MARSHAL("Expecting fewer enclosing valuetypes.  "
                                      + "Received end tag " + anEndTag
                                      + " but expected " + chunkedValueNestingLevel,
                                      MinorCodes.UNEXPECTED_ENCLOSING_VALUETYPE,
                                      CompletionStatus.COMPLETED_MAYBE);

                // If the end tag is bigger than what we expected, but
                // still negative, then the sender has done some end tag
                // compaction.  We back up the stream 4 bytes so that the
                // next time readEndTag is called, it will get down here
                // again.  Even with fragmentation, we'll always be able
                // to do this.
                if (anEndTag != chunkedValueNestingLevel)
                    bbwi.index -= 4;

            } else {
                
                // When talking to Kestrel or Ladybird, we use our old
                // end tag rules and are less strict.  If the end tag
                // isn't what we expected, we back up, assuming
                // compaction.
                if (anEndTag != end_flag) {
                    bbwi.index -= 4;
                }
            }

            // This only keeps track of the enclosing chunked
            // valuetypes
            chunkedValueNestingLevel++;
        }

        // This keeps track of all enclosing valuetypes
	end_flag++;
    }

    protected int get_offset() {
	return bbwi.index;
    }

    private void start_block() {
		
	// if (outerValueDone)
	if (!isChunked)
	    return;
	
	// if called from alignAndCheck, need to reset blockLength
	// to avoid an infinite recursion loop on read_long() call
	blockLength = maxBlockLength;

	blockLength = read_long();

        // Must remember where we began the chunk to calculate how far
        // along we are.  See notes above about chunkBeginPos.

	if ((blockLength > 0) && (blockLength < maxBlockLength)) {
	    blockLength += get_offset();  // _REVISIT_ unsafe, should use a Java long

	    // inBlock = true;
	} else {
	    // not a chunk length field
	    blockLength = maxBlockLength;

	    bbwi.index -= 4;
	}
		
    }

    // Makes sure that if we were reading a chunked value, we end up
    // at the right place in the stream, no matter how little the
    // unmarshalling code read
    private void handleEndOfValue() {
        if (!isChunked)
            return;

        while (blockLength != maxBlockLength) {
            end_block();
            start_block();
        }
    }

    private void end_block() {

	// if in a chunk, check for underflow or overflow
	if (blockLength != maxBlockLength) {
	    if (blockLength == get_offset()) {
                // Chunk ended correctly
		blockLength = maxBlockLength;
            } else {
                // Skip over anything left by bad unmarshaling code (ex:
                // a buggy custom unmarshaler).  REVISIT   This needs to be
                // done for all remaining chunks in this value, right?
                if (blockLength > get_offset()) {
                    skipToOffset(blockLength);
                } else {
                    throw new MARSHAL("Incorrect chunk length " 
                                      + blockLength 
                                      + " at offset " 
                                      + get_offset(),
                                      com.sun.corba.se.internal.orbutil.MinorCodes.CHUNK_OVERFLOW, 
                                      CompletionStatus.COMPLETED_NO);
                }
            }
        }
    }
    
    private int readValueTag(){
	// outerValueDone = false;
        return read_long();
    }

    public org.omg.CORBA.ORB orb() {
        return orb;    
    }

    // ------------ End RMI related methods --------------------------

    public final void read_boolean_array(boolean[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_boolean();
    	}
    }

    public final void read_char_array(char[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_char();
    	}
    }

    public final void read_wchar_array(char[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_wchar();
    	}
    }

    public final void read_short_array(short[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_short();
    	}
    }

    public final void read_ushort_array(short[] value, int offset, int length) {
    	read_short_array(value, offset, length);
    }

    public final void read_long_array(int[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_long();
    	}
    }

    public final void read_ulong_array(int[] value, int offset, int length) {
    	read_long_array(value, offset, length);
    }

    public final void read_longlong_array(long[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_longlong();
    	}
    }

    public final void read_ulonglong_array(long[] value, int offset, int length) {
    	read_longlong_array(value, offset, length);
    }

    public final void read_float_array(float[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_float();
    	}
    }

    public final void read_double_array(double[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_double();
    	}
    }

    public final void read_any_array(org.omg.CORBA.Any[] value, int offset, int length) {
    	for(int i=0; i < length; i++) {
    	    value[i+offset] = read_any();
    	}
    }

    //--------------------------------------------------------------------//
    // CDRInputStream state management.
    //

    /** 
     * Are we at the end of the input stream?
     */
//     public final boolean isAtEnd() {
//     	return bbwi.index == bbwi.buflen;
//     }

//     public int available() throws IOException {
//         return bbwi.buflen - bbwi.index;
//     }
    
    private String read_repositoryIds() {
		
	// Read # of repository ids
	int numRepIds = read_long();
	if (numRepIds == 0xffffffff) {
            int indirection = read_long() + get_offset() - 4;
            if (repositoryIdCache != null && repositoryIdCache.containsOrderedVal(indirection))
		return (String)repositoryIdCache.getKey(indirection);
            else
		throw new org.omg.CORBA.MARSHAL("Unable to locate array of repository IDs from indirection " 
                                                + indirection);			

	} else {

	    // read first array element and store it as an indirection to the whole array
	    int indirection = get_offset() - 4; 
	    String repID = read_repositoryId();
            if (repositoryIdCache == null)
        	repositoryIdCache = new CacheTable(false);
            repositoryIdCache.put(repID, indirection);

	    // read and ignore the subsequent array elements, but put them in the
	    // indirection table in case there are later indirections back to them
	    for (int i = 1; i < numRepIds; i++) {
		read_repositoryId();
	    }
		
	    return repID;
	}
    }

    private final String read_repositoryId() {

        String result = readStringOrIndirection(true);

        if (result == null) { // Indirection

            int indirection = read_long() + get_offset() - 4;

            if (repositoryIdCache != null && repositoryIdCache.containsOrderedVal(indirection))
                return (String)repositoryIdCache.getKey(indirection);
            else
		throw new org.omg.CORBA.MARSHAL("Repid indirection @ " + bbwi.index, 
                                                MinorCodes.BAD_REP_ID_INDIRECTION,
						CompletionStatus.COMPLETED_MAYBE);
        } else {

            if (repositoryIdCache == null)
                repositoryIdCache = new CacheTable(false);
            repositoryIdCache.put(result, stringIndirection);

            return result;
        }
    }

    private final String read_codebase_URL() {

        String result = readStringOrIndirection(true);

        if (result == null) { // Indirection

            int indirection = read_long() + get_offset() - 4;

            if (codebaseCache != null && codebaseCache.containsVal(indirection))
                return (String)codebaseCache.getKey(indirection);
            else
		throw new org.omg.CORBA.MARSHAL("Codebase indirection @ " + bbwi.index, 
                                                MinorCodes.BAD_CODEBASE_INDIRECTION,
						CompletionStatus.COMPLETED_MAYBE);
	} else {

	    if (codebaseCache == null)
		codebaseCache = new CacheTable(false);
	    codebaseCache.put(result, stringIndirection);

            return result;
        }
    }

    /* DataInputStream methods */

    public java.lang.Object read_Abstract () {
        return read_abstract_interface();
    }

    public java.io.Serializable read_Value () {
        return read_value();
    }

    public void read_any_array (org.omg.CORBA.AnySeqHolder seq, int offset, int length) {
        read_any_array(seq.value, offset, length);
    }

    public void read_boolean_array (org.omg.CORBA.BooleanSeqHolder seq, int offset, int length) {
        read_boolean_array(seq.value, offset, length);
    }

    public void read_char_array (org.omg.CORBA.CharSeqHolder seq, int offset, int length) {
        read_char_array(seq.value, offset, length);
    }

    public void read_wchar_array (org.omg.CORBA.WCharSeqHolder seq, int offset, int length) {
        read_wchar_array(seq.value, offset, length);
    }

    public void read_octet_array (org.omg.CORBA.OctetSeqHolder seq, int offset, int length) {
        read_octet_array(seq.value, offset, length);
    }

    public void read_short_array (org.omg.CORBA.ShortSeqHolder seq, int offset, int length) {
        read_short_array(seq.value, offset, length);
    }

    public void read_ushort_array (org.omg.CORBA.UShortSeqHolder seq, int offset, int length) {
        read_ushort_array(seq.value, offset, length);
    }

    public void read_long_array (org.omg.CORBA.LongSeqHolder seq, int offset, int length) {
        read_long_array(seq.value, offset, length);
    }

    public void read_ulong_array (org.omg.CORBA.ULongSeqHolder seq, int offset, int length) {
        read_ulong_array(seq.value, offset, length);
    }

    public void read_ulonglong_array (org.omg.CORBA.ULongLongSeqHolder seq, int offset, int length) {
        read_ulonglong_array(seq.value, offset, length);
    }

    public void read_longlong_array (org.omg.CORBA.LongLongSeqHolder seq, int offset, int length) {
        read_longlong_array(seq.value, offset, length);
    }

    public void read_float_array (org.omg.CORBA.FloatSeqHolder seq, int offset, int length) {
        read_float_array(seq.value, offset, length);
    }

    public void read_double_array (org.omg.CORBA.DoubleSeqHolder seq, int offset, int length) {
        read_double_array(seq.value, offset, length);
    }

    public java.math.BigDecimal read_fixed(short digits, short scale) {
        // digits isn't really needed here
        StringBuffer buffer = read_fixed_buffer();
        if (digits != buffer.length())
            throw new MARSHAL();
        buffer.insert(digits - scale, '.');
        return new BigDecimal(buffer.toString());
    }

    // This method is unable to yield the correct scale.
    public java.math.BigDecimal read_fixed() {
        return new BigDecimal(read_fixed_buffer().toString());
    }

    // Each octet contains (up to) two decimal digits.
    // If the fixed type has an odd number of decimal digits, then the representation
    // begins with the first (most significant) digit.
    // Otherwise, this first half-octet is all zero, and the first digit
    // is in the second half-octet.
    // The sign configuration, in the last half-octet of the representation,
    // is 0xD for negative numbers and 0xC for positive and zero values.
    private StringBuffer read_fixed_buffer() {
        StringBuffer buffer = new StringBuffer(64);
        byte doubleDigit;
        int firstDigit;
        int secondDigit;
        boolean wroteFirstDigit = false;
        boolean more = true;
        while (more) {
            doubleDigit = this.read_octet();
            firstDigit = (int)((doubleDigit & 0xf0) >> 4);
            secondDigit = (int)(doubleDigit & 0x0f);
            if (wroteFirstDigit || firstDigit != 0) {
                buffer.append(Character.forDigit(firstDigit, 10));
                wroteFirstDigit = true;
            }
            if (secondDigit == 12) {
                // positive number or zero
                if ( ! wroteFirstDigit) {
                    // zero
                    return new StringBuffer("0.0");
                } else {
                    // positive number
                    // done
                }
                more = false;
            } else if (secondDigit == 13) {
                // negative number
                buffer.insert(0, '-');
                more = false;
            } else {
                buffer.append(Character.forDigit(secondDigit, 10));
                wroteFirstDigit = true;
            }
        }
        return buffer;
    }

    private final static String _id = "IDL:omg.org/CORBA/DataInputStream:1.0";
    private final static String[] _ids = { _id };

    public String[] _truncatable_ids() {
        if (_ids == null)
            return null;

        return (String[])_ids.clone();
    }

    /* for debugging */

    public void printBuffer() {
        CDRInputStream_1_0.printBuffer(this.bbwi);
    }

    public static void printBuffer(ByteBufferWithInfo bbwi) {

        System.out.println("----- Input Buffer -----");
        System.out.println();
        System.out.println("Current index: " + bbwi.index);
        System.out.println("Total length : " + bbwi.buflen);
        System.out.println();

        try {

            char[] charBuf = new char[16];

            for (int i = 0; i < bbwi.buflen; i += 16) {

                int j = 0;

                // For every 16 bytes, there is one line
                // of output.  First, the hex output of
                // the 16 bytes with each byte separated
                // by a space.
                while (j < 16 && j + i < bbwi.buflen) {
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
                while (x < 16 && x + i < bbwi.buflen) {
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

        System.out.println("------------------------");
    }

    public byte[] getByteBuffer() {
        return bbwi.buf;
    }

    public void setByteBuffer(byte buffer[]) {
        bbwi.buf = buffer;
    }

    public int getBufferLength() {
        return bbwi.buflen;
    }

    public void setBufferLength(int value) {
        bbwi.buflen = value;
    }

    public int getIndex() {
        return bbwi.index;
    }

    public void setIndex(int value) {
        bbwi.index = value;
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void orb(org.omg.CORBA.ORB orb) {
        this.orb = (com.sun.corba.se.internal.corba.ORB)orb;
    }

    public BufferManagerRead getBufferManager() {
        return bufferManagerRead;
    }

    private void skipToOffset(int offset) {

        // Number of bytes to skip
        int len = offset - get_offset();

    	int n = 0;

    	while (n < len) {
    	    int avail;
    	    int bytes;
    	    int wanted;

    	    avail = bbwi.buflen - bbwi.index;
            if (avail <= 0) {
                grow(1, 1);
                avail = bbwi.buflen - bbwi.index;
            }

    	    wanted = len - n;
    	    bytes = (wanted < avail) ? wanted : avail;
    	    bbwi.index += bytes;
    	    n += bytes;
    	}
    }


    // Mark and reset -------------------------------------------------

    protected MarkAndResetHandler markAndResetHandler = null;

    protected class StreamMemento
    {
        // These are the fields that may change after marking
        // the stream position, so we need to save them.
        private int blockLength_;
        private int end_flag_;
        private int chunkedValueNestingLevel_;
        private int valueIndirection_;
        private int stringIndirection_;
        private boolean isChunked_;
        private javax.rmi.CORBA.ValueHandler valueHandler_;
        private ByteBufferWithInfo bbwi_;

        public StreamMemento()
        {
            blockLength_ = blockLength;
            end_flag_ = end_flag;
            chunkedValueNestingLevel_ = chunkedValueNestingLevel;
            valueIndirection_ = valueIndirection;
            stringIndirection_ = stringIndirection;
            isChunked_ = isChunked;
            valueHandler_ = valueHandler;
            bbwi_ = new ByteBufferWithInfo(bbwi);
        }
    }

    public java.lang.Object createStreamMemento() {
        return new StreamMemento();
    }

    public void restoreInternalState(java.lang.Object streamMemento) {

        StreamMemento mem = (StreamMemento)streamMemento;

        blockLength = mem.blockLength_;
        end_flag = mem.end_flag_;
        chunkedValueNestingLevel = mem.chunkedValueNestingLevel_;
        valueIndirection = mem.valueIndirection_;
        stringIndirection = mem.stringIndirection_;
        isChunked = mem.isChunked_;
        valueHandler = mem.valueHandler_;

        bbwi = mem.bbwi_;
    }

    public int getPosition() {
        return get_offset();
    }

    public void mark(int readlimit) {
        markAndResetHandler.mark(this);
    }

    public void reset() {
        markAndResetHandler.reset();
    }

    // ---------------------------------- end Mark and Reset

    // Provides a hook so subclasses of CDRInputStream can provide
    // a CodeBase.  This ultimately allows us to grab a Connection
    // instance in IIOPInputStream, the only subclass where this
    // is actually used.
    CodeBase getCodeBase() {
        return parent.getCodeBase();
    }

    /**
     * Attempts to find the class described by the given
     * repository ID string and expected type.  The first
     * attempt is to find the class locally, falling back
     * on the URL that came with the value.  The second
     * attempt is to use a URL from the remote CodeBase.
     */
    private Class getClassFromString(String repositoryIDString,
                                     String codebaseURL,
                                     Class expectedType)
    {
        RepositoryIdInterface repositoryID 
            = repIdStrs.getFromString(repositoryIDString);

        try {
            try {
                // First try to load the class locally, then use
                // the provided URL (if it isn't null)
                return repositoryID.getClassFromType(expectedType,
                                                     codebaseURL);
            } catch (ClassNotFoundException cnfeOuter) {
                
                try {
                    // Get a URL from the remote CodeBase and retry
                    codebaseURL = getCodeBase().implementation(repositoryIDString);
                    
                    // Don't bother trying to find it locally again if
                    // we got a null URL
                    if (codebaseURL == null)
                        return null;
                    
                    return repositoryID.getClassFromType(expectedType,
                                                         codebaseURL);
                } catch (ClassNotFoundException cnfeInner) {
                    debugPrintThrowable(cnfeInner);
                    // Failed to load the class
                    return null;
                }
            }
        } catch (MalformedURLException mue) {
            debugPrintThrowable(mue);

            // Always report a bad URL
            throw new MARSHAL("Unable to locate value class for rep. id : "
                              + repositoryIDString + 
                              " because of malformed URL "
                              + codebaseURL,
                              MinorCodes.MALFORMED_URL,
                              CompletionStatus.COMPLETED_MAYBE);
        }
    }

    /**
     * Attempts to find the class described by the given
     * repository ID string.  At most, three attempts are made:
     * Try to find it locally, through the provided URL, and
     * finally, via a URL from the remote CodeBase.
     */
    private Class getClassFromString(String repositoryIDString,
                                     String codebaseURL)
    {
        RepositoryIdInterface repositoryID 
            = repIdStrs.getFromString(repositoryIDString);

        for (int i = 0; i < 3; i++) {

            try {

                switch (i) 
                {
                    case 0:
                        // First try to load the class locally
                        return repositoryID.getClassFromType();
                    case 1:
                        // Try to load the class using the provided
                        // codebase URL (falls out below)
                        break;
                    case 2:
                        // Try to load the class using a URL from the
                        // remote CodeBase
                        codebaseURL = getCodeBase().implementation(repositoryIDString);
                        break;
                }

                // Don't bother if the codebaseURL is null
                if (codebaseURL == null)
                    continue;

                return repositoryID.getClassFromType(codebaseURL);

            } catch(ClassNotFoundException cnfe) {
                // Will ultimately return null if all three
                // attempts fail, but don't do anything here.
            } catch (MalformedURLException mue) {
                debugPrintThrowable(mue);

                // Always report a bad URL
                throw new MARSHAL("Unable to locate value class for rep. id : "
                                  + repositoryIDString + 
                                  " because of malformed URL "
                                  + codebaseURL,
                                  MinorCodes.MALFORMED_URL,
                                  CompletionStatus.COMPLETED_MAYBE);
            }
        }

        // If we get here, we have failed to load the class
        debugPrintMessage("getClassFromString failed with rep id "
                          + repositoryIDString
                          + " and codebase "
                          + codebaseURL);
        
        return null;
    }

    // Utility method used to get chars from bytes
    char[] getConvertedChars(int numBytes,
                             CodeSetConversion.BTCConverter converter) {

        // To be honest, I doubt this saves much real time
        if (bbwi.buflen - bbwi.index >= numBytes) {
            // If the entire string is in this buffer,
            // just convert directly from the bbwi rather than
            // allocating and copying.
            char[] result = converter.getChars(bbwi.buf, bbwi.index, numBytes);

            bbwi.index += numBytes;

            return result;
            
        } else {
            // Stretches across buffers.  Unless we provide an
            // incremental conversion interface, allocate and
            // copy the bytes.            
            byte[] bytes = new byte[numBytes];
            read_octet_array(bytes, 0, bytes.length);

            return converter.getChars(bytes, 0, numBytes);
        }
    }

    protected CodeSetConversion.BTCConverter getCharConverter() {
        if (charConverter == null)
            charConverter = parent.createCharBTCConverter();
        
        return charConverter;
    }

    protected CodeSetConversion.BTCConverter getWCharConverter() {
        if (wcharConverter == null)
            wcharConverter = parent.createWCharBTCConverter();
    
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

    /**
     * Aligns the current position on the given octet boundary
     * if there are enough bytes available to do so.  Otherwise,
     * it just returns.  This is used for some (but not all)
     * GIOP 1.2 message headers.
     */
    void alignOnBoundary(int octetBoundary) {
        int needed = computeAlignment(octetBoundary);

        if (bbwi.index + needed <= bbwi.buflen)
            bbwi.index += needed;
    }

    public void resetCodeSetConverters() {
        charConverter = null;
        wcharConverter = null;
    }
}
