/*
 * @(#)FactoryEnumeration.java	1.3 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.naming.internal;

import java.util.Vector;
import javax.naming.NamingException;

/**
  * The FactoryEnumeration is used for returning factory instances.
  * 
  * @author Rosanna Lee
  * @author Scott Seligman
  * @version 1.3 00/02/02
 */

// no need to implement Enumeration since this is only for internal use
public final class FactoryEnumeration { 
    private Vector vec;
    private int posn = 0;

    /**
     * Records the input vector and uses it directly to satisfy
     * hasMore()/next() requests. An alternative would have been to use
     * vec.elements(), but we want to update the vector so we keep the
     * original Vector. The Vector initially contains Class objects.
     * As each element is used, the Class object is replaced by an
     * instance of the Class itself; eventually, the vector contains
     * only a list of factory instances and no more updates are required.
     * 
     * @param A non-null vector.
     */
    FactoryEnumeration(Vector vec) {
	this.vec = vec;
    }
 
    public Object next() throws NamingException {
	synchronized (vec) {
	    Object answer = vec.elementAt(posn++);

	    if (!(answer instanceof Class)) {
		return answer;
	    }

	    // Still a Class; need to instantiate
	    try {
		answer = ((Class)answer).newInstance();
		vec.setElementAt(answer, posn-1);  // replace Class object
		return answer;
	    } catch (InstantiationException e) {
		NamingException ne = 
		    new NamingException("Cannot instantiate " + answer);
		ne.setRootCause(e);
		throw ne;
	    } catch (IllegalAccessException e) {
		NamingException ne = new NamingException("Cannot access " + answer);
		ne.setRootCause(e);
		throw ne;
	    }
	}
    }

    public boolean hasMore() {
	return posn < vec.size();
    }
}
