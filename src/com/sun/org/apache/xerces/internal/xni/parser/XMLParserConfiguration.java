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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.xni.parser;

import java.io.IOException;
import java.util.Locale;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;

/**
 * Represents a parser configuration. The parser configuration maintains
 * a table of recognized features and properties, assembles components
 * for the parsing pipeline, and is responsible for initiating parsing
 * of an XML document.
 * <p>
 * By separating the configuration of a parser from the specific parser
 * instance, applications can create new configurations and re-use the
 * existing parser components and external API generators (e.g. the
 * DOMParser and SAXParser).
 * <p>
 * The internals of any specific parser configuration instance are hidden.
 * Therefore, each configuration may implement the parsing mechanism any
 * way necessary. However, the parser configuration should follow these
 * guidelines:
 * <ul>
 *  <li>
 *   Call the <code>reset</code> method on each component before parsing.
 *   This is only required if the configuration is re-using existing
 *   components that conform to the <code>XMLComponent</code> interface.
 *   If the configuration uses all custom parts, then it is free to 
 *   implement everything as it sees fit as long as it follows the
 *   other guidelines.
 *  </li>
 *  <li>
 *   Call the <code>setFeature</code> and <code>setProperty</code> method
 *   on each component during parsing to propagate features and properties
 *   that have changed. This is only required if the configuration is
 *   re-using existing components that conform to the <code>XMLComponent</code>
 *   interface. If the configuration uses all custom parts, then it is free
 *   to implement everything as it sees fit as long as it follows the other
 *   guidelines.
 *  </li>
 *  <li>
 *   Pass the same unique String references for all symbols that are
 *   propagated to the registered handlers. Symbols include, but may not
 *   be limited to, the names of elements and attributes (including their
 *   uri, prefix, and localpart). This is suggested but not an absolute 
 *   must. However, the standard parser components may require access to 
 *   the same symbol table for creation of unique symbol references to be
 *   propagated in the XNI pipeline.
 *  </li>
 * </ul>
 *
 * @author Arnaud  Le Hors, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLParserConfiguration.java,v 1.5 2002/01/29 23:16:43 sandygao Exp $
 */
public interface XMLParserConfiguration
    extends XMLComponentManager {

    //
    // XMLParserConfiguration methods
    //

    // parsing

    /**
     * Parse an XML document.
     * <p>
     * The parser can use this method to instruct this configuration
     * to begin parsing an XML document from any valid input source
     * (a character stream, a byte stream, or a URI).
     * <p>
     * Parsers may not invoke this method while a parse is in progress.
     * Once a parse is complete, the parser may then parse another XML
     * document.
     * <p>
     * This method is synchronous: it will not return until parsing
     * has ended.  If a client application wants to terminate 
     * parsing early, it should throw an exception.
     * <p>
     * When this method returns, all characters streams and byte streams
     * opened by the parser are closed.
     * 
     * @param source The input source for the top-level of the
     *               XML document.
     *
     * @exception XNIException Any XNI exception, possibly wrapping 
     *                         another exception.
     * @exception IOException  An IO exception from the parser, possibly
     *                         from a byte stream or character stream
     *                         supplied by the parser.
     */
    public void parse(XMLInputSource inputSource) 
        throws XNIException, IOException;

    // generic configuration

    /**
     * Allows a parser to add parser specific features to be recognized
     * and managed by the parser configuration.
     *
     * @param featureIds An array of the additional feature identifiers 
     *                   to be recognized.
     */
    public void addRecognizedFeatures(String[] featureIds);

    /**
     * Sets the state of a feature. This method is called by the parser
     * and gets propagated to components in this parser configuration.
     * 
     * @param featureId The feature identifier.
     * @param state     The state of the feature.
     *
     * @throws XMLConfigurationException Thrown if there is a configuration
     *                                   error.
     */
    public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException;

    /**
     * Returns the state of a feature.
     * 
     * @param featureId The feature identifier.
     * 
     * @throws XMLConfigurationException Thrown if there is a configuration
     *                                   error.
     */
    public boolean getFeature(String featureId)
        throws XMLConfigurationException;

    /**
     * Allows a parser to add parser specific properties to be recognized
     * and managed by the parser configuration.
     *
     * @param propertyIds An array of the additional property identifiers 
     *                    to be recognized.
     */
    public void addRecognizedProperties(String[] propertyIds);

    /**
     * Sets the value of a property. This method is called by the parser
     * and gets propagated to components in this parser configuration.
     * 
     * @param propertyId The property identifier.
     * @param value      The value of the property.
     *
     * @throws XMLConfigurationException Thrown if there is a configuration
     *                                   error.
     */
    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException;

    /**
     * Returns the value of a property.
     * 
     * @param propertyId The property identifier.
     * 
     * @throws XMLConfigurationException Thrown if there is a configuration
     *                                   error.
     */
    public Object getProperty(String propertyId)
        throws XMLConfigurationException;

    // handlers

    /**
     * Sets the error handler.
     *
     * @param errorHandler The error resolver.
     */
    public void setErrorHandler(XMLErrorHandler errorHandler);

    /** Returns the registered error handler. */
    public XMLErrorHandler getErrorHandler();

    /**
     * Sets the document handler to receive information about the document.
     * 
     * @param documentHandler The document handler.
     */
    public void setDocumentHandler(XMLDocumentHandler documentHandler);

    /** Returns the registered document handler. */
    public XMLDocumentHandler getDocumentHandler();

    /**
     * Sets the DTD handler.
     * 
     * @param dtdHandler The DTD handler.
     */
    public void setDTDHandler(XMLDTDHandler dtdHandler);

    /** Returns the registered DTD handler. */
    public XMLDTDHandler getDTDHandler();

    /**
     * Sets the DTD content model handler.
     * 
     * @param dtdContentModelHandler The DTD content model handler.
     */
    public void setDTDContentModelHandler(XMLDTDContentModelHandler dtdContentModelHandler);

    /** Returns the registered DTD content model handler. */
    public XMLDTDContentModelHandler getDTDContentModelHandler();

    // other settings

    /**
     * Sets the entity resolver.
     *
     * @param entityResolver The new entity resolver.
     */
    public void setEntityResolver(XMLEntityResolver entityResolver);

    /** Returns the registered entity resolver. */
    public XMLEntityResolver getEntityResolver();

    /**
     * Set the locale to use for messages.
     *
     * @param locale The locale object to use for localization of messages.
     *
     * @exception XNIException Thrown if the parser does not support the
     *                         specified locale.
     */
    public void setLocale(Locale locale) throws XNIException;

    /** Returns the locale. */
    public Locale getLocale();

} // interface XMLParserConfiguration
