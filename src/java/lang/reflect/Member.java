/*
 * @(#)Member.java	1.5 98/07/01
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
 * Member is an interface that reflects identifying information about
 * a single member (a field or a method) or a constructor.
 *
 * @see	java.lang.Class
 * @see	Field
 * @see	Method
 * @see	Constructor
 *
 * @author Nakul Saraiya
 */
public
interface Member {

    /**
     * Identifies the set of all public members of a class or interface,
     * including inherited members.
     * @see java.lang.SecurityManager#checkMemberAccess
     */
    public static final int PUBLIC = 0;

    /**
     * Identifies the set of declared members of a class or interface.
     * Inherited members are not included.
     * @see java.lang.SecurityManager#checkMemberAccess
     */
    public static final int DECLARED = 1;

    /**
     * Returns the Class object representing the class or interface
     * that declares the member or constructor represented by this Member.
     */
    public Class getDeclaringClass();

    /**
     * Returns the simple name of the underlying member or constructor
     * represented by this Member.
     */
    public String getName();

    /**
     * Returns the Java language modifiers for the member or
     * constructor represented by this Member, as an integer.  The
     * Modifier class should be used to decode the modifiers in
     * the integer.
     * @see Modifier
     */
    public int getModifiers();

}
