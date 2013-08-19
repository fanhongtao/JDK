/*
 * @(#)IIOPOutputStream.java	1.30 03/01/23
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

package com.sun.corba.se.internal.io;

import org.omg.CORBA.portable.OutputStream;
import java.io.IOException;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.io.InvalidClassException;
import java.io.StreamCorruptedException;
import java.io.Externalizable;
import java.io.ObjectStreamException;
import java.io.NotSerializableException;
import java.io.NotActiveException;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import com.sun.corba.se.internal.io.ObjectStreamClass;
import com.sun.corba.se.internal.util.Utility;
import com.sun.corba.se.internal.util.RepositoryId;
import javax.rmi.CORBA.Util;

/**
 * IIOPOutputStream is ...
 *
 * @author  Stephen Lewallen
 * @version 0.01, 4/6/98
 * @since   JDK1.1.6
 */

public class IIOPOutputStream
    extends com.sun.corba.se.internal.io.OutputStreamHook
{
    private static final byte kFormatVersionOne = 1;

    private org.omg.CORBA_2_3.portable.OutputStream orbStream;

    private Object currentObject = null;

    private ObjectStreamClass currentClassDesc = null;

    private int recursionDepth = 0;

    private int simpleWriteDepth = 0;

    private IOException abortIOException = null;

    private java.util.Stack classDescStack = new java.util.Stack();

    // Used when calling an object's writeObject method
    private Object[] writeObjectArglist = {this};

    public IIOPOutputStream()
	throws java.io.IOException
    {
	super();
    }

    public final void setOrbStream(org.omg.CORBA_2_3.portable.OutputStream os) {
    	orbStream = os;
    }

    public final org.omg.CORBA_2_3.portable.OutputStream getOrbStream() {
    	return orbStream;
    }

    public final void increaseRecursionDepth(){
	recursionDepth++;
    }

    public final int decreaseRecursionDepth(){
	return --recursionDepth;
    }

    /**
     * Override the actions of the final method "writeObject()"
     * in ObjectOutputStream.
     * @since     JDK1.1.6
     */
    public final void writeObjectDelegate(Object obj)
    /* throws IOException */
    {
	Util.writeAbstractObject((OutputStream)orbStream, obj);
    	//orbStream.write_value((java.io.Serializable)obj);
    }

    public final void writeObjectOverride(Object obj)
	throws IOException
    {
	writeObjectDelegate(obj);
    }

    /**
     * Override the actions of the final method "writeObject()"
     * in ObjectOutputStream.
     * @since     JDK1.1.6
     */
    public final void simpleWriteObject(Object obj)
    /* throws IOException */
    {


    	Object prevObject = currentObject;
    	ObjectStreamClass prevClassDesc = currentClassDesc;
    	simpleWriteDepth++;

    	try {
	    // if (!checkSpecialClasses(obj) && !checkSubstitutableSpecialClasses(obj))
	    outputObject(obj);

    	} catch (IOException ee) {
    	    if (abortIOException == null)
		abortIOException = ee;
    	} finally {
    	    /* Restore state of previous call incase this is a nested call */
    	    simpleWriteDepth--;
    	    currentObject = prevObject;
    	    currentClassDesc = prevClassDesc;
    	}

    	/* If the recursion depth is 0, test for and clear the pending exception.
    	 * If there is a pending exception throw it.
    	 */
    	IOException pending = abortIOException;
    	if (simpleWriteDepth == 0)
    	    abortIOException = null;
    	if (pending != null) {
    	    throwExceptionType(java.io.IOException.class, pending.getMessage());
    	}
    }

    // Required by the superclass.
    ObjectStreamField[] getFieldsNoCopy() {
        return currentClassDesc.getFieldsNoCopy();
    }

    /**
     * Override the actions of the final method "defaultWriteObject()"
     * in ObjectOutputStream.
     * @since     JDK1.1.6
     */
    public final void defaultWriteObjectDelegate()
    /* throws IOException */
    {
        try {
	    if (currentObject == null || currentClassDesc == null)
		throw new NotActiveException("defaultWriteObjectDelegate");

	    ObjectStreamField[] fields =
		currentClassDesc.getFieldsNoCopy();
	    if (fields.length > 0) {
		outputClassFields(currentObject, currentClassDesc.forClass(),
				  fields);
	    }
        }
        catch(IOException ioe)
	    {
		throwExceptionType(java.io.IOException.class, ioe.getMessage());
	    }
    }

    /**
     * Override the actions of the final method "enableReplaceObject()"
     * in ObjectOutputStream.
     * @since     JDK1.1.6
     */
    public final boolean enableReplaceObjectDelegate(boolean enable)
    /* throws SecurityException */
    {
        return false;
		
    }


    protected final void annotateClass(Class cl) throws IOException{
        throw new IOException("Method annotateClass not supported");
    }

    public final void close() throws IOException{
        // no op
    }

    protected final void drain() throws IOException{
        // no op
    }

    public final void flush() throws IOException{
        try{
            orbStream.flush();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    protected final Object replaceObject(Object obj) throws IOException{
        throw new IOException("Method replaceObject not supported");
    }

    /**
     * Reset will disregard the state of any objects already written
     * to the stream.  The state is reset to be the same as a new
     * ObjectOutputStream.  The current point in the stream is marked
     * as reset so the corresponding ObjectInputStream will be reset
     * at the same point.  Objects previously written to the stream
     * will not be refered to as already being in the stream.  They
     * will be written to the stream again.
     * @since     JDK1.1
     */
    public final void reset() throws IOException{
        try{
            //orbStream.reset();

	    if (currentObject != null || currentClassDesc != null)
		throw new IOException("Illegal call to reset");

	    abortIOException = null;

	    if (classDescStack == null)
		classDescStack = new java.util.Stack();
	    else
		classDescStack.setSize(0);

        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void write(byte b[]) throws IOException{
        try{
            orbStream.write_octet_array(b, 0, b.length);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void write(byte b[], int off, int len) throws IOException{
        try{
            orbStream.write_octet_array(b, off, len);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void write(int data) throws IOException{
        try{
            orbStream.write_octet((byte)(data & 0xFF));
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeBoolean(boolean data) throws IOException{
        try{
            orbStream.write_boolean(data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeByte(int data) throws IOException{
        try{
            orbStream.write_octet((byte)data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeBytes(String data) throws IOException{
        try{
            byte buf[] = data.getBytes();
            orbStream.write_octet_array(buf, 0, buf.length);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeChar(int data) throws IOException{
        try{
            orbStream.write_wchar((char)data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeChars(String data) throws IOException{
        try{
            char buf[] = data.toCharArray();
            orbStream.write_wchar_array(buf, 0, buf.length);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeDouble(double data) throws IOException{
        try{
            orbStream.write_double(data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeFloat(float data) throws IOException{
        try{
            orbStream.write_float(data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeInt(int data) throws IOException{
        try{
            orbStream.write_long(data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeLong(long data) throws IOException{
        try{
            orbStream.write_longlong(data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void writeShort(int data) throws IOException{
        try{
            orbStream.write_short((short)data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    protected final void writeStreamHeader() throws IOException{
        // no op
    }

    /**
     * Helper method for correcting the Kestrel bug 4367783 (dealing
     * with larger than 8-bit chars).  The old behavior is preserved
     * in orbutil.IIOPInputStream_1_3 in order to interoperate with
     * our legacy ORBs.
     */
    protected void internalWriteUTF(org.omg.CORBA.portable.OutputStream stream,
                                    String data) 
    {
        stream.write_wstring(data);
    }

    public final void writeUTF(String data) throws IOException{
        try{
            internalWriteUTF(orbStream, data);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    // INTERNAL UTILITY METHODS
    /*
     * Check for special cases of serializing objects.
     * These objects are not subject to replacement.
     */
    private boolean checkSpecialClasses(Object obj) throws IOException {

    	/*
    	 * If this is a class, don't allow substitution
    	 */
    	//if (obj instanceof Class) {
        //    throw new IOException("Serialization of Class not supported");
    	//}

    	if (obj instanceof ObjectStreamClass) {
            throw new IOException("Serialization of ObjectStreamClass not supported");
    	}

    	return false;
    }

    /*
     * Check for special cases of substitutable serializing objects.
     * These classes are replaceable.
     */
    private boolean checkSubstitutableSpecialClasses(Object obj)
	throws IOException
    {
    	if (obj instanceof String) {
    	    orbStream.write_value((java.io.Serializable)obj);
    	    return true;
    	}

    	//if (obj.getClass().isArray()) {
    	//    outputArray(obj);
    	//    return true;
    	//}

    	return false;
    }

    /*
     * Write out the object
     */
    private void outputObject(Object obj) throws IOException{

    	currentObject = obj;
    	Class currclass = obj.getClass();

    	/* Get the Class descriptor for this class,
    	 * Throw a NotSerializableException if there is none.
    	 */
    	currentClassDesc = ObjectStreamClass.lookup(currclass);
    	if (currentClassDesc == null) {
    	    throw new NotSerializableException(currclass.getName());
    	}

    	/* If the object is externalizable,
    	 * call writeExternal.
    	 * else do Serializable processing.
    	 */
    	if (currentClassDesc.isExternalizable()) {
	    // Write format version
	    writeByte(kFormatVersionOne);

    	    Externalizable ext = (Externalizable)obj;
    	    ext.writeExternal(this);
    	} else {

    	    /* The object's classes should be processed from supertype to subtype
    	     * Push all the clases of the current object onto a stack.
    	     * Remember the stack pointer where this set of classes is being pushed.
    	     */
    	    int stackMark = classDescStack.size();
    	    try {
    		ObjectStreamClass next;
    		while ((next = currentClassDesc.getSuperclass()) != null) {
    		    classDescStack.push(currentClassDesc);
    		    currentClassDesc = next;
    		}

    		/*
    		 * For currentClassDesc and all the pushed class descriptors
    		 *    If the class is writing its own data
    		 *		  set blockData = true; call the class writeObject method
    		 *    If not
    		 *     invoke either the defaultWriteObject method.
    		 */
    		do {
    		    if (currentClassDesc.hasWriteObject()) {
			invokeObjectWriter(obj, currentClassDesc.forClass());

    		    } else {
			defaultWriteObjectDelegate();
    		    }
    		} while (classDescStack.size() > stackMark &&
    			 (currentClassDesc = (ObjectStreamClass)classDescStack.pop()) != null);
    	    } finally {
		classDescStack.setSize(stackMark);
    	    }
    	}
    }

    /*
     * Invoke writer.
     * _REVISIT_ invokeObjectWriter and invokeObjectReader behave inconsistently with each other since
     * the reader returns a boolean...fix later
     */
    private void invokeObjectWriter(Object obj, Class c)
	throws IOException
    {
    	try {
			
	    // Write format version
	    writeByte(kFormatVersionOne);

	    // Write defaultWriteObject indicator
	    // - We write this as false, but may have to go back and undo this if it is later called

            // Changed to true since this is what will happen in most cases.  We still need
            // to go back and fix this at some point.
	    writeBoolean(true);

	    writeObject(obj, c, this);

    	} catch (InvocationTargetException e) {
    	    Throwable t = e.getTargetException();
    	    if (t instanceof IOException)
    		throw (IOException)t;
    	    else if (t instanceof RuntimeException)
    		throw (RuntimeException) t;
    	    else if (t instanceof Error)
    		throw (Error) t;
    	    else
    		throw new Error("invokeObjectWriter interal error");
    	} catch (IllegalAccessException e) {
    	    // cannot happen
    	} finally {
    	}
    }

    void writeField(ObjectStreamField field, Object value) throws IOException {
        switch (field.getTypeCode()) {
            case 'B':
                if (value == null)
                    orbStream.write_octet((byte)0);
                else
                    orbStream.write_octet(((Byte)value).byteValue());
		break;
	    case 'C':
                if (value == null)
                    orbStream.write_wchar((char)0);
                else
                    orbStream.write_wchar(((Character)value).charValue());
		break;
	    case 'F':
                if (value == null)
                    orbStream.write_float((float)0);
                else
                    orbStream.write_float(((Float)value).floatValue());
		break;
            case 'D':
                if (value == null)
                    orbStream.write_double((double)0);
                else
                    orbStream.write_double(((Double)value).doubleValue());
		break;
	    case 'I':
                if (value == null)
                    orbStream.write_long((int)0);
                else
                    orbStream.write_long(((Integer)value).intValue());
		break;
	    case 'J':
                if (value == null)
                    orbStream.write_longlong((long)0);
                else
                    orbStream.write_longlong(((Long)value).longValue());
		break;
	    case 'S':
                if (value == null)
                    orbStream.write_short((short)0);
                else
                    orbStream.write_short(((Short)value).shortValue());
		break;
	    case 'Z':
                if (value == null)
                    orbStream.write_boolean(false);
                else
                    orbStream.write_boolean(((Boolean)value).booleanValue());
		break;
	    case '[':
	    case 'L':
                // What to do if it's null?
                writeObjectField(field, value);
		break;
	    default:
		throw new InvalidClassException(currentClassDesc.getName());
	    }
    }

    private void writeObjectField(ObjectStreamField field,
                                  Object objectValue) throws IOException {

        if (ObjectStreamClassCorbaExt.isAny(field.getTypeString())) {
            javax.rmi.CORBA.Util.writeAny(orbStream, objectValue);
        }
        else {
            Class type = field.getType();
            int callType = ValueHandlerImpl.kValueType;
					
            if (type.isInterface()) { 
                String className = type.getName();
                
                if (java.rmi.Remote.class.isAssignableFrom(type)) {
                    
                    // RMI Object reference...
                    
                    callType = ValueHandlerImpl.kRemoteType;
                    
                    
                } else if (org.omg.CORBA.Object.class.isAssignableFrom(type)){
                    
                    // IDL Object reference...
                    callType = ValueHandlerImpl.kRemoteType;
                    
                } else if (RepositoryId.isAbstractBase(type)) {
                    // IDL Abstract Object reference...
                    callType = ValueHandlerImpl.kAbstractType;
                } else if (ObjectStreamClassCorbaExt.isAbstractInterface(type)) {
                    callType = ValueHandlerImpl.kAbstractType;
                }
            }
					
            switch (callType) {
            case ValueHandlerImpl.kRemoteType: 
                Util.writeRemoteObject(orbStream, objectValue);
                break;
            case ValueHandlerImpl.kAbstractType: 
                Util.writeAbstractObject(orbStream, objectValue);
                break;
            case ValueHandlerImpl.kValueType:
                try{
                    orbStream.write_value((java.io.Serializable)objectValue, type);
                }
                catch(ClassCastException cce){
                    if (objectValue instanceof java.io.Serializable)
                        throw cce;
                    else
                        Utility.throwNotSerializableForCorba(objectValue.getClass().getName());
                }
            }
        }
    }

    /* Write the fields of the specified class by invoking the appropriate
     * write* method on this class.
     */
    private void outputClassFields(Object o, Class cl,
				   ObjectStreamField[] fields)
	throws IOException, InvalidClassException {

    	for (int i = 0; i < fields.length; i++) {
    	    if (fields[i].getField() == null)
    		throw new InvalidClassException(cl.getName(),
						"Nonexistent field " + fields[i].getName());

	    switch (fields[i].getTypeCode()) {
	    case 'B':
		byte byteValue = getByteFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_octet(byteValue);
		break;
	    case 'C':
		char charValue = getCharFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_wchar(charValue);
		break;
	    case 'F':
		float floatValue = getFloatFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_float(floatValue);
		break;
	    case 'D' :
		double doubleValue = getDoubleFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_double(doubleValue);
		break;
	    case 'I':
		int intValue = getIntFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_long(intValue);
		break;
	    case 'J':
		long longValue = getLongFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_longlong(longValue);
		break;
	    case 'S':
		short shortValue = getShortFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_short(shortValue);
		break;
	    case 'Z':
		boolean booleanValue = getBooleanFieldOpt(o, fields[i].getFieldID(cl));
		orbStream.write_boolean(booleanValue);
		break;
	    case '[':
	    case 'L':
		Object objectValue = getObjectFieldOpt(o, fields[i].getFieldID(cl));
                writeObjectField(fields[i], objectValue);
                break;
	    default:
		throw new InvalidClassException(cl.getName());
	    }
    	}
    }

    /* Create a pending exception.  This is needed to get around
     * the fact that the *Delegate methods do not explicitly
     * declare that they throw exceptions.  
     *
     * This native method creates an exception of the given type with
     * the given message string and posts it to the pending queue.
     */
    private static native void throwExceptionType(Class c, String message);

    private static native Object getObjectFieldOpt(Object o, long fieldID);
    private static native boolean getBooleanFieldOpt(Object o, long fieldID);
    private static native byte getByteFieldOpt(Object o, long fieldID);
    private static native char getCharFieldOpt(Object o, long fieldID);
    private static native short getShortFieldOpt(Object o, long fieldID);
    private static native int getIntFieldOpt(Object o, long fieldID);
    private static native long getLongFieldOpt(Object o, long fieldID);
    private static native float getFloatFieldOpt(Object o, long fieldID);
    private static native double getDoubleFieldOpt(Object o, long fieldID);

    private static native void writeObject(Object obj, Class asClass, Object oos)
        throws InvocationTargetException, IllegalAccessException;

}

