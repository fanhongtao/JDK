/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws.handler;

import javax.xml.ws.LogicalMessage;

/** The <code>LogicalMessageContext</code> interface extends 
 *  <code>MessageContext</code> to
 *  provide access to a the contained message as a protocol neutral
 *  LogicalMessage
 * 
 *  @since JAX-WS 2.0
**/
public interface LogicalMessageContext 
                    extends MessageContext {

  /** Gets the message from this message context
   *
   *  @return The contained message; returns null if no 
   *          message is present in this message context
  **/
  public LogicalMessage getMessage();
}
