/*
 * @(#)ObjectStreamField.java	1.8 97/02/05
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 */

package java.io;

/**
 * A description of a field in a class.
 *
 * @author  unascribed
 * @version 1.8, 02/05/97
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

	/* compare the object types */
	return typeString.equals(other.typeString);
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
