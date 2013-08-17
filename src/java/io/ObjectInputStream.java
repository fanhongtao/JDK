/*
 * @(#)ObjectInputStream.java	1.41 98/07/09
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

import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;

import sun.io.ObjectInputStreamDelegate; // RMI over IIOP hook.

/**
 * An ObjectInputStream deserializes primitive data and objects previously
 * written using an ObjectOutputStream.
 * 
 * ObjectOutputStream and ObjectInputStream can provide an application
 * with persistent storage for graphs of objects when used with a
 * FileOutputStream and FileInputStream respectively.
 * ObjectInputStream is used to recover those objects previously
 * serialized. Other uses include passing objects between hosts using
 * a socket stream or for marshaling and unmarshaling arguments and
 * parameters in a remote communication system.<p>
 *
 * ObjectInputStream ensures that the types of all objects in the
 * graph created from the stream match the classes present in the
 * Java Virtual Machine.  Classes are loaded as required using the
 * standard mechanisms. <p>
 *
 * Only objects that support the java.io.Serializable or
 * java.io.Externalizable interface can be read from streams.
 *
 * The method <STRONG>readObject</STRONG> is used to read an object
 * from the stream.  Java's safe casting should be used to get the
 * desired type.  In Java, strings and arrays are objects and are
 * treated as objects during serialization. When read they need to be
 * cast to the expected type.<p>
 *
 * Primitive data types can be read from the stream using the appropriate
 * method on DataInput. <p>
 * 
 * The default deserialization mechanism for objects restores the
 * contents of each field to the value and type it had when it was written.
 * Fields declared as transient or static are ignored by the
 * deserialization process.  References to other objects cause those
 * objects to be read from the stream as necessary.  Graphs of objects
 * are restored correctly using a reference sharing mechanism.  New
 * objects are always allocated when deserializing, which prevents
 * existing objects from being overwritten. <p>
 *
 * Reading an object is analogous to running the constructors of a new
 * object.  Memory is allocated for the object and initialized to zero
 * (NULL).  No-arg constructors are invoked for the non-serializable
 * classes and then the fields of the serializable classes are
 * restored from the stream starting with the serializable class closest to
 * java.lang.object and finishing with the object's most specifiec
 * class. <p>
 *
 * For example to read from a stream as written by the example in
 * ObjectOutputStream: <br>
 *
 * <PRE>
 *	FileInputStream istream = new FileInputStream("t.tmp");
 *	ObjectInputStream p = new ObjectInputStream(istream);
 *
 *	int i = p.readInt();
 *	String today = (String)p.readObject();
 *	Date date = (Date)p.readObject();
 *
 *	istream.close();
 * </PRE>
 *
 * Classes control how they are serialized by implementing either the
 * java.io.Serializable or java.io.Externalizable interfaces.<P>
 *
 * Implementing the Serializable interface allows object serialization
 * to save and restore the entire state of the object and it allows
 * classes to evolve between the time the stream is written and the time it is
 * read.  It automatically traverses references between objects,
 * saving and restoring entire graphs.
 *
 * Serializable classes that require special handling during the
 * serialization and deserialization process should implement both
 * of these methods:<p>
 *
 * <PRE>
 * private void writeObject(java.io.ObjectOutputStream stream)
 *     throws IOException;
 * private void readObject(java.io.ObjectInputStream stream)
 *     throws IOException, ClassNotFoundException; 
 * </PRE><p>
 *
 * The readObject method is responsible for reading and restoring the
 * state of the object for its particular class using data written to
 * the stream by the corresponding writeObject method.  The method
 * does not need to concern itself with the state belonging to its
 * superclasses or subclasses.  State is restored by reading data from
 * the ObjectInputStream for the individual fields and making
 * assignments to the appropriate fields of the object.  Reading
 * primitive data types is supported by DataInput. <p>
 *
 * Serialization does not read or assign values to the fields of any
 * object that does not implement the java.io.Serializable interface.
 * Subclasses of Objects that are not serializable can be
 * serializable. In this case the non-serializable class must have a
 * no-arg constructor to allow its fields to be initialized.  In this
 * case it is the responsibility of the subclass to save and restore
 * the state of the non-serializable class. It is frequently the case that
 * the fields of that class are accessible (public, package, or
 * protected) or that there are get and set methods that can be used
 * to restore the state. <p>
 *
 * Any exception that occurs while deserializing an object will be
 * caught by the ObjectInputStream and abort the reading process. <p>
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
 * @author  Roger Riggs
 * @version 1.41, 07/09/98
 * @see java.io.DataInput
 * @see java.io.ObjectOutputStream
 * @see java.io.Serializable
 * @since   JDK1.1
 */
