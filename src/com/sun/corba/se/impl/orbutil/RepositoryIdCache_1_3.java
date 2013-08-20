/*
 * @(#)RepositoryIdCache_1_3.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.impl.orbutil;

import java.util.Stack;
import java.util.Hashtable;
import java.util.EmptyStackException;
import java.util.Enumeration;

// Really limited pool - in this case just creating several at a time...
class RepositoryIdPool_1_3 extends Stack {
    
    private static int MAX_CACHE_SIZE = 4;
    private RepositoryIdCache_1_3 cache;
    
    public final synchronized RepositoryId_1_3 popId() {

        try {
            return (RepositoryId_1_3)super.pop();
        }
        catch(EmptyStackException e) {
            increasePool(5);
            return (RepositoryId_1_3)super.pop();
        }

    }

    // Pool management
    final void increasePool(int size) {
        //if (cache.size() <= MAX_CACHE_SIZE)
	for (int i = size; i > 0; i--)
	    push(new RepositoryId_1_3());
        /*
	  // _REVISIT_ This will not work w/out either thread tracing or weak references.  I am
	  // betting that thread tracing almost completely negates benefit of reuse.  Until either
	  // 1.2 only inclusion or proof to the contrary, I'll leave it this way...
	  else {
	  int numToReclaim = cache.size() / 2;
	  Enumeration keys = cache.keys();
	  Enumeration elements = cache.elements();
	  for (int i = numToReclaim; i > 0; i--) {
	  Object key = keys.nextElement();
	  Object element = elements.nextElement();
                
	  push(element);
	  cache.remove(key);
	  }
	  }
        */
    }
    
    final void setCaches(RepositoryIdCache_1_3 cache) {
        this.cache = cache;  
    }

}

public class RepositoryIdCache_1_3 extends Hashtable {

    private RepositoryIdPool_1_3 pool = new RepositoryIdPool_1_3();
    
    public RepositoryIdCache_1_3() {
        pool.setCaches(this);    
    }
    
    public final synchronized RepositoryId_1_3 getId(String key) {
        RepositoryId_1_3 repId = (RepositoryId_1_3)super.get(key);

        if (repId != null)
            return repId;
        else {
            //repId = pool.popId().init(key);
	    repId = new RepositoryId_1_3(key);
            put(key, repId);
            return repId;
        }

    }
}


