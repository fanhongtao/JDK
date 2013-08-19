/*
 * @(#)TypeCodeImpl.java	1.91 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;

import org.omg.CORBA.*;
import org.omg.CORBA.TypeCodePackage.*;

import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.iiop.CDROutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

class WrapperInputStream extends org.omg.CORBA_2_3.portable.InputStream implements TypeCodeReader
{
    private CDRInputStream stream;
    private Map typeMap = null;
    private int startPos = 0;

    public WrapperInputStream(CDRInputStream s) {
        super();
        stream = s;
        startPos = stream.getPosition();
    }

    public int read() throws IOException { return stream.read(); }
    public int read(byte b[]) throws IOException { return stream.read(b); }
    public int read(byte b[], int off, int len) throws IOException {
	return stream.read(b, off, len);
    }
    public long skip(long n) throws IOException { return stream.skip(n); }
    public int available() throws IOException { return stream.available(); }
    public void close() throws IOException { stream.close(); }
    public void mark(int readlimit) { stream.mark(readlimit); }
    public void reset() { stream.reset(); }
    public boolean markSupported() { return stream.markSupported(); }
    public int getPosition() { return stream.getPosition(); }
    public void consumeEndian() { stream.consumeEndian(); }
    public boolean read_boolean() { return stream.read_boolean(); }
    public char read_char() { return stream.read_char(); }
    public char read_wchar() { return stream.read_wchar(); }
    public byte read_octet() { return stream.read_octet(); }
    public short read_short() { return stream.read_short(); }
    public short read_ushort() { return stream.read_ushort(); }
    public int read_long() { return stream.read_long(); }
    public int read_ulong() { return stream.read_ulong(); }
    public long read_longlong() { return stream.read_longlong(); }
    public long read_ulonglong() { return stream.read_ulonglong(); }
    public float read_float() { return stream.read_float(); }
    public double read_double() { return stream.read_double(); }
    public String read_string() { return stream.read_string(); }
    public String read_wstring() { return stream.read_wstring(); }

    public void read_boolean_array(boolean[] value, int offset, int length) {
	stream.read_boolean_array(value, offset, length);
    }
    public void read_char_array(char[] value, int offset, int length) {
	stream.read_char_array(value, offset, length);
    }
    public void read_wchar_array(char[] value, int offset, int length) {
	stream.read_wchar_array(value, offset, length);
    }
    public void read_octet_array(byte[] value, int offset, int length) {
	stream.read_octet_array(value, offset, length);
    }
    public void read_short_array(short[] value, int offset, int length) {
	stream.read_short_array(value, offset, length);
    }
    public void read_ushort_array(short[] value, int offset, int length) {
	stream.read_ushort_array(value, offset, length);
    }
    public void read_long_array(int[] value, int offset, int length) {
	stream.read_long_array(value, offset, length);
    }
    public void read_ulong_array(int[] value, int offset, int length) {
	stream.read_ulong_array(value, offset, length);
    }
    public void read_longlong_array(long[] value, int offset, int length) {
	stream.read_longlong_array(value, offset, length);
    }
    public void read_ulonglong_array(long[] value, int offset, int length) {
	stream.read_ulonglong_array(value, offset, length);
    }
    public void read_float_array(float[] value, int offset, int length) {
	stream.read_float_array(value, offset, length);
    }
    public void read_double_array(double[] value, int offset, int length) {
	stream.read_double_array(value, offset, length);
    }

    public org.omg.CORBA.Object read_Object() { return stream.read_Object(); }
    public java.io.Serializable read_value() {return stream.read_value();}
    public TypeCode read_TypeCode() { return stream.read_TypeCode(); }
    public Any read_any() { return stream.read_any(); }
    public Principal read_Principal() { return stream.read_Principal(); }
    public java.math.BigDecimal read_fixed() { return stream.read_fixed(); }
    public org.omg.CORBA.Context read_Context() { return stream.read_Context(); }

    public org.omg.CORBA.ORB orb() { return stream.orb(); }

    public void addTypeCodeAtPosition(TypeCodeImpl tc, int position) {
        if (typeMap == null) {
            //if (TypeCodeImpl.debug) System.out.println("Creating typeMap");
            typeMap = new HashMap(16);
        }
        //if (TypeCodeImpl.debug) System.out.println(this + " adding tc " + tc + " at position " + position);
        typeMap.put(new Integer(position), tc);
    }

    public TypeCodeImpl getTypeCodeAtPosition(int position) {
        if (typeMap == null)
	    return null;
        //if (TypeCodeImpl.debug) System.out.println("Getting tc " + (TypeCodeImpl)typeMap.get(new Integer(position)) +
            //" at position " + position);
        return (TypeCodeImpl)typeMap.get(new Integer(position));
    }

    public void setEnclosingInputStream(InputStream enclosure) {
        // WrapperInputStream has no enclosure
    }

    public TypeCodeReader getTopLevelStream() {
        // WrapperInputStream has no enclosure
        return this;
    }

    public int getTopLevelPosition() {
        //if (TypeCodeImpl.debug) System.out.println("WrapperInputStream.getTopLevelPosition " +
            //"returning getPosition " + getPosition() + " - startPos " + startPos +
            //" = " + (getPosition() - startPos));
        return getPosition() - startPos;
    }

    public void performORBVersionSpecificInit() {
        // This is never actually called on a WrapperInputStream, but
        // exists to satisfy the interface requirement.
        stream.performORBVersionSpecificInit();
    }

    public void resetCodeSetConverters() {
        stream.resetCodeSetConverters();
    }

    //public void printBuffer() { stream.printBuffer(); }

    public void printTypeMap() {
        System.out.println("typeMap = {");
        List sortedKeys = new ArrayList(typeMap.keySet());
        Collections.sort(sortedKeys);
        Iterator i = sortedKeys.iterator();
        while (i.hasNext()) {
            Integer pos = (Integer)i.next();
            TypeCodeImpl tci = (TypeCodeImpl)typeMap.get(pos);
            System.out.println("  key = " + pos.intValue() + ", value = " + tci.description());
        }
        System.out.println("}");
    }
}

interface TypeCodeReader extends MarshalInputStream {
    public void addTypeCodeAtPosition(TypeCodeImpl tc, int position);
    public TypeCodeImpl getTypeCodeAtPosition(int position);
    public void setEnclosingInputStream(InputStream enclosure);
    public TypeCodeReader getTopLevelStream();
    public int getTopLevelPosition();
    // for debugging
    //public void printBuffer();
    public int getPosition();
    public void printTypeMap();
}

class TypeCodeInputStream extends EncapsInputStream implements TypeCodeReader
{
    private Map typeMap = null;
    private InputStream enclosure = null;
    private boolean isEncapsulation = false;

    public TypeCodeInputStream(org.omg.CORBA.ORB orb, byte[] data, int size) {
        super(orb, data, size);
    }

    public TypeCodeInputStream(org.omg.CORBA.ORB orb, byte[] data, int size,
                               boolean littleEndian) {
        super(orb, data, size, littleEndian);
    }

    public void addTypeCodeAtPosition(TypeCodeImpl tc, int position) {
        if (typeMap == null) {
            //if (TypeCodeImpl.debug) System.out.println("Creating typeMap");
            typeMap = new HashMap(16);
        }
        //if (TypeCodeImpl.debug) System.out.println(this + " adding tc " + tc + " at position " + position);
        typeMap.put(new Integer(position), tc);
    }

    public TypeCodeImpl getTypeCodeAtPosition(int position) {
        if (typeMap == null)
	    return null;
        //if (TypeCodeImpl.debug) {
            //System.out.println("Getting tc " + (TypeCode)typeMap.get(new Integer(position)) +
                               //" at position " + position);
        //}
        return (TypeCodeImpl)typeMap.get(new Integer(position));
    }

    public void setEnclosingInputStream(InputStream enclosure) {
        this.enclosure = enclosure;
    }

    public TypeCodeReader getTopLevelStream() {
        if (enclosure == null)
            return this;
        if (enclosure instanceof TypeCodeReader)
            return ((TypeCodeReader)enclosure).getTopLevelStream();
        return this;
    }

    public int getTopLevelPosition() {
        if (enclosure != null && enclosure instanceof TypeCodeReader) {
            // The enclosed stream has to consider if the enclosing stream
            // had to read the enclosed stream completely when creating it.
            // This is why the size of the enclosed stream needs to be substracted.
            int topPos = ((TypeCodeReader)enclosure).getTopLevelPosition();
            // Substract getBufferLength from the parents pos because it read this stream
            // from its own when creating it
            int pos = topPos - getBufferLength() + getPosition();
            //if (TypeCodeImpl.debug) {
                //System.out.println("TypeCodeInputStream.getTopLevelPosition using getTopLevelPosition " + topPos +
                    //(isEncapsulation ? " - encaps length 4" : "") +
                    //" - getBufferLength() " + getBufferLength() +
                    //" + getPosition() " + getPosition() + " = " + pos);
            //}
            return pos;
        }
        //if (TypeCodeImpl.debug) {
            //System.out.println("TypeCodeInputStream.getTopLevelPosition returning getPosition() = " +
                               //getPosition() + " because enclosure is " + enclosure);
        //}
        return getPosition();
    }

    public static TypeCodeInputStream readEncapsulation(InputStream is, org.omg.CORBA.ORB _orb) {
	// _REVISIT_ Would be nice if we didn't have to copy the buffer!
	TypeCodeInputStream encap;

        int encapLength = is.read_long();

        // read off part of the buffer corresponding to the encapsulation
	byte[] encapBuffer = new byte[encapLength];
	is.read_octet_array(encapBuffer, 0, encapBuffer.length);

	// create an encapsulation using the marshal buffer
        if (is instanceof CDRInputStream) {
            encap = new TypeCodeInputStream(_orb, encapBuffer, encapBuffer.length,
                                            ((CDRInputStream)is).isLittleEndian());
        } else {
            encap = new TypeCodeInputStream(_orb, encapBuffer, encapBuffer.length);
        }
	encap.setEnclosingInputStream(is);
        encap.makeEncapsulation();
        //if (TypeCodeImpl.debug) {
            //System.out.println("Created TypeCodeInputStream " + encap + " with parent " + is);
            //encap.printBuffer();
        //}
	return encap;
    }

    protected void makeEncapsulation() {
        // first entry in an encapsulation is the endianess
        consumeEndian();
        isEncapsulation = true;
    }

    public void printTypeMap() {
        System.out.println("typeMap = {");
        Iterator i = typeMap.keySet().iterator();
        while (i.hasNext()) {
            Integer pos = (Integer)i.next();
            TypeCodeImpl tci = (TypeCodeImpl)typeMap.get(pos);
            System.out.println("  key = " + pos.intValue() + ", value = " + tci.description());
        }
        System.out.println("}");
    }
}

final class TypeCodeOutputStream extends EncapsOutputStream {

    private OutputStream enclosure = null;
    private Map typeMap = null;
    private boolean isEncapsulation = false;

    public TypeCodeOutputStream(org.omg.CORBA.ORB orb) {
        super(orb, false);
    }

    public TypeCodeOutputStream(org.omg.CORBA.ORB orb, boolean littleEndian) {
        super(orb, littleEndian);
    }

    public org.omg.CORBA.portable.InputStream create_input_stream()
    {
        //return new TypeCodeInputStream(orb(), getByteBuffer(), getIndex(), isLittleEndian());
        TypeCodeInputStream tcis = new TypeCodeInputStream(orb(), getByteBuffer(), getIndex(), isLittleEndian());
        //if (TypeCodeImpl.debug) {
            //System.out.println("Created TypeCodeInputStream " + tcis + " with no parent");
            //tcis.printBuffer();
        //}
        return tcis;
    }

    public void setEnclosingOutputStream(OutputStream enclosure) {
        this.enclosure = enclosure;
    }

    /*
      public boolean isEncapsulatedIn(TypeCodeOutputStream outerEnclosure) {
      if (outerEnclosure == this)
      return true;
      if (enclosure == null)
      return false;
      if (enclosure instanceof TypeCodeOutputStream)
      return ((TypeCodeOutputStream)enclosure).isEncapsulatedIn(outerEnclosure);
      // Last chance! Recursion ends with first non TypeCodeOutputStream.
      return (enclosure == outerEnclosure);
      }
    */

    public TypeCodeOutputStream getTopLevelStream() {
        if (enclosure == null)
            return this;
        if (enclosure instanceof TypeCodeOutputStream)
            return ((TypeCodeOutputStream)enclosure).getTopLevelStream();
        return this;
    }

    public int getTopLevelPosition() {
        if (enclosure != null && enclosure instanceof TypeCodeOutputStream) {
            int pos = ((TypeCodeOutputStream)enclosure).getTopLevelPosition() + getPosition();
            // Add four bytes for the encaps length, not another 4 for the byte order
            // which is included in getPosition().
            if (isEncapsulation) pos += 4;
            //if (TypeCodeImpl.debug) {
                //System.out.println("TypeCodeOutputStream.getTopLevelPosition using getTopLevelPosition " +
                    //((TypeCodeOutputStream)enclosure).getTopLevelPosition() +
                    //" + getPosition() " + getPosition() +
                    //(isEncapsulation ? " + encaps length 4" : "") +
                    //" = " + pos);
            //}
            return pos;
        }
        //if (TypeCodeImpl.debug) {
            //System.out.println("TypeCodeOutputStream.getTopLevelPosition returning getPosition() = " +
                               //getPosition() + ", enclosure is " + enclosure);
        //}
        return getPosition();
    }

    public void addIDAtPosition(String id, int position) {
        if (typeMap == null)
            typeMap = new HashMap(16);
        //if (TypeCodeImpl.debug) System.out.println(this + " adding id " + id + " at position " + position);
        typeMap.put(id, new Integer(position));
    }

    public int getPositionForID(String id) {
        if (typeMap == null)
	    throw new MARSHAL("Referenced type of indirect type not marshaled!");
        //if (TypeCodeImpl.debug) System.out.println("Getting position " + ((Integer)typeMap.get(id)).intValue() +
            //" for id " + id);
        return ((Integer)typeMap.get(id)).intValue();
    }

    public void writeRawBuffer(org.omg.CORBA.portable.OutputStream s, int firstLong) {
        // Writes this streams buffer to the given OutputStream
        // without byte order flag and length as is the case for encapsulations.

        // Make sure to align s to 4 byte boundaries.
        // Unfortunately we can't do just this:
        // s.alignAndReserve(4, 4);
        // So we have to take the first four bytes given in firstLong and write them
        // with a call to write_long which will trigger the alignment.
        // Then write the rest of the byte array.

        //if (TypeCodeImpl.debug) {
            //System.out.println(this + ".writeRawBuffer(" + s + ", " + firstLong + ")");
            //if (s instanceof CDROutputStream) {
                //System.out.println("Parent position before writing kind = " + ((CDROutputStream)s).getIndex());
            //}
        //}
        s.write_long(firstLong);
        //if (TypeCodeImpl.debug) {
            //if (s instanceof CDROutputStream) {
                //System.out.println("Parent position after writing kind = " + ((CDROutputStream)s).getIndex());
            //}
        //}
        s.write_octet_array(getByteBuffer(), 4, getIndex() - 4);
        //if (TypeCodeImpl.debug) {
            //if (s instanceof CDROutputStream) {
                //System.out.println("Parent position after writing all " + getIndex() + " bytes = " + ((CDROutputStream)s).getIndex());
            //}
        //}
    }

    public TypeCodeOutputStream createEncapsulation(org.omg.CORBA.ORB _orb) {
	TypeCodeOutputStream encap = new TypeCodeOutputStream(_orb, isLittleEndian());
	encap.setEnclosingOutputStream(this);
        encap.makeEncapsulation();
        //if (TypeCodeImpl.debug) System.out.println("Created TypeCodeOutputStream " + encap + " with parent " + this);
	return encap;
    }

    protected void makeEncapsulation() {
        // first entry in an encapsulation is the endianess
        putEndian();
        isEncapsulation = true;
    }

    public static TypeCodeOutputStream wrapOutputStream(OutputStream os) {
        boolean littleEndian = ((os instanceof CDROutputStream) ? ((CDROutputStream)os).isLittleEndian() : false);
        TypeCodeOutputStream wrapper = new TypeCodeOutputStream(os.orb(), littleEndian);
        wrapper.setEnclosingOutputStream(os);
        //if (TypeCodeImpl.debug) System.out.println("Created TypeCodeOutputStream " + wrapper + " with parent " + os);
        return wrapper;
    }

    public int getPosition() {
        return getIndex();
    }

    public int getRealIndex(int index) {
        int topPos = getTopLevelPosition();
        //if (TypeCodeImpl.debug) System.out.println("TypeCodeOutputStream.getRealIndex using getTopLevelPosition " +
            //topPos + " instead of getPosition " + getPosition());
	return topPos;
    }
