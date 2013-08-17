/*
 * @(#)StringTokenizer.java	1.16 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.util;

import java.lang.*;

/**
 * The string tokenizer class allows an application to break a 
 * string into tokens. The tokenization method is much simpler than 
 * the one used by the <code>StreamTokenizer</code> class. The 
 * <code>StringTokenizer</code> methods do not distinguish among 
 * identifiers, numbers, and quoted strings, nor do they recognize 
 * and skip comments. 
 * <p>
 * The set of delimiters (the characters that separate tokens) may 
 * be specified either at creation time or on a per-token basis. 
 * <p>
 * An instance of <code>StringTokenizer</code> behaves in one of two 
 * ways, depending on whether it was created with the 
 * <code>returnTokens</code> flag having the value <code>true</code> 
 * or <code>false</code>: 
 * <ul>
 * <li>If the flag is <code>false</code>, delimiter characters serve to 
 *     separate tokens. A token is a maximal sequence of consecutive 
 *     characters that are not delimiters. 
 * <li>If the flag is <code>true</code>, delimiter characters are considered to
 *     be tokens. A token is either one delimiter character, or a maximal
 *     sequence of consecutive characters that are not delimiters.
 * </ul>
 * <p>
 * The following is one example of the use of the tokenizer. The code:
 * <blockquote><pre>
 *     StringTokenizer st = new StringTokenizer("this is a test");
 *     while (st.hasMoreTokens()) {
 *         println(st.nextToken());
 *     }
 * </pre></blockquote>
 * <p>
 * prints the following output:
 * <blockquote><pre>
 *     this
 *     is
 *     a
 *     test
 * </pre></blockquote>
 *
 * @author  unascribed
 * @version 1.16, 07/01/98
 * @see     java.io.StreamTokenizer
 * @since   JDK1.0
 */
public
class StringTokenizer implements Enumeration {
    private int currentPosition;
    private int maxPosition;
    private String str;
    private String delimiters;
    private boolean retTokens;

    /**
     * Constructs a string tokenizer for the specified string. The 
     * characters in the <code>delim</code> argument are the delimiters 
     * for separating tokens. 
     * <p>
     * If the <code>returnTokens</code> flag is <code>true</code>, then 
     * the delimiter characters are also returned as tokens. Each 
     * delimiter is returned as a string of length one. If the flag is 
     * <code>false</code>, the delimiter characters are skipped and only 
     * serve as separators between tokens. 
     *
     * @param   str            a string to be parsed.
     * @param   delim          the delimiters.
     * @param   returnTokens   flag indicating whether to return the delimiters
     *                         as tokens.
     * @since   JDK1.0
     */
    public StringTokenizer(String str, String delim, boolean returnTokens) {
	currentPosition = 0;
	this.str = str;
	maxPosition = str.length();
	delimiters = delim;
	retTokens = returnTokens;
    }

    /**
     * Constructs a string tokenizer for the specified string. The 
     * characters in the <code>delim</code> argument are the delimiters 
     * for separating tokens. 
     *
     * @param   str     a string to be parsed.
     * @param   delim   the delimiters.
     * @since   JDK1.0
     */
    public StringTokenizer(String str, String delim) {
	this(str, delim, false);
    }

    /**
     * Constructs a string tokenizer for the specified string. The 
     * tokenizer uses the default delimiter set, which is 
     * <code>"&#92;t&#92;n&#92;r"</code>: the space character, the tab
     * character, the newline character, and the carriage-return character. 
     *
     * @param   str   a string to be parsed.
     * @since   JDK1.0
     */
    public StringTokenizer(String str) {
	this(str, " \t\n\r", false);
    }

    /**
     * Skips delimiters.
     */
    private void skipDelimiters() {
	while (!retTokens &&
	       (currentPosition < maxPosition) &&
	       (delimiters.indexOf(str.charAt(currentPosition)) >= 0)) {
	    currentPosition++;
	}
    }

    /**
     * Tests if there are more tokens available from this tokenizer's string.
     *
     * @return  <code>true</code> if there are more tokens available from this
     *          tokenizer's string; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean hasMoreTokens() {
	skipDelimiters();
	return (currentPosition < maxPosition);
    }

    /**
     * Returns the next token from this string tokenizer.
     *
     * @return     the next token from this string tokenizer.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     * @since      JDK1.0
     */
    public String nextToken() {
	skipDelimiters();

	if (currentPosition >= maxPosition) {
	    throw new NoSuchElementException();
	}

	int start = currentPosition;
	while ((currentPosition < maxPosition) && 
	       (delimiters.indexOf(str.charAt(currentPosition)) < 0)) {
	    currentPosition++;
	}
	if (retTokens && (start == currentPosition) &&
	    (delimiters.indexOf(str.charAt(currentPosition)) >= 0)) {
	    currentPosition++;
	}
	return str.substring(start, currentPosition);
    }

    /**
     * Returns the next token in this string tokenizer's string. The new 
     * delimiter set remains the default after this call. 
     *
     * @param      delim   the new delimiters.
     * @return     the next token, after switching to the new delimiter set.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     * @since   JDK1.0
     */
    public String nextToken(String delim) {
	delimiters = delim;
	return nextToken();
    }

    /**
     * Returns the same value as the <code>hasMoreTokens</code>
     * method. It exists so that this class can implement the
     * <code>Enumeration</code> interface. 
     *
     * @return  <code>true</code> if there are more tokens;
     *          <code>false</code> otherwise.
     * @see     java.util.Enumeration
     * @see     java.util.StringTokenizer#hasMoreTokens()
     * @since   JDK1.0
     */
    public boolean hasMoreElements() {
	return hasMoreTokens();
    }

    /**
     * Returns the same value as the <code>nextToken</code> method,
     * except that its declared return value is <code>Object</code> rather than
     * <code>String</code>. It exists so that this class can implement the
     * <code>Enumeration</code> interface. 
     *
     * @return     the next token in the string.
     * @exception  NoSuchElementException  if there are no more tokens in this
     *               tokenizer's string.
     * @see        java.util.Enumeration
     * @see        java.util.StringTokenizer#nextToken()
     * @since      JDK1.0
     */
    public Object nextElement() {
	return nextToken();
    }

    /**
     * Calculates the number of times that this tokenizer's 
     * <code>nextToken</code> method can be called before it generates an 
     * exception. 
     *
     * @return  the number of tokens remaining in the string using the current
     *          delimiter set.
     * @see     java.util.StringTokenizer#nextToken()
     * @since   JDK1.0
     */
    public int countTokens() {
	int count = 0;
	int currpos = currentPosition;

	while (currpos < maxPosition) {
	    /*
	     * This is just skipDelimiters(); but it does not affect
	     * currentPosition.
	     */
	    while (!retTokens &&
		   (currpos < maxPosition) &&
		   (delimiters.indexOf(str.charAt(currpos)) >= 0)) {
		currpos++;
	    }

	    if (currpos >= maxPosition) {
		break;
	    }

	    int start = currpos;
	    while ((currpos < maxPosition) && 
		   (delimiters.indexOf(str.charAt(currpos)) < 0)) {
		currpos++;
	    }
	    if (retTokens && (start == currpos) &&
		(delimiters.indexOf(str.charAt(currpos)) >= 0)) {
		currpos++;
	    }
	    count++;

	}
	return count;
    }
}
