/*
 * @(#)Void.java	1.8 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * The Void class is an uninstantiable placeholder class to hold a
 * reference to the Class object representing the primitive Java type
 * void.
 *
 * @author  unascribed
 * @version 1.8, 02/02/00
 * @since   JDK1.1
 */
public final
class Void {

    /**
     * The Class object representing the primitive Java type void.
     */
    public static final Class TYPE = Class.getPrimitiveClass("void");

    /*
     * The Void class cannot be instantiated.
     */
    private Void() {}
}
