/*
 * @(#)Parameter.java	1.11 04/04/30
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.javadoc;

/**
 * Parameter information.
 * This includes a parameter type and parameter name.
 *
 * @author Robert Field
 */
public interface Parameter {

    /**
     * Get the type of this parameter.
     */
    Type type();

    /**
     * Get local name of this parameter.
     * For example if parameter is the short 'index', returns "index".
     */
    String name();

    /**
     * Get type name of this parameter.
     * For example if parameter is the short 'index', returns "short".
     * <p>
     * This method returns a complete string
     * representation of the type, including the dimensions of arrays and
     * the type arguments of parameterized types.  Names are qualified.
     */
    String typeName();

    /**
     * Returns a string representation of the parameter.
     * <p>
     * For example if parameter is the short 'index', returns "short index".
     *
     * @return type and parameter name of this parameter.
     */
    String toString();

    /**
     * Get the annotations of this parameter.
     * Return an empty array if there are none.
     *
     * @return the annotations of this parameter.
     * @since 1.5
     */
    AnnotationDesc[] annotations();
}


