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
package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.StringReader;
import java.io.IOException;

/**
 * This is an implementation of the XSAnnotation schema component.
 */
public class XSAnnotationImpl implements XSAnnotation {

    // Data

    // the content of the annotation node, including all children, along
    // with any non-schema attributes from its parent
    private String fData = null;

    // the grammar which owns this annotation; we get parsers
    // from here when we need them
    private SchemaGrammar fGrammar = null;

    // constructors
    public XSAnnotationImpl(String contents, SchemaGrammar grammar) {
        fData = contents;
        fGrammar = grammar;
    }

    /**
     *  Write contents of the annotation to the specified DOM object. If the 
     * specified <code>target</code> object is a DOM in-scope namespace 
     * declarations for <code>annotation</code> element are added as 
     * attributes nodes of the serialized <code>annotation</code>, otherwise 
     * the corresponding events for all in-scope namespace declaration are 
     * sent via specified document handler. 
     * @param target  A target pointer to the annotation target object, i.e. 
     *   <code>org.w3c.dom.Document</code>, 
     *   <code>org.xml.sax.ContentHandler</code>. 
     * @param targetType  A target type. 
     * @return If the <code>target</code> is recognized type and supported by 
     *   this implementation return true, otherwise return false. 
     */
    public boolean writeAnnotation(Object target, 
                                   short targetType) {
        if(targetType == XSAnnotation.W3C_DOM_ELEMENT || targetType == XSAnnotation.W3C_DOM_DOCUMENT) {
            writeToDOM((Node)target, targetType);
            return true;
        } else if (targetType == SAX_CONTENTHANDLER) {
            writeToSAX((ContentHandler)target);
            return true;
        }
        return false;
    }

    /**
     * A text representation of annotation.
     */
    public String getAnnotationString() {
        return fData;
    }

    // XSObject methods

    /**
     *  The <code>type</code> of this object, i.e. 
     * <code>ELEMENT_DECLARATION</code>. 
     */
    public short getType() {
        return XSConstants.ANNOTATION;
    }

    /**
     * The name of type <code>NCName</code> of this declaration as defined in 
     * XML Namespaces.
     */
    public String getName() {
        return null;
    }

    /**
     *  The [target namespace] of this object, or <code>null</code> if it is 
     * unspecified. 
     */
    public String getNamespace() {
        return null;
    }

    /**
     * A namespace schema information item corresponding to the target 
     * namespace of the component, if it's globally declared; or null 
     * otherwise.
     */
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

    // private methods
    private synchronized void writeToSAX(ContentHandler handler) {
        // nothing must go wrong with this parse...
        SAXParser parser = fGrammar.getSAXParser();
        StringReader aReader = new StringReader(fData);
        InputSource aSource = new InputSource(aReader);
        parser.setContentHandler(handler);
        try {
            parser.parse(aSource);
        } catch (SAXException e) {
            // this should never happen!
            // REVISIT:  what to do with this?; should really not
            // eat it...
        } catch (IOException i) {
            // ditto with above
        }
    }

    // this creates the new Annotation element as the first child
    // of the Node
    private synchronized void writeToDOM(Node target, short type){
        Document futureOwner = (type == XSAnnotation.W3C_DOM_ELEMENT)?target.getOwnerDocument():(Document)target;
        DOMParser parser = fGrammar.getDOMParser();
        StringReader aReader = new StringReader(fData);
        InputSource aSource = new InputSource(aReader);
        try {
            parser.parse(aSource);
        } catch (SAXException e) {
            // this should never happen!
            // REVISIT:  what to do with this?; should really not
            // eat it...
        } catch (IOException i) {
            // ditto with above
        }
        Document aDocument = parser.getDocument();
        Element annotation = aDocument.getDocumentElement();
        Node newElem = futureOwner.importNode(annotation, true);
        target.insertBefore(newElem, target.getFirstChild());
    }

}
