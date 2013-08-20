/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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
package com.sun.org.apache.wml.internal.dom;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.NodeImpl;
import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import org.w3c.dom.DOMException;
import com.sun.org.apache.wml.internal.*;

/**
 * @version $Id: WMLDocumentImpl.java,v 1.2 2000/10/04 11:19:26 jeffreyr Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */

public class WMLDocumentImpl extends DocumentImpl implements WMLDocument {

    private static Hashtable        _elementTypesWML;
    private static final Class[]    _elemClassSigWML =
	new Class[] { WMLDocumentImpl.class, String.class };

    public Element createElement( String tagName ) throws DOMException
    {
        Class        elemClass;
        Constructor    cnst;

        elemClass = (Class) _elementTypesWML.get( tagName );
        if ( elemClass != null ) {
            try	{
                cnst = elemClass.getConstructor( _elemClassSigWML );
                return (Element) cnst.newInstance( new Object[] { this, tagName } );
            } catch ( Exception except ) {
                Throwable thrw;

                if ( except instanceof java.lang.reflect.InvocationTargetException )
                    thrw = ( (java.lang.reflect.InvocationTargetException) except ).getTargetException();
                else
                    thrw = except;

                System.out.println( "Exception " + thrw.getClass().getName() );
                System.out.println( thrw.getMessage() );

                throw new IllegalStateException( "Tag '" + tagName + "' associated with an Element class that failed to construct." );
            }
        }
        return new WMLElementImpl( this, tagName );
    }

    static {
	_elementTypesWML = new Hashtable();
	_elementTypesWML.put("b", WMLBElementImpl.class);
	_elementTypesWML.put("noop", WMLNoopElementImpl.class);
	_elementTypesWML.put("a", WMLAElementImpl.class);
	_elementTypesWML.put("setvar", WMLSetvarElementImpl.class);
	_elementTypesWML.put("access", WMLAccessElementImpl.class);
	_elementTypesWML.put("strong", WMLStrongElementImpl.class);
	_elementTypesWML.put("postfield", WMLPostfieldElementImpl.class);
	_elementTypesWML.put("do", WMLDoElementImpl.class);
	_elementTypesWML.put("wml", WMLWmlElementImpl.class);
	_elementTypesWML.put("tr", WMLTrElementImpl.class);
	_elementTypesWML.put("go", WMLGoElementImpl.class);
	_elementTypesWML.put("big", WMLBigElementImpl.class);
	_elementTypesWML.put("anchor", WMLAnchorElementImpl.class);
	_elementTypesWML.put("timer", WMLTimerElementImpl.class);
	_elementTypesWML.put("small", WMLSmallElementImpl.class);
	_elementTypesWML.put("optgroup", WMLOptgroupElementImpl.class);
	_elementTypesWML.put("head", WMLHeadElementImpl.class);
	_elementTypesWML.put("td", WMLTdElementImpl.class);
	_elementTypesWML.put("fieldset", WMLFieldsetElementImpl.class);
	_elementTypesWML.put("img", WMLImgElementImpl.class);
	_elementTypesWML.put("refresh", WMLRefreshElementImpl.class);
	_elementTypesWML.put("onevent", WMLOneventElementImpl.class);
	_elementTypesWML.put("input", WMLInputElementImpl.class);
	_elementTypesWML.put("prev", WMLPrevElementImpl.class);
	_elementTypesWML.put("table", WMLTableElementImpl.class);
	_elementTypesWML.put("meta", WMLMetaElementImpl.class);
	_elementTypesWML.put("template", WMLTemplateElementImpl.class);
	_elementTypesWML.put("br", WMLBrElementImpl.class);
	_elementTypesWML.put("option", WMLOptionElementImpl.class);
	_elementTypesWML.put("u", WMLUElementImpl.class);
	_elementTypesWML.put("p", WMLPElementImpl.class);
	_elementTypesWML.put("select", WMLSelectElementImpl.class);
	_elementTypesWML.put("em", WMLEmElementImpl.class);
	_elementTypesWML.put("i", WMLIElementImpl.class);
	_elementTypesWML.put("card", WMLCardElementImpl.class);       
    }

    
    /* DOM level 2 */
    public WMLDocumentImpl(DocumentType doctype) {
        super(doctype, false);
    }
}
