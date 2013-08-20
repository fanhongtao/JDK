/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
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

package com.sun.org.apache.xerces.internal.parsers;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import org.xml.sax.ext.EntityResolver2;

/**
 * This is the main Xerces DOM parser class. It uses the abstract DOM
 * parser with a document scanner, a dtd scanner, and a validator, as
 * well as a grammar pool.
 *
 * @author Arnaud  Le Hors, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: DOMParser.java,v 1.69 2004/02/17 07:14:49 neeraj Exp $
 */
public class DOMParser
extends AbstractDOMParser {
    
    //
    // Constants
    //
    
    // properties
    
    /** Property identifier: symbol table. */
    protected static final String SYMBOL_TABLE =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;
    
    /** Property identifier: XML grammar pool. */
    protected static final String XMLGRAMMAR_POOL =
    Constants.XERCES_PROPERTY_PREFIX+Constants.XMLGRAMMAR_POOL_PROPERTY;
    
    /** Recognized properties. */
    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        XMLGRAMMAR_POOL,
    };
    
    //
    // Constructors
    //
    
    /**
     * Constructs a DOM parser using the specified parser configuration.
     */
    public DOMParser(XMLParserConfiguration config) {
        super(config);
    } // <init>(XMLParserConfiguration)
    
    /**
     * Constructs a DOM parser using the dtd/xml schema parser configuration.
     */
    public DOMParser() {
        this(null, null);
    } // <init>()
    
    /**
     * Constructs a DOM parser using the specified symbol table.
     */
    public DOMParser(SymbolTable symbolTable) {
        this(symbolTable, null);
    } // <init>(SymbolTable)
    
    
    /**
     * Constructs a DOM parser using the specified symbol table and
     * grammar pool.
     */
    public DOMParser(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
        super((XMLParserConfiguration)ObjectFactory.createObject(
        "com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration",
        "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration"
        ));
        
        // set properties
        fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
        }
        if (grammarPool != null) {
            fConfiguration.setProperty(XMLGRAMMAR_POOL, grammarPool);
        }
        
    } // <init>(SymbolTable,XMLGrammarPool)
    
    //
    // XMLReader methods
    //
    
    /**
     * Parses the input source specified by the given system identifier.
     * <p>
     * This method is equivalent to the following:
     * <pre>
     *     parse(new InputSource(systemId));
     * </pre>
     *
     * @param source The input source.
     *
     * @exception org.xml.sax.SAXException Throws exception on SAX error.
     * @exception java.io.IOException Throws exception on i/o error.
     */
    public void parse(String systemId) throws SAXException, IOException {
        
        // parse document
        XMLInputSource source = new XMLInputSource(null, systemId, null);
        try {
            parse(source);
        }
        
        // wrap XNI exceptions as SAX exceptions
        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null) {
                // must be a parser exception; mine it for locator info and throw
                // a SAXParseException
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw new SAXParseException(e.getMessage(), locatorImpl);
            }
            if (ex instanceof SAXException) {
                // why did we create an XMLParseException?
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (XNIException e) {
            e.printStackTrace();
            Exception ex = e.getException();
            if (ex == null) {
                throw new SAXException(e.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        
    } // parse(String)
    
    /**
     * parse
     *
     * @param inputSource
     *
     * @exception org.xml.sax.SAXException
     * @exception java.io.IOException
     */
    public void parse(InputSource inputSource)
    throws SAXException, IOException {
        
        // parse document
        try {
            XMLInputSource xmlInputSource =
            new XMLInputSource(inputSource.getPublicId(),
            inputSource.getSystemId(),
            null);
            xmlInputSource.setByteStream(inputSource.getByteStream());
            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
            xmlInputSource.setEncoding(inputSource.getEncoding());
            parse(xmlInputSource);
        }
        
        // wrap XNI exceptions as SAX exceptions
        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null) {
                // must be a parser exception; mine it for locator info and throw
                // a SAXParseException
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw new SAXParseException(e.getMessage(), locatorImpl);
            }
            if (ex instanceof SAXException) {
                // why did we create an XMLParseException?
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (XNIException e) {
            Exception ex = e.getException();
            if (ex == null) {
                throw new SAXException(e.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        
    } // parse(InputSource)
    
    /**
     * Sets the resolver used to resolve external entities. The EntityResolver
     * interface supports resolution of public and system identifiers.
     *
     * @param resolver The new entity resolver. Passing a null value will
     *                 uninstall the currently installed resolver.
     */
    public void setEntityResolver(EntityResolver resolver) {
        
        try {
            if(resolver instanceof EntityResolver2){
                fConfiguration.setProperty(ENTITY_RESOLVER, new EntityResolver2Wrapper((EntityResolver2)resolver));
            }else{
                fConfiguration.setProperty(ENTITY_RESOLVER, new EntityResolverWrapper(resolver));
            }
        }
        catch (XMLConfigurationException e) {
            // do nothing
        }
        
    } // setEntityResolver(EntityResolver)
    
    /**
     * Return the current entity resolver.
     *
     * @return The current entity resolver, or null if none
     *         has been registered.
     * @see #setEntityResolver
     */
    public EntityResolver getEntityResolver() {
        
        EntityResolver entityResolver = null;
        try {
            XMLEntityResolver xmlEntityResolver =
            (XMLEntityResolver)fConfiguration.getProperty(ENTITY_RESOLVER);
            if (xmlEntityResolver != null){
                if(xmlEntityResolver instanceof EntityResolverWrapper) {
                    entityResolver = ((EntityResolverWrapper)xmlEntityResolver).getEntityResolver();
                }else if(xmlEntityResolver instanceof EntityResolver2Wrapper){
                    entityResolver = ((EntityResolver2Wrapper)xmlEntityResolver).getEntityResolver();
                }
            }
        }catch (XMLConfigurationException e) {
            // do nothing
        }
        return entityResolver;
        
    } // getEntityResolver():EntityResolver
    
    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error handler, all
     * error events reported by the SAX parser will be silently
     * ignored; however, normal processing may not continue.  It is
     * highly recommended that all SAX applications implement an
     * error handler to avoid unexpected bugs.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param errorHandler The error handler.
     * @exception java.lang.NullPointerException If the handler
     *            argument is null.
     * @see #getErrorHandler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        
        try {
            fConfiguration.setProperty(ERROR_HANDLER,
            new ErrorHandlerWrapper(errorHandler));
        }
        catch (XMLConfigurationException e) {
            // do nothing
        }
        
    } // setErrorHandler(ErrorHandler)
    
    /**
     * Return the current error handler.
     *
     * @return The current error handler, or null if none
     *         has been registered.
     * @see #setErrorHandler
     */
    public ErrorHandler getErrorHandler() {
        
        ErrorHandler errorHandler = null;
        try {
            XMLErrorHandler xmlErrorHandler =
            (XMLErrorHandler)fConfiguration.getProperty(ERROR_HANDLER);
            if (xmlErrorHandler != null &&
            xmlErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
            }
        }
        catch (XMLConfigurationException e) {
            // do nothing
        }
        return errorHandler;
        
    } // getErrorHandler():ErrorHandler
    
    /**
     * Set the state of any feature in a SAX2 parser.  The parser
     * might not recognize the feature, and if it does recognize
     * it, it might not be able to fulfill the request.
     *
     * @param featureId The unique identifier (URI) of the feature.
     * @param state The requested state of the feature (true or false).
     *
     * @exception SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception SAXNotSupportedException If the
     *            requested feature is known, but the requested
     *            state is not supported.
     */
    public void setFeature(String featureId, boolean state)
    throws SAXNotRecognizedException, SAXNotSupportedException {
        
        try {
            fConfiguration.setFeature(featureId, state);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(message);
            }
            else {
                throw new SAXNotSupportedException(message);
            }
        }
        
    } // setFeature(String,boolean)
    
    /**
     * Query the state of a feature.
     *
     * Query the current state of any feature in a SAX2 parser.  The
     * parser might not recognize the feature.
     *
     * @param featureId The unique identifier (URI) of the feature
     *                  being set.
     * @return The current state of the feature.
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception SAXNotSupportedException If the
     *            requested feature is known but not supported.
     */
    public boolean getFeature(String featureId)
    throws SAXNotRecognizedException, SAXNotSupportedException {
        
        try {
            return fConfiguration.getFeature(featureId);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(message);
            }
            else {
                throw new SAXNotSupportedException(message);
            }
        }
        
    } // getFeature(String):boolean
    
    /**
     * Set the value of any property in a SAX2 parser.  The parser
     * might not recognize the property, and if it does recognize
     * it, it might not support the requested value.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     * @param Object The value to which the property is being set.
     *
     * @exception SAXNotRecognizedException If the
     *            requested property is not known.
     * @exception SAXNotSupportedException If the
     *            requested property is known, but the requested
     *            value is not supported.
     */
    public void setProperty(String propertyId, Object value)
    throws SAXNotRecognizedException, SAXNotSupportedException {
        
        try {
            fConfiguration.setProperty(propertyId, value);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(message);
            }
            else {
                throw new SAXNotSupportedException(message);
            }
        }
        
    } // setProperty(String,Object)
    
    /**
     * Query the value of a property.
     *
     * Return the current value of a property in a SAX2 parser.
     * The parser might not recognize the property.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     * @return The current value of the property.
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested property is not known.
     * @exception SAXNotSupportedException If the
     *            requested property is known but not supported.
     */
    public Object getProperty(String propertyId)
    throws SAXNotRecognizedException, SAXNotSupportedException {
        
        if (propertyId.equals(CURRENT_ELEMENT_NODE)) {
            boolean deferred = false;
            try {
                deferred = getFeature(DEFER_NODE_EXPANSION);
            }
            catch (XMLConfigurationException e){
                // ignore
            }
            if (deferred) {
                throw new SAXNotSupportedException("Current element node cannot be queried when node expansion is deferred.");
            }
            return (fCurrentNode!=null &&
            fCurrentNode.getNodeType() == Node.ELEMENT_NODE)? fCurrentNode:null;
        }
        
        try {
            return fConfiguration.getProperty(propertyId);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == XMLConfigurationException.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(message);
            }
            else {
                throw new SAXNotSupportedException(message);
            }
        }
        
    } // getProperty(String):Object
    
} // class DOMParser
