/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.AccessController;

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
 * Primitive data, excluding serializable fields and externalizable
 * data, is written to the ObjectOutputStream in block-data
 * records. A block data record is composed of a header and
 * data. The block data header consists of a marker and the
 * number of bytes to follow the header.  Consecutive primitive data
 * writes are merged into one block-data record.
 *
 *  (*) The blocking factor used for a block-data record will
 *      be 1024 bytes.
 *
 *  (*) Each block-data record will be filled up to 1024 bytes, or be
 *      written whenever there is a termination of block-data mode.
 *
 *  Calls to the ObjectOutputStream methods writeObject,
 *  defaultWriteObject and writeFields initially terminate any
 *  existing block-data record.
 *
 * @author	Roger Riggs
 * @version     1.109, 02/06/02
 * @see java.io.DataOutput
 * @see java.io.ObjectInputStream
 * @see java.io.Serializable
 * @see java.io.Externalizable
 * @see <a href="../../../guide/serialization/spec/output.doc.html"> Object Serialization Specification, Section 2, Object Output Classes</a>
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
     *
     * @param out <code>OutputStream</code> to read from
     * @exception IOException Any exception thrown by the underlying
     * OutputStream.

     */
    public ObjectOutputStream(OutputStream out) throws IOException {
	enableSubclassImplementation = false;
	this.out = out;
	dos = new DataOutputStream(this);
	buf = new byte[1024];	// allocate buffer
	writeStreamHeader();
	resetStream();
    }

    /**
     * Provide a way for subclasses that are completely reimplementing
     * ObjectOutputStream to not have to allocate private data just used by
     * this implementation of ObjectOutputStream.
     *
     * <p>If there is a security manager installed, this method first calls the
     * security manager's <code>checkPermission</code> method with a
     * <code>SerializablePermission("enableSubclassImplementation")</code>
     * permission to ensure it's ok to enable subclassing.
     *
     * @exception IOException   Thrown if not called by a subclass.
     * 
     * @throws SecurityException
     *    if a security manager exists and its 
     *    <code>checkPermission</code> method denies
     *    enabling subclassing.
     *
     * @see SecurityManager#checkPermission
     * @see java.io.SerializablePermission
     */
    protected ObjectOutputStream() throws IOException, SecurityException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
	enableSubclassImplementation = true;
     }

    /**
     * Method used by subclasses to override the default writeObject method.
     * This method is called by trusted subclasses of ObjectInputStream
     * that constructed ObjectInputStream using the
     * protected no-arg constructor. The subclass is expected to provide
     * an override method with the modifier "final".
     *
     * @param obj object to be written to the underlying stream
     * @throws IOException if there are I/O errors while writing to the
     * underlying stream
     * @see #ObjectOutputStream()
     * @see #writeObject(Object)
     * @since 1.2
     */
    protected void writeObjectOverride(Object obj) throws IOException
    {
    }

    /**
     * Specify stream protocol version to use when writing the stream.<p>
     *
     * This routine provides a hook to enable the current version
     * of Serialization to write in a format that is backwards
     * compatible to a previous version of the stream format.<p>
     *
     * Every effort will be made to avoid introducing additional
     * backwards incompatibilities; however, sometimes there is no
     * other alternative.<p>
     *
     * @param version   use ProtocolVersion from java.io.ObjectStreamConstants.
     * @exception IllegalStateException   Thrown if called after any objects
     * have been serialized.
     * @exception IllegalArgumentException if invalid version is passed in.
     * @throws IOException if I/O errors occur
     *
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_1
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_2
     * @since	1.2
     */
    public void useProtocolVersion(int version) throws IOException {
	if (handleTable.size() != 0) {
	    throw new IllegalStateException("Must call useProtocolVersion" +
		    " before writing any objects to the stream");
	}

	switch (version) {
	case PROTOCOL_VERSION_1:
	    useDeprecatedExternalizableFormat = true;
	    break;
	case PROTOCOL_VERSION_2:
	    break;
	default:
	    throw new IllegalArgumentException("unknown version:" + version);
	};
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
     *
     * @exception InvalidClassException Something is wrong with a class used by
     *	   serialization.
     * @exception NotSerializableException Some object to be serialized does not
     *	  implement the java.io.Serializable interface.
     * @exception IOException Any exception thrown by the underlying OutputStream.
     */
    public final void writeObject(Object obj)
	throws IOException
    {
	if (enableSubclassImplementation) {
	    writeObjectOverride(obj);
	    return;
	}

	Object prevObject = currentObject;
	ObjectStreamClass prevClassDesc = currentClassDesc;
	boolean oldBlockDataMode = setBlockData(false);
	recursionDepth++;

	try {
	    if (serializeNullAndRepeat(obj, REPLACEABLE))
		return;

	    if (checkSpecialClasses(obj))
		return;

	    Class curclass = obj.getClass();
	    Class oldclass = null;
	    Object altobj = obj;

	    currentClassDesc =
		ObjectStreamClass.lookupInternal(curclass);

	    /* Allow the class to replace the instance to be serialized. */
	    /* Fix for 4217737: allow replacement object to nominate its own
	     * replacement, so long as it's not the same class as the replaced
	     * object.
	     */
	    while (currentClassDesc != null &&
		   currentClassDesc.isReplaceable() &&
		   curclass != oldclass) 
	    {
		altobj = ObjectStreamClass.invokeMethod(
					    currentClassDesc.writeReplaceMethod,
					    altobj,
					    null);
		oldclass = curclass;
		if (altobj != null) {
		    curclass = altobj.getClass();
		    currentClassDesc = 
			ObjectStreamClass.lookupInternal(curclass);
		} else {
		    curclass = null;
		    currentClassDesc = null;
		}
	    }

	    /* If the replacment is enabled, give subclasses one chance
	     * to substitute a new object. If one is substituted,
	     * recheck for null, repeated refs, and special cased classes
	     */
	    if (enableReplace) {
		altobj = replaceObject(altobj);
		currentClassDesc = (altobj != null) ?
		    ObjectStreamClass.lookupInternal(altobj.getClass()) : null;
	    }

	    /* If the object has been replaced check that the object
	     * is serializable and recheck for the special cases.
	     */
	    if (obj != altobj) {
		if (altobj != null && ! (altobj instanceof Serializable)) {
		    String clname = altobj.getClass().getName();
		    throw new NotSerializableException(clname);
		}

		// If the alternate object is already
		// serialized just remember the replacement
		if (serializeNullAndRepeat(altobj, REPLACEABLE)) {
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
	    if (checkSubstitutableSpecialClasses(obj,
						 currentClassDesc.forClass()))
		return;

	    /* Write out the object as itself */
	    outputObject(obj);
	} catch (IOException ee) {
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
		    setBlockData(false); //added since resetStream set to TRUE.
		    currentClassDesc =
			ObjectStreamClass.lookupInternal(ee.getClass());
		    this.outputObject(ee); //avoid recursing with writeObject
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
     * These objects are not subject to replacement.
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
    private boolean checkSubstitutableSpecialClasses(Object obj, Class cl)
	throws IOException
    {
	if (cl == String.class) {
	    outputString((String)obj);
	    return true;
	}

	if (cl.isArray()) {
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
     *
     * @throws IOException if I/O errors occur while writing to the underlying
     * <code>OutputStream</code>
     */
    public void defaultWriteObject() throws IOException {
	if (currentObject == null || currentClassDesc == null)
	    throw new NotActiveException("defaultWriteObject");

	ObjectStreamField[] fields =
	    currentClassDesc.getFieldsNoCopy();
	if (fields.length > 0) {
	    boolean prevmode = setBlockData(false);
	    outputClassFields(currentObject, currentClassDesc.forClass(),
			      fields);
	    setBlockData(prevmode);
	}
    }

    /**
     * Retrieve the object used to buffer persistent fields to be written to
     * the stream.  The fields will be written to the stream when writeFields
     * method is called.
     * 
     * @return an instance of the class Putfield that holds the serializable
     * fields
     * @exception IOException if I/O errors occur
     * @since 1.2
     */
    public ObjectOutputStream.PutField putFields() throws IOException {
	if (currentObject == null || currentClassDesc == null)
	    throw new NotActiveException("putFields");
    	// TBD: check if defaultWriteObject has already been called.
    	currentPutFields = new ObjectOutputStream.PutFieldImpl(currentClassDesc);
    	return currentPutFields;
    }

    /**
     * Write the buffered fields to the stream.
     *
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     * @throws NotActiveException Called when a classes writeObject
     * method was not called to write the state of the object.
     * @since 1.2
     */
    public void writeFields() throws IOException {
    	if (currentObject == null || currentClassDesc == null || currentPutFields == null)
    	    throw new NotActiveException("writeFields");

	boolean prevmode = setBlockData(false);
    	currentPutFields.write(this);
	setBlockData(prevmode);
    }

    /**
     * Reset will disregard the state of any objects already written
     * to the stream.  The state is reset to be the same as a new
     * ObjectOutputStream.  The current point in the stream is marked
     * as reset so the corresponding ObjectInputStream will be reset
     * at the same point.  Objects previously written to the stream
     * will not be refered to as already being in the stream.  They
     * will be written to the stream again.
     * 
     * @throws IOException if reset() is invoked while serializing an object.
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
	if (handleTable == null) {
	    handleTable = new HandleTable(11, (float) 7.0);
	} else {
	    handleTable.clear();
	}

	if (classDescStack == null) {
	    classDescStack = new Stack();
	} else {
	    classDescStack.setSize(0);
	}

	if (replaceTable != null) {
	    replaceTable.clear();
	}

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
     *
     * @param cl the class to annotate custom data for
     * @exception IOException Any exception thrown by the underlying OutputStream.
     */
    protected void annotateClass(Class cl)
	throws IOException
    {
    }

    /**
     * Subclasses may implement this method to store custom data in the
     * stream along with descriptors for dynamic proxy classes.
     *
     * <p>This method is called exactly once for each unique proxy class
     * descriptor in the stream.  The default implementation of this
     * method in <code>ObjectOutputStream</code> does nothing.
     *
     * <p>The corresponding method in <code>ObjectInputStream</code> is
     * <code>resolveProxyClass</code>.  For a given subclass of
     * <code>ObjectOutputStream</code> that overrides this method, the
     * <code>resolveProxyClass</code> method in the corresponding
     * subclass of <code>ObjectInputStream</code> must read any data or
     * objects writtem by <code>annotateProxyClass</code>.
     *
     * @param	cl the proxy class to annotate custom data for
     * @throws	IOException any exception thrown by the underlying
     *			    <code>OutputStream</code>
     * @see ObjectInputStream#resolveProxyClass(String[])
     * @since	1.3
     */
    protected void annotateProxyClass(Class cl)
	throws IOException
    {
    }

    /** 
     * This method will allow trusted subclasses of ObjectOutputStream to
     * substitute one object for another during serialization. Replacing objects
     * is disabled until enableReplaceObject is called. The enableReplaceObject
     * method checks that the stream requesting to do replacment can be trusted.
     * The first occurrence of each object written into the serialization stream
     * is passed to replaceObject.  Subsequent references to the object are
     * replaced by the object returned by the original call to replaceObject.
     * To ensure that the private state of objects is not unintentionally
     * exposed, only trusted streams may use replaceObject. <p>
     * 
     * The ObjectOutputStream.writeObject method takes a parameter of type
     * Object (as opposed to type Serializable) to allow for cases where
     * non-serializable objects are replaced by serializable ones.
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
     * @param obj the object to be replaced
     * @return the alternate object that replaced the specified one
     * @exception IOException Any exception thrown by the underlying
     * OutputStream.
     */
    protected Object replaceObject(Object obj)
	throws IOException
    {
	return obj;
    }

    /**
     * Enable the stream to do replacement of objects in the stream.
     *
     * <p>When enabled, the replaceObject method is called for every object
     * being serialized.
     *
     * <p>If <i>enable</i> is true, and there is a security manager installed,
     * this method first calls the
     * security manager's <code>checkPermission</code> method with a
     * <code>SerializablePermission("enableSubstitution")</code>
     * permission to ensure it's ok to 
     * enable the stream to do replacement of objects in the stream.
     *
     * @param enable boolean parameter to enable replacement of objects
     * @return the previous setting before this method was invoked
     * @throws SecurityException
     *    if a security manager exists and its 
     *    <code>checkPermission</code> method denies
     *    enabling the stream to do replacement of objects in the stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.io.SerializablePermission
     */
    protected boolean enableReplaceObject(boolean enable)
	throws SecurityException
    {
	boolean previous = enableReplace;
	if (enable) {
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null) sm.checkPermission(SUBSTITUTION_PERMISSION);
 	    enableReplace = true;
	} else {
	    enableReplace = false;
	}
	return previous;
    }

    /**
     * The writeStreamHeader method is provided so subclasses can
     * append or prepend their own header to the stream.
     * It writes the magic number and version to the stream.
     *
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
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
	long utflen;
	int slen = s.length();

	handleTable.assignWireOffset(s);
	
	if ((cdata == null) || (cdata.length < slen)) {
	    cdata = s.toCharArray();
	} else {
	    s.getChars(0, slen, cdata, 0);
	}

	utflen = getUTFLength(cdata, slen);
	if (utflen <= 0xFFFF) { 	// use normal utf format
	    writeCode(TC_STRING);
	    writeShort((int) utflen);
	} else {			// use long utf format
	    writeCode(TC_LONGSTRING);
	    writeLong(utflen);
	}
	writeUTFBody(cdata, slen);
	
	if (slen > CDATA_MAX_LEN)
	    cdata = null;		// let go of long arrays
    }

    /**
     * Return length of UTF encoding of this string, excluding initial length
     * tag.
     */
    private static long getUTFLength(char[] chars, int len) {
	long utflen = 0;

	for (int i = 0; i < len; i++) {
	    int c = chars[i];
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}
	return utflen;
    }

    /**
     * Write UTF encoding of this string, but don't write initial length tag.
     */
    private void writeUTFBody(char[] chars, int len)
	throws IOException
    {
	final int PADLEN = 2;   // keep extra 2 bytes at end of buffer
	int blimit = buf.length - PADLEN;
	int c;

	for (int i = 0; i < len; i++) {
	    c = chars[i];

	    if (count >= blimit) {
		if (blockDataMode) {
		    /* must write utf encoding of character one byte at a time,
		     * to normalize block data record.
		     */
		    if ((c >= 0x0001) && (c <= 0x007F)) {
			write(c);
		    } else if (c > 0x07FF) {
			write(0xE0 | ((c >> 12) & 0x0F));
			write(0x80 | ((c >>  6) & 0x3F));
			write(0x80 | ((c >>  0) & 0x3F));
		    } else {
			write(0xC0 | ((c >>  6) & 0x1F));
			write(0x80 | ((c >>  0) & 0x3F));
		    }
		    continue;
		}
		drain();
	    }

	    if ((c >= 0x0001) && (c <= 0x007F)) {
		buf[count++] = (byte) c;
	    } else if (c > 0x07FF) {
		buf[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
		buf[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
		buf[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
	    } else {
		buf[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
		buf[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
	    }
	}
    }

    /* Classes are special, they can not be created during deserialization,
     * but the appropriate class can be found.
     */
    private void outputClass(Class aclass) throws IOException {

	writeCode(TC_CLASS);
	/* Find the class descriptor and write it out */
	ObjectStreamClass v = ObjectStreamClass.lookupInternal(aclass);
	if (v == null)
	    throw new NotSerializableException(aclass.getName());

	outputClassDescriptor(v);

	handleTable.assignWireOffset(aclass);
    }

    /**
     * Write class descriptor to stream in the default format.
     */
    private void writeClassDescriptor0(ObjectStreamClass classdesc)
	throws IOException
    {
	writeUTF(classdesc.getName());
	writeLong(classdesc.getSerialVersionUID());
	classdesc.write(this);
    }

    /**
     * Write the specified class descriptor to the ObjectOutputStream.  Class
     * descriptors are used to identify the classes of objects written to the
     * stream.  Subclasses of ObjectOutputStream may override this method to
     * customize the way in which class descriptors are written to the
     * serialization stream.  The corresponding method in ObjectInputStream,
     * <code>readClassDescriptor</code>, should then be overridden to
     * reconstitute the class descriptor from its custom stream representation.
     * By default, this method writes class descriptors according to the format
     * defined in the Object Serialization specification.
     * <p>
     * Note that this method will only be called if the ObjectOutputStream is
     * not using the old serialization stream format (set by calling
     * ObjectOutputStream's <code>useProtocolVersion</code> method).  If this
     * serialization stream is using the old format
     * (<code>PROTOCOL_VERSION_1</code>), the class descriptor will be written
     * internally in a manner that cannot be overridden or customized.
     * <p>
     *
     * @param classdesc class descriptor to write to the stream
     * @exception IOException If an I/O error has occurred.
     * @see java.io.ObjectInputStream#readClassDescriptor()
     * @see #useProtocolVersion(int)
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_1
     * @since 1.3
     */
    protected void writeClassDescriptor(ObjectStreamClass classdesc)
	throws IOException
    {
	writeClassDescriptor0(classdesc);
    }

    /* Write the class descriptor */
    private void outputClassDescriptor(ObjectStreamClass classdesc)
	throws IOException
    {
	if (serializeNullAndRepeat(classdesc, NOT_REPLACEABLE))
	    return;

	Class cl = classdesc.forClass();
	if (!classdesc.forProxyClass) {
	    /*
	     * Write out the type code for a class descriptor.
	     */	
	    writeCode(TC_CLASSDESC);
    
	    /*
	     * Wire offset must be assigned before class descriptor is
	     * written, to be symmetric with inputClassDescriptor.
	     */
	    handleTable.assignWireOffset(classdesc);

	    /*
	     * Write out the class descriptor.  If the deprecated
	     * externalizable format is _not_ being used, give subclasses
	     * a chance to override class descriptor write hook.
	     */
	    if (useDeprecatedExternalizableFormat)
		writeClassDescriptor0(classdesc);
	    else
		writeClassDescriptor(classdesc);

	    /*
	     * Give subclassers a chance to add the class implementation
	     * to the stream.  Set BlockData mode so any information they
	     * write can be skipped on reading.
	     */
	    boolean prevMode = setBlockData(true);
	    annotateClass(cl);
	    setBlockData(prevMode);
	    writeCode(TC_ENDBLOCKDATA);

	    /*
	     * Write out the superclass descriptor of this descriptor
	     * only if it is for a java.io.Serializable class.
	     * else write null.
	     */
	    ObjectStreamClass superdesc = classdesc.getSuperclass();
	    outputClassDescriptor(superdesc);

	} else {
	    /*
	     * Write out the type code for a proxy class descriptor.
	     */
	    writeCode(TC_PROXYCLASSDESC);

	    /*
	     * The wire handle must be assigned before writing any other
	     * objects to the stream that could reference this descriptor.
	     */
	    handleTable.assignWireOffset(classdesc);

	    /*
	     * Write out the names of the proxy interfaces.
	     */
	    Class[] interfaces = cl.getInterfaces();
	    writeInt(interfaces.length);
	    for (int i = 0; i < interfaces.length; i++) {
		writeUTF(interfaces[i].getName());
	    }

	    /*
	     * Give subclasses a change to add custom annotations to the
	     * proxy class descriptor.  Use block data mode to allow
	     * custom data to be skipped.
	     */
	    boolean prevMode = setBlockData(true);
	    annotateProxyClass(cl);
	    setBlockData(prevMode);
	    writeCode(TC_ENDBLOCKDATA);

	    /*
	     * Write out the descriptor for the superclass.
	     */
	    outputClassDescriptor(classdesc.getSuperclass());
	}
    }

    /**
     * Write an array out. Note that since Arrays are Objects, writeObject(obj)
     * will behave identically. <br><br>
     *
     * @param o can represent an array of any type/dimension.
     */
    private void outputArray(Object obj)
	throws IOException
    {
	Class currclass = currentClassDesc.forClass();

	/* Write out the code for an array and the name of the class */
	writeCode(TC_ARRAY);
	outputClassDescriptor(currentClassDesc);

	/* Assign the wirehandle for this object and outputArrayValues
	 * writes the length and the array contents.
	 */
	handleTable.assignWireOffset(obj);

	int i, length, limit;
	Class type = currclass.getComponentType();

	if (type.isPrimitive()) {
	    /* Write arrays of primitive types using the DataOutput
	     * methods that convert each element into the output buffer.
	     * The data types are ordered by the frequency
	     * in which they are expected to occur.
	     * 
	     * Note: there is no need to worry about normalizing block data
	     * records here, since at this point we're not in block data mode.
	     */
	    if (type == Integer.TYPE) {
		int[] array = (int[])obj;
		length = array.length;
		limit = buf.length - 4;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    if (count > limit)
			drain();
		    int v = array[i];
		    buf[count++] = (byte) (v >>> 24);
		    buf[count++] = (byte) (v >>> 16);
		    buf[count++] = (byte) (v >>> 8);
		    buf[count++] = (byte) (v >>> 0);
		}
	    } else if (type == Byte.TYPE) {
		byte[] array = (byte[])obj;
		length = array.length;
		writeInt(length);
		writeInternal(array, 0, length, true);
	    } else if (type == Long.TYPE) {
		long[] array = (long[])obj;
		length = array.length;
		limit = buf.length - 8;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    if (count > limit)
			drain();
		    long v = array[i];
		    buf[count++] = (byte) (v >>> 56);
		    buf[count++] = (byte) (v >>> 48);
		    buf[count++] = (byte) (v >>> 40);
		    buf[count++] = (byte) (v >>> 32);
		    buf[count++] = (byte) (v >>> 24);
		    buf[count++] = (byte) (v >>> 16);
		    buf[count++] = (byte) (v >>> 8);
		    buf[count++] = (byte) (v >>> 0);
		}
	    } else if (type == Float.TYPE) {
		float[] array = (float[])obj;
		length = array.length;
		writeInt(length);
		
		int off = 0;
		while (length > 0) {
		    int avail = (buf.length - count) >> 2;
		    if (avail > 0) {
			int n = (length < avail) ? length : avail;
			floatsToBytes(array, off, buf, count, n);
			off += n;
			length -= n;
			count += n << 2;
		    } else {
			drain();
		    }
		}
	    } else if (type == Double.TYPE) {
		double[] array = (double[])obj;
		length = array.length;
		writeInt(length);
		
		int off = 0;
		while (length > 0) {
		    int avail = (buf.length - count) >> 3;
		    if (avail > 0) {
			int n = (length < avail) ? length : avail;
			doublesToBytes(array, off, buf, count, n);
			off += n;
			length -= n;
			count += n << 3;
		    } else {
			drain();
		    }
		}
	    } else if (type == Short.TYPE) {
		short[] array = (short[])obj;
		length = array.length;
		limit = buf.length - 2;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    if (count > limit)
			drain();
		    short v = array[i];
		    buf[count++] = (byte) (v >>> 8);
		    buf[count++] = (byte) (v >>> 0);
		}
	    } else if (type == Character.TYPE) {
		char[] array = (char[])obj;
		length = array.length;
		limit = buf.length - 2;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    if (count > limit)
			drain();
		    char v = array[i];
		    buf[count++] = (byte) (v >>> 8);
		    buf[count++] = (byte) (v >>> 0);
		}
	    } else if (type == Boolean.TYPE) {
		boolean[] array = (boolean[])obj;
		length = array.length;
		limit = buf.length - 1;
		writeInt(length);
		for (i = 0; i < length; i++) {
		    if (count > limit)
			drain();
		    buf[count++] = (byte) (array[i] ? 1 : 0);
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
     * Convert nfloats float values to their byte representations.  Float values
     * are read from array src starting at offset srcpos and written to array
     * dst starting at offset dstpos.
     */
    private static native void floatsToBytes(float[] src, int srcpos,
	    byte[] dst, int dstpos, int nfloats);
    
    /*
     * Convert ndoubles double values to their byte representations.  Double
     * values are read from array src starting at offset srcpos and written to
     * array dst starting at offset dstpos.
     */
    private static native void doublesToBytes(double[] src, int srcpos,
	    byte[] dst, int dstpos, int ndoubles);
    
    /*
     * Write a typeString to the stream.
     * Do not allow replaceObject to be called on typeString.
     */
    void writeTypeString(String typeString) throws IOException {

	int handle = handleTable.findWireOffset(typeString);
	if (handle >= 0) {
	    writeCode(TC_REFERENCE);
	    writeInt(handle + baseWireHandle);
	} else {
	    handleTable.assignWireOffset(typeString);
	    writeCode(TC_STRING);
	    writeUTF(typeString);
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
	if (currentClassDesc.isNonSerializable()) {
	    throw new NotSerializableException(currentClassDesc.getName());
	}


	/* Write the code to expect an instance and
	 * the class descriptor of the instance
	 */
	writeCode(TC_OBJECT);
	outputClassDescriptor(currentClassDesc);

	/* Assign the next wirehandle */
	handleTable.assignWireOffset(obj);

	/* If the object is externalizable,
	 * call writeExternal.
	 * else do Serializable processing.
	 */
	if (currentClassDesc.isExternalizable()) {
	    Externalizable ext = (Externalizable)obj;
	    if (useDeprecatedExternalizableFormat) {

		/* JDK 1.1 external data format.
		 * Don't write in block data mode and no terminator tag.
		 */
		ext.writeExternal(this);
	    } else {

		/* Java 2 SDK Externalizable data format writes in block data mode
		 * and terminates externalizable data with TAG_ENDBLOCKDATA.
		 */
		setBlockData(true);
		try {
		    ext.writeExternal(this);
		} finally {
		    setBlockData(false);
		    writeCode(TC_ENDBLOCKDATA);
		}
	    }
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
			invokeObjectWriter(obj);
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

    /*
     * Return a replacement for 'forObject', if one exists.
     */
    private Object lookupReplace(Object obj) {
	return (replaceTable != null) ? replaceTable.lookup(obj) : obj;
    }


    /* Serialize the reference if it is NULL or is for an object that
     * was already replaced or already serialized.
     * If the object was already replaced, look for the replacement
     * object in the known objects and if found, write its handle
     *
     * @param checkForReplace  only if true. Enables optimization of
     *                         not checking for replacement of non-replacable
     *                         objects.
     * @return True if the reference is either null or a repeat.
     */
    private boolean serializeNullAndRepeat(Object obj, boolean checkForReplace)
	throws IOException
    {
    	if (obj == null) {
	    writeCode(TC_NULL);
	    return true;
	}

	/* Look to see if this object has already been replaced.
	 * If so, proceed using the replacement object.
	 */
	if (checkForReplace) {
	    obj = lookupReplace(obj);
	}

	int handle = handleTable.findWireOffset(obj);
	if (handle >= 0) {
	    /* Add a reference to the stream */
	    writeCode(TC_REFERENCE);
	    writeInt(handle + baseWireHandle);
	    return true;
	}
	return false;		// not serialized, its up to the caller
    }

    /*
     * Add a replacement object to the table.
     * The even numbered indices are the original objects.
     * The odd numbered indices are the replacement objects.
     *
     */
    private void addReplacement(Object orig, Object replacement) {
	if (replaceTable == null) {
	    replaceTable = new ReplaceTable(11, (float) 7.0);
	}
	replaceTable.assign(orig, replacement);
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
     *
     * @param data the byte to be written to the stream
     * @exception IOException If an I/O error has occurred.
     */
    public void write(int data) throws IOException {

	if (count >= buf.length)
	    drain();		/* Drain, make room for more */
	buf[count++] = (byte)data;
    }

    /**
     * Writes an array of bytes. This method will block until the bytes
     * are actually written.
     *
     * @param b	the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    public void write(byte b[]) throws IOException {
	write(b, 0, b.length);
    }

    /*
     * Writes a sub array of bytes.
     *
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @param copyOnWrite do not expose b to overrides of ObjectStream.write,
     *                    copy the contents of b to a buffer before writing.
     * @exception IOException If an I/O error has occurred.
     */
    private void writeInternal(byte b[], int off, int len,
			       boolean copyOnWrite) throws IOException {
	if ((len < 0) || (off < 0) || (off + len > b.length)) {
	    throw new IndexOutOfBoundsException();
	}

	if (len == 0) {
	    return;
	}

	if (blockDataMode) {
	    writeCanonical(b, off, len);
	} else {
	    /*
	     * If array will fit in output buffer, copy it in there; otherwise,
	     * drain anything in the buffer and send it through to underlying
	     * output stream directly.
	     */
	    int avail = buf.length - count;
	    if (len <= avail) {
		System.arraycopy(b, off, buf, count, len);
		count += len;
	    } else if (copyOnWrite) {
		bufferedWrite(b, off, len);
	    } else {
		drain();
		out.write(b, off, len);
	    }
	}
    }

    /**
     * Writes a sub array of bytes.
     *
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
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
     * Flushes the stream. This will write any buffered
     * output bytes and flush through to the underlying stream.
     *
     * @exception IOException If an I/O error has occurred.
     */
    public void flush() throws IOException {
	drain();
	out.flush();
    }

    /**
     * Drain any buffered data in ObjectOutputStream.  Similar to flush
     * but does not propagate the flush to the underlaying stream.
     *
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
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

	if (blockDataMode)
	    writeBlockDataHeader(count);
	out.write(buf, 0, count);
	count = 0;
    }

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     *
     * @exception IOException If an I/O error has occurred.
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

    /* Write the Block-data marker and the length of the data to follow
     * to stream 'out'. If the length is < 256, use the short header form.
     * othewise use the long form.
     */
    private void writeBlockDataHeader(int len) throws IOException {
	if (len <= 255) {
	    out.write(TC_BLOCKDATA);
	    out.write((byte)len);
	} else {
	    // use block data with int size if necessary
	    out.write(TC_BLOCKDATALONG);
	    // send 32 bit int directly to underlying stream
	    out.write((byte)((len >> 24) & 0xFF));
	    out.write((byte)((len >> 16) & 0xFF));
	    out.write((byte)((len >>  8) & 0xFF));
	    out.write((byte)(len & 0xFF));
	}
    }

    /* Canonical form requires constant blocking factor. Write data
     * in b array in constant block-data chunks.
     *
     * Assumes only called when blockDataMode is true.
     */
    private void writeCanonical(byte b[], int off, int len)
	throws IOException
    {
	int bufAvail = buf.length - count;
	int bytesToWrite = len;

	// Handle case where byte array is larger than available buffer.
	if (bytesToWrite > bufAvail) {
	    // Logically: fill rest of 'buf' with 'b' and drain.
	    // Optimization: avoid copying to 'buf', write partial 'buf' and partial
	    //               'b' directly to 'out'.
	    writeBlockDataHeader(buf.length);
	    out.write(buf, 0, count);
	    out.write(b, off, bufAvail);

	    count = 0;
	    off += bufAvail;
	    bytesToWrite -= bufAvail;

	    // Optimization: write 'buf.length' BlockData directly to stream.
	    while (bytesToWrite >= buf.length) {
		if (blockDataMode)
		    writeBlockDataHeader(buf.length);
		out.write(b, off, buf.length);
		off += buf.length;
		bytesToWrite -= buf.length;
	    }
	}

	// Put remainder of byte array into buffer.
	if (bytesToWrite != 0) {
	    System.arraycopy(b, off, buf, count, bytesToWrite);
	    count += bytesToWrite;
	}
    }

    /* -------------------------------------------------------------- */
    /*
     * Provide the methods to implement DataOutput.  When possible, values are
     * written directly to the internal buffer.  
     * 
     * Note that each block data record must be filled to capacity (1024 bytes),
     * unless block data mode terminates before 1024 bytes has been reached.
     * This "canonical" block data size allows serialized objects to be compared
     * against each other by checking whether or not the bytes of their
     * serialized representations differ.  
     * 
     * Consequently, if the current block data record is near capacity and only
     * has f bytes free, and we're attempting to write a primitive value p of
     * length l where l > f, then we must write the first f bytes of p in the
     * current data block, and the l - f remaining bytes in the next data block.
     * The easiest way to do this is to delegate to DataOutputStream, which will
     * write the values one byte at a time through
     * ObjectOutputStream.write(int), ensuring that the data block is completely
     * filled before draining it.
     */
    private DataOutputStream dos;

    /**
     * Writes a boolean.
     *
     * @param data the boolean to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeBoolean(boolean data) throws IOException {
	if (count >= buf.length) {
	    dos.writeBoolean(data);
	    return;
	}
	buf[count++] = (byte) (data ? 1 : 0);

    }

    /**
     * Writes an 8 bit byte.
     *
     * @param data the byte value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeByte(int data) throws IOException  {
	if (count >= buf.length) {
	    dos.writeByte(data);
	    return;
	}
	buf[count++] = (byte) data;
    }

    /**
     * Writes a 16 bit short.
     *
     * @param data the short value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeShort(int data)  throws IOException {
	if (count + 2 > buf.length) {	// canonicalize data block
	    dos.writeShort(data);
	    return;
	}
	buf[count++] = (byte) (data >>>  8);
	buf[count++] = (byte) (data >>>  0);
    }

    /**
     * Writes a 16 bit char.
     *
     * @param data the char value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeChar(int data)  throws IOException {
	if (count + 2 > buf.length) {	// canonicalize data block
	    dos.writeChar(data);
	    return;
	}
	buf[count++] = (byte) (data >>>  8);
	buf[count++] = (byte) (data >>>  0);
    }

    /**
     * Writes a 32 bit int.
     *
     * @param data the integer value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeInt(int data)  throws IOException {
	if (count + 4 > buf.length) {	// canonicalize data block
	    dos.writeInt(data);
	    return;
	}
	buf[count++] = (byte) (data >>> 24);
	buf[count++] = (byte) (data >>> 16);
	buf[count++] = (byte) (data >>>  8);
	buf[count++] = (byte) (data >>>  0);
    }

    /**
     * Writes a 64 bit long.
     *
     * @param data the long value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeLong(long data)  throws IOException {
	if (count + 8 > buf.length) {	// canonicalize data block
	    dos.writeLong(data);
	    return;
	}
	buf[count++] = (byte) (data >>> 56);
	buf[count++] = (byte) (data >>> 48);
	buf[count++] = (byte) (data >>> 40);
	buf[count++] = (byte) (data >>> 32);
	buf[count++] = (byte) (data >>> 24);
	buf[count++] = (byte) (data >>> 16);
	buf[count++] = (byte) (data >>>  8);
	buf[count++] = (byte) (data >>>  0);
    }

    /**
     * Writes a 32 bit float.
     *
     * @param data the float value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeFloat(float data) throws IOException {
	int value = Float.floatToIntBits(data);
	if (count + 4 > buf.length) {	// canonicalize data block
	    dos.writeFloat(data);
	    return;
	}
	buf[count++] = (byte) (value >>> 24);
	buf[count++] = (byte) (value >>> 16);
	buf[count++] = (byte) (value >>>  8);
	buf[count++] = (byte) (value >>>  0);
    }

    /**
     * Writes a 64 bit double.
     *
     * @param data the double value to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeDouble(double data) throws IOException {
	long value = Double.doubleToLongBits(data);
	if (count + 8 > buf.length) {	// canonicalize data block
	    dos.writeDouble(data);
	    return;
	}
	buf[count++] = (byte) (value >>> 56);
	buf[count++] = (byte) (value >>> 48);
	buf[count++] = (byte) (value >>> 40);
	buf[count++] = (byte) (value >>> 32);
	buf[count++] = (byte) (value >>> 24);
	buf[count++] = (byte) (value >>> 16);
	buf[count++] = (byte) (value >>>  8);
	buf[count++] = (byte) (value >>>  0);
    }

    /**
     * Writes a String as a sequence of bytes.
     *
     * @param data the String of bytes to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeBytes(String data) throws IOException {
	char[] chars = data.toCharArray();
	int len = chars.length;
	int buflen = buf.length;

	for (int i = 0; i < len; i++) {
	    if (count >= buflen)
		drain();
	    buf[count++] = (byte) chars[i];
	}
    }

    /**
     * Writes a String as a sequence of chars.
     *
     * @param data the String of chars to be written
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeChars(String data) throws IOException {
	char[] chars = data.toCharArray();
	int len = chars.length;
	int limit = buf.length - 2;
	char c;
	
	for (int i = 0; i < len; i++) {
	    c = chars[i];
	    if (count > limit) {
		dos.writeChar(c);
	    } else {
		buf[count++] = (byte) (c >>> 8);
		buf[count++] = (byte) (c >>> 0);
	    }
	}
    }

    /**
     * Primitive data write of this String in UTF format.
     *
     * Note that there is a significant difference between
     * writing a String into the stream as primitive data or
     * as an Object. A String instance written by writeObject
     * is written into the stream as a String initially. Future
     * writeObject() calls write references to the string into
     * the stream.
     *
     * @param s the String in UTF format
     * @throws IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeUTF(String s) throws IOException {
	long utflen;
	int slen = s.length();
	
	if ((cdata == null) || (cdata.length < slen)) {
	    cdata = s.toCharArray();
	} else {
	    s.getChars(0, slen, cdata, 0);
	}

	utflen = getUTFLength(cdata, slen);
	if (utflen > 0xFFFF)
	    throw new UTFDataFormatException();
	writeShort((int) utflen);
	writeUTFBody(cdata, slen);
	
	if (slen > CDATA_MAX_LEN)
	    cdata = null;		// let go of long arrays
    }

    /* Write the fields of the specified class by invoking the appropriate
     * write* method on this class.
     */
    private void outputClassFields(Object o, Class cl,
				   ObjectStreamField[] fields)
	throws IOException, InvalidClassException 
    {
	/*
	 * Fetch and write primitive data fields to the output stream.
	 */
	int numPrimBytes = currentClassDesc.numPrimBytes;
  	if (numPrimBytes > 0) {
	    if (data == null) {
		data = new byte[Math.max(numPrimBytes, INITIAL_BUFFER_SIZE)];
	    } else if (data.length < numPrimBytes) {
		data = new byte[numPrimBytes];
	    }
	    getPrimitiveFieldValues(o, currentClassDesc.primFieldIDs,
		    currentClassDesc.primFieldTypecodes, data);
	    writeInternal(data, 0, numPrimBytes, false);
	}

	int numPrimFields = fields.length - currentClassDesc.numObjFields;
	long[] objFieldIDs = currentClassDesc.objFieldIDs;
	for (int i = 0; i < currentClassDesc.numObjFields; i++) {
	    Object val;
	    try {
		val = getObjectFieldValue(o, objFieldIDs[i]);
	    } catch (Exception e) {
		throw new InvalidClassException(cl.getName(),
			"Invalid field " + fields[numPrimFields + i].getName());
	    }
	    writeObject(val);
	}
    }

    /*
     * Gets the values of the primitive fields of object obj.  fieldIDs is an
     * array of field IDs (the primFieldsID field of the appropriate
     * ObjectStreamClass) identifying which fields to get.  typecodes is an
     * array of characters designating the primitive type of each field (e.g.,
     * 'C' for char, 'Z' for boolean, etc.)  data is the byte buffer in which
     * the primitive field values are written, in the order of their field IDs.
     * 
     * For efficiency, this method does not check all of its arguments for
     * safety.  Specifically, it assumes that obj's type is compatible with the
     * given field IDs, and that the data array is long enough to contain all of
     * the byte values that will be written to it.
     */
    private static native void getPrimitiveFieldValues(Object obj, 
	    long[] fieldIDs, char[] typecodes, byte[] data);
    
    /*
     * Gets the value of an object field of object obj.  fieldID is the field ID
     * identifying which field to set (obtained from the objFieldsID array field
     * of the appropriate ObjectStreamClass).
     * 
     * For efficiency, this method does not check to make sure that obj's type
     * is compatible with the given field ID.
     */
    private static native Object getObjectFieldValue(Object obj, long fieldID);
    
    /*
     * Test if WriteObject method is present, and if so, invoke writer.
     */
    private void invokeObjectWriter(Object obj)
	throws IOException
    {
	try {
	    currentClassDesc.writeObjectMethod.invoke(obj, writeObjectArglist);
	} catch (InvocationTargetException e) {
	    Throwable t = e.getTargetException();
	    if (t instanceof IOException)
		throw (IOException)t;
	    else if (t instanceof RuntimeException)
		throw (RuntimeException) t;
	    else if (t instanceof Error)
		throw (Error) t;
	    else
		throw new Error("internal error");
	} catch (IllegalAccessException e) {
	    /* Should not happen, unless there is a problem with the VM. This
	     * is because the writeObject method is obtained via
	     * ObjectStreamClass.getDeclaredMethod, which calls setAccessible
	     * on it, in privileged mode.
	     */
	    throw new InternalError("Unable to access writeObject method");
	}
    }

    /*************************************/

    /**
     * Provide programatic access to the persistent fields to be written
     * to ObjectOutput.
     *
     * @since 1.2
     */
    static public abstract class PutField {
	/**
	 * Put the value of the named boolean field into the persistent field.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, boolean value);

	/**
	 * Put the value of the named char field into the persistent fields.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, char value);

	/**
	 * Put the value of the named byte field into the persistent fields.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, byte value);

	/**
	 * Put the value of the named short field into the persistent fields.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, short value);

	/**
	 * Put the value of the named int field into the persistent fields.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, int value);

	/**
	 * Put the value of the named long field into the persistent fields.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, long value);

	/**
	 * Put the value of the named float field into the persistent fields.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, float value);

	/**
	 * Put the value of the named double field into the persistent field.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, double value);

	/**
	 * Put the value of the named Object field into the persistent field.
	 *
	 * @param name the name of the serializable field
	 * @param value the value to assign to the field
	 */
	abstract public void put(String name, Object value);

	/**
	 * Write the data and fields to the specified ObjectOutput stream.
	 * 
	 * @param out the stream to write the data and fields to
	 * @throws IOException if I/O errors occur while writing to the
	 * underlying stream
	 */
	abstract public void write(ObjectOutput out) throws IOException;
    };

    /*************************************************************/


    /**
     * Provide access to the persistent fields to be written to the output
     * stream.
     */
    static final class PutFieldImpl extends PutField {
	 /**
	  * Put the value of the named boolean field into the persistent field.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, boolean value)
	     throws IllegalArgumentException
	 {
	     ObjectStreamField field = desc.getField(name, Boolean.TYPE);
	     if (field == null || field.getType() != Boolean.TYPE)
		 throw new IllegalArgumentException("No such boolean field");
	     data[field.getOffset()] = (byte)(value ? 1 : 0);
	}

	 /**
	  * Put the value of the named char field into the persistent fields.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, char value) {
	     ObjectStreamField field = desc.getField(name, Character.TYPE);
	     if (field == null || field.getType() != Character.TYPE)
		 throw new IllegalArgumentException("No such char field");
	     data[field.getOffset()] = (byte)(value >> 8);
	     data[field.getOffset()+1] = (byte)(value);
	 }

	 /**
	  * Put the value of the named byte field into the persistent fields.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, byte value) {
	     ObjectStreamField field = desc.getField(name, Byte.TYPE);
	     if (field == null || field.getType() != Byte.TYPE)
		 throw new IllegalArgumentException("No such byte field");
	     data[field.getOffset()] = value;
	 }

	 /**
	  * Put the value of the named short field into the persistent fields.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, short value) {
	     ObjectStreamField field = desc.getField(name, Short.TYPE);
	     if (field == null || field.getType() != Short.TYPE)
		 throw new IllegalArgumentException("No such short field");

	     int loffset = field.getOffset();
	     data[loffset] = (byte)(value >> 8);
	     data[loffset+1] = (byte)(value);
	 }

	 /**
	  * Put the value of the named int field into the persistent fields.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, int value) {
	     ObjectStreamField field = desc.getField(name, Integer.TYPE);
	     if (field == null || field.getType() != Integer.TYPE)
		 throw new IllegalArgumentException("No such int field");

	     int loffset = field.getOffset();
	     data[loffset] = (byte)(value >> 24);
	     data[loffset+1] = (byte)(value >> 16);
	     data[loffset+2] = (byte)(value >> 8);
	     data[loffset+3] = (byte)value;
	 }

	 /**
	  * Put the value of the named long field into the persistent fields.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, long value) {
	     ObjectStreamField field = desc.getField(name, Long.TYPE);
	     if (field == null || field.getType() != Long.TYPE)
		 throw new IllegalArgumentException("No such long field");

	     int loffset = field.getOffset();
	     data[loffset] = (byte)(value >> 56);
	     data[loffset+1] = (byte)(value >> 48);
	     data[loffset+2] = (byte)(value >> 40);
	     data[loffset+3] = (byte)(value >> 32);
	     data[loffset+4] = (byte)(value >> 24);
	     data[loffset+5] = (byte)(value >> 16);
	     data[loffset+6] = (byte)(value >> 8);
	     data[loffset+7] = (byte)value;
	 }

	 /**
	  * Put the value of the named float field into the persistent fields.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, float value) {
	     int val = Float.floatToIntBits(value);
	     ObjectStreamField field = desc.getField(name, Float.TYPE);
	     if (field == null || field.getType() != Float.TYPE)
		 throw new IllegalArgumentException("No such float field");

	     int loffset = field.getOffset();
	     data[loffset] = (byte)(val >> 24);
	     data[loffset+1] = (byte)(val >> 16);
	     data[loffset+2] = (byte)(val >> 8);
	     data[loffset+3] = (byte)val;
	 }

	 /**
	  * Put the value of the named double field into the persistent field.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, double value) {
	     long val = Double.doubleToLongBits(value);
	     ObjectStreamField field = desc.getField(name, Double.TYPE);
	     if (field == null || field.getType() != Double.TYPE)
		 throw new IllegalArgumentException("No such double field");

	     int loffset = field.getOffset();
	     data[loffset] = (byte)(val >> 56);
	     data[loffset+1] = (byte)(val >> 48);
	     data[loffset+2] = (byte)(val >> 40);
	     data[loffset+3] = (byte)(val >> 32);
	     data[loffset+4] = (byte)(val >> 24);
	     data[loffset+5] = (byte)(val >> 16);
	     data[loffset+6] = (byte)(val >> 8);
	     data[loffset+7] = (byte)val;
	 }

	 /**
	  * Put the value of the named Object field into the persistent field.
	  *
	  * @param name the name of the serializable field
	  * @param value the value to assign to the field
	  * @throws IllegalArgumentException if specified field does not exist
	  * or the type of the field does not match the type of
	  * <code>value</code>
	  */
	 public void put(String name, Object value) {
	     ObjectStreamField field = desc.getField(name, Object.class);
	     if (field == null || field.isPrimitive())
		 throw new IllegalArgumentException("No such object field");
	     objects[field.getOffset()] = value;
	 }

	 /**
	  * Write the data and fields to the specified stream.
	  * 
	  * @param out the stream to write the data and fields to
	  * @throws IOException if I/O errors occur while writing to the
	  * underlying stream
	  */
	 public void write(ObjectOutput out) throws IOException {
	     if (data != null)
		 out.write(data, 0, data.length);

	     if (objects != null) {
		 for (int i = 0; i < objects.length; i++)
		     out.writeObject(objects[i]);
	     }
	 }

	 /**
	  * Create a PutField object for the a Class.
	  * Allocate the arrays for primitives and objects.
	  */
	 PutFieldImpl(ObjectStreamClass descriptor) {
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
     };

    /*************************************************************/

    /* Remember the first exception that stopped this stream. */
    private IOException abortIOException = null;

    /* Object references are mapped to the wire handles through a hashtable
     * WireHandles are integers generated by the ObjectOutputStream,
     * they need only be unique within a stream.
     */
    private HandleTable handleTable;

    /* The object is the current object and ClassDescriptor is the current
     * subclass of the object being written. Nesting information is kept
     * on the stack.
     */
    private Object currentObject;
    private ObjectStreamClass currentClassDesc;
    private Stack classDescStack;
    private PutField currentPutFields;
    private Object[] writeObjectArglist = {this};

    /*
     * Primitive data is fetched from objects and stored in
     * this array prior to writing. The array is allocated prior to first use.
     */
    private static final int INITIAL_BUFFER_SIZE = 64;
    private byte[] data;

    /* Buffer for writing strings. */
    private static final int CDATA_MAX_LEN = 1024;
    private char[] cdata;

    /*
     * Flag set to true to allow replaceObject to be called.
     * Set by enableReplaceObject.
     * Identity hash table mapping objects to their replacements.
     */
    boolean enableReplace;
    private ReplaceTable replaceTable;
    static final private boolean REPLACEABLE = true;
    static final private boolean NOT_REPLACEABLE = false;

    /* Recursion level, starts at zero and is incremented for each entry
     * to writeObject.  Decremented before exit.
     */
    private int recursionDepth = 0;

    /* If true, use JDK 1.1 Externalizable data format. */
    boolean useDeprecatedExternalizableFormat = false;

    /* if true, override writeObject implementation with writeObjectOverride.
     */
    private boolean enableSubclassImplementation;

    /**
     * Lightweight hash table mapping objects to their corresponding wire
     * handles.
     */
    private static final class HandleTable {
        
        private int nextWireOffset;         // next free wire handle offset
        private int wireHashCapacity;       // when to grow hash table next
	private float loadFactor;           // factor for computing capacity
        private int[] wireHash2Handle;      // hash spine
        private int[] wireNextHandle;       // next hash entry
        private Object[] wireHandle2Object; // handle -> object mapping

        /**
         * Constructs handle table with given capacity and load factor.
         */
        public HandleTable(int initialCapacity, float loadFactor) {
	    this.loadFactor = loadFactor;
	    wireHash2Handle = new int[initialCapacity];
	    wireNextHandle = new int[initialCapacity];
	    wireHandle2Object = new Object[initialCapacity];
	    wireHashCapacity = (int) (initialCapacity * loadFactor);

            clear();
        }
        
        /**
         * Add an object to the handle table, assigning it the next available
         * wire handle offset.  Returns the value of the wire handle offset
         * assigned to the object.
         */
        public int assignWireOffset(Object obj) {
            if (nextWireOffset >= wireNextHandle.length)
                growEntries();
            if (nextWireOffset >= wireHashCapacity)
                growSpine();

            insert(obj, nextWireOffset);
            
            return nextWireOffset++;
        }
        
        /**
         * Insert new element into hash table.  This is only called when checks
         * have already been performed to ensure that the hash table is large
         * enough.
         */
        private void insert(Object obj, int offset) {
            int index = (System.identityHashCode(obj) & 0x7FFFFFFF) %
                wireHash2Handle.length;
            wireHandle2Object[offset] = obj;
            wireNextHandle[offset] = wireHash2Handle[index];
            wireHash2Handle[index] = offset;
        }

        /**
         * Double the size of the hash table spine.
         */
        private void growSpine() {
	    wireHash2Handle = new int[(wireHash2Handle.length << 1) + 1];
	    wireHashCapacity = (int) (wireHash2Handle.length * loadFactor);
	    Arrays.fill(wireHash2Handle, -1);
	    for (int i = 0; i < nextWireOffset; i++) {
		insert(wireHandle2Object[i], i);
	    }
        }

        /**
         * Double the number of hash table entry spaces.
         */
        private void growEntries() {
            int[] newWireNextHandle = new int[wireNextHandle.length * 2];
            System.arraycopy(wireNextHandle, 0, newWireNextHandle, 0,
                    nextWireOffset);
            wireNextHandle = newWireNextHandle;
            
            Object[] newWireHandle2Object =
                new Object[wireHandle2Object.length * 2];
            System.arraycopy(wireHandle2Object, 0, newWireHandle2Object, 0,
                    nextWireOffset);
            wireHandle2Object = newWireHandle2Object;
        }
        
        /**
         * Returns the wire handle offset associated with the given object, or
         * -1 if not found.
         */
        public int findWireOffset(Object obj) {
            int index = (System.identityHashCode(obj) & 0x7FFFFFFF) %
                wireHash2Handle.length;
            
            for (int handle = wireHash2Handle[index]; 
                    handle >= 0; 
                    handle = wireNextHandle[handle]) {
                if (wireHandle2Object[handle] == obj)
                    return handle;
            }
            return -1;
        }
        
        /**
         * Empties all mappings from this handle table, resets next available
         * wire handle offset.
         */
        public void clear() {
	    Arrays.fill(wireHash2Handle, -1);
	    Arrays.fill(wireHandle2Object, 0, nextWireOffset, null);
	    nextWireOffset = 0;
        }
        
        /**
         * Return number of entries currently in handle table.
         */
        public int size() {
            return nextWireOffset;
        }
    }

    /**
     * Lightweight identity hash table mapping objects to corresponding
     * replacement objects.
     */
    private static final class ReplaceTable {
	
	private HandleTable htab;      // maps object -> index
	private Object[] reps;         // maps index -> replacement object

	/**
	 * Creates new replacement table with given capacity and load factor.
	 */
	public ReplaceTable(int initialCapacity, float loadFactor) {
	    htab = new HandleTable(initialCapacity, loadFactor);
	    reps = new Object[initialCapacity];
	}

	/**
	 * Enters mapping from object to replacement object.
	 */
	public void assign(Object obj, Object rep) {
	    int index = htab.assignWireOffset(obj);
	    while (index >= reps.length) {
		grow();
	    }
	    reps[index] = rep;
	}

	/**
	 * Looks up and returns replacement for given object.  If no
	 * replacement is found, returns the lookup object itself.
	 */
	public Object lookup(Object obj) {
	    int index = htab.findWireOffset(obj);
	    return (index >= 0) ? reps[index] : obj;
	}

	/**
	 * Resets table to its initial (empty) state.
	 */
	public void clear() {
	    Arrays.fill(reps, 0, htab.size(), null);
	    htab.clear();
	}

	/**
	 * Returns the number of mappings currently in table.
	 */
	public int size() {
	    return htab.size();
	}
	
	/**
	 * Increases table capacity.
	 */
	private void grow() {
	    Object[] newReps = new Object[(reps.length << 1) + 1];
	    System.arraycopy(reps, 0, newReps, 0, reps.length);
	    reps = newReps;
	}
    }
    
    /**
     * Unsynchronized Stack.
     */
    private static final class Stack extends ArrayList {

	// use serialVersionUID from JDK 1.2.2 for interoperability
	private static final long serialVersionUID = -428799992207134975L;

	public void setSize(int newSize) {
	    if (newSize == 0) {
		clear();
	    } else {
		int len = size();

		for (int i = len - 1; i >= newSize; i--) {
		    remove(i);
		}
	    }
	}

	public Object push(Object item) {
	    add(item);
	    return item;
	}

	/**
	 * Removes the object at the top of this stack and returns that
	 * object as the value of this function.
	 *
	 * @return     The object at the top of this stack (the last item
	 *             of the <tt>Vector</tt> object).
	 * @exception  EmptyStackException  if this stack is empty.
	 */
	public Object pop() {
	    Object	obj;
	    int	len = size();

	    obj = peek();
	    remove(len - 1);

	    return obj;
	}

	/**
	 * Looks at the object at the top of this stack without removing it
	 * from the stack.
	 *
	 * @return     the object at the top of this stack (the last item
	 *             of the <tt>Vector</tt> object).
	 * @exception  EmptyStackException  if this stack is empty.
	 */
	public Object peek() {
	    int	len = size();

	    if (len == 0)
		throw new java.util.EmptyStackException();
	    return get(len - 1);
	}
    }
};
