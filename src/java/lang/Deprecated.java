/*
 * @(#)Deprecated.java	1.5 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import java.lang.annotation.*;

/**
 * A program element annotated &#64;Deprecated is one that programmers
 * are discouraged from using, typically because it is dangerous,
 * or because a better alternative exists.  Compilers warn when a
 * deprecated program element is used or overridden in non-deprecated code.
 *
 * @author  Neal Gafter
 * @version 1.5, 11/17/05
 * @since 1.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Deprecated {
}
