/*
 * @(#)IIOPInputStream.java	1.48 03/01/23
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

import java.io.InputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputValidation;
import java.io.NotActiveException;
import java.io.InvalidObjectException;
import java.io.InvalidClassException;
import java.io.DataInputStream;
import java.io.OptionalDataException;
import java.io.WriteAbortedException;
import java.io.Externalizable;
import java.io.EOFException;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;

import com.sun.corba.se.internal.io.ObjectStreamClass;
import com.sun.corba.se.internal.util.Utility;
import com.sun.corba.se.internal.util.MinorCodes;
import com.sun.corba.se.internal.util.Utility;

import org.omg.CORBA.ValueMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB; //d11638
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TypeCode;

import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;  // d11638

import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import java.util.*;

/**
 * IIOPInputStream is used by the ValueHandlerImpl to handle Java serialization
 * input semantics.
 *
 * @author  Stephen Lewallen
 * @since   JDK1.1.6
 */

public class IIOPInputStream
    extends com.sun.corba.se.internal.io.InputStreamHook
{
    // Necessary to pass the appropriate fields into the
    // defaultReadObjectDelegate method (which takes no
    // parameters since it's called from 
    // java.io.ObjectInpuStream defaultReadObject()
    // which we can't change).
    //
    // This is only used in the case where the fields had 
    // to be obtained remotely because of a serializable
    // version difference.  Set in inputObjectUsingFVD.
    // Part of serialization evolution fixes for Ladybird,
    // bug 4365188.
    private ValueMember defaultReadObjectFVDMembers[] = null;

    private org.omg.CORBA_2_3.portable.InputStream orbStream;

    private CodeBase cbSender;  //d11638

    private ValueHandlerImpl vhandler;  //d4365188

    private Object currentObject = null;

    private ObjectStreamClass currentClassDesc = null;

    private Class currentClass = null;

    private int recursionDepth = 0;

    private int simpleReadDepth = 0;

    // The ActiveRecursionManager replaces the old RecursionManager which
    // used to record how many recursions were made, and resolve them after
    // an object was completely deserialized.
    //
    // That created problems (as in bug 4414154) because when custom
    // unmarshaling in readObject, there can be recursive references
    // to one of the objects currently being unmarshaled, and the
    // passive recursion system failed.
    ActiveRecursionManager activeRecursionMgr = new ActiveRecursionManager();

    private IOException abortIOException = null;

    /* Remember the first exception that stopped this stream. */
    private ClassNotFoundException abortClassNotFoundException = null;

    /* Vector of validation callback objects
     * The vector is created as needed. The vector is maintained in
     * order of highest (first) priority to lowest
     */
    private Vector callbacks;

    // Serialization machinery fields
    /* Arrays used to keep track of classes and ObjectStreamClasses
     * as they are being merged; used in inputObject.
     * spClass is the stack pointer for both.  */
    ObjectStreamClass[] classdesc;
    Class[] classes;
    int spClass;

    private static final String kEmptyStr = "";

    // TCKind TypeCodes used in FVD inputClassFields
    //public static final TypeCode kRemoteTypeCode = new TypeCodeImpl(TCKind._tk_objref);
    //public static final TypeCode kValueTypeCode =  new TypeCodeImpl(TCKind._tk_value);
    //d11638 removed TypeCodeImpl dependency
    public static final TypeCode kRemoteTypeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
    public static final TypeCode kValueTypeCode =  ORB.init().get_primitive_tc(TCKind.tk_value);


    // TESTING CODE - useFVDOnly should be made final before FCS in order to
    // optimize out the check.
    private static final boolean useFVDOnly = false;
	
    public static void setTestFVDFlag(boolean val){
	//  useFVDOnly = val;
    }

    /**
     * Dummy constructor; passes upper stream a dummy stream;
     **/
    public IIOPInputStream()
    	throws java.io.IOException {
    	super();
	resetStream();
    }
	
    public final void setOrbStream(org.omg.CORBA_2_3.portable.InputStream os) {
    	orbStream = os;
    }

    public final org.omg.CORBA_2_3.portable.InputStream getOrbStream() {
    	return orbStream;
    }

    //d11638 added setSender and getSender
    public final void setSender(CodeBase cb) {
        cbSender = cb;
    }

    public final CodeBase getSender() {
        return cbSender;
    }

    // 4365188 this is added to enable backward compatability w/ wrong
    // rep-ids
    public final void setValueHandler(ValueHandler vh) {
        vhandler = (com.sun.corba.se.internal.io.ValueHandlerImpl) vh;
    }

    public final ValueHandler getValueHandler() {
	return (javax.rmi.CORBA.ValueHandler) vhandler;
    }
	
    public final void increaseRecursionDepth(){
	recursionDepth++;
    }

    public final int decreaseRecursionDepth(){
	return --recursionDepth;
    }

    /**
     * Override the actions of the final method "readObject()"
     * in ObjectInputStream.
     * @since     JDK1.1.6
     *
     * Read an object from the ObjectInputStream.
     * The class of the object, the signature of the class, and the values
     * of the non-transient and non-static fields of the class and all
     * of its supertypes are read.  Default deserializing for a class can be
     * overriden using the writeObject and readObject methods.
     * Objects referenced by this object are read transitively so
     * that a complete equivalent graph of objects is reconstructed by readObject. <p>
     *
     * The root object is completly restored when all of its fields
     * and the objects it references are completely restored.  At this
     * point the object validation callbacks are executed in order
     * based on their registered priorities. The callbacks are
     * registered by objects (in the readObject special methods)
     * as they are individually restored.
     *
     * Exceptions are thrown for problems with the InputStream and for classes
     * that should not be deserialized.  All exceptions are fatal to the
     * InputStream and leave it in an indeterminate state; it is up to the caller
     * to ignore or recover the stream state.
     * @exception java.lang.ClassNotFoundException Class of a serialized object
     *      cannot be found.
     * @exception InvalidClassException Something is wrong with a class used by
     *     serialization.
     * @exception StreamCorruptedException Control information in the
     *     stream is inconsistent.
     * @exception OptionalDataException Primitive data was found in the
     * stream instead of objects.
     * @exception IOException Any of the usual Input/Output related exceptions.
     * @since     JDK1.1
     */
    public final Object readObjectDelegate() throws IOException
    {
	try
	    {
		return orbStream.read_abstract_interface();
	    }
	catch(IndirectionException cdrie)
	    {
                // The CDR stream had never seen the given offset before,
                // so check the recursion manager (it will throw an
                // IOException if it doesn't have a reference, either).
                return activeRecursionMgr.getObject(cdrie.offset);
	    }
    }

    final Object simpleReadObject(Class clz,
                                  String repositoryID,
                                  com.sun.org.omg.SendingContext.CodeBase sender,
                                  int offset)
					 /* throws OptionalDataException, ClassNotFoundException, IOException */
    {

    	/* Save the current state and get ready to read an object. */
    	Object prevObject = currentObject;
    	ObjectStreamClass prevClass = currentClassDesc;

    	simpleReadDepth++;	// Entering
    	Object obj = null;

    	/*
    	 * Check for reset, handle it before reading an object.
    	 */
    	try {
	    // d4365188: backward compatability
	    if (vhandler.useFullValueDescription(clz, repositoryID)) {
		obj = inputObjectUsingFVD(clz, repositoryID, sender, offset);
	    } else {
                obj = inputObject(clz, repositoryID, sender, offset);
	    }

	    obj = currentClassDesc.readResolve(obj);
    	}
    	catch(ClassNotFoundException cnfe)
	    {
		throwExceptionType(ClassNotFoundException.class, cnfe.getMessage());
		return null;
	    }
    	catch(IOException ioe)
	    {
		// System.out.println("CLZ = " + clz + "; " + ioe.toString());
		throwExceptionType(IOException.class, ioe.getMessage());
		return null;
	    }
    	finally {
    	    simpleReadDepth --;
    	    currentObject = prevObject;
    	    currentClassDesc = prevClass;
    	}


    	/* Check for thrown exceptions and re-throw them, clearing them if
    	 * this is the last recursive call .
    	 */
    	IOException exIOE = abortIOException;
    	if (simpleReadDepth == 0)
    	    abortIOException = null;
    	if (exIOE != null){
            throwExceptionType(IOException.class, exIOE.getMessage());
            return null;
    	}


    	ClassNotFoundException exCNF = abortClassNotFoundException;
    	if (simpleReadDepth == 0)
    	    abortClassNotFoundException = null;
    	if (exCNF != null) {
            throwExceptionType(ClassNotFoundException.class, exCNF.getMessage());
            return null;
    	}

    	return obj;
    }

    public final void simpleSkipObject(String repositoryID,
				       com.sun.org.omg.SendingContext.CodeBase sender)
				       /* throws OptionalDataException, ClassNotFoundException, IOException */
    {
				
    	/* Save the current state and get ready to read an object. */
    	Object prevObject = currentObject;
    	ObjectStreamClass prevClass = currentClassDesc;

    	simpleReadDepth++;	// Entering
    	Object obj = null;

    	/*
    	 * Check for reset, handle it before reading an object.
    	 */
    	try {
	    skipObjectUsingFVD(repositoryID, sender);
    	}
    	catch(ClassNotFoundException cnfe)
	    {
		throwExceptionType(ClassNotFoundException.class, cnfe.getMessage());
		return;
	    }
    	catch(IOException ioe)
	    {
		throwExceptionType(IOException.class, ioe.getMessage());
		return;
	    }
    	finally {
    	    simpleReadDepth --;
    	    currentObject = prevObject;
    	    currentClassDesc = prevClass;
    	}


    	/* Check for thrown exceptions and re-throw them, clearing them if
    	 * this is the last recursive call .
    	 */
    	IOException exIOE = abortIOException;
    	if (simpleReadDepth == 0)
    	    abortIOException = null;
    	if (exIOE != null){
            throwExceptionType(IOException.class, exIOE.getMessage());
            return;
    	}


    	ClassNotFoundException exCNF = abortClassNotFoundException;
    	if (simpleReadDepth == 0)
    	    abortClassNotFoundException = null;
    	if (exCNF != null) {
            throwExceptionType(ClassNotFoundException.class, exCNF.getMessage());
            return;
    	}

	return;
    }
    /////////////////

    /**
     * This method is called by trusted subclasses of ObjectOutputStream
     * that constructed ObjectOutputStream using the
     * protected no-arg constructor. The subclass is expected to provide
     * an override method with the modifier "final".
     *
     * @return the Object read from the stream.
     *
     * @see #ObjectInputStream()
     * @see #readObject
     * @since JDK 1.2
     */
    protected final Object readObjectOverride()
 	throws OptionalDataException, ClassNotFoundException, IOException
    {
        return readObjectDelegate();
    }

    /**
     * Override the actions of the final method "defaultReadObject()"
     * in ObjectInputStream.
     * @since     JDK1.1.6
     *
     * Read the non-static and non-transient fields of the current class
     * from this stream.  This may only be called from the readObject method
     * of the class being deserialized. It will throw the NotActiveException
     * if it is called otherwise.
     *
     * @exception java.lang.ClassNotFoundException if the class of a serialized
     *              object could not be found.
     * @exception IOException        if an I/O error occurs.
     * @exception NotActiveException if the stream is not currently reading
     *              objects.
     * @since     JDK1.1
     */
    public final void defaultReadObjectDelegate()
    /* throws IOException, ClassNotFoundException, NotActiveException */
    {
        try {
	    if (currentObject == null || currentClassDesc == null)
		throw new NotActiveException("defaultReadObjectDelegate");

            // The array will be null unless fields were retrieved
            // remotely because of a serializable version difference.
            // Bug fix for 4365188.  See the definition of
            // defaultReadObjectFVDMembers for more information.
            if (defaultReadObjectFVDMembers != null &&
                defaultReadObjectFVDMembers.length > 0) {

                // WARNING:  Be very careful!  What if some of
                // these fields actually have to do this, too?
                // This works because the defaultReadObjectFVDMembers
                // reference is passed to inputClassFields, but
                // there is no guarantee that
                // defaultReadObjectFVDMembers will point to the
                // same array after calling inputClassFields.

                // Use the remote fields to unmarshal.
                inputClassFields(currentObject, 
                                 currentClass, 
                                 currentClassDesc,
                                 defaultReadObjectFVDMembers,
                                 cbSender);

            } else {

                // Use the local fields to unmarshal.
                ObjectStreamField[] fields =
                    currentClassDesc.getFieldsNoCopy();
                if (fields.length > 0) {
                    inputClassFields(currentObject, currentClass, fields, cbSender); //d11638
                }
            }
        }
        catch(NotActiveException nae)
	    {
		throwExceptionType(java.io.NotActiveException.class, nae.getMessage());
	    }
        catch(IOException ioe)
	    {
		throwExceptionType(java.io.IOException.class, ioe.getMessage());
	    }
        catch(ClassNotFoundException cnfe)
	    {
		throwExceptionType(java.lang.ClassNotFoundException.class, cnfe.getMessage());
	    }

    }

    /**
     * Override the actions of the final method "enableResolveObject()"
     * in ObjectInputStream.
     * @since     JDK1.1.6
     *
     * Enable the stream to allow objects read from the stream to be replaced.
     * If the stream is a trusted class it is allowed to enable replacment.
     * Trusted classes are those classes with a classLoader equals null. <p>
     *
     * When enabled the resolveObject method is called for every object
     * being deserialized.
     *
     * @exception SecurityException The classloader of this stream object is non-null.
     * @since     JDK1.1
     */
    public final boolean enableResolveObjectDelegate(boolean enable)
    /* throws SecurityException */
    {
	return false;
    }

    // The following three methods allow the implementing orbStream
    // to provide mark/reset behavior as defined in java.io.InputStream.

    public final void mark(int readAheadLimit) {
        orbStream.mark(readAheadLimit);
    }
    
    public final boolean markSupported() {
        return orbStream.markSupported();
    }
    
    public final void reset() throws IOException {
        try {
            orbStream.reset();
        } catch (Error e) {
            throw new IOException(e.getMessage());
        }
    }

    public final int available() throws IOException{
        return 0; // unreliable
    }

    public final void close() throws IOException{
        // no op
    }

    public final int read() throws IOException{
        try{
            return (orbStream.read_octet() << 0) & 0x000000FF;
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final int read(byte data[], int offset, int length) throws IOException{
        try{
            orbStream.read_octet_array(data, offset, length);
            return length;
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }

    }

    public final boolean readBoolean() throws IOException{
        try{
            return orbStream.read_boolean();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final byte readByte() throws IOException{
        try{
            return orbStream.read_octet();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final char readChar() throws IOException{
        try{
            return orbStream.read_wchar();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final double readDouble() throws IOException{
        try{
            return orbStream.read_double();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final float readFloat() throws IOException{
        try{
            return orbStream.read_float();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final void readFully(byte data[]) throws IOException{
// d11623 : implement readFully, required for serializing some core classes
        readFully(data, 0, data.length);
    }

    public final void readFully(byte data[],  int offset,  int size) throws IOException{
// d11623 : implement readFully, required for serializing some core classes
        try{
            orbStream.read_octet_array(data, offset, size);
        }
        catch(Error e)
            {
                throw new IOException(e.getMessage());
            }
    }

    public final int readInt() throws IOException{
        try{
            return orbStream.read_long();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final String readLine() throws IOException{
        throw new IOException("Method readLine not supported");
    }

    public final long readLong() throws IOException{
        try{
            return orbStream.read_longlong();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final short readShort() throws IOException{
        try{
            return orbStream.read_short();
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    protected final void readStreamHeader() throws IOException, StreamCorruptedException{
        // no op
    }

    public final int readUnsignedByte() throws IOException{
        try{
    	    return (orbStream.read_octet() << 0) & 0x000000FF;
    }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final int readUnsignedShort() throws IOException{
        try{
    	    return (orbStream.read_ushort() << 0) & 0x0000FFFF;
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    /**
     * Helper method for correcting the Kestrel bug 4367783 (dealing
     * with larger than 8-bit chars).  The old behavior is preserved
     * in orbutil.IIOPInputStream_1_3 in order to interoperate with
     * our legacy ORBs.
     */
    protected String internalReadUTF(org.omg.CORBA.portable.InputStream stream)
    {
        return stream.read_wstring();
    }

    public final String readUTF() throws IOException{
        try{
            return internalReadUTF(orbStream);
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    public final synchronized void registerValidation(ObjectInputValidation obj,
						      int prio)
	throws NotActiveException, InvalidObjectException{
        throw new Error("Method registerValidation not supported");
    }

    protected final Class resolveClass(ObjectStreamClass v)
	throws IOException, ClassNotFoundException{
        throw new IOException("Method resolveClass not supported");
    }

    protected final Object resolveObject(Object obj) throws IOException{
        throw new IOException("Method resolveObject not supported");
    }

    public final int skipBytes(int len) throws IOException{
        try{
            byte buf[] = new byte[len];
            orbStream.read_octet_array(buf, 0, len);
            return len;
        }
        catch(Error e)
	    {
		throw new IOException(e.getMessage());
	    }
    }

    private Object inputObject(Class clz,
			       String repositoryID,
			       com.sun.org.omg.SendingContext.CodeBase sender,
                               int offset)
	throws IOException, ClassNotFoundException
    {

    	/*
    	 * Get the descriptor and then class of the incoming object.
    	 */

    	currentClassDesc = ObjectStreamClass.lookup(clz);
    	currentClass = currentClassDesc.forClass();
    	//currentClassDesc.setClass(currentClass);
    	if (currentClass == null)
    	    throw new ClassNotFoundException(currentClassDesc.getName());


        try {

            /* If Externalizable,
             *  Create an instance and tell it to read its data.
             * else,
             *  Handle it as a serializable class.
             */
            if (currentClassDesc.isExternalizable()) {
                try {
                    currentObject = (currentClass == null) ?
                        null : allocateNewObject(currentClass, currentClass);
                    if (currentObject != null) {

                        // Store this object and its beginning position
                        // since there might be indirections to it while
                        // it's been unmarshalled.
                        activeRecursionMgr.addObject(offset, currentObject);

                        // Read format version
                        readByte();

                        Externalizable ext = (Externalizable)currentObject;
                        ext.readExternal(this);
		}
	    }
	    catch (NoSuchMethodError e) {
		throw new InvalidClassException(currentClass.getName(),
						"NoSuchMethodError accessing no-arg constructor");
    	    }
    	    catch (IllegalAccessException e) {
		throw new InvalidClassException(currentClass.getName(),
						"IllegalAccessException");
    	    }
    	    catch (InstantiationException e) {
		throw new InvalidClassException(currentClass.getName(),
						"InstantiationException");
    	    }
    	} // end : if (currentClassDesc.isExternalizable())
	else {
    	    /* Count number of classes and descriptors we might have
    	     * to work on.
    	     */

    	    ObjectStreamClass currdesc = currentClassDesc;
    	    Class currclass = currentClass;

    	    int spBase = spClass;	// current top of stack

    	    /* The object's classes should be processed from supertype to subtype
    	     * Push all the clases of the current object onto a stack.
    	     * Note that only the serializable classes are represented
    	     * in the descriptor list.
    	     *
    	     * Handle versioning where one or more supertypes of
    	     * have been inserted or removed.  The stack will
    	     * contain pairs of descriptors and the corresponding
    	     * class.  If the object has a class that did not occur in
    	     * the original the descriptor will be null.  If the
    	     * original object had a descriptor for a class not
    	     * present in the local hierarchy of the object the class will be
    	     * null.
    	     *
    	     */

    	    /*
    	     * This is your basic diff pattern, made simpler
    	     * because reordering is not allowed.
    	     */
            // sun.4296963 ibm.11861 
            // d11861 we should stop when we find the highest serializable class
            // We need this so that when we allocate the new object below, we
            // can call the constructor of the non-serializable superclass.
            // Note that in the JRMP variant of this code the
            // ObjectStreamClass.lookup() method handles this, but we've put
            // this fix here rather than change lookup because the new behaviour
            // is needed in other cases.

    	    for (currdesc = currentClassDesc, currclass = currentClass;
    		 currdesc != null && currdesc.isSerializable();   /*sun.4296963 ibm.11861*/
    		 currdesc = currdesc.getSuperclass()) {

    		/*
    		 * Search the classes to see if the class of this
    		 * descriptor appears further up the hierarchy. Until
    		 * it's found assume its an inserted class.  If it's
    		 * not found, its the descriptor's class that has been
    		 * removed.
    		 */
    		Class cc = currdesc.forClass();
    		Class cl;
    		for (cl = currclass; cl != null; cl = cl.getSuperclass()) {
    		    if (cc == cl) {
    			// found a superclass that matches this descriptor
    			break;
    		    } else {
    			/* Ignore a class that doesn't match.  No
    			 * action is needed since it is already
    			 * initialized.
    			 */
    		    }
		} // end : for (cl = currclass; cl != null; cl = cl.getSuperclass()) 
    		/* Test if there is room for this new entry.
    		 * If not, double the size of the arrays and copy the contents.
    		 */
    		spClass++;
    		if (spClass >= classes.length) {
    		    int newlen = classes.length * 2;
    		    Class[] newclasses = new Class[newlen];
    		    ObjectStreamClass[] newclassdesc = new ObjectStreamClass[newlen];

    		    System.arraycopy(classes, 0,
				     newclasses, 0,
				     classes.length);
    		    System.arraycopy(classdesc, 0,
    				     newclassdesc, 0,
    				     classes.length);

    		    classes = newclasses;
    		    classdesc = newclassdesc;
    		}

    		if (cl == null) {
    		    /* Class not found corresponding to this descriptor.
    		     * Pop off all the extra classes pushed.
    		     * Push the descriptor and a null class.
    		     */
    		    classdesc[spClass] = currdesc;
    		    classes[spClass] = null;
    		} else {
		    /* Current class descriptor matches current class.
    		     * Some classes may have been inserted.
    		     * Record the match and advance the class, continue
    		     * with the next descriptor.
    		     */
    		    classdesc[spClass] = currdesc;
    		    classes[spClass] = cl;
    		    currclass = cl.getSuperclass();
    		}
    	    } // end : for (currdesc = currentClassDesc, currclass = currentClass;

    	    /* Allocate a new object.  The object is only constructed
    	     * above the highest serializable class and is set to
    	     * default values for all more specialized classes.
    	     */
    	    try {
    		currentObject = (currentClass == null) ?
		    null : allocateNewObject(currentClass, currclass);

                // Store this object and its beginning position
                // since there might be indirections to it while
                // it's been unmarshalled.
                activeRecursionMgr.addObject(offset, currentObject);

    	    } catch (NoSuchMethodError e) {
    		throw new InvalidClassException(currclass.getName(),
    						"NoSuchMethodError accessing no-arg constructor");
    	    } catch (IllegalAccessException e) {
    		throw new InvalidClassException(currclass.getName(),
    						"IllegalAccessException");
    	    } catch (InstantiationException e) {
		throw new InvalidClassException("UNKNOWN",//currclass.getName(),
    						"InstantiationException");
    	    }

    	    /*
    	     * For all the pushed descriptors and classes.
    	     * 	if the class has its own writeObject and readObject methods
    	     *	    call the readObject method
    	     *	else
    	     *	    invoke the defaultReadObject method
    	     */
    	    try {
    		for (spClass = spClass; spClass > spBase; spClass--) {
    		    /*
    		     * Set current descriptor and corresponding class
    		     */
    		    currentClassDesc = classdesc[spClass];
    		    currentClass = classes[spClass];
    		    if (classes[spClass] != null) {
    			/* Read the data from the stream described by the
    			 * descriptor and store into the matching class.
    			 */


                        // Changed since invokeObjectReader no longer does this.
                        if (currentClassDesc.hasWriteObject()) {
			    // Read format version
                            readByte();
                            
			    // Read defaultWriteObject indicator
                            readBoolean();
                        }

                        if (!invokeObjectReader(currentClassDesc, currentObject, currentClass)) {

				ObjectStreamField[] fields =
				    currentClassDesc.getFieldsNoCopy();
				if (fields.length > 0) {
				    inputClassFields(currentObject, currentClass, fields, sender);
				}

			    }
    		    } else {
			// _REVISIT_ : Can we ever get here?
			/* No local class for this descriptor,
			 * Skip over the data for this class.
			 * like defaultReadObject with a null currentObject.
			 * The code will read the values but discard them.
			 */
			    ObjectStreamField[] fields =
				currentClassDesc.getFieldsNoCopy();
			    if (fields.length > 0) {
				inputClassFields(null, currentClass, fields, sender);
			    }
						
			}
					
		}
    	    } finally {
				// Make sure we exit at the same stack level as when we started.
		spClass = spBase;
    	    }
    	}
        } finally {
            // We've completed deserializing this object.  Any
            // future indirections will be handled correctly at the
            // CDR level.  The ActiveRecursionManager only deals with
            // objects currently being deserialized.
            activeRecursionMgr.removeObject(offset);
        }
		
    	return currentObject;
    }

    // This retrieves a vector of FVD's for the hierarchy of serializable classes stemming from 
    // repositoryID.  It is assumed that the sender will not provide base_value id's for non-serializable
    // classes!
    private Vector getOrderedDescriptions(String repositoryID,
					  com.sun.org.omg.SendingContext.CodeBase sender) {
	Vector descs = new Vector();

	FullValueDescription aFVD = sender.meta(repositoryID);
	while (aFVD != null) {
	    descs.insertElementAt(aFVD, 0);
	    if ((aFVD.base_value != null) && !kEmptyStr.equals(aFVD.base_value)) {
		aFVD = sender.meta(aFVD.base_value);
	    }
	    else return descs;
	}

	return descs;
    }

    /**
     * This input method uses FullValueDescriptions retrieved from the sender's runtime to 
     * read in the data.  This method is capable of throwing out data not applicable to client's fields.
     * This method handles instances where the reader has a class not sent by the sender, the sender sent
     * a class not present on the reader, and/or the reader's class does not match the sender's class.
     *
     * NOTE : If the local description indicates custom marshaling and the remote type's FVD also
     * indicates custom marsahling than the local type is used to read the data off the wire.  However,
     * if either says custom while the other does not, a MARSHAL error is thrown.  Externalizable is 
     * a form of custom marshaling.
     *
     */
    private Object inputObjectUsingFVD(Class clz,
				       String repositoryID,
				       com.sun.org.omg.SendingContext.CodeBase sender,
                                       int offset)
	throws IOException, ClassNotFoundException
    {
		
	int spBase = spClass;	// current top of stack
	try{
			
	    /*
	     * Get the descriptor and then class of the incoming object.
	     */
			
	    ObjectStreamClass currdesc = currentClassDesc = ObjectStreamClass.lookup(clz);
	    Class currclass = currentClass = clz;
			
	    /* If Externalizable,
	     *  Create an instance and tell it to read its data.
	     * else,
	     *  Handle it as a serializable class.
	     */
	    if (currentClassDesc.isExternalizable()) {
				
		try {
		    currentObject = (currentClass == null) ?
			null : allocateNewObject(currentClass, currentClass);
		    if (currentObject != null) {
                        // Store this object and its beginning position
                        // since there might be indirections to it while
                        // it's been unmarshalled.
                        activeRecursionMgr.addObject(offset, currentObject);

			// Read format version
			readByte();
						
			Externalizable ext = (Externalizable)currentObject;
			ext.readExternal(this);
		    }
		}
		catch (NoSuchMethodError e) {
		    throw new InvalidClassException(currentClass.getName(),
						    "NoSuchMethodError accessing no-arg constructor");
		}
		catch (IllegalAccessException e) {
		    throw new InvalidClassException(currentClass.getName(),
						    "IllegalAccessException");
		}
		catch (InstantiationException e) {
		    throw new InvalidClassException(currentClass.getName(),
						    "InstantiationException");
		}
	    } // end : if (currentClassDesc.isExternalizable())
			else {
				
				/*
				 * This is your basic diff pattern, made simpler
				 * because reordering is not allowed.
				 */
		for (currdesc = currentClassDesc, currclass = currentClass;
		     currdesc != null && currdesc.isSerializable();   /*sun.4296963 ibm.11861*/

		     currdesc = currdesc.getSuperclass()) {
					
		    /*
		     * Search the classes to see if the class of this
		     * descriptor appears further up the hierarchy. Until
		     * it's found assume its an inserted class.  If it's
		     * not found, its the descriptor's class that has been
		     * removed.
		     */
		    Class cc = currdesc.forClass();
		    Class cl;
		    for (cl = currclass; cl != null; cl = cl.getSuperclass()) {
			if (cc == cl) {
			    // found a superclass that matches this descriptor
			    break;
			} else {
			    /* Ignore a class that doesn't match.  No
			     * action is needed since it is already
			     * initialized.
			     */
			}
		    } // end : for (cl = currclass; cl != null; cl = cl.getSuperclass()) 
		    /* Test if there is room for this new entry.
		     * If not, double the size of the arrays and copy the contents.
		     */
		    spClass++;
		    if (spClass >= classes.length) {
			int newlen = classes.length * 2;
			Class[] newclasses = new Class[newlen];
			ObjectStreamClass[] newclassdesc = new ObjectStreamClass[newlen];
				
			System.arraycopy(classes, 0,
					 newclasses, 0,
					 classes.length);
			System.arraycopy(classdesc, 0,
					 newclassdesc, 0,
					 classes.length);
						
			classes = newclasses;
			classdesc = newclassdesc;
    		    }

		    if (cl == null) {
			/* Class not found corresponding to this descriptor.
			 * Pop off all the extra classes pushed.
			 * Push the descriptor and a null class.
			 */
			classdesc[spClass] = currdesc;
			classes[spClass] = null;
		    } else {
			/* Current class descriptor matches current class.
			 * Some classes may have been inserted.
			 * Record the match and advance the class, continue
			 * with the next descriptor.
			 */
			classdesc[spClass] = currdesc;
			classes[spClass] = cl;
			currclass = cl.getSuperclass();
    		}
		} // end : for (currdesc = currentClassDesc, currclass = currentClass;
				
				/* Allocate a new object.  
				 */
		try {
		    currentObject = (currentClass == null) ?
			null : allocateNewObject(currentClass, currclass);

                    // Store this object and its beginning position
                    // since there might be indirections to it while
                    // it's been unmarshalled.
                    activeRecursionMgr.addObject(offset, currentObject);

		} catch (NoSuchMethodError e) {
		    throw new InvalidClassException(currclass.getName(),
						    "NoSuchMethodError accessing no-arg constructor");
		} catch (IllegalAccessException e) {
		    throw new InvalidClassException(currclass.getName(),
						    "IllegalAccessException");
		} catch (InstantiationException e) {
		    throw new InvalidClassException(currclass.getName(),
						    "InstantiationException");
		}
				
		Enumeration fvdsList = getOrderedDescriptions(repositoryID, sender).elements();
				
		while((fvdsList.hasMoreElements()) && (spClass > spBase)) {
		    FullValueDescription fvd = (FullValueDescription)fvdsList.nextElement();
	            // d4365188: backward compatability
		    String repIDForFVD = vhandler.getClassName(fvd.id);
		    String repIDForClass = vhandler.getClassName(vhandler.getRMIRepositoryID(currentClass));
					
		    while ((spClass > spBase) &&
			   (!repIDForFVD.equals(repIDForClass))) {
			int pos = findNextClass(repIDForFVD, classes, spClass, spBase);
			if (pos != -1){
			    spClass = pos;
			    currclass = currentClass = classes[spClass];
			    repIDForClass = vhandler.getClassName(vhandler.getRMIRepositoryID(currentClass));
			}
			else { // Read and throw away one level of the fvdslist
			    if (fvd.is_custom) {
                                // Is this actually an error?  REVISIT
				throw new IOException("Remote superclass is custom marshalled: "
                                                      + fvd.id);
			    } else {
				inputClassFields(null, currentClass, null, fvd.members, sender);
			    }
			    if (fvdsList.hasMoreElements()){
				fvd = (FullValueDescription)fvdsList.nextElement();
				repIDForFVD = vhandler.getClassName(fvd.id);
			    }
			    else return currentObject;
			}
						
		    }
					
		    currdesc = currentClassDesc = ObjectStreamClass.lookup(currentClass);
					
		    if (!repIDForClass.equals("java.lang.Object")) {

                        // If the sender used custom marshaling, then it should have put
                        // these two bytes on the wire.  Currently, we don't do anything
                        // with these, but that has to change at some point.
                        if (fvd.is_custom) {
			    // Read format version
			    readByte();
                            
			    // Read defaultWriteObject indicator
			    readBoolean();
                        }

                        // Always use readObject if it exists, and fall back to default
                        // unmarshaling if it doesn't.
                        boolean usedReadObject = false;
                        try {

                            // See the definition of defaultReadObjectFVDMembers
                            // for more information.  This concerns making sure
                            // we use the remote FVD's members in defaultReadObject.
                            defaultReadObjectFVDMembers = fvd.members;
                            usedReadObject = invokeObjectReader(currentClassDesc,
                                                                currentObject,
                                                                currentClass);
                        } finally {
                            defaultReadObjectFVDMembers = null;
                        }

                        if (!usedReadObject)
                            inputClassFields(currentObject, currentClass, currdesc, fvd.members, sender);
                           
                        currclass = currentClass = classes[--spClass];

		    } else { 

			// The remaining hierarchy of the local class does not match the sender's FVD.
			// So, use remaining FVDs to read data off wire.  If any remaining FVDs indicate
			// custom marshaling, throw MARSHAL error.
			inputClassFields(null, currentClass, null, fvd.members, sender);
						
			while (fvdsList.hasMoreElements()){
			    fvd = (FullValueDescription)fvdsList.nextElement();
			    if (fvd.is_custom)
				throw new IOException("Sender's custom marshaling class does not match local class: " + fvd.id);
			    else inputClassFields(null, currentClass, null, fvd.members, sender);
			}
						
		    }
					
		} // end : while(fvdsList.hasMoreElements()) 
		while (fvdsList.hasMoreElements()){
		    FullValueDescription fvd = (FullValueDescription)fvdsList.nextElement();
		    if (fvd.is_custom)
			throw new IOException("Sender's custom marshaling class does not match local class: " + fvd.id);
		    else throwAwayData(fvd.members, sender);			
		}
	    }
			
	    return currentObject;
	}
	finally {
    		// Make sure we exit at the same stack level as when we started.
    		spClass = spBase;

                // We've completed deserializing this object.  Any
                // future indirections will be handled correctly at the
                // CDR level.  The ActiveRecursionManager only deals with
                // objects currently being deserialized.
                activeRecursionMgr.removeObject(offset);
    	    }
		
    	}

    /**
     * This input method uses FullValueDescriptions retrieved from the sender's runtime to 
     * read in the data.  This method is capable of throwing out data not applicable to client's fields.
     *
     * NOTE : If the local description indicates custom marshaling and the remote type's FVD also
     * indicates custom marsahling than the local type is used to read the data off the wire.  However,
     * if either says custom while the other does not, a MARSHAL error is thrown.  Externalizable is 
     * a form of custom marshaling.
     *
     */
    private Object skipObjectUsingFVD(String repositoryID,
				      com.sun.org.omg.SendingContext.CodeBase sender)
	throws IOException, ClassNotFoundException
    {

	Enumeration fvdsList = getOrderedDescriptions(repositoryID, sender).elements();
		
	while(fvdsList.hasMoreElements()) {
	    FullValueDescription fvd = (FullValueDescription)fvdsList.nextElement();
	    String repIDForFVD = vhandler.getClassName(fvd.id);
			
	    if (!repIDForFVD.equals("java.lang.Object")) {
		if (fvd.is_custom) {
		    throw new IOException("Can't skip sender's custom marshaled class: "
                                          + fvd.id);
    }
		else { 
		    // Use default marshaling
		    inputClassFields(null, null, null, fvd.members, sender);
		}
	    }

	} // end : while(fvdsList.hasMoreElements()) 
	return null;
		
    }

    ///////////////////

    private int findNextClass(String classname, Class classes[], int _spClass, int _spBase){

	for (int i = _spClass; i > _spBase; i--){
	    if (classname.equals(classes[i].getName())) {
		return i;
	    }
	}

	return -1;
    }

    /*
     * Invoke the readObject method if present.  Assumes that in the case of custom
     * marshaling, the format version and defaultWriteObject indicator were already
     * removed.
     */
    private boolean invokeObjectReader(ObjectStreamClass osc, Object obj, Class aclass)
	throws InvalidClassException, StreamCorruptedException,
	       ClassNotFoundException, IOException
    {
	if (osc.readObjectMethod == null) {
	    return false;
	}
		
	try {
	    readObject(obj, aclass, this);

	    return true;
	} catch (InvocationTargetException e) {
	    Throwable t = e.getTargetException();
	    if (t instanceof ClassNotFoundException)
		throw (ClassNotFoundException)t;
	    else if (t instanceof IOException)
		throw (IOException)t;
	    else if (t instanceof RuntimeException)
		throw (RuntimeException) t;
	    else if (t instanceof Error)
		throw (Error) t;
	    else
		throw new Error("interal error");
	} catch (IllegalAccessException e) {
	    return false;
	}
    }

    /*
     * Reset the stream to be just like it was after the constructor.
     */
    private void resetStream() throws IOException {

	if (classes == null)
	    classes = new Class[20];
	else {
	    for (int i = 0; i < classes.length; i++)
		classes[i] = null;
	}
	if (classdesc == null)
	    classdesc = new ObjectStreamClass[20];
	else {
	    for (int i = 0; i < classdesc.length; i++)
		classdesc[i] = null;
	}
	spClass = 0;

	if (callbacks != null)
	    callbacks.setSize(0);	// discard any pending callbacks
    }

    /**
     * Factored out of inputClassFields  This reads a primitive value and sets it 
     * (with a native call) in the field of o described by the ObjectStreamField
     * field.
     */
    private void inputPrimitiveField(Object o, Class cl, ObjectStreamField field)
        throws InvalidClassException, IOException {

        try {
            
            switch (field.getTypeCode()) {
                case 'B':
                    byte byteValue = orbStream.read_octet();
                    setByteFieldOpt(o, field.getFieldID(cl), byteValue);
                    break;
                case 'Z':
                   boolean booleanValue = orbStream.read_boolean();
                   setBooleanFieldOpt(o, field.getFieldID(cl), booleanValue);
                   break;
		case 'C':
                    char charValue = orbStream.read_wchar();
                    setCharFieldOpt(o, field.getFieldID(cl), charValue);
                    break;
		case 'S':
                    short shortValue = orbStream.read_short();
                    setShortFieldOpt(o, field.getFieldID(cl), shortValue);
                    break;
		case 'I':
                    int intValue = orbStream.read_long();
                    setIntFieldOpt(o, field.getFieldID(cl), intValue);
                    break;
		case 'J':
                    long longValue = orbStream.read_longlong();
                    setLongFieldOpt(o, field.getFieldID(cl), longValue);
                    break;
		case 'F' :
                    float floatValue = orbStream.read_float();
                    setFloatFieldOpt(o, field.getFieldID(cl), floatValue);
                    break;
		case 'D' :
                    double doubleValue = orbStream.read_double();
                    setDoubleFieldOpt(o, field.getFieldID(cl), doubleValue);
                    break;
		default:
                    throw new InvalidClassException(cl.getName());
            }
        } catch (IllegalArgumentException e) {
            /* This case should never happen. If the field types
               are not the same, InvalidClassException is raised when
               matching the local class to the serialized ObjectStreamClass. */
            throw new ClassCastException("Assigning instance of class " +
                                         field.getType().getName() +
                                         " to field " +
                                         currentClassDesc.getName() + '#' +
                                         field.getField().getName());
        }
    }

    private Object inputObjectField(org.omg.CORBA.ValueMember field,
                                    com.sun.org.omg.SendingContext.CodeBase sender)
        throws IndirectionException, ClassNotFoundException, IOException,
               StreamCorruptedException {

        Object objectValue = null;
        Class type = null;
        String id = field.id;
							
        try {
            type = vhandler.getClassFromType(id);
        } catch(ClassNotFoundException cnfe) {
            // Make sure type = null
            type = null;
        }

        String signature = null;
        if (type != null)
            signature = ValueUtility.getSignature(field);
								
        if (signature != null && (signature.equals("Ljava/lang/Object;") ||
                                  signature.equals("Ljava/io/Serializable;") ||
                                  signature.equals("Ljava/io/Externalizable;"))) {
            objectValue = javax.rmi.CORBA.Util.readAny(orbStream);
        } else {
            // Decide what method call to make based on the type. If
            // it is a type for which we need to load a stub, convert
            // the type to the correct stub type.
            //
            // NOTE : Since FullValueDescription does not allow us
            // to ask whether something is an interface we do not
            // have the ability to optimize this check.
            
            int callType = ValueHandlerImpl.kValueType;
            
            if (!vhandler.isSequence(id)) {

                if (field.type.kind().value() == kRemoteTypeCode.kind().value()) {

                    // RMI Object reference...
                    callType = ValueHandlerImpl.kRemoteType;
                    
                } else {

                    // REVISIT.  If we don't have the local class,
                    // we should probably verify that it's an RMI type, 
                    // query the remote FVD, and use is_abstract.
                    // Our FVD seems to get NullPointerExceptions for any
                    // non-RMI types.

                    // This uses the local class in the same way as
                    // inputObjectField(ObjectStreamField) does.  REVISIT
                    // inputObjectField(ObjectStreamField)'s loadStubClass
                    // logic.  Assumption is that the given type cannot
                    // evolve to become a CORBA abstract interface or
                    // a RMI abstract interface.

                    if (type != null && type.isInterface() &&
                        (vhandler.isAbstractBase(type) ||
                         ObjectStreamClassCorbaExt.isAbstractInterface(type))) {
                
                        callType = ValueHandlerImpl.kAbstractType;
                    }
                }
            }
                
            // Now that we have used the FVD of the field to determine the proper course
            // of action, it is ok to use the type (Class) from this point forward since 
            // the rep. id for this read will also follow on the wire.

            switch (callType) {
                case ValueHandlerImpl.kRemoteType: 
                    if (type != null)
                        objectValue = Utility.readObjectAndNarrow(orbStream, type);
                    else
                        objectValue = orbStream.read_Object();
                    break;
                case ValueHandlerImpl.kAbstractType: 
                    if (type != null)
                        objectValue = Utility.readAbstractAndNarrow(orbStream, type);
                    else
                        objectValue = orbStream.read_abstract_interface();
                    break;
                case ValueHandlerImpl.kValueType:
                    if (type != null)
                        objectValue = orbStream.read_value(type);
                    else
                        objectValue = orbStream.read_value();
                    break;
                default:
                    throw new StreamCorruptedException("Unknown callType: " + callType);
            }
        }

        return objectValue;
    }

    /**
     * Factored out of inputClassFields and reused in 
     * inputCurrentClassFieldsForReadFields.
     *
     * Reads the field (which of an Object type as opposed to a primitive) 
     * described by ObjectStreamField field and returns it.
     */
    private Object inputObjectField(ObjectStreamField field) 
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IndirectionException, IOException {

        if (ObjectStreamClassCorbaExt.isAny(field.getTypeString())) {
            return javax.rmi.CORBA.Util.readAny(orbStream);
        }

        Object objectValue = null;

        // fields have an API to provide the actual class
        // corresponding to the data type
        // Class type = osc.forClass();
        Class type = field.getType();
				
        // Decide what method call to make based on the type. If
        // it is a type for which we need to load a stub, convert
        // the type to the correct stub type.
        
        int callType = ValueHandlerImpl.kValueType;
        boolean narrow = false;
        
        if (type.isInterface()) { 
            boolean loadStubClass = false;
            
            if (java.rmi.Remote.class.isAssignableFrom(type)) {
                
                // RMI Object reference...
                callType = ValueHandlerImpl.kRemoteType;
                
            } else if (org.omg.CORBA.Object.class.isAssignableFrom(type)){
                
                // IDL Object reference...
                callType = ValueHandlerImpl.kRemoteType;
                loadStubClass = true;
                
            } else if (vhandler.isAbstractBase(type)) {
                // IDL Abstract Object reference...
                
                callType = ValueHandlerImpl.kAbstractType;
                loadStubClass = true;
            } else if (ObjectStreamClassCorbaExt.isAbstractInterface(type)) {
                // RMI Abstract Object reference...
                
                callType = ValueHandlerImpl.kAbstractType;
            }
            
            if (loadStubClass) {
                try {
                    String codebase = Util.getCodebase(type);
                    String repID = vhandler.createForAnyType(type);
                    type = Utility.loadStubClass(repID, codebase, type); //d11638
                } catch (ClassNotFoundException e) {
                    narrow = true;
                }
            } else {
                narrow = true;
            }
        }			

        switch (callType) {
            case ValueHandlerImpl.kRemoteType: 
                if (!narrow) 
                    objectValue = (Object)orbStream.read_Object(type);
                else
                    objectValue = Utility.readObjectAndNarrow(orbStream, type);
                break;
            case ValueHandlerImpl.kAbstractType: 
                if (!narrow)
                    objectValue = (Object)orbStream.read_abstract_interface(type); 
                else
                    objectValue = Utility.readAbstractAndNarrow(orbStream, type);
                break;
            case ValueHandlerImpl.kValueType:
                objectValue = (Object)orbStream.read_value(type);
                break;
            default:
                throw new StreamCorruptedException("Unknown callType: " + callType);
        }

        return objectValue;
    }

    private final boolean mustUseRemoteValueMembers() {
        return defaultReadObjectFVDMembers != null;
    }

    void readFields(java.util.Map fieldToValueMap)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        if (mustUseRemoteValueMembers()) {
            inputRemoteMembersForReadFields(fieldToValueMap);
        } else
            inputCurrentClassFieldsForReadFields(fieldToValueMap);
    }

    private final void inputRemoteMembersForReadFields(java.util.Map fieldToValueMap)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        // Must have this local variable since defaultReadObjectFVDMembers
        // may get mangled by recursion.
        ValueMember fields[] = defaultReadObjectFVDMembers;

	try {

	    for (int i = 0; i < fields.length; i++) {
                
                switch (fields[i].type.kind().value()) {

                case TCKind._tk_octet:
                    byte byteValue = orbStream.read_octet();
                    fieldToValueMap.put(fields[i].name, new Byte(byteValue));
                    break;
                case TCKind._tk_boolean:
                    boolean booleanValue = orbStream.read_boolean();
                    fieldToValueMap.put(fields[i].name, new Boolean(booleanValue));
                    break;
                case TCKind._tk_char:
                    // Backwards compatibility.  Older Sun ORBs sent
                    // _tk_char even though they read and wrote wchars
                    // correctly.
                    //
                    // Fall through to the _tk_wchar case.
                case TCKind._tk_wchar:
                    char charValue = orbStream.read_wchar();
                    fieldToValueMap.put(fields[i].name, new Character(charValue));
                    break;
                case TCKind._tk_short:
                    short shortValue = orbStream.read_short();
                    fieldToValueMap.put(fields[i].name, new Short(shortValue));
                    break;
                case TCKind._tk_long:
                    int intValue = orbStream.read_long();
                    fieldToValueMap.put(fields[i].name, new Integer(intValue));
                    break;
                case TCKind._tk_longlong:
                    long longValue = orbStream.read_longlong();
                    fieldToValueMap.put(fields[i].name, new Long(longValue));
                    break;
                case TCKind._tk_float:
                    float floatValue = orbStream.read_float();
                    fieldToValueMap.put(fields[i].name, new Float(floatValue));
                    break;
                case TCKind._tk_double:
                    double doubleValue = orbStream.read_double();
                    fieldToValueMap.put(fields[i].name, new Double(doubleValue));
                    break;
                case TCKind._tk_value:
                case TCKind._tk_objref:
                case TCKind._tk_value_box:
                    Object objectValue = null;
                    try {
                        objectValue = inputObjectField(fields[i],
                                                       cbSender);

                    } catch (IndirectionException cdrie) {
                        // The CDR stream had never seen the given offset before,
                        // so check the recursion manager (it will throw an
                        // IOException if it doesn't have a reference, either).
                        objectValue = activeRecursionMgr.getObject(cdrie.offset);
                    }

                    fieldToValueMap.put(fields[i].name, objectValue);
                    break;
                default:
                    throw new StreamCorruptedException("Unknown kind: "
                                                       + fields[i].type.kind().value());
                }
            }
        } catch (Throwable t) {
            throw new StreamCorruptedException(t.getMessage());
	}
    }

    /**
     * Called from InputStreamHook.
     *
     * Reads the fields of the current class (could be the ones
     * queried from the remote FVD) and puts them in
     * the given Map, name to value.  Wraps primitives in the
     * corresponding java.lang Objects.
     */
    private final void inputCurrentClassFieldsForReadFields(java.util.Map fieldToValueMap)
        throws InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException {

        ObjectStreamField[] fields = currentClassDesc.getFieldsNoCopy();

	int primFields = fields.length - currentClassDesc.objFields;

        // Handle the primitives first
        for (int i = 0; i < primFields; ++i) {

            switch (fields[i].getTypeCode()) {
                case 'B':
                    byte byteValue = orbStream.read_octet();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Byte(byteValue));
                    break;
                case 'Z':
                   boolean booleanValue = orbStream.read_boolean();
                   fieldToValueMap.put(fields[i].getName(),
                                       new Boolean(booleanValue));
                   break;
		case 'C':
                    char charValue = orbStream.read_wchar();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Character(charValue));
                    break;
		case 'S':
                    short shortValue = orbStream.read_short();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Short(shortValue));
                    break;
		case 'I':
                    int intValue = orbStream.read_long();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Integer(intValue));
                    break;
		case 'J':
                    long longValue = orbStream.read_longlong();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Long(longValue));
                    break;
		case 'F' :
                    float floatValue = orbStream.read_float();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Float(floatValue));
                    break;
		case 'D' :
                    double doubleValue = orbStream.read_double();
                    fieldToValueMap.put(fields[i].getName(),
                                        new Double(doubleValue));
                    break;
		default:
                    throw new InvalidClassException(currentClassDesc.getName());
	    }
	}

	/* Read and set object fields from the input stream. */
	if (currentClassDesc.objFields > 0) {
	    for (int i = primFields; i < fields.length; i++) {
                Object objectValue = null;
                try {
                    objectValue = inputObjectField(fields[i]);
                } catch(IndirectionException cdrie) {
                    // The CDR stream had never seen the given offset before,
                    // so check the recursion manager (it will throw an
                    // IOException if it doesn't have a reference, either).
                    objectValue = activeRecursionMgr.getObject(cdrie.offset);
                }

                fieldToValueMap.put(fields[i].getName(), objectValue);
            }
        }
    }

    /*
     * Read the fields of the specified class from the input stream and set
     * the values of the fields in the specified object. If the specified
     * object is null, just consume the fields without setting any values. If
     * any ObjectStreamField does not have a reflected Field, don't try to set
     * that field in the object.
     *
     * REVISIT -- This code doesn't do what the comment says to when
     * getField() is null!
     */
    private void inputClassFields(Object o, Class cl,
				  ObjectStreamField[] fields, 
				  com.sun.org.omg.SendingContext.CodeBase sender)
	throws InvalidClassException, StreamCorruptedException,
	       ClassNotFoundException, IOException
    {
		
	int primFields = fields.length - currentClassDesc.objFields;

	if (o != null) {
	    for (int i = 0; i < primFields; ++i) {
		if (fields[i].getField() == null)
		    continue;

                inputPrimitiveField(o, cl, fields[i]);
	    }
	}

	/* Read and set object fields from the input stream. */
	if (currentClassDesc.objFields > 0) {
	    for (int i = primFields; i < fields.length; i++) {
		Object objectValue = null;

                try {
                    objectValue = inputObjectField(fields[i]);
                } catch(IndirectionException cdrie) {
                    // The CDR stream had never seen the given offset before,
                    // so check the recursion manager (it will throw an
                    // IOException if it doesn't have a reference, either).
                    objectValue = activeRecursionMgr.getObject(cdrie.offset);
                }

		if ((o == null) || (fields[i].getField() == null)) {
		    continue;
		}

		try {
		    setObjectFieldOpt(o, fields[i].getFieldID(cl), objectValue);
		} catch (IllegalArgumentException e) {

		    throw new ClassCastException("Assigning instance of class " +
						 objectValue.getClass().getName() +
						 " to field " +
						 currentClassDesc.getName() +
						 '#' +
						 fields[i].getField().getName());
		}
	    } // end : for loop
	    }
	}

    /*
     * Read the fields of the specified class from the input stream and set
     * the values of the fields in the specified object. If the specified
     * object is null, just consume the fields without setting any values. If
     * any ObjectStreamField does not have a reflected Field, don't try to set
     * that field in the object.
     */
    private void inputClassFields(Object o, Class cl, 
				  ObjectStreamClass osc,
				  ValueMember[] fields,
				  com.sun.org.omg.SendingContext.CodeBase sender)
	throws InvalidClassException, StreamCorruptedException,
	       ClassNotFoundException, IOException
    {
	try{

	    for (int i = 0; i < fields.length; ++i) {
		try {
		    switch (fields[i].type.kind().value()) {
		    case TCKind._tk_octet:
			byte byteValue = orbStream.read_octet();
			if ((o != null) && osc.hasField(fields[i]))
			setByteField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), byteValue);
			break;
		    case TCKind._tk_boolean:
			boolean booleanValue = orbStream.read_boolean();
			if ((o != null) && osc.hasField(fields[i]))
			setBooleanField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), booleanValue);
			break;
		    case TCKind._tk_char:
                        // Backwards compatibility.  Older Sun ORBs sent
                        // _tk_char even though they read and wrote wchars
                        // correctly.
                        //
                        // Fall through to the _tk_wchar case.
                    case TCKind._tk_wchar:
			char charValue = orbStream.read_wchar();
			if ((o != null) && osc.hasField(fields[i]))
			setCharField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), charValue);
			break;
		    case TCKind._tk_short:
			short shortValue = orbStream.read_short();
			if ((o != null) && osc.hasField(fields[i]))
			setShortField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), shortValue);
			break;
		    case TCKind._tk_long:
			int intValue = orbStream.read_long();
			if ((o != null) && osc.hasField(fields[i]))
			setIntField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), intValue);
			break;
		    case TCKind._tk_longlong:
			long longValue = orbStream.read_longlong();
			if ((o != null) && osc.hasField(fields[i]))
			setLongField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), longValue);
			break;
		    case TCKind._tk_float:
			float floatValue = orbStream.read_float();
			if ((o != null) && osc.hasField(fields[i]))
			setFloatField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), floatValue);
			break;
		    case TCKind._tk_double:
			double doubleValue = orbStream.read_double();
			if ((o != null) && osc.hasField(fields[i]))
			setDoubleField(o, cl, fields[i].name, ValueUtility.getSignature(fields[i]), doubleValue);
			break;
                    case TCKind._tk_value:
		    case TCKind._tk_objref:
		    case TCKind._tk_value_box:
                        Object objectValue = null;
                        try {
                            objectValue = inputObjectField(fields[i], sender);
                        } catch (IndirectionException cdrie) {
                            // The CDR stream had never seen the given offset before,
                            // so check the recursion manager (it will throw an
                            // IOException if it doesn't have a reference, either).
                            objectValue = activeRecursionMgr.getObject(cdrie.offset);
                        }
								
			if (o == null)
			    continue;
			try {
			    if (osc.hasField(fields[i])){
                                setObjectField(o, 
                                               cl, 
                                               fields[i].name, 
                                               ValueUtility.getSignature(fields[i]), 
                                               objectValue);
			    }
			} catch (IllegalArgumentException e) {
								
			    throw new ClassCastException("Assigning instance of class " +
							 objectValue.getClass().getName() +
							 " to field " +
							 fields[i].name);

			}		
			break;
                    default:
                        throw new StreamCorruptedException("Unknown kind: "
                                                           + fields[i].type.kind().value());
		    }
		} catch (IllegalArgumentException e) {
		    /* This case should never happen. If the field types
		       are not the same, InvalidClassException is raised when
		       matching the local class to the serialized ObjectStreamClass. */
		    throw new ClassCastException("Assigning instance of class " +
						 fields[i].id +
						 " to field " +
						 currentClassDesc.getName() + '#' +
						 fields[i].name);
		}
	    }
			

	}
	catch(Throwable t){

	    throw new StreamCorruptedException(t.getMessage());
	}
		
    }
	
    /*
     * Read the fields of the specified class from the input stream throw data away.
     * This must handle same switch logic as above.
     */
    private void throwAwayData(ValueMember[] fields,
			       com.sun.org.omg.SendingContext.CodeBase sender)
	throws InvalidClassException, StreamCorruptedException,
	       ClassNotFoundException, IOException
    {
	for (int i = 0; i < fields.length; ++i) {
	
	    try {
					
		switch (fields[i].type.kind().value()) {
		case TCKind._tk_octet:
		    orbStream.read_octet();
		    break;
		case TCKind._tk_boolean:
		    orbStream.read_boolean();
		    break;
		case TCKind._tk_char:
                    // Backwards compatibility.  Older Sun ORBs sent
                    // _tk_char even though they read and wrote wchars
                    // correctly.
                    //
                    // Fall through to the _tk_wchar case.
                case TCKind._tk_wchar:
		    orbStream.read_wchar();
		    break;
		case TCKind._tk_short:
		    orbStream.read_short();
		    break;
		case TCKind._tk_long:
		    orbStream.read_long();
		    break;
		case TCKind._tk_longlong:
		    orbStream.read_longlong();
		    break;
		case TCKind._tk_float:
		    orbStream.read_float();
		    break;
		case TCKind._tk_double:
		    orbStream.read_double();
		    break;
                case TCKind._tk_value:
		case TCKind._tk_objref:
		case TCKind._tk_value_box:
		    Class type = null;
	            String id = fields[i].id;

		    try {
			type = vhandler.getClassFromType(id);
		    }
		    catch(ClassNotFoundException cnfe){
			// Make sure type = null
			type = null;
		    }
		    String signature = null;
		    if (type != null)
			signature = ValueUtility.getSignature(fields[i]);
								
		    // Read value
		    try {
			if ((signature != null) && ( signature.equals("Ljava/lang/Object;") ||
						     signature.equals("Ljava/io/Serializable;") ||
						     signature.equals("Ljava/io/Externalizable;")) ) {
			    javax.rmi.CORBA.Util.readAny(orbStream);
			}
			else {
			    // Decide what method call to make based on the type.
			    //
			    // NOTE : Since FullValueDescription does not allow us
			    // to ask whether something is an interface we do not
			    // have the ability to optimize this check.
										
			    int callType = ValueHandlerImpl.kValueType;

			    if (!vhandler.isSequence(id)) {
				FullValueDescription fieldFVD = sender.meta(fields[i].id);
				if (kRemoteTypeCode == fields[i].type) {

				    // RMI Object reference...
				    callType = ValueHandlerImpl.kRemoteType;
				} else if (fieldFVD.is_abstract) {
				    // RMI Abstract Object reference...

				    callType = ValueHandlerImpl.kAbstractType;
				}
			    }
										
			    // Now that we have used the FVD of the field to determine the proper course
			    // of action, it is ok to use the type (Class) from this point forward since 
			    // the rep. id for this read will also follow on the wire.

			    switch (callType) {
			    case ValueHandlerImpl.kRemoteType: 
				orbStream.read_Object();
				break;
			    case ValueHandlerImpl.kAbstractType: 
				orbStream.read_abstract_interface(); 
				break;
			    case ValueHandlerImpl.kValueType:
				if (type != null) {
				    orbStream.read_value(type);
				} else {
				    orbStream.read_value();
				}
				break;
                            default:
                                throw new StreamCorruptedException("Unknown callType: "
                                                                   + callType);
			    }
			}
										
		    }
		    catch(IndirectionException cdrie) {
			// Since we are throwing this away, don't bother handling recursion.
			continue;
		    }
									
		    break;
                default:
                    throw new StreamCorruptedException("Unknown kind: "
                                                       + fields[i].type.kind().value());

		}
	    } catch (IllegalArgumentException e) {
		/* This case should never happen. If the field types
		   are not the same, InvalidClassException is raised when
		   matching the local class to the serialized ObjectStreamClass. */
		throw new ClassCastException("Assigning instance of class " +
					     fields[i].id +
					     " to field " +
					     currentClassDesc.getName() + '#' +
					     fields[i].name);
	    }
	}
		
    }

    /* Allocate a new object for the specified class
     * 
     */
    private static native Object allocateNewObject(Class aclass, 
						   Class initclass)
	throws InstantiationException, IllegalAccessException;
    /* Create a pending exception.  This is needed to get around
     * the fact that the *Delegate methods do not explicitly
     * declare that they throw exceptions.
     *
     * This native methods creates an exception of the given type with
     * the given message string and posts it to the pending queue.
     */
    private static native void throwExceptionType(Class c, String message);

    /* The following native methods of the form set*Field are used
     * to set private, protected, and package private fields
     * of an Object.
     */
    private static native void setObjectField(Object o, Class c, String fieldName, String fieldSig, Object v);
    private static native void setBooleanField(Object o, Class c, String fieldName, String fieldSig, boolean v);
    private static native void setByteField(Object o, Class c, String fieldName, String fieldSig, byte v);
    private static native void setCharField(Object o, Class c, String fieldName, String fieldSig, char v);
    private static native void setShortField(Object o, Class c, String fieldName, String fieldSig, short v);
    private static native void setIntField(Object o, Class c, String fieldName, String fieldSig, int v);
    private static native void setLongField(Object o, Class c, String fieldName, String fieldSig, long v);
    private static native void setFloatField(Object o, Class c, String fieldName, String fieldSig, float v);
    private static native void setDoubleField(Object o, Class c, String fieldName, String fieldSig, double v);
    private static native void readObject(Object obj, Class asClass, Object ois)
        throws InvocationTargetException, IllegalAccessException;

    private static native void setObjectFieldOpt(Object o, long fieldID, Object v);
    private static native void setBooleanFieldOpt(Object o, long fieldID, boolean v);
    private static native void setByteFieldOpt(Object o, long fieldID, byte v);
    private static native void setCharFieldOpt(Object o, long fieldID, char v);
    private static native void setShortFieldOpt(Object o, long fieldID, short v);
    private static native void setIntFieldOpt(Object o, long fieldID, int v);
    private static native void setLongFieldOpt(Object o, long fieldID, long v);
    private static native void setFloatFieldOpt(Object o, long fieldID, float v);
    private static native void setDoubleFieldOpt(Object o, long fieldID, double v);

    /**
     * This class maintains a map of stream position to
     * an Object currently being deserialized.  It is used
     * to handle the cases where the are indirections to
     * an object on the recursion stack.  The CDR level
     * handles indirections to objects previously seen
     * (and completely deserialized) in the stream.
     */
    static class ActiveRecursionManager
    {
        private Map offsetToObjectMap;
        
        public ActiveRecursionManager() {
            // A hash map is unsynchronized and allows
            // null values
            offsetToObjectMap = new HashMap();
        }

        // Called right after allocating a new object.
        // Offset is the starting position in the stream
        // of the object.
        public void addObject(int offset, Object value) {
            offsetToObjectMap.put(new Integer(offset), value);
        }

        // If the given starting position doesn't refer
        // to the beginning of an object currently being
        // deserialized, this throws an IOException.
        // Otherwise, it returns a reference to the
        // object.
        public Object getObject(int offset) throws IOException {
            Integer position = new Integer(offset);

            if (!offsetToObjectMap.containsKey(position))
                throw new IOException("Invalid indirection to offset "
                                      + offset);

            return offsetToObjectMap.get(position);
        }
        
        // Called when an object has been completely
        // deserialized, so it should no longer be in
        // this mapping.  The CDR level can handle
        // further indirections.
        public void removeObject(int offset) {
            offsetToObjectMap.remove(new Integer(offset));
        }

        // If the given offset doesn't map to an Object,
        // then it isn't an indirection to an object
        // currently being deserialized.
        public boolean containsObject(int offset) {
            return offsetToObjectMap.containsKey(new Integer(offset));
        }
    }
}
