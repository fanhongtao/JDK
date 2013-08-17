/*
 * @(#)InputSubset.java	1.2 98/09/16
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.im;


/**
 * Defines additional Unicode subsets for use by input methods.  Unlike the
 * UnicodeBlock subsets defined in the <code>{@link
 * java.lang.Character.UnicodeBlock}</code> class, these constants do not
 * directly correspond to Unicode code blocks.
 *
 * @version 1.2, 98/09/16
 * @since   JDK1.2
 */

public final class InputSubset extends Character.Subset {

    private InputSubset(String name) {
	super(name);
    }

    /**
     * Constant for all Latin characters, including the characters
     * in the BASIC_LATIN, LATIN_1_SUPPLEMENT, LATIN_EXTENDED_A,
     * LATIN_EXTENDED_B Unicode character blocks.
     */
    public static final InputSubset LATIN
        = new InputSubset("LATIN");

    /**
     * Constant for the digits included in the BASIC_LATIN Unicode character
     * block.
     */
    public static final InputSubset LATIN_DIGITS
        = new InputSubset("LATIN_DIGITS");

    /**
     * Constant for all Han characters used in writing Traditional Chinese,
     * including a subset of the CJK unified ideographs as well as Traditional
     * Chinese Han characters that may be defined as surrogate characters.
     */
    public static final InputSubset TRADITIONAL_HANZI
        = new InputSubset("TRADITIONAL_HANZI");

    /**
     * Constant for all Han characters used in writing Simplified Chinese,
     * including a subset of the CJK unified ideographs as well as Simplified
     * Chinese Han characters that may be defined as surrogate characters.
     */
    public static final InputSubset SIMPLIFIED_HANZI
        = new InputSubset("SIMPLIFIED_HANZI");

    /**
     * Constant for all Han characters used in writing Japanese, including a
     * subset of the CJK unified ideographs as well as Japanese Han characters
     * that may be defined as surrogate characters.
     */
    public static final InputSubset KANJI
        = new InputSubset("KANJI");

    /**
     * Constant for all Han characters used in writing Korean, including a
     * subset of the CJK unified ideographs as well as Korean Han characters
     * that may be defined as surrogate characters.
     */
    public static final InputSubset HANJA
        = new InputSubset("HANJA");

    /**
     * Constant for the halfwidth katakana subset of the Unicode halfwidth and
     * fullwidth forms character blocks.
     */
    public static final InputSubset HALFWIDTH_KATAKANA
        = new InputSubset("HALFWIDTH_KATAKANA");

}
