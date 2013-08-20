/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

/**
 * Represent the schema type "hexBinary"
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: HexBinaryDV.java,v 1.4 2002/11/18 23:10:10 sandygao Exp $
 */
public class HexBinaryDV extends TypeValidator {

    public short getAllowedFacets(){
        return (XSSimpleTypeDecl.FACET_LENGTH | XSSimpleTypeDecl.FACET_MINLENGTH | XSSimpleTypeDecl.FACET_MAXLENGTH | XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_ENUMERATION | XSSimpleTypeDecl.FACET_WHITESPACE );
    }

    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        byte[] decoded = HexBin.decode(content);
        if (decoded == null)
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "hexBinary"});

        return new XHex(decoded);
    }

    // length of a binary type is the number of bytes
    public int getDataLength(Object value) {
        return ((XHex)value).length();
    }

    private static final class XHex {
        // actually data stored in a byte array
        final byte[] data;
        // canonical representation of the data
        private String canonical;
        public XHex(byte[] data) {
            this.data = data;
        }
        public synchronized String toString() {
            if (canonical == null) {
                canonical = HexBin.encode(data);
            }
            return canonical;
        }
        public int length() {
            return data.length;
        }
        public boolean equals(Object obj) {
            if (!(obj instanceof XHex))
                return false;
            byte[] odata = ((XHex)obj).data;
            int len = data.length;
            if (len != odata.length)
                return false;
            for (int i = 0; i < len; i++) {
                if (data[i] != odata[i])
                    return false;
            }
            return true;
        }
    }
}
