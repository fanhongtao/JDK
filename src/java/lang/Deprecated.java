/*
 * @(#)Deprecated.java	1.4 04/06/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.4, 06/10/04
 * @since 1.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Deprecated {
}
