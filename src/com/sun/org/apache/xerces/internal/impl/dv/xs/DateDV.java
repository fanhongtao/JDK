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

/**
 * Validator for <date> datatype (W3C Schema datatypes)
 *
 * @author Elena Litani
 * @author Gopal Sharma, SUN Microsystems Inc.
 *
 * @version $Id: DateDV.java,v 1.9 2003/05/08 20:11:55 elena Exp $
 */
public class DateDV extends DateTimeDV {

    public Object getActualValue(String content) throws InvalidDatatypeValueException {
        try{
            return new DateTimeData(parse(content), this);
        } catch(Exception ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "date"});
        }
    }

    /**
     * Parses, validates and computes normalized version of dateTime object
     *
     * @param str    The lexical representation of dateTime object CCYY-MM-DD
     *               with possible time zone Z or (-),(+)hh:mm
     * @param date   uninitialized date object
     * @return normalized dateTime representation
     * @exception SchemaDateTimeException Invalid lexical representation
     */
    protected int[] parse(String str) throws SchemaDateTimeException{
        int len = str.length();
        int[] date = new int[TOTAL_SIZE];
        int[] timeZone = new int[2];

        int end = getDate(str, 0, len, date);
        parseTimeZone (str, end, len, date, timeZone);

        //validate and normalize
        //REVISIT: do we need SchemaDateTimeException?
        validateDateTime(date, timeZone);

        if ( date[utc]!=0 && date[utc]!='Z' ) {
            normalize(date, timeZone);
        }
        return date;
    }

    protected String dateToString(int[] date) {
        StringBuffer message = new StringBuffer(25);
        append(message, date[CY], 4);
        message.append('-');
        append(message, date[M], 2);
        message.append('-');
        append(message, date[D], 2);
        append(message, (char)date[utc], 0);
        return message.toString();
    }

}
