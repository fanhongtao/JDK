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
import java.lang.annotation.Inherited;

/** 
 * Used to indicate whether a Provider implementation wishes to work
 * with entire protocol messages or just with protocol message payloads.
 *
 *  @since JAX-WS 2.0
**/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ServiceMode {
  /**
   * Service mode. PAYLOAD indicates that the Provider implementation
   * wishes to work with protocol message payloads only. MESSAGE indicates
   * that the Provider implementation wishes to work with entire protocol
   * messages.
  **/
  public Service.Mode value() default Service.Mode.PAYLOAD;
}
