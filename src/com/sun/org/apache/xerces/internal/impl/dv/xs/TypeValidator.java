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

/**
 * All primitive types plus ID/IDREF/ENTITY/INTEGER are derived from this abstract
 * class. It provides extra information XSSimpleTypeDecl requires from each
 * type: allowed facets, converting String to actual value, check equality,
 * comparison, etc.
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: TypeValidator.java,v 1.5 2002/11/18 23:10:10 sandygao Exp $
 */
public abstract class TypeValidator {

    // which facets are allowed for this type
    public abstract short getAllowedFacets();

    // convert a string to an actual value. for example,
    // for number types (decimal, double, float, and types derived from them),
    // get the BigDecimal, Double, Flout object.
    // for some types (string and derived), they just return the string itself
    public abstract Object getActualValue(String content, ValidationContext context)
        throws InvalidDatatypeValueException;

    // for ID/IDREF/ENTITY types, do some extra checking after the value is
    // checked to be valid with respect to both lexical representation and
    // facets
    public void checkExtraRules(Object value, ValidationContext context) throws InvalidDatatypeValueException {
    }

    // the following methods might not be supported by every DV.
    // but XSSimpleTypeDecl should know which type supports which methods,
    // and it's an *internal* error if a method is called on a DV that
    // doesn't support it.

    //order constants
    public static final short LESS_THAN     = -1;
    public static final short EQUAL         = 0;
    public static final short GREATER_THAN  = 1;
    public static final short INDETERMINATE = 2;

    // check the order relation between the two values
    // the parameters are in compiled form (from getActualValue)
    public int compare(Object value1, Object value2) {
        return -1;
    }

    // get the length of the value
    // the parameters are in compiled form (from getActualValue)
    public int getDataLength(Object value) {
        return (value instanceof String) ? ((String)value).length() : -1;
    }

    // get the number of digits of the value
    // the parameters are in compiled form (from getActualValue)
    public int getTotalDigits(Object value) {
        return -1;
    }

    // get the number of fraction digits of the value
    // the parameters are in compiled form (from getActualValue)
    public int getFractionDigits(Object value) {
        return -1;
    }

    // check whether the character is in the range 0x30 ~ 0x39
    public static final boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    // if the character is in the range 0x30 ~ 0x39, return its int value (0~9),
    // otherwise, return -1
    public static final int getDigit(char ch) {
        return isDigit(ch) ? ch - '0' : -1;
    }
    
} // interface TypeValidator
