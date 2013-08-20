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
 * Represent the schema type "decimal"
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: DecimalDV.java,v 1.9 2003/05/08 20:11:55 elena Exp $
 */
public class DecimalDV extends TypeValidator {

    public final short getAllowedFacets(){
        return ( XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_WHITESPACE | XSSimpleTypeDecl.FACET_ENUMERATION |XSSimpleTypeDecl.FACET_MAXINCLUSIVE |XSSimpleTypeDecl.FACET_MININCLUSIVE | XSSimpleTypeDecl.FACET_MAXEXCLUSIVE  | XSSimpleTypeDecl.FACET_MINEXCLUSIVE | XSSimpleTypeDecl.FACET_TOTALDIGITS | XSSimpleTypeDecl.FACET_FRACTIONDIGITS);
    }

    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return new XDecimal(content);
        } catch (NumberFormatException nfe) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "decimal"});
        }
    }

    public final int compare(Object value1, Object value2){
        return ((XDecimal)value1).compareTo((XDecimal)value2);
    }

    public final int getTotalDigits(Object value){
        return ((XDecimal)value).totalDigits;
    }

    public final int getFractionDigits(Object value){
        return ((XDecimal)value).fracDigits;
    }
    
    // Avoid using the heavy-weight java.math.BigDecimal
    static class XDecimal {
        // sign: 0 for vlaue 0; 1 for positive values; -1 for negative values
        int sign = 1;
        // total digits. >= 1
        int totalDigits = 0;
        // integer digits when sign != 0
        int intDigits = 0;
        // fraction digits when sign != 0
        int fracDigits = 0;
        // the string representing the integer part
        String ivalue = "";
        // the string representing the fraction part
        String fvalue = "";
        // whether the canonical form contains decimal point
        boolean integer = false;
        
        XDecimal(String content) throws NumberFormatException {
            initD(content);
        }
        XDecimal(String content, boolean integer) throws NumberFormatException {
            if (integer)
                initI(content);
            else
                initD(content);
        }
        void initD(String content) throws NumberFormatException {
            int len = content.length();
            if (len == 0)
                throw new NumberFormatException();
    
            // these 4 variables are used to indicate where the integre/fraction
            // parts start/end.
            int intStart = 0, intEnd = 0, fracStart = 0, fracEnd = 0;
            
            // Deal with leading sign symbol if present
            if (content.charAt(0) == '+') {
                // skip '+', so intStart should be 1
                intStart = 1;
            }
            else if (content.charAt(0) == '-') {
                // keep '-', so intStart is stil 0
                intStart = 1;
                sign = -1;
            }
    
            // skip leading zeroes in integer part
            int actualIntStart = intStart;
            while (actualIntStart < len && content.charAt(actualIntStart) == '0') {
                actualIntStart++;
            }
    
            // Find the ending position of the integer part
            for (intEnd = actualIntStart;
                 intEnd < len && TypeValidator.isDigit(content.charAt(intEnd));
                 intEnd++);
    
            // Not reached the end yet
            if (intEnd < len) {
                // the remaining part is not ".DDD", error
                if (content.charAt(intEnd) != '.')
                    throw new NumberFormatException();
    
                // fraction part starts after '.', and ends at the end of the input
                fracStart = intEnd + 1;
                fracEnd = len;
            }
    
            // no integer part, no fraction part, error.
            if (intStart == intEnd && fracStart == fracEnd)
                throw new NumberFormatException();
    
            // ignore trailing zeroes in fraction part
            while (fracEnd > fracStart && content.charAt(fracEnd-1) == '0') {
                fracEnd--;
            }
    
            // check whether there is non-digit characters in the fraction part
            for (int fracPos = fracStart; fracPos < fracEnd; fracPos++) {
                if (!TypeValidator.isDigit(content.charAt(fracPos)))
                    throw new NumberFormatException();
            }
    
            intDigits = intEnd - actualIntStart;
            fracDigits = fracEnd - fracStart;
            totalDigits = intDigits + fracDigits;
    
            if (intDigits > 0) {
                ivalue = content.substring(actualIntStart, intEnd);
                if (fracDigits > 0)
                    fvalue = content.substring(fracStart, fracEnd);
            }
            else {
                if (fracDigits > 0) {
                    fvalue = content.substring(fracStart, fracEnd);
                }
                else {
                    // ".00", treat it as "0"
                    sign = 0;
                }
            }
        }
        void initI(String content) throws NumberFormatException {
            int len = content.length();
            if (len == 0)
                throw new NumberFormatException();
    
            // these 2 variables are used to indicate where the integre start/end.
            int intStart = 0, intEnd = 0;
    
            // Deal with leading sign symbol if present
            if (content.charAt(0) == '+') {
                // skip '+', so intStart should be 1
                intStart = 1;
            }
            else if (content.charAt(0) == '-') {
                // keep '-', so intStart is stil 0
                intStart = 1;
                sign = -1;
            }
    
            // skip leading zeroes in integer part
            int actualIntStart = intStart;
            while (actualIntStart < len && content.charAt(actualIntStart) == '0') {
                actualIntStart++;
            }
    
            // Find the ending position of the integer part
            for (intEnd = actualIntStart;
                 intEnd < len && TypeValidator.isDigit(content.charAt(intEnd));
                 intEnd++);
    
            // Not reached the end yet, error
            if (intEnd < len)
                throw new NumberFormatException();
    
            // no integer part, error.
            if (intStart == intEnd)
                throw new NumberFormatException();
    
            intDigits = intEnd - actualIntStart;
            fracDigits = 0;
            totalDigits = intDigits;
    
            if (intDigits > 0) {
                ivalue = content.substring(actualIntStart, intEnd);
            }
            else {
                // "00", treat it as "0"
                sign = 0;
            }
            
            integer = true;
        }
        public boolean equals(Object val) {
            if (val == this)
                return true;
    
            if (!(val instanceof XDecimal))
                return false;
            XDecimal oval = (XDecimal)val;
            
            if (sign != oval.sign)
               return false;
            if (sign == 0)
                return true;
            
            return intDigits == oval.intDigits && fracDigits == oval.fracDigits &&
                   ivalue.equals(oval.ivalue) && fvalue.equals(oval.fvalue);
        }
        public int compareTo(XDecimal val) {
            if (sign != val.sign)
                return sign > val.sign ? 1 : -1;
            if (sign == 0)
                return 0;
            return sign * intComp(val);
        }
        private int intComp(XDecimal val) {
            if (intDigits != val.intDigits)
                return intDigits > val.intDigits ? 1 : -1;
            int ret = ivalue.compareTo(val.ivalue);
            if (ret != 0)
                return ret > 0 ? 1 : -1;;
            ret = fvalue.compareTo(val.fvalue);
            return ret == 0 ? 0 : (ret > 0 ? 1 : -1);
        }
        private String canonical;
        public synchronized String toString() {
            if (canonical == null) {
                makeCanonical();
            }
            return canonical;
        }
        
        private void makeCanonical() {
            if (sign == 0) {
                if (integer)
                    canonical = "0";
                else
                    canonical = "0.0";
                return;
            }
            if (integer && sign > 0) {
                canonical = ivalue;
                return;
            }
            // for -0.1, total digits is 1, so we need 3 extra spots
            StringBuffer buffer = new StringBuffer(totalDigits+3);
            if (sign == -1)
                buffer.append('-');
            if (intDigits != 0)
                buffer.append(ivalue);
            else
                buffer.append('0');
            if (!integer) {
                buffer.append('.');
                if (fracDigits != 0) {
                    buffer.append(fvalue);
                }
                else {
                    buffer.append('0');
                }
            }
            canonical = buffer.toString();
        }
    }
} // class DecimalDV

