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

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import org.w3c.dom.DOMException;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 *
 * @version $Id: NamedNodeMapImpl.java,v 1.2 2002/12/13 17:23:24 sandygao Exp $
 */
public class NamedNodeMapImpl implements NamedNodeMap {

	Attr[] attrs;
	
	public NamedNodeMapImpl(Attr[] attrs) {
		this.attrs = attrs;
	}
	
	public Node getNamedItem(String name) {
		for (int i=0; i<attrs.length; i++) {
    			if (attrs[i].getName().equals(name)) {
    				return attrs[i];
    			}
    		}
    		return null;
	}
	
	public Node item(int index) {
		if (index < 0 && index > getLength()) {
			return null;
		}
		return attrs[index];
	}
	
	public int getLength() {
		return attrs.length;
	}
	
	public Node getNamedItemNS(String namespaceURI, String localName) {
		for (int i=0; i<attrs.length; i++) {
    			if (attrs[i].getName().equals(localName) && attrs[i].getNamespaceURI().equals(namespaceURI)) {
    				return attrs[i];
    			}
    		}
    		return null;
	}
	
	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
	}
	
	public Node setNamedItem(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
	}
	
	public Node removeNamedItem(String name) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
	}

	public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
	}
}

