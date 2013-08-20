/*
 * @(#)Retention.java	1.5 04/06/22
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.annotation;

/**
 * Indicates how long annotations with the annotated type are to
 * be retained.  If no Retention annotation is present on
 * an annotation type declaration, the retention policy defaults to
 * <tt>RetentionPolicy.CLASS</tt>.
 *
 * <p>A Target meta-annotation has effect only if the meta-annotated
 * type is use directly for annotation.  It has no effect if the meta-annotated
 * type is used as a member type in another annotation type.
 *
 * @author  Joshua Bloch
 * @since 1.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Retention {
    RetentionPolicy value();
}
