/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights 
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

package com.sun.org.apache.xerces.internal.dom;

import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;
import org.w3c.dom.DOMImplementation;
import com.sun.org.apache.xerces.internal.dom.DOMImplementationListImpl;

/**
 * Supply one the right implementation, based upon requested features. Each
 * implemented <code>DOMImplementationSource</code> object is listed in the
 * binding-specific list of available sources so that its
 * <code>DOMImplementation</code> objects are made available.
 * 
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/core.html#DOMImplementationSource'>Document Object Model (DOM) Level 3 Core Specification</a>.
 * 
 * @version $Id: DOMImplementationSourceImpl.java,v 1.15 2004/04/23 21:45:04 mrglavas Exp $
 */
public class DOMImplementationSourceImpl
    implements DOMImplementationSource {

    /**
     * A method to request a DOM implementation.
     * @param features A string that specifies which features are required. 
     *   This is a space separated list in which each feature is specified 
     *   by its name optionally followed by a space and a version number. 
     *   This is something like: "XML 1.0 Traversal Events 2.0"
     * @return An implementation that has the desired features, or 
     *   <code>null</code> if this source has none.
     */
    public DOMImplementation getDOMImplementation(String features) {
        // first check whether the CoreDOMImplementation would do
        DOMImplementation impl =
            CoreDOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            return impl;
        }
        // if not try the DOMImplementation
        impl = DOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            return impl;
        }
        
        return null;
    }
    
    /**
     * A method to request a list of DOM implementations that support the 
     * specified features and versions, as specified in .
     * @param features A string that specifies which features and versions 
     *   are required. This is a space separated list in which each feature 
     *   is specified by its name optionally followed by a space and a 
     *   version number. This is something like: "XML 3.0 Traversal +Events 
     *   2.0"
     * @return A list of DOM implementations that support the desired 
     *   features.
     */
    public DOMImplementationList getDOMImplementationList(String features) {
        // first check whether the CoreDOMImplementation would do
        DOMImplementation impl = CoreDOMImplementationImpl.getDOMImplementation();
		final Vector implementations = new Vector();
        if (testImpl(impl, features)) {
			implementations.addElement(impl);
        }
        impl = DOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
			implementations.addElement(impl);
        }

        return new DOMImplementationListImpl(implementations);
    }

    boolean testImpl(DOMImplementation impl, String features) {
       
        StringTokenizer st = new StringTokenizer(features);
        String feature = null;
        String version = null;
 
        if (st.hasMoreTokens()) {
           feature = st.nextToken();
        }
        while (feature != null) {
           boolean isVersion = false;
           if (st.hasMoreTokens()) {
               char c;
               version = st.nextToken();
               c = version.charAt(0);
               switch (c) {
               case '0': case '1': case '2': case '3': case '4':
               case '5': case '6': case '7': case '8': case '9':
                   isVersion = true;
               }
           } else {
               version = null;
           }
           if (isVersion) {
               if (!impl.hasFeature(feature, version)) {
                   return false;
               }
               if (st.hasMoreTokens()) {
                   feature = st.nextToken();
               } else {
                   feature = null;
               }
           } else {
               if (!impl.hasFeature(feature, null)) {
                   return false;
               }
               feature = version;
           }
        }
        return true;
    }
}
