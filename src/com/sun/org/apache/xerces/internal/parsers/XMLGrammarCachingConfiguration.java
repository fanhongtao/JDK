/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  
 * All rights reserved.
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
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

/**
 * <p> This configuration provides a generic way of using
 * Xerces's grammar caching facilities.  It extends the
 * XML11Configuration and thus may validate documents
 * according to XML schemas or DTD's.  It also allows the user to
 * preparse a grammar, and to lock the grammar pool
 * implementation such that no more grammars will be added.</p>
 * <p> Using the com.sun.org.apache.xerces.internal.xni.parser property, an
 * application may instantiate a Xerces SAX or DOM parser with
 * this configuration.  When invoked in this manner, the default
 * behaviour will be elicited; to use this configuration's
 * specific facilities, the user will need to reference it
 * directly.</p>
 * <p>
 * In addition to the features and properties recognized by the base
 * parser configuration, this class recognizes these additional 
 * features and properties:
 * <ul>
 * </ul>
 *
 * @author Neil Graham, IBM
 *
 * @version $Id: XMLGrammarCachingConfiguration.java,v 1.15 2004/02/18 22:18:31 mrglavas Exp $
 */
public class XMLGrammarCachingConfiguration 
    extends XML11Configuration {

    //
    // Constants
    //

    // a larg(ish) prime to use for a symbol table to be shared
    // among
    // potentially man parsers.  Start one as close to 2K (20
    // times larger than normal) and see what happens...
    public static final int BIG_PRIME = 2039;

    // the static symbol table to be shared amongst parsers
    protected static final SynchronizedSymbolTable fStaticSymbolTable = 
            new SynchronizedSymbolTable(BIG_PRIME);

    // the Grammar Pool to be shared similarly
    protected static final XMLGrammarPoolImpl fStaticGrammarPool =
            new XMLGrammarPoolImpl();

    // schema full checking constant
    protected static final String SCHEMA_FULL_CHECKING =
            Constants.XERCES_FEATURE_PREFIX+Constants.SCHEMA_FULL_CHECKING;

    // Data

    // variables needed for caching schema grammars.  
    protected XMLSchemaLoader fSchemaLoader;

    // the DTD grammar loader
    protected XMLDTDLoader fDTDLoader;

    //
    // Constructors
    //

    /** Default constructor. */
    public XMLGrammarCachingConfiguration() {
        this(fStaticSymbolTable, fStaticGrammarPool, null);
    } // <init>()

    /** 
     * Constructs a parser configuration using the specified symbol table. 
     *
     * @param symbolTable The symbol table to use.
     */
    public XMLGrammarCachingConfiguration(SymbolTable symbolTable) {
        this(symbolTable, fStaticGrammarPool, null);
    } // <init>(SymbolTable)

    /**
     * Constructs a parser configuration using the specified symbol table and
     * grammar pool.
     * <p>
     * <strong>REVISIT:</strong> 
     * Grammar pool will be updated when the new validation engine is
     * implemented.
     *
     * @param symbolTable The symbol table to use.
     * @param grammarPool The grammar pool to use.
     */
    public XMLGrammarCachingConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } // <init>(SymbolTable,XMLGrammarPool)

    /**
     * Constructs a parser configuration using the specified symbol table,
     * grammar pool, and parent settings.
     * <p>
     * <strong>REVISIT:</strong> 
     * Grammar pool will be updated when the new validation engine is
     * implemented.
     *
     * @param symbolTable    The symbol table to use.
     * @param grammarPool    The grammar pool to use.
     * @param parentSettings The parent settings.
     */
    public XMLGrammarCachingConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool,
                                       XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        // REVISIT:  may need to add some features/properties
        // specific to this configuration at some point...

        // add default recognized features
        // set state for default features
        // add default recognized properties
        // create and register missing components
        fSchemaLoader = new XMLSchemaLoader(fSymbolTable);
        fSchemaLoader.setProperty(XMLGRAMMAR_POOL, fGrammarPool);

        // and set up the DTD loader too:
        fDTDLoader = new XMLDTDLoader(fSymbolTable, fGrammarPool);
    } // <init>(SymbolTable,XMLGrammarPool, XMLComponentManager)

    //
    // Public methods
    //

    /*
     * lock the XMLGrammarPoolImpl object so that it does not
     * accept any more grammars from the validators.  
     */
    public void lockGrammarPool() {
        fGrammarPool.lockPool();
    } // lockGrammarPool()

    /*
     * clear the XMLGrammarPoolImpl object so that it does not
     * contain any more grammars.  
     */
    public void clearGrammarPool() {
        fGrammarPool.clear();
    } // clearGrammarPool()

    /*
     * unlock the XMLGrammarPoolImpl object so that it  
     * accepts more grammars from the validators.  
     */
    public void unlockGrammarPool() {
        fGrammarPool.unlockPool();
    } // unlockGrammarPool()

    /**
     * Parse a grammar from a location identified by an URI.
     * This method also adds this grammar to the XMLGrammarPool
     *
     * @param type The type of the grammar to be constructed
     * @param uri The location of the grammar to be constructed.
     * <strong>The parser will not expand this URI or make it
     * available to the EntityResolver</strong>
     * @return The newly created <code>Grammar</code>.
     * @exception XNIException thrown on an error in grammar
     * construction
     * @exception IOException thrown if an error is encountered
     * in reading the file
     */
    public Grammar parseGrammar(String type, String uri)
                              throws XNIException, IOException {
        XMLInputSource source = new XMLInputSource(null, uri, null);
        return parseGrammar(type, source);

    }

    /**
     * Parse a grammar from a location identified by an
     * XMLInputSource.  
     * This method also adds this grammar to the XMLGrammarPool
     *
     * @param type The type of the grammar to be constructed
     * @param source The XMLInputSource containing this grammar's
     * information
     * <strong>If a URI is included in the systemId field, the parser will not expand this URI or make it
     * available to the EntityResolver</strong>
     * @return The newly created <code>Grammar</code>.
     * @exception XNIException thrown on an error in grammar
     * construction
     * @exception IOException thrown if an error is encountered
     * in reading the file
     */
    public Grammar parseGrammar(String type, XMLInputSource
                is) throws XNIException, IOException {
        if(type.equals(XMLGrammarDescription.XML_SCHEMA)) {
            // by default, make all XMLGrammarPoolImpl's schema grammars available to fSchemaHandler
            return parseXMLSchema(is);
        } else if(type.equals(XMLGrammarDescription.XML_DTD)) {
            return parseDTD(is);
        }
        // don't know this grammar...
        return null;
    } // parseGrammar(String, XMLInputSource):  Grammar

    //
    // Protected methods
    //
    
    // features and properties

    /**
     * Check a feature. If feature is known and supported, this method simply
     * returns. Otherwise, the appropriate exception is thrown.
     *
     * @param featureId The unique identifier (URI) of the feature.
     *
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    protected void checkFeature(String featureId)
        throws XMLConfigurationException {

        super.checkFeature(featureId);

    } // checkFeature(String)

    /**
     * Check a property. If the property is known and supported, this method
     * simply returns. Otherwise, the appropriate exception is thrown.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     *
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    protected void checkProperty(String propertyId)
        throws XMLConfigurationException {
        super.checkProperty(propertyId);

    } // checkProperty(String)

    // package-protected methods

    /* This method parses an XML Schema document.  
     * It requires a GrammarBucket parameter so that DOMASBuilder can
     * extract the info it needs.
     * Therefore, bucket must not be null!
     */
    SchemaGrammar parseXMLSchema(XMLInputSource is) 
                throws IOException {
        XMLEntityResolver resolver = getEntityResolver();
        if(resolver != null) {
            fSchemaLoader.setEntityResolver(resolver);
        }
        if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
            fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
        } 
        fSchemaLoader.setProperty(ERROR_REPORTER, fErrorReporter);

        String propPrefix = Constants.XERCES_PROPERTY_PREFIX;
        String propName = propPrefix + Constants.SCHEMA_LOCATION;
        fSchemaLoader.setProperty(propName, getProperty(propName));
        propName = propPrefix + Constants.SCHEMA_NONS_LOCATION;
        fSchemaLoader.setProperty(propName, getProperty(propName));
        propName = Constants.JAXP_PROPERTY_PREFIX+Constants.SCHEMA_SOURCE;
        fSchemaLoader.setProperty(propName, getProperty(propName));
        fSchemaLoader.setFeature(SCHEMA_FULL_CHECKING, getFeature(SCHEMA_FULL_CHECKING));

        // Should check whether the grammar with this namespace is already in
        // the grammar resolver. But since we don't know the target namespace
        // of the document here, we leave such check to XSDHandler
        SchemaGrammar grammar = (SchemaGrammar)fSchemaLoader.loadGrammar(is);
        // by default, hand it off to the grammar pool
        if (grammar != null) {
            fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_SCHEMA,
                                      new Grammar[]{grammar});
        }
        
        return grammar;

    } // parseXMLSchema(XMLInputSource) :  SchemaGrammar

    /* This method parses an external DTD entity.
     */
    DTDGrammar parseDTD(XMLInputSource is) 
                throws IOException {
        XMLEntityResolver resolver = getEntityResolver();
        if(resolver != null) {
            fDTDLoader.setEntityResolver(resolver);
        }
        fDTDLoader.setProperty(ERROR_REPORTER, fErrorReporter);

        // Should check whether the grammar with this namespace is already in
        // the grammar resolver. But since we don't know the target namespace
        // of the document here, we leave such check to the application...
        DTDGrammar grammar = (DTDGrammar)fDTDLoader.loadGrammar(is);
        // by default, hand it off to the grammar pool
        if (grammar != null) {
            fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_DTD,
                                      new Grammar[]{grammar});
        }
        
        return grammar;

    } // parseXMLDTD(XMLInputSource) :  DTDGrammar


} // class XMLGrammarCachingConfiguration
