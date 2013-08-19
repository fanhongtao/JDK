/*
 * @(#)ORBUtility.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.lang.reflect.Field;

import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.Util;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.CORBA.TypeCodePackage.*;

import java.rmi.NoSuchObjectException;
import java.util.NoSuchElementException;

import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionImpl ;

/**
 *  Handy class full of static functions that don't belong in util.Utility for pure ORB reasons.
 */
public final class ORBUtility {
    private ORBUtility() {}

    private static StructMember[] members = null;

    private static StructMember[] systemExceptionMembers (ORB orb) {
        if (members == null) {
            members = new StructMember[3];
            members[0] = new StructMember("id", orb.create_string_tc(0), null);
            members[1] = new StructMember("minor", orb.get_primitive_tc(TCKind.tk_long), null);
            members[2] = new StructMember("completed", orb.get_primitive_tc(TCKind.tk_long), null);
        }
        return members;
    }

    private static TypeCode getSystemExceptionTypeCode(ORB orb, String repID, String name) {
        synchronized (TypeCode.class) {
            return orb.create_exception_tc(repID, name, systemExceptionMembers(orb));
        }
    }

    private static boolean isSystemExceptionTypeCode(TypeCode type, ORB orb) {
        StructMember[] systemExceptionMembers = systemExceptionMembers(orb);
        try {
            return (type.kind().value() == TCKind._tk_except &&
                    type.member_count() == 3 &&
                    type.member_type(0).equal(systemExceptionMembers[0].type) &&
                    type.member_type(1).equal(systemExceptionMembers[1].type) &&
                    type.member_type(2).equal(systemExceptionMembers[2].type));
        } catch (BadKind ex) {
            return false;
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            return false;
        }
    }

    /**
     * Static method for writing a CORBA standard exception to an Any.
     * @param any The Any to write the SystemException into.
     */
    public static void insertSystemException(SystemException ex, Any any) {
        OutputStream out = any.create_output_stream();
        ORB orb = out.orb();
        String name = ex.getClass().getName();
        String repID = ORBUtility.repositoryIdOf(name);
        out.write_string(repID);
        out.write_long(ex.minor);
        out.write_long(ex.completed.value());
        any.read_value(out.create_input_stream(), 
	    getSystemExceptionTypeCode(orb, repID, name));
    }

    public static SystemException extractSystemException(Any any) {
        InputStream in = any.create_input_stream();
        ORB orb = in.orb();
        if ( ! isSystemExceptionTypeCode(any.type(), orb)) {
	    return new org.omg.CORBA.UNKNOWN(MinorCodes.UNKNOWN_DSI_SYSEX,
					     CompletionStatus.COMPLETED_MAYBE);
        }
        return ORBUtility.readSystemException(in);
    }

    /**
     * Creates the correct ValueHandler for the given ORB, 
     * querying ORBVersion information.  If the ORB or
     * ORBVersion is null, gets the ValueHandler from
     * Util.createValueHandler.
     */
    public static ValueHandler createValueHandler(com.sun.corba.se.internal.corba.ORB orb) {

        if (orb == null)
            return Util.createValueHandler();

        ORBVersion version = orb.getORBVersion();

        if (version == null)
            return Util.createValueHandler();

        if (version.equals(ORBVersionImpl.OLD))
            return new ValueHandlerImpl_1_3();
        if (version.equals(ORBVersionImpl.NEW))
            return new ValueHandlerImpl_1_3_1();

        return Util.createValueHandler();
    }

    /**
     * Returns true if the given ORB could accurately be determined to be a
     * Kestrel or earlier ORB.  Note: If passed the ORBSingleton, this will return
     * false.
     */
    public static boolean isLegacyORB(com.sun.corba.se.internal.corba.ORB orb) 
    {
        try {
            ORBVersion currentORB = orb.getORBVersion();
            return currentORB.equals( ORBVersionImpl.OLD ) ;
        } catch (SecurityException se) {
            return false;
        }
    }

    /**
     * Returns true if it was accurately determined that the remote ORB is
     * a foreign (non-JavaSoft) ORB.  Note:  If passed the ORBSingleton, this
     * will return false.
     */
    public static boolean isForeignORB(com.sun.corba.se.internal.corba.ORB orb)
    {
        if (orb == null)
            return false;

        try {
            return orb.getORBVersion().equals(ORBVersionImpl.FOREIGN);
        } catch (SecurityException se) {
            return false;
        }
    }

