/*
 * @(#)Void.java	1.15 05/12/01
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * The Void class is an uninstantiable placeholder class to hold a
 * reference to the Class object representing the Java keyword
 * void.
 *
 * @author  unascribed
 * @version 1.15, 12/01/05
 * @since   JDK1.1
 */
public final
class Void {

    /**
     * The Class object representing the pseudo-type corresponding to
     * the keyword void.
     */
    public static final Class<Void> TYPE = Class.getPrimitiveClass("void");

    /*
     * The Void class cannot be instantiated.
     */
    private Void() {}
}
