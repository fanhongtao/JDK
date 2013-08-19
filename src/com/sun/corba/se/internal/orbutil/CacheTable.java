/*
 * @(#)CacheTable.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
                    
package com.sun.corba.se.internal.orbutil;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

/**
 *  _REVISIT_: Replace CacheTable with a better data structure, the searches
 *  are linear, can be a performance bottleneck while deserializng an object
 *  with lot of indirections.
 */
public class CacheTable  {
	
    private static final int kGrowthRate = 10;

    // Set kMaxCacheSize = 3 * Max simultaneous calls in order to get optimal max
    private static final int kMaxCacheSize = 30; 
    private static final int kKeysPos = 0;
    private static final int kValsPos = 1;
    private static final Object cache[][] = new Object[kMaxCacheSize + 1][2];
    private static int cacheIndex = -1;

    private java.lang.Object keys[] = null;
    private int vals[] = null;
    private int index = 0;
    private int lastFoundIndex = 0;
	
    private FastCacheTable hashedTable = null;
	
    private CacheTable(){}

    public CacheTable(boolean useHashing){
	if (useHashing)
	    hashedTable = new FastCacheTable();
    }

    public final void put(java.lang.Object key, int val){
	if (hashedTable != null) {
	    hashedTable.put(key, val);
	    return;
	}

	if (key == null)
	    return;

	if (!containsKey(key)) {
	    checkForGrowth();
	    keys[index] = key;
	    vals[index] = val;
	    index++;
	} else {
            // If there is an entry in the Cachetable with the same key, then
            // check to see if the Indirection(value) is different. If it is
            // different then we have a bug in our code. This check is added
            // to make sure that our serialization code doesn't do any 
            // unintended optimization w.r.t object aliasing.
            int oldValue = this.getVal( key );
            if( oldValue != val ) {
                throw new INTERNAL( MinorCodes.DUPLICATE_INDIRECTION_OFFSET,
                    CompletionStatus.COMPLETED_NO );
            }
        }
    }

    public final boolean containsKey(java.lang.Object key){
				
	if (hashedTable != null)
	    return hashedTable.containsKey(key);

	for (int i = lastFoundIndex; i < index; i++){
	    if (keys[i] == key) {
		lastFoundIndex = i;
		return true;

	    }
	}

	for (int i = 0; i < lastFoundIndex; i++){
	    if (keys[i] == key) {
		lastFoundIndex = i;
		return true;
	    }
	}
	
	return false;
    }

    public final int getVal(java.lang.Object key){
	if (hashedTable != null)
	    return hashedTable.getVal(key);

	for (int i = lastFoundIndex; i < index; i++){
	    if (keys[i] == key) {
		lastFoundIndex = i;
		return vals[i];
				
	    }
	}

	for (int i = 0; i < lastFoundIndex; i++){
	    if (keys[i] == key) {
		lastFoundIndex = i;
		return vals[i];
	    }
	}

	return -1;

    }

    public final boolean containsVal(int val){

	for (int i = lastFoundIndex; i < index; i++){
	    if (vals[i] == val) {
		lastFoundIndex = i;
		return true;
			
	    }
	}

	for (int i = 0; i < lastFoundIndex; i++){
	    if (vals[i] == val) {
		lastFoundIndex = i;
		return true;
	    }
	}

	return false;
		
    }

    public final boolean containsOrderedVal(int val){
		
    int low, high, mid, midVal;

	low = 0;
	high = index;
		
	while (low <= high) {
	    mid =(low + high)/2;
	    midVal = vals[mid];
			
	    if (midVal < val)
		low = mid + 1;
	    else if (midVal > val)
		high = mid - 1;
	    else {
		lastFoundIndex = mid;
		return true; // key found
	    }
	}		

	/*	
		for (int i = lastFoundIndex; i < index; i++){
		if (vals[i] == val) {
		lastFoundIndex = i;
		return true;
			
		}
		}

		for (int i = 0; i < lastFoundIndex; i++){
		if (vals[i] == val) {
		lastFoundIndex = i;
		return true;
		}
		}
	*/
	return false;
    }
	
