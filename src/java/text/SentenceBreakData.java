/*
 * @(#)SentenceBreakData.java	1.5 97/01/17
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
    // digit
    private static final int COL_COUNT_BACKWARD = 11;
    private static final byte quote = 11;
    private static final int COL_COUNT_FORWARD = 12;
    private static final byte SI = (byte)0x80;
    private static final byte kSentenceForwardData[] =
    {
        // other        space               terminator      ambTerm
        // open         close               CJK             PB
        // lower        upper               digit           Quote
        (byte)(0),      (byte)(0),          (byte)(0),     (byte)(0),
        (byte)(0),      (byte)(0),          (byte)(0),     (byte)(0),
        (byte)(0),      (byte)(0),          (byte)(0),     (byte)(0),     //0

        (byte)(SI+1),   (byte)(SI+1),       (byte)(SI+2),   (byte)(SI+5),
        (byte)(SI+1),   (byte)(SI+1),       (byte)(SI+1),   (byte)(SI+4),
        (byte)(SI+1),   (byte)(SI+8),       (byte)(SI+9),   (byte)(SI+1), //1

        (byte)(SI+0),   (byte)(SI+3),       (byte)(SI+2),   (byte)(SI+5),
        (byte)(SI+1),   (byte)(SI+2),       (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+2), //2

        (byte)(SI+0),   (byte)(SI+3),       (byte)(SI+0),   (byte)(SI+0),
        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+0), //3

        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+0),
        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+0),
        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+0), //4

        (byte)(SI+0),   (byte)(SI+6),       (byte)(SI+2),   (byte)(SI+5),
        (byte)(SI+1),   (byte)(SI+5),       (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+1),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+5), //5

        (byte)(SI+0),   (byte)(SI+6),       (byte)(SI+0),   (byte)(SI+0),
        (byte)(SI+7),   (byte)(SI+1),       (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+1),   (byte)(SI+0),       (byte)(SI+1),   (byte)(SI+0), //6

        (byte)(SI+0),   (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+0),
        (byte)(7),      (byte)(SI+0),       (byte)(SI+0),   (byte)(SI+0),
        (byte)(SI+1),   (byte)(0),          (byte)(SI+0),   (byte)(SI+0), //7

        (byte)(SI+1),   (byte)(SI+1),       (byte)(SI+2),   (byte)(SI+8),
        (byte)(SI+1),   (byte)(SI+5),       (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+1),   (byte)(SI+8),       (byte)(SI+9),   (byte)(SI+5), //8

        (byte)(SI+1),   (byte)(SI+1),       (byte)(SI+2),   (byte)(SI+9),
        (byte)(SI+1),   (byte)(SI+5),       (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+1),   (byte)(SI+1),       (byte)(SI+9),   (byte)(SI+5), //9
    };

    private static final WordBreakTable kSentenceForward
        = new WordBreakTable(COL_COUNT_FORWARD, kSentenceForwardData);

    private static final byte kSentenceBackwardData[] =
    {
        // other        space         terminator             ambTerm
        // open         close         CJK                    PB
        // lower        upper         digit
        (byte)(0),     (byte)(0),     (byte)(0),        (byte)(0),
        (byte)(0),     (byte)(0),     (byte)(0),        (byte)(0),
        (byte)(0),     (byte)(0),     (byte)(0),

        (byte)(SI+1),   (byte)(SI+1),   (byte)(SI+1),   (byte)(SI+1),
        (byte)(SI+1),   (byte)(SI+1),   (byte)(SI+4),   (byte)(SI+1),
        (byte)(SI+2),   (byte)(SI+1),   (byte)(SI+1),

        (byte)(SI+1),   (byte)(SI+3),   (byte)(SI+0),   (byte)(SI+2),
        (byte)(SI+3),   (byte)(SI+3),   (byte)(SI+4),   (byte)(SI+0),
        (byte)(SI+2),   (byte)(SI+1),   (byte)(SI+1),

        (byte)(SI+1),   (byte)(SI+3),   (byte)(SI+0),   (byte)(SI+2),
        (byte)(SI+3),   (byte)(SI+3),   (byte)(SI+4),   (byte)(SI+0),
        (byte)(SI+2),   (byte)(SI+1),   (byte)(SI+1),

        (byte)(SI+4),   (byte)(SI+3),   (byte)(SI+0),   (byte)(SI+4),
        (byte)(SI+4),   (byte)(SI+4),   (byte)(SI+4),   (byte)(SI+0),
        (byte)(SI+2),   (byte)(SI+4),   (byte)(SI+4)
    };

    private static final WordBreakTable kSentenceBackward
    = new WordBreakTable(COL_COUNT_BACKWARD, kSentenceBackwardData);

	private static final int kRawMapping[] =
	{
		other, //UNASSIGNED		= 0,
		upperCase, //UPPERCASE_LETTER	= 1,
		lowerCase, //LOWERCASE_LETTER	= 2,
		other, //TITLECASE_LETTER	= 3,
		other, //MODIFIER_LETTER		= 4,
		other, //OTHER_LETTER		= 5,
		other, //NON_SPACING_MARK	= 6,
		other, //ENCLOSING_MARK		= 7,
		other, //COMBINING_SPACING_MARK	= 8,
		number, //DECIMAL_DIGIT_NUMBER	= 9,
		number, //LETTER_NUMBER		= 10,
		number, //OTHER_NUMBER		= 11,
		space, //SPACE_SEPARATOR		= 12,
		space, //LINE_SEPARATOR		= 13,
		space, //PARAGRAPH_SEPARATOR	= 14,			???????
		other, //CONTROL			= 15,
		other, //PRIVATE_USE		= 16,
		other, //FORMAT		= 17,
		other, //????		= 18,
		other, //SURROGATE		= 19,
		other, //DASH_PUNCTUATION	= 20,
		openBracket, //START_PUNCTUATION	= 21,
		closeBracket, //END_PUNCTUATION		= 22,
		other, //CONNECTOR_PUNCTUATION	= 23,
		other, //OTHER_PUNCTUATION	= 24,
		other, //MATH_SYMBOL		= 25,
		other, //CURRENCY_SYMBOL		= 26,
		other, //MODIFIER_SYMBOL		= 27,
		other, //OTHER_SYMBOL		= 28;
	};

 /* private static final int kRawMapping[] =
    {
        other, //00   Invalid
        space, //01   SpaceWhitespace
        space, //02   ZerowidthSpaceWhitespace
        other, //03   ISOcontrol
        space, //04   WhitespaceISOcontrol
        other, //05   Dash
        other, //06   Punctuation
        other, //07   DashPunctuation
        other, //08   HyphenPunctuation
        other, //09   DashHyphenPunctuation
        other, //10   PunctuationQuotationmark
        other, //11   PunctuationTerminalpunctuation
        other, //12   Currencysymbol
        closeBracket, //13   PunctuationPairedpunctuation
        closeBracket, //14   PunctuationQuotationmarkPairedpunctuation
        openBracket, //15   PunctuationPairedpunctuationLeftofpair
        openBracket, //16   PunctuationQuotationmarkPairedpunctuationLeftofpair
        other, //17   PunctuationPairedpunctuationCombining
        other, //18   PunctuationPairedpunctuationLeftofpairCombining
        other, //19   Composite
        number, //20   Numeric
        number, //21   CompositeNumeric
        other, //22   PunctuationAlphabetic
        other, //23   Diacritic
        other, //24   CompositeDiacritic
        other, //25   PunctuationIdentifierpart
        number, //26   DecimaldigitNumericIdentifierpart
        number, //27   HexdigitDecimaldigitNumericIdentifierpart
        other, //28   AlphabeticIdentifierpart
        other, //29   CombiningAlphabeticIdentifierpart
        other, //30   CompositeAlphabeticIdentifierpart
        other, //31   CombiningCompositeAlphabeticIdentifierpart
        number, //32   NumericAlphabeticIdentifierpart
        number, //33   CompositeNumericAlphabeticIdentifierpart
        other, //34   IdeographicIdentifierpart
        other, //35   NumericIdeographicIdentifierpart
        other, //36   CombiningDiacriticIdentifierpart
        other, //37   ExtenderIdentifierpart
        other, //38   CompositeExtenderIdentifierpart
        other, //39   DiacriticExtenderIdentifierpart
        other, //40   PunctuationDiacriticExtenderIdentifierpart
        other, //41   ZerowidthWhitespaceBidicontrolIgnorablecontrol
        other, //42   ZerowidthWhitespaceJoincontrolIgnorablecontrol
        other, //43   ZerowidthWhitespaceFormatcontrolIgnorablecontrol
        lowerCase, //44   AlphabeticIdentifierpartLower
        lowerCase, //45   CompositeAlphabeticIdentifierpartLower
        lowerCase, //46   HexdigitAlphabeticIdentifierpartLower
        upperCase, //47   AlphabeticIdentifierpartUpper
        upperCase, //48   CompositeAlphabeticIdentifierpartUpper
        upperCase, //49   HexdigitAlphabeticIdentifierpartUpper
        other, //50   CompositeAlphabeticIdentifierpartTitle
        other, //51   Marknonspacing
        other, //52   CombiningMarknonspacing
        other, //53   CombiningIdentifierpartMarknonspacing
        other, //54   AlphabeticIdentifierpartMarknonspacing
        other, //55   CombiningAlphabeticIdentifierpartMarknonspacing
        other, //56   CompositeAlphabeticIdentifierpartMarknonspacing
        other, //57   CombiningCompositeAlphabeticIdentifierpartMarknonspacing
        other, //58   CombiningDiacriticIdentifierpartMarknonspacing
        other, //59   CombiningCompositeDiacriticIdentifierpartMarknonspacing
        space, //60   WhitespaceNongraphicSeparator
        other, //61   WhitespaceISOcontrolNongraphicSeparator
        space, //62   SpaceWhitespaceNongraphicNobreak
        space, //63   ZerowidthSpaceWhitespaceIgnorablecontrolNongraphicNobreak
    }; */

    private static SpecialMapping kExceptionChar[] =
    {
        //note: the ranges in this table must be sorted in ascending order
        //as required by the UnicodeClassMapping class.
        new SpecialMapping(ASCII_HORIZONTAL_TABULATION, space),
        new SpecialMapping(ASCII_LINEFEED, space),
        new SpecialMapping(ASCII_FORM_FEED, terminator),

        new SpecialMapping(ASCII_EXCLAMATION_MARK, terminator),
        new SpecialMapping(ASCII_QUOTATION_MARK, quote),

        new SpecialMapping(ASCII_APOSTROPHE, quote),

        new SpecialMapping(ASCII_FULL_STOP, ambiguosTerm),
        new SpecialMapping(ASCII_QUESTION_MARK, terminator),
        new SpecialMapping(ASCII_NONBREAKING_SPACE, other),
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
    };

    private static final UnicodeClassMapping kSentenceMap
        = new UnicodeClassMapping(kRawMapping, kExceptionChar);

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

