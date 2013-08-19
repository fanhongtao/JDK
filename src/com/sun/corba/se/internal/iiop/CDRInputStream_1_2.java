/*
 * @(#)CDRInputStream_1_2.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.core.GIOPVersion;

public class CDRInputStream_1_2 extends CDRInputStream_1_1
{
    protected void alignAndCheck(int align, int n) {

        checkBlockLength();

        // In GIOP 1.2, a fragment may end with some alignment
        // padding (which leads to all fragments ending perfectly
        // on evenly divisible 8 byte boundaries).  A new fragment
        // never requires alignment with the header since it ends
        // on an 8 byte boundary.
        bbwi.index += computeAlignment(align);

    	if (bbwi.index + n > bbwi.buflen) {
            grow(1, n);
        }
    }

    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }
        
    public char read_wchar() {
        // In GIOP 1.2, a wchar is encoded as an unsigned octet length
        // followed by the octets of the converted wchar.
        int numBytes = read_octet();

        char[] result = getConvertedChars(numBytes, getWCharConverter());

        // Did the provided bytes convert to more than one
        // character?  This may come up as more unicode values are
        // assigned, and a single 16 bit Java char isn't enough.
        // Better to use strings for i18n purposes.
        if (getWCharConverter().getNumChars() > 1)
            throw new DATA_CONVERSION(MinorCodes.BTC_RESULT_MORE_THAN_ONE_CHAR,
                                      CompletionStatus.COMPLETED_NO);
        return result[0];
    }

    public String read_wstring() {
        // In GIOP 1.2, wstrings are not terminated by a null.  The
        // length is the number of octets in the converted format.
        // A zero length string is represented with the 4 byte length
        // value of 0.
        int len = read_long();

        // IMPORTANT: Do not replace 'new String("")' with "", it may result
        // in a Serialization bug (See serialization.zerolengthstring) and
        // bug id: 4728756 for details
        if (len == 0)
            return new String("");

        checkForNegativeLength(len);

        return new String(getConvertedChars(len, getWCharConverter()),
                          0,
                          getWCharConverter().getNumChars());
    }
}
