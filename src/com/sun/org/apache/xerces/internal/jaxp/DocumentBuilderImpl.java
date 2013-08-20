/*
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc., 
 * http://www.sun.com.  For more information on the Apache Software 
 * Foundation, please see <http://www.apache.org/>.
 */


package com.sun.org.apache.xerces.internal.jaxp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;

import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.JAXPConfiguration;
import com.sun.org.apache.xerces.internal.util.SecurityManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * @author Rajiv Mordani
 * @author Edwin Goei
 * @version $Id: DocumentBuilderImpl.java,v 1.24 2004/02/24 23:15:58 mrglavas Exp $
 */
public class DocumentBuilderImpl extends DocumentBuilder
        implements JAXPConstants
{
    private EntityResolver er = null;
    private ErrorHandler eh = null;
    private final DOMParser domParser;
	private boolean enableSP = true;
    private final Schema grammar;
    
    /**
     * null if the secure processing is disabled.
     * otherwise a valid {@link SecureProcessing} object.
     */
    private final SecurityManager secureProcessing ;
    private final boolean xincludeAware;
 

    protected DocumentBuilderImpl(DocumentBuilderFactory dbf, Hashtable dbfAttrs)
          throws SAXNotRecognizedException, SAXNotSupportedException
      {
        grammar = dbf.getSchema();
    	secureProcessing = new SecurityManager();    
        this.domParser = new DOMParser(new JAXPConfiguration(grammar));
        this.xincludeAware = dbf.isXIncludeAware();
        
        domParser.setFeature(
                Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_AWARE,
                xincludeAware);

        // If validating, provide a default ErrorHandler that prints
        // validation errors with a warning telling the user to set an
        // ErrorHandler
        if (dbf.isValidating()) {
            setErrorHandler(new DefaultValidationErrorHandler());
        }

        domParser.setFeature(Constants.SAX_FEATURE_PREFIX +
                             Constants.VALIDATION_FEATURE, dbf.isValidating());

        // "namespaceAware" == SAX Namespaces feature
        domParser.setFeature(Constants.SAX_FEATURE_PREFIX +
                             Constants.NAMESPACES_FEATURE,
                             dbf.isNamespaceAware());

        // Set various parameters obtained from DocumentBuilderFactory
        domParser.setFeature(Constants.XERCES_FEATURE_PREFIX +
                             Constants.INCLUDE_IGNORABLE_WHITESPACE,
                             !dbf.isIgnoringElementContentWhitespace());
        domParser.setFeature(Constants.XERCES_FEATURE_PREFIX +
                             Constants.CREATE_ENTITY_REF_NODES_FEATURE,
                             !dbf.isExpandEntityReferences());
        domParser.setFeature(Constants.XERCES_FEATURE_PREFIX +
                             Constants.INCLUDE_COMMENTS_FEATURE,
                             !dbf.isIgnoringComments());
        domParser.setFeature(Constants.XERCES_FEATURE_PREFIX +
                             Constants.CREATE_CDATA_NODES_FEATURE,
                             !dbf.isCoalescing());

        setDocumentBuilderFactoryAttributes(dbfAttrs);
		if( enableSP)
			domParser.setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY, secureProcessing);
    }
    
	/**
	  * <p>Reset this <code>DocumentBuilder</code> to its original configuration.</p>
	  * 
	  * <p><code>DocumentBuilder</code> is reset to the same state as when it was created with
	  * {@link DocumentBuilderFactory#newDocumentBuilder()}.
	  * <code>reset()</code> is designed to allow the reuse of existing <code>DocumentBuilder</code>s
	  * thus saving resources associated with the creation of new <code>DocumentBuilder</code>s.</p>
	  * 
	  * <p>The reset <code>DocumentBuilder</code> is not guaranteed to have the same {@link EntityResolver} or {@link ErrorHandler}
	  * <code>Object</code>s, e.g. {@link Object#equals(Object obj)}.  It is guaranteed to have a functionally equal
	  * <code>EntityResolver</code> and <code>ErrorHandler</code>.</p>
	  * 
	  * @since 1.5
	  */

    public void reset(){
        if(domParser != null){
            try{
                //we dont need to worry about any properties being set on this object because 
                //DocumentBuilder doesn't provide any way to set the properties
                //once it is created.
                domParser.reset();
            }
            //xxx: underlying implementation reset throws XNIException what should we do in this case ?
            //other question is why underlying implementation should throw an exception is it because 
            //of properties being set.. if there was any propery that is not being supported
            //exception would have been thrown when setting it on the underlying implementation.
            catch(XNIException ex){
                //coninue.
            }
        }
    }
    
    /**
     * Set any DocumentBuilderFactory attributes of our underlying DOMParser
     *
     * Note: code does not handle possible conflicts between DOMParser
     * attribute names and JAXP specific attribute names,
     * eg. DocumentBuilderFactory.setValidating()
     */
    private void setDocumentBuilderFactoryAttributes(Hashtable dbfAttrs)
        throws SAXNotSupportedException, SAXNotRecognizedException
    {
        if (dbfAttrs == null) {
            // Nothing to do
            return;
        }

        // TODO: reroute those properties to use new JAXP1.3 API. -KK
        
        for (Enumeration e = dbfAttrs.keys(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            Object val = dbfAttrs.get(name);
            if (val instanceof Boolean) {
                // Assume feature
				if (Constants.FEATURE_SECURE_PROCESSING.equals(name)){
					enableSP = ((Boolean)val).booleanValue();
				}else
	                domParser.setFeature(name, ((Boolean)val).booleanValue());
            } else {
                // Assume property
                if (JAXP_SCHEMA_LANGUAGE.equals(name)) {
                    // JAXP 1.2 support
                    //None of the properties will take effect till the setValidating(true) has been called                                        
                    if ( W3C_XML_SCHEMA.equals(val) ) {
                        if( isValidating() ) {
                            domParser.setFeature(
                                Constants.XERCES_FEATURE_PREFIX +
                                Constants.SCHEMA_VALIDATION_FEATURE, true);
                            //also set the schema full checking to true.
                            domParser.setFeature(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_FULL_CHECKING,
                                     true);
                            // this should allow us not to emit DTD errors, as expected by the 
                            // spec when schema validation is enabled
                            domParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                        }
                    }
        		} else if(JAXP_SCHEMA_SOURCE.equals(name)){
               		if( isValidating() ) {
						String value=(String)dbfAttrs.get(JAXP_SCHEMA_LANGUAGE);
						if(value !=null && W3C_XML_SCHEMA.equals(value)){
            				domParser.setProperty(name, val);
						}else{
                            throw new IllegalArgumentException(
                                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, 
                                "jaxp-order-not-supported",
                                new Object[] {JAXP_SCHEMA_LANGUAGE, JAXP_SCHEMA_SOURCE}));
						}
					}
            	} 
				/*else if(name.equals(Constants.ENTITY_EXPANSION_LIMIT)){
					String elimit = (String)value;
					if(elimit != null && elimit != ""){
						int val = Integer.parseInt(elimit);
						secureProcessing.setEntityExpansionLimit(val);
					}
				}else if(name.equals(Constants.MAX_OCCUR_LIMIT)){
					String mlimit = (String)value;
					if(mlimit != null && mlimit != ""){
						int val = Integer.parseInt(mlimit);
						secureProcessing.setMaxOccurNodeLimit(val);
					}
        		}*/ else {
                    // Let Xerces code handle the property
                    domParser.setProperty(name, val);
				}
			}
		}
	}

    /**
     * Non-preferred: use the getDOMImplementation() method instead of this
     * one to get a DOM Level 2 DOMImplementation object and then use DOM
     * Level 2 methods to create a DOM Document object.
     */
    public Document newDocument() {
        return new com.sun.org.apache.xerces.internal.dom.DocumentImpl();
    }

    public DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }

    public Document parse(InputSource is) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException(
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, 
                "jaxp-null-input-source", null));
        }

        if (er != null) {
            domParser.setEntityResolver(er);
        }

        if (eh != null) {
            domParser.setErrorHandler(eh);      
        }

        domParser.parse(is);
        return domParser.getDocument();
    }

    public boolean isNamespaceAware() {
        try {
            return domParser.getFeature(Constants.SAX_FEATURE_PREFIX +
                                        Constants.NAMESPACES_FEATURE);
        } catch (SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }

    public boolean isValidating() {
        try {
            return domParser.getFeature(Constants.SAX_FEATURE_PREFIX +
                                        Constants.VALIDATION_FEATURE);
        } catch (SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }

    public void setEntityResolver(org.xml.sax.EntityResolver er) {
        this.er = er;
    }

    public void setErrorHandler(org.xml.sax.ErrorHandler eh) {
        // If app passes in a ErrorHandler of null, then ignore all errors
        // and warnings
        this.eh = (eh == null) ? new DefaultHandler() : eh;
    }

    /** <p>Get a reference to the the <code>GrammarCache</code> being used by
     * the XML processor.</p>
     *
     * <p>If no cache is being used, <code>null</code> is returned.</p>
     *
     * @return <code>GrammarCache</code> being used or <code>null</code>
     *  if none in use
     */
    
    public Schema getSchema(){
        return grammar;
    }
    
    public boolean isXIncludeAware() {
         return xincludeAware;
    }

    final DOMParser getDOMParser() {
          return domParser;
    }
    
}
