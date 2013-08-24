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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.parsers.AbstractSAXParser;
import com.sun.org.apache.xerces.internal.parsers.JAXPConfiguration;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import com.sun.org.apache.xerces.internal.util.SecurityManager;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

/**
 * This is the implementation specific class for the
 * <code>javax.xml.parsers.SAXParser</code>.
 * 
 * @author Rajiv Mordani
 * @author Edwin Goei
 * 
 * @version $Id: SAXParserImpl.java,v 1.1.2.2 2007/03/09 16:11:55 spericas Exp $
 */
public class SAXParserImpl extends javax.xml.parsers.SAXParser
    implements JAXPConstants {

        // Should XMLParserConfiguration have reset, ? we have reset at component level
    private final JAXPConfiguration parserConfiguration;
	private final AbstractSAXParser xmlReader;
    private String schemaLanguage = null;     // null means DTD
    private final Schema grammar;
    private final SecurityManager secureProcessing;
    private boolean isXIncludeAware;
   	private boolean enableSecureProcessing = true; 

    //we have to keep the reference to features and SAXParserFactory so that 
    //we can set the SAXParser to the same state when it 
    //was createed from the factory.
    private Hashtable features;
    private SAXParserFactory spf;
    
    private Hashtable parserFeatures = new Hashtable();
    
    public boolean isXIncludeAware() {
        return isXIncludeAware;
    }
    

    /**
     * Create a SAX parser with the associated features
     * @param features Hashtable of SAX features, may be null
     */
    SAXParserImpl(SAXParserFactory spfactory, Hashtable factoryfeatures)
        throws SAXException
    {
        this.spf = spfactory ;
        this.features = factoryfeatures;
        // inherit 
		secureProcessing = new SecurityManager();
        this.grammar = spf.getSchema();        
        parserConfiguration = new JAXPConfiguration(grammar);
        xmlReader = new com.sun.org.apache.xerces.internal.parsers.SAXParser(parserConfiguration);
        
        //initialize the feature as per factory settings..
        init();
    }

    /** Reset this instance back to factory settings */
    void resetSettings() throws SAXNotSupportedException, SAXNotRecognizedException {
        Enumeration keys  = parserFeatures.keys();        
        
        while(keys.hasMoreElements()){
            String propertyId = (String)keys.nextElement();
            Object value = parserFeatures.get(propertyId);
            if(value instanceof Boolean){
                //System.out.println("Remvoing feature = " + propertyId + " with value = " + parserConfiguration.getFeatureDefaultValue(propertyId));
                //this means it is a feature, we have to get default value from the configuration
                xmlReader.setFeature(propertyId, parserConfiguration.getFeatureDefaultValue(propertyId));
            }
            else{//it's a property
                //System.out.println("Remvoing property = " + propertyId);
                //null value should delete the property from underlying implementation.
                xmlReader.setProperty(propertyId, null);
            }
        }
        //clear the hashtable once we have removed all the properties.
        parserFeatures.clear();
        
    }
    
    //initialize the features as per factory settings.
    void init() throws SAXNotSupportedException, SAXNotRecognizedException {
                
        schemaLanguage = null ;
        this.isXIncludeAware = spf.isXIncludeAware();
		if(features != null ){
       		Object tmpValue = features.get(Constants.FEATURE_SECURE_PROCESSING);
			if( tmpValue != null ) 
				enableSecureProcessing = ((Boolean)tmpValue).booleanValue();
		}
		
		if(enableSecureProcessing){
        	try {
            	setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY, secureProcessing);
            } catch (SAXNotRecognizedException sex) {
                sex.printStackTrace();
            } catch (SAXNotSupportedException se) {
                se.printStackTrace();
            }
        }
        
		xmlReader.setFeature(
                Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_AWARE,
                isXIncludeAware);
        
        // If validating, provide a default ErrorHandler that prints
        // validation errors with a warning telling the user to set an
        // ErrorHandler.
        if (spf.isValidating()) {
            xmlReader.setErrorHandler(new DefaultValidationErrorHandler());
        }

        xmlReader.setFeature(Constants.SAX_FEATURE_PREFIX +
                             Constants.VALIDATION_FEATURE, spf.isValidating());

        // JAXP "namespaceAware" == SAX Namespaces feature
        // Note: there is a compatibility problem here with default values:
        // JAXP default is false while SAX 2 default is true!
        xmlReader.setFeature(Constants.SAX_FEATURE_PREFIX +
                             Constants.NAMESPACES_FEATURE,
                             spf.isNamespaceAware());

        // SAX "namespaces" and "namespace-prefixes" features should not
        // both be false.  We make them opposite for backward compatibility
        // since JAXP 1.0 apps may want to receive xmlns* attributes.
        xmlReader.setFeature(Constants.SAX_FEATURE_PREFIX +
                             Constants.NAMESPACE_PREFIXES_FEATURE,
                             !spf.isNamespaceAware());
        setFeatures(features);
        
    }//init()
    
    /**
     * Set any features of our XMLReader based on any features set on the
     * SAXParserFactory.
     *
     * XXX Does not handle possible conflicts between SAX feature names and
     * JAXP specific feature names, eg. SAXParserFactory.isValidating()
     */
    private void setFeatures(Hashtable features)
        throws SAXNotSupportedException, SAXNotRecognizedException
    {
        if (features != null) {
            for (Enumeration e = features.keys(); e.hasMoreElements();) {
                String feature = (String)e.nextElement();
                boolean value = ((Boolean)features.get(feature)).booleanValue();
				if(!feature.equals(Constants.FEATURE_SECURE_PROCESSING))
                	xmlReader.setFeature(feature, value);
            }
        }
    }
    
    /**
     * @deprecated
     */
    public org.xml.sax.Parser getParser() throws SAXException {
        return xmlReader;
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
        if(xmlReader != null){
            try{
                xmlReader.reset();
                //set the object back to its factory settings
                resetSettings();
                
            }
            //xxx: underlying implementation reset throws XNIException what should we do in this case ?
            //if there was any propery that is not being supported
            //exception would have been thrown when setting it on the underlying implementation.
            catch(XNIException ex){
                //coninue.
            }
            catch(SAXException sax){}
        }
    }

    
    /**
     * Returns the XMLReader that is encapsulated by the implementation of
     * this class.
     */
    public XMLReader getXMLReader() {
        return xmlReader;
    }

    public boolean isNamespaceAware() {
        try {
            return xmlReader.getFeature(Constants.SAX_FEATURE_PREFIX +
                                        Constants.NAMESPACES_FEATURE);
        } catch (SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }

    public boolean isValidating() {
        try {
            return xmlReader.getFeature(Constants.SAX_FEATURE_PREFIX +
                                        Constants.VALIDATION_FEATURE);
        } catch (SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }

    /**
     * Sets the particular property in the underlying implementation of 
     * org.xml.sax.XMLReader.
     */
    public void setProperty(String name, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // the spec says if a schema is given via SAXParserFactory
        // the JAXP 1.2 properties shouldn't be allowed. So
        // reject them first.
        if(grammar!=null) {
            if (JAXP_SCHEMA_LANGUAGE.equals(name)
            ||  JAXP_SCHEMA_SOURCE.equals(name)) {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(null, "schema-already-specified", null));
            }
        }
        
        if (JAXP_SCHEMA_LANGUAGE.equals(name)) {
            // JAXP 1.2 support            
            if ( W3C_XML_SCHEMA.equals(value) ) {
                //None of the properties will take effect till the setValidating(true) has been called                                                        
                if( isValidating() ) {
                    schemaLanguage = W3C_XML_SCHEMA;
                    xmlReader.setFeature(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_VALIDATION_FEATURE,
                                     true);
                    //also set the schema full checking to true.
                    xmlReader.setFeature(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_FULL_CHECKING,
                                     true);
                    //add this among the list of parser  features since this is added 
                    //on the parser instance and should be set to default value during 
                    //reset.
                    parserFeatures.put(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_VALIDATION_FEATURE, new Boolean(true));
                    parserFeatures.put(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_FULL_CHECKING, new Boolean(true));
                                     
                    // this will allow the parser not to emit DTD-related
                    // errors, as the spec demands
                    xmlReader.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                    parserFeatures.put(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                    
                }
                else{
                    //System.out.println("Property = " + name + "is not set");
                }
                
            } else if (value == null) {
                schemaLanguage = null;
                xmlReader.setFeature(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_VALIDATION_FEATURE,
                                     false);
                parserFeatures.put(Constants.XERCES_FEATURE_PREFIX +
                                     Constants.SCHEMA_VALIDATION_FEATURE,
                                     new Boolean(false));
                
            } else {
                // REVISIT: It would be nice if we could format this message
                // using a user specified locale as we do in the underlying
                // XMLReader -- mrglavas
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(null, "schema-not-supported", null));
            }
        } 
        else if(JAXP_SCHEMA_SOURCE.equals(name)) {
            //If we are not validating then don't check for  JAXP_SCHEMA_LANGUAGE, JAXP_SCHEMA_SOURCED            
            if ( isValidating() ) {
                String val = (String)getProperty(JAXP_SCHEMA_LANGUAGE);
                if ( val != null && W3C_XML_SCHEMA.equals(val) ) {
                    xmlReader.setProperty(name, value);
                    parserFeatures.put(name, value);
                }
                else {
                    throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(null, 
                        "jaxp-order-not-supported", 
                        new Object[] {JAXP_SCHEMA_LANGUAGE, JAXP_SCHEMA_SOURCE}));
                }
            }
		}
		/*else if(name.equlas(Constants.ENTITY_EXPANSION_LIMIT)){
			String elimit = (String)value;
			if(elimit != null && elimit != ""){
				int val = Integer.parseInt(elimit);
				secureProcessing.setEntityExpansionLimit(val);
			}
		}else if(name.equals(Constants.MAX_OCCUR_LIMIT)) {
			String mlimit = (String)value;
			if(mlimit != null && mlimit != ""){
				int val = Integer.parseInt(mlimit);
				secureProcessing.setMaxOccurNodeLimit(val);
			}
		}*/
        else if(value instanceof Boolean){
            //assume feature
            xmlReader.setFeature(name,((Boolean)value).booleanValue());
            parserFeatures.put(name, value);
        } 
        else{
            xmlReader.setProperty(name, value);
            // If value is null, remove property from Hashtable
            if (value == null) {
                parserFeatures.remove(name);
            } else {
                parserFeatures.put(name, value);
            }
        }
    }

    /**
     * returns the particular property requested for in the underlying 
     * implementation of org.xml.sax.XMLReader.
     */
    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // TODO: reroute those properties to use new JAXP1.3 API. -KK
        if (JAXP_SCHEMA_LANGUAGE.equals(name)) {
            // JAXP 1.2 support
            return schemaLanguage;
        } else {
            return xmlReader.getProperty(name);
        }
    }

    
    public Schema getSchema(){
        return grammar;
    }
    
    /**
     * <p>Return True if secure processing in effect.</p>
     * Defaults to <code>false</code>.</p>
     *
     * @return state of Schema Caching
     */
    public boolean isSecureProcessing(){
        return secureProcessing!=null;
    }

    
}
