/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.naming.internal;

import java.util.Vector;
import javax.naming.NamingException;

/**
  * The FactoryEnumeration is used for returning factory instances.
  * 
  * @author Rosanna Lee
  * @author Scott Seligman
  * @version 1.4 01/06/18
 */

// no need to implement Enumeration since this is only for internal use
public final class FactoryEnumeration { 
    private Vector vec;
    private int posn = 0;
    private ClassLoader loader;

    /**
     * Records the input vector and uses it directly to satisfy
     * hasMore()/next() requests. An alternative would have been to use
     * vec.elements(), but we want to update the vector so we keep the
     * original Vector. The Vector initially contains Class objects.
     * As each element is used, the Class object is replaced by an
     * instance of the Class itself; eventually, the vector contains
     * only a list of factory instances and no more updates are required.
     *
     * <p> Both Class objects and factories are wrapped in weak
     * references so as not to prevent GC of the class loader.  Each
     * weak reference is tagged with the factory's class name so the
     * class can be reloaded if the reference is cleared.

     * @param vec	A non-null vector
     * @param loader	The class loader of the vector's contents
     */
    FactoryEnumeration(Vector vec, ClassLoader loader) {
	this.vec = vec;
	this.loader = loader;
    }
 
    public Object next() throws NamingException {
	synchronized (vec) {

	    NamedWeakReference ref = (NamedWeakReference)
		vec.elementAt(posn++);
	    Object answer = ref.get();
	    if ((answer != null) && !(answer instanceof Class)) {
		return answer;
	    }

	    String className = ref.getName();

	    try {
		if (answer == null) {	// reload class if weak ref cleared
		    answer = Class.forName(className, true, loader);
		}
		// Instantiate Class to get factory
		answer = ((Class) answer).newInstance();
		ref = new NamedWeakReference(answer, className);
		vec.setElementAt(ref, posn-1);  // replace Class object or null
		return answer;
	    } catch (ClassNotFoundException e) {
		NamingException ne = 
		    new NamingException("No longer able to load " + className);
		ne.setRootCause(e);
		throw ne;
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

