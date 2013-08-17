/*
 * @(#)WordBreakData.java	1.5 97/01/17
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
    private static final byte cr = 10;
    private static final byte kata = 11;
    private static final byte hira = 12;
    private static final byte kanji = 13;
    private static final byte diacrit = 14;
    private static final int COL_COUNT = 15;
    private static final byte SI = (byte)0x80;
    private static final byte kWordForwardData[] =
    {
        // brk        let            num          mLe           mLN
        // prN        poN            mNu          pMN           blk
        // cr         kat            hir          kan           dia

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(SI+4), (byte)(SI+2), (byte)(SI+3), (byte)(SI+4), (byte)(SI+4),
        (byte)(SI+5), (byte)(SI+4), (byte)(SI+4), (byte)(SI+5), (byte)(SI+6),
        (byte)(SI+4),(byte)(SI+10),(byte)(SI+11),(byte)(SI+12), (byte)(SI+9),

        (byte)(SI+0), (byte)(SI+2), (byte)(SI+3), (byte)(SI+7), (byte)(SI+7),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+7), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),

        (byte)(SI+0), (byte)(SI+2), (byte)(SI+3), (byte)(SI+0), (byte)(SI+8),
        (byte)(SI+0), (byte)(SI+4), (byte)(SI+8), (byte)(SI+8), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+3), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+6),
        (byte)(SI+4), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),

        (byte)(0), (byte)(SI+2),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0),    (byte)(0), (byte)(SI+3),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10),(byte)(SI+11), (byte)(SI+0), (byte)(SI+9),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0),(byte)(SI+10), (byte)(SI+0), (byte)(SI+0),(byte)(SI+10),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0),(byte)(SI+11), (byte)(SI+0),(byte)(SI+11),

        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),
        (byte)(SI+0), (byte)(SI+0), (byte)(SI+0),(byte)(SI+12), (byte)(SI+0)
    };
    private static final WordBreakTable kWordForward =
    new WordBreakTable(COL_COUNT, kWordForwardData);
    private static final byte kWordBackwardData[] =
    {
        // brk         let             num            mLe           mLN
        // prN         poN             mNu            pMN           blk
        // cr          kat             hir            kan           dia
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(SI+6), (byte)(SI+2), (byte)(SI+3), (byte)(SI+4), (byte)(SI+5),
        (byte)(SI+6), (byte)(SI+7), (byte)(SI+7), (byte)(SI+5), (byte)(SI+8),
        (byte)(SI+8), (byte)(SI+9),(byte)(SI+10),(byte)(SI+12),(byte)(SI+11),

        (byte)(0), (byte)(SI+2), (byte)(SI+3),    (byte)(4),    (byte)(4),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(4),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0), (byte)(SI+2), (byte)(SI+3),    (byte)(0),    (byte)(7),
        (byte)(SI+0),    (byte)(0),    (byte)(7), (byte)(SI+7),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0), (byte)(SI+2),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0), (byte)(SI+2), (byte)(SI+3),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0),    (byte)(0), (byte)(SI+3),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0), (byte)(SI+8),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+9),    (byte)(0),    (byte)(0),    (byte)(9),

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),(byte)(SI+10),    (byte)(0),   (byte)(10),

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0), (byte)(SI+9),(byte)(SI+10),    (byte)(0),(byte)(SI+11),

        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),    (byte)(0),
        (byte)(0),    (byte)(0),    (byte)(0),(byte)(SI+12),    (byte)(0)
    };
    private static final WordBreakTable kWordBackward =
    new WordBreakTable(COL_COUNT, kWordBackwardData);
    private static final int kRawMapping[] =
    {
		BREAK, //UNASSIGNED		= 0,
		letter, //UPPERCASE_LETTER	= 1,
		letter, //LOWERCASE_LETTER	= 2,
		letter, //TITLECASE_LETTER	= 3,
		letter, //MODIFIER_LETTER		= 4,
		letter, //OTHER_LETTER		= 5,
		BREAK, //NON_SPACING_MARK	= 6,
		BREAK, //ENCLOSING_MARK		= 7,
		BREAK, //COMBINING_SPACING_MARK	= 8,
		number, //DECIMAL_DIGIT_NUMBER	= 9,
		letter, //LETTER_NUMBER		= 10,
		number, //OTHER_NUMBER		= 11,
		blank, //SPACE_SEPARATOR		= 12,
		BREAK, //LINE_SEPARATOR		= 13,
		BREAK, //PARAGRAPH_SEPARATOR	= 14,
		BREAK, //CONTROL			= 15,
		BREAK, //PRIVATE_USE		= 16,
		BREAK, //FORMAT		= 17,
		BREAK, //???		= 18,
		BREAK, //SURROGATE		= 19,
		midLetter, //DASH_PUNCTUATION	= 20,
		BREAK, //START_PUNCTUATION	= 21,
		BREAK, //END_PUNCTUATION		= 22,
		BREAK, //CONNECTOR_PUNCTUATION	= 23,
		BREAK, //OTHER_PUNCTUATION	= 24,
		BREAK, //MATH_SYMBOL		= 25,
		preNum, //CURRENCY_SYMBOL		= 26,
		BREAK, //MODIFIER_SYMBOL		= 27,
		BREAK //OTHER_SYMBOL		= 28
    };
/*  private static final int kRawMapping[] =
    {
        BREAK, //00   Invalid
        blank, //01   SpaceWhitespace
        blank, //02   ZerowidthSpaceWhitespace
        BREAK, //03   ISOcontrol
        blank, //04   WhitespaceISOcontrol
        BREAK, //05   Dash
        BREAK, //06   Punctuation
        midLetter, //07   DashPunctuation
        BREAK, //08   HyphenPunctuation
        midLetter, //09   DashHyphenPunctuation
        BREAK, //10   PunctuationQuotationmark
        BREAK, //11   PunctuationTerminalpunctuation
        preNum, //12   Currencysymbol
        BREAK, //13   PunctuationPairedpunctuation
        BREAK, //14   PunctuationQuotationmarkPairedpunctuation
        BREAK, //15   PunctuationPairedpunctuationLeftofpair
        BREAK, //16   PunctuationQuotationmarkPairedpunctuationLeftofpair
        BREAK, //17   PunctuationPairedpunctuationCombining
        BREAK, //18   PunctuationPairedpunctuationLeftofpairCombining
        BREAK, //19   Composite
        number, //20   Numeric
        number, //21   CompositeNumeric
        letter, //22   PunctuationAlphabetic
        letter, //23   Diacritic
        letter, //24   CompositeDiacritic
        BREAK, //25   PunctuationIdentifierpart
        number, //26   DecimaldigitNumericIdentifierpart
        number, //27   HexdigitDecimaldigitNumericIdentifierpart
        letter, //28   AlphabeticIdentifierpart
        BREAK, //29   CombiningAlphabeticIdentifierpart
        letter, //30   CompositeAlphabeticIdentifierpart
        BREAK, //31   CombiningCompositeAlphabeticIdentifierpart
        letter, //32   NumericAlphabeticIdentifierpart
        letter, //33   CompositeNumericAlphabeticIdentifierpart
        letter, //34   IdeographicIdentifierpart
        letter, //35   NumericIdeographicIdentifierpart
        BREAK, //36   CombiningDiacriticIdentifierpart
        BREAK, //37   ExtenderIdentifierpart
        BREAK, //38   CompositeExtenderIdentifierpart
        letter, //39   DiacriticExtenderIdentifierpart
        letter, //40   PunctuationDiacriticExtenderIdentifierpart
        blank, //41   ZerowidthWhitespaceBidicontrolIgnorablecontrol
        blank, //42   ZerowidthWhitespaceJoincontrolIgnorablecontrol
        blank, //43   ZerowidthWhitespaceFormatcontrolIgnorablecontrol
        letter, //44   AlphabeticIdentifierpartLower
        letter, //45   CompositeAlphabeticIdentifierpartLower
        letter, //46   HexdigitAlphabeticIdentifierpartLower
        letter, //47   AlphabeticIdentifierpartUpper
        letter, //48   CompositeAlphabeticIdentifierpartUpper
        letter, //49   HexdigitAlphabeticIdentifierpartUpper
        letter, //50   CompositeAlphabeticIdentifierpartTitle
        BREAK, //51   Marknonspacing
        BREAK, //52   CombiningMarknonspacing
        BREAK, //53   CombiningIdentifierpartMarknonspacing
        letter, //54   AlphabeticIdentifierpartMarknonspacing
        BREAK, //55   CombiningAlphabeticIdentifierpartMarknonspacing
        letter, //56   CompositeAlphabeticIdentifierpartMarknonspacing
        BREAK, //57   CombiningCompositeAlphabeticIdentifierpartMarknonspacing
        BREAK, //58   CombiningDiacriticIdentifierpartMarknonspacing
        BREAK, //59   CombiningCompositeDiacriticIdentifierpartMarknonspacing
        BREAK, //60   WhitespaceNongraphicSeparator
        BREAK, //61   WhitespaceISOcontrolNongraphicSeparator
        BREAK, //62   SpaceWhitespaceNongraphicNobreak
        BREAK,
        //63   ZerowidthSpaceWhitespaceIgnorablecontrolNongraphicNobreak

    };*/
    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order
        //as required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION, blank),
        new SpecialMapping(ASCII_LINEFEED, cr),
        new SpecialMapping(ASCII_FORM_FEED, ASCII_CARRIAGE_RETURN, cr),
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
                           PUNCTUATION_PARAGRAPH_SEPARATOR, cr),
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
    };
    private static final UnicodeClassMapping kWordMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar);

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

