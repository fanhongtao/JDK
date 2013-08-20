/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  
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

package com.sun.org.apache.xerces.internal.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

/**
 * This class implements the basic operations for managing parser
 * configuration features and properties. This utility class can
 * be used as a base class for parser configurations or separately
 * to encapsulate a number of parser settings as a component
 * manager.
 * <p>
 * This class can be constructed with a "parent" settings object
 * (in the form of an <code>XMLComponentManager</code>) that allows
 * parser configuration settings to be "chained" together.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: ParserConfigurationSettings.java,v 1.11 2004/04/25 05:05:50 mrglavas Exp $
 */
public class ParserConfigurationSettings
    implements XMLComponentManager {
       
   protected static final String PARSER_SETTINGS =
       Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;    

    //
    // Data
    //

    // data

    /** Recognized properties. */
    protected ArrayList fRecognizedProperties;

    /** Properties. */
    protected HashMap fProperties;

    /** Recognized features. */
    protected ArrayList fRecognizedFeatures;

    /** Features. */
    protected HashMap fFeatures;

    /** Parent parser configuration settings. */
    protected XMLComponentManager fParentSettings;

    //
    // Constructors
    //

    /** Default Constructor. */
    public ParserConfigurationSettings() {
        this(null);
    } // <init>()

    /**
     * Constructs a parser configuration settings object with a
     * parent settings object.
     */
    public ParserConfigurationSettings(XMLComponentManager parent) {

        // create storage for recognized features and properties
        fRecognizedFeatures = new ArrayList();
        fRecognizedProperties = new ArrayList();

        // create table for features and properties
        fFeatures = new HashMap();
        fProperties = new HashMap();

        // save parent
        fParentSettings = parent;

    } // <init>(XMLComponentManager)

    //
    // XMLParserConfiguration methods
    //

    /**
     * Allows a parser to add parser specific features to be recognized
     * and managed by the parser configuration.
     *
     * @param featureIds An array of the additional feature identifiers 
     *                   to be recognized.
     */
    public void addRecognizedFeatures(String[] featureIds) {

        // add recognized features
        int featureIdsCount = featureIds != null ? featureIds.length : 0;
        for (int i = 0; i < featureIdsCount; i++) {
            String featureId = featureIds[i];
            if (!fRecognizedFeatures.contains(featureId)) {
                fRecognizedFeatures.add(featureId);
            }
        }

    } // addRecognizedFeatures(String[])

    /**
     * Set the state of a feature.
     *
     * Set the state of any feature in a SAX2 parser.  The parser
     * might not recognize the feature, and if it does recognize
     * it, it might not be able to fulfill the request.
     *
     * @param featureId The unique identifier (URI) of the feature.
     * @param state The requested state of the feature (true or false).
     *
     * @exception com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {

        // check and store
        checkFeature(featureId);

        fFeatures.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);
    } // setFeature(String,boolean)

    /**
     * Allows a parser to add parser specific properties to be recognized
     * and managed by the parser configuration.
     *
     * @param propertyIds An array of the additional property identifiers 
     *                    to be recognized.
     */
    public void addRecognizedProperties(String[] propertyIds) {

        // add recognizedProperties
        int propertyIdsCount = propertyIds != null ? propertyIds.length : 0;
        for (int i = 0; i < propertyIdsCount; i++) {
            String propertyId = propertyIds[i];
            if (!fRecognizedProperties.contains(propertyId)) {
                fRecognizedProperties.add(propertyId);
            }
        }

    } // addRecognizedProperties(String[])

    /**
     * setProperty
     * 
     * @param propertyId 
     * @param value 
     * @exception com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        // check and store
        checkProperty(propertyId);
		if(value == null){
			fProperties.remove(propertyId);
		}else
        	fProperties.put(propertyId, value);

    } // setProperty(String,Object)

    //
    // XMLComponentManager methods
    //

    /**
     * Returns the state of a feature.
     * 
     * @param featureId The feature identifier.
		 * @return true if the feature is supported
     * 
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    public boolean getFeature(String featureId)
        throws XMLConfigurationException {

        Boolean state = (Boolean) fFeatures.get(featureId);

        if (state == null) {
            checkFeature(featureId);
            return false;
        }
        return state.booleanValue();

    } // getFeature(String):boolean

    /**
     * Returns the value of a property.
     * 
     * @param propertyId The property identifier.
		 * @return the value of the property
     * 
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    public Object getProperty(String propertyId)
        throws XMLConfigurationException {

        Object propertyValue = fProperties.get(propertyId);

        if (propertyValue == null) {
            checkProperty(propertyId);
        }

        return propertyValue;

    } // getProperty(String):Object

    //
    // Protected methods
    //

    /**
     * Check a feature. If feature is known and supported, this method simply
     * returns. Otherwise, the appropriate exception is thrown.
     *
     * @param featureId The unique identifier (URI) of the feature.
     *
     * @exception com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    protected void checkFeature(String featureId)
        throws XMLConfigurationException {

        // check feature
        if (!fRecognizedFeatures.contains(featureId)) {
            if (fParentSettings != null) {
                fParentSettings.getFeature(featureId);
            }
            else {
                short type = XMLConfigurationException.NOT_RECOGNIZED;
                throw new XMLConfigurationException(type, featureId);
            }
        }

    } // checkFeature(String)

    /**
     * Check a property. If the property is known and supported, this method
     * simply returns. Otherwise, the appropriate exception is thrown.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     * @exception com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    protected void checkProperty(String propertyId)
        throws XMLConfigurationException {

        // check property
        if (!fRecognizedProperties.contains(propertyId)) {
            if (fParentSettings != null) {
                fParentSettings.getProperty(propertyId);
            }
            else {
                short type = XMLConfigurationException.NOT_RECOGNIZED;
                throw new XMLConfigurationException(type, propertyId);
            }
        }

    } // checkProperty(String)

} // class ParserConfigurationSettings
