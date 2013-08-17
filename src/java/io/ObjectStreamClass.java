/*
 * @(#)ObjectStreamClass.java	1.41 98/07/09
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

import sun.misc.Ref;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestOutputStream;
import java.lang.reflect.Modifier;

/**
 * A ObjectStreamClass describes a class that can be serialized to a stream
 * or a class that was serialized to a stream.  It contains the name
 * and the serialVersionUID of the class.
 * <br>
 * The ObjectStreamClass for a specific class loaded in this Java VM can
 * be found using the lookup method.
 *
 * @author  unascribed
 * @version 1.41, 07/09/98
 * @since   JDK1.1
 */
public class ObjectStreamClass implements java.io.Serializable {

   static final long serialVersionUID = -6120832682080437368L;

   /** Find the descriptor for a class that can be serialized.  Null
     * is returned if the specified class does not implement
     * java.io.Serializable or java.io.Externalizable.
     * @since   JDK1.1
     */
    public static ObjectStreamClass lookup(Class cl)
    {
	/* Synchronize on the hashtable so no two threads will do
	 * this at the same time.
	 */
	ObjectStreamClass v = null;
	synchronized (descriptorFor) {
	    /* Find the matching descriptor if it already known */
	    v = findDescriptorFor(cl);
	    if (v != null) {
		return v;
	    }
	    
	    /* Check if it's serializable or externalizable.
	     * Since Externalizable extends Serializiable, return
	     * null immediately if it's not Serializable.
	     */
	    boolean serializable = classSerializable.isAssignableFrom(cl);
	    if (!serializable)
		return null;

	    /* Test if it's Externalizable, clear the serializable flag
	     * only one or the other may be set in the protocol.
	     */
	    boolean externalizable = classExternalizable.isAssignableFrom(cl);
	    if (externalizable)
		serializable = false;

	    /* If the class is only Serializable,
	     * lookup the descriptor for the superclass.
	     */
	    ObjectStreamClass superdesc = null;
	    if (serializable) {
		Class superclass = cl.getSuperclass();
		if (superclass != null) 
		    superdesc = lookup(superclass);
	    }

	    /* Create a new version descriptor,
	     * it put itself in the known table.
	     */
	    v = new ObjectStreamClass(cl, superdesc,
				      serializable, externalizable);
	}
	return v;
    }
    
    /**
     * The name of the class described by this descriptor.
     * @since   JDK1.1
     */
    public String getName() {
	return name;
    }

    /**
     * Return the serialVersionUID for this class.
     * The serialVersionUID defines a set of classes all with the same name
     * that have evolved from a common root class and agree to be serialized
     * and deserialized using a common format.
     * @since   JDK1.1
     */
    public long getSerialVersionUID() {
	return suid;
    }

    /**
     * Return the class in the local VM that this version is mapped to.
     * Null is returned if there is no corresponding local class.
     * @since   JDK1.1
     */
    public Class forClass() {
	return ofClass;
    }
    
    /**
     * Return a string describing this ObjectStreamClass.
     * @since   JDK1.1
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
    private ObjectStreamClass(java.lang.Class cl, ObjectStreamClass superdesc,
			      boolean serial, boolean extern)
    {
	int i;
	ofClass = cl;		/* created from this class */

	name = cl.getName();
	superclass = superdesc;
	serializable = serial;
	externalizable = extern;

	/*
	 * Enter this class in the table of known descriptors.
	 * Otherwise, when the fields are read it may recurse
	 * trying to find the descriptor for itself.
	 */
	insertDescriptorFor(this);

	if (externalizable || name.equals("java.lang.String")) {
	    fields = new ObjectStreamField[0];
	} else {
	    /* Fill in the list of persistent fields. */
	    fields = getFields0(cl);

	    if (fields.length > 0) {
		/* sort the fields by type and name,
		 * primitive fields come first, sorted by name,
		 * then object fields, sorted by name.
		 */
		boolean done;
		do {
		    done = true;
		    for (i = fields.length - 1 ; i > 0 ; i--) {
			if (fields[i - 1].compare(fields[i]) > 0) {
			    ObjectStreamField exch = fields[i];
			    fields[i] = fields[i-1];
			    fields[i-1] = exch;
			    done = false;
			}
		    }
		} while (!done);

		computeFieldSequence();
	    }
	}

