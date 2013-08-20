/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  
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

package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;

/**
 * All information specific to XML Schema grammars.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XMLSchemaDescription.java,v 1.2 2003/11/14 16:54:05 mrglavas Exp $
 */
public interface XMLSchemaDescription extends XMLGrammarDescription {

    // used to indicate what triggered the call
    /**
     * Indicate that the current schema document is &lt;include&gt;d by another
     * schema document.
     */
    public final static short CONTEXT_INCLUDE   = 0;
    /**
     * Indicate that the current schema document is &lt;redefine&gt;d by another
     * schema document.
     */
    public final static short CONTEXT_REDEFINE  = 1;
    /**
     * Indicate that the current schema document is &lt;import&gt;ed by another
     * schema document.
     */
    public final static short CONTEXT_IMPORT    = 2;
    /**
     * Indicate that the current schema document is being preparsed.
     */
    public final static short CONTEXT_PREPARSE  = 3;
    /**
     * Indicate that the parse of the current schema document is triggered
     * by xsi:schemaLocation/noNamespaceSchemaLocation attribute(s) in the
     * instance document. This value is only used if we don't defer the loading
     * of schema documents.
     */
    public final static short CONTEXT_INSTANCE  = 4;
    /**
     * Indicate that the parse of the current schema document is triggered by
     * the occurrence of an element whose namespace is the target namespace
     * of this schema document. This value is only used if we do defer the
     * loading of schema documents until a component from that namespace is
     * referenced from the instance.
     */
    public final static short CONTEXT_ELEMENT   = 5;
    /**
     * Indicate that the parse of the current schema document is triggered by
     * the occurrence of an attribute whose namespace is the target namespace
     * of this schema document. This value is only used if we do defer the
     * loading of schema documents until a component from that namespace is
     * referenced from the instance.
     */
    public final static short CONTEXT_ATTRIBUTE = 6;
    /**
     * Indicate that the parse of the current schema document is triggered by
     * the occurrence of an "xsi:type" attribute, whose value (a QName) has
     * the target namespace of this schema document as its namespace.
     * This value is only used if we do defer the loading of schema documents
     * until a component from that namespace is referenced from the instance.
     */
    public final static short CONTEXT_XSITYPE   = 7;

    /**
     * Get the context. The returned value is one of the pre-defined
     * CONTEXT_xxx constants.
     * 
     * @return  the value indicating the context
     */
    public short getContextType();

    /**
     * If the context is "include" or "redefine", then return the target
     * namespace of the enclosing schema document; otherwise, the expected
     * target namespace of this document.
     * 
     * @return  the expected/enclosing target namespace
     */
    public String getTargetNamespace();

    /**
     * For import and references from the instance document, it's possible to
     * have multiple hints for one namespace. So this method returns an array,
     * which contains all location hints.
     * 
     * @return  an array of all location hints associated to the expected
     *          target namespace
     */          
    public String[] getLocationHints();

    /**
     * If a call is triggered by an element/attribute/xsi:type in the instance,
     * this call returns the name of such triggering component: the name of
     * the element/attribute, or the value of the xsi:type.
     * 
     * @return  the name of the triggering component
     */
    public QName getTriggeringComponent();

    /**
     * If a call is triggered by an attribute or xsi:type, then this mehtod
     * returns the enclosing element of such element.
     * 
     * @return  the name of the enclosing element
     */
    public QName getEnclosingElementName();
    
    /**
     * If a call is triggered by an element/attribute/xsi:type in the instance,
     * this call returns all attribute of such element (or enclosing element).
     * 
     * @return  all attributes of the tiggering/enclosing element
     */
    public XMLAttributes getAttributes();
    
} // XSDDescription
