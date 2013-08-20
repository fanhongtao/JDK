/*
 * @(#)WildcardType.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 * 
 */

package com.sun.javadoc;


/**
 * Represents a wildcard type argument.
 * Examples include:	<pre>
 * {@code <?>}
 * {@code <? extends E>}
 * {@code <? super T>}
 * </pre>
 * A wildcard type can have explicit <i>extends</i> bounds
 * or explicit <i>super</i> bounds or neither, but not both.
 *
 * @author Scott Seligman
 * @version 1.2 03/12/19
 * @since 1.5
 */
public interface WildcardType extends Type {

    /**
     * Return the upper bounds of this wildcard type argument
     * as given by the <i>extends</i> clause.
     * Return an empty array if no such bounds are explicitly given.
     *
     * @return the extends bounds of this wildcard type argument
     */
    Type[] extendsBounds();

    /**
     * Return the lower bounds of this wildcard type argument
     * as given by the <i>super</i> clause.
     * Return an empty array if no such bounds are explicitly given.
     *
     * @return the super bounds of this wildcard type argument
     */
    Type[] superBounds();
}
