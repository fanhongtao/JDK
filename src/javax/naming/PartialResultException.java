/*
 * @(#)PartialResultException.java	1.7 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.naming;

/**
  * This exception is thrown to indicate that the result being returned
  * or returned so far is partial, and that the operation cannot
  * be completed.  For example, when listing a context, this exception
  * indicates that returned results only represents some of the bindings
  * in the context.
  * <p>
  * Synchronization and serialization issues that apply to NamingException
  * apply directly here.
  *
  * @author Rosanna Lee
  * @version 1.7 03/12/19
  * @author Scott Seligman
  * @since 1.3
  */

public class PartialResultException extends NamingException {
    /**
      * Constructs a new instance of the exception using the explanation
      * message specified. All other fields default to null.
      *
      * @param	explanation	Possibly null detail explaining the exception.
      */
    public PartialResultException(String explanation) {
	super(explanation);
    }

    /**
      * Constructs a new instance of PartialResultException.
      * All fields default to null.
      */
    public PartialResultException() {
	super();
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = 2572144970049426786L;
}
