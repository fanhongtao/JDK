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
package com.sun.org.apache.xerces.internal.jaxp.validation;

import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.DraconianErrorHandler;
import com.sun.org.apache.xerces.internal.util.LocatorWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * {@link ValidatorHandler} implementation that wraps
 * {@link InsulatedValidatorComponent}.
 *
 * <p>
 * This class implements all the SAX {@link org.xml.sax.ContentHandler}
 * methods and turn SAX events into XNI events.
 *
 * <p>
 * This class also implements {@link XMLComponentManager}
 * to host a validator.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class ValidatorHandlerImpl extends ValidatorHandler {
    // TODO: reuse SAX2XNI
    
    /**
     * The actual validator.
     */
    private final InsulatedValidatorComponent validator;
    
    /**
     * <code>validator.getValidator()</code>.
     */
    private final XMLDocumentFilter validatorFilter;
    
    /**
     * Used to adopt the output from a validtor to the input of
     * the user specified {@link ContentHandler}.
     */
    private final XNI2SAXEx xni2sax = new XNI2SAXEx();
    
    
    // XMLSchemaValidator needs various helper components to work.
    private final SymbolTable symbolTable = new SymbolTable();
    private final NamespaceSupport nsContext = new NamespaceSupport();
    private final ValidationManager validationManager = new ValidationManager();
    private final XMLEntityManager entityManager = new XMLEntityManager();
    
    /** error reporter is used to format error messages. */
    private final XMLErrorReporter errorReporter = new XMLErrorReporter();    
    
    /** User-specified error handler. Maybe null. */
    private ErrorHandler errorHandler;
    
    /** This flag is set to true while we are processing the startElement event. */
    private boolean inStartElement;
    
    /** The value of the <tt>http://xml.org/sax/features/namespace-prefixes</tt> feature. */
    private boolean namespacePrefixesFeature = false;
    
    //private Hashtable fProperties;
    /**
     * Used by {@link XMLDTDValidator} to report errors.
     */
    private final ErrorHandlerAdaptor xercesErrorHandler = new ErrorHandlerAdaptor() {
        protected ErrorHandler getErrorHandler() {
            if(errorHandler==null )
                return DraconianErrorHandler.theInstance;
            else
                return errorHandler;
        }
    };
    
    /** User-specified entity resolver. Maybe null. */
    private LSResourceResolver resourceResolver;
    
    
    
    
    ValidatorHandlerImpl( InsulatedValidatorComponent validator ) {
        this.validator = validator;
        this.validatorFilter = validator.getValidator();

        // format error message with Schema aware formatter
        errorReporter.putMessageFormatter(
                XSMessageFormatter.SCHEMA_DOMAIN,
                new XSMessageFormatter());
    }
    
    /**
     * Obtains the current augmentation.
     * <p>
     * used for {@link javax.xml.validation.TypeInfoProvider}.
     *
     * @return
     *      may return null.
     */
    private final Augmentations getCurrentAugmentation() {
        return xni2sax.getCurrentAugmentation();
    }
    
    /**
     * Obtains the current attributes.
     *
     * @throws IllegalStateException
     */
    private final XMLAttributes getCurrentAttributes() {
        return xni2sax.getCurrentAttributes();
    }
    
    
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals("http://xml.org/sax/features/namespace-prefixes") )
            return namespacePrefixesFeature;
        return super.getFeature(name);
    }
    
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals("http://xml.org/sax/features/namespace-prefixes") ) {
            namespacePrefixesFeature = value;
            return;
        }
        super.setFeature(name, value);
    }
    
    
    
    //
    //
    //  ValidaorHandler implementation
    //
    //
    
    public boolean isValidSoFar() {
        return !xercesErrorHandler.hadError();
    }
    
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
    
    public void setResourceResolver(LSResourceResolver entityResolver) {
        this.resourceResolver = entityResolver;
    }
    
    public LSResourceResolver getResourceResolver() {
        return resourceResolver;
    }
    
    public final void setContentHandler(ContentHandler result) {
        xni2sax.setContentHandler(result);
        if(result==null)    validatorFilter.setDocumentHandler(null);
        else                validatorFilter.setDocumentHandler(xni2sax);
    }
    
    public final ContentHandler getContentHandler() {
        return xni2sax.getContentHandler();
    }
    
    
    
    
    //
    //
    // XMLComponentManager implementation
    //
    //
    private final XMLComponentManager manager = new XMLComponentManager() {
        public Object getProperty( String propName ) {
            if( propName.equals(XercesConstants.SYMBOL_TABLE) )
                return symbolTable;
            if( propName.equals(XercesConstants.VALIDATION_MANAGER) )
                return validationManager;
            if( propName.equals(XercesConstants.ERROR_REPORTER) )
                return errorReporter;
            if( propName.equals(XercesConstants.ERROR_HANDLER) )
                return xercesErrorHandler;
            if( propName.equals(XercesConstants.ENTITY_MANAGER) )
                return entityManager;
            if( propName.equals(XercesConstants.ENTITY_RESOLVER) )
                return entityManager;
            
            throw new XMLConfigurationException(
            XMLConfigurationException.NOT_RECOGNIZED, propName );
        }
        public boolean getFeature( String propName ) {
            //       if( propName.equals(XercesConstants.SCHEMA_VALIDATION) )
            //           // this flag will turn on the validation
            //           return true;
            if( propName.equals(XercesConstants.VALIDATION) )
                return true;//TODO:: Configure
            
            throw new XMLConfigurationException(
            XMLConfigurationException.NOT_RECOGNIZED, propName );
        }
    };
    
    //
    //
    // ContentHandler implementation
    //
    //
    public void startDocument() throws SAXException {
        try {
            resetComponents();
            
            XMLLocator xmlLocator = (locator==null)?null:new LocatorWrapper(locator);
            
            // set the locator to the error reporter
            errorReporter.setDocumentLocator(xmlLocator);
            
            validatorFilter.startDocument(
            xmlLocator,
            null,
            nsContext,
            null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        }
    }
    
    /**
     * Resets the components we use internally.
     */
    private void resetComponents() {
        // reset the error flag when we start a new validation.
        xercesErrorHandler.reset();
        nsContext.reset();
        errorReporter.reset(manager);
        validator.reset(manager);
    }
    
    public void endDocument() throws SAXException {
        try {
            validatorFilter.endDocument(null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        }
    }
    
    public void startElement( String uri, String local, String qname, Attributes att ) throws SAXException {
        try {
            inStartElement = true;
            validatorFilter.startElement(createQName(uri,local,qname),createAttributes(att),null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        } finally {
            inStartElement = false;
        }
    }
    
    public void endElement( String uri, String local, String qname ) throws SAXException {
        try {
            validatorFilter.endElement(createQName(uri,local,qname),null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        }
    }
    
    public void characters( char[] buf, int offset, int len ) throws SAXException {
        try {
            validatorFilter.characters(new XMLString(buf,offset,len),null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        }
    }
    
    public void ignorableWhitespace( char[] buf, int offset, int len ) throws SAXException {
        try {
            validatorFilter.ignorableWhitespace(new XMLString(buf,offset,len),null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        }
    }
    
    public void startPrefixMapping( String prefix, String uri ) {
        nsContext.pushContext();
        nsContext.declarePrefix(prefix,uri);
    }
    
    public void endPrefixMapping( String prefix ) {
        nsContext.popContext();
    }
    
    public void processingInstruction( String target, String data ) throws SAXException {
        try {
            validatorFilter.processingInstruction(
            symbolize(target),createXMLString(data),null);
        } catch( WrappedSAXException e ) {
            throw e.exception;
        }
    }
    
    public void skippedEntity( String name ) {
        // there seems to be no corresponding method on XMLDocumentFilter.
        // just pass it down to the output, if any.
        ContentHandler handler = getContentHandler();
        if( handler!=null )
            skippedEntity(name);
    }
    
    private Locator locator;
    public void setDocumentLocator( Locator _loc ) {
        this.locator = _loc;
    }
    
    
    public TypeInfoProvider getTypeInfoProvider() {
        return typeInfoProvider;
    }
    
    
    /**
     * {@link TypeInfoProvider} implementation.
     *
     * REVISIT: I'm not sure if this code should belong here.
     */
    private final TypeInfoProvider typeInfoProvider = new TypeInfoProvider() {
        /**
         * Throws a {@link IllegalStateException} if we are not in
         * the startElement callback. the JAXP API requires this
         * for most of the public methods.
         */
        private void checkState() {
            if( !inStartElement )
                throw new IllegalStateException();
        }
        
        public TypeInfo getAttributeTypeInfo(int index) {
            checkState();
            return getAttributeType(index);
        }
        
        private XSTypeDefinition getAttributeType( int index ) {
            checkState();
            XMLAttributes atts = getCurrentAttributes();
            if( index<0 || atts.getLength()<=index )
                throw new IndexOutOfBoundsException(Integer.toString(index));
            Augmentations augs = atts.getAugmentations(index);
            if(augs==null)   return null;
            AttributePSVI psvi = (AttributePSVI)augs.getItem(Constants.ATTRIBUTE_PSVI);
            return getTypeInfoFromPSVI(psvi);
        }
        
        public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
            checkState();
            return getAttributeTypeInfo(getCurrentAttributes().getIndex(attributeUri,attributeLocalName));
        }
        
        public TypeInfo getAttributeTypeInfo(String attributeQName) {
            checkState();
            return getAttributeTypeInfo(getCurrentAttributes().getIndex(attributeQName));
        }
        
        public TypeInfo getElementTypeInfo() {
            checkState();
            Augmentations augs = getCurrentAugmentation();
            if(augs==null)  return null;
            ElementPSVI psvi = (ElementPSVI)augs.getItem(Constants.ELEMENT_PSVI);
            return getTypeInfoFromPSVI(psvi);
        }
        
        private XSTypeDefinition getTypeInfoFromPSVI( ItemPSVI psvi ) {
            if(psvi==null)  return null;
            
            // TODO: make sure if this is correct.
            // TODO: since the number of types in a schema is quite limited,
            // TypeInfoImpl should be pooled. Even better, it should be a part
            // of the element decl.
            if( psvi.getValidity()== ElementPSVI.VALIDITY_VALID ) {
                XSTypeDefinition t = psvi.getMemberTypeDefinition();
                if(t!=null)     return t;
            }
            
            XSTypeDefinition t = psvi.getTypeDefinition();
            if(t!=null)         return t; // TODO: can t be null?
            return null;
        }
        
        public boolean isIdAttribute(int index) {
            checkState();
            XSSimpleType type = (XSSimpleType)getAttributeType(index);
            if(type==null)  return false;
            return type.isIDType();
        }
        
        public boolean isSpecified(int index) {
            checkState();
            return getCurrentAttributes().isSpecified(index);
        }
    };
    
    
    
    //
    //
    // helper methods
    //
    //
    /** Symbolizes the specified string. */
    private String symbolize(String s) {
        if (s == null)
            return null;
        else
            return symbolTable.addSymbol(s);
    }
    
    /** Creates a QName object. */
    private QName createQName(String uri, String local, String raw) {
        
        if( local.length()==0 ) {
            // if naemspace processing is turned off, local could be "".
            // in that case, treat everything to be in the no namespace.
            uri = "";
            local = raw;
        }
        
        int idx = raw.indexOf(':');
        String prefix;
        if (idx < 0)
            prefix = null;
        else
            prefix = raw.substring(0, idx);
        
        if (uri != null && uri.length() == 0)
            uri = null; // XNI uses null whereas SAX uses the empty string
        
        return new QName(symbolize(prefix), symbolize(local), symbolize(raw), symbolize(uri));
    }
    
    /** only one instance of XMLAttributes is used. */
    private final XMLAttributes xa = new XMLAttributesImpl();
    
    /** Creates an XMLAttributes object. */
    private XMLAttributes createAttributes(Attributes att) {
        xa.removeAllAttributes();
        int len = att.getLength();
        for (int i = 0; i < len; i++) {
            int idx = xa.addAttribute(
            createQName(att.getURI(i), att.getLocalName(i), att.getQName(i)),
            att.getType(i),
            att.getValue(i));
            // attributes present in the original SAX event streams
            // are considered as "specified".
            xa.setSpecified(idx,true);
        }
        return xa;
    }
    
    private XMLString createXMLString(String str) {
        // with my patch
        // return new XMLString(str);
        
        // for now
        return new XMLString(str.toCharArray(), 0, str.length());
    }
    
    /**
     * Resets this handler.
     * <p>
     * Meaning resets all the user-specified configurations to the
     * initial state, then also resets all the components.
     */
    public void reset() {
        resetComponents();
        errorHandler = null;
        resourceResolver = null;
    }
}
