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
 * originally based on software copyright (c) 2002, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.util.Vector;

import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItemList;
/**
 * Containts a list of Object's.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: NSItemListImpl.java,v 1.3 2003/11/11 20:15:00 sandygao Exp $
 */
public class NSItemListImpl implements XSNamespaceItemList {

    // The array to hold all data
    private XSNamespaceItem[] fArray = null;
    // Number of elements in this list
    private int fLength = 0;

    // REVISIT: this is temp solution. In general we need to use this class
    //          instead of the Vector.
    private Vector fVector;

    public NSItemListImpl(Vector v) {
        fVector = v;        
        fLength = v.size();
    }

    /**
     * Construct an XSNamespaceItemList implementation
     * 
     * @param array     the data array
     * @param length    the number of elements
     */
    public NSItemListImpl(XSNamespaceItem[] array, int length) {
        fArray = array;
        fLength = length;
    }

    /**
     * The number of <code>Objects</code> in the list. The range of valid
     * child node indices is 0 to <code>length-1</code> inclusive.
     */
    public int getLength() {
        return fLength;
    }

    public XSNamespaceItem item(int index) {
        if (index < 0 || index >= fLength)
            return null;
        if (fVector != null) {
            return (XSNamespaceItem)fVector.elementAt(index);
        }
        return fArray[index];
    }

} // class XSParticle
