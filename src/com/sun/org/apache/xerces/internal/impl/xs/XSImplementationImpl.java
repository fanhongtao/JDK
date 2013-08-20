/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.CoreDOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.XSImplementation;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import org.w3c.dom.DOMImplementation;


/**
 * Implements XSImplementation interface that allows one to retrieve an instance of <code>XSLoader</code>. 
 * This interface should be implemented on the same object that implements 
 * DOMImplementation.
 *
 * @author Elena Litani, IBM
 * @version $Id: XSImplementationImpl.java,v 1.3 2003/11/17 15:57:43 mrglavas Exp $
 */
public class XSImplementationImpl extends CoreDOMImplementationImpl 
 								  implements XSImplementation {

    //
    // Data
    //

    // static

    /** Dom implementation singleton. */
    static XSImplementationImpl singleton = new XSImplementationImpl();

    //
    // Public methods
    //

    /** NON-DOM: Obtain and return the single shared object */
    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }  

    //
    // DOMImplementation methods
    //

    /** 
     * Test if the DOM implementation supports a specific "feature" --
     * currently meaning language and level thereof.
     * 
     * @param feature      The package name of the feature to test.
     * In Level 1, supported values are "HTML" and "XML" (case-insensitive).
     * At this writing, com.sun.org.apache.xerces.internal.dom supports only XML.
     *
     * @param version      The version number of the feature being tested.
     * This is interpreted as "Version of the DOM API supported for the
     * specified Feature", and in Level 1 should be "1.0"
     *
     * @return    true iff this implementation is compatable with the specified
     * feature and version.
     */
    public boolean hasFeature(String feature, String version) {
    	
        return (feature.equalsIgnoreCase("XS-Loader") && (version == null || version.equals("1.0")) ||
		super.hasFeature(feature, version));
    } // hasFeature(String,String):boolean
    


    /* (non-Javadoc)
     * @see com.sun.org.apache.xerces.internal.xs.XSImplementation#createXSLoader(com.sun.org.apache.xerces.internal.xs.StringList)
     */
    public XSLoader createXSLoader(StringList versions) throws XSException {
    	XSLoader loader = new XMLSchemaLoader();
    	if (versions == null){
			return loader;
    	}
    	for (int i=0; i<versions.getLength();i++){
    		if (!versions.item(i).equals("1.0")){
				String msg =
					DOMMessageFormatter.formatMessage(
						DOMMessageFormatter.DOM_DOMAIN,
						"FEATURE_NOT_SUPPORTED",
						new Object[] { versions.item(i) });
				throw new XSException(XSException.NOT_SUPPORTED_ERR, msg);
    		}
    	}
    	return loader;
    }

    /* (non-Javadoc)
     * @see com.sun.org.apache.xerces.internal.xs.XSImplementation#getRecognizedVersions()
     */
    public StringList getRecognizedVersions() {
        StringListImpl list = new StringListImpl(new String[]{"1.0"}, 1);
        return list;
    }

} // class XSImplementationImpl
