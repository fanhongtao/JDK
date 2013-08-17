/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.*;

/**
 * The class contains a utility method for converting from
 * a MIME format called "<code>x-www-form-urlencoded</code>"
 * to a <code>String</code>
 * <p>
 * To convert to a <code>String</code>, each character is examined in turn:
 * <ul>
 * <li>The ASCII characters '<code>a</code>' through '<code>z</code>',
 * '<code>A</code>' through '<code>Z</code>', and '<code>0</code>'
 * through '<code>9</code>' remain the same.
 * <li>The plus sign '<code>+</code>'is converted into a
 * space character '<code>&nbsp;</code>'.
 * <li>The remaining characters are represented by 3-character
 * strings which begin with the percent sign,
 * "<code>%<i>xy</i></code>", where <i>xy</i> is the two-digit
 * hexadecimal representation of the lower 8-bits of the character.
 * </ul>
 *
 * @author  Mark Chamness
 * @author  Michael McCloskey
 * @version 1.10, 02/06/02
 * @since   1.2
 */

public class URLDecoder {

/**
 * Decodes a &quot;x-www-form-urlencoded&quot; 
 * to a <tt>String</tt>.
 * @param s the <code>String</code> to decode
 * @return the newly decoded <code>String</code>
 */
    public static String decode(String s) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char)Integer.parseInt(
                                        s.substring(i+1,i+3),16));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    }
                    i += 2;
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        // Undo conversion to external encoding
        String result = sb.toString();
        try {
            byte[] inputBytes = result.getBytes("8859_1");
            result = new String(inputBytes);
        } catch (UnsupportedEncodingException e) {
            // The system should always have 8859_1
        }
        return result;
    }
}