    /** Unmarshal a byte array to an integer.
        Assume the bytes are in BIGENDIAN order.
        i.e. array[offset] is the most-significant-byte
        and  array[offset+3] is the least-significant-byte.
        @param array The array of bytes.
        @param offset The offset from which to start unmarshalling.
    */
    public static int bytesToInt(byte[] array, int offset)
    {
        int b1, b2, b3, b4;

        b1 = (array[offset++] << 24) & 0xFF000000;
        b2 = (array[offset++] << 16) & 0x00FF0000;
        b3 = (array[offset++] << 8)  & 0x0000FF00;
        b4 = (array[offset++] << 0)  & 0x000000FF;

        return (b1 | b2 | b3 | b4);
    }

    /** Marshal an integer to a byte array.
        The bytes are in BIGENDIAN order.
        i.e. array[offset] is the most-significant-byte
        and  array[offset+3] is the least-significant-byte.
        @param array The array of bytes.
        @param offset The offset from which to start marshalling.
    */
    public static void intToBytes(int value, byte[] array, int offset)
    {
        array[offset++] = (byte)((value >>> 24) & 0xFF);
        array[offset++] = (byte)((value >>> 16) & 0xFF);
        array[offset++] = (byte)((value >>> 8) & 0xFF);
        array[offset++] = (byte)((value >>> 0) & 0xFF);
    }

    /** Converts an Ascii Character into Hexadecimal digit
     */
    public static int hexOf( char x )
    {
	int val;

        val = x - '0';
        if (val >=0 && val <= 9)
            return val;

        val = (x - 'a') + 10;
        if (val >= 10 && val <= 15)
            return val;

        val = (x - 'A') + 10;
        if (val >= 10 && val <= 15)
            return val;

        throw new DATA_CONVERSION(MinorCodes.BAD_HEX_DIGIT,
                                  CompletionStatus.COMPLETED_NO);
    }

    // method moved from util.Utility

    /**
     * Static method for writing a CORBA standard exception to a stream.
     * @param strm The OutputStream to use for marshaling.
     */
    public static void writeSystemException(SystemException ex, OutputStream strm)
    {
	String s;

	s = repositoryIdOf(ex.getClass().getName());
        strm.write_string(s);
        strm.write_long(ex.minor);
        strm.write_long(ex.completed.value());
    }

    /**
     * Static method for reading a CORBA standard exception from a stream.
     * @param strm The InputStream to use for unmarshaling.
     */
    public static SystemException readSystemException(InputStream strm)
    {
	try {
	    String name = classNameOf(strm.read_string());
	    SystemException ex 
                = (SystemException)ORBClassLoader.loadClass(name).newInstance();
	    ex.minor = strm.read_long();
	    ex.completed = CompletionStatus.from_int(strm.read_long());
	    return ex;
	} catch ( Exception ex ) {
	    return new org.omg.CORBA.UNKNOWN(MinorCodes.UNKNOWN_SYSEX,
					     CompletionStatus.COMPLETED_MAYBE);
	}
    }

    /**
     * Get the class name corresponding to a particular repository Id.
     * This is used by the system to unmarshal (instantiate) the
     * appropriate exception class for an marshaled as the value of
     * its repository Id.
     * @param repositoryId The repository Id for which we want a class name.
     */
    public static String classNameOf(String repositoryId)
    {
	String className=null;

	className = (String) exceptionClassNames.get(repositoryId);
	if (className == null)
	    className = "org.omg.CORBA.UNKNOWN";

	return className;
    }

    /**
     * Return true if this repositoryId is a SystemException.
     * @param repositoryId The repository Id to check.
     */
    public static boolean isSystemException(String repositoryId)
    {
	String className=null;

	className = (String) exceptionClassNames.get(repositoryId);
	if (className == null)
	    return false;
	else
	    return true;
    }

    /**
     * Get the repository id corresponding to a particular class.
     * This is used by the system to write the
     * appropriate repository id for a system exception.
     * @param name The class name of the system exception.
     */
    public static String repositoryIdOf(String name)
    {
	String id;

	id = (String) exceptionRepositoryIds.get(name);
	if (id == null)
	    id = "IDL:omg.org/CORBA/UNKNOWN:1.0";

	return id;
    }

