/*
 * @(#)SupportedSourceVersion.java	1.5 06/08/02
 *
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package javax.annotation.processing;

import java.lang.annotation.*;
import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;
import javax.lang.model.SourceVersion;


/**
 * An annotation used to indicate the latest source version an
 * annotation processor supports.  The {@link
 * Processor#getSupportedSourceVersion} method can construct its
 * result from the value of this annotation, as done by {@link
 * AbstractProcessor#getSupportedSourceVersion}.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @version 1.5 06/08/02
 * @since 1.6
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface SupportedSourceVersion {
    SourceVersion value();
}
