/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Serialization's descriptor for classes.
 * It contains the name and serialVersionUID of the class.
 * <br>
 * The ObjectStreamClass for a specific class loaded in this Java VM can
 * be found/created using the lookup method.<p>
 * The algorithm to compute the SerialVersionUID is described in 
 * <a href="../../../guide/serialization/spec/class.doc4.html"> Object Serialization Specification, Section 4.4, Stream Unique Identifiers</a>.
 *
 * @author  Roger Riggs
 * @version 1.99 02/06/02
 * @see ObjectStreamField
 * @see <a href="../../../guide/serialization/spec/class.doc.html"> Object Serialization Specification, Section 4, Class Descriptors</a>
 * @since   JDK1.1
 */
public class ObjectStreamClass implements java.io.Serializable {

    /** 
     * Find the descriptor for a class that can be serialized. 
     * Creates an ObjectStreamClass instance if one does not exist 
     * yet for class. Null is returned if the specified class does not 
     * implement java.io.Serializable or java.io.Externalizable.
     *
     * @param cl class for which to get the descriptor
     * @return the class descriptor for the specified class
     */
    public static ObjectStreamClass lookup(Class cl)
    {
	ObjectStreamClass desc = lookupInternal(cl);
	if (desc.isSerializable() || desc.isExternalizable())
	    return desc;
	return null;
    }
    /*
     * Find the class descriptor for the specified class.
     * Package access only so it can be called from ObjectIn/OutStream.
     */
    static ObjectStreamClass lookupInternal(Class cl)
    {
	/* Synchronize on the hashtable so no two threads will do
	 * this at the same time.
	 */
	ObjectStreamClass desc = null;
	synchronized (descriptorFor) {
	    /* Find the matching descriptor if it already known */
	    desc = findDescriptorFor(cl);
	    if (desc == null) {
		/* Check if it's serializable */
		boolean serializable = Serializable.class.isAssignableFrom(cl);

		/* If the class is only Serializable,
		 * lookup the descriptor for the superclass.
		 */
		ObjectStreamClass superdesc = null;
		if (serializable) {
		    Class superclass = cl.getSuperclass();
		    if (superclass != null) 
			superdesc = lookup(superclass);
		}

		/* Check if its' externalizable.
		 * If it's Externalizable, clear the serializable flag.
		 * Only one or the other may be set in the protocol.
		 */
		boolean externalizable = false;
		if (serializable) {
		    externalizable = 
			((superdesc != null) && superdesc.isExternalizable()) ||
			Externalizable.class.isAssignableFrom(cl);
		    if (externalizable) {
			serializable = false;
		    }
		}

		/* Create a new version descriptor,
		 * it put itself in the known table.
		 */
		desc = new ObjectStreamClass(cl, superdesc,
			serializable, externalizable);
	    }
	}
	desc.init();
	return desc;
    }
    
    /**
     * The name of the class described by this descriptor.
     *
     * @return a <code>String</code> representing the fully qualified name of
     * the class
     */
    public String getName() {
	return name;
    }

    /**
     * Return the serialVersionUID for this class.
     * The serialVersionUID defines a set of classes all with the same name
     * that have evolved from a common root class and agree to be serialized
     * and deserialized using a common format.
     * NonSerializable classes have a serialVersionUID of 0L.
     *
     * @return the SUID of the class described by this descriptor
     */
    public long getSerialVersionUID() {
	return suid;
    }

    /**
     * Return the class in the local VM that this version is mapped to.
     * Null is returned if there is no corresponding local class.
     *
     * @return the <code>Class</code> instance that this descriptor represents
     */
    public Class forClass() {
	return ofClass;
    }

    /**
     * Return an array of the fields of this serializable class.
     *
     * @return an array containing an element for each persistent
     * field of this class. Returns an array of length zero if
     * there are no fields.
     * @since 1.2
     */
    public ObjectStreamField[] getFields() {
    	// Return a copy so the caller can't change the fields.
	if (fields.length > 0) {
	    ObjectStreamField[] dup = new ObjectStreamField[fields.length];
	    System.arraycopy(fields, 0, dup, 0, fields.length);
	    return dup;
	} else {
	    return fields;
	}
    }

    /* Avoid unnecessary allocations within package. */
    final ObjectStreamField[] getFieldsNoCopy() {
	return fields;
    }

    /**
     * Get the field of this class by name.
     *
     * @param name the name of the data field to look for
     * @return The ObjectStreamField object of the named field or null if there
     * is no such named field.
     */
    public ObjectStreamField getField(String name) {
	ObjectStreamField searchKey = 
	    ObjectStreamField.constructSearchKey(name, Byte.TYPE);

	int index = -1;
	if (numObjFields != fields.length) {
	    // perform binary search over primitive fields.
	    index = Arrays.binarySearch(fields, searchKey);
	}

	if (index < 0 && numObjFields > 0) {
	    // perform binary search over object fields. So passing false for
	    // the isPrimitive flag.
	    searchKey.setSearchKeyTypeString(false);
	    index = Arrays.binarySearch(fields, searchKey);
	}
	return (index < 0) ? null : fields[index];
    }


    /**
     * Get the field of this class by name and fieldType.
     *
     * @param name the name of the data field to look for
     * @param fieldType the type of the data field
     * @return The ObjectStreamField object of the named field, type
     *         or null if there is no such named field of fieldType.
     */
    ObjectStreamField getField(String name, Class fieldType) {
	ObjectStreamField searchKey = 
	    ObjectStreamField.constructSearchKey(name, fieldType);
	int index = Arrays.binarySearch(fields, searchKey);
	return (index < 0) ? null : fields[index];
    }

    /**
     * Return a string describing this ObjectStreamClass.
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append(name);
	sb.append(": static final long serialVersionUID = ");
	sb.append(Long.toString(suid));
	sb.append("L;");
	return sb.toString();
    }

    /*
     * Create a new ObjectStreamClass from a loaded class.
     * Don't call this directly, call lookup instead.
     */
    private ObjectStreamClass(final Class cl, ObjectStreamClass superdesc,
			      boolean serial, boolean extern)
    {
	ofClass = cl;		/* created from this class */

	if (Proxy.isProxyClass(cl)) {
	    forProxyClass = true;
	}

	name = cl.getName();
	superclass = superdesc;
	serializable = serial;
	if (!forProxyClass) {
	    // proxy classes are never externalizable
	    externalizable = extern;
	}

	/*
	 * Enable block data mode by default.  This is done to accommodate
	 * ObjectInputStream subclasses which may override readClassDescriptor()
	 * to obtain the class descriptor from ObjectStreamClass.lookup().
	 */
	hasExternalizableBlockData = true;

	/*
	 * Enter this class in the table of known descriptors.
	 * Otherwise, when the fields are read it may recurse
	 * trying to find the descriptor for itself.
	 */
	insertDescriptorFor(this);
	
	/*
	 * The remainder of initialization occurs in init(), which is called
	 * after the lock on the global class descriptor table has been
	 * released.
	 */
    }

