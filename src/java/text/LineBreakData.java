/*
 * @(#)LineBreakData.java	1.8 98/01/12
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
 * The LineBreakData contains data used by SimpleTextBoundary
 * to determine line breaks.
 * @see #BreakIterator
 */
final class LineBreakData extends TextBoundaryData
{
    private static final byte BREAK                 = 0;
    //always breaks (must be present as first item)
    private static final byte blank                 = 1;
    //spaces, tabs, nulls.
    private static final byte cr                    = 2;
    //carriage return
    private static final byte nonBlank              = 3;
    //everything not included elsewhere
    private static final byte op                    = 4;
    //hyphens....
    private static final byte jwrd                  = 5;
    //hiragana, katakana, and kanji
    private static final byte preJwrd               = 6;
    //characters that bind to the beginning of a Japanese word
    private static final byte postJwrd              = 7;
    //characters that bind to the end of a Japanese word
    private static final byte digit                 = 8;
    //digits
    private static final byte numPunct              = 9;
    //punctuation that can appear within a number
    private static final byte currency              = 10;
    //currency symbols that can precede a number
    private static final byte nsm                   = 11;
    // non-spacing marks
    private static final byte nbsp                  = 12;
    // non-breaking characters
    private static final byte EOS                   = 13;
    private static final int COL_COUNT = 14;

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    private static final byte kLineForwardData[] =
    {
        // brk         bl             cr             nBl
        // op          kan            prJ            poJ
        // dgt         np             curr           nsm
        // nbsp        EOS
        /*00*/
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,
        /*01*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  (byte)(SI+3),
        (byte)(SI+6),  (byte)(SI+5),  (byte)(SI+1),  (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+8),  (byte)(SI+1),  (byte)(SI+1),
        (byte)(SI+1),  SI_STOP,
        /*02*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+2),
        (byte)(SI+1),  SI_STOP,
        /*03*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  (byte)(SI+3),
        (byte)(SI+6),  SI_STOP,       SI_STOP,       (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+8),  SI_STOP,       (byte)(SI+3),
        (byte)(SI+1),  SI_STOP,
        /*04*/
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,
        /*05*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+8),
        SI_STOP,       (byte)(SI+8),  SI_STOP,       (byte)(SI+5),
        (byte)(SI+1),  SI_STOP,
        /*06*/
        (byte)(SI+4),  SI_STOP,       (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+9),  SI_STOP,       (byte)(SI+11), (byte)(SI+6),
        (byte)(SI+1),  SI_STOP,
        /*07*/
        (byte)(SI+4),  SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,
        /*08*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+8),
        SI_STOP,       (byte)(SI+8),  SI_STOP,       (byte)(SI+8),
        (byte)(SI+1),  SI_STOP,
        /*09*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  (byte)(SI+3),
        (byte)(SI+6),  SI_STOP,       SI_STOP,       (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+10), (byte)(SI+10), (byte)(SI+9),
        (byte)(SI+1),  SI_STOP,
        /*10*/
        (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+7),  SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+8),
        (byte)(SI+9),  (byte)(SI+8),  SI_STOP,       (byte)(SI+10),
        (byte)(SI+1),  SI_STOP,
        /*11*/
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        (byte)(SI+9),  STOP,          STOP,          (byte)(11),
        (byte)(SI+1),  STOP
    };

    private static final WordBreakTable kLineForward
        = new WordBreakTable(COL_COUNT, kLineForwardData);

    private static final byte kLineBackwardData[] =
    {
        // brk         bl             cr             nBl
        // op          kan            prJ            poJ
        // dgt         np             curr           nsm
        // nbsp        EOS
        /*00*/
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,
        /*01*/
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+1),
        (byte)(SI+2),  STOP,
        /*02*/
        STOP,          STOP,          STOP,          (byte)(SI+2),
        (byte)(SI+2),  STOP,          (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  STOP,
        /*03*/
        STOP,          STOP,          STOP,          (byte)(SI+2),
        STOP,          (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+3),
        (byte)(SI+2),  STOP,
        /*04*/
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          (byte)(SI+2),  STOP,
        STOP,          STOP,          (byte)(SI+2),  (byte)(SI+4),
        (byte)(SI+4),  STOP
    };

    private static final WordBreakTable kLineBackward
        = new WordBreakTable(COL_COUNT, kLineBackwardData);

    private static final int kRawMapping[] =
    {
        nonBlank, //UNASSIGNED             = 0,
        nonBlank, //UPPERCASE_LETTER       = 1,
        nonBlank, //LOWERCASE_LETTER       = 2,
        nonBlank, //TITLECASE_LETTER       = 3,
        nonBlank, //MODIFIER_LETTER        = 4,
        nonBlank, //OTHER_LETTER           = 5,
        nsm,      //NON_SPACING_MARK       = 6,
        nsm,      //ENCLOSING_MARK         = 7,
        nonBlank, //COMBINING_SPACING_MARK = 8,
        digit, //DECIMAL_DIGIT_NUMBER      = 9,
        nonBlank, //LETTER_NUMBER          = 10,
        digit, //OTHER_NUMBER              = 11,
        blank, //SPACE_SEPARATOR           = 12,
        blank, //LINE_SEPARATOR            = 13,
        blank, //PARAGRAPH_SEPARATOR       = 14,     ???????????
        blank, //CONTROL                   = 15,
        nonBlank, //PRIVATE_USE            = 16,
        nonBlank, //FORMAT                 = 17
        nonBlank, //????                   = 18,
        nonBlank, //SURROGATE              = 19,
        op, //DASH_PUNCTUATION             = 20,
        preJwrd, //START_PUNCTUATION       = 21,
        postJwrd, //END_PUNCTUATION        = 22,
        nonBlank, //CONNECTOR_PUNCTUATION  = 23,
        nonBlank, //OTHER_PUNCTUATION      = 24,
        nonBlank, //MATH_SYMBOL            = 25,
        preJwrd, //CURRENCY_SYMBOL         = 26,
        nonBlank, //MODIFIER_SYMBOL        = 27,
        nonBlank  //OTHER_SYMBOL           = 28;
    };

    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order as
        //      required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_END_OF_TEXT, BREAK),
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION,
                           ASCII_FORM_FEED, BREAK),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, cr),
        new SpecialMapping(ASCII_EXCLAMATION_MARK, postJwrd),
        new SpecialMapping(ASCII_DOLLAR_SIGN, preJwrd),
        new SpecialMapping(ASCII_PERCENT, postJwrd),
        new SpecialMapping(ASCII_COMMA, numPunct),
        new SpecialMapping(ASCII_FULL_STOP, numPunct),
        new SpecialMapping(ASCII_COLON, ASCII_SEMICOLON, postJwrd),
        new SpecialMapping(ASCII_QUESTION_MARK, postJwrd),
        new SpecialMapping(ASCII_NONBREAKING_SPACE, nbsp),
        new SpecialMapping(ASCII_CENT_SIGN, postJwrd),
        new SpecialMapping(LATIN1_SOFTHYPHEN, op),
        new SpecialMapping(LATIN1_DEGREE_SIGN, postJwrd),
        new SpecialMapping(ARABIC_PERCENT_SIGN, postJwrd),
        new SpecialMapping(FIGURE_SPACE, nbsp),
        new SpecialMapping(NONBREAKING_HYPHEN, nbsp),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR,
                           PUNCTUATION_PARAGRAPH_SEPARATOR, BREAK),
        new SpecialMapping(PER_MILLE_SIGN, postJwrd),
        new SpecialMapping(PER_TEN_THOUSAND_SIGN, postJwrd),
        new SpecialMapping(PRIME, TRIPLE_PRIME, postJwrd),
        new SpecialMapping(DEGREE_CELSIUS, postJwrd),
        new SpecialMapping(DEGREE_FAHRENHEIT, postJwrd),
        new SpecialMapping(PUNCTUATION_IDEOGRAPHIC_COMMA,
                           PUNCTUATION_IDEOGRAPHIC_FULL_STOP, postJwrd),
        new SpecialMapping(IDEOGRAPHIC_ITERATION_MARK, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_A, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_I, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_I, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_U, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_U, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_E, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_E, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_O, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_O, HIRAGANA_LETTER_DI, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_TU, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_TU, HIRAGANA_LETTER_MO, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_YA, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_YA, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_YU, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_YU, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_YO, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_YO, HIRAGANA_LETTER_RO, jwrd),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_WA, postJwrd),
        new SpecialMapping(HIRAGANA_LETTER_WA, HIRAGANA_LETTER_VU, jwrd),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, postJwrd),
        new SpecialMapping(HIRAGANA_ITERATION_MARK, HIRAGANA_VOICED_ITERATION_MARK, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_A, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_A, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_I, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_I, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_U, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_U, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_E, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_E, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_O, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_O, KATAKANA_LETTER_DI, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_TU, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_TU, KATAKANA_LETTER_MO, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_YA, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_YA, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_YU, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_YU, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_YO, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_YO, KATAKANA_LETTER_RO, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_WA, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_WA, KATAKANA_LETTER_VU, jwrd),
        new SpecialMapping(KATAKANA_LETTER_SMALL_KA, KATAKANA_LETTER_SMALL_KE, postJwrd),
        new SpecialMapping(KATAKANA_LETTER_VA, KATAKANA_LETTER_VO, jwrd),
        new SpecialMapping(KATAKANA_HIRAGANA_PROLONGED_SOUND_MARK, postJwrd),
        new SpecialMapping(KATAKANA_ITERATION_MARK, KATAKANA_VOICED_ITERATION_MARK, postJwrd),
        new SpecialMapping(UNICODE_LOW_BOUND_HAN,UNICODE_HIGH_BOUND_HAN,jwrd),
        new SpecialMapping(CJK_COMPATIBILITY_F900,
                           CJK_COMPATIBILITY_FA2D, jwrd),
        new SpecialMapping(UNICODE_ZERO_WIDTH_NON_BREAKING_SPACE, nbsp),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean LineExceptionFlags[] = {
        false,          // kNonCharacter            = 0,
        false,          // kUppercaseLetter         = 1,
        false,          // kLowercaseLetter         = 2,
        false,          // kTitlecaseLetter         = 3,
        true,           // kModifierLetter          = 4,
        true,           // kOtherLetter             = 5,
        true,           // kNonSpacingMark          = 6,
        false,          // kEnclosingMark           = 7,
        false,          // kCombiningSpacingMark    = 8,
        false,          // kDecimalNumber           = 9,
        false,          // kLetterNumber            = 10,
        false,          // kOtherNumber             = 11,
        true,           // kSpaceSeparator          = 12,
        true,           // kLineSeparator           = 13,
        true,           // kParagraphSeparator      = 14,
        true,           // kControlCharacter        = 15,
        true,           // kFormatCharacter         = 16,
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
        true            // kOtherSymbol             = 28
    };

    private static final int kLineAsciiValues[] = {
        //  null    soh     stx     etx     eot     enq     ask     bell
            blank,  blank,  blank,  BREAK,  blank,  blank,  blank,  blank,
        //  bs      ht      lf      vt      ff      cr      so      si
            blank,  BREAK,  BREAK,  BREAK,  BREAK,  cr,     blank,  blank,
        //  dle     dc1     dc2     dc3     dc4     nak     syn     etb
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  can     em      sub     esc     fs      gs      rs      us
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  sp      !         "         #         $         %         &         '
            blank,  postJwrd, nonBlank, nonBlank, currency, postJwrd, nonBlank, nonBlank,
        //  (       )          *         +         ,         -   .         /
            preJwrd, postJwrd, nonBlank, nonBlank, numPunct, op, numPunct, nonBlank,
        //  0         1         2         3         4         5         6         7
            digit,    digit,    digit,    digit,    digit,    digit,    digit,    digit,
        //  8         9         :         ;         <         =         >         ?
            digit,    digit,    postJwrd, postJwrd, nonBlank, nonBlank, nonBlank, postJwrd,
        //  @         A         B         C         D         E         F         G
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  H         I         J         K         L         M         N         O
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  P         Q         R         S         T         U         V         W
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  X         Y         Z         [        \         ]         ^         _
            nonBlank, nonBlank, nonBlank, preJwrd, nonBlank, postJwrd, nonBlank, nonBlank,
        //  `         a         b         c         d         e         f         g
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  h         i         j         k         l         m         n         o
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  p         q         r         s         t         u         v         w
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  x         y         z         {        |         }         ~         del
            nonBlank, nonBlank, nonBlank, preJwrd, nonBlank, postJwrd, nonBlank, blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            blank,  blank,  blank,  blank,  blank,  blank,  blank,  blank,
        //  nbsp   ¡         ¢         £         ¤         ¥         ¦
            nbsp,  nonBlank, postJwrd, currency, currency, currency, nonBlank, nonBlank,
        //  ¨         ©         ª         «        ¬         ­   ®         ¯
            nonBlank, nonBlank, nonBlank, preJwrd, nonBlank, op, nonBlank, nonBlank,
        //  °         ±         ²         ³         ´         µ         ¶         ·
            postJwrd, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  ¸         ¹         º         »         ¼         ½         ¾         ¿
            nonBlank, nonBlank, nonBlank, postJwrd, digit,    digit,    digit,    nonBlank,
        //  À         Á         Â         Ã         Ä         Å         Æ         Ç
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  È         É         Ê         Ë         Ì         Í         Î         Ï
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  Ð         Ñ         Ò         Ó         Ô         Õ         Ö         ×
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  Ø         Ù         Ú         Û         Ü         Ý         Þ         ß
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  à         á         â         ã         ä         å         æ         ç
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  è         é         ê         ë         ì         í         î         ï
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  ð         ñ         ò         ó         ô         õ         ö         ÷
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank,
        //  ø         ù         ú         û         ü         ý         þ         ÿ
            nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank, nonBlank
    };

    private static final UnicodeClassMapping kLineMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, LineExceptionFlags,
        kLineAsciiValues);

    public WordBreakTable forward()
    {
        return kLineForward;
    }

    public WordBreakTable backward()
    {
        return kLineBackward;
    }

    public UnicodeClassMapping map()
    {
        return kLineMap;
    }
}
