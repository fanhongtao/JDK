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
 * originally based on software copyright (c) 2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XPointerSchema;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

/**
 * @author Arun Yadav, Sun Microsystem.
 */
public class XPointerParserConfiguration extends XML11Configuration {
    
    private XPointerSchema fXPointerSchemaComponent;
    
    /** Property identifier: error reporter. */
    protected static final String XINCLUDE_HANDLER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.XINCLUDE_HANDLER_PROPERTY;
    
    /** Property identifier: error reporter. */
    protected static final String NAMESPACE_CONTEXT =
    Constants.XERCES_PROPERTY_PREFIX + Constants.NAMESPACE_CONTEXT_PROPERTY;
    
    /** Default constructor. */
    public XPointerParserConfiguration() {
        this(null, null, null);
        //this(null, new XMLGrammarPoolImpl(), null);
    } // <init>()
    
    /**
     * Constructs a parser configuration using the specified symbol table.
     *
     * @param symbolTable The symbol table to use.
     */
    public XPointerParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
        //this(symbolTable, new XMLGrammarPoolImpl(), null);
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
    public XPointerParserConfiguration(
    SymbolTable symbolTable,
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
    public XPointerParserConfiguration(
    SymbolTable symbolTable,
    XMLGrammarPool grammarPool,
    XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);
        
        // add default recognized properties
        final String[] recognizedProperties =
        { XINCLUDE_HANDLER, NAMESPACE_CONTEXT };
        
        addRecognizedProperties(recognizedProperties);
        setProperty(NAMESPACE_CONTEXT, new XIncludeNamespaceSupport());
        
    } // <init>(SymbolTable,XMLGrammarPool)}
    
    /** Configures the pipeline. */
    protected void configurePipeline() {
        
        super.configurePipeline();
        // setup document pipeline
        // add the XPointerSchema component to the list of recognized components
        if (fXPointerSchemaComponent == null) {
            if( (fXPointerSchemaComponent = (XPointerSchema)getProperty(XINCLUDE_HANDLER)) !=null){
                addComponent(fXPointerSchemaComponent);
            }
        }
        // insert before fSchemaValidator, if one exists.
        XMLDocumentHandler next = null;
        if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
            // we don't have to worry about fSchemaValidator being null, since
            // super.configurePipeline() instantiated it if the feature was set
            next = fSchemaValidator.getDocumentHandler();
        }
        // Otherwise, insert after the last component in the pipeline
        
        if (next != null) {
            XMLDocumentSource prev = next.getDocumentSource();
            if (prev != null) {
                fXPointerSchemaComponent.setDocumentSource(prev);
                prev.setDocumentHandler(fXPointerSchemaComponent);
            }
            next.setDocumentSource(fXPointerSchemaComponent);
            fXPointerSchemaComponent.setDocumentHandler(next);
        }
        else {
            next = fLastComponent.getDocumentHandler();
            if (next != null) {
                fXPointerSchemaComponent.setDocumentHandler(next);
                next.setDocumentSource(fXPointerSchemaComponent);
            }
            fLastComponent.setDocumentHandler(fXPointerSchemaComponent);
            fXPointerSchemaComponent.setDocumentSource(fLastComponent);
            
            setDocumentHandler(fXPointerSchemaComponent);
        }
        
    } // configurePipeline()
}
