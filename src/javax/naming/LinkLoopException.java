/*
 * @(#)LinkLoopException.java	1.4 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.naming;

/**
  * This exception is thrown when
  * a loop was detected will attempting to resolve a link, or an implementation
  * specific limit on link counts has been reached.
  * <p>
  * Synchronization and serialization issues that apply to LinkException
  * apply directly here.
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @version 1.4 00/02/02
  *
  * @see LinkRef
  * @since 1.3
  */

public class LinkLoopException extends LinkException {
    /**
      * Constructs a new instance of LinkLoopException with an explanation
      * All the other fields are initialized to null.
      * @param	explanation	A possibly null string containing additional
      *				detail about this exception.
      * @see java.lang.Throwable#getMessage
      */
    public LinkLoopException(String explanation) {
	super(explanation);
    }

    /**
      * Constructs a new instance of LinkLoopException.
      * All the non-link-related and link-related fields are initialized to null.
      */
    public LinkLoopException() {
	super();
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = -3119189944325198009L;
}