    public final java.lang.Object getKey(int val){
	for (int i = lastFoundIndex; i < index; i++){
	    if (vals[i] == val) {
		lastFoundIndex = i;
		return keys[i];
	    }
	}

	for (int i = 0; i < lastFoundIndex; i++){
	    if (vals[i] == val) {
		lastFoundIndex = i;
		return keys[i];
	    }
	}

	return null;
    }

    public void done(){
	if (hashedTable != null) {
	    hashedTable.done();
	    return;
	}

	synchronized (cache) {
	    if (cacheIndex < kMaxCacheSize) {
		cacheIndex++;
		cache[cacheIndex][kKeysPos] = keys;
		cache[cacheIndex][kValsPos] = vals;

	    }
	}
	keys = null;
	vals = null;

    }

    private final void checkForGrowth(){
	if (keys == null) {
	    synchronized (cache) {
	    if (cacheIndex > -1) {
		keys = (java.lang.Object[])cache[cacheIndex][kKeysPos];
		vals = (int[])cache[cacheIndex][kValsPos];
		cacheIndex--;
		return;
	    }
	}
			
	keys =  new java.lang.Object[kGrowthRate];
	vals = new int[kGrowthRate];

	} else if (keys.length == index) {

	    java.lang.Object newKeys[] = new java.lang.Object[index + kGrowthRate];
	    int newVals[] = new int[index + kGrowthRate];
			
	    System.arraycopy(keys, 0, newKeys, 0, index);
	    System.arraycopy(vals, 0, newVals, 0, index);
		
	    synchronized (cache) {

		if (cacheIndex < kMaxCacheSize) {
		    cacheIndex++;
		    cache[cacheIndex][kKeysPos] = keys;
		    cache[cacheIndex][kValsPos] = vals;

		}
	    }
			
	    keys = newKeys;
	    vals = newVals;
			
	}
    }
}


class Collision {
    java.lang.Object key;
    int val;
    int hash;
    Collision next;
}

class FastCacheTable  {

    static final int kGrowthRate = 101;

    // Set kMaxCacheSize = 3 * Max simultaneous calls in order to get optimal max
    static final int kMaxCacheSize = 30; 
    static final int kKeysPos = 0;
    static final int kValsPos = 1;
    static final int kHashCodesPos = 2;
    static final int kCollisionsPos = 3;
    static final Object cache[][] = new Object[kMaxCacheSize + 1][4];
    static int cacheIndex = -1;

    java.lang.Object keys[] = null;
    int vals[];
    int hashCodes[];
    Collision collisions[];
    int count;
    int threshold;

    public FastCacheTable(){}

