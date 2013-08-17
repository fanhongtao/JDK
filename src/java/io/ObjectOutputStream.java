/*
 * @(#)ObjectOutputStream.java	1.36 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;

import java.util.Stack;

import sun.io.ObjectOutputStreamDelegate; // RMI over IIOP hook.

/**
 * An ObjectOutputStream writes primitive data types and graphs of
 * Java objects to an OutputStream.  The objects can be read
 * (reconstituted) using an ObjectInputStream.
 * Persistent storage of objects can be accomplished by using a file for
 * the stream.
 * If the stream is a network socket stream, the objects can be reconsituted
 * on another host or in another process. <p>
 *
 * Only objects that support the java.io.Serializable interface can be
 * written to streams.
 *
 * The class of each serializable object is encoded including the class
 * name and signature of the class, the values of the
 * object's fields and arrays, and the closure of any other objects
 * referenced from the initial objects. <p>
 * 
 * The method <STRONG>writeObject</STRONG> is used to write an object
 * to the stream.  Any object, including Strings and arrays, is
 * written with writeObject. Multiple objects or primitives can be
 * written to the stream.  The objects must be read back from the
 * corresponding ObjectInputstream with the same types and in the same
 * order as they were written.<p>
 *
 * Primitive data types can also be written to the stream using the
 * appropriate methods from DataOutput. Strings can also be written
 * using the writeUTF method.<p>
 *
 * The default serialization mechanism for an object writes the class
 * of the object, the class signature, and the values of all
 * non-transient and non-static fields.  References to other objects
 * (except in transient or static fields) cause those objects to be
 * written also. Multiple references to a single object are encoded
 * using a reference sharing mechanism so that graphs of objects can
 * be restored to the same shape as when the original was written. <p>
 *
 * For example to write an object that can be read by the example in ObjectInputStream: <br>
 * <PRE>
 *	FileOutputStream ostream = new FileOutputStream("t.tmp");
 *	ObjectOutputStream p = new ObjectOutputStream(ostream);
 *
 *	p.writeInt(12345);
 *	p.writeObject("Today");
 *	p.writeObject(new Date());
 *
 *	p.flush();
 *	ostream.close();
 *
 * </PRE>
 *
 * Classes that require special handling during the serialization and deserialization
 * process must implement special methods with these exact signatures: <p>
 *
 * <PRE>
 * private void readObject(java.io.ObjectInputStream stream)
 *     throws IOException, ClassNotFoundException; 
 * private void writeObject(java.io.ObjectOutputStream stream)
 *     throws IOException
 * </PRE><p>
 * The writeObject method is responsible for writing the state of
 * the object for its particular class so that the corresponding
 * readObject method can restore it.
 * The method does not need to concern itself with the
 * state belonging to the object's superclasses or subclasses.
 * State is saved by writing the individual fields to the ObjectOutputStream
 * using the writeObject method or by using the methods for
 * primitive data types supported by DataOutput. <p>
 *
 * Serialization does not write out the fields of any object that does
 * not implement the java.io.Serializable interface.  Subclasses of
 * Objects that are not serializable can be serializable. In this case
 * the non-serializable class must have a no-arg constructor to allow
 * its fields to be initialized.  In this case it is the
 * responsibility of the subclass to save and restore the state of the
 * non-serializable class. It is frequently the case that the fields
 * of that class are accessible (public, package, or protected) or
 * that there are get and set methods that can be used to restore the
 * state. <p>
 *
 * Serialization of an object can be prevented by implementing writeObject
 * and readObject methods that throw the NotSerializableException.
 * The exception will be caught by the ObjectOutputStream and abort the
 * serialization process.
 *
 * Implementing the Externalizable interface allows the object to
 * assume complete control over the contents and format of the object's
 * serialized form.  The methods of the Externalizable interface,
 * writeExternal and readExternal, are called to save and restore the
 * objects state.  When implemented by a class they can write and read
 * their own state using all of the methods of ObjectOutput and
 * ObjectInput.  It is the responsibility of the objects to handle any
 * versioning that occurs.
 *
 * @author	Roger Riggs
 * @version     1.36, 07/01/98
 * @see java.io.DataOutput
 * @see java.io.ObjectInputStream
 * @see java.io.Serializable
 * @see java.io.Externalizable
 * @since       JDK1.1
 */
public class ObjectOutputStream
	extends OutputStream
	implements ObjectOutput, ObjectStreamConstants
			
