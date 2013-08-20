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

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.w3c.dom.DOMException;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 *
 * @version $Id: DefaultNode.java,v 1.6 2003/06/10 18:09:39 elena Exp $
 */
public class DefaultNode implements Node {

    // default constructor
    public DefaultNode() {
    }
    
    //
    // org.w3c.dom.Node methods
    //
    
    // getter methods
    public String getNodeName() {
    	return null;
    }


    public String getNodeValue() throws DOMException {
    	return null;
    }


    public short getNodeType() {
    	return -1;
    }


    public Node getParentNode() {
    	return null;
    }


    public NodeList getChildNodes() {
    	return null;
    }


    public Node getFirstChild() {
    	return null;
    }


    public Node getLastChild() {
    	return null;
    }


    public Node getPreviousSibling() {
    	return null;
    }


    public Node getNextSibling() {
    	return null;
    }


    public NamedNodeMap getAttributes() {
    	return null;
    }


    public Document getOwnerDocument() {
    	return null;
    }


    public boolean hasChildNodes() {
    	return false;
    }


    public Node cloneNode(boolean deep) {
    	return null;
    }


    public void normalize() {
    }


    public boolean isSupported(String feature, String version) {
    	return false;
    }


    public String getNamespaceURI() {
    	return null;
    }


    public String getPrefix() {
    	return null;
    }


    public String getLocalName() {
    	return null;
    }
    /** DOM Level 3*/
    public String getBaseURI(){
        return null;
    }



    public boolean hasAttributes() {
    	return false;
    }
    
    // setter methods
    public void setNodeValue(String nodeValue) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Node removeChild(Node oldChild) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Node appendChild(Node newChild) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public void setPrefix(String prefix) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    public short compareDocumentPosition(Node other){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    public String getTextContent() throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    public void setTextContent(String textContent)throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    public boolean isSameNode(Node other){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");

    }
    public String lookupPrefix(String namespaceURI){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
                                        }
    public boolean isDefaultNamespace(String namespaceURI){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    public String lookupNamespaceURI(String prefix){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    public boolean isEqualNode(Node arg){
       throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");

    }
    
    public Object getFeature(String feature, String version){
        return null;
    }
    public Object setUserData(String key,  Object data, UserDataHandler handler){
       throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");                              
    }
    public Object getUserData(String key){
        return null;
    }


}
	
