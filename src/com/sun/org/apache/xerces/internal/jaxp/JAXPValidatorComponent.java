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
package com.sun.org.apache.xerces.internal.jaxp;

import java.io.IOException;

import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.DraconianErrorHandler;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.TeeXMLDocumentFilterImpl;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Runs events through a {@link javax.xml.validation.ValidatorHandler}
 * and performs validation/infoset-augmentation by an external validator.
 * 
 * <p>
 * This component sets up the pipeline as follows:
 *  
 * <!-- this picture may look teribble on your IDE but it is correct. -->
 * <pre>
 *             __                                           __
 *            /  |==> XNI2SAX --> Validator --> SAX2XNI ==>|  
 *           /   |                                         |   
 *       ==>| Tee|                                         | next
 *           \   |                                         |  component
 *            \  |============other XNI events============>|  
 *             ~~                                           ~~
 * </pre>
 * <p>
 * only those events that need to go through Validator will go the 1st route,
 * and other events go the 2nd direct route.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JAXPValidatorComponent extends TeeXMLDocumentFilterImpl implements XMLComponent {
    
    // pipeline parts
    private final ValidatorHandler validator;
    private final XNI2SAX xni2sax = new XNI2SAX();
    private final SAX2XNI sax2xni = new SAX2XNI();
    
    // never be null
    private final TypeInfoProvider typeInfoProvider;
    
    /**
     * Used to store the {@link Augmentations} associated with the
     * current event, so that we can pick it up again
     * when the event is forwarded by the {@link ValidatorHandler}.
     * 
     * UGLY HACK.
     */
    private Augmentations fCurrentAug;
    
    /**
     * {@link XMLAttributes} version of {@link #fCurrentAug}.
     */
    private XMLAttributes fCurrentAttributes;
    
    // components obtained from a manager / property
    
    private SymbolTable fSymbolTable;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityResolver fEntityResolver;
    
    /**
     * @param validatorHandler
     *      may not be null.
     */
    public JAXPValidatorComponent( ValidatorHandler validatorHandler ) {
        this.validator = validatorHandler;
        TypeInfoProvider tip = validatorHandler.getTypeInfoProvider();;
        if(tip==null)   tip = noInfoProvider;
        this.typeInfoProvider = tip;
        
        // configure wiring between internal components.
        xni2sax.setContentHandler(validator);
        validator.setContentHandler(sax2xni);
        this.setSide(xni2sax);

        // configure validator with proper EntityResolver/ErrorHandler.
        validator.setErrorHandler(new ErrorHandlerProxy() {
            protected XMLErrorHandler getErrorHandler() {
                XMLErrorHandler handler = fErrorReporter.getErrorHandler();
                if(handler!=null)   return handler;
                return new ErrorHandlerWrapper(DraconianErrorHandler.theInstance);
            }
        });
        validator.setResourceResolver(new LSResourceResolver() {
            public LSInput resolveResource(String type,String ns, String publicId, String systemId, String baseUri) {
                if(fEntityResolver==null)   return null;
                try {
                    XMLInputSource is = fEntityResolver.resolveEntity(
                        new XMLResourceIdentifierImpl(publicId,systemId,baseUri,systemId));
                    if(is==null)    return null;
                        
                    LSInput di = new DOMInputImpl();
                    di.setBaseURI(is.getBaseSystemId());
                    di.setByteStream(is.getByteStream());
                    di.setCharacterStream(is.getCharacterStream());
                    di.setEncoding(is.getEncoding());
                    di.setPublicId(is.getPublicId());
                    di.setSystemId(is.getSystemId());
                        
                    return di;
                } catch( IOException e ) {
                    // erors thrown by the callback is not supposed to be
                    // reported to users.
                    throw new XNIException(e);
                }
            }
        });
    }
    

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        fCurrentAttributes = attributes;
        fCurrentAug = augs;
        xni2sax.startElement(element,attributes,null);
        fCurrentAttributes = null; // mostly to make it easy to find any bug.
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        fCurrentAug = augs;
        xni2sax.endElement(element,null);
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        startElement(element,attributes,augs);
        endElement(element,augs);
    }

    
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        // since a validator may change the contents,
        // let this one go through a validator
        fCurrentAug = augs;
        xni2sax.characters(text,null);
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        // since a validator may change the contents,
        // let this one go through a validator
        fCurrentAug = augs;
        xni2sax.ignorableWhitespace(text,null);
    }

    public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
        // obtain references from the manager
        fSymbolTable = (SymbolTable)componentManager.getProperty(
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY);
        
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY);
        
    }

    
    /**
     * 
     * Uses {@link DefaultHandler} as a default implementation of
     * {@link ContentHandler}.
     * 
     * <p>
     * We only forward certain events from a {@link ValidatorHandler}.
     * Other events should go "the 2nd direct route".
     */
    private final class SAX2XNI extends DefaultHandler {
        
        /**
         * {@link Augmentations} to send along with events.
         * We reuse one object for efficiency.
         */
        private final Augmentations fAugmentations = new AugmentationsImpl();
        
        /**
         * {@link QName} to send along events.
         * we reuse one QName for efficiency.
         */
        private final QName fQName = new QName(); 
        
        public void characters(char[] ch, int start, int len) throws SAXException {
            try {
                handler().characters(new XMLString(ch,start,len),aug());
            } catch( XNIException e ) {
                throw toSAXException(e);
            }
        }

        public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException {
            try {
                handler().ignorableWhitespace(new XMLString(ch,start,len),aug());
            } catch( XNIException e ) {
                throw toSAXException(e);
            }
        }

        public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
            try {
                updateAttributes(atts);
                
                handler().startElement(toQName(uri,localName,qname), fCurrentAttributes, elementAug());
            } catch( XNIException e ) {
                throw toSAXException(e);
            }
        }

        public void endElement(String uri, String localName, String qname) throws SAXException {
            try {
                handler().endElement(toQName(uri,localName,qname),aug());
            } catch( XNIException e ) {
                throw toSAXException(e);
            }
        }
        
        private Augmentations elementAug() {
            Augmentations aug = aug();
            aug.putItem(Constants.TYPEINFO,typeInfoProvider.getElementTypeInfo());
            return aug;
        }

        
        /**
         * Gets the {@link Augmentations} that should be associated with
         * the current event.
         */
        private Augmentations aug() {
            if( fCurrentAug!=null ) {
                Augmentations r = fCurrentAug;
                fCurrentAug = null; // we "consumed" this augmentation.
                return r;
            }
            fAugmentations.removeAllItems();
            return fAugmentations;
        }
        
        /**
         * Get the handler to which we should send events.
         */
        private XMLDocumentHandler handler() {
            return JAXPValidatorComponent.this.getDocumentHandler();
        }
        
        /**
         * Converts the {@link XNIException} received from a downstream
         * component to a {@link SAXException}.
         */
        private SAXException toSAXException( XNIException xe ) {
            Exception e = xe.getException();
            if( e==null )   e = xe;
            if( e instanceof SAXException )  return (SAXException)e;
            return new SAXException(e);
        }
        
        /**
         * Creates a proper {@link QName} object from 3 parts.
         * <p>
         * This method does the symbolization.
         */
        private QName toQName( String uri, String localName, String qname ) {
            String prefix = null;
            int idx = qname.indexOf(':');
            if( idx>0 )
                prefix = symbolize(qname.substring(0,idx));
            
            localName = symbolize(localName);
            qname = symbolize(qname);
            uri = symbolize(uri);

            // notify handlers
            fQName.setValues(prefix, localName, qname, uri);
            return fQName;
        }
    }
    
    
    /**
     * Compares the given {@link Attributes} with {@link #fCurrentAttributes}
     * and update the latter accordingly.
     */
    private void updateAttributes( Attributes atts ) {
        int len = atts.getLength();
        for( int i=0; i<len; i++ ) {
            String aqn = atts.getQName(i);
            int j = fCurrentAttributes.getIndex(aqn);
            String av = atts.getValue(i);
            if(j==-1) {
                // newly added attribute. add to the current attribute list.
                
                String prefix;
                int idx = aqn.indexOf(':');
                if( idx<0 ) {
                    prefix = null;
                } else {
                    prefix = symbolize(aqn.substring(0,idx));
                }
                
                j = fCurrentAttributes.addAttribute(
                    new QName(
                        prefix,
                        symbolize(atts.getLocalName(i)),
                        symbolize(aqn),
                        symbolize(atts.getURI(i))),
                    atts.getType(i),av);
            } else {
                // the attribute is present.
                if( !av.equals(fCurrentAttributes.getValue(j)) ) {
                    // but the value was changed.
                    fCurrentAttributes.setValue(j,av);
                }
            }
            
            Augmentations augs = fCurrentAttributes.getAugmentations(j);
            augs.putItem( Constants.TYPEINFO,
                typeInfoProvider.getAttributeTypeInfo(i) );
            augs.putItem( Constants.ID_ATTRIBUTE,
                typeInfoProvider.isIdAttribute(i)?Boolean.TRUE:Boolean.FALSE );
        }
        

// spec says attributes won't be removed.
//        
//        // we might remove attributes as we go through,
//        // so iterate in the reverse order.
//        for( int j=fCurrentAttributes.getLength()-1; j>=0; j-- ) {
//            String aqn = fCurrentAttributes.getQName(j);
//            int i = atts.getIndex(aqn);
//            if(i==-1) 
//                // this attribute is removed.
//                fCurrentAttributes.removeAttributeAt(j);
//        }
    }
    
    private String symbolize( String s ) {
        return fSymbolTable.addSymbol(s);
    }

    
    /**
     * {@link TypeInfoProvider} that returns no info.
     */
    private static final TypeInfoProvider noInfoProvider = new TypeInfoProvider() {
        public TypeInfo getElementTypeInfo() {
            return null;
        }
        public TypeInfo getAttributeTypeInfo(int index) {
            return null;
        }
        public TypeInfo getAttributeTypeInfo(String attributeQName) {
            return null;
        }
        public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
            return null;
        }
        public boolean isIdAttribute(int index) {
            return false;
        }
        public boolean isSpecified(int index) {
            return false;
        }
    };
    
//
//
// XMLComponent implementation.
//
//

// no property/feature supported
    public String[] getRecognizedFeatures() {
        return null;
    }

    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    }

    public String[] getRecognizedProperties() {
        return new String[]{Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY};
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        if(propertyId.equals(Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY)) {
            fEntityResolver = (XMLEntityResolver)value;
        }
    }
    
    public Boolean getFeatureDefault(String featureId) {
        return null;
    }

    public Object getPropertyDefault(String propertyId) {
        return null;
    }

}
