/*
 *
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
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc.,
 * http://www.sun.com.  For more information on the Apache Software
 * Foundation, please see <http://www.apache.org/>.
 */


package com.sun.org.apache.xerces.internal.jaxp;

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.xml.sax.SAXException;

/**
 * @author Rajiv Mordani
 * @author Edwin Goei
 * @version $Id: DocumentBuilderFactoryImpl.java,v 1.14 2004/02/24 23:15:58 mrglavas Exp $
 */
public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
    /** These are DocumentBuilderFactory attributes not DOM attributes */
    private Hashtable attributes;
    private Schema grammar;
    private boolean isXIncludeAware;
    
    /**
     * Creates a new instance of a {@link javax.xml.parsers.DocumentBuilder}
     * using the currently configured parameters.
     */
    public DocumentBuilder newDocumentBuilder()
    throws ParserConfigurationException {
        // check the consistency between the specified schema and
        // the schema property. I thought about putting this into
        // DocumentBuilderImpl, but because of the hack in the getAttribute method,
        // we can't really do that. -KK
        if( attributes!= null &&  attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage") &&  grammar!=null )
            throw new ParserConfigurationException(
            DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN,
            "jaxp-schema-support",null));
        
        
        try {
            return new DocumentBuilderImpl(this, attributes);
        } catch (SAXException se) {
            // Handles both SAXNotSupportedException, SAXNotRecognizedException
            throw new ParserConfigurationException(se.getMessage());
        }
    }
    
    /**
     * Allows the user to set specific attributes on the underlying
     * implementation.
     * @param name    name of attribute
     * @param value   null means to remove attribute
     */
    public void setAttribute(String name, Object value)
    throws IllegalArgumentException {
        // This handles removal of attributes
        if (value == null) {
            if (attributes != null) {
                attributes.remove(name);
            }
            // Unrecognized attributes do not cause an exception
            return;
        }
        
        // This is ugly.  We have to collect the attributes and then
        // later create a DocumentBuilderImpl to verify the attributes.
        
        // Create Hashtable if none existed before
        if (attributes == null) {
            attributes = new Hashtable();
        }
        
        attributes.put(name, value);
        
        // Test the attribute name by possibly throwing an exception
        try {
            new DocumentBuilderImpl(this, attributes);
        } catch (Exception e) {
            attributes.remove(name);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    /**
     * Allows the user to retrieve specific attributes on the underlying
     * implementation.
     */
    public Object getAttribute(String name) throws IllegalArgumentException {
        // See if it's in the attributes Hashtable
        if (attributes != null) {
            Object val = attributes.get(name);
            if (val != null) {
                return val;
            }
        }
        
        DOMParser domParser = null;
        try {
            // We create a dummy DocumentBuilderImpl in case the attribute
            // name is not one that is in the attributes hashtable.
            domParser =
            new DocumentBuilderImpl(this, attributes).getDOMParser();
            return domParser.getProperty(name);
        } catch (SAXException se1) {
            // assert(name is not recognized or not supported), try feature
            try {
                boolean result = domParser.getFeature(name);
                // Must have been a feature
                return result ? Boolean.TRUE : Boolean.FALSE;
            } catch (SAXException se2) {
                // Not a property or a feature
                throw new IllegalArgumentException(se1.getMessage());
            }
        }
    }
    
    public Schema getSchema() {
        return grammar;
    }
    
    public void setSchema(Schema grammar) {
        this.grammar = grammar;
    }
    
    public boolean isXIncludeAware() {
        return this.isXIncludeAware;
    }
    
    public void setXIncludeAware(boolean state) {
        this.isXIncludeAware = state;
    }
    
    public  void setFeature(String name, boolean value)
    throws ParserConfigurationException{
        
        //Revisit::
        //for now use attributes itself. we just support on feature.
        //If we need to use setFeature in full fledge we should
        //document what is supported by setAttribute
        //and what is by setFeature.
        //user should not use setAttribute("xyz",Boolean.TRUE)
        //instead of setFeature("xyz",true);
        if(attributes == null)
            attributes = new Hashtable();
        if(name.equals(Constants.FEATURE_SECURE_PROCESSING)){
            attributes.put(Constants.FEATURE_SECURE_PROCESSING,Boolean.valueOf(value));
        } else throw new ParserConfigurationException(
        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN,
        "jaxp_feature_not_supported",
        new Object[] {name}));
        
    }
    
    public  boolean getFeature(String name)
    throws ParserConfigurationException {
        
        if (name.equals(Constants.FEATURE_SECURE_PROCESSING)){
            Object ob =  attributes.get(Constants.FEATURE_SECURE_PROCESSING);
            if(ob == null) return false;
            return ((Boolean)ob).booleanValue();
        }
        else
            throw new ParserConfigurationException(DOMMessageFormatter.formatMessage(
            DOMMessageFormatter.DOM_DOMAIN,"jaxp_feature_not_supported",
            new Object[] {name}));
    }
}