{ 
    /** 
     * Creates an ObjectOutputStream that writes to the specified OutputStream.
     * The stream header is written to the stream. The caller may want to call
     * flush immediately so that the corresponding ObjectInputStream can read
     * the header immediately.
     * @exception IOException Any exception thrown by the underlying OutputStream.
     * @since     JDK1.1
     */
    public ObjectOutputStream(OutputStream out) throws IOException {

        /*
         * RMI over IIOP hook. Check if we are a trusted subclass
         * that has implemented the "sun.io.ObjectOutputStream"
         * interface. If so, set our private flag that will be
         * checked in "writeObject", "defaultWriteObject" and
         * "enableReplaceObject". Note that we don't initialize
         * private instance variables in this case as an optimization
         * (subclasses using the hook should have no need for them).
         */
        
        if (this instanceof sun.io.ObjectOutputStreamDelegate && this.getClass().getClassLoader() == null) {
            isTrustedSubclass = true;
            return;
        }
        
	this.out = out;
	dos = new DataOutputStream(this);
	buf = new byte[1024];	// allocate buffer
	writeStreamHeader();
	resetStream();
    }

    /**
     * Write the specified object to the ObjectOutputStream.
     * The class of the object, the signature of the class, and the values
     * of the non-transient and non-static fields of the class and all
     * of its supertypes are written.  Default serialization for a class can be
     * overridden using the writeObject and the readObject methods. 
     * Objects referenced by this object are written transitively so
     * that a complete equivalent graph of objects can be
     * reconstructed by an ObjectInputStream.  <p>
     *
     * Exceptions are thrown for
     * problems with the OutputStream and for classes that should not be
     * serialized.  All exceptions are fatal to the OutputStream, which
     * is left in an indeterminate state, and it is up to the caller
     * to ignore or recover the stream state.
     * @exception InvalidClassException Something is wrong with a class used by
     *	   serialization.
     * @exception NotSerializableException Some object to be serialized does not
     *	  implement the java.io.Serializable interface.
     * @exception IOException Any exception thrown by the underlying OutputStream.
     * @since     JDK1.1
     */
    public final void writeObject(Object obj)
	throws IOException
    {
 	
	/*
	 * RMI over IIOP hook. Invoke delegate method if indicated.
	 */
	if (isTrustedSubclass) {
	    ((ObjectOutputStreamDelegate) this).writeObjectDelegate(obj);
	    return;
	}

	Object prevObject = currentObject;
	ObjectStreamClass prevClassDesc = currentClassDesc;
	boolean oldBlockDataMode = setBlockData(false);
	recursionDepth++;

	try {
	    if (serializeNullAndRepeat(obj))
		return;

	    if (checkSpecialClasses(obj))
		return;


	    /* If the replacment is enabled, give subclasses one chance
	     * to substitute a new object. If one is substituted,
	     * recheck for null, repeated refs, and special cased classes
	     */
	    if (enableReplace) {
		Object altobj = replaceObject(obj);
		if (obj != altobj) {

		    if (altobj != null && !(altobj instanceof Serializable)) {
			String clname = altobj.getClass().getName();
			throw new NotSerializableException(clname);
		    }
			
		    // If the alternate object is already
		    // serialized just remember the replacement
		    if (serializeNullAndRepeat(altobj)) {
			addReplacement(obj, altobj);
			return;
		    }

		    /* Add this to the set of replaced objects.
		     * This must be done before the object is
		     * serialized so that if the object indirectly
		     * refers to the original it will be redirected to
		     * the replacement.
		     *
		     * NB: checkSpecialClasses should only call
		     * serializeNullandRepeat for objects that will not
		     * recurse.
		     */
		    addReplacement(obj, altobj);

		    if (checkSpecialClasses(altobj))
			return;

		    obj = altobj;
		}
	    }
	    if (checkSubstitutableSpecialClasses(obj))
		return;
	    else {
		/* Write out the object as itself */
		outputObject(obj);
	    }
	} catch (ObjectStreamException ee) {
	    if (abortIOException == null) {
		try {
		    /* Prepare to write the exception to the stream.
		     * End blockdatamode in case it's set
		     * Write the exception code
		     * reset the stream to forget all previous objects
		     * write the exception that occurred
		     * reset the stream again so subsequent objects won't map to
		     * the exception or its args.
		     * Continue below to rethrow the exception.
		     */
		    setBlockData(false);

		    writeCode(TC_EXCEPTION);
		    resetStream();
		    this.writeObject(ee);
		    resetStream();

		    // Set the pending exception to be rethrown.
		    abortIOException = ee;
		} catch (IOException fatal) {
		    /* An exception occurred while writing the original exception to
		     * the stream.  The original exception is not complete in
		     * the stream and recusion would be bad. Supercede the original
		     * Exception with a StreamCorruptedException using the message
		     * from this current exception.
		     */
		    abortIOException =
			new StreamCorruptedException(fatal.getMessage());
		}
	    }
	} catch (IOException ee) {
	    // Don't supercede a pending exception, the original will be re-thrown.
	    if (abortIOException == null)
		abortIOException = ee;
	    
	} finally {
	    /* Restore state of previous call incase this is a nested call */
	    recursionDepth--;
	    currentObject = prevObject;
	    currentClassDesc = prevClassDesc;
	    setBlockData(oldBlockDataMode);
	}
	
	/* If the recursion depth is 0, test for and clear the pending exception.
	 * If there is a pending exception throw it.
	 */
	IOException pending = abortIOException;
	if (recursionDepth == 0)
	    abortIOException = null;
	if (pending != null) {
	    throw pending;
	}
    }
    
    /*
     * Check for special cases of serializing objects.
     */
    private boolean checkSpecialClasses(Object obj) throws IOException {

	/*
	 * If this is a class, don't allow substitution
	 */
	if (obj instanceof Class) {
	    outputClass((Class)obj);
	    return true;
	}

	if (obj instanceof ObjectStreamClass) {
	    outputClassDescriptor((ObjectStreamClass)obj);
	    return true;
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
	    outputString((String)obj);
	    return true;
	}

	if (obj.getClass().isArray()) {
	    outputArray(obj);
	    return true;
	}

	return false;
    }

    /**
     * Write the non-static and non-transient fields of the current class
     * to this stream.  This may only be called from the writeObject method
     * of the class being serialized. It will throw the NotActiveException
     * if it is called otherwise.
     * @since     JDK1.1
     */
    public final void defaultWriteObject() throws IOException {
 	
	/*
	 * RMI over IIOP hook. Invoke delegate method if indicated.
	 */
	if (isTrustedSubclass) {
	    ((ObjectOutputStreamDelegate) this).defaultWriteObjectDelegate();
	    return;
	}
 	    
	if (currentObject == null || currentClassDesc == null)
	    throw new NotActiveException("defaultWriteObject");
	
	if (currentClassDesc.getFieldSequence() != null) {
	    boolean prevmode = setBlockData(false);
	    outputClassFields(currentObject, currentClassDesc.forClass(),
			      currentClassDesc.getFieldSequence());
	    setBlockData(prevmode);
	}
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
    public void reset() throws IOException {
	if (currentObject != null || currentClassDesc != null)
	    throw new IOException("Illegal call to reset");
	
	/* Write a reset to the stream. */		
	setBlockData(false);
	writeCode(TC_RESET);
	
	resetStream();			// re-init the stream
	abortIOException = null;
    }

    /*
     * Internal reset function to reinitialize the state of the stream.
     * Reset state of things changed by using the stream.
     */
    private void resetStream() throws IOException {
	if (wireHandle2Object == null) {
	    wireHandle2Object = new Object[100];
	    wireNextHandle = new int[100];
	    wireHash2Handle = new int[101];
	} else {

	    // Storage Optimization for frequent calls to reset method.
	    // Do not reallocate, only reinitialize.
	    for (int i = 0; i < nextWireOffset; i++) {
		wireHandle2Object[i] = null;
		wireNextHandle[i] = 0;
	    }
	}
  	nextWireOffset = 0;
  	for (int i = 0; i < wireHash2Handle.length; i++) {
  	    wireHash2Handle[i] = -1;
  	}
	if (classDescStack == null)
	    classDescStack = new Stack();
	else
	    classDescStack.setSize(0);
	
	for (int i = 0; i < nextReplaceOffset; i++)
	    replaceObjects[i] = null;
	nextReplaceOffset = 0;
	setBlockData(true);		/* Re-enable buffering */
    }
    
    /**
     * Subclasses may implement this method to allow class data to be stored
     * in the stream. By default this method does nothing.
     * The corresponding method in ObjectInputStream is resolveClass.
     * This method is called exactly once for each unique class in the stream.
     * The class name and signature will have already been written to the stream.
     * This method may make free use of the ObjectOutputStream to save
     * any representation of the class it deems suitable (for example,
     * the bytes of the class file).  The resolveClass method in the corresponding
     * subclass of ObjectInputStream must read and use any data or objects
     * written by annotateClass. 
     * annotateClass is called only for normal classes.  Arrays are not normal classes.
     * @exception IOException Any exception thrown by the underlying OutputStream.
     * @since     JDK1.1
     */
    protected void annotateClass(Class cl)
	throws IOException
    {
    }

    /** This method will allow trusted subclasses of ObjectOutputStream
     * to substitute one object for another during
     * serialization. Replacing objects is disabled until
     * enableReplaceObject is called. The enableReplaceObject method
     * checks that the stream requesting to do replacment can be
     * trusted. Every reference to serializable objects is passed to
     * replaceObject.  To insure that the private state of objects is
     * not unintentionally exposed only trusted streams may use
     * replaceObject. <p>
     *
     * When a subclass is replacing objects it must insure that either
     * a complementary substitution must be made during
     * deserialization or that the substituted object is compatible
     * with every field where the reference will be stored.  Objects
     * whose type is not a subclass of the type of the field or array
     * element abort the serialization by raising an exception and the
     * object is not be stored. <p>
     *
     * This method is called only once when each object is first encountered.
     * All subsequent references to the object will be redirected to the
     * new object. This method should return the object to be substituted or
     * the original object. <P>
     *
     * Null can be returned as the object to be substituted, but may
     * cause NullReferenceException in classes that contain references
     * to the original object since they may be expecting an object
     * instead of null.<p>
     *
     * @exception IOException Any exception thrown by the underlying
     * OutputStream.
     * @since     JDK1.1
     */
    protected Object replaceObject(Object obj)
	throws IOException
    {
	return obj;
    }

    /**
     * Enable the stream to do replacement of objects in the stream.
     * If the stream is a trusted class it is allowed to enable replacement.
     * Trusted classes are those classes with a classLoader equals null. <p>
     * 
     * When enabled the replaceObject method is called for every object
     * being serialized.
     * 
     * @exception SecurityException The classloader of this stream object is non-null.
     * @since     JDK1.1
     */
    protected final boolean enableReplaceObject(boolean enable)
	throws SecurityException
    {
 	
	/*
	 * RMI over IIOP hook. Invoke delegate method if indicated.
	 */
	if (isTrustedSubclass) {
	  return ((ObjectOutputStreamDelegate) this).enableReplaceObjectDelegate(enable);
	}
 	    
	boolean previous = enableReplace;
	if (enable) {
	    ClassLoader loader = this.getClass().getClassLoader();
	    if (loader == null) {
		enableReplace = true;
		return previous;
	    }
	    throw new SecurityException("Not trusted class");
	} else {
	    enableReplace = false;
	}
	return previous;
    }

    /**
     * The writeStreamHeader method is provided so subclasses can
     * append or prepend their own header to the stream.
     * It writes the magic number and version to the stream.
     * @since     JDK1.1
     */
    protected void writeStreamHeader() throws IOException {
	writeShort(STREAM_MAGIC);
	writeShort(STREAM_VERSION);
    }

    /**
     * Write a string to the stream.
     * Note that since Strings are Objects, writeObject
     * will behave identically.
     */
    private void outputString(String s) throws IOException {
	/* Allocate a write handle but don't write it to the stream,
	 * Write out the code for a string,
	 * the read can regenerate the same sequence and it saves bytes.
	 */
	assignWireOffset(s);
	writeCode(TC_STRING);
	writeUTF(s); 
    }


    /* Classes are special, they can not created during deserialization,
     * but the appropriate class can be found. 
     */
    private void outputClass(Class aclass) throws IOException {

	writeCode(TC_CLASS);
	/* Find the class descriptor and write it out */
	ObjectStreamClass v = ObjectStreamClass.lookup(aclass);

	if (v == null)
	    throw new NotSerializableException(aclass.getName());

	outputClassDescriptor(v);

	assignWireOffset(aclass);
    }
  

    /* Write the class descriptor */
    private void outputClassDescriptor(ObjectStreamClass classdesc) 
	throws IOException
    {
	if (serializeNullAndRepeat(classdesc))
	    return;

	/* Write out the code for a class
	 * Write out the class name and its serialVersionUID
	 */
	writeCode(TC_CLASSDESC);
	String classname = classdesc.getName();

	writeUTF(classname);
	writeLong(classdesc.getSerialVersionUID());

	/* This is done here to be symetric with the inputClass method
	 * Since the resolveClassName() method may use the stream.
	 * The assignments of wirehandles must be done in the same order
	 */
	assignWireOffset(classdesc);

	/* Write the version description for this class */
	classdesc.write(this);

	/* Give subclassers a chance to add the class implementation
	 * to the stream.  Set BlockData mode so any information they
	 * write can be skipped on reading.
	 */
	boolean prevMode = setBlockData(true);
	annotateClass(classdesc.forClass());
	setBlockData(prevMode);
	writeCode(TC_ENDBLOCKDATA);

	/*
	 * Write out the superclass descriptor of this descriptor
	 * only if it is for a java.io.Serializable class.
	 * else write null.
	 */
	ObjectStreamClass superdesc = classdesc.getSuperclass();
	outputClassDescriptor(superdesc);
    }
    
    /**
     * Write an array out. Note that since Arrays are Objects, writeObject(obj)
     * will behave identically. <br><br>
     * @param o can represent an array of any type/dimension.
     */
    private void outputArray(Object obj)
	throws IOException
    {
	Class currclass = obj.getClass();

	ObjectStreamClass v = ObjectStreamClass.lookup(currclass);

	/* Write out the code for an array and the name of the class */
	writeCode(TC_ARRAY);
	outputClassDescriptor(v);

	/* Assign the wirehandle for this object and outputArrayValues
	 * writes the length and the array contents.
	 */
	assignWireOffset(obj);

	int i, length;
	Class type = currclass.getComponentType();

	if (type.isPrimitive()) {
	    /* Write arrays of primitive types using the DataOutput
	     * methods that convert each element into the output buffer.
	     * The data types are ordered by the frequency
	     * in which they are expected to occur.
	     */
	    if (type == Integer.TYPE) {
		int[] array = (int[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeInt(array[i]);
		}
	    } else if (type == Byte.TYPE) {
		byte[] array = (byte[])obj;
		length = array.length;
		writeInt(length);
		writeInternal(array, 0, length, true);
	    } else if (type == Long.TYPE) {
		long[] array = (long[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeLong(array[i]);
		}
	    } else if (type == Float.TYPE) {
		float[] array = (float[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeFloat(array[i]);
		}
	    } else if (type == Double.TYPE) {
		double[] array = (double[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeDouble(array[i]);
		}
	    } else if (type == Short.TYPE) {
		short[] array = (short[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeShort(array[i]);
		}
	    } else if (type == Character.TYPE) {
		char[] array = (char[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeChar(array[i]);
		}
	    } else if (type == Boolean.TYPE) {
		boolean[] array = (boolean[])obj;
		length = array.length;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    writeBoolean(array[i]);
		}
	    } else {
		throw new InvalidClassException(currclass.getName());
	    }
	} else {
	    Object[] array = (Object[])obj;
	    length = array.length;
	    writeInt(length);
	    for (i = 0; i < length; i++) {
		writeObject(array[i]);
	    }
	}
    }

    /*
     * Put the object into the stream The newObject code is written
     * followed by the ObjectStreamClass for the object's class.  Each
     * of the objects classes is written using the default
     * serialization code and dispatching to Specials where
     * appropriate.
     */
    private void outputObject(Object obj)
	throws IOException
    {
	currentObject = obj;
	Class currclass = obj.getClass();

	/* Get the Class descriptor for this class,
	 * Throw a NotSerializableException if there is none.
	 */
	currentClassDesc = ObjectStreamClass.lookup(currclass);
	if (currentClassDesc == null) {
	    throw new NotSerializableException(currclass.getName());
	}

	/* Write the code to expect an instance and
	 * the class descriptor of the instance
	 */
	writeCode(TC_OBJECT);
	outputClassDescriptor(currentClassDesc);

	/* Assign the next wirehandle */
	assignWireOffset(obj);

	/* If the object is externalizable,
	 * call writeExternal.
	 * else do Serializable processing.
	 */
	if (currentClassDesc.isExternalizable()) {
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
			setBlockData(true); /* Block any data the class writes */
			invokeObjectWriter(obj, currentClassDesc.forClass());
			setBlockData(false);
			writeCode(TC_ENDBLOCKDATA);
		    } else {
			defaultWriteObject();
		    }
		} while (classDescStack.size() > stackMark &&
			 (currentClassDesc = (ObjectStreamClass)classDescStack.pop()) != null);
	    } finally {
		classDescStack.setSize(stackMark);
	    }
	}
    }
    
    /* Serialize the reference if it is NULL or is for an object that
     * was already replaced or already serialized.
     * If the object was already replaced, look for the replacement
     * object in the known objects and if found, write its handle
     * Return True if the reference is either null or a repeat.
     */
    private boolean serializeNullAndRepeat(Object obj)
	throws IOException
    {
    	if (obj == null) {
	    writeCode(TC_NULL);
	    return true;
	}
	/* Look to see if this object has already been replaced.
	 * If so, proceed using the replacement object.
	 */
	if (replaceObjects != null) {
	    for (int i = 0; i < nextReplaceOffset; i+= 2) {
		if (replaceObjects[i] == obj) {
		    obj = replaceObjects[i+1];
		    break;
		}
	    }
	}

	int handle = findWireOffset(obj);
	if (handle >= 0) {
	    /* Add a reference to the stream */
	    writeCode(TC_REFERENCE);
	    writeInt(handle + baseWireHandle);
	    return true;
	}
	return false;		// not serialized, its up to the caller
    }

    /*
     * Locate and return if found the handle for the specified object.
     * -1 is returned if the object does not occur in the array of
     * known objects. 
     */
    private int findWireOffset(Object obj) {
	int hash = System.identityHashCode(obj);
	int index = (hash & 0x7FFFFFFF) % wireHash2Handle.length;

	for (int handle = wireHash2Handle[index];
	     handle >= 0;
	     handle = wireNextHandle[handle]) {
	    
	    if (wireHandle2Object[handle] == obj)
		return handle;
	}
	return -1;
    }

    /* Allocate a handle for an object.
     * The Vector is indexed by the wireHandleOffset
     * and contains the object.
     */
    private void assignWireOffset(Object obj)
	throws IOException
    {
	// Extend the array if there isn't room for this new element
	if (nextWireOffset == wireHandle2Object.length) {
	    Object[] oldhandles = wireHandle2Object;
	    wireHandle2Object = new Object[nextWireOffset*2];
	    System.arraycopy(oldhandles, 0,
			     wireHandle2Object, 0,
			     nextWireOffset);
	    int[] oldnexthandles = wireNextHandle;
	    wireNextHandle = new int[nextWireOffset*2];
	    System.arraycopy(oldnexthandles, 0,
			     wireNextHandle, 0,
			     nextWireOffset);
	    // TBD: Rehash the hash array if necessary
	}
	wireHandle2Object[nextWireOffset] = obj;

	hashInsert(obj, nextWireOffset);

	nextWireOffset++;
	return;
    }


    /*
     * Insert the specified object into the hash array and link if
     * necessary. Put the new object into the hash table and link the
     * previous to it. Newer objects occur earlier in the list.
     */
    private void hashInsert(Object obj, int offset) {
	int hash = System.identityHashCode(obj);
	int index = (hash & 0x7FFFFFFF) % wireHash2Handle.length;
	wireNextHandle[offset] = wireHash2Handle[index];
	wireHash2Handle[index] = offset;
    }

    /*
     * Add a replacement object to the table.
     * The even numbered indices are the original objects.
     * The odd numbered indices are the replacement objects.
     *
     */
    private void addReplacement(Object orig, Object replacement) {
	// Extend the array if there isn't room for this new element

	if (replaceObjects == null) {
	    replaceObjects = new Object[10];
	}
	if (nextReplaceOffset == replaceObjects.length) {
	    Object[] oldhandles = replaceObjects;
	    replaceObjects = new Object[2+nextReplaceOffset*2];
	    System.arraycopy(oldhandles, 0,
			     replaceObjects, 0,
			     nextReplaceOffset);
	}
	replaceObjects[nextReplaceOffset++] = orig;
	replaceObjects[nextReplaceOffset++] = replacement;
    }

    /* Write out the code indicating the type what follows.
     * See ObjectStreamConstants for definitions.
     */
    private void writeCode(int tag)
	throws IOException
    {
	writeByte(tag);
    }

    /*
     * Implement the OutputStream methods.  The stream has
     * two modes used internally to ObjectOutputStream.  When
     * in BlockData mode, all writes are buffered and written
     * to the underlying stream prefixed by a code and length.
     * When not in BlockData mode (false), writes pass directly
     * through to the underlying stream.
     * The BlockData mode is used to encapsulate data written
     * by class specific writeObject methods that is intended
     * only to be read by corresponding readObject method of the 
     * same class.  The blocking of data allows it to be skipped
     * if necessary.
     *
     * The setBlockData method is used to switch buffering
     * on and off.  When switching between on and off
     * the buffer is drained.
     *
     * The actual buffering is very similar to that of
     * BufferedOutputStream but BufferedOutputStream can
     * write to the underlying stream without the headers.
     */
    private boolean blockDataMode;	/* true to buffer and block data */
    private byte[] buf;		/* byte array of buffered data. */
    private int count;		/* count of bytes in the buffer */
    private OutputStream out;	/* Stream to write the data to */

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     * @param b	the byte
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public void write(int data) throws IOException {
	
	if (count >= buf.length)
	    drain();		/* Drain, make room for more */
	buf[count++] = (byte)data;
    }

    /**
     * Writes an array of bytes. This method will block until the bytes
     * are actually written.
     * @param b	the data to be written
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public void write(byte b[]) throws IOException {
	write(b, 0, b.length);
    }

    /*
     * Writes a sub array of bytes. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @param copyOnWrite do not expose b to overrides of ObjectStream.write,
     *                    copy the contents of b to a buffer before writing.
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    private void writeInternal(byte b[], int off, int len, 
			       boolean copyOnWrite) throws IOException {
	if (len < 0)
	    throw new IndexOutOfBoundsException();

	/*
	 * If array will fit in output buffer, copy it in there; otherwise,
	 * drain anything in the buffer and send it through to underlying
	 * output stream directly.
	 */
	int avail = buf.length - count;
	if (len <= avail) {
	    System.arraycopy(b, off, buf, count, len);
	    count += len;
	} else if (! blockDataMode && copyOnWrite) {
	    bufferedWrite(b, off, len);	
	} else {
	    drain();
	    if (blockDataMode) {
		if (len <= 255) {
		    out.write(TC_BLOCKDATA);
		    out.write(len);
		} else {
		    // use block data with int size if necessary
		    out.write(TC_BLOCKDATALONG);
		    // send 32 bit int directly to underlying stream
		    out.write((len >> 24) & 0xFF);
		    out.write((len >> 16) & 0xFF);
		    out.write((len >>  8) & 0xFF);
		    out.write(len & 0xFF);
		}
	    }
	    out.write(b, off, len);
	}
    }


    /**
     * Flushes the stream. This will write any buffered
     * output bytes and flush through to the underlying stream.
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public void flush() throws IOException {
	drain();
	out.flush();
    }

    /**
     * Drain any buffered data in ObjectOutputStream.  Similar to flush
     * but does not propagate the flush to the underlaying stream.
     * @since     JDK1.1
     */
    protected void drain() throws IOException {
	/*
	 * Drain the data buffer.
	 * If the blocking mode is on, prepend the buffer
	 * with the code for a blocked data and the length.
	 * The code is TC_BLOCKDATA and the length is < 255 so it fits
	 * in a byte. 
	 */
	if (count == 0)
	    return;

	/* If in blockdata mode, write the header with the count.
	 * If the count is < 256, use the short header form.
	 * othewise use the long form.
	 */
	if (blockDataMode) {
	    if (count <= 255) {
		out.write(TC_BLOCKDATA);
		out.write(count);
	    } else {
		out.write(TC_BLOCKDATALONG);
		out.write((count >> 24) & 0xFF);
		out.write((count >> 16) & 0xFF);
		out.write((count >>  8) & 0xFF);
		out.write(count & 0xFF);
	    }
	}
	out.write(buf, 0, count);
	count = 0;
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public void close() throws IOException {
	flush();		/* Make sure we're not holding any data */
	out.close();
    }

    /*
     * Set the blockData mode,  if it turned from on to off
     * the buffer is drained.  The previous mode is returned.
     */
    private boolean setBlockData(boolean mode) throws IOException {
	if (blockDataMode == mode)
	    return mode;
	drain();
	blockDataMode = mode;
	return !mode;		/* previous value was the opposite */
    }
	
    /* -------------------------------------------------------------- */
    /*
     * Provide the methods to implement DataOutput.
     * These are copied from DataOutputStream to avoid the overhead
     * of multiple method calls and to buffer the data directly.
     */
    private DataOutputStream dos;

    /**
     * Writes a boolean.
     * @param data the boolean to be written
     * @since     JDK1.1
     */
    public void writeBoolean(boolean data) throws IOException {
	if (count >= buf.length)
	    drain();
	buf[count++] = (byte)(data ? 1 : 0);

    }

    /**
     * Writes a sub array of bytes. 
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public void write(byte b[], int off, int len) throws IOException {
	writeInternal(b, off, len, false);
    }

    /* Use write buffering of byte[] b to prevent exposure of
     * 'b' reference to untrusted overrides of ObjectOutput.write(byte[]).
     * 
     * NOTE: Method is only intended for protecting serializable byte []
     *       fields written by default serialization. Thus, it can 
     *       never get called while in blockDataMode.
     */
    private void bufferedWrite(byte b[], int off, int len) throws IOException {
	int bufAvail = buf.length - count;
	int bytesToWrite = len;
	
	// Handle case where byte array is larger than available buffer.
	if (bytesToWrite > bufAvail) {

	    // Logically: fill rest of 'buf' with 'b' and drain.
	    System.arraycopy(b, off, buf, count, bufAvail);
	    off += bufAvail;
	    bytesToWrite -= bufAvail;
	    out.write(buf, 0, buf.length);
	    count = 0;

	    // Write out buf.length chunks of byte array.
	    while (bytesToWrite >= buf.length) {
		System.arraycopy(b, off, buf, 0, buf.length);
		out.write(buf, 0, buf.length);
		off += buf.length;
		bytesToWrite -= buf.length;

		// Optimization: do not modify or access "count" in this loop.
	    }
	}

	// Put remainder of byte array b into buffer. 
	if (bytesToWrite != 0) {
	    System.arraycopy(b, off, buf, count, bytesToWrite);
	    count += bytesToWrite;
	}
    }

    /**
     * Writes an 8 bit byte.
     * @param data the byte value to be written
     * @since     JDK1.1
     */
    public void writeByte(int data) throws IOException  {
	if (count >= buf.length)
	    drain();
	buf[count++] = (byte)data;
    }

    /**
     * Writes a 16 bit short.
     * @param data the short value to be written
     * @since     JDK1.1
     */
    public void writeShort(int data)  throws IOException {
	if (count + 2 > buf.length)
	    drain();
	buf[count++] = (byte)((data >>>  8));
	buf[count++] = (byte)((data >>>  0));
    }

    /**
     * Writes a 16 bit char.
     * @param data the char value to be written
     * @since     JDK1.1
     */
    public void writeChar(int data)  throws IOException {
	if (count + 2 > buf.length)
	    drain();
	buf[count++] = (byte)((data >>>  8));
	buf[count++] = (byte)((data >>>  0));
    }

    /**
     * Writes a 32 bit int.
     * @param data the integer value to be written
     * @since     JDK1.1
     */
    public void writeInt(int data)  throws IOException {
	if (count + 4 > buf.length)
	    drain();
	buf[count++] = (byte)((data >>> 24));
	buf[count++] = (byte)((data >>> 16));
	buf[count++] = (byte)((data >>>  8));
	buf[count++] = (byte)((data >>>  0));
    }

    /**
     * Writes a 64 bit long.
     * @param data the long value to be written
     * @since     JDK1.1
     */
    public void writeLong(long data)  throws IOException {
	if (count + 8 > buf.length)
	    drain();
	buf[count++] = (byte)((int)(data >>> 56));
	buf[count++] = (byte)((int)(data >>> 48));
	buf[count++] = (byte)((int)(data >>> 40));
	buf[count++] = (byte)((int)(data >>> 32));
	buf[count++] = (byte)((data >>> 24));
	buf[count++] = (byte)((data >>> 16));
	buf[count++] = (byte)((data >>>  8));
	buf[count++] = (byte)((data >>>  0));
    }

    /**
     * Writes a 32 bit float.
     * @param data the float value to be written
     * @since     JDK1.1
     */
    public void writeFloat(float data) throws IOException {
	int value = Float.floatToIntBits(data);
	if (count + 4 > buf.length)
	    drain();
	buf[count++] = (byte)((value >>> 24));
	buf[count++] = (byte)((value >>> 16));
	buf[count++] = (byte)((value >>>  8));
	buf[count++] = (byte)((value >>>  0));
    }

    /**
     * Writes a 64 bit double.
     * @param data the double value to be written
     * @since     JDK1.1
     */
    public void writeDouble(double data) throws IOException {
	long value = Double.doubleToLongBits(data);
	if (count + 8 > buf.length)
	    drain();
	buf[count++] = (byte)((int)(value >>> 56));
	buf[count++] = (byte)((int)(value >>> 48));
	buf[count++] = (byte)((int)(value >>> 40));
	buf[count++] = (byte)((int)(value >>> 32));
	buf[count++] = (byte)((value >>> 24));
	buf[count++] = (byte)((value >>> 16));
	buf[count++] = (byte)((value >>>  8));
	buf[count++] = (byte)((value >>>  0));
    }

    /**
     * Writes a String as a sequence of bytes.
     * @param s the String of bytes to be written
     * @since     JDK1.1
     */
    public void writeBytes(String data) throws IOException {
	dos.writeBytes(data);
    }

    /**
     * Writes a String as a sequence of chars.
     * @param s the String of chars to be written
     * @since     JDK1.1
     */
    public void writeChars(String data) throws IOException {
	dos.writeChars(data);
    }

    /**
     * Writes a String in UTF format.
     * @param str the String in UTF format
     * @since     JDK1.1
     */
    public void writeUTF(String data) throws IOException {
	dos.writeUTF(data);
    }
    
    /*************************************************************/
    
    /* Remember the first exception that stopped this stream. */
    private IOException abortIOException = null;
    
    /* Write the fields of the specified class.
     * The native implemention sorts the field names to put them
     * in cononical order, ignores transient and static fields
     * and invokes the appropriate write* method on this class.
     */
    private native void outputClassFields(Object o, Class cl,
					  int[] fieldSequence)
	throws IOException, InvalidClassException;

    /* Test if Read/WriteObject methods are present, if so
     * invoke writer and return true.
     */
    private native boolean invokeObjectWriter(Object o, Class c)
	throws IOException;

    /* Object references are mapped to the wire handles through a hashtable
     * WireHandles are integers generated by the ObjectOutputStream,
     * they need only be unique within a stream.
     * Objects are assigned sequential handles and stored in wireHandle2Object.
     * The handle for an object is its index in wireHandle2Object.
     * Object with the "same" hashcode are chained using wireHash2Handle.
     * The hashcode of objects is used to index through the wireHash2Handle.
     * -1 is the marker for unused cells in wireNextHandle
     */
    private Object[] wireHandle2Object;
    private int[] wireNextHandle;
    private int[] wireHash2Handle;
    private int nextWireOffset;

    /* The object is the current object and ClassDescriptor is the current
     * subclass of the object being read. Nesting information is kept
     * on the stack.
     */
    private Object currentObject;
    private ObjectStreamClass currentClassDesc;
    private Stack classDescStack;

    /* 
     * Flag set to true to allow replaceObject to be called.
     * Set by enableReplaceObject.
     * The array of replaceObjects and the index of the next insertion.
     */
    boolean enableReplace;
    private Object[] replaceObjects;
    private int nextReplaceOffset;

    /* Recursion level, starts at zero and is incremented for each entry
     * to writeObject.  Decremented before exit.
     */ 
    private int recursionDepth = 0;
    
    /*
     * RMI over IIOP hook: Flag to indicate if we are
     * a trusted subclass that has implemented the delegate
     * interface "sun.io.ObjectOutputStreamDelegate".
     */
    private boolean isTrustedSubclass = false;
}
