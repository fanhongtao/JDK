/*
 * @(#)InvalidNameException.java	1.4 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.naming;

/**
  * This exception indicates that the name being specified does
  * not conform to the naming syntax of a naming system.
  * This exception is thrown by any of the methods that does name
  * parsing (such as those in Context, DirContext, CompositeName and CompoundName).
  * <p>
  * Synchronization and serialization issues that apply to NamingException
  * apply directly here.
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @version 1.4 00/02/02
  *
  * @see Context
  * @see javax.naming.directory.DirContext
  * @see CompositeName
  * @see CompoundName
  * @see NameParser
  * @since 1.3
  */

public class InvalidNameException extends NamingException {
    /**
      * Constructs an instance of InvalidNameException using an
      * explanation of the problem.
      * All other fields are initialized to null.
      * @param explanation	A possibly null message explaining the problem.
      * @see java.lang.Throwable#getMessage
      */
    public InvalidNameException(String explanation) {
	super(explanation);
    }

    /**
      * Constructs an instance of InvalidNameException with
      * all fields set to null.
      */
    public InvalidNameException() {
	super();
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = -8370672380823801105L;
}
