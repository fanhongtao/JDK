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

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.InsulatedValidatorComponent;
import com.sun.org.apache.xerces.internal.jaxp.validation.XercesConstants;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class InsulatedSchemaValidator extends InsulatedValidatorComponent {
    
    /**
     * We don't want the {@link XMLSchemaValidator} to use
     * the grammar pool provided by the parser, so that
     * we can exclusively contrl the set of schemas used.
     */
    private final XMLGrammarPool pool;
    
    /**
     * True to ignore schema location hints.
     */
    private final boolean ignoreLocationHints;

    InsulatedSchemaValidator( XMLGrammarPool _pool, boolean _ignoreLocationHints ) {
        super( new XMLSchemaValidator() );
        this.pool = _pool;
        this.ignoreLocationHints = _ignoreLocationHints;
    }

    public Object getProperty(String propName) {
        if( propName.equals(XercesConstants.XMLGRAMMAR_POOL) )
            return pool;
        return super.getProperty(propName);
    }
    
    public boolean getFeature(String propName) {
        if( propName.equals(XercesConstants.IGNORE_SCHEMA_LOCATION_HINTS) )
            return ignoreLocationHints;
        if( propName.equals(Constants.XERCES_FEATURE_PREFIX + Constants.CHANGE_IGNORABLE_CHARACTERS_INTO_IGNORABLE_WHITESPACES) )
            return true;
        if( propName.equals(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_ELEMENT_DEFAULT) )
            return true;
        return super.getFeature(propName);
    }
}
