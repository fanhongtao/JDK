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

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

/**
 * This is the "standard" parser configuration. It extends the DTD
 * configuration with the standard set of parser components. 
 * The standard set of parser components include those needed
 * to parse and validate with DTD's, and those needed for XML
 * Schema.</p>
 * <p>
 * In addition to the features and properties recognized by the base
 * parser configuration, this class recognizes these additional 
 * features and properties:
 * <ul>
 * <li>Features
 *  <ul>
 *  <li>http://apache.org/xml/features/validation/schema</li>
 *  <li>http://apache.org/xml/features/validation/schema-full-checking</li>
 *  <li>http://apache.org/xml/features/validation/schema/normalized-value</li>
 *  <li>http://apache.org/xml/features/validation/schema/element-default</li>
 *  </ul>
 * <li>Properties
 *  <ul>
 *   <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *   <li>http://apache.org/xml/properties/internal/entity-manager</li>
 *   <li>http://apache.org/xml/properties/internal/document-scanner</li>
 *   <li>http://apache.org/xml/properties/internal/dtd-scanner</li>
 *   <li>http://apache.org/xml/properties/internal/grammar-pool</li>
 *   <li>http://apache.org/xml/properties/internal/validator/dtd</li>
 *   <li>http://apache.org/xml/properties/internal/datatype-validator-factory</li>
 *  </ul>
 * </ul>
 *
 * @author Arnaud  Le Hors, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: StandardParserConfiguration.java,v 1.33 2004/01/26 17:28:10 mrglavas Exp $
 */
