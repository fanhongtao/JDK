/*
 * @(#)ObjectInputStream.java	1.78 98/09/24
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

import java.util.ArrayList;
import java.util.Stack;
import java.util.Hashtable;
import java.lang.Math;
import java.lang.reflect.InvocationTargetException;

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
 * @version 1.78, 09/24/98
 * @see java.io.DataInput
 * @see java.io.ObjectOutputStream
 * @see java.io.Serializable
 * @see <a href="http://java.sun.com/products/jdk/1.2/docs/guide/serialization/spec/input.doc.html"> Object Serialization Specification, Section 3, Object Input Classes</a>
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
     */
    public ObjectInputStream(InputStream in)
	throws IOException, StreamCorruptedException
    {
        enableSubclassImplementation = false;
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
     * Provide a way for subclasses that are completely reimplementing
     * ObjectInputStream to not have to allocate private data just used by
     * this implementation of ObjectInputStream.
     *
     * <p>If there is a security manager installed, this method first calls the
     * security manager's <code>checkPermission</code> method with the
     * <code>SerializablePermission("enableSubclassImplementation")</code>
     * permission to ensure it's ok to enable subclassing.
     *
     * @exception IOException   Thrown if not called by a subclass.
     * @throws SecurityException
     *    if a security manager exists and its 
     *    <code>checkPermission</code> method denies
     *    enabling subclassing.
     *
     * @see SecurityManager#checkPermission
     * @see java.security.SerializablePermission
     */
    protected ObjectInputStream() throws IOException, SecurityException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
	enableSubclassImplementation = true;
    }

    /**
     * Read an object from the ObjectInputStream.
     * The class of the object, the signature of the class, and the values
     * of the non-transient and non-static fields of the class and all
     * of its supertypes are read.  Default deserializing for a class can be
     * overriden using the writeObject and readObject methods.
     * Objects referenced by this object are read transitively so
     * that a complete equivalent graph of objects is reconstructed by 
     * readObject. <p>
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
     * InputStream and leave it in an indeterminate state; it is up to the 
     * caller to ignore or recover the stream state.
     * @exception java.lang.ClassNotFoundException Class of a serialized object
     *      cannot be found.
     * @exception InvalidClassException Something is wrong with a class used by
     *     serialization.
     * @exception StreamCorruptedException Control information in the
     *     stream is inconsistent.
     * @exception OptionalDataException Primitive data was found in the 
     * stream instead of objects.
     * @exception IOException Any of the usual Input/Output related exceptions.
     */
    public final Object readObject()
	throws OptionalDataException, ClassNotFoundException, IOException {
	if (enableSubclassImplementation)
	    return readObjectOverride();
	else {

	    /* require local Class for object by default. */
	    return readObject(true);
	}
    }

    /**
     * This method is called by trusted subclasses of ObjectOutputStream
     * that constructed ObjectOutputStream using the 
     * protected no-arg constructor. The subclass is expected to provide
     * an override method with the modifier "final".
     *
     * @return the Object read from the stream.
     *
     * @see #ObjectInputStream()
     * @see #readObject()
     * @since JDK 1.2
     */
    protected Object readObjectOverride()
 	throws OptionalDataException, ClassNotFoundException, IOException 
    {
	return null;
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
		    obj = wireHandle2Object.get(wireoffset);
		} catch (ArrayIndexOutOfBoundsException e) {
		    throw new StreamCorruptedException("Reference to object never serialized.");
		}
		break;
		
	    case TC_STRING:
		{
		    obj = readUTF(); 
		    Object localObj = obj;
		    wireoffset = assignWireOffset(obj);
		    /* Allow subclasses to replace the object */
		    if (enableResolve) {
			obj = resolveObject(obj);
		    }

		    if (obj != localObj)
			wireHandle2Object.set(wireoffset, obj);
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
		    wireHandle2Object.set(wireoffset, obj);
		break;
		
	    case TC_OBJECT:
		wireoffset = inputObject(requireLocalClass);
		obj = currentObject;

		/* Allow the object to resolve itself. */
		if (currentObject != null && 
		    currentClassDesc != null && 
		    currentClassDesc.isResolvable()) {
		    obj = 
			ObjectStreamClass.invokeMethod(currentClassDesc.readResolveMethod,
						       obj, null);
		}

		/* Allow subclasses to replace the object */
		if (enableResolve) {
		    obj = resolveObject(obj);
		}

		if (obj != currentObject)
		    wireHandle2Object.set(wireoffset, obj);
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
     */
    public void defaultReadObject()
	throws IOException, ClassNotFoundException, NotActiveException
    {
	if (currentObject == null || currentClassDesc == null)
	    throw new NotActiveException("defaultReadObject");

	ObjectStreamField[] fields = 
	    currentClassDesc.getFieldsNoCopy();
	if (fields.length > 0) {
	    boolean prevmode = setBlockData(false);
	    inputClassFields(currentObject, currentClass, fields);
	    setBlockData(prevmode);
	}
    }
    
    /**
     * Reads the persistent fields from the stream and makes them 
     * available by name.
     * 
     * @exception java.lang.ClassNotFoundException if the class of a serialized
     *              object could not be found.
     * @exception IOException        if an I/O error occurs.
     * @exception NotActiveException if the stream is not currently reading
     *              objects.
     * @since JDK 1.2
     */
    public ObjectInputStream.GetField readFields()
    	throws IOException, ClassNotFoundException, NotActiveException
    {
	if (currentObject == null || currentClassDesc == null)
	    throw new NotActiveException("defaultReadObject");

	// TBD: Interlock w/ defaultReadObject

	GetFieldImpl curr = new GetFieldImpl(currentClassDesc);
	currentGetFields = curr;
	boolean prevmode = setBlockData(false);
	curr.read(this);
	setBlockData(prevmode);
	return curr;
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
     * @exception NotActiveException The stream is not currently reading 
     * objects so it is invalid to register a callback.
     * @exception InvalidObjectException The validation object is null.
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
	    callbacks = new ArrayList();
	}
	// insert at the end if the priority is less than or equal to
	// the last element.
	if (callbacks.isEmpty() ||
	    ((ValidationCallback)(callbacks.get(callbacks.size()-1))).priority >= prio) {
	    callbacks.add(cb);
	    return;
	}

	// search for the element with priority that is <= to the new
	// priority, insert before it. 
	int size = callbacks.size();
	for (int i = 0; i < size; i++) {
	    ValidationCallback curr = (ValidationCallback)callbacks.get(i);
	    if (curr.priority <= prio) {
		callbacks.add(i, cb);
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
	for (int i = 0; i < size; i++) {
	    ValidationCallback curr = (ValidationCallback)callbacks.get(i);
	    curr.callback.validateObject();
	}
	callbacks.clear();
    }

    /**
     * Load the local class equivalent of the specified stream class description.
     *
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
     */
    protected Class resolveClass(ObjectStreamClass v)
	throws IOException, ClassNotFoundException
    {
	/* Resolve by looking up the stack for a non-zero class
	 * loader. If not found use the system class loader.
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
     */
    protected Object resolveObject(Object obj)
    	throws IOException
    {
	return obj;
    }


    /**
     * Enable the stream to allow objects read from the stream to be replaced.
     * 
     * When enabled, the resolveObject method is called for every object
     * being deserialized.
     *
     * If <i>enable</i> is true, and there is a security manager installed, 
     * this method first calls the
     * security manager's <code>checkPermission</code> method with the
     * <code>SerializablePermission("enableSubstitution")</code>
     * permission to ensure it's ok to 
     * enable the stream to allow objects read from the stream to be replaced.
     * 
     * @throws SecurityException
     *    if a security manager exists and its 
     *    <code>checkPermission</code> method denies
     *    enabling the stream to allow objects read from the stream to be replaced.
     *
     * @see SecurityManager#checkPermission
     * @see java.security.SerializablePermission
     */
    protected boolean enableResolveObject(boolean enable)
	throws SecurityException
    {
	boolean previous = enableResolve;
	if (enable) {
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null) sm.checkPermission(SUBSTITUTION_PERMISSION);
	    enableResolve = true;
	} else {
	    enableResolve = false;
	}
	return previous;
    }


    /**
     * The readStreamHeader method is provided to allow subclasses to
     * read and verify their own stream headers. It reads and
     * verifies the magic number and version number.
     */
    protected void readStreamHeader()
	throws IOException, StreamCorruptedException
    {
	short incoming_magic = 0;
	short incoming_version = 0;
	try {
	    incoming_magic = readShort();
	    incoming_version = readShort();
	} catch (EOFException e) {
	    throw new StreamCorruptedException("Caught EOFException " +
					       "while reading the stream header");
	} 
	if (incoming_magic != STREAM_MAGIC)
	    throw new StreamCorruptedException("InputStream does not contain a serialized object");
	
	if (incoming_version != STREAM_VERSION)
	    throw new StreamCorruptedException("Version Mismatch, Expected " +
					       STREAM_VERSION + " and got " +
					       incoming_version);
    }

    /*
     * Read a ObjectStreamClass from the stream, it may recursively
     * create other ObjectStreamClasses for the classes it references.
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
	    /* if the most derived class, this exception will be thrown at a later time. */
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
     */
    private int inputArray(boolean requireLocalClass)
	throws IOException, ClassNotFoundException
    {
	/* May raise ClassNotFoundException */
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
     */
    private int inputObject(boolean requireLocalClass)
	throws IOException, ClassNotFoundException
    {
	int handle = -1;
	/*
	 * Get the descriptor and then class of the incoming object.
	 */
	currentClassDesc = (ObjectStreamClass)readObject();
	currentClass = currentClassDesc.forClass();
	if (currentClass == null && requireLocalClass)
	    throw new ClassNotFoundException(currentClassDesc.getName());
	
	if (requireLocalClass)
	    currentClassDesc.verifyInstanceDeserialization();
	
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
		try {
		    if (currentClassDesc.hasExternalizableBlockDataMode()) {
			prevmode = setBlockData(true);
		    }

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
	    } catch (NoSuchMethodError e) {
		throw new InvalidClassException(currentClass.getName(),
						e.getMessage());
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
		                 null : allocateNewObject(currentClass, currclass);
	    } catch (NoSuchMethodError e) {
		throw new InvalidClassException(currclass.getName(),
						e.getMessage());
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
			if (!invokeObjectReader(currentObject)) {
			    defaultReadObject();
			}
		    } else {
			/* No local class for this descriptor,
			 * Skip over the data for this class.
			 * like defaultReadObject with a null currentObject.
			 * The code will read the values but discard them.
			 */
			ObjectStreamField[] fields = 
			    currentClassDesc.getFieldsNoCopy();
			if (fields.length > 0) {
			    boolean prevmode = setBlockData(false);
			    inputClassFields(null, currentClass, fields);
			    setBlockData(prevmode);
			}
		    }

		    /*
		     * If the source class (stream) had a write object method
		     * it may have written more data and will have written the
		     * TC_ENDBLOCKDATA.  Skip anything up to that and read it.
		     */
		    if (currentClassDesc.hasWriteObject()) {
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

		/* do not require a local Class equivalent of object being read.*/
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
	    wireHandle2Object = new ArrayList();
	else
	    wireHandle2Object.clear();
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
	    callbacks.clear();	// discard any pending callbacks
    }

    /* Allocate a handle for an object.
     * The list is indexed by the wireHandleOffset
     * and contains the object.
     */
    private int assignWireOffset(Object obj)
	throws IOException
    {
	wireHandle2Object.add(obj);
	if (++nextWireOffset != wireHandle2Object.size())
	  throw new StreamCorruptedException(
	      "Elements not assigned in order");
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
     * block until some input is available. Consider
     * using java.io.DataInputStream.readFully to read exactly
     * 'length' bytes.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     * @see java.io.DataInputStream#readFully(byte[],int,int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
	int v;
	int i;

	if (b == null) {
	    throw new NullPointerException();
	} else if ((off < 0) || (off > b.length) || (len < 0) ||
		   ((off + len) > b.length) || ((off + len) < 0)) {
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return 0;
	}

	if (blockDataMode) {
	    while (count == 0)
		refill();
	    if (count < 0)
		return -1;
	    int l = Math.min(len, count);
	    i = in.read(b, off, l);
	    if (i > 0)
		count -= i;
	    return i;			/* return number of bytes read */
	} else {
	    /* read directly from input stream */
	    return in.read(b, off, len);
	}
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     * @return the number of available bytes.
     */
    public int available() throws IOException {
	/*
	 * If in blockdataMode returns the number of bytes in the
	 * current block. If that is zero, it will try to read
	 * another blockdata from the stream if any data is available from the
	 * underlying stream..
	 * If not in blockdata mode it returns zero.
	 */
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
     */
    public boolean readBoolean() throws IOException {
	return dis.readBoolean();
    }

    /**
     * Reads an 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public byte readByte() throws IOException  {
	return dis.readByte();
    }

    /**
     * Reads an unsigned 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int readUnsignedByte()  throws IOException {
	return dis.readUnsignedByte();
    }

    /**
     * Reads a 16 bit short.
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public short readShort()  throws IOException {
	return dis.readShort();
    }

    /**
     * Reads an unsigned 16 bit short.
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int readUnsignedShort() throws IOException {
	return dis.readUnsignedShort();
    }

    /**
     * Reads a 16 bit char.
     * @return the 16 bit char read. 
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public char readChar()  throws IOException {
	return dis.readChar();
    }

    /**
     * Reads a 32 bit int.
     * @return the 32 bit integer read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int readInt()  throws IOException {
	return dis.readInt();
    }

    /**
     * Reads a 64 bit long.
     * @return the read 64 bit long.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public long readLong()  throws IOException {
	return dis.readLong();
    }

    /**
     * Reads a 32 bit float.
     * @return the 32 bit float read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public float readFloat() throws IOException {
	return dis.readFloat();
    }

    /**
     * Reads a 64 bit double.
     * @return the 64 bit double read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public double readDouble() throws IOException {
	return dis.readDouble();
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
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
     */
    public int skipBytes(int len) throws IOException {
	return dis.skipBytes(len);
    }

    /**
     * Reads in a line that has been terminated by a \n, \r, 
     * \r\n or EOF.
     * @return a String copy of the line.
     * @deprecated This method does not properly convert bytes to characters.
     * see DataInputStream for the details and alternatives.
     */
    public String readLine() throws IOException {
	return dis.readLine();
    }

    /**
     * Reads a UTF format String.
     * @return the String.
     */
     public String readUTF() throws IOException {
	return dis.readUTF();
    }
    
    /*
     * Invoke the readObject method if present
     */
    private boolean invokeObjectReader(Object obj)
	throws InvalidClassException, StreamCorruptedException,
	    ClassNotFoundException, IOException
    {
	if (currentClassDesc.readObjectMethod == null)
	    return false;

	try {
	    currentClassDesc.readObjectMethod.invoke(obj, readObjectArglist);
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
     * Read the fields of the specified class from the input stream and set
     * the values of the fields in the specified object. If the specified
     * object is null, just consume the fields without setting any values. If
     * any ObjectStreamField does not have a reflected Field, don't try to set
     * that field in the object.
     */
    private void inputClassFields(Object o, Class cl,
				  ObjectStreamField[] fields) 
	throws InvalidClassException, StreamCorruptedException,
	    ClassNotFoundException, IOException
    {
	int primFields = fields.length - currentClassDesc.objFields;

	/*
	 * Read and dispatch primitive data fields from the input
	 * stream.
	 */
  	if (currentClassDesc.primBytes > 0) {
	    if (data == null) {
		data = new byte[Math.max(currentClassDesc.primBytes,
					 INITIAL_BUFFER_SIZE)];
	    } else if (data.length < currentClassDesc.primBytes) {
		data = new byte[currentClassDesc.primBytes];
	    }
	    readFully(data, 0, currentClassDesc.primBytes);
	}

	if (o != null) {
	    for (int i = 0; i < primFields; ++i) {
		if (fields[i].getField() == null)
		    continue;

		try {
		    int lower;
		    int upper;
		    int loffset = fields[i].getOffset();
		    
		    switch (fields[i].getTypeCode()) {
		    case 'B':
			byte byteValue = data[loffset];
			fields[i].getField().setByte(o, byteValue);
			break;
		    case 'Z':
			boolean booleanValue =
			    (boolean)(data[loffset] != 0);
			fields[i].getField().setBoolean(o, booleanValue);
			break;
		    case 'C':
			char charValue =
			    (char)(((data[loffset] & 0xff) << 8) +
				   ((data[loffset+1] & 0xff)));
			fields[i].getField().setChar(o, charValue);
			break;
		    case 'S': 
			short shortValue =
			    (short)(((data[loffset] & 0xff) << 8) +
				    ((data[loffset+1] & 0xff)));
			fields[i].getField().setShort(o, shortValue);
			break;
		    case 'I':
			int intValue =
			    (((data[loffset]   & 0xff) << 24) +
			     ((data[loffset+1] & 0xff) << 16) +
			     ((data[loffset+2] & 0xff) << 8) +
			     ((data[loffset+3] & 0xff)));
			fields[i].getField().setInt(o, intValue);
			break;
		    case 'J':
			upper = (((data[loffset]   & 0xff) << 24) +
				 ((data[loffset+1] & 0xff) << 16) +
				 ((data[loffset+2] & 0xff) << 8) +
				 ((data[loffset+3] & 0xff)));
			lower = (((data[loffset+4] & 0xff) << 24) +
				 ((data[loffset+5] & 0xff) << 16) +
				 ((data[loffset+6] & 0xff) << 8) +
				 ((data[loffset+7] & 0xff)));
			long longValue =
			    ((long)upper << 32) + ((long) lower & 0xFFFFFFFFL);
			fields[i].getField().setLong(o, longValue);
			break;
		    case 'F' :
			int v = (((data[loffset]   & 0xff) << 24) +
				 ((data[loffset+1] & 0xff) << 16) +
				 ((data[loffset+2] & 0xff) << 8) +
				 ((data[loffset+3] & 0xff)));
			float floatValue = Float.intBitsToFloat(v);
			fields[i].getField().setFloat(o, floatValue);
			break;
		    case 'D' :
			upper = (((data[loffset] & 0xff) << 24) +
				 ((data[loffset+1] & 0xff) << 16) +
				 ((data[loffset+2] & 0xff) << 8) +
				 ((data[loffset+3] & 0xff)));
			lower = (((data[loffset+4] & 0xff) << 24) +
				 ((data[loffset+5] & 0xff) << 16) +
				 ((data[loffset+6] & 0xff) << 8) +
				 ((data[loffset+7] & 0xff)));
			long vv =
			    ((long) upper << 32) + ((long)lower & 0xFFFFFFFFL);
			double doubleValue = Double.longBitsToDouble(vv);
			fields[i].getField().setDouble(o, doubleValue);
			break;
		    default:
			// "Impossible"
			throw new InvalidClassException(cl.getName());
		    }
		} catch (IllegalAccessException e) {
		    throw new InvalidClassException(cl.getName(),
						    "IllegalAccessException");
		} catch (IllegalArgumentException e) {
		    /* This case should never happen. If the field types
		       are not the same, InvalidClassException is raised when
		       matching the local class to the serialized ObjectStreamClass. */
		    throw new ClassCastException("Assigning instance of class " +
						 fields[i].getType().getName() +
						 " to field " +
						 currentClassDesc.getName() + '#' +
						 fields[i].getField().getName());
		}
	    }
	}

	/* Read and set object fields from the input stream. */
	if (currentClassDesc.objFields > 0) {
	    for (int i = primFields; i < fields.length; i++) {
		boolean requireLocalClass = (fields[i].getField() != null);
		Object objectValue = readObject(requireLocalClass);
		if ((o == null) || (fields[i].getField() == null)) {
		    continue;
		}
		try {
		    fields[i].getField().set(o, objectValue);
		} catch (IllegalAccessException e) {
		    throw new InvalidClassException(cl.getName(),
						    "IllegalAccessException");
		} catch (IllegalArgumentException e) {
		    throw new ClassCastException("Assigning instance of class " +
						 objectValue.getClass().getName() +
						 " to field " +
						 currentClassDesc.getName() +
						 '#' +
						 fields[i].getField().getName());
		}
	    }
	}
    }

    /*************************************************************/

    /**
     * Provide access to the persistent fields read from the input stream.
     */
    abstract public static class GetField {
 
 	/**
 	 * Get the ObjectStreamClass that describes the fields in the stream.
 	 */
 	abstract public ObjectStreamClass getObjectStreamClass();
 
 	/**
 	 * Return true if the named field is defaulted and has no value
 	 * in this stream.
 	 */
 	abstract public boolean defaulted(String name)
 	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named boolean field from the persistent field.
	 */
	abstract public boolean get(String name, boolean defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named char field from the persistent fields.
	 */
	abstract public char get(String name, char defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named byte field from the persistent fields.
	 */
	abstract public byte get(String name, byte defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named short field from the persistent fields.
	 */
	abstract public short get(String name, short defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named int field from the persistent fields.
	 */
	abstract public int get(String name, int defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named long field from the persistent fields.
	 */
	abstract public long get(String name, long defvalue)
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named float field from the persistent fields.
	 */
	abstract public float get(String name, float defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named double field from the persistent field.
	 */
	abstract public double get(String name, double defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named Object field from the persistent field.
	 */
	abstract public Object get(String name, Object defvalue) 
	    throws IOException, IllegalArgumentException;
    }
 
    /* Internal Implementation of GetField. */
    static final class GetFieldImpl extends GetField {
 
	    /**
	     * Get the ObjectStreamClass that describes the fields in the stream.
	     */
	    public ObjectStreamClass getObjectStreamClass() {
		return desc;
	    }

	    /**
	     * Return true if the named field is defaulted and has no value
	     * in this stream.
	     */
	    public boolean defaulted(String name)
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, null);
		    return (field == null);   	
		}

	    /**
	     * Get the value of the named boolean field from the persistent field.
	     */
	    public boolean get(String name, boolean defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Boolean.TYPE);
		    if (field == null)
			return defvalue;

		    return (boolean)(data[field.getOffset()] != 0);
		}

	    /**
	     * Get the value of the named char field from the persistent fields.
	     */
	    public char get(String name, char defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Character.TYPE);
		    if (field == null)
			return defvalue;

		    int loffset = field.getOffset();
		    return (char)(((data[loffset] & 0xff) << 8) +
				  ((data[loffset+1] & 0xff)));
		}

	    /**
	     * Get the value of the named byte field from the persistent fields.
	     */
	    public byte get(String name, byte defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Byte.TYPE);
		    if (field == null)
			return defvalue;

		    return data[field.getOffset()];
		}

	    /**
	     * Get the value of the named short field from the persistent fields.
	     */
	    public short get(String name, short defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Short.TYPE);
		    if (field == null)
			return defvalue;

		    int loffset = field.getOffset();
		    return (short)(((data[loffset] & 0xff) << 8) +
				   ((data[loffset+1] & 0xff)));
		}

	    /**
	     * Get the value of the named int field from the persistent fields.
	     */
	    public int get(String name, int defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Integer.TYPE);
		    if (field == null)
			return defvalue;

		    int loffset = field.getOffset();	     
		    return (((data[loffset] & 0xff) << 24) +
			    ((data[loffset+1] & 0xff) << 16) +
			    ((data[loffset+2] & 0xff) << 8) +
			    ((data[loffset+3] & 0xff)));
		}

	    /**
	     * Get the value of the named long field from the persistent fields.
	     */
	    public long get(String name, long defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Long.TYPE);
		    if (field == null)
			return defvalue;

		    int loffset = field.getOffset();	     
		    int upper = (((data[loffset] & 0xff) << 24) +
				 ((data[loffset+1] & 0xff) << 16) +
				 ((data[loffset+2] & 0xff) << 8) +
				 ((data[loffset+3] & 0xff)));
		    int lower = (((data[loffset+4] & 0xff) << 24) +
				 ((data[loffset+5] & 0xff) << 16) +
				 ((data[loffset+6] & 0xff) << 8) +
				 ((data[loffset+7] & 0xff)));
		    long v = ((long)upper << 32) + ((long)lower & 0xFFFFFFFFL);
		    return v;
		}

	    /**
	     * Get the value of the named float field from the persistent fields.
	     */
	    public float get(String name, float defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Float.TYPE);
		    if (field == null)
			return defvalue;

		    int loffset = field.getOffset();	     
		    int v = (((data[loffset] & 0xff) << 24) +
			     ((data[loffset+1] & 0xff) << 16) +
			     ((data[loffset+2] & 0xff) << 8) +
			     ((data[loffset+3] & 0xff)));
		    return Float.intBitsToFloat(v);
		}

	    /**
	     * Get the value of the named double field from the persistent field.
	     */
	    public double get(String name, double defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Double.TYPE);
		    if (field == null)
			return defvalue;

		    int loffset = field.getOffset();	     
		    int upper = (((data[loffset] & 0xff) << 24) +
				 ((data[loffset+1] & 0xff) << 16) +
				 ((data[loffset+2] & 0xff) << 8) +
				 ((data[loffset+3] & 0xff)));
		    int lower = (((data[loffset+4] & 0xff) << 24) +
				 ((data[loffset+5] & 0xff) << 16) +
				 ((data[loffset+6] & 0xff) << 8) +
				 ((data[loffset+7] & 0xff)));
		    long v = ((long)upper << 32) + ((long)lower & 0xFFFFFFFFL);
		    return Double.longBitsToDouble(v);
		}

	    /**
	     * Get the value of the named Object field from the persistent field.
	     */
	    public Object get(String name, Object defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Object.class);
		    if (field == null)
			return defvalue;

		    return objects[field.getOffset()];

		}

	    /*
	     * Retrieve the named field.
	     * If the field is known in the current descriptor return it.
	     * If not, find the descriptor for the class that is being
	     * read into and check the name.  If the name is not
	     * valid in the class throw a IllegalArgumentException.
	     * otherwise return null.
	     */
	    private ObjectStreamField checkField(String name, Class type)
		throws IllegalArgumentException
		{
		    ObjectStreamField field = (type == null) ? 
			desc.getField(name) : desc.getField(name, type);
		    if (field != null) {
			/*
			 * Check the type of the field in the stream.
			 * If correct return the field.
			 */
			if (type != null && type != field.getType())
			    throw new IllegalArgumentException("field type incorrect");
			return field;
		    } else {
			/* Check the name in the local classes descriptor.
			 * If found it's a valid persistent field an
			 * should be defaulted.
			 * If not the caller shouldn't be using that name.
			 */
			ObjectStreamClass localdesc =
			    desc.localClassDescriptor();
			if (localdesc == null)
			    throw new IllegalArgumentException("No local class descriptor");

			ObjectStreamField localfield = (type == null)
			   ? localdesc.getField(name) : 
			    localdesc.getField(name, type);
			if (localfield == null)
			    throw new IllegalArgumentException("no such field");
			if (type != null && type != localfield.getType() &&
			    (type.isPrimitive() || localfield.getType().isPrimitive()))
			    throw new IllegalArgumentException("field type incorrect");
			return null;
		    }
		}

	    /**
	     * Read the data and fields from the specified stream.
	     */
	    void read(ObjectInputStream in)
		throws IOException, ClassNotFoundException
		{
		    if (data != null)
			in.readFully(data, 0, data.length);

		    if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
			    /* don't require local class when reading object from stream.*/
			    objects[i] = in.readObject(false);
			}
		    }
		}

	    /**
	     * Create a GetField object for the a Class.
	     * Allocate the arrays for primitives and objects.
	     */
	    GetFieldImpl(ObjectStreamClass descriptor){
		desc = descriptor;
		if (desc.primBytes > 0)
		    data = new byte[desc.primBytes];
		if (desc.objFields > 0)
		    objects = new Object[desc.objFields];
	    }

	    /*
	     * The byte array that contains the bytes for the primitive fields.
	     * The Object array that contains the objects for the object fields.
	     */
	    private byte[] data;
	    private Object[] objects;
	    private ObjectStreamClass desc;
	}

    /* Remember the first exception that stopped this stream. */
    private IOException abortIOException = null;
    private ClassNotFoundException abortClassNotFoundException = null;

    /* Allocate a new object for the specified class
     * Native since newInstance may not be able to find a zero arg constructor.
     */
    private static native Object allocateNewObject(Class aclass, Class initclass)
	throws InstantiationException, IllegalAccessException;
    
    /* Allocate a new array for the specified class
     * Native since the type of the array needs to be set to the class
     */
    private static native Object allocateNewArray(Class aclass, int length);
    
    /* The object is the current object and class is the the current
     * subclass of the object being read. Nesting information is kept
     * on the stack.
     */
    private Object currentObject;
    private ObjectStreamClass currentClassDesc;
    private Class currentClass;
    private Object currentGetFields;

    /*
     * Primitive data are read from the input stream and stored in
     * this array. The array is allocated prior to first use.
     */
    private static final int INITIAL_BUFFER_SIZE = 64;
    private byte[] data;

    /* Arrays used to keep track of classes and ObjectStreamClasses
     * as they are being merged; used in inputObject.
     * spClass is the stack pointer for both.  */
    ObjectStreamClass[] classdesc;
    Class[] classes;
    int spClass;

    /* During deserialization the objects in the stream are represented by
     * handles (ints), they need to be mapped to the objects.
     * The vector is indexed by the offset between baseWireHandle and the
     * wire handle in the stream.
     */
    private ArrayList wireHandle2Object;
    private int nextWireOffset;

    /* List of validation callback objects
     * The list is created as needed, and ValidationCallback objects added
     * for each call to registerObject. The list is maintained in
     * order of highest (first) priority to lowest
     */
    private ArrayList callbacks;

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

 
    /* if true, override readObject implementation with readObjectOverride.
     */
    private boolean enableSubclassImplementation;

    private Object[] readObjectArglist = {this};
};

// Internal class to hold the Callback object and priority
class ValidationCallback {
    ValidationCallback(ObjectInputValidation cb, int prio) {
	callback = cb;
	priority = prio;
    }

    int priority;			// priority of this callback
    ObjectInputValidation callback; // object to be called back
}
