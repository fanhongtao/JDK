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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

/**
 * Validator for <gYear> datatype (W3C Schema Datatypes)
 *
 * @author Elena Litani
 * @author Gopal Sharma, SUN Microsystem Inc.
 *
 * @version $Id: YearDV.java,v 1.7 2003/01/16 18:34:04 sandygao Exp $
 */

public class YearDV extends AbstractDateTimeDV {

    /**
     * Convert a string to a compiled form
     *
     * @param  content The lexical representation of time
     * @return a valid and normalized time object
     */
    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException{
        try{
            return new DateTimeData(parse(content), this);
        } catch(Exception ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "gYear"});
        }
    }

    /**
     * Parses, validates and computes normalized version of gYear object
     *
     * @param str    The lexical representation of year object CCYY
     *               with possible time zone Z or (-),(+)hh:mm
     * @param date   uninitialized date object
     * @return normalized date representation
     * @exception SchemaDateTimeException Invalid lexical representation
     */
    protected int[] parse(String str) throws SchemaDateTimeException{
        int len = str.length();
        int[] date = new int[TOTAL_SIZE];
        int[] timeZone = new int[2];

        // check for preceding '-' sign
        int start = 0;
        if (str.charAt(0)=='-') {
            start = 1;
        }
        int sign = findUTCSign(str, start, len);
        if (sign == -1) {
            date[CY]=parseIntYear(str, len);
        }
        else {
            date[CY]=parseIntYear(str, sign);
            getTimeZone (str, date, sign, len, timeZone);
        }

        //initialize values
        date[M]=MONTH;
        date[D]=1;

        //validate and normalize
        validateDateTime(date, timeZone);

        if ( date[utc]!=0 && date[utc]!='Z' ) {
            normalize(date, timeZone);
        }
        return date;
    }

    /**
     * Converts year object representation to String
     *
     * @param date   year object
     * @return lexical representation of month: CCYY with optional time zone sign
     */
    protected String dateToString(int[] date) {
        StringBuffer message = new StringBuffer(5);
        append(message, date[CY], 4);
        append(message, (char)date[utc], 0);
        return message.toString();
    }

}


