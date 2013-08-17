/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Hashtable;
import java.lang.Math;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Field;

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
 * @version 1.108, 02/06/02
 * @see java.io.DataInput
 * @see java.io.ObjectOutputStream
 * @see java.io.Serializable
 * @see <a href="../../../guide/serialization/spec/input.doc.html"> Object Serialization Specification, Section 3, Object Input Classes</a>
 * @since   JDK1.1
 */
public class ObjectInputStream extends InputStream
	implements ObjectInput, ObjectStreamConstants
{ 
    /**
     * Create an ObjectInputStream that reads from the specified InputStream.
     * The stream header containing the magic number and version number
     * are read from the stream and verified. This method will block
     * until the corresponding ObjectOutputStream has written and flushed the 
     * header.
     *
     * @param in  the underlying <code>InputStream</code> from which to read
     * @exception StreamCorruptedException The version or magic number are 
     * incorrect.
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
	buf = new byte[8];
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
     * @see java.io.SerializablePermission
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
     *
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
     * @exception java.lang.ClassNotFoundException Class definition of a
     * serialized object cannot be found.
     * @exception OptionalDataException Primitive data was found in the 
     * stream instead of objects.
     * @exception IOException if I/O errors occurred while reading from the
     * underlying stream
     *
     * @see #ObjectInputStream()
     * @see #readObject()
     * @since 1.2
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
     * @since     1.2
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
	    case TC_LONGSTRING:
		{
		    long utflen = (rcode == TC_STRING) ? 
			readUnsignedShort() : readLong();
		    obj = readUTFBody(utflen);
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
		    throw v.pendingException;
		}
		assignWireOffset(obj);
		break;
		
	    case TC_CLASSDESC:
		obj = inputClassDescriptor();
		break;
		
	    case TC_PROXYCLASSDESC:
		obj = inputProxyClassDescriptor();
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
		    obj = ObjectStreamClass.invokeMethod(
		    	    currentClassDesc.readResolveMethod, obj, null);
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
     * @return the <code>GetField</code> object representing the persistent
     * fields of the object being deserialized
     * @exception java.lang.ClassNotFoundException if the class of a serialized
     *              object could not be found.
     * @exception IOException        if an I/O error occurs.
     * @exception NotActiveException if the stream is not currently reading
     *              objects.
     * @since 1.2
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
     *
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
     * @param v  an instance of class ObjectStreamClass
     * @return a Class object corresponding to <code>v</code>
     * @exception IOException Any of the usual Input/Output exceptions.
     * @exception ClassNotFoundException If class of
     * a serialized object cannot be found.
     */
    protected Class resolveClass(ObjectStreamClass v)
	throws IOException, ClassNotFoundException
    {
	/* Resolve by looking up the stack for a non-zero class
	 * loader. If not found use the system class loader.
	 */
	ClassLoader loader = latestUserDefinedLoader();
	return Class.forName(v.getName(), false, loader);
    }

    /**
     * Returns a proxy class that implements the interfaces named in a
     * proxy class descriptor; subclasses may implement this method to
     * read custom data from the stream along with the descriptors for
     * dynamic proxy classes, allowing them to use an alternate loading
     * mechanism for the interfaces and the proxy class.
     *
     * <p>This method is called exactly once for each unique proxy class
     * descriptor in the stream.
     *
     * <p>The corresponding method in <code>ObjectOutputStream</code> is
     * <code>annotateProxyClass</code>.  For a given subclass of
     * <code>ObjectInputStream</code> that overrides this method, the
     * <code>annotateProxyClass</code> method in the corresponding
     * subclass of <code>ObjectOutputStream</code> must write any data or
     * objects read by this method.
     *
     * <p>The default implementation of this method in
     * <code>ObjectInputStream</code> returns the result of calling
     * <code>Proxy.getProxyClass</code> with the list of
     * <code>Class</code> objects for the interfaces that are named in
     * the <code>interfaces</code> parameter.  The <code>Class</code>
     * object for each interface name <code>i</code> is the value
     * returned by calling
     * <pre>
     *     Class.forName(i, false, loader)
     * </pre>
     * where <code>loader</code> is that of the first non-null class
     * loader up the execution stack, or <code>null</code> if no non-null
     * class loaders are on the stack (the same class loader choice used
     * by the <code>resolveClass</code> method).  This same value of
     * <code>loader</code> is also the class loader passed to
     * <code>Proxy.getProxyClass</code>.  If <code>Proxy.getProxyClass</code>
     * throws an <code>IllegalArgumentException</code>,
     * <code>resolveProxyClass</code> will throw a
     * <code>ClassNotFoundException</code> containing the
     * <code>IllegalArgumentException</code>.
     *
     * @param	interfaces the list of interface names that were
     *		deserialized in the proxy class descriptor
     * @return  a proxy class for the specified interfaces
     * @throws	IOException any exception thrown by the underlying
     *		<code>InputStream</code>
     * @throws	ClassNotFoundException if the proxy class or any of the
     * 		named interfaces could not be found
     * @see ObjectOutputStream#annotateProxyClass(Class)
     * @since	1.3
     */
    protected Class resolveProxyClass(String[] interfaces)
	throws IOException, ClassNotFoundException
    {
	ClassLoader loader = latestUserDefinedLoader();

	Class[] classObjs = new Class[interfaces.length];
	for (int i = 0; i < interfaces.length; i++) {
	    classObjs[i] = Class.forName(interfaces[i], false, loader);
	}
	try {
	    return Proxy.getProxyClass(loader, classObjs);
	} catch (IllegalArgumentException e) {
	    throw new ClassNotFoundException(null, e);
	}
    }

    /*
     * Returns the first non-null class loader up the execution stack, or null
     * if only code from the null class loader is on the stack.
     */
    private static native ClassLoader latestUserDefinedLoader()
	throws ClassNotFoundException;

    /*
     * Simulates the behavior of the old native "loadClass0" method,
     * preserved here only for invocation by the following native method
     * in the RMI-IIOP implementation:
     *
     *     com.sun.corba.se.internal.util.JDKClassLoader.specialLoadClass
     *
     * This method should be removed when the above use is cleaned up.
     */
    private Class loadClass0(Class cl, String classname)
	throws ClassNotFoundException
    {
	ClassLoader loader;
	if (cl != null) {
	    loader = cl.getClassLoader();
	} else {
	    loader = latestUserDefinedLoader();
	}
	return Class.forName(classname, false, loader);
    }

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
     * just returns the same object. <p>
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
     * @param obj object to be substituted
     * @return the substituted object
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
     * @param enable true for enabling use of <code>resolveObject</code> for
     *               every object being deserialized
     * @return the previous setting before this method was invoked
     * @throws SecurityException
     *    if a security manager exists and its 
     *    <code>checkPermission</code> method denies
     *    enabling the stream to allow objects read from the stream to be
     *    replaced.
     *
     * @see SecurityManager#checkPermission
     * @see java.io.SerializablePermission
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
     *
     * @throws IOException if there are I/O errors while reading from the
     * underlying <code>InputStream</code> 
     * @throws StreamCorruptedException if control information in the
     * stream is inconsistent
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

    /**
     * Read a class descriptor from the serialization stream.  This method is
     * called when the ObjectInputStream expects a class descriptor as the next
     * item in the serialization stream.  Subclasses of ObjectInputStream may
     * override this method to read in class descriptors that have been written
     * in non-standard formats (by subclasses of ObjectOutputStream which have
     * overridden the <code>writeClassDescriptor</code> method).  By default,
     * this method reads class descriptors according to the format defined in
     * the Object Serialization specification.
     * <p>
     *
     * @return the class descriptor read
     * @exception IOException If an I/O error has occurred.
     * @exception ClassNotFoundException If the Class of a serialized object
     *            used in the class descriptor representation cannot be found
     * @see
     * java.io.ObjectOutputStream#writeClassDescriptor(java.io.ObjectStreamClass)
     * @since 1.3
     */
    protected ObjectStreamClass readClassDescriptor() 
	throws IOException, ClassNotFoundException
    {
	String classname = readUTF();
	long hash = readLong();
	ObjectStreamClass v = new ObjectStreamClass(classname, hash);
	v.read(this);
	return v;
    }

    /*
     * Read a ObjectStreamClass from the stream, it may recursively
     * create other ObjectStreamClasses for the classes it references.
     */
    private ObjectStreamClass inputClassDescriptor()
	throws IOException, InvalidClassException, ClassNotFoundException
    {
	Class aclass;

	/* For backwards compatibility, the wire handle for the incoming class
	 * descriptor must be assigned _before_ the class descriptor data is
	 * read in.  To get around this, we reserve the next wire handle by
	 * assigning it to an empty class descriptor, then the class descriptor
	 * data is read in, and finally we copy the class descriptor data back
	 * into the empty class descriptor.
	 */
	ObjectStreamClass copydesc = new ObjectStreamClass(null, 0);
	int wireoffset = assignWireOffset(copydesc);

	/* Read in class descriptor */
	ObjectStreamClass desc = readClassDescriptor();

	/* copy into empty class descriptor */
	copydesc.lightCopy(desc);

	/* Switch to BlockDataMode and call resolveClass.
	 * It may raise ClassNotFoundException.
	 * Consume any extra data or objects left by resolve class and
	 * read the endOfBlockData. Then switch out of BlockDataMode.
	 */
	boolean prevMode = setBlockData(true);
	try {
	    aclass = resolveClass(copydesc);
	} catch (ClassNotFoundException e) {
	    /* if the most derived class, this exception will be thrown at a
	     * later time. */
	    aclass = null;
	    
	    /* Fix for 4191941: stash original exception, to throw later. */
	    copydesc.pendingException = e;
	}
	skipToEndOfBlockData();
	prevMode = setBlockData(prevMode);


	/* Verify that the class returned is "compatible" with
	 * the class description.  i.e. the name and hash match.
	 * Set the class this ObjectStreamClass will use to create 
	 * instances.
	 */
	copydesc.setClass(aclass);

	/* Get the superdescriptor of this one and it set it.
	 */
	ObjectStreamClass superdesc = (ObjectStreamClass)readObject();
	copydesc.setSuperclass(superdesc);

	return copydesc;
    }

    /**
     * Read a proxy class descriptor from the stream, returning the
     * equivalent incoming ObjectStreamClass object for the resolved
     * proxy class.
     */
    private ObjectStreamClass inputProxyClassDescriptor()
	throws IOException, InvalidClassException, ClassNotFoundException
    {
	/*
	 * The wire handle must be assigned for the ObjectStreamClass
	 * object we will return before reading any other objects from
	 * the stream that could reference it, so we have to construct
	 * the ObjectStreamClass first, with no name.
	 *
	 * All proxy classes have a serialVersionUID of 0L.
	 */
	ObjectStreamClass v = new ObjectStreamClass("", 0L);
	int wireoffset = assignWireOffset(v);

	/*
	 * Read in the names of the proxy interfaces.
	 */
	int numInterfaces = readInt();
	String[] interfaces = new String[numInterfaces];
	for (int i = 0; i < numInterfaces; i++) {
	    interfaces[i] = readUTF();
	}

	/*
	 * Within block data mode to allow reading custom data, invoke
	 * resolveProxyClass to get the appropriate Class object for the
	 * proxy class with the described interfaces.
	 */
	Class cl;
	boolean prevMode = setBlockData(true);
	try {
	    cl = resolveProxyClass(interfaces);
	} catch (ClassNotFoundException e) {
	    cl = null;
	    v.pendingException = e;
	}
	skipToEndOfBlockData();
	prevMode = setBlockData(prevMode);

	/*
	 * Fill in the fields of ObjectStreamClass that are read from the
	 * stream for non-proxy classes but are implicit for proxy classes.
	 */
	v.initProxyClassDesc(cl);

	/*
	 * Set the local Class object for the descriptor and read and set
	 * the descriptor for the superclass.
	 */
	v.setClass(cl);
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
	    throw v.pendingException;

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
	    int offset = data.length;
	    int buflen = data.length;

	    if (type == Boolean.TYPE) {
		boolean[] array = (boolean[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset >= buflen) {
			int readlen = Math.min(length-i, buflen);
			readFully(data, 0, readlen);
			offset = 0;
		    }
		    array[i] = (data[offset] != 0);
		    offset += 1;
		}
	    } else if (type == Byte.TYPE) {
		byte[] array = (byte[])currentObject;
		int ai = 0;
		while (ai < length) {
		    int readlen = Math.min(length-ai, buflen);
		    readFully(data, 0, readlen);
		    System.arraycopy(data, 0, array, ai, readlen);
		    ai += readlen;
		}
	    } else if (type == Short.TYPE) {
		short[] array = (short[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 2) {
			int readlen = Math.min((length-i)*2, buflen);
			readFully(data, 0, readlen);
			offset = 0;
		    }
		    array[i] = (short)(((data[offset] & 0xff) << 8) +
				       ((data[offset+1] & 0xff) << 0));
		    offset += 2;
		}
	    } else if (type == Integer.TYPE) {
		int[] array = (int[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 4) {
			int readlen = Math.min((length-i) << 2, buflen);
			readFully(data, 0, readlen);
			offset = 0;
		    }
		    array[i] = (((data[offset] & 0xff) << 24) +
				((data[offset+1] & 0xff) << 16) +
				((data[offset+2] & 0xff) << 8) +
				((data[offset+3] & 0xff) << 0));
		    offset += 4;
		}
	    } else if (type == Long.TYPE) {
		long[] array = (long[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 8) {
			int readlen = Math.min((length - i) << 3, buflen);
			readFully(data, 0, readlen);
			offset = 0;
		    }
		    int upper = (((data[offset] & 0xff) << 24) +
				 ((data[offset+1] & 0xff) << 16) +
				 ((data[offset+2] & 0xff) << 8) +
				 ((data[offset+3] & 0xff) << 0));
		    int lower = (((data[offset+4] & 0xff) << 24) +
				 ((data[offset+5] & 0xff) << 16) +
				 ((data[offset+6] & 0xff) << 8) +
				 ((data[offset+7] & 0xff) << 0));
		    array[i] = ((long)upper << 32) + ((long)lower & 0xFFFFFFFFL);
		    offset += 8;
		}
	    } else if (type == Float.TYPE) {
		float[] array = (float[])currentObject;
		for (i = 0; i < length; ) {
		    int n = Math.min(length - i, buflen >> 2);
		    readFully(data, 0, n << 2);
		    bytesToFloats(data, 0, array, i, n);
		    i += n;
		}
	    } else if (type == Double.TYPE) {
		double[] array = (double[])currentObject;
		for (i = 0; i < length; ) {
		    int n = Math.min(length - i, buflen >> 3);
		    readFully(data, 0, n << 3);
		    bytesToDoubles(data, 0, array, i, n);
		    i += n;
		}
	    } else if (type == Character.TYPE) {
		char[] array = (char[])currentObject;
		for (i = 0; i < length; i++) {
		    if (offset > buflen - 2) {
			int readlen = Math.min((length-i)*2, buflen);
			readFully(data, 0, readlen);
			offset = 0;
		    }
		    array[i] = (char)(((data[offset] & 0xff) << 8) +
				      ((data[offset+1] & 0xff) << 0));
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
     * Reconstitutes nfloats float values from their byte representations.  Byte
     * values are read from array src starting at offset srcpos; the resulting
     * float values are written to array dst starting at dstpos.
     */
    private static native void bytesToFloats(byte[] src, int srcpos,
	    float[] dst, int dstpos, int nfloats);
    
    /*
     * Reconstitutes ndoubles double values from their byte representations.
     * Byte values are read from array src starting at offset srcpos; the
     * resulting double values are written to array dst starting at dstpos.
     */
    private static native void bytesToDoubles(byte[] src, int srcpos,
	    double[] dst, int dstpos, int ndoubles);
    

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
	    throw currentClassDesc.pendingException;
	
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
			skipToEndOfBlockData();
			setBlockData(prevmode);
		    }
		}
	    } catch (NoSuchMethodError e) {
		throw new InvalidClassException(currentClass.getName() + 
			"Missing no-arg constructor for class"); 
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
	    ObjectStreamClass currdesc;
	    Class currclass;
	    int numAncestors;
	    int spBase;

	    if (currentClassDesc.ancestors == null) {
		currdesc = currentClassDesc;
		currclass = currentClass;

		spBase = spClass;	// current top of stack

		/* The object's classes should be processed from supertype to
		 * subtype.  Push all the classes of the current object onto a
		 * stack.  Note that only the serializable classes are
		 * represented in the descriptor list. 
		 *
		 * Handle versioning where one or more supertypes of have been
		 * inserted or removed.  The stack will contain pairs of
		 * descriptors and the corresponding class.  If the object has a
		 * class that did not occur in the original the descriptor will
		 * be null.  If the original object had a descriptor for a class
		 * not present in the local hierarchy of the object the class
		 * will be null.
		 *
		 */

		/*
		 * This is your basic diff pattern, made simpler
		 * because reordering is not allowed.
		 */
		for (currdesc = currentClassDesc, currclass = currentClass;
			currdesc != null;
			currdesc = currdesc.superclass) {

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
		    /* Test if there is room for this new entry.  If not, double
		     * the size of the arrays and copy the contents.
		     */
		    if (spClass >= classes.length) {
			growClassStacks();
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

			/* Ignoring all the classes between currclass and cl.
			 */
			currclass = cl.getSuperclass();
		    }
		    spClass++;
		}
		
		/* 
		 * Find the highest non-serializable object. Note that the
		 * class Object is non-serializable.
		 */
		if (currclass != null) {
		    while (Serializable.class.isAssignableFrom(currclass)) 
			currclass = currclass.getSuperclass();
		}		    

		numAncestors = spClass - spBase;
		currentClassDesc.ancestors = new Class[numAncestors + 1];
		for (int i = 0; i < numAncestors; i++) 
		    currentClassDesc.ancestors[i] = classes[spBase + i];
		currentClassDesc.ancestors[numAncestors] = currclass;
		
		spClass = spBase;
	    }
	    
	    numAncestors = currentClassDesc.ancestors.length - 1;
	    currclass = currentClassDesc.ancestors[numAncestors];



	    /* Allocate a new object.  The object is only constructed
	     * above the highest serializable class and is set to
	     * default values for all more specialized classes.
	     * Remember the next wirehandle goes with the new object
	     */
	    try {
		currentObject = (currentClass == null) ?
			     null : allocateNewObject(currentClass, currclass);
	    } catch (NoSuchMethodError e) {
		throw new InvalidClassException(currclass.getName() +
			"Missing no-arg constructor for class"); 
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
	    
	    /* Push ancestor class descriptors and their associated local
	     * classes onto the stack.
	     */
	    spBase = spClass;
	    for (currdesc = currentClassDesc; currdesc != null;
		    currdesc = currdesc.superclass)
	    {
		if (spClass >= classes.length) {
		    growClassStacks();
		}
		classdesc[spClass] = currdesc;
		classes[spClass] = 
		    currentClassDesc.ancestors[spClass - spBase];
		spClass++;
	    }

	    try {
		for (spClass--; spClass >= spBase; spClass--) {
		    /*
		     * Set current descriptor and corresponding class
		     */
		    currentClassDesc = classdesc[spClass];
		    currentClass = classes[spClass];

		    setBlockData(true);  /* any reads are from datablocks */

		    if (classes[spClass] != null) {
			/* Read the data from the stream described by the
			 * descriptor and store into the matching class.
			 */
			ObjectStreamClass localDesc = 
			    currentClassDesc.localClassDescriptor();
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
			skipToEndOfBlockData();
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
     * Grow the size of the class descriptor and class stacks.
     */
    private void growClassStacks() {
	int newlen = classes.length * 2;

	Class[] newclasses = new Class[newlen];
	ObjectStreamClass[] newclassdesc = new ObjectStreamClass[newlen];

	System.arraycopy(classes, 0, newclasses, 0, classes.length);
	System.arraycopy(classdesc, 0, newclassdesc, 0, classes.length);

	classes = newclasses;
	classdesc = newclassdesc;
    }

    /*
     * Skip any unread block data and objects up to the next
     * TC_ENDBLOCKDATA.  Anybody can do this because readObject
     * handles the details of reporting if there is data left.
     * Try reading objects.  If it throws optional data
     * skip over it and try again. 
     */
    private void skipToEndOfBlockData()
	throws IOException, ClassNotFoundException
    {
	if (! blockDataMode)
	    return;

	for (;;) {
	    while (count > 0)
		skip(count);
	    switch (peekCode()) {
		case -1:		// EOF
		    return;
		    
		case TC_BLOCKDATA:
		case TC_BLOCKDATALONG:
		    refill();		// read in next block header
		    break;

		case TC_ENDBLOCKDATA:
		    readCode();		// consume TC_ENDBLOCKDATA
		    return;
		    
		default:
		    readObject(false);	// don't require local class
		    break;
	    }
	}
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
    
    private byte[] buf;		// byte array of buffered data
    private int bufpos;		// current position in buf
    private int bufsize;	// number of valid bytes in buf

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

	count =  mode ? 0 : -1;		// set count to allow reading or not
	
	bufpos = 0;			// clear read buffer
	bufsize = 0;

	blockDataMode = mode;
	return !mode;
    }
    
    /**
     * Reads a byte of data. This method will block if no input is 
     * available.
     *
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read() throws IOException {
	if (blockDataMode) {
	    while (count == 0)		// read block data header(s)
		refill();
	    if (count < 0)
		return -1;

	    try {			// attempt to read next byte
		int pos = bufferData(1);
		return buf[pos] & 0xFF;
	    } catch (EOFException e) {
		return -1;
	    }
	} else {			// not block data, read directly
	    return in.read();
	}
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
    
    /*
     * Buffer the given number of bytes in buf[], mark the bytes as read, and
     * return the index in buf[] of the first byte of the sequence.  When in
     * block data mode, this method may eagerly buffer more than the specified
     * number of bytes (however, the caller cannot assume that any index into
     * buf[] past the return value + (len - 1) is valid).
     */
    int bufferData(int len) throws IOException {
	ensureBufferCapacity(len);

	if (blockDataMode) {
	    if (len > count)			// sanity check
		throw new InternalError("attempt to read past block end");
	    
	    int bufavail = bufsize - bufpos;
	    if (len > bufavail) {		// must refill buffer
		// shift unread contents to beginning of buffer
		if (bufpos > 0) {
		    System.arraycopy(buf, bufpos, buf, 0, bufavail);
		    bufpos = 0;
		    bufsize = bufavail;
		}
		
		// buffer remainder of data block
		ensureBufferCapacity(count);
		readFullyInternal(buf, bufsize, count - bufsize);
		bufsize = count;
	    }
	    
	    // mark bytes as read, return starting index into buffer
	    int pos = bufpos;
	    bufpos += len;
	    count -= len;
	    return pos;
	} else {		// not block data mode, read directly
	    readFullyInternal(buf, 0, len);
	    return 0;
	}
    }
    
    /*
     * Ensures that read buffer is large enough to hold the given number of
     * bytes.
     */
    private void ensureBufferCapacity(int size) {
	if (buf.length < size) {
	    byte[] newbuf = new byte[size];
	    if (bufsize > 0)
		System.arraycopy(buf, 0, newbuf, 0, bufsize);
	    buf = newbuf;
	}
    }
    
    /*
     * The same as readFully, except that it reads from the underlying input
     * stream, instead of the ObjectInputStream.  Since this method is unaware
     * of block data boundaries, it should only be called when not in block data
     * mode, or when the block data header is known not to occur within the
     * requested span of bytes.
     */
    private void readFullyInternal(byte[] b, int off, int len)
	throws IOException
    {
	int n = 0;
	while (n < len) {
	    int c = in.read(b, off + n, len - n);
	    if (c < 0)
		throw new EOFException();
	    n += c;
	}
    }
    
    /**
     * Reads into an array of bytes.  This method will
     * block until some input is available. Consider
     * using java.io.DataInputStream.readFully to read exactly
     * 'length' bytes.
     *
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, -1 is
     * 		returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     * @see java.io.DataInputStream#readFully(byte[],int,int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
	if (b == null) {
	    throw new NullPointerException();
	} else if ((off < 0) || (len < 0) || 
		  ((off + len) > b.length) || ((off + len) < 0)) 
	{
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return 0;
	}
	
	if (blockDataMode) {
	    while (count == 0)		// read block data header(s)
		refill();
	    if (count < 0)
		return -1;

	    len = Math.min(len, count);	// don't read beyond end of block
	    
	    int nread = 0;
	    int bufavail = bufsize - bufpos;

	    if (bufavail > 0) {		// copy already buffered data
		nread = Math.min(len, bufavail);
		System.arraycopy(buf, bufpos, b, off, nread);
		bufpos += nread;
	    } 
	    
	    if (len > nread)		// read remaining data from stream
		nread += in.read(b, off + nread, len - nread);
	    
	    count -= nread;		
	    return nread;
	} else {			// not block data, read from stream
	    return in.read(b, off, len);
	}
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     *
     * @return the number of available bytes.
     * @throws IOException if there are I/O errors while reading from the
     * underlying <code>InputStream</code>
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
     *
     * @exception IOException If an I/O error has occurred.
     */
    public void close() throws IOException {
	in.close();
    }

    /* -----------------------------------------------------*/
    /*
     * Provide the methods to implement DataInput.  Depending on the current
     * buffer and block size, they either read the values directly from an
     * internal buffer, or delegate to an instance of DataInputStream.
     */
    private DataInputStream dis;
    
    /**
     * Reads in a boolean.
     * @return the boolean read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public boolean readBoolean() throws IOException {
	int c = read();
	if (c < 0)
	    throw new EOFException();
	return (c != 0);
    }

    /**
     * Reads an 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public byte readByte() throws IOException  {
	int c = read();
	if (c < 0)
	    throw new EOFException();
	return (byte) c;
    }

    /**
     * Reads an unsigned 8 bit byte.
     *
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int readUnsignedByte()  throws IOException {
	int c = read();
	if (c < 0)
	    throw new EOFException();
	return c;
    }

    /**
     * Reads a 16 bit short.
     *
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public short readShort()  throws IOException {
	if (blockDataMode && count < 2)
	    return dis.readShort();
	int pos = bufferData(2);
	return (short) (((buf[pos + 0] & 0xFF) << 8) +
			((buf[pos + 1] & 0xFF) << 0));
    }

    /**
     * Reads an unsigned 16 bit short.
     *
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int readUnsignedShort() throws IOException {
	if (blockDataMode && count < 2)
	    return dis.readUnsignedShort();
	int pos = bufferData(2);
	return ((buf[pos + 0] & 0xFF) << 8) +
	       ((buf[pos + 1] & 0xFF) << 0);
    }

    /**
     * Reads a 16 bit char.
     *
     * @return the 16 bit char read. 
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public char readChar()  throws IOException {
	if (blockDataMode && count < 2)
	    return dis.readChar();
	int pos = bufferData(2);
	return (char) (((buf[pos + 0] & 0xFF) << 8) +
		       ((buf[pos + 1] & 0xFF) << 0));
    }

    /**
     * Reads a 32 bit int.
     *
     * @return the 32 bit integer read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int readInt()  throws IOException {
	if (blockDataMode && count < 4)
	    return dis.readInt();
	int pos = bufferData(4);
	return ((buf[pos + 0] & 0xFF) << 24) +
	       ((buf[pos + 1] & 0xFF) << 16) +
	       ((buf[pos + 2] & 0xFF) << 8) +
	       ((buf[pos + 3] & 0xFF) << 0);
    }

    /**
     * Reads a 64 bit long.
     *
     * @return the read 64 bit long.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public long readLong()  throws IOException {
	if (blockDataMode && count < 8)
	    return dis.readLong();
	int pos = bufferData(8);
	return ((buf[pos + 0] & 0xFFL) << 56) +
	       ((buf[pos + 1] & 0xFFL) << 48) +
	       ((buf[pos + 2] & 0xFFL) << 40) +
	       ((buf[pos + 3] & 0xFFL) << 32) +
	       ((buf[pos + 4] & 0xFFL) << 24) +
	       ((buf[pos + 5] & 0xFFL) << 16) +
	       ((buf[pos + 6] & 0xFFL) << 8) +
	       ((buf[pos + 7] & 0xFFL) << 0);
    }

    /**
     * Reads a 32 bit float.
     *
     * @return the 32 bit float read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public float readFloat() throws IOException {
	if (blockDataMode && count < 4)
	    return dis.readFloat();
	int pos = bufferData(4);
	return Float.intBitsToFloat(((buf[pos + 0] & 0xFF) << 24) +
				    ((buf[pos + 1] & 0xFF) << 16) +
				    ((buf[pos + 2] & 0xFF) << 8) +
				    ((buf[pos + 3] & 0xFF) << 0));
    }

    /**
     * Reads a 64 bit double.
     *
     * @return the 64 bit double read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public double readDouble() throws IOException {
	if (blockDataMode && count < 8)
	    return dis.readDouble();
	int pos = bufferData(8);
	return Double.longBitsToDouble(((buf[pos + 0] & 0xFFL) << 56) +
				       ((buf[pos + 1] & 0xFFL) << 48) +
				       ((buf[pos + 2] & 0xFFL) << 40) +
				       ((buf[pos + 3] & 0xFFL) << 32) +
				       ((buf[pos + 4] & 0xFFL) << 24) +
				       ((buf[pos + 5] & 0xFFL) << 16) +
				       ((buf[pos + 6] & 0xFFL) << 8) +
				       ((buf[pos + 7] & 0xFFL) << 0));
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     *
     * @param data the buffer into which the data is read
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public void readFully(byte[] data) throws IOException {
	dis.readFully(data);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     *
     * @param data the buffer into which the data is read
     * @param offset the start offset of the data
     * @param size the maximum number of bytes to read
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
     *
     * @param len the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    public int skipBytes(int len) throws IOException {
	return dis.skipBytes(len);
    }

    /**
     * Reads in a line that has been terminated by a \n, \r, \r\n or EOF.
     *
     * @return a String copy of the line.
     * @throws IOException if there are I/O errors while reading from the
     * underlying <code>InputStream</code>
     * @deprecated This method does not properly convert bytes to characters.
     * see DataInputStream for the details and alternatives.
     */
    public String readLine() throws IOException {
	return dis.readLine();
    }

    /**
     * Reads a UTF format String.
     *
     * @return the String.
     * @throws IOException if there are I/O errors while reading from the
     * underlying <code>InputStream</code>
     */
     public String readUTF() throws IOException {
	 return readUTFBody(readUnsignedShort());
     }
    
    /**
     * Reads the body of a UTF string with the given length.
     */
    private String readUTFBody(long utflen) throws IOException {
	final int PADLEN = 2;
	long remaining = utflen;        // bytes left to read
	int didx = 0;                   // current index into data buffer
	int dlen = 0;                   // number of valid bytes in data buffer
	int cidx = 0;                   // current index into cdata
	int clen = cdata.length;	// length of char buffer

	if ((utflen > clen) && (clen < CDATA_MAX_LEN)) {
	    cdata = new char[(int) Math.min(utflen, CDATA_MAX_LEN)];
	    clen = cdata.length;
	}
	
	// sbuf.length() == 0, since cleared after each read

	while (remaining > 0) {
	    int c, c2, c3;

	    // move any leftover bytes from previous loop to start of buffer
	    int gap = dlen - didx;
	    if (gap > 0)
		System.arraycopy(data, didx, data, 0, gap);

	    // read in as many bytes as will fit
	    int nread = (int) Math.min(remaining, data.length - gap);
	    dis.readFully(data, gap, nread);
	    dlen = gap + nread;
	    remaining -= nread;
	    didx = 0;

	    try {
		/* don't initiate a conversion with less than 3 bytes at end of
		 * buffer, unless there are no more refills left.
		 */
		int dlimit = (remaining > 0) ? (dlen - PADLEN) : dlen;
		while (didx < dlimit) {
		    if (cidx >= clen) {		// flush cdata
			sbuf.append(cdata);
			cidx = 0;
		    }

		    c = (int) data[didx++] & 0xFF;
		    switch (c >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:  
		     	    // 1 byte format: 0xxxxxxx
			    cdata[cidx++] = (char) c;
			    break;
			case 12:
			case 13:        
			    // 2 byte format: 110xxxxx 10xxxxxx
			    c2 = (int) data[didx++];
			    if ((c2 & 0xC0) != 0x80)
				throw new UTFDataFormatException();
			    cdata[cidx++] = 
				(char) (((c & 0x1F) << 6) | (c2 & 0x3F));
			    break;
			case 14:         
			    // 3 byte format: 1110xxxx 10xxxxxx 10xxxxxx
			    c2 = (int) data[didx++];
			    c3 = (int) data[didx++];
			    if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80))
				throw new UTFDataFormatException();
			    cdata[cidx++] = (char) (((c & 0x0F) << 12) | 
				    ((c2 & 0x3F) << 6) | ((c3 & 0x3F) << 0));
			    break;
			default:
			    // 10xx xxxx, 1111 xxxx
			    throw new UTFDataFormatException();
		    }
		}
	    } catch (IndexOutOfBoundsException e) {
		throw new UTFDataFormatException();
	    }
	}

	if (cidx > 0)
	    sbuf.append(cdata, 0, cidx);
	
	String s = sbuf.toString();
	sbuf.setLength(0);
	return s;
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
	/*
	 * Read and dispatch primitive data fields from the input
	 * stream.
	 */
  	if (currentClassDesc.numPrimBytes > 0) {
	    if (data.length < currentClassDesc.numPrimBytes)
		data = new byte[currentClassDesc.numPrimBytes];
	    readFully(data, 0, currentClassDesc.numPrimBytes);
	    if (o != null)
		setPrimitiveFieldValues(o, currentClassDesc.primFieldIDs,
			currentClassDesc.primFieldTypecodes, data);
	}

	/* Read and set object fields from the input stream. */
	int numPrimFields = fields.length - currentClassDesc.numObjFields;
	for (int i = 0; i < currentClassDesc.numObjFields; i++) {
	    Field field = fields[numPrimFields + i].getField();
	    boolean requireLocalClass = (field != null);
	    Object val = readObject(requireLocalClass);
	    if ((o == null) || (field == null))
		continue;
	    try {
		setObjectFieldValue(o, currentClassDesc.objFieldIDs[i], 
			currentClassDesc.objFieldTypes[i], val);
	    } catch (ClassCastException e) {
		// fill in error message
		throw new ClassCastException("Assigning instance of class " +
			val.getClass().getName() + " to field " + 
			cl.getName() + '#' + field.getName());
	    } catch (Exception e) {
		throw new InvalidClassException(cl.getName(), 
			"Invalid field " + field.getName());
	    }
	}
    }

    /*
     * Sets the values of the primitive fields of object obj.  fieldIDs is an
     * array of field IDs (the primFieldsID field of the appropriate
     * ObjectStreamClass) identifying which fields to set.  typecodes is an
     * array of characters designating the primitive type of each field (e.g.,
     * 'C' for char, 'Z' for boolean, etc.)  data is the byte buffer from which
     * the primitive field values are read, in the order of their field IDs.
     * 
     * For efficiency, this method does not check all of its arguments for
     * safety.  Specifically, it assumes that obj's type is compatible with the
     * given field IDs, and that the data array is long enough to contain all of
     * the byte values that will be read out of it.
     */
    private static native void setPrimitiveFieldValues(Object obj, 
	    long[] fieldIDs, char[] typecodes, byte[] data);
    
    /*
     * Sets the value of an object field of object obj.  fieldID is the field ID
     * identifying which field to set (obtained from the objFieldsID array field
     * of the appropriate ObjectStreamClass).  type is the field type; it is
     * provided so that the native method can ensure that the passed value val
     * is assignable to the field.
     * 
     * For efficiency, this method does not check all of its arguments for
     * safety.  Specifically, it assumes that obj's type is compatible with the
     * given field IDs, and that type is indeed the class type of the field
     * designated by fieldID.
     */
    private static native void setObjectFieldValue(Object obj, long fieldID, 
	    Class type, Object val);


    /*************************************************************/

    /**
     * Provide access to the persistent fields read from the input stream.
     */
    abstract public static class GetField {
 
 	/**
 	 * Get the ObjectStreamClass that describes the fields in the stream.
	 *
	 * @return the descriptor class that describes the serializable fields
 	 */
 	abstract public ObjectStreamClass getObjectStreamClass();
 
 	/**
 	 * Return true if the named field is defaulted and has no value
 	 * in this stream.
	 *
	 * @param name the name of the field
	 * @return true, if and only if the named field is defaulted
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if <code>name</code> does not
	 * correspond to a serializable field
	 */
 	abstract public boolean defaulted(String name)
 	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named boolean field from the persistent field.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>boolean</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public boolean get(String name, boolean defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named char field from the persistent fields.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>char</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public char get(String name, char defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named byte field from the persistent fields.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>byte</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public byte get(String name, byte defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named short field from the persistent fields.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>short</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public short get(String name, short defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named int field from the persistent fields.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>int</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public int get(String name, int defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named long field from the persistent fields.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>long</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public long get(String name, long defvalue)
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named float field from the persistent fields.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>float</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public float get(String name, float defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named double field from the persistent field.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>double</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public double get(String name, double defvalue) 
	    throws IOException, IllegalArgumentException;
 
	/**
	 * Get the value of the named Object field from the persistent field.
	 *
	 * @param name the name of the field
	 * @param defvalue the default value to use if <code>name</code>
	 * does not have a value
	 * @return the value of the named <code>Object</code> field
	 * @throws IOException if there are I/O errors while reading from
	 * the underlying <code>InputStream</code>
	 * @throws IllegalArgumentException if type of <code>name</code> is
	 * not serializable or if the field type is incorrect
	 */
	abstract public Object get(String name, Object defvalue) 
	    throws IOException, IllegalArgumentException;
    }
 
    /* Internal Implementation of GetField. */
    static final class GetFieldImpl extends GetField {
 
	    /** 
	     * Get the ObjectStreamClass that describes the fields in the
	     * stream.
	     *
	     * @return the descriptor class that describes the serializable
	     * fields
	     */
	    public ObjectStreamClass getObjectStreamClass() {
		return desc;
	    }

	    /**
	     * Return true if the named field is defaulted and has no value
	     * in this stream.
	     *
	     * @param name the name of the field
	     * @return true, if and only if the named field is defaulted
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if <code>name</code> does not
	     * correspond to a serializable field
	     */
	    public boolean defaulted(String name)
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, null);
		    return (field == null);   	
		}

	    /**
	     * Get the value of the named boolean field from the persistent
	     * field.
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>boolean</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>char</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>byte</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
	     */
	    public byte get(String name, byte defvalue) 
		throws IOException, IllegalArgumentException
		{
		    ObjectStreamField field = checkField(name, Byte.TYPE);
		    if (field == null)
			return defvalue;

		    return data[field.getOffset()];
		}

	    /** Get the value of the named short field from the persistent
	     * fields.
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>short</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>int</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>long</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     * Get the value of the named float field from the persistent
	     * fields.
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>float</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     * Get the value of the named double field from the persistent
	     * field.
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>double</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
	     * Get the value of the named Object field from the persistent
	     * field.
	     *
	     * @param name the name of the field
	     * @param defvalue the default value to use if <code>name</code>
	     * does not have a value
	     * @return the value of the named <code>Object</code> field
	     * @throws IOException if there are I/O errors while reading from
	     * the underlying <code>InputStream</code>
	     * @throws IllegalArgumentException if type of <code>name</code> is
	     * not serializable or if the field type is incorrect
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
		if (desc.numPrimBytes > 0)
		    data = new byte[desc.numPrimBytes];
		if (desc.numObjFields > 0)
		    objects = new Object[desc.numObjFields];
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

    /* Buffer used for temporarily storing data for primitive fields, primitive
     * arrays, and UTF strings.
     */
    private byte[] data = new byte[1024];
    
    /* Buffer used for temporarily storing decoded fragments of UTF strings. */
    private char[] cdata = new char[50];
    private static final int CDATA_MAX_LEN = 1000;
    
    /* String buffer used for constructing strings. */
    private StringBuffer sbuf = new StringBuffer();

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
