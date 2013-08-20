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
package com.sun.org.apache.xerces.internal.jaxp.validation.xs;

import java.io.IOException;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.jaxp.validation.ReadonlyGrammarPool;
import com.sun.org.apache.xerces.internal.jaxp.validation.Util;
import com.sun.org.apache.xerces.internal.jaxp.validation.XercesConstants;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.org.apache.xerces.internal.util.SecurityManager;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * {@link SchemaFactory} for XML Schema.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SchemaFactoryImpl extends SchemaFactory {
    
    private final XMLSchemaLoader loader = new XMLSchemaLoader();
    private static XSMessageFormatter messageFormatter = new XSMessageFormatter();
    /**
     * User-specified ErrorHandler. can be null.
     */
    private ErrorHandler errorHandler;
    
    private LSResourceResolver resourceResolver;
    
    private SAXParseException lastException;
    
    private final SecurityManager secureProcessing ;
    
    private boolean enableSP;
    
    public SchemaFactoryImpl() {
        secureProcessing = new SecurityManager();
        // intercept error report and remember the last thrown exception.
        loader.setErrorHandler(new ErrorHandlerWrapper(new ErrorHandler() {
            public void warning(SAXParseException exception) throws SAXException {
                if( errorHandler!=null )    errorHandler.warning(exception);
            }
            
            public void error(SAXParseException exception) throws SAXException {
                lastException = exception;
                if( errorHandler!=null )    errorHandler.error(exception);
                else    throw exception;
            }
            
            public void fatalError(SAXParseException exception) throws SAXException {
                lastException = exception;
                if( errorHandler!=null )    errorHandler.fatalError(exception);
                else    throw exception;
            }
        }));
    }
    
    
    /**
     * <p>Is specified schema supported by this <code>SchemaFactory</code>?</p>
     *
     * @param schemaLanguage Specifies the schema language which the returned <code>SchemaFactory</code> will understand.
     *    <code>schemaLanguage</code> must specify a <a href="#schemaLanguage">valid</a> schema language.
     *
     * @return <code>true</code> if <code>SchemaFactory</code> supports <code>schemaLanguage</code>, else <code>false</code>.
     *
     * @throws NullPointerException If <code>schemaLanguage</code> is <code>null</code>.
     * @throws IllegalArgumentException If <code>schemaLanguage.length() == 0</code>
     *   or <code>schemaLanguage</code> does not specify a <a href="#schemaLanguage">valid</a> schema language.
     */
    public boolean isSchemaLanguageSupported(String schemaLanguage) {
        
        if (schemaLanguage == null) {
            throw new NullPointerException(
            messageFormatter.formatMessage(Locale.getDefault(),
            "SchemaLanguageSupportedErrorWhenNull",
            new Object [] {this.getClass().getName()}));
        }
        
        if (schemaLanguage.length() == 0) {
            throw new IllegalArgumentException(
            messageFormatter.formatMessage(Locale.getDefault(),
            "SchemaLanguageSupportedErrorWhenLength",
            new Object [] {this.getClass().getName()}));
        }
        
        // understand W3C Schema and RELAX NG
        if (schemaLanguage.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        || schemaLanguage.equals(XMLConstants.RELAXNG_NS_URI)) {
            return true;
        }
        
        // don't know how to validate anything else
        return false;
    }
    
    public LSResourceResolver getResourceResolver() {
        return resourceResolver;
    }
    
    public void setResourceResolver(LSResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        loader.setEntityResolver(new DOMEntityResolverWrapper(resourceResolver));
    }
    
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
    
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    
    
    public Schema newSchema( Source[] schemas ) throws SAXException {
        
        lastException = null;
        
        // this will let the loader store parsed Grammars into the pool.
        XMLGrammarPool pool = new XMLGrammarPoolImpl();
        loader.setProperty(XercesConstants.XMLGRAMMAR_POOL,pool);
        loader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING,true);
        if(enableSP)
            loader.setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY,secureProcessing);
        else
            loader.setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY,null);
        
        for( int i=0; i<schemas.length; i++ ) {
            try {
                loader.loadGrammar(schemas[i]);
            } catch (XNIException e) {
                // this should have been reported to users already.
                throw Util.toSAXException(e);
            } catch (IOException e) {
                // this hasn't been reported, so do so now.
                SAXParseException se = new SAXParseException(e.getMessage(),null,e);
                errorHandler.error(se);
                throw se; // and we must throw it.
            }
        }
        
        // if any error had been reported, throw it.
        if( lastException!=null )
            throw lastException;
        
        // make sure no further grammars are added by making it read-only.
        return new SchemaImpl(new ReadonlyGrammarPool(pool),true);
    }
    
    public Schema newSchema() throws SAXException {
        // use a pool that uses the system id as the equality source.
        return new SchemaImpl(new XMLGrammarPoolImpl() {
            public boolean equals(XMLGrammarDescription desc1, XMLGrammarDescription desc2) {
                String sid1 = desc1.getExpandedSystemId();
                String sid2 = desc2.getExpandedSystemId();
                if( sid1!=null && sid2!=null )
                    return sid1.equals(sid2);
                if( sid1==null && sid2==null )
                    return true;
                return false;
            }
            public int hashCode(XMLGrammarDescription desc) {
                String s = desc.getExpandedSystemId();
                if(s!=null)     return s.hashCode();
                return 0;
            }
        }, false);
    }
    
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(name==null) throw new NullPointerException(SAXMessageFormatter.formatMessage(Locale.getDefault(),
        "nullparameter",new Object[] {"setFeature(String,boolean)"}));
        if(name.equals(Constants.FEATURE_SECURE_PROCESSING)){
            enableSP = value;
        }else throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(Locale.getDefault(),
        "feature-not-supported", new Object [] {name}));
        
    }
    
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(name==null) throw new NullPointerException(SAXMessageFormatter.formatMessage(Locale.getDefault(),
        "nullparameter",new Object[] {"getFeature(String)"}));
        if(name.equals(Constants.FEATURE_SECURE_PROCESSING))
            return enableSP;
        else throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(Locale.getDefault(),
        "feature-not-supported", new Object [] {name}));
    }
}
