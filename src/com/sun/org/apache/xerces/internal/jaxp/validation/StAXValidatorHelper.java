/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jaxp.dev.java.net/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jaxp.dev.java.net/CDDLv1.0.html
 * If applicable add the following below this CDDL HEADER
 * with the fields enclosed by brackets "[]" replaced with
 * your own identifying information: Portions Copyright
 * [year] [name of copyright owner]
 */

/*
 * $Id: XMLEntityReader.java,v 1.3 2005/11/03 17:02:21 jeffsuttor Exp $
 * @(#)StAXValidatorHelper.java	1.3 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;
import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;

import org.xml.sax.SAXException;

/**
 * <p>A validator helper for <code>StAXSource</code>s.</p>
 *
 * @author <a href="mailto:Sunitha.Reddy@Sun.com">Sunitha Reddy</a>
 */
public final class StAXValidatorHelper implements ValidatorHelper {
    
    /** Component manager. **/
    private XMLSchemaValidatorComponentManager fComponentManager;
    
    private Transformer identityTransformer1 = null;
    private TransformerHandler identityTransformer2 = null;
    private ValidatorHandlerImpl handler = null;
    
    /** Creates a new instance of StaxValidatorHelper */
    public StAXValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
        fComponentManager = componentManager;
    }
    
    public void validate(Source source, Result result) 
        throws SAXException, IOException {
        
        if (result == null || result instanceof StAXResult) {
         
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
                handler = new ValidatorHandlerImpl(fComponentManager);
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
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(Locale.getDefault(), 
                "SourceResultMismatch", 
                new Object [] {source.getClass().getName(), result.getClass().getName()}));
    }
}