/*
    protected void printBuffer() {
        super.printBuffer();
    }
*/
    byte[] getTypeCodeBuffer() {
        // Returns the buffer trimmed of the trailing zeros and without the
        // known _kind value at the beginning.
        byte[] theBuffer = getByteBuffer();
        //System.out.println("outBuffer length = " + (getIndex() - 4));
        byte[] tcBuffer = new byte[getIndex() - 4];
        System.arraycopy(theBuffer, 4, tcBuffer, 0, getIndex() - 4);
        return tcBuffer;
    }

    public void printTypeMap() {
        System.out.println("typeMap = {");
        Iterator i = typeMap.keySet().iterator();
        while (i.hasNext()) {
            String id = (String)i.next();
            Integer pos = (Integer)typeMap.get(id);
            System.out.println("  key = " + id + ", value = " + pos);
        }
        System.out.println("}");
    }
}

// no chance of subclasses, so no problems with runtime helper lookup
public final class TypeCodeImpl extends TypeCode 
{
    //static final boolean debug = false;

    // the predefined typecode constants

    private static final TypeCodeImpl primitiveConstants[] = {
	new TypeCodeImpl(TCKind._tk_null),			// tk_null      
	new TypeCodeImpl(TCKind._tk_void),			// tk_void      
	new TypeCodeImpl(TCKind._tk_short),			// tk_short     
	new TypeCodeImpl(TCKind._tk_long),			// tk_long      
	new TypeCodeImpl(TCKind._tk_ushort),		// tk_ushort    
	new TypeCodeImpl(TCKind._tk_ulong),			// tk_ulong     
	new TypeCodeImpl(TCKind._tk_float),			// tk_float     
	new TypeCodeImpl(TCKind._tk_double),		// tk_double    
	new TypeCodeImpl(TCKind._tk_boolean),		// tk_boolean   
	new TypeCodeImpl(TCKind._tk_char),			// tk_char      
	new TypeCodeImpl(TCKind._tk_octet),			// tk_octet     
	new TypeCodeImpl(TCKind._tk_any),			// tk_any       
	new TypeCodeImpl(TCKind._tk_TypeCode),		// tk_typecode  
	new TypeCodeImpl(TCKind._tk_Principal),		// tk_principal 
	new TypeCodeImpl(TCKind._tk_objref),		// tk_objref    
	null,						// tk_struct    
	null,						// tk_union     
	null,						// tk_enum      
	new TypeCodeImpl(TCKind._tk_string),		// tk_string    
	null,						// tk_sequence  
	null,						// tk_array     
	null,						// tk_alias     
	null,						// tk_except    
	new TypeCodeImpl(TCKind._tk_longlong),		// tk_longlong  
	new TypeCodeImpl(TCKind._tk_ulonglong),		// tk_ulonglong 
	new TypeCodeImpl(TCKind._tk_longdouble),		// tk_longdouble
	new TypeCodeImpl(TCKind._tk_wchar),			// tk_wchar     
	new TypeCodeImpl(TCKind._tk_wstring),		// tk_wstring
	new TypeCodeImpl(TCKind._tk_fixed),         // tk_fixed
	new TypeCodeImpl(TCKind._tk_value),			// tk_value
	new TypeCodeImpl(TCKind._tk_value_box),		// tk_value_box
	new TypeCodeImpl(TCKind._tk_native),		// tk_native
	new TypeCodeImpl(TCKind._tk_abstract_interface)	// tk_abstract_interface   
    };

    // the indirection TCKind, needed for recursive typecodes. 
    protected static final int tk_indirect = 0xFFFFFFFF;
  
    // typecode encodings have three different categories that determine
    // how the encoding should be done.
  
    private static final int EMPTY = 0;	// no parameters
    private static final int SIMPLE = 1;	// simple parameters.
    private static final int COMPLEX = 2; // complex parameters. need to
    // use CDR encapsulation for
    // parameters 
  
    // a table storing the encoding category for the various typecodes.
  
    private static final int typeTable[] = {
	EMPTY,	// tk_null      
	EMPTY,	// tk_void      
	EMPTY,	// tk_short     
	EMPTY,	// tk_long      
	EMPTY,	// tk_ushort    
	EMPTY,	// tk_ulong     
	EMPTY,	// tk_float     
	EMPTY,	// tk_double    
	EMPTY,	// tk_boolean   
	EMPTY,	// tk_char      
	EMPTY,	// tk_octet     
	EMPTY,	// tk_any       
	EMPTY,	// tk_typecode  
	EMPTY,	// tk_principal 
	COMPLEX,	// tk_objref    
	COMPLEX,	// tk_struct    
	COMPLEX,	// tk_union     
	COMPLEX,	// tk_enum      
	SIMPLE,	// tk_string    
	COMPLEX,	// tk_sequence  
	COMPLEX,	// tk_array     
	COMPLEX,	// tk_alias     
	COMPLEX,	// tk_except    
	EMPTY,	// tk_longlong  
	EMPTY,	// tk_ulonglong 
	EMPTY,	// tk_longdouble
	EMPTY,	// tk_wchar     
	SIMPLE,	// tk_wstring
	SIMPLE,	// tk_fixed
	COMPLEX,	// tk_value
	COMPLEX,	// tk_value_box
	COMPLEX,	// tk_native
	COMPLEX	// tk_abstract_interface
    };