    public final void put(java.lang.Object key, int val){
	checkForGrowth();

	int hash = System.identityHashCode(key);
	int index = (hash & 0x7FFFFFFF) % keys.length;
	if (keys[index] == null) {
	    keys[index] = key;
	    vals[index] = val;
	    hashCodes[index] = hash;
	    if (collisions != null)
		collisions[index] = null;
	} else {
	    Collision c = new Collision();
	    c.key = key;
	    c.val = val;
	    c.hash = hash;
	    if (collisions == null)
		collisions = new Collision[keys.length];
	    c.next = collisions[index];
	    collisions[index] = c;
	}
	count++;
}

public final boolean containsKey(java.lang.Object key){
    int hash = System.identityHashCode(key);
    int index = (hash & 0x7FFFFFFF) % keys.length;
    if (keys[index] == null)
	return false;
    if (keys[index] == key)
	return true;
    Collision c = (collisions == null) ? null : collisions[index];
    if (c == null)
	return false;
    do {
	if (c.key == key)
	    return true;
	c = c.next;
    } while (c != null);
    return false;
}

public final int getVal(java.lang.Object key){
    int hash = System.identityHashCode(key);
    int index = (hash & 0x7FFFFFFF) % keys.length;
    if (keys[index] == null)
	return -1;
    if (keys[index] == key)
	return vals[index];
    Collision c = (collisions == null) ? null : collisions[index];
    if (c == null)
	return -1;
    do {
	if (c.key == key)
	    return c.val;
	c = c.next;
    } while (c != null);
    return -1;
}

public void done(){
    synchronized (cache) {
	if (cacheIndex < kMaxCacheSize) {
	    cacheIndex++;
	    cache[cacheIndex][kKeysPos] = keys;
	    cache[cacheIndex][kValsPos] = vals;
	    cache[cacheIndex][kHashCodesPos] = hashCodes;
	    cache[cacheIndex][kCollisionsPos] = collisions;
	}
    }
    keys = null;
    vals = null;
    hashCodes = null;
    collisions = null;
}

private final void checkForGrowth(){
    if (keys == null) {
	count = 0;
	synchronized (cache) {
	    if (cacheIndex > -1) {
		keys = (java.lang.Object[])cache[cacheIndex][kKeysPos];
		vals = (int[])cache[cacheIndex][kValsPos];
		hashCodes = (int[])cache[cacheIndex][kHashCodesPos];
		collisions = (Collision[])cache[cacheIndex][kCollisionsPos];
		for (int i = keys.length; i > 0; i--) {
		    keys[i-1] = null;
		}
		cacheIndex--;
		threshold = (3 * keys.length) / 4;
		return;
	    }
	}
	keys =  new java.lang.Object[kGrowthRate];
	vals = new int[kGrowthRate];
	hashCodes = new int[kGrowthRate];
	collisions = null;
	threshold = (3 * keys.length) / 4;
    }
    else if (threshold == count) {
	int size = keys.length;
	int newSize = size + kGrowthRate;
	java.lang.Object newKeys[] = new java.lang.Object[newSize];
	int newVals[] = new int[newSize];
	int newHashCodes[] = new int[newSize];
	Collision newCollisions[] = null;
	for (int i = 0; i < size; i++) {
	    if (keys[i] != null) {
		int hash = hashCodes[i];
		int index = (hash & 0x7FFFFFFF) % newSize;
		if (newKeys[index] == null) {
		    newKeys[index] = keys[i];
		    newVals[index] = vals[i];
		    newHashCodes[index] = hash;
		    if (newCollisions != null)
			newCollisions[index] = null;
		} else {
		    Collision c = new Collision();
		    c.key = keys[i];
		    c.val = vals[i];
		    c.hash = hash;
		    if (newCollisions == null)
			newCollisions = new Collision[newSize];
		    c.next = newCollisions[index];
		    newCollisions[index] = c;
		}
		if (collisions == null)
		    continue;
		for (Collision c = collisions[i]; c != null; c = c.next) {
		    hash = c.hash;
		    index = (hash & 0x7FFFFFFF) % newSize;
		    if (newKeys[index] == null) {
			newKeys[index] = c.key;
			newVals[index] = c.val;
			newHashCodes[index] = hash;
			if (newCollisions != null)
			    newCollisions[index] = null;
		    } else {
			Collision c2 = new Collision();
			c2.key = c.key;
			c2.val = c.val;
			c2.hash = hash;
			if (newCollisions == null)
			    newCollisions = new Collision[newSize];
			c2.next = newCollisions[index];
			newCollisions[index] = c2;
		    }
		}
	    }
	}

	synchronized (cache) {

	    if (cacheIndex < kMaxCacheSize) {
		cacheIndex++;
		cache[cacheIndex][kKeysPos] = keys;
		cache[cacheIndex][kValsPos] = vals;
		cache[cacheIndex][kHashCodesPos] = hashCodes;
		cache[cacheIndex][kCollisionsPos] = collisions;
	    }
	}

	keys = newKeys;
	vals = newVals;
	hashCodes = newHashCodes;
	collisions = newCollisions;
	threshold = (3 * keys.length) / 4;
    }
}
}
