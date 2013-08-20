/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Text;
import org.w3c.dom.DOMException;

/*
 * @author Neil Graham, IBM
 * @version $Id: DefaultText.java,v 1.3 2003/11/18 00:22:58 kk122374 Exp $
 */
/**
 * The <code>Text</code> interface inherits from <code>CharacterData</code> 
 * and represents the textual content (termed character data in XML) of an 
 * <code>Element</code> or <code>Attr</code>. If there is no markup inside 
 * an element's content, the text is contained in a single object 
 * implementing the <code>Text</code> interface that is the only child of 
 * the element. If there is markup, it is parsed into the information items 
 * (elements, comments, etc.) and <code>Text</code> nodes that form the list 
 * of children of the element.
 * <p>When a document is first made available via the DOM, there is only one 
 * <code>Text</code> node for each block of text. Users may create adjacent 
 * <code>Text</code> nodes that represent the contents of a given element 
 * without any intervening markup, but should be aware that there is no way 
 * to represent the separations between these nodes in XML or HTML, so they 
 * will not (in general) persist between DOM editing sessions. The 
 * <code>normalize()</code> method on <code>Node</code> merges any such 
 * adjacent <code>Text</code> objects into a single node for each block of 
 * text.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Core-20001113'>Document Object Model (DOM) Level 2 Core Specification</a>.
 *
 * This is an empty implementation.
 */
public class DefaultText extends NodeImpl implements Text {

    // CharacterData methods

    /**
     * The character data of the node that implements this interface. The DOM 
     * implementation may not put arbitrary limits on the amount of data 
     * that may be stored in a <code>CharacterData</code> node. However, 
     * implementation limits may mean that the entirety of a node's data may 
     * not fit into a single <code>DOMString</code>. In such cases, the user 
     * may call <code>substringData</code> to retrieve the data in 
     * appropriately sized pieces.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @exception DOMException
     *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than 
     *   fit in a <code>DOMString</code> variable on the implementation 
     *   platform.
     */
    public String getData()
                            throws DOMException {
        return null;
    }

    /**
     * The character data of the node that implements this interface. The DOM 
     * implementation may not put arbitrary limits on the amount of data 
     * that may be stored in a <code>CharacterData</code> node. However, 
     * implementation limits may mean that the entirety of a node's data may 
     * not fit into a single <code>DOMString</code>. In such cases, the user 
     * may call <code>substringData</code> to retrieve the data in 
     * appropriately sized pieces.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @exception DOMException
     *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than 
     *   fit in a <code>DOMString</code> variable on the implementation 
     *   platform.
     */
    public void setData(String data)
                            throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * The number of 16-bit units that are available through <code>data</code> 
     * and the <code>substringData</code> method below. This may have the 
     * value zero, i.e., <code>CharacterData</code> nodes may be empty.
     */
    public int getLength() {
        return 0;
    }

    /**
     * Extracts a range of data from the node.
     * @param offset Start offset of substring to extract.
     * @param count The number of 16-bit units to extract.
     * @return The specified substring. If the sum of <code>offset</code> and 
     *   <code>count</code> exceeds the <code>length</code>, then all 16-bit 
     *   units to the end of the data are returned.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is 
     *   negative or greater than the number of 16-bit units in 
     *   <code>data</code>, or if the specified <code>count</code> is 
     *   negative.
     *   <br>DOMSTRING_SIZE_ERR: Raised if the specified range of text does 
     *   not fit into a <code>DOMString</code>.
     */
    public String substringData(int offset, 
                                int count)
                                throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * Append the string to the end of the character data of the node. Upon 
     * success, <code>data</code> provides access to the concatenation of 
     * <code>data</code> and the <code>DOMString</code> specified.
     * @param arg The <code>DOMString</code> to append.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void appendData(String arg)
                           throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * Insert a string at the specified 16-bit unit offset.
     * @param offset The character offset at which to insert.
     * @param arg The <code>DOMString</code> to insert.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is 
     *   negative or greater than the number of 16-bit units in 
     *   <code>data</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void insertData(int offset, 
                           String arg)
                           throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * Remove a range of 16-bit units from the node. Upon success, 
     * <code>data</code> and <code>length</code> reflect the change.
     * @param offset The offset from which to start removing.
     * @param count The number of 16-bit units to delete. If the sum of 
     *   <code>offset</code> and <code>count</code> exceeds 
     *   <code>length</code> then all 16-bit units from <code>offset</code> 
     *   to the end of the data are deleted.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is 
     *   negative or greater than the number of 16-bit units in 
     *   <code>data</code>, or if the specified <code>count</code> is 
     *   negative.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void deleteData(int offset, 
                           int count)
                           throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * Replace the characters starting at the specified 16-bit unit offset 
     * with the specified string.
     * @param offset The offset from which to start replacing.
     * @param count The number of 16-bit units to replace. If the sum of 
     *   <code>offset</code> and <code>count</code> exceeds 
     *   <code>length</code>, then all 16-bit units to the end of the data 
     *   are replaced; (i.e., the effect is the same as a <code>remove</code>
     *    method call with the same range, followed by an <code>append</code>
     *    method invocation).
     * @param arg The <code>DOMString</code> with which the range must be 
     *   replaced.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified <code>offset</code> is 
     *   negative or greater than the number of 16-bit units in 
     *   <code>data</code>, or if the specified <code>count</code> is 
     *   negative.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void replaceData(int offset, 
                            int count, 
                            String arg)
                            throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    // Text node methods
    /**
     * Breaks this node into two nodes at the specified <code>offset</code>, 
     * keeping both in the tree as siblings. After being split, this node 
     * will contain all the content up to the <code>offset</code> point. A 
     * new node of the same type, which contains all the content at and 
     * after the <code>offset</code> point, is returned. If the original 
     * node had a parent node, the new node is inserted as the next sibling 
     * of the original node. When the <code>offset</code> is equal to the 
     * length of this node, the new node has no data.
     * @param offset The 16-bit unit offset at which to split, starting from 
     *   <code>0</code>.
     * @return The new node, of the same type as this node.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or greater 
     *   than the number of 16-bit units in <code>data</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public Text splitText(int offset)
                          throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    public boolean isWhitespaceInElementContent() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    /** DOM Level 3 CR */
    public boolean isElementContentWhitespace(){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    public String getWholeText() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    public Text replaceWholeText(String content)
                                 throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


}
