/*
 * @(#)TextBoundaryData.java	1.1 96/10/08
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.text;

/**
 * This class wraps up the data tables needed for SimpleTextBoundary.
 * It is subclassed for each type of text boundary.
 */
abstract class TextBoundaryData
{
    public abstract WordBreakTable forward();
    public abstract WordBreakTable backward();
    public abstract UnicodeClassMapping map();

    // usefull Unicode constant
    protected static final char ASCII_END_OF_TEXT
        = '\u0003';
    protected static final char ASCII_HORIZONTAL_TABULATION
        = '\u0009';
    protected static final char ASCII_LINEFEED
        = (char)0x000A;
    protected static final char ASCII_VERTICAL_TABULATION
        = '\u000B';
    protected static final char ASCII_FORM_FEED
        = '\u000C';
    protected static final char ASCII_CARRIAGE_RETURN
        = '\u000D';
    protected static final char ASCII_SPACE
        = '\u0020';
    protected static final char ASCII_EXCLAMATION_MARK
        = '\u0021';
    protected static final char ASCII_QUOTATION_MARK
        = '\u0022';
    protected static final char ASCII_NUMBER_SIGN
        = '\u0023';
    protected static final char ASCII_PERCENT
        = '\u0025';
    protected static final char ASCII_AMPERSAND
        = '\u0026';
    protected static final char ASCII_APOSTROPHE
        = '\u0027';
    protected static final char ASCII_COMMA
        = '\u002C';
    protected static final char ASCII_FULL_STOP
        = '\u002E';
    protected static final char ASCII_COLON
        = '\u003A';
    protected static final char ASCII_SEMICOLON
        = '\u003B';
    protected static final char ASCII_QUESTION_MARK
        = '\u003F';
    protected static final char ASCII_NONBREAKING_SPACE
        = '\u00A0';
    protected static final char ASCII_CENT_SIGN
        = '\u00A2';
    protected static final char LATIN1_SOFTHYPHEN
        = '\u00AD';
    protected static final char ARABIC_PERCENT_SIGN
        = '\u066A';
    protected static final char ARABIC_DECIMAL_SEPARATOR
        = '\u066B';
    protected static final char PUNCTUATION_HYPHENATION_POINT
        = '\u2027';
    protected static final char PUNCTUATION_LINE_SEPARATOR
        = '\u2028';
    protected static final char PUNCTUATION_PARAGRAPH_SEPARATOR
        = '\u2029';
    protected static final char PER_MILLE_SIGN
        = '\u2030';
    protected static final char PER_TEN_THOUSAND_SIGN
        = '\u2031';
    protected static final char PUNCTUATION_IDEOGRAPHIC_FULL_STOP
        = '\u3002';
    protected static final char HIRAGANA_LETTER_SMALL_A
        = '\u3041';
    protected static final char HIRAGANA_LETTER_VU
        = '\u3094';
    protected static final char COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK
        = '\u3099';
    protected static final char HIRAGANA_SEMIVOICED_SOUND_MARK
        = '\u309C';
    protected static final char KATAKANA_LETTER_SMALL_A
        = '\u30A1';
    protected static final char KATAKANA_LETTER_SMALL_KE
        = '\u30F6';
    protected static final char UNICODE_LOW_BOUND_HAN
        = '\u4E00';
    protected static final char UNICODE_HIGH_BOUND_HAN
        = '\u9FA5';
    protected static final char HANGUL_SYL_LOW
        = '\uAC00';
    protected static final char HANGUL_SYL_HIGH
        = '\uD7A3';
    protected static final char CJK_COMPATIBILITY_F900
        = '\uF900';
    protected static final char CJK_COMPATIBILITY_FA2D
        = '\uFA2D';
    protected static final char UNICODE_ZERO_WIDTH_NON_BREAKING_SPACE
        = '\uFEFF';
}

