/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002, 2003 The Apache Software Foundation.  All rights 
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

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

/**
 * The XMLResourceIdentifierImpl class is an implementation of the 
 * XMLResourceIdentifier interface which defines the location identity
 * of a resource.
 *
 * @author Andy Clark 
 *
 * @version $Id: XMLResourceIdentifierImpl.java,v 1.2 2003/03/24 21:10:59 sandygao Exp $
 */
public class XMLResourceIdentifierImpl
    implements XMLResourceIdentifier {

    //
    // Data
    //

    /** The public identifier. */
    protected String fPublicId;

    /** The literal system identifier. */
    protected String fLiteralSystemId;

    /** The base system identifier. */
    protected String fBaseSystemId;

    /** The expanded system identifier. */
    protected String fExpandedSystemId;

    /** The namespace of the resource. */
    protected String fNamespace;

    //
    // Constructors
    //

    /** Constructs an empty resource identifier. */
    public XMLResourceIdentifierImpl() {} // <init>()

    /**
     * Constructs a resource identifier.
     *
     * @param publicId The public identifier.
     * @param literalSystemId The literal system identifier.
     * @param baseSystemId The base system identifier.
     * @param expandedSystemId The expanded system identifier.
     */
    public XMLResourceIdentifierImpl(String publicId,
                                     String literalSystemId, String baseSystemId,
                                     String expandedSystemId) {
        setValues(publicId, literalSystemId, baseSystemId,
                  expandedSystemId, null);
    } // <init>(String,String,String,String)

    /**
     * Constructs a resource identifier.
     *
     * @param publicId The public identifier.
     * @param literalSystemId The literal system identifier.
     * @param baseSystemId The base system identifier.
     * @param expandedSystemId The expanded system identifier.
     * @param namespace The namespace.
     */
    public XMLResourceIdentifierImpl(String publicId, String literalSystemId,
                                     String baseSystemId, String expandedSystemId,
                                     String namespace) {
        setValues(publicId, literalSystemId, baseSystemId,
                  expandedSystemId, namespace);
    } // <init>(String,String,String,String,String)

    //
    // Public methods
    //

    /** Sets the values of the resource identifier. */
    public void setValues(String publicId, String literalSystemId,
                          String baseSystemId, String expandedSystemId) {
        setValues(publicId, literalSystemId, baseSystemId,
                  expandedSystemId, null);
    } // setValues(String,String,String,String)

    /** Sets the values of the resource identifier. */
    public void setValues(String publicId, String literalSystemId,
                          String baseSystemId, String expandedSystemId,
                          String namespace) {
        fPublicId = publicId;
        fLiteralSystemId = literalSystemId;
        fBaseSystemId = baseSystemId;
        fExpandedSystemId = expandedSystemId;
        fNamespace = namespace;
    } // setValues(String,String,String,String,String)

    /** Clears the values. */
    public void clear() {
        fPublicId = null;
        fLiteralSystemId = null;
        fBaseSystemId = null;
        fExpandedSystemId = null;
        fNamespace = null;
    } // clear()

    /** Sets the public identifier. */
    public void setPublicId(String publicId) {
        fPublicId = publicId;
    } // setPublicId(String)

    /** Sets the literal system identifier. */
    public void setLiteralSystemId(String literalSystemId) {
        fLiteralSystemId = literalSystemId;
    } // setLiteralSystemId(String)

    /** Sets the base system identifier. */
    public void setBaseSystemId(String baseSystemId) {
        fBaseSystemId = baseSystemId;
    } // setBaseSystemId(String)

    /** Sets the expanded system identifier. */
    public void setExpandedSystemId(String expandedSystemId) {
        fExpandedSystemId = expandedSystemId;
    } // setExpandedSystemId(String)

    /** Sets the namespace of the resource. */
    public void setNamespace(String namespace) {
        fNamespace = namespace;
    } // setNamespace(String)

    //
    // XMLResourceIdentifier methods
    //

    /** Returns the public identifier. */
    public String getPublicId() {
        return fPublicId;
    } // getPublicId():String

    /** Returns the literal system identifier. */
    public String getLiteralSystemId() {
        return fLiteralSystemId;
    } // getLiteralSystemId():String

    /** 
     * Returns the base URI against which the literal SystemId is to be resolved.
     */
    public String getBaseSystemId() {
        return fBaseSystemId;
    } // getBaseSystemId():String

    /** Returns the expanded system identifier. */
    public String getExpandedSystemId() {
        return fExpandedSystemId;
    } // getExpandedSystemId():String

    /** Returns the namespace of the resource. */
    public String getNamespace() {
        return fNamespace;
    } // getNamespace():String

    //
    // Object methods
    //

    /** Returns a hash code for this object. */
    public int hashCode() {
        int code = 0;
        if (fPublicId != null) {
            code += fPublicId.hashCode();
        }
        if (fLiteralSystemId != null) {
            code += fLiteralSystemId.hashCode();
        }
        if (fBaseSystemId != null) {
            code += fBaseSystemId.hashCode();
        }
        if (fExpandedSystemId != null) {
            code += fExpandedSystemId.hashCode();
        }
        if (fNamespace != null) {
            code += fNamespace.hashCode();
        }
        return code;
    } // hashCode():int

    /** Returns a string representation of this object. */
    public String toString() {
        StringBuffer str = new StringBuffer();
        if (fPublicId != null) {
            str.append(fPublicId);
        }
        str.append(':');
        if (fLiteralSystemId != null) {
            str.append(fLiteralSystemId);
        }
        str.append(':');
        if (fBaseSystemId != null) {
            str.append(fBaseSystemId);
        }
        str.append(':');
        if (fExpandedSystemId != null) {
            str.append(fExpandedSystemId);
        }
        str.append(':');
        if (fNamespace != null) {
            str.append(fNamespace);
        }
        return str.toString();
    } // toString():String

} // class XMLResourceIdentifierImpl
