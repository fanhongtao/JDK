/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
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

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

import com.sun.org.apache.xerces.internal.dom3.as.DOMImplementationAS;
import com.sun.org.apache.xerces.internal.dom3.as.ASModel;
import com.sun.org.apache.xerces.internal.dom3.as.DOMASBuilder;
import com.sun.org.apache.xerces.internal.dom3.as.DOMASWriter;
import com.sun.org.apache.xerces.internal.parsers.DOMASBuilderImpl;



/**
 * The DOMImplementation class is description of a particular
 * implementation of the Document Object Model. As such its data is
 * static, shared by all instances of this implementation.
 * <P>
 * The DOM API requires that it be a real object rather than static
 * methods. However, there's nothing that says it can't be a singleton,
 * so that's how I've implemented it.
 * <P>
 * This particular class, along with DocumentImpl, supports the DOM
 * Core, DOM Level 2 optional mofules, and Abstract Schemas (Experimental).
 * @deprecated 
 * @version $Id: ASDOMImplementationImpl.java,v 1.5 2003/07/30 10:30:35 neeraj Exp $
 * @since PR-DOM-Level-1-19980818.
 */
public class ASDOMImplementationImpl extends DOMImplementationImpl 
    implements DOMImplementationAS {


    // static

    /** Dom implementation singleton. */
    static ASDOMImplementationImpl singleton = new ASDOMImplementationImpl();


    //
    // Public methods
    //

    /** NON-DOM: Obtain and return the single shared object */
    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }  

    //
    // DOM L3 Abstract Schemas:
    // REVISIT: implement hasFeature()
    //

    /**
     * DOM Level 3 WD - Experimental.
     * Creates an ASModel.
     * @param isNamespaceAware Allow creation of <code>ASModel</code> with 
     *   this attribute set to a specific value.
     * @return A <code>null</code> return indicates failure.what is a 
     *   failure? Could be a system error.
     */
    public ASModel createAS(boolean isNamespaceAware){
        return new ASModelImpl(isNamespaceAware);
    }

    /**
     * DOM Level 3 WD - Experimental.
     * Creates an <code>DOMASBuilder</code>.Do we need the method since we 
     * already have <code>DOMImplementationLS.createDOMParser</code>?
     * @return  DOMASBuilder
     */
    public DOMASBuilder createDOMASBuilder(){
        return new DOMASBuilderImpl();
    }


    /**
     * DOM Level 3 WD - Experimental.
     * Creates an <code>DOMASWriter</code>.
     * @return  a DOMASWriter
     */
    public DOMASWriter createDOMASWriter(){
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }
    


} // class DOMImplementationImpl
