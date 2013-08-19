/*
 * @(#)AnyImpl.java	1.61 03/01/23
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

package com.sun.corba.se.internal.corba;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.orbutil.RepositoryIdFactory;
import com.sun.corba.se.internal.orbutil.RepositoryIdStrings;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.io.ValueUtility;

import java.io.Serializable;
import java.math.BigDecimal;

//import test.Debug;


final class AnyInputStream extends EncapsInputStream {
    
    AnyInputStream(EncapsInputStream theStream ) {
	super( theStream );
    }
}

final class AnyOutputStream extends EncapsOutputStream {

    public AnyOutputStream(org.omg.CORBA.ORB orb) {
        super(orb);
    }

    public AnyOutputStream(org.omg.CORBA.ORB orb, int size) {
        super(orb, size);
    }

    public org.omg.CORBA.portable.InputStream create_input_stream() {
        return new AnyInputStream((com.sun.corba.se.internal.corba.EncapsInputStream)super.create_input_stream());
    }
}


// subclasses must provide a matching helper class
public class AnyImpl extends Any
{

    //
    // Always valid.
    //
    private TypeCodeImpl typeCode;
    protected org.omg.CORBA.ORB orb;

    //
    // Validity depends upon typecode. The 'value' and 'object' instance
    // members are used to hold immutable types as defined by the
    // isStreamed[] table below. Otherwise, 'stream' is non-null and
    // holds the value in CDR marshaled format. As an optimization, the
    // stream type is an Any extension of CDR stream that is used to
    // detect an optimization in read_value().
    //

    private CDRInputStream stream;
    private long value;
    private java.lang.Object object;

    // Setting the typecode via the type() accessor wipes out the value.
    // An attempt to extract before the value is set will result
    // in a BAD_OPERATION exception being raised.
    private boolean isInitialized = false;

    private static final int DEFAULT_BUFFER_SIZE = 32;

    /*
     * This boolean array tells us if a given typecode must be
     * streamed. Objects that are immutable don't have to be streamed.
     */

    static boolean isStreamed[] = {
	false,  // null
	false,  // void
	false,  // short
	false,  // long
	false,  // ushort
	false,  // ulong
	false,  // float
	false,  // double
	false,  // boolean
	false,  // char
	false,  // octet
	false,  // any
	false,  // TypeCode
	true,   // Principal
	false,  // objref
	true,   // struct
	true,   // union
	false,  // enum
    	false,  // string
	true,   // sequence
	true,   // array
	true,   // alias
	true,   // except
	false,  // longlong
	false,  // ulonglong
	false,  // longdouble
	false,  // wchar
	false,  // wstring
	false,  // fixed
	false,  // value
	false,  // value_box (used to be true)
        false,  // native
        false   // abstract interface
    };

    static AnyImpl convertToNative(org.omg.CORBA.ORB orb, Any any) {
        if (any instanceof AnyImpl) {
            return (AnyImpl)any;
        } else {
            AnyImpl anyImpl = new AnyImpl(orb, any);
            anyImpl.typeCode = TypeCodeImpl.convertToNative(orb, anyImpl.typeCode);
            return anyImpl;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Constructors

    /**
     * A constructor that sets the Any to contain a null. It also marks
     * the value as being invalid so that extractions throw an exception
     * until an insertion has been performed. 
     */

    //private Debug debug = new Debug ("AnyImpl ");

    public AnyImpl(org.omg.CORBA.ORB orb)
    {
	this.orb = orb;

	typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_null);
	stream = null;
	object = null;
	value = 0;
        // null is a valid value
        isInitialized = true;
    }


    //
    // Create a new AnyImpl which is a copy of obj.
    //
    public AnyImpl(org.omg.CORBA.ORB orb, Any obj) {
	this(orb);

	if ((obj instanceof AnyImpl)) {
	    AnyImpl objImpl = (AnyImpl)obj;
	    typeCode = objImpl.typeCode;
	    value = objImpl.value;
	    object = objImpl.object;
            isInitialized = objImpl.isInitialized;

	    if (objImpl.stream != null)
		stream = objImpl.stream.dup();

	} else {
	    read_value(obj.create_input_stream(), obj.type());
	}
    }


    ///////////////////////////////////////////////////////////////////////////
    // basic accessors

    /**
     * returns the type of the element contained in the Any.
     *
     * @result		the TypeCode for the element in the Any
     */
   
    public TypeCode type() {
	return typeCode;
    }

    private TypeCode realType() {
        return realType(typeCode);
    }

    private TypeCode realType(TypeCode aType) {
        TypeCode realType = aType;
        try {
            // Note: Indirect types are handled in kind() method
            while (realType.kind().value() == TCKind._tk_alias) {
                realType = realType.content_type();
            }
        } catch (BadKind bad) { // impossible
        }
        return realType;
    }

/**
 * sets the type of the element to be contained in the Any.
 *
 * @param tc		the TypeCode for the element in the Any
 */
   
public void type(TypeCode tc)
{
    //debug.log ("type2");
    // set the typecode
    typeCode = TypeCodeImpl.convertToNative(orb, tc);

    stream = null;
    value = 0;
    object = null;
    // null is the only legal value this Any can have after resetting the type code
    isInitialized = (tc.kind().value() == TCKind._tk_null);
}


/**
 * checks for equality between Anys.
 *
 * @param otherAny	the Any to be compared with.
 * @result		true if the Anys are equal, false otherwise.
 */
   
public boolean equal(Any otherAny)
{
    //debug.log ("equal");

    if (otherAny == this)
        return true;

    // first check for typecode equality.
    // note that this will take aliases into account
    if (!typeCode.equal(otherAny.type()))
	return false;

    // Resolve aliases here
    TypeCode realType = realType();

    // _REVISIT_ Possible optimization for the case where
    // otherAny is a AnyImpl and the endianesses match.
    // Need implementation of CDRInputStream.equals()
    // For now we disable this to encourage testing the generic,
    // unoptimized code below.
    // Unfortunately this generic code needs to copy the whole stream
    // at least once.
//    if (AnyImpl.isStreamed[realType.kind().value()]) {
//        if (otherAny instanceof AnyImpl) {
//            return ((AnyImpl)otherAny).stream.equals(stream);
//        }
//    }

    switch (realType.kind().value()) {
        // handle primitive types
        case TCKind._tk_null:
        case TCKind._tk_void:
            return true;
        case TCKind._tk_short:
            return (extract_short() == otherAny.extract_short());
        case TCKind._tk_long:
            return (extract_long() == otherAny.extract_long());
        case TCKind._tk_ushort:
            return (extract_ushort() == otherAny.extract_ushort());
        case TCKind._tk_ulong:
            return (extract_ulong() == otherAny.extract_ulong());
        case TCKind._tk_float:
            return (extract_float() == otherAny.extract_float());
        case TCKind._tk_double:
            return (extract_double() == otherAny.extract_double());
        case TCKind._tk_boolean:
            return (extract_boolean() == otherAny.extract_boolean());
        case TCKind._tk_char:
            return (extract_char() == otherAny.extract_char());
        case TCKind._tk_wchar:
            return (extract_wchar() == otherAny.extract_wchar());
        case TCKind._tk_octet:
            return (extract_octet() == otherAny.extract_octet());
        case TCKind._tk_any:
            return extract_any().equal(otherAny.extract_any());
        case TCKind._tk_TypeCode:
            return extract_TypeCode().equal(otherAny.extract_TypeCode());
        case TCKind._tk_string:
            return extract_string().equals(otherAny.extract_string());
        case TCKind._tk_wstring:
            return (extract_wstring().equals(otherAny.extract_wstring()));
        case TCKind._tk_longlong:
            return (extract_longlong() == otherAny.extract_longlong());
        case TCKind._tk_ulonglong:
            return (extract_ulonglong() == otherAny.extract_ulonglong());

        case TCKind._tk_objref:
            return (extract_Object().equals(otherAny.extract_Object()));
        case TCKind._tk_Principal:
            return (extract_Principal().equals(otherAny.extract_Principal()));

        case TCKind._tk_enum:
            return (extract_long() == otherAny.extract_long());
        case TCKind._tk_fixed:
            return (extract_fixed().compareTo(otherAny.extract_fixed()) == 0);
        case TCKind._tk_except:
        case TCKind._tk_struct:
        case TCKind._tk_union:
        case TCKind._tk_sequence:
        case TCKind._tk_array:
            InputStream copyOfMyStream = this.create_input_stream();
            InputStream copyOfOtherStream = otherAny.create_input_stream();
            return equalMember(realType, copyOfMyStream, copyOfOtherStream);

        // Too complicated to handle value types the way we handle
        // other complex types above. Don't try to decompose it here
        // for faster comparison, just use Object.equals().
        case TCKind._tk_value:
        case TCKind._tk_value_box:
            return extract_Value().equals(otherAny.extract_Value());

        case TCKind._tk_alias:
            // error resolving alias above
            throw new org.omg.CORBA.INTERNAL();

        case TCKind._tk_longdouble:
            // Unspecified for Java
            throw new org.omg.CORBA.NO_IMPLEMENT();
        default:
            throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}

// Needed for equal() in order to achieve linear performance for complex types.
// Uses up (recursively) copies of the InputStream in both Anys that got created in equal().
private boolean equalMember(TypeCode memberType, InputStream myStream, InputStream otherStream) {
    // Resolve aliases here
    TypeCode realType = realType(memberType);

    try {
        switch (realType.kind().value()) {
            // handle primitive types
            case TCKind._tk_null:
            case TCKind._tk_void:
                return true;
            case TCKind._tk_short:
                return (myStream.read_short() == otherStream.read_short());
            case TCKind._tk_long:
                return (myStream.read_long() == otherStream.read_long());
            case TCKind._tk_ushort:
                return (myStream.read_ushort() == otherStream.read_ushort());
            case TCKind._tk_ulong:
                return (myStream.read_ulong() == otherStream.read_ulong());
            case TCKind._tk_float:
                return (myStream.read_float() == otherStream.read_float());
            case TCKind._tk_double:
                return (myStream.read_double() == otherStream.read_double());
            case TCKind._tk_boolean:
                return (myStream.read_boolean() == otherStream.read_boolean());
            case TCKind._tk_char:
                return (myStream.read_char() == otherStream.read_char());
            case TCKind._tk_wchar:
                return (myStream.read_wchar() == otherStream.read_wchar());
            case TCKind._tk_octet:
                return (myStream.read_octet() == otherStream.read_octet());
            case TCKind._tk_any:
                return myStream.read_any().equal(otherStream.read_any());
            case TCKind._tk_TypeCode:
                return myStream.read_TypeCode().equal(otherStream.read_TypeCode());
            case TCKind._tk_string:
                return myStream.read_string().equals(otherStream.read_string());
            case TCKind._tk_wstring:
                return (myStream.read_wstring().equals(otherStream.read_wstring()));
            case TCKind._tk_longlong:
                return (myStream.read_longlong() == otherStream.read_longlong());
            case TCKind._tk_ulonglong:
                return (myStream.read_ulonglong() == otherStream.read_ulonglong());

            case TCKind._tk_objref:
                return (myStream.read_Object().equals(otherStream.read_Object()));
            case TCKind._tk_Principal:
                return (myStream.read_Principal().equals(otherStream.read_Principal()));

            case TCKind._tk_enum:
                return (myStream.read_long() == otherStream.read_long());
            case TCKind._tk_fixed:
                return (myStream.read_fixed().compareTo(otherStream.read_fixed()) == 0);
            case TCKind._tk_except:
            case TCKind._tk_struct: {
                int length = realType.member_count();
                for (int i=0; i<length; i++) {
                    if ( ! equalMember(realType.member_type(i), myStream, otherStream)) {
                        return false;
                    }
                }
                return true;
            }
            case TCKind._tk_union: {
                Any myDiscriminator = orb.create_any();
                Any otherDiscriminator = orb.create_any();
                myDiscriminator.read_value(myStream, realType.discriminator_type());
                otherDiscriminator.read_value(otherStream, realType.discriminator_type());

                if ( ! myDiscriminator.equal(otherDiscriminator)) {
                    return false;
                }
                TypeCodeImpl realTypeCodeImpl = TypeCodeImpl.convertToNative(orb, realType);
                int memberIndex = realTypeCodeImpl.currentUnionMemberIndex(myDiscriminator);
                if (memberIndex == -1)
                    throw new MARSHAL();

                if ( ! equalMember(realType.member_type(memberIndex), myStream, otherStream)) {
                    return false;
                }
                return true;
            }
            case TCKind._tk_sequence: {
                int length = myStream.read_long();
                otherStream.read_long(); // just so that the two stream are in sync
                for (int i=0; i<length; i++) {
                    if ( ! equalMember(realType.content_type(), myStream, otherStream)) {
                        return false;
                    }
                }
                return true;
            }
            case TCKind._tk_array: {
                int length = realType.member_count();
                for (int i=0; i<length; i++) {
                    if ( ! equalMember(realType.content_type(), myStream, otherStream)) {
                        return false;
                    }
                }
                return true;
            }

            // Too complicated to handle value types the way we handle
            // other complex types above. Don't try to decompose it here
            // for faster comparison, just use Object.equals().
            case TCKind._tk_value:
            case TCKind._tk_value_box:
		org.omg.CORBA_2_3.portable.InputStream mine =
		    (org.omg.CORBA_2_3.portable.InputStream)myStream;
		org.omg.CORBA_2_3.portable.InputStream other =
		    (org.omg.CORBA_2_3.portable.InputStream)otherStream;
                return mine.read_value().equals(other.read_value());

            case TCKind._tk_alias:
                // error resolving alias above
                throw new org.omg.CORBA.INTERNAL();

            case TCKind._tk_longdouble:
                // Unspecified for Java
                throw new org.omg.CORBA.NO_IMPLEMENT();
            default:
                throw new org.omg.CORBA.NO_IMPLEMENT();
        }
    } catch (BadKind badKind) { // impossible
    } catch (Bounds bounds) { // impossible
    }
    return false;
}

///////////////////////////////////////////////////////////////////////////
// accessors for marshaling/unmarshaling

/**
 * returns an output stream that an Any value can be marshaled into. 
 *
 * @result		the OutputStream to marshal value of Any into
 */
   
public org.omg.CORBA.portable.OutputStream create_output_stream()
{
    //debug.log ("create_output_stream");
    return new AnyOutputStream(orb, DEFAULT_BUFFER_SIZE);
}

/**
 * returns an input stream that an Any value can be marshaled out of.
 *
 * @result		the InputStream to marshal value of Any out of.
 */

//
// We create a new InputStream so that multiple threads can call here
// and read the streams in parallel without thread safety problems.
//
public org.omg.CORBA.portable.InputStream create_input_stream()
{
    //debug.log ("create_input_stream");
    if (AnyImpl.isStreamed[realType().kind().value()]) {
	return stream.dup();
    } else {
	OutputStream os = (OutputStream)orb.create_output_stream();
	TCUtility.marshalIn(os, realType(), value, object);

	return os.create_input_stream();
    }
}

///////////////////////////////////////////////////////////////////////////
// marshaling/unmarshaling routines

//
// If the InputStream is a CDRInputStream then we can copy the bytes
// since it is in our format and does not have alignment issues.
//
public void read_value(org.omg.CORBA.portable.InputStream in, TypeCode tc)
{ 
    //debug.log ("read_value");
    //
    // Assume that someone isn't going to think they can keep reading
    // from this stream after calling us. That would be likely for
    // an IIOPInputStream but if it is an AnyInputStream then they
    // presumably obtained it via our create_output_stream() so they could
    // write the contents of an IDL data type to it and then call
    // create_input_stream() for us to read it. This is how Helper classes
    // typically implement the insert() method.
    // We should probably document this behavior in the 1.1 revision
    // task force.
    //

    typeCode = TypeCodeImpl.convertToNative(orb, tc);
    int kind = realType().kind().value();
    if (kind >= isStreamed.length) {
        throw new INTERNAL("Unknown isStreamed kind value: " + kind, 
                           MinorCodes.INVALID_ISSTREAMED_TCKIND, 
                           CompletionStatus.COMPLETED_MAYBE);
    }

    if (AnyImpl.isStreamed[kind]) {
	if ( in instanceof AnyInputStream ) {
	    // could only have been created here
	    stream = (CDRInputStream)in;
	} else {
	    org.omg.CORBA_2_3.portable.OutputStream out =
                (org.omg.CORBA_2_3.portable.OutputStream)orb.create_output_stream();
	    typeCode.copy((org.omg.CORBA_2_3.portable.InputStream)in, out);
	    stream = (CDRInputStream)out.create_input_stream();
	}
    } else {
	java.lang.Object[] objholder = new java.lang.Object[1];
	objholder[0] = object;
	long[] longholder = new long[1];
	TCUtility.unmarshalIn(in, typeCode, longholder, objholder);
	value = longholder[0];
	object = objholder[0];
	stream = null;
    }
    isInitialized = true;
}


//
// We could optimize this by noticing whether the target stream
// has ever had anything marshaled on it that required an
// alignment of greater than 4 (was write_double() ever called on it).
// If not, then we can just do a byte array copy without having to
// drive the remarshaling through typecode interpretation.
//
public void write_value(OutputStream out)
{
    //debug.log ("write_value");
    if (AnyImpl.isStreamed[realType().kind().value()]) {
	typeCode.copy(stream.dup(), out);
    } else {
        // _REVISIT_ check isInitialized whether all we write is TypeCode!
	TCUtility.marshalIn(out, realType(), value, object);
    }
}

/**
 * takes a streamable and inserts its reference into the any
 *
 * @param s		the streamable to insert
 */
public void insert_Streamable(Streamable s)
{
    //debug.log ("insert_Streamable");
    typeCode = TypeCodeImpl.convertToNative(orb, s._type());
    object = s;
    isInitialized = true;
}
 
public Streamable extract_Streamable()
{
    //debug.log( "extract_Streamable" ) ;
    return (Streamable)object;
}

///////////////////////////////////////////////////////////////////////////
// insertion/extraction/replacement for all basic types

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_short(short s)
{
    //debug.log ("insert_short");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_short);
    value = s;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public short extract_short()
{
    //debug.log ("extract_short");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_short))
	throw new BAD_OPERATION();
    return (short)value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_long(int l)
{
    //debug.log ("insert_long");
    // A long value is applicable to enums as well, so don't erase the enum type code
    // in case it was initialized that way before.
    int kind = realType().kind().value();
    if (kind != TCKind._tk_long && kind != TCKind._tk_enum) {
        typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_long);
    }
    value = l;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public int extract_long()
{
    //debug.log ("extract_long");
    int kind = realType().kind().value();
    if ( ! isInitialized) {
	throw new BAD_OPERATION();
    }
    if ( ! (kind == TCKind._tk_long || kind == TCKind._tk_enum))
        throw new BAD_OPERATION();
    return (int)value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_ushort(short s)
{
    //debug.log ("insert_ushort");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_ushort);
    value = s;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public short extract_ushort()
{
    //debug.log ("extract_ushort");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_ushort))
	throw new BAD_OPERATION();
    return (short)value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_ulong(int l)
{
    //debug.log ("insert_ulong");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_ulong);
    value = l;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public int extract_ulong()
{
    //debug.log ("extract_ulong");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_ulong))
	throw new BAD_OPERATION();
    return (int)value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_float(float f)
{
    //debug.log ("insert_float");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_float);
    value = Float.floatToIntBits(f);
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public float extract_float()
{
    //debug.log ("extract_float");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_float))
	throw new BAD_OPERATION();
    return Float.intBitsToFloat((int)value);
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_double(double d)
{
    //debug.log ("insert_double");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_double);
    value = Double.doubleToLongBits(d);
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public double extract_double()
{
    //debug.log ("extract_double");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_double))
	throw new BAD_OPERATION();
    return Double.longBitsToDouble(value);
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_longlong(long l)
{
    //debug.log ("insert_longlong");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_longlong);
    value = l;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public long extract_longlong()
{
    //debug.log ("extract_longlong");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_longlong))
	throw new BAD_OPERATION();
    return value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_ulonglong(long l)
{
    //debug.log ("insert_ulonglong");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_ulonglong);
    value = l;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public long extract_ulonglong()
{
    //debug.log ("extract_ulonglong");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_ulonglong))
	throw new BAD_OPERATION();
    return value;
}


/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_boolean(boolean b)
{
    //debug.log ("insert_boolean");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_boolean);
    value = (b)? 1:0;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public boolean extract_boolean()
{
    //debug.log ("extract_boolean");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_boolean))
	throw new BAD_OPERATION();
    return (value == 0)? false: true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_char(char c)
{
    //debug.log ("insert_char");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_char);
    value = c;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public char extract_char()
{
    //debug.log ("extract_char");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_char))
	throw new BAD_OPERATION();
    return (char)value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_wchar(char c)
{
    //debug.log ("insert_wchar");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_wchar);
    value = c;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public char extract_wchar()
{
    //debug.log ("extract_wchar");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_wchar))
	throw new BAD_OPERATION();
    return (char)value;
}


/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_octet(byte b)
{
    //debug.log ("insert_octet");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_octet);
    value = b;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public byte extract_octet()
{
    //debug.log ("extract_octet");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_octet))
	throw new BAD_OPERATION();
    return (byte)value;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_string(String s)
{
    //debug.log ("insert_string");
    // Make sure type code information for bounded strings is not erased
    if (typeCode.kind() == TCKind.tk_string) {
        int length = 0;
        try { length = typeCode.length(); } catch (BadKind bad) {}
        // Check if bounded strings length is not exceeded
        if (length != 0 && s != null && s.length() > length) {
            throw new DATA_CONVERSION();
        }
    } else {
        typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_string);
    }
    object = s;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public String extract_string()
{
    //debug.log ("extract_string");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_string))
	throw new BAD_OPERATION();
    return (String)object;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_wstring(String s)
{
    //debug.log ("insert_wstring");
    // Make sure type code information for bounded strings is not erased
    if (typeCode.kind() == TCKind.tk_wstring) {
        int length = 0;
        try { length = typeCode.length(); } catch (BadKind bad) {}
        // Check if bounded strings length is not exceeded
        if (length != 0 && s != null && s.length() > length) {
            throw new DATA_CONVERSION();
        }
    } else {
        typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_wstring);
    }
    object = s;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public String extract_wstring()
{
    //debug.log ("extract_wstring");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_wstring))
	throw new BAD_OPERATION();
    return (String)object;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_any(Any a)
{
    //debug.log ("insert_any");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_any);
    object = a;
    stream = null;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public Any extract_any()
{
    //debug.log ("extract_any");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_any))
	throw new BAD_OPERATION();
    return (Any)object;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_Object(org.omg.CORBA.Object o)
{
    //debug.log ("insert_Object");
    if ( o == null ) {
	typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_objref);
    } else {
        if (o instanceof ObjectImpl) {
            ObjectImpl objImpl = (ObjectImpl)o;
            String[] ids = objImpl._ids();
            typeCode = new TypeCodeImpl(orb, TCKind._tk_objref, ids[0], "");
        } else {
            throw new BAD_PARAM("Attempted to insert non-ObjectImpl "
                                + o.getClass().getName()
                                + " into an Any",
                                MinorCodes.BAD_INSERTOBJ_PARAM,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }
    
    object = o;
    isInitialized = true;
}

/**
 * A variant of the insertion operation that takes a typecode
 * argument as well.
 */

public void insert_Object(org.omg.CORBA.Object o, TypeCode tc)
{
    //debug.log ("insert_Object2");
    try {
	if ( tc.id().equals("IDL:omg.org/CORBA/Object:1.0") || o._is_a(tc.id()) )
	    {
		typeCode = TypeCodeImpl.convertToNative(orb, tc);
		object = o;
	    }
	else {
	    throw new BAD_OPERATION();
	}
    } catch ( Exception ex ) {
	throw new BAD_OPERATION();
    }
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public org.omg.CORBA.Object extract_Object()
{
    //debug.log ("extract_Object");
    if (!isInitialized)
	throw new BAD_OPERATION("Invalid operation on uninitialized Any");

    // Check if the object contained here is of the type in typeCode
    org.omg.CORBA.Object obj = null;
    try {
	obj = (org.omg.CORBA.Object) object;
	if (typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") || obj._is_a(typeCode.id())) {
	    return obj;
	} else {
	    throw new BAD_OPERATION();
	}
    } catch ( Exception ex ) {
	throw new BAD_OPERATION();
    }
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public void insert_TypeCode(TypeCode tc)
{
    //debug.log ("insert_TypeCode");
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_TypeCode);
    object = tc;
    isInitialized = true;
}

/**
 * See the description of the <a href="#anyOps">general Any operations.</a>
 */

public TypeCode extract_TypeCode()
{
    //debug.log ("extract_TypeCode");
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_TypeCode))
	throw new BAD_OPERATION();
    return (TypeCode)object;
}

/**
 * @deprecated
 */

public void insert_Principal(Principal p)
{
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_Principal);
    object = p;
    isInitialized = true;
}

/**
 * @deprecated
 */

public Principal extract_Principal()
{
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_Principal))
	throw new BAD_OPERATION();
    return (Principal)object;
}

/**
 * Note that the Serializable really should be an IDLEntity of
 * some kind.  It shouldn't just be an RMI-IIOP type.  Currently,
 * we accept and will produce RMI repIds with the latest
 * calculations if given a non-IDLEntity Serializable.
 */
public Serializable extract_Value() throws org.omg.CORBA.BAD_OPERATION
{
    //debug.log ("extract_Value");

    if (!isInitialized)
	throw new BAD_OPERATION("Invalid operation on uninitialized Any");

    int kind = realType().kind().value();
    if (kind != TCKind._tk_value && kind != TCKind._tk_value_box && kind != TCKind._tk_abstract_interface)
	throw new BAD_OPERATION();

    return (Serializable)object;
}

public void insert_Value(Serializable v)
{
    //debug.log ("insert_Value");
    object = v;

    TypeCode tc;

    if ( v == null ) {
	tc = orb.get_primitive_tc (TCKind.tk_value);
    } else {
        // See note in getPrimitiveTypeCodeForClass.  We
        // have to use the latest type code fixes in this
        // case since there is no way to know what ORB will
        // actually send this Any.  In RMI-IIOP, when using
        // Util.writeAny, we can do the versioning correctly,
        // and use the insert_Value(Serializable, TypeCode)
        // method.
        //
        // The ORB singleton uses the latest version.
	tc = createTypeCodeForClass (v.getClass(), 
                                     (com.sun.corba.se.internal.corba.ORB)ORB.init());
    }

    typeCode = TypeCodeImpl.convertToNative(orb, tc);
    isInitialized = true;
}
  
public void insert_Value(Serializable v, org.omg.CORBA.TypeCode t)
    throws org.omg.CORBA.MARSHAL
{
    //debug.log ("insert_Value2");
    object = v;
    typeCode = TypeCodeImpl.convertToNative(orb, t);
    isInitialized = true;
}

public void insert_fixed(java.math.BigDecimal value) {
    typeCode = TypeCodeImpl.convertToNative(orb,
        orb.create_fixed_tc(TypeCodeImpl.digits(value), TypeCodeImpl.scale(value)));
    object = value;
    isInitialized = true;
}

public void insert_fixed(java.math.BigDecimal value, org.omg.CORBA.TypeCode type)
    throws org.omg.CORBA.BAD_INV_ORDER
{
    try {
        if (TypeCodeImpl.digits(value) > type.fixed_digits() ||
            TypeCodeImpl.scale(value) > type.fixed_scale())
        {
            // type and value don't match
            throw new BAD_INV_ORDER();
        }
    } catch (org.omg.CORBA.TypeCodePackage.BadKind bk) {
        // type isn't even of kind fixed
        throw new BAD_INV_ORDER();
    }
    typeCode = TypeCodeImpl.convertToNative(orb, type);
    object = value;
    isInitialized = true;
}

public java.math.BigDecimal extract_fixed() {
    if (!isInitialized || !(realType().kind().value() == TCKind._tk_fixed))
	throw new BAD_OPERATION();
    return (BigDecimal)object;
}

/**
 * Utility method for insert_Value and Util.writeAny.
 *
 * The ORB passed in should have the desired ORBVersion.  It
 * is used to generate the type codes.
 */
public TypeCode createTypeCodeForClass (java.lang.Class c,
                                        com.sun.corba.se.internal.corba.ORB tcORB)
{
    // Look in the cache first
    TypeCodeImpl classTC = tcORB.getTypeCodeForClass(c);
    if (classTC != null)
        return classTC;

    // All cases need to be able to create repository IDs.
    //
    // See bug 4391648 for more info about the tcORB in this
    // case.
    RepositoryIdStrings repStrs 
        = RepositoryIdFactory.getRepIdStringsFactory(tcORB);


    // Assertion: c instanceof Serializable?

    if ( c.isArray() ) {
	// Arrays - may recurse for multi-dimensional arrays
	Class componentClass = c.getComponentType();
	TypeCode embeddedType;
	if ( componentClass.isPrimitive() ) {
	    embeddedType = getPrimitiveTypeCodeForClass(componentClass,
                                                        tcORB);
	} else {
	    embeddedType = createTypeCodeForClass (componentClass,
                                                   tcORB);
	}
	TypeCode t = tcORB.create_sequence_tc (0, embeddedType);

        String id = repStrs.createForJavaType(c);

	return tcORB.create_value_box_tc (id, "Sequence", t);
    } else if ( c == java.lang.String.class ) {
	// Strings
	TypeCode t = tcORB.create_string_tc (0);

        String id = repStrs.createForJavaType(c);

	return tcORB.create_value_box_tc (id, "StringValue", t);
    }

    // Anything else
    // We know that this is a TypeCodeImpl since it is our ORB
    classTC = (TypeCodeImpl)ValueUtility.createTypeCodeForClass(
        tcORB, c, ORBUtility.createValueHandler(tcORB));
    // Intruct classTC to store its buffer
    classTC.setCaching(true);
    // Update the cache
    tcORB.setTypeCodeForClass(c, classTC);
    return classTC;
}

/**
 * It looks like this was copied from io.ValueUtility at some
 * point.
 *
 * It's used by createTypeCodeForClass.  The tcORB passed in
 * should have the desired ORB version, and is used to
 * create the type codes.
 */
private TypeCode getPrimitiveTypeCodeForClass (Class c,
                                               com.sun.corba.se.internal.corba.ORB tcORB)
{
    //debug.log ("getPrimitiveTypeCodeForClass");

    if (c == Integer.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_long);
    } else if (c == Byte.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_octet);
    } else if (c == Long.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_longlong);
    } else if (c == Float.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_float);
    } else if (c == Double.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_double);
    } else if (c == Short.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_short);
    } else if (c == Character.TYPE) {
        // For Merlin or later JDKs, or for foreign ORBs,
        // we correctly say that a Java char maps to a
        // CORBA wchar.  For backwards compatibility
        // with our older ORBs, we say it maps to a
        // CORBA char.  This is only used in RMI-IIOP
        // in our javax.rmi.CORBA.Util delegate's
        // writeAny method.  In Java IDL, there's no way
        // to know the ORB version that the Any will be
        // sent out with -- it could be different than
        // the one used to create the Any -- so we use the
        // most recent version (see insert_Value).
        if (ORBVersionImpl.FOREIGN.compareTo(tcORB.getORBVersion()) == 0 ||
            ORBVersionImpl.NEWER.compareTo(tcORB.getORBVersion()) <= 0)
            return tcORB.get_primitive_tc(TCKind.tk_wchar);
        else
            return tcORB.get_primitive_tc(TCKind.tk_char);
    } else if (c == Boolean.TYPE) {
	return tcORB.get_primitive_tc (TCKind.tk_boolean);
    } else {
	// _REVISIT_ Not sure if this is right.
	return tcORB.get_primitive_tc (TCKind.tk_any);
    }
}

/*
    public java.lang.Object clone()
        throws CloneNotSupportedException
    {
        // Do we want to depend on this constructor?
        //return new AnyImpl(orb, this);
        AnyImpl clone = (AnyImpl)super.clone();
        if (stream != null) {
            clone.stream = stream.dup();
        }
        return clone;
    }
*/

    // Extracts a member value according to the given TypeCode from the given complex Any
    // (at the Anys current internal stream position, consuming the anys stream on the way)
    // and returns it wrapped into a new Any
    public Any extractAny(TypeCode memberType, org.omg.CORBA.ORB orb) {
        Any returnValue = orb.create_any();
        OutputStream out = returnValue.create_output_stream();
        TypeCodeImpl.convertToNative(orb, memberType).copy((InputStream)stream, out);
        returnValue.read_value(out.create_input_stream(), memberType);
        return returnValue;
    }
/*
    public Any extractAny(TypeCode memberType, org.omg.CORBA.ORB orb) {
        // Resolve aliases here
        TypeCode realType = realType(memberType);
        Any returnValue = orb.create_any();
        OutputStream out;
        int length;

        try {
            switch (realType.kind().value()) {
                case TCKind._tk_boolean:
                    returnValue.insert_boolean(stream.read_boolean());
                    break;
                case TCKind._tk_char:
                    returnValue.insert_char(stream.read_char());
                    break;
                case TCKind._tk_wchar:
                    returnValue.insert_wchar(stream.read_wchar());
                    break;
                case TCKind._tk_octet:
                    returnValue.insert_octet(stream.read_octet());
                    break;
                case TCKind._tk_short:
                    returnValue.insert_short(stream.read_short());
                    break;
                case TCKind._tk_enum:
                    // Set the type first and then fall through to long
                    returnValue.type(realType());
                case TCKind._tk_long:
                    returnValue.insert_long(stream.read_long());
                    break;
                case TCKind._tk_ushort:
                    returnValue.insert_ushort(stream.read_ushort());
                    break;
                case TCKind._tk_ulong:
                    returnValue.insert_ulong(stream.read_ulong());
                    break;
                case TCKind._tk_longlong:
                    returnValue.insert_longlong(stream.read_longlong());
                    break;
                case TCKind._tk_ulonglong:
                    returnValue.insert_ulonglong(stream.read_ulonglong());
                    break;
                case TCKind._tk_float:
                    returnValue.insert_float(stream.read_float());
                    break;
                case TCKind._tk_double:
                    returnValue.insert_double(stream.read_double());
                    break;
                case TCKind._tk_string:
                    returnValue.insert_string(stream.read_string());
                    break;
                case TCKind._tk_wstring:
                    returnValue.insert_wstring(stream.read_wstring());
                    break;
                case TCKind._tk_objref:
                    returnValue.insert_Object(stream.read_Object());
                    break;
                case TCKind._tk_any:
                    returnValue.insert_any(stream.read_any());
                    break;
                case TCKind._tk_TypeCode:
                    returnValue.insert_TypeCode(stream.read_TypeCode());
                    break;
                case TCKind._tk_Principal:
                    returnValue.insert_Principal(stream.read_Principal());
                    break;
                case TCKind._tk_fixed:
                    returnValue.insert_fixed(stream.read_fixed());
                    break;
                case TCKind._tk_value:
                case TCKind._tk_value_box:
                    returnValue.insert_Value(stream.read_Value());
                    break;

                // _REVISIT_ Room for optimization. Lots of intermediary Anys created
                // for all complex types here.
                case TCKind._tk_struct:
                case TCKind._tk_array:
                case TCKind._tk_except:
                    out = returnValue.create_output_stream();
                    // Copy all the members
                    length = realType.member_count();
                    for (int i=0; i<length; i++) {
                        extractAny(realType.member_type(i), orb).write_value(out);
                    }
                    returnValue.read_value(out.create_input_stream(), realType);
                    break;
                case TCKind._tk_sequence:
                    out = returnValue.create_output_stream();
                    // Copy the length and all the members
                    length = stream.read_long();
                    for (int i=0; i<length; i++) {
                        extractAny(realType.member_type(i), orb).write_value(out);
                    }
                    returnValue.read_value(out.create_input_stream(), realType);
                    break;
                case TCKind._tk_union:
                    out = returnValue.create_output_stream();
                    // Copy discriminator and value
                    Any discriminator = extractAny(realType.discriminator_type(), orb);
                    TypeCodeImpl realTypeCodeImpl = TypeCodeImpl.convertToNative(orb, realType);
                    int memberIndex = realTypeCodeImpl.currentUnionMemberIndex(discriminator);
                    if (memberIndex == -1)
                        throw new MARSHAL();
                    Any unionValue = extractAny(realType.member_type(memberIndex), orb);
                    unionValue.write_value(out);
                    discriminator.write_value(out);
                    returnValue.read_value(out.create_input_stream(), realType);
                    break;

                case TCKind._tk_alias:
                    // error resolving alias above
                    //return extractAny(realType.content_type(), orb);
                    throw new org.omg.CORBA.INTERNAL();
                case TCKind._tk_null:
                case TCKind._tk_void:
                case TCKind._tk_native:
                case TCKind._tk_abstract_interface:
                    // These Anys don't have values, just a TypeCode
                    returnValue.type(realType);
                    break;
                case TCKind._tk_longdouble:
                    // Unspecified for Java
                    throw new org.omg.CORBA.NO_IMPLEMENT();
                default:
                    throw new org.omg.CORBA.BAD_PARAM();
            }
        } catch (BadKind badKind) {
        } catch (Bounds bounds) {
        }
        return returnValue;
    }
*/

    // This method could very well be moved into TypeCodeImpl or a common utility class,
    // but is has to be in this package.
    static public Any extractAnyFromStream(TypeCode memberType, InputStream input, org.omg.CORBA.ORB orb) {
        Any returnValue = orb.create_any();
        OutputStream out = returnValue.create_output_stream();
        TypeCodeImpl.convertToNative(orb, memberType).copy(input, out);
        returnValue.read_value(out.create_input_stream(), memberType);
        return returnValue;
    }

    // There is no other way for DynAnys to find out whether the Any is initialized.
    public boolean isInitialized() {
        return isInitialized;
    }
}

