/*
 * @(#)Void.java	1.4 98/07/01
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
 * The Void class is an uninstantiable placeholder class to hold a
 * reference to the Class object representing the primitive Java type
 * void.
 *
 * @author  unascribed
 * @version 1.4, 07/01/98
 * @since   JDK1.1
 */
public final
class Void {

    /**
     * The Class object representing the primitive Java type void.
     *
     * @since   JDK1.1
     */
    public static final Class TYPE = Class.getPrimitiveClass("void");

    /*
     * The Void class cannot be instantiated.
     */
    private Void() {}
}
