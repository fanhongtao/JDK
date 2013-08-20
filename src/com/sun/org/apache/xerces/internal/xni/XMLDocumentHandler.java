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

package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

/**
 * The document handler interface defines callback methods to report
 * information items in XML documents. Parser components interested in 
 * document information implement this interface and are registered
 * as the document handler on the document source.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLDocumentHandler.java,v 1.10 2002/12/16 01:26:21 elena Exp $
 */
public interface XMLDocumentHandler {

    //
    // XMLDocumentHandler methods
    //

    /**
     * The start of the document.
     * 
     * @param locator  The document locator, or null if the document
     *                 location cannot be reported during the parsing
     *                 of this document. However, it is <em>strongly</em>
     *                 recommended that a locator be supplied that can
     *                 at least report the system identifier of the
     *                 document.
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
     *                 
     * @param augs     Additional information that may include infoset augmentations
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void startDocument(XMLLocator locator, String encoding, 
                              NamespaceContext namespaceContext,
                              Augmentations augs) 
        throws XNIException;

    /**
     * Notifies of the presence of an XMLDecl line in the document. If
     * present, this method will be called immediately following the
     * startDocument call.
     * 
     * @param version    The XML version.
     * @param encoding   The IANA encoding name of the document, or null if
     *                   not specified.
     * @param standalone The standalone value, or null if not specified.
     * @param augs       Additional information that may include infoset augmentations
     *                   
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
        throws XNIException;

    /**
     * Notifies of the presence of the DOCTYPE line in the document.
     * 
     * @param rootElement
     *                 The name of the root element.
     * @param publicId The public identifier if an external DTD or null
     *                 if the external DTD is specified using SYSTEM.
     * @param systemId The system identifier if an external DTD, null
     *                 otherwise.
     * @param augs     Additional information that may include infoset augmentations
     *                 
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
        throws XNIException;

    /**
     * A comment.
     * 
     * @param text   The text in the comment.
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by application to signal an error.
     */
    public void comment(XMLString text, Augmentations augs) throws XNIException;

    /**
     * A processing instruction. Processing instructions consist of a
     * target name and, optionally, text data. The data is only meaningful
     * to the application.
     * <p>
     * Typically, a processing instruction's data will contain a series
     * of pseudo-attributes. These pseudo-attributes follow the form of
     * element attributes but are <strong>not</strong> parsed or presented
     * to the application as anything other than text. The application is
     * responsible for parsing the data.
     * 
     * @param target The target.
     * @param data   The data or null if none specified.
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException;

    /**
     * The start of an element.
     * 
     * @param element    The name of the element.
     * @param attributes The element attributes.
     * @param augs       Additional information that may include infoset augmentations
     *                   
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException;

    /**
     * An empty element.
     * 
     * @param element    The name of the element.
     * @param attributes The element attributes.
     * @param augs       Additional information that may include infoset augmentations
     *                   
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException;

    /**
     * This method notifies the start of a general entity.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     * 
     * @param name     The name of the general entity.
     * @param identifier The resource identifier.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal entities or a document entity that is
     *                 parsed from a java.io.Reader).
     * @param augs     Additional information that may include infoset augmentations
     *                 
     * @exception XNIException Thrown by handler to signal an error.
     */
    public void startGeneralEntity(String name, 
                                   XMLResourceIdentifier identifier,
                                   String encoding,
                                   Augmentations augs) throws XNIException;

    /**
     * Notifies of the presence of a TextDecl line in an entity. If present,
     * this method will be called immediately following the startEntity call.
     * <p>
     * <strong>Note:</strong> This method will never be called for the
     * document entity; it is only called for external general entities
     * referenced in document content.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     * 
     * @param version  The XML version, or null if not specified.
     * @param encoding The IANA encoding name of the entity.
     * @param augs     Additional information that may include infoset augmentations
     *                 
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException;

    /**
     * This method notifies the end of a general entity.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     * 
     * @param name   The name of the entity.
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException;

    /**
     * Character content.
     * 
     * @param text   The content.
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void characters(XMLString text, Augmentations augs) throws XNIException;

    /**
     * Ignorable whitespace. For this method to be called, the document
     * source must have some way of determining that the text containing
     * only whitespace characters should be considered ignorable. For
     * example, the validator can determine if a length of whitespace
     * characters in the document are ignorable based on the element
     * content model.
     * 
     * @param text   The ignorable whitespace.
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException;

    /**
     * The end of an element.
     * 
     * @param element The name of the element.
     * @param augs    Additional information that may include infoset augmentations
     *                
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void endElement(QName element, Augmentations augs) throws XNIException;

    /**
     * The start of a CDATA section.
     * 
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void startCDATA(Augmentations augs) throws XNIException;

    /**
     * The end of a CDATA section.
     * 
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void endCDATA(Augmentations augs) throws XNIException;

    /**
     * The end of the document.
     * 
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void endDocument(Augmentations augs) throws XNIException;


    /** Sets the document source. */
    public void setDocumentSource(XMLDocumentSource source);


    /** Returns the document source. */
    public XMLDocumentSource getDocumentSource();

} // interface XMLDocumentHandler
