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

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/*
 * @author Neil Graham, IBM
 * @version $Id: TextImpl.java,v 1.2 2003/07/04 13:47:49 mrglavas Exp $
 */

public class TextImpl extends DefaultText {

    // Data
    String fData = null;
    SchemaDOM fSchemaDOM = null;
    int fRow;
    int fCol;

    public TextImpl(StringBuffer str, SchemaDOM sDOM, int row, int col) {
        fData = str.toString();
        fSchemaDOM = sDOM;
        fRow = row;
        fCol = col;
        rawname = prefix = localpart = uri = null;
        nodeType = Node.TEXT_NODE;
    }

    //
    // org.w3c.dom.Node methods
    //
    
    public Node getParentNode() {
        return fSchemaDOM.relations[fRow][0];
    }

    public Node getPreviousSibling() {
        if (fCol == 1) {
            return null;
        }
        return fSchemaDOM.relations[fRow][fCol-1];
    }


    public Node getNextSibling() {
        if (fCol == fSchemaDOM.relations[fRow].length-1) {
            return null;
        }
        return fSchemaDOM.relations[fRow][fCol+1];
    }

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
        return fData;
    }

    /**
     * The number of 16-bit units that are available through <code>data</code> 
     * and the <code>substringData</code> method below. This may have the 
     * value zero, i.e., <code>CharacterData</code> nodes may be empty.
     */
    public int getLength() {
        if(fData == null) return 0;
        return fData.length();
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
        if(fData == null) return null;
        if(count < 0 || offset < 0 || offset > fData.length()) 
    	    throw new DOMException(DOMException.INDEX_SIZE_ERR, "parameter error");
        if(offset+count >= fData.length()) 
            return fData.substring(offset);
        return fData.substring(offset, offset+count);
    }

}
