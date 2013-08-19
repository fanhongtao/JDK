/*
 * @(#)NoPermissionException.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.naming;

/**
  * This exception is thrown when attempting to perform an operation
  * for which the client has no permission. The access control/permission
  * model is dictated by the directory/naming server.
  *
  * <p>
  * Synchronization and serialization issues that apply to NamingException
  * apply directly here.
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @version 1.6 03/01/23
  * @since 1.3
  */

public class NoPermissionException extends NamingSecurityException {
    /**
     * Constructs a new instance of NoPermissionException using an
     * explanation. All other fields default to null.
     *
     * @param	explanation	Possibly null additional detail about this exception.
     */
    public NoPermissionException(String explanation) {
	super(explanation);
    }

    /**
      * Constructs a new instance of NoPermissionException.
      * All fields are initialized to null.
      */
    public NoPermissionException() {
	super();
    }
    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = 8395332708699751775L;
}
