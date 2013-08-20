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
 * Validator for <gMonth> datatype (W3C Schema Datatypes)
 *
 * @author Elena Litani
 * @author Gopal Sharma, SUN Microsystem Inc.
 *
 * @version $Id: MonthDV.java,v 1.9 2003/01/16 18:34:04 sandygao Exp $
 */

public class MonthDV extends AbstractDateTimeDV {

    /**
     * Convert a string to a compiled form
     *
     * @param  content The lexical representation of gMonth
     * @return a valid and normalized gMonth object
     */
    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException{
        try{
            return new DateTimeData(parse(content), this);
        } catch(Exception ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "gMonth"});
        }
    }

    /**
     * Parses, validates and computes normalized version of gMonth object
     *
     * @param str    The lexical representation of gMonth object --MM
     *               with possible time zone Z or (-),(+)hh:mm
     * @param date   uninitialized date object
     * @return normalized date representation
     * @exception SchemaDateTimeException Invalid lexical representation
     */
    protected int[] parse(String str) throws SchemaDateTimeException{
        int len = str.length();
        int[] date=new int[TOTAL_SIZE];
        int[] timeZone = new int[2];

        //set constants
        date[CY]=YEAR;
        date[D]=DAY;
        if (str.charAt(0)!='-' || str.charAt(1)!='-') {
            throw new SchemaDateTimeException("Invalid format for gMonth: "+str);
        }
        int stop = 4;
        date[M]=parseInt(str,2,stop);

        // REVISIT: allow both --MM and --MM-- now.
        // need to remove the following 4 lines to disallow --MM--
        // when the errata is offically in the rec.
        if (str.length() >= stop+2 &&
            str.charAt(stop) == '-' && str.charAt(stop+1) == '-') {
            stop += 2;
        }
        if (stop < len) {
            int sign = findUTCSign(str, stop, len);
            if ( sign<0 ) {
                throw new SchemaDateTimeException ("Error in month parsing: "+str);
            }
            else {
                getTimeZone(str, date, sign, len, timeZone);
            }
        }
        //validate and normalize
        validateDateTime(date, timeZone);

        if ( date[utc]!=0 && date[utc]!='Z' ) {
            normalize(date, timeZone);
        }
        return date;
    }

    /**
     * Overwrite compare algorithm to optimize month comparison
     *
     * REVISIT: this one is lack of the third parameter: boolean strict, so it
     *          doesn't override the method in the base. But maybe this method
     *          is not correctly implemented, and I did encounter errors when
     *          trying to add the extra parameter. I'm leaving it as is. -SG
     *
     * @param date1
     * @param date2
     * @return less, greater, equal, indeterminate
     */
    protected  short compareDates(int[] date1, int[] date2) {

        if ( date1[utc]==date2[utc] ) {
            return (short)((date1[M]>=date2[M])?(date1[M]>date2[M])?1:0:-1);
        }

        if ( date1[utc]=='Z' || date2[utc]=='Z' ) {

            if ( date1[M]==date2[M] ) {
                //--05--Z and --05--
                return INDETERMINATE;
            }
            if ( (date1[M]+1 == date2[M] || date1[M]-1 == date2[M]) ) {
                //--05--Z and (--04-- or --05--)
                //REVISIT: should this case be less than or equal?
                //         maxExclusive should fail but what about maxInclusive
                //
                return INDETERMINATE;
            }
        }

        if ( date1[M]<date2[M] ) {
            return -1;
        }
        else {
            return 1;
        }

    }

    /**
     * Converts month object representation to String
     *
     * @param date   month object
     * @return lexical representation of month: --MM with an optional time zone sign
     */
    protected String dateToString(int[] date) {
        StringBuffer message = new StringBuffer(5);
        message.append('-');
        message.append('-');
        append(message, date[M], 2);
        append(message, (char)date[utc], 0);
        return message.toString();
    }

}
