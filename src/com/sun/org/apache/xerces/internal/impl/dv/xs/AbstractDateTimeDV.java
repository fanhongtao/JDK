/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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

/**
 * This is the base class of all date/time datatype validators.
 * It implements common code for parsing, validating and comparing datatypes.
 * Classes that extend this class, must implement parse() method.
 *
 * REVISIT: There are many instance variables, which would cause problems
 *          when we support grammar caching. A grammar is possibly used by
 *          two parser instances at the same time, then the same simple type
 *          decl object can be used to validate two strings at the same time.
 *          -SG
 *
 * @author Elena Litani
 * @author Len Berman
 * @author Gopal Sharma, SUN Microsystems Inc.
 *
 * @version $Id: AbstractDateTimeDV.java,v 1.12 2003/06/16 18:15:51 sandygao Exp $
 */
public abstract class AbstractDateTimeDV extends TypeValidator {

    //debugging
    private static final boolean DEBUG=false;

    //define shared variables for date/time

    //define constants
    protected final static int CY = 0,  M = 1, D = 2, h = 3,
    m = 4, s = 5, ms = 6, utc=7, hh=0, mm=1;

    //size for all objects must have the same fields:
    //CCYY, MM, DD, h, m, s, ms + timeZone
    protected final static int TOTAL_SIZE = 8;

    //define constants to be used in assigning default values for
    //all date/time excluding duration
    protected final static int YEAR=2000;
    protected final static int MONTH=01;
    protected final static int DAY = 15;

    public short getAllowedFacets(){
        return ( XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_WHITESPACE | XSSimpleTypeDecl.FACET_ENUMERATION |XSSimpleTypeDecl.FACET_MAXINCLUSIVE |XSSimpleTypeDecl.FACET_MININCLUSIVE | XSSimpleTypeDecl.FACET_MAXEXCLUSIVE  | XSSimpleTypeDecl.FACET_MINEXCLUSIVE  );
    }//getAllowedFacets()

    // the parameters are in compiled form (from getActualValue)
    public int compare (Object value1, Object value2) {
        return compareDates(((DateTimeData)value1).data,
                            ((DateTimeData)value2).data, true);
    }//compare()

    /**
     * Compare algorithm described in dateDime (3.2.7).
     * Duration datatype overwrites this method
     *
     * @param date1  normalized date representation of the first value
     * @param date2  normalized date representation of the second value
     * @param strict
     * @return less, greater, less_equal, greater_equal, equal
     */
    protected short compareDates(int[] date1, int[] date2, boolean strict) {
        if ( date1[utc]==date2[utc] ) {
            return compareOrder(date1, date2);
        }
        short c1, c2;
        
        int[] tempDate = new int[TOTAL_SIZE];
        int[] timeZone = new int[2];

        if ( date1[utc]=='Z' ) {

            //compare date1<=date1<=(date2 with time zone -14)
            //
            cloneDate(date2, tempDate); //clones date1 value to global temporary storage: fTempDate
            timeZone[hh]=14;
            timeZone[mm]=0;
            tempDate[utc]='+';
            normalize(tempDate, timeZone);
            c1 = compareOrder(date1, tempDate);
            if (c1 == LESS_THAN)
                return c1;

            //compare date1>=(date2 with time zone +14)
            //
            cloneDate(date2, tempDate); //clones date1 value to global temporary storage: tempDate
            timeZone[hh]=14;
            timeZone[mm]=0;
            tempDate[utc]='-';
            normalize(tempDate, timeZone);
            c2 = compareOrder(date1, tempDate);
            if (c2 == GREATER_THAN)
                return c2;

            return INDETERMINATE;
        }
        else if ( date2[utc]=='Z' ) {

            //compare (date1 with time zone -14)<=date2
            //
            cloneDate(date1, tempDate); //clones date1 value to global temporary storage: tempDate
            timeZone[hh]=14;
            timeZone[mm]=0;
            tempDate[utc]='-';
            if (DEBUG) {
               System.out.println("tempDate=" + dateToString(tempDate));
            }
            normalize(tempDate, timeZone);
            c1 = compareOrder(tempDate, date2);
            if (DEBUG) {
                System.out.println("date=" + dateToString(date2));
                System.out.println("tempDate=" + dateToString(tempDate));
            }
            if (c1 == LESS_THAN)
                return c1;

            //compare (date1 with time zone +14)<=date2
            //
            cloneDate(date1, tempDate); //clones date1 value to global temporary storage: tempDate
            timeZone[hh]=14;
            timeZone[mm]=0;
            tempDate[utc]='+';
            normalize(tempDate, timeZone);
            c2 = compareOrder(tempDate, date2);
            if (DEBUG) {
               System.out.println("tempDate=" + dateToString(tempDate));
            }
            if (c2 == GREATER_THAN)
                return c2;

            return INDETERMINATE;
        }
        return INDETERMINATE;

    }

