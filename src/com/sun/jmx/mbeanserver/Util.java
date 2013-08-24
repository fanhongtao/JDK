/*
 * @(#)Util.java	1.3 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class Util {
    static <K, V> Map<K, V> newMap() {
        return new HashMap<K, V>();
    }
    
    static <K, V> Map<K, V> newSynchronizedMap() {
        return Collections.synchronizedMap(Util.<K, V>newMap());
    }
    
    static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }
    
    static <K, V> Map<K, V> newSynchronizedIdentityHashMap() {
        Map<K, V> map = newIdentityHashMap();
        return Collections.synchronizedMap(map);
    }
    
    static <K, V> SortedMap<K, V> newSortedMap() {
        return new TreeMap<K, V>();
    }
    
    static <K, V> SortedMap<K, V> newSortedMap(Comparator<? super K> comp) {
        return new TreeMap<K, V>(comp);
    }
    
    static <E> Set<E> newSet() {
        return new HashSet<E>();
    }
    
    static <E> Set<E> newSet(Collection<E> c) {
        return new HashSet<E>(c);
    }
    
    static <E> List<E> newList() {
        return new ArrayList<E>();
    }
    
    static <E> List<E> newList(Collection<E> c) {
        return new ArrayList<E>(c);
    }
}
