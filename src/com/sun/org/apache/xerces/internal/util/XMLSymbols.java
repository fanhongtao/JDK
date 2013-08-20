/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

/**
 * All internalized xml symbols. They can be compared using "==".
 * 
 * @author Sandy Gao, IBM
 * @version $Id: XMLSymbols.java,v 1.2 2002/08/14 17:52:52 sandygao Exp $
 */
public class XMLSymbols {
    
    // public constructor.
    public XMLSymbols(){}
    
    //==========================
    // Commonly used strings
    //==========================
    
    /**
     * The empty string.
     */
    public final static String EMPTY_STRING = "".intern();

    //==========================
    // Namespace prefixes/uris
    //==========================
    
    /**
     * The internalized "xml" prefix.
     */
    public final static String PREFIX_XML = "xml".intern();

    /**
     * The internalized "xmlns" prefix.
     */
    public final static String PREFIX_XMLNS = "xmlns".intern();

    //==========================
    // DTD symbols
    //==========================
    
    /** Symbol: "ANY". */
    public static final String fANYSymbol = "ANY".intern();

    /** Symbol: "CDATA". */
    public static final String fCDATASymbol = "CDATA".intern();

    /** Symbol: "ID". */
    public static final String fIDSymbol = "ID".intern();

    /** Symbol: "IDREF". */
    public static final String fIDREFSymbol = "IDREF".intern();

    /** Symbol: "IDREFS". */
    public static final String fIDREFSSymbol = "IDREFS".intern();

    /** Symbol: "ENTITY". */
    public static final String fENTITYSymbol = "ENTITY".intern();

    /** Symbol: "ENTITIES". */
    public static final String fENTITIESSymbol = "ENTITIES".intern();

    /** Symbol: "NMTOKEN". */
    public static final String fNMTOKENSymbol = "NMTOKEN".intern();

    /** Symbol: "NMTOKENS". */
    public static final String fNMTOKENSSymbol = "NMTOKENS".intern();

    /** Symbol: "NOTATION". */
    public static final String fNOTATIONSymbol = "NOTATION".intern();

    /** Symbol: "ENUMERATION". */
    public static final String fENUMERATIONSymbol = "ENUMERATION".intern();

    /** Symbol: "#IMPLIED. */
    public static final String fIMPLIEDSymbol = "#IMPLIED".intern();

    /** Symbol: "#REQUIRED". */
    public static final String fREQUIREDSymbol = "#REQUIRED".intern();

    /** Symbol: "#FIXED". */
    public static final String fFIXEDSymbol = "#FIXED".intern();
    
    
}
