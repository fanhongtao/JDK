/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/** 
 * Used to annotate service specific exception classes to customize 
 * to the local and namespace name of the fault element and the name
 * of the fault bean.
 *
 *  @since JAX-WS 2.0
**/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFault {
  /**
   *  Elements local name.
  **/
  public String name() default "";

  /**
   *  Elements namespace name.
  **/
  public String targetNamespace() default "";

  /**
   *  Fault bean name.
  **/
  public String faultBean() default "";
}