    private static final Hashtable exceptionClassNames = new Hashtable();
    private static final Hashtable exceptionRepositoryIds = new Hashtable();

    static {

	//
	// construct repositoryId -> className hashtable
	//
	exceptionClassNames.put("IDL:omg.org/CORBA/BAD_CONTEXT:1.0",
				"org.omg.CORBA.BAD_CONTEXT");
	exceptionClassNames.put("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0",
				"org.omg.CORBA.BAD_INV_ORDER");
	exceptionClassNames.put("IDL:omg.org/CORBA/BAD_OPERATION:1.0",
				"org.omg.CORBA.BAD_OPERATION");
	exceptionClassNames.put("IDL:omg.org/CORBA/BAD_PARAM:1.0",
				"org.omg.CORBA.BAD_PARAM");
	exceptionClassNames.put("IDL:omg.org/CORBA/BAD_TYPECODE:1.0",
				"org.omg.CORBA.BAD_TYPECODE");
	exceptionClassNames.put("IDL:omg.org/CORBA/COMM_FAILURE:1.0",
				"org.omg.CORBA.COMM_FAILURE");
	exceptionClassNames.put("IDL:omg.org/CORBA/DATA_CONVERSION:1.0",
				"org.omg.CORBA.DATA_CONVERSION");
	exceptionClassNames.put("IDL:omg.org/CORBA/IMP_LIMIT:1.0",
				"org.omg.CORBA.IMP_LIMIT");
	exceptionClassNames.put("IDL:omg.org/CORBA/INTF_REPOS:1.0",
				"org.omg.CORBA.INTF_REPOS");
	exceptionClassNames.put("IDL:omg.org/CORBA/INTERNAL:1.0",
				"org.omg.CORBA.INTERNAL");
	exceptionClassNames.put("IDL:omg.org/CORBA/INV_FLAG:1.0",
				"org.omg.CORBA.INV_FLAG");
	exceptionClassNames.put("IDL:omg.org/CORBA/INV_IDENT:1.0",
				"org.omg.CORBA.INV_IDENT");
	exceptionClassNames.put("IDL:omg.org/CORBA/INV_OBJREF:1.0",
				"org.omg.CORBA.INV_OBJREF");
	exceptionClassNames.put("IDL:omg.org/CORBA/MARSHAL:1.0",
				"org.omg.CORBA.MARSHAL");
	exceptionClassNames.put("IDL:omg.org/CORBA/NO_MEMORY:1.0",
				"org.omg.CORBA.NO_MEMORY");
	exceptionClassNames.put("IDL:omg.org/CORBA/FREE_MEM:1.0",
				"org.omg.CORBA.FREE_MEM");
	exceptionClassNames.put("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0",
				"org.omg.CORBA.NO_IMPLEMENT");
	exceptionClassNames.put("IDL:omg.org/CORBA/NO_PERMISSION:1.0",
				"org.omg.CORBA.NO_PERMISSION");
	exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESOURCES:1.0",
				"org.omg.CORBA.NO_RESOURCES");
	exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESPONSE:1.0",
				"org.omg.CORBA.NO_RESPONSE");
	exceptionClassNames.put("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0",
				"org.omg.CORBA.OBJ_ADAPTER");
	exceptionClassNames.put("IDL:omg.org/CORBA/INITIALIZE:1.0",
				"org.omg.CORBA.INITIALIZE");
	exceptionClassNames.put("IDL:omg.org/CORBA/PERSIST_STORE:1.0",
				"org.omg.CORBA.PERSIST_STORE");
	exceptionClassNames.put("IDL:omg.org/CORBA/TRANSIENT:1.0",
				"org.omg.CORBA.TRANSIENT");
	exceptionClassNames.put("IDL:omg.org/CORBA/UNKNOWN:1.0",
				"org.omg.CORBA.UNKNOWN");
	exceptionClassNames.put("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0",
				"org.omg.CORBA.OBJECT_NOT_EXIST");

	// SystemExceptions from OMG Transactions Service Spec
	exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0",
				"org.omg.CORBA.INVALID_TRANSACTION");
	exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0",
				"org.omg.CORBA.TRANSACTION_REQUIRED");
	exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0",
				"org.omg.CORBA.TRANSACTION_ROLLEDBACK");

	// from portability RTF 98-07-01.txt
	exceptionClassNames.put("IDL:omg.org/CORBA/INV_POLICY:1.0",
				"org.omg.CORBA.INV_POLICY");

	// from orbrev/00-09-01 (CORBA 2.4 Draft Specification)
	exceptionClassNames.
	    put("IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0",
				"org.omg.CORBA.TRANSACTION_UNAVAILABLE");
	exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_MODE:1.0",
				"org.omg.CORBA.TRANSACTION_MODE");

	//
	// construct className -> repositoryId hashtable
	//
	Enumeration keys = exceptionClassNames.keys();
	java.lang.Object s;
	String rId;
	String cName;

	try{
            while (keys.hasMoreElements()) {
                s = keys.nextElement();
	        rId = (String) s;
	        cName = (String) exceptionClassNames.get(rId);
	        exceptionRepositoryIds.put (cName, rId);
	    }
	} catch (NoSuchElementException e) { }
    }

    /** Parse a version string such as "1.1.6" or "jdk1.2fcs" into
	a version array of integers {1, 1, 6} or {1, 2}.
	A string of "n." or "n..m" is equivalent to "n.0" or "n.0.m" respectively.
    */
    public static int[] parseVersion(String version) {
	if (version == null)
	    return new int[0];
	char[] s = version.toCharArray();
	//find the maximum span of the string "n.n.n..." where n is an integer
	int start = 0;
	for (; start < s.length  && (s[start] < '0' || s[start] > '9'); ++start)
	    if (start == s.length)	//no digit found
		return new int[0];
	int end = start + 1;
	int size = 1;
	for (; end < s.length; ++end)
	    if (s[end] == '.')
		++size;
	    else if (s[end] < '0' || s[end] > '9')
		break;
	int[] val = new int[size];
	for (int i = 0; i < size; ++i) {
	    int dot = version.indexOf('.', start);
	    if (dot == -1 || dot > end)
		dot = end;
	    if (start >= dot)	//cases like "n." or "n..m"
		val[i] = 0;	//convert equivalent to "n.0" or "n.0.m"
	    else
		val[i] = Integer.parseInt(version.substring(start, dot));
	    start = dot + 1;
	}
	return val;
    }

    /** Compare two version arrays.
	Return 1, 0 or -1 if v1 is greater than, equal to, or less than v2.
    */
    public static int compareVersion(int[] v1, int[] v2) {
	if (v1 == null)
	    v1 = new int[0];
	if (v2 == null)
	    v2 = new int[0];
	for (int i = 0; i < v1.length; ++i) {
	    if (i >= v2.length || v1[i] > v2[i])	//v1 is longer or greater than v2
		return 1;
	    if (v1[i] < v2[i])
		return -1;
	}
	return v1.length == v2.length ? 0 : -1;
    }

    /** Compare two version strings.
	Return 1, 0 or -1 if v1 is greater than, equal to, or less than v2.
    */
    public static int compareVersion(String v1, String v2) {
	return compareVersion(parseVersion(v1), parseVersion(v2));
    }

    /** A smarter toString that uses the built-in toString.
     * If obj isA:
     *	Object that supports toString() directly: call toString
     *   array of objects: array[length]( toString results of non-null elements ) 
     *   other: print the contents of the object's public fields.
     * Note that this method is NOT SAFE FOR USE WITH CIRCULAR REFERENCES!
     * It will go into an infinite recursion if handed the top level
     * reference to a cyclic graph of objects.
     */
    public static String objectToString(java.lang.Object obj)
    {
	if (obj==null)
	    return "null" ;

	StringBuffer result = new StringBuffer() ;
	Class cls = obj.getClass() ;
	Class compClass = cls.getComponentType() ;

	if (obj instanceof java.util.Properties) {
	    Properties props = (Properties)obj ;
	    result.append( cls.getName() ) ;
	    result.append( "(" ) ;
	    Enumeration keys = props.propertyNames() ;
	    while (keys.hasMoreElements()) {
		String key = (String)(keys.nextElement()) ;
		String value = props.getProperty( key ) ;
		result.append( " " ) ;
		result.append( key ) ;
		result.append( "=" ) ;
		result.append( value ) ;
	    }

	    result.append( " )" ) ;
	    return result.toString() ;
	}

	try {
	    // If class of obj directly declares toString(), use it.
	    cls.getDeclaredMethod( "toString", null ) ;
	    return obj.toString() ;
	} catch (Exception exc) {
	    if (compClass == null) {
		// Must be some reference type, so use reflection to print fields
		Field[] fields ;

		try {
		    fields = cls.getFields() ;
		} catch (SecurityException sexc) {
		    // Can't get fields, so use default toString()
		    return obj.toString() ;
		}

		result.append( cls.getName() ) ;
		result.append( "(" ) ;

		for (int ctr=0; ctr<fields.length; ctr++ ) {
		    Field fld = fields[ctr] ;
		    result.append( " " ) ;
		    result.append( fld.getName() ) ;
		    result.append( "=" ) ;

		    try {
			java.lang.Object value = fld.get( obj ) ;
			result.append( objectToString( value ) ) ;
		    } catch (Exception exc2) {
			result.append( "???" ) ;
		    }
		}

		result.append( " )" ) ;
		return result.toString() ;
	    } else // we have an array
		result.append( compClass.getName() ) ;
	    result.append( "[" ) ;
	    if (compClass == boolean.class) {
		boolean[] arr = (boolean[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == byte.class) {
		byte[] arr = (byte[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == short.class) {
		short[] arr = (short[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == int.class) {
		int[] arr = (int[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == long.class) {
		long[] arr = (long[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == char.class) {
		char[] arr = (char[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == float.class) {
		float[] arr = (float[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else if (compClass == double.class) {
		double[] arr = (double[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( arr[ctr] ) ;
		}
	    } else { // array of object
		java.lang.Object[] arr = (java.lang.Object[])obj ;
		result.append( arr.length ) ;
		result.append( "](" ) ;
		for (int ctr=0; ctr<arr.length; ctr++) {
		    result.append( " " ) ;
		    result.append( objectToString( arr[ctr] ) ) ;
		}
	    }

	    result.append( " ]" ) ;
	    return result.toString() ;
	}
    }

    //
    // Implements all dprint calls in this package.
    //
    public static void dprint(java.lang.Object obj, String msg) {
	System.out.println(obj.getClass().getName() + "("  +
			   Thread.currentThread() + "): " + msg);
    }

    public static void dprint(String className, String msg) {
	System.out.println(className + "("  +
			   Thread.currentThread() + "): " + msg);
    }

    public void dprint(String msg) {
	ORBUtility.dprint(this, msg);
    }

    public static void dprint(java.lang.Object caller,
                              String msg,
                              Throwable t) {

        System.out.println(caller.getClass().getName() + '('
                           + Thread.currentThread() + "): "
                           + msg);

        if (t != null)
            t.printStackTrace();
    }

    public static String[] concatenateStringArrays( String[] arr1, String[] arr2 ) 
    {
	String[] result = new String[ 
	    arr1.length + arr2.length ] ;

	for (int ctr = 0; ctr<arr1.length; ctr++)
	    result[ctr] = arr1[ctr] ;

	for (int ctr = 0; ctr<arr2.length; ctr++)
	    result[ctr + arr1.length] = arr2[ctr] ;

	return result ;
    }

    /**
     * Throws the CORBA equivalent of a java.io.NotSerializableException
     *
     * Duplicated from util/Utility for Pure ORB reasons.  There are two
     * reasons for this:
     *
     * 1) We can't introduce dependencies on the util version from outside
     * of the io/util packages since it will not exist in the pure ORB
     * build running on JDK 1.3.x.
     *
     * 2) We need to pick up the correct minor code from orbutil.MinorCodes.
     * If we picked it up from util.MinorCodes, then it would have the
     * incorrect value when running the pure ORB on JDK 1.3.x.
     */
    public static void throwNotSerializableForCorba(String className) {
        throw new BAD_PARAM(className,
                            MinorCodes.NOT_SERIALIZABLE,
                            CompletionStatus.COMPLETED_MAYBE);
    }
}
