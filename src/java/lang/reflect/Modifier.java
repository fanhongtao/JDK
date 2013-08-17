/*
 * @(#)Modifier.java	1.6 98/07/01
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

package java.lang.reflect;

/**
 * The Modifier class provides static methods and constants to decode
 * class and member access modifiers.
 *
 * @see Class#getModifiers()
 * @see Member#getModifiers()
 *
 * @author Nakul Saraiya
 */
public
class Modifier {

    /**
     * Return true if the specified integer includes the <tt>public</tt>
     * modifier.
     */
    public static boolean isPublic(int mod) {
	return (mod & PUBLIC) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>private</tt>
     * modifier.
     */
    public static boolean isPrivate(int mod) {
	return (mod & PRIVATE) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>protected</tt>
     * modifier.
     */
    public static boolean isProtected(int mod) {
	return (mod & PROTECTED) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>static</tt>
     * modifier.
     */
    public static boolean isStatic(int mod) {
	return (mod & STATIC) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>final</tt>
     * modifier.
     */
    public static boolean isFinal(int mod) {
	return (mod & FINAL) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>synchronized</tt>
     * modifier.
     */
    public static boolean isSynchronized(int mod) {
	return (mod & SYNCHRONIZED) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>volatile</tt>
     * modifier.
     */
    public static boolean isVolatile(int mod) {
	return (mod & VOLATILE) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>transient</tt>
     * modifier.
     */
    public static boolean isTransient(int mod) {
	return (mod & TRANSIENT) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>native</tt>
     * modifier.
     */
    public static boolean isNative(int mod) {
	return (mod & NATIVE) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>interface</tt>
     * modifier.
     */
    public static boolean isInterface(int mod) {
	return (mod & INTERFACE) != 0;
    }

    /**
     * Return true if the specifier integer includes the <tt>abstract</tt>
     * modifier.
     */
    public static boolean isAbstract(int mod) {
	return (mod & ABSTRACT) != 0;
    }

    /**
     * Return a string describing the access modifier flags in
     * the specified modifier. For example:
     * <pre>
     *    public final synchronized
     *    private transient volatile
     * </pre>
     * The modifier names are return in canonical order, as
     * specified by <em>The Java Language Specification<em>.
     */
    public static String toString(int mod) {
	StringBuffer sb = new StringBuffer();
	int len;

	if ((mod & PUBLIC) != 0)	sb.append("public ");
	if ((mod & PRIVATE) != 0)	sb.append("private ");
	if ((mod & PROTECTED) != 0)	sb.append("protected ");

	/* Canonical order */
	if ((mod & ABSTRACT) != 0)	sb.append("abstract ");
	if ((mod & STATIC) != 0)	sb.append("static ");
	if ((mod & FINAL) != 0)		sb.append("final ");
	if ((mod & TRANSIENT) != 0)	sb.append("transient ");
	if ((mod & VOLATILE) != 0)	sb.append("volatile ");
	if ((mod & NATIVE) != 0)	sb.append("native ");
	if ((mod & SYNCHRONIZED) != 0)	sb.append("synchronized ");

	if ((mod & INTERFACE) != 0)	sb.append("interface ");

	if ((len = sb.length()) > 0)	/* trim trailing space */
	    return sb.toString().substring(0, len-1);
	return "";
    }

    /*
     * Access modifier flag constants from <em>The Java Virtual
     * Machine Specification</em>, Table 4.1.
     */
    public static final int PUBLIC           = 0x00000001;
    public static final int PRIVATE          = 0x00000002;
    public static final int PROTECTED        = 0x00000004;
    public static final int STATIC           = 0x00000008;
    public static final int FINAL            = 0x00000010;
    public static final int SYNCHRONIZED     = 0x00000020;
    public static final int VOLATILE         = 0x00000040;
    public static final int TRANSIENT        = 0x00000080;
    public static final int NATIVE           = 0x00000100;
    public static final int INTERFACE        = 0x00000200;
    public static final int ABSTRACT         = 0x00000400;

}
