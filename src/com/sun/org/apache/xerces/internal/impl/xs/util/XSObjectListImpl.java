/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation.  All rights
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
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

/**
 * Containts a list of XSObject's.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSObjectListImpl.java,v 1.7 2003/11/11 20:15:00 sandygao Exp $
 */
public class XSObjectListImpl implements XSObjectList {

    private final int DEFAULT_SIZE = 4;

    // The array to hold all data
    private XSObject[] fArray = null;
    // Number of elements in this list
    private int fLength = 0;
    


    public XSObjectListImpl(){
        fArray = new XSObject[DEFAULT_SIZE];
        fLength = 0;
    }

    /**
     * Construct an XSObjectList implementation
     * 
     * @param array     the data array
     * @param length    the number of elements
     */
    public XSObjectListImpl(XSObject[] array, int length) {
        fArray = array;
        fLength = length;
    }

    /**
     * The number of <code>XSObjects</code> in the list. The range of valid
     * child node indices is 0 to <code>length-1</code> inclusive.
     */
    public int getLength() {
        return fLength;
    }

    /**
     * Returns the <code>index</code>th item in the collection. The index
     * starts at 0. If <code>index</code> is greater than or equal to the
     * number of nodes in the list, this returns <code>null</code>.
     * @param index index into the collection.
     * @return The XSObject at the <code>index</code>th position in the
     *   <code>XSObjectList</code>, or <code>null</code> if that is not a
     *   valid index.
     */
    public XSObject item(int index) {
        if (index < 0 || index >= fLength)
            return null;
        return fArray[index];
    }

    // clear this object
    public void clear() {
        for (int i=0; i<fLength; i++) {
            fArray[i] = null;
        }
        fArray = null;
        fLength = 0;
    }
    
    public void add (XSObject object){
       if (fLength == fArray.length){  
           XSObject[] temp = new XSObject[fLength + 4];
           System.arraycopy(fArray, 0, temp, 0, fLength);
           fArray = temp;
       }
       fArray[fLength++]=object;
    }
    public void add (int index, XSObject object){
        fArray [index] = object;
    }

} // class XSObjectList