    /*
     * Initialize class descriptor.  This method is only invoked on class
     * descriptors created via calls to lookupInternal().  This method is kept
     * separate from the ObjectStreamClass constructor so that lookupInternal
     * does not have to hold onto a global class descriptor table lock while the
     * class descriptor is being initialized (see bug 4165204).
     */
    private void init() {
	synchronized (lock) {
	    final Class cl = ofClass;

	    if (fields != null)	// already initialized
		return;

	    if (!serializable || externalizable || forProxyClass) {
		/*
		 * Proxy classes never have serializable fields,
		 * so short circuit expensive reflection lookup.
		 */
		fields = NO_FIELDS;
	    } else if (serializable) {
		/* Ask for permission to override field access checks.
		 */
		AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			/* Fill in the list of persistent fields.  If it is
			 * declared, use the declared serialPersistentFields.
			 * Otherwise, extract the fields from the class itself.
			 */
			try {
			    Field pf = 
				cl.getDeclaredField("serialPersistentFields");
			    pf.setAccessible(true);
			    ObjectStreamField[] f = 
				(ObjectStreamField[])pf.get(cl);
			    int mods = pf.getModifiers();
			    // field must be private for security reasons.
			    if (Modifier.isPrivate(mods) && 
				Modifier.isFinal(mods) && 
				Modifier.isStatic(mods)) {
				fields = f;
			    }
			} catch (NoSuchFieldException e) {
			    /* Thrown if  serialPersistentField is not a data
			     * member of the class.
			     */
			    fields = null;
			} catch (IllegalAccessException e) {
			    fields = null;
			} catch (IllegalArgumentException e) {
			    /* Thrown if the field serialPersistentField is not
			     * static.
			     */
			    fields = null;
			} catch (ClassCastException e) {
			    /* Thrown if a field serialPersistentField exists
			     * but it is not of type ObjectStreamField.
			     */
			    fields = null;
			}

			if (fields == null) {
			    /* Get all of the declared fields for this Class.
			     * setAccessible on all fields so they can be
			     * accessed later.  Create a temporary
			     * ObjectStreamField array to hold each non-static,
			     * non-transient field. Then copy the temporary
			     * array into an array of the correct size once the
			     * number of fields is known.
			     */
			    Field[] actualfields = cl.getDeclaredFields();

			    int numFields = 0;
			    ObjectStreamField[] tempFields = 
				new ObjectStreamField[actualfields.length];
			    for (int i = 0; i < actualfields.length; i++) {
				int modifiers = actualfields[i].getModifiers();
				if (!Modifier.isStatic(modifiers) &&
					!Modifier.isTransient(modifiers)) {
				    tempFields[numFields++] =
					new ObjectStreamField(actualfields[i]);
				}
			    }
			    fields = new ObjectStreamField[numFields];
			    System.arraycopy(tempFields, 0, fields, 0, 
				    numFields);

			} else {
			    /*
			     * For each declared persistent field, look for an
			     * actual reflected Field. If there is one, make
			     * sure it's the correct type and cache it in the
			     * ObjectStreamClass for that field.
			     */
			    for (int j = fields.length-1; j >= 0; j--) {
				try {
				    Field reflField = cl.getDeclaredField(
						fields[j].getName());
				    if (fields[j].getType() == 
					    reflField.getType()) 
				    {
					fields[j].setField(reflField);
				    } else {
					// TBD: Flag this as error?
				    }
				} catch (NoSuchFieldException e) {
				    // Nothing to do
				}
			    }
			}
			return null;
		    }
		});

		if (fields.length > 1)
		    Arrays.sort(fields);

