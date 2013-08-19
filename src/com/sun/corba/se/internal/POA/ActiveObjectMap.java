/*
 * @(#)ActiveObjectMap.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import org.omg.PortableServer.Servant;

import java.util.*;

/** The ActiveObjectMap is essentially a hashtable which maps object-ids 
 *  (which are byte arrays) to servants (which are of type Servant). 
 *  Currently it just uses java.util.Hashtable internally, and 
 *  will be optimized later. 
 */

final class ActiveObjectMap
{
    private Hashtable map;
    private Vector multipleIDs;

    public ActiveObjectMap(boolean trackMultipleIDS) {
	map = new Hashtable();
	if (trackMultipleIDS)
	    multipleIDs = new Vector();
    }

    public synchronized final boolean contains(Servant value) {
	if (value == null)
	    throw new NullPointerException();
        Enumeration contents = map.elements();
        while (contents.hasMoreElements()) {
            Servant s = (Servant) contents.nextElement();
            if (s == value)
                return true;
        }
        return false;
    }

    public synchronized final boolean containsKey(byte[] key) {
	return map.containsKey(new Key(key));
    }

    public synchronized final Servant get(byte[] key) {
	return (Servant)map.get(new Key(key));
    }

    public synchronized final byte[] getKey(Servant value)
    {
	Enumeration keys = map.keys();
	while ( keys.hasMoreElements() ) {
	    Object key = keys.nextElement();
	    Object element = map.get(key);
	    if ( element.equals(value) ) 
		return ((Key)key).id;
	}
	return null;
    }

    public synchronized final Object put(byte[] key, Servant value) {
	if (this.contains(value) && multipleIDs != null)
	    if (!multipleIDs.contains(value))
		multipleIDs.addElement(value);
	return map.put(new Key(key), value);
    }

    public synchronized final boolean hasMultipleIDs(Servant value) {
	if (multipleIDs == null)
	    return false;
	if (multipleIDs.contains(value))
	    return true;
	return false;
    }

    public synchronized final Object remove(byte[] key) {
	Servant s = (Servant) map.remove(new Key(key));
	if (s == null)
	    return s;
	if (s != null && multipleIDs != null && !this.contains(s))
	    multipleIDs.removeElement(s);
	return s;
    }

    public synchronized final void clear() {
        map.clear();
        if (multipleIDs != null) {
            multipleIDs.clear();
            multipleIDs = null;
        }
    }

    public synchronized Enumeration keys()
    {
	int size = map.size();
	Object[] objs = new Object[size];
	Enumeration ks = map.keys();
	for ( int i=0; i<size; i++ ) {
	    Key key = (Key)ks.nextElement(); 	
	    objs[i] = key.id;
	}
	return new EnumerationImpl(objs);
    }
}


class EnumerationImpl implements Enumeration 
{
    int index;
    int size;
    Object[] objArray;
    
    EnumerationImpl(Object[] objs)
    {
	objArray = objs;
	size = objs.length;		
	index = 0;
    }

    public boolean hasMoreElements()
    {
	if ( index < size )
	    return true;
	else
	    return false;
    }

    public Object nextElement()
    {
	Object obj = objArray[index];
	index++;
	return obj;
    }
}
    

class Key {
    byte[] id;

    Key(byte[] id) {
	this.id = id;
    }
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	for(int i = 0; i < id.length; i++) {
	    buffer.append(Integer.toString((int) id[i], 16));
	    if (i != id.length-1)
		buffer.append(":");
	}
	return buffer.toString();
    }

    public boolean equals(java.lang.Object key) {
	if (!(key instanceof Key))
	    return false;
	Key k = (Key) key;
	if (k.id.length != this.id.length)
	    return false;
	for(int i = 0; i < this.id.length; i++)
	    if (this.id[i] != k.id[i])
		return false;
	return true;
    }

    // Use the same hash function as what exists for Strings
    public int hashCode() {
	int h = 0;
	for (int i = 0; i < id.length; i++)
	    h = 31*h + id[i];
	return h;
    }
}
