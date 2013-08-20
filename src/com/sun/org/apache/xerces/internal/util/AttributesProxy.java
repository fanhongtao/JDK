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
package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;

/**
 * Wraps {@link XMLAttributes} and makes it look like
 * {@link AttributeList} and {@link Attributes}.
 *
 * @author Arnaud Le Hors, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: AttributesProxy.java,v 1.2 2004/01/27 00:31:58 kk122374 Exp $
 */
public final class AttributesProxy
    implements AttributeList, Attributes {

    //
    // Data
    //

    /** XML attributes. */
    private XMLAttributes fAttributes;

    //
    // Public methods
    //

    /** Sets the XML attributes to be wrapped. */
    public void setAttributes(XMLAttributes attributes) {
        fAttributes = attributes;
    } // setAttributes(XMLAttributes)
    
    public XMLAttributes getAttributes() {
        return fAttributes;
    }

    public int getLength() {
        return fAttributes.getLength();
    }

    public String getName(int i) {
        return fAttributes.getQName(i);
    }

    public String getQName(int index) {
        return fAttributes.getQName(index);
    }

    public String getURI(int index) {
        // REVISIT: this hides the fact that internally we use
        //          null instead of empty string
        //          SAX requires URI to be a string or an empty string
        String uri= fAttributes.getURI(index);
        return uri != null ? uri : "";
    }

    public String getLocalName(int index) {
        return fAttributes.getLocalName(index);
    }

    public String getType(int i) {
        return fAttributes.getType(i);
    }

    public String getType(String name) {
        return fAttributes.getType(name);
    }

    public String getType(String uri, String localName) {
        return uri.equals("") ? fAttributes.getType(null, localName) :
                                fAttributes.getType(uri, localName);
    }

    public String getValue(int i) {
        return fAttributes.getValue(i);
    }

    public String getValue(String name) {
        return fAttributes.getValue(name);
    }

    public String getValue(String uri, String localName) {
        return uri.equals("") ? fAttributes.getValue(null, localName) :
                                fAttributes.getValue(uri, localName);
    }

    public int getIndex(String qName) {
        return fAttributes.getIndex(qName);
    }

    public int getIndex(String uri, String localPart) {
        return uri.equals("") ? fAttributes.getIndex(null, localPart) :
                                fAttributes.getIndex(uri, localPart);
    }

}

