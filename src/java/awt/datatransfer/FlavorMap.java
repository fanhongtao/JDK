/*
 * @(#)FlavorMap.java	1.9 98/04/14
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.datatransfer;

import java.util.Map;

/**
 * <p>
 * The FlavorMap is an interface to a map that maps platform native
 * type names (strings) to MIME type strings, and also their associated 
 * DataFlavors.
 * </p>
 * This map is used by the DnD system to map platform data types to MIME
 * types to enable the transfer of objects between Java and the platform via
 * the platform DnD System.
 * </p>
 *
 * @see SystemFlavorMap
 *
 * @version 1.9
 * @since JDK1.2
 *
 */

public interface FlavorMap {

    /**
     * map flavors to native data types names
     *
     * @param flavors The array of DataFlavors to map to native types, or null
     * 
     * @return a Map object which contains between 0 or more entries
     * with keys of type DataFlavor and values of type String, where the String
     * values mapped (if any) are the native (platform dependent) data type
     * name corresponding to the (platform independent) DataFlavor (MimeType).
     *
     * If the parameter is null then the Map returned should be the
     * complete map of all mappings between DataFlavors and their
     * corresponding native names known to the implementation at the time
     * of the call.
     *
     * The Map returned is mutable and considered to be owned by the caller,
     * thus allowing "nesting" of FlavorMap implementations.
     */

    Map getNativesForFlavors(DataFlavor[] flavors);

    /**
     * map natives to corresponding flavors
     *
     * @param native The array of String native types to map to DataFlavors, or null
     *
     * @return a Map object which contains 0 or more entries
     * with keys of type String and values of type DataFlavor, where the
     * DataFlavor values mapped (if any) are the (platform independent) types
     * corresponding to their native (platform dependent) data type names. 
     *
     * If the parameter is null then the map returned should be the
     * complete map of all mappings between native names and their
     * corresponding DataFlavors known to the implementation at the time
     * of the call.
     *
     * The Map returned is mutable and considered to be owned by the caller,
     * thus allowing "nesting" of FlavorMap implementations.
     */

    Map getFlavorsForNatives(String[] natives);
}
