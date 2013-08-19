/*
 * @(#)CodeSetCache.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.core;

import java.util.Map;
import java.util.WeakHashMap;
import sun.io.*;

/**
 * Thread local cache of sun.io code set converters for performance.
 *
 * The thread local class contains a single reference to a Map[]
 * containing two WeakHashMaps.  One for CharToByteConverters and
 * one for ByteToCharConverters.  Constants are defined for
 * indexing.
 *
 * This is used internally by CodeSetConversion.
 */
class CodeSetCache
{
    /**
     * The ThreadLocal data is a 2 element Map array indexed
     * by BTC_CACHE_MAP and CTB_CACHE_MAP.
     */
    private ThreadLocal converterCaches = new ThreadLocal() {
        public java.lang.Object initialValue() {
            return new Map[] { new WeakHashMap(), new WeakHashMap() };
        }
    };

    /**
     * Index in the thread local converterCaches array for
     * the byte to char converter Map.  A key is the Java
     * name corresponding to the desired code set.
     */
    private static final int BTC_CACHE_MAP = 0;

    /** 
     * Index in the thread local converterCaches array for
     * the char to byte converter Map.  A key is the Java
     * name corresponding to the desired code set.
     */
    private static final int CTB_CACHE_MAP = 1;

    /**
     * Retrieve a ByteToCharConverter from the Map using the given key.
     */
    ByteToCharConverter getByteToCharConverter(Object key) {
        Map btcMap = ((Map[])converterCaches.get())[BTC_CACHE_MAP];
        
        return (ByteToCharConverter)btcMap.get(key);
    }

    /**
     * Retrieve a CharToByteConverter from the Map using the given key.
     */
    CharToByteConverter getCharToByteConverter(Object key) {
        Map ctbMap = ((Map[])converterCaches.get())[CTB_CACHE_MAP];

        return (CharToByteConverter)ctbMap.get(key);
    }

    /**
     * Stores the given ByteToCharConverter in the thread local cache,
     * and returns the same converter.
     */
    ByteToCharConverter setConverter(Object key,
                                     ByteToCharConverter converter) {
        Map btcMap = ((Map[])converterCaches.get())[BTC_CACHE_MAP];

        btcMap.put(key, converter);

        return converter;
    }

    /**
     * Stores the given CharToByteConverter in the thread local cache,
     * and returns the same converter.
     */
    CharToByteConverter setConverter(Object key,
                                     CharToByteConverter converter) {

        Map ctbMap = ((Map[])converterCaches.get())[CTB_CACHE_MAP];

        ctbMap.put(key, converter);

        return converter;
    }
}
