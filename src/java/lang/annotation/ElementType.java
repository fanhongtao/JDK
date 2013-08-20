/*
 * @(#)ElementType.java	1.6 04/03/16
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.annotation;

/**
 * A program element type.  The constants of this enumerated type
 * provide a simple classification of the declared elements in a
 * Java program.
 *
 * <p>These constants are used with the {@link Target} meta-annotation type
 * to specify where it is legal to use an annotation type.
 *
 * @author  Joshua Bloch
 * @since 1.5
 */
public enum ElementType {
    /** Class, interface (including annotation type), or enum declaration */
    TYPE,

    /** Field declaration (inlcudes enum constants) */
    FIELD,

    /** Method declaration */
    METHOD,

    /** Parameter declaration */
    PARAMETER,

    /** Constructor declaration */
    CONSTRUCTOR,

    /** Local variable declaration */
    LOCAL_VARIABLE,

    /** Annotation type declaration */
    ANNOTATION_TYPE,

    /** Package declaration */
    PACKAGE
}
