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
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

/**
 * Represent the schema type "double"
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: DoubleDV.java,v 1.6 2003/02/17 13:45:57 sandygao Exp $
 */
public class DoubleDV extends TypeValidator {

    public short getAllowedFacets(){
        return ( XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_WHITESPACE | XSSimpleTypeDecl.FACET_ENUMERATION |XSSimpleTypeDecl.FACET_MAXINCLUSIVE |XSSimpleTypeDecl.FACET_MININCLUSIVE | XSSimpleTypeDecl.FACET_MAXEXCLUSIVE  | XSSimpleTypeDecl.FACET_MINEXCLUSIVE  );
    }//getAllowedFacets()

    //convert a String to Double form, we have to take care of cases specified in spec like INF, -INF and NaN
    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        try{
            return new XDouble(content);
        } catch (NumberFormatException ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "double"});
        }
    }//getActualValue()

    // Can't call Double#compareTo method, because it's introduced in jdk 1.2
    public int compare(Object value1, Object value2) {
        return ((XDouble)value1).compareTo((XDouble)value2);
    }//compare()

    private static final class XDouble {
        private double value;
        public XDouble(String s) throws NumberFormatException {
            try {
                value = Double.parseDouble(s);
            }
            catch ( NumberFormatException nfe ) {
                if ( s.equals("INF") ) {
                    value = Double.POSITIVE_INFINITY;
                }
                else if ( s.equals("-INF") ) {
                    value = Double.NEGATIVE_INFINITY;
                }
                else if ( s.equals("NaN" ) ) {
                    value = Double.NaN;
                }
                else {
                    throw nfe;
                }
            }
        }

        public boolean equals(Object val) {
            if (val == this)
                return true;
    
            if (!(val instanceof XDouble))
                return false;
            XDouble oval = (XDouble)val;

            // NOTE: we don't distinguish 0.0 from -0.0
            if (value == oval.value)
                return true;
            
            if (value != value && oval.value != oval.value)
                return true;

            return false;
        }

        private int compareTo(XDouble val) {
            double oval = val.value;

            // this < other
            if (value < oval)
                return -1;
            // this > other
            if (value > oval)
                return 1;
            // this == other
            // NOTE: we don't distinguish 0.0 from -0.0
            if (value == oval)
                return 0;

            // one of the 2 values or both is/are NaN(s)

            if (value != value) {
                // this = NaN = other
                if (oval != oval)
                    return 0;
                // this is NaN <> other
                return INDETERMINATE;
            }

            // other is NaN <> this
            return INDETERMINATE;
        }

        private String canonical;
        public synchronized String toString() {
            if (canonical == null) {
                if (value == Double.POSITIVE_INFINITY)
                    canonical = "INF";
                else if (value == Double.NEGATIVE_INFINITY)
                    canonical = "-INF";
                else if (value != value)
                    canonical = "NaN";
                // NOTE: we don't distinguish 0.0 from -0.0
                else if (value == 0)
                    canonical = "0.0E1";
                else {
                    // REVISIT: use the java algorithm for now, because we
                    // don't know what to output for 1.1d (which is no
                    // actually 1.1)
                    canonical = Double.toString(value);
                    // if it contains 'E', then it should be a valid schema
                    // canonical representation
                    if (canonical.indexOf('E') == -1) {
                        int len = canonical.length();
                        // at most 3 longer: E, -, 9
                        char[] chars = new char[len+3];
                        canonical.getChars(0, len, chars, 0);
                        // expected decimal point position
                        int edp = chars[0] == '-' ? 2 : 1;
                        // for non-zero integer part
                        if (value >= 1 || value <= -1) {
                            // decimal point position
                            int dp = canonical.indexOf('.');
                            // move the digits: ddd.d --> d.ddd
                            for (int i = dp; i > edp; i--) {
                                chars[i] = chars[i-1];
                            }
                            chars[edp] = '.';
                            // trim trailing zeros: d00.0 --> d.000 --> d.
                            while (chars[len-1] == '0')
                                len--;
                            // add the last zero if necessary: d. --> d.0
                            if (chars[len-1] == '.')
                                len++;
                            // append E: d.dd --> d.ddE
                            chars[len++] = 'E';
                            // how far we shifted the decimal point
                            int shift = dp - edp;
                            // append the exponent --> d.ddEd
                            // the exponent is at most 7
                            chars[len++] = (char)(shift + '0');
                        }
                        else {
                            // non-zero digit point
                            int nzp = edp + 1;
                            // skip zeros: 0.003
                            while (chars[nzp] == '0')
                                nzp++;
                            // put the first non-zero digit to the left of '.'
                            chars[edp-1] = chars[nzp];
                            chars[edp] = '.';
                            // move other digits (non-zero) to the right of '.'
                            for (int i = nzp+1, j = edp+1; i < len; i++, j++)
                                chars[j] = chars[i];
                            // adjust the length
                            len -= nzp - edp;
                            // append 0 if nessary: 0.03 --> 3. --> 3.0
                            if (len == edp + 1)
                                chars[len++] = '0';
                            // append E-: d.dd --> d.ddE-
                            chars[len++] = 'E';
                            chars[len++] = '-';
                            // how far we shifted the decimal point
                            int shift = nzp - edp;
                            // append the exponent --> d.ddEd
                            // the exponent is at most 3
                            chars[len++] = (char)(shift + '0');
                        }
                        canonical = new String(chars, 0, len);
                    }
                }
            }
            return canonical;
        }
    }
} // class DoubleDV