    // Maps TCKind values to names
    private static final String[] kindNames = {
        "null",
        "void",
        "short",
        "long",
        "ushort",
        "ulong",
        "float",
        "double",
        "boolean",
        "char",
        "octet",
        "any",
        "typecode",
        "principal",
        "objref",
        "struct",
        "union",
        "enum",
        "string",
        "sequence",
        "array",
        "alias",
        "exception",
        "longlong",
        "ulonglong",
        "longdouble",
        "wchar",
        "wstring",
        "fixed",
        "value",
        "valueBox",
        "native",
        "abstractInterface"
    };

private int 		_kind		= 0;	// the typecode kind

// data members for representing the various kinds of typecodes. 

private String          _id             = "";   // the typecode repository id
private String          _name           = "";   // the typecode name
private int             _memberCount    = 0;    // member count
private String          _memberNames[]  = null; // names of members
private TypeCodeImpl    _memberTypes[]  = null; // types of members
private AnyImpl         _unionLabels[]  = null; // values of union labels
private TypeCodeImpl    _discriminator  = null; // union discriminator type
private int             _defaultIndex   = -1;   // union default index
private int             _length         = 0;    // string/seq/array length
private TypeCodeImpl    _contentType    = null; // seq/array/alias type
// fixed
private short           _digits         = 0;
private short           _scale          = 0;
// value type
// _REVISIT_ We might want to keep references to the ValueMember classes
// passed in at initialization instead of copying the relevant data.
// Is the data immutable? What about StructMember, UnionMember etc.?
private short           _type_modifier  = -1;   // VM_NONE, VM_CUSTOM,
// VM_ABSTRACT, VM_TRUNCATABLE
private TypeCodeImpl    _concrete_base  = null; // concrete base type
private short           _memberAccess[] = null; // visibility of ValueMember
// recursive sequence support
private TypeCodeImpl    _parent         = null; // the enclosing type code
private int             _parentOffset   = 0;    // the level of enclosure
// recursive type code support
private TypeCodeImpl    _indirectType   = null;

// caches the byte buffer written in write_value for quick remarshaling...
private byte[] outBuffer                = null;
// ... but only if caching is enabled
private boolean cachingEnabled          = false;

// the ORB instance: may be instanceof ORBSingleton or ORB
private org.omg.CORBA.ORB _orb; 		

///////////////////////////////////////////////////////////////////////////
// Constructors...

public TypeCodeImpl(org.omg.CORBA.ORB orb) 
{
    // initialized to tk_null
    _orb = orb;
}
  
public TypeCodeImpl(org.omg.CORBA.ORB orb, TypeCode tc)
// to handle conversion of "remote" typecodes into "native" style.
// also see the 'convertToNative(ORB orb, TypeCode tc)' function
{
    // the orb object
    _orb = orb;

    // This is a protection against misuse of this constructor.
    // Should only be used if tc is not an instance of this class!
    // Otherwise we run into problems with recursive/indirect type codes.
    // _REVISIT_ We should make this constructor private
    if (tc instanceof TypeCodeImpl) {
	TypeCodeImpl tci = (TypeCodeImpl)tc;
	if (tci._kind == tk_indirect)
	    throw new BAD_TYPECODE();
	if (tci._kind == TCKind._tk_sequence && tci._contentType == null)
	    throw new BAD_TYPECODE();
    }

    // set up kind
    _kind 	= tc.kind().value();

    try {
	// set up parameters
	switch (_kind) {
	case TCKind._tk_value:
	    _type_modifier = tc.type_modifier();
	    // concrete base may be null
	    TypeCode tccb = tc.concrete_base_type();
	    if (tccb != null) {
		_concrete_base = convertToNative(_orb, tccb);
	    } else {
		_concrete_base = null;
	    }
	    //_memberAccess = tc._memberAccess;
	    // Need to reconstruct _memberAccess using member_count() and member_visibility()
	    _memberAccess = new short[tc.member_count()];
	    for (int i=0; i < tc.member_count(); i++) {
		_memberAccess[i] = tc.member_visibility(i);
	    }
	case TCKind._tk_except:
	case TCKind._tk_struct:
	case TCKind._tk_union:
	    // set up member types
	    _memberTypes = new TypeCodeImpl[tc.member_count()];
	    for (int i=0; i < tc.member_count(); i++) {
	        _memberTypes[i] = convertToNative(_orb, tc.member_type(i));
		_memberTypes[i].setParent(this);
	    }
	case TCKind._tk_enum:
	    // set up member names
	    _memberNames = new String[tc.member_count()];
	    for (int i=0; i < tc.member_count(); i++) {
	        _memberNames[i] = tc.member_name(i);
	    }
	    // set up member count
	    _memberCount = tc.member_count();
	case TCKind._tk_objref:
	case TCKind._tk_alias:
	case TCKind._tk_value_box:
	case TCKind._tk_native:
	case TCKind._tk_abstract_interface:
	    setId(tc.id());
	    _name = tc.name();
	    break;
	}
      
	// set up stuff for unions
	switch (_kind) {
	case TCKind._tk_union:
	    _discriminator = convertToNative(_orb, tc.discriminator_type());
	    _defaultIndex  = tc.default_index();
	    _unionLabels = new AnyImpl[_memberCount];
	    for (int i=0; i < _memberCount; i++)
		_unionLabels[i] = new AnyImpl(_orb, tc.member_label(i));
	    break;
	}
      
	// set up length
	switch (_kind) {
	case TCKind._tk_string:
	case TCKind._tk_wstring:
	case TCKind._tk_sequence:
	case TCKind._tk_array:
	    _length = tc.length();
	}
      
	// set up content type
	switch (_kind) {
	case TCKind._tk_sequence:
	case TCKind._tk_array:
	case TCKind._tk_alias:
	case TCKind._tk_value_box:
	    _contentType = convertToNative(_orb, tc.content_type());
	}
    } catch (org.omg.CORBA.TypeCodePackage.Bounds e) {} catch (BadKind e) {}
    // dont have to worry about these since code ensures we dont step
    // out of bounds.
}
    
public TypeCodeImpl(int creationKind)
// for primitive types
{
    // the orb object
    _orb = null;

    // private API. dont bother checking that
    //     (creationKind < 0 || creationKind > typeTable.length)

    _kind = creationKind;

    // do initialization for special cases
    switch (_kind) {
    case TCKind._tk_objref:
	{
	    // this is being used to create typecode for CORBA::Object
	    setId("IDL:omg.org/CORBA/Object:1.0");
	    _name = "Object";
	    break;
	}

    case TCKind._tk_string:
    case TCKind._tk_wstring:
	{
	    _length =0;
	    break;
	}

    case TCKind._tk_value:
	{
	    _concrete_base = null;
	    break;
	}
    }
}

public TypeCodeImpl(org.omg.CORBA.ORB orb,
		    int creationKind,
		    String id,
		    String name,
		    StructMember[] members)
		    // for structs and exceptions
{
    // the orb object
    _orb = orb;

    if ((creationKind == TCKind._tk_struct) || (creationKind == TCKind._tk_except))
	{
	
	    _kind		= creationKind;
	    setId(id);
	    _name		= name;
	    _memberCount	= members.length;
	
	    _memberNames = new String[_memberCount];
	    _memberTypes = new TypeCodeImpl[_memberCount];

	    for (int i = 0 ; i < _memberCount ; i++) {
		_memberNames[i] = members[i].name;
		_memberTypes[i] = convertToNative(_orb, members[i].type);
		_memberTypes[i].setParent(this);
	    }
	} // else initializes to null
}

public TypeCodeImpl(org.omg.CORBA.ORB orb, 
		    int creationKind,
		    String id,
		    String name,
		    TypeCode discriminator_type,
		    UnionMember[] members)
		    // for unions
{
    // the orb object
    _orb = orb;

    if (creationKind == TCKind._tk_union) {
        _kind		= creationKind;
        setId(id);
        _name		= name;
        _memberCount	= members.length;
        _discriminator	= convertToNative(_orb, discriminator_type);

        _memberNames = new String[_memberCount];
        _memberTypes = new TypeCodeImpl[_memberCount];
        _unionLabels = new AnyImpl[_memberCount];

        for (int i = 0 ; i < _memberCount ; i++) {
            _memberNames[i] = members[i].name;
            _memberTypes[i] = convertToNative(_orb, members[i].type);
            _memberTypes[i].setParent(this);
            _unionLabels[i] = new AnyImpl(_orb, members[i].label);
            // check whether this is the default branch.
            if (_unionLabels[i].type().kind() == TCKind.tk_octet) {
                if (_unionLabels[i].extract_octet() == (byte)0) {
                    _defaultIndex = i;
                }
            }
        }
    } // else initializes to null
}

public TypeCodeImpl(org.omg.CORBA.ORB orb,
		    int creationKind,
		    String id,
		    String name,
		    short type_modifier,
		    TypeCode concrete_base,
		    ValueMember[] members)
		    // for value types
{
    // the orb object
    _orb = orb;

    if (creationKind == TCKind._tk_value) {
        _kind		= creationKind;
        setId(id);
        _name		= name;
        _type_modifier	= type_modifier;
        if (_concrete_base != null) {
            _concrete_base = convertToNative(_orb, concrete_base);
        }
        _memberCount	= members.length;

        _memberNames = new String[_memberCount];
        _memberTypes = new TypeCodeImpl[_memberCount];
        _memberAccess = new short[_memberCount];

        for (int i = 0 ; i < _memberCount ; i++) {
	    _memberNames[i] = members[i].name;
	    _memberTypes[i] = convertToNative(_orb, members[i].type);
	    _memberTypes[i].setParent(this);
	    _memberAccess[i] = members[i].access;
        }
    } // else initializes to null
}


public TypeCodeImpl(org.omg.CORBA.ORB orb,
		    int creationKind,
		    String id,
		    String name,
		    String[] members)
		    // for enums
{
    // the orb object
    _orb = orb;

    if (creationKind == TCKind._tk_enum)
	{
	    _kind		= creationKind;
	    setId(id);
	    _name		= name;
	    _memberCount	= members.length;

	    _memberNames = new String[_memberCount];

	    for (int i = 0 ; i < _memberCount ; i++)
		_memberNames[i] = members[i];
	} // else initializes to null
}

public TypeCodeImpl(org.omg.CORBA.ORB orb, 
		    int creationKind,
		    String id,
		    String name,
		    TypeCode original_type)
		    // for aliases and value boxes
{
    // the orb object
    _orb = orb;

    if ( creationKind == TCKind._tk_alias || creationKind == TCKind._tk_value_box )
	{
	    _kind		= creationKind;
	    setId(id);
	    _name		= name;
	    _contentType	= convertToNative(_orb, original_type);
	}
    // else initializes to null

}

public TypeCodeImpl(org.omg.CORBA.ORB orb, 
		    int creationKind,
		    String id,
		    String name)
{
    // the orb object
    _orb = orb;

    if (creationKind == TCKind._tk_objref ||
        creationKind == TCKind._tk_native ||
        creationKind == TCKind._tk_abstract_interface)
	{
	    _kind		= creationKind;
	    setId(id);
	    _name		= name;
	} // else initializes to null
}

  
public TypeCodeImpl(org.omg.CORBA.ORB orb, 
		    int creationKind,
		    int bound)
		    // for strings
{
    if (bound < 0)
        throw new BAD_PARAM("bound can not be negative!");

    // the orb object
    _orb = orb;

    if ((creationKind == TCKind._tk_string) || (creationKind == TCKind._tk_wstring))
	{
	    _kind		= creationKind;
	    _length		= bound;
	} // else initializes to null
}

public TypeCodeImpl(org.omg.CORBA.ORB orb, 
		    int creationKind,
		    int bound,
		    TypeCode element_type)
		    // for sequences and arrays
{
    // the orb object
    _orb = orb;

    if ( creationKind == TCKind._tk_sequence || creationKind == TCKind._tk_array )
	{
	    _kind		= creationKind;
	    _length		= bound;
	    _contentType	= convertToNative(_orb, element_type);
	} // else initializes to null
}
  
public TypeCodeImpl(org.omg.CORBA.ORB orb,
		    int creationKind,
		    int bound,
		    int offset)
		    // for recursive sequences
{
    // the orb object
    _orb = orb;

    if (creationKind == TCKind._tk_sequence) {
        _kind		= creationKind;
        _length		= bound;
        _parentOffset	= offset;
    } // else initializes to null
}

public TypeCodeImpl(org.omg.CORBA.ORB orb,
		    String id)
		    // for recursive type codes
{
    // the orb object
    _orb = orb;

    _kind	= tk_indirect;
    // This is the type code of the type we stand in for, not our own.
    _id		= id;
    // Try to resolve it now. May return null in which case
    // we try again later (see indirectType()).
    tryIndirectType();
}

public TypeCodeImpl(org.omg.CORBA.ORB orb,
		    int creationKind,
		    short digits,
		    short scale)
		    // for fixed
{
    // the orb object
    _orb = orb;

    //if (digits < 1 || digits > 31)
    //throw new BAD_TYPECODE();

    if (creationKind == TCKind._tk_fixed) {
        _kind		= creationKind;
        _digits		= digits;
        _scale		= scale;
    } // else initializes to null
}

///////////////////////////////////////////////////////////////////////////
// Other creation functions...

public static TypeCodeImpl get_primitive_tc(TCKind tcKind)
{
    try {
	return primitiveConstants[tcKind.value()];
    } catch (Throwable t) {
	throw new BAD_OPERATION("Invalid or unavailable typecode for kind = "+tcKind.value());
    }
}

public static TypeCodeImpl get_primitive_tc(int val)
{
    try {
	return primitiveConstants[val];
    } catch (Throwable t) {
	throw new BAD_OPERATION("Invalid or unavailable typecode for kind = "+val);
    }
}

// Optimization:
// If we checked for and returned constant primitive typecodes
// here we could reduce object creation and also enable more
// efficient typecode comparisons for primitive typecodes.
//
protected static TypeCodeImpl convertToNative(org.omg.CORBA.ORB orb,
					      TypeCode tc) 
{
    if (tc instanceof TypeCodeImpl)
	return (TypeCodeImpl) tc;
    else
	return new TypeCodeImpl(orb, tc);
}

public static CDROutputStream newOutputStream(org.omg.CORBA.ORB orb) {
    TypeCodeOutputStream tcos = new TypeCodeOutputStream(orb);
    //if (debug) System.out.println("Created TypeCodeOutputStream " + tcos + " with no parent");
    return tcos;
}

// Support for indirect/recursive type codes

private TypeCodeImpl indirectType() {
    _indirectType = tryIndirectType();
    if (_indirectType == null) {
        // Nothing we can do about that.
        throw new BAD_TYPECODE("Invoked operation on unresolved recursive type code!");
    }
    return _indirectType;
}

private TypeCodeImpl tryIndirectType() {
    // Assert that _kind == tk_indirect
    if (_indirectType != null)
        return _indirectType;

    if (_orb instanceof TypeCodeFactory) {
        setIndirectType(((TypeCodeFactory)_orb).getTypeCode(_id));
    } else {
        throw new BAD_TYPECODE("ORB not supporting recursive type codes!");
    }
    return _indirectType;
}

private void setIndirectType(TypeCodeImpl newType) {
    _indirectType = newType;
    if (_indirectType != null) {
	try {
	    _id = _indirectType.id();
	} catch (BadKind e) {} // can't happen
    }
}

private void setId(String newID) {
    _id = newID;
    if (_orb instanceof TypeCodeFactory) {
        ((TypeCodeFactory)_orb).setTypeCode(_id, this);
    }
    // check whether return value != this which would indicate that the
    // repository id isn't unique.
}

private void setParent(TypeCodeImpl parent) {
    _parent = parent;
}

private TypeCodeImpl getParentAtLevel(int level) {
    if (level == 0)
        return this;
    if (_parent == null)
        throw new BAD_TYPECODE("Invoked operation on unresolved recursive type code!");
    return _parent.getParentAtLevel(level - 1);
}

private TypeCodeImpl lazy_content_type() {
    if (_contentType == null) {
        if (_kind == TCKind._tk_sequence && _parentOffset > 0 && _parent != null) {
	    // This is an unresolved recursive sequence tc.
	    // Try to resolve it now if the hierarchy is complete.
	    TypeCodeImpl realParent = getParentAtLevel(_parentOffset);
	    if (realParent != null && realParent._id != null) {
		// Create a recursive type code object as the content type.
		// This is when the recursive sequence typecode morphes
		// into a sequence typecode containing a recursive typecode.
		_contentType = new TypeCodeImpl(_orb, realParent._id);
	    }
        }
    }
    return _contentType;
}

// Other private functions

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

///////////////////////////////////////////////////////////////////////////
// TypeCode operations

public final boolean equal(TypeCode tc)
// _REVISIT_ for all optional names/ids, we might want to check that
// they are equal in case both are non-nil.
{
    if (tc == this)
        return true;

    try {

	if (_kind == tk_indirect) {
	    //return indirectType().equal(tc);
	    if (_id != null && tc.id() != null)
		return _id.equals(tc.id());
	    return (_id == null && tc.id() == null);
	}

	// make sure kinds are identical.
	if (_kind != tc.kind().value()) {
	    return false;
	}

	switch (typeTable[_kind]) {
	case EMPTY:
	    // no parameters to check.
	    return true;
      
	case SIMPLE:
	    switch (_kind) {
	    case TCKind._tk_string:
	    case TCKind._tk_wstring:
		// check for bound.
		return (_length == tc.length());

	    case TCKind._tk_fixed:
		return (_digits == tc.fixed_digits() && _scale == tc.fixed_scale());
	    default:
		return false;
	    }
      
	case COMPLEX:

	    switch(_kind) {

	    case TCKind._tk_objref:
		{
		    // check for logical id.
		    if (_id.compareTo(tc.id()) == 0) {
			return true;
		    }

		    if (_id.compareTo(get_primitive_tc(_kind).id()) == 0) {
			return true;
		    }

		    if (tc.id().compareTo(get_primitive_tc(_kind).id()) == 0) {
			return true;
		    }

		    return false;
		}

	    case TCKind._tk_native:
	    case TCKind._tk_abstract_interface:
		{
		    // check for logical id.
		    if (_id.compareTo(tc.id()) != 0) {
			return false;

		    }
		    // ignore name since its optional.
		    return true;
		}

	    case TCKind._tk_struct:
	    case TCKind._tk_except:
		{
		    // check for member count
		    if (_memberCount != tc.member_count())
			return false;
		    // check for repository id
		    if (_id.compareTo(tc.id()) != 0)
			return false;
		    // check for member types.
		    for (int i = 0 ; i < _memberCount ; i++)
			if (! _memberTypes[i].equal(tc.member_type(i)))
			    return false;
		    // ignore id and names since those are optional.
		    return true;
		}

	    case TCKind._tk_union:
		{
		    // check for member count
		    if (_memberCount != tc.member_count())
			return false;
		    // check for repository id
		    if (_id.compareTo(tc.id()) != 0)
			return false;
		    // check for default index
		    if (_defaultIndex != tc.default_index())
			return false;
		    // check for discriminator type
		    if (!_discriminator.equal(tc.discriminator_type()))
			return false;
		    // check for label types and values
		    for (int i = 0 ; i < _memberCount ; i++)
			if (! _unionLabels[i].equal(tc.member_label(i)))
			    return false;
		    // check for branch types
		    for (int i = 0 ; i < _memberCount ; i++)
			if (! _memberTypes[i].equal(tc.member_type(i)))
			    return false;
		    // ignore id and names since those are optional.
		    return true;
		}

	    case TCKind._tk_enum:
		{
		    // check for repository id
		    if (_id.compareTo(tc.id()) != 0)
			return false;
		    // check member count
		    if (_memberCount != tc.member_count())
			return false;
		    // ignore names since those are optional.
		    return true;
		}

	    case TCKind._tk_sequence:
	    case TCKind._tk_array:
		{
		    // check bound/length
		    if (_length != tc.length()) {
			return false;
		    }
		    // check content type
		    if (! lazy_content_type().equal(tc.content_type())) {
			return false;
		    }
		    // ignore id and name since those are optional.
		    return true;
		}

	    case TCKind._tk_value:
		{
		    // check for member count
		    if (_memberCount != tc.member_count())
			return false;
		    // check for repository id
		    if (_id.compareTo(tc.id()) != 0)
			return false;
		    // check for member types.
		    for (int i = 0 ; i < _memberCount ; i++)
			if (_memberAccess[i] != tc.member_visibility(i) ||
			    ! _memberTypes[i].equal(tc.member_type(i)))
			    return false;
		    if (_type_modifier == tc.type_modifier())
			return false;
		    // concrete_base may be null
		    TypeCode tccb = tc.concrete_base_type();
		    if ((_concrete_base == null && tccb != null) ||
			(_concrete_base != null && tccb == null) ||
			! _concrete_base.equal(tccb))
		    {
			return false;
		    }
		    // ignore id and names since those are optional.
		    return true;
		}

	    case TCKind._tk_alias:
	    case TCKind._tk_value_box:
		{
		    // check for repository id
		    if (_id.compareTo(tc.id()) != 0) {
			return false;
		    }
		    // check for equality with the true type
		    return _contentType.equal(tc.content_type());
		}
	    }
	}
    } catch (org.omg.CORBA.TypeCodePackage.Bounds e) {} catch (BadKind e) {}
    // dont have to worry about these since the code ensures these dont
    // arise.
    return false;
}

/**
* The equivalent operation is used by the ORB when determining type equivalence
* for values stored in an IDL any.
*/
public boolean equivalent(TypeCode tc) {
    if (tc == this) {
        return true;
    }

    // If the result of the kind operation on either TypeCode is tk_alias, recursively
    // replace the TypeCode with the result of calling content_type, until the kind is no
    // longer tk_alias.
    // Note: Always resolve indirect types first!
    TypeCode myRealType = (_kind == tk_indirect ? indirectType() : this);
    myRealType = realType(myRealType);
    TypeCode otherRealType = realType(tc);

    // If results of the kind operation on each typecode differ, equivalent returns false.
    if (myRealType.kind().value() != otherRealType.kind().value()) {
        return false;
    }

    String myID = null;
    String otherID = null;
    try {
        myID = this.id();
        otherID = tc.id();
        // At this point the id operation is valid for both TypeCodes.

        // Return true if the results of id for both TypeCodes are non-empty strings
        // and both strings are equal.
        // If both ids are non-empty but are not equal, then equivalent returns FALSE.
        if (myID != null && otherID != null) {
            return (myID.equals(otherID));
        }
    } catch (BadKind e) {
        // id operation is not valid for either or both TypeCodes
    }

    // If either or both id is an empty string, or the TypeCode kind does not support
    // the id operation, perform a structural comparison of the TypeCodes.

    int myKind = myRealType.kind().value();
    try {
        if (myKind == TCKind._tk_struct ||
            myKind == TCKind._tk_union ||
            myKind == TCKind._tk_enum ||
            myKind == TCKind._tk_except ||
            myKind == TCKind._tk_value)
        {
            if (myRealType.member_count() != otherRealType.member_count())
                return false;
        }
        if (myKind == TCKind._tk_union)
        {
            if (myRealType.default_index() != otherRealType.default_index())
                return false;
        }
        if (myKind == TCKind._tk_string ||
            myKind == TCKind._tk_wstring ||
            myKind == TCKind._tk_sequence ||
            myKind == TCKind._tk_array)
        {
            if (myRealType.length() != otherRealType.length())
                return false;
        }
        if (myKind == TCKind._tk_fixed)
        {
            if (myRealType.fixed_digits() != otherRealType.fixed_digits() ||
                myRealType.fixed_scale() != otherRealType.fixed_scale())
                return false;
        }
        if (myKind == TCKind._tk_union)
        {
            for (int i=0; i<myRealType.member_count(); i++) {
                if (myRealType.member_label(i) != otherRealType.member_label(i))
                    return false;
            }
            if ( ! myRealType.discriminator_type().equivalent(otherRealType.discriminator_type()))
                return false;
        }
        if (myKind == TCKind._tk_alias ||
            myKind == TCKind._tk_value_box ||
            myKind == TCKind._tk_sequence ||
            myKind == TCKind._tk_array)
        {
            if ( ! myRealType.content_type().equivalent(otherRealType.content_type()))
                return false;
        }
        if (myKind == TCKind._tk_struct ||
            myKind == TCKind._tk_union ||
            myKind == TCKind._tk_except ||
            myKind == TCKind._tk_value)
        {
            for (int i=0; i<myRealType.member_count(); i++) {
                if ( ! myRealType.member_type(i).equivalent(otherRealType.member_type(i)))
                    return false;
            }
        }
    } catch (BadKind e) {
        // impossible if we checked correctly above
        throw new INTERNAL();
    } catch (org.omg.CORBA.TypeCodePackage.Bounds e) {
        // impossible if we checked correctly above
        throw new INTERNAL();
    }
    // Structural comparison succeeded!
    return true;
}

public TypeCode get_compact_typecode() {
    // _REVISIT_ It isn't clear whether this method should operate on this or a copy.
    // For now just return this unmodified because the name and member_name fields
    // aren't used for comparison anyways.
    return this;
}

public TCKind kind() 
{
    if (_kind == tk_indirect)
        return indirectType().kind();
    return TCKind.from_int(_kind);
}
  
public boolean is_recursive() 
{
    // Recursive is the only form of indirect type codes right now.
    // Indirection can also be used for repeated type codes.
    return (_kind == tk_indirect);
}
  
public String id()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	//return indirectType().id(); // same as _id
    case TCKind._tk_except:
    case TCKind._tk_objref:
    case TCKind._tk_struct:
    case TCKind._tk_union:
    case TCKind._tk_enum:
    case TCKind._tk_alias:
    case TCKind._tk_value:
    case TCKind._tk_value_box:
    case TCKind._tk_native:
    case TCKind._tk_abstract_interface:
	// exception and objref typecodes must have a repository id.
	// structs, unions, enums, and aliases may or may not.
	return _id;
    default:
	// all other typecodes throw the BadKind exception.
	throw new BadKind();
    }
}