	/* Get the serialVersionUID from the class */
	suid = getSerialVersionUID(cl);
	if (suid == 0) {
	    suid = computeSerialVersionUID(cl);
	}
	hasWriteObjectMethod = externalizable ? false : hasWriteObject(cl);
    }

    /*
     * Create an empty ObjectStreamClass for a class about to be read.
     * This is separate from read so ObjectInputStream can assign the
     * wire handle early, before any nested ObjectStreamClasss might
     * be read.
     */
    ObjectStreamClass(String n, long s) {
	name = n;
	suid = s;
	superclass = null;
    }

    /*
     * Set the class this version descriptor matches.
     * The name and serializable hash  must match.
     * Compute and fill in the fieldSequence that will be used
     * for reading.
     */
    void setClass(Class cl) throws InvalidClassException {
	if (cl == null) {

	    /* There is no local equivalent of this class read from the serialized
	     * stream. Initialize this class to always discard data associated with
	     * this class.
	     */
	    localClassDesc = null;
	    ofClass = null;
	    for (int i = 0; i < fields.length; i++ ) {
		fields[i].offset = -1; // discard data read from stream.
	    }
	    computeFieldSequence();
	    return;
	}

	localClassDesc = lookup(cl);

	if (localClassDesc == null)
	    throw new InvalidClassException(cl.getName(), 
					    "Local class not compatible");

	if (suid != localClassDesc.suid) {
	    
	    /* Disregard the serialVersionUID of an array
	     * when name and cl.Name differ. If resolveClass() returns
	     * an array with a different package name,
	     * the serialVersionUIDs will not match since the fully
	     * qualified array class is used in the
	     * computation of the array's serialVersionUID. There is
	     * no way to set a permanent serialVersionUID for an array type.
	     */
	    if (! (cl.isArray() && ! cl.getName().equals(name)))
		throw new InvalidClassException(cl.getName(), 
		    "Local class not compatible:" + 
		    " stream classdesc serialVersionUID=" + suid +
		    " local class serialVersionUID=" + localClassDesc.suid);
	}

	if (! compareClassNames(name, cl.getName(), '.'))
	    throw new InvalidClassException(name,
					    "Incompatible local class name: " +
					    cl.getName());

	/*
	 * Test that both implement either serializable or externalizable.
	 */
	if (serializable != localClassDesc.serializable ||
	    externalizable != localClassDesc.externalizable)
	    throw new InvalidClassException(cl.getName(),
					"Serialization incompatible with Externalization");

	/* Compute the offsets in the class where each field in this descriptor
	 * should be stored.  The fieldSequence is computed from the offsets
	 * and used by the native code to read and store the values.
	 * Each field in this ObjectStreamClass (the source) is located (by name) in
	 * the ObjectStreamClass of the class(the destination).
	 * In the usual (non-versioned case) the field is in both
	 * descriptors and the types match, so the offset is copied.
	 * If the type does not match, a InvalidClass exception is thrown.
	 * If the field is not present in the class, the offset is set to -1
	 * so the field will be read but discarded.
	 * If extra fields are present in the class they are ignored. Their
	 * values will be set to the default value by the object allocator.
	 * Both the src and dest field list are sorted by type and name.
	 */

	ObjectStreamField[] destfield = localClassDesc.getFields();
	ObjectStreamField[] srcfield = fields;

	int j = 0;
    nextsrc:
	for (int i = 0; i < srcfield.length; i++ ) {
	    /* Find this field in the dest*/
	    for (int k = j; k < destfield.length; k++) {
	      if (srcfield[i].name.equals(destfield[k].name)) {
		  /* found match */
		  if (!srcfield[i].typeEquals(destfield[k])) {
		      throw new InvalidClassException(cl.getName(),
						  "The type of field " +
						       srcfield[i].name +
						       " of class " + name +
						       " is incompatible.");
		  }

		  /* Skip over any fields in the dest that are not in the src */
 		  j = k; 
		  
		  srcfield[i].offset = destfield[j].offset;
		  // go on to the next source field
		  continue nextsrc;
	      }
	    }
	    /* Source field not found in dest, mark field to discard. */
	    srcfield[i].offset = -1;
	}

	/* Setup the field sequence for native code */
	computeFieldSequence();

	/* Remember the class this represents */
	ofClass = cl;
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

	boolean result = streamName.regionMatches(false, streamNameIndex, 
					localName, localNameIndex,
					streamName.length() - streamNameIndex);
	return result;
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
     * Return the array of persistent fields for this class.
     */
    ObjectStreamField[] getFields(){
	return fields;
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
     * Return true if 'this' Externalizable class was written in block data mode.
     * Maintain forwards compatibility for JDK 1.1 streams containing non-block data
     * mode externalizable data.
     *
     * @since JDK 1.1.6
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
     * Get the externalizability of the class.
     */
    boolean isExternalizable() {
	return externalizable;
    }

    /*
     * Get the sequence of fields for this Class.
     */
    int[] getFieldSequence() {
	return fieldSequence;
    }

    /*
     * Create the array used by the native code containing
     * the types and offsets to store value read from the stream.
     * The array is an array of int's with the even numbered elements
     * containing the type (first character) and the odd elements
     * containing the offset into the object where the value should be
     * stored.  An offset of -1 means the value should be discarded.
     */
    private void computeFieldSequence() {
	fieldSequence = new int[fields.length*2];
	for (int i = 0; i < fields.length; i++ ) {
	    fieldSequence[i*2] = fields[i].type;
	    fieldSequence[i*2+1] = fields[i].offset;
	}
    }
    
    /*
     * Compute a hash for the specified class.  Incrementally add
     * items to the hash accumulating in the digest stream.
     * Fold the hash into a long.  Use the SHA secure hash function.
     */
    private static long computeSerialVersionUID(Class thisclass) {
	ByteArrayOutputStream devnull = new ByteArrayOutputStream(512);

	long h = 0;
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA");
	    DigestOutputStream mdo = new DigestOutputStream(devnull, md);
	    DataOutputStream data = new DataOutputStream(mdo);

	    data.writeUTF(thisclass.getName());
	    
	    int classaccess = getClassAccess(thisclass);
	    classaccess &= (Modifier.PUBLIC | Modifier.FINAL |
			    Modifier.INTERFACE | Modifier.ABSTRACT);
	    data.writeInt(classaccess);
	    
	    /* 
	     * Get the list of interfaces supported,
	     * Accumulate their names their names in Lexical order
	     * and add them to the hash
	     */
	    Class interfaces[] = thisclass.getInterfaces();
	    quicksort(interfaces);
		
	    for (int i = 0; i < interfaces.length; i++) {
		data.writeUTF(interfaces[i].getName());
	    }

	    /* Sort the field names to get a deterministic order */
	    String fields[] = getFieldSignatures(thisclass);
	    quicksort(fields);
	    
	    /* Include in the hash all fields except those that are
	     * private transient and private static.
	     */
	    for (int i = 0; i < fields.length; i++) {
		String field = fields[i];
		int access = getFieldAccess(thisclass, field);
		if ((access & M_PRIVATE) == M_PRIVATE &&
		    (((access & M_TRANSIENT) == M_TRANSIENT)||
		     ((access & M_STATIC) == M_STATIC)))
		    continue;
		int offset = field.indexOf(' ');
		String name = field.substring(0, offset);
		String desc = field.substring(offset+1);
		data.writeUTF(name);
		data.writeInt(access);
		data.writeUTF(desc);
	    }

	    /*
	     * Get the list of methods including name and signature
	     * Sort lexically, add all except the private methods
	     * to the hash with their access flags
	     */
	    String methods[] = getMethodSignatures(thisclass);
	    quicksort(methods);
	    
	    for (int i = 0; i < methods.length; i++) {
		String method = methods[i];
		int access = getMethodAccess(thisclass, method);
		if ((access & M_PRIVATE) != 0)
		    continue;
		int offset = method.indexOf(' ');
		String mname = method.substring(0, offset);
		String desc = method.substring(offset+1);
		desc = desc.replace('/', '.');
		data.writeUTF(mname);
		data.writeInt(access);
		data.writeUTF(desc);
	    }

	    /* Compute the hash value for this class.
	     * Use only the first 64 bits of the hash.
	     */
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

    /* These are in this class so that there is no chance they can be used
     * outside the class.
     */
    private static native int getClassAccess(Class aclass);

    private static native String[] getMethodSignatures(Class aclass);
    private static native int getMethodAccess(Class aclass, String methodsig);

    private static native String[] getFieldSignatures(Class aclass);
    private static native int getFieldAccess(Class aclass, String fieldsig);

    private static final int M_TRANSIENT = 0x0080;
    private static final int M_PRIVATE = 0x0002;
    private static final int M_STATIC = 0x0008;

    /*
     * locate the ObjectStreamClass for this class and write it to the stream.
     */
    void write(ObjectOutputStream s) throws IOException {
	
	/* write the flag indicating that this class has write/read object methods */
	int flags = 0;
	if (hasWriteObjectMethod)
	    flags |= ObjectStreamConstants.SC_WRITE_METHOD;
	if (serializable)
	    flags |= ObjectStreamConstants.SC_SERIALIZABLE;
	if (externalizable)
	    flags |= ObjectStreamConstants.SC_EXTERNALIZABLE;
	s.writeByte(flags);
	
	/* write the total number of fields */
	s.writeShort(fields.length);
	
	/* Write out the descriptors of the primitive fields Each
	 * descriptor consists of the UTF fieldname, a short for the
	 * access modes, and the first byte of the signature byte.
	 * For the object types, ('[' and 'L'), a reference to the
	 * type of the field follows.
	 */

	/* disable replacement of String objects written
	 * by ObjectStreamClass. */
	boolean prevReplaceObject = s.enableReplace;
	s.enableReplace = false;
	try {
	    for (int i = 0; i < fields.length; i++ ) {
		ObjectStreamField f = fields[i];
		s.writeByte(f.type);
		s.writeUTF(f.name);
		if (!f.isPrimitive()) {
		    s.writeObject(f.typeString);
		}
	    }
	} finally {
	    s.enableReplace = prevReplaceObject;
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

	/* MOVED from ObjectStreamConstants, due to failing SignatureTest.
         * In JDK 1.2, SC_BLOCK_DATA is a constant in ObjectStreamConstants.
	 * If SC_EXTERNALIZABLE, this bit indicates externalizable data 
	 * written in block data mode. */
        final byte SC_BLOCK_DATA = 0x08;  

	hasExternalizableBlockData = externalizable ? 
	    (flags & SC_BLOCK_DATA) != 0 :
	    false;

	/* Read the number of fields described.
	 * For each field read the type byte, the name.
	 */    
	int count = s.readShort();
	fields = new ObjectStreamField[count];

	/* disable replacement of String objects written
	 * by ObjectStreamClass. */
	boolean prevEnableResolve = s.enableResolve;
	s.enableResolve = false;
	try {
	    for (int i = 0; i < count; i++ ) {
		char type = (char)s.readByte();
		String name = s.readUTF();
		String ftype = null;
		if (type == '[' || type == 'L') {
		    ftype = (String)s.readObject();
		}
		fields[i] = new ObjectStreamField(name, type, -1, ftype);
	    }
	} finally {
	    s.enableResolve = prevEnableResolve;
	}
    }


    /*
     * Cache of Class -> ClassDescriptor Mappings.
     */
    static private ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];

    /*
     * findDescriptorFor a Class.
     * This looks in the cache for a mapping from Class -> ObjectStreamClass mappings.
     * The hashCode of the Class is used for the lookup since the Class is the key.
     * The entries are extended from sun.misc.Ref so the gc will be able to free them
     * if needed.
     */
    private static ObjectStreamClass findDescriptorFor(Class cl) {

	int hash = cl.hashCode();
	int index = (hash & 0x7FFFFFFF) % descriptorFor.length;
	ObjectStreamClassEntry e;
	ObjectStreamClassEntry prev;
	
	/* Free any initial entries whose refs have been cleared */
	while ((e = descriptorFor[index]) != null && e.check() == null) {
	    descriptorFor[index] = e.next;
	}

	/* Traverse the chain looking for a descriptor with ofClass == cl.
	 * unlink entries that are unresolved.
	 */
	prev = e;
	while (e != null ) {
	    ObjectStreamClass desc = (ObjectStreamClass)(e.check());
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
	ObjectStreamClassEntry e = new ObjectStreamClassEntry();
	e.setThing(desc);
	e.next = descriptorFor[index];
       	descriptorFor[index] = e;
    }

    /*
     * The name of this descriptor
     */
    private String name;
    
    /*
     * The descriptor of the supertype.
     */
    private ObjectStreamClass superclass;

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
     * SerialVersionUID for this class.
     */
    private long suid;
    
    /*
     * This sequence of type, byte offset of the fields to be
     * serialized and deserialized.
     */
    private int[] fieldSequence;

    /* True if this class has/had a writeObject method */
    private boolean hasWriteObjectMethod;
    
    /* In JDK 1.1, external data was not written in block mode.
     * As of JDK 1.2, external data is written in block data mode. This
     * flag enables JDK 1.1.6 to distinguish between JDK 1.1 external
     * data format and JDK 1.2 external data format.
     *
     * @since JDK 1.1.6
     */
    private boolean hasExternalizableBlockData;

    /*
     * ObjectStreamClass that this one was built from.
     */
    private ObjectStreamClass localClassDesc;
    
    /* Get the array of non-static and non-transient fields */
    private native ObjectStreamField[] getFields0(Class cl);
    
    /* Get the serialVersionUID from the specified class */
    private static native long getSerialVersionUID(Class cl);
    
    /* Get the boolean as to whether the class has/had a writeObject method. */
    private static native boolean hasWriteObject(Class cl);
    
    /* The Class Object for java.io.Serializable */
    private static Class classSerializable = null;
    private static Class classExternalizable = null;

    /*
     * Resolve java.io.Serializable at load time.
     */
    static {
	try {
	    classSerializable = Class.forName("java.io.Serializable");
	    classExternalizable = Class.forName("java.io.Externalizable");
	} catch (Throwable e) {
	    System.err.println("Could not load java.io.Serializable or java.io.Externalizable.");
	}
    }

    /* Support for quicksort */

    /*
     * Implement the doCompare method.
     * Strings are compared directly.
     * Classes are compared using their names.
     * ObjectStreamField objects are compared by type and name
     * and then their descriptors (as strings).
     */
    private static int doCompare(Object o1, Object o2) {
	String s1, s2;
	if (o1 instanceof String && o2 instanceof String) {
	    s1 = (String)o1;
	    s2 = (String)o2;
	} else if (o1 instanceof Class && o2 instanceof Class) {
	    Class c1 = (Class)o1;
	    Class c2 = (Class)o2;
	    s1 = c1.getName();
	    s2 = c2.getName();
	} else if (o1 instanceof ObjectStreamField &&
		   o2 instanceof ObjectStreamField) {
	    ObjectStreamField f1 = (ObjectStreamField)o1;
	    ObjectStreamField f2 = (ObjectStreamField)o2;
	    s1 = f1.name;
	    s2 = f2.name;
	} else {
	    throw new Error("Unsupported types");
	}
	return s1.compareTo(s2);
    }

    private static void swap(Object arr[], int i, int j) {
	Object tmp;

	tmp = arr[i];
	arr[i] = arr[j];
	arr[j] = tmp;
    }

    /*
     * quicksort the array of objects.
     *
     * @param arr[] - an array of objects
     * @param left - the start index - from where to begin sorting
     * @param right - the last index.
     */
    private static void quicksort(Object arr[], int left, int right)
    {
	int i, last;

	if (left >= right) { /* do nothing if array contains fewer than two */
	    return; 	     /* two elements */
	}
	swap(arr, left, (left+right) / 2);
	last = left;
	for (i = left+1; i <= right; i++) {
	    if (doCompare(arr[i], arr[left]) < 0) {
		swap(arr, ++last, i);
	    }
	}
	swap(arr, left, last);
	quicksort(arr, left, last-1);
	quicksort(arr, last+1, right);
    }

    /*
     * Preform a sort using the specified comparitor object.
     */       
    private static void quicksort(Object arr[]) {
        quicksort(arr, 0, arr.length-1);
    }
}


/*
 * Entries held in the Cache of known ObjectStreamClass objects.
 * Entries are chained together with the same hash value (modulo array size).
 */
class ObjectStreamClassEntry extends sun.misc.Ref {
    ObjectStreamClassEntry next;
    public Object reconstitute() {
	return null;
    }
}
