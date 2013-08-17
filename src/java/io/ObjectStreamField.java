/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.lang.reflect.Field;

/**
 * A description of a Serializable field from a Serializable class.
 * An array of ObjectStreamFields is used to declare the Serializable
 * fields of a class.
 *
 * @author  Roger Riggs
 * @version 1.31, 02/06/02
 * @see ObjectStreamClass
 * @since 1.2
 */
public class ObjectStreamField implements Comparable {
    /**
     * Create a Serializable field with the specified type.
     * This field should be documented with a <code>serialField</code>
     * tag. 
     *
     * @param n the name of the serializable field
     * @param clazz the <code>Class</code> object of the serializable field
     */
    public ObjectStreamField(String n, Class clazz) {
    	name = n;
    	this.clazz = clazz;

	// Compute the typecode for easy switching
	if (clazz.isPrimitive()) {
	    if (clazz == Integer.TYPE) {
		type = 'I';
	    } else if (clazz == Byte.TYPE) {
		type = 'B';
	    } else if (clazz == Long.TYPE) {
		type = 'J';
	    } else if (clazz == Float.TYPE) {
		type = 'F';
	    } else if (clazz == Double.TYPE) {
		type = 'D';
	    } else if (clazz == Short.TYPE) {
		type = 'S';
	    } else if (clazz == Character.TYPE) {
		type = 'C';
	    } else if (clazz == Boolean.TYPE) {
		type = 'Z';
	    }
	} else if (clazz.isArray()) {
	    type = '[';
	    typeString = ObjectStreamClass.getSignature(clazz).intern();
	} else {
	    type = 'L';
	    typeString = ObjectStreamClass.getSignature(clazz).intern();
	}
    }

    /**
     * Create a default Serializable field for <code>field</code>.
     */
    ObjectStreamField(Field field) {
	this(field.getName(), field.getType());
	this.field = field;
    }

    /**
     * Create an ObjectStreamField containing a reflected Field.
     */
    ObjectStreamField(String n, char t, Field f, String ts)
    {
	//	System.out.println("new field, " + n + " " + t + " " + ts);
	name = n;
	type = t;
	field = f;
	typeString = (ts != null ? ts.intern() : null);
    }

    /**
     * SearchKey constructor.
     * @see #compareTo(Object)
     */
    private ObjectStreamField(String name, boolean isPrimitive) {

	// only set fields that compareTo uses for comparison.
	this.name = name;
	setSearchKeyTypeString(isPrimitive);
    }

    /**
     * Get the name of this field.
     *
     * @return a <code>String</code> representing the name of the serializable
     * field 
     */
    public String getName() {
    	return name;
    }

    /**
     * Get the type of the field.
     *
     * @return the <code>Class</code> object of the serializable field 
     */
    public Class getType() {
    	if (clazz != null)
    	    return clazz;
	switch (type) {
	case 'B': clazz = Byte.TYPE;
	    break;
	case 'C': clazz = Character.TYPE;
	    break;
	case 'S': clazz = Short.TYPE;
	    break;
	case 'I': clazz = Integer.TYPE;
	    break;
	case 'J': clazz = Long.TYPE;
	    break;
	case 'F': clazz = Float.TYPE;
	    break;
	case 'D': clazz = Double.TYPE;
	    break;
	case 'Z': clazz = Boolean.TYPE;
	    break;
	case '[':
	case 'L':
	    clazz = Object.class;
	    break;
	}

    	return clazz;
    }

    /** 
     * Returns character encoding of field type.
     * The encoding is as follows:
     * <blockquote><pre>
     * B            byte
     * C            char
     * D            double
     * F            float
     * I            int
     * J            long
     * L            class or interface
     * S            short
     * Z            boolean
     * [            array
     * </pre></blockquote>
     *
     * @return the typecode of the serializable field
     */
    public char getTypeCode() {
	return type;
    }

    /**
     * Return the JVM type signature.
     *
     * @return null if this field has a primitive type.
     */
    public String getTypeString() {
	return typeString;
    }

    /**
     * Offset of field within instance data.
     *
     * @return the offset of this field
     * @see #setOffset
     */
    public int getOffset() {
	return bufoffset;
    }

    /** 
     * Offset within instance data.
     *
     * @param offset the offset of the field
     * @see #getOffset
     */
    protected void setOffset(int offset) {
	bufoffset = offset;
    }

    /*
     * Default constructor creates an empty field.
     * Usually used just to get to the sort functions.
     */ 
    ObjectStreamField() {
    }

    /**
     * Return true if this field has a primitive type.
     *
     * @return true if and only if this field corresponds to a primitive type
     */
    public boolean isPrimitive() {
	return (type != '[' && type != 'L');
    }

    /**
     * Compare this field with another <code>ObjectStreamField</code>.
     * Return -1 if this is smaller, 0 if equal, 1 if greater.
     * Types that are primitives are "smaller" than object types.
     * If equal, the field names are compared.
     */
    public int compareTo(Object o) {
	ObjectStreamField f2 = (ObjectStreamField)o;
	boolean thisprim = (this.typeString == null);
	boolean otherprim = (f2.typeString == null);

	if (thisprim != otherprim) {
	    return (thisprim ? -1 : 1);
	}
	return this.name.compareTo(f2.name);
    }

    /**
     * Return a string that describes this field.
     */
    public String toString() {
	if (typeString != null)
	    return typeString + " " + name;
	else
	    return type + " " + name;
    }

    /**
     * Compare the type of this ObjectStreamField with <code>other</code>.
     * 
     * @return true if both ObjectStreamFields are serializable compatible.
     */
    boolean typeEquals(ObjectStreamField other) {
	if (other == null || type != other.type)
	  return false;

	/* Optimization: Since interning typeString, 
	 *               this short circuit can save some time.
	 * Also, covers case where both typeStrings are null. 
	 */
	if (typeString == other.typeString)
	    return true;
	else
	    return ObjectStreamClass.compareClassNames(typeString,
						       other.typeString,
						       '/');
    }

    /*
     * Return a field.
     * @see java.lang.reflect.Field
     */
    Field getField() {
 	return field;
    }
 
    void setField(Field field) {
 	this.field = field;
    }


    /**
     * @return a ObjectStreamField with enough fields set to search 
     *          a list for a matching ObjectStreamField.
     * @see #compareTo(Object)
     */
    static ObjectStreamField constructSearchKey(String fieldName, 
						Class fieldType) 
    {
	return new ObjectStreamField(fieldName, fieldType.isPrimitive());
    }

    void setSearchKeyTypeString(boolean isPrimitive) {
	typeString = isPrimitive ? null : OBJECT_TYPESTRING;
    }

    private String name;	// the name of the field
    private char type;		// first byte of the type signature
    private Field field;	// Reflected field.
    private String typeString;	// iff object, fully qualified typename containing '/'
    private int bufoffset;      // offset in the data buffer, or index in objects
    private Class clazz;	// the type of this field, if has been resolved

    /* a string object used for ObjectStreamField search. For search purposes,
     * it is sufficient that this string is non-null.
     */
    private static final String OBJECT_TYPESTRING=new String("");

};