public String name()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().name();
    case TCKind._tk_except:
    case TCKind._tk_objref:
    case TCKind._tk_struct:
    case TCKind._tk_union:
    case TCKind._tk_enum:
    case TCKind._tk_alias:
    case TCKind._tk_value:
    case TCKind._tk_value_box:
    case TCKind._tk_native:
    case TCKind._tk_abstract_interface:
	return _name;
    default:
	throw new BadKind();
    }
}

public int member_count()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().member_count();
    case TCKind._tk_except:
    case TCKind._tk_struct:
    case TCKind._tk_union:
    case TCKind._tk_enum:
    case TCKind._tk_value:
	return _memberCount;
    default:
	throw new BadKind();
    }
}

public String member_name(int index)
    throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().member_name(index);
    case TCKind._tk_except:
    case TCKind._tk_struct:
    case TCKind._tk_union:
    case TCKind._tk_enum:
    case TCKind._tk_value:
	try {
	    return _memberNames[index];
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new org.omg.CORBA.TypeCodePackage.Bounds();
	}
    default:
	throw new BadKind();
    }
}

public TypeCode member_type(int index)
    throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().member_type(index);
    case TCKind._tk_except:
    case TCKind._tk_struct:
    case TCKind._tk_union:
    case TCKind._tk_value:
	try {
	    return _memberTypes[index];
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new org.omg.CORBA.TypeCodePackage.Bounds();
	}
    default:
	throw new BadKind();
    }
}
  