		/* Set up field data for use while writing using the API api. */
		computeFieldInfo();
	    }

	    /* Get the serialVersionUID from the class.
	     * It uses the access override mechanism so make sure
	     * the field objects is only used here.
	     *
	     * NonSerializable classes have a serialVerisonUID of 0L.
	     */
	    if (isNonSerializable()) {
		suid = 0L;
	    } else {
		// Lookup special Serializable members using reflection.
		AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			if (forProxyClass) {
			    // proxy classes always have serialVersionUID of 0L
			    suid = 0L;
			} else {
			    try {
				Field f = 
				    cl.getDeclaredField("serialVersionUID");
				int mods = f.getModifiers();
				if (Modifier.isStatic(mods) && 
					Modifier.isFinal(mods)) 
				{
				    f.setAccessible(true);
				    suid = f.getLong(cl);
				} else {
				    suid = computeSerialVersionUID(cl);
				}
			    } catch (NoSuchFieldException ex) {
				suid = computeSerialVersionUID(cl);
			    } catch (IllegalAccessException ex) {
				suid = computeSerialVersionUID(cl);
			    }
			}

			/* check for class provided substitution methods, 
			 * writeReplace and readResolve. Methods can not
			 * be static.
			 */
			writeReplaceMethod = 
			    getDeclaredMethod("writeReplace", NULL_ARGS,
				    0, Modifier.STATIC);
			if (writeReplaceMethod == null && 
				superclass != null &&
				checkSuperMethodAccess(
				    superclass.writeReplaceMethod)) 
			{
			    writeReplaceMethod = superclass.writeReplaceMethod;
			}

			readResolveMethod = 
			    getDeclaredMethod("readResolve", NULL_ARGS,
				    0, Modifier.STATIC);
			if (readResolveMethod == null && superclass != null &&
				checkSuperMethodAccess(
				    superclass.readResolveMethod)) 
			{
			    readResolveMethod = superclass.readResolveMethod;
			}

			/* Cache lookup of writeObject and readObject for
			 * Serializable classes. (Do not lookup for
			 * Externalizable)
			 */
			if (serializable && !forProxyClass) { 
			    /* Work around compiler bug.  See declaration for
			     * more detail.
			     */
			    if (OOS_ARGS == null || OIS_ARGS == null) {
				initStaticMethodArgs();
			    }

			    writeObjectMethod = 
				getDeclaredMethod("writeObject", OOS_ARGS,
					Modifier.PRIVATE, Modifier.STATIC);
			    if (writeObjectMethod != null) {
				hasWriteObjectMethod = true;
			    }
			    readObjectMethod = 
				getDeclaredMethod("readObject", OIS_ARGS,
					Modifier.PRIVATE, Modifier.STATIC);
			}
			return null;
		    }
		});
	    }
	}
    }

    /*
     * Create an empty ObjectStreamClass for a class about to be read.
     * This is separate from read so ObjectInputStream can assign the
     * wire handle early, before any nested ObjectStreamClass might
     * be read.
     */
    ObjectStreamClass(String n, long s) {
	name = n;
	suid = s;
	superclass = null;
    }

    /* Validate the compatibility of the stream class descriptor and 
     * the specified local class.
     *
     * @exception InvalidClassException if stream and local class are 
     *                                  not compatible.
     */
    private void validateLocalClass(Class localCl) throws InvalidClassException {
	if (localClassDesc == null)
	    throw new InvalidClassException(localCl.getName(), 
					    "Local class not compatible");

	if (suid != localClassDesc.suid) {

	    /* Check for exceptional cases that allow mismatched suid. */

	    /* Allow adding Serializable or Externalizable
	     * to a later release of the class. 
	     */
	    boolean addedSerialOrExtern = 
		isNonSerializable() || localClassDesc.isNonSerializable();

	    /* Disregard the suid of an array when name and localCl.Name differ. 
	     * If resolveClass() returns an array with a different package 
	     * name, the serialVersionUIDs will not match since the fully
	     * qualified array class is used in the
	     * computation of the array's serialVersionUID. There is
	     * no way to set a permanent serialVersionUID for an array type.
	     */
	    boolean arraySUID = (localCl.isArray() && ! localCl.getName().equals(name));

	    if (! arraySUID && ! addedSerialOrExtern ) {
		throw new InvalidClassException(localCl.getName(), 
		    "Local class not compatible:" + 
		    " stream classdesc serialVersionUID=" + suid +
		    " local class serialVersionUID=" + localClassDesc.suid);
	    }
	    
	}

	/* compare the class names, stripping off package names. */
	if (! compareClassNames(name, localCl.getName(), '.'))
	    throw new InvalidClassException(localCl.getName(),
			 "Incompatible local class name. " +
	        	 "Expected class name compatible with " + 
			 name);

	/*
	 * Test that both implement either serializable or externalizable.
	 */
	if ((serializable && localClassDesc.externalizable) ||
	    (externalizable && localClassDesc.serializable))
	    throw new InvalidClassException(localCl.getName(),
		    "Serializable is incompatible with Externalizable");

    }

    /*
     * Set the local class that this stream class descriptor matches.
     * The base class name and serialization version id must match if
     * both classes are serializable.
     * Fill in the reflected Fields that will be used for reading.
     */
    void setClass(Class cl) throws InvalidClassException {

	/* Allow no local class implementation. Must be able to 
	 * skip objects in stream introduced by class evolution.
	 */
	if (cl == null) {
	    localClassDesc = null;
	    ofClass = null;
	    computeFieldInfo();
	    return;
	}

	localClassDesc = lookupInternal(cl);
	validateLocalClass(cl);

	/* Disable instance deserialization when one class is serializable 
	 * and the other is not or if both the classes are neither serializable
	 * nor externalizable. 
	 */
	if ((serializable != localClassDesc.serializable) ||
	    (externalizable != localClassDesc.externalizable) || 
	    (!serializable && !externalizable)) {

	    /* Delay signaling InvalidClassException until trying 
             * to deserialize an instance of this class. Allows
	     * a previously nonSerialized class descriptor that was written 
             * into the stream to be made Serializable
	     * or Externalizable, in a later release.
	     */
	    disableInstanceDeserialization = true;
	    ofClass = cl;
	    return;
	}

	/* Set up the reflected Fields in the class where the value of each 
	 * field in this descriptor should be stored.
	 * Each field in this ObjectStreamClass (the source) is located (by 
	 * name) in the ObjectStreamClass of the class(the destination).
	 * In the usual (non-versioned case) the field is in both
	 * descriptors and the types match, so the reflected Field is copied.
	 * If the type does not match, a InvalidClass exception is thrown.
	 * If the field is not present in the class, the reflected Field 
	 * remains null so the field will be read but discarded.
	 * If extra fields are present in the class they are ignored. Their
	 * values will be set to the default value by the object allocator.
	 * Both the src and dest field list are sorted by type and name.
	 */

	ObjectStreamField[] destfield = 
	    (ObjectStreamField[])localClassDesc.fields;
	ObjectStreamField[] srcfield = 
	    (ObjectStreamField[])fields;

	int j = 0;
    nextsrc:
	for (int i = 0; i < srcfield.length; i++ ) {
	    /* Find this field in the dest*/
	    for (int k = j; k < destfield.length; k++) {
	      if (srcfield[i].getName().equals(destfield[k].getName())) {
		  /* found match */
		  if (srcfield[i].isPrimitive() && 
		      !srcfield[i].typeEquals(destfield[k])) {
		      throw new InvalidClassException(cl.getName(),
						  "The type of field " +
						       destfield[i].getName() +
						       " of class " + name +
						       " is incompatible.");
		  }

		  /* Skip over any fields in the dest that are not in the src */
 		  j = k; 
		  
		  srcfield[i].setField(destfield[j].getField());
		  // go on to the next source field
		  continue nextsrc;
	      }
	    }
	}

	/* Remember the class this represents */
	ofClass = cl;

	/* Set up field data for use while reading from the input stream. */
	computeFieldInfo();

	/* get the cache of these methods from the local class 
	 * implementation. 
	 */
	readObjectMethod = localClassDesc.readObjectMethod;
	readResolveMethod = localClassDesc.readResolveMethod;
    }

    /* Compare the base class names of streamName and localName.
     * 
     * @return  Return true iff the base class name compare.
     * @parameter streamName	Fully qualified class name.
     * @parameter localName	Fully qualified class name.
     * @parameter pkgSeparator	class names use either '.' or '/'.
     * 
     * Only compare base class name to allow package renaming.
     */
    static boolean compareClassNames(String streamName,
					 String localName,
					 char pkgSeparator) {
	/* compare the class names, stripping off package names. */
	int streamNameIndex = streamName.lastIndexOf(pkgSeparator);
	if (streamNameIndex < 0) 
	    streamNameIndex = 0;

	int localNameIndex = localName.lastIndexOf(pkgSeparator);
	if (localNameIndex < 0)
	    localNameIndex = 0;

	return streamName.regionMatches(false, streamNameIndex, 
					localName, localNameIndex,
					streamName.length() - streamNameIndex);
    }

    /*
     * Compare the types of two class descriptors.
     * They match if they have the same class name and suid
     */
    boolean typeEquals(ObjectStreamClass other) {
	return (suid == other.suid) &&
	    compareClassNames(name, other.name, '.');
    }
    
    /*
     * Return the superclass descriptor of this descriptor.
     */
    void setSuperclass(ObjectStreamClass s) {
	superclass = s;
    }

    /*
     * Return the superclass descriptor of this descriptor.
     */
    ObjectStreamClass getSuperclass() {
	return superclass;
    }
    
    /*
     * Return whether the class has a writeObject method
     */
    boolean hasWriteObject() {
	return hasWriteObjectMethod;
    }
    
    /*
     * Return true if all instances of 'this' Externalizable class 
     * are written in block-data mode from the stream that 'this' was read
     * from. <p>
     *
     * In JDK 1.1, all Externalizable instances are not written 
     * in block-data mode.
     * In the Java 2 SDK, all Externalizable instances, by default, are written
     * in block-data mode and the Externalizable instance is terminated with
     * tag TC_ENDBLOCKDATA. Change enabled the ability to skip Externalizable 
     * instances.
     *
     * IMPLEMENTATION NOTE:
     *   This should have been a mode maintained per stream; however,
     *   for compatibility reasons, it was only possible to record
     *   this change per class. All Externalizable classes within
     *   a given stream should either have this mode enabled or 
     *   disabled. This is enforced by not allowing the PROTOCOL_VERSION
     *   of a stream to he changed after any objects have been written.
     *
     * @see ObjectOuputStream#useProtocolVersion
     * @see ObjectStreamConstants#PROTOCOL_VERSION_1
     * @see ObjectStreamConstants#PROTOCOL_VERSION_2
     *
     * @since 1.2
     */
    boolean hasExternalizableBlockDataMode() {
	return hasExternalizableBlockData;
    }

    /*
     * Return the ObjectStreamClass of the local class this one is based on.
     */
    ObjectStreamClass localClassDescriptor() {
	return localClassDesc;
    }
    
    /*
     * Get the Serializability of the class.
     */
    boolean isSerializable() {
	return serializable;
    }

    /*
     * Get the externalizability of the class.
     */
    boolean isExternalizable() {
	return externalizable;
    }

    boolean isNonSerializable() {
	return ! (externalizable || serializable);
    }

    /*
     * Calculate the size of the array needed to store primitive data and the
     * number of object references to read when reading from the input 
     * stream.
     */
    private void computeFieldInfo() {
	numPrimBytes = 0;
	numObjFields = 0;

	for (int i = 0; i < fields.length; i++ ) {
	    switch (fields[i].getTypeCode()) {
	    case 'B':
	    case 'Z':
	    	fields[i].setOffset(numPrimBytes);
	    	numPrimBytes += 1;
	    	break;
	    case 'C':
	    case 'S': 
		fields[i].setOffset(numPrimBytes);
	    	numPrimBytes += 2;
	    	break;

	    case 'I':
	    case 'F': 
	    	fields[i].setOffset(numPrimBytes);
	    	numPrimBytes += 4;
	    	break;
	    case 'J':
	    case 'D' :
		fields[i].setOffset(numPrimBytes);
	    	numPrimBytes += 8;
	    	break;
	    
	    case 'L':
	    case '[':
	    	fields[i].setOffset(numObjFields);
	    	numObjFields += 1;
	    	break;
	    }
	}
	
	/* if this descriptor is bound to a class, obtain field IDs and
	 * associated info.
	 */
	if (ofClass != null) {
	    int numPrimFields = fields.length - numObjFields;

	    if (numPrimFields > 0) {
		primFieldIDs = new long[numPrimFields];
		primFieldTypecodes = new char[numPrimFields];
	    }
	    if (numObjFields > 0) {
		objFieldIDs = new long[numObjFields];
		objFieldTypes = new Class[numObjFields];
	    }
	    
	    getFieldIDs(fields, primFieldIDs, objFieldIDs);
	    
	    int oi = 0, pi = 0;
	    try {
		for (int i = 0; i < fields.length; i++) {
		    char tc = fields[i].getTypeCode();
		    switch (tc) {
			case 'L':
			case '[':
			    {
				Field f = fields[i].getField();
				objFieldTypes[oi++] = (f != null) ? 
				    f.getType() : null;
			    }
			    break;

			default:
			    primFieldTypecodes[pi++] = tc;
			    break;
		    }
		}
	    } catch (ArrayIndexOutOfBoundsException e) {
		throw new InternalError("field count mismatch for class " +
			ofClass.getName());
	    }
	    if (oi != numObjFields || pi != numPrimFields) {
		throw new InternalError("field count mismatch for class " +
			ofClass.getName());
	    }
	}
    }
    
    /*
     * Compute a hash for the specified class.  Incrementally add
     * items to the hash accumulating in the digest stream.
     * Fold the hash into a long.  Use the SHA secure hash function.
     */
    private static long computeSerialVersionUID(Class cl) {
	ByteArrayOutputStream devnull = new ByteArrayOutputStream(512);

	long h = 0;
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA");
	    DigestOutputStream mdo = new DigestOutputStream(devnull, md);
	    DataOutputStream data = new DataOutputStream(mdo);


	    data.writeUTF(cl.getName());
	    
	    int classaccess = cl.getModifiers();
	    classaccess &= (Modifier.PUBLIC | Modifier.FINAL |
			    Modifier.INTERFACE | Modifier.ABSTRACT);

	    /* Workaround for javac bug that only set ABSTRACT for
	     * interfaces if the interface had some methods.
	     * The ABSTRACT bit reflects that the number of methods > 0.
	     * This is required so correct hashes can be computed
	     * for existing class files.
	     * Previously this hack was previously present in the VM.
	     */
	    Method[] method = cl.getDeclaredMethods();
	    if ((classaccess & Modifier.INTERFACE) != 0) {
		classaccess &= (~Modifier.ABSTRACT);
		if (method.length > 0) {
		    classaccess |= Modifier.ABSTRACT;
		}
	    }

	    data.writeInt(classaccess);

	    /* 
	     * Get the list of interfaces supported,
	     * Accumulate their names in Lexical order
	     * and add them to the hash
	     */
	    if (!cl.isArray()) {
		/* In 1.2fcs, getInterfaces() was modified to return
		 * {java.lang.Cloneable, java.io.Serializable} when
		 * called on array classes.  These values would upset
		 * the computation of the hash, so we explicitly omit
		 * them from its computation.
		 */
		Class interfaces[] = cl.getInterfaces();
		Arrays.sort(interfaces, compareClassByName);
		
		for (int i = 0; i < interfaces.length; i++) {
		    data.writeUTF(interfaces[i].getName());
		}
	    }

	    /* Sort the field names to get a deterministic order */
	    Field[] field = cl.getDeclaredFields();
	    Arrays.sort(field, compareMemberByName);

	    for (int i = 0; i < field.length; i++) {
		Field f = field[i];

		/* Include in the hash all fields except those that are
		 * private transient and private static.
		 */
		int m = f.getModifiers();
		if (Modifier.isPrivate(m) && 
		    (Modifier.isTransient(m) || Modifier.isStatic(m)))
		    continue;

		data.writeUTF(f.getName());
		data.writeInt(m);
		data.writeUTF(getSignature(f.getType()));
	    }

	    if (hasStaticInitializer(cl)) {
		data.writeUTF("<clinit>");
		data.writeInt(Modifier.STATIC); // TBD: what modifiers does it have
		data.writeUTF("()V");
	    }

	    /*
	     * Get the list of constructors including name and signature
	     * Sort lexically, add all except the private constructors
	     * to the hash with their access flags
	     */

	    MethodSignature[] constructors =
		MethodSignature.removePrivateAndSort(cl.getDeclaredConstructors());
	    for (int i = 0; i < constructors.length; i++) {
		MethodSignature c = constructors[i];
		String mname = "<init>";
		String desc = c.signature;
		desc = desc.replace('/', '.');
		data.writeUTF(mname);
		data.writeInt(c.member.getModifiers());
		data.writeUTF(desc);
	    }

	    /* Include in the hash all methods except those that are
	     * private transient and private static.
	     */
	    MethodSignature[] methods =
		MethodSignature.removePrivateAndSort(method);
	    for (int i = 0; i < methods.length; i++ ) {
		MethodSignature m = methods[i];	
		String desc = m.signature;
		desc = desc.replace('/', '.');
		data.writeUTF(m.member.getName());
		data.writeInt(m.member.getModifiers());
		data.writeUTF(desc);
	    }

	    /* Compute the hash value for this class.
	     * Use only the first 64 bits of the hash.
	     */
	    data.flush();
	    byte hasharray[] = md.digest();
	    for (int i = 0; i < Math.min(8, hasharray.length); i++) {
		h += (long)(hasharray[i] & 255) << (i * 8);
	    }
	} catch (IOException ignore) {
	    /* can't happen, but be deterministic anyway. */
	    h = -1;
	} catch (NoSuchAlgorithmException complain) {
	    throw new SecurityException(complain.getMessage());
	}
	return h;
    }


    /**
      * Compute the JVM signature for the class.
      */
    static String getSignature(Class clazz) {
	String type = null;
	if (clazz.isArray()) {
	    Class cl = clazz;
	    int dimensions = 0;
	    while (cl.isArray()) {
		dimensions++;
		cl = cl.getComponentType();
	    }
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < dimensions; i++) {
		sb.append("[");
	    }
	    sb.append(getSignature(cl));
	    type = sb.toString();
	} else if (clazz.isPrimitive()) {
	    if (clazz == Integer.TYPE) {
		type = "I";
	    } else if (clazz == Byte.TYPE) {
		type = "B";
	    } else if (clazz == Long.TYPE) {
		type = "J";
	    } else if (clazz == Float.TYPE) {
		type = "F";
	    } else if (clazz == Double.TYPE) {
		type = "D";
	    } else if (clazz == Short.TYPE) {
		type = "S";
	    } else if (clazz == Character.TYPE) {
		type = "C";
	    } else if (clazz == Boolean.TYPE) {
		type = "Z";
	    } else if (clazz == Void.TYPE) {
		type = "V";
	    }
	} else {
	    type = "L" + clazz.getName().replace('.', '/') + ";";
	}
	return type;
    }

    /*
     * Compute the JVM method descriptor for the method.
     */
    static String getSignature(Method meth) {
	StringBuffer sb = new StringBuffer();

	sb.append("(");

	Class[] params = meth.getParameterTypes(); // avoid clone
	for (int j = 0; j < params.length; j++) {
	    sb.append(getSignature(params[j]));
	}
	sb.append(")");
	sb.append(getSignature(meth.getReturnType()));
	return sb.toString();
    }

    /*
     * Compute the JVM constructor descriptor for the constructor.
     */
    static String getSignature(Constructor cons) {
	StringBuffer sb = new StringBuffer();

	sb.append("(");

	Class[] params = cons.getParameterTypes(); // avoid clone
	for (int j = 0; j < params.length; j++) {
	    sb.append(getSignature(params[j]));
	}
	sb.append(")V");
	return sb.toString();
    }


    /*
     * locate the ObjectStreamClass for this class and write it to the stream.
     *
     * @serialData
     *  <code>primitive UTF-8 String</code>  Qalified class name.
     *  <code>long</code>  Serial version unique identifier for compatible classes.
     *	<code>byte</code>. Mask with <code>java.io.ObjectStreamConstants.SC_*</code>.<br>
     *	<code>short</code>. Number of Serializable fields to follow. If 0, no more data.
     *  <code>list of Serializable Field descriptors</code>. Descriptors for Primitive 
     *       typed fields are written first sorted by field name 
     *       followed by descriptors for the object typed fields sorted 
     *       by field name. The names are sorted using String.compareTo.
     *
     *  A Serializable field consists of the following data:
     *   <code>byte</code>  TypeCode of field. See ObjectStreamField.getTypeCode().
     *   <code>primitive UTF-8 encoded String</code>  Unqualified name of field.
     *   <code>String</code>   Qualified class name. 
     */
    void write(ObjectOutputStream s) throws IOException {
	
	/* write the flag indicating that this class has write/read object methods */
	int flags = 0;
	if (hasWriteObjectMethod)
	    flags |= ObjectStreamConstants.SC_WRITE_METHOD;
	if (serializable)
	    flags |= ObjectStreamConstants.SC_SERIALIZABLE;
	if (externalizable) {
	    flags |=  ObjectStreamConstants.SC_EXTERNALIZABLE;

	    /* Enabling the SC_BLOCK_DATA flag indicates PROTCOL_VERSION_2.*/
	    if (! s.useDeprecatedExternalizableFormat)
		flags |= ObjectStreamConstants.SC_BLOCK_DATA;
        }
	s.writeByte(flags);
	
	// If there are no fields, write a null and return
	if (fields == null) {
	    s.writeShort(0);
	    return;
	}

	/* write the total number of fields */
	s.writeShort(fields.length);
	
	/* Write out the descriptors of the primitive fields Each
	 * descriptor consists of the UTF fieldname, a short for the
	 * access modes, and the first byte of the signature byte.
	 * For the object types, ('[' and 'L'), a reference to the
	 * type of the field follows.
	 */
	for (int i = 0; i < fields.length; i++ ) {
	    ObjectStreamField f = fields[i];
	    s.writeByte(f.getTypeCode());
	    s.writeUTF(f.getName());
	    if (!f.isPrimitive()) {
		s.writeTypeString(f.getTypeString());
	    }
	}
    }

    /*
     * Read the version descriptor from the stream.
     * Write the count of field descriptors
     * for each descriptor write the first character of its type,
     * the name of the field.
     * If the type is for an object either array or object, write
     * the type typedescriptor for the type
     */
    void read(ObjectInputStream s) throws IOException, ClassNotFoundException {
	
	/* read flags and determine whether the source class had
         * write/read methods.
	 */
	byte flags = s.readByte();
	serializable = (flags & ObjectStreamConstants.SC_SERIALIZABLE) != 0;
	externalizable = (flags & ObjectStreamConstants.SC_EXTERNALIZABLE) != 0;
	hasWriteObjectMethod = serializable ?
	    (flags & ObjectStreamConstants.SC_WRITE_METHOD) != 0 :
	    false;
	hasExternalizableBlockData = externalizable ? 
	    (flags & ObjectStreamConstants.SC_BLOCK_DATA) != 0 :
	    false;
	                 

	/* Read the number of fields described.
	 * For each field read the type byte, the name.
	 */    
	int count = s.readShort();
	fields = new ObjectStreamField[count];

	/* disable replacement of String objects read
	 * by ObjectStreamClass. */
	boolean prevEnableResolve = s.enableResolve;
	s.enableResolve = false;
	try {
	    for (int i = 0; i < count; i++ ) {
		char type = (char)s.readByte();
		String name = s.readUTF();
		String ftype = null;
		switch (type) {
		    case '[':
		    case 'L':
			ftype = (String) s.readObject();
			break;
			
		    case 'Z':
		    case 'B':
		    case 'C':
		    case 'S':
		    case 'I':
		    case 'J':
		    case 'F':
		    case 'D':
			break;
			
		    default:
			throw new StreamCorruptedException("illegal field " +
				"descriptor typecode: " + type);
		}
		fields[i] = new ObjectStreamField(name, type, null, ftype);
	    }
	} finally {
	    s.enableResolve = prevEnableResolve;
	}
    }

    /*
     * Perform a light copy of the given class descriptor into this class
     * descriptor: only copy members that would be copied if the descriptor were
     * serialized to a stream and then read back out again.  Used for class
     * descriptor cacheing.
     * 
     * Note that this method assumes it is being called on a newly-constructed
     * ObjectStreamClass, so it doesn't bother to set all fields (e.g.,
     * superclass, ofClass, numPrimBytes, etc.) which have already been
     * initialized to their proper default values.  Most of these are filled in
     * when setClass() is called on this object.
     */
    void lightCopy(ObjectStreamClass desc) {
	// copy essential members
	name = desc.name;
	serializable = desc.serializable;
	externalizable = desc.externalizable;
	fields = new ObjectStreamField[desc.fields.length];
	for (int i = 0; i < fields.length; i++) {
	    ObjectStreamField cf = desc.fields[i];
	    fields[i] = new ObjectStreamField(cf.getName(), 
		    cf.getTypeCode(), null, cf.getTypeString());
	}
	suid = desc.suid;
	hasWriteObjectMethod = desc.hasWriteObjectMethod;
	hasExternalizableBlockData = desc.hasExternalizableBlockData;
    }

    /**
     * Initialize the contents of an incoming class descriptor for a
     * proxy class.
     *
     * This method performs the equivalent of the read() method for proxy
     * classes, for which the equivalent data is implicit rather than in
     * the stream format for the class descriptor, and sets the name,
     * which could not be determined when the descriptor object was
     * constructed.  Thus, this method prepares an incoming proxy class
     * descriptor for the setClass() method.
     */
    void initProxyClassDesc(Class cl) {
	forProxyClass = true;
	if (cl != null) {
	    name = cl.getName();
	}
	serializable = true;
	externalizable = false;
	fields = ObjectStreamClass.NO_FIELDS;
	hasWriteObjectMethod = false;
	hasExternalizableBlockData = true;
    }	

    /* To accomodate nonSerializable classes written into a stream,
     * this check must be delayed until an instance is deserialized.
     */
    void verifyInstanceDeserialization() throws InvalidClassException {
	if (disableInstanceDeserialization) {
	    String name = (serializable || externalizable) ? 
  		              localClassDesc.getName() : getName();
	    String stype = (serializable || localClassDesc.serializable) ? 
			  "Serializable" : 
			  (externalizable || localClassDesc.externalizable) ?
			  "Externalizable" : "Serializable or Externalizable";
	    throw new InvalidClassException(name, "is not " + stype);
	}
    }

    /*
     * Cache of Class -> ClassDescriptor Mappings.
     */
    static private ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];

    /*
     * findDescriptorFor a Class.  This looks in the cache for a
     * mapping from Class -> ObjectStreamClass mappings.  The hashCode
     * of the Class is used for the lookup since the Class is the key.
     * The entries are extended from java.lang.ref.SoftReference so the
     * gc will be able to free them if needed.
     */
    private static ObjectStreamClass findDescriptorFor(Class cl) {

	int hash = cl.hashCode();
	int index = (hash & 0x7FFFFFFF) % descriptorFor.length;
	ObjectStreamClassEntry e;
	ObjectStreamClassEntry prev;
	
	/* Free any initial entries whose refs have been cleared */
	while ((e = descriptorFor[index]) != null && e.get() == null) {
	    descriptorFor[index] = e.next;
	}

	/* Traverse the chain looking for a descriptor with ofClass == cl.
	 * unlink entries that are unresolved.
	 */
	prev = e;
	while (e != null ) {
	    ObjectStreamClass desc = (ObjectStreamClass)(e.get());
	    if (desc == null) {
		// This entry has been cleared,  unlink it
		prev.next = e.next;
	    } else {
		if (desc.ofClass == cl)
		    return desc;
		prev = e;
	    }
	    e = e.next;
	}
	return null;
    }

    /*
     * insertDescriptorFor a Class -> ObjectStreamClass mapping.
     */
    private static void insertDescriptorFor(ObjectStreamClass desc) {
	// Make sure not already present
	if (findDescriptorFor(desc.ofClass) != null) {
	    return;
	}

	int hash = desc.ofClass.hashCode();
	int index = (hash & 0x7FFFFFFF) % descriptorFor.length;
	ObjectStreamClassEntry e = new ObjectStreamClassEntry(desc);
	e.next = descriptorFor[index];
       	descriptorFor[index] = e;
    }

    /*
     * Initialize native code.
     */
    static {
	initNative();
    }

    /*
     * The name of this descriptor
     */
    private String name;
    
    /*
     * The descriptor of the supertype.
     */
    ObjectStreamClass superclass;

    /*
     * Flags for Serializable and Externalizable.
     */
    private boolean serializable;
    private boolean externalizable;
    
    /*
     * Array of persistent fields of this class, sorted by
     * type and name.
     */
    private ObjectStreamField[] fields;
    
    /*
     * Class that is a descriptor for in this virtual machine.
     */
    private Class ofClass;

    /*
     * True if descriptor for a proxy class.
     */
    boolean forProxyClass;

    /* 
     * SerialVersionUID for this class.
     */
    private long suid;
    
    /*
     * The total number of bytes of primitive fields.
     * The total number of object fields.
     */
    int numPrimBytes;
    int numObjFields;

    /* True if this class has/had a writeObject method */
    private boolean hasWriteObjectMethod;

    /* In JDK 1.1, external data was not written in block mode.
     * As of the Java 2 SDK, external data is written in block data mode. This
     * flag enables the Java 2 SDK to be able to read JDK 1.1 written external data.
     *
     * @since 1.2
     */
    private boolean hasExternalizableBlockData;
    Method writeObjectMethod;
    Method readObjectMethod;
    Method readResolveMethod;
    Method writeReplaceMethod;

    /*
     * ObjectStreamClass that this one was built from.
     */
    private ObjectStreamClass localClassDesc;
    
    /* Indicates that stream and local class are not both
     * serializable. No instances of this class can be deserialized.
     */
    private boolean disableInstanceDeserialization = false;

    /* place to temporarily hold ClassNotFoundException thrown by
     * ObjectInputStream.resolveClass().
     */
    ClassNotFoundException pendingException;

    /* Class objects corresponding to this class descriptor and its super-class
     * descriptors.  ancestors[0] is the class which this descriptor maps to.
     * ancestors[1..n] (where n is the number of super-class descriptors above
     * this descriptor) contain the classes that the corresponding super-class
     * descriptor maps to (or null if it has no equivalent in this vm).
     * Finally, ancestors[n + 1] contains the uppermost ancestor, which is the
     * lowest non-serializable superclass.  This array is set by
     * ObjectInputStream.inputObject() for Serializable, but not Externalizable,
     * class descriptors.
     */
    Class[] ancestors;

    /* 
     * IDs and typecodes (e.g., 'I', 'Z') for the primitive fields of the class
     * this descriptor represents.
     */
    long[] primFieldIDs;
    char[] primFieldTypecodes;
    
    /*
     * IDs and class types for the object fields of the class this descriptor
     * represents.
     */
    long[] objFieldIDs;
    Class[] objFieldTypes;

    /* Internal lock object. */
    private Object lock = new Object();

    /*
     * Initialize native code.  Should be called once, at class initialization.
     */
    private static native void initNative();
    
    /*
     * Get the field IDs associated with the given fields.  The field IDs are
     * later passed as arguments to the various ObjectInputStream and
     * ObjectOutputStream native methods for setting and getting field values.
     */
    private static native void getFieldIDs(ObjectStreamField[] fields,
	    long[] primFieldIDs, long[] objFieldIDs);

    /* Find out if the class has a static class initializer <clinit> */
    private static native boolean hasStaticInitializer(Class cl);

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = -6120832682080437368L;

    /**
     * Set serialPersistentFields of a Serializable class to this value to 
     * denote that the class has no Serializable fields. 
     */
    public static final ObjectStreamField[] NO_FIELDS = 
	new ObjectStreamField[0];

    /**
     * Class ObjectStreamClass is special cased within the 
     * Serialization Stream Protocol. 
     *
     * An ObjectStreamClass is written intially into an ObjectOutputStream 
     * in the following format:
     * <pre>
     *      TC_CLASSDESC className, serialVersionUID, flags, 
     *                   length, list of field descriptions.
     *
     * FIELDNAME        TYPES
     *                  DESCRIPTION
     * --------------------------------------
     * className        primitive data String
     *                  Fully qualified class name.
     *
     * serialVersionUID long
     *                  Stream Unique Identifier for compatible classes
     *                  with same base class name.
     *
     * flags            byte
     *                  Attribute bit fields defined in 
     *                  <code>java.io.ObjectStreamConstants.SC_*</code>.
     *
     * length           short
     *                  The number of field descriptions to follow.
     *
     * fieldDescription (byte, primitive data String, String Object)
     *                  A pseudo-externalized format of class
     *                  <code>java.io.ObjectStreamField</code>.
     *                  Consists of typeCode, fieldName, and,
     *                  if a nonPrimitive typecode, a fully qualified
     *                  class name. See <code>Class.getName</code> method 
     *                  for the typecode byte encodings.
     * </pre>
     * The first time the class descriptor
     * is written into the stream, a new handle is generated.
     * Future references to the class descriptor are
     * written as references to the initial class descriptor instance.
     *
     * @see java.io.ObjectOutputStream#writeUTF(java.lang.String)
     */
    private static final ObjectStreamField[] serialPersistentFields = 
	NO_FIELDS;

    /*
     * Entries held in the Cache of known ObjectStreamClass objects.
     * Entries are chained together with the same hash value (modulo array size).
     */
    private static class ObjectStreamClassEntry extends java.lang.ref.SoftReference
    {
	ObjectStreamClassEntry(ObjectStreamClass c) {
	    super(c);
	}
	ObjectStreamClassEntry next;
    }

    /*
     * Comparator object for Classes and Interfaces
     */
    private static Comparator compareClassByName =
    	new CompareClassByName();

    private static class CompareClassByName implements Comparator {
	public int compare(Object o1, Object o2) {
	    Class c1 = (Class)o1;
	    Class c2 = (Class)o2;
	    return (c1.getName()).compareTo(c2.getName());
	}
    }

    /*
     * Comparator object for Members, Fields, and Methods
     */
    private static Comparator compareMemberByName =
    	new CompareMemberByName();

    private static class CompareMemberByName implements Comparator {
	public int compare(Object o1, Object o2) {
	    String s1 = ((Member)o1).getName();
	    String s2 = ((Member)o2).getName();

	    if (o1 instanceof Method) {
		s1 += getSignature((Method)o1);
		s2 += getSignature((Method)o2);
	    } else if (o1 instanceof Constructor) {
		s1 += getSignature((Constructor)o1);
		s2 += getSignature((Constructor)o2);
	    }
	    return s1.compareTo(s2);
	}
    }

    /* It is expensive to recompute a method or constructor signature
       many times, so compute it only once using this data structure. */
    private static class MethodSignature implements Comparator {
	Member member;
	String signature;      // cached parameter signature

	/* Given an array of Method or Constructor members,
	   return a sorted array of the non-private members.*/
	/* A better implementation would be to implement the returned data
	   structure as an insertion sorted link list.*/
	static MethodSignature[] removePrivateAndSort(Member[] m) {
	    int numNonPrivate = 0;
	    for (int i = 0; i < m.length; i++) {
		if (! Modifier.isPrivate(m[i].getModifiers())) {
		    numNonPrivate++;
		}
	    }
	    MethodSignature[] cm = new MethodSignature[numNonPrivate];
	    int cmi = 0;
	    for (int i = 0; i < m.length; i++) {
		if (! Modifier.isPrivate(m[i].getModifiers())) {
		    cm[cmi] = new MethodSignature(m[i]);
		    cmi++;
		}
	    }
	    if (cmi > 0)
		Arrays.sort(cm, cm[0]);
	    return cm;
	}

	/* Assumes that o1 and o2 are either both methods
	   or both constructors.*/
	public int compare(Object o1, Object o2) {
	    /* Arrays.sort calls compare when o1 and o2 are equal.*/
	    if (o1 == o2)
		return 0;
	    
	    MethodSignature c1 = (MethodSignature)o1;
	    MethodSignature c2 = (MethodSignature)o2;

	    int result;
	    if (isConstructor()) {
		result = c1.signature.compareTo(c2.signature);
	    } else { // is a Method.
		result = c1.member.getName().compareTo(c2.member.getName());
		if (result == 0)
		    result = c1.signature.compareTo(c2.signature);
	    }
	    return result;
	}

	private boolean isConstructor() {
	    return member instanceof Constructor;
	}

	private MethodSignature(Member m) {
	    member = m;
	    if (isConstructor()) {
		signature = ObjectStreamClass.getSignature((Constructor)m);
	    } else {
		signature = ObjectStreamClass.getSignature((Method)m);
	    }
	}
    }

    boolean isResolvable() {
	return readResolveMethod != null;
    }

    boolean isReplaceable() {
	return writeReplaceMethod != null;
    }

    static Object invokeMethod(Method method, Object obj, Object[] args)
	throws IOException
    {
	Object returnValue = null;
	try {
	    returnValue = method.invoke(obj, args);
	} catch (java.lang.reflect.InvocationTargetException e) {
	    Throwable t = e.getTargetException();
	    if (t instanceof IOException)
		throw (IOException)t;
	    else if (t instanceof RuntimeException)
		throw (RuntimeException) t;
	    else if (t instanceof Error)
		throw (Error) t;
	    else
		throw new Error("interal error");
	} catch (IllegalAccessException e) {
	    // cannot happen
	    throw new Error("interal error");
	}
	return returnValue;
    }

    /* ASSUMPTION: Called within priviledged access block. 
     *             Needed to set declared methods and to set the
     *             accessibility bit.
     */
    private Method getDeclaredMethod(String methodName, Class[] args, 
				     int requiredModifierMask,
				     int disallowedModifierMask) {
	Method method = null;
	try {
	    method = 
		ofClass.getDeclaredMethod(methodName, args);
	    if (method != null) {
		int mods = method.getModifiers();
		if ((mods & disallowedModifierMask) != 0 ||
		    (mods & requiredModifierMask) != requiredModifierMask) {
		    method = null;
		} else {
		    method.setAccessible(true);
		}
	    }
	} catch (NoSuchMethodException e) {
	    // Since it is alright if methodName does not exist,
	    // no need to do anything special here.
	}
	return method;
    }

    /*
     * Return true if scMethod is accessible from the context of 
     * this ObjectStreamClass' local implementation class.
     * Simulate java accessibility rules of accessing method 'scMethod' 
     * from a method within subclass this.forClass.
     * If method would not be accessible, returns null. 
     *
     * @param scMethod  A method from the superclass of this ObjectStreamClass.
     */
    private boolean checkSuperMethodAccess(Method scMethod) {
	if (scMethod == null) {
	    return false;
	}
	
	int supermods =  scMethod.getModifiers();
	if (Modifier.isPublic(supermods) || Modifier.isProtected(supermods)) {
	    return true;
	} else if (Modifier.isPrivate(supermods)) {
	    return false;
	} else {
	    // check package-private access.
	    return isSameClassPackage(scMethod.getDeclaringClass(), ofClass);
	}
    }

    /* Will not work for array classes. */
    static private boolean isSameClassPackage(Class cl1, Class cl2) {
	if (cl1.getClassLoader() != cl2.getClassLoader()) {
	    return false;
	} else {
	    String clName1 = cl1.getName();
	    String clName2 = cl2.getName();
	    int idx1 = clName1.lastIndexOf('.');
	    int idx2 = clName2.lastIndexOf('.');
	    if (idx1 == -1 || idx2 == -1) {
		/* One of the two doesn't have a package. Only return true
		 * if the other one also does not have a package.
		 */
		return idx1 == idx2;
	    } else {
		return clName1.regionMatches(false, 0, 
					     clName2, 0, idx1 - 1);
	    }
	}
    }

    private final static Class[] NULL_ARGS = {};
    
    //WORKAROUND compiler bug with following code.
    //static final Class[] OIS_ARGS = {ObjectInpuStream.class};
    //static final Class[] OOS_ARGS = {ObjectOutpuStream.class};
    private static Class[] OIS_ARGS = null;
    private static Class[] OOS_ARGS = null;
    private static void initStaticMethodArgs() {
	OOS_ARGS = new Class[1];
	OOS_ARGS[0] = ObjectOutputStream.class;
	OIS_ARGS = new Class[1];
	OIS_ARGS[0] = ObjectInputStream.class;
    }
}
