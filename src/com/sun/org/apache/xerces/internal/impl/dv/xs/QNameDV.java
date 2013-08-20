/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

/**
 * Represent the schema type "QName" and "NOTATION"
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: QNameDV.java,v 1.6 2003/02/26 16:24:59 sandygao Exp $
 */
public class QNameDV extends TypeValidator {

    private static final String EMPTY_STRING = "".intern();

    public short getAllowedFacets() {
        return (XSSimpleTypeDecl.FACET_LENGTH | XSSimpleTypeDecl.FACET_MINLENGTH | XSSimpleTypeDecl.FACET_MAXLENGTH | XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_ENUMERATION | XSSimpleTypeDecl.FACET_WHITESPACE);
    }

    public Object getActualValue(String content, ValidationContext context)
        throws InvalidDatatypeValueException {

        // "prefix:localpart" or "localpart"
        // get prefix and local part out of content
        String prefix, localpart;
        int colonptr = content.indexOf(":");
        if (colonptr > 0) {
            prefix = context.getSymbol(content.substring(0,colonptr));
            localpart = content.substring(colonptr+1);
        } else {
            prefix = EMPTY_STRING;
            localpart = content;
        }

        // both prefix (if any) a nd localpart must be valid NCName
        if (prefix.length() > 0 && !XMLChar.isValidNCName(prefix))
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "QName"});

        if(!XMLChar.isValidNCName(localpart))
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "QName"});

        // resove prefix to a uri, report an error if failed
        String uri = context.getURI(prefix);
        if (prefix.length() > 0 && uri == null)
            throw new InvalidDatatypeValueException("UndeclaredPrefix", new Object[]{content, prefix});

        return new XQName(prefix, context.getSymbol(localpart), context.getSymbol(content), uri);

    }

    // REVISIT: qname and notation shouldn't support length facets.
    //          now we just return the length of the rawname
    public int getDataLength(Object value) {
        return ((XQName)value).rawname.length();
    }

    /**
     * represent QName data
     */
    private static final class XQName extends QName {
        /** Constructs a QName with the specified values. */
        public XQName(String prefix, String localpart, String rawname, String uri) {
            setValues(prefix, localpart, rawname, uri);
        } // <init>(String,String,String,String)

        /** Returns true if the two objects are equal. */
        public boolean equals(Object object) {
            if (object instanceof QName) {
                QName qname = (QName)object;
                return uri == qname.uri && localpart == qname.localpart;
            }
            return false;
        } // equals(Object):boolean

        public synchronized String toString() {
            return rawname;
        }
    }
} // class QNameDVDV
