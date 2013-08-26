/*
 * @(#)IllegalFormatException.java	1.5 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * Unchecked exception thrown when a format string contains an illegal syntax
 * or a format specifier that is incompatible with the given arguments.  Only
 * explicit subtypes of this exception which correspond to specific errors
 * should be instantiated.
 *
 * @version 	1.5, 03/23/10
 * @since 1.5
 */
public class IllegalFormatException extends IllegalArgumentException {

    private static final long serialVersionUID = 18830826L;

    // package-private to prevent explicit instantiation
    IllegalFormatException() { }
}
