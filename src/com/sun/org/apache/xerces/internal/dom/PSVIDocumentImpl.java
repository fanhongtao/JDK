/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2004 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// REVISIT: This is a HACK! DO NOT MODIFY THIS import.
//          It allows us to expose DOM L3 implemenation via org.w3c.dom packages
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.*;

/**
 * Our own document implementation, which knows how to create an element
 * with PSVI information.
 * 
 * @author Sandy Gao, IBM
 * 
 * @version $Id: PSVIDocumentImpl.java,v 1.9 2004/04/15 18:38:22 mrglavas Exp $
 */
public class PSVIDocumentImpl extends DocumentImpl {
   
    /** Serialization version. */
    static final long serialVersionUID = -8822220250676434522L;

    /**
     * Create a document.
     */
    public PSVIDocumentImpl() {
        super();
    }

    /**
     * For DOM2 support.
     * The createDocument factory method is in DOMImplementation.
     */
    public PSVIDocumentImpl(DocumentType doctype) {
        super(doctype);
    }
    
    /**
     * Deep-clone a document, including fixing ownerDoc for the cloned
     * children. Note that this requires bypassing the WRONG_DOCUMENT_ERR
     * protection. I've chosen to implement it by calling importNode
     * which is DOM Level 2.
     *
     * @return org.w3c.dom.Node
     * @param deep boolean, iff true replicate children
     */
    public Node cloneNode(boolean deep) {

        PSVIDocumentImpl newdoc = new PSVIDocumentImpl();
        callUserDataHandlers(this, newdoc, UserDataHandler.NODE_CLONED);
        cloneNode(newdoc, deep);

        // experimental
        newdoc.mutationEvents = mutationEvents;

        return newdoc;

    } // cloneNode(boolean):Node
	    
    /**
     * Retrieve information describing the abilities of this particular
     * DOM implementation. Intended to support applications that may be
     * using DOMs retrieved from several different sources, potentially
     * with different underlying representations.
     */
    public DOMImplementation getImplementation() {
        // Currently implemented as a singleton, since it's hardcoded
        // information anyway.
        return PSVIDOMImplementationImpl.getDOMImplementation();
    }

    /**
     * Create an element with PSVI information
     */
    public Element createElementNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        return new PSVIElementNSImpl(this, namespaceURI, qualifiedName);
    }

    /**
     * Create an element with PSVI information
     */
    public Element createElementNS(String namespaceURI, String qualifiedName,
                                   String localpart) throws DOMException {
        return new PSVIElementNSImpl(this, namespaceURI, qualifiedName, localpart);
    }

    /**
     * Create an attribute with PSVI information
     */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        return new PSVIAttrNSImpl(this, namespaceURI, qualifiedName);
    } 
    
    /**
     * Create an attribute with PSVI information
     */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName,
                                  String localName) throws DOMException {
        return new PSVIAttrNSImpl(this, namespaceURI, qualifiedName, localName);
    } 
    
    /**
     * 
     * The configuration used when <code>Document.normalizeDocument</code> is 
     * invoked. 
     * @since DOM Level 3
     */
    public DOMConfiguration getDomConfig(){
        super.getDomConfig();
        return fConfiguration;
    }
    
    // REVISIT: Forbid serialization of PSVI DOM until
    // we support object serialization of grammars -- mrglavas
    
    private void writeObject(ObjectOutputStream out)
        throws IOException {
        throw new NotSerializableException(getClass().getName());
	}

    private void readObject(ObjectInputStream in) 
        throws IOException, ClassNotFoundException {
        throw new NotSerializableException(getClass().getName());
    }
    
} // class PSVIDocumentImpl