public class StandardParserConfiguration
    extends DTDConfiguration {

    //
    // Constants
    //

    // feature identifiers

    /** Feature identifier: expose schema normalized value */
    protected static final String NORMALIZE_DATA =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_NORMALIZED_VALUE;


    /** Feature identifier: send element default value via characters() */
    protected static final String SCHEMA_ELEMENT_DEFAULT =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_ELEMENT_DEFAULT;


    /** Feature identifier: augment PSVI */
    protected static final String SCHEMA_AUGMENT_PSVI =
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;


    /** feature identifier: XML Schema validation */
    protected static final String XMLSCHEMA_VALIDATION = 
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;

    /** feature identifier: XML Schema validation -- full checking */
    protected static final String XMLSCHEMA_FULL_CHECKING = 
    Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;

    // property identifiers

    /** Property identifier: XML Schema validator. */
    protected static final String SCHEMA_VALIDATOR =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;

    /** Property identifier: schema location. */
    protected static final String SCHEMA_LOCATION =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;

    /** Property identifier: no namespace schema location. */
    protected static final String SCHEMA_NONS_LOCATION =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;

    //
    // Data
    //

    // components (non-configurable)

    /** XML Schema Validator. */
    protected XMLSchemaValidator fSchemaValidator;

    //
    // Constructors
    //

    /** Default constructor. */
    public StandardParserConfiguration() {
        this(null, null, null);
    } // <init>()

    /** 
     * Constructs a parser configuration using the specified symbol table. 
     *
     * @param symbolTable The symbol table to use.
     */
    public StandardParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
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
    public StandardParserConfiguration(SymbolTable symbolTable,
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
    public StandardParserConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool,
                                       XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        // add default recognized features
        final String[] recognizedFeatures = {
            NORMALIZE_DATA,
            SCHEMA_ELEMENT_DEFAULT,
            SCHEMA_AUGMENT_PSVI, 
            // NOTE: These shouldn't really be here but since the XML Schema
            //       validator is constructed dynamically, its recognized
            //       features might not have been set and it would cause a
            //       not-recognized exception to be thrown. -Ac
            XMLSCHEMA_VALIDATION,
            XMLSCHEMA_FULL_CHECKING,
        };
        addRecognizedFeatures(recognizedFeatures);

        // set state for default features
        setFeature(SCHEMA_ELEMENT_DEFAULT, true);
        setFeature(NORMALIZE_DATA, true);
        setFeature(SCHEMA_AUGMENT_PSVI, true);

        // add default recognized properties
    
        final String[] recognizedProperties = {
            // NOTE: These shouldn't really be here but since the XML Schema
            //       validator is constructed dynamically, its recognized
            //       properties might not have been set and it would cause a
            //       not-recognized exception to be thrown. -Ac
            SCHEMA_LOCATION,
            SCHEMA_NONS_LOCATION,       
            };

			addRecognizedProperties(recognizedProperties);

    } // <init>(SymbolTable,XMLGrammarPool)

    //
    // Public methods
    //

    /** Configures the pipeline. */
    protected void configurePipeline() {
        super.configurePipeline();
        if ( getFeature(XMLSCHEMA_VALIDATION )) {
            // If schema validator was not in the pipeline insert it.
            if (fSchemaValidator == null) {
                fSchemaValidator = new XMLSchemaValidator(); 
            
                // add schema component
                fProperties.put(SCHEMA_VALIDATOR, fSchemaValidator);
                addComponent(fSchemaValidator);
                 // add schema message formatter
                if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
                    XSMessageFormatter xmft = new XSMessageFormatter();
                    fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, xmft);
                }

            }
            fLastComponent = fSchemaValidator;
            fNamespaceBinder.setDocumentHandler(fSchemaValidator);
            
            fSchemaValidator.setDocumentHandler(fDocumentHandler);
            fSchemaValidator.setDocumentSource(fNamespaceBinder);
        } 


    } // configurePipeline()

    // features and properties

    /**
     * Check a feature. If feature is know and supported, this method simply
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

        //
        // Xerces Features
        //

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();
        	
            //
            // http://apache.org/xml/features/validation/schema
            //   Lets the user turn Schema validation support on/off.
            //
            if (suffixLength == Constants.SCHEMA_VALIDATION_FEATURE.length() && 
                featureId.endsWith(Constants.SCHEMA_VALIDATION_FEATURE)) {
                return;
            }
            // activate full schema checking
            if (suffixLength == Constants.SCHEMA_FULL_CHECKING.length() &&
                featureId.endsWith(Constants.SCHEMA_FULL_CHECKING)) {
                return;
            }
            // Feature identifier: expose schema normalized value 
            //  http://apache.org/xml/features/validation/schema/normalized-value
            if (suffixLength == Constants.SCHEMA_NORMALIZED_VALUE.length() && 
                featureId.endsWith(Constants.SCHEMA_NORMALIZED_VALUE)) {
                return;
            } 
            // Feature identifier: send element default value via characters() 
            // http://apache.org/xml/features/validation/schema/element-default
            if (suffixLength == Constants.SCHEMA_ELEMENT_DEFAULT.length() && 
                featureId.endsWith(Constants.SCHEMA_ELEMENT_DEFAULT)) {
                return;
            }
        }

        //
        // Not recognized
        //

        super.checkFeature(featureId);

    } // checkFeature(String)

    /**
     * Check a property. If the property is know and supported, this method
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

        //
        // Xerces Properties
        //

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();
            
            if (suffixLength == Constants.SCHEMA_LOCATION.length() && 
                propertyId.endsWith(Constants.SCHEMA_LOCATION)) {
                return;
            }
            if (suffixLength == Constants.SCHEMA_NONS_LOCATION.length() && 
                propertyId.endsWith(Constants.SCHEMA_NONS_LOCATION)) {
                return;
            }
        }

        if (propertyId.startsWith(Constants.JAXP_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.JAXP_PROPERTY_PREFIX.length();
        	
            if (suffixLength == Constants.SCHEMA_SOURCE.length() && 
                propertyId.endsWith(Constants.SCHEMA_SOURCE)) {
                return;
            }
        }

        //
        // Not recognized
        //

        super.checkProperty(propertyId);

    } // checkProperty(String)

} // class StandardParserConfiguration
