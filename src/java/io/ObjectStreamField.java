/*
 * @(#)ObjectStreamField.java	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * A description of a field in a class.
 *
 * @author  unascribed
 * @version 1.11, 12/10/01
 */
class ObjectStreamField {
    ObjectStreamField(String n, char t, int o, String ts)
    {
	//	System.out.println("new field, " + n + " " + t + " " + ts);
	name = n;
	type = t;
	offset = o;
	typeString = ts;
    }

    /*
     * Default constructor creates an empty field.
     * Usually used just to get to the sort functions.
     */ 
    ObjectStreamField() {
    }

    /**
     * test if this field is a primitive or not.
     */
    boolean isPrimitive() {
	return (type != '[' && type != 'L');
    }

    /**
     * Compare this with another ObjectStreamField.
     * return -1 if this is smaller, 0 if equal, 1 if greater
     * types that are primitives are "smaller" than objects.
     * if equal, the names are compared.
     */
    int compare(ObjectStreamField other) {
	boolean thisprim = (typeString == null);
	boolean otherprim = (other.typeString == null);

	if (thisprim != otherprim) {
	    return (thisprim ? -1 : 1);
	}
	return name.compareTo(other.name);
    }

    /**
     * Compare the types of two class descriptors.
     * The match if they have the same primitive types.
     * or if they are both objects and the object types match.
     */
    boolean typeEquals(ObjectStreamField other) {
	if (other == null || type != other.type)
	  return false;

	/* Return true if the primitive types matched */
	if (typeString == null && other.typeString == null)
	    return true;

	return ObjectStreamClass.compareClassNames(typeString,
						   other.typeString,
						   '/');
    }

    /**
     * Return a string describing this field.
     */
    public String toString() {
	if (typeString != null)
	    return typeString + " " + name + " @" + offset;
	else
	    return type + " " + name + " @" + offset;
    }

    String name;		// the name of the field
    char type;			// type first byte of the type signature
    int  offset;		// Offset into the object of the field
    String typeString;		// iff object, typename
}
