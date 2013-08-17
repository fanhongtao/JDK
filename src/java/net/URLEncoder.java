/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.BitSet;

/**
 * The class contains a utility method for converting a
 * <code>String</code> into a MIME format called
 * "<code>x-www-form-urlencoded</code>" format.
 * <p>
 * To convert a <code>String</code>, each character is examined in turn:
 * <ul>
 * <li>The ASCII characters '<code>a</code>' through '<code>z</code>',
 *     '<code>A</code>' through '<code>Z</code>', '<code>0</code>'
 *     through '<code>9</code>', and &quot;.&quot;, &quot;-&quot;, 
 * &quot;*&quot;, &quot;_&quot; remain the same.
 * <li>The space character '<code>&nbsp;</code>' is converted into a
 *     plus sign '<code>+</code>'.
 * <li>All other characters are converted into the 3-character string
 *     "<code>%<i>xy</i></code>", where <i>xy</i> is the two-digit
 *     hexadecimal representation of the lower 8-bits of the character.
 * </ul>
 *
 * @author  Herb Jellinek
 * @version 1.19, 02/06/02
 * @since   JDK1.0
 */
public class URLEncoder {
    static BitSet dontNeedEncoding;
    static final int caseDiff = ('a' - 'A');

    /* The list of characters that are not encoded have been determined by
       referencing O'Reilly's "HTML: The Definitive Guide" (page 164). */

    static {
	dontNeedEncoding = new BitSet(256);
	int i;
	for (i = 'a'; i <= 'z'; i++) {
	    dontNeedEncoding.set(i);
	}
	for (i = 'A'; i <= 'Z'; i++) {
	    dontNeedEncoding.set(i);
	}
	for (i = '0'; i <= '9'; i++) {
	    dontNeedEncoding.set(i);
	}
	dontNeedEncoding.set(' '); /* encoding a space to a + is done in the encode() method */
	dontNeedEncoding.set('-');
	dontNeedEncoding.set('_');
	dontNeedEncoding.set('.');
	dontNeedEncoding.set('*');
    }

    /**
     * You can't call the constructor.
     */
    private URLEncoder() { }

    /**
     * Translates a string into <code>x-www-form-urlencoded</code> format.
     *
     * @param   s   <code>String</code> to be translated.
     * @return  the translated <code>String</code>.
     */
    public static String encode(String s) {
	int maxBytesPerChar = 10;
        StringBuffer out = new StringBuffer(s.length());
	ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
	OutputStreamWriter writer = new OutputStreamWriter(buf);

	for (int i = 0; i < s.length(); i++) {
	    int c = (int)s.charAt(i);
	    if (dontNeedEncoding.get(c)) {
		if (c == ' ') {
		    c = '+';
		}
		out.append((char)c);
	    } else {
		// convert to external encoding before hex conversion
		try {
		    writer.write(c);
		    writer.flush();
		} catch(IOException e) {
		    buf.reset();
		    continue;
		}
		byte[] ba = buf.toByteArray();
		for (int j = 0; j < ba.length; j++) {
		    out.append('%');
		    char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
		    // converting to use uppercase letter as part of
		    // the hex value if ch is a letter.
		    if (Character.isLetter(ch)) {
			ch -= caseDiff;
		    }
		    out.append(ch);
		    ch = Character.forDigit(ba[j] & 0xF, 16);
		    if (Character.isLetter(ch)) {
			ch -= caseDiff;
		    }
		    out.append(ch);
		}
		buf.reset();
	    }
	}

	return out.toString();
    }
}
