/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

/**
 * Containts the map between qnames and XSObject's.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSNamedMap4Types.java,v 1.5 2003/11/11 20:15:00 sandygao Exp $
 */
public class XSNamedMap4Types extends XSNamedMapImpl {

    // the type of component stored here: complex or simple type
    short fType;
    
    /**
     * Construct an XSNamedMap implmentation for one namespace
     * 
     * @param namespace the namespace to which the components belong
     * @param map       the map from local names to components
     * @param type      the type of components
     */
    public XSNamedMap4Types(String namespace, SymbolHash map, short type) {
        super(namespace, map);
        fType = type;
    }

    /**
     * Construct an XSNamedMap implmentation for a list of namespaces
     * 
     * @param namespaces the namespaces to which the components belong
     * @param maps       the maps from local names to components
     * @param num        the number of namespaces
     * @param type      the type of components
     */
    public XSNamedMap4Types(String[] namespaces, SymbolHash[] maps, int num, short type) {
        super(namespaces, maps, num);
        fType = type;
    }

    /**
     * The number of <code>XSObjects</code> in the <code>XSObjectList</code>. The
     * range of valid child node indices is 0 to <code>length-1</code>
     * inclusive.
     */
    public synchronized int getLength() {
        if (fLength == -1) {
            // first get the number of components for all types
            int length = 0;
            for (int i = 0; i < fNSNum; i++)
                length += fMaps[i].getLength();
            // then copy all types to an temporary array
            int pos = 0;
            XSObject[] array = new XSObject[length];
            for (int i = 0; i < fNSNum; i++) {
                pos += fMaps[i].getValues(array, pos);
            }
            // then copy either simple or complex types to fArray,
            // depending on which kind is required
            fLength = 0;
            fArray = new XSObject[length];
            XSTypeDefinition type;
            for (int i = 0; i < length; i++) {
                type = (XSTypeDefinition)array[i];
                if (type.getTypeCategory() == fType) {
                    fArray[fLength++] = type;
                }
            }
        }
        return fLength;
    }

    /**
     * Retrieves an <code>XSObject</code> specified by local name and namespace
     * URI.
     * @param namespace The namespace URI of the <code>XSObject</code> to
     *   retrieve.
     * @param localName The local name of the <code>XSObject</code> to retrieve.
     * @return A <code>XSObject</code> (of any type) with the specified local
     *   name and namespace URI, or <code>null</code> if they do not
     *   identify any <code>XSObject</code> in this map.
     */
    public XSObject itemByName(String namespace, String localName) {
        if (namespace != null)
            namespace = namespace.intern();
        for (int i = 0; i < fNSNum; i++) {
            if (namespace == fNamespaces[i]) {
                XSTypeDefinition type = (XSTypeDefinition)fMaps[i].get(localName);
                // only return it if it mataches the required type
                if (type.getTypeCategory() == fType)
                    return type;
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the <code>index</code>th item in the map. The index starts at
     * 0. If <code>index</code> is greater than or equal to the number of
     * nodes in the list, this returns <code>null</code>.
     * @param index The position in the map from which the item is to be
     *   retrieved.
     * @return The <code>XSObject</code> at the <code>index</code>th position
     *   in the <code>XSNamedMap</code>, or <code>null</code> if that is
     *   not a valid index.
     */
    public synchronized XSObject item(int index) {
        if (fArray == null) {
            getLength();
        }
        if (index < 0 || index >= fLength)
            return null;
        return fArray[index];
    }
    
} // class XSNamedMapImpl
