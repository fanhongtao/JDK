/*
 * @(#)SentenceBreakData.java	1.9 98/03/05
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
 * The SentenceBreakData contains data used by SimpleTextBoundary
 * to determine sentence breaks.
 * @see #BreakIterator
 */
final class SentenceBreakData extends TextBoundaryData
{
    private static final byte other = 0;
    // lower case letters, digits...
    private static final byte space = 1;
    // spaces...
    private static final byte terminator = 2;
    // period, questionmark...
    private static final byte ambiguosTerm = 3;
    // Ambiguos terminator
    private static final byte openBracket = 4;
    // open brackets
    private static final byte closeBracket = 5;
    // close brackets
    private static final byte cjk = 6;
    // Characters where the previous sentence does not have a space
    // after a terminator. Common in Japanese, Chinese, and Korean
    private static final byte paragraphBreak = 7;
    // Paragraph break
    private static final byte lowerCase = 8;
    // Lower case
    private static final byte upperCase = 9;
    private static final byte number = 10;

    private static final byte quote = 11;

    private static final byte sent_cr = 12;
    private static final byte nsm = 13;
    private static final byte EOS = 14;

    // digit
    private static final int COL_COUNT = 15;

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    private static final byte kSentenceForwardData[] =
    {
        // other       space          terminator     ambTerm
        // open        close          CJK            PB
        // lower       upper          digit          Quote
        // cr          nsm            EOS

        // 0
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,

        // 1
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+2),  (byte)(SI+5),
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+4),
        (byte)(SI+1),  (byte)(SI+8),  (byte)(SI+9),  (byte)(SI+1),
        (byte)(SI+10), (byte)(SI+1),  SI_STOP,

        // 2
        SI_STOP,       (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+5),
        (byte)(SI+1),  (byte)(SI+2),  SI_STOP,       (byte)(SI+4),
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+2),
        (byte)(SI+10), (byte)(SI+2),  SI_STOP,

        // 3
        SI_STOP,       (byte)(SI+3),  SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+4),
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+10), (byte)(SI+3),  SI_STOP,

        // 4
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,

        // 5
        SI_STOP,       (byte)(SI+6),  (byte)(SI+2),  (byte)(SI+5),
        (byte)(SI+1),  (byte)(SI+5),  SI_STOP,       (byte)(SI+4),
        (byte)(SI+1),  SI_STOP,       SI_STOP,       (byte)(SI+5),
        (byte)(SI+10), (byte)(SI+5),  SI_STOP,

        // 6
        SI_STOP,       (byte)(SI+6),  SI_STOP,       SI_STOP,
        (byte)(SI+7),  (byte)(SI+1),  SI_STOP,       (byte)(SI+4),
        (byte)(SI+1),  SI_STOP,       (byte)(SI+1),  SI_STOP,
        (byte)(SI+10), (byte)(SI+6),  SI_STOP,

        // 7
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(7),     SI_STOP,       SI_STOP,       SI_STOP,
        (byte)(SI+1),  STOP,          SI_STOP,       SI_STOP,
        SI_STOP,       (byte)(SI+7),  SI_STOP,

        // 8
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+2),  (byte)(SI+8),
        (byte)(SI+1),  (byte)(SI+5),  SI_STOP,       (byte)(SI+4),
        (byte)(SI+1),  (byte)(SI+8),  (byte)(SI+9),  (byte)(SI+5),
        (byte)(SI+10), (byte)(SI+8),  SI_STOP,

        // 9
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+2),  (byte)(SI+9),
        (byte)(SI+1),  (byte)(SI+5),  SI_STOP,       (byte)(SI+4),
        (byte)(SI+1),  (byte)(SI+1),  (byte)(SI+9),  (byte)(SI+5),
        (byte)(SI+10), (byte)(SI+9),  SI_STOP,

        // 10
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP,       (byte)(SI+4),
        SI_STOP,       SI_STOP,       SI_STOP,       SI_STOP,
        SI_STOP,       SI_STOP,       SI_STOP
    };

    private static final WordBreakTable kSentenceForward
        = new WordBreakTable(COL_COUNT, kSentenceForwardData);

    private static final byte kSentenceBackwardData[] =
    {
        // other       space          terminator     ambTerm
        // open        close          CJK            PB
        // lower       upper          digit          quote
        // cr          nsm            EOS

        // 0
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,          STOP,
        STOP,          STOP,          STOP,

        // 1
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+1),  STOP,

        // 2
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        STOP,          (byte)(SI+2),  STOP,

        // 3
        (byte)(SI+2),  (byte)(SI+4),  (byte)(SI+2),  (byte)(SI+2),
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+2),
        STOP,          (byte)(SI+3),  STOP,

        // 4
        (byte)(SI+2),  (byte)(SI+4),  SI_STOP,       SI_STOP,
        (byte)(SI+2),  (byte)(SI+2),  (byte)(SI+3),  STOP,
        (byte)(SI+2),  (byte)(SI+3),  (byte)(SI+2),  (byte)(SI+2),
        STOP,          (byte)(SI+4),  STOP
    };

    private static final WordBreakTable kSentenceBackward
        = new WordBreakTable(COL_COUNT, kSentenceBackwardData);

    private static final int kRawMapping[] =
    {
        other,        // UNASSIGNED             = 0,
        upperCase,    // UPPERCASE_LETTER       = 1,
        lowerCase,    // LOWERCASE_LETTER       = 2,
        other,        // TITLECASE_LETTER       = 3,
        other,        // MODIFIER_LETTER        = 4,
        other,        // OTHER_LETTER           = 5,
        nsm,          // NON_SPACING_MARK       = 6,
        nsm,          // ENCLOSING_MARK         = 7,
        other,        // COMBINING_SPACING_MARK = 8,
        number,       // DECIMAL_DIGIT_NUMBER   = 9,
        number,       // LETTER_NUMBER          = 10,
        number,       // OTHER_NUMBER           = 11,
        space,        // SPACE_SEPARATOR        = 12,
        space,        // LINE_SEPARATOR         = 13,
        space,        // PARAGRAPH_SEPARATOR    = 14,            ???????
        other,        // CONTROL                = 15,
        other,        // PRIVATE_USE            = 16,
        other,        // FORMAT                 = 17,
        other,        // ????                   = 18,
        other,        // SURROGATE              = 19,
        other,        // DASH_PUNCTUATION       = 20,
        openBracket,  // START_PUNCTUATION      = 21,
        closeBracket, // END_PUNCTUATION        = 22,
        other,        // CONNECTOR_PUNCTUATION  = 23,
        other,        // OTHER_PUNCTUATION      = 24,
        other,        // MATH_SYMBOL            = 25,
        other,        // CURRENCY_SYMBOL        = 26,
        other,        // MODIFIER_SYMBOL        = 27,
        other,        // OTHER_SYMBOL           = 28;
    };

    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order
        //as required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION, space),
        new SpecialMapping(ASCII_LINEFEED, space),
        new SpecialMapping(ASCII_FORM_FEED, terminator),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, space),

        new SpecialMapping(ASCII_EXCLAMATION_MARK, terminator),
        new SpecialMapping(ASCII_QUOTATION_MARK, quote),

        new SpecialMapping(ASCII_APOSTROPHE, quote),

        new SpecialMapping(ASCII_FULL_STOP, ambiguosTerm),
        new SpecialMapping(ASCII_QUESTION_MARK, terminator),
        new SpecialMapping(ASCII_NONBREAKING_SPACE, other),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR, space),
        new SpecialMapping(PUNCTUATION_PARAGRAPH_SEPARATOR, paragraphBreak),
        new SpecialMapping(PUNCTUATION_IDEOGRAPHIC_FULL_STOP, terminator),
        new SpecialMapping(HIRAGANA_LETTER_SMALL_A, HIRAGANA_LETTER_VU, cjk),
        new SpecialMapping(COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK,
                           HIRAGANA_SEMIVOICED_SOUND_MARK, cjk),         // cjk
        new SpecialMapping(KATAKANA_LETTER_SMALL_A, KATAKANA_LETTER_SMALL_KE,
                           cjk),   // cjk
        new SpecialMapping(UNICODE_LOW_BOUND_HAN, UNICODE_HIGH_BOUND_HAN, cjk),
        new SpecialMapping(CJK_COMPATIBILITY_F900, CJK_COMPATIBILITY_FA2D,cjk),
        new SpecialMapping(UNICODE_ZERO_WIDTH_NON_BREAKING_SPACE, other),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean SentenceExceptionFlags[] = {
        false,            // kNonCharacter         = 0,
        false,            // kUppercaseLetter      = 1,
        false,            // kLowercaseLetter      = 2,
        false,            // kTitlecaseLetter      = 3,
        false,            // kModifierLetter       = 4,
        true,             // kOtherLetter          = 5,
        true,             // kNonSpacingMark       = 6,
        false,            // kEnclosingMark        = 7,
        false,            // kCombiningSpacingMark = 8,
        false,            // kDecimalNumber        = 9,
        false,            // kLetterNumber         = 10,
        false,            // kOtherNumber          = 11,
        true,             // kSpaceSeparator       = 12,
        true,             // kLineSeparator        = 13,
        true,             // kParagraphSeparator   = 14,
        true,             // kControlCharacter     = 15,
        true,             // kFormatCharacter      = 16,
        false,            // UNDEFINED             = 17,
        false,            // kPrivateUseCharacter  = 18,
        false,            // kSurrogate            = 19,
        false,            // kDashPunctuation      = 20,
        false,            // kOpenPunctuation      = 21,
        false,            // kClosePunctuation     = 22,
        false,            // kConnectorPunctuation = 23,
        true,             // kOtherPunctuation     = 24,
        false,            // kMathSymbol           = 25,
        false,            // kCurrencySymbol       = 26,
        false,            // kModifierSymbol       = 27,
        false             // kOtherSymbol          = 28
    };

    private static final int kSentenceAsciiValues[] = {
        //  null    soh     stx     etx     eot     enq     ask     bell
            other,  other,  other,  other,  other,  other,  other,  other,
        //  bs      ht      lf     vt     ff          cr     so     si
            other,  space,  space, other, terminator, space, other, other,
        //  dle     dc1     dc2     dc3     dc4     nak     syn     etb
            other,  other,  other,  other,  other,  other,  other,  other,
        //  can     em      sub     esc     fs      gs      rs      us
            other,  other,  other,  other,  other,  other,  other,  other,
        //  sp      !           "      #      $      %      &      '
            space,  terminator, quote, other, other, other, other, quote,
        //  (            )             *      +      ,      -      .             /
            openBracket, closeBracket, other, other, other, other, ambiguosTerm, other,
        //  0       1       2       3       4       5       6       7
            number, number, number, number, number, number, number, number,
        //  8       9       :       ;       <       =       >       ?
            number, number, other,  other,  other,  other,  other,  terminator,
        //  @       A          B          C          D          E          F          G
            other,  upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  H          I          J          K          L          M          N          O
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  P          Q          R          S          T          U          V          W
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  X          Y          Z          [            \      ]             ^      _
            upperCase, upperCase, upperCase, openBracket, other, closeBracket, other, other,
        //  `       a          b          c          d          e          f          g
            other,  lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  h          i          j          k          l          m          n          o
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  p          q          r          s          t          u          v          w
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  x          y          z          {            |      }             ~      del
            lowerCase, lowerCase, lowerCase, openBracket, other, closeBracket, other, other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl    ctrl
            other,  other,  other,  other,  other,  other,  other,  other,
        //  nbsp    ¡       ¢       £       ¤       ¥       ¦
            other,  other,  other,  other,  other,  other,  other,  other,
        //  ¨       ©       ª          «            ¬      ­      ®      ¯
            other,  other,  lowerCase, openBracket, other, other, other, other,
        //  °       ±       ²       ³       ´       µ          ¶      ·
            other,  other,  number, number, other,  lowerCase, other, other,
        //  ¸       ¹          º      »             ¼       ½       ¾       ¿
            other,  lowerCase, other, closeBracket, number, number, number, other,
        //  À          Á          Â          Ã          Ä          Å          Æ          Ç
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  È          É          Ê          Ë          Ì          Í          Î          Ï
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase,
        //  Ð          Ñ          Ò          Ó          Ô          Õ          Ö          ×
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, other,
        //  Ø          Ù          Ú          Û          Ü          Ý          Þ          ß
            upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, upperCase, lowerCase,
        //  à          á          â          ã          ä          å          æ          ç
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  è          é          ê          ë          ì          í          î          ï
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase,
        //  ð          ñ          ò          ó          ô          õ          ö          ÷
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, other,
        //  ø          ù          ú          û          ü          ý          þ          ÿ
            lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase, lowerCase
    };

    private static final UnicodeClassMapping kSentenceMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, SentenceExceptionFlags,
        kSentenceAsciiValues);

    public WordBreakTable forward()
    {
        return kSentenceForward;
    }

    public WordBreakTable backward()
    {
        return kSentenceBackward;
    }

    public UnicodeClassMapping map()
    {
        return kSentenceMap;
    }
}

