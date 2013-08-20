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


package com.sun.org.apache.xml.internal.serialize;


import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Locale;

import com.sun.org.apache.xerces.internal.util.EncodingMap;


/**
 * Provides information about encodings. Depends on the Java runtime
 * to provides writers for the different encodings, but can be used
 * to override encoding names and provide the last printable character
 * for each encoding.
 *
 * @version $Id: Encodings.java,v 1.8 2003/07/18 16:47:22 mrglavas Exp $
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 */
public class Encodings
{


    /**
     * The last printable character for unknown encodings.
     */
    static final int DEFAULT_LAST_PRINTABLE = 0x7F;

    // last printable character for Unicode-compatible encodings
    static final int LAST_PRINTABLE_UNICODE = 0xffff;
    // unicode-compliant encodings; can express plane 0
    static final String[] UNICODE_ENCODINGS = {
        "Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", 
    };
    // default (Java) encoding if none supplied:
    static final String DEFAULT_ENCODING = "UTF8";

    // note that the size of this Hashtable
    // is bounded by the number of encodings recognized by EncodingMap;
    // therefore it poses no static mutability risk.
    static Hashtable _encodings = new Hashtable();

    /**
     * @param encoding a MIME charset name, or null.
     */
    static EncodingInfo getEncodingInfo(String encoding, boolean allowJavaNames) throws UnsupportedEncodingException {
        EncodingInfo eInfo = null;
        if (encoding == null) {
            if((eInfo = (EncodingInfo)_encodings.get(DEFAULT_ENCODING)) != null) 
                return eInfo;
            eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(DEFAULT_ENCODING), DEFAULT_ENCODING, LAST_PRINTABLE_UNICODE);
            _encodings.put(DEFAULT_ENCODING, eInfo);
            return eInfo;
        }
        // need to convert it to upper case:
        encoding = encoding.toUpperCase(Locale.ENGLISH);
        String jName = EncodingMap.getIANA2JavaMapping(encoding);
        if(jName == null) {
            // see if the encoding passed in is a Java encoding name.
            if(allowJavaNames ) {
                EncodingInfo.testJavaEncodingName(encoding);
                if((eInfo = (EncodingInfo)_encodings.get(encoding)) != null) 
                    return eInfo;
                // is it known to be unicode-compliant?
                int i=0;
                for(; i<UNICODE_ENCODINGS.length; i++) {
                    if(UNICODE_ENCODINGS[i].equalsIgnoreCase(encoding)) {
                        eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, LAST_PRINTABLE_UNICODE);
                        break;
                    }
                }
                if(i == UNICODE_ENCODINGS.length) {
                    eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, DEFAULT_LAST_PRINTABLE);
                }
                _encodings.put(encoding, eInfo); 
                return eInfo;
            } else {
                throw new UnsupportedEncodingException(encoding);
            }
        }
        if ((eInfo = (EncodingInfo)_encodings.get(jName)) != null)
            return eInfo;
        // have to create one...
        // is it known to be unicode-compliant?
        int i=0;
        for(; i<UNICODE_ENCODINGS.length; i++) {
            if(UNICODE_ENCODINGS[i].equalsIgnoreCase(jName)) {
                eInfo = new EncodingInfo(encoding, jName, LAST_PRINTABLE_UNICODE);
                break;
            }
        }
        if(i == UNICODE_ENCODINGS.length) {
            eInfo = new EncodingInfo(encoding, jName, DEFAULT_LAST_PRINTABLE);
        }
        _encodings.put(jName, eInfo); 
        return eInfo;
    }

    static final String JIS_DANGER_CHARS
    = "\\\u007e\u007f\u00a2\u00a3\u00a5\u00ac"
    +"\u2014\u2015\u2016\u2026\u203e\u203e\u2225\u222f\u301c"
    +"\uff3c\uff5e\uffe0\uffe1\uffe2\uffe3";

}
