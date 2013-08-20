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

package com.sun.org.apache.xerces.internal.parsers;

import java.util.Vector;

import com.sun.org.apache.xerces.internal.dom.ASModelImpl;
import com.sun.org.apache.xerces.internal.dom3.as.ASModel;
import com.sun.org.apache.xerces.internal.dom3.as.DOMASBuilder;
import com.sun.org.apache.xerces.internal.dom3.as.DOMASException;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.ls.LSInput;

/**
 * This is Abstract Schema DOM Builder class. It extends the DOMParserImpl
 * class. Provides support for preparsing schemas.
 *
 * @deprecated
 * @author Pavani Mukthipudi, Sun Microsystems Inc.
 * @author Neil Graham, IBM
 * @version $Id: DOMASBuilderImpl.java,v 1.24 2003/11/17 13:48:41 venu Exp $
 *
 */

public class DOMASBuilderImpl
    extends DOMParserImpl implements DOMASBuilder {

    //
    // Constants
    //

    // Feature ids

    protected static final String SCHEMA_FULL_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;

    // Property ids

    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    protected static final String ENTITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    //
    // Data
    //

    protected XSGrammarBucket fGrammarBucket;

    protected ASModelImpl fAbstractSchema;

    //
    // Constructors
    //

    /**
     * Constructs a DOM Builder using the dtd/xml schema parser configuration.
     */
    public DOMASBuilderImpl() {
        super(new XMLGrammarCachingConfiguration());
    } // <init>

    /**
     * Constructs a DOM Builder using the specified parser configuration.
     * We must demand that the configuration extend XMLGrammarCachingConfiguration to make
     * sure all relevant methods/features are available.
     */
    public DOMASBuilderImpl(XMLGrammarCachingConfiguration config) {
        super(config);
    } // <init>(XMLParserConfiguration)

    /**
     * Constructs a DOM Builder using the specified symbol table.
     */
    public DOMASBuilderImpl(SymbolTable symbolTable) {
        super(new XMLGrammarCachingConfiguration(symbolTable));
    } // <init>(SymbolTable)


    /**
     * Constructs a DOM Builder using the specified symbol table and
     * grammar pool.
     * The grammarPool implementation should extent the default
     * implementation; otherwise, correct functioning of this class may
     * not occur.
     */
    public DOMASBuilderImpl(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
        super(new XMLGrammarCachingConfiguration(symbolTable, grammarPool));
    }

    //
    // DOMASBuilder methods
    //

    /**
     * Associate an <code>ASModel</code> with a document instance. This
     * <code>ASModel</code> will be used by the "
     * <code>validate-if-schema</code>" and "
     * <code>datatype-normalization</code>" options during the load of a new
     * <code>Document</code>.
     */
    public ASModel getAbstractSchema() {
        return fAbstractSchema;
    }

    /**
     * Associate an <code>ASModel</code> with a document instance. This
     * <code>ASModel</code> will be used by the "
     * <code>validate-if-schema</code>" and "
     * <code>datatype-normalization</code>" options during the load of a new
     * <code>Document</code>.
     */
    public void setAbstractSchema(ASModel abstractSchema) {

        // since the ASModel associated with this object is an attribute
        // according to the DOM IDL, we must obliterate anything
        // that was set before, rather than adding to it.
        // REVISIT:  so shouldn't we attempt to clear the
        // grammarPool before adding stuff to it?  - NG
        fAbstractSchema = (ASModelImpl)abstractSchema;

        // make sure the GrammarPool is properly initialized.
        XMLGrammarPool grammarPool = (XMLGrammarPool)fConfiguration.getProperty(StandardParserConfiguration.XMLGRAMMAR_POOL);
        // if there is no grammar pool, create one
        // REVISIT: ASBuilder should always create one.
        if (grammarPool == null) {
            // something's not right in this situation...
            grammarPool = new XMLGrammarPoolImpl();
            fConfiguration.setProperty(StandardParserConfiguration.XMLGRAMMAR_POOL,
                                       grammarPool);
        }
        if (fAbstractSchema != null) {
            initGrammarPool(fAbstractSchema, grammarPool);
        }
    }

    /**
     * Parse a Abstract Schema from a location identified by an URI.
     *
     * @param uri The location of the Abstract Schema to be read.
     * @return The newly created <code>Abstract Schema</code>.
     * @exception DOMASException
     *   Exceptions raised by <code>parseASURI()</code> originate with the
     *   installed ErrorHandler, and thus depend on the implementation of
     *   the <code>DOMErrorHandler</code> interfaces. The default error
     *   handlers will raise a <code>DOMASException</code> if any form of
     *   Abstract Schema inconsistencies or warning occurs during the parse,
     *   but application defined errorHandlers are not required to do so.
     *   <br> WRONG_MIME_TYPE_ERR: Raised when <code>mimeTypeCheck</code> is
     *   <code>true</code> and the inputsource has an incorrect MIME Type.
     *   See attribute <code>mimeTypeCheck</code>.
     * @exception DOMSystemException
     *   Exceptions raised by <code>parseURI()</code> originate with the
     *   installed ErrorHandler, and thus depend on the implementation of
     *   the <code>DOMErrorHandler</code> interfaces. The default error
     *   handlers will raise a DOMSystemException if any form I/O or other
     *   system error occurs during the parse, but application defined error
     *   handlers are not required to do so.
     */
    public ASModel parseASURI(String uri)
                              throws DOMASException, Exception {
        XMLInputSource source = new XMLInputSource(null, uri, null);
        return parseASInputSource(source);
    }

    /**
     * Parse a Abstract Schema from a location identified by an
     * <code>LSInput</code>.
     *
     * @param is The <code>LSInput</code> from which the source
     *   Abstract Schema is to be read.
     * @return The newly created <code>ASModel</code>.
     * @exception DOMASException
     *   Exceptions raised by <code>parseASURI()</code> originate with the
     *   installed ErrorHandler, and thus depend on the implementation of
     *   the <code>DOMErrorHandler</code> interfaces. The default error
     *   handlers will raise a <code>DOMASException</code> if any form of
     *   Abstract Schema inconsistencies or warning occurs during the parse,
     *   but application defined errorHandlers are not required to do so.
     *   <br> WRONG_MIME_TYPE_ERR: Raised when <code>mimeTypeCheck</code> is
     *   true and the inputsource has an incorrect MIME Type. See attribute
     *   <code>mimeTypeCheck</code>.
     * @exception DOMSystemException
     *   Exceptions raised by <code>parseURI()</code> originate with the
     *   installed ErrorHandler, and thus depend on the implementation of
     *   the <code>DOMErrorHandler</code> interfaces. The default error
     *   handlers will raise a DOMSystemException if any form I/O or other
     *   system error occurs during the parse, but application defined error
     *   handlers are not required to do so.
     */
    public ASModel parseASInputSource(LSInput is)
                                      throws DOMASException, Exception {
                                      
        // need to wrap the LSInput with an XMLInputSource
        XMLInputSource xis = this.dom2xmlInputSource(is);
        try {
            return parseASInputSource(xis);
        }
        catch (XNIException e) {
            Exception ex = e.getException();
            throw ex;
        }
    }

    ASModel parseASInputSource(XMLInputSource is) throws Exception {
                                      
        if (fGrammarBucket == null) {
            fGrammarBucket = new XSGrammarBucket();
        }

        initGrammarBucket();

        // actually do the parse:
        // save some casting
        XMLGrammarCachingConfiguration gramConfig = (XMLGrammarCachingConfiguration)fConfiguration;
        // ensure grammarPool doesn't absorb grammars while it's parsing
        gramConfig.lockGrammarPool();
        SchemaGrammar grammar = gramConfig.parseXMLSchema(is);
        gramConfig.unlockGrammarPool();

        ASModelImpl newAsModel = null;
        if (grammar != null) {
            newAsModel = new ASModelImpl();
            fGrammarBucket.putGrammar (grammar, true);
            addGrammars(newAsModel, fGrammarBucket);
        }
        return newAsModel;
    }

    // put all the grammars we have access to in the GrammarBucket
    private void initGrammarBucket() {
        fGrammarBucket.reset();
        if (fAbstractSchema != null)
            initGrammarBucketRecurse(fAbstractSchema);
    }
    private void initGrammarBucketRecurse(ASModelImpl currModel) {
        if(currModel.getGrammar() != null) {
            fGrammarBucket.putGrammar(currModel.getGrammar());
        }
        for(int i = 0; i < currModel.getInternalASModels().size(); i++) {
            ASModelImpl nextModel = (ASModelImpl)(currModel.getInternalASModels().elementAt(i));
            initGrammarBucketRecurse(nextModel);
        }
    }

    private void addGrammars(ASModelImpl model, XSGrammarBucket grammarBucket) {
        SchemaGrammar [] grammarList = grammarBucket.getGrammars();
        for(int i=0; i<grammarList.length; i++) {
            ASModelImpl newModel = new ASModelImpl();
            newModel.setGrammar(grammarList[i]);
            model.addASModel(newModel);
        }
    } // addGrammars

    private void initGrammarPool(ASModelImpl currModel, XMLGrammarPool grammarPool) {
        // put all the grammars in fAbstractSchema into the grammar pool.
        // grammarPool must never be null!
        Grammar[] grammars = new Grammar[1];
        if ((grammars[0] = (Grammar)currModel.getGrammar()) != null) {
            grammarPool.cacheGrammars(grammars[0].getGrammarDescription().getGrammarType(), grammars);
        }
        Vector modelStore = currModel.getInternalASModels();
        for (int i = 0; i < modelStore.size(); i++) {
            initGrammarPool((ASModelImpl)modelStore.elementAt(i), grammarPool);
        }
    }
} // class DOMASBuilderImpl