public class ObjectInputStream extends InputStream
	implements ObjectInput, ObjectStreamConstants
{ 
    /**
     * Create an ObjectInputStream that reads from the specified InputStream.
     * The stream header containing the magic number and version number
     * are read from the stream and verified. This method will block
     * until the corresponding ObjectOutputStream has written and flushed the header.
     * @exception StreamCorruptedException The version or magic number are incorrect.
     * @exception IOException An exception occurred in the underlying stream.
     * @since     JDK1.1
     */
    public ObjectInputStream(InputStream in)
	throws IOException, StreamCorruptedException
  {
        /*
         * RMI over IIOP hook. Check if we are a trusted subclass
         * that has implemented the "sun.io.ObjectInputStream"
         * interface. If so, set our private flag that will be
         * checked in "readObject", "defaultReadObject" and
         * "enableResolveObject". Note that we don't initialize
         * private instance variables in this case as an optimization
         * (subclasses using the hook should have no need for them).
         */
        
        if (this instanceof sun.io.ObjectInputStreamDelegate && this.getClass().getClassLoader() == null) {
            isTrustedSubclass = true;
            return;
        }
        
  	/*
  	 * Save the input stream to read bytes from
  	 * Create a DataInputStream used to read primitive types.
  	 * Setup the DataInputStream to read from this ObjectInputStream
  	 */
	this.in = in;
	dis  = new DataInputStream(this); 
	readStreamHeader();
	resetStream();
    }

    /**
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
    public final Object readObject()
 	throws OptionalDataException, ClassNotFoundException, IOException {
 	
 	    /*
 	     * RMI over IIOP hook. Invoke delegate method if indicated.
 	     */
 	    if (isTrustedSubclass) {
 	        return ((ObjectInputStreamDelegate) this).readObjectDelegate();
 	    }
 	    
 	    /* require local Class for object by default. */
 	    return readObject(true);
    }
 
    /*
      * Private implementation of Read an object from the ObjectInputStream.
      *
      * @param requireLocalClass If false, do not throw ClassNotFoundException
      *                          when local class does not exist.
      *
      * @since     JDK1.2
      */
    private final Object readObject(boolean requireLocalClass)
	throws OptionalDataException, ClassNotFoundException, IOException
    {
	/* If the stream is in blockData mode and there's any data
	 * left throw an exception to report how much there is.
	 */
	if (blockDataMode) {
	    /* Can't use member method available() since it depends on the unreliable
	     *  method InputStream.available().
	     */
	    if (count == 0)
		refill();
	    if (count > 0)
		throw new OptionalDataException(count);
	}
	
	/*
	 * Look ahead now to absorb any pending reset's.
	 * Before changing the state.
	 */
	peekCode();

	/* Save the current state and get ready to read an object. */
	Object prevObject = currentObject;
	ObjectStreamClass prevClass = currentClassDesc;
	boolean prevBlockDataMode = setBlockData(false);
	
	recursionDepth++;	// Entering
	Object obj = null;

	/* 
	 * Check for reset, handle it before reading an object.
	 */

	byte rcode;
	rcode = readCode();
	try {
	    /*
	     * Dispatch on the next code in the stream.
	     */
	    int wireoffset = -1;

	    switch (rcode) {
		
	    case TC_NULL:
		obj = null;
		break;
		
	    case TC_REFERENCE: 
		/* This is a reference to a pre-existing object */
		wireoffset = readInt() - baseWireHandle; 
		
		try {
		    obj = wireHandle2Object.elementAt(wireoffset);
		} catch (ArrayIndexOutOfBoundsException e) {
		    throw new StreamCorruptedException("Reference to object never serialized.");
		}
		break;
		
	    case TC_STRING:
		{
		    obj = readUTF(); 
		    Object localObj = obj; //readUTF does not set currentObject
		    wireoffset = assignWireOffset(obj);

		    /* Allow subclasses to replace the object */
		    if (enableResolve) {
			obj = resolveObject(obj);
		    }

		    if (obj != localObj)
			wireHandle2Object.setElementAt(obj, wireoffset);
		}
		break;
		
	    case TC_CLASS:
		ObjectStreamClass v = 
		    (ObjectStreamClass)readObject(requireLocalClass);
		if (v == null) {
		    /*
		     * No class descriptor in stream or class not serializable
		     */
		    throw new StreamCorruptedException("Class not in stream");
		}
		obj = v.forClass();
		if (obj == null && requireLocalClass) {
		    throw new ClassNotFoundException(v.getName());
		}
		assignWireOffset(obj);
		break;
		
	    case TC_CLASSDESC:
		obj = inputClassDescriptor();
		break;
		
	    case TC_ARRAY:
		wireoffset = inputArray(requireLocalClass);
		obj = currentObject;
		/* Allow subclasses to replace the object */
		if (enableResolve) {
		    obj = resolveObject(obj);
		}

		if (obj != currentObject)
		    wireHandle2Object.setElementAt(obj, wireoffset);
		break;
		
	    case TC_OBJECT:
		wireoffset = inputObject(requireLocalClass);
		obj = currentObject;
		if (enableResolve) {
		    /* Hook for alternate object */
		    obj = resolveObject(obj);
		    wireHandle2Object.setElementAt(obj, wireoffset);
		}
		break;
		
	    case TC_ENDBLOCKDATA:
		if (!prevBlockDataMode)
		    throw new StreamCorruptedException("Unexpected end of block data");
		pushbackCode(TC_ENDBLOCKDATA);
		count = -1;	/* Flag EOF */ 
		throw new OptionalDataException(true);
		
	    case TC_BLOCKDATA:
	    case TC_BLOCKDATALONG:
		if (rcode == TC_BLOCKDATALONG) { /* long block: 32 bit size */
		    int b3 = in.read();
		    int b2 = in.read();
		    int b1 = in.read();
		    int b0 = in.read();
		    if ((b3 | b2 | b1 | b0) < 0)
			throw new StreamCorruptedException("EOF expecting count");
		    count = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
		    if (count < 0)
			throw new StreamCorruptedException("Negative block data size");
		} else {			/* normal block: 8 bit size */
		    count = in.read();
		    if (count < 0)
			throw new StreamCorruptedException("EOF expecting count");
		}

    		if (!prevBlockDataMode)
		    throw new StreamCorruptedException("Unexpected blockdata");
		
		throw new OptionalDataException(count);
		
	    case TC_EXCEPTION:
		/* An exception happened during writing, reset the
		 * stream, read the exception, reset the stream and
		 * throw a writeAbortedException with the exception
		 * that was read.
		 */
		resetStream();
		IOException ee = (IOException)readObject();
		resetStream();
		throw new WriteAbortedException("Writing aborted by exception", ee);

	    default:
		throw new StreamCorruptedException("Unknown code in readObject " + rcode);
	    }
	} catch (OptionalDataException optdata) {
	    /* OptionalDataExceptions won't terminate everything.
	     * so just rethrow it.
	     */
	    throw optdata;
	} catch(IOException ee) {
	    if (abortIOException == null && abortClassNotFoundException == null)
		abortIOException = ee;
	} catch(ClassNotFoundException ee) {
	    if (abortIOException == null && abortClassNotFoundException == null)
		abortClassNotFoundException = ee;
	} finally {
	    recursionDepth --;
	    currentObject = prevObject;
	    currentClassDesc = prevClass;
	    currentClass = currentClassDesc != null ? 
		currentClassDesc.forClass() : null;
	    setBlockData(prevBlockDataMode);
	}
	
	/* Check for thrown exceptions and re-throw them, clearing them if
	 * this is the last recursive call .
	 */
	IOException exIOE = abortIOException;
	if (recursionDepth == 0)
	    abortIOException = null;
	if (exIOE != null)
	    throw exIOE;

	
	ClassNotFoundException exCNF = abortClassNotFoundException;
	if (recursionDepth == 0)
	    abortClassNotFoundException = null;
	if (exCNF != null) {
	    throw exCNF;
	}
	
	// Check if this is the last nested read, if so
	// Call the validations
	if (recursionDepth == 0) {
	    doValidations();
	}

	return obj;
    }

    /**
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
    public final void defaultReadObject()
	throws IOException, ClassNotFoundException, NotActiveException
    {
 	
	/*
	 * RMI over IIOP hook. Invoke delegate method if indicated.
	 */
	if (isTrustedSubclass) {
	    ((ObjectInputStreamDelegate) this).defaultReadObjectDelegate();
	    return;
	}
 	    
	if (currentObject == null || currentClassDesc == null)
	    throw new NotActiveException("defaultReadObject");
	

	if (currentClassDesc.getFieldSequence() != null) {
	    boolean prevmode = setBlockData(false);
	    inputClassFields(currentObject, currentClass,
			     currentClassDesc.getFieldSequence());
	    setBlockData(prevmode);
	}
    }
    
    /**
     * Register an object to be validated before the graph is
     * returned.  While similar to resolveObject these validations are
     * called after the entire graph has been reconstituted.
     * Typically, a readObject method will register the object with
     * the stream so that when all of the objects are restored a final
     * set of validations can be performed.
     * @param obj the object to receive the validation callback.
     * @param prio controls the order of callbacks;zero is a good default.
     * Use higher numbers to be called back earlier, lower numbers for later
     * callbacks. Within a priority, callbacks are processed in no
     * particular order.
     *
     * @exception NotActiveException The stream is not currently reading objects
     * so it is invalid to register a callback.
     * @exception InvalidObjectException The validation object is null.
     * @since     JDK1.1
     */
    public synchronized void registerValidation(ObjectInputValidation obj,
						int prio)
	throws NotActiveException, InvalidObjectException
    {
	if (recursionDepth == 0) {
	    throw new NotActiveException("readObject not Active");
	}
	if (obj == null) {
	    throw new InvalidObjectException("Null is not a valid callback object");
	}

	ValidationCallback cb = new ValidationCallback(obj, prio);

	if (callbacks == null) {
	    callbacks = new Vector(100,100);
	}
	// insert at the end if the priority is less than or equal to
	// the last element.
	if (callbacks.isEmpty() ||
	    ((ValidationCallback)(callbacks.lastElement())).priority >= prio) {
	    callbacks.addElement(cb);
	    return;
	}

	// search for the element with priority that is <= to the new
	// priority, insert before it. 
	int size = callbacks.size();
	for (int i = 0; i < size; i++) {
	    ValidationCallback curr = (ValidationCallback)callbacks.elementAt(i);
	    if (curr.priority <= prio) {
		callbacks.insertElementAt(cb, i);
		break;
	    }
	}
    }

    /*
     * If any validations are pending, do them and cleanup the validation vector
     * if an exception is raised, it is passed on to abort the deserialization.
     */
    private void doValidations() throws InvalidObjectException {
	if (callbacks == null)
	    return;
	
	int size = callbacks.size();
	if (size == 0)
	    return;
	
	for (int i = 0; i < size; i++) {
	    ValidationCallback curr = (ValidationCallback)callbacks.elementAt(i);
	    curr.callback.validateObject();
	}
	/* All pending validations completed successfully. Reset.*/
	callbacks.setSize(0);
    }

    /**
     * Subclasses may implement this method to allow classes to be
     * fetched from an alternate source. 
     *
     * The corresponding method in ObjectOutputStream is
     * annotateClass.  This method will be invoked only once for each
     * unique class in the stream.  This method can be implemented by
     * subclasses to use an alternate loading mechanism but must
     * return a Class object.  Once returned, the serialVersionUID of the
     * class is compared to the serialVersionUID of the serialized class.
     * If there is a mismatch, the deserialization fails and an exception
     * is raised. <p>
     *
     * By default the class name is resolved relative to the class
     * that called readObject. <p>
     *
     * @exception ClassNotFoundException If class of
     * a serialized object cannot be found.
     * @since     JDK1.1
     */
    protected Class resolveClass(ObjectStreamClass v)
	throws IOException, ClassNotFoundException
    {
	/* Resolve by looking up the stack for a non-zero class
	 * loader. If not found use the system loader.
	 */
	return loadClass0(null, v.getName());
    }

    /* Resolve a class name relative to the specified class.  If the
     * class is null find the first available class loader up the
     * stack.  This will resolve classes relative to the caller of
     * ObjectInputStream instead of the itself. Classes must be
     * loaded/resolved relative to the application.
     */
    private native Class loadClass0(Class cl, String classname)
	throws ClassNotFoundException;

    /**
     * This method will allow trusted subclasses of ObjectInputStream
     * to substitute one object for another during
     * deserialization. Replacing objects is disabled until
     * enableResolveObject is called. The enableResolveObject method
     * checks that the stream requesting to resolve object can be
     * trusted. Every reference to serializable objects is passed to
     * resolveObject.  To insure that the private state of objects is
     * not unintentionally exposed only trusted streams may use
     * resolveObject. <p>
     *
     * This method is called after an object has been read but before it is
     * returned from readObject.  The default resolveObject method
     * just returns the new object. <p>
     *
     * When a subclass is replacing objects it must insure that the
     * substituted object is compatible with every field where the
     * reference will be stored.  Objects whose type is not a subclass
     * of the type of the field or array element abort the
     * serialization by raising an exception and the object is not be
     * stored. <p>
     *
     * This method is called only once when each object is first encountered.
     * All subsequent references to the object will be redirected to the
     * new object. <P>
     *
     * @exception IOException Any of the usual Input/Output exceptions.
     * @since     JDK1.1
     */
    protected Object resolveObject(Object obj)
    	throws IOException
    {
	return obj;
    }

    /**
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
    protected final boolean enableResolveObject(boolean enable)
	throws SecurityException
    {
 	
	/*
	 * RMI over IIOP hook. Invoke delegate method if indicated.
	 */
	if (isTrustedSubclass) {
	  return ((ObjectInputStreamDelegate) this).enableResolveObjectDelegate(enable);
	}
 	    
	boolean previous = enableResolve;
	if (enable) {
	    ClassLoader loader = this.getClass().getClassLoader();
	    if (loader == null) {
		enableResolve = true;
		return previous;
	    }
	    throw new SecurityException("Not trusted class");
	} else {
	    enableResolve = false;
	}
	return previous;
    }


    /**
     * The readStreamHeader method is provided to allow subclasses to
     * read and verify their own stream headers. It reads and
     * verifies the magic number and version number.
     * @since     JDK1.1
     */
    protected void readStreamHeader()
	throws IOException, StreamCorruptedException
    {
	short incoming_magic = readShort();
	short incoming_version = readShort();
	if (incoming_magic != STREAM_MAGIC)
	    throw new StreamCorruptedException("InputStream does not contain a serialized object");
	
	if (incoming_version != STREAM_VERSION)
	    throw new StreamCorruptedException("Version Mismatch, Expected " +
					       STREAM_VERSION + " and got " +
					       incoming_version);
    }

    /*
     * Read a ObjectStreamClasss from the stream, it may recursively
     * create another ObjectStreamClass for the superclass it references.
     */
    private ObjectStreamClass inputClassDescriptor()
	throws IOException, InvalidClassException, ClassNotFoundException
    {

	/* Read the class name and hash */
	Class aclass;
	String classname = readUTF(); 
	long hash = readLong();

	/* Read a new class version descriptor from the stream */
	ObjectStreamClass v = new ObjectStreamClass(classname, hash);

	/* Assign the wire handle for this ObjectStreamClass and read it */
	int wireoffset = assignWireOffset(v); 
	v.read(this);

	/* Switch to BlockDataMode and call resolveClass.
	 * It may raise ClassNotFoundException.
	 * Consume any extra data or objects left by resolve class and
	 * read the endOfBlockData. Then switch out of BlockDataMode.
	 */
	boolean prevMode = setBlockData(true);
	try {
	    aclass = resolveClass((ObjectStreamClass)v);
	} catch (ClassNotFoundException e) {
	    /* Not all classes in the serialized stream must be resolvable to
	     * a class in the current VM. The original version of a class need not
	     * resolve a superclass added by an evolved version of the class.
   	     * ClassNotFoundException will be thrown if it is detected elsewhere
	     * that this class would be used as a most derived class.
	     */
	    aclass = null;
 	} catch (NoClassDefFoundError e) {
 	    /* This exception was thrown when looking for an array of class,
 	     * and class could not be found.
 	     */
 	    aclass = null;
	}
	SkipToEndOfBlockData();
	prevMode = setBlockData(prevMode);


	/* Verify that the class returned is "compatible" with
	 * the class description.  i.e. the name and hash match.
	 * Set the class this ObjectStreamClass will use to create 
	 * instances.
	 */
	v.setClass(aclass);

	/* Get the superdescriptor of this one and it set it.
	 */
	ObjectStreamClass superdesc = (ObjectStreamClass)readObject();
	v.setSuperclass(superdesc);

	return v;
    }

    /* Private routine to read in an array. Called from inputObject
     * after the typecode has been read from the stream.
     *
     * @param requireLocalClass If false, do not throw ClassNotFoundException
     *                          when local class does not exist.
     */
    private int inputArray(boolean requireLocalClass)
	throws IOException, ClassNotFoundException
    {
	ObjectStreamClass v = (ObjectStreamClass)readObject();
	Class arrayclass = v.forClass();
	if (arrayclass == null && requireLocalClass)
	    throw new ClassNotFoundException(v.getName());

	/* This can't be done with new because only the top level array
	 * is needed and the type must be set properly.
	 * the lower level arrays will be created when they are read.
	 */
	int length = readInt();
	currentObject = (arrayclass == null) ? 
	    null : allocateNewArray(arrayclass, length);
	int wireoffset = assignWireOffset(currentObject);
	
	/* Read in the values from the array,
	 * It dispatches using the type and read using the read* methods.
	 */
	int i;

	if (arrayclass != null 
	    && arrayclass.getComponentType().isPrimitive()) {
	    Class type = arrayclass.getComponentType();
	    /* Arrays of primitive types read data in blocks and
	     * decode the data types from the buffer.
	     */
	    if (buffer == null)
		buffer = new byte[1024];
	    int offset = buffer.length;
	    int buflen = buffer.length;

	    if (type == Boolean.TYPE) {
		boolean[] array = (boolean[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset >= buflen) {
			int readlen = Math.min(length-i, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    array[i] = (buffer[offset] != 0);
		    offset += 1;
		}
	    } else if (type == Byte.TYPE) {
		byte[] array = (byte[])currentObject;
		int ai = 0;
		while (ai < length) {
		    int readlen = Math.min(length-ai, buflen);
		    readFully(buffer, 0, readlen);
		    System.arraycopy(buffer, 0, array, ai, readlen);
		    ai += readlen;
		}
	    } else if (type == Short.TYPE) {
		short[] array = (short[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 2) {
			int readlen = Math.min((length-i)*2, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    array[i] = (short)(((buffer[offset] & 0xff) << 8) +
				       ((buffer[offset+1] & 0xff) << 0));
		    offset += 2;
		}
	    } else if (type == Integer.TYPE) {
		int[] array = (int[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 4) {
			int readlen = Math.min((length-i)*4, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    array[i] = (((buffer[offset] & 0xff) << 24) +
				((buffer[offset+1] & 0xff) << 16) +
				((buffer[offset+2] & 0xff) << 8) +
				((buffer[offset+3] & 0xff) << 0));
		    offset += 4;
		}
	    } else if (type == Long.TYPE) {
		long[] array = (long[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 8) {
			int readlen = Math.min((length-i)*8, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    int upper = (((buffer[offset] & 0xff) << 24) +
				((buffer[offset+1] & 0xff) << 16) +
				((buffer[offset+2] & 0xff) << 8) +
				((buffer[offset+3] & 0xff) << 0));
		    int lower = (((buffer[offset+4] & 0xff) << 24) +
				((buffer[offset+5] & 0xff) << 16) +
				((buffer[offset+6] & 0xff) << 8) +
				((buffer[offset+7] & 0xff) << 0));
		    array[i] = ((long)upper << 32) + ((long)lower & 0xFFFFFFFFL);
		    offset += 8;
		}
	    } else if (type == Float.TYPE) {
		float[] array = (float[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 4) {
			int readlen = Math.min((length-i)*4, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    int value = (((buffer[offset] & 0xff) << 24) +
				 ((buffer[offset+1] & 0xff) << 16) +
				 ((buffer[offset+2] & 0xff) << 8) +
				 ((buffer[offset+3] & 0xff) << 0));
		    offset += 4;
		    array[i] = Float.intBitsToFloat(value);
		}
	    } else if (type == Double.TYPE) {
		double[] array = (double[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 8) {
			int readlen = Math.min((length-i)*8, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    int upper = (((buffer[offset] & 0xff) << 24) +
				 ((buffer[offset+1] & 0xff) << 16) +
				 ((buffer[offset+2] & 0xff) << 8) +
				 ((buffer[offset+3] & 0xff) << 0));
		    int lower = (((buffer[offset+4] & 0xff) << 24) +
				 ((buffer[offset+5] & 0xff) << 16) +
				 ((buffer[offset+6] & 0xff) << 8) +
				 ((buffer[offset+7] & 0xff) << 0));
		    offset += 8;
		    array[i] = Double.longBitsToDouble((((long)upper) << 32) +
						       (lower & 0xFFFFFFFFL));
		}
	    } else if (type == Character.TYPE) {
		char[] array = (char[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 2) {
			int readlen = Math.min((length-i)*2, buflen);
			readFully(buffer, 0, readlen);
			offset = 0;
		    }
		    array[i] = (char)(((buffer[offset] & 0xff) << 8) +
				      ((buffer[offset+1] & 0xff) << 0));
		    offset += 2;
		}
	    } else {
		throw new InvalidClassException(arrayclass.getName());
	    }
	} else {		// Is array of objects
	    Object[] array = (Object[])currentObject;
	    boolean requiresLocalClass = (arrayclass != null);
	    for (i = 0; i < length; i++) {
		Object obj = readObject(requiresLocalClass);
		if (array != null)
		    array[i] = obj;
	    }
	}

	return wireoffset;
    }

    /*
     * Read an instance of a class from the stream
     * The new object typecode has already been read and used to dispatch to here.
     * The ObjectStreamClass for the class is read and the class
     * of the object retrieved from it.
     * A new object is created of the specified class and
     * each serializable class is processed using either
     * the default serialization methods or class defined special methods
     * if they have been defined.
     * The handle for the object is returned, the object itself is in currentObject.
     *
     * @param requireLocalClass If false, do not throw ClassNotFoundException
     *                          when local class does not exist.
     */
    private int inputObject(boolean requireLocalClass)
	throws IOException, ClassNotFoundException
    {
	int handle = -1;
	/*
	 * Get the descriptor and then class of the incomming object.
	 */
	currentClassDesc = (ObjectStreamClass)readObject();
	currentClass = currentClassDesc.forClass();

	/* require the class if required or if Externalizable data
	 * can not be skipped if it was not written in BlockData mode.
	 */
	if (currentClass == null &&
	    (requireLocalClass ||
	     (currentClassDesc.isExternalizable() &&
	      !currentClassDesc.hasExternalizableBlockDataMode())))
	    throw new ClassNotFoundException(currentClassDesc.getName());
	

	/* If Externalizable,
	 *  Create an instance and tell it to read its data.
	 * else,
	 *  Handle it as a serializable class.
	 */
	if (currentClassDesc.isExternalizable()) {
	    try {
		currentObject = (currentClass == null) ?
		    null : allocateNewObject(currentClass, currentClass);
		handle = assignWireOffset(currentObject);
		boolean prevmode = blockDataMode;
		if (currentClassDesc.hasExternalizableBlockDataMode()) {
		    prevmode = setBlockData(true);
		}
		try {
		    if (currentObject != null) {
			Externalizable ext = (Externalizable)currentObject;
			ext.readExternal(this);
		    }
		} finally {
		    if (currentClassDesc.hasExternalizableBlockDataMode()) {
			SkipToEndOfBlockData();
			setBlockData(prevmode);
		    }
		}
	    } catch (IllegalAccessException e) {
		throw new InvalidClassException(currentClass.getName(),
					    "IllegalAccessException");
	    } catch (InstantiationException e) {
		throw new InvalidClassException(currentClass.getName(),
					    "InstantiationException");
	    }
	} else {
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
	    for (currdesc = currentClassDesc, currclass = currentClass;
		 currdesc != null;
		 currdesc = currdesc.getSuperclass()) {

		/*
		 * Search the classes to see if thisthe class of this
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
		}
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
	    }

	    /* Allocate a new object.  The object is only constructed
	     * above the highest serializable class and is set to
	     * default values for all more specialized classes.
	     * Remember the next wirehandle goes with the new object
	     */
	    try {
		currentObject = (currentClass == null) ? 
		    null: allocateNewObject(currentClass, currclass);
	    } catch (NoSuchMethodError e) {
		throw new InvalidClassException(currclass.getName(),
					    "NoSuchMethodError");
	    } catch (IllegalAccessException e) {
		throw new InvalidClassException(currclass.getName(),
					    "IllegalAccessException");
	    } catch (InstantiationException e) {
		throw new InvalidClassException(currclass.getName(),
					    "InstantiationException");
	    }
	    handle = assignWireOffset(currentObject);
	    
	    /* 
	     * For all the pushed descriptors and classes.
	     * If there is a descriptor but no class, skip the
	     * data for that class.
	     * If there is a class but no descriptor, just advance,
	     * The classes fields have already been initialized to default
	     * values.
	     * Otherwise, there is both a descriptor and class,
	     * 	if the class has its own writeObject and readObject methods
	     *	    set blockData = true; and call the readObject method
	     *	else
	     *	    invoke the defaultReadObject method
	     *	if the stream was written by class specific methods
	     *	    skip any remaining data a objects until TC_ENDBLOCKDATA
	     * Avoid setting BlockData=true unless necessary becase it flushes
	     * the buffer.
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
			setBlockData(true);	/* any reads are from datablocks */
			ObjectStreamClass localDesc = currentClassDesc.localClassDescriptor();
			if (!invokeObjectReader(currentObject, currentClass)) {
			    defaultReadObject();
			}
		    } else {
			/* No local class for this descriptor,
			 * Skip over the data for this class.
			 * like defaultReadObject with a null currentObject.
			 * The native code will read the values but discard them.
			 */
			if (currentClassDesc.getFieldSequence() != null) {
			    boolean prevmode = setBlockData(false);
			    inputClassFields(null, currentClass,
					     currentClassDesc.getFieldSequence());
			    setBlockData(prevmode);
			}
		    }

		    /*
		     * If the source class (stream) had a write object method
		     * it may have written more data and will have written the
		     * TC_ENDBLOCKDATA.  Skip anything up to that and read it.
		     */
		    if (currentClassDesc.hasWriteObject()) {
			setBlockData(true);
			SkipToEndOfBlockData();
		    }
		    setBlockData(false);
		}
	    } finally {
		// Make sure we exit at the same stack level as when we started.
		spClass = spBase;
	    }
	}
	return handle;
    }

    /*
     * Skip any unread block data and objects up to the next
     * TC_ENDBLOCKDATA.  Anybody can do this because readObject
     * handles the details of reporting if there is data left.
     * Try reading objects.  If it throws optional data
     * skip over it and try again. 
     */
    private void SkipToEndOfBlockData()
	throws IOException, ClassNotFoundException
    {
	while (peekCode() != TC_ENDBLOCKDATA) {
	    try {
		/* do not require a local Class equivalent of object being read. */
		Object ignore = readObject(false);
	    } catch (OptionalDataException data) {
		if (data.length > 0)
		    skip(data.length);
	    }
	}
	readCode();			/* Consume TC_ENDBLOCKDATA */
    }
    
    /*
     * Reset the stream to be just like it was after the constructor.
     */
    private void resetStream() throws IOException {
	if (wireHandle2Object == null)
	    wireHandle2Object = new Vector(100,100);
	else
	    wireHandle2Object.setSize(0);   // release all references.
	nextWireOffset = 0;

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
	setBlockData(true);		// Re-enable buffering
	if (callbacks != null)
	    callbacks.setSize(0);	// discard any pending callbacks
    }

    /* Allocate a handle for an object.
     * The Vector is indexed by the wireHandleOffset
     * and contains the object.
     */
    private int assignWireOffset(Object obj)
	throws IOException
    {
	wireHandle2Object.addElement(obj);
	if (++nextWireOffset != wireHandle2Object.size())
	  throw new StreamCorruptedException("Elements not assigned in order");;
	return nextWireOffset-1;
    }

    /*
     * Peek at the next control code in the stream.
     * If the code has not been peeked at yet, read it from the stream.
     */
    private byte peekCode() throws IOException, StreamCorruptedException{
	while (currCode == 0) {

	    int newcode = in.read();	// Read byte from the underlying stream
	    if (newcode < 0) 
		throw new EOFException("Expecting code");
	    
	    currCode = (byte)newcode;
	    if (currCode < TC_BASE || currCode > TC_MAX)
		throw new StreamCorruptedException("Type code out of range, is " + currCode);

	    /* 
	     * Handle reset as a hidden code and reset the stream.
	     */
	    if (currCode == TC_RESET) {
		if (recursionDepth != 0 ||
		    currentObject != null ||
		    currentClassDesc != null)
		    throw new StreamCorruptedException("Illegal stream state for reset");
	    
		/* Reset the stream, and repeat the peek at the next code
		 */
		resetStream();
		currCode = 0;
	    }
	}
	return currCode;
    }
    
    /*
     * Return the next control code in the stream.
     * peekCode gets the next code.  readCode just consumes it.
     */
    private byte readCode()
	throws IOException, StreamCorruptedException
    {
	byte tc = peekCode();
	currCode = 0;
	return tc;
    }
    
    /*
     * Put back the specified code to be peeked at next time.
     */
    private void pushbackCode(byte code) {
	currCode = code;
    }
    /* -----------------------------------------------------*/
    /*
     * Implement the InputStream methods.  The stream has
     * two modes used internally to ObjectInputStream.  When
     * in BlockData mode, all reads are only from datablocks
     * as original written. End of data (-1) is returned
     * if something other than a datablock is next in the stream.
     * When not in BlockData mode (false), reads pass directly
     * through to the underlying stream.
     * The BlockData mode is used to encapsulate data written
     * by class specific writeObject methods that is intended
     * only to be read by corresponding readObject method of the 
     * same class.  The blocking of data allows it to be skipped
     * if necessary.
     *
     * The setBlockData method is used to switch buffering
     * on and off.  When switching between on and off 
     * there must be no data pending to be read. This is 
     * an internal consistency check that will throw an exception. 
     *
     */
    private InputStream in;

    /*
     * Count of bytes available from blockData, if zero, call refill
     * to look for more.  If -1 always return eof (-1)
     */
    private int count;
	    
    private boolean blockDataMode;

    private byte[] buffer;	// buffer for reading array data

    /*
     * Set the blockdata buffering mode.
     * If it is being set to false after being true there must
     * be no pending data. If count > 0 a corrupted exception is thrown.
     */
    private boolean setBlockData(boolean mode) throws IOException {
	if (blockDataMode == mode)
	    return mode;
	if (blockDataMode && count > 0)
	    throw new StreamCorruptedException("Unread data");

	/* Set count to allow reading or not */
	count =  mode ? 0 : -1;

	blockDataMode = mode;
	return !mode;
    }
    
    /**
     * Reads a byte of data. This method will block if no input is 
     * available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public int read() throws IOException {
	int data;
	if (blockDataMode) {
	    while (count == 0)
		refill();
	    if (count < 0)
		return -1;			/* EOF */
	    data = in.read();
	    if (data >= 0)
		count--;
	} else {
	    data = in.read();		/* read directly from input stream */
	}
	return data;
    }

    /*
     * Expect the next thing in the stream is a datablock, If its a
     * datablock, extract the count of bytes to allow.  If data is not
     * available set the count to zero.  On error or EOF, set count to -1.
     */
    private void refill() throws IOException {
	count = -1;		/*  No more data to read, EOF */
	byte code;
	try {
	    code = peekCode();
	} catch (EOFException e) {
	    return;
	}
	if (code == TC_BLOCKDATA) {
	    code = readCode();			/* Consume the code */
	    int c = in.read();
	    if (c < 0)
		throw new StreamCorruptedException("EOF expecting count");
	    
	    count = c & 0xff;
	} else if (code == TC_BLOCKDATALONG) {
	    code = readCode();
	    int b3 = in.read();
	    int b2 = in.read();
	    int b1 = in.read();
	    int b0 = in.read();
	    if ((b3 | b2 | b1 | b0) < 0)
		throw new StreamCorruptedException("EOF expecting count");
	    int c = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
	    /*
	     * The 32 bit integer size in the long block data format is
	     * signed (unlike like the normal block data format), and
	     * negative values are invalid.
	     */
	    if (c < 0)
		throw new StreamCorruptedException("Negative block data size");

	    count = c;
	}
    }
    
    /**
     * Reads into an array of bytes.  This method will
     * block until some input is available.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public int read(byte[] data, int offset, int length) throws IOException {
	int v;
	int i;

	if (length < 0)
	    throw new IndexOutOfBoundsException();

	if (blockDataMode) {
	    while (count == 0)
		refill();
	    if (count < 0)
		return -1;
	    int l = Math.min(length, count);
	    i = in.read(data, offset, l);
	    if (i > 0)
		count -= i;
	    return i;			/* return number of bytes read */
	} else {
	    /* read directly from input stream */
	    return in.read(data, offset, length);
	}
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     * @return the number of available bytes.
     * @since     JDK1.1
     */
    /*
     * If in blockdataMode returns the number of bytes in the
     * current block. If that is zero, it will try to read
     * another blockdata from the stream if any data is available from the
     * underlying stream..
     * If not in blockdata mode it returns zero.
     */
    public int available() throws IOException {
	if (blockDataMode) {
	    if (count == 0 && in.available() > 0)
		refill();
	    if (count >= 0) {
		return count;
	    } else
		return 0;	/* EOF is no bytes available */
	} else {
	    return 0;		/* Not blockdata, no bytes available */
	}
    }

    /**
     * Closes the input stream. Must be called
     * to release any resources associated with
     * the stream.
     * @exception IOException If an I/O error has occurred.
     * @since     JDK1.1
     */
    public void close() throws IOException {
	in.close();
    }

    /* -----------------------------------------------------*/
    /*
     * Provide the methods to implement DataInput.
     * They delegate to an Instance of DataInputStream that
     * reads its input from the ObjectInputStream.
     * This allows this stream to manage the blocked data the data
     * as necessary.
     */
    private DataInputStream dis;
    
    /**
     * Reads in a boolean.
     * @return the boolean read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public boolean readBoolean() throws IOException {
	return dis.readBoolean();
    }

    /**
     * Reads an 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public byte readByte() throws IOException  {
	return dis.readByte();
    }

    /**
     * Reads an unsigned 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public int readUnsignedByte()  throws IOException {
	return dis.readUnsignedByte();
    }

    /**
     * Reads a 16 bit short.
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public short readShort()  throws IOException {
	return dis.readShort();
    }

    /**
     * Reads an unsigned 16 bit short.
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public int readUnsignedShort() throws IOException {
	return dis.readUnsignedShort();
    }

    /**
     * Reads a 16 bit char.
     * @return the 16 bit char read. 
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public char readChar()  throws IOException {
	return dis.readChar();
    }

    /**
     * Reads a 32 bit int.
     * @return the 32 bit integer read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public int readInt()  throws IOException {
	return dis.readInt();
    }

    /**
     * Reads a 64 bit long.
     * @return the read 64 bit long.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public long readLong()  throws IOException {
	return dis.readLong();
    }

    /**
     * Reads a 32 bit float.
     * @return the 32 bit float read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public float readFloat() throws IOException {
	return dis.readFloat();
    }

    /**
     * Reads a 64 bit double.
     * @return the 64 bit double read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public double readDouble() throws IOException {
	return dis.readDouble();
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public void readFully(byte[] data) throws IOException {
	dis.readFully(data);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes to read
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public void readFully(byte[] data, int offset, int size) throws IOException {
	if (size < 0)
	    throw new IndexOutOfBoundsException();
	
	dis.readFully(data, offset, size);
    }

    /**
     * Skips bytes, block until all bytes are skipped.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     * @since     JDK1.1
     */
    public int skipBytes(int len) throws IOException {
	return dis.skipBytes(len);
    }

    /**
     * Reads in a line that has been terminated by a \n, \r, 
     * \r\n or EOF.
     * @return a String copy of the line.
     * @since     JDK1.1
     */
    public String readLine() throws IOException {
	return dis.readLine();
    }

    /**
     * Reads a UTF format String.
     * @return the String.
     * @since     JDK1.1
     */
     public String readUTF() throws IOException {
	return dis.readUTF();
    }
    
    /* Remember the first exception that stopped this stream. */
    private IOException abortIOException = null;
    private ClassNotFoundException abortClassNotFoundException = null;

    /* Read the fields of the specified class.
     * The native implementation sorts the field names to put them
     * in cononical order, ignores transient and static fields
     * and invokes the appropriate write* method on this class.
     */
    private native void inputClassFields(Object o, Class cl, int[] fieldSequence)
	throws InvalidClassException,
	    StreamCorruptedException, ClassNotFoundException, IOException;

    /* Allocate a new object for the specified class
     * Native since newInstance may not be able to find a zero arg constructor.
     */
    private static native Object allocateNewObject(Class aclass, Class initclass)
	throws InstantiationException, IllegalAccessException;
    
    /* Allocate a new array for the specified class
     * Native since the type of the array needs to be set to the class
     */
    private static native Object allocateNewArray(Class aclass, int length);
    
    /* Test if readObject/Writer methods are present, if so
     * invoke the reader and return true.
     */
    private native boolean invokeObjectReader(Object o, Class aclass)
	throws InvalidClassException,
	    StreamCorruptedException, ClassNotFoundException, IOException;

    
    /* The object is the current object and class is the the current
     * subclass of the object being read. Nesting information is kept
     * on the stack.
     */
    private Object currentObject;
    private ObjectStreamClass currentClassDesc;
    private Class currentClass;

    /* Arrays used to keep track of classes and ObjectStreamClasses
     * as they are being merged; used in inputObject.
     * spClass is the stack pointer for both.
     */
    ObjectStreamClass[] classdesc;
    Class[] classes;
    int spClass;

    /* During deserialization the objects in the stream are represented by
     * handles (ints), they need to be mapped to the objects.
     * The vector is indexed by the offset between baseWireHandle and the
     * wire handle in the stream.
     */
    private Vector wireHandle2Object;
    private int nextWireOffset;

    /* Vector of validation callback objects
     * The vector is created as needed, and ValidationCallback objects added
     * for each call to registerObject. The vector is maintained in
     * order of highest (first) priority to lowest
     */
    private Vector callbacks;

    /* Recursion level, starts at zero and is incremented for each entry
     * to readObject.  Decremented before exit.
     */ 
    private int recursionDepth;

    /* Last code Peek'ed, if any */
    private byte currCode;

    /* 
     * Flag set to true to allow resolveObject to be called.
     * Set by enableResolveObject.
     */
    boolean enableResolve;
    
    /*
     * RMI over IIOP hook: Flag to indicate if we are
     * a trusted subclass that has implemented the delegate
     * interface "sun.io.ObjectInputStreamDelegate".
     */
    private boolean isTrustedSubclass = false;
}

// Internal class to hold the Callback object and priority
class ValidationCallback {
    ValidationCallback(ObjectInputValidation cb, int prio) {
	callback = cb;
	priority = prio;
    }

    int priority;			// priority of this callback
    ObjectInputValidation callback; // object to be called back
}
