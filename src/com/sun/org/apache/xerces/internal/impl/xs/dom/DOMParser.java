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

package com.sun.org.apache.xerces.internal.impl.xs.dom;

import com.sun.org.apache.xerces.internal.parsers.NonValidatingConfiguration;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.XMLChar;

import org.w3c.dom.Element;

/**
 * A dom parser used to parse schema documents into DOM trees
 * 
 * @author Sandy Gao, IBM
 * 
 * @version $Id: DOMParser.java,v 1.9 2002/12/11 16:01:18 sandygao Exp $
 */
public class DOMParser extends com.sun.org.apache.xerces.internal.parsers.DOMParser {

    /** Property identifier: entity manager. */
    protected static final String ENTITY_MANAGER = 
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;
    
    /** Property identifier: DOM document class name. */
    protected static final String DOCUMENT_CLASS = 
        Constants.XERCES_PROPERTY_PREFIX + Constants.DOCUMENT_CLASS_NAME_PROPERTY;
    
    /** Feature identifier: DOM Defer node expansion. */
    protected static final String DEFER_EXPANSION = 
        Constants.XERCES_FEATURE_PREFIX + Constants.DEFER_NODE_EXPANSION_FEATURE;
    
    /** Property identifier: error reporter. */
    public static final String ERROR_REPORTER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    // the locator containing line/column information
    protected XMLLocator   fLocator;
    
    // our own document implementation, which knows how to create Element
    // with line/column information
    public DocumentImpl fDocumentImpl;

    private DOMNodePool fNodePool;
    
    //
    // Constructors
    //

    /**
     * Constructs a DOM parser using the dtd/xml schema parser configuration.
     */
    public DOMParser() {
        // REVISIT: should we use a new configuration with scannerNS->dom API with 
        //          no dtd scanners/valitors..?
        //
        super(new NonValidatingConfiguration());
        try {
            // use our own document implementation
            setProperty(DOCUMENT_CLASS, "com.sun.org.apache.xerces.internal.impl.xs.dom.DocumentImpl");
            // don't defer DOM expansion
            setFeature(DEFER_EXPANSION, false);

        }
        catch (Exception e) {
        }
        fNodePool = new DOMNodePool();
    } // <init>()

    /**
     * Resets the node pool.
     */
    public void resetNodePool() {
        fNodePool.reset();
    }

    /**
     * The start of the document.
     *
     * @param locator The system identifier of the entity if the entity
     *                 is external, null otherwise.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal entities or a document entity that is
     *                 parsed from a java.io.Reader).
     * @param namespaceContext
     *                 The namespace context in effect at the
     *                 start of this document.
     *                 This object represents the current context.
     *                 Implementors of this class are responsible
     *                 for copying the namespace bindings from the
     *                 the current context (and its parent contexts)
     *                 if that information is important.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startDocument(XMLLocator locator, String encoding, 
                              NamespaceContext namespaceContext, Augmentations augs)
        throws XNIException {

        super.startDocument(locator, encoding, namespaceContext, augs);
        // get a handle to the document created
        fDocumentImpl = (DocumentImpl)super.fDocumentImpl;
        fDocumentImpl.fNodePool=fNodePool;
        fLocator = locator;

    } // startDocument(XMLLocator,String,Augmentations)

    // Where xs:appinfo or xs:documentation starts;
    // -1 means not in the scope of either of the two elements.
    private int fAnnotationDepth = -1;
    // The current element depth
    private int fDepth = -1;
    // Use to report the error when characters are not allowed.
    XMLErrorReporter fErrorReporter;

    // override startElement method to check whether it's one of
    // xs:appinfo or xs:documentation
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {
        super.startElement(element, attributes, augs);
        fDepth++;
        // if it's not within either element, check whether it's one of them
        // if so, record the current depth, so that any element with larger
        // depth is allowed to have character data.
        if (fAnnotationDepth == -1) {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA &&
                (element.localpart == SchemaSymbols.ELT_APPINFO ||
                 element.localpart == SchemaSymbols.ELT_DOCUMENTATION)) {
                fAnnotationDepth = fDepth;
            }
        }
    }
    
    // override this method to check whether there are non-whitespace characters
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        // when it's not within xs:appinfo or xs:documentation
        if (fAnnotationDepth == -1) {
            for (int i=text.offset; i<text.offset+text.length; i++) {
                // and there is a non-whitespace character
                if (!XMLChar.isSpace(text.ch[i])) {
                    // only get the error reporter when reporting an error
                    if (fErrorReporter == null) {
                        try {
                            fErrorReporter = (XMLErrorReporter)getProperty(ERROR_REPORTER);
                        } catch (Exception e) {
                            //ignore the excpetion
                        }
                        if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
                            XSMessageFormatter xmft = new XSMessageFormatter();
                            fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, xmft);
                        }
                    }
                    // the string we saw: starting from the first non-whitespace character.
                    String txt = new String(text.ch, i, text.length+text.offset-i);
                    // report an error
                    fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN,
                                               "s4s-elt-character",
                                               new Object[]{txt},
                                               XMLErrorReporter.SEVERITY_ERROR);
                    break;
                }
            }
            // don't call super.characters() when it's not within one of the 2
            // annotation elements: the traversers ignore them anyway. We can
            // save time/memory creating the text nodes.
        }
        // when it's not within either of the 2 elements, characters are allowed
        // and we need to call super.characters().
        else {
            super.characters(text, augs);
        }
    }
    
    // override this method to update the depth variables
    public void endElement(QName element, Augmentations augs) throws XNIException {
        super.endElement(element, augs);
        // when we reach the endElement of xs:appinfo or xs:documentation,
        // change fAnnotationDepth to -1
        if (fAnnotationDepth == fDepth)
            fAnnotationDepth = -1;
        fDepth--;
    }

    // override this method to store line/column information in Element created
    protected Element createElementNode(QName element) {
        // create an element containing line/column information
        return fDocumentImpl.createElementNS(element.uri, element.rawname,
                                             element.localpart,
                                             fLocator.getLineNumber(),
                                             fLocator.getColumnNumber());
    }

} // class DOMParser
