/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ls.LSOutput;

import java.io.Writer;
import java.io.OutputStream;

/**
 * This class represents an output destination for data.
 * This interface allows an application to encapsulate information about an
 * output destination in a single object, which may include a URI, a byte stream
 * (possibly with a specifiedencoding), a base URI, and/or a character stream.
 * The exact definitions of a byte stream and a character stream are binding
 * dependent.
 * The application is expected to provide objects that implement this interface
 * whenever such objects are needed. The application can either provide its
 * own objects that implement this interface, or it can use the generic factory
 * method DOMImplementationLS.createLSOutput() to create objects that
 * implement this interface.
 * The DOMSerializer will use the LSOutput object to determine where to
 * serialize the output to. The DOMSerializer will look at the different
 * outputs specified in the LSOutput in the following order to know which one
 * to output to, the first one that data can be output to will be used:
 * 1.LSOutput.characterStream
 * 2.LSOutput.byteStream
 * 3.LSOutput.systemId
 * LSOutput objects belong to the application. The DOM implementation will
 * never modify them (though it may make copies and modify the copies,
 * if necessary).
 *
 *
 * @author Arun Yadav, Sun Microsytems
 * @author Gopal Sharma, Sun Microsystems
 **/

public class DOMOutputImpl implements LSOutput {

        protected Writer fCharStream = null;
        protected OutputStream fByteStream = null;
        protected String fSystemId = null;
        protected String fEncoding = null;

   /**
    * Default Constructor
    */
    public DOMOutputImpl() {}

   /**
    * An attribute of a language and binding dependent type that represents a
    * writable stream of bytes. If the application knows the character encoding
    * of the byte stream, it should set the encoding attribute. Setting the
    * encoding in this way will override any encoding specified in an XML
    * declaration in the data.
    */

    public Writer getCharacterStream(){
        return fCharStream;
     };

   /**
    * An attribute of a language and binding dependent type that represents a
    * writable stream of bytes. If the application knows the character encoding
    * of the byte stream, it should set the encoding attribute. Setting the
    * encoding in this way will override any encoding specified in an XML
    * declaration in the data.
    */

    public void setCharacterStream(Writer characterStream){
        fCharStream = characterStream;
    };

   /**
    * Depending on the language binding in use, this attribute may not be
    * available. An attribute of a language and binding dependent type that
    * represents a writable stream to which 16-bit units can be output. The
    * application must encode the stream using UTF-16 (defined in [Unicode] and
    *  Amendment 1 of [ISO/IEC 10646]).
    */

    public OutputStream getByteStream(){
        return fByteStream;
    };

   /**
    * Depending on the language binding in use, this attribute may not be
    * available. An attribute of a language and binding dependent type that
    * represents a writable stream to which 16-bit units can be output. The
    * application must encode the stream using UTF-16 (defined in [Unicode] and
    *  Amendment 1 of [ISO/IEC 10646]).
    */

    public void setByteStream(OutputStream byteStream){
        fByteStream = byteStream;
    };

   /**
    * The system identifier, a URI reference [IETF RFC 2396], for this output
    *  destination. If the application knows the character encoding of the
    *  object pointed to by the system identifier, it can set the encoding
    *  using the encoding attribute. If the system ID is a relative URI
    *  reference (see section 5 in [IETF RFC 2396]), the behavior is
    *  implementation dependent.
    */

    public String getSystemId(){
        return fSystemId;
    };

   /**
    * The system identifier, a URI reference [IETF RFC 2396], for this output
    *  destination. If the application knows the character encoding of the
    *  object pointed to by the system identifier, it can set the encoding
    *  using the encoding attribute. If the system ID is a relative URI
    *  reference (see section 5 in [IETF RFC 2396]), the behavior is
    *  implementation dependent.
    */

    public void setSystemId(String systemId){
        fSystemId = systemId;
    };

   /**
    * The character encoding, if known. The encoding must be a string
    * acceptable for an XML encoding declaration ([XML 1.0] section 4.3.3
    * "Character Encoding in Entities"). This attribute has no effect when the
    * application provides a character stream or string data. For other sources
    * of input, an encoding specified by means of this attribute will override
    * any encoding specified in the XML declaration or the Text declaration, or
    * an encoding obtained from a higher level protocol, such as HTTP
    * [IETF RFC 2616].
    */

    public String getEncoding(){
        return fEncoding;
    };

   /**
    * The character encoding, if known. The encoding must be a string
    * acceptable for an XML encoding declaration ([XML 1.0] section 4.3.3
    * "Character Encoding in Entities"). This attribute has no effect when the
    * application provides a character stream or string data. For other sources
    * of input, an encoding specified by means of this attribute will override
    * any encoding specified in the XML declaration or the Text declaration, or
    * an encoding obtained from a higher level protocol, such as HTTP
    * [IETF RFC 2616].
    */

    public void setEncoding(String encoding){
        fEncoding = encoding;
    };

}//DOMOutputImpl
