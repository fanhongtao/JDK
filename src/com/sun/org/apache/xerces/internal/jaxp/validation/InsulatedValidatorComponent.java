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

import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

/**
 * Wraps a validator {@link XMLComponent} and isolates
 * it from the rest of the components.
 * 
 * <p>
 * For the performance reason, when a validator from Xerces is used
 * for a parser from Xerces, we will do "chating" by building an
 * XNI pipeline. This saves the overhead of conversion between
 * XNI events and SAX events.
 * 
 * <p>
 * However, if we just insert the validator component into the
 * parser pipeline, the {@link XMLComponentManager} that the parser
 * uses could change the way the validator works. On the other hand,
 * certain configuration (such as error handlers) need to be given
 * through a parser configuration. 
 * 
 * <p>
 * To avoid this harmful interaction, this class wraps the validator
 * and behaves as an insulation. The class itself will implement
 * {@link XMLComponent}, and it selectively deliver properties to
 * the wrapped validator.
 * 
 * <p>
 * Since the exact set of properties/features that require insulation
 * depends on the actual validator implementation, this class is
 * expected to be derived to add such validator-specific insulation
 * code.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class InsulatedValidatorComponent implements
    XMLComponent, // this object behaves as a component to the parent component manager
    XMLComponentManager // this object behaves as a manager to the wrapped validator.
{
    
    /**
     * The object being wrapped.
     * We require the validator to be both {@link XMLDocumentFilter}
     * and {@link XMLComponent}.
     */
    private final XMLDocumentFilter fValidator;
    
    /**
     * The same object as {@link #fValidator}.
     */
    private final XMLComponent fValidatorComponent;
    
    /**
     * We will not use external {@link ValidationManager} to
     * avoid interaction.
     * <p>
     * The existance of JAXP validator should not change the
     * semantics of the parser processing. IOW it should not
     * interact with the other validators in the parser pipeline.
     */
    private final ValidationManager fValidationManager = new ValidationManager();
    
    /**
     * The current component manager.
     */
    private XMLComponentManager fManager;
    
    public InsulatedValidatorComponent( XMLDocumentFilter validator ) {
        fValidator = validator;
        fValidatorComponent = (XMLComponent)validator;
    }
    
    
    /**
     * Obtains a reference to the validator as a filter.
     * 
     * @return
     *      non-null valid object.
     */
    public final XMLDocumentFilter getValidator() {
        return fValidator;
    }

    
//
//
// XMLComponent implementation
//
//
    public final void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
        fManager = componentManager;
        fValidatorComponent.reset(this);
        fValidationManager.reset();
    }


    public final String[] getRecognizedFeatures() {
        return fValidatorComponent.getRecognizedFeatures();
    }


    public final void setFeature(String featureId, boolean state) throws XMLConfigurationException {
        // don't allow features to be set.
    }


    public final String[] getRecognizedProperties() {
        return fValidatorComponent.getRecognizedProperties();
    }


    public final void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        // don't allow properties to be set.
    }


    public final Boolean getFeatureDefault(String featureId) {
        return fValidatorComponent.getFeatureDefault(featureId);
    }


    public final Object getPropertyDefault(String propertyId) {
        return fValidatorComponent.getPropertyDefault(propertyId);
    }

    
//
//
//  XMLComponentManager implementation
//
//
    /**
     * Derived class may override this method to block additional features.
     */
    public boolean getFeature(String featureId) throws XMLConfigurationException {
        return fManager.getFeature(featureId);
    }


    /**
     * Derived class may override this method to block additional properties.
     */
    public Object getProperty(String propertyId) throws XMLConfigurationException {
        if( propertyId.equals(XercesConstants.VALIDATION_MANAGER) )
            return fValidationManager;
        return fManager.getProperty(propertyId);
    }
}
