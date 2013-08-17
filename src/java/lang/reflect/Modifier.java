/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
     * Return true if the specified integer includes the <tt>final</tt>
     * modifier.
     */
    public static boolean isFinal(int mod) {
	return (mod & FINAL) != 0;
    }

    /**
     * Return true if the specified integer includes the <tt>synchronized</tt>
     * modifier.
     */
    public static boolean isSynchronized(int mod) {
	return (mod & SYNCHRONIZED) != 0;
    }

    /**
     * Return true if the specified integer includes the <tt>volatile</tt>
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
     * Return true if the specifier integer includes the <tt>strictfp</tt>
     * modifier.
     */
    public static boolean isStrict(int mod) {
	return (mod & STRICT) != 0;
    }

    /**
     * Return a string describing the access modifier flags in
     * the specified modifier. For example:
     * <blockquote><pre>
     *    public final synchronized
     *    private transient volatile
     * </pre></blockquote>
     * The modifier names are return in canonical order, as
     * specified by <em>The Java Language Specification</em>.
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

	if ((mod & STRICT) != 0)	sb.append("strictfp ");

	if ((len = sb.length()) > 0)	/* trim trailing space */
	    return sb.toString().substring(0, len-1);
	return "";
    }

    /*
     * Access modifier flag constants from <em>The Java Virtual
     * Machine Specification</em>, Table 4.1.
     */

    /**
     * The <code>int</code> value representing the <code>public</code> 
     * modifier.
     */    
    public static final int PUBLIC           = 0x00000001;

    /**
     * The <code>int</code> value representing the <code>private</code> 
     * modifier.
     */    
    public static final int PRIVATE          = 0x00000002;

    /**
     * The <code>int</code> value representing the <code>protected</code> 
     * modifier.
     */    
    public static final int PROTECTED        = 0x00000004;

    /**
     * The <code>int</code> value representing the <code>static</code> 
     * modifier.
     */    
    public static final int STATIC           = 0x00000008;

    /**
     * The <code>int</code> value representing the <code>final</code> 
     * modifier.
     */    
    public static final int FINAL            = 0x00000010;

    /**
     * The <code>int</code> value representing the <code>synchronized</code> 
     * modifier.
     */    
    public static final int SYNCHRONIZED     = 0x00000020;

    /**
     * The <code>int</code> value representing the <code>volatile</code> 
     * modifier.
     */    
    public static final int VOLATILE         = 0x00000040;

    /**
     * The <code>int</code> value representing the <code>transient</code> 
     * modifier.
     */    
    public static final int TRANSIENT        = 0x00000080;

    /**
     * The <code>int</code> value representing the <code>native</code> 
     * modifier.
     */    
    public static final int NATIVE           = 0x00000100;

    /**
     * The <code>int</code> value representing the <code>interface</code> 
     * modifier.
     */    
    public static final int INTERFACE        = 0x00000200;

    /**
     * The <code>int</code> value representing the <code>abstract</code> 
     * modifier.
     */    
    public static final int ABSTRACT         = 0x00000400;

    /**
     * The <code>int</code> value representing the <code>strictfp</code> 
     * modifier.
     */    
    public static final int STRICT           = 0x00000800;

}
