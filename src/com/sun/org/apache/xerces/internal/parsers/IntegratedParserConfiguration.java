/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  
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
 * originally based on software copyright (c) 2002, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;

/**
 * This is configuration uses a scanner that integrates both scanning of the document
 * and binding namespaces.
 *
 * If namespace feature is turned on, the pipeline is constructured with the 
 * following components:
 * XMLNSDocumentScannerImpl -> XMLNSDTDValidator -> (optional) XMLSchemaValidator
 * 
 * If the namespace feature is turned off the default document scanner implementation
 * is used (XMLDocumentScannerImpl).
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
 * @author Elena Litani, IBM
 *
 * @version $Id: IntegratedParserConfiguration.java,v 1.12 2003/07/25 18:57:23 elena Exp $
 */
public class IntegratedParserConfiguration
extends StandardParserConfiguration {

 
    //
    // REVISIT: should this configuration depend on the others
    //          like DTD/Standard one?
    //

    /** Document scanner that does namespace binding. */
    protected XMLNSDocumentScannerImpl fNamespaceScanner;

    /** Default Xerces implementation of scanner */
    protected XMLDocumentScannerImpl fNonNSScanner;

    /** DTD Validator that does not bind namespaces */
    protected XMLDTDValidator fNonNSDTDValidator;
    
    //
    // Constructors
    //

    /** Default constructor. */
    public IntegratedParserConfiguration() {
        this(null, null, null);
    } // <init>()

    /** 
     * Constructs a parser configuration using the specified symbol table. 
     *
     * @param symbolTable The symbol table to use.
     */
    public IntegratedParserConfiguration(SymbolTable symbolTable) {
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
    public IntegratedParserConfiguration(SymbolTable symbolTable,
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
    public IntegratedParserConfiguration(SymbolTable symbolTable,
                                         XMLGrammarPool grammarPool,
                                         XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);
        
        // create components
        fNonNSScanner = new XMLDocumentScannerImpl();
        fNonNSDTDValidator = new XMLDTDValidator();

        // add components
        addComponent((XMLComponent)fNonNSScanner);
        addComponent((XMLComponent)fNonNSDTDValidator);

    } // <init>(SymbolTable,XMLGrammarPool)

    
    /** Configures the pipeline. */
	protected void configurePipeline() {

		// use XML 1.0 datatype library
		setProperty(DATATYPE_VALIDATOR_FACTORY, fDatatypeValidatorFactory);

		// setup DTD pipeline
		configureDTDPipeline();

		// setup document pipeline
		if (fFeatures.get(NAMESPACES) == Boolean.TRUE) {
            fProperties.put(NAMESPACE_BINDER, fNamespaceBinder);
			fScanner = fNamespaceScanner;
			fProperties.put(DOCUMENT_SCANNER, fNamespaceScanner);
			if (fDTDValidator != null) {
				fProperties.put(DTD_VALIDATOR, fDTDValidator);
				fNamespaceScanner.setDTDValidator(fDTDValidator);
				fNamespaceScanner.setDocumentHandler(fDTDValidator);
				fDTDValidator.setDocumentSource(fNamespaceScanner);
				fDTDValidator.setDocumentHandler(fDocumentHandler);
				if (fDocumentHandler != null) {
					fDocumentHandler.setDocumentSource(fDTDValidator);
				}
				fLastComponent = fDTDValidator;
			}
			else {
				fNamespaceScanner.setDocumentHandler(fDocumentHandler);
                fNamespaceScanner.setDTDValidator(null);
				if (fDocumentHandler != null) {
					fDocumentHandler.setDocumentSource(fNamespaceScanner);
				}
				fLastComponent = fNamespaceScanner;
			}
		}
		else {
			fScanner = fNonNSScanner;
			fProperties.put(DOCUMENT_SCANNER, fNonNSScanner);
			if (fNonNSDTDValidator != null) {
				fProperties.put(DTD_VALIDATOR, fNonNSDTDValidator);
				fNonNSScanner.setDocumentHandler(fNonNSDTDValidator);
				fNonNSDTDValidator.setDocumentSource(fNonNSScanner);
				fNonNSDTDValidator.setDocumentHandler(fDocumentHandler);
				if (fDocumentHandler != null) {
					fDocumentHandler.setDocumentSource(fNonNSDTDValidator);
				}
				fLastComponent = fNonNSDTDValidator;
			}
			else {
				fScanner.setDocumentHandler(fDocumentHandler);
				if (fDocumentHandler != null) {
					fDocumentHandler.setDocumentSource(fScanner);
				}
				fLastComponent = fScanner;
			}
		}

		// setup document pipeline
		if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
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

			fLastComponent.setDocumentHandler(fSchemaValidator);
			fSchemaValidator.setDocumentSource(fLastComponent);
			fSchemaValidator.setDocumentHandler(fDocumentHandler);
			if (fDocumentHandler != null) {
				fDocumentHandler.setDocumentSource(fSchemaValidator);
			}
			fLastComponent = fSchemaValidator;
		}
	} // configurePipeline()



    /** Create a document scanner: this scanner performs namespace binding 
      */
    protected XMLDocumentScanner createDocumentScanner() {
        fNamespaceScanner = new XMLNSDocumentScannerImpl();
        return fNamespaceScanner;
    } // createDocumentScanner():XMLDocumentScanner


    /** Create a DTD validator: this validator performs namespace binding.
      */
    protected XMLDTDValidator createDTDValidator() {
        return new XMLNSDTDValidator();
    } // createDTDValidator():XMLDTDValidator

} // class IntegratedParserConfiguration

