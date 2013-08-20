/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
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
package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultXMLDocumentHandler;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.LocatorProxy;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Converts {@link XNI} events to {@link ContentHandler} events.
 * 
 * <p>
 * Deriving from {@link DefaultXMLDocumentHandler}
 * to reuse its default {@link com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler}
 * implementation.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XNI2SAX extends DefaultXMLDocumentHandler {
    
    private ContentHandler fContentHandler;

    private String fVersion;

    /** Namespace context */
    protected NamespaceContext fNamespaceContext;
    
    /**
     * For efficiency, we reuse one instance.
     */
    private final AttributesProxy fAttributesProxy = new AttributesProxy();

    public void setContentHandler( ContentHandler handler ) {
        this.fContentHandler = handler;
    }
    
    public ContentHandler getContentHandler() {
        return fContentHandler;
    }
    

    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        this.fVersion = version;
    }

    public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
        fNamespaceContext = namespaceContext;
        fContentHandler.setDocumentLocator(new LocatorProxy(locator,fVersion));
        try {
            fContentHandler.startDocument();
        } catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    public void endDocument(Augmentations augs) throws XNIException {
        try {
            fContentHandler.endDocument();
        } catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        try {
            fContentHandler.processingInstruction(target,data.toString());
        } catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        try {
            // start namespace prefix mappings
            int count = fNamespaceContext.getDeclaredPrefixCount();
            if (count > 0) {
                String prefix = null;
                String uri = null;
                for (int i = 0; i < count; i++) {
                    prefix = fNamespaceContext.getDeclaredPrefixAt(i);
                    uri = fNamespaceContext.getURI(prefix);
                    fContentHandler.startPrefixMapping(prefix, (uri == null)?"":uri);
                }
            }
                    
            String uri = element.uri != null ? element.uri : "";
            String localpart = element.localpart;
            fAttributesProxy.setAttributes(attributes);
            fContentHandler.startElement(uri, localpart, element.rawname, fAttributesProxy);
        } catch( SAXException e ) {
            throw new XNIException(e);
        }
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        try {
            String uri = element.uri != null ? element.uri : "";
            String localpart = element.localpart;
            fContentHandler.endElement(uri, localpart, element.rawname);
            
            // send endPrefixMapping events
            int count = fNamespaceContext.getDeclaredPrefixCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    fContentHandler.endPrefixMapping(fNamespaceContext.getDeclaredPrefixAt(i));
                }
            }
        } catch( SAXException e ) {
            throw new XNIException(e);
        }
    }





    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        startElement(element,attributes,augs);
        endElement(element,augs);
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
        try {
            fContentHandler.characters(text.ch,text.offset,text.length);
        } catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        try {
            fContentHandler.ignorableWhitespace(text.ch,text.offset,text.length);
        } catch (SAXException e) {
            throw new XNIException(e);
        }
    }


}
