/*
 * @(#)Boolean.java	1.28 98/07/07
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

package java.lang;

/**
 * The Boolean class wraps a value of the primitive type 
 * <code>boolean</code> in an object. An object of type 
 * <code>Boolean</code> contains a single field whose type is 
 * <code>boolean</code>. 
 * <p>
 * In addition, this class provides many methods for 
 * converting a <code>boolean</code> to a <code>String</code> and a 
 * <code>String</code> to a <code>boolean</code>, as well as other 
 * constants and methods useful when dealing with a 
 * <code>boolean</code>. 
 *
 * @author  Arthur van Hoff
 * @version 1.28, 07/07/98
 * @since   JDK1.0
 */
public final
class Boolean implements java.io.Serializable {
    /** 
     * The <code>Boolean</code> object corresponding to the primitive 
     * value <code>true</code>. 
     *
     * @since   JDK1.0
     */
    public static final Boolean TRUE = new Boolean(true);

    /** 
     * The <code>Boolean</code> object corresponding to the primitive 
     * value <code>false</code>. 
     *
     * @since   JDK1.0
     */
    public static final Boolean FALSE = new Boolean(false);

    /**
     * The Class object representing the primitive type boolean.
     *
     * @since   JDK1.1
     */
    public static final Class	TYPE = Class.getPrimitiveClass("boolean");

    /**
     * The value of the Boolean.
     */
    private boolean value;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -3665804199014368530L;

    /**
     * Allocates a <code>Boolean</code> object representing the 
     * <code>value</code> argument. 
     *
     * @param   value   the value of the <code>Boolean</code>.
     * @since   JDK1.0
     */
    public Boolean(boolean value) {
	this.value = value;
    }

    /**
     * Allocates a <code>Boolean</code> object representing the value 
     * <code>true</code> if the string argument is not <code>null</code> 
     * and is equal, ignoring case, to the string <code>"true"</code>. 
     * Otherwise, allocate a <code>Boolean</code> object representing the 
     * value <code>false</code>. 
     *
     * @param   s   the string to be converted to a <code>Boolean</code>.
     * @since   JDK1.0
     */
    public Boolean(String s) {
	this(toBoolean(s));
    }

    /**
     * Returns the value of this Boolean object as a boolean.
     *
     * @return  the primitive <code>boolean</code> value of this object.
     * @since   JDK1.0
     */
    public boolean booleanValue() {
	return value;
    }

    /**
     * Returns the boolean value represented by the specified String.
     * A new <code>Boolean</code> object is constructed. This 
     * <code>Boolean</code> contains the value <code>true</code> if the 
     * string argument is not <code>null</code> and is equal, ignoring 
     * case, to the string <code>"true"</code>. 
     *
     * @param   s   a string.
     * @return  the <code>Boolean</code> value represented by the string.
     * @since   JDK1.0
     */
    public static Boolean valueOf(String s) {
	return new Boolean(toBoolean(s));
    }

    /**
     * Returns a String object representing this Boolean's value.
     * If this object contains the value <code>true</code>, a string equal 
     * to <code>"true"</code> is returned. Otherwise, a string equal to 
     * <code>"false"</code> is returned. 
     *
     * @return  a string representation of this object. 
     * @since   JDK1.0
     */
    public String toString() {
	return value ? "true" : "false";
    }

    /**
     * Returns a hash code for this Boolean.
     *
     * @return  a hash code value for this object.
     * @since   JDK1.0
     */
    public int hashCode() {
	return value ? 1231 : 1237;
    }

    /**
     * Returns <code>true</code> if and only if the argument is not 
     * <code>null</code> and is a <code>Boolean </code>object that 
     * contains the same <code>boolean</code> value as this object. 
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Boolean)) {
	    return value == ((Boolean)obj).booleanValue();
	} 
	return false;
    }

    /**
     * Returns is <code>true</code> if and only if the system property 
     * named by the argument exists and is equal to the string 
     * <code>"true"</code>. (Beginning with Java 1.0.2, the test of 
     * this string is case insensitive.) A system property is accessible 
     * through <code>getProperty</code>, a method defined by the 
     * <code>System</code> class. 
     *
     * @param   name   the system property name.
     * @return  the <code>boolean</code> value of the system property.
     * @see     java.lang.System#getProperty(java.lang.String)
     * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @since   JDK1.0
     */
    public static boolean getBoolean(String name) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null)
	    sm.checkPropertyAccess(name);
	return toBoolean(System.getProperty(name));
    }

    private static boolean toBoolean(String name) { 
	return ((name != null) && name.toLowerCase().equals("true"));
    }
}