    /**
     * Given normalized values, determines order-relation
     * between give date/time objects.
     *
     * @param date1  date/time object
     * @param date2  date/time object
     * @return 0 if date1 and date2 are equal, a value less than 0 if date1 is less than date2, a value greater than 0 if date1 is greater than date2
     */
    protected short compareOrder (int[] date1, int[] date2) {

        for ( int i=0;i<TOTAL_SIZE;i++ ) {
            if ( date1[i]<date2[i] ) {
                return -1;
            }
            else if ( date1[i]>date2[i] ) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Parses time hh:mm:ss.sss and time zone if any
     *
     * @param start
     * @param end
     * @param data
     * @exception RuntimeException
     */
    protected  void getTime (String buffer, int start, int end, int[] data, int[] timeZone) throws RuntimeException{

        int stop = start+2;

        //get hours (hh)
        data[h]=parseInt(buffer, start,stop);

        //get minutes (mm)

        if (buffer.charAt(stop++)!=':') {
                throw new RuntimeException("Error in parsing time zone" );
        }
        start = stop;
        stop = stop+2;
        data[m]=parseInt(buffer, start,stop);

        //get seconds (ss)
        if (buffer.charAt(stop++)!=':') {
                throw new RuntimeException("Error in parsing time zone" );
        }
        start = stop;
        stop = stop+2;
        data[s]=parseInt(buffer, start,stop);

        if (stop == end)
            return;
        
        //get miliseconds (ms)
        start = stop;
        int milisec = buffer.charAt(start) == '.' ? start : -1;

        //find UTC sign if any
        int sign = findUTCSign(buffer, start, end);

        //parse miliseconds
        if ( milisec != -1 ) {
            // The end of millisecond part is between . and
            // either the end of the UTC sign
            start = sign < 0 ? end : sign;
            data[ms]=parseInt(buffer, milisec+1, start);
        }

        //parse UTC time zone (hh:mm)
        if ( sign>0 ) {
            if (start != sign)
                throw new RuntimeException("Error in parsing time zone" );
            getTimeZone(buffer, data, sign, end, timeZone);
        }
        else if (start != end) {
            throw new RuntimeException("Error in parsing time zone" );
        }
    }

    /**
     * Parses date CCYY-MM-DD
     *
     * @param start
     * @param end
     * @param data
     * @exception RuntimeException
     */
    protected int getDate (String buffer, int start, int end, int[] date) throws RuntimeException{

        start = getYearMonth(buffer, start, end, date);

        if (buffer.charAt(start++) !='-') {
            throw new RuntimeException("CCYY-MM must be followed by '-' sign");
        }
        int stop = start + 2;
        date[D]=parseInt(buffer, start, stop);
        return stop;
    }

    /**
     * Parses date CCYY-MM
     *
     * @param start
     * @param end
     * @param data
     * @exception RuntimeException
     */
    protected int getYearMonth (String buffer, int start, int end, int[] date) throws RuntimeException{

        if ( buffer.charAt(0)=='-' ) {
            // REVISIT: date starts with preceding '-' sign
            //          do we have to do anything with it?
            //
            start++;
        }
        int i = indexOf(buffer, start, end, '-');
        if ( i==-1 ) throw new RuntimeException("Year separator is missing or misplaced");
        int length = i-start;
        if (length<4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        }
        else if (length > 4 && buffer.charAt(start)=='0'){
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        date[CY]= parseIntYear(buffer, i);
        if (buffer.charAt(i)!='-') {
            throw new RuntimeException("CCYY must be followed by '-' sign");
        }
        start = ++i;
        i = start +2;
        date[M]=parseInt(buffer, start, i);
        return i; //fStart points right after the MONTH
    }

    /**
     * Shared code from Date and YearMonth datatypes.
     * Finds if time zone sign is present
     *
     * @param end
     * @param date
     * @exception RuntimeException
     */
    protected void parseTimeZone (String buffer, int start, int end, int[] date, int[] timeZone) throws RuntimeException{

        //fStart points right after the date

        if ( start<end ) {
            int sign = findUTCSign(buffer, start, end);
            if ( sign<0 ) {
                throw new RuntimeException ("Error in month parsing");
            }
            else {
                getTimeZone(buffer, date, sign, end, timeZone);
            }
        }
    }

    /**
     * Parses time zone: 'Z' or {+,-} followed by  hh:mm
     *
     * @param data
     * @param sign
     * @exception RuntimeException
     */
    protected void getTimeZone (String buffer, int[] data, int sign, int end, int[] timeZone) throws RuntimeException{
        data[utc]=buffer.charAt(sign);

        if ( buffer.charAt(sign) == 'Z' ) {
            if (end>(++sign)) {
                throw new RuntimeException("Error in parsing time zone");
            }
            return;
        }
        if ( sign<=(end-6) ) {

            //parse [hh]
            int stop = ++sign+2;
            timeZone[hh]=parseInt(buffer, sign, stop);
            if (buffer.charAt(stop++)!=':') {
                throw new RuntimeException("Error in parsing time zone" );
            }

            //parse [ss]
            timeZone[mm]=parseInt(buffer, stop, stop+2);

            if ( stop+2!=end ) {
                throw new RuntimeException("Error in parsing time zone");
            }

        }
        else {
            throw new RuntimeException("Error in parsing time zone");
        }
        if ( DEBUG ) {
            System.out.println("time[hh]="+timeZone[hh] + " time[mm]=" +timeZone[mm]);
        }
    }

    /**
     * Computes index of given char within StringBuffer
     *
     * @param start
     * @param end
     * @param ch     character to look for in StringBuffer
     * @return index of ch within StringBuffer
     */
    protected  int indexOf (String buffer, int start, int end, char ch) {
        for ( int i=start;i<end;i++ ) {
            if ( buffer.charAt(i) == ch ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Validates given date/time object accoring to W3C PR Schema
     * [D.1 ISO 8601 Conventions]
     *
     * @param data
     */
    protected void validateDateTime (int[] data, int[] timeZone) {

        //REVISIT: should we throw an exception for not valid dates
        //          or reporting an error message should be sufficient?
        if ( data[CY]==0 ) {
            throw new RuntimeException("The year \"0000\" is an illegal year value");

        }

        if ( data[M]<1 || data[M]>12 ) {
            throw new RuntimeException("The month must have values 1 to 12");

        }

        //validate days
        if ( data[D]>maxDayInMonthFor(data[CY], data[M]) || data[D]<1 ) {
            throw new RuntimeException("The day must have values 1 to 31");
        }

        //validate hours
        if ( data[h]>23 || data[h]<0 ) {
            if (data[h] == 24 && data[m] == 0 && data[s] == 0 && data[ms] == 0) {
                data[h] = 0;
                if (++data[D] > maxDayInMonthFor(data[CY], data[M])) {
                    data[D] = 1;
                    if (++data[M] > 12) {
                        data[M] = 1;
                        if (++data[CY] == 0)
                            data[CY] = 1;
                    }
                }
            }
            else {
                throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
            }
        }

        //validate
        if ( data[m]>59 || data[m]<0 ) {
            throw new RuntimeException("Minute must have values 0-59");
        }

        //validate
        if ( data[s]>60 || data[s]<0 ) {
            throw new RuntimeException("Second must have values 0-60");

        }

        //validate
        if ( timeZone[hh]>14 || timeZone[hh]<-14 ) {
            throw new RuntimeException("Time zone should have range -14..+14");
        }

        //validate
        if ( timeZone[mm]>59 || timeZone[mm]<-59 ) {
            throw new RuntimeException("Minute must have values 0-59");
        }
    }

    /**
     * Return index of UTC char: 'Z', '+', '-'
     *
     * @param start
     * @param end
     * @return index of the UTC character that was found
     */
    protected int findUTCSign (String buffer, int start, int end) {
        int c;
        for ( int i=start;i<end;i++ ) {
            c=buffer.charAt(i);
            if ( c == 'Z' || c=='+' || c=='-' ) {
                return i;
            }

        }
        return -1;
    }

    /**
     * Given start and end position, parses string value
     *
     * @param value  string to parse
     * @param start  Start position
     * @param end    end position
     * @return  return integer representation of characters
     */
    protected  int parseInt (String buffer, int start, int end)
    throws NumberFormatException{
        //REVISIT: more testing on this parsing needs to be done.
        int radix=10;
        int result = 0;
        int digit=0;
        int limit = -Integer.MAX_VALUE;
        int multmin = limit / radix;
        int i = start;
        do {
            digit = getDigit(buffer.charAt(i));
            if ( digit < 0 ) throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
            if ( result < multmin ) throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
            result *= radix;
            if ( result < limit + digit ) throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
            result -= digit;

        }while ( ++i < end );
        return -result;
    }

    // parse Year differently to support negative value.
    protected int parseIntYear (String buffer, int end){
        int radix=10;
        int result = 0;
        boolean negative = false;
        int i=0;
        int limit;
        int multmin;
        int digit=0;

        if (buffer.charAt(0) == '-'){
            negative = true;
            limit = Integer.MIN_VALUE;
            i++;

        }
        else{
            limit = -Integer.MAX_VALUE;
        }
        multmin = limit / radix;
        while (i < end)
        {
            digit = getDigit(buffer.charAt(i++));
            if (digit < 0) throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
            if (result < multmin) throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
            result *= radix;
            if (result < limit + digit) throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
            result -= digit;
        }

        if (negative)
        {
            if (i > 1) return result;
            else throw new NumberFormatException("'"+buffer.toString()+"' has wrong format");
        }
        return -result;

    }

    /**
     * If timezone present - normalize dateTime  [E Adding durations to dateTimes]
     *
     * @param date   CCYY-MM-DDThh:mm:ss+03
     * @return CCYY-MM-DDThh:mm:ssZ
     */
    protected  void normalize (int[] date, int[] timeZone) {

        // REVISIT: we have common code in addDuration() for durations
        //          should consider reorganizing it.
        //

        //add minutes (from time zone)
        int negate = 1;
        if (date[utc]=='+') {
            negate = -1;
        }
        if ( DEBUG ) {
            System.out.println("==>date[m]"+date[m]);
            System.out.println("==>timeZone[mm]" +timeZone[mm]);
        }
        int temp = date[m] + negate*timeZone[mm];
        int carry = fQuotient (temp, 60);
        date[m]= mod(temp, 60, carry);

        if ( DEBUG ) {
            System.out.println("==>carry: " + carry);
        }
        //add hours
        temp = date[h] + negate*timeZone[hh] + carry;
        carry = fQuotient(temp, 24);
        date[h]=mod(temp, 24, carry);
        if ( DEBUG ) {
            System.out.println("==>date[h]"+date[h]);
            System.out.println("==>carry: " + carry);
        }

        date[D]=date[D]+carry;

        while ( true ) {
            temp=maxDayInMonthFor(date[CY], date[M]);
            if (date[D]<1) {
                date[D] = date[D] + maxDayInMonthFor(date[CY], date[M]-1);
                carry=-1;
            }
            else if ( date[D]>temp ) {
                date[D]=date[D]-temp;
                carry=1;
            }
            else {
                break;
            }
            temp=date[M]+carry;
            date[M]=modulo(temp, 1, 13);
            date[CY]=date[CY]+fQuotient(temp, 1, 13);
        }
        date[utc]='Z';
    }


    /**
     * Resets object representation of date/time
     *
     * @param data   date/time object
     */
    protected void resetDateObj (int[] data) {
        for ( int i=0;i<TOTAL_SIZE;i++ ) {
            data[i]=0;
        }
    }

    /**
     * Given {year,month} computes maximum
     * number of days for given month
     *
     * @param year
     * @param month
     * @return integer containg the number of days in a given month
     */
    protected int maxDayInMonthFor(int year, int month) {
        //validate days
        if ( month==4 || month==6 || month==9 || month==11 ) {
            return 30;
        }
        else if ( month==2 ) {
            if ( isLeapYear(year) ) {
                return 29;
            }
            else {
                return 28;
            }
        }
        else {
            return 31;
        }
    }

    private boolean isLeapYear(int year) {

        //REVISIT: should we take care about Julian calendar?
        return((year%4 == 0) && ((year%100 != 0) || (year%400 == 0)));
    }

    //
    // help function described in W3C PR Schema [E Adding durations to dateTimes]
    //
    protected int mod (int a, int b, int quotient) {
        //modulo(a, b) = a - fQuotient(a,b)*b
        return (a - quotient*b) ;
    }

    //
    // help function described in W3C PR Schema [E Adding durations to dateTimes]
    //
    protected int fQuotient (int a, int b) {

        //fQuotient(a, b) = the greatest integer less than or equal to a/b
        return (int)Math.floor((float)a/b);
    }

    //
    // help function described in W3C PR Schema [E Adding durations to dateTimes]
    //
    protected int modulo (int temp, int low, int high) {
        //modulo(a - low, high - low) + low
        int a = temp - low;
        int b = high - low;
        return (mod (a, b, fQuotient(a, b)) + low) ;
    }

    //
    // help function described in W3C PR Schema [E Adding durations to dateTimes]
    //
    protected int fQuotient (int temp, int low, int high) {
        //fQuotient(a - low, high - low)

        return fQuotient(temp - low, high - low);
    }


    protected String dateToString(int[] date) {
        StringBuffer message = new StringBuffer(25);
        append(message, date[CY], 4);
        message.append('-');
        append(message, date[M], 2);
        message.append('-');
        append(message, date[D], 2);
        message.append('T');
        append(message, date[h], 2);
        message.append(':');
        append(message, date[m], 2);
        message.append(':');
        append(message, date[s], 2);
        message.append('.');
        message.append(date[ms]);
        append(message, (char)date[utc], 0);
        return message.toString();
    }
    
    protected void append(StringBuffer message, int value, int nch) {
        if (value < 0) {
            message.append('-');
            value = -value;
        }
        if (nch == 4) {
            if (value < 10)
                message.append("000");
            else if (value < 100)
                message.append("00");
            else if (value < 1000)
                message.append("0");
            message.append(value);
        }
        else if (nch == 2) {
            if (value < 10)
                message.append('0');
            message.append(value);
        }
        else {
            if (value != 0)
                message.append((char)value);
        }
    }

    //
    //Private help functions
    //

    private void cloneDate (int[] finalValue, int[] tempDate) {
        System.arraycopy(finalValue, 0, tempDate, 0, TOTAL_SIZE);
    }

    /**
     * Represents date time data
     */
    static final class DateTimeData {
        // actual data stored in an int array
        final int[] data;
        // a pointer to the type that was used go generate this data
        // note that this is not the actual simple type, but one of the
        // statically created XXXDV objects, so this won't cause any GC problem.
        final AbstractDateTimeDV type;
        private String canonical;
        public DateTimeData(int[] data, AbstractDateTimeDV type) {
            this.data = data;
            this.type = type;
        }
        public boolean equals(Object obj) {
            if (!(obj instanceof DateTimeData))
                return false;
            int[] odata = ((DateTimeData)obj).data;
            return type.compareDates(data, odata, true)==0;
        }
        public synchronized String toString() {
            if (canonical == null) {
                canonical = type.dateToString(data);
            }
            return canonical;
        }
    }
}
