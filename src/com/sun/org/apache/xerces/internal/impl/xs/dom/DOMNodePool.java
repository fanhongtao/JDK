/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2002 International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.dom;

import com.sun.org.apache.xerces.internal.dom.TextImpl;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;


/**
 * This class is pool that enables caching of DOM nodes, such as Element, Attr, 
 * Text, that are used to parse and later traverse XML Schemas.
 * The pool is reset before a new set of schemas is traversed.
 * Note: pool is not reset during traversals of imported/included
 * schemas.
 * 
 * @author Elena Litani, IBM
 * @version $Id: DOMNodePool.java,v 1.3 2002/11/20 00:49:47 twl Exp $
 */
public final class DOMNodePool {
    /** Chunk shift (8). */
    private static final int CHUNK_SHIFT = 8; // 2^8 = 256

    /** Chunk size (1 << CHUNK_SHIFT). */
    private static final int CHUNK_SIZE = 1 << CHUNK_SHIFT;

    /** Chunk mask (CHUNK_SIZE - 1). */
    private static final int CHUNK_MASK = CHUNK_SIZE - 1;

    /** Initial chunk count (). */
    private static final int INITIAL_CHUNK_COUNT = (1 << (10 - CHUNK_SHIFT)); // 2^10 = 1k

    /** Element nodes pool*/
    private ElementNSImpl fElements[][] = new ElementNSImpl[INITIAL_CHUNK_COUNT][];
    private int fElementIndex = 0;


    /** Text nodes pool*/
    private TextImpl fTextNode[][] = new TextImpl[INITIAL_CHUNK_COUNT][];
    private int fTextNodeIndex = 0;


    /** Attribute nodes pool*/
    private AttrNSImpl fAttrNode[][] = new AttrNSImpl[INITIAL_CHUNK_COUNT][];
    private int fAttrNodeIndex = 0;

    

    /**
     * This method creates a new element node or provides a 
     * free element node if such exists in the pool.
     * 
     * @return usable element node
     */
    public final  ElementNSImpl getElementNode(){
        int     chunk       = fElementIndex >> CHUNK_SHIFT;
        int     index       = fElementIndex &  CHUNK_MASK;
        ensureElementsCapacity(chunk);
        if (fElements[chunk][index] == null) {
            fElements[chunk][index] = new ElementNSImpl();
        } 
        fElementIndex++;
        return fElements[chunk][index];
    }

    private void ensureElementsCapacity(int chunk) {
        if (fElements.length <= chunk) {
            fElements = resize(fElements, fElements.length * 2);
        }
        else if (fElements[chunk] != null) {
            return;
        }

        fElements[chunk] = new ElementNSImpl[CHUNK_SIZE];
        return;
    }

    private static ElementNSImpl[][] resize(ElementNSImpl array[][], int newsize) {
        ElementNSImpl newarray[][] = new ElementNSImpl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    /**
     * This methods creates text node or provides a free
     * text node if such exists in the pool.
     * 
     * @return a usable TextNode
     */
    public final  TextImpl getTextNode(){
        int     chunk       = fTextNodeIndex >> CHUNK_SHIFT;
        int     index       = fTextNodeIndex &  CHUNK_MASK;
        ensureTextCapacity(chunk);
        if (fTextNode[chunk][index] == null) {
            fTextNode[chunk][index] = new TextImpl();
        } 
        fTextNodeIndex++;
        return fTextNode[chunk][index];
    }

    private void ensureTextCapacity(int chunk) {
        if (fTextNode.length <= chunk) {
            fTextNode = resize(fTextNode, fTextNode.length * 2);
        }
        else if (fTextNode[chunk] != null) {
            return;
        }

        fTextNode[chunk] = new TextImpl[CHUNK_SIZE];
        return;
    }

    private static TextImpl[][] resize(TextImpl array[][], int newsize) {
        TextImpl newarray[][] = new TextImpl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    /**
     * This methods creates attribute node or provides a free
     * attribute node if such exists in the pool.
     * 
     * @return a usable attribute node
     */
    public final  AttrNSImpl getAttrNode(){
        int     chunk       = fAttrNodeIndex >> CHUNK_SHIFT;
        int     index       = fAttrNodeIndex &  CHUNK_MASK;
        ensureAttrsCapacity(chunk);
        if (fAttrNode[chunk][index] == null) {
            fAttrNode[chunk][index] = new AttrNSImpl();
        } 
        fAttrNodeIndex++;
        return fAttrNode[chunk][index];
    }

    private void ensureAttrsCapacity(int chunk) {
        if (fAttrNode.length <= chunk) {
            fAttrNode = resize(fAttrNode, fAttrNode.length * 2);
        }
        else if (fAttrNode[chunk] != null) {
            return;
        }

        fAttrNode[chunk] = new AttrNSImpl[CHUNK_SIZE];
        return;
    }

    private static AttrNSImpl[][] resize(AttrNSImpl array[][], int newsize) {
        AttrNSImpl newarray[][] = new AttrNSImpl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }


    /**
     * Reset the pool. The nodes in the pool become 'free' nodes.
     */
    public void reset(){
        fElementIndex = 0;
        fTextNodeIndex = 0;
        fAttrNodeIndex = 0;
    }


}
