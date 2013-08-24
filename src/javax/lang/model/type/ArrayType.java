/*
 * @(#)ArrayType.java	1.3 06/07/31
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.lang.model.type;


/**
 * Represents an array type.
 * A multidimensional array type is represented as an array type
 * whose component type is also an array type.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @version 1.3 06/07/31
 * @since 1.6
 */
public interface ArrayType extends ReferenceType {

    /**
     * Returns the component type of this array type.
     *
     * @return the component type of this array type
     */
    TypeMirror getComponentType();
}
