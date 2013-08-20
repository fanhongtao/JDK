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

package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.XMLInputSourceAdaptor;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.stream.StreamSource;

/**
 * This class represents an input source for an XML document. The
 * basic properties of an input source are the following:
 * <ul>
 *  <li>public identifier</li>
 *  <li>system identifier</li>
 *  <li>byte stream or character stream</li>
 *  <li>
 * </ul>
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLInputSource.java,v 1.4 2002/01/29 01:15:19 lehors Exp $
 */
public class XMLInputSource {

    //
    // Data
    //

    /** Public identifier. */
    protected String fPublicId;

    /** System identifier. */
    protected String fSystemId;

    /** Base system identifier. */
    protected String fBaseSystemId;

    /** Byte stream. */
    protected InputStream fByteStream;

    /** Character stream. */
    protected Reader fCharStream;

    /** Encoding. */
    protected String fEncoding;

    //
    // Constructors
    //

    /** 
     * Constructs an input source from just the public and system
     * identifiers, leaving resolution of the entity and opening of
     * the input stream up to the caller.
     *
     * @param publicId     The public identifier, if known.
     * @param systemId     The system identifier. This value should
     *                     always be set, if possible, and can be
     *                     relative or absolute. If the system identifier
     *                     is relative, then the base system identifier
     *                     should be set.
     * @param baseSystemId The base system identifier. This value should
     *                     always be set to the fully expanded URI of the
     *                     base system identifier, if possible.
     */
    public XMLInputSource(String publicId, String systemId,  
                          String baseSystemId) {
        fPublicId = publicId;
        fSystemId = systemId;
        fBaseSystemId = baseSystemId;
    } // <init>(String,String,String)

    /** 
     * Constructs an input source from a XMLResourceIdentifier
     * object, leaving resolution of the entity and opening of
     * the input stream up to the caller.
     *
     * @param resourceIdentifier    the XMLResourceIdentifier containing the information
     */
    public XMLInputSource(XMLResourceIdentifier resourceIdentifier) {

        fPublicId = resourceIdentifier.getPublicId();
        fSystemId = resourceIdentifier.getLiteralSystemId();
        fBaseSystemId = resourceIdentifier.getBaseSystemId();
    } // <init>(XMLResourceIdentifier)

    /**
     * Constructs an input source from a byte stream.
     *
     * @param publicId     The public identifier, if known.
     * @param systemId     The system identifier. This value should
     *                     always be set, if possible, and can be
     *                     relative or absolute. If the system identifier
     *                     is relative, then the base system identifier
     *                     should be set.
     * @param baseSystemId The base system identifier. This value should
     *                     always be set to the fully expanded URI of the
     *                     base system identifier, if possible.
     * @param byteStream   The byte stream.
     * @param encoding     The encoding of the byte stream, if known.
     */
    public XMLInputSource(String publicId, String systemId,  
                          String baseSystemId, InputStream byteStream,
                          String encoding) {
        fPublicId = publicId;
        fSystemId = systemId;
        fBaseSystemId = baseSystemId;
        fByteStream = byteStream;
        fEncoding = encoding;
    } // <init>(String,String,String,InputStream,String)

    /**
     * Constructs an input source from a character stream.
     *
     * @param publicId     The public identifier, if known.
     * @param systemId     The system identifier. This value should
     *                     always be set, if possible, and can be
     *                     relative or absolute. If the system identifier
     *                     is relative, then the base system identifier
     *                     should be set.
     * @param baseSystemId The base system identifier. This value should
     *                     always be set to the fully expanded URI of the
     *                     base system identifier, if possible.
     * @param charStream   The character stream.
     * @param encoding     The original encoding of the byte stream
     *                     used by the reader, if known.
     */
    public XMLInputSource(String publicId, String systemId,  
                          String baseSystemId, Reader charStream,
                          String encoding) {
        fPublicId = publicId;
        fSystemId = systemId;
        fBaseSystemId = baseSystemId;
        fCharStream = charStream;
        fEncoding = encoding;
    } // <init>(String,String,String,Reader,String)

    /**
     * Constructs an input source from {@link StreamSource}.
     */
    public XMLInputSource( StreamSource source ) {
        fPublicId = source.getPublicId();
        fSystemId = source.getSystemId();
        fCharStream = source.getReader();
        fByteStream = source.getInputStream();
    }
    
    //
    // Public methods
    //

    /** 
     * Sets the public identifier. 
     *
     * @param publicId The new public identifier.
     */
    public void setPublicId(String publicId) {
        fPublicId = publicId;
    } // setPublicId(String)

    /** Returns the public identifier. */
    public String getPublicId() {
        return fPublicId;
    } // getPublicId():String

    /** 
     * Sets the system identifier. 
     *
     * @param systemId The new system identifier.
     */
    public void setSystemId(String systemId) {
        fSystemId = systemId;
    } // setSystemId(String)

    /** Returns the system identifier. */
    public String getSystemId() {
        return fSystemId;
    } // getSystemId():String

    /** 
     * Sets the base system identifier. 
     *
     * @param baseSystemId The new base system identifier.
     */
    public void setBaseSystemId(String baseSystemId) {
        fBaseSystemId = baseSystemId;
    } // setBaseSystemId(String)

    /** Returns the base system identifier. */
    public String getBaseSystemId() {
        return fBaseSystemId;
    } // getBaseSystemId():String

    /**
     * Sets the byte stream. If the byte stream is not already opened
     * when this object is instantiated, then the code that opens the
     * stream should also set the byte stream on this object. Also, if
     * the encoding is auto-detected, then the encoding should also be
     * set on this object.
     *
     * @param byteStream The new byte stream.
     */
    public void setByteStream(InputStream byteStream) {
        fByteStream = byteStream;
    } // setByteStream(InputSource)

    /** Returns the byte stream. */
    public InputStream getByteStream() {
        return fByteStream;
    } // getByteStream():InputStream

    /**
     * Sets the character stream. If the character stream is not already
     * opened when this object is instantiated, then the code that opens 
     * the stream should also set the character stream on this object. 
     * Also, the encoding of the byte stream used by the reader should 
     * also be set on this object, if known.
     *
     * @param charStream The new character stream.
     *
     * @see #setEncoding
     */
    public void setCharacterStream(Reader charStream) {
        fCharStream = charStream;
    } // setCharacterStream(Reader)

    /** Returns the character stream. */
    public Reader getCharacterStream() {
        return fCharStream;
    } // getCharacterStream():Reader

    /**
     * Sets the encoding of the stream.
     *
     * @param encoding The new encoding.
     */
    public void setEncoding(String encoding) {
        fEncoding = encoding;
    } // setEncoding(String)

    /** Returns the encoding of the stream, or null if not known. */
    public String getEncoding() {
        return fEncoding;
    } // getEncoding():String
    
    /**
     * Wraps this object into a {@link Source} object.
     */
    public final XMLInputSourceAdaptor toSource() {
        return new XMLInputSourceAdaptor(this);
    }
} // class XMLInputSource
