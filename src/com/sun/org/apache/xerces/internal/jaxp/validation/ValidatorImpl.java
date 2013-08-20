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

import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * <b>(For Implementors)</b> Default implementation of {@link Validator}.
 *
 * <p>
 * This class is intended to be used in conjunction with
 * {@link AbstractSchemaImpl} to promote consistent
 * behaviors among {@link Schema} implementations.
 *
 * <p>
 * This class wraps a {@link javax.xml.validation.ValidatorHandler}
 * object and implements the {@link Validator} semantics.
 *
 * @author <a href="mailto:Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.5 $, $Date: 2004/07/12 20:38:39 $
 * @since 1.5
 */
class ValidatorImpl extends Validator {
    
    /**
     * The actual validation will be done by this object.
     */
    private final ValidatorHandlerImpl handler;
    
    /**
     * Lazily created identity transformer.
     */
    private Transformer identityTransformer1 = null;
    private TransformerHandler identityTransformer2 = null;
    
    ValidatorImpl( ValidatorHandlerImpl _handler ) {
        this.handler = _handler;
    }
    
    public LSResourceResolver getResourceResolver() {
        return handler.getResourceResolver();
    }
    
    
    public ErrorHandler getErrorHandler() {
        return handler.getErrorHandler();
    }
    
    public void setResourceResolver(LSResourceResolver resolver) {
        handler.setResourceResolver(resolver);
    }
    
    public void setErrorHandler(ErrorHandler errorHandler) {
        handler.setErrorHandler(errorHandler);
    }
    
    public void validate(Source source, Result result) throws SAXException, IOException {
        if( source instanceof DOMSource ) {
            if( result!=null && !(result instanceof DOMResult) )
                throw new IllegalArgumentException(result.getClass().getName());
            process( (DOMSource)source, (DOMResult)result );
            return;
        }
        if( source instanceof SAXSource ) {
            if( result!=null && !(result instanceof SAXResult) )
                throw new IllegalArgumentException(result.getClass().getName());
            process( (SAXSource)source, (SAXResult)result );
            return;
        }
        if( source instanceof StreamSource ) {
            if( result!=null )
                throw new IllegalArgumentException(result.getClass().getName());
            StreamSource ss = (StreamSource)source;
            InputSource is = new InputSource();
            is.setByteStream(ss.getInputStream());
            is.setCharacterStream(ss.getReader());
            is.setPublicId(ss.getPublicId());
            is.setSystemId(ss.getSystemId());
            process( new SAXSource(is), null );
            return;
        }
        throw new IllegalArgumentException(source.getClass().getName());
    }
    
    /**
     * Parses a {@link SAXSource} potentially to a {@link SAXResult}.
     */
    private void process(SAXSource source, SAXResult result) throws IOException, SAXException {
        if( result!=null ) {
            handler.setContentHandler(result.getHandler());
        }
        
        try {
            XMLReader reader = source.getXMLReader();
            if( reader==null ) {
                // create one now
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                try {
                    reader = spf.newSAXParser().getXMLReader();
                } catch( Exception e ) {
                    // this is impossible, but better safe than sorry
                    throw new FactoryConfigurationError(e);
                }
            }
            
            reader.setErrorHandler(errorForwarder);
            reader.setEntityResolver(resolutionForwarder);
            reader.setContentHandler(handler);
            
            InputSource is = source.getInputSource();
            reader.parse(is);
        } finally {
            // release the reference to user's handler ASAP
            handler.setContentHandler(null);
        }
    }
    
    /**
     * Parses a {@link DOMSource} potentially to a {@link DOMResult}.
     */
    private void process( DOMSource source, DOMResult result ) throws SAXException {
        if( identityTransformer1==null ) {
            try {
                SAXTransformerFactory tf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                identityTransformer1 = tf.newTransformer();
                identityTransformer2 = tf.newTransformerHandler();
            } catch (TransformerConfigurationException e) {
                // this is impossible, but again better safe than sorry
                throw new TransformerFactoryConfigurationError(e);
            }
        }

        if( result!=null ) {
            handler.setContentHandler(identityTransformer2);
            identityTransformer2.setResult(result);
        }
        
        try {
            identityTransformer1.transform( source, new SAXResult(handler) );
        } catch (TransformerException e) {
            if( e.getException() instanceof SAXException )
                throw (SAXException)e.getException();
            throw new SAXException(e);
        } finally {
            handler.setContentHandler(null);
        }
    }
    
    /**
     * Forwards the error to the {@link ValidatorHandler}.
     * If the {@link ValidatorHandler} doesn't have its own
     * {@link ErrorHandler}, behave draconian.
     */
    private final ErrorHandler errorForwarder = new ErrorHandler() {
        public void warning(SAXParseException exception) throws SAXException {
            ErrorHandler realHandler = handler.getErrorHandler();
            if( realHandler!=null )
                realHandler.warning(exception);
        }
        
        public void error(SAXParseException exception) throws SAXException {
            ErrorHandler realHandler = handler.getErrorHandler();
            if( realHandler!=null )
                realHandler.error(exception);
            else
                throw exception;
        }
        
        public void fatalError(SAXParseException exception) throws SAXException {
            ErrorHandler realHandler = handler.getErrorHandler();
            if( realHandler!=null )
                realHandler.fatalError(exception);
            else
                throw exception;
        }
    };
    
    /**
     * Forwards the entity resolution to the {@link ValidatorHandler}.
     * If the {@link ValidatorHandler} doesn't have its own
     * {@link DOMResourceResolver}, let the parser do the resolution.
     */
    private final EntityResolver resolutionForwarder = new EntityResolver() {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            LSResourceResolver resolver = handler.getResourceResolver();
            if( resolver==null )    return null;
            
            LSInput di = resolver.resolveResource(null,null,publicId,systemId,null);
            if(di==null)    return null;
            
            InputSource r = new InputSource();
            r.setByteStream(di.getByteStream());
            r.setCharacterStream(di.getCharacterStream());
            r.setEncoding(di.getEncoding());
            r.setPublicId(di.getPublicId());
            r.setSystemId(di.getSystemId());
            return r;
        }
    };
    
    public void reset() {
        handler.reset();
        
        // I don't think this is necessary, but I don't think it hurts either.
        // so reset just for the kick.
        if(identityTransformer1!=null) {
            identityTransformer1.reset();
        }
    }
}
