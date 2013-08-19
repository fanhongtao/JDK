/*
 * @(#)HasControls.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.naming.ldap;

import javax.naming.NamingException;

/**
  * This interface is for returning controls with objects returned
  * in NamingEnumerations.
  * For example, suppose a server sends back controls with the results
  * of a search operation, the service provider would return a NamingEnumeration of
  * objects that are both SearchResult and implement HasControls.
  *<blockquote><pre>
  *   NamingEnumeration enum = ectx.search((Name)name, filter, sctls);
  *   while (enum.hasMore()) {
  *	Object entry = enum.next();
  *
  *	// Get search result 
  *	SearchResult res = (SearchResult)entry;
  *	// do something with it 
  *
  *	// Get entry controls
  *  	if (entry instanceof HasControls) {
  *	    Control[] entryCtls = ((HasControls)entry).getControls();
  *	    // do something with controls
  *	}
  *   }
  *</pre></blockquote>
  * 
  * @author Rosanna Lee
  * @author Scott Seligman
  * @author Vincent Ryan
  * @version 1.6 03/01/23
  * @since 1.3
  *
  */

public interface HasControls {

    /**
      * Retrieves an array of <tt>Control</tt>s from the object that
      * implements this interface. It is null if there are no controls.
      *
      * @return A possibly null array of <tt>Control</tt> objects.
      * @throws NamingException If cannot return controls due to an error.
      */
    public Control[] getControls() throws NamingException;
}
