/*
 * @(#)Void.java	1.16 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * The Void class is an uninstantiable placeholder class to hold a
 * reference to the Class object representing the Java keyword
 * void.
 *
 * @author  unascribed
 * @version 1.16, 03/23/10
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
