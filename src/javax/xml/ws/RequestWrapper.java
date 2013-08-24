/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/** 

 * Used to annotate methods in the Service Endpoint Interface with the request 
 * wrapper bean to be used at runtime. The default value of the localName is 
 * the operationName, as defined in WebMethod annotation and the
 * targetNamespace is the target namespace of the SEI.
 * <p> When starting from Java this annotation is used resolve
 * overloading conflicts in document literal mode. Only the className
 * is required in this case.
 * 
 *  @since JAX-WS 2.0
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestWrapper {
  /**
   *  Elements local name.
  **/
  public String localName() default "";

  /**
   *  Elements namespace name.
  **/
  public String targetNamespace() default "";

  /**
   *  Request wrapper bean name.
  **/
  public String className() default "";

}

