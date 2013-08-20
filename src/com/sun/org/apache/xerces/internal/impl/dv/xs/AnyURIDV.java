/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

/**
 * Represent the schema type "anyURI"
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: AnyURIDV.java,v 1.5 2004/01/20 17:01:53 sandygao Exp $
 */
public class AnyURIDV extends TypeValidator {

    private static final URI BASE_URI;
    static {
        URI uri = null;
        try {
            uri = new URI("abc://def.ghi.jkl");
        } catch (URI.MalformedURIException ex) {
        }
        BASE_URI = uri;
    }

    public short getAllowedFacets(){
        return (XSSimpleTypeDecl.FACET_LENGTH | XSSimpleTypeDecl.FACET_MINLENGTH | XSSimpleTypeDecl.FACET_MAXLENGTH | XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_ENUMERATION | XSSimpleTypeDecl.FACET_WHITESPACE );
    }

    // before we return string we have to make sure it is correct URI as per spec.
    // for some types (string and derived), they just return the string itself
    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        // check 3.2.17.c0 must: URI (rfc 2396/2723)
        try {
            if( content.length() != 0 ) {
                // encode special characters using XLink 5.4 algorithm
                content = encode(content);
                // Support for relative URLs
                // According to Java 1.1: URLs may also be specified with a
                // String and the URL object that it is related to.
                new URI(BASE_URI, content );
            }
        } catch (URI.MalformedURIException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "anyURI"});
        }

        // REVISIT: do we need to return the new URI object?
        return content;
    }

    // which ASCII characters need to be escaped
    private static boolean gNeedEscaping[] = new boolean[128];
    // the first hex character if a character needs to be escaped
    private static char gAfterEscaping1[] = new char[128];
    // the second hex character if a character needs to be escaped
    private static char gAfterEscaping2[] = new char[128];
    private static char[] gHexChs = {'0', '1', '2', '3', '4', '5', '6', '7',
                                     '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    // initialize the above 3 arrays
    static {
        for (int i = 0; i <= 0x1f; i++) {
            gNeedEscaping[i] = true;
            gAfterEscaping1[i] = gHexChs[i >> 4];
            gAfterEscaping2[i] = gHexChs[i & 0xf];
        }
        gNeedEscaping[0x7f] = true;
        gAfterEscaping1[0x7f] = '7';
        gAfterEscaping2[0x7f] = 'F';
        char[] escChs = {' ', '<', '>', '"', '{', '}',
                         '|', '\\', '^', '~', '`'};
        int len = escChs.length;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = escChs[i];
            gNeedEscaping[ch] = true;
            gAfterEscaping1[ch] = gHexChs[ch >> 4];
            gAfterEscaping2[ch] = gHexChs[ch & 0xf];
        }
    }

    // To encode special characters in anyURI, by using %HH to represent
    // special ASCII characters: 0x00~0x1F, 0x7F, ' ', '<', '>', etc.
    // and non-ASCII characters (whose value >= 128).
    private static String encode(String anyURI){
        int len = anyURI.length(), ch;
        StringBuffer buffer = new StringBuffer(len*3);

        // for each character in the anyURI
        int i = 0;
        for (; i < len; i++) {
            ch = anyURI.charAt(i);
            // if it's not an ASCII character, break here, and use UTF-8 encoding
            if (ch >= 128)
                break;
            if (gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(gAfterEscaping1[ch]);
                buffer.append(gAfterEscaping2[ch]);
            }
            else {
                buffer.append((char)ch);
            }
        }

        // we saw some non-ascii character
        if (i < len) {
            // get UTF-8 bytes for the remaining sub-string
            byte[] bytes = null;
            byte b;
            try {
                bytes = anyURI.substring(i).getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                // should never happen
                return anyURI;
            }
            len = bytes.length;

            // for each byte
            for (i = 0; i < len; i++) {
                b = bytes[i];
                // for non-ascii character: make it positive, then escape
                if (b < 0) {
                    ch = b + 256;
                    buffer.append('%');
                    buffer.append(gHexChs[ch >> 4]);
                    buffer.append(gHexChs[ch & 0xf]);
                }
                else if (gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(gAfterEscaping1[b]);
                    buffer.append(gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }

        // If encoding happened, create a new string;
        // otherwise, return the orginal one.
        if (buffer.length() != len)
            return buffer.toString();
        else
            return anyURI;
    }

} // class AnyURIDV
