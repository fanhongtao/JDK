/*
 * @(#)TextBoundaryData.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

/**
 * This class wraps up the data tables needed for SimpleTextBoundary.
 * It is subclassed for each type of text boundary.
 */
abstract class TextBoundaryData
{
    private WordBreakTable forwardStateTable = null;
    private WordBreakTable backwardStateTable = null;
    private UnicodeClassMapping mappingTable = null;

    protected TextBoundaryData(WordBreakTable fwd, WordBreakTable bwd, UnicodeClassMapping map) {
        forwardStateTable = fwd;
        backwardStateTable = bwd;
        mappingTable = map;
    }

    public WordBreakTable forward() {
        return forwardStateTable;
    }

    public WordBreakTable backward() {
        return backwardStateTable;
    }

    public UnicodeClassMapping map() {
        return mappingTable;
    }

    // useful Unicode constants
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
        = (char)0x000D;
    protected static final char ASCII_SPACE
        = '\u0020';
    protected static final char ASCII_EXCLAMATION_MARK
        = '\u0021';
    protected static final char ASCII_QUOTATION_MARK
        = '\u0022';
    protected static final char ASCII_NUMBER_SIGN
        = '\u0023';
    protected static final char ASCII_DOLLAR_SIGN
        = '\u0024';
    protected static final char ASCII_PERCENT
        = '\u0025';
    protected static final char ASCII_AMPERSAND
        = '\u0026';
    protected static final char ASCII_APOSTROPHE
        = (char)0x0027;
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
    protected static final char ASCII_POUND_SIGN
        = '\u00a3';
    protected static final char ASCII_YEN_SIGN
        = '\u00a5';
    protected static final char LATIN1_SOFTHYPHEN
        = '\u00AD';
    protected static final char LATIN1_DEGREE_SIGN
        = '\u00B0';
    protected static final char ARABIC_PERCENT_SIGN
        = '\u066A';
    protected static final char ARABIC_DECIMAL_SEPARATOR
        = '\u066B';
    protected static final char HANGUL_CHOSEONG_LOW
        = '\u1100';
    protected static final char HANGUL_CHOSEONG_HIGH
        = '\u115f';
    protected static final char HANGUL_JUNGSEONG_LOW
        = '\u1160';
    protected static final char HANGUL_JUNGSEONG_HIGH
        = '\u11A7';
    protected static final char HANGUL_JONGSEONG_LOW
        = '\u11A8';
    protected static final char HANGUL_JONGSEONG_HIGH
        = '\u11FF';
    protected static final char FIGURE_SPACE
        = '\u2007';
    protected static final char NONBREAKING_HYPHEN
        = '\u2011';
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
    protected static final char PRIME
        = '\u2032';
    protected static final char DOUBLE_PRIME
        = '\u2033';
    protected static final char TRIPLE_PRIME
        = '\u2034';
    protected static final char DEGREE_CELSIUS
        = '\u2103';
    protected static final char DEGREE_FAHRENHEIT
        = '\u2109';
    protected static final char PUNCTUATION_IDEOGRAPHIC_COMMA
        = '\u3001';
    protected static final char PUNCTUATION_IDEOGRAPHIC_FULL_STOP
        = '\u3002';
    protected static final char IDEOGRAPHIC_ITERATION_MARK
        = '\u3005';
    protected static final char HIRAGANA_LETTER_SMALL_A
        = '\u3041';
    protected static final char HIRAGANA_LETTER_A
        = '\u3042';
    protected static final char HIRAGANA_LETTER_SMALL_I
        = '\u3043';
    protected static final char HIRAGANA_LETTER_I
        = '\u3044';
    protected static final char HIRAGANA_LETTER_SMALL_U
        = '\u3045';
    protected static final char HIRAGANA_LETTER_U
        = '\u3046';
    protected static final char HIRAGANA_LETTER_SMALL_E
        = '\u3047';
    protected static final char HIRAGANA_LETTER_E
        = '\u3048';
    protected static final char HIRAGANA_LETTER_SMALL_O
        = '\u3049';
    protected static final char HIRAGANA_LETTER_O
        = '\u304A';
    protected static final char HIRAGANA_LETTER_DI
        = '\u3062';
    protected static final char HIRAGANA_LETTER_SMALL_TU
        = '\u3063';
    protected static final char HIRAGANA_LETTER_TU
        = '\u3064';
    protected static final char HIRAGANA_LETTER_MO
        = '\u3082';
    protected static final char HIRAGANA_LETTER_SMALL_YA
        = '\u3083';
    protected static final char HIRAGANA_LETTER_YA
        = '\u3084';
    protected static final char HIRAGANA_LETTER_SMALL_YU
        = '\u3085';
    protected static final char HIRAGANA_LETTER_YU
        = '\u3086';
    protected static final char HIRAGANA_LETTER_SMALL_YO
        = '\u3087';
    protected static final char HIRAGANA_LETTER_YO
        = '\u3088';
    protected static final char HIRAGANA_LETTER_RO
        = '\u308D';
    protected static final char HIRAGANA_LETTER_SMALL_WA
        = '\u308E';
    protected static final char HIRAGANA_LETTER_WA
        = '\u308F';
    protected static final char HIRAGANA_LETTER_VU
        = '\u3094';
    protected static final char COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK
        = '\u3099';
    protected static final char HIRAGANA_SEMIVOICED_SOUND_MARK
        = '\u309C';
    protected static final char HIRAGANA_ITERATION_MARK
        = '\u309D';
    protected static final char HIRAGANA_VOICED_ITERATION_MARK
        = '\u309E';
    protected static final char KATAKANA_LETTER_SMALL_A
        = '\u30A1';
    protected static final char KATAKANA_LETTER_A
        = '\u30A2';
    protected static final char KATAKANA_LETTER_SMALL_I
        = '\u30A3';
    protected static final char KATAKANA_LETTER_I
        = '\u30A4';
    protected static final char KATAKANA_LETTER_SMALL_U
        = '\u30A5';
    protected static final char KATAKANA_LETTER_U
        = '\u30A6';
    protected static final char KATAKANA_LETTER_SMALL_E
        = '\u30A7';
    protected static final char KATAKANA_LETTER_E
        = '\u30A8';
    protected static final char KATAKANA_LETTER_SMALL_O
        = '\u30A9';
    protected static final char KATAKANA_LETTER_O
        = '\u30AA';
    protected static final char KATAKANA_LETTER_DI
        = '\u30C2';
    protected static final char KATAKANA_LETTER_SMALL_TU
        = '\u30C3';
    protected static final char KATAKANA_LETTER_TU
        = '\u30C4';
    protected static final char KATAKANA_LETTER_MO
        = '\u30E2';
    protected static final char KATAKANA_LETTER_SMALL_YA
        = '\u30E3';
    protected static final char KATAKANA_LETTER_YA
        = '\u30E4';
    protected static final char KATAKANA_LETTER_SMALL_YU
        = '\u30E5';
    protected static final char KATAKANA_LETTER_YU
        = '\u30E6';
    protected static final char KATAKANA_LETTER_SMALL_YO
        = '\u30E7';
    protected static final char KATAKANA_LETTER_YO
        = '\u30E8';
    protected static final char KATAKANA_LETTER_RO
        = '\u30ED';
    protected static final char KATAKANA_LETTER_SMALL_WA
        = '\u30EE';
    protected static final char KATAKANA_LETTER_WA
        = '\u30EF';
    protected static final char KATAKANA_LETTER_VU
        = '\u30F4';
    protected static final char KATAKANA_LETTER_SMALL_KA
        = '\u30F5';
    protected static final char KATAKANA_LETTER_SMALL_KE
        = '\u30F6';
    protected static final char KATAKANA_LETTER_VA
        = '\u30F7';
    protected static final char KATAKANA_LETTER_VO
        = '\u30FA';
    protected static final char KATAKANA_HIRAGANA_PROLONGED_SOUND_MARK
        = '\u30FC';
    protected static final char KATAKANA_ITERATION_MARK
        = '\u30FD';
    protected static final char KATAKANA_VOICED_ITERATION_MARK
        = '\u30FE';
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
    protected static final char FULLWIDTH_EXCLAMATION_MARK
        = '\uFF01';
    protected static final char FULLWIDTH_COMMA
        = '\uFF0C';
    protected static final char FULLWIDTH_FULL_STOP
        = '\uFF0E';
    protected static final char FULLWIDTH_QUESTION_MARK
        = '\uFF1F';

    // SimpleTextBoundary has an internal convention that the not-a-Unicode value
    // $FFFF is used to signify the end of the string when looking up a proper state
    // transition for the end of the string
    protected static final char END_OF_STRING
        = '\uFFFF';
}

