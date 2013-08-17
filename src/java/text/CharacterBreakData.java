/*
 * @(#)CharacterBreakData.java	1.9 98/01/12
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
 * The CharacterBreakData contains data used by SimpleTextBoundary
 * to determine character breaks.
 * @see #BreakIterator
 */
final class CharacterBreakData extends TextBoundaryData
{
    private static final byte accent_diacritic = 0;
    private static final byte baseForm = 1;
    private static final byte baseCR = 2;
    private static final byte baseLF = 3;
    private static final byte choseong = 4;   // Korean initial consonant
    private static final byte jungseong = 5;  // Korean vowel
    private static final byte jongseong = 6;  // Korean final consonant
    private static final byte EOS = 7;
    private static final int COL_COUNT = 8;

    private static final byte SI = (byte)0x80;
    private static final byte STOP = (byte) 0;
    private static final byte SI_STOP = (byte)SI + STOP;

    private static final byte kCharacterForwardData[] =
    {
        // acct        base             cr              lf
        // cho         jung             jong            EOS
        STOP,          STOP,            STOP,           STOP,
        STOP,          STOP,            STOP,           STOP,

        // 1
        (byte)(SI+2),  (byte)(SI+2),    (byte)(SI+3),   (byte)(SI+7),
        (byte)(SI+4),  (byte)(SI+5),    (byte)(SI+6),   SI_STOP,

        // 2
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP,

        // 3
        SI_STOP,       SI_STOP,         SI_STOP,        (byte)(SI+7),
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP,

        // 4
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        (byte)(SI+4),  (byte)(SI+5),    (byte)(SI+6),   SI_STOP,

        // 5
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,      (byte)(SI+5),    (byte)(SI+6),    SI_STOP,

        // 6
        (byte)(SI+2),  SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,       SI_STOP,         (byte)(SI+6),   SI_STOP,

        // 7
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP,
        SI_STOP,       SI_STOP,         SI_STOP,        SI_STOP
    };
    private static final WordBreakTable kCharacterForwardTable =
    new WordBreakTable(COL_COUNT, kCharacterForwardData);
    private static final byte kCharacterBackwardData[] =
    {
        // acct         base            cr              lf
        // cho          jung            jong            EOS
        STOP,           STOP,           STOP,           STOP,
        STOP,           STOP,           STOP,           STOP,

        // 1
        (byte)(SI+1),   SI_STOP,        SI_STOP,        (byte)(SI+1),
        SI_STOP,        (byte)(SI+1),   (byte)(SI+1),   SI_STOP
    };

    private static final WordBreakTable kCharacterBackwardTable =
    new WordBreakTable(COL_COUNT, kCharacterBackwardData);
    private static final int kRawMapping[] =
    {
        baseForm, //UNASSIGNED      = 0,
        baseForm, //UPPERCASE_LETTER    = 1,
        baseForm, //LOWERCASE_LETTER    = 2,
        baseForm, //TITLECASE_LETTER    = 3,
        baseForm, //MODIFIER_LETTER     = 4,
        baseForm, //OTHER_LETTER        = 5,
        accent_diacritic, //NON_SPACING_MARK    = 6,
        accent_diacritic, //ENCLOSING_MARK      = 7,
        baseForm, //COMBINING_SPACING_MARK  = 8,
        baseForm, //DECIMAL_DIGIT_NUMBER    = 9,
        baseForm, //LETTER_NUMBER       = 10,
        baseForm, //OTHER_NUMBER        = 11,
        baseForm, //SPACE_SEPARATOR     = 12,
        baseForm, //LINE_SEPARATOR      = 13,
        baseForm, //PARAGRAPH_SEPARATOR = 14,
        baseForm, //CONTROL         = 15,
        baseForm, //FORMAT      = 16,
        baseForm, //????            = 17,
        baseForm, //PRIVATE_USE     = 18,
        baseForm, //SURROGATE        = 19,
        baseForm, //DASH_PUNCTUATION    = 20,
        baseForm, //START_PUNCTUATION    = 21,
        baseForm, //END_PUNCTUATION     = 22,
        baseForm, //CONNECTOR_PUNCTUATION   = 23,
        baseForm, //OTHER_PUNCTUATION   = 24,
        baseForm, //MATH_SYMBOL     = 25,
        baseForm, //CURRENCY_SYMBOL     = 26,
        baseForm, //MODIFIER_SYMBOL     = 27,
        baseForm, //OTHER_SYMBOL        = 28;
    };

