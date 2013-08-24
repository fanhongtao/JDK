/*
 * @(#)file      BerException.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   4.13
 * @(#)date      06/11/29
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


package com.sun.jmx.snmp;





/**
 * Exception thrown when a BER encoding/decoding error occurs.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @version     4.13     11/17/05
 * @author      Sun Microsystems, Inc
 *
 * @since 1.5
 */

public class BerException extends Exception {

  public static final int BAD_VERSION=1;

  private int errorType= 0;

  public BerException() {
    errorType= 0;
  }

  public BerException(int x) {
    errorType= x;
  }

  public boolean isInvalidSnmpVersion() {
    if (errorType == BAD_VERSION) 
      return true;
    else
      return false;
  }
}
