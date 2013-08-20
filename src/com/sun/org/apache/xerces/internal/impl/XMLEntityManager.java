/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2004 The Apache Software Foundation.
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

package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLEntityDescriptionImpl;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.SecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeInputSource;
/**
 * The entity manager handles the registration of general and parameter
 * entities; resolves entities; and starts entities. The entity manager
 * is a central component in a standard parser configuration and this
 * class works directly with the entity scanner to manage the underlying
 * xni.
 * <p>
 * This component requires the following features and properties from the
 * component manager that uses it:
 * <ul>
 *  <li>http://xml.org/sax/features/validation</li>
 *  <li>http://xml.org/sax/features/external-general-entities</li>
 *  <li>http://xml.org/sax/features/external-parameter-entities</li>
 *  <li>http://apache.org/xml/features/allow-java-encodings</li>
 *  <li>http://apache.org/xml/properties/internal/symbol-table</li>
 *  <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *  <li>http://apache.org/xml/properties/internal/entity-resolver</li>
 *  <li>http://apache.org/xml/properties/security-manager</li>
 * </ul>
 *
 *
 * @author Andy Clark, IBM
 * @author Arnaud  Le Hors, IBM
 *
 * @version $Id: XMLEntityManager.java,v 1.79 2004/03/16 22:03:22 mrglavas Exp $
 */
