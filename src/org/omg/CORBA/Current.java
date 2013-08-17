/*
 * @(#)Current.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/**
 * An interface that makes it possible to access information
 * associated with a particular thread of execution, such as
 * security information or a transaction identifier.
 * <P>
 * An ORB or CORBA service that needs its own thread-specific
 * state extends the CORBA package's <code>Current</code>.
 * Users of the service can obtain an instance of the appropriate
 * <code>Current</code> interface by invoking
 * <code>ORB.resolve_initial_references</code>.
 * For example, the Security service obtains the <code>Current</code>
 * relevant to it by invoking
 * <PRE>
 *    ORB.resolve_initial_references("SecurityCurrent");
 * </PRE>
 * <P>
 * A CORBA service does not have to use this method of keeping context
 * but may choose to do so.
 * <P>
 * Methods on classes that implement from <code>Current</code> access state
 * associated with the thread in which they are invoked, not state associated
 * with the thread from which the <code>Current</code> was obtained.
 *  Current objects must not be exported to other processes, or externalized
 *  with ORB.object_to_string. If any attempt is made to do so, the offending
 *  operation will raise a MARSHAL system exception.
 * @see <a href="package-summary.html#unimpl"><code>portable</code>
 * package comments for unimplemented features</a>
 */

public interface Current extends org.omg.CORBA.Object
{
}