public Any member_label(int index)
    throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().member_label(index);
    case TCKind._tk_union:
	try {
            // _REVISIT_ Why create a new Any for this?
	    return new AnyImpl(_orb, _unionLabels[index]);
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new org.omg.CORBA.TypeCodePackage.Bounds();
	}
    default:
	throw new BadKind();
    }
}

public TypeCode discriminator_type()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().discriminator_type();
    case TCKind._tk_union:
	return _discriminator;
    default:
	throw new BadKind();
    }
}

public int default_index()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().default_index();
    case TCKind._tk_union:
	return _defaultIndex;
    default:
	throw new BadKind();
    }
}

public int length()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().length();
    case TCKind._tk_string:
    case TCKind._tk_wstring:
    case TCKind._tk_sequence:
    case TCKind._tk_array:
	return _length;
    default:
	throw new BadKind();
    }
}
  
public TypeCode content_type()
    throws BadKind
{
    switch (_kind) {
    case tk_indirect:
	return indirectType().content_type();
    case TCKind._tk_sequence:
	return lazy_content_type();
    case TCKind._tk_array:
    case TCKind._tk_alias:
    case TCKind._tk_value_box:
	return _contentType;
    default:
	throw new BadKind();
    }
}

public short fixed_digits() throws BadKind {
    switch (_kind) {
    case TCKind._tk_fixed:
	return _digits;
    default:
	throw new BadKind();
    }
}

public short fixed_scale() throws BadKind {
    switch (_kind) {
    case TCKind._tk_fixed:
	return _scale;
    default:
	throw new BadKind();
    }
}