    private static final SpecialMapping kExceptionChar[] = //{};
    {
        new SpecialMapping(ASCII_LINEFEED, baseLF),
        new SpecialMapping(ASCII_CARRIAGE_RETURN, baseCR),
        new SpecialMapping(HANGUL_CHOSEONG_LOW, HANGUL_CHOSEONG_HIGH, choseong),
        new SpecialMapping(HANGUL_JUNGSEONG_LOW, HANGUL_JUNGSEONG_HIGH, jungseong),
        new SpecialMapping(HANGUL_JONGSEONG_LOW, HANGUL_JONGSEONG_HIGH, jongseong),
        new SpecialMapping(PUNCTUATION_LINE_SEPARATOR, PUNCTUATION_PARAGRAPH_SEPARATOR, baseLF),
        new SpecialMapping(END_OF_STRING, EOS)
    };

    private static final boolean CharacterExceptionFlags[] = {
        false,          // kNonCharacter            = 0,
        false,          // kUppercaseLetter         = 1,
        false,          // kLowercaseLetter         = 2,
        false,          // kTitlecaseLetter         = 3,
        false,          // kModifierLetter          = 4,
        true,           // kOtherLetter             = 5,
        false,          // kNonSpacingMark          = 6,
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
        false,          // kDashPunctuation         = 20,
        false,          // kOpenPunctuation         = 21,
        false,          // kClosePunctuation        = 22,
        false,          // kConnectorPunctuation    = 23,
        false,          // kOtherPunctuation        = 24,
        false,          // kMathSymbol              = 25,
        false,          // kCurrencySymbol          = 26,
        false,          // kModifierSymbol          = 27,
        false           // kOtherSymbol             = 28
    };

    private static final int kCharacterAsciiValues[] = {
        //  null      soh       stx       etx       eot       enq       ask       bell
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  bs        ht        lf      vt        ff        cr      so        si
            baseForm, baseForm, baseLF, baseForm, baseForm, baseCR, baseForm, baseForm,
        //  dle       dc1       dc2       dc3       dc4       nak       syn       etb
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  can       em        sub       esc       fs        gs        rs        us
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  sp        !         "         #         $         %         &         '
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  (         )         *         +         ,         -         .         /
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  0         1         2         3         4         5         6         7
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  8         9         :         ;         <         =         >         ?
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  @         A         B         C         D         E         F         G
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  H         I         J         K         L         M         N         O
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  P         Q         R         S         T         U         V         W
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  X         Y         Z         [         \         ]         ^         _
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  `         a         b         c         d         e         f         g
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  h         i         j         k         l         m         n         o
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  p         q         r         s         t         u         v         w
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  x         y         z         {         |         }         ~         del
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl      ctrl
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  nbsp      ¡         ¢         £         ¤         ¥         ¦
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ¨         ©         ª         «         ¬         ­         ®         ¯
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  °         ±         ²         ³         ´         µ         ¶         ·
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ¸         ¹         º         »         ¼         ½         ¾         ¿
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  À         Á         Â         Ã         Ä        Å         Æ         Ç
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  È         É         Ê         Ë         Ì         Í         Î         Ï
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  Ð         Ñ         Ò         Ó         Ô         Õ         Ö         ×
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  Ø         Ù         Ú         Û         Ü         Ý         Þ         ß
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  à         á         â         ã         ä         å         æ         ç
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  è         é         ê         ë         ì         í         î         ï
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ð         ñ         ò         ó         ô         õ         ö        ÷
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm,
        //  ø         ù         ú         û         ü         ý         þ         ÿ
            baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm, baseForm
    };

    private static final UnicodeClassMapping kCharacterMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar, CharacterExceptionFlags,
        kCharacterAsciiValues);


    public WordBreakTable forward()
    {
        return kCharacterForwardTable;
    }

    public WordBreakTable backward()
    {
        return kCharacterBackwardTable;
    }

    public UnicodeClassMapping map()
    {
        return kCharacterMap;
    }
}

