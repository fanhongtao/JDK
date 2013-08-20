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

package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.*;
import com.sun.org.apache.xerces.internal.util.XMLChar;

/**
 * <P>IDDatatypeValidator - ID represents the ID attribute
 * type from XML 1.0 Recommendation. The value space
 * od ID is the set of all strings that match the
 * NCName production and have been used in an XML
 * document. The lexical space of ID is the set of all
 * strings that match the NCName production.</P>
 * <P>The value space of ID is scoped to a specific
 * instance document.</P>
 * <P>The following constraint applies:
 * An ID must not appear more than once in an XML
 * document as a value of this type; i.e., ID values
 * must uniquely identify the elements which bear
 * them.</P>
 * 
 * @author Jeffrey Rodriguez, IBM
 * @author Sandy Gao, IBM
 * 
 * @version $Id: IDDatatypeValidator.java,v 1.7 2003/06/05 21:51:32 neilg Exp $
 */
public class IDDatatypeValidator implements DatatypeValidator {

    // construct an ID datatype validator
    public IDDatatypeValidator() {
    }

    /**
     * Checks that "content" string is valid ID value.
     * If invalid a Datatype validation exception is thrown.
     * 
     * @param content       the string value that needs to be validated
     * @param context       the validation context
     * @throws InvalidDatatypeException if the content is
     *         invalid according to the rules for the validators
     * @see InvalidDatatypeValueException
     */
    public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {

        //Check if is valid key-[81] EncName ::= [A-Za-z] ([A-Za-z0-9._] | '-')*
        if(context.useNamespaces()) {
            if (!XMLChar.isValidNCName(content)) {
                throw new InvalidDatatypeValueException("IDInvalidWithNamespaces", new Object[]{content});
            }
        }
        else {
            if (!XMLChar.isValidName(content)) {
                throw new InvalidDatatypeValueException("IDInvalid", new Object[]{content});
            }
        }

        if (context.isIdDeclared(content)) {
            throw new InvalidDatatypeValueException("IDNotUnique", new Object[]{content});
        }
        
        context.addId(content);
    }
    
}
