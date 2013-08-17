/*
 * @(#)Environment.java	1.15 98/08/28
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package org.omg.CORBA;

/**
 * A container (holder) for an exception that is used in <code>Request</code>
 * operations to make exceptions available to the client.  An
 * <code>Environment</code> object is created with the <code>ORB</code>
 * method <code>create_environment</code>.
 *
 * @version 1.11, 09/09/97
 * @since   JDK1.2
 */

public abstract class Environment {

  /**
   * Retrieves the exception in this <code>Environment</code> object.
   *
   * @return			the exception in this <code>Environment</code> object
   */

  public abstract java.lang.Exception exception();

  /**
   * Inserts the given exception into this <code>Environment</code> object.
   *
   * @param except		the exception to be set
   */

  public abstract void exception(java.lang.Exception except);

  /**
   * Clears this <code>Environment</code> object of its exception.
   */

  public abstract void clear();

}
