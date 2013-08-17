/*
 * @(#)WordBreakData.java	1.8 98/01/12
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
 * The WordBreakData contains data used by SimpleTextBoundary
 * to determine word breaks.
 * @see #BreakIterator
 */
final class WordBreakData extends TextBoundaryData
{
    private static final byte BREAK = 0;
    private static final byte letter = 1;
    private static final byte number = 2;
    private static final byte midLetter = 3;
    private static final byte midLetNum = 4;
    private static final byte preNum = 5;
    private static final byte postNum = 6;
    private static final byte midNum = 7;
    private static final byte preMidNum = 8;
    private static final byte blank = 9;
    private static final byte lf = 10;
    private static final byte kata = 11;
    private static final byte hira = 12;
    private static final byte kanji = 13;
    private static final byte diacrit = 14;
    private static final byte cr = 15;
    private static final byte nsm = 16;
    private static final byte EOS = 17;
    private static final int COL_COUNT = 18;

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    private static final byte kWordForwardData[] =
    {
        // brk         let            num            mLe            mLN
        // prN         poN            mNu            pMN            blk
        // lf          kat            hir            kan            dia
        // cr          nsm            EOS

        // 0
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,

        // 1
        (byte)(SI+14), (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+14), (byte)(SI+14),
        (byte)(SI+5),  (byte)(SI+14), (byte)(SI+14), (byte)(SI+5),  (byte)(SI+6),
        (byte)(SI+4),  (byte)(SI+10), (byte)(SI+11), (byte)(SI+12), (byte)(SI+9),
        (byte)(SI+13), (byte)(1),     SI_STOP,

        // 2
        SI_STOP,       (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+7),  (byte)(SI+7),
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(2),     SI_STOP,

        // 3
        SI_STOP,       (byte)(SI+2),  (byte)(SI+3),  SI_STOP,       (byte)(SI+8),
        SI_STOP,       (byte)(SI+14), (byte)(SI+8),  (byte)(SI+8),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(3),     SI_STOP,

        // 4
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,

        // 5
        SI_STOP,       SI_STOP,       (byte)(SI+3),  SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(5),     SI_STOP,

        // 6
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+6),
        (byte)(SI+4),  SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+13), (byte)(6),     SI_STOP,

        // 7
        STOP,          (byte)(SI+2),  STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(7),     STOP,

        // 8
        STOP,          STOP,          (byte)(SI+3),  STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(8),     STOP,

        // 9
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(SI+10), (byte)(SI+11), SI_STOP,       (byte)(SI+9),
        SI_STOP,       (byte)(9),     SI_STOP,

        // 10
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(SI+10), SI_STOP,       SI_STOP,       (byte)(SI+10),
        SI_STOP,       (byte)(10),    SI_STOP,

        // 11
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       (byte)(SI+11), SI_STOP,       (byte)(SI+11),
        SI_STOP,       (byte)(11),    SI_STOP,

        // 12
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+12), SI_STOP,
        SI_STOP,       (byte)(12),    SI_STOP,

        // 13
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+4),  SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,

        // 14
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(14),    SI_STOP
    };
    private static final WordBreakTable kWordForward =
    new WordBreakTable(COL_COUNT, kWordForwardData);
    private static final byte kWordBackwardData[] =
    {
        // brk         let            num            mLe            mLN
        // prN         poN            mNu            pMN            blk
        // lf          kat            hir            kan            dia
        // cr          nsm            EOS

        // 0
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,

        // 1
        (byte)(SI+6),  (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+4),  (byte)(SI+5),
        (byte)(SI+6),  (byte)(SI+7),  (byte)(SI+7),  (byte)(SI+5),  (byte)(SI+8),
        (byte)(SI+8),  (byte)(SI+9),  (byte)(SI+10), (byte)(SI+12), (byte)(SI+11),
        (byte)(SI+8),  (byte)(1),     STOP,

        // 2
        STOP,          (byte)(SI+2), (byte)(SI+3),   (byte)(4),     (byte)(4),
        STOP,          STOP,          STOP,          (byte)(4),     STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(2),     STOP,

        // 3
        STOP,          (byte)(SI+2),  (byte)(SI+3),  STOP,          (byte)(7),
        SI_STOP,       STOP,          (byte)(7),     (byte)(SI+7),  STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(3),     STOP,

        // 4
        STOP,          (byte)(SI+2),  STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(4),     STOP,

        // 5
        STOP,          (byte)(SI+2), (byte)(SI+3), STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(5),     STOP,

        // 6
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(6),     STOP,

        // 7
        STOP,          STOP,          (byte)(SI+3),  STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(7),     STOP,

        // 8
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          (byte)(SI+8),
        (byte)(SI+8),  STOP,          STOP,          STOP,          STOP,
        (byte)(SI+8),  (byte)(8),     STOP,

        // 9
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(SI+9),  STOP,          STOP,          (byte)(9),
        STOP,          (byte)(9),     STOP,

        // 10
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          (byte)(SI+10),STOP,          (byte)(10),
        STOP,          (byte)(10),    STOP,

        // 11
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          (byte)(SI+9), (byte)(SI+10),  STOP,          (byte)(SI+11),
        STOP,          (byte)(11),    STOP,

        // 12
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          (byte)(SI+12), STOP,
        STOP,          (byte)(12),    STOP
    };
    private static final WordBreakTable kWordBackward =
    new WordBreakTable(COL_COUNT, kWordBackwardData);
    private static final int kRawMapping[] =
    {
        BREAK,     // UNASSIGNED             = 0,
        letter,    // UPPERCASE_LETTER       = 1,
        letter,    // LOWERCASE_LETTER       = 2,
        letter,    // TITLECASE_LETTER       = 3,
        letter,    // MODIFIER_LETTER        = 4,
        letter,    // OTHER_LETTER           = 5,
        nsm,       // NON_SPACING_MARK       = 6,
        nsm,       // ENCLOSING_MARK         = 7,
        BREAK,     // COMBINING_SPACING_MARK = 8,
        number,    // DECIMAL_DIGIT_NUMBER   = 9,
        letter,    // LETTER_NUMBER          = 10,
        number,    // OTHER_NUMBER           = 11,
        blank,     // SPACE_SEPARATOR        = 12,
        BREAK,     // LINE_SEPARATOR         = 13,
        BREAK,     // PARAGRAPH_SEPARATOR    = 14,
        BREAK,     // CONTROL                = 15,
        BREAK,     // FORMAT                 = 16
        BREAK,     // ????                   = 17,
        BREAK,     // PRIVATE_USE            = 18,
        BREAK,     // SURROGATE              = 19,
        midLetter, // DASH_PUNCTUATION       = 20,
        BREAK,     // START_PUNCTUATION      = 21,
        BREAK,     // END_PUNCTUATION        = 22,
        BREAK,     // CONNECTOR_PUNCTUATION  = 23,
        BREAK,     // OTHER_PUNCTUATION      = 24,
        BREAK,     // MATH_SYMBOL            = 25,
        preNum,    // CURRENCY_SYMBOL        = 26,
        BREAK,     // MODIFIER_SYMBOL        = 27,
        BREAK      // OTHER_SYMBOL           = 28
    };
    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order
        //as required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION, blank),
        new SpecialMapping(ASCII_LINEFEED, lf),
        new SpecialMapping(ASCII_FORM_FEED, lf),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, cr),
        new SpecialMapping(ASCII_QUOTATION_MARK, midLetNum),
        new SpecialMapping(ASCII_NUMBER_SIGN, preNum),
        new SpecialMapping(ASCII_PERCENT, postNum),
        new SpecialMapping(ASCII_AMPERSAND, postNum),
        new SpecialMapping(ASCII_APOSTROPHE, midLetNum),
        new SpecialMapping(ASCII_COMMA, midNum),
        new SpecialMapping(ASCII_FULL_STOP, preMidNum),
        new SpecialMapping(ASCII_CENT_SIGN, postNum),
        new SpecialMapping(LATIN1_SOFTHYPHEN, midLetter),
        new SpecialMapping(ARABIC_PERCENT_SIGN, postNum),
        new SpecialMapping(ARABIC_DECIMAL_SEPARATOR, midNum),
        new SpecialMapping(PUNCTUATION_HYPHENATION_POINT, midLetter),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR,
                           PUNCTUATION_PARAGRAPH_SEPARATOR, lf),
        new SpecialMapping(PER_MILLE_SIGN, postNum),
        new SpecialMapping(PER_TEN_THOUSAND_SIGN, postNum),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, HIRAGANA_LETTER_VU, hira),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, diacrit),
        new SpecialMapping(KATAKANA_LETTER_SMALL_A,
                           KATAKANA_LETTER_SMALL_KE, kata),
        new SpecialMapping(UNICODE_LOW_BOUND_HAN,
                           UNICODE_HIGH_BOUND_HAN, kanji),
        new SpecialMapping(HANGUL_SYL_LOW, HANGUL_SYL_HIGH, letter),
        new SpecialMapping(CJK_COMPATIBILITY_F900,
                           CJK_COMPATIBILITY_FA2D, kanji),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean WordExceptionFlags[] = {
        false,          // kNonCharacter            = 0,
        false,          // kUppercaseLetter         = 1,
        false,          // kLowercaseLetter         = 2,
        false,          // kTitlecaseLetter         = 3,
        false,          // kModifierLetter          = 4,
        true,           // kOtherLetter             = 5,
        true,           // kNonSpacingMark          = 6,
        false,          // kEnclosingMark           = 7,
        false,          // kCombiningSpacingMark    = 8,
        false,          // kDecimalNumber           = 9,
        false,          // kLetterNumber            = 10,
        false,          // kOtherNumber             = 11,
        false,          // kSpaceSeparator          = 12,
        true,           // kLineSeparator           = 13,
        true,           // kParagraphSeparator      = 14,
        true,           // kControlCharacter        = 15,
        false,          // kFormatCharacter         = 16,
        false,          // UNDEFINED                = 17,
        false,          // kPrivateUseCharacter     = 18,
        false,          // kSurrogate               = 19,
        true,           // kDashPunctuation         = 20,
        false,          // kOpenPunctuation         = 21,
        false,          // kClosePunctuation        = 22,
        false,          // kConnectorPunctuation    = 23,
        true,           // kOtherPunctuation        = 24,
        false,          // kMathSymbol              = 25,
        true,           // kCurrencySymbol          = 26,
        false,          // kModifierSymbol          = 27,
        false           // kOtherSymbol             = 28
    };

    private static final int kWordAsciiValues[] = {
        //  null    soh     stx     etx     eot     enq     ask     bell
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  bs      ht      lf      vt      ff      cr      so      si
            BREAK,  blank,  lf,     BREAK,  lf,     cr,     BREAK,  BREAK,
        //  dle     dc1     dc2     dc3     dc4     nak     syn     etb
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  can     em      sub     esc     fs      gs      rs      us
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  sp      !       "          #       $       %        &        '
            blank,  BREAK,  midLetNum, preNum, preNum, postNum, postNum, midLetNum,
        //  (       )       *       +       ,       -          .          /
            BREAK,  BREAK,  BREAK,  BREAK,  midNum, midLetter, preMidNum, BREAK,
        //  0       1       2       3       4       5       6       7
            number, number, number, number, number, number, number, number,
        //  8       9       :       ;       <       =       >       ?
            number, number, BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  @       A       B       C       D       E       F       G
            BREAK,  letter, letter, letter, letter, letter, letter, letter,
        //  H       I       J       K       L       M       N       O
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  P       Q       R       S       T       U       V       W
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  X       Y       Z       [       \       ]       ^       _
            letter, letter, letter, BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  `       a       b       c       d       e       f       g
            BREAK,  letter, letter, letter, letter, letter, letter, letter,
        //  h       i       j       k       l       m       n       o
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  p       q       r       s       t       u       v       w
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  x       y       z       {       |       }       ~       del
            letter, letter, letter, BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,  BREAK,
        //  nbsp    ¡       ¢       £       ¤       ¥       ¦
            blank,  BREAK,  postNum, preNum, preNum, preNum, BREAK,  BREAK,
        //  ¨       ©       ª       «       ¬       ­       ®       ¯
            BREAK,  BREAK,  letter, BREAK,  BREAK,  midLetter, BREAK, BREAK,
        //  °       ±       ²       ³       ´       µ       ¶       ·
            BREAK,  BREAK,  number, number, BREAK,  letter, BREAK,  BREAK,
        //  ¸       ¹       º       »       ¼       ½       ¾       ¿
            BREAK,  letter, BREAK,  BREAK,  number, number, number, BREAK,
        //  À       Á       Â       Ã       Ä       Å       Æ       Ç
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  È       É       Ê       Ë       Ì       Í       Î       Ï
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  Ð       Ñ       Ò       Ó       Ô       Õ       Ö       ×
            letter, letter, letter, letter, letter, letter, letter, BREAK,
        //  Ø       Ù       Ú       Û       Ü       Ý       Þ       ß
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  à       á       â       ã       ä       å       æ       ç
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  è       é       ê       ë       ì       í       î       ï
            letter, letter, letter, letter, letter, letter, letter, letter,
        //  ð       ñ       ò       ó       ô       õ       ö       ÷
            letter, letter, letter, letter, letter, letter, letter, BREAK,
        //  ø       ù       ú       û       ü       ý       þ       ÿ
            letter, letter, letter, letter, letter, letter, letter, letter
    };

    private static final UnicodeClassMapping kWordMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, WordExceptionFlags,
        kWordAsciiValues);

    public WordBreakTable forward()
    {
        return kWordForward;
    }

    public WordBreakTable backward()
    {
        return kWordBackward;
    }

    public UnicodeClassMapping map()
    {
        return kWordMap;
    }
}

