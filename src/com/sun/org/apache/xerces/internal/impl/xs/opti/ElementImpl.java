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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: ElementImpl.java,v 1.5 2003/07/24 15:14:42 neilg Exp $
 */
public class ElementImpl extends DefaultElement {

    SchemaDOM schemaDOM;
    Attr[] attrs;
    int row;
    int col;
    int parentRow;
    
    int line;
    int column;

    public ElementImpl(int line, int column) {
        row = -1;
        col = -1;
        parentRow = -1;
        nodeType = Node.ELEMENT_NODE;
        
        this.line = line;
        this.column = column;
    }
    
    
    public ElementImpl(String prefix, String localpart, String rawname,
                       String uri, int line, int column) {
    	super(prefix, localpart, rawname, uri, Node.ELEMENT_NODE);
    	row = -1;
        col = -1;
        parentRow = -1;

        this.line = line;
        this.column = column;
    }


    //
    // org.w3c.dom.Node methods
    //
    
    public Document getOwnerDocument() {
        return schemaDOM;
    }
    
    
    public Node getParentNode() {
        return schemaDOM.relations[row][0];
    }


    public boolean hasChildNodes() {
        if (parentRow == -1) {
            return false;
        }
        else {
            return true;
        }
    }


    public Node getFirstChild() {
        if (parentRow == -1) {
            return null;
        }
        return schemaDOM.relations[parentRow][1];
    }


    public Node getLastChild() {
        if (parentRow == -1) {
            return null;
        }
        int i=1;
        for (; i<schemaDOM.relations[parentRow].length; i++) {
            if (schemaDOM.relations[parentRow][i] == null) {
                return schemaDOM.relations[parentRow][i-1];
            }
        }
        if (i ==1) {
            i++;
        }
        return schemaDOM.relations[parentRow][i-1];
    }


    public Node getPreviousSibling() {
        if (col == 1) {
            return null;
        }
        return schemaDOM.relations[row][col-1];
    }


    public Node getNextSibling() {
        if (col == schemaDOM.relations[row].length-1) {
            return null;
        }
        return schemaDOM.relations[row][col+1];
    }


    public NamedNodeMap getAttributes() {
        return new NamedNodeMapImpl(attrs);
    }


    public boolean hasAttributes() {
        return (attrs.length == 0 ? false : true);
    }
    

    
    //
    // org.w3c.dom.Element methods
    //
    
    public String getTagName() {
        return rawname;
    }


    public String getAttribute(String name) {
    
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getName().equals(name)) {
                return attrs[i].getValue();
            }
        }
        return "";
    }


    public Attr getAttributeNode(String name) {
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getName().equals(name)) {
                return attrs[i];
            }
        }
        return null;
    }


    public String getAttributeNS(String namespaceURI, String localName) {
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getLocalName().equals(localName) && attrs[i].getNamespaceURI().equals(namespaceURI)) {
                return attrs[i].getValue();
            }
        }
        return "";
    }


    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getName().equals(localName) && attrs[i].getNamespaceURI().equals(namespaceURI)) {
                return attrs[i];
            }
        }
        return null;
    }


    public boolean hasAttribute(String name) {
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    public boolean hasAttributeNS(String namespaceURI, String localName) {
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getName().equals(localName) && attrs[i].getNamespaceURI().equals(namespaceURI)) {
                return true;
            }
        }
        return false;
    }
    
    
    public void setAttribute(String name, String value) {
        for (int i=0; i<attrs.length; i++) {
            if (attrs[i].getName().equals(name)) {
                attrs[i].setValue(value);
                return;
            }
        }
    }

    /** Returns the line number. */
    public int getLineNumber() {
        return line;
    }

    /** Returns the column number. */
    public int getColumnNumber() {
        return column;
    }

}