public class XMLEntityManager
    implements XMLComponent, XMLEntityResolver {

    //
    // Constants
    //

    /** Default buffer size (2048). */
    public static final int DEFAULT_BUFFER_SIZE = 2048;

    /** Default buffer size before we've finished with the XMLDecl:  */
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;

    /** Default internal entity buffer size (1024). */
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;

    // feature identifiers

    /** Feature identifier: validation. */
    protected static final String VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;

    /** Feature identifier: external general entities. */
    protected static final String EXTERNAL_GENERAL_ENTITIES =
        Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE;

    /** Feature identifier: external parameter entities. */
    protected static final String EXTERNAL_PARAMETER_ENTITIES =
        Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;

    /** Feature identifier: allow Java encodings. */
    protected static final String ALLOW_JAVA_ENCODINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE;

    /** Feature identifier: warn on duplicate EntityDef */
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF =
    Constants.XERCES_FEATURE_PREFIX +Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE;

    /** Feature identifier: standard uri conformant */
    protected static final String STANDARD_URI_CONFORMANT =
    Constants.XERCES_FEATURE_PREFIX +Constants.STANDARD_URI_CONFORMANT_FEATURE;

    protected static final String PARSER_SETTINGS = 
        Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;    
    
    // property identifiers

    /** Property identifier: symbol table. */
    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    /** Property identifier: error reporter. */
    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    /** Property identifier: entity resolver. */
    protected static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;

    // property identifier:  ValidationManager
    protected static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;

    /** property identifier: buffer size. */
    protected static final String BUFFER_SIZE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.BUFFER_SIZE_PROPERTY;

    /** property identifier: security manager. */
    protected static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;

    // recognized features and properties

    /** Recognized features. */
    private static final String[] RECOGNIZED_FEATURES = {
        VALIDATION,
        EXTERNAL_GENERAL_ENTITIES,
        EXTERNAL_PARAMETER_ENTITIES,
        ALLOW_JAVA_ENCODINGS,
        WARN_ON_DUPLICATE_ENTITYDEF,
        STANDARD_URI_CONFORMANT
    };

    /** Feature defaults. */
    private static final Boolean[] FEATURE_DEFAULTS = {
        null,
        Boolean.TRUE,
        Boolean.TRUE,
        Boolean.TRUE,
        Boolean.FALSE,
        Boolean.FALSE
    };

    /** Recognized properties. */
    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ENTITY_RESOLVER,
        VALIDATION_MANAGER,
        BUFFER_SIZE,
        SECURITY_MANAGER,
    };

    /** Property defaults. */
    private static final Object[] PROPERTY_DEFAULTS = {
        null,
        null,
        null,
        null,
        new Integer(DEFAULT_BUFFER_SIZE),
        null,
    };

    private static final String XMLEntity = "[xml]".intern();
    private static final String DTDEntity = "[dtd]".intern();

    // debugging

    /**
     * Debug printing of buffer. This debugging flag works best when you
     * resize the DEFAULT_BUFFER_SIZE down to something reasonable like
     * 64 characters.
     */
    private static final boolean DEBUG_BUFFER = false;

    /** Debug some basic entities. */
    private static final boolean DEBUG_ENTITIES = false;

    /** Debug switching readers for encodings. */
    private static final boolean DEBUG_ENCODINGS = false;

    // should be diplayed trace resolving messages
    private static final boolean DEBUG_RESOLVER = false;

    //
    // Data
    //

    // features

    /**
     * Validation. This feature identifier is:
     * http://xml.org/sax/features/validation
     */
    protected boolean fValidation;

    /**
     * External general entities. This feature identifier is:
     * http://xml.org/sax/features/external-general-entities
     */
    protected boolean fExternalGeneralEntities = true;

    /**
     * External parameter entities. This feature identifier is:
     * http://xml.org/sax/features/external-parameter-entities
     */
    protected boolean fExternalParameterEntities = true;

    /**
     * Allow Java encoding names. This feature identifier is:
     * http://apache.org/xml/features/allow-java-encodings
     */
    protected boolean fAllowJavaEncodings;

    /** warn on duplicate Entity declaration.
     *  http://apache.org/xml/features/warn-on-duplicate-entitydef
     */
    protected boolean fWarnDuplicateEntityDef;

    /**
     * standard uri conformant (strict uri).
     * http://apache.org/xml/features/standard-uri-conformant
     */
    protected boolean fStrictURI;

    protected boolean fCharsetOverrideXmlEncoding;
    // properties

    /**
     * Symbol table. This property identifier is:
     * http://apache.org/xml/properties/internal/symbol-table
     */
    protected SymbolTable fSymbolTable;

    /**
     * Error reporter. This property identifier is:
     * http://apache.org/xml/properties/internal/error-reporter
     */
    protected XMLErrorReporter fErrorReporter;

    /**
     * Entity resolver. This property identifier is:
     * http://apache.org/xml/properties/internal/entity-resolver
     */
    protected XMLEntityResolver fEntityResolver;

    /**
     * Validation manager. This property identifier is:
     * http://apache.org/xml/properties/internal/validation-manager
     */
    protected ValidationManager fValidationManager;

    // settings

    /**
     * Buffer size. We get this value from a property. The default size
     * is used if the input buffer size property is not specified.
     * REVISIT: do we need a property for internal entity buffer size?
     */
    protected int fBufferSize = DEFAULT_BUFFER_SIZE;

    // stores defaults for entity expansion limit if it has
    // been set on the configuration.
    protected SecurityManager fSecurityManager = null;

    /**
     * True if the document entity is standalone. This should really
     * only be set by the document source (e.g. XMLDocumentScanner).
     */
    protected boolean fStandalone;

    // are the entities being parsed in the external subset?
    // NOTE:  this *is not* the same as whether they're external entities!
    protected boolean fInExternalSubset = false;

    // handlers

    /** Entity handler. */
    protected XMLEntityHandler fEntityHandler;

    // scanner

    /** Current entity scanner. */
    protected XMLEntityScanner fEntityScanner;

    /** XML 1.0 entity scanner. */
    protected XMLEntityScanner fXML10EntityScanner;

    /** XML 1.1 entity scanner. */
    protected XMLEntityScanner fXML11EntityScanner;

    // entity expansion limit (contains useful data if and only if
    // fSecurityManager is non-null)
    protected int fEntityExpansionLimit = 0;
    // entity currently being expanded:
    protected int fEntityExpansionCount = 0;

    // entities

    /** Entities. */
    protected Hashtable fEntities = new Hashtable();

    /** Entity stack. */
    protected Stack fEntityStack = new Stack();

    /** Current entity. */
    protected ScannedEntity fCurrentEntity;

    // shared context

    /** Shared declared entities. */
    protected Hashtable fDeclaredEntities;

    // temp vars

    /** Resource identifer. */
    private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
    
    /** Augmentations for entities. */
    private final Augmentations fEntityAugs = new AugmentationsImpl();

    //
    // Constructors
    //

    /** Default constructor. */
    public XMLEntityManager() {
        this(null);
    } // <init>()

    /**
     * Constructs an entity manager that shares the specified entity
     * declarations during each parse.
     * <p>
     * <strong>REVISIT:</strong> We might want to think about the "right"
     * way to expose the list of declared entities. For now, the knowledge
     * how to access the entity declarations is implicit.
     */
    public XMLEntityManager(XMLEntityManager entityManager) {


        // save shared entity declarations
        fDeclaredEntities = entityManager != null
                          ? entityManager.getDeclaredEntities() : null;

        setScannerVersion(Constants.XML_VERSION_1_0);
    } // <init>(XMLEntityManager)

    //
    // Public methods
    //

    /**
     * Sets whether the document entity is standalone.
     *
     * @param standalone True if document entity is standalone.
     */
    public void setStandalone(boolean standalone) {
        fStandalone = standalone;
    } // setStandalone(boolean)

    /** Returns true if the document entity is standalone. */
    public boolean isStandalone() {
        return fStandalone;
    } // isStandalone():boolean

    /**
     * Sets the entity handler. When an entity starts and ends, the
     * entity handler is notified of the change.
     *
     * @param entityHandler The new entity handler.
     */
    public void setEntityHandler(XMLEntityHandler entityHandler) {
        fEntityHandler = entityHandler;
    } // setEntityHandler(XMLEntityHandler)

    // this simply returns the fResourceIdentifier object;
    // this should only be used with caution by callers that
    // carefully manage the entity manager's behaviour, so that
    // this doesn't returning meaningless or misleading data.
    // @return  a reference to the current fResourceIdentifier object
    public XMLResourceIdentifier getCurrentResourceIdentifier() {
        return fResourceIdentifier;
    }

    // this simply returns the fCurrentEntity object;
    // this should only be used with caution by callers that
    // carefully manage the entity manager's behaviour, so that
    // this doesn't returning meaningless or misleading data.
    // @return  a reference to the current fCurrentEntity object
    public ScannedEntity getCurrentEntity() {
        return fCurrentEntity;
    }

    /**
     * Adds an internal entity declaration.
     * <p>
     * <strong>Note:</strong> This method ignores subsequent entity
     * declarations.
     * <p>
     * <strong>Note:</strong> The name should be a unique symbol. The
     * SymbolTable can be used for this purpose.
     *
     * @param name The name of the entity.
     * @param text The text of the entity.
     *
     * @see SymbolTable
     */
    public void addInternalEntity(String name, String text) {
        if (!fEntities.containsKey(name)) {
            Entity entity = new InternalEntity(name, text, fInExternalSubset);
            fEntities.put(name, entity);
        }
        else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                             "MSG_DUPLICATE_ENTITY_DEFINITION",
                                             new Object[]{ name },
                                             XMLErrorReporter.SEVERITY_WARNING );
            }
        }

    } // addInternalEntity(String,String)

    /**
     * Adds an external entity declaration.
     * <p>
     * <strong>Note:</strong> This method ignores subsequent entity
     * declarations.
     * <p>
     * <strong>Note:</strong> The name should be a unique symbol. The
     * SymbolTable can be used for this purpose.
     *
     * @param name         The name of the entity.
     * @param publicId     The public identifier of the entity.
     * @param literalSystemId     The system identifier of the entity.
     * @param baseSystemId The base system identifier of the entity.
     *                     This is the system identifier of the entity
     *                     where <em>the entity being added</em> and
     *                     is used to expand the system identifier when
     *                     the system identifier is a relative URI.
     *                     When null the system identifier of the first
     *                     external entity on the stack is used instead.
     *
     * @see SymbolTable
     */
    public void addExternalEntity(String name,
                                  String publicId, String literalSystemId,
                                  String baseSystemId) throws IOException {
        if (!fEntities.containsKey(name)) {
            if (baseSystemId == null) {
                // search for the first external entity on the stack
                int size = fEntityStack.size();
                if (size == 0 && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
                    baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
                }
                for (int i = size - 1; i >= 0 ; i--) {
                    ScannedEntity externalEntity =
                        (ScannedEntity)fEntityStack.elementAt(i);
                    if (externalEntity.entityLocation != null && externalEntity.entityLocation.getExpandedSystemId() != null) {
                        baseSystemId = externalEntity.entityLocation.getExpandedSystemId();
                        break;
                    }
                }
            }
            Entity entity = new ExternalEntity(name,
                new XMLEntityDescriptionImpl(name, publicId, literalSystemId, baseSystemId, 
                expandSystemId(literalSystemId, baseSystemId, false)), null, fInExternalSubset);
            fEntities.put(name, entity);
        }
        else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                             "MSG_DUPLICATE_ENTITY_DEFINITION",
                                             new Object[]{ name },
                                             XMLErrorReporter.SEVERITY_WARNING );
            }
        }

    } // addExternalEntity(String,String,String,String)

    /**
     * Checks whether an entity given by name is external.
     *
     * @param entityName The name of the entity to check.
     * @return True if the entity is external, false otherwise
     * (including when the entity is not declared).
     */
    public boolean isExternalEntity(String entityName) {

        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isExternal();
    }

    /**
     * Checks whether the declaration of an entity given by name is
     // in the external subset.
     *
     * @param entityName The name of the entity to check.
     * @return True if the entity was declared in the external subset, false otherwise
     *           (including when the entity is not declared).
     */
    public boolean isEntityDeclInExternalSubset(String entityName) {

        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isEntityDeclInExternalSubset();
    }

    /**
     * Adds an unparsed entity declaration.
     * <p>
     * <strong>Note:</strong> This method ignores subsequent entity
     * declarations.
     * <p>
     * <strong>Note:</strong> The name should be a unique symbol. The
     * SymbolTable can be used for this purpose.
     *
     * @param name     The name of the entity.
     * @param publicId The public identifier of the entity.
     * @param systemId The system identifier of the entity.
     * @param notation The name of the notation.
     *
     * @see SymbolTable
     */
    public void addUnparsedEntity(String name,
                                  String publicId, String systemId,
                                  String baseSystemId, String notation) {
        if (!fEntities.containsKey(name)) {
            Entity entity = new ExternalEntity(name, 
                new XMLEntityDescriptionImpl(name, publicId, systemId, baseSystemId, null), 
                notation, fInExternalSubset);
            fEntities.put(name, entity);
        }
        else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                             "MSG_DUPLICATE_ENTITY_DEFINITION",
                                             new Object[]{ name },
                                             XMLErrorReporter.SEVERITY_WARNING );
            }
        }
    } // addUnparsedEntity(String,String,String,String)

    /**
     * Checks whether an entity given by name is unparsed.
     *
     * @param entityName The name of the entity to check.
     * @return True if the entity is unparsed, false otherwise
     *          (including when the entity is not declared).
     */
    public boolean isUnparsedEntity(String entityName) {

        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isUnparsed();
    }

    /**
     * Checks whether an entity given by name is declared.
     *
     * @param entityName The name of the entity to check.
     * @return True if the entity is declared, false otherwise.
     */
    public boolean isDeclaredEntity(String entityName) {

        Entity entity = (Entity)fEntities.get(entityName);
        return entity != null;
    }

    /**
     * Resolves the specified public and system identifiers. This
     * method first attempts to resolve the entity based on the
     * EntityResolver registered by the application. If no entity
     * resolver is registered or if the registered entity handler
     * is unable to resolve the entity, then default entity
     * resolution will occur.
     *
     * @param publicId     The public identifier of the entity.
     * @param systemId     The system identifier of the entity.
     * @param baseSystemId The base system identifier of the entity.
     *                     This is the system identifier of the current
     *                     entity and is used to expand the system
     *                     identifier when the system identifier is a
     *                     relative URI.
     *
     * @return Returns an input source that wraps the resolved entity.
     *         This method will never return null.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity resolver to signal an error.
     */
    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
            throws IOException, XNIException {
        if(resourceIdentifier == null ) return null;
        String publicId = resourceIdentifier.getPublicId();
        String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();
        // if no base systemId given, assume that it's relative
        // to the systemId of the current scanned entity
        // Sometimes the system id is not (properly) expanded.
        // We need to expand the system id if:
        // a. the expanded one was null; or
        // b. the base system id was null, but becomes non-null from the current entity.
        boolean needExpand = (expandedSystemId == null);
        // REVISIT:  why would the baseSystemId ever be null?  if we
        // didn't have to make this check we wouldn't have to reuse the
        // fXMLResourceIdentifier object...
        if (baseSystemId == null && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
            baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null)
                needExpand = true;
         }
         if (needExpand)
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false);

       // give the entity resolver a chance
        XMLInputSource xmlInputSource = null;
        if (fEntityResolver != null) {
            resourceIdentifier.setBaseSystemId(baseSystemId);
            resourceIdentifier.setExpandedSystemId(expandedSystemId);
            xmlInputSource = fEntityResolver.resolveEntity(resourceIdentifier);
        }

        // do default resolution
        // REVISIT: what's the correct behavior if the user provided an entity
        // resolver (fEntityResolver != null), but resolveEntity doesn't return
        // an input source (xmlInputSource == null)?
        // do we do default resolution, or do we just return null? -SG
        if (xmlInputSource == null) {
            // REVISIT: when systemId is null, I think we should return null.
            //          is this the right solution? -SG
            //if (systemId != null)
            xmlInputSource = new XMLInputSource(publicId, literalSystemId, baseSystemId);
        }

        if (DEBUG_RESOLVER) {
            System.err.println("XMLEntityManager.resolveEntity(" + publicId + ")");
            System.err.println(" = " + xmlInputSource);
        }

        return xmlInputSource;

    } // resolveEntity(XMLResourceIdentifier):XMLInputSource

    /**
     * Starts a named entity.
     *
     * @param entityName The name of the entity to start.
     * @param literal    True if this entity is started within a literal
     *                   value.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startEntity(String entityName, boolean literal)
        throws IOException, XNIException {

        // was entity declared?
        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            if (fEntityHandler != null) {
                String encoding = null;
                fResourceIdentifier.clear();
                fEntityAugs.removeAllItems();
                fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding, fEntityAugs);
                fEntityAugs.removeAllItems();
                fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                fEntityHandler.endEntity(entityName, fEntityAugs);
            }
            return;
        }

        // should we skip external entities?
        boolean external = entity.isExternal();
        if (external && (fValidationManager == null || !fValidationManager.isCachedDTD())) {
            boolean unparsed = entity.isUnparsed();
            boolean parameter = entityName.startsWith("%");
            boolean general = !parameter;
            if (unparsed || (general && !fExternalGeneralEntities) ||
                (parameter && !fExternalParameterEntities)) {
                if (fEntityHandler != null) {
                    fResourceIdentifier.clear();
                    final String encoding = null;
                    ExternalEntity externalEntity = (ExternalEntity)entity;
                    //REVISIT:  since we're storing expandedSystemId in the
                    // externalEntity, how could this have got here if it wasn't already
                    // expanded??? - neilg
                    String extLitSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null);
                    String extBaseSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null);
                    String expandedSystemId = expandSystemId(extLitSysId, extBaseSysId, false);
                    fResourceIdentifier.setValues(
                            (externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null),
                            extLitSysId, extBaseSysId, expandedSystemId);
                    fEntityAugs.removeAllItems();
                    fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                    fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding, fEntityAugs);
                    fEntityAugs.removeAllItems();
                    fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                    fEntityHandler.endEntity(entityName, fEntityAugs);
                }
                return;
            }
        }

        // is entity recursive?
        int size = fEntityStack.size();
        for (int i = size; i >= 0; i--) {
            Entity activeEntity = i == size
                                ? fCurrentEntity
                                : (Entity)fEntityStack.elementAt(i);
            if (activeEntity.name == entityName) {
                String path = entityName;
                for (int j = i + 1; j < size; j++) {
                    activeEntity = (Entity)fEntityStack.elementAt(j);
                    path = path + " -> " + activeEntity.name;
                }
                path = path + " -> " + fCurrentEntity.name;
                path = path + " -> " + entityName;
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                           "RecursiveReference",
                                           new Object[] { entityName, path },
                                           XMLErrorReporter.SEVERITY_FATAL_ERROR);
                if (fEntityHandler != null) {
                    fResourceIdentifier.clear();
                    final String encoding = null;
                    if (external) {
                        ExternalEntity externalEntity = (ExternalEntity)entity;
                        // REVISIT:  for the same reason above...
                        String extLitSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null);
                        String extBaseSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null);
                        String expandedSystemId = expandSystemId(extLitSysId, extBaseSysId, false);
                        fResourceIdentifier.setValues(
                                (externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null),
                                extLitSysId, extBaseSysId, expandedSystemId);
                    }
                    fEntityAugs.removeAllItems();
                    fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                    fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding, fEntityAugs);
                    fEntityAugs.removeAllItems();
                    fEntityAugs.putItem(Constants.ENTITY_SKIPPED, Boolean.TRUE);
                    fEntityHandler.endEntity(entityName, fEntityAugs);
                }
                return;
            }
        }

        // resolve external entity
        XMLInputSource xmlInputSource = null;
        if (external) {
            ExternalEntity externalEntity = (ExternalEntity)entity;
            xmlInputSource = resolveEntity(externalEntity.entityLocation);
        }

        // wrap internal entity
        else {
            InternalEntity internalEntity = (InternalEntity)entity;
            Reader reader = new StringReader(internalEntity.text);
            xmlInputSource = new XMLInputSource(null, null, null, reader, null);
        }

        // start the entity
        startEntity(entityName, xmlInputSource, literal, external);

    } // startEntity(String,boolean)

    /**
     * Starts the document entity. The document entity has the "[xml]"
     * pseudo-name.
     *
     * @param xmlInputSource The input source of the document entity.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startDocumentEntity(XMLInputSource xmlInputSource)
        throws IOException, XNIException {
        startEntity(XMLEntity, xmlInputSource, false, true);
    } // startDocumentEntity(XMLInputSource)

    /**
     * Starts the DTD entity. The DTD entity has the "[dtd]"
     * pseudo-name.
     *
     * @param xmlInputSource The input source of the DTD entity.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startDTDEntity(XMLInputSource xmlInputSource)
        throws IOException, XNIException {
        startEntity(DTDEntity, xmlInputSource, false, true);
    } // startDTDEntity(XMLInputSource)

    // indicate start of external subset so that
    // location of entity decls can be tracked
    public void startExternalSubset() {
        fInExternalSubset = true;
    }

    public void endExternalSubset() {
        fInExternalSubset = false;
    }

    /**
     * Starts an entity.
     * <p>
     * This method can be used to insert an application defined XML
     * entity stream into the parsing stream.
     *
     * @param name           The name of the entity.
     * @param xmlInputSource The input source of the entity.
     * @param literal        True if this entity is started within a
     *                       literal value.
     * @param isExternal    whether this entity should be treated as an internal or external entity.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startEntity(String name,
                            XMLInputSource xmlInputSource,
                            boolean literal, boolean isExternal)
        throws IOException, XNIException {

        String encoding = setupCurrentEntity(name, xmlInputSource, literal, isExternal);
       
        //when entity expansion limit is set by the Application, we need to
        //check for the entity expansion limit set by the parser, if number of entity
        //expansions exceeds the entity expansion limit, parser will throw fatal error.
        // Note that this represents the nesting level of open entities.
        fEntityExpansionCount++;
        if( fSecurityManager != null && fEntityExpansionCount > fEntityExpansionLimit ){
            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                             "EntityExpansionLimitExceeded",
                                             new Object[]{new Integer(fEntityExpansionLimit) },
                                             XMLErrorReporter.SEVERITY_FATAL_ERROR );
            // is there anything better to do than reset the counter?
            // at least one can envision debugging applications where this might
            // be useful...
            fEntityExpansionCount = 0;
        }

        // call handler
        if (fEntityHandler != null) {
            fEntityHandler.startEntity(name, fResourceIdentifier, encoding, null);
        }

    } // startEntity(String,XMLInputSource)

    /**
     * This method uses the passed-in XMLInputSource to make
     * fCurrentEntity usable for reading.
     * @param name  name of the entity (XML is it's the document entity)
     * @param xmlInputSource    the input source, with sufficient information
     *      to begin scanning characters.
     * @param literal        True if this entity is started within a
     *                       literal value.
     * @param isExternal    whether this entity should be treated as an internal or external entity.
     * @throws IOException  if anything can't be read
     *  XNIException    If any parser-specific goes wrong.
     * @return the encoding of the new entity or null if a character stream was employed
     */
    public String setupCurrentEntity(String name, XMLInputSource xmlInputSource,
                boolean literal, boolean isExternal)
            throws IOException, XNIException {
        // get information

        final String publicId = xmlInputSource.getPublicId();
        String literalSystemId = xmlInputSource.getSystemId();
        String baseSystemId = xmlInputSource.getBaseSystemId();
        String encoding = xmlInputSource.getEncoding();
        Boolean isBigEndian = null;
        boolean declaredEncoding = false;

        // create reader
        InputStream stream = null;
        Reader reader = xmlInputSource.getCharacterStream();
        // First chance checking strict URI
        String expandedSystemId = expandSystemId(literalSystemId, baseSystemId, fStrictURI);
        if (baseSystemId == null) {
            baseSystemId = expandedSystemId;
        }
        if (reader == null) {
            stream = xmlInputSource.getByteStream();
            if(stream != null && encoding != null)
                declaredEncoding = true;
            if (stream == null) {
                URL location = new URL(expandedSystemId);
                URLConnection connect = location.openConnection();
                if (connect instanceof HttpURLConnection) {
               		setHttpProperties(connect,xmlInputSource); 
				}	
				stream = connect.getInputStream();
                
                // REVISIT: If the URLConnection has external encoding
                // information, we should be reading it here. It's located
                // in the charset parameter of Content-Type. -- mrglavas
                if (connect instanceof HttpURLConnection) {
                    String redirect = connect.getURL().toString();
                    // E43: Check if the URL was redirected, and then
                    // update literal and expanded system IDs if needed.
                    if (!redirect.equals(expandedSystemId)) {
                        literalSystemId = redirect;
                        expandedSystemId = redirect;
                    }
                }
            }
            // wrap this stream in RewindableInputStream
            stream = new RewindableInputStream(stream);

            // perform auto-detect of encoding if necessary
            if (encoding == null) {
                // read first four bytes and determine encoding
                final byte[] b4 = new byte[4];
                int count = 0;
                for (; count<4; count++ ) {
                    b4[count] = (byte)stream.read();
                }
                if (count == 4) {
                    Object [] encodingDesc = getEncodingName(b4, count);
                    encoding = (String)(encodingDesc[0]);
                    isBigEndian = (Boolean)(encodingDesc[1]);

                    stream.reset();
                    // Special case UTF-8 files with BOM created by Microsoft
                    // tools. It's more efficient to consume the BOM than make
                    // the reader perform extra checks. -Ac
                    if (count > 2 && encoding.equals("UTF-8")) {
                        int b0 = b4[0] & 0xFF;
                        int b1 = b4[1] & 0xFF;
                        int b2 = b4[2] & 0xFF;
                        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
                            // ignore first three bytes...
                            stream.skip(3);
                        }
                    }
                    reader = createReader(stream, encoding, isBigEndian);
                }
                else {
                    reader = createReader(stream, encoding, isBigEndian);
                }
            }

            // use specified encoding
            else {
                encoding = encoding.toUpperCase(Locale.ENGLISH);

                // If encoding is UTF-8, consume BOM if one is present.
                if (encoding.equals("UTF-8")) {
                    final int[] b3 = new int[3];
                    int count = 0;
                    for (; count < 3; ++count) {
                        b3[count] = stream.read();
                        if (b3[count] == -1)
                            break;
                    }
                    if (count == 3) {
                        if (b3[0] != 0xEF || b3[1] != 0xBB || b3[2] != 0xBF) {
                            // First three bytes are not BOM, so reset.
                            stream.reset();
                        }
                    }
                    else {
                        stream.reset();
                    }
                }
                // If encoding is UCS-4, we still need to read the first four bytes
                // in order to discover the byte order.
                else if (encoding.equals("ISO-10646-UCS-4")) {
                    final int[] b4 = new int[4];
                    int count = 0;
                    for (; count < 4; ++count) {
                        b4[count] = stream.read();
                        if (b4[count] == -1)
                            break;
                    }
                    stream.reset();

                    // Ignore unusual octet order for now.
                    if (count == 4) {
                        // UCS-4, big endian (1234)
                        if (b4[0] == 0x00 && b4[1] == 0x00 && b4[2] == 0x00 && b4[3] == 0x3C) {
                            isBigEndian = Boolean.TRUE;
                        }
                        // UCS-4, little endian (1234)
                        else if (b4[0] == 0x3C && b4[1] == 0x00 && b4[2] == 0x00 && b4[3] == 0x00) {
                            isBigEndian = Boolean.FALSE;
                        }
                    }
                }
                // If encoding is UCS-2, we still need to read the first four bytes
                // in order to discover the byte order.
                else if (encoding.equals("ISO-10646-UCS-2")) {
                    final int[] b4 = new int[4];
                    int count = 0;
                    for (; count < 4; ++count) {
                        b4[count] = stream.read();
                        if (b4[count] == -1)
                            break;
                    }
                    stream.reset();

                    if (count == 4) {
                        // UCS-2, big endian
                        if (b4[0] == 0x00 && b4[1] == 0x3C && b4[2] == 0x00 && b4[3] == 0x3F) {
                            isBigEndian = Boolean.TRUE;
                        }
                        // UCS-2, little endian
                        else if (b4[0] == 0x3C && b4[1] == 0x00 && b4[2] == 0x3F && b4[3] == 0x00) {
                            isBigEndian = Boolean.FALSE;
                        }
                    }
                }

                reader = createReader(stream, encoding, isBigEndian);
            }

            // read one character at a time so we don't jump too far
            // ahead, converting characters from the byte stream in
            // the wrong encoding
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ no longer wrapping reader in OneCharReader");
            }
            //reader = new OneCharReader(reader);
        }

        // We've seen a new Reader.
        // Push it on the stack so we can close it later.
        fReaderStack.push(reader);

        // push entity on stack
        if (fCurrentEntity != null) {
            fEntityStack.push(fCurrentEntity);
        }

        // create entity
        fCurrentEntity = new ScannedEntity(name,
                new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandedSystemId),
                stream, reader, encoding, literal, false, isExternal);
        fCurrentEntity.setDeclaredEncoding(declaredEncoding);
        fEntityScanner.setCurrentEntity(fCurrentEntity);
        fResourceIdentifier.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
        return encoding;
    } //setupCurrentEntity(String, XMLInputSource, boolean, boolean):  String

	void setHttpProperties(URLConnection conn, XMLInputSource source ){

		if( source instanceof XIncludeInputSource ){
			String fAccept = null;
			String fAcceptCharset = null;
			String fAcceptLanguage = null;
		
			XIncludeInputSource xiSrc= (XIncludeInputSource)source;
		
			fAccept = (String)xiSrc.getProperty(XIncludeHandler.HTTP_ACCEPT);
			fAcceptCharset = (String)xiSrc.getProperty(XIncludeHandler.HTTP_ACCEPT_CHARSET);
			fAcceptLanguage = (String)xiSrc.getProperty(XIncludeHandler.HTTP_ACCEPT_LANGUAGE);

			if( fAccept != null && fAccept.length() > 0) {
				conn.setRequestProperty(XIncludeHandler.HTTP_ACCEPT, fAccept);
			}
			if( fAcceptCharset != null && fAcceptCharset.length() > 0) {
				conn.setRequestProperty(XIncludeHandler.HTTP_ACCEPT_CHARSET, fAcceptCharset);
			}
			if( fAcceptLanguage != null && fAcceptLanguage.length() > 0) {
				conn.setRequestProperty(XIncludeHandler.HTTP_ACCEPT_LANGUAGE, fAcceptLanguage);
			}
		}
	}

    // set version of scanner to use
    public void setScannerVersion(short version) {
        if(version == Constants.XML_VERSION_1_0) {
            if(fXML10EntityScanner == null) {
                fXML10EntityScanner = new XMLEntityScanner();
            }
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML10EntityScanner;
            fEntityScanner.setCurrentEntity(fCurrentEntity);
        } else {
            if(fXML11EntityScanner == null) {
                fXML11EntityScanner = new XML11EntityScanner();
            }
            fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML11EntityScanner;
            fEntityScanner.setCurrentEntity(fCurrentEntity);
        }
    } // setScannerVersion(short)

    /** Returns the entity scanner. */
    public XMLEntityScanner getEntityScanner() {
        if(fEntityScanner == null) {
            // default to 1.0
            if(fXML10EntityScanner == null) {
                fXML10EntityScanner = new XMLEntityScanner();
            }
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML10EntityScanner;
        }
        return fEntityScanner;
    } // getEntityScanner():XMLEntityScanner

    // A stack containing all the open readers
    protected Stack fReaderStack = new Stack();

    /**
     * Close all opened InputStreams and Readers opened by this parser.
     */
    public void closeReaders() {
        // close all readers
        for (int i = fReaderStack.size()-1; i >= 0; i--) {
            try {
                ((Reader)fReaderStack.pop()).close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    //
    // XMLComponent methods
    //

    /**
     * Resets the component. The component can query the component manager
     * about any features and properties that affect the operation of the
     * component.
     *
     * @param componentManager The component manager.
     *
     * @throws SAXException Thrown by component on initialization error.
     *                      For example, if a feature or property is
     *                      required for the operation of the component, the
     *                      component manager may throw a
     *                      SAXNotRecognizedException or a
     *                      SAXNotSupportedException.
     */
    public void reset(XMLComponentManager componentManager)
        throws XMLConfigurationException {

        boolean parser_settings;
        try {
                parser_settings = componentManager.getFeature(PARSER_SETTINGS);
        } catch (XMLConfigurationException e) {
                parser_settings = true;
        }

        if (!parser_settings) {
            // parser settings have not been changed
            reset();
            return;
        }

        // sax features
        try {
            fValidation = componentManager.getFeature(VALIDATION);
        }
        catch (XMLConfigurationException e) {
            fValidation = false;
        }
        try {
            fExternalGeneralEntities = componentManager.getFeature(EXTERNAL_GENERAL_ENTITIES);
        }
        catch (XMLConfigurationException e) {
            fExternalGeneralEntities = true;
        }
        try {
            fExternalParameterEntities = componentManager.getFeature(EXTERNAL_PARAMETER_ENTITIES);
        }
        catch (XMLConfigurationException e) {
            fExternalParameterEntities = true;
        }

        // xerces features
        try {
            fAllowJavaEncodings = componentManager.getFeature(ALLOW_JAVA_ENCODINGS);
        }
        catch (XMLConfigurationException e) {
            fAllowJavaEncodings = false;
        }

        try {
            fWarnDuplicateEntityDef = componentManager.getFeature(WARN_ON_DUPLICATE_ENTITYDEF);
        }
        catch (XMLConfigurationException e) {
            fWarnDuplicateEntityDef = false;
        }

        try {
            fStrictURI = componentManager.getFeature(STANDARD_URI_CONFORMANT);
        }
        catch (XMLConfigurationException e) {
            fStrictURI = false;
        }
        try {
            fCharsetOverrideXmlEncoding = componentManager.getFeature(Constants.DOM_CHARSET_OVERRIDES_XML_ENCODING);
        }
        catch (XMLConfigurationException e) {
            fCharsetOverrideXmlEncoding = true;
        }
        // xerces properties
        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        try {
            fEntityResolver = (XMLEntityResolver)componentManager.getProperty(ENTITY_RESOLVER);
        }
        catch (XMLConfigurationException e) {
            fEntityResolver = null;
        }
        try {
            fValidationManager = (ValidationManager)componentManager.getProperty(VALIDATION_MANAGER);
        }
        catch (XMLConfigurationException e) {
            fValidationManager = null;
        }
        try {
            fSecurityManager = (SecurityManager)componentManager.getProperty(SECURITY_MANAGER);
        }
        catch (XMLConfigurationException e) {
            fSecurityManager = null;
        }

        // reset general state
        reset();

    } // reset(XMLComponentManager)

    // reset general state.  Should not be called other than by
    // a class acting as a component manager but not
    // implementing that interface for whatever reason.
    public void reset() {
        fEntityExpansionLimit = (fSecurityManager != null)?fSecurityManager.getEntityExpansionLimit():0;

        // initialize state
        fStandalone = false;
        fEntities.clear();
        fEntityStack.removeAllElements();
        fEntityExpansionCount = 0;

        fCurrentEntity = null;
        // reset scanner
        if(fXML10EntityScanner != null){
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
        }
        if(fXML11EntityScanner != null) {
            fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
        }

        // DEBUG
        if (DEBUG_ENTITIES) {
            addInternalEntity("text", "Hello, World.");
            addInternalEntity("empty-element", "<foo/>");
            addInternalEntity("balanced-element", "<foo></foo>");
            addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
            addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
            addInternalEntity("unbalanced-entity", "<foo>");
            addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
            addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
            addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
            try {
                addExternalEntity("external-text", null, "external-text.ent", "test/external-text.xml");
                addExternalEntity("external-balanced-element", null, "external-balanced-element.ent", "test/external-balanced-element.xml");
                addExternalEntity("one", null, "ent/one.ent", "test/external-entity.xml");
                addExternalEntity("two", null, "ent/two.ent", "test/ent/one.xml");
            }
            catch (IOException ex) {
                // should never happen
            }
        }

        // copy declared entities
        if (fDeclaredEntities != null) {
            java.util.Enumeration keys = fDeclaredEntities.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = fDeclaredEntities.get(key);
                fEntities.put(key, value);
            }
        }
        fEntityHandler = null;

    } // reset(XMLComponentManager)

    /**
     * Returns a list of feature identifiers that are recognized by
     * this component. This method may return null if no features
     * are recognized by this component.
     */
    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } // getRecognizedFeatures():String[]

    /**
     * Sets the state of a feature. This method is called by the component
     * manager any time after reset when a feature changes state.
     * <p>
     * <strong>Note:</strong> Components should silently ignore features
     * that do not affect the operation of the component.
     *
     * @param featureId The feature identifier.
     * @param state     The state of the feature.
     *
     * @throws SAXNotRecognizedException The component should not throw
     *                                   this exception.
     * @throws SAXNotSupportedException The component should not throw
     *                                  this exception.
     */
    public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {

        // xerces features
        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();
            if (suffixLength == Constants.ALLOW_JAVA_ENCODINGS_FEATURE.length() && 
                featureId.endsWith(Constants.ALLOW_JAVA_ENCODINGS_FEATURE)) {
                fAllowJavaEncodings = state;
            }
        }

    } // setFeature(String,boolean)

    /**
     * Returns a list of property identifiers that are recognized by
     * this component. This method may return null if no properties
     * are recognized by this component.
     */
    public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } // getRecognizedProperties():String[]

    /**
     * Sets the value of a property. This method is called by the component
     * manager any time after reset when a property changes value.
     * <p>
     * <strong>Note:</strong> Components should silently ignore properties
     * that do not affect the operation of the component.
     *
     * @param propertyId The property identifier.
     * @param value      The value of the property.
     *
     * @throws SAXNotRecognizedException The component should not throw
     *                                   this exception.
     * @throws SAXNotSupportedException The component should not throw
     *                                  this exception.
     */
    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        // Xerces properties
        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();
        	
            if (suffixLength == Constants.SYMBOL_TABLE_PROPERTY.length() && 
                propertyId.endsWith(Constants.SYMBOL_TABLE_PROPERTY)) {
                fSymbolTable = (SymbolTable)value;
                return;
            }
            if (suffixLength == Constants.ERROR_REPORTER_PROPERTY.length() && 
                propertyId.endsWith(Constants.ERROR_REPORTER_PROPERTY)) {
                fErrorReporter = (XMLErrorReporter)value;
                return;
            }
            if (suffixLength == Constants.ENTITY_RESOLVER_PROPERTY.length() && 
                propertyId.endsWith(Constants.ENTITY_RESOLVER_PROPERTY)) {
                fEntityResolver = (XMLEntityResolver)value;
                return;
            }
            if (suffixLength == Constants.BUFFER_SIZE_PROPERTY.length() && 
                propertyId.endsWith(Constants.BUFFER_SIZE_PROPERTY)) {
                Integer bufferSize = (Integer)value;
                if (bufferSize != null &&
                    bufferSize.intValue() > DEFAULT_XMLDECL_BUFFER_SIZE) {
                    fBufferSize = bufferSize.intValue();
                    fEntityScanner.setBufferSize(fBufferSize);
                }
            }
            if (suffixLength == Constants.SECURITY_MANAGER_PROPERTY.length() && 
                propertyId.endsWith(Constants.SECURITY_MANAGER_PROPERTY)) {
                fSecurityManager = (SecurityManager)value; 
                fEntityExpansionLimit = (fSecurityManager != null)?fSecurityManager.getEntityExpansionLimit():0;
            }
        }

    } // setProperty(String,Object)

    /**
     * Returns the default state for a feature, or null if this
     * component does not want to report a default value for this
     * feature.
     *
     * @param featureId The feature identifier.
     *
     * @since Xerces 2.2.0
     */
    public Boolean getFeatureDefault(String featureId) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
            if (RECOGNIZED_FEATURES[i].equals(featureId)) {
                return FEATURE_DEFAULTS[i];
            }
        }
        return null;
    } // getFeatureDefault(String):Boolean

    /**
     * Returns the default state for a property, or null if this
     * component does not want to report a default value for this
     * property.
     *
     * @param propertyId The property identifier.
     *
     * @since Xerces 2.2.0
     */
    public Object getPropertyDefault(String propertyId) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
            if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    } // getPropertyDefault(String):Object

    //
    // Public static methods
    //

    // current value of the "user.dir" property
    private static String gUserDir;
    // escaped value of the current "user.dir" property
    private static String gEscapedUserDir;
    // which ASCII characters need to be escaped
    private static boolean gNeedEscaping[] = new boolean[128];
    // the first hex character if a character needs to be escaped
    private static char gAfterEscaping1[] = new char[128];
    // the second hex character if a character needs to be escaped
    private static char gAfterEscaping2[] = new char[128];
    private static char[] gHexChs = {'0', '1', '2', '3', '4', '5', '6', '7',
                                     '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    // initialize the above 3 arrays
    static {
        for (int i = 0; i <= 0x1f; i++) {
            gNeedEscaping[i] = true;
            gAfterEscaping1[i] = gHexChs[i >> 4];
            gAfterEscaping2[i] = gHexChs[i & 0xf];
        }
        gNeedEscaping[0x7f] = true;
        gAfterEscaping1[0x7f] = '7';
        gAfterEscaping2[0x7f] = 'F';
        char[] escChs = {' ', '<', '>', '#', '%', '"', '{', '}',
                         '|', '\\', '^', '~', '[', ']', '`'};
        int len = escChs.length;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = escChs[i];
            gNeedEscaping[ch] = true;
            gAfterEscaping1[ch] = gHexChs[ch >> 4];
            gAfterEscaping2[ch] = gHexChs[ch & 0xf];
        }
    }
    // To escape the "user.dir" system property, by using %HH to represent
    // special ASCII characters: 0x00~0x1F, 0x7F, ' ', '<', '>', '#', '%'
    // and '"'. It's a static method, so needs to be synchronized.
    // this method looks heavy, but since the system property isn't expected
    // to change often, so in most cases, we only need to return the string
    // that was escaped before.
    // According to the URI spec, non-ASCII characters (whose value >= 128)
    // need to be escaped too.
    // REVISIT: don't know how to escape non-ASCII characters, especially
    // which encoding to use. Leave them for now.
    private static synchronized String getUserDir() {
        // get the user.dir property
        String userDir = "";
        try {
            userDir = System.getProperty("user.dir");
        }
        catch (SecurityException se) {
        }

        // return empty string if property value is empty string.
        if (userDir.length() == 0)
            return "";

        // compute the new escaped value if the new property value doesn't
        // match the previous one
        if (userDir.equals(gUserDir)) {
            return gEscapedUserDir;
        }

        // record the new value as the global property value
        gUserDir = userDir;

        char separator = java.io.File.separatorChar;
        userDir = userDir.replace(separator, '/');

        int len = userDir.length(), ch;
        StringBuffer buffer = new StringBuffer(len*3);
        // change C:/blah to /C:/blah
        if (len >= 2 && userDir.charAt(1) == ':') {
            ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 'A' && ch <= 'Z') {
                buffer.append('/');
            }
        }

        // for each character in the path
        int i = 0;
        for (; i < len; i++) {
            ch = userDir.charAt(i);
            // if it's not an ASCII character, break here, and use UTF-8 encoding
            if (ch >= 128)
                break;
            if (gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(gAfterEscaping1[ch]);
                buffer.append(gAfterEscaping2[ch]);
                // record the fact that it's escaped
            }
            else {
                buffer.append((char)ch);
            }
        }

        // we saw some non-ascii character
        if (i < len) {
            // get UTF-8 bytes for the remaining sub-string
            byte[] bytes = null;
            byte b;
            try {
                bytes = userDir.substring(i).getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                // should never happen
                return userDir;
            }
            len = bytes.length;

            // for each byte
            for (i = 0; i < len; i++) {
                b = bytes[i];
                // for non-ascii character: make it positive, then escape
                if (b < 0) {
                    ch = b + 256;
                    buffer.append('%');
                    buffer.append(gHexChs[ch >> 4]);
                    buffer.append(gHexChs[ch & 0xf]);
                }
                else if (gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(gAfterEscaping1[b]);
                    buffer.append(gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }

        // change blah/blah to blah/blah/
        if (!userDir.endsWith("/"))
            buffer.append('/');

        gEscapedUserDir = buffer.toString();

        return gEscapedUserDir;
    }

    /**
     * Expands a system id and returns the system id as a URI, if
     * it can be expanded. A return value of null means that the
     * identifier is already expanded. An exception thrown
     * indicates a failure to expand the id.
     *
     * @param systemId The systemId to be expanded.
     *
     * @return Returns the URI string representing the expanded system
     *         identifier. A null value indicates that the given
     *         system identifier is already expanded.
     *
     */
    public static String expandSystemId(String systemId, String baseSystemId,
                                        boolean strict)
            throws URI.MalformedURIException {

        // system id has to be a valid URI
        if (strict) {
            
            // check if there is a system id before 
            // trying to expand it.
            if (systemId == null) {
                return null;
            }
            
            try {
                // if it's already an absolute one, return it
                new URI(systemId);
                return systemId;
            }
            catch (URI.MalformedURIException ex) {
            }
            URI base = null;
            // if there isn't a base uri, use the working directory
            if (baseSystemId == null || baseSystemId.length() == 0) {
                base = new URI("file", "", getUserDir(), null, null);
            }
            // otherwise, use the base uri
            else {
                try {
                    base = new URI(baseSystemId);
                }
                catch (URI.MalformedURIException e) {
                    // assume "base" is also a relative uri
                    String dir = getUserDir();
                    dir = dir + baseSystemId;
                    base = new URI("file", "", dir, null, null);
                }
            }
            // absolutize the system id using the base
            URI uri = new URI(base, systemId);
            // return the string rep of the new uri (an absolute one)
            return uri.toString();

            // if any exception is thrown, it'll get thrown to the caller.
        }

        // check for bad parameters id
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        // if id already expanded, return
        try {
            URI uri = new URI(systemId.trim());
            if (uri != null) {
                return systemId;
            }
        }
        catch (URI.MalformedURIException e) {
            // continue on...
        }
        // normalize id
        String id = fixURI(systemId);

        // normalize base
        URI base = null;
        URI uri = null;
        try {
            if (baseSystemId == null || baseSystemId.length() == 0 ||
                baseSystemId.equals(systemId)) {
                String dir = getUserDir();
                base = new URI("file", "", dir, null, null);
            }
            else {
                try {
                    base = new URI(fixURI(baseSystemId).trim());
                }
                catch (URI.MalformedURIException e) {
                    if (baseSystemId.indexOf(':') != -1) {
                        // for xml schemas we might have baseURI with
                        // a specified drive
                        base = new URI("file", "", fixURI(baseSystemId).trim(), null, null);
                    }
                    else {
                        String dir = getUserDir();
                        dir = dir + fixURI(baseSystemId);
                        base = new URI("file", "", dir, null, null);
                    }
                }
             }
             // expand id
             uri = new URI(base, id.trim());
        }
        catch (Exception e) {
            // let it go through

        }

        if (uri == null) {
            return systemId;
        }
        return uri.toString();

    } // expandSystemId(String,String):String

    //
    // Protected methods
    //

    /**
     * Ends an entity.
     *
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    void endEntity() throws XNIException {
        
        // call handler
        if (DEBUG_BUFFER) {
            System.out.print("(endEntity: ");
            print(fCurrentEntity);
            System.out.println();
        }
        if (fEntityHandler != null) {
            fEntityHandler.endEntity(fCurrentEntity.name, null);
        }
        
        // Close the reader for the current entity once we're 
        // done with it, and remove it from our stack. If parsing
        // is halted at some point, the rest of the readers on
        // the stack will be closed during cleanup.
        try {
            fCurrentEntity.reader.close();
        }
        catch (IOException e) {
            // ignore
        }
        // REVISIT: We should never encounter underflow if the calls
        // to startEntity and endEntity are balanced, but guard
        // against the EmptyStackException for now. -- mrglavas
        if(!fReaderStack.isEmpty()) {
            fReaderStack.pop();
        } 

        // Pop entity stack.
        fCurrentEntity = fEntityStack.size() > 0
                       ? (ScannedEntity)fEntityStack.pop() : null;
        fEntityScanner.setCurrentEntity(fCurrentEntity);
        if (DEBUG_BUFFER) {
            System.out.print(")endEntity: ");
            print(fCurrentEntity);
            System.out.println();
        }
        
        // as entity is no longer open, decrement open entity expansion count
        
    } // endEntity()

    /**
     * Returns the IANA encoding name that is auto-detected from
     * the bytes specified, with the endian-ness of that encoding where appropriate.
     *
     * @param b4    The first four bytes of the input.
     * @param count The number of bytes actually read.
     * @return a 2-element array:  the first element, an IANA-encoding string,
     *  the second element a Boolean which is true iff the document is big endian, false
     *  if it's little-endian, and null if the distinction isn't relevant.
     */
    protected Object[] getEncodingName(byte[] b4, int count) {

        if (count < 2) {
            return new Object[]{"UTF-8", null};
        }

        // UTF-16, with BOM
        int b0 = b4[0] & 0xFF;
        int b1 = b4[1] & 0xFF;
        if (b0 == 0xFE && b1 == 0xFF) {
            // UTF-16, big-endian
            return new Object [] {"UTF-16BE", new Boolean(true)};
        }
        if (b0 == 0xFF && b1 == 0xFE) {
            // UTF-16, little-endian
            return new Object [] {"UTF-16LE", new Boolean(false)};
        }

        // default to UTF-8 if we don't have enough bytes to make a
        // good determination of the encoding
        if (count < 3) {
            return new Object [] {"UTF-8", null};
        }

        // UTF-8 with a BOM
        int b2 = b4[2] & 0xFF;
        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
            return new Object [] {"UTF-8", null};
        }

        // default to UTF-8 if we don't have enough bytes to make a
        // good determination of the encoding
        if (count < 4) {
            return new Object [] {"UTF-8", null};
        }

        // other encodings
        int b3 = b4[3] & 0xFF;
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x00 && b3 == 0x3C) {
            // UCS-4, big endian (1234)
            return new Object [] {"ISO-10646-UCS-4", new Boolean(true)};
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x00 && b3 == 0x00) {
            // UCS-4, little endian (4321)
            return new Object [] {"ISO-10646-UCS-4", new Boolean(false)};
        }
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x3C && b3 == 0x00) {
            // UCS-4, unusual octet order (2143)
            // REVISIT: What should this be?
            return new Object [] {"ISO-10646-UCS-4", null};
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x00) {
            // UCS-4, unusual octect order (3412)
            // REVISIT: What should this be?
            return new Object [] {"ISO-10646-UCS-4", null};
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x3F) {
            // UTF-16, big-endian, no BOM
            // (or could turn out to be UCS-2...
            // REVISIT: What should this be?
            return new Object [] {"UTF-16BE", new Boolean(true)};
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x3F && b3 == 0x00) {
            // UTF-16, little-endian, no BOM
            // (or could turn out to be UCS-2...
            return new Object [] {"UTF-16LE", new Boolean(false)};
        }
        if (b0 == 0x4C && b1 == 0x6F && b2 == 0xA7 && b3 == 0x94) {
            // EBCDIC
            // a la xerces1, return CP037 instead of EBCDIC here
            return new Object [] {"CP037", null};
        }

        // default encoding
        return new Object [] {"UTF-8", null};

    } // getEncodingName(byte[],int):Object[]

    /**
     * Creates a reader capable of reading the given input stream in
     * the specified encoding.
     *
     * @param inputStream  The input stream.
     * @param encoding     The encoding name that the input stream is
     *                     encoded using. If the user has specified that
     *                     Java encoding names are allowed, then the
     *                     encoding name may be a Java encoding name;
     *                     otherwise, it is an ianaEncoding name.
     * @param isBigEndian   For encodings (like uCS-4), whose names cannot
     *                      specify a byte order, this tells whether the order is bigEndian.  null menas
     *                      unknown or not relevant.
     *
     * @return Returns a reader.
     */
    protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian)
        throws IOException {

        // normalize encoding name
        if (encoding == null) {
            encoding = "UTF-8";
        }

        // try to use an optimized reader
        String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
        if (ENCODING.equals("UTF-8")) {
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ creating UTF8Reader");
            }
            return new UTF8Reader(inputStream, fBufferSize, fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN), fErrorReporter.getLocale() );
        }
        if(ENCODING.equals("ISO-10646-UCS-4")) {
            if(isBigEndian != null) {
                boolean isBE = isBigEndian.booleanValue();
                if(isBE) {
                    return new UCSReader(inputStream, UCSReader.UCS4BE);
                } else {
                    return new UCSReader(inputStream, UCSReader.UCS4LE);
                }
            } else {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                       "EncodingByteOrderUnsupported",
                                       new Object[] { encoding },
                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
        }
        if(ENCODING.equals("ISO-10646-UCS-2")) {
            if(isBigEndian != null) { // sould never happen with this encoding...
                boolean isBE = isBigEndian.booleanValue();
                if(isBE) {
                    return new UCSReader(inputStream, UCSReader.UCS2BE);
                } else {
                    return new UCSReader(inputStream, UCSReader.UCS2LE);
                }
            } else {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                       "EncodingByteOrderUnsupported",
                                       new Object[] { encoding },
                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
        }

        // check for valid name
        boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || (fAllowJavaEncodings && !validJava)) {
            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                       "EncodingDeclInvalid",
                                       new Object[] { encoding },
                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
            // NOTE: AndyH suggested that, on failure, we use ISO Latin 1
            //       because every byte is a valid ISO Latin 1 character.
            //       It may not translate correctly but if we failed on
            //       the encoding anyway, then we're expecting the content
            //       of the document to be bad. This will just prevent an
            //       invalid UTF-8 sequence to be detected. This is only
            //       important when continue-after-fatal-error is turned
            //       on. -Ac
            encoding = "ISO-8859-1";
        }

        // try to use a Java reader
        String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
        if (javaEncoding == null) {
            if(fAllowJavaEncodings) {
            javaEncoding = encoding;
            } else {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                       "EncodingDeclInvalid",
                                       new Object[] { encoding },
                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
                // see comment above.
                javaEncoding = "ISO8859_1";
            }
        }
        else if (javaEncoding.equals("ASCII")) {
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ creating ASCIIReader");
            }
            return new ASCIIReader(inputStream, fBufferSize, fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN), fErrorReporter.getLocale());
        }
        
        
        if (DEBUG_ENCODINGS) {
            System.out.print("$$$ creating Java InputStreamReader: encoding="+javaEncoding);
            if (javaEncoding == encoding) {
                System.out.print(" (IANA encoding)");
            }
            System.out.println();
        }
        return new InputStreamReader(inputStream, javaEncoding);

    } // createReader(InputStream,String, Boolean): Reader

    //
    // Protected static methods
    //

    /**
     * Fixes a platform dependent filename to standard URI form.
     *
     * @param str The string to fix.
     *
     * @return Returns the fixed URI string.
     */
    protected static String fixURI(String str) {

        // handle platform dependent strings
        str = str.replace(java.io.File.separatorChar, '/');

        StringBuffer sb = null;

        // Windows fix
        if (str.length() >= 2) {
            char ch1 = str.charAt(1);
            // change "C:blah" to "/C:blah"
            if (ch1 == ':') {
                char ch0 = Character.toUpperCase(str.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    sb = new StringBuffer(str.length());
                    sb.append('/');
                }
            }
            // change "//blah" to "file://blah"
            else if (ch1 == '/' && str.charAt(0) == '/') {
                sb = new StringBuffer(str.length());
                sb.append("file:");
            }
        }

        int pos = str.indexOf(' ');
        // there is no space in the string
        // we just append "str" to the end of sb
        if (pos < 0) {
            if (sb != null) {
                sb.append(str);
                str = sb.toString();
            }
        }
        // otherwise, convert all ' ' to "%20".
        // Note: the following algorithm might not be very performant,
        // but people who want to use invalid URI's have to pay the price.
        else {
            if (sb == null)
                sb = new StringBuffer(str.length());
            // put characters before ' ' into the string buffer
            for (int i = 0; i < pos; i++)
                sb.append(str.charAt(i));
            // and %20 for the space
            sb.append("%20");
            // for the remamining part, also convert ' ' to "%20".
            for (int i = pos+1; i < str.length(); i++) {
                if (str.charAt(i) == ' ')
                    sb.append("%20");
                else
                    sb.append(str.charAt(i));
            }
            str = sb.toString();
        }

        // done
        return str;

    } // fixURI(String):String

    //
    // Package visible methods
    //

    /**
     * Returns the hashtable of declared entities.
     * <p>
     * <strong>REVISIT:</strong>
     * This should be done the "right" way by designing a better way to
     * enumerate the declared entities. For now, this method is needed
     * by the constructor that takes an XMLEntityManager parameter.
     */
    Hashtable getDeclaredEntities() {
        return fEntities;
    } // getDeclaredEntities():Hashtable

    /** Prints the contents of the buffer. */
    static final void print(ScannedEntity currentEntity) {
        if (DEBUG_BUFFER) {
            if (currentEntity != null) {
                System.out.print('[');
                System.out.print(currentEntity.count);
                System.out.print(' ');
                System.out.print(currentEntity.position);
                if (currentEntity.count > 0) {
                    System.out.print(" \"");
                    for (int i = 0; i < currentEntity.count; i++) {
                        if (i == currentEntity.position) {
                            System.out.print('^');
                        }
                        char c = currentEntity.ch[i];
                        switch (c) {
                            case '\n': {
                                System.out.print("\\n");
                                break;
                            }
                            case '\r': {
                                System.out.print("\\r");
                                break;
                            }
                            case '\t': {
                                System.out.print("\\t");
                                break;
                            }
                            case '\\': {
                                System.out.print("\\\\");
                                break;
                            }
                            default: {
                                System.out.print(c);
                            }
                        }
                    }
                    if (currentEntity.position == currentEntity.count) {
                        System.out.print('^');
                    }
                    System.out.print('"');
                }
                System.out.print(']');
                System.out.print(" @ ");
                System.out.print(currentEntity.lineNumber);
                System.out.print(',');
                System.out.print(currentEntity.columnNumber);
            }
            else {
                System.out.print("*NO CURRENT ENTITY*");
            }
        }
    } // print(ScannedEntity)

    //
    // Classes
    //

    /**
     * Entity information.
     *
     * @author Andy Clark, IBM
     */
    public static abstract class Entity {

        //
        // Data
        //

        /** Entity name. */
        public String name;

        // whether this entity's declaration was found in the internal
        // or external subset
        public boolean inExternalSubset;

        //
        // Constructors
        //

        /** Default constructor. */
        public Entity() {
            clear();
        } // <init>()

        /** Constructs an entity. */
        public Entity(String name, boolean inExternalSubset) {
            this.name = name;
            this.inExternalSubset = inExternalSubset;
        } // <init>(String)

        //
        // Public methods
        //

        /** Returns true if this entity was declared in the external subset. */
        public boolean isEntityDeclInExternalSubset () {
            return inExternalSubset;
        }

        /** Returns true if this is an external entity. */
        public abstract boolean isExternal();

        /** Returns true if this is an unparsed entity. */
        public abstract boolean isUnparsed();

        /** Clears the entity. */
        public void clear() {
            name = null;
            inExternalSubset = false;
        } // clear()

        /** Sets the values of the entity. */
        public void setValues(Entity entity) {
            name = entity.name;
            inExternalSubset = entity.inExternalSubset;
        } // setValues(Entity)

    } // class Entity

    /**
     * Internal entity.
     *
     * @author Andy Clark, IBM
     */
    protected static class InternalEntity
        extends Entity {

        //
        // Data
        //

        /** Text value of entity. */
        public String text;

        //
        // Constructors
        //

        /** Default constructor. */
        public InternalEntity() {
            clear();
        } // <init>()

        /** Constructs an internal entity. */
        public InternalEntity(String name, String text, boolean inExternalSubset) {
            super(name,inExternalSubset);
            this.text = text;
        } // <init>(String,String)

        //
        // Entity methods
        //

        /** Returns true if this is an external entity. */
        public final boolean isExternal() {
            return false;
        } // isExternal():boolean

        /** Returns true if this is an unparsed entity. */
        public final boolean isUnparsed() {
            return false;
        } // isUnparsed():boolean

        /** Clears the entity. */
        public void clear() {
            super.clear();
            text = null;
        } // clear()

        /** Sets the values of the entity. */
        public void setValues(Entity entity) {
            super.setValues(entity);
            text = null;
        } // setValues(Entity)

        /** Sets the values of the entity. */
        public void setValues(InternalEntity entity) {
            super.setValues(entity);
            text = entity.text;
        } // setValues(InternalEntity)

    } // class InternalEntity

    /**
     * External entity.
     *
     * @author Andy Clark, IBM
     */
    protected static class ExternalEntity
        extends Entity {

        //
        // Data
        //

        /** container for all relevant entity location information. */
        public XMLResourceIdentifier entityLocation;

        /** Notation name for unparsed entity. */
        public String notation;

        //
        // Constructors
        //

        /** Default constructor. */
        public ExternalEntity() {
            clear();
        } // <init>()

        /** Constructs an internal entity. */
        public ExternalEntity(String name, XMLResourceIdentifier entityLocation,
                              String notation, boolean inExternalSubset) {
            super(name,inExternalSubset);
            this.entityLocation = entityLocation;
            this.notation = notation;
        } // <init>(String,XMLResourceIdentifier, String)

        //
        // Entity methods
        //

        /** Returns true if this is an external entity. */
        public final boolean isExternal() {
            return true;
        } // isExternal():boolean

        /** Returns true if this is an unparsed entity. */
        public final boolean isUnparsed() {
            return notation != null;
        } // isUnparsed():boolean

        /** Clears the entity. */
        public void clear() {
            super.clear();
            entityLocation = null;
            notation = null;
        } // clear()

        /** Sets the values of the entity. */
        public void setValues(Entity entity) {
            super.setValues(entity);
            entityLocation = null;
            notation = null;
        } // setValues(Entity)

        /** Sets the values of the entity. */
        public void setValues(ExternalEntity entity) {
            super.setValues(entity);
            entityLocation = entity.entityLocation;
            notation = entity.notation;
        } // setValues(ExternalEntity)

    } // class ExternalEntity

    /**
     * Entity state.
     *
     * @author Andy Clark, IBM
     */
    public class ScannedEntity
        extends Entity {

        //
        // Data
        //

        // i/o

        /** Input stream. */
        public InputStream stream;

        /** Reader. */
        public Reader reader;

        // locator information

        /** entity location information */
        public XMLResourceIdentifier entityLocation;

        /** Line number. */
        public int lineNumber = 1;

        /** Column number. */
        public int columnNumber = 1;

        // encoding

        /** Auto-detected encoding. */
        public String encoding;

        /** Encoding has been set externally for eg: using DOMInput*/
        boolean declaredEncoding = false;
                
        // status

        /** True if in a literal.  */
        public boolean literal;

        // whether this is an external or internal scanned entity
        public boolean isExternal;

        // buffer

        /** Character buffer. */
        public char[] ch = null;

        /** Position in character buffer. */
        public int position;

        /** Count of characters in buffer. */
        public int count;

        // to allow the reader/inputStream to behave efficiently:
        public boolean mayReadChunks;

        //
        // Constructors
        //

        /** Constructs a scanned entity. */
        public ScannedEntity(String name,
                             XMLResourceIdentifier entityLocation,
                             InputStream stream, Reader reader,
                             String encoding, boolean literal, boolean mayReadChunks, boolean isExternal) {
            super(name,XMLEntityManager.this.fInExternalSubset);
            this.entityLocation = entityLocation;
            this.stream = stream;
            this.reader = reader;
            this.encoding = encoding;
            this.literal = literal;
            this.mayReadChunks = mayReadChunks;
            this.isExternal = isExternal;
            this.ch = new char[isExternal ? fBufferSize : DEFAULT_INTERNAL_BUFFER_SIZE];
        } // <init>(StringXMLResourceIdentifier,InputStream,Reader,String,boolean, boolean)

        //
        // Entity methods
        //

        /** Returns true if this is an external entity. */
        public final boolean isExternal() {
            return isExternal;
        } // isExternal():boolean

        /** Returns true if this is an unparsed entity. */
        public final boolean isUnparsed() {
            return false;
        } // isUnparsed():boolean

        public void setReader(InputStream stream, String encoding, Boolean isBigEndian) throws IOException {
            reader = createReader(stream, encoding, isBigEndian);
        }

        // return the expanded system ID of the
        // first external entity on the stack, null
        // otherwise.
        public String getExpandedSystemId() {

            // search for the first external entity on the stack
            int size = fEntityStack.size();
            for (int i = size - 1; i >= 0 ; i--) {
               ScannedEntity externalEntity =
                    (ScannedEntity)fEntityStack.elementAt(i);

                if (externalEntity.entityLocation != null &&
                        externalEntity.entityLocation.getExpandedSystemId() != null) {
                    return externalEntity.entityLocation.getExpandedSystemId();
                }
            }
            return null;
        }

        // return literal systemId of
        // nearest external entity
        public String getLiteralSystemId() {
            // search for the first external entity on the stack
            int size = fEntityStack.size();
            for (int i = size - 1; i >= 0 ; i--) {
               ScannedEntity externalEntity =
                    (ScannedEntity)fEntityStack.elementAt(i);

                if (externalEntity.entityLocation != null &&
                        externalEntity.entityLocation.getLiteralSystemId() != null) {
                    return externalEntity.entityLocation.getLiteralSystemId();
                }
            }
            return null;
        }

        // return line number of position in most
        // recent external entity
        public int getLineNumber() {
            // search for the first external entity on the stack
            int size = fEntityStack.size();
            for (int i=size-1; i>0 ; i--) {
               ScannedEntity firstExternalEntity = (ScannedEntity)fEntityStack.elementAt(i);
                if (firstExternalEntity.isExternal()) {
                    return firstExternalEntity.lineNumber;
                }
            }
            return -1;
        }

        // return column number of position in most
        // recent external entity
        public int getColumnNumber() {
            // search for the first external entity on the stack
            int size = fEntityStack.size();
            for (int i=size-1; i>0 ; i--) {
               ScannedEntity firstExternalEntity = (ScannedEntity)fEntityStack.elementAt(i);
                if (firstExternalEntity.isExternal()) {
                    return firstExternalEntity.columnNumber;
                }
            }
            return -1;
        }

        // return encoding of most recent external entity
        public String getEncoding() {
            // search for the first external entity on the stack
            int size = fEntityStack.size();
            for (int i=size-1; i>0 ; i--) {
               ScannedEntity firstExternalEntity = (ScannedEntity)fEntityStack.elementAt(i);
                if (firstExternalEntity.isExternal()) {
                    return firstExternalEntity.encoding;
                }
            }
            return null;
        }

        //
        // Object methods
        //

        /** Returns a string representation of this object. */
        public String toString() {

            StringBuffer str = new StringBuffer();
            str.append("name=\""+name+'"');
            str.append(",ch=");
            str.append(ch);
            str.append(",position="+position);
            str.append(",count="+count);
            return str.toString();

        } // toString():String
       
        public boolean isDeclaredEncoding() {
            return declaredEncoding;
        }
       
        public void setDeclaredEncoding(boolean value) {
            declaredEncoding = value;
        }

    } // class ScannedEntity

    // This class wraps the byte inputstreams we're presented with.
    // We need it because java.io.InputStreams don't provide
    // functionality to reread processed bytes, and they have a habit
    // of reading more than one character when you call their read()
    // methods.  This means that, once we discover the true (declared)
    // encoding of a document, we can neither backtrack to read the
    // whole doc again nor start reading where we are with a new
    // reader.
    //
    // This class allows rewinding an inputStream by allowing a mark
    // to be set, and the stream reset to that position.  <strong>The
    // class assumes that it needs to read one character per
    // invocation when it's read() method is inovked, but uses the
    // underlying InputStream's read(char[], offset length) method--it
    // won't buffer data read this way!</strong>
    //
    // @author Neil Graham, IBM
    // @author Glenn Marcy, IBM

    protected final class RewindableInputStream extends InputStream {

        private InputStream fInputStream;
        private byte[] fData;
        private int fStartOffset;
        private int fEndOffset;
        private int fOffset;
        private int fLength;
        private int fMark;

        public RewindableInputStream(InputStream is) {
            fData = new byte[DEFAULT_XMLDECL_BUFFER_SIZE];
            fInputStream = is;
            fStartOffset = 0;
            fEndOffset = -1;
            fOffset = 0;
            fLength = 0;
            fMark = 0;
        }

        public void setStartOffset(int offset) {
            fStartOffset = offset;
        }

        public void rewind() {
            fOffset = fStartOffset;
        }

        public int read() throws IOException {
            int b = 0;
            if (fOffset < fLength) {
                return fData[fOffset++] & 0xff;
            }
            if (fOffset == fEndOffset) {
                return -1;
            }
            if (fOffset == fData.length) {
                byte[] newData = new byte[fOffset << 1];
                System.arraycopy(fData, 0, newData, 0, fOffset);
                fData = newData;
            }
            b = fInputStream.read();
            if (b == -1) {
                fEndOffset = fOffset;
                return -1;
            }
            fData[fLength++] = (byte)b;
            fOffset++;
            return b & 0xff;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return -1;
                }
                // better get some more for the voracious reader...
                if(fCurrentEntity.mayReadChunks) {
                    return fInputStream.read(b, off, len);
                }
                int returnedVal = read();
                if(returnedVal == -1) {
                    fEndOffset = fOffset;
                    return -1;
                }
                b[off] = (byte)returnedVal;
                return 1;
            }
            if (len < bytesLeft) {
                if (len <= 0) {
                    return 0;
                }
            }
            else {
                len = bytesLeft;
            }
            if (b != null) {
                System.arraycopy(fData, fOffset, b, off, len);
            }
            fOffset += len;
            return len;
        }

        public long skip(long n)
            throws IOException
        {
            int bytesLeft;
            if (n <= 0) {
                return 0;
            }
            bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return 0;
                }
                return fInputStream.skip(n);
            }
            if (n <= bytesLeft) {
                fOffset += n;
                return n;
            }
            fOffset += bytesLeft;
            if (fOffset == fEndOffset) {
                return bytesLeft;
            }
            n -= bytesLeft;
           /*
            * In a manner of speaking, when this class isn't permitting more
            * than one byte at a time to be read, it is "blocking".  The
            * available() method should indicate how much can be read without
            * blocking, so while we're in this mode, it should only indicate
            * that bytes in its buffer are available; otherwise, the result of
            * available() on the underlying InputStream is appropriate.
            */
            return fInputStream.skip(n) + bytesLeft;
        }

        public int available() throws IOException {
            int bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return -1;
                }
                return fCurrentEntity.mayReadChunks ? fInputStream.available()
                                                    : 0;
            }
            return bytesLeft;
        }

        public void mark(int howMuch) {
            fMark = fOffset;
        }

        public void reset() {
            fOffset = fMark;
        }

        public boolean markSupported() {
            return true;
        }

        public void close() throws IOException {
            if (fInputStream != null) {
                fInputStream.close();
                fInputStream = null;
            }
        }
    } // end of RewindableInputStream class

} // class XMLEntityManager
