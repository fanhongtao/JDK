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

import org.w3c.dom.TypeInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.w3c.dom.DOMException;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 *
 * @version $Id: DefaultElement.java,v 1.5 2003/01/16 22:53:45 elena Exp $
 */
public class DefaultElement extends NodeImpl 
                            implements Element {

    // default constructor
    public DefaultElement() {
    }
    
    
    public DefaultElement(String prefix, String localpart, String rawname, String uri, short nodeType) {
    	super(prefix, localpart, rawname, uri, nodeType);
    }
    
    
    //
    // org.w3c.dom.Element methods
    //
    
    // getter methods
    public String getTagName() {
    	return null;
    }


    public String getAttribute(String name) {
    	return null;
    }


    public Attr getAttributeNode(String name) {
    	return null;
    }


    public NodeList getElementsByTagName(String name) {
    	return null;
    }


    public String getAttributeNS(String namespaceURI, String localName) {
    	return null;
    }


    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
    	return null;
    }


    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
    	return null;
    }


    public boolean hasAttribute(String name) {
    	return false;
    }


    public boolean hasAttributeNS(String namespaceURI, String localName) {
    	return false;
    }
    
    public TypeInfo getSchemaTypeInfo(){
      return null;
    }
    

    // setter methods
    public void setAttribute(String name, String value) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public void removeAttribute(String name) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    public void setIdAttributeNode(Attr at, boolean makeId) throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    public void setIdAttribute(String name, boolean makeId) throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    public void setIdAttributeNS(String namespaceURI, String localName,
                                    boolean makeId) throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

}
	