public short member_visibility(int index) throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds {
    switch (_kind) {
    case tk_indirect:
        return indirectType().member_visibility(index);
    case TCKind._tk_value:
        try {
            return _memberAccess[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new org.omg.CORBA.TypeCodePackage.Bounds();
        }
    default:
        throw new BadKind();
    }
}

public short type_modifier() throws BadKind {
    switch (_kind) {
    case tk_indirect:
        return indirectType().type_modifier();
    case TCKind._tk_value:
        return _type_modifier;
    default:
        throw new BadKind();
    }
}

public TypeCode concrete_base_type() throws BadKind {
    switch (_kind) {
    case tk_indirect:
        return indirectType().concrete_base_type();
    case TCKind._tk_value:
        return _concrete_base;
    default:
        throw new BadKind();
    }
}

public void read_value(InputStream is) {
    if (is instanceof TypeCodeReader) {
	// hardly possible unless caller knows our "private" stream classes.
	if (read_value_kind((TypeCodeReader)is))
	    read_value_body(is);
    } else if (is instanceof CDRInputStream) {
        WrapperInputStream wrapper = new WrapperInputStream((CDRInputStream)is);
        //if (debug) System.out.println("Created WrapperInputStream " + wrapper + " with no parent");
	if (read_value_kind((TypeCodeReader)wrapper))
	    read_value_body(wrapper);
    } else {
	read_value_kind(is);
	read_value_body(is);
    }
}

private void read_value_recursive(TypeCodeInputStream is) {
    // don't wrap a CDRInputStream reading "inner" TypeCodes.
    if (is instanceof TypeCodeReader) {
	if (read_value_kind((TypeCodeReader)is))
	    read_value_body(is);
    } else {
	read_value_kind((InputStream)is);
	read_value_body(is);
    }
}

boolean read_value_kind(TypeCodeReader tcis) {
    int myPosition = tcis.getTopLevelPosition();

    _kind = tcis.read_long();
    // check validity of kind
    if ((_kind < 0 || _kind > typeTable.length) && _kind != tk_indirect) {
	throw new MARSHAL();
    }
    // Don't do any work if this is native
    if (_kind == TCKind._tk_native)
	throw new MARSHAL();

    // We have to remember the stream and position for EVERY type code
    // in case some recursive or indirect type code references it.
    TypeCodeReader topStream = tcis.getTopLevelStream();

    if (_kind == tk_indirect) {
	int streamOffset = tcis.read_long();
	if (streamOffset > -4)
	    throw new MARSHAL("Invalid indirection value " + streamOffset);

	// The encoding used for indirection is the same as that used for recursive TypeCodes,
	// i.e., a 0xffffffff indirection marker followed by a long offset
	// (in units of octets) from the beginning of the long offset.
        int topPos = tcis.getTopLevelPosition();
        // substract 4 to get back to the beginning of the long offset.
	int indirectTypePosition = topPos - 4 + streamOffset;

	// Now we have to find the referenced type
	// by its indirectTypePosition within topStream.
        //if (debug) System.out.println("TypeCodeImpl looking up indirection at position topPos " +
            //topPos + " - 4 + offset " + streamOffset + " = " + indirectTypePosition);
	TypeCodeImpl type = topStream.getTypeCodeAtPosition(indirectTypePosition);
        if (type == null)
	    throw new MARSHAL("Referenced type of indirect type not marshaled!");
        setIndirectType(type);
	return false;
    }

    topStream.addTypeCodeAtPosition(this, myPosition);
    return true;
}

void read_value_kind(InputStream is) {
    // unmarshal the kind
    _kind = is.read_long();

    // check validity of kind
    if ((_kind < 0 || _kind > typeTable.length) && _kind != tk_indirect) {
	throw new MARSHAL();
    }
    // Don't do any work if this is native
    if (_kind == TCKind._tk_native)
	throw new MARSHAL();

    if (_kind == tk_indirect) {
	throw new MARSHAL("InputStream subtype not supporting recursive type codes!");
    }
}

void read_value_body(InputStream is) {
    // start unmarshaling the rest of the typecode, based on the
    // encoding (empty, simple or complex).

    switch (typeTable[_kind]) {
    case EMPTY:
	// nothing to unmarshal
	break;

    case SIMPLE:
	switch (_kind) {
	case TCKind._tk_string:
	case TCKind._tk_wstring:
	    _length = is.read_long();
	    break;
	case TCKind._tk_fixed:
	    _digits = is.read_ushort();
	    _scale = is.read_short();
	    break;
	default:
	    throw new MARSHAL();
	}
	break;

    case COMPLEX:
	{
	    TypeCodeInputStream _encap = TypeCodeInputStream.readEncapsulation(is, _orb);

	    switch(_kind) {

	    case TCKind._tk_objref:
	    case TCKind._tk_abstract_interface:
		{
		    // get the repository id
		    setId(_encap.read_string());
		    // get the name
		    _name = _encap.read_string();
		}
		break;

	    case TCKind._tk_union:
		{
		    // get the repository id
		    setId(_encap.read_string());

		    // get the name
		    _name = _encap.read_string();

		    // discriminant typecode
		    _discriminator = new TypeCodeImpl(_orb);
		    _discriminator.read_value_recursive(_encap);

		    // default index
		    _defaultIndex = _encap.read_long();

		    // get the number of members
		    _memberCount = _encap.read_long();

		    // create arrays for the label values, names and types of members
		    _unionLabels = new AnyImpl[_memberCount];
		    _memberNames = new String[_memberCount];
		    _memberTypes = new TypeCodeImpl[_memberCount];

		    // read off label values, names and types
		    for (int i=0; i < _memberCount; i++) {
			_unionLabels[i] = new AnyImpl(_orb);
			if (i == _defaultIndex) 
			    // for the default case, read off the zero octet
			    _unionLabels[i].insert_octet(_encap.read_octet());
			else {
			    switch (realType(_discriminator).kind().value()) {
			    case TCKind._tk_short:
				_unionLabels[i].insert_short(_encap.read_short());
				break;
			    case TCKind._tk_long:
				_unionLabels[i].insert_long(_encap.read_long());
				break;
			    case TCKind._tk_ushort:
				_unionLabels[i].insert_ushort(_encap.read_short());
				break;
			    case TCKind._tk_ulong:
				_unionLabels[i].insert_ulong(_encap.read_long());
				break;
			    case TCKind._tk_float:
				_unionLabels[i].insert_float(_encap.read_float());
				break;
			    case TCKind._tk_double:
				_unionLabels[i].insert_double(_encap.read_double());
				break;
			    case TCKind._tk_boolean:
				_unionLabels[i].insert_boolean(_encap.read_boolean());
				break;
			    case TCKind._tk_char:
				_unionLabels[i].insert_char(_encap.read_char());
				break;
			    case TCKind._tk_enum:
				_unionLabels[i].type(_discriminator);
				_unionLabels[i].insert_long(_encap.read_long());
				break;
			    case TCKind._tk_longlong:
				_unionLabels[i].insert_longlong(_encap.read_longlong());
				break;
			    case TCKind._tk_ulonglong:
				_unionLabels[i].insert_ulonglong(_encap.read_longlong());
				break;
				// _REVISIT_ figure out long double mapping
				// case TCKind.tk_longdouble:
				// _unionLabels[i].insert_longdouble(_encap.getDouble());
				// break;
                            case TCKind._tk_wchar:
				_unionLabels[i].insert_wchar(_encap.read_wchar());
                                break;
			    default:
				throw new MARSHAL();
			    }
			}
			_memberNames[i] = _encap.read_string();
			_memberTypes[i] = new TypeCodeImpl(_orb);
			_memberTypes[i].read_value_recursive(_encap);
			_memberTypes[i].setParent(this);
		    }
		}
		break;

	    case TCKind._tk_enum:
		{
		    // get the repository id
		    setId(_encap.read_string());

		    // get the name
		    _name = _encap.read_string();

		    // get the number of members
		    _memberCount = _encap.read_long();

		    // create arrays for the identifier names
		    _memberNames = new String[_memberCount];

		    // read off identifier names
		    for (int i=0; i < _memberCount; i++)
			_memberNames[i] = _encap.read_string();
		}
		break;

	    case TCKind._tk_sequence:
		{
		    // get the type of the sequence
		    _contentType = new TypeCodeImpl(_orb);
		    _contentType.read_value_recursive(_encap);
	    
		    // get the bound on the length of the sequence
		    _length = _encap.read_long();
		}
		break;

	    case TCKind._tk_array:
		{
		    // get the type of the array
		    _contentType = new TypeCodeImpl(_orb);
		    _contentType.read_value_recursive(_encap);

		    // get the length of the array
		    _length = _encap.read_long();
		}
		break;

	    case TCKind._tk_alias:
	    case TCKind._tk_value_box:
		{
		    // get the repository id
		    setId(_encap.read_string());

		    // get the name
		    _name = _encap.read_string();

		    // get the type aliased
		    _contentType = new TypeCodeImpl(_orb);
		    _contentType.read_value_recursive(_encap);
		}
		break;

	    case TCKind._tk_except:
	    case TCKind._tk_struct:
		{
		    // get the repository id
		    setId(_encap.read_string());

		    // get the name
		    _name = _encap.read_string();

		    // get the number of members
		    _memberCount = _encap.read_long();

		    // create arrays for the names and types of members
		    _memberNames = new String[_memberCount];
		    _memberTypes = new TypeCodeImpl[_memberCount];

		    // read off member names and types
		    for (int i=0; i < _memberCount; i++) {
			_memberNames[i] = _encap.read_string();
			_memberTypes[i] = new TypeCodeImpl(_orb);
                        //if (debug) System.out.println("TypeCode " + _name + " reading member " + _memberNames[i]);
			_memberTypes[i].read_value_recursive(_encap);
			_memberTypes[i].setParent(this);
		    }
		}
		break;

	    case TCKind._tk_value:
		{
		    // get the repository id
		    setId(_encap.read_string());

		    // get the name
		    _name = _encap.read_string();

		    // get the type modifier
		    _type_modifier = _encap.read_short();

		    // get the type aliased
		    _concrete_base = new TypeCodeImpl(_orb);
		    _concrete_base.read_value_recursive(_encap);
		    if (_concrete_base.kind().value() == TCKind._tk_null) {
			_concrete_base = null;
		    }

		    // get the number of members
		    _memberCount = _encap.read_long();

		    // create arrays for the names, types and visibility of members
		    _memberNames = new String[_memberCount];
		    _memberTypes = new TypeCodeImpl[_memberCount];
		    _memberAccess = new short[_memberCount];

		    // read off value member visibilities
		    for (int i=0; i < _memberCount; i++) {
			_memberNames[i] = _encap.read_string();
			_memberTypes[i] = new TypeCodeImpl(_orb);
                        //if (debug) System.out.println("TypeCode " + _name + " reading member " + _memberNames[i]);
			_memberTypes[i].read_value_recursive(_encap);
			_memberTypes[i].setParent(this);
			_memberAccess[i] = _encap.read_short();
		    }
		}
		break;

	    default:
		throw new MARSHAL();
	    }
	    break;
	}
    }
}

public void write_value(OutputStream os) {
    // Wrap OutputStream into TypeCodeOutputStream.
    // This test shouldn't be necessary according to the Java language spec.
    if (os instanceof TypeCodeOutputStream) {
	this.write_value((TypeCodeOutputStream)os);
    } else {
        TypeCodeOutputStream wrapperOutStream = null;

        if (outBuffer == null) {
            wrapperOutStream = TypeCodeOutputStream.wrapOutputStream(os);
            this.write_value(wrapperOutStream);
            if (cachingEnabled) {
                // Cache the buffer for repeated writes
                outBuffer = wrapperOutStream.getTypeCodeBuffer();
                //if (outBuffer != null)
                    //System.out.println("Caching outBuffer with length = " + outBuffer.length +
                                       //" for id = " + _id);
            }
        } else {
            //System.out.println("Using cached outBuffer: length = " + outBuffer.length +
                               //", id = " + _id);
        }
        // Write the first 4 bytes first to trigger alignment.
        // We know that it is the kind.
        if (cachingEnabled && outBuffer != null) {
            os.write_long(_kind);
            os.write_octet_array(outBuffer, 0, outBuffer.length);
        } else {
            //System.out.println("Buffer is empty for " + _id);
            wrapperOutStream.writeRawBuffer(os, _kind);
        }
    }
}

public void write_value(TypeCodeOutputStream tcos) {

    // Don't do any work if this is native
    if (_kind == TCKind._tk_native)
        throw new MARSHAL();

    TypeCodeOutputStream topStream = tcos.getTopLevelStream();
    //if (debug) tcos.printBuffer();

    if (_kind == tk_indirect) {
        //if (debug) System.out.println("Writing indirection " + _name + "to " + _id);
	// The encoding used for indirection is the same as that used for recursive TypeCodes,
	// i.e., a 0xffffffff indirection marker followed by a long offset
	// (in units of octets) from the beginning of the long offset.
        int pos = topStream.getPositionForID(_id);
        int topPos = tcos.getTopLevelPosition();
        //if (debug) System.out.println("TypeCodeImpl " + tcos + " writing indirection " + _id +
            //" to position " + pos + " at position " + topPos);
        tcos.writeIndirection(tk_indirect, pos);
	// All that gets written is _kind and offset.
	return;
    }

    //if (debug) System.out.println("Writing " + _name + " with id " + _id);
    // We have to remember the stream and position for EVERY type code
    // in case some recursive or indirect type code references it.
    topStream.addIDAtPosition(_id, tcos.getTopLevelPosition());
    // marshal the kind
    tcos.write_long(_kind);

    switch (typeTable[_kind]) {
    case EMPTY:
	// nothing more to marshal
	break;
      
    case SIMPLE:
	switch (_kind) {
	case TCKind._tk_string:
	case TCKind._tk_wstring:
	    // marshal the bound on string length
	    tcos.write_long(_length);
	    break;
	case TCKind._tk_fixed:
	    tcos.write_ushort(_digits);
	    tcos.write_short(_scale);
	    break;
	default:
	    // unknown typecode kind
	    throw new MARSHAL();
	}
	break;
      
    case COMPLEX:
	{
	    // create an encapsulation
	    TypeCodeOutputStream _encap = tcos.createEncapsulation(_orb);

	    switch(_kind) {
	  
	    case TCKind._tk_objref:
	    case TCKind._tk_abstract_interface:
		{
		    // put the repository id
		    _encap.write_string(_id);
	    
		    // put the name
		    _encap.write_string(_name);
		}
		break;
	
	    case TCKind._tk_union:
		{
		    // put the repository id
		    _encap.write_string(_id);

		    // put the name
		    _encap.write_string(_name);

		    // discriminant typecode
		    _discriminator.write_value(_encap);

		    // default index
		    _encap.write_long(_defaultIndex);

		    // put the number of members
		    _encap.write_long(_memberCount);

		    // marshal label values, names and types
		    for (int i=0; i < _memberCount; i++) {

			// for the default case, marshal the zero octet
			if (i == _defaultIndex)
			    _encap.write_octet(_unionLabels[i].extract_octet());

			else {
			    switch (realType(_discriminator).kind().value()) {
			    case TCKind._tk_short:
				_encap.write_short(_unionLabels[i].extract_short());
				break;
			    case TCKind._tk_long:
				_encap.write_long(_unionLabels[i].extract_long());
				break;
			    case TCKind._tk_ushort:
				_encap.write_short(_unionLabels[i].extract_ushort());
				break;
			    case TCKind._tk_ulong:
				_encap.write_long(_unionLabels[i].extract_ulong());
				break;
			    case TCKind._tk_float:
				_encap.write_float(_unionLabels[i].extract_float());
				break;
			    case TCKind._tk_double:
				_encap.write_double(_unionLabels[i].extract_double());
				break;
			    case TCKind._tk_boolean:
				_encap.write_boolean(_unionLabels[i].extract_boolean());
				break;
			    case TCKind._tk_char:
				_encap.write_char(_unionLabels[i].extract_char());
				break;
			    case TCKind._tk_enum:
				_encap.write_long(_unionLabels[i].extract_long());
				break;
			    case TCKind._tk_longlong:
				_encap.write_longlong(_unionLabels[i].extract_longlong());
				break;
			    case TCKind._tk_ulonglong:
				_encap.write_longlong(_unionLabels[i].extract_ulonglong());
				break;
				// _REVISIT_ figure out long double mapping
				// case TCKind.tk_longdouble:
				// _encap.putDouble(_unionLabels[i].extract_longdouble());
				// break;
                            case TCKind._tk_wchar:
                                _encap.write_wchar(_unionLabels[i].extract_wchar());
                                break;
			    default:
				throw new MARSHAL();
			    }
			}
			_encap.write_string(_memberNames[i]);
			_memberTypes[i].write_value(_encap);
		    }
		}
		break;

	    case TCKind._tk_enum:
		{
		    // put the repository id
		    _encap.write_string(_id);

		    // put the name
		    _encap.write_string(_name);

		    // put the number of members
		    _encap.write_long(_memberCount);

		    // marshal identifier names
		    for (int i=0; i < _memberCount; i++)
			_encap.write_string(_memberNames[i]);
		}
		break;

	    case TCKind._tk_sequence:
		{
		    // put the type of the sequence
		    lazy_content_type().write_value(_encap);
	    
		    // put the bound on the length of the sequence
		    _encap.write_long(_length);
		}
		break;

	    case TCKind._tk_array:
		{
		    // put the type of the array
		    _contentType.write_value(_encap);
	    
		    // put the length of the array
		    _encap.write_long(_length);
		}
		break;

	    case TCKind._tk_alias:
	    case TCKind._tk_value_box:
		{
		    // put the repository id
		    _encap.write_string(_id);

		    // put the name
		    _encap.write_string(_name);

		    // put the type aliased
		    _contentType.write_value(_encap);
		}
		break;

	    case TCKind._tk_struct:
	    case TCKind._tk_except:
		{
		    // put the repository id
		    _encap.write_string(_id);

		    // put the name
		    _encap.write_string(_name);

		    // put the number of members
		    _encap.write_long(_memberCount);

		    // marshal member names and types
		    for (int i=0; i < _memberCount; i++) {
			_encap.write_string(_memberNames[i]);
                        //if (debug) System.out.println("TypeCode " + _name + " writing member " + _memberNames[i]);
			_memberTypes[i].write_value(_encap);
		    }
		}
		break;
	
	    case TCKind._tk_value:
		{
		    // put the repository id
		    _encap.write_string(_id);

		    // put the name
		    _encap.write_string(_name);

		    // put the type modifier
		    _encap.write_short(_type_modifier);

		    // put the type aliased
		    if (_concrete_base == null) {
			primitiveConstants[TCKind._tk_null].write_value(_encap);
		    } else {
			_concrete_base.write_value(_encap);
		    }

		    // put the number of members
		    _encap.write_long(_memberCount);

		    // marshal member names and types
		    for (int i=0; i < _memberCount; i++) {
			_encap.write_string(_memberNames[i]);
                        //if (debug) System.out.println("TypeCode " + _name + " writing member " + _memberNames[i]);
			_memberTypes[i].write_value(_encap);
			_encap.write_short(_memberAccess[i]);
		    }
		}
		break;
          
	    default:
		throw new MARSHAL();
	    }

	    // marshal the encapsulation
	    _encap.writeOctetSequenceTo(tcos);
	    break;
	}
    }
}

/**
 * This is not a copy of the TypeCodeImpl objects, but instead it
 * copies the value this type code is representing.
 * See AnyImpl read_value and write_value for usage.
 * The state of this TypeCodeImpl instance isn't changed, only used
 * by the Any to do the correct copy.
 */
protected void copy(org.omg.CORBA.portable.InputStream src, org.omg.CORBA.portable.OutputStream dst)
{
    switch (_kind) {

    case TCKind._tk_null:
    case TCKind._tk_void:
    case TCKind._tk_native:
    case TCKind._tk_abstract_interface:
	break;

    case TCKind._tk_short:
    case TCKind._tk_ushort:
	dst.write_short(src.read_short());
	break;

    case TCKind._tk_long:
    case TCKind._tk_ulong:
	dst.write_long(src.read_long());
	break;

    case TCKind._tk_float:
	dst.write_float(src.read_float());
	break;

    case TCKind._tk_double:
	dst.write_double(src.read_double());
	break;

    case TCKind._tk_longlong:
    case TCKind._tk_ulonglong:
	dst.write_longlong(src.read_longlong());
	break;

    case TCKind._tk_longdouble:
        throw new org.omg.CORBA.NO_IMPLEMENT();

    case TCKind._tk_boolean:
	dst.write_boolean(src.read_boolean());
	break;
      
    case TCKind._tk_char:
	dst.write_char(src.read_char());
	break;

    case TCKind._tk_wchar:
	dst.write_wchar(src.read_wchar());
	break;
      
    case TCKind._tk_octet:
	dst.write_octet(src.read_octet());
	break;

    case TCKind._tk_string:
	{
	    String s;
	    s = src.read_string();
	    // make sure length bound in typecode is not violated
	    if ((_length != 0) && (s.length() > _length))
		throw new MARSHAL();
	    dst.write_string(s);
	}
	break;

    case TCKind._tk_wstring:
	{
	    String s;
	    s = src.read_wstring();
	    // make sure length bound in typecode is not violated
	    if ((_length != 0) && (s.length() > _length))
		throw new MARSHAL();
	    dst.write_wstring(s);
	}
	break;

    case TCKind._tk_fixed:
	{
	    dst.write_ushort(src.read_ushort());
	    dst.write_short(src.read_short());
	}
	break;

    case TCKind._tk_any: 
	{
	    //Any tmp = new AnyImpl(_orb);
	    Any tmp =  ((CDRInputStream)src).orb().create_any();
	    TypeCodeImpl t = new TypeCodeImpl(_orb);
	    t.read_value((org.omg.CORBA_2_3.portable.InputStream)src);
	    t.write_value((org.omg.CORBA_2_3.portable.OutputStream)dst);
	    tmp.read_value(src, t);
	    tmp.write_value(dst);
	    break;
	}
    
    case TCKind._tk_TypeCode: 
	{
	    dst.write_TypeCode(src.read_TypeCode());
	    break;
	}
    
    case TCKind._tk_Principal: 
	{
	    dst.write_Principal(src.read_Principal());
	    break;
	}

    case TCKind._tk_objref:
	{
	    dst.write_Object(src.read_Object());
	    break;
	}

    case TCKind._tk_except:
	// Copy repositoryId
	dst.write_string(src.read_string());

	// Fall into ...
    // _REVISIT_ what about the inherited members of this values concrete base type?
    case TCKind._tk_value:
    case TCKind._tk_struct:
	{
	    // copy each element, using the corresponding member type
	    for (int i=0; i < _memberTypes.length; i++) {
		_memberTypes[i].copy(src, dst);
	    }
	    break;
	}
    case TCKind._tk_union:
/* _REVISIT_ More generic code?
	{
	    Any discriminator = new AnyImpl(_orb);
            discriminator.read_value(src, _discriminator);
            discriminator.write_value(dst);
            int labelIndex = currentUnionMemberIndex(discriminator);
	    if (labelIndex == -1) {
		// check if label has not been found
		if (_defaultIndex == -1)
		    // throw exception if default was not expected
		    throw new MARSHAL();
		else 
		    // must be of the default branch type
		    _memberTypes[_defaultIndex].copy(src, dst);
	    } else {
                _memberTypes[labelIndex].copy(src, dst);
            }
        }
*/
	{
	    Any tagValue = new AnyImpl(_orb);

	    switch  (realType(_discriminator).kind().value()) {
	    case TCKind._tk_short:
		{
		    short value = src.read_short();
		    tagValue.insert_short(value);
		    dst.write_short(value);
		    break;
		}
	    case TCKind._tk_long:
		{
		    int value = src.read_long();
		    tagValue.insert_long(value);
		    dst.write_long(value);
		    break;
		}
	    case TCKind._tk_ushort:
		{
		    short value = src.read_short();
		    tagValue.insert_ushort(value);
		    dst.write_short(value);
		    break;
		}
	    case TCKind._tk_ulong:
		{
		    int value = src.read_long();
		    tagValue.insert_ulong(value);
		    dst.write_long(value);
		    break;
		}
	    case TCKind._tk_float:
		{
		    float value = src.read_float();
		    tagValue.insert_float(value);
		    dst.write_float(value);
		    break;
		}
	    case TCKind._tk_double:
		{
		    double value = src.read_double();
		    tagValue.insert_double(value);
		    dst.write_double(value);
		    break;
		}
	    case TCKind._tk_boolean:
		{
		    boolean value = src.read_boolean();
		    tagValue.insert_boolean(value);
		    dst.write_boolean(value);
		    break;
		}
	    case TCKind._tk_char:
		{
		    char value = src.read_char();
		    tagValue.insert_char(value);
		    dst.write_char(value);
		    break;
		}
	    case TCKind._tk_enum:
		{
		    int value = src.read_long();
		    tagValue.type(_discriminator);
		    tagValue.insert_long(value);
		    dst.write_long(value);
		    break;
		}
	    case TCKind._tk_longlong:
		{
		    long value = src.read_longlong();
		    tagValue.insert_longlong(value);
		    dst.write_longlong(value);
		    break;
		}
	    case TCKind._tk_ulonglong:
		{
		    long value = src.read_longlong();
		    tagValue.insert_ulonglong(value);
		    dst.write_longlong(value);
		    break;
		}
		// _REVISIT_ figure out long double mapping
		// case TCKind.tk_longdouble:
		// {
		// double value = src.read_double();
		//  tagValue.insert_longdouble(value);
		//  dst.putDouble(value);
		//  break;
		//}
            case TCKind._tk_wchar:
                {
                    char value = src.read_wchar();
                    tagValue.insert_wchar(value);
                    dst.write_wchar(value);
                    break;
                }
	    default:
                throw new MARSHAL();
	    }

	    // using the value of the tag, find out the type of the value
	    // following. 

	    int labelIndex;
	    for (labelIndex = 0; labelIndex < _unionLabels.length; labelIndex++) {
		// use equality over anys
		if (tagValue.equal(_unionLabels[labelIndex])) {
		    _memberTypes[labelIndex].copy(src, dst);
		    break;
		}
	    }

	    if (labelIndex == _unionLabels.length) {
		// check if label has not been found
		if (_defaultIndex == -1)
		    // throw exception if default was not expected
		    throw new MARSHAL();
		else 
		    // must be of the default branch type
		    _memberTypes[_defaultIndex].copy(src, dst);
	    }
	    break;
	}

    case TCKind._tk_enum:
	dst.write_long(src.read_long());
	break;
      
    case TCKind._tk_sequence:
	// get the length of the sequence
	int seqLength = src.read_long();

	// check for sequence bound violated
	if ((_length != 0) && (seqLength > _length))
	    throw new MARSHAL();

	// write the length of the sequence
	dst.write_long(seqLength);

	// copy each element of the seq using content type
	lazy_content_type(); // make sure it's resolved
	for (int i=0; i < seqLength; i++)
	    _contentType.copy(src, dst);
	break;

    case TCKind._tk_array:
	// copy each element of the array using content type
	for (int i=0; i < _length; i++)
	    _contentType.copy(src, dst);
	break;

    case TCKind._tk_alias:
    case TCKind._tk_value_box:
	// follow the alias
	_contentType.copy(src, dst);
	break;

    case tk_indirect:
	// need to follow offset, get unmarshal typecode from that
	// offset, and use that to do the copy
	// Don't need to read type code before using it to do the copy.
	// It should be fully usable.
	indirectType().copy(src, dst);
	break;

    default:
	throw new MARSHAL();
    }
}

/*
    // Provides a deep copy of this TypeCode instance down to primitive TypeCode constants.
    protected Object clone()
        throws CloneNotSupportedException
    {
        // Don't clone primitiveConstants
        if (get_primitive_tc(_kind) != null)
            return this;

        TypeCodeImpl clone = (TypeCodeImpl)super.clone();
        // clone _id
        if (_id != null) {
            clone._id = new String(_id);
        }
        // clone _name
        if (_name != null) {
            clone._name = new String(_name);
        }
        // clone _memberNames
        if (_memberNames != null) {
            clone._memberNames = new String[_memberNames.length];
            for (int i=0; i<_memberNames.length; i++) {
                clone._memberNames[i] = new String(_memberNames[i]);
            }
        }
        // clone _memberTypes (this won't clone primitiveConstants either)
        if (_memberTypes != null) {
            clone._memberTypes = new TypeCodeImpl[_memberTypes.length];
            for (int i=0; i<_memberTypes.length; i++) {
                clone._memberTypes[i] = convertToNative(_orb, _memberTypes[i]).clone();
                // This takes care of the _parent instance variable.
                clone._memberTypes[i].setParent(clone);
            }
        }
        // clone _unionLabels
        if (_unionLabels != null) {
            clone._unionLabels = new AnyImpl[_unionLabels.length];
            for (int i=0; i<_unionLabels.length; i++) {
		clone._unionLabels[i] = new AnyImpl(_orb, _unionLabels[i]);
            }
        }
        // clone _discriminator
        if (_discriminator != null) {
            clone._discriminator = _discriminator.clone();
        }
        // clone _contentType
        if (_contentType != null) {
            clone._contentType = _contentType.clone();
        }
        // clone _concrete_base
        if (_concrete_base != null) {
            clone._concrete_base = _concrete_base.clone();
        }
        // clone _unionLabels
        if (_memberAccess != null) {
            clone._memberAccess = new short[_memberAccess.length];
            for (int i=0; i<_memberAccess.length; i++) {
		clone._memberAccess[i] = _memberAccess[i];
            }
        }
        // nothing to clone for _indirectType
    }
*/

    static protected short digits(java.math.BigDecimal value) {
        if (value == null)
            return 0;
        short length = (short)value.unscaledValue().toString().length();
        if (value.signum() == -1)
            length--;
        return length;
    }

    static protected short scale(java.math.BigDecimal value) {
        if (value == null)
            return 0;
        return (short)value.scale();
    }

    // Utility methods

    // Only for union type. Returns the index of the union member
    // corresponding to the discriminator. If not found returns the
    // default index or -1 if there is no default index.
    int currentUnionMemberIndex(Any discriminatorValue) throws BadKind {
        if (_kind != TCKind._tk_union)
            throw new BadKind();

        try {
            for (int i=0; i<member_count(); i++) {
                if (member_label(i).equal(discriminatorValue)) {
                    return i;
                }
            }
            if (_defaultIndex != -1) {
                return _defaultIndex;
            }
        } catch (BadKind bad) {
        } catch (org.omg.CORBA.TypeCodePackage.Bounds bounds) {
        }
        return -1;
    }

    String description() {
        return "TypeCodeImpl with kind " + _kind + " and id " + _id;
    }

    public String toString() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1024);
        PrintStream printOut = new PrintStream(byteOut, true);
        printStream(printOut);
        return super.toString() + " =\n" + byteOut.toString();
    }

    public void printStream(PrintStream s) {
        printStream(s, 0);
    }

    private void printStream(PrintStream s, int level) {
        if (_kind == tk_indirect) {
            s.print("indirect " + _id);
            return;
        }

        switch (_kind) {
            case TCKind._tk_null:
            case TCKind._tk_void:
            case TCKind._tk_short:
            case TCKind._tk_long:
            case TCKind._tk_ushort:
            case TCKind._tk_ulong:
            case TCKind._tk_float:
            case TCKind._tk_double:
            case TCKind._tk_boolean:
            case TCKind._tk_char:
            case TCKind._tk_octet:
            case TCKind._tk_any:
            case TCKind._tk_TypeCode:
            case TCKind._tk_Principal:
            case TCKind._tk_objref:
            case TCKind._tk_longlong:
            case TCKind._tk_ulonglong:
            case TCKind._tk_longdouble:
            case TCKind._tk_wchar:
            case TCKind._tk_native:
                s.print(kindNames[_kind] + " " + _name);
	        break;

            case TCKind._tk_struct:
            case TCKind._tk_except:
            case TCKind._tk_value:
                s.println(kindNames[_kind] + " " + _name + " = {");
                for(int i=0; i<_memberCount; i++) {
                    // memberName might differ from the name of the member.
                    s.print(indent(level + 1));
                    if (_memberTypes[i] != null)
                        _memberTypes[i].printStream(s, level + 1);
                    else
                        s.print("<unknown type>");
                    s.println(" " + _memberNames[i] + ";");
                }
                s.print(indent(level) + "}");
	        break;

            case TCKind._tk_union:
                s.print("union " + _name + "...");
	        break;

            case TCKind._tk_enum:
                s.print("enum " + _name + "...");
	        break;

            case TCKind._tk_string:
                if (_length == 0)
                    s.print("unbounded string " + _name);
                else
                    s.print("bounded string(" + _length + ") " + _name);
	        break;

            case TCKind._tk_sequence:
            case TCKind._tk_array:
                s.println(kindNames[_kind] + "[" + _length + "] " + _name + " = {");
                s.print(indent(level + 1));
                if (lazy_content_type() != null) {
                    lazy_content_type().printStream(s, level + 1);
                }
                s.println(indent(level) + "}");
	        break;

            case TCKind._tk_alias:
                s.print("alias " + _name + " = " + (_contentType != null ? _contentType._name : "<unresolved>"));
	        break;

            case TCKind._tk_wstring:
                s.print("wstring[" + _length + "] " + _name);
	        break;

            case TCKind._tk_fixed:
                s.print("fixed(" + _digits + ", " + _scale + ") " + _name);
	        break;

            case TCKind._tk_value_box:
                s.print("valueBox " + _name + "...");
	        break;

            case TCKind._tk_abstract_interface:
                s.print("abstractInterface " + _name + "...");
	        break;

            default:
                s.print("<unknown type>");
	        break;
        }
    }

    private String indent(int level) {
        String indent = "";
        for(int i=0; i<level; i++) {
            indent += "  ";
        }
        return indent;
    }

    protected void setCaching(boolean enableCaching) {
        cachingEnabled = enableCaching;
        if (enableCaching == false)
            outBuffer = null;
    }
}
